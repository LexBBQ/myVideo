package com.half.mapper;

import com.half.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersDao extends JpaRepository<Users,String> {
//    Users findByUserName(String name);
}
