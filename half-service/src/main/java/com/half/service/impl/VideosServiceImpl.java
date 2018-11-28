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

        //声明分页的类
        PageResult pageResult =new PageResult();
        List<VideosVo> videosVoList =new ArrayList<VideosVo>();
        Page<Videos> videosPage=null;


        //对搜索的关键字进行查询，如果数据库中没有，就新添加一条数据，如果有，就在num上做+1的操作
        if (!StringUtils.isEmpty(searchValue)){
            Hot byContent = hotDao.findByContent(searchValue);
            if (StringUtils.isEmpty(byContent)){
                Hot hot =new Hot();
                hot.setId(IdUtils.getId());
                hot.setContent(searchValue);
                hot.setNum(1l);
                hotDao.save(hot);
            }
            if (!StringUtils.isEmpty(byContent)){
                byContent.setNum(byContent.getNum()+1);
                hotDao.save(byContent);
            }
            Pageable pageable =new PageRequest(pageNum-1,size);
            videosPage= findAllVideosByVideoDesc(searchValue,pageable);
        }
        if (StringUtils.isEmpty(searchValue)){
            Sort sort =new Sort(Sort.Direction.DESC,"createTime");
            Pageable pageable =new PageRequest(pageNum-1,size,sort);
            videosPage= videosDao.findAll(pageable);
        }


        pageResult.setPage(pageNum);
        pageResult.setTotalPage(videosPage.getTotalPages());
        pageResult.setTotalElements(videosPage.getTotalElements());
        for (Videos v:videosPage
             ) {
            VideosVo videosVo =new VideosVo();
            Users users = usersDao.findOne(v.getUserId());
            BeanUtils.copyProperties(users,videosVo);
            BeanUtils.copyProperties(v,videosVo);
            videosVoList.add(videosVo);
        }

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
        Sort sort =new Sort(Sort.Direction.DESC,"num");
        Pageable pageable =new PageRequest(0,5,sort);
        Page<Hot> hotPage = hotDao.findAll(pageable);
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

    @Override
    public PageResult findComments(final String videoId, Integer page, Integer size) {
        Pageable pageable =new PageRequest(page-1,size);
        PageResult pageResult =new PageResult();
        List<CommentsVo> rows =new ArrayList<CommentsVo>();

        Page<Comments> commentsPage = commentsDao.findAll(new Specification<Comments>() {
            @Override
            public Predicate toPredicate(Root<Comments> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Predicate p1 = cb.equal(root.get("videoId").as(String.class), videoId);
//                Order order = cb.desc(root.get("createTime").as(Date.class));
//                Predicate p2 = (Predicate) order.getExpression();
//                Predicate[] predicate = new Predicate[]{p1, p2};
                cq.where(p1);
                cq.orderBy(cb.desc(root.get("createTime").as(Date.class)));

                return cq.getRestriction();
            }
        }, pageable);
        pageResult.setPage(page);
        pageResult.setTotalPage(commentsPage.getTotalPages());
        pageResult.setTotalElements(commentsPage.getTotalElements());
        for (Comments comments:commentsPage){
            CommentsVo commentsVo =new CommentsVo();
            commentsVo.setTimeAgo(TimeAgoUtils.format(comments.getCreateTime()));
            Users one = usersDao.findOne(comments.getFromUserId());
            if(!StringUtils.isEmpty(comments.getToUserId())){
                Users one1 = usersDao.findOne(comments.getToUserId());
                commentsVo.setToNickname(one1.getNickname());
            }
            commentsVo.setFaceImage(one.getFaceImage());
            commentsVo.setNickname(one.getNickname());
            BeanUtils.copyProperties(comments,commentsVo);
            rows.add(commentsVo);
        }
        pageResult.setRows(rows);
        return pageResult;
    }


    /**
     * 根据条件分页模糊查询
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
