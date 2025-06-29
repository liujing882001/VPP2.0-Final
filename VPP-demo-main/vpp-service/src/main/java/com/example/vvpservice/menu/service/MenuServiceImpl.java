package com.example.vvpservice.menu.service;

import com.example.vvpcommom.SmartCopyUtil;
import com.example.vvpdomain.MenuRepository;
import com.example.vvpdomain.RoleMenuRepository;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.entity.Menu;
import com.example.vvpdomain.entity.Role;
import com.example.vvpdomain.entity.RoleMenu;
import com.example.vvpdomain.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements IMenuService {

    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RoleMenuRepository roleMenuRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${os.type}")
    private String osType;

    @Override
    public List<Map<String, Object>> getMenuList() {
        List<Menu> mi = menuRepository.findAllByOsTypeInAndStatusOrderByOrderNum(Arrays.asList("common", osType),"0");
        List<String> ids = mi.stream().map(Menu::getMenuId).collect(Collectors.toList());
        Map parentIdMap = mi.stream().collect(Collectors.groupingBy(Menu::getParentId));
        List<Map<String, Object>> result = new ArrayList<>();
        mi.stream().filter(e -> !ids.contains(e.getParentId())).collect(Collectors.toList()).forEach(e -> {
            Map<String, Object> data = new HashMap<>();
            deal(data, parentIdMap, e);
            result.add(data);
        });
        return result;
    }


    public List<Map<String, Object>> getMenuListFilter(List<String> menuIds) {
        List<Menu> mi = menuRepository.findAllByMenuIdInAndOsTypeInAndStatusOrderByOrderNum(menuIds,Arrays.asList("common", osType),"0");
        if (mi != null && !mi.isEmpty()) {
            mi = mi.stream().filter(c -> !c.getMenuType().equals("F") && !c.getMenuType().equals("J")).collect(Collectors.toList());
        }
        List<String> ids = mi.stream().map(Menu::getMenuId).collect(Collectors.toList());
        Map parentIdMap = mi.stream().collect(Collectors.groupingBy(Menu::getParentId));
        List<Map<String, Object>> result = new ArrayList<>();
        mi.stream().filter(e -> !ids.contains(e.getParentId())).collect(Collectors.toList()).forEach(e -> {
            Map<String, Object> data = new HashMap<>();
            deal(data, parentIdMap, e);
            result.add(data);
        });
        return result;
    }


    @Override
    public List<String> getMenuIds(String roleId) {
        List<String> result = new ArrayList<>();
        List<RoleMenu> allByRoleId = roleMenuRepository.findAllByRoleId(roleId);
        if (allByRoleId != null && !allByRoleId.isEmpty()) {
            result = allByRoleId.stream().map(e -> e.getMenuId()).collect(Collectors.toList());
        }
        return result;

    }

    @Override
    public List<Map<String, Object>> getMenuListByUserId(String userId) {
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isPresent()) {
            User user = byId.get();
            Role role = user.getRole();
            if (role != null) {
                String roleId = role.getRoleId();

                List<String> menuIds = getMenuIds(roleId);

                List<Map<String, Object>> menuListFilter = getMenuListFilter(menuIds);

                return menuListFilter;
            }

        }
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getCopilotPermission(String userId) {
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isPresent()) {
            User user = byId.get();
            Role role = user.getRole();
            if (role != null) {
                String roleId = role.getRoleId();

                List<String> menuIds = getMenuIds(roleId);

                List<Menu> mi = menuRepository.findAllByMenuIdInAndOsTypeInAndStatusOrderByOrderNum(menuIds,Arrays.asList("common", osType),"0");
                if (mi != null && mi.size() > 0) {
                    mi = mi.stream().filter(c -> "J".equals(c.getMenuType())).collect(Collectors.toList());
                }
                mi.forEach(menu -> {
                    if (StringUtils.isNotEmpty(menu.getIconLink())) {
                        menu.setIconLink(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + menu.getIconLink());
                    }
                });
                List<String> ids = mi.stream().map(u -> u.getMenuId()).collect(Collectors.toList());
                Map parentIdMap = mi.stream().collect(Collectors.groupingBy(Menu::getParentId));
                List<Map<String, Object>> result = new ArrayList<>();
                mi.stream().filter(e -> !ids.contains(e.getParentId())).collect(Collectors.toList()).forEach(e -> {
                    Map<String, Object> data = new HashMap<>();
                    deal(data, parentIdMap, e);
                    result.add(data);
                });
                return result;
            }

        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public boolean updateMenuName(String menuId, String menuName) {
        if(StringUtils.isEmpty(menuId)||StringUtils.isEmpty(menuName)){

            throw new IllegalArgumentException("参数有误！");
        }
        Optional<Menu> byId = menuRepository.findById(menuId);
        if (!byId.isPresent()) {
            throw new IllegalArgumentException("更新菜单ID不存在的菜单");
        }
        Menu menu = byId.get();

        Menu byMenuName = menuRepository.findByMenuName(menuName);
        if (byMenuName != null && !menuId.equals(byMenuName.getMenuId())) {
            throw new IllegalArgumentException("更新菜单名称已经存在");
        }

        menu.setMenuName(menuName);
        menuRepository.save(menu);
        return true;
    }

    @Override
    @Transactional
    public boolean updateMenuAndComponent(String menuId, String menuName, String component) {
        if(StringUtils.isEmpty(menuId)||StringUtils.isEmpty(menuName)||StringUtils.isEmpty(component)){

            throw new IllegalArgumentException("参数有误！");
        }
        Optional<Menu> byId = menuRepository.findById(menuId);
        if (!byId.isPresent()) {
            throw new IllegalArgumentException("更新菜单ID不存在的菜单");
        }
        Menu menu = byId.get();

        Menu byMenuName = menuRepository.findByMenuName(menuName);
        if (byMenuName != null && !menuId.equals(byMenuName.getMenuId())) {
            throw new IllegalArgumentException("更新菜单名称已经存在");
        }

        menu.setMenuName(menuName);
        menu.setComponent(component);
        menuRepository.save(menu);
        return true;

    }

    @Override
    public List<Map<String, Object>> menuTree() {
        List<Menu> mi = menuRepository.findAllByOsTypeInAndStatusOrderByOrderNum(Arrays.asList("common", osType),"0");
        List<String> ids = mi.stream().map(Menu::getMenuId).collect(Collectors.toList());
        Map parentIdMap = mi.stream().collect(Collectors.groupingBy(Menu::getParentId));
        List<Map<String, Object>> result = new ArrayList<>();
        mi.stream().filter(e -> !ids.contains(e.getParentId())).collect(Collectors.toList()).forEach(e -> {
            Map<String, Object> data = new HashMap<>();
            dealTree(data, parentIdMap, e);
            result.add(data);
        });
        return result;
    }


    private void deal(Map<String, Object> data, Map<Object, List> parentIdMap, Menu root) {
        try {
            data.putAll(SmartCopyUtil.objectToMap(root));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> children = new ArrayList<>();
        data.put("children", children);

        if (parentIdMap.get(root.getMenuId()) != null && !parentIdMap.get(root.getMenuId()).isEmpty()) {
            parentIdMap.get(root.getMenuId()).forEach(e -> {
                Map<String, Object> de = new HashMap<>();
                try {
                    de = SmartCopyUtil.objectToMap(e);
                    children.add(de);
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
                if (parentIdMap.get(((Menu) e).getMenuId()) != null && !parentIdMap.get(((Menu) e).getMenuId()).isEmpty()) {
                    deal(de, parentIdMap, (Menu) e);
                }

            });
        }


    }

    private void dealTree(Map<String, Object> data, Map<Object, List> parentIdMap, Menu root) {
        try {
            Map<String, Object> stringObjectMap = SmartCopyUtil.objectToMap(root);
            data.put("key", stringObjectMap.get("menuId"));
            data.put("title", stringObjectMap.get("menuName"));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> children = new ArrayList<>();
        data.put("children", children);

        if (parentIdMap.get(root.getMenuId()) != null && !parentIdMap.get(root.getMenuId()).isEmpty()) {
            parentIdMap.get(root.getMenuId()).forEach(e -> {
                Map<String, Object> de = new HashMap<>();
                try {
                    Map<String, Object> nde = SmartCopyUtil.objectToMap(e);
                    de.put("key", nde.get("menuId"));
                    de.put("title", nde.get("menuName"));
                    children.add(de);
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
                if (parentIdMap.get(((Menu) e).getMenuId()) != null && !parentIdMap.get(((Menu) e).getMenuId()).isEmpty()) {
                    dealTree(de, parentIdMap, (Menu) e);
                }

            });

        }
        if (children.isEmpty()) {
            data.remove("children");
        }


    }


}
