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
    private Boolean hasMaybeCorona = null;
    private Date doctorAppointment;

    public void updateHealthInfo(Struct parameters) throws ParseException {
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

    public void updateAppointment(Struct parameters) throws ParseException {
        var appointment = parameters.getFieldsMap().get("AppointmentTime").getStringValue();
        doctorAppointment = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(appointment);
    }

    public void checkCorona(Struct webhookPayload) {
        var payload = webhookPayload.getFieldsMap().entrySet();
        for (Map.Entry<String, Value> entry : payload) {
            if (entry.getKey().equals("hasMaybeCorona")) {
                this.hasMaybeCorona = entry.getValue().getBoolValue();
            }
        }
    }

    public String getReport() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat dtf = new SimpleDateFormat("dd.MM.yyyy - hh:mm");

        String symptomTypesString = (symptomTypes == null ? "-" : symptomTypes.toString());

        return "Symptom type(s):\t" + symptomTypesString + "\n" +
               "Is maybe corona:\t" + (hasMaybeCorona == null ? "-" : hasMaybeCorona) + "\n" +
               "Symptom intensity:\t" + (symptomIntensity == 0 ? "-" : symptomIntensity) + "\n" +
               "Symptom start:\t\t" + (symptomStart == null ? "-" : df.format(symptomStart)) + "\n" +
               "Symptom duration:\t" + (symptomDuration == 0 ? "-" : symptomDuration) + "\n" +
               "Doctor appointment:\t" + (doctorAppointment == null ? "-" : dtf.format(doctorAppointment) + "\n");
    }

    private void setSymptomType(List<Value> symptomTypes) {
        this.symptomTypes = new ArrayList<>();
        for (var value : symptomTypes) {
            this.symptomTypes.add(value.getStringValue());
        }
    }

    private void setSymptomIntensity(int symptomIntensity) {
        this.symptomIntensity = symptomIntensity;
    }

    private void setSymptomStart(Date symptomStart) {
        this.symptomStart = symptomStart;
        this.symptomDuration = (int) ChronoUnit.DAYS.between(symptomStart.toInstant(), new Date().toInstant());
    }
}
