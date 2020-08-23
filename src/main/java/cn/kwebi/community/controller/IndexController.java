package cn.kwebi.community.controller;

import cn.kwebi.community.dto.PaginationDTO;
import cn.kwebi.community.dto.QuestionDTO;
import cn.kwebi.community.service.QuestionService;
import cn.kwebi.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size) {
        PaginationDTO pagination = questionService.list(page,size);
        if(pagination != null){
            for(QuestionDTO qt : pagination.getQuestions()){
                if(qt.getTitle().length() > 50){
                    qt.setTitle(qt.getTitle().substring(0,50)+"...");
                }
//                if(qt.getDescription().length() > 200){
//                    qt.setDescription(qt.getDescription().substring(0,200)+"[全文请进贴查阅]");
//                }
                qt.setDescription(qt.getTag());
            }
        }
        model.addAttribute("pagination", pagination);
        return "index";
    }
}
