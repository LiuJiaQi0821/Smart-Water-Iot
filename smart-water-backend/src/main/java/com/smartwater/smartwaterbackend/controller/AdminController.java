package com.smartwater.smartwaterbackend.controller;

import com.smartwater.smartwaterbackend.entity.DecisionLog;
import com.smartwater.smartwaterbackend.entity.User;
import com.smartwater.smartwaterbackend.mapper.DecisionLogMapper;
import com.smartwater.smartwaterbackend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DecisionLogMapper mapper;
    private final UserMapper userMapper;

    @GetMapping("/decisions")
    public List<DecisionLog> list() {
        return mapper.selectList(null);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userMapper.selectList(null);
    }
}