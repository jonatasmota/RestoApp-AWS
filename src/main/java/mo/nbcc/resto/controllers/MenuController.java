package mo.nbcc.resto.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mo.nbcc.resto.model.Menu;
import mo.nbcc.resto.model.MenuItem;
import mo.nbcc.resto.services.MenuItemService;
import mo.nbcc.resto.services.MenuService;

@Controller
public class MenuController {

	private MenuService menuService;
	private MenuItemService menuItemService;

	@Autowired
	public MenuController(MenuService menuService, MenuItemService menuItemService) {
		this.menuService = menuService;
		this.menuItemService = menuItemService;
	}

	@GetMapping("/menus")
	public String allMenus(@RequestParam(value = "search", required = false) String search,
			@RequestParam(defaultValue = "menuName") String sort, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "asc") String direction,
			Model model) {

		Sort.Order sortOrder = new Sort.Order(Sort.Direction.fromString(direction), sort).ignoreCase();
		PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortOrder));

		Page<Menu> menuPage;
		if (search != null && !search.isBlank()) {
			menuPage = menuService.getMenuByName(search, pageRequest);
		} else {
			menuPage = menuService.getAllMenus(pageRequest);
		}
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", menuPage.getTotalPages());
		model.addAttribute("menu", menuPage.getContent());

		return "menu/index";
	}

	@GetMapping("/menus/new")
	public String createMenu(@RequestParam(value = "menuId", required = false) Long menuId, Model model) {
		Menu menu = new Menu();

		if (menuId != null) {
			menu = menuService.getMenuById(menuId);
		}
		model.addAttribute("menuItems", menu.getItems());
		model.addAttribute("menu", menu);
		return "menu/menuForm";
	}

	@PostMapping("/menus/save")
	public String saveMenu(@ModelAttribute("menu") Menu menu, Model model) {
		Set<MenuItem> items = new HashSet<MenuItem>();
		Menu saveMenu = new Menu();
		if (menu.getMenuId() == 0) {
			saveMenu = new Menu(menu.getMenuName(), menu.getMenuDetails(), menu.getMenuCreated(), items,
					menu.getEvents());
		}
		menuService.saveMenu(saveMenu);
		model.addAttribute("menu", saveMenu);

		return "menu/menuForm";
	}
	
	@PostMapping("/menu/save")
	public String saveNewMenu(Menu menu) {
		menuService.saveMenu(menu);
		return "menu/menuForm";
	}
	
	@PostMapping("/menus/updateMenu")
	public String updateMenu(Menu menu) {
		menuService.saveMenu(menu);
		return "menu/menuForm";
	}

	@GetMapping("/menus/edit/{menuId}")
	public String editMenu(@PathVariable("menuId") Long menuId, Model model) {
		Menu menu = menuService.getMenuById(menuId);
		model.addAttribute("menu", menu);
		return "menu/menuForm";
	}

	@GetMapping("/menus/delete/{menuId}")
	public String deleteMenu(@PathVariable("menuId") Long menuId) {
		menuService.deleteById(menuId);
		return "redirect:/menus";
	}

	// list of Menu Items
	@GetMapping("/menuItems")
	public String allMenuItems(@RequestParam(value = "searchItem", required = false) String searchItem,
			@RequestParam(defaultValue = "menuItemName") String sort, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "asc") String direction,
			Model model, Menu menu) {

		Sort.Order sortOrder = new Sort.Order(Sort.Direction.fromString(direction), sort).ignoreCase();
		PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortOrder));

		Page<MenuItem> menuItemPage;
		if (searchItem != null && !searchItem.isBlank()) {
			menuItemPage = menuItemService.getMenuItemsBySearchValue(searchItem, menu, pageRequest);
		} else {
			menuItemPage = menuItemService.getAllMenuItems(pageRequest);
		}
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", menuItemPage.getTotalPages());
		model.addAttribute("menuItem", menuItemPage.getContent());
		model.addAttribute("menu", menu);

		return "menuItem/index";
	}

	// create new menu item
	@GetMapping("/newMenuItem")
	public String createMenuItem(Model model) {
		List<Menu> menuList = menuService.getAllMenus();
		MenuItem menuItem = new MenuItem();
		model.addAttribute("menuItem", menuItem);
		model.addAttribute("menuList", menuList);
		
		return "menuItem/menuItemForm";
	}

	// save new Menu Item
	@Transactional
	@PostMapping("/saveMenuItem")
	public String saveMenuItem(@Valid @ModelAttribute("menuItem") MenuItem menuItem, Menu menu, Model model,
			RedirectAttributes ra, BindingResult br) {
		MenuItem itemId = menuItemService.getMenuItemById(menuItem.getMenuItemId());
		
		menu = menuService.getMenuById(menu.getMenuId());
		Set<MenuItem> menuItems = menu.getItems();
		menuItem.setMenu(menuService.getMenuById(menu.getMenuId()));
		menuItemService.saveMenuItem(menuItem);
		menuItems.add(menuItem);
		menu.setItems(menuItems);
		menuService.saveMenu(menu);

		ra.addFlashAttribute("menuItem", menuItem);
		ra.addAttribute("menuId", menu.getMenuId());

		return "redirect:/menus/new";
	}
	
	/*
	@PostMapping("/updateMenuItem")
	public String updateMenuItem(@ModelAttribute("menuItem") MenuItem menuItem, Menu menu) {
		Menu menuId = menuService.getMenuById(menu.getMenuId());
		MenuItem newItem = new MenuItem(menuItem.getMenuItemName(), menuItem.getMenuItemDescription(), menuItem.getMenuItemPrice(), menuItem.getMenuItemImgUrl(), menuId);
		menuItemService.saveMenuItem(newItem);
		
		return "menuItem/editMenuItem";
	}
	
	 * @GetMapping("/editItem") public String updateMenuItem(@RequestParam(value =
	 * "menuItemId") Long menuItemId, Model model) { MenuItem existingItem =
	 * menuItemService.getMenuItemById(menuItemId); List<Menu> menus =
	 * menuService.getAllMenus();
	 * 
	 * model.addAttribute("menuList", menus); model.addAttribute("menuItem",
	 * existingItem); return "menuItem/editMenuItem"; }
	 */
	
	//save new menu item
	
	@PostMapping("/menuItem/save")
	public String saveNewMenuItem(MenuItem menuItem) {
		menuItemService.saveMenuItem(menuItem);
		return "redirect:/menuItems";
	}
	
	@GetMapping("/updateItem/{menuItemId}")
	public String editMenuItem(@PathVariable("menuItemId") Long menuItemId, Model model) {
		MenuItem existingItem = menuItemService.getMenuItemById(menuItemId);
		List<Menu> menus = menuService.getAllMenus();
		model.addAttribute("menuList", menus);
		model.addAttribute("menuItem", existingItem);
		return "menuItem/menuItemForm";
	}

	@GetMapping("/deleteItem/{menuItemId}")
	public String deleteMenuItem(@PathVariable("menuItemId") Long menuItemId) {
		menuItemService.deleteById(menuItemId);
		return "redirect:/menuItems";
	}

}
