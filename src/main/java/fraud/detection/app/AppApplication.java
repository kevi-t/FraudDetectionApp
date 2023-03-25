package fraud.detection.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        System.out.println("APPLICATION STARTING");
        SpringApplication.run(AppApplication.class, args);
    }
}