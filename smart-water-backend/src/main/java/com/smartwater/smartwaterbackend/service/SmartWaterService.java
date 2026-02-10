package com.smartwater.smartwaterbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smartwater.smartwaterbackend.entity.*;
import com.smartwater.smartwaterbackend.mapper.*;
import com.smartwater.smartwaterbackend.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SmartWaterService {

    private final UserMapper userMapper;
    private final DeviceDataMapper deviceDataMapper;
    private final DecisionLogMapper decisionLogMapper;
    private final SecurityUtil securityUtil;

    public User login(String username, String password) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        User user = userMapper.selectOne(query);
        if (user != null && securityUtil.match(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean register(User user) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", user.getUsername());
        if (userMapper.selectCount(query) > 0) {
            return false;
        }

        user.setPassword(securityUtil.encrypt(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return true;
    }

    public void processData(DeviceData data) {
        if (data.getRecordTime() == null) {
            data.setRecordTime(LocalDateTime.now());
        }

        boolean isLeak = (data.getFlowRate().doubleValue() > 10.0 && data.getPressure().doubleValue() < 2.0);
        data.setLeakRisk(isLeak ? new BigDecimal("0.98") : new BigDecimal("0.02"));

        deviceDataMapper.insert(data);

        if (isLeak) {
            DecisionLog log = new DecisionLog();
            log.setLogType("LEAK_ALARM");
            log.setContent("警报：设备 " + data.getDeviceId() + " 疑似爆管，建议立即排查。");
            log.setStatus(0);
            log.setCreateTime(LocalDateTime.now());
            decisionLogMapper.insert(log);
        }
    }
}