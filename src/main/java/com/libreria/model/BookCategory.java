package com.libreria.model;

public class BookCategory {
    private Long id;
    
    private String name;
    
    public BookCategory() {
        // Initialize the books set to prevent null pointer exceptions
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Helper methods to manage the bidirectional association
    public void addBook(Book book) {
        book.setCategory(this);
    }

    public void removeBook(Book book) {
        book.setCategory(null);
    } 
}
