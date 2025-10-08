package triage.Model;

public enum PriorityLevel {
    LOW(0, "Low"),
    MEDIUM(1, "Medium"), 
    HIGH(2, "High");
    
    private final int priorityScore;
    private final String description;
    
    PriorityLevel(int priorityScore, String description) {
        this.priorityScore = priorityScore;
        this.description = description;
    }
    
    public int getPriorityScore() {
        return priorityScore;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PriorityLevel max(PriorityLevel... levels) {
        PriorityLevel max = LOW;
        for (PriorityLevel level : levels) {
            if (level.getPriorityScore() > max.getPriorityScore()) {
                max = level;
            }
        }
        return max;
    }

    public static int sum(PriorityLevel... levels) {
        int scoreSum = 0;
        for (PriorityLevel level : levels) {
            scoreSum += level.getPriorityScore();
        }
        return scoreSum;
    }
}