package com.example.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.model.CategoryBlog;
import com.example.model.Voucher;

@Controller
@RequestMapping("/voucher")
public class VoucherController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String getListVoucher(HttpSession session) {
		List<Voucher>listVoucher=new ArrayList<>();
		listVoucher=Arrays.asList(rest.getForObject(url+"/voucher/getAll", Voucher[].class));
		session.setAttribute("listVoucher", listVoucher);
		return "manageVoucher/listVoucher";
	}
	/*
	
	 * */
	@GetMapping("/add")
	private String getAddVoucher(Model model) {
		Voucher voucher=new Voucher();
		model.addAttribute("voucher", voucher);
		return "manageVoucher/addVoucher";
	}
	@PostMapping("/add")
	private String saveAdd(@RequestParam("code")String code,@RequestParam("dateStart")Date dateStart, @RequestParam("dateEnd")Date dateEnd,
			@RequestParam("amount")int amount, Model model) {
		Voucher voucher=new Voucher();
		voucher.setAmount(amount);
		voucher.setCode(code);
		voucher.setDateEnd(dateEnd);
		voucher.setDateStart(dateStart);
		Date date=new Date(System.currentTimeMillis());
		voucher.setDateUpdate(date);
		Voucher vi=rest.postForObject(url+"/voucher/save", voucher, Voucher.class);
		if(vi==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("voucher", voucher);
		return "manageVoucher/addVoucher";
	}
	@GetMapping("/edit")
	private String getEdit(Model model, HttpSession session,@RequestParam("index")int index) {
		List<Voucher>listVoucher=(List<Voucher>) session.getAttribute("listVoucher");
		session.setAttribute("voucher",listVoucher.get(index));
		
		return "manageVoucher/editVoucher";
	}
	@PostMapping("/edit")
	private String saveEdit(@RequestParam("code")String code,@RequestParam("dateStart")Date dateStart, @RequestParam("dateEnd")Date dateEnd,
			@RequestParam("amount")int amount, Model model, HttpSession session) {
		Voucher voucher=new Voucher();
		voucher.setAmount(amount);
		voucher.setCode(code);
		voucher.setDateEnd(dateEnd);
		voucher.setDateStart(dateStart);
		Date date=new Date(System.currentTimeMillis());
		voucher.setDateUpdate(date);
		Voucher vi=rest.postForObject(url+"/voucher/save", voucher, Voucher.class);
		if(vi==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		model.addAttribute("voucher", voucher);
		return "manageVoucher/editVoucher";
	}
	@GetMapping("/delete")
	private String deleteItem(@RequestParam(name="code")String code, HttpSession session) {
		String key=(String)session.getAttribute("key");
		rest.delete(url+"/voucher/delete?code="+code);
		return "redirect:/voucher";
	}
}
