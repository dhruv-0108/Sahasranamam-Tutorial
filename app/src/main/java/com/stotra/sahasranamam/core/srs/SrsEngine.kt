package com.stotra.sahasranamam.core.srs

import kotlin.math.max
import kotlin.math.roundToLong

/**
 * Anki-inspired SuperMemo-2 (SM-2) Spaced Repetition Engine for Shloka learning.
 */
object SrsEngine {

    const val STATE_NEW = 0
    const val STATE_LEARNING = 1
    const val STATE_REVIEW = 2
    const val STATE_MASTERED = 3

    const val RATING_AGAIN = 1 // Failed recall
    const val RATING_HARD = 2  // Hard recall
    const val RATING_GOOD = 3  // Good recall
    const val RATING_EASY = 4  // Easy recall

    const val MIN_EASE_FACTOR = 1.3
    const val DEFAULT_EASE_FACTOR = 2.5
    private const val ONE_DAY_MS = 86_400_000L

    data class SrsResult(
        val nextState: Int,
        val repetitionCount: Int,
        val intervalDays: Double,
        val easeFactor: Double,
        val nextReviewDueMs: Long
    )

    /**
     * Calculates the next review date, interval, and ease factor using SM-2 rules.
     *
     * @param rating 1 = Again, 2 = Hard, 3 = Good, 4 = Easy
     * @param currentRepetition Number of successful consecutive reviews
     * @param currentInterval Current review interval in days
     * @param currentEaseFactor Current ease factor (default 2.5)
     * @param currentTimeMs Current unix epoch timestamp in milliseconds
     */
    fun calculateNextReview(
        rating: Int,
        currentRepetition: Int,
        currentInterval: Double,
        currentEaseFactor: Double,
        currentTimeMs: Long
    ): SrsResult {
        val validRating = rating.coerceIn(RATING_AGAIN, RATING_EASY)

        // 1. Calculate new Ease Factor using SM-2 formula:
        // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        // Mapping rating 1..4 -> q 2..5
        val q = validRating + 1
        val updatedEf = currentEaseFactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val newEaseFactor = max(MIN_EASE_FACTOR, updatedEf)

        val newRepetition: Int
        val newInterval: Double
        val newState: Int

        if (validRating == RATING_AGAIN) {
            // Failed recall: reset repetitions, set short interval (10 mins / 0.007 days or 1 day)
            newRepetition = 0
            newInterval = 1.0
            newState = STATE_LEARNING
        } else {
            newRepetition = currentRepetition + 1
            newInterval = when (newRepetition) {
                1 -> 1.0
                2 -> 3.0
                else -> {
                    val multiplier = when (validRating) {
                        RATING_HARD -> 1.2
                        RATING_GOOD -> currentEaseFactor
                        RATING_EASY -> currentEaseFactor * 1.3
                        else -> currentEaseFactor
                    }
                    max(1.0, currentInterval * multiplier)
                }
            }

            newState = if (newInterval >= 21.0) {
                STATE_MASTERED
            } else {
                STATE_REVIEW
            }
        }

        val nextReviewDueMs = currentTimeMs + (newInterval * ONE_DAY_MS).roundToLong()

        return SrsResult(
            nextState = newState,
            repetitionCount = newRepetition,
            intervalDays = newInterval,
            easeFactor = newEaseFactor,
            nextReviewDueMs = nextReviewDueMs
        )
    }
}
