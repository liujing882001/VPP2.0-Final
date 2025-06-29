package com.example.vvpweb.systemmanagement.systemuser.UserLoginService;

import com.example.vvpcommom.ResponseResult;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@Service
public interface UserService {
    String getSmsCode(String phone) throws Exception;
    String register(String phone, String smsCode, String ip) throws ParseException;
    ResponseResult logOut(String phone, HttpServletRequest request) throws ParseException;
}
