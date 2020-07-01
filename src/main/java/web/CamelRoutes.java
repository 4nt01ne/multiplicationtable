package web;

import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CamelRoutes extends RouteBuilder {
  
  @Value("${server.port}")
  String serverPort;
  
  @Value("${web.path}")
  String contextPath;
  
  @Override
  public void configure() throws Exception {
    CamelContext context = new DefaultCamelContext();
    
    restConfiguration().contextPath(contextPath)
        .port(serverPort)
        .enableCORS(true)
        .apiContextPath("/api-doc")
        .apiProperty("api.title", "Test REST API")
        .apiProperty("api.version", "v1")
        .apiProperty("cors", "true")
        .apiContextRouteId("doc-api")
        .component("servlet")
        .bindingMode(RestBindingMode.json)
        .dataFormatProperty("prettyPrint", "true");
    
    rest().description("Teste REST Service")
        .id("api-route")
        .get()
          .produces(MediaType.APPLICATION_JSON)
          .to("direct:hello");
    

    from("direct:hello")
        .routeId("direct")
        .tracing()
        .process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                HashMap<String, String> body = new HashMap<>();
                body.put("message", "hello world");
                exchange.getIn().setBody(body);
            }
        });
  }
}
