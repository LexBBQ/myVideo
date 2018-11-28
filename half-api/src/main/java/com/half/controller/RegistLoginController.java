package com.half.controller;
import com.half.pojo.Users;
import com.half.service.UserService;
import com.half.utils.LexJSONResult;
import com.half.utils.MD5Utils;
import com.half.utils.RedisOperator;
import com.half.vo.UsersVo;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@Api(value = "用户注册登录的接口",tags = {"注册和登录的controller"})
public class RegistLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperator redisOperator;
    public final String USERSESSIONID="user-session-id";
    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @PostMapping("/regist")
    public LexJSONResult regist( @RequestBody Users users) throws Exception {
        //1.判断用户名和密码是否为空
        if (StringUtils.isEmpty(users.getUsername())){
            return LexJSONResult.errorMsg("用户名不能为空");
        }
        if (StringUtils.isEmpty(users.getPassword())){
            return LexJSONResult.errorMsg("密码不能为空");
        }
        //2.查询数据库看该用户名是否已经被注册
        Users userIsExist = userService.findUserIsExist(users);
        //如果为空说明没有注册过
        if (!StringUtils.isEmpty(userIsExist)){
            return LexJSONResult.errorMsg("该用户名已经被注册");
        }
        //3.初始化用户信息，并存入数据库
        String md5Str = MD5Utils.getMD5Str(users.getPassword());
        users.setPassword(md5Str);
        users.setNickname(users.getUsername());
        users.setFansCounts(0);
        users.setFollowCounts(0);
        users.setReceiveLikeCounts(0);
        Users save = userService.save(users);
        save.setPassword("");
        String token=UUID.randomUUID().toString();
        redisOperator.set(USERSESSIONID+":"+save.getId(),token,60*30);
        UsersVo usersVo =new UsersVo();
        BeanUtils.copyProperties(save,usersVo);
        usersVo.setToken(token);
        return LexJSONResult.ok();
    }


    @ApiOperation(value = "用户登录", notes = "用户登录的接口")
    @PostMapping("/login")
    public LexJSONResult login(@RequestBody Users users) throws Exception {

        //判断用户名和密码不能为空
        if (StringUtils.isEmpty(users.getUsername())){
            return LexJSONResult.errorMsg("用户名不能为空");
        }
        if (StringUtils.isEmpty(users.getPassword())){
            return LexJSONResult.errorMsg("密码不能为空");
        }
        //查询该用户是否存在

        Users userIsExist = userService.findUserIsExist(users);
        if (StringUtils.isEmpty(userIsExist)){
            return LexJSONResult.errorMsg("对不起，该用户不存在");
        }

        //检验密码和用户名是否相符
        users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
        Users u = userService.checkPassword(users);
        if (StringUtils.isEmpty(u)){
            return LexJSONResult.errorMsg("对不起，你输入的密码不正确");
        }
        u.setPassword("");


        String token=UUID.randomUUID().toString();
        redisOperator.set(USERSESSIONID+":"+u.getId(),token,60*30);
        UsersVo usersVo =new UsersVo();
        BeanUtils.copyProperties(u,usersVo);
        usersVo.setToken(token);

        return LexJSONResult.ok(usersVo);
    }

    @ApiOperation(value = "用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "String" ,paramType = "query")
    @PostMapping("/logout")
    public LexJSONResult logout(String id){
        //将redis中的数据删除
        redisOperator.del(USERSESSIONID+":"+id);
        return LexJSONResult.ok();
    }
}
