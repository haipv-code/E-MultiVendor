package com.aptech.shop;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.aptech.ControllerHelper;
import com.aptech.common.entity.Customer;
import com.aptech.common.entity.Shop;


@Controller
public class ShopController {
	@Autowired private ShopService shopService;
	@Autowired private ControllerHelper controllerHelper;
	
	@GetMapping("/shops")
	public String listFirstPage(Model model, HttpServletRequest request) {
		return listShopsByPage(model, request, 1, "createdTime", "desc", null);
	}

	@GetMapping("/shops/page/{pageNum}")
	public String listShopsByPage(Model model, HttpServletRequest request,
						@PathVariable(name = "pageNum") int pageNum,
						String sortField, String sortDir, String keyword
			) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Page<Shop> page = shopService.listForCustomerByPage(customer, pageNum, sortField, sortDir, keyword);
		List<Shop> listShops = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listShops", listShops);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/shops");
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		long startCount = (pageNum - 1) * ShopService.SHOPS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ShopService.SHOPS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "shops/shop_customer";		
	}
}
