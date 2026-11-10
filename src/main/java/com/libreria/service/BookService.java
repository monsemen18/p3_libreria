package com.libreria.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.libreria.model.Book;
import com.libreria.model.BookCategory;
import com.libreria.repository.BookDao;
import java.util.logging.Logger;

@Service 
public class BookService {
    private static final Logger LOGGER = Logger.getLogger(BookService.class.getName());

	
    private final BookDao bookDao;

    BookService(BookDao bookDao) {
        this.bookDao = bookDao;
    }

//    Retrieve and Sort All Books from the Database Including Their Categories
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        List<Book> books = bookDao.findAllWithCategories();
        LOGGER.info("Retrieved " + books.size() + " books from database");
        System.out.println("Retrieved " + books.size() + " books from database");
        return books.stream()
                    .sorted(Comparator.comparing(Book::getId))
                    .collect(Collectors.toList());
    }

//    Fetch a Specific Book by Its ID
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookDao.findById(id);
    }
    
//    Save a New Book to the Database
    @Transactional
    public void addBook(Book book) {
        bookDao.saveBook(book);
    }
    
//    Update an Existing Book's Details in the Database
    @Transactional
    public void updateBook(Book book) {
        Book existingBook = bookDao.findById(book.getId());
        if (existingBook != null) {
            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setImage(book.getImage());
            existingBook.setPrice(book.getPrice());
            existingBook.setStock(book.getStock());
            existingBook.setDescription(book.getDescription());
            existingBook.setCategory(book.getCategory());
            bookDao.updateBook(existingBook);
        }
    }
    
//    Remove a Book from the Database by ID
    @Transactional
    public void deleteBook(Long id) {
        bookDao.deleteBook(id);
    }
    
//    Retrieve Books Associated with a Specific Category ID
    @Transactional(readOnly = true)
    public List<Book> findBooksByCategoryId(Long categoryId) {
        return bookDao.findBooksByCategory(categoryId);
    }
    
//    Fetch and Sort All Books That Are Currently in Stock
    @Transactional(readOnly = true)
    public List<Book> getAllBooksInStock() {
        return bookDao.findAllWithCategories().stream()
                      .filter(book -> book.getStock() > 0)
                      .sorted(Comparator.comparing(Book::getId))
                      .collect(Collectors.toList());
    }

//    Retrieve All Book Categories from the Database
    @Transactional(readOnly = true)
    public List<BookCategory> getAllCategories() {
        return bookDao.findAllCategories(); // This method needs to be implemented in BookDao
    }
}
