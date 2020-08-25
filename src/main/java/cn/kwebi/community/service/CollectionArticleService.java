package cn.kwebi.community.service;

import cn.kwebi.community.mapper.CollectionArticleMapper;
import cn.kwebi.community.util.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionArticleService {

    @Autowired
    private CollectionArticleMapper collectionArticleMapper;

    /**
     * 收藏文章
     * @param accountId
     * @param id 文章ID
     * @param type (0添加 1移除)
     * @return
     */
    public Object Collection(Integer accountId,Integer id,Integer type){
        try{
            if(type.equals(0)){
                if(collectionArticleMapper.check(accountId,id,0) == 0){
                    collectionArticleMapper.create(accountId,id,0);
                }else{
                    return JsonMessage.error();
                }
            }else{
                collectionArticleMapper.delete(accountId,id,0);
            }
        }catch (Exception e){
            return JsonMessage.error();
        }finally {
            return JsonMessage.success();
        }
    }
}

