package com.example.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.model.PlantFamily;
import com.example.model.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class HomeController {
	private RestTemplate rest=new RestTemplate();
	@GetMapping
	private String getHome(HttpServletRequest request, HttpSession session) {
		User user=(User)session.getAttribute("user");
		if(user==null)
		{
			// kiem tra trong cookie co thong tin dang nhap khong
			Cookie[] cookies=request.getCookies();
			String token="";
			for(Cookie cookie:cookies)
				if(cookie.getName().equals("token"))
				{
					token=cookie.getValue();
					break;
				}
			if(!token.isEmpty()) {
				HttpHeaders headers = new HttpHeaders();
				headers.set("Authorization", "Bearer "+token);
				ResponseEntity<User> u=rest.exchange("https://da-server2-production.up.railway.app/user/getUserInfo",HttpMethod.GET, new HttpEntity<Void>(headers), User.class);
				session.setAttribute("user", u.getBody());
				session.setAttribute("token", token);
			}
		}
		List<PlantFamily> listFamily =  (List<PlantFamily>) session.getAttribute("listFamily");
		if (listFamily == null) {
			listFamily = Arrays.asList(rest.getForObject("https://da-server2-production.up.railway.app/plantfamily/get", PlantFamily[].class));
			session.setAttribute("listFamily", listFamily);
		}
		return "index";
	}
	@GetMapping("/getInfor")
	private String getInfor(@RequestParam("editor1")String edit1,Model model, HttpServletResponse response) {
		System.out.println(edit1);
		model.addAttribute("text", "<ul><li>Gỗ dác và gỗ lõi phân biệt, gỗ dác màu trắng vàng nhạt, gỗ lõi màu nâu vàng tới nâu sẫm.</li><li>Vòng năm tương đối rõ ràng, rải mô mềm hẹp ở ranh giới vòng năm.</li><li>Mạch đơn và kép ngắn, phân tán.</li><li>Mô mềm vây quanh mạch không đều, hình cánh, hình thoi. Trong một vòng sinh trưởng có khi gặp một đến hai dải mô mềm hẹp.</li><li>Tia gỗ nhỏ, hẹp, có màu gần giống màu mặt gỗ.</li><li>Gỗ cứng và nặng trung bình.</li></ul>");
		response.setContentType("text/html");
		return "index.html";
	}
}
