package cn.kwebi.community.mapper;

import cn.kwebi.community.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Select("select gmt_create from message WHERE creator=#{id} order by gmt_create desc limit 1 ")
    String lastSendTime(@Param(value = "id") Integer id);

    @Select("select * from message order by gmt_create desc limit 5 ")
    List<Message> list();

    @Insert("insert into message (content,sender,gmt_create,creator)" +
            " values (#{content},#{sender},#{gmt_create},#{creator})")
    void create(Message message);

}
