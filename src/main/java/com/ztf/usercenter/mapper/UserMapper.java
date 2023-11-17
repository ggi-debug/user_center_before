package com.ztf.usercenter.mapper;

import com.ztf.usercenter.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author k_dababases
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2023-11-09 23:25:44
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




