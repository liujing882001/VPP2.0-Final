package com.example.vvpdomain.entity;


import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chat_prompt")
public class ChatPrompt implements Serializable {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "query_type")
    private String queryType;
    @Column(name = "prompt")
    private String prompt;
    @Column(name = "dynamic_date")
    private String dynamicDate;
}
