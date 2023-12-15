package com.example.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.AppendixCITES;
import com.example.model.CategoryWood;
import com.example.model.GeographicalArea;
import com.example.model.Glossary;
import com.example.model.Image;
import com.example.model.PlantFamily;
import com.example.model.Preservation;
import com.example.model.Wood;

@Controller
@RequestMapping("/wood")
public class WoodController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String get(Model model, HttpSession session , @RequestParam(name="category", defaultValue = "0", required = false)int category) {
		List<Wood>listWood=Arrays.asList(rest.getForObject(url+"/wood/getAll?category="+category, Wood[].class));
		session.setAttribute("listWood", listWood);
		List<CategoryWood> listCateWood=Arrays.asList(rest.getForObject(url+"/category-wood/getAll", CategoryWood[].class));
		session.setAttribute("listCateWood", listCateWood);
		return "manageWood/listWood";
	}
	
	@GetMapping("/add")
	private String getAdd(Model model, HttpSession session) {
		List<AppendixCITES>cites=Arrays.asList(rest.getForObject(url+"/cites/get", AppendixCITES[].class));
		session.setAttribute("cites", cites);
		List<GeographicalArea>areas=Arrays.asList(rest.getForObject(url+"/area/getAll", GeographicalArea[].class));
		session.setAttribute("areas", areas);
		List<Preservation>listPre=Arrays.asList(rest.getForObject(url+"/preservation/get", Preservation[].class));
		session.setAttribute("listPre", listPre);
		List<PlantFamily>listFamily=Arrays.asList(rest.getForObject(url+"/plantfamily/getAll", PlantFamily[].class));
		session.setAttribute("listFamily", listFamily);
		model.addAttribute("wood", new Wood());
		List<CategoryWood> listCateWood=Arrays.asList(rest.getForObject(url+"/category-wood/getAll", CategoryWood[].class));
		session.setAttribute("listCateWood", listCateWood);
		return "manageWood/addWood";
	}
	@PostMapping("/save")
	private String saveAddInfor(Model model,
			@RequestParam("vietnamese")String vietnamese,
			@RequestParam("scientificName")String scientificName,
			@RequestParam("commercialName")String commercialName,
			@RequestParam("specificGravity")int specificGravity,
			@RequestParam("color")String color,
			@RequestParam("note")String note,
			@RequestParam("area")int[] area,
			@RequestParam("cite")int cite,
			@RequestParam("image")MultipartFile[]file,
			@RequestParam("nameImage")String[]nameImage,
			@RequestParam("preservation")int preservation,
			@RequestParam("character")String character,
			@RequestParam("family")int family,
			@RequestParam("category")int category,
			HttpSession session)throws IOException {
		Wood wood=new Wood();
		wood.setVietnameName(vietnamese);
		wood.setScientificName(scientificName);
		wood.setCommercialName(commercialName);
		wood.setSpecificGravity(specificGravity);
		wood.setColor(color);
		List<GeographicalArea>areas=(List<GeographicalArea>) session.getAttribute("areas");
		List<GeographicalArea>listAreas=new ArrayList<>();
		for(int i:area)
			listAreas.add(areas.get(i));
		wood.setListAreas(listAreas);
		List<AppendixCITES>cites=(List<AppendixCITES>) session.getAttribute("cites");
		wood.setAppendixCites(cites.get(cite));
		List<Preservation>listPre=(List<Preservation>) session.getAttribute("listPre");
		wood.setPreservation(listPre.get(preservation));
		List<PlantFamily>listFamily=(List<PlantFamily>) session.getAttribute("listFamily");
		wood.setFamily(listFamily.get(family));
		List<CategoryWood>listCategory=(List<CategoryWood>)session.getAttribute("listCateWood");
		wood.setCategoryWood(listCategory.get(category));
		wood.setCharacteristic(character);
		List<Image>listImage=new ArrayList<Image>();
		for(String i:nameImage) {
			if(!i.isEmpty()) {
				Image img=new Image();
				img.setNameImg(i);
				listImage.add(img);
			}
		}
		wood.setListImage(listImage);
		MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);// Main request's headers
		for(MultipartFile i:file) {
			HttpHeaders requestHeadersAttachment = new HttpHeaders();
			HttpEntity<ByteArrayResource> attachmentPart;
			ByteArrayResource fileAsResource = new ByteArrayResource(i.getBytes()) {
				@Override
				public String getFilename() {
					return i.getOriginalFilename();
				}
			};
			attachmentPart = new HttpEntity<>(fileAsResource, requestHeadersAttachment);

			multipartRequest.add("file", attachmentPart);
		}
		HttpHeaders requestHeadersJSON = new HttpHeaders();
		requestHeadersJSON.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Wood> requestEntityJSON = new HttpEntity<>(wood, requestHeadersJSON);
		multipartRequest.add("wood", requestEntityJSON);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, requestHeaders);// final
																														// request
		ResponseEntity<Wood> response = rest.postForEntity(url+"/wood/save", requestEntity,
				Wood.class);
		System.out.println(response.getBody());
		if (response.getBody() == null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		return "manageWood/addWood";
	}
	@GetMapping("/edit")
	private String getEdit(Model model, @RequestParam(name="index")int index, HttpSession session) {
		List<AppendixCITES>cites=Arrays.asList(rest.getForObject(url+"/cites/get", AppendixCITES[].class));
		session.setAttribute("cites", cites);
		List<Preservation>listPre=Arrays.asList(rest.getForObject(url+"/preservation/get", Preservation[].class));
		session.setAttribute("listPre", listPre);
		List<PlantFamily>listFamily=Arrays.asList(rest.getForObject(url+"/plantfamily/getAll", PlantFamily[].class));
		session.setAttribute("listFamily", listFamily);
		model.addAttribute("wood", new Wood());
		List<CategoryWood> listCateWood=Arrays.asList(rest.getForObject(url+"/category-wood/getAll", CategoryWood[].class));
		session.setAttribute("listCateWood", listCateWood);
		List<Wood>listWood=(List<Wood>) session.getAttribute("listWood");
		session.setAttribute("wood", listWood.get(index));
		Map<GeographicalArea, Boolean>mapArea=new HashMap<GeographicalArea, Boolean>();
		List<GeographicalArea>areas=Arrays.asList(rest.getForObject(url+"/area/getAll", GeographicalArea[].class));
		session.setAttribute("areas", areas);
		for(GeographicalArea i:areas) {
			if(listWood.get(index).getListAreas().contains(i))
				mapArea.put(i, true);
			else
				mapArea.put(i, false);
		}
		session.setAttribute("mapArea", mapArea);
		return "manageWood/editWood";
	}
	@PostMapping("/saveEdit")
	private String saveEdit(Model model,
			@RequestParam("vietnamese")String vietnamese,
			@RequestParam("scientificName")String scientificName,
			@RequestParam("commercialName")String commercialName,
			@RequestParam("specificGravity")int specificGravity,
			@RequestParam("color")String color,
			@RequestParam("note")String note,
			@RequestParam("area")List<Integer> area,
			@RequestParam("cite")int cite,
			@RequestParam("preservation")int preservation,
			@RequestParam("character")String character,
			@RequestParam("family")int family,
			@RequestParam("category")int category,
			HttpSession session) {
			Wood wood=(Wood)session.getAttribute("wood");
			wood.setVietnameName(vietnamese);
			wood.setScientificName(scientificName);
			wood.setCommercialName(commercialName);
			wood.setSpecificGravity(specificGravity);
			wood.setColor(color);
			wood.setNote(note);
			List<GeographicalArea>list=new ArrayList<GeographicalArea>();
			List<GeographicalArea>listAreas=(List<GeographicalArea>) session.getAttribute("areas");
			for(GeographicalArea i:listAreas) {
				if(area.contains(i.getId()))
					list.add(i);
			}
			wood.setListAreas(list);
			List<AppendixCITES>listCites=(List<AppendixCITES>) session.getAttribute("cites");
			wood.setAppendixCites(listCites.get(cite));
			List<Preservation>listPre=(List<Preservation>) session.getAttribute("listPre");
			wood.setPreservation(listPre.get(preservation));
			wood.setCharacteristic(character);
			List<PlantFamily> listFamily=(List<PlantFamily>) session.getAttribute("listFamily");
			wood.setFamily(listFamily.get(family));
			List<CategoryWood>listCate=(List<CategoryWood>) session.getAttribute("listCateWood");
			wood.setCategoryWood(listCate.get(category));
			wood = rest.postForObject(url + "/wood/saveEdit", wood, Wood.class);
			if (wood == null)
				model.addAttribute("result", "fail");
			else
				model.addAttribute("result", "success");
		return "manageWood/editWood";
	}
	@GetMapping("/image")
	private String getView(@RequestParam("index")int index,Model model, HttpSession session) {
		List<Wood>listWood=(List<Wood>) session.getAttribute("listWood");
		session.setAttribute("wood", listWood.get(index));
		return "manageWood/imageWood";
	}
	@GetMapping("/deleteImg")
	private String deleteImg(HttpSession session,@RequestParam(name="index")int index) {
		Wood wood=(Wood)session.getAttribute("wood");
		rest.postForObject(url+"/wood/deleteImg", wood.getListImage().get(index), Void.class);
		wood.getListImage().remove(index);
		session.setAttribute("wood", wood);
		return "manageWood/imageWood";
	}
	@PostMapping("/saveImg")
	private String addImg(HttpSession session,@RequestParam("image")MultipartFile file, @RequestParam("nameImage")String nameImage ) throws IOException {
		Wood wood=(Wood)session.getAttribute("wood");
		Image image=new Image();
		image.setNameImg(nameImage);
		wood.getListImage().add(image);
		MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);// Main request's headers
		HttpHeaders requestHeadersAttachment = new HttpHeaders();
		HttpEntity<ByteArrayResource> attachmentPart;
		ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}
		};
		attachmentPart = new HttpEntity<>(fileAsResource, requestHeadersAttachment);

		multipartRequest.add("file", attachmentPart);
		
		HttpHeaders requestHeadersJSON = new HttpHeaders();
		requestHeadersJSON.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Wood> requestEntityJSON = new HttpEntity<>(wood, requestHeadersJSON);
		multipartRequest.add("wood", requestEntityJSON);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, requestHeaders);// final
																														// request
		ResponseEntity<Wood> response = rest.postForEntity(url+"/wood/saveImg", requestEntity,
				Wood.class);
		session.setAttribute("wood", response.getBody());
		return "manageWood/imageWood";
	}
	@GetMapping("/delete")
	private String deleteItem(@RequestParam("index")int index, HttpSession session) {
		List<Wood>listWood=(List<Wood>) session.getAttribute("listWood");
		Wood wood=listWood.get(index);
		rest.postForObject(url+"/wood/delete",wood , Void.class);
		return "redirect:/wood";
	}
}
