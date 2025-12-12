package com.portfolio.defstuf.services.study;

import com.portfolio.defstuf.models.study.ScheduledReview;
import com.portfolio.defstuf.repository.study.ScheduledReviewRepository;
import com.portfolio.defstuf.session.SessionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service for spaced repetition algorithm
 * FR-05.5: Calculates next review dates based on user ratings
 */
public class SpacedRepetitionService {
    
    private final ScheduledReviewRepository scheduledReviewRepository;
    private final Long userId;
    
    // Rating factors for interval calculation
    private static final double FORGOTTEN_FACTOR = 0.0; // Reset to 1 minute (will be handled separately)
    private static final double DIFFICULT_FACTOR = 1.2;
    private static final double GOOD_FACTOR = 1.8;
    private static final double EASY_FACTOR = 2.5;
    
    // Initial interval for new notes (1 day)
    private static final int INITIAL_INTERVAL_DAYS = 1;
    
    // Minimum interval for forgotten notes (1 minute = 0 days, but scheduled for today)
    private static final int FORGOTTEN_INTERVAL_DAYS = 0; // Review today
    
    public SpacedRepetitionService() {
        this.scheduledReviewRepository = new ScheduledReviewRepository();
        this.userId = SessionManager.getInstance().getCurrentUserId();
    }
    
    /**
     * Updates scheduled review for a note based on rating
     * FR-05.4, FR-05.5: Calculates next review date using spaced repetition algorithm
     * 
     * @param noteId The note ID
     * @param rating The rating ("forgot", "hard", "good", "easy")
     * @throws SQLException If database error occurs
     */
    public void updateScheduledReview(Long noteId, String rating) throws SQLException {
        if (noteId == null || rating == null || userId == null) {
            return;
        }
        
        ScheduledReview review = scheduledReviewRepository.findByNoteIdAndUserId(noteId, userId);
        ScheduledReview.ReviewQuality quality = ratingToQuality(rating);
        
        if (review == null) {
            // Create new scheduled review for new note
            review = createNewScheduledReview(noteId, quality);
        } else {
            // Update existing review
            updateExistingScheduledReview(review, quality);
        }
    }
    
    /**
     * Creates a new scheduled review for a note
     */
    private ScheduledReview createNewScheduledReview(Long noteId, ScheduledReview.ReviewQuality quality) throws SQLException {
        ScheduledReview review = new ScheduledReview(noteId, userId, LocalDate.now());
        review.setReviewedDate(LocalDateTime.now());
        review.setReviewQuality(quality);
        review.setReviewStatus(ScheduledReview.ReviewStatus.REVIEWED);
        review.setCurrentInterval(INITIAL_INTERVAL_DAYS);
        
        // Calculate next interval based on rating
        int nextIntervalDays = calculateNextInterval(INITIAL_INTERVAL_DAYS, quality);
        review.setNextInterval(nextIntervalDays);
        review.setScheduledDate(LocalDate.now().plusDays(nextIntervalDays));
        
        return scheduledReviewRepository.save(review);
    }
    
    /**
     * Updates an existing scheduled review
     */
    private void updateExistingScheduledReview(ScheduledReview review, ScheduledReview.ReviewQuality quality) throws SQLException {
        review.setReviewedDate(LocalDateTime.now());
        review.setReviewQuality(quality);
        review.setReviewStatus(ScheduledReview.ReviewStatus.REVIEWED);
        
        // Get current interval (use nextInterval if available, otherwise currentInterval)
        int currentInterval = review.getNextInterval() != null ? review.getNextInterval() : review.getCurrentInterval();
        review.setCurrentInterval(currentInterval);
        
        // Calculate next interval based on rating
        int nextIntervalDays = calculateNextInterval(currentInterval, quality);
        review.setNextInterval(nextIntervalDays);
        review.setScheduledDate(LocalDate.now().plusDays(nextIntervalDays));
        
        scheduledReviewRepository.update(review);
    }
    
    /**
     * Calculates the next interval in days based on current interval and rating
     * FR-05.4: Interval calculation based on rating
     * 
     * @param currentIntervalDays Current interval in days
     * @param quality The review quality rating
     * @return Next interval in days
     */
    private int calculateNextInterval(int currentIntervalDays, ScheduledReview.ReviewQuality quality) {
        if (quality == null) {
            return currentIntervalDays;
        }
        
        switch (quality) {
            case FORGOT:
                // FR-05.4: Forgotten resets to 1 minute (scheduled for today)
                return FORGOTTEN_INTERVAL_DAYS;
                
            case HARD:
                // FR-05.4: Difficult multiplies by 1.2
                return Math.max(1, (int) Math.round(currentIntervalDays * DIFFICULT_FACTOR));
                
            case GOOD:
                // FR-05.4: Good multiplies by 1.8
                return Math.max(1, (int) Math.round(currentIntervalDays * GOOD_FACTOR));
                
            case EASY:
                // FR-05.4: Easy multiplies by 2.5
                return Math.max(1, (int) Math.round(currentIntervalDays * EASY_FACTOR));
                
            default:
                return currentIntervalDays;
        }
    }
    
    /**
     * Converts rating string to ReviewQuality enum
     */
    private ScheduledReview.ReviewQuality ratingToQuality(String rating) {
        if (rating == null) {
            return null;
        }
        
        String ratingLower = rating.toLowerCase().trim();
        switch (ratingLower) {
            case "forgot":
            case "forgotten":
                return ScheduledReview.ReviewQuality.FORGOT;
            case "hard":
            case "difficult":
                return ScheduledReview.ReviewQuality.HARD;
            case "good":
                return ScheduledReview.ReviewQuality.GOOD;
            case "easy":
                return ScheduledReview.ReviewQuality.EASY;
            default:
                return null;
        }
    }
}

