package com.example.vvpweb.systemmanagement.systemuser;


import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpservice.menu.service.IMenuService;
import com.example.vvpweb.systemmanagement.systemuser.model.SysMenuAndUrlModel;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@CrossOrigin
@RequestMapping("/system_management/system_user/menu")
@Api(value = "系统管理-系统用户-菜单", tags = {"系统管理-系统用户-菜单"})
public class SysMenuInfoController {

    @Autowired
    private IMenuService menuService;


    @UserLoginToken
    @RequestMapping(value = "/menuList", method = {RequestMethod.POST})
    public ResponseResult menuList() {
        return ResponseResult.success(menuService.getMenuList());
    }

    @UserLoginToken
    @RequestMapping(value = "/menuTree", method = {RequestMethod.POST})
    public ResponseResult menuTree() {
        return ResponseResult.success(menuService.menuTree());
    }

    @UserLoginToken
    @RequestMapping(value = "/menuListByRoleId", method = {RequestMethod.POST})
    public ResponseResult menuListByRoleId(@RequestParam("roleId") String roleId) {
        return ResponseResult.success(menuService.getMenuIds(roleId));
    }

    @UserLoginToken
    @RequestMapping(value = "/updateMenuName", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult updateMenuName(@RequestParam("menuId") String menuId, @RequestParam("menuName") String menuName) {
        return ResponseResult.success(menuService.updateMenuName(menuId, menuName));
    }

    @UserLoginToken
    @RequestMapping(value = "/updateMenuAndComponent", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult updateMenuAndComponent(@RequestBody SysMenuAndUrlModel model) {

        return ResponseResult.success(menuService.updateMenuAndComponent(model.getMenuId(), model.getMenuName(), model.getComponent()));
    }


}
