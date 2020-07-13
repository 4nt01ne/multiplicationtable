package be.web;

import be.model.Exercise;
import be.model.Exercises;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestExercisesITest {
  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void setWantedExercisesTest() throws JsonProcessingException {
    Exercises exercises = newExercises(2);
    ResponseEntity<String> response = restTemplate.postForEntity("/wantedExercises/" + exercises.getId() + "?wanted=2",null, String.class);
    Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  public void setResultTest() throws JsonProcessingException {
    Exercises exercises = newExercises(1);
    Exercise result = nextExercise(exercises.getId());
    result.setResult(result.getFirstInt() * result.getSecondInt());
    ResponseEntity<String> response = restTemplate.postForEntity("/result/" + exercises.getId(),result, String.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  private Exercises newExercises(int wanted) throws JsonProcessingException {
    ResponseEntity<Exercises> response = restTemplate.getForEntity("/exercises?wanted=" + wanted, Exercises.class);
    return response.getBody();
  }

  private Exercise nextExercise(String exercisesId) throws JsonProcessingException {
    ResponseEntity<Exercise> response = restTemplate.getForEntity("/next/"  + exercisesId,Exercise.class);
    return response.getBody();
  }
}
