package me.sahil.book_management.book.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Pattern
import me.sahil.book_management.user.entity.User
import org.hibernate.validator.constraints.ISBN
import java.time.LocalDateTime

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,  // The author is now the user

    @Column(nullable = false)
    val name: String,

    val description: String? = null,

    val pdfUrl: String? = null,

    @Column(nullable = false, unique = true)
    @field:Pattern(
        regexp = "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}\$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}\$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]\$", // Regex for both ISBN-13 and ISBN-10
        message = "Invalid ISBN format, must be either ISBN-10 or ISBN-13"
    )
    val isbn: String, // ISBN field with validation pattern

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(0, User(), "", null, null, "", LocalDateTime.now())
}

