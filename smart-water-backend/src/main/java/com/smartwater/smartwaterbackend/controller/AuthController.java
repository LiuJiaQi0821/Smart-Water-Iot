package com.smartwater.smartwaterbackend.controller;

import com.smartwater.smartwaterbackend.entity.User;
import com.smartwater.smartwaterbackend.service.SmartWaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SmartWaterService service;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User req) {
        User user = service.login(req.getUsername(), req.getPassword());
        Map<String, Object> res = new HashMap<>();
        if (user != null) {
            res.put("code", 200);
            res.put("data", user);
        } else {
            res.put("code", 401);
            res.put("msg", "失败");
        }
        return res;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User req) {
        boolean ok = service.register(req);
        return Map.of("code", ok ? 200 : 400, "msg", ok ? "成功" : "已存在");
    }
}