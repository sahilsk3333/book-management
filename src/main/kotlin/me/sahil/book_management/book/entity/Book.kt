package me.sahil.book_management.book.entity

import jakarta.persistence.*
import me.sahil.book_management.auth.entity.User

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

    val pdfUrl: String? = null
)
