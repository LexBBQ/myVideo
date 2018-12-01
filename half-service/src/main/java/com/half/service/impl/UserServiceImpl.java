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

    /**
     * 根据传入的用户的用户名在数据库中进行查找
     * 如果该用户存在就将该用户返回，如果不存在则返回的就为null
     * @param users 用户的pojo类
     * @return
     */
    @Override
    public Users findUserIsExist(Users users) {
        Users u =new Users();
        u.setUsername(users.getUsername());
        Example<Users> example = Example.of(u);
        return usersDao.findOne(example);

    }

    /**
     * 将用户信息存入数据库
     * @param users
     * @return
     */
    @Override
    @Transactional
    public Users save(Users users) {
        users.setId(IdUtils.getId());
        return usersDao.save(users);
    }

    /**
     * 通过用户名校验用户密码是否正确，如正确则将查询到的对象返回
     * 若果错误则返回null
     * @param users
     * @return
     */
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

    /**
     * 用户更新操作，将用户的头像存储的相对路径保存到数据
     * @param users
     */
    @Override
    @Transactional
    public void updateUserInfo(Users users) {
        Users one = usersDao.findOne(users.getId());
        one.setFaceImage(users.getFaceImage());
        usersDao.save(one);

    }

    /**
     * 根据用户id返回查询用户信息并返回
     * @param id
     * @return
     */
    @Override
    public Users findUserInfo(String id) {
        return usersDao.findOne(id);
    }

    /**
     * 用户点赞功能
     * @param id
     * @param videoId
     * @param videoCreateId
     */
    @Override
    @Transactional
    public void like(String id, String videoId, String videoCreateId) {
        //将用户与视频的点赞关系存入表中
        UsersLikeVideos usersLikeVideos =new UsersLikeVideos();
        usersLikeVideos.setId(IdUtils.getId());
        usersLikeVideos.setUserId(id);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosDao.save(usersLikeVideos);
        //根据videoId查询视频，并将该视频的喜欢次数+1
        Videos videos = videosDao.findOne(videoId);
        videos.setLikeCounts(videos.getLikeCounts()+1);
        videosDao.save(videos);
        //根据视频发布者的id查询发布者信息，并将点赞数+1
        Users users = usersDao.findOne(videoCreateId);
        users.setReceiveLikeCounts(users.getReceiveLikeCounts()+1);
        usersDao.save(users);

    }

    /**
     * 用户取消点赞
     * @param id
     * @param videoId
     * @param videoCreateId
     */
    @Override
    @Transactional
    public void unlike(final String id, final String videoId, String videoCreateId) {

        //调用我们自定义的方法去数据库中查询该记录，并返回一个列表
        List<UsersLikeVideos> list = findByUserIdAndVideoId(id, videoId);

        //因为该列表中只有一个元素，所以将第一个元素取出来
        UsersLikeVideos usersLikeVideos  =list.get(0);
        //通过该元素的id对该元素进行删除
        usersLikeVideosDao.delete(usersLikeVideos.getId());
        //通过视频的id查询视频，并将该视频的喜欢数量减一，并保存
        Videos videos = videosDao.findOne(videoId);
        videos.setLikeCounts(videos.getLikeCounts()-1);
        videosDao.save(videos);
        //根据发布者的id查询到发布者的用户信息，并将改用户的被点赞数减一，并保存回数据库
        Users users = usersDao.findOne(videoCreateId);
        users.setReceiveLikeCounts(users.getReceiveLikeCounts()-1);
        usersDao.save(users);

    }

    /**
     * 查询用户与视频的点赞关系
     * @param id
     * @param videoId
     * @return
     */
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

    /**
     * 取消关注
     * @param id
     * @param fansId
     */
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

    /**
     * 关注用户
     * @param id
     * @param fansId
     * @return
     */
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
    @Transactional
    List<UsersLikeVideos> findByUserIdAndVideoId(final String id, final String videoId){
        //下面的方法为jpa的多条件查询
        return usersLikeVideosDao.findAll(new Specification<UsersLikeVideos>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery cq, CriteriaBuilder cb) {
                //将usersLikeVideos实体类中的userid获取，类型为String，并将id的值赋值给他，作为第一个查询条件
                Predicate p1 = cb.equal(root.get("userId").as(String.class), id);
                //将usersLikeVideos实体类中的videoId获取，类型为String，并将videoId的值赋值给他，作为第二个查询条件
                Predicate p2 = cb.equal(root.get("videoId").as(String.class), videoId);
                //将这两个条件放到一个数组中
                Predicate[] predicate = new Predicate[]{p1, p2};
                //最后就根据数组中的这两个条件进行插叙，实现多条件查询
                return cb.and(predicate);
            }
        });
    }


    /**
     * 根基用户id个粉丝id精确查询用户和粉丝关系
     * @param id
     * @param fansId
     * @return
     */
    @Transactional
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
