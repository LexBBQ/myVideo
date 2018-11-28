package com.half.mapper;

import com.half.pojo.UsersLikeVideos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersLikeVideosDao extends JpaRepository<UsersLikeVideos,String> ,JpaSpecificationExecutor {

}
