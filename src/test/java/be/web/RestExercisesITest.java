package be.web;

import be.model.Exercise;
import be.model.Exercises;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestExercisesITest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void setWantedExercisesTest() {
        Exercises exercises = newExercises();
        HttpStatus statusCode = setWantedExercises(exercises.getId(), 2);
        assertEquals(HttpStatus.NO_CONTENT, statusCode);
    }

    @Test
    public void hasNextResultTest() {
        Exercises exercises = newExercises();
        setWantedExercises(exercises.getId(), 2);
        Exercise result = nextExercise(exercises.getId());
        assertNotNull(result);
        ResponseEntity<Map> response = restTemplate.getForEntity("/hasNext/" + exercises.getId(), Map.class);
        Map body = response.getBody();
        assertTrue(body.containsKey("hasNext"));
        assertTrue(Boolean.valueOf(body.get("hasNext").toString()));

        result = nextExercise(exercises.getId());
        assertNotNull(result);
        response = restTemplate.getForEntity("/hasNext/" + exercises.getId(), Map.class);
        body = response.getBody();
        assertTrue(body.containsKey("hasNext"));
        assertFalse(Boolean.valueOf(body.get("hasNext").toString()));
    }

    @Test
    public void setResultTest() {
        Exercises exercises = newExercises();
        setWantedExercises(exercises.getId(), 1);
        Exercise result = nextExercise(exercises.getId());
        result.setResult(result.getFirstInt() * result.getSecondInt());
        ResponseEntity<String> response = restTemplate.postForEntity("/result/" + exercises.getId(), result, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void cannotSetUnknownResultTest() {
        Exercise fakeResult = new Exercise();
        ResponseEntity<String> response = restTemplate.postForEntity("/result/" + fakeResult.getId(), fakeResult, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private Exercises newExercises() {
        ResponseEntity<Exercises> response = restTemplate.getForEntity("/exercises", Exercises.class);
        return response.getBody();
    }
    private HttpStatus setWantedExercises(String exercisesId, int wanted) {
        return restTemplate.postForEntity("/wantedExercises/" + exercisesId + "?wanted=" + wanted, null, null).getStatusCode();
    }

    private Exercise nextExercise(String exercisesId) {
        ResponseEntity<Exercise> response = restTemplate.getForEntity("/next/" + exercisesId, Exercise.class);
        return response.getBody();
    }
}
