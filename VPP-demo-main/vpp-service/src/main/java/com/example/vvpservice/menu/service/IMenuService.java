package com.example.vvpservice.menu.service;


import java.util.List;
import java.util.Map;

public interface IMenuService {

    List<Map<String, Object>> getMenuList();


    List<String> getMenuIds(String roleId);

    List<Map<String, Object>> getMenuListByUserId(String userId);

    List<Map<String, Object>> getCopilotPermission(String userId);

    boolean updateMenuName(String menuId, String menuName);

    boolean updateMenuAndComponent(String menuId, String menuName, String component);

    List<Map<String, Object>> menuTree();

}
