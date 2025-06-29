package com.example.vvpweb.presalesmodule.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChatModel {

    @ApiModelProperty("请求id，不能为空")
    private String requestId;
    @ApiModelProperty("会话id，不能为空")
    private String sessionId;
    @ApiModelProperty("系统（特定下游任务标识），请先设置为null，后续将根据具体情况更改设置的值")
    private String system;
//    @ApiModelProperty("历史记录（同时包含用户输入和模型返回的内容），其中的结构依照OpenAI多轮对话的输入格式设计，具体可参考其说明文档，无历史会话时请设置为null")
//    private String history;
    @ApiModelProperty("用户输入，不能为空")
    private String input;
//    @ApiModelProperty("文档内容，可为空(null)")
//    private String doc_content;
    @ApiModelProperty("文件id")
    private List<String> fileId;

}
