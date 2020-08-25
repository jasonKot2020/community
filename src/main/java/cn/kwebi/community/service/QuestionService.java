package cn.kwebi.community.service;

import cn.kwebi.community.dto.PaginationDTO;
import cn.kwebi.community.dto.QuestionDTO;
import cn.kwebi.community.exception.CustomizeErrorCode;
import cn.kwebi.community.exception.CustomizeException;
import cn.kwebi.community.mapper.*;
import cn.kwebi.community.model.Question;
import cn.kwebi.community.model.User;
import cn.kwebi.community.util.JsonMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LikePostMapper likePostMapper;

    @Autowired
    private CollectionArticleMapper collectionArticleMapper;

    public PaginationDTO list(Integer accountId,Integer page, Integer size) {
        Integer totalCount = questionMapper.count();
        PaginationDTO paginationDTO = new PaginationDTO();

        if (page < 1) page = 1;
        Integer totalPage = (totalCount + size - 1) / size;
        if (page > totalPage) page = totalPage;
        Integer offset = size*(page-1);
        if(offset<0)offset=0;
        paginationDTO.setPagination(totalPage,page);
        List<Question> list = questionMapper.list(offset,size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for(Question question: list){
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            //是否收藏
            if(accountId != null)questionDTO.setCollection(collectionArticleMapper.check(accountId,question.getId(),0));
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }

    public PaginationDTO list(Integer id, Integer page, Integer size,String action) {
        Integer totalCount = 0;

        if("questions".equals(action)){
            totalCount = questionMapper.totalCount(id);
        }
        if("replies".equals(action)){
            totalCount = questionMapper.countByUserId(id);
        }
        if("collect".equals(action)){
            totalCount = questionMapper.collectionByUserId(id);
        }
        if("likePost".equals(action)){
            totalCount = questionMapper.likeCountByUserId(id);
        }

        PaginationDTO paginationDTO = new PaginationDTO();

        if (page < 1) page = 1;
        Integer totalPage = (totalCount + size - 1) / size;
        if (page > totalPage) page = totalPage;
        Integer offset = size*(page-1);
        if(offset<0)offset=0;
        paginationDTO.setPagination(totalPage,page);
        //action.equals("replies") ? questionMapper.listByCount(id,offset,size) : questionMapper.listByUserId(id,offset,size)
        List<Question> list = new ArrayList<Question>();
        if("questions".equals(action)){
            list = questionMapper.listByUserId(id,offset,size);
        }
        if("replies".equals(action)){
            list = questionMapper.listByCount(id,offset,size);
        }
        if("collect".equals(action)){
            list = questionMapper.listByCollection(id,offset,size);
        }
        if("likePost".equals(action)){
            list = questionMapper.listByLike(id,offset,size);
        }
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for(Question question: list){
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            //是否收藏
            if(user != null)questionDTO.setCollection(collectionArticleMapper.check(user.getId(),question.getId(),0));
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }


    public QuestionDTO getById(Integer accountId,Integer id) {
        Question question = questionMapper.getById(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.findById(question.getCreator());
        questionDTO.setUser(user);
        //是否收藏
        if(accountId != null)questionDTO.setCollection(collectionArticleMapper.check(accountId,question.getId(),0));
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId()==null){
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.create(question);
        }else{
            //更新
            question.setGmtModified(System.currentTimeMillis());
            int update = questionMapper.update(question);
            if(update==0){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Integer id) {
        questionMapper.incViewCount(id);
    }

    public Integer totalCount(Integer id) {
        return questionMapper.totalCount(id);
    }

    public Integer totalCollect(Integer id) {
        return questionMapper.collectionByUserId(id);
    }

    public Object deletById(Integer accountId,Integer id){
        Question qt = questionMapper.getById(id);
        if(qt == null){
            return JsonMessage.error();
        }
        if(!accountId.equals(qt.getCreator())){
            return  JsonMessage.error("操作失败，无此权限！");
        }
        try {
            questionMapper.deleteById(accountId,id);
            commentMapper.deleteByParentId(id);
            likePostMapper.deleteByArticleId(id);
            collectionArticleMapper.delete(accountId,id,0);
        }catch (Exception e){
            return JsonMessage.error();
        }finally {
            return JsonMessage.success();
        }
    }
}
