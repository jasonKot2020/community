package cn.kwebi.community.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface CollectionArticleMapper {

    @Insert("insert into user_collection (account_id,article_id,type)" +
            " values (#{accountId},#{id},#{type})")
    void create(@Param(value = "accountId") Integer accountId, @Param(value = "id") Integer id, @Param(value = "type") Integer type);

    @Update("delete from user_collection where account_id=#{accountId} AND article_id=#{id} AND type=#{type} ")
    void delete(@Param(value = "accountId") Integer accountId, @Param(value = "id") Integer id, @Param(value = "type") Integer type);

    @Select("select IFNULL(count(1),0) from user_collection where account_id=#{accountId} AND article_id=#{id} AND type=#{type}")
    int check(@Param(value = "accountId") Integer accountId, @Param(value = "id") Integer id, @Param(value = "type") Integer type);
}
