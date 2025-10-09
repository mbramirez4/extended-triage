package triage.Service;

import java.util.Map;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import triage.Model.Patient;

public class PatientDeserializer implements JsonDeserializer<Patient> {
    @Override
    public Patient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        JsonObject jsonObject = json.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        int age = jsonObject.get("age").getAsInt();
        
        Patient patient = new Patient(name, age);

        if (jsonObject.has("pain_level") && !jsonObject.get("pain_level").isJsonNull()) {
            int painLevel = jsonObject.get("pain_level").getAsInt();
            try {
                patient.setPainLevel(painLevel);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Invalid pain level: " + painLevel, e);
            }
        }
        
        if (jsonObject.has("current_illness") && !jsonObject.get("current_illness").isJsonNull()) {
            String currentIllness = jsonObject.get("current_illness").getAsString();
            patient.setCurrentIllness(currentIllness);
        }
        
        if (jsonObject.has("medical_history") && jsonObject.get("medical_history").isJsonArray()) {
            JsonArray medicalHistoryArray = jsonObject.getAsJsonArray("medical_history");
            for (JsonElement element : medicalHistoryArray) {
                if (!element.isJsonNull()) {
                    patient.addMedicalHistory(element.getAsString());
                }
            }
        }
        
        if (jsonObject.has("social_factors") && jsonObject.get("social_factors").isJsonArray()) {
            JsonArray socialFactorsArray = jsonObject.getAsJsonArray("social_factors");
            for (JsonElement element : socialFactorsArray) {
                if (!element.isJsonNull()) {
                    patient.addSocialFactor(element.getAsString());
                }
            }
        }
        
        if (jsonObject.has("vital_signs") && jsonObject.get("vital_signs").isJsonObject()) {
            JsonObject vitalSignsObject = jsonObject.getAsJsonObject("vital_signs");
            for (Map.Entry<String, JsonElement> entry : vitalSignsObject.entrySet()) {
                if (!entry.getValue().isJsonNull()) {
                    String sign = entry.getKey();
                    double value = entry.getValue().getAsDouble();
                    patient.addVitalSign(sign, value);
                }
            }
        }
        
        return patient;
    }
}
