package com.libreria.model;

public class Book {
    private Long book_id;

    private String title;
    private String author;
    private String image;
    private Double price;
    private Integer stock;
    private String description;

    private BookCategory category_id;

    public BookCategory getCategory() {
        if (this.category_id == null) {
            this.category_id = new BookCategory();
        }
        return category_id;
    }

    public void setCategory(BookCategory category) {
        this.category_id = category;
    }

    public Long getId() {
        return book_id;
    }

    public void setId(Long id) {
        this.book_id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
