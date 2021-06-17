package com.example.testcode.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class DocsController {
	@GetMapping(value = "/hello")
	public ModelAndView goIndex(ModelAndView modelAndView) {
		modelAndView.setViewName("docs/index");

		return modelAndView;
	}

}
