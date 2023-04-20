package fraud.detection.app.utils;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LogFileCreator {
        public static void createLogFile(String error, String metadata) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("error.log", true));
                writer.write("Timestamp: " + timestamp + " | Error message: " + error + " | Metadata: " + metadata + "\n");
                writer.close();
            }
            catch (IOException e) {
                System.out.println("Unable to create log file.");
                e.printStackTrace();
            }
        }
}