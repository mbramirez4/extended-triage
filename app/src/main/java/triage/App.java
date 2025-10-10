package triage;

import java.io.IOException;

import triage.Service.UrgenciesService;

public class App {
    public static void main(String[] args) {
        try {
            // Load patients from the sample JSON file
            String filePath = "app/src/main/resources/sample_patients.json";
            UrgenciesService.loadPatientsFromJsonFile(filePath);
            
            UrgenciesService.attendPatients();
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing patients: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
