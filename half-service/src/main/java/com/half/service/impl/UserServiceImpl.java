package com.half.service.impl;


import com.half.mapper.UsersDao;
import com.half.mapper.UsersFansDao;
import com.half.mapper.UsersLikeVideosDao;
import com.half.mapper.VideosDao;
import com.half.pojo.Users;
import com.half.pojo.UsersFans;
import com.half.pojo.UsersLikeVideos;
import com.half.pojo.Videos;
import com.half.service.UserService;

import com.half.utils.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersDao usersDao;
    @Autowired
    private UsersLikeVideosDao usersLikeVideosDao;
    @Autowired
    private VideosDao videosDao;
    @Autowired
    private UsersFansDao usersFansDao;
    @Override
    public Users findUserIsExist(Users users) {
        Users u =new Users();
        u.setUsername(users.getUsername());
        Example<Users> example = Example.of(u);
        return usersDao.findOne(example);

    }

    @Override
    @Transactional
    public Users save(Users users) {
        users.setId(IdUtils.getId());
        return usersDao.save(users);
    }

    @Override
    public Users checkPassword(Users users) {

        //根据用户名查询用户密码是否正确
        Users u=new Users();
        u.setUsername(users.getUsername());
        Example<Users> example =Example.of(u);
        Users one = usersDao.findOne(example);
        //如果密码相同就将该对象返回，密码正确
        if (users.getPassword().equals(one.getPassword())){
            return one;
        }
        //如果返回null，则说明密码错误
        return null;
    }

    @Override
    @Transactional
    public void updateUserInfo(Users users) {
        Users one = usersDao.findOne(users.getId());
        one.setFaceImage(users.getFaceImage());
        usersDao.save(one);

    }

    @Override
    public Users findUserInfo(String id) {
        return usersDao.findOne(id);
    }


    @Override
    @Transactional
    public void like(String id, String videoId, String videoCreateId) {
        //添加信息到
        UsersLikeVideos usersLikeVideos =new UsersLikeVideos();
        usersLikeVideos.setId(IdUtils.getId());
        usersLikeVideos.setUserId(id);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosDao.save(usersLikeVideos);

        Videos videos = videosDao.findOne(videoId);
        videos.setLikeCounts(videos.getLikeCounts()+1);
        videosDao.save(videos);

        Users users = usersDao.findOne(videoCreateId);
        users.setReceiveLikeCounts(users.getReceiveLikeCounts()+1);
        usersDao.save(users);

    }

    @Override
    @Transactional
    public void unlike(final String id, final String videoId, String videoCreateId) {

//        usersLikeVideosDao.delete(videoId);

        List<UsersLikeVideos> list = findByUserIdAndVideoId(id, videoId);


        UsersLikeVideos usersLikeVideos  =list.get(0);
        usersLikeVideosDao.delete(usersLikeVideos.getId());

        Videos videos = videosDao.findOne(videoId);
        videos.setLikeCounts(videos.getLikeCounts()-1);
        videosDao.save(videos);

        Users users = usersDao.findOne(videoCreateId);
        users.setReceiveLikeCounts(users.getReceiveLikeCounts()-1);
        usersDao.save(users);

    }

    @Override
    public Boolean findUserIsLike(String id, String videoId) {
        List<UsersLikeVideos> list = findByUserIdAndVideoId(id, videoId);
        if (list.size()==0){
            return false;
        }else {
            return true;
        }

    }

    /**
     * 用户关注他人
     * @param id
     * @param fansId
     */
    @Override
    @Transactional
    public void follow(String id, String fansId) {
        //保存用户和粉丝关系表
        UsersFans usersFans =new UsersFans();
        usersFans.setId(IdUtils.getId());
        usersFans.setUserId(id);
        usersFans.setFanId(fansId);
        usersFansDao.save(usersFans);
        //添加该用户的粉丝数
        Users one = usersDao.findOne(id);
        one.setFansCounts(one.getFansCounts()+1);
        usersDao.save(one);
        //添加登陆者的关注数
        Users daoOne = usersDao.findOne(fansId);
        daoOne.setFollowCounts(daoOne.getFansCounts()+1);
        usersDao.save(daoOne);

    }

    @Override
    public void unfollow(String id, String fansId) {
        List<UsersFans> usersFansList = findByUserIdAndFansId(id, fansId);
        UsersFans usersFans = usersFansList.get(0);
        usersFansDao.delete(usersFans.getId());

        Users one = usersDao.findOne(id);
        one.setFansCounts(one.getFansCounts()-1);
        usersDao.save(one);

        Users daoOne = usersDao.findOne(fansId);
        daoOne.setFollowCounts(daoOne.getFansCounts()-1);
        usersDao.save(daoOne);
    }

    @Override
    public Boolean findIsFollowed(String id, String fansId) {
        List<UsersFans> usersFansList = findByUserIdAndFansId(id, fansId);
        if (usersFansList.size()==0){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 通过用户id和视频id查询 UsersLikeVideos
     * @param id
     * @param videoId
     * @return
     */
    List<UsersLikeVideos> findByUserIdAndVideoId(final String id, final String videoId){
        return usersLikeVideosDao.findAll(new Specification<UsersLikeVideos>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery cq, CriteriaBuilder cb) {
                Predicate p1 = cb.equal(root.get("userId").as(String.class), id);
                Predicate p2 = cb.equal(root.get("videoId").as(String.class), videoId);

                Predicate[] predicate = new Predicate[]{p1, p2};

                return cb.and(predicate);
            }
        });
    }

    /**
     * 根据用户id和粉丝id查询
     * @param id
     * @param fansId
     * @return
     */
    List<UsersFans> findByUserIdAndFansId(final String id, final String fansId){
        return usersFansDao.findAll(new Specification<UsersFans>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery cq, CriteriaBuilder cb) {
                Predicate p1 = cb.equal(root.get("userId").as(String.class), id);
                Predicate p2 = cb.equal(root.get("fanId").as(String.class), fansId);
                Predicate[] predicate = new Predicate[]{p1, p2};
                return cb.and(predicate);
            }
        });
    }

}
