package com.remizov.bookservice.service;

import com.remizov.bookservice.entity.Book;
import com.remizov.bookservice.exception.BookNotFoundException;
import com.remizov.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    public List<Book> findAll() {
        log.debug("Получение всех книг из репозитория");
        List<Book> books = bookRepository.findAll();
        log.info("Получено {} книг", books.size());
        return books;
    }

    public Book findById(Long id) {
        log.debug("Поиск книги с id={}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Книга с id={} не найдена", id);
                    return new BookNotFoundException(id);
                });
    }

    public Book create(Book book) {
        log.debug("Создание книги: название='{}', автор='{}'", book.getTitle(), book.getAuthor());
        Book saved = bookRepository.save(book);
        log.info("Книга успешно создана с id={}", saved.getId());
        return saved;
    }

    public Book update(Long id, Book updated) {
        log.debug("Обновление книги id={}", id);
        Book existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setYear(updated.getYear());
        existing.setPrice(updated.getPrice());
        Book saved = bookRepository.save(existing);
        log.info("Книга id={} успешно обновлена", id);
        return saved;
    }

    public void delete(Long id) {
        log.debug("Удаление книги id={}", id);
        Book book = findById(id);
        bookRepository.delete(book);
        log.info("Книга id={} успешно удалена", id);
    }

    public long heavyComputation(int iterations) {
        log.info("Запуск тяжёлого вычисления с {} итерациями", iterations);
        long result = 0;

        for (int i = 0; i < iterations; i++) {
            result += (long) Math.sqrt(i) * Math.round(Math.log(i + 1));

            if (i % 1_000_000 == 0 && i > 0) {
                log.trace("Прогресс: {}/{} итераций, результат={}", i, iterations, result);
            }
        }

        log.info("Тяжёлое вычисление завершено. Результат={}", result);
        return result;
    }
}