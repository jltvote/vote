package com.jlt.action;

import com.xcrm.log.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class VoteController {

    private static Logger logger = Logger.getLogger(VoteController.class);

    /**
     * 首页登陆
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/index",method = {RequestMethod.GET})
    public String login(HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.login");
        return "index";
    }

}
