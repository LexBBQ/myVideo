package com.half.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
/**
 * 用于前端返回评论列表的包装类
 */
public class CommentsVo {
    @Id
    private String id;

    @Column(name = "father_comment_id")
    private String fatherCommentId;

    @Column(name = "to_user_id")
    private String toUserId;

    /**
     * 视频id
     */
    @Column(name = "video_id")
    private String videoId;

    /**
     * 留言者，评论的用户id
     */
    @Column(name = "from_user_id")
    private String fromUserId;

    /**
     * 评论内容
     */
    private String comment;
    /**
     * 评论距现在的时间
     */
    private String timeAgo;

    private String nickname;

    private String faceImage;

    private String toNickname;


}