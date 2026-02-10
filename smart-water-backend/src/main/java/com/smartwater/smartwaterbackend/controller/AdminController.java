package com.smartwater.smartwaterbackend.controller;

import com.smartwater.smartwaterbackend.entity.DecisionLog;
import com.smartwater.smartwaterbackend.mapper.DecisionLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DecisionLogMapper mapper;

    @GetMapping("/decisions")
    public List<DecisionLog> list() {
        return mapper.selectList(null);
    }
}