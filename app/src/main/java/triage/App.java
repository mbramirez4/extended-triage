package triage;

import java.io.IOException;

import triage.Service.UrgenciesService;

public class App {
    public static void main(String[] args) {
        try {
            // Load patients from the sample JSON file
            String filePath = "app/src/main/resources/sample_patients.json";
            UrgenciesService.loadPatientsFromJsonFile(filePath);
            
            // Run a cycle of treatment
            UrgenciesService.dequeueCycle();

            // Add more patients to the waiting queue
            filePath = "app/src/main/resources/test_patients.json";
            UrgenciesService.loadPatientsFromJsonFile(filePath);
            
            UrgenciesService.attendPatients();
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing patients: " + e.getMessage());
            e.printStackTrace();
        }

    System.out.println("\n============= EXECUTION SUMMARY =============\n");
    System.out.println("High priority patients treated: " + UrgenciesService.getNumberHighPriorityTreated());
    System.out.println("Medium priority patients treated: " + UrgenciesService.getNumberMediumPriorityTreated());
    System.out.println("Low priority patients treated: " + UrgenciesService.getNumberLowPriorityTreated());
    System.out.println("Longest wait time: " + UrgenciesService.getLongestWaitTime());
    System.out.println("Patient with longest wait time: " + UrgenciesService.getPatientWithLongestWaitTime());
    System.out.println("Total treated patients: " + UrgenciesService.getNumberTreatedPatients());
    System.out.println("Total waiting patients: " + UrgenciesService.getNumberWaitingPatients());

    System.out.println("\n============= PATIENTS DISTRIBUTION =============\n");
    System.out.printf("%-15s %-10s%n", "Priority", "Count");
    System.out.printf("%-15s %-10d%n", "High", UrgenciesService.getNumberHighPriorityTreated());
    System.out.printf("%-15s %-10d%n", "Medium", UrgenciesService.getNumberMediumPriorityTreated());
    System.out.printf("%-15s %-10d%n", "Low", UrgenciesService.getNumberLowPriorityTreated());
    System.out.printf("%-15s %-10d%n", "Total", UrgenciesService.getNumberTreatedPatients());
    }

}
