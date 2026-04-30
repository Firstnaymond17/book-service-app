package com.remizov.bookservice.service;

import com.remizov.bookservice.entity.Book;
import com.remizov.bookservice.exception.BookNotFoundException;
import com.remizov.bookservice.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
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
    void findAll_shouldReturnListOfBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Чистый код");
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_shouldThrowException_whenNotExists() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_shouldSaveAndReturnBook() {
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.create(book);

        assertThat(result.getTitle()).isEqualTo("Чистый код");
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void update_shouldUpdateFields_whenBookExists() {
        Book updated = Book.builder()
                .title("Новый заголовок")
                .author("Новый автор")
                .year(2022)
                .price(44.99)
                .build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book result = bookService.update(1L, updated);

        assertThat(result.getTitle()).isEqualTo("Новый заголовок");
        assertThat(result.getYear()).isEqualTo(2022);
    }

    @Test
    void update_shouldThrowException_whenBookNotExists() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(99L, book))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        assertThatCode(() -> bookService.delete(1L)).doesNotThrowAnyException();
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void delete_shouldThrowException_whenNotExists() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.delete(99L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void heavyComputation_shouldReturnPositiveResult() {
        long result = bookService.heavyComputation(1000);
        assertThat(result).isGreaterThanOrEqualTo(0);
    }

    @Test
    void heavyComputation_withZeroIterations_shouldReturnZero() {
        long result = bookService.heavyComputation(0);
        assertThat(result).isEqualTo(0);
    }
}