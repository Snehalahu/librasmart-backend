package com.librasmart.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookDto {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    private String isbn;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String categoryName;

    private String publisher;

    private Integer publishedYear;

    @NotNull(message = "Total copies is required")
    private Integer totalCopies;

    private Integer availableCopies;

    private String description;

    private String coverImage;

    private String ebookUrl;

    private BigDecimal averageRating;
}