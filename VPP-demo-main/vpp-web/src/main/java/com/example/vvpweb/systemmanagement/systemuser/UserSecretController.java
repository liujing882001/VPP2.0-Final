package com.example.vvpweb.systemmanagement.systemuser;

import com.example.vvpcommom.PreAuthorize;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.UserSecretRepository;
import com.example.vvpdomain.entity.UserSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.transaction.Transactional;
import java.util.UUID;

@RestController
@RequestMapping("/userSecret")
public class UserSecretController {

    @Autowired
    private UserSecretRepository userSecretRepository;

    /**
     * 创建需求响应接入用户
     * @param model
     * @return
     */
    @UserLoginToken
    @PreAuthorize("system:user:change_pwd")
    @PostMapping(value = "addUserSecret")
    @Transactional
    public ResponseResult addUserSecret(@RequestBody UserSecret model) {

        if (StringUtils.isEmpty(model.getUserName())) {
            throw new RuntimeException("userName 不可为空");
        }
        if (StringUtils.isEmpty(model.getThirdPublicKey())) {
            throw new RuntimeException("thirdPublicKey 不可为空");
        }
        UserSecret u = new UserSecret();
        u.setUserName(model.getUserName());
        u.setThirdPublicKey(model.getThirdPublicKey());
        u.setUserId(UUID.randomUUID().toString());
        u.setUserPassword(genSecretKey());
        userSecretRepository.save(u);

        return ResponseResult.success(u);
    }

    private String genSecretKey() {
        try {
            // 创建KeyGenerator对象
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            // 初始化密钥生成器，指定密钥长度（128位、192位或256位）
            keyGen.init(256); // 可以是128, 192, 256

            // 生成密钥
            SecretKey secretKey = keyGen.generateKey();
            return java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
