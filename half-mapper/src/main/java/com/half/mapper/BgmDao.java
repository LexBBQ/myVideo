package com.half.mapper;

import com.half.pojo.Bgm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BgmDao extends JpaRepository<Bgm,String> {
}
