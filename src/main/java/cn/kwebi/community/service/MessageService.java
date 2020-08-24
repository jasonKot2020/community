package cn.kwebi.community.service;

import cn.hutool.core.util.StrUtil;
import cn.kwebi.community.mapper.MessageMapper;
import cn.kwebi.community.model.Message;
import cn.kwebi.community.model.User;
import cn.kwebi.community.util.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    private static final int SEND_LOOK_TIME = 60;

    public Object createMessage(User user,String content){
        long nowTime = new Date().getTime();

        //判断用户2分钟内只能吐槽一次
        String val = messageMapper.lastSendTime(user.getId());
        if(null != val && val != ""){
            long lastSendTime = Long.parseLong(val);
            if(((nowTime - lastSendTime)/1000) < SEND_LOOK_TIME){
                return JsonMessage.error("一分钟内只能吐槽一次哦~");
            }
        }

        //内容是否为空
        if(content == null || content.equals("") || StrUtil.hasBlank(content.replace("&nbsp;","")) || content.length() == 0){
            return JsonMessage.error("吐槽内容不能为空喔~~");
        }

        //内容是否大于100字符
//        if(content.length() > 100){
//            return JsonMessage.error("吐槽内容不能大于100个字符~~");
//        }

        //写入信息
        Message mes = new Message();
        mes.setContent(content);
        mes.setCreator(user.getId());
        mes.setGmt_create(nowTime);
        mes.setSender(user.getName());

        messageMapper.create(mes);
        return JsonMessage.success("吐槽成功~");
    }

    public List<Message> list(){
        return messageMapper.list();
    }
}
