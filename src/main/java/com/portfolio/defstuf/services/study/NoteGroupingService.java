package com.portfolio.defstuf.services.study;

import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.models.note.Note;
import com.portfolio.defstuf.models.study.NoteGroup;
import com.portfolio.defstuf.models.study.StudySession;
import com.portfolio.defstuf.models.study.ScheduledReview;
import com.portfolio.defstuf.repository.note.NoteRepository;
import com.portfolio.defstuf.repository.study.ScheduledReviewRepository;
import com.portfolio.defstuf.session.SessionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Service for grouping notes by title and area
 * FR-05.3: Handles note grouping and queue management for study sessions
 */
public class NoteGroupingService {
    
    private final NoteRepository noteRepository;
    private final ScheduledReviewRepository scheduledReviewRepository;
    private final Long userId;
    
    // Queue for notes that need to be reviewed again (negative answers)
    private final Queue<NoteGroup> reviewQueue;
    
    public NoteGroupingService() {
        this.noteRepository = new NoteRepository();
        this.scheduledReviewRepository = new ScheduledReviewRepository();
        this.userId = SessionManager.getInstance().getCurrentUserId();
        this.reviewQueue = new LinkedList<>();
    }
    
    /**
     * Loads and groups notes for a study session
     * 
     * @param areaId The area ID
     * @param sourceIds List of source IDs (null or empty means all sources)
     * @param cardsLimit Maximum number of note groups (null means no limit)
     * @param reviewOrder The order to sort notes
     * @return List of NoteGroup objects
     * @throws SQLException If database error occurs
     */
    public List<NoteGroup> loadNotesForSession(
            Long areaId,
            List<Long> sourceIds,
            Integer cardsLimit,
            StudySession.ReviewOrder reviewOrder,
            Area area
    ) throws SQLException {
        
        if (areaId == null || userId == null) {
            return new ArrayList<>();
        }
        
        // Get eligible notes
        List<Note> eligibleNotes = noteRepository.findByAreaIdAndUserIdAndSourceIds(areaId, userId, sourceIds);
        
        if (eligibleNotes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Group notes by title (case-insensitive)
        Map<String, List<Note>> notesByTitle = new HashMap<>();
        for (Note note : eligibleNotes) {
            String titleKey = note.getTitle().toLowerCase().trim();
            notesByTitle.computeIfAbsent(titleKey, k -> new ArrayList<>()).add(note);
        }
        
        // Convert to NoteGroup objects
        List<NoteGroup> noteGroups = new ArrayList<>();
        for (Map.Entry<String, List<Note>> entry : notesByTitle.entrySet()) {
            // All notes in a group must have the same area (already filtered)
            NoteGroup group = new NoteGroup();
            group.setSharedTitle(entry.getValue().get(0).getTitle()); // Use original title
            group.setNotes(entry.getValue());
            group.setArea(area);
            noteGroups.add(group);
        }
        
        // Apply sorting based on reviewOrder
        applyReviewOrder(noteGroups, reviewOrder);
        
        // Apply cards limit
        if (cardsLimit != null && cardsLimit > 0 && noteGroups.size() > cardsLimit) {
            noteGroups = noteGroups.subList(0, cardsLimit);
        }
        
        return noteGroups;
    }
    
    /**
     * Applies sorting based on review order
     */
    private void applyReviewOrder(List<NoteGroup> noteGroups, StudySession.ReviewOrder reviewOrder) {
        if (reviewOrder == null) {
            return;
        }
        
        switch (reviewOrder) {
            case RANDOM:
                Collections.shuffle(noteGroups);
                break;
                
            case OLDEST_FIRST:
                // Sort by earliest created_at in the group
                noteGroups.sort((g1, g2) -> {
                    LocalDate date1 = getEarliestDate(g1);
                    LocalDate date2 = getEarliestDate(g2);
                    return date1.compareTo(date2);
                });
                break;
                
            case HARDEST_FIRST:
                // Sort by review quality (lower = harder)
                noteGroups.sort((g1, g2) -> {
                    try {
                        int difficulty1 = getGroupDifficulty(g1);
                        int difficulty2 = getGroupDifficulty(g2);
                        return Integer.compare(difficulty1, difficulty2); // Lower = harder, so they come first
                    } catch (SQLException e) {
                        // If error occurs, don't change order
                        System.err.println("Error sorting by difficulty: " + e.getMessage());
                        return 0;
                    }
                });
                break;
        }
    }
    
    /**
     * Gets the earliest created date from a note group
     */
    private LocalDate getEarliestDate(NoteGroup group) {
        return group.getNotes().stream()
                .filter(n -> n.getCreatedAt() != null)
                .map(n -> n.getCreatedAt().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
    }
    
    /**
     * Gets the difficulty of a note group (based on last review quality)
     * Lower value = harder (1=Forgot, 2=Hard, 3=Good, 4=Easy)
     * Returns 0 if no review exists (new notes are considered hardest)
     */
    private int getGroupDifficulty(NoteGroup group) throws SQLException {
        int minDifficulty = 5; // Default to easiest
        
        for (Note note : group.getNotes()) {
            ScheduledReview review = scheduledReviewRepository.findByNoteIdAndUserId(note.getId(), userId);
            if (review != null && review.getReviewQuality() != null) {
                int quality = review.getReviewQuality().getValue();
                minDifficulty = Math.min(minDifficulty, quality);
            } else {
                // New note (no review) is considered hardest
                return 1;
            }
        }
        
        return minDifficulty == 5 ? 1 : minDifficulty; // If no reviews found, return 1 (hardest)
    }
    
    /**
     * Adds a note group to the review queue (for negative answers)
     * FR-05.3: Notes with negative answers reappear in the same session
     * 
     * @param noteGroup The note group to add to review queue
     */
    public void addToReviewQueue(NoteGroup noteGroup) {
        if (noteGroup != null && !reviewQueue.contains(noteGroup)) {
            reviewQueue.offer(noteGroup);
        }
    }
    
    /**
     * Gets the next note group from review queue
     * 
     * @return The next NoteGroup from queue, or null if queue is empty
     */
    public NoteGroup getNextFromReviewQueue() {
        return reviewQueue.poll();
    }
    
    /**
     * Checks if review queue has more items
     * 
     * @return true if queue is not empty
     */
    public boolean hasMoreInReviewQueue() {
        return !reviewQueue.isEmpty();
    }
    
    /**
     * Clears the review queue
     */
    public void clearReviewQueue() {
        reviewQueue.clear();
    }
}

