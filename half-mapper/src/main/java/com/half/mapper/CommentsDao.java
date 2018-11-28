package com.half.mapper;

import com.half.pojo.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommentsDao  extends JpaRepository<Comments,String> ,JpaSpecificationExecutor<Comments> {
}
