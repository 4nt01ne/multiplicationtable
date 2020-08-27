package be.web;

import be.controller.ExercisesController;
import be.controller.ExercisesControllerInterface;
import be.controller.MultiplicationExercisesController;
import be.exception.NotFoundExercisesException;
import be.model.Exercise;
import be.model.MultiplicationExercise;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
//FIXME limit the memory by setting a maximum exercises at the sames time
//FIXME add a watch dog to clean old exercises
public class RestMultiplicationExercises extends RouteBuilder {
  @Autowired
  private ExercisesController exercisesController;

  @Value("${server.port}")
  String serverPort;

  @Value("${web.camel.urlMapping}")
  String contextPath;

  @Override
  public void configure() throws Exception {

    onException(NotFoundExercisesException.class)
      .handled(true)
      .process(exchange -> exchange.getIn().setBody(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage()))
      .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
      .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_FOUND.value()));

    restConfiguration().contextPath(contextPath)
        .port(serverPort)
        .enableCORS(true)
        .apiContextPath("/api-doc")
        .apiProperty("api.title", "Test REST API")
        .apiProperty("api.version", "v1")
        .apiProperty("cors", "true")
        .apiContextRouteId("doc-api")
        .component("servlet")
        .dataFormatProperty("prettyPrint", "true");

    rest().description("Exercises REST Service")
        .produces(MediaType.APPLICATION_JSON_VALUE)
        .consumes(MediaType.APPLICATION_JSON_VALUE)
        .bindingMode(RestBindingMode.json)
        .get("exercises")
          .type(ExercisesControllerInterface.class)
          .id("rest-exercises")
          .to(exercises("exercises"))
        .post("wantedExercises/{id}")
          .id("rest-wantedExercises")
          .to(setWantedExercises("wantedExercises"))
        .get("hasNext/{id}")
          .type(Map.class)
          .id("rest-hasNext")
          .to(hasNext("hasNext"))
        .get("next/{id}")
          .type(Exercise.class)
          .id("rest-next")
          .to(next("next"))
        .post("result/{id}")
          .type(MultiplicationExercise.class)
          .id("rest-setResult")
          .to(setResult("setResult"))
        .get("result/{id}")
          .type(String.class)
          .id("rest-getResult")
          .to(getResult("getResult"))
        .get("duration/{id}")
          .type(Map.class)
          .id("rest-getDuration")
          .to(duration("getDuration"))
        .get("maxExercises/{id}")
          .type(Map.class)
          .id("rest-getMaxExercises")
          .to(getMaxExercises("getMaxExercises"))
        .get("requestedExercises/{id}")
          .type(Map.class)
          .id("rest-getRequestedExercises/{id}")
          .to(getRequestedExercises("getRequestedExercises"));
  }

  public String exercises(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .routeId(endpointName)
      .process(exchange -> exchange.getIn().setBody(exercisesController.createExercise(MultiplicationExercisesController.class)));
    return endpointUri;
  }

  public String setWantedExercises(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .routeId(endpointName)
      .process(exchange -> exercisesController.setWantedExercises(extractId(exchange), exchange.getIn().getHeader("wanted", Integer.class)));
    return endpointUri;
  }

  public String hasNext(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .routeId(endpointName)
      .process(exchange -> {
        Map<String, Boolean> result = new HashMap<>(1);
        result.put("hasNext", exercisesController.hasNext(extractId(exchange)));
        exchange.getIn().setBody(result);
      });
    return endpointUri;
  }

  public String next(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .routeId(endpointName)
      .setBody(exchange -> {
        try {
          Exercise next = exercisesController.next(extractId(exchange));
          if(next != null) {
            return next;
          } else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NO_CONTENT.value()));
            return null;
          }
        } catch (NotFoundExercisesException e) {
          exchange.setException(e);
          return null;
        }
      });
    return endpointUri;
  }

  public String setResult(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .routeId(endpointName)
      .process(exchange -> exercisesController.setResult(extractId(exchange), exchange.getIn().getBody(Exercise.class)));
    return endpointUri;
  }

  public String getResult(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .routeId(endpointName)
      .process(exchange -> exchange.getIn().setBody(exercisesController.result(extractId(exchange))));
    return endpointUri;
  }

  public String duration(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .routeId(endpointName)
      .process(exchange -> {
        Map<String, Long> result = new HashMap<>(1);
        result.put("durationSeconds", exercisesController.duration(extractId(exchange)).getSeconds());
        exchange.getIn().setBody(result);
      });
    return endpointUri;
  }

  public String getMaxExercises(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .routeId(endpointName)
      .process(exchange -> {
        Map<String, Integer> result = new HashMap<>(1);
        result.put("maxExercises", exercisesController.getMaxExercises(MultiplicationExercisesController.class));
        exchange.getIn().setBody(result);
      });
    return endpointUri;
  }

  public String getRequestedExercises(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .routeId(endpointName)
      .process(exchange -> {
        Map<String, Integer> result = new HashMap<>(1);
        result.put("requestedExercises", exercisesController.getActualExercises(extractId(exchange)));
        exchange.getIn().setBody(result);
      });
    return endpointUri;
  }

  private String extractId(Exchange exchange) {
    return exchange.getIn().getHeader("id").toString();
  }
}
