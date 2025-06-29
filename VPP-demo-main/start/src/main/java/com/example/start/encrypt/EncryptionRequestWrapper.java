package com.example.start.encrypt;

import com.example.vvpcommom.Encrypted;
import com.example.vvpscheduling.util.bean.SpringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 自定义HttpServletRequestWrapper
 * qxc
 * 20240622
 */
public class EncryptionRequestWrapper extends HttpServletRequestWrapper {

    private String body;

    private RequestMappingHandlerMapping handlerMapping;
    private boolean hasAnnotation = false;

    public EncryptionRequestWrapper(HttpServletRequest request) {
        super(request);
        this.handlerMapping = SpringUtils.getBean("requestMappingHandlerMapping");
        this.hasAnnotation = checkForAnnotation(request);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        body = stringBuilder.toString();
    }

    private boolean checkForAnnotation(HttpServletRequest request) {
        try {
            HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
            if (handlerExecutionChain != null) {
                HandlerMethod handler = (HandlerMethod) handlerExecutionChain.getHandler();
                Encrypted methodAnnotation = AnnotationUtils.findAnnotation(handler.getMethod(), Encrypted.class);

                if (methodAnnotation != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 处理异常
        }
        return false;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }
            @Override
            public boolean isReady() {
                return false;
            }
            @Override
            public void setReadListener(ReadListener readListener) {
            }
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;

    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean hasAnnotation() {
        return hasAnnotation;
    }
}

