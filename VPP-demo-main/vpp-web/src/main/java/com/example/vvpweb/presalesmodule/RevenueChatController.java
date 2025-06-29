package com.example.vvpweb.presalesmodule;

import com.alibaba.fastjson.JSON;
import com.example.vvpweb.presalesmodule.model.ChatModel;
import com.example.vvpweb.presalesmodule.model.ChatRequest;
import com.example.vvpweb.presalesmodule.model.EleBillModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EnableAsync
@Slf4j
@RestController
@RequestMapping("/revenueChat")
@CrossOrigin
@EnableScheduling
@Configuration
@Api(value = "收益管理-对话", tags = {"收益管理-对话"})
public class RevenueChatController {

    @Value("${chat.server.elebill.url}")
    private String chatServer;

    @Value("${chat.server.authorization}")
    private String chatServerAuth;
    private final Map<String, SseEmitter> sessionEmitters = new ConcurrentHashMap<>();

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSse(HttpServletRequest request, @RequestBody ChatModel model, HttpServletResponse response) {
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("X-Accel-Buffering", "no");
        String useId = request.getHeader("authorizationcode");
        String sessionId = model.getSessionId().isEmpty() ? UUID.randomUUID().toString() : model.getSessionId();
//        log.info("sessionId: {}", sessionId);
        log.info("model传参:{}", JSON.toJSONString(model));
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sessionEmitters.put(sessionId, emitter);

        ChatRequest chat = new ChatRequest(model);
        chat.setConversation_id(sessionId);
        chat.setInput(model.getInput());
        chat.setDoc_content("");
        chat.setSystem("收益管理");
        chat.setUser_id(useId);
        log.info("chat:{}",JSON.toJSONString(chat));
        boolean[] isCompleted = {false};
        try {
            StringBuilder sysContent = new StringBuilder();
            sendSseEvent(emitter, new HashMap<String, String>() {{
                put("requestId", model.getRequestId());
                put("sessionId", sessionId);
            }});
            OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(Duration.ofMinutes(3)).build();
            okhttp3.MediaType mediaType = okhttp3.MediaType.get("application/json");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, JSON.toJSONString(chat));
            Request request11 = new Request.Builder()
                    .url(chatServer)
                    .header("Authorization", chatServerAuth)
                    .addHeader("Accept", "text/event-stream")
                    .post(body)
                    .build();
            new Thread(() -> handleResponse(client, request11, emitter, isCompleted, sessionId, sysContent)).start();
        } catch (Exception e) {
            handleError(emitter, e);
        } finally {
            emitter.onCompletion(() -> {
                isCompleted[0] = true;
                emitter.complete();
                sessionEmitters.remove(sessionId);
                log.info("清除");
            });
            emitter.onTimeout(() -> log.info("超时"));
        }
        return emitter;
    }

    private void handleResponse(OkHttpClient client, Request request, SseEmitter emitter, boolean[] isCompleted
            , String sessionId, StringBuilder sysContent) {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Algorithm Request failed with HTTP error code: " + response.code());
            okhttp3.ResponseBody responseBody = response.body();
            if (responseBody == null || isCompleted[0]) return;
            BufferedSource source = responseBody.source();
            while (!source.exhausted()) {
                String data = source.readUtf8Line();
                if (data != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    EleBillModel model = mapper.readValue(data, EleBillModel.class);
                    sysContent.append(model.getText());
                    sendSseEvent(emitter, isCompleted, model);
                    if ("1".equals(model.getIs_finished())) {
                        isCompleted[0] = true;
                        emitter.complete();
                        sessionEmitters.remove(sessionId);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            sessionEmitters.remove(sessionId);
            handleError(emitter, e);
        } finally {
            log.info("储存");
        }
    }
    private void sendSseEvent(SseEmitter emitter, Object event) {
        synchronized (emitter) {
            try {
                emitter.send(SseEmitter.event().data(event));
            } catch (IOException e) {
                log.error("Error while sending SSE event first time: {}", e.getMessage());
                emitter.completeWithError(e);
            }
        }
    }

    private void sendSseEvent(SseEmitter emitter, boolean[] isCompleted, Object event) {
        synchronized (emitter) {
            if (isCompleted[0]) return;
            try {
                emitter.send(SseEmitter.event().data(event));
            } catch (IOException e) {
                log.error("Error while sending SSE event: {}", e.getMessage());
                emitter.completeWithError(e);
                isCompleted[0] = true;
            }
        }
    }

    private void handleError(SseEmitter emitter, Exception e) {
        log.error("Error while streaming data: {}", e.getMessage());

        synchronized (emitter) {
            try {
                emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
            } catch (IOException ioException) {
                log.error("Error while sending error event: {}", ioException.getMessage());
            } finally {
                log.info("handleError完成");
                emitter.complete();
            }
        }
    }

}
