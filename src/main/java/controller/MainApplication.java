package controller;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages =  {"controller", "web"})
public class MainApplication {

  @Value("${web.path}")
  String contextPath;
  
  public static void main(String[] args) {
    SpringApplication.run(MainApplication.class, args);
  }
  
  @Bean
  ServletRegistrationBean<CamelHttpTransportServlet> servletRegistrationBean() {
      ServletRegistrationBean<CamelHttpTransportServlet> servlet = new ServletRegistrationBean<>(new CamelHttpTransportServlet(), contextPath);
      servlet.setName("CamelServlet");
      return servlet;
  }
  
  
}