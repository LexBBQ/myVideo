package com.half.service;

import com.half.pojo.Comments;
import com.half.pojo.Hot;
import com.half.pojo.Videos;
import com.half.utils.PageResult;

import java.util.List;

public interface VideosService {
    void save(Videos videos);
    PageResult findAllVideos(String searchValue,Integer pageNum, Integer size);
    List<String> findHot();
//    Hot findByContent(String key);
    Videos findVideo(String id);
    void saveComments(Comments comments);
    PageResult findComments(String videoId,Integer page ,Integer size);


}
