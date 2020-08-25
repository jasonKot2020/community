package cn.kwebi.community.controller;

import cn.kwebi.community.dto.CommentDTO;
import cn.kwebi.community.dto.QuestionDTO;
import cn.kwebi.community.enums.CommentTypeEnum;
import cn.kwebi.community.model.User;
import cn.kwebi.community.service.CollectionArticleService;
import cn.kwebi.community.service.CommentService;
import cn.kwebi.community.service.LikePostService;
import cn.kwebi.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikePostService likePostService;

    @Autowired
    private CollectionArticleService collectionArticleService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable("id") Integer id,
                           HttpServletRequest request,
                           Model model){
        User user = (User) request.getSession().getAttribute("user");

        QuestionDTO questionDTO = questionService.getById(user.getId(),id);
        if(user != null && questionDTO.getCreator().equals(user.getId())){
            commentService.updateLookStatus(id);
        }

        List<CommentDTO> comments = commentService.listByParentId(id, CommentTypeEnum.QUESTION);

        questionService.incView(id);//增加阅读数
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);

        if (user != null) {
            model.addAttribute("user",user);
        }else{
            model.addAttribute("user",questionDTO.getUser());
        }
        return "question";
    }

    @ResponseBody
    @GetMapping("/likePost")
    public Object like(HttpServletRequest request,
                       @RequestParam(name = "accountId", required = false) Integer accountId,
                       @RequestParam(name = "id", required = false) Integer id,
                       @RequestParam(name = "articleId", required = false) Integer articleId){
        return likePostService.LikePost(accountId,id,articleId);
    }

    @ResponseBody
    @GetMapping("/question/delete")
    public Object delete(HttpServletRequest request,
                       @RequestParam(name = "id", required = false) Integer id){
        User user = (User) request.getSession().getAttribute("user");
        return questionService.deletById(user.getId(),id);
    }

    @ResponseBody
    @GetMapping("/addCollection")
    public Object addCollection(HttpServletRequest request, @RequestParam(name = "articleId", required = false) Integer articleId){
        User u = (User) request.getSession().getAttribute("user");
        return collectionArticleService.Collection(u.getId(),articleId,0);
    }

    @ResponseBody
    @GetMapping("/removeCollection")
    public Object removeCollection(HttpServletRequest request, @RequestParam(name = "articleId", required = false) Integer articleId){
        User u = (User) request.getSession().getAttribute("user");
        return collectionArticleService.Collection(u.getId(),articleId,1);
    }
}
