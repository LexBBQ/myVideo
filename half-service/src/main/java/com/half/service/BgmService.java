package com.half.service;

import com.half.pojo.Bgm;

import java.util.List;

public interface BgmService {
    //查询所有的bgm列表
    List<Bgm> findAllBgm();
    //根据主键查询bgm
    Bgm  findOne(String bgmId);
}
