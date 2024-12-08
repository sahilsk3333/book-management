package me.sahil.book_management.book.repository

import me.sahil.book_management.book.entity.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Page

/**
 * Repository interface for performing CRUD operations on [Book] entities.
 *
 * This interface extends [JpaRepository] and provides additional custom query methods
 * to interact with the `Book` table in the database. These methods allow retrieving books
 * by author ID, checking if a book's ISBN exists, and checking if a book with a specific
 * ISBN already exists (excluding a given book by its ID).
 *
 * @see JpaRepository
 */
interface BookRepository : JpaRepository<Book, Long> {

    /**
     * Find books by the author's ID, with pagination support.
     *
     * This method allows retrieving a paginated list of books written by a specific author.
     *
     * @param authorId The ID of the author whose books are to be retrieved.
     * @param pageable Pagination information, such as page number and page size.
     * @return A [Page] of books written by the specified author.
     */
    fun findByAuthorId(authorId: Long, pageable: Pageable): Page<Book>

    /**
     * Check if a book with a specific ISBN already exists in the database, excluding the book with the provided ID.
     *
     * This method is useful for ensuring that a new or updated book does not have a duplicate ISBN,
     * excluding the current book's ISBN.
     *
     * @param isbn The ISBN of the book to check.
     * @param id The ID of the current book to exclude from the check.
     * @return `true` if a book with the same ISBN (and different ID) exists, `false` otherwise.
     */
    fun existsByIsbnAndIdNot(isbn: String, id: Long): Boolean

    /**
     * Check if a book with a specific ISBN already exists in the database.
     *
     * This method is used to determine if a book with the given ISBN exists in the database.
     *
     * @param isbn The ISBN of the book to check.
     * @return `true` if a book with the same ISBN exists, `false` otherwise.
     */
    fun existsByIsbn(isbn: String): Boolean
}
