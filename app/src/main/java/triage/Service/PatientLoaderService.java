package triage.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import triage.Model.Patient;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public class PatientLoaderService {
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Patient.class, new PatientDeserializer())
        .create();

    public static List<Patient> loadPatientsFromFile(String filePath) throws IOException, JsonSyntaxException {
        try (Reader reader = new FileReader(filePath)) {
            Type patientListType = new TypeToken<List<Patient>>(){}.getType();
            List<Patient> patients = gson.fromJson(reader, patientListType);
            
            return patients;
        }
    }

    public static List<Patient> loadPatientsFromJson(String jsonString) throws JsonSyntaxException {
        Type patientListType = new TypeToken<List<Patient>>(){}.getType();
        List<Patient> patients = gson.fromJson(jsonString, patientListType);
        
        return patients;
    }
}
