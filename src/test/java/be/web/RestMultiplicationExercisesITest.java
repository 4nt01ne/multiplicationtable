package be.web;

import be.model.Exercise;
import be.controller.MultiplicationExercisesController;
import be.model.MultiplicationExercise;
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
public class RestMultiplicationExercisesITest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void setWantedExercisesTest() {
        MultiplicationExercisesController exercises = newExercises();
        HttpStatus statusCode = setWantedExercises(exercises.getId(), 2);
        assertEquals(HttpStatus.NO_CONTENT, statusCode);
    }

    @Test
    public void hasNextResultTest() {
        MultiplicationExercisesController exercises = newExercises();
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
        MultiplicationExercisesController exercises = newExercises();
        setWantedExercises(exercises.getId(), 1);
        MultiplicationExercise result = nextExercise(exercises.getId());
        result.setResult(String.valueOf(result.getFirstInt() * result.getSecondInt()));
        ResponseEntity<String> response = restTemplate.postForEntity("/result/" + exercises.getId(), result, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void cannotSetUnknownResultTest() {
        MultiplicationExercise fakeResult = new MultiplicationExercise();
        ResponseEntity<String> response = restTemplate.postForEntity("/result/" + fakeResult.getId(), fakeResult, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private MultiplicationExercisesController newExercises() {
        ResponseEntity<MultiplicationExercisesController> response = restTemplate.getForEntity("/exercises", MultiplicationExercisesController.class);
        return response.getBody();
    }
    private HttpStatus setWantedExercises(String exercisesId, int wanted) {
        return restTemplate.postForEntity("/wantedExercises/" + exercisesId + "?wanted=" + wanted, null, null).getStatusCode();
    }

    private MultiplicationExercise nextExercise(String exercisesId) {
        ResponseEntity<MultiplicationExercise> response = restTemplate.getForEntity("/next/" + exercisesId, MultiplicationExercise.class);
        return response.getBody();
    }
}
