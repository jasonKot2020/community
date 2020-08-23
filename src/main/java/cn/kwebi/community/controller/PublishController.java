package cn.kwebi.community.controller;

import cn.kwebi.community.dto.QuestionDTO;
import cn.kwebi.community.model.Question;
import cn.kwebi.community.model.User;
import cn.kwebi.community.service.QuestionService;
import cn.kwebi.community.util.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Integer id,
                       HttpServletRequest request,
                       Model model) {
        QuestionDTO question = questionService.getById(id);
        if (id != null && question != null) {
            //判断是否为自己的贴子
            QuestionDTO qt = questionService.getById(id);
            User u = (User) request.getSession().getAttribute("user");
            if (!qt.getCreator().equals(u.getId())) {
                return "redirect:/";
            }
        }
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("id", id);
        return "publish";
    }

    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Integer id,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);

        if (title == null || title.equals("") || tag == null || tag.equals("") || description == null || description.equals("")) {
            model.addAttribute("error", "填写不完整");
            return "publish";
        }

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "用户未登陆");
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());

        question.setId(id);
        questionService.createOrUpdate(question);
        if (id != null) {
            model.addAttribute("contextId", id);
            return "publish";
        }
        return "redirect:/";
    }

    /*
    删除指定ID的帖子
     */
    @ResponseBody
    @GetMapping("/del/{id}")
    public Object del(@PathVariable String id, HttpServletRequest request) {
        return JsonMessage.success();
    }

    /*
    订阅star
     */
    @ResponseBody
    @GetMapping("/star/{id}")
    public Object star(@PathVariable String id, HttpServletRequest request) {
        return JsonMessage.success();
    }
}
