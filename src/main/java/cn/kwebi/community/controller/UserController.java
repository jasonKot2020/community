package cn.kwebi.community.controller;

import cn.kwebi.community.mapper.UserMapper;
import cn.kwebi.community.model.User;
import cn.kwebi.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        if(request.getSession().getAttribute("user") == null){
            model.addAttribute("type",1);
            return "/login";
        }

        //用户是否存在
        User u = userMapper.findByAccount(account);
        if(u == null)return "redirect:/";

        return "user";
    }

}
