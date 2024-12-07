package me.sahil.book_management.book.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Pattern
import me.sahil.book_management.auth.entity.User
import org.hibernate.validator.constraints.ISBN

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
    @ISBN(message = "Invalid ISBN format", type = ISBN.Type.ISBN_13)
    val isbn: String // ISBN field with validation pattern
) {
    constructor() : this(0, User(), "", null, null, "")
}

