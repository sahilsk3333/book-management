package me.sahil.book_management.user.entity

import jakarta.persistence.*
import me.sahil.book_management.book.entity.Book
import me.sahil.book_management.core.role.Role
import me.sahil.book_management.file.Entity.File
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING) // Persist as a String
    @Column(nullable = false)
    val role: Role = Role.READER, // Default to READER role

    val image: String? = null,  // Optional image

    val age: Int? = null,       // Optional age

    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    val books: List<Book> = emptyList(),  // A list of books by this user


    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val files: List<File> = emptyList(),  // One-to-many relationship with File


    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    // Secondary constructor for referencing by ID
    constructor(id: Long) : this(id = id, name = "", email = "", password = "")

    // Default constructor for Hibernate (required)
    constructor() : this(0, "", "", "", Role.READER, null, 21, emptyList(), emptyList(), LocalDateTime.now())

}
