package be.web;

import be.controller.ExercicesController;
import be.exception.NotFoundExercisesException;
import be.model.Exercise;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
//FIXME limit the memory by setting a maximum exercises at the sames time
//FIXME add a watch dog to clean old exercises
public class RestExercises extends RouteBuilder {
  @Autowired
  private ExercicesController exercicesController;

  @Value("${server.port}")
  String serverPort;

  @Value("${web.path}")
  String contextPath;

  @Override
  public void configure() throws Exception {

    onException(NotFoundExercisesException.class)
      .handled(true)
      .process(exchange -> exchange.getIn().setBody(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage()))
      .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
      .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404));

    restConfiguration().contextPath(contextPath)
        .port(serverPort)
        .enableCORS(true)
        .apiContextPath("/api-doc")
        .apiProperty("api.title", "Test REST API")
        .apiProperty("api.version", "v1")
        .apiProperty("cors", "true")
        .apiContextRouteId("doc-api")
        .component("servlet")
        //.bindingMode(RestBindingMode.json)
        .dataFormatProperty("prettyPrint", "true");

    rest().description("Exercices REST Service")
        .produces(MediaType.APPLICATION_JSON_VALUE)
        .consumes(MediaType.APPLICATION_JSON_VALUE)
        .get("exercises")
          .to(exercises("exercises"))
        .post("wantedExercises/{id}")
          .to(setWantedExercises("wanted-exercises"))
        .get("hasNext/{id}")
          .to(hasNext("has-next"))
        .get("next/{id}")
          .to(next("next"))
        .post("result/{id}")
          .to(setResult("setResult"))
        .get("result/{id}")
          .to(result("getResult"));

  }

  public String exercises(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .process(exchange -> exchange.getIn().setBody(exercicesController.createExercise()))
      .marshal().json(JsonLibrary.Jackson);
    return endpointUri;
  }

  public String setWantedExercises(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .process(exchange -> exercicesController.setWantedExercices(extractId(exchange), exchange.getIn().getHeader("wanted", Integer.class)));
    return endpointUri;
  }

  public String hasNext(String endpointName) {

    Map<String, Boolean> result = new HashMap<>(1);

    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .process(exchange -> {
        result.put("hasNext", exercicesController.hasNext(extractId(exchange)));
        exchange.getIn().setBody(result);
      });
    return endpointUri;
  }

  public String next(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .setBody(exchange -> {
        try {
          return exercicesController.next(extractId(exchange));
        } catch (NotFoundExercisesException e) {
          exchange.setException(e);
          return null;
        }
      })
      .marshal().json(JsonLibrary.Jackson);
    return endpointUri;
  }

  public String setResult(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .unmarshal().json(JsonLibrary.Jackson, Exercise.class)
      .process(exchange -> exercicesController.setResult(extractId(exchange), exchange.getIn().getBody(Exercise.class)));
    return endpointUri;
  }

  public String result(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .process(exchange -> exchange.getIn().setBody(exercicesController.result(extractId(exchange))));
    return endpointUri;
  }


//  public String duration(String endpointName) {
//    String endpointUri = "direct:" + endpointName;
//from(endpointUri)
//.process(exchange -> controller.setWantedExercices(exchange.getIn().getHeader("wanted", Integer.class)));
//    return exercises.duration();
//  }
//
//  public String getMaxExercices(String endpointName) {
//    String endpointUri = "direct:" + endpointName;
//from(endpointUri)
//.process(exchange -> controller.setWantedExercices(exchange.getIn().getHeader("wanted", Integer.class)));
//    return Exercises.getMaxExercices();
//  }
//
//  public String getActualExercices(String endpointName) {
//    String endpointUri = "direct:" + endpointName;
//from(endpointUri)
//.process(exchange -> controller.setWantedExercices(exchange.getIn().getHeader("wanted", Integer.class)));
//    return exercises.getCount();
//  }
  private String extractId(Exchange exchange) {
    return exchange.getIn().getHeader("id").toString();
  }
}
