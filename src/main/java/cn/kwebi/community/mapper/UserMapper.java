package cn.kwebi.community.mapper;

import cn.kwebi.community.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("insert into user (name,account,password,token,gmt_create,gmt_modified,avatar_url) values (#{name},#{account},#{password},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token = #{token}")
    User findByToken(@Param("token") String token);

    @Select("select * from user where id = #{id}")
    User findById(@Param("id") Integer id);

    @Select("select * from user where account = #{account}")
    User findByAccount(@Param("account") String account);

    @Update("update user set name=#{name}, token=#{token}, gmt_modified=#{gmtModified},avatar_url=#{avatarUrl},mail=#{mail},remark=#{remark},social=#{social} where id=#{id}")
    void update(User dbUser);

    @Select("select count(1) from user where account = #{account}")
    Integer checkAccount(@Param("account") String account);

    @Select("select count(1) from user where account = #{account} AND password = #{password}")
    Integer checkUserPassword(@Param("account") String account,@Param("password") String password);
}
