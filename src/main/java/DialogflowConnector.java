import java.io.IOException;

import com.google.cloud.dialogflow.v2.*;

public class DialogflowConnector {
    private static final String PROJECT_ID = "medical-assistant-jgrt";
    private static final String SESSION_ID = "JavaClientSession";
    private static final String LANGUAGE_CODE = "en-US";

    public static String getAnswer(String inputText, MedicalAssistant medicalAssistant) throws IOException {
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            DetectIntentResponse response = getResponse(sessionsClient, inputText);
            QueryResult queryResult = response.getQueryResult();

            String intent = queryResult.getIntent().getDisplayName();
            handleIntent(intent, medicalAssistant);

            return queryResult.getFulfillmentText();
        }
    }

    private static DetectIntentResponse getResponse(SessionsClient sessionsClient, String inputText) {
        SessionName session = SessionName.of(PROJECT_ID, SESSION_ID);
        TextInput.Builder textInput = TextInput.newBuilder().setText(inputText).setLanguageCode(LANGUAGE_CODE);
        QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
        return sessionsClient.detectIntent(session, queryInput);
    }

    private static void handleIntent(String intent, MedicalAssistant medicalAssistant) {
        switch (intent) {
            default:
        }
    }
}