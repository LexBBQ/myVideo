package com.half.service.impl;

import com.half.mapper.CommentsDao;
import com.half.mapper.HotDao;
import com.half.mapper.UsersDao;
import com.half.mapper.VideosDao;
import com.half.pojo.Comments;
import com.half.pojo.Hot;
import com.half.pojo.Users;
import com.half.pojo.Videos;
import com.half.service.VideosService;
import com.half.utils.IdUtils;
import com.half.utils.PageResult;
import com.half.utils.TimeAgoUtils;
import com.half.vo.CommentsVo;
import com.half.vo.VideosVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VideosServiceImpl implements VideosService {

    @Autowired
    private VideosDao videosDao;
    @Autowired
    private UsersDao usersDao;
    @Autowired
    private HotDao hotDao;
    @Autowired
    private CommentsDao commentsDao;

    /**
     * 保存视频到数据库
     * @param videos
     */
    @Override
    @Transactional
    public void save(Videos videos) {
        videos.setId(IdUtils.getId());
        videosDao.save(videos);
    }

    /**
     * 查询全部的视频，分页
     * @param searchValue
     * @param pageNum
     * @param size
     * @return
     */
    @Override
    @Transactional
    public PageResult findAllVideos(final String searchValue , Integer pageNum, Integer size) {

        //创建一个分页结果类
        PageResult pageResult =new PageResult();
        List<VideosVo> videosVoList =new ArrayList<VideosVo>();
        Page<Videos> videosPage=null;


        //对搜索的关键字进行查询，如果数据库中没有，就新添加一条数据，如果有，就在num上做+1的操作
        if (!StringUtils.isEmpty(searchValue)){
            //根据搜索内容查询
            Hot byContent = hotDao.findByContent(searchValue);
            //如果返回值为空，则说明该关键字没有被人搜索过，所以将该关键字添加到数据库，并将搜索次数设置为1
            if (StringUtils.isEmpty(byContent)){
                Hot hot =new Hot();
                hot.setId(IdUtils.getId());
                hot.setContent(searchValue);
                hot.setNum(1l);
                hotDao.save(hot);
            }
            //如果返回结果不为空，则说明该关键字已经被搜索过，在该关键字的搜索次数字段自增1，并保存
            if (!StringUtils.isEmpty(byContent)){
                byContent.setNum(byContent.getNum()+1);
                hotDao.save(byContent);
            }
            //将前端传来的页数和每页数据数放入PageRequest中，因为前端分页都是从1开始的所以，这里我们需要-1
            Pageable pageable =new PageRequest(pageNum-1,size);
            //通过视频描述模糊查询视频，返回分页结果
            videosPage= findAllVideosByVideoDesc(searchValue,pageable);
        }
        //当searchValue为空的时候，说明用户是直接来到视频展示页，没有做搜索操作
        if (StringUtils.isEmpty(searchValue)){
            //创建一个排序条件，降序排列，按照创建日期排序
            Sort sort =new Sort(Sort.Direction.DESC,"createTime");
            //将页数，和每页大小以及排序条件传入分页对象
            Pageable pageable =new PageRequest(pageNum-1,size,sort);
            //返回一个page列表
            videosPage= videosDao.findAll(pageable);
        }

        //将当前为第几页传入分页结果中
        pageResult.setPage(pageNum);
        //传入总页数
        pageResult.setTotalPage(videosPage.getTotalPages());
        //传入总记录数
        pageResult.setTotalElements(videosPage.getTotalElements());
        //通过对page中的对象进行遍历，做数据收集，最后返回videosVoList
        for (Videos v:videosPage
             ) {
            VideosVo videosVo =new VideosVo();
            Users users = usersDao.findOne(v.getUserId());
            BeanUtils.copyProperties(users,videosVo);
            BeanUtils.copyProperties(v,videosVo);
            videosVoList.add(videosVo);
        }
        //将videosVoList存入分页结果中
        pageResult.setRows(videosVoList);
        return pageResult;
    }
    /**
     * 查询热搜词列表，并按照搜索次数降序排列，并只显示前5条数据
     * @return
     */
    @Override
    public List<String> findHot() {
        List<String> list =new ArrayList<String>();
        //定义一个排序的条件，该条件为按照num的大小对数据库表中数据进行降序排列
        Sort sort =new Sort(Sort.Direction.DESC,"num");
        //定义一个分页的类，表示取第1页，每页5条数据，也就是取出前5条数据
        Pageable pageable =new PageRequest(0,5,sort);
        Page<Hot> hotPage = hotDao.findAll(pageable);
        //并将查询到的数据的关键字内容添加进list列表，并返回给前端
        for (Hot h:hotPage
             ) {
            list.add(h.getContent());
        }
        return list;
    }
    /**
     * 测试方法
     * @param
     * @return
     */
//    @Override
//    public Hot findByContent(String key) {
////        return hotDao.findByNum(10L);
////        List<Hot> videoByContentLike = hotDao.findVideoByContentLike(key);
////        List<Videos> list = videosDao.findByVideoDescLike("%"+key+"%");
////        Hot dao = hotDao.findByContent("古河渚");
////        System.out.println(list.size());
////        System.out.println(dao);
////        List<Videos> c = videosDao.findByVideoDescLikeOrderByCreateTimeDesc(key);
////        System.out.println(c.size());
////        for (Videos v:c
////             ) {
////            System.out.println(v.getCreateTime());
////        }
//        return null;
//    }

    /**
     * 根据id查询视频
     * @param id
     * @return
     */
    @Override
    public Videos findVideo(String id) {
        return videosDao.findOne(id);
    }

    /**
     * 保存用户评论
     * @param comments
     */
    @Override
    @Transactional
    public void saveComments(Comments comments) {
        comments.setId(IdUtils.getId());
        comments.setCreateTime(new Date());
        commentsDao.save(comments);
    }

    /**
     * 分页查询全部的评论
     * @param videoId
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult findComments(final String videoId, Integer page, Integer size) {
        //创建分页的条件
        Pageable pageable =new PageRequest(page-1,size);
        //创建返回给前端的分页结果类
        PageResult pageResult =new PageResult();
        //创建一个用来装返回结果的集合
        List<CommentsVo> rows =new ArrayList<CommentsVo>();
        //根据视频的id分页查询数据，并按照创建时间降序排列
        Page<Comments> commentsPage = commentsDao.findAll(new Specification<Comments>() {
            @Override
            public Predicate toPredicate(Root<Comments> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                //将实体类中的videoId作为参数传入
                Predicate p1 = cb.equal(root.get("videoId").as(String.class), videoId);
//                Order order = cb.desc(root.get("createTime").as(Date.class));
//                Predicate p2 = (Predicate) order.getExpression();
//                Predicate[] predicate = new Predicate[]{p1, p2};
                //通过这个条件进行查询
                cq.where(p1);
                //通过创建实现对查询结果进行降序排列
                cq.orderBy(cb.desc(root.get("createTime").as(Date.class)));

                return cq.getRestriction();
            }
        }, pageable);
        //设置当前的页数
        pageResult.setPage(page);
        //设置总页数
        pageResult.setTotalPage(commentsPage.getTotalPages());
        //设置当前元素数
        pageResult.setTotalElements(commentsPage.getTotalElements());
        //进行数据收集，将数据拼装成需要返回给前端的vo类
        for (Comments comments:commentsPage){
            CommentsVo commentsVo =new CommentsVo();
            //使用我们封装好的显示时间的工具类，对创建时间进行处理，使得在前端页面以xx前的形式显示
            commentsVo.setTimeAgo(TimeAgoUtils.format(comments.getCreateTime()));
            //查询评论者的相关用户信息
            Users one = usersDao.findOne(comments.getFromUserId());
            //如果getToUserId不为空，说明有用户对该条评论做出了回复
            if(!StringUtils.isEmpty(comments.getToUserId())){
                //查询到被回复对象的信息
                Users one1 = usersDao.findOne(comments.getToUserId());
                //将用户的昵称封装到vo中
                commentsVo.setToNickname(one1.getNickname());
            }
            //将用户的头像信息存放到vo中
            commentsVo.setFaceImage(one.getFaceImage());
            //将用户的昵称存放到vo中
            commentsVo.setNickname(one.getNickname());
            //将查询到的comments类拷贝到vo对象中
            BeanUtils.copyProperties(comments,commentsVo);
            //将这个对象存放近列表
            rows.add(commentsVo);
        }
        //将列表结果存放到分页结果对象中
        pageResult.setRows(rows);
        //返回分页结果对象
        return pageResult;
    }


    /**
     * 根据条件分页模糊查询，用来做搜索时候使用
     * @param searchValue
     * @param pageable
     * @return
     */

    public Page<Videos> findAllVideosByVideoDesc(final String searchValue,Pageable pageable){
        Page<Videos> videoDesc = videosDao.findAll(new Specification<Videos>() {
            @Override
            public Predicate toPredicate(Root<Videos> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Predicate predicate = cb.like(root.get("videoDesc").as(String.class), "%" + searchValue + "%");
                return predicate;
            }
        }, pageable);
        return  videoDesc;
    }


}
