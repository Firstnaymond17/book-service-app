package com.remizov.bookservice.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Книга не найдена с id: " + id);
    }
}