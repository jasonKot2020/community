package cn.kwebi.community.controller;

import cn.kwebi.community.dto.AccessTockenDTO;
import cn.kwebi.community.dto.GithubUser;
import cn.kwebi.community.mapper.UserMapper;
import cn.kwebi.community.model.User;
import cn.kwebi.community.provider.GithubProvider;
import cn.kwebi.community.service.UserService;
import cn.kwebi.community.util.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;


    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) {
        String accessTocken;
        AccessTockenDTO accessTockenDTO = new AccessTockenDTO();
        accessTockenDTO.setCode(code);
        accessTockenDTO.setRedirect_uri(redirectUri);
        accessTockenDTO.setState(state);
        accessTockenDTO.setClient_id(clientId);
        accessTockenDTO.setClient_secret(clientSecret);
        accessTocken = githubProvider.getAccessTocken(accessTockenDTO);

        //System.out.println(accessTocken);
        GithubUser githubUser = githubProvider.getUser(accessTocken);
        if (githubUser != null && githubUser.getId() != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setAccount(String.valueOf(githubUser.getId()));
            if (githubUser.getName() == null) {
                user.setName(githubUser.getLogin());
            } else {
                user.setName(githubUser.getName());
            }
            user.setAvatarUrl(githubUser.getAvatarUrl());

            userService.createOrUpdate(user);
            //写cookie
            response.addCookie(new Cookie("token", token));
            return "redirect:/";
        } else {
            //登录失败
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response,Model model) {
        if(request.getSession().getAttribute("user") != null)logout(request,response);
        model.addAttribute("type", 1);
        return "login";
    }

    /**
     * 登录账号
     * @param account
     * @param password
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ResponseBody
    @PostMapping("/login")
    public Object login(
            @RequestParam(name = "account", required = false) String account,
            @RequestParam(name = "pass", required = false) String password,
            HttpServletRequest request, HttpServletResponse response, Model model) {

        if(account == null || password == null || account.equals("") || password.equals("")){
            return JsonMessage.error("请认真填写！");
        }
        User user = userMapper.findByAccount(account);
        //账号不存在
        if(user == null){
            return JsonMessage.error("账号或密码错误！");
        }
        //密码错误
        if(userMapper.checkUserPassword(account,password).equals(0)){
            return JsonMessage.error("账号或密码错误！");
        }

        request.getSession().setAttribute("user",user);//用户信息
        //写cookie
        response.addCookie(new Cookie("token", user.getToken()));
        return JsonMessage.success(0001);
    }

    @GetMapping("/regAccount")
    public String regAccount(HttpServletRequest request, HttpServletResponse response,Model model) {
        if(request.getSession().getAttribute("user") != null)logout(request,response);
        model.addAttribute("type", 2);
        return "login";
    }

    /**
     * 注册账号
     * @param account
     * @param password
     * @param checkPass
     * @param name
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ResponseBody
    @PostMapping("/regAccount")
    public Object regAccount(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "account", required = false) String account,
            @RequestParam(name = "pass", required = false) String password,
            @RequestParam(name = "checkPass", required = false) String checkPass,
            HttpServletRequest request, HttpServletResponse response, Model model) {

        if(account == null || password == null || checkPass == null ||
        account.equals("") || password.equals("") || checkPass.equals("")
        || name.equals("") || name == null){
            return JsonMessage.error("请认真填写注册信息！");
        }
        User u = userMapper.findByAccount(account);
        //两次密码是否一致
        if(!password.equals(checkPass)){
            return JsonMessage.error("注册密码不一致,请认真填写密码！");
        }
        //是否已注册
        if(u != null){
            return JsonMessage.error("["+account+"]该账号已被注册！");
        }

        User user = new User();
        String token = UUID.randomUUID().toString();
        user.setAccount(account);
        user.setPassword(password);
        user.setName(name);
        user.setToken(token);
        userService.createOrUpdate(user);

        request.getSession().setAttribute("user",userMapper.findByAccount(account));//用户信息
        //写cookie
        response.addCookie(new Cookie("token", token));
//        model.addAttribute("type", 6);//注册成功
        return JsonMessage.success(0000);
    }

    @ResponseBody
    @GetMapping("/getContent")
    public Object getContent(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("title","测试公告标题");
        map.put("content","测试公告内容..........");
        return JsonMessage.success(map);
    }

    /**
     * 临时方案，之后添加工具类
     * @param model
     * @param type
     * @param message
     * @param url
     * @return
     */
    public String errorMessage(Model model,Integer type,String message,String url){
        model.addAttribute("type", type);
        model.addAttribute("error", message);
        return url;
    }
}
