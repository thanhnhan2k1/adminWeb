package com.example.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.model.AppendixCITES;
import com.example.model.CategoryWood;

@Controller
@RequestMapping("/cateWood")
public class CateWoodController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	
	@GetMapping
	private String getList(HttpSession session, Model model) {
		List<CategoryWood> cateWoods=Arrays.asList(rest.getForObject(url+"/category-wood/getAll", CategoryWood[].class));
		session.setAttribute("cateWoods", cateWoods);
		model.addAttribute("length",cateWoods.size());
		return "manageCategoryWood/listCateWood.html";
	}
	@GetMapping("/add")
	private String getAdd(Model model) {
		model.addAttribute("cateWood", new CategoryWood());
		return "manageCategoryWood/addCateWood.html";
	}
	@PostMapping("/add")
	private String saveAdd(CategoryWood cateWood, Model model) {
		Date date=new Date(System.currentTimeMillis());
		cateWood.setUpdateAt(date);
		CategoryWood cate=rest.postForObject(url+"/category-wood/save", cateWood, CategoryWood.class);
		if(cate==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result","success");
		model.addAttribute("cateWood", cateWood);
		return "manageCategoryWood/addCateWood.html";
	}
	@GetMapping("/edit")
	private String getEdit(@RequestParam("index")int index, Model model, HttpSession session) {
		List<CategoryWood>list=(List<CategoryWood>) session.getAttribute("cateWoods");
		model.addAttribute("cateWood", list.get(index));
		return "manageCategoryWood/editCateWood.html";
	}
	@PostMapping("/edit")
	private String saveEdit(CategoryWood cateWood, Model model) {
		Date date=new Date(System.currentTimeMillis());
		cateWood.setUpdateAt(date);
		CategoryWood cate=rest.postForObject(url+"/category-wood/save", cateWood, CategoryWood.class);
		if(cate==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("cateWood", cateWood);
		return "manageCategoryWood/editCateWood.html";
	}
	@GetMapping("/delete")
	private String delete(@RequestParam(name="id")int id, HttpSession session) {
		rest.delete(url+"/category-wood/delete?id="+id);
		return "redirect:/cateWood";
	}
}
