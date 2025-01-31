package workoutlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class WorkoutLogApplication {

  public static void main(String[] args) {
    SpringApplication.run(WorkoutLogApplication.class, args);
  }
}
