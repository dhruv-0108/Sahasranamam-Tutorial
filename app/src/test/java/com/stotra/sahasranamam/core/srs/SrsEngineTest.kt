package com.stotra.sahasranamam.core.srs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SrsEngineTest {

    @Test
    fun calculateNextReview_failedRecall_resetsProgress() {
        val now = 1000000L
        val result = SrsEngine.calculateNextReview(
            rating = SrsEngine.RATING_AGAIN,
            currentRepetition = 5,
            currentInterval = 14.0,
            currentEaseFactor = 2.5,
            currentTimeMs = now
        )

        assertEquals(SrsEngine.STATE_LEARNING, result.nextState)
        assertEquals(0, result.repetitionCount)
        assertEquals(1.0, result.intervalDays, 0.01)
        assertTrue(result.easeFactor < 2.5) // Ease factor drops on failure
    }

    @Test
    fun calculateNextReview_goodRecall_increasesInterval() {
        val now = 1000000L
        val result = SrsEngine.calculateNextReview(
            rating = SrsEngine.RATING_GOOD,
            currentRepetition = 1,
            currentInterval = 1.0,
            currentEaseFactor = 2.5,
            currentTimeMs = now
        )

        assertEquals(2, result.repetitionCount)
        assertEquals(3.0, result.intervalDays, 0.01)
    }

    @Test
    fun calculateNextReview_easyRecall_boostsEaseFactorAndInterval() {
        val now = 1000000L
        val result = SrsEngine.calculateNextReview(
            rating = SrsEngine.RATING_EASY,
            currentRepetition = 2,
            currentInterval = 3.0,
            currentEaseFactor = 2.5,
            currentTimeMs = now
        )

        assertEquals(3, result.repetitionCount)
        assertTrue(result.easeFactor > 2.5)
        assertTrue(result.intervalDays > 3.0)
    }
}
