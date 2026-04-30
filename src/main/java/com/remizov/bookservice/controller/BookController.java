package com.remizov.bookservice.controller;

import com.remizov.bookservice.entity.Book;
import com.remizov.bookservice.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAll() {
        log.debug("GET /api/books");
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        log.debug("GET /api/books/{}", id);
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody Book book) {
        log.debug("POST /api/books, название='{}'", book.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @Valid @RequestBody Book book) {
        log.debug("PUT /api/books/{}", id);
        return ResponseEntity.ok(bookService.update(id, book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("DELETE /api/books/{}", id);
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/heavy")
    public ResponseEntity<Map<String, Object>> heavy(
            @RequestParam(defaultValue = "50000000") int iterations) {
        log.info("Запуск тяжёлого эндпоинта, итераций={}", iterations);

        if (iterations > 200_000_000) {
            log.warn("Запрошено {} итераций — превышен лимит, ограничиваем до 200_000_000", iterations);
            iterations = 200_000_000;
        }

        long result = bookService.heavyComputation(iterations);
        return ResponseEntity.ok(Map.of(
                "iterations", iterations,
                "result", result,
                "message", "Вычисление завершено"
        ));
    }
}