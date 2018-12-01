package com.half.controller;

import com.half.pojo.Users;
import com.half.service.UserService;
import com.half.utils.LexJSONResult;
import com.half.vo.PublishVo;
import com.half.vo.UsersVo;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @ApiOperation(value = "用户头像上传", notes = "用户头像上传的接口")
    @PostMapping("/upload")
    public LexJSONResult upload(String id, @RequestParam("file") MultipartFile[] file) throws IOException {
        if (StringUtils.isEmpty(id)){
            return LexJSONResult.errorMsg("id不能为空");
        }
        //创建一个文件输出流对象
        FileOutputStream fileOutputStream=null;
        //创建一个输入流对象
        InputStream inputStream=null;
        //文件的命名空间
        String fileSpace="F:/file";
        //文件保存的相对路径
        String upLoadPathDb="/"+id+"/face";
        try {
            //判断传入的文件是否为空
            if (file!=null && file.length>0){
                //获取到传入的文件名称
                String filename = file[0].getOriginalFilename();
                //判断该文件名是否为空
                if (!StringUtils.isEmpty(filename)){
                    //获得该文件的绝对路径
                    String finalPath=fileSpace+upLoadPathDb+"/"+filename;
                    //获得该文件存入数据库的相对路径
                    upLoadPathDb=upLoadPathDb+"/"+filename;
                    File f=new File(finalPath);
                    if (f.getParentFile()!=null || !f.getParentFile().isDirectory()){
                        //创建该文件所在目录的文件夹
                        f.getParentFile().mkdirs();
                    }
                    fileOutputStream=new FileOutputStream(f);
                    inputStream=file[0].getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                    Users u=new Users();
                    u.setId(id);
                    u.setFaceImage(upLoadPathDb);
                    //将用户id和头像相对路径传入，调用更新方法
                    userService.updateUserInfo(u);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (fileOutputStream!=null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        //将文件的相对路径返回给前端，用来做头像的回显
        return LexJSONResult.ok(upLoadPathDb);
    }

    @ApiOperation(value = "用户信息查询", notes = "用户信息查询的接口")
    @PostMapping("/findUserInfo")
    public LexJSONResult findUserInfo(String id,String fansId){
        Boolean followed=false;
        if (!StringUtils.isEmpty(fansId)){
             followed = userService.findIsFollowed(id, fansId);
        }
        Users users= userService.findUserInfo(id);
        UsersVo usersVo=new UsersVo();
        BeanUtils.copyProperties(users,usersVo);
        usersVo.setFollowed(followed);
        return LexJSONResult.ok(usersVo);
    }

    @ApiOperation(value = "用户点赞功能", notes = "用户点赞功能")
    @PostMapping("/like")
    public LexJSONResult like(String id,String videoId,String videoCreatedId){
        userService.like(id,videoId,videoCreatedId);
        return LexJSONResult.ok();
    }

    @ApiOperation(value = "用户取消点赞功能", notes = "用户取消点赞功能")
    @PostMapping("/unlike")
    public LexJSONResult unlike(String id,String videoId,String videoCreatedId){

        userService.unlike(id,videoId,videoCreatedId);
        return LexJSONResult.ok();
    }


    @ApiOperation(value = "查询视频发布者信息", notes = "查询视频发布者信息")
    @PostMapping("/findPublish")
    public LexJSONResult findPublish(String id,String videoId,String publishId){
     //查询视频发布者的信息
        Users userInfo = userService.findUserInfo(publishId);
        userInfo.setPassword("");
        //查询该用户是否给该视频点赞
        Boolean b = userService.findUserIsLike(id, videoId);
        PublishVo publishVo =new PublishVo();
        publishVo.setUsers(userInfo);
        publishVo.setIsLike(b);
        return LexJSONResult.ok(publishVo);
    }


    @ApiOperation(value = "用户关注接口", notes = "用户关注接口")
    @PostMapping(value = "/follow")
    public LexJSONResult follow(String id,String fansId){
        userService.follow(id,fansId);
        Boolean b = userService.findIsFollowed(id, fansId);
        return LexJSONResult.ok(b);
    }

    @ApiOperation(value = "用户取消关注接口", notes = "用户取消关注接口")
    @PostMapping(value = "/unfollow")
    public LexJSONResult unfollow(String id,String fansId){
        userService.unfollow(id,fansId);
        Boolean b = userService.findIsFollowed(id, fansId);
        return LexJSONResult.ok(b);
    }
}
