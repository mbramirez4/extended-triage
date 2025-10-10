package triage.Service;

import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.PriorityQueue;
import java.io.IOException;

import triage.Model.Patient;
import triage.Model.PriorityLevel;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class UrgenciesService {
    private static final Logger logger = LogManager.getLogger(UrgenciesService.class);

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
        
        logger.info("Loaded " + patients.size() + " patients from JSON file: " + filePath);
        
        for (Patient patient : patients) {
            try{
                addPatient(patient);
            } catch (IllegalArgumentException e) {
                logger.error("Error adding patient: " + patient + "\n" + e.getMessage(), e);
            }
        }

        logger.info("Patients enqueued. Total number of waiting patients: " + waitingPatients);
    }

    private static void addPatient(Patient patient) throws IllegalArgumentException {
        patient.calculatePriority();

        switch (patient.getPriority()) {
            case PriorityLevel.HIGH:
                highPriority.add(patient);
                logger.debug("Patient added to high priority queue: ", patient);
                break;
            case PriorityLevel.MEDIUM:
                mediumPriority.add(patient);
                logger.debug("Patient added to medium priority queue: ", patient);
                break;
            case PriorityLevel.LOW:
                lowPriority.add(patient);
                logger.debug("Patient added to low priority queue: ", patient);
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
        Patient patient;

        int nHigh = highPriority.isEmpty() ? 0 : nHighPriorityToTreat;
        int nMedium = mediumPriority.isEmpty() ? 0 : nMediumPriorityToTreat;
        int nLow = lowPriority.isEmpty() ? 0 : nLowPriorityToTreat;

        float scaleFactor = nHighPriorityToTreat + nMediumPriorityToTreat + nLowPriorityToTreat;
        scaleFactor = scaleFactor / (float)(nHigh + nMedium + nLow);

        if (scaleFactor != 1) {
            logger.info("Some queues are empty. Scaling the number of patients to be treated.");
            nHigh = (int) Math.floor(nHigh * scaleFactor);
            nMedium = (int) Math.floor(nMedium * scaleFactor);
            nLow = (int) Math.floor(nLow * scaleFactor);

            logger.info("New number of patients to be treated: High: " + nHigh + ", Medium: " + nMedium + ", Low: " + nLow);
        }

        for (int i = 0; i < nHigh; i++) {
            if (highPriority.isEmpty()) {
                break;
            }
            patient = highPriority.poll();
            logger.info("Patient dequeued from the High priority queue: " + patient);
            waitingPatients--;
        }
        for (int i = 0; i < nMedium; i++) {
            if (mediumPriority.isEmpty()) {
                break;
            }
            patient = mediumPriority.poll();
            logger.info("Patient dequeued from the Medium priority queue: " + patient);
            waitingPatients--;
        }
        for (int i = 0; i < nLow; i++) {
            if (lowPriority.isEmpty()) {
                break;
            }
            patient = lowPriority.poll();
            logger.info("Patient dequeued from the Low priority queue: " + patient);
            waitingPatients--;
        }
    }
}
