package be.web;

import org.junit.Assert;
import org.junit.Ignore;
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
@Ignore("FIXME contract has changed")
public class RestExercisesTest {
  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void setWantedExercisesTest() {
    ResponseEntity<String> response = restTemplate.postForEntity("/wantedExercices?wanted=2",null, String.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void setResultTest() {
    String result = "{\n" +
            "  \"firstInt\": 8,\n" +
            "  \"secondInt\": 8,\n" +
            "  \"result\": 16,\n" +
            "  \"start\": 1593983669888,\n" +
            "  \"id\": \"2f427944-343c-42ee-a1ad-dfb2935f01fe\",\n" +
            "  \"correct\": false\n" +
            "}";
    ResponseEntity<String> response = restTemplate.postForEntity("/result",result, String.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
