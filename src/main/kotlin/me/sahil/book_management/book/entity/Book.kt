package me.sahil.book_management.book.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Pattern
import me.sahil.book_management.user.entity.User
import org.hibernate.validator.constraints.ISBN
import java.time.LocalDateTime
import java.time.ZonedDateTime

/**
 * Entity class representing a book in the system.
 *
 * This class is mapped to the "books" table in the database and holds all the necessary details for a book.
 * It includes the book's ID, author information, name, description, PDF URL, ISBN, and creation timestamp.
 * The `author` is a `User` entity representing the book's author, while other fields may be nullable.
 *
 * @property id The unique identifier of the book.
 * @property author The author of the book, represented by a `User` entity. This field cannot be null.
 * @property name The name of the book. This field cannot be null.
 * @property description A description of the book. This field is optional and can be null.
 * @property pdfUrl The URL of the book's PDF version. This field is optional and can be null.
 * @property isbn The ISBN of the book, which must be either ISBN-10 or ISBN-13. This field cannot be null.
 * @property createdAt The timestamp indicating when the book was created. This field is set to the current time by default.
 */
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
    val createdAt: ZonedDateTime = ZonedDateTime.now()
) {
    constructor() : this(0, User(), "", null, null, "", ZonedDateTime.now())
}

