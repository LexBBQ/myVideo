package com.half.mapper;

import com.half.pojo.Videos;
import com.half.vo.VideosVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideosDao extends JpaRepository<Videos,String>,JpaSpecificationExecutor {

//    @Query("select v,u.face_image as face_image ,u.nickname as nickname from videos v left join users u on u.id=v.user_id where 1=1 and v.status=1 order by v.create_time desc ")
//    List<VideosVo> findVideosList();
//    @Query(value = "select * from hot h where h.content like %:key% ",nativeQuery = true)
    List<Videos> findByVideoDescLikeOrderByCreateTimeDesc(String desc);
}
