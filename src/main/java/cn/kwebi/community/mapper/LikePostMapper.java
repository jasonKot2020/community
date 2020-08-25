package cn.kwebi.community.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface LikePostMapper {
    @Insert("insert into user_like_post (account_id,comment_id,article_id)" +
            " values (#{accountId},#{commentId},#{articleId})")
    void create(@Param(value = "accountId") Integer accountId, @Param(value = "commentId") Integer commentId
            , @Param(value = "articleId") Integer articleId);

    @Select("select count(1) from user_like_post where account_id=#{accountId} AND comment_id=#{commentId}")
    Integer check(@Param(value = "accountId") Integer accountId, @Param(value = "commentId") Integer commentId);

    @Select("delete from user_like_post where account_id=#{accountId} AND comment_id=#{commentId}")
    void delete(@Param(value = "accountId") Integer accountId, @Param(value = "commentId") Integer commentId);

    @Update("update comment set like_count = like_count+#{count} where id=#{id}")
    void setLikeCount(@Param(value = "id") Integer id,@Param(value = "count") Integer count);

    @Select("delete from user_like_post where article_id=#{articleId}")
    void deleteByArticleId(@Param(value = "articleId") Integer articleId);
}
