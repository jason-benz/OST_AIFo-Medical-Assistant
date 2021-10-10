import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

public class Program {
    public static void main (String[] args) throws IOException {
        MedicalAssistant medicalAssistant = new MedicalAssistant();
        DialogflowConnector dialogflowConnector = new DialogflowConnector();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            printWelcomeMessage();
            while (true) {
                String line = br.readLine();
                if (line.equals("")) {
                    continue;
                }
                if (line.equals("q")) {
                    break;
                }
                if (line.equals("v")) {
                    System.out.println(medicalAssistant.getReport());
                    continue;
                }
                System.out.println(dialogflowConnector.getAnswer(line, medicalAssistant));
            }
            System.out.println("Goodbye");
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }
    }

    private static void printWelcomeMessage() {
        System.out.println("Hello, I'm your medical assistant. How can I help you?");
        System.out.println("Please enter your request and confirm with enter. " +
                           "Other options: q to quit, v to view your medical report");
        System.out.println();
    }
}
