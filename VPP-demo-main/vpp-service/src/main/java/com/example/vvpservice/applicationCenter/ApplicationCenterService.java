package com.example.vvpservice.applicationCenter;

import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.applicationCenter.model.ApplicationResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationCenterService {

	public static final String ICON_DIR = System.getProperty("user.home") + "/icons/";

	public List<ApplicationResponse> queryApplication(String name, String type, HttpServletRequest request) {

		if (StringUtils.isEmpty(name) && StringUtils.isEmpty(type)) {
			throw new RuntimeException("必须输入名称或类别");
		}
		String userId = request.getHeader("authorizationCode");
		List<String> menuList = queryPrivilege(userId);

		MenuRepository menuRepository = SpringBeanHelper.getBeanOrThrow(MenuRepository.class);
		List<ApplicationResponse> responseList = new ArrayList<>();
		List<Menu> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(name)) {
			list = menuRepository.findAllByMenuNameLike("%" + name + "%");
			list =
					list.stream().filter(o -> !o.getParentId().equals("0")).filter(o -> StringUtils.isNotEmpty(o.getIconLink())).filter(o -> menuList.contains(o.getMenuId())).sorted(Comparator.comparing(Menu::getOrderNum)).collect(Collectors.toList());
		}
		if (!StringUtils.isEmpty(type)) {
			list = menuRepository.findAllByType("%" + type + "%");
			list =
					list.stream().filter(o -> menuList.contains(o.getMenuId())).sorted(Comparator.comparing(Menu::getOrderNum)).collect(Collectors.toList());
		}
		list.forEach(o -> {
			ApplicationResponse response = new ApplicationResponse(o);
			responseList.add(response);
		});
		return responseList;
	}

	public void saveIconLink(String menuName, String fileName) {
		MenuRepository menuRepository = SpringBeanHelper.getBeanOrThrow(MenuRepository.class);
		Menu menu = menuRepository.findByMenuName(menuName);
		if (menu == null) {
			throw new RuntimeException("找不到应用");
		}
		menu.setIconLink("/applicationCenter/icon/" + fileName);
		menuRepository.save(menu);
	}

	public void addApplicationLog(HttpServletRequest request, String name) {
		String userId = request.getHeader("authorizationCode");
		MenuRepository menuRepository = SpringBeanHelper.getBeanOrThrow(MenuRepository.class);
		Menu menu = menuRepository.findByMenuName(name);
		if (menu == null) {
			throw new RuntimeException("找不到应用");
		}
		ApplicationLogRepository applicationLogRepository = SpringBeanHelper.getBeanOrThrow(ApplicationLogRepository.class);
		ApplicationLog applicationLog = applicationLogRepository.findByUserIdAndApplicationName(userId, name);
		if (applicationLog == null) {
			applicationLog = new ApplicationLog();
			applicationLog.setUserId(userId);
			applicationLog.setApplicationId(menu.getMenuId());
			applicationLog.setApplicationName(name);

		} else {
			applicationLog.setTs(LocalDateTime.now());
		}
		applicationLogRepository.save(applicationLog);
	}

	public List<ApplicationResponse> queryApplicationLog(HttpServletRequest request) {
		String userId = request.getHeader("authorizationCode");
		ApplicationLogRepository applicationLogRepository = SpringBeanHelper.getBeanOrThrow(ApplicationLogRepository.class);
		MenuRepository menuRepository = SpringBeanHelper.getBeanOrThrow(MenuRepository.class);
		List<ApplicationLog> logs =
				applicationLogRepository.findAllByUserId(userId).stream().sorted((o1, o2) -> o2.getTs().compareTo(o1.getTs())).collect(Collectors.toList());
		List<ApplicationResponse> responses = new ArrayList<>();
		logs.forEach(o -> {
			Menu menu = menuRepository.findByMenuName(o.getApplicationName());
			if (menu != null) {
				ApplicationResponse response = new ApplicationResponse(menu);
				responses.add(response);
			}
		});
		return responses;
	}

	public List<ApplicationResponse> queryAllApplication(HttpServletRequest request) {
		MenuRepository menuRepository = SpringBeanHelper.getBeanOrThrow(MenuRepository.class);
		String userId = request.getHeader("authorizationcode");
		List<String> menuList = queryPrivilege(userId);
		List<Menu> title =
				menuRepository.findAllByParentId("0").stream().filter(o -> StringUtils.isNotEmpty(o.getIconLink())).filter(o -> menuList.contains(o.getMenuId())).sorted(Comparator.comparing(Menu::getOrderNum)).collect(Collectors.toList());
		List<ApplicationResponse> res = new ArrayList<>();
		for (Menu menu : title) {
			ApplicationResponse m = new ApplicationResponse(menu);
			List<Menu> child =
					menuRepository.findAllByParentId(menu.getMenuId()).stream().filter(o -> StringUtils.isNotEmpty(o.getIconLink())).sorted(Comparator.comparing(Menu::getOrderNum)).collect(Collectors.toList());
			child = child.stream().filter(o -> menuList.contains(o.getMenuId())).collect(Collectors.toList());
			for (Menu c : child) {
				m.getChild().add(new ApplicationResponse(c));
			}
			res.add(m);
		}
		return res;
	}

	private List<String> queryPrivilege(String userId) {
		UserRoleRepository userRoleRepository = SpringBeanHelper.getBeanOrThrow(UserRoleRepository.class);
		RoleMenuRepository roleRepository = SpringBeanHelper.getBeanOrThrow(RoleMenuRepository.class);
		UserRole userRole = userRoleRepository.findByUserId(userId);
		List<RoleMenu> menuList = roleRepository.findAllByRoleId(userRole.getRoleId());
		return menuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
	}
}
