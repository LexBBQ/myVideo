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

    private String faceImage;

    private String nickname;
}
