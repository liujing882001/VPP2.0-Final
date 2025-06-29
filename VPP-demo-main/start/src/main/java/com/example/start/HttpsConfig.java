//package com.example.start;
//
//import org.apache.catalina.connector.Connector;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author lxh
// * @Decription 配置同时支持 HTTP 与 HTTPS 访问
// * @date 2024/6/4
// **/
//@Configuration
//public class HttpsConfig {
//    @Value("${server.httpPort}")
//    private Integer httpPort;
//
//    @Value("${server.port}")
//    private Integer httpsPort;
//
//
//    @Bean
//    public ServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory()
////        {
////            @Override
////            protected void postProcessContext(Context context) {
////                SecurityConstraint securityConstraint = new SecurityConstraint();
////                securityConstraint.setUserConstraint("CONFIDENTIAL");
////                SecurityCollection collection = new SecurityCollection();
////                collection.addPattern("/*");
////                securityConstraint.addCollection(collection);
////                context.addConstraint(securityConstraint);
////            }
////        }
//        ;
//        tomcat.addAdditionalTomcatConnectors(createHttpConnector());
//        return tomcat;
//    }
//
//    /**
//     * 创建HTTP连接器
//     */
//    private Connector createHttpConnector() {
//        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
////        connector.setScheme("http");
//        connector.setPort(httpPort);
////        connector.setSecure(false);
////        connector.setRedirectPort(httpsPort);
//        return connector;
//    }
//}