package com.portfolio.defstuf.models.study;

/**
 * Answer model representing a rating answer (forgot, hard, good, easy)
 */
public class Answer {
    
    private Long id;
    private String content;  // "forgot", "hard", "good", "easy"
    
    public Answer() {
        // Default constructor
    }
    
    public Answer(String content) {
        this.content = content;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}





