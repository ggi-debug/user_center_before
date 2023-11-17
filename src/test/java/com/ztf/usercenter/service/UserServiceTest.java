package com.ztf.usercenter.service;

import com.ztf.usercenter.model.domain.User;
import com.ztf.usercenter.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 * @author 张腾飞
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("euwe432455");
        user.setUserAccount("123");
        user.setAvatarUrl("https://tse3-mm.cn.bing.net/th/id/OIP-C.6szqS1IlGtWsaiHQUtUOVwHaQC?w=115&h=180&c=7&r=0&o=5&pid=1.7");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123");
        user.setEmail("123");
        boolean result = userService.save(user);
        System.out.println("用户的id为"+result);
        Assertions.assertTrue(result);
    }

    @Test
    public void userAccountVerification(){
        String userAccount = "wangfi33123";
        String userPassword = "321432321432";
        String checkPassword = "321432321432";
        long register = userService.userRegister(userAccount, userPassword, checkPassword);
        // System.out.println("99999999"+register);
        // Assertions.assertEquals(11,register);
    }
}
