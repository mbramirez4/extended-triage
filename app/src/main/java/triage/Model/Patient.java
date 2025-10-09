package triage.Model;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class Patient implements Comparable<Patient> {
    private final static String[] highRiskConditions = {
        "cancer", "heart disease", "epoc", "kidney disease", "liver disease",
    };
    private final static String[] mediumRiskConditions = {
        "hypertension", "diabetes", "vascular disease",
    };
    private final static String[] highRiskSymptoms = {
        "chest pain", "breathing difficulty", "head injury", "unconscious",
        "internal bleeding", "arrythmias",
    };
    private final static String[] mediumRiskSymptoms = {
        "fracture", "persistent fever", "abdominal pain", "bloody vomit",
        "chest pain",
    };
    private final static String[] highRiskSocialFactors = {
        "pregnant", "minor without guardian", "elderly alone",
    };
    private final static String[] mediumRiskSocialFactors = {
        "with family support",
    };
    private static int patientsCount = 0;

    private int id;
    private int age;
    private String name;
    
    private int painLevel; // Scale 1-10
    private String currentIllness;
    private List<String> medicalHistory;
    private List<String> socialFactors;
    private Map<String, Double> vitalSigns;

    private int overallPriorityScore;
    private PriorityLevel overallPriority;
    
    public Patient(String name, int age) {
        this.id = patientsCount++;
        this.name = name;
        this.age = age;
        
        this.medicalHistory = new ArrayList<>();
        this.socialFactors = new ArrayList<>();
        this.vitalSigns = new HashMap<>();
        this.painLevel = 0;
        
        this.overallPriority = PriorityLevel.LOW;
        this.overallPriorityScore = overallPriority.getPriorityScore();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getPainLevel() {
        return painLevel;
    }
    
    public PriorityLevel getPriority() {
        return overallPriority;
    }

    public int getPriorityScore() {
        return overallPriorityScore;
    }
    
    public String getCurrentIllness() {
        return currentIllness;
    }
    
    // Create copies of the reference type objects to ensure
    // immutability
    public List<String> getMedicalHistory() {
        return new ArrayList<>(medicalHistory);
    }
    
    public List<String> getSocialFactors() {
        return new ArrayList<>(socialFactors);
    }
    
    public Map<String, Double> getVitalSigns() {
        return new HashMap<>(vitalSigns);
    }
    
    public void setCurrentIllness(String illness) {
        this.currentIllness = illness;
    }

    public void setPainLevel(int painLevel) throws IllegalArgumentException {
        if (painLevel >= 1 && painLevel <= 10) {
            this.painLevel = painLevel; 
            return;
        }
        throw new IllegalArgumentException("Pain level must be between 1 and 10");
    }
    
    public void addMedicalHistory(String condition) {
        if (!medicalHistory.contains(condition)) {
            medicalHistory.add(condition);
        }
    }
    
    public void addVitalSign(String sign, double value) {
        vitalSigns.put(sign, value);
    }
    
    public void addSocialFactor(String factor) {
        if (!socialFactors.contains(factor)) {
            socialFactors.add(factor);
        }
    }

    public void calculatePriority() {
        PriorityLevel agePriority = classifyByAge();
        PriorityLevel medicalHistoryPriority = classifyByMedicalHistory();
        PriorityLevel illnessPriority = classifyByCurrentIllness();
        PriorityLevel vitalSignsPriority = classifyByVitalSigns();
        PriorityLevel painPriority = classifyByPainLevel();
        PriorityLevel socialPriority = classifyBySocialFactors();
        
        overallPriority = PriorityLevel.max(
            agePriority, medicalHistoryPriority, illnessPriority,
            vitalSignsPriority, painPriority, socialPriority
        );

        overallPriorityScore = PriorityLevel.sum(
            agePriority, medicalHistoryPriority, illnessPriority,
            vitalSignsPriority, painPriority, socialPriority
        );
    }
    
    private PriorityLevel classifyByAge() {
        if (age < 1 || age > 70) {
            return PriorityLevel.HIGH;
        } else if (age <= 70 || age >= 40) {
            return PriorityLevel.MEDIUM;
        }
        return PriorityLevel.LOW;
    }
    
    private PriorityLevel classifyByMedicalHistory() {
        if (medicalHistory == null) return PriorityLevel.LOW;

        for (String condition : medicalHistory) {
            condition = condition.toLowerCase();
            
            for (String highRisk : highRiskConditions) {
                if (condition.equals(highRisk)) {
                    return PriorityLevel.HIGH;
                }
            }

            for (String mediumRisk : mediumRiskConditions) {
                if (condition.equals(mediumRisk)) {
                    return PriorityLevel.HIGH;
                }
            }
        }
        return PriorityLevel.LOW;
    }
    
    private PriorityLevel classifyByCurrentIllness() {
        if (currentIllness == null) return PriorityLevel.LOW;

        String illness = currentIllness.toLowerCase();
        for (String highRisk : highRiskSymptoms) {
            if (illness.equals(highRisk)) {
                return PriorityLevel.HIGH;
            }
        }

        for (String mediumRisk : mediumRiskSymptoms) {
            if (illness.equals(mediumRisk)) {
                return PriorityLevel.HIGH;
            }
        }   
        
        return PriorityLevel.LOW;
    }
    
    private PriorityLevel classifyByVitalSigns() {
        PriorityLevel maxVitalPriority = PriorityLevel.LOW;

        Double temperature = vitalSigns.get("temperature");
        if (temperature != null) {
            if (temperature < 35 || temperature > 40) {
                maxVitalPriority = PriorityLevel.max(maxVitalPriority, PriorityLevel.HIGH);
                return maxVitalPriority;
            } else if (temperature < 36 || temperature > 38) {
                maxVitalPriority = PriorityLevel.max(maxVitalPriority, PriorityLevel.MEDIUM);
            }
        }

        Double oxygenSaturation = vitalSigns.get("oxygen_saturation");
        if (oxygenSaturation != null) {
            if (oxygenSaturation < 90) {
                maxVitalPriority = PriorityLevel.max(maxVitalPriority, PriorityLevel.HIGH);
                return maxVitalPriority;
            } else if (oxygenSaturation < 95) {
                maxVitalPriority = PriorityLevel.max(maxVitalPriority, PriorityLevel.MEDIUM);
            }
        }
        
        // https://www.heart.org/en/health-topics/high-blood-pressure/understanding-blood-pressure-readings
        // https://en.wikipedia.org/wiki/Hypotension
        Double bloodPressure = vitalSigns.get("blood_pressure");
        if (bloodPressure != null) {
            if (bloodPressure < 90 || bloodPressure > 180) {
                maxVitalPriority = PriorityLevel.max(maxVitalPriority, PriorityLevel.HIGH);
                return maxVitalPriority;
            } else if (bloodPressure > 140) {
                maxVitalPriority = PriorityLevel.max(maxVitalPriority, PriorityLevel.MEDIUM);
            }
        }
        
        return maxVitalPriority;
    }
    
    private PriorityLevel classifyByPainLevel() {
        if (painLevel >= 8) {
            return PriorityLevel.HIGH;
        } else if (painLevel >= 5) {
            return PriorityLevel.MEDIUM;
        }
        return PriorityLevel.LOW;
    }
    
    private PriorityLevel classifyBySocialFactors() {
        for (String factor : socialFactors) {
            factor = factor.toLowerCase();
            for (String highRisk : highRiskSocialFactors) {
                if (factor.contains(highRisk)) {
                    return PriorityLevel.HIGH;
                }
            }
            
            for (String mediumRisk : mediumRiskSocialFactors) {
                if (factor.contains(mediumRisk)) {
                    return PriorityLevel.MEDIUM;
                }
            }
        }
        return PriorityLevel.LOW;
    }
    
    @Override
    public String toString() {
        String message = "Patient{name=" + name;
        message += ", age=" + age;
        message += ", painLevel=" + painLevel;
        message += ", currentIllness=" + currentIllness;
        message += ", overallPriority=" + overallPriority.getDescription() + '}';

        return message;
    }

    @Override
    public int compareTo(Patient o) {
        int priorityDifference;

        // Compare priority levels. The highest priority level has
        // the highest priority
        priorityDifference = Integer.compare(
            overallPriority.getPriorityScore(),
            o.getPriority().getPriorityScore()
        );
        if (priorityDifference != 0) {
            return priorityDifference;
        }
        
        // If priorities are equal, compare overall priority scores
        // (how many factors are high medium or low). The highest
        // overall priority score has the highest priority
        priorityDifference = Integer.compare(
            overallPriorityScore, o.getPriorityScore()
        );
        if (priorityDifference != 0) {
            return priorityDifference;
        }
        
        // if priorities and overall priority scores are equal, use age
        // distance to optimum age in the low priority range (18-40).
        // The highest age distance has the highest priority (very young
        // or very old patients).
        int optimumAge = (18 + 40) / 2;
        priorityDifference = Integer.compare(
            Math.abs(this.age - optimumAge), Math.abs(o.getAge() - optimumAge)
        );
        if (priorityDifference != 0) {
            return priorityDifference;
        }

        // if age is equal, use pain level: highest pain level has
        // highest priority
        priorityDifference = Integer.compare(painLevel, o.getPainLevel());
        if (priorityDifference != 0) {
            return priorityDifference;
        }
        
        // if age is equal, use patient ID: lowest ID has highest
        // priority as it was the first to be registered
        return -Integer.compare(id, o.getId());
    }
}