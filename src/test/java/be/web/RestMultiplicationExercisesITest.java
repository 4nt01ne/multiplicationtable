package be.web;

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

import java.time.Instant;
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
        ResponseEntity<Map> response = restTemplate.getForEntity("/requestedExercises/" + exercises.getId(), Map.class);
        Map body = response.getBody();
        assertTrue(body.containsKey("requestedExercises"));
        assertEquals(2, body.get("requestedExercises"));
    }

    @Test
    public void hasNextResultTest() {
        MultiplicationExercisesController exercises = newExercises();
        setWantedExercises(exercises.getId(), 2);
        MultiplicationExercise exercise = nextExercise(exercises.getId());
        assertNotNull(exercise);

        ResponseEntity<Map> mapResponse = restTemplate.getForEntity("/hasNext/" + exercises.getId(), Map.class);
        Map body = mapResponse.getBody();
        assertTrue(body.containsKey("hasNext"));
        assertTrue(Boolean.valueOf(body.get("hasNext").toString()));

        exercise = nextExercise(exercises.getId());
        assertNotNull("expected to receive a non null exercise",exercise);
        assertNotNull(exercise.getId());
        assertNotNull(exercise.duration());
        assertFalse(exercise.isCorrect());

        mapResponse = restTemplate.getForEntity("/hasNext/" + exercises.getId(), Map.class);
        body = mapResponse.getBody();
        assertTrue(body.containsKey("hasNext"));
        assertFalse(Boolean.valueOf(body.get("hasNext").toString()));

        assertNull(nextExercise(exercises.getId()));
    }

    @Test
    public void setResultTest() {
        MultiplicationExercisesController exercises = newExercises();
        setWantedExercises(exercises.getId(), 1);
        MultiplicationExercise result = nextExercise(exercises.getId());
        result.setResult(String.valueOf(result.getFirstInt() * result.getSecondInt()));
        ResponseEntity<String> response = restTemplate.postForEntity("/result/" + exercises.getId(), result, String.class);
        assertEquals("expected HTTP 200 OK response code", HttpStatus.OK, response.getStatusCode());

        response = restTemplate.getForEntity("/result/" + exercises.getId(), String.class);
        assertTrue("expected successful result", response.getBody().contains("1/1"));

        exercises = newExercises();
        setWantedExercises(exercises.getId(), 1);
        result = nextExercise(exercises.getId());
        result.setResult(String.valueOf(-1));
        response = restTemplate.postForEntity("/result/" + exercises.getId(), result, String.class);
        assertEquals("expected HTTP 200 OK response code", HttpStatus.OK, response.getStatusCode());

        response = restTemplate.getForEntity("/result/" + exercises.getId(), String.class);
        assertTrue("expected failure result", response.getBody().contains("0/1"));
    }

    @Test
    public void cannotSetUnknownResultTest() {
        MultiplicationExercise fakeResult = new MultiplicationExercise();
        ResponseEntity<String> response = restTemplate.postForEntity("/result/" + fakeResult.getId(), fakeResult, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void durationTest() throws InterruptedException {
        MultiplicationExercisesController exercises = newExercises();
        HttpStatus statusCode = setWantedExercises(exercises.getId(), 1);
        nextExercise(exercises.getId());
        assertEquals(HttpStatus.NO_CONTENT, statusCode);

        Thread.sleep(1000);

        ResponseEntity<Map> response = restTemplate.getForEntity("/duration/" + exercises.getId(), Map.class);
        Map body = response.getBody();
        assertTrue(body.containsKey("durationSeconds"));
        Object durationSeconds = body.get("durationSeconds");
        assertTrue("exercises should have lasted more than 1 second but was " + durationSeconds, Instant.ofEpochSecond((int) durationSeconds).minusSeconds(1).toEpochMilli() >= 0);
    }

    @Test
    public void getMaxExercisesTest() throws InterruptedException {
        MultiplicationExercisesController exercises = newExercises();
        HttpStatus statusCode = setWantedExercises(exercises.getId(), 1);
        assertEquals(HttpStatus.NO_CONTENT, statusCode);

        ResponseEntity<Map> response = restTemplate.getForEntity("/maxExercises/" + exercises.getId(), Map.class);
        Map body = response.getBody();
        assertTrue(body.containsKey("maxExercises"));
        Object maxExercises = body.get("maxExercises");
        assertEquals("max multiplication exercises not correct", MultiplicationExercisesController.maxCombinations(), maxExercises);
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
