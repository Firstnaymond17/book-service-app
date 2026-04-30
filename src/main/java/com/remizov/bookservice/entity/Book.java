package com.remizov.bookservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название не должно быть пустым")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Автор не должен быть пустым")
    @Column(nullable = false)
    private String author;

    @Min(value = 0, message = "Год должен быть положительным")
    @Max(value = 2100, message = "Год слишком далёкий")
    @Column(name = "publish_year", nullable = false)
    private Integer year;

    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    @Column(nullable = false)
    private Double price;
}