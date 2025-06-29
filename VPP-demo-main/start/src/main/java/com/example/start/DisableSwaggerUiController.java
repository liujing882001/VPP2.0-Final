//package com.example.start;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Profile;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Profile("!swagger")
//@RestController
//@Slf4j
//public class DisableSwaggerUiController {
//
//    @RequestMapping(value = "swagger-ui.html", method = RequestMethod.GET)
//    public void getSwagger(HttpServletResponse httpResponse) throws IOException {
//        redirect(httpResponse);
//    }
//
//    @RequestMapping(value = "doc.html", method = RequestMethod.GET)
//    public void getSwaggerUi(HttpServletResponse httpResponse) throws IOException {
//        redirect(httpResponse);
//    }
//
//    @RequestMapping(value = "/swagger-resources", method = RequestMethod.GET)
//    public void getSwaggerResources(HttpServletResponse httpResponse) throws IOException {
//        redirect(httpResponse);
//    }
//
//
//    @RequestMapping(value = "/swagger-resources/configuration/security", method = RequestMethod.GET)
//    public void getSwaggerResourcesConfigurationSecurity(HttpServletResponse httpResponse) throws IOException {
//        redirect(httpResponse);
//    }
//
//
//    @RequestMapping(value = "/swagger-resources/configuration/security/", method = RequestMethod.GET)
//    public void getSwaggerResourcesConfigurationSecurity_(HttpServletResponse httpResponse) throws IOException {
//        redirect(httpResponse);
//    }
//
//    @RequestMapping(value = "/swagger-resources/configuration/ui", method = RequestMethod.GET)
//    public void getSwaggerResourcesConfigurationUi(HttpServletResponse httpResponse) throws IOException {
//        redirect(httpResponse);
//    }
//
//
//    @RequestMapping(value = "/swagger-resources/configuration/ui/", method = RequestMethod.GET)
//    public void getSwaggerResourcesConfigurationUi_(HttpServletResponse httpResponse) throws IOException {
//        redirect(httpResponse);
//    }
//
//    @RequestMapping(value = "/index", method = RequestMethod.GET)
//    public void getIndex(HttpServletResponse httpResponse) throws IOException {
//        redirect(httpResponse);
//    }
//
//    private void redirect(HttpServletResponse httpResponse) throws IOException {
//        //1. 设置状态码为302
//        httpResponse.setStatus(302);
//        //简单的重定向⽅法
//        httpResponse.sendRedirect("/index.html");
//    }
//}