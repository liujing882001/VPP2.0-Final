package com.example.vvpweb.presalesmodule.model;

import lombok.Data;

@Data
public class ChatRequest {

    //会话id
    private String conversation_id;
    //系统
    private String system;
    //历史记录
    private String history;
    //输入
    private String input;
    //文档内容
    private String doc_content;
    private String user_id;

    public ChatRequest() {}
    public ChatRequest(ChatModel model) {
        this.conversation_id = model.getSessionId();
        this.input = model.getInput();
    }
}
