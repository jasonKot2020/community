package cn.kwebi.community.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import cn.kwebi.community.mapper.UserMapper;
import cn.kwebi.community.model.User;
import cn.kwebi.community.service.UserService;
import cn.kwebi.community.util.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/account")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 进入个人中心
     * @param request
     * @param response
     * @param model
     * @param account
     * @return
     */
    @GetMapping("/{account}")
    public String userInfo(HttpServletRequest request, HttpServletResponse response, Model model,
                             @PathVariable String account) {

        //是否登录
        User loginUser = (User) request.getSession().getAttribute("user");
        if(loginUser == null){
            model.addAttribute("type",1);
            return "/login";
        }

        //用户是否存在
        User u = userMapper.findByAccount(account);
        if(u == null || !u.getAccount().equals(loginUser.getAccount()))return "redirect:/";

        return "user";
    }

    @ResponseBody
    @GetMapping("/setAvatar")
    public Object setAvatar(HttpServletRequest request, @RequestParam(value = "url")String url){
        User u = (User) request.getSession().getAttribute("user");
        u.setAvatarUrl(url);
        userService.update(u);
        return JsonMessage.success();
    }

    @ResponseBody
    @PostMapping("/updateUserInfo")
    public Object setAvatar(HttpServletRequest request, User user,String code){
        User u = (User) request.getSession().getAttribute("user");
        if(StrUtil.hasEmpty(user.getName())){
            return JsonMessage.error("昵称不能为空！");
        }
        if(!StrUtil.hasEmpty(user.getMail())){
            if(StrUtil.hasEmpty(code)){
                return JsonMessage.error("邮箱验证码不能为空！");
            }
            if(!Pattern.matches(user.getMail(),"^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")){
                return JsonMessage.error("邮箱格式错误！");
            }
        }
        u.setName(user.getName());
        u.setMail(user.getMail());
        u.setRemark(user.getRemark());
        u.setSocial(user.getSocial());
        userService.update(u);
        return JsonMessage.success();
    }

}
