package com.half.mapper;

import com.half.pojo.Hot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotDao extends JpaRepository<Hot,String> {
    Hot findByContent(String key);
    Hot findByNum(Long l);
        //TODO 做模糊查询 完成搜索功能和热搜词的添加功能
    @Query(value = "select * from hot h where h.content like %:key% ",nativeQuery = true)
    List<Hot> findVideoByContentLike(@Param("key") String key);
}
