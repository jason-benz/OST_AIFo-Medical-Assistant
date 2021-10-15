import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import com.google.cloud.dialogflow.v2.*;

public class DialogflowConnector {
    private static final String PROJECT_ID = "medical-assistant-jgrt";
    private static final String LANGUAGE_CODE = "en-US";

    private static String sessionId;

    public DialogflowConnector() {
        sessionId = UUID.randomUUID().toString();
    }

    public String getAnswer(String inputText, MedicalAssistant medicalAssistant) throws IOException, ParseException {
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            DetectIntentResponse response = getResponse(sessionsClient, inputText);
            QueryResult queryResult = response.getQueryResult();

            handleIntent(queryResult, medicalAssistant);

            return queryResult.getFulfillmentText();
        }
    }

    private DetectIntentResponse getResponse(SessionsClient sessionsClient, String inputText) {
        SessionName session = SessionName.of(PROJECT_ID, sessionId);
        TextInput.Builder textInput = TextInput.newBuilder().setText(inputText).setLanguageCode(LANGUAGE_CODE);
        QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
        return sessionsClient.detectIntent(session, queryInput);
    }

    private void handleIntent(QueryResult queryResult, MedicalAssistant medicalAssistant) throws ParseException {
        String intent = queryResult.getIntent().getDisplayName();

        switch (intent) {
            case "check_symptoms":
                boolean hasMayCorona = queryResult.getFulfillmentMessagesOrBuilder(0).toString().contains("corona-virus");
                medicalAssistant.updateHealthInfo(queryResult.getParameters(), hasMayCorona);
                break;
        }
    }
}