package com.libreria.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.libreria.model.Book;
import com.libreria.model.BookCategory;
import com.libreria.service.BookCategoryService;
import com.libreria.service.BookService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/book")
public class BookController {

    // Inyectar dependencias para libro
    
    private final BookCategoryService bookCategoryService;
	private final BookService bookService;

    BookController(BookCategoryService bookCategoryService, BookService bookService) {
        this.bookCategoryService = bookCategoryService;
        this.bookService = bookService;
    }

    // operaciones sobre book category
    // List All Book Categories
    @GetMapping("/book-categories")
    public String listBookCategories(Model model) {
        model.addAttribute("bookCategories", bookCategoryService.listAllCategories());
        return "bookCategories";
    }

    // Add a New Book Category
    @PostMapping("/addBookCategory")
    public String addBookCategory(BookCategory bookCategory) {
        bookCategoryService.saveBookCategory(bookCategory);
        return "redirect:/admin/book-categories";
    }

    // Delete a Book Category    
    @PostMapping("/deleteBookCategory")
    public String deleteBookCategory(@RequestParam Long id) {
        bookCategoryService.deleteCategory(id);
        return "redirect:/admin/book-categories";
    }

    // Edit Book Category Form
    @GetMapping("/editBookCategory/{id}")
    public String editBookCategoryForm(@PathVariable Long id, Model model) {
        BookCategory bookCategory = bookCategoryService.getCategoryById(id);
        model.addAttribute("bookCategory", bookCategory);
        return "redirect:/admin/book-categories"; // Assuming you have a separate JSP for editing a category
    }

    //  Update Book Category Details
    @PostMapping("/updateBookCategory")
    public String updateBookCategory(@RequestParam("id") Long id, @RequestParam("name") String name) {
        BookCategory category = bookCategoryService.getCategoryById(id);
        if (category != null) {
            category.setName(name);
            bookCategoryService.updateCategory(category);
        }
        return "redirect:/admin/book-categories";
    }

    // operaciones sobre los libros
    @GetMapping("/manageBooks")
    public String showManageBooks(@RequestParam(value = "category_id", required = false) Long categoryId, Model model) {
        List<Book> books = (categoryId != null && categoryId > 0) ? bookService.findBooksByCategoryId(categoryId) : bookService.getAllBooks();
        List<BookCategory> categories = bookCategoryService.listAllCategories();
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        return "manageBooks";
    }
 
 // Form to Edit Book Details
    @GetMapping("/editBook/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        List<BookCategory> categories = bookCategoryService.listAllCategories();  // Fetch categories
        if (book == null) {
            return "redirect:/admin/manageBooks";
        }
        model.addAttribute("book", book);
        model.addAttribute("categories", categories);  // Add categories to model
        return "updateBook";
    }
    
//  Form to Add New Book
    @GetMapping("/addBookForm")
    public String addBookForm(Model model) {
        List<BookCategory> categories = bookCategoryService.listAllCategories();  // Fetch categories
        model.addAttribute("categories", categories); 
        model.addAttribute("book", new Book());
        return "addBook";
    }
//    Add New Book with Image Upload
    @PostMapping("/addBook")
    public String addBook(@ModelAttribute("book") Book book, 
                          @RequestParam("category_id") Long categoryId,
                          @RequestParam(value = "imageUrl", required = false) String imageUrl,
                          @RequestParam("imageFile") MultipartFile imageFile,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {

        if (categoryId != null) {
            BookCategory category = bookCategoryService.getCategoryById(categoryId);
            if (category != null) {
                book.setCategory(category);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid book category.");
                return "redirect:/admin/addBookForm";
            }
        }

        // Handle image file if uploaded
        if (!imageFile.isEmpty()) {
            String imagePath = saveFile(imageFile, request);
            if (imagePath != null) {
                book.setImage(imagePath);
            }
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            book.setImage(imageUrl);
        }

        bookService.addBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
        return "redirect:/admin/manageBooks";
    }


//    Update Book Details Including Image  
    @PostMapping("/updateBook/{id}")
    public String updateBook(@PathVariable Long id, 
            @ModelAttribute("book") Book updatedBook, 
            @RequestParam("category_id") Long categoryId,
            @RequestParam(value = "imageUrl", required = false) String imageUrl, // Capture the image URL from the form
            BindingResult result, 
            RedirectAttributes redirectAttributes,
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpServletRequest request) {
        
        if (result.hasErrors()) {
            return "updateBook";
        }

        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found!");
            return "redirect:/admin/manageBooks";
        }

        if (!updatedBook.getTitle().isEmpty()) {
            existingBook.setTitle(updatedBook.getTitle());
        }
        if (!updatedBook.getAuthor().isEmpty()) {
            existingBook.setAuthor(updatedBook.getAuthor());
        }
        if (updatedBook.getPrice() != null) {
            existingBook.setPrice(updatedBook.getPrice());
        }
        if (updatedBook.getStock() != null) {
            existingBook.setStock(updatedBook.getStock());
        }
        if (!updatedBook.getDescription().isEmpty()) {
            existingBook.setDescription(updatedBook.getDescription());
        }

        // Handle category update
        if (categoryId != null) {
            BookCategory category = bookCategoryService.getCategoryById(categoryId);
            if (category != null) {
                existingBook.setCategory(category);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected category does not exist.");
                return "redirect:/admin/manageBooks";
            }
        }
     // Handle image upload or URL input
        if (!imageFile.isEmpty()) {
            String imagePath = saveFile(imageFile, request);
            if (imagePath != null) {
                existingBook.setImage(imagePath);
            } else {
            }
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            existingBook.setImage(imageUrl); // Set image URL if no file is uploaded
        } else {
        }

        bookService.updateBook(existingBook);
        redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
        return "redirect:/admin/manageBooks";
    }

//    Save Uploaded File to Server
    private String saveFile(MultipartFile file, HttpServletRequest request) {
        String uploadsDir = "/uploads/";
        String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
        if (!new File(realPathtoUploads).exists()) {
            new File(realPathtoUploads).mkdir();
        }

        String orgName = file.getOriginalFilename();
        String filePath = realPathtoUploads + orgName;
        File dest = new File(filePath);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return null;
        }

        String savedPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + uploadsDir + orgName;
        return savedPath;
    }
    
//    Delete a Book from Database
    @PostMapping("/deleteBook")
    public String deleteBook(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting book: " + e.getMessage());
        }
        return "redirect:/admin/manageBooks";
    }

    
}
