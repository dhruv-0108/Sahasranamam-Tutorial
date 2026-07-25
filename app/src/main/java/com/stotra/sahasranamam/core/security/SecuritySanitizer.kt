package com.stotra.sahasranamam.core.security

/**
 * SecuritySanitizer provides utilities for sanitizing inputs, guarding against SQL injection
 * in raw room queries, and ensuring path traversal protection on asset reads.
 */
object SecuritySanitizer {

    /**
     * Sanitizes user input search terms to prevent SQL wildcard exploitation (% and _)
     * when executing LIKE queries in Room DAOs.
     */
    fun sanitizeSearchQuery(query: String): String {
        return query
            .trim()
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
    }

    /**
     * Validates relative asset path to prevent directory traversal attacks (e.g. "../..").
     * Returns true if path is safe to load from assets directory.
     */
    fun isSafeAssetPath(path: String): Boolean {
        if (path.isBlank()) return false
        val normalized = path.replace('\\', '/')
        if (normalized.contains("../") || normalized.startsWith("/")) {
            return false
        }
        // Only allow safe alphanumeric, underscores, hyphens, and slashes
        return normalized.matches(Regex("^[a-zA-Z0-9_/\\-.]+$"))
    }
}
