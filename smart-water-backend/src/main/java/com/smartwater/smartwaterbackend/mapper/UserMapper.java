package com.smartwater.smartwaterbackend.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwater.smartwaterbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {}