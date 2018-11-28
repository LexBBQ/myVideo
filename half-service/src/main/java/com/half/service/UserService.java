package com.half.service;

import com.half.pojo.Users;


public interface UserService {
    Users findUserIsExist(Users users);

    Users save(Users users);

    Users checkPassword(Users users);

    void updateUserInfo(Users users);

    Users findUserInfo(String id);

    void like(String id,String videoID,String videoCreateId);
    void unlike(String id,String videoID,String videoCreateId);
    Boolean findUserIsLike(String id,String videoId);

    void follow(String id,String fansId);
    void unfollow(String id,String fansId);
    Boolean findIsFollowed(String id,String fansId);
}

