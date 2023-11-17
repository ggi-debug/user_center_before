package com.ztf.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ztf.usercenter.model.domain.request.UserLoginRequest;
import com.ztf.usercenter.model.domain.request.UserRegisterRequest;
import com.ztf.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.ztf.usercenter.model.domain.User;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ztf.usercenter.contant.UserContant.ADMIN_ROLE;
import static com.ztf.usercenter.contant.UserContant.USER_LOGIN_STATE;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return  null;
        }
        return userService.userRegister(userAccount,userPassword,checkPassword);
    }


    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return  null;
        }
        return userService.userLogin(userAccount,userPassword,request);
    }


    /**
     * 根据用户名搜索该用户的用户信息
     * @return 返回该用户的用户信息
     */
    @GetMapping("/search")
    public List<User> userSearch(String username,HttpServletRequest request){
        // 仅管理员可查询
        if(!isAdmin(request))  {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }

        List<User> userList = userService.list(queryWrapper);
        return userList.stream().peek(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
    }


    /**
     * 根据用户名id删除用户
     * @return 返回删除用户的id
     */
    @PostMapping("/delete")
    public Boolean userDelete(@RequestBody long userId,HttpServletRequest request){
        // 仅管理员可查询
        if(!isAdmin(request))  {
            return false;
        }
        if(userId <= 0){
            return false;
        }
        return  userService.removeById(userId);
    }

    /**
     * 是否为管理员
     */
    private Boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user.getStrators() == ADMIN_ROLE && user != null;
    }
}
