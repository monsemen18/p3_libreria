package com.libreria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.libreria.model.BookCategory;
import com.libreria.repository.BookCategoryDao;

@Service 
public class BookCategoryService {
    private final BookCategoryDao bookCategoryDao;

    BookCategoryService(BookCategoryDao bookCategoryDao) {
        this.bookCategoryDao = bookCategoryDao;
    }

//    Save or Update a Book Category in the Database
    @Transactional
    public void saveBookCategory(BookCategory bookCategory) {
        bookCategoryDao.saveBookCategory(bookCategory);
    }

//    Retrieve All Book Categories from the Database
    @Transactional(readOnly = true)
    public List<BookCategory> listAllCategories() {
        return bookCategoryDao.findAll();
    }

//    Fetch a Specific Book Category by ID
    @Transactional(readOnly = true)
    public BookCategory getCategoryById(Long id) {
        return bookCategoryDao.findById(id);
    }

//    Delete a Book Category from the Database by ID
    @Transactional
    public void deleteCategory(Long id) {
        bookCategoryDao.deleteBookCategory(id);
    }

//    Update Details of a Book Category in the Database
    @Transactional
    public void updateCategory(BookCategory bookCategory) {
        bookCategoryDao.updateBookCategory(bookCategory);
    }
}
