package com.half.controller;

import com.half.pojo.Bgm;
import com.half.service.BgmService;
import com.half.utils.LexJSONResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BgmController {

    @Autowired
    private BgmService bgmService;


    @ApiOperation(value = "查询BGM列表", notes = "查询bgm列表的接口")
    @PostMapping("/bgmList")
    public LexJSONResult bgmList(){
        //查询全部都bgm列表
        List<Bgm> allBgm = bgmService.findAllBgm();
        return LexJSONResult.ok(allBgm);
    }
}
