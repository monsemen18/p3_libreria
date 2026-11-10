package com.libreria.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.libreria.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.libreria.model.BookCategory;

@Repository
public class BookCategoryDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookCategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<BookCategory> rowMapper = (rs, rowNum) -> {
        BookCategory c = new BookCategory();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        return c;
    };

    // Equivalente a saveOrUpdate
    public void saveBookCategory(BookCategory bookCategory) {
        try {
            if (bookCategory.getId() == null) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO book_categories (name) VALUES (?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, bookCategory.getName());
                    return ps;
                }, keyHolder);
                if (keyHolder.getKey() != null) {
                    bookCategory.setId(keyHolder.getKey().longValue());
                }
            } else {
                updateBookCategory(bookCategory);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to save book category", e);
        }
    }

    public List<BookCategory> findAll() {
        try {
            return jdbcTemplate.query("SELECT id, name FROM book_categories", rowMapper);
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve all book categories", e);
        }
    }

    public BookCategory findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, name FROM book_categories WHERE id = ?", rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve book category by ID", e);
        }
    }

    public void deleteBookCategory(Long id) {
        try {
            jdbcTemplate.update("DELETE FROM book_categories WHERE id = ?", id);
        } catch (Exception e) {
            throw new DataAccessException("Failed to delete book category", e);
        }
    }

    public void updateBookCategory(BookCategory bookCategory) {
        try {
            jdbcTemplate.update("UPDATE book_categories SET name = ? WHERE id = ?",
                    bookCategory.getName(), bookCategory.getId());
        } catch (Exception e) {
            throw new DataAccessException("Failed to update book category", e);
        }
    }
}
