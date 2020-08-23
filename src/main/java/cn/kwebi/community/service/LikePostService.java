package cn.kwebi.community.service;

import cn.kwebi.community.mapper.LikePostMapper;
import cn.kwebi.community.util.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;

public class LikePostService {

    @Autowired
    private LikePostMapper likePostMapper;

    /**
     * 保存点赞
     * @param accountId
     * @param id
     * @return
     */
    public Object LikePost(Integer accountId,Integer id){

        //已点赞则取消点赞
        if(likePostMapper.check(accountId,id) > 0){
            likePostMapper.delete(accountId,id);
            likePostMapper.setLikeCount(id,-1);
            return JsonMessage.success("取消点赞~");
        }else{
            likePostMapper.check(accountId,id);
            likePostMapper.setLikeCount(id,-1);
            return JsonMessage.success("成功点赞~");
        }

    }
}
