import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MedicalAssistant {
    private ArrayList<String> symptomTypes;
    private int symptomIntensity;
    private Date symptomStart;
    private int symptomDuration;
    private boolean hasMayCorona;

    public void updateHealthInfo(Struct parameters, boolean hasMayCorona) throws ParseException {
        this.hasMayCorona = hasMayCorona;
        Set<Map.Entry<String, Value>> entries = parameters.getFieldsMap().entrySet();

        for (Map.Entry<String, Value> entry : entries) {
            Value value = entry.getValue();

            switch (entry.getKey()) {
                case "SymIntensity":
                    if (!value.getStringValue().isEmpty()) {
                        setSymptomIntensity(Integer.parseInt(value.getStringValue()));
                    }
                    break;
                case "SymStart":
                    if (!value.getStringValue().isEmpty()) {
                        setSymptomStart(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(value.getStringValue()));
                    }
                    break;
                case "SymType":
                    if (value.getListValue().getValuesCount() > 0) {
                        setSymptomType(value.getListValue().getValuesList());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public String getReport() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String symptomTypesString = (symptomTypes == null ? "-" : symptomTypes.toString());

        return "Symptom type(s):\t" + symptomTypesString + "\n" +
               "Is may corona:\t\t" + (symptomTypesString.equals("-") ? "-" : hasMayCorona) + "\n" +
               "Symptom intensity:\t" + (symptomIntensity == 0 ? "-" : symptomIntensity) + "\n" +
               "Symptom start:\t\t" + (symptomStart == null ? "-" : df.format(symptomStart)) + "\n" +
               "Symptom duration:\t" + (symptomDuration == 0 ? "-" : symptomDuration) + "\n";
    }

    public ArrayList<String> getSymptomTypes() {
        return symptomTypes;
    }

    public void setSymptomType(List<Value> symptomTypes) {
        this.symptomTypes = new ArrayList<>();
        for (var value : symptomTypes) {
            this.symptomTypes.add(value.getStringValue());
        }
    }

    public int getSymptomIntensity() {
        return symptomIntensity;
    }

    public void setSymptomIntensity(int symptomIntensity) {
        this.symptomIntensity = symptomIntensity;
    }

    public Date getSymptomStart() {
        return symptomStart;
    }

    public void setSymptomStart(Date symptomStart) {
        this.symptomStart = symptomStart;
        setSymptomDuration(symptomStart);
    }

    public int getSymptomDuration() {
        return symptomDuration;
    }

    public void setSymptomDuration(Date symptomStart) {
        symptomDuration = (int) ChronoUnit.DAYS.between(symptomStart.toInstant(), new Date().toInstant());
    }
}
