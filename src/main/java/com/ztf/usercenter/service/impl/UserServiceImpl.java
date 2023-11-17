package com.ztf.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ztf.usercenter.model.domain.User;
import com.ztf.usercenter.service.UserService;
import com.ztf.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ztf.usercenter.contant.UserContant.USER_LOGIN_STATE;

/**
 * @author k_dababases
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-11-09 23:25:44
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    /**
     * 加密 盐
     */
    private static final String SALT = "zhangtengfei";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        // 校验非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }

        // 账户不小于4位
        if (userAccount.length() < 4) {
            return -1;
        }

        // 密码和确认密码不小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 校验账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1;
        }

        // 账户不能包含特殊字符
        String regEx = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }

        // 对用户输入密码进行加密
        String asHex = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(asHex);
        int saveResult = userMapper.insert(user);
        if (saveResult != 1) {
            return -1;
        }
        return user.getId();
    }



    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }

        // 账户不小于4位
        if (userAccount.length() < 4) {
            return null;
        }

        // 密码不小于8位
        if (userPassword.length() < 8) {
            return null;
        }



        // 账户不能包含特殊字符
        String regEx = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        // 对用户输入密码进行加密
        String asHex = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());


        //登录账户是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", asHex);
        User userLogin = userMapper.selectOne(queryWrapper);
        if(userLogin == null){
            log.info("没有这个用户");
            return null;
        }

        // 用户脱敏
        User safetyuser = getSafetyUser(userLogin);
        // 4. 记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,userLogin);

        return safetyuser;
    }

    /**
     * 用户脱敏
     */
    @Override
    public User getSafetyUser(User originUser){
        User safetyuser = new User();
        safetyuser.setId(originUser.getId());
        safetyuser.setUsername(originUser.getUsername());
        safetyuser.setUserAccount(originUser.getUserAccount());
        safetyuser.setAvatarUrl(originUser.getAvatarUrl());
        safetyuser.setGender(originUser.getGender());
        safetyuser.setPhone(originUser.getPhone());
        safetyuser.setEmail(originUser.getEmail());
        safetyuser.setUserStatus(originUser.getUserStatus());
        safetyuser.setCreateTime(originUser.getCreateTime());
        safetyuser.setStrators(originUser.getStrators());
        return safetyuser;
    }
}




