package com.smartwater.smartwaterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartwater.smartwaterbackend.entity.DeviceData;
import com.smartwater.smartwaterbackend.mapper.DeviceDataMapper;
import com.smartwater.smartwaterbackend.service.SmartWaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor //
public class MonitorController {

    private final SmartWaterService service;
    private final DeviceDataMapper mapper;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestBody DeviceData data) {
        service.processData(data);
        return Map.of("code", 200);
    }

    @GetMapping("/search")
    public IPage<DeviceData> search(
            @RequestParam(required = false) String deviceId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<DeviceData> page = new Page<>(current, size);

        QueryWrapper<DeviceData> query = new QueryWrapper<>();
        if (deviceId != null && !deviceId.isEmpty()) {
            query.eq("device_id", deviceId);
        }

        query.orderByDesc("record_time");

        return mapper.selectPage(page, query);
    }
}