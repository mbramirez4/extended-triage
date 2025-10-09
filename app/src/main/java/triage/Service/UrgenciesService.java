package triage.Service;

import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.PriorityQueue;
import java.io.IOException;

import triage.Model.Patient;
import triage.Model.PriorityLevel;
import triage.Service.PatientLoaderService;


public class UrgenciesService {
    private static int waitingPatients = 0;
    private static int nHighPriorityToTreat = 3;
    private static int nMediumPriorityToTreat = 2;
    private static int nLowPriorityToTreat = 1;

    private static PriorityQueue<Patient> highPriority = new PriorityQueue<>(Collections.reverseOrder());
    private static PriorityQueue<Patient> mediumPriority = new PriorityQueue<>(Collections.reverseOrder());
    private static PriorityQueue<Patient> lowPriority = new PriorityQueue<>(Collections.reverseOrder());
    
    public static void registerPatient(
        String name,
        int age,
        int painLevel,
        String currentIllness,
        List<String> medicalHistory,
        List<String> socialFactors,
        Map<String, Double> vitalSigns
    ) throws IllegalArgumentException {
        Patient patient = new Patient(name, age);
        
        patient.setPainLevel(painLevel);
        patient.setCurrentIllness(currentIllness);
        
        for (String condition : medicalHistory) {
            patient.addMedicalHistory(condition);
        }
        for (String factor : socialFactors) {
            patient.addSocialFactor(factor);
        }
        for (String sign : vitalSigns.keySet()) {
            patient.addVitalSign(sign, vitalSigns.get(sign));
        }
        addPatient(patient);
    }

    public static void attendPatients() {
        while (waitingPatients > 0) {
            dequeueCycle();
        }
    }

    public static void loadPatientsFromJsonFile(String filePath) throws IOException, IllegalArgumentException {
        List<Patient> patients = PatientLoaderService.loadPatientsFromFile(filePath);
        
        for (Patient patient : patients) {
            addPatient(patient);
        }
    }

    private static void addPatient(Patient patient) throws IllegalArgumentException {
        patient.calculatePriority();
        switch (patient.getPriority()) {
            case PriorityLevel.HIGH:
                highPriority.add(patient);
                break;
            case PriorityLevel.MEDIUM:
                mediumPriority.add(patient);
                break;
            case PriorityLevel.LOW:
                lowPriority.add(patient);
                break;
            default:
                throw new IllegalArgumentException("Invalid priority level");
        }

        waitingPatients++;
    }

    private static void dequeueCycle() {
        /* It is checked if each queue has patients to be treated at the
        beginning of the cycle; in that case, the proportion is
        maintained regardless of the number of patients to be treated in
        the given queue.
         */
        int nHigh = highPriority.isEmpty() ? 0 : nHighPriorityToTreat;
        int nMedium = mediumPriority.isEmpty() ? 0 : nMediumPriorityToTreat;
        int nLow = lowPriority.isEmpty() ? 0 : nLowPriorityToTreat;

        float scaleFactor = nHighPriorityToTreat + nMediumPriorityToTreat + nLowPriorityToTreat;
        scaleFactor = scaleFactor / (float)(nHigh + nMedium + nLow);

        nHigh = (int) Math.floor(nHigh * scaleFactor);
        nMedium = (int) Math.floor(nMedium * scaleFactor);
        nLow = (int) Math.floor(nLow * scaleFactor);

        for (int i = 0; i < nHigh; i++) {
            if (highPriority.isEmpty()) {
                break;
            }
            highPriority.poll();
            waitingPatients--;
        }
        for (int i = 0; i < nMedium; i++) {
            if (mediumPriority.isEmpty()) {
                break;
            }
            mediumPriority.poll();
            waitingPatients--;
        }
        for (int i = 0; i < nLow; i++) {
            if (lowPriority.isEmpty()) {
                break;
            }
            lowPriority.poll();
            waitingPatients--;
        }
    }
}
