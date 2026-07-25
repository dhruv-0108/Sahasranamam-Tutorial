package com.stotra.sahasranamam.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecuritySanitizerTest {

    @Test
    fun sanitizeSearchQuery_escapesSqlWildcards() {
        val rawInput = "vishnu_100%_test"
        val sanitized = SecuritySanitizer.sanitizeSearchQuery(rawInput)
        assertEquals("vishnu\\_100\\%\\_test", sanitized)
    }

    @Test
    fun isSafeAssetPath_validatesSafePaths() {
        assertTrue(SecuritySanitizer.isSafeAssetPath("stotras/vishnu_sahasranamam.json"))
        assertTrue(SecuritySanitizer.isSafeAssetPath("audios/vs_001.mp3"))

        // Rejects path traversal attempts
        assertFalse(SecuritySanitizer.isSafeAssetPath("../sensitive.txt"))
        assertFalse(SecuritySanitizer.isSafeAssetPath("/etc/passwd"))
        assertFalse(SecuritySanitizer.isSafeAssetPath(""))
    }
}
