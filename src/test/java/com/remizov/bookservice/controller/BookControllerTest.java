package com.remizov.bookservice.controller;

import com.remizov.bookservice.entity.Book;
import com.remizov.bookservice.exception.BookNotFoundException;
import com.remizov.bookservice.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Чистый код")
                .author("Роберт Мартин")
                .year(2008)
                .price(39.99)
                .build();
    }

    @Test
    void getAll_shouldReturn200WithBooks() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Чистый код"));
    }

    @Test
    void getById_shouldReturn200_whenExists() throws Exception {
        when(bookService.findById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_shouldReturn404_whenNotExists() throws Exception {
        when(bookService.findById(99L)).thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn201_withValidBody() throws Exception {
        when(bookService.create(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Чистый код"));
    }

    @Test
    void create_shouldReturn400_withInvalidBody() throws Exception {
        Book invalid = Book.builder()
                .title("")
                .author("")
                .year(2008)
                .price(39.99)
                .build();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturn200_whenExists() throws Exception {
        when(bookService.update(eq(1L), any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturn404_whenNotExists() throws Exception {
        when(bookService.update(eq(99L), any(Book.class)))
                .thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(put("/api/books/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn204_whenExists() throws Exception {
        doNothing().when(bookService).delete(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404_whenNotExists() throws Exception {
        doThrow(new BookNotFoundException(99L)).when(bookService).delete(99L);

        mockMvc.perform(delete("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void heavy_shouldReturn200() throws Exception {
        when(bookService.heavyComputation(anyInt())).thenReturn(12345L);

        mockMvc.perform(get("/api/books/heavy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Вычисление завершено"));
    }
}