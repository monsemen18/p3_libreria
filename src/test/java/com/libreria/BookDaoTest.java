package com.libreria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.libreria.model.Book;
import com.libreria.model.BookCategory;
import com.libreria.repository.BookCategoryDao;
import com.libreria.repository.BookDao;

@SpringBootTest
@Transactional   // revierte cada prueba: no deja basura en la BD
class BookDaoTest {

    @Autowired private BookDao bookDao;
    @Autowired private BookCategoryDao categoryDao;

    private BookCategory nuevaCategoria() {
        BookCategory c = new BookCategory();
        c.setName("Ficción");
        categoryDao.saveBookCategory(c);
        return c;
    }

    private Book nuevoLibro(BookCategory cat) {
        Book b = new Book();
        b.setTitle("Cien años de soledad");
        b.setAuthor("García Márquez");
        b.setPrice(199.9);
        b.setStock(5);
        b.setCategory(cat);
        bookDao.saveBook(b);
        return b;
    }

    @Test
    void guardaYBuscaCategoria() {
        BookCategory c = nuevaCategoria();
        assertNotNull(c.getId());                      // se generó el id
        assertEquals("Ficción", categoryDao.findById(c.getId()).getName());
    }

    @Test
    void guardaYBuscaLibroConCategoria() {
        Book b = nuevoLibro(nuevaCategoria());
        assertNotNull(b.getId());
        Book leido = bookDao.findById(b.getId());
        assertEquals("Cien años de soledad", leido.getTitle());
        assertEquals("Ficción", leido.getCategory().getName());   // el JOIN funciona
    }

    @Test
    void actualizaLibro() {
        Book b = nuevoLibro(nuevaCategoria());
        b.setTitle("Editado");
        bookDao.updateBook(b);
        assertEquals("Editado", bookDao.findById(b.getId()).getTitle());
    }

    @Test
    void eliminaLibro() {
        Book b = nuevoLibro(nuevaCategoria());
        bookDao.deleteBook(b.getId());
        assertNull(bookDao.findById(b.getId()));
    }

    @Test
    void filtraPorCategoria() {
        BookCategory c = nuevaCategoria();
        nuevoLibro(c);
        assertEquals(1, bookDao.findBooksByCategory(c.getId()).size());
    }

    @Test
    void descuentaStock() {
        Book b = nuevoLibro(nuevaCategoria());
        bookDao.updateBookStock(b.getId(), 2);
        assertEquals(3, bookDao.findById(b.getId()).getStock());
    }

    @Test
    void stockInsuficienteLanzaExcepcion() {
        Book b = nuevoLibro(nuevaCategoria());
        assertThrows(RuntimeException.class, () -> bookDao.updateBookStock(b.getId(), 99));
    }
}