package com.half.service.impl;

import com.half.mapper.BgmDao;
import com.half.pojo.Bgm;
import com.half.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmDao bgmDao;

    @Override
    public List<Bgm> findAllBgm() {
        return bgmDao.findAll();
    }

    @Override
    public Bgm findOne(String bgmId) {
        return bgmDao.findOne(bgmId);
    }
}
