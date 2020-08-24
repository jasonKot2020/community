package cn.kwebi.community.controller;

import cn.kwebi.community.model.Message;
import cn.kwebi.community.model.User;
import cn.kwebi.community.service.MessageService;
import cn.kwebi.community.util.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    MessageService messageService;

    @ResponseBody
    @PostMapping("/send")
    public Object crateMessage(@RequestParam(name = "content", required = false) String content,
            HttpServletRequest request,
            Model model){
        User user = (User) request.getSession().getAttribute("user");
        return messageService.createMessage(user,content);
    }

    @ResponseBody
    @GetMapping("/list")
    public Object list(){
        return JsonMessage.success(messageService.list());
    }
}
