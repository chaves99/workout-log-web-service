package workoutlog.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ExerciseController {

  @GetMapping("/exercise")
  public String exercise() {
    return new String("Hello");
  }
}
