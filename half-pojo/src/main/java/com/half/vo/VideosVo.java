package com.half.vo;

import lombok.Data;


import java.util.Date;

@Data
public class VideosVo {

    private String id;

    private String userId;

    private String audioId;


    private String videoDesc;


    private String videoPath;


    private Float videoSeconds;


    private Integer videoWidth;


    private Integer videoHeight;

    private String coverPath;


    private Long likeCounts;


    private Integer status;


    private Date createTime;
    /**
     * 下面为新添加的两个字段，分别为用户头像的信息和昵称
     */

    private String faceImage;

    private String nickname;
}
