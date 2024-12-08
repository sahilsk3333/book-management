package me.sahil.book_management.file.Entity

import jakarta.persistence.*
import me.sahil.book_management.user.entity.User
import java.time.LocalDateTime


@Entity
@Table(name = "files")
data class File(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val fileName: String,

    @Column(nullable = false)
    val mimeType: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val downloadUrl: String,

    @Column(nullable = false)
    val isUsed: Boolean = false,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
){
    // Default constructor is needed for JPA
    constructor() : this(0, "", "", User(), "", false, LocalDateTime.now())
}
