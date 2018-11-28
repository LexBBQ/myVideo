package com.half.mapper;

import com.half.pojo.UsersFans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersFansDao extends JpaRepository<UsersFans,String>,JpaSpecificationExecutor<UsersFans> {
}
