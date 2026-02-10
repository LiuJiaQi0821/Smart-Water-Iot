package com.smartwater.smartwaterbackend.utils;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    // 预留加密API
    public String encrypt(String rawPwd) { return rawPwd; }
    // 预留校验API
    public boolean match(String rawPwd, String dbPwd) { return rawPwd.equals(dbPwd); }
}