package be.web;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import be.controller.ExercicesController;
import be.model.Exercise;

@Component
public class RestExercises extends RouteBuilder {
  @Autowired
  private ExercicesController exercicesController;

  @Value("${server.port}")
  String serverPort;

  @Value("${web.path}")
  String contextPath;

  @Override
  public void configure() throws Exception {

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
        .post("wantedExercices")
          .to(setWantedExercises("wanted-exercices"))
        .get("hasNext")
          .to(hasNext("has-next"))
        .get("next")
          .to(next("next"))
        .post("result")
          .to(setResult("setResult"))
        .get("result")
          .to(result("getResult"));
  }

  public String setWantedExercises(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .process(exchange -> exercicesController.setWantedExercices(exchange.getIn().getHeader("wanted", Integer.class)));
    return endpointUri;
  }

  public String hasNext(String endpointName) {

    Map<String, Boolean> result = new HashMap<>(1);

    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .process(exchange -> {
        result.put("hasNext", exercicesController.hasNext());
        exchange.getIn().setBody(result);
      });
    return endpointUri;
  }

  public String next(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .setBody(exchange -> exercicesController.next())
      .marshal().json();
    return endpointUri;
  }

  public String setResult(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .streamCaching()
      .unmarshal().json(JsonLibrary.Jackson, Exercise.class)
      .process(exchange -> exercicesController.setResult(exchange.getIn().getBody(Exercise.class)));
    return endpointUri;
  }

  public String result(String endpointName) {
    String endpointUri = "direct:" + endpointName;
    from(endpointUri)
      .process(exchange -> exchange.getIn().setBody(exercicesController.result()));
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
}
