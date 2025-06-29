package com.example.vvpdomain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_secret")
public class UserSecret implements Serializable {


    /**
     * 用户id
     */
    @Id
    @Column(name = "user_id")
    private String userId;

    /**
     * 用户名称
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 公钥
     */
    @Column(name = "third_public_key")
    private String thirdPublicKey;

    /**
     * 密码
     */
    @Column(name = "user_password")
    private String userPassword;

}
