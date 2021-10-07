import java.io.IOException;

import com.google.cloud.dialogflow.v2.*;

public class MedicalAssistant {
    private static final String projectId = "medical-assistant-jgrt";
    private static final String sessionId = "JavaClientSession";
    private static final String text = "Hello World";
    private static final String languageCode = "en-US";

    public static void main (String[] args) throws IOException
    {
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.println("Session Path: " + session.toString());

            TextInput.Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(languageCode);
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

            QueryResult queryResult = response.getQueryResult();
            String intent = queryResult.getIntent().getDisplayName();

            System.out.println(intent);
            System.out.println(queryResult.getFulfillmentText());
        }
    }
}
