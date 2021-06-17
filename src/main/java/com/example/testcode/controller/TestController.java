package com.example.testcode.controller;

import com.example.testcode.dto.Person;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

	@GetMapping(value = "/get", produces = "application/json;charset=UTF-8")
	public Map<String, Object> GetMethod(@RequestParam String name, @RequestParam String id) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("id", id);
		return map;
	}

	@PostMapping("/post/{test}")
	public Map<String, Object> PostMethod(@PathVariable String test, @RequestBody Person person) {
		Map<String, Object> map = new HashMap<>();
		map.put("value", person);
		map.put("test", test);

		return map;
	}

	@DeleteMapping("/delete/{test}")
	public Map<String, Object> DeleteMethod(@PathVariable String test) {

		Map<String, Object> map = new HashMap<>();
		map.put("id", test);
		return map;
	}

	@PutMapping("/put")
	public String PutMethod() {
		return "Put 확인 ";
	}
}
