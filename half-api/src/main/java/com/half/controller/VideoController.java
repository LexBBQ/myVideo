package com.half.controller;

import com.half.pojo.Bgm;
import com.half.pojo.Comments;
import com.half.pojo.Hot;
import com.half.pojo.Videos;
import com.half.service.BgmService;
import com.half.service.VideosService;
import com.half.utils.FFMPeg;
import com.half.utils.FFMPegCover;
import com.half.utils.LexJSONResult;
import com.half.utils.PageResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private BgmService bgmService;
    @Autowired
    private VideosService videosService;
    //该变量为ffmpeg所在的路径
    private final String FFMPEGEXE="F:\\ffmpeg\\bin\\ffmpeg.exe";
    //用户资源文件的目录
    private final  String  FILESPACE="F:/file";
    //默认每页显示数据数
    private final Integer SIZE=4;

    /**
     *
     * @param id
     * @param bgmId
     * @param videoSeconds
     * @param videoWidth
     * @param videoHeight
     * @param desc
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "用户视频上传", notes = "用户视频上传的接口")
    @PostMapping(value = "/uploadVideo",headers = "content-type=multipart/form-data")
    public LexJSONResult upload(@RequestParam("id") String id,
                                String bgmId,
                                @RequestParam("videoSeconds") Integer videoSeconds,
                                @RequestParam("videoWidth") int videoWidth,
                                @RequestParam("videoHeight") int videoHeight,
                                String desc,
                                @ApiParam(value = "短视频",required = true) MultipartFile file) throws IOException {
        if (StringUtils.isEmpty(id)){
            return LexJSONResult.errorMsg("id不能为空");
        }
        //文件保存的命名空间
        FileOutputStream fileOutputStream=null;
        InputStream inputStream=null;
        //定义视频存放的相对路径
        String upLoadPathDb="/"+id+"/video";
        //定义视频封面存放的相路径
        String coverPathDb="/"+id+"/video";
        String coverName="/"+UUID.randomUUID().toString()+".jpg";
        try {
            if (file!=null){
                String filename = file.getOriginalFilename();
                if (!StringUtils.isEmpty(filename)){
                    String finalPath=FILESPACE+upLoadPathDb+"/"+filename;
                    upLoadPathDb=upLoadPathDb+"/"+filename;
                    File f=new File(finalPath);
                    if (f.getParentFile()!=null || !f.getParentFile().isDirectory()){
                        f.getParentFile().mkdirs();
                    }
                    fileOutputStream=new FileOutputStream(f);
                    inputStream=file.getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                    if (!StringUtils.isEmpty(bgmId)){
                        //判断bgmid是否为空，如果不为空，说明用户选择了bgm，根据该bgmid对bgm进行查询
                        Bgm bgm = bgmService.findOne(bgmId);
                        //获取bgm的相对路径，并和命名空间拼接获得该bgm的绝对路径
                        String bgmPath = FILESPACE+ bgm.getPath();
                        //创建ffmpeg工具类，并传入ffmpeg.exe所在的路径
                        FFMPeg ffmPeg =new FFMPeg(FFMPEGEXE);
                        //该路径为视频合成后需要存放的相对路径
                        upLoadPathDb="/"+id+"/video"+"/"+UUID.randomUUID()+".mp4";
                        //使用uuid生成新得mp4视频名称并且和路径拼接，作为绝对路径传入ffmpeg的convert方法中
                        String videoOutPath=FILESPACE+upLoadPathDb;
                        //分别传入用户上传视频的绝对路径bgm的路径，视频的秒数，合成后视频的路径
                        ffmPeg.convert(finalPath,bgmPath,videoSeconds,videoOutPath);
                    }
                    FFMPegCover ffmPegCover =new FFMPegCover(FFMPEGEXE);
                    ffmPegCover.convert(FILESPACE+upLoadPathDb,FILESPACE+coverPathDb+coverName);
                    //TODO 保存视频到数据库
                    Videos videos =new Videos();
                    videos.setUserId(id);
                    videos.setAudioId(bgmId);
                    videos.setVideoDesc(desc);
                    videos.setStatus(1);
                    videos.setVideoSeconds((float)videoSeconds);
                    videos.setVideoHeight(videoHeight);
                    videos.setVideoWidth(videoWidth);
                    videos.setVideoPath(upLoadPathDb);
                    videos.setCreateTime(new Date());
                    videos.setLikeCounts(0l);
                    videos.setCoverPath(coverPathDb+coverName);
                    videosService.save(videos);
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
        return LexJSONResult.ok(upLoadPathDb);
    }

    @ApiOperation(value = "查询视频列表", notes = "查询视频列表")
    @PostMapping(value = "/findVideoList")
    public LexJSONResult findAll(@RequestParam(value = "searchValue",defaultValue = "",required = false) String searchValue, @RequestParam(value = "pageNum",defaultValue = "1" ,required = false) Integer pageNum){
        PageResult pageResult = videosService.findAllVideos(searchValue,pageNum, SIZE);
        return LexJSONResult.ok(pageResult);
    }

    @ApiOperation(value = "查询热搜词", notes = "查询热搜词")
    @PostMapping(value = "/hot")
    public LexJSONResult findHot(){
        List<String> list = videosService.findHot();
        return LexJSONResult.ok(list);
    }

    @ApiOperation(value = "查询视频详情", notes = "查询视频详情")
    @PostMapping(value = "/findVideo")
    public LexJSONResult findVideo( @RequestParam String id){
        //TODO
        Videos video = videosService.findVideo(id);
        return LexJSONResult.ok(video);
    }



    @ApiOperation(value = "用户评论的接口/回复用户的留言", notes = "用户评论的接口/回复用户的留言")
    @PostMapping(value = "/saveComments")
    public LexJSONResult saveComments(@RequestBody Comments comments){
        videosService.saveComments(comments);
        return LexJSONResult.ok();
    }

    @ApiOperation(value = "查询用户留言", notes = "查询用户留言")
    @PostMapping(value = "/findComments")
    public LexJSONResult findComments(String videoId,Integer page){
        PageResult pageResult = videosService.findComments(videoId, page, SIZE);
        return LexJSONResult.ok(pageResult);
    }


//    @ApiOperation(value = "回复评论功能", notes = "回复评论功能")
//    @PostMapping(value = "/appendComments")
//    public LexJSONResult appendComments(String id,String fatherCommentId,String toUserId,String comment){
//        videosService.appendComments(id,fatherCommentId,toUserId,comment);
//        return LexJSONResult.ok();
//    }

    @ApiOperation(value = "测试接口", notes = "测试接口")
    @PostMapping(value = "/test")
    public LexJSONResult test(String key){
//        Hot content = videosService.findByContent(key);
//        return LexJSONResult.ok(content);
        return null;
    }



}
