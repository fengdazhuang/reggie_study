package com.fzz.reggie.controller;

import cn.hutool.core.lang.PatternPool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fzz.reggie.bean.User;
import com.fzz.reggie.common.R;
import com.fzz.reggie.service.UserService;
import com.fzz.reggie.utils.SMSUtils;
import com.fzz.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping("/sendMsg")
    public R<String> sendMessage(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();
        Pattern mobile = PatternPool.MOBILE;
        Matcher matcher = mobile.matcher(phone);

        if(!matcher.matches()){
            R.error("手机号格式错误");
        }

        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码为：{}",code);
//            SMSUtils.sendMessage("fengdazhuang","",phone,code);
            redisTemplate.opsForValue().set(phone,code);

            R.success("验证码发送成功");
        }

        return R.error("验证码发送失败");


    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone = map.get("phone").toString();
        String userCode = map.get("code").toString();

        String code = (String) redisTemplate.opsForValue().get(phone);
        if(code!=null && code.equals(userCode)){
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

            if(user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登陆失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出登录成功");
    }
}
