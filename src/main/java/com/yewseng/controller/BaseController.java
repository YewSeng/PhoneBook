package com.yewseng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BaseController {
	
	@GetMapping("/")
	public ModelAndView goToIndexPage(HttpServletRequest request, 
			HttpServletResponse response) {
		log.info("Entered into the / request");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("contactHome");
		log.info("Went to contactHome.jsp page");
	    response.setStatus(HttpServletResponse.SC_OK);
		return mv;
	}	
}
