package com.libreria.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.libreria.model.Book;
import com.libreria.model.BookCategory;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class BookDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_WITH_CATEGORY =
            "SELECT b.book_id, b.title, b.author, b.image, b.price, b.stock, b.description, " +
            "       c.id AS category_id, c.name AS category_name " +
            "FROM books b JOIN book_categories c ON b.category_id = c.id ";

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        BookCategory category = new BookCategory();
        category.setId(rs.getLong("category_id"));
        category.setName(rs.getString("category_name"));

        Book book = new Book();
        book.setId(rs.getLong("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setImage(rs.getString("image"));
        book.setPrice(rs.getDouble("price"));
        book.setStock(rs.getInt("stock"));
        book.setDescription(rs.getString("description"));
        book.setCategory(category);
        return book;
    };

    private final RowMapper<BookCategory> categoryRowMapper = (rs, rowNum) -> {
        BookCategory c = new BookCategory();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        return c;
    };

    public List<Book> findAllWithCategories() {
        try {
            return jdbcTemplate.query(SELECT_WITH_CATEGORY + "ORDER BY b.book_id", bookRowMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve books", e);
        }
    }

    public Book findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_WITH_CATEGORY + "WHERE b.book_id = ?", bookRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find the book with ID: " + id, e);
        }
    }

    public void saveBook(Book book) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO books (title, author, image, price, stock, description, category_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, book.getTitle());
                ps.setString(2, book.getAuthor());
                ps.setString(3, book.getImage());
                ps.setObject(4, book.getPrice());
                ps.setObject(5, book.getStock());
                ps.setString(6, book.getDescription());
                ps.setObject(7, book.getCategory().getId());   // null si no hay categoría
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                book.setId(keyHolder.getKey().longValue());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save the book", e);
        }
    }

    public void updateBook(Book book) {
        try {
            jdbcTemplate.update(
                    "UPDATE books SET title = ?, author = ?, image = ?, price = ?, stock = ?, " +
                    "description = ?, category_id = ? WHERE book_id = ?",
                    book.getTitle(), book.getAuthor(), book.getImage(), book.getPrice(),
                    book.getStock(), book.getDescription(), book.getCategory().getId(), book.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update the book", e);
        }
    }

    public void deleteBook(Long id) {
        try {
            jdbcTemplate.update("DELETE FROM books WHERE book_id = ?", id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete the book with ID: " + id, e);
        }
    }

    public List<Book> findBooksByCategory(Long categoryId) {
        try {
            return jdbcTemplate.query(
                    SELECT_WITH_CATEGORY + "WHERE b.category_id = ? ORDER BY b.book_id",
                    bookRowMapper, categoryId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve books by category", e);
        }
    }

    public List<BookCategory> findAllCategories() {
        try {
            return jdbcTemplate.query("SELECT id, name FROM book_categories", categoryRowMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve categories", e);
        }
    }

    // Reduce el stock de forma atómica; falla si no hay suficiente
    public void updateBookStock(Long bookId, int quantity) {
        int rows;
        try {
            rows = jdbcTemplate.update(
                    "UPDATE books SET stock = stock - ? WHERE book_id = ? AND stock >= ?",
                    quantity, bookId, quantity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update book stock", e);
        }
        if (rows == 0) {
            throw new RuntimeException("Failed to update book stock: Insufficient stock or book not found.");
        }
    }
}
