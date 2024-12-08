package me.sahil.book_management.core.role


/**
 * Enum class representing the different roles that a user can have in the system.
 *
 * This enum defines the possible roles for users during registration. The roles determine the
 * permissions and access level within the system. The available roles are:
 *
 * - **ADMIN**: A user with administrative privileges, able to manage the system's settings and users.
 * - **AUTHOR**: A user who can create and manage content (e.g., books or articles).
 * - **READER**: A user with basic access to view content but not modify it.
 */
enum class Role {
    ADMIN,    // Administrative user with elevated privileges.
    AUTHOR,   // User who can create and manage content.
    READER    // User with read-only access to content.
}
