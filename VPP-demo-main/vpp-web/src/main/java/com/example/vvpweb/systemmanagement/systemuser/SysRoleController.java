package com.example.vvpweb.systemmanagement.systemuser;


import com.example.vvpcommom.MD5Utils;
import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.RoleRepository;
import com.example.vvpdomain.UserRoleRepository;
import com.example.vvpdomain.entity.Role;
import com.example.vvpdomain.entity.UserRole;
import com.example.vvpservice.role.service.IRoleService;
import com.example.vvpservice.usernode.service.IPageableService;
import com.example.vvpweb.systemmanagement.systemuser.model.*;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/system_management/system_user/role")
@Api(value = "系统管理-系统用户-角色", tags = {"系统管理-系统用户-角色"})
public class SysRoleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysRoleController.class);


    @Autowired
    private IRoleService roleService;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private IPageableService pageableService;

    @UserLoginToken
    @RequestMapping(value = "/allRoleTypes", method = {RequestMethod.POST})
    public ResponseResult<List<SysRoleTypeResponse>> allRoleTypes() {
        try {
            List<Role> roles = roleService.allUserRoles();
            List<SysRoleTypeResponse> sysRoles = new ArrayList<>();
            if (roles != null && roles.size() > 0) {
                //根据roleKey去重
                roles.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(s -> s.getRoleKey()))), ArrayList::new))
                        .forEach(e -> {
                            if (e.getConfigType().equals("Y")) {
                                SysRoleTypeResponse sr = new SysRoleTypeResponse();
                                sr.setRoleKey(e.getRoleKey());
                                sr.setRoleKeyDesc(e.getRoleKeyDesc());
                                sysRoles.add(sr);
                            }
                        });
            }
            return ResponseResult.success(sysRoles);
        } catch (Exception e) {
            return ResponseResult.error("获取所有的角色类型失败," + e.getMessage());
        }

    }

    @UserLoginToken
    @RequestMapping(value = "/addRole", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult addRole(@RequestBody SysRole sysRole) {
        try {
            if (sysRole == null || StringUtils.isEmpty(sysRole.getRoleName())) {
                return ResponseResult.error("添加角色参数有误,请重新输入！");
            }
            if (roleService.CheckNameIsExists(sysRole.getRoleName())) {
                return ResponseResult.error("添加角色失败，该角色名称已存在!");
            }
            Role role = new Role();
            role.setRoleId(MD5Utils.generateMD5(sysRole.getRoleName()));
            role.setRoleName(sysRole.getRoleName());
            role.setRoleKey(5);
            role.setRoleKeyDesc("普通用户");
            role.setConfigType("N");
            role.setRoleLabel(sysRole.getRoleLabel());
            roleService.addRole(role);
            return ResponseResult.success();

        } catch (Exception e) {
            return ResponseResult.error("添加角色异常," + e.getMessage());
        }
    }

    @UserLoginToken
    @RequestMapping(value = "/updateRoleName", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult updateRole(@RequestParam("roleId") String roleId,
                                                  @RequestParam("roleName") String newRoleName,
                                                  @RequestParam("roleLabel") String roleLabel) {
        try {
            if (StringUtils.isEmpty(newRoleName)|| StringUtils.isEmpty(roleId)) {
                return ResponseResult.error("更新角色失败，参数信息不能为空");
            }
            Role oldRole = roleRepository.findByRoleName(newRoleName);
            if (oldRole!=null && oldRole.getRoleId().equals(roleId)==false) {
                return ResponseResult.error("更新角色失败，该角色名称已存在");
            }
            roleService.updateRoleName(roleId, newRoleName, roleLabel);

            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.error("更新角色名称异常," + e.getMessage());
        }
    }

    @UserLoginToken
    @RequestMapping(value = "/deleteRoleById", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult deleteRoleById(@RequestParam("roleId") String roleId) {
        try {
            List<UserRole> allByRoleId = userRoleRepository.findAllByRoleId(roleId);
            if (allByRoleId != null && !allByRoleId.isEmpty()) {
                return ResponseResult.error("删除角色失败，该角色被用户关联!");
            }
            roleService.deleteRole(roleId);
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.error("deleteRoleById," + e.getMessage());
        }
    }


    @UserLoginToken
    @RequestMapping(value = "/updateRoleMenuIds", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult updateRoleMenuIds(@RequestBody RoleId2MenuIds rm) {
        try {
            roleService.updateRoleInfoMenu(rm.getRoleId(), rm.getMenuIds());
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.error("updateRoleMenuIds," + e.getMessage());
        }

    }

    @UserLoginToken
    @RequestMapping(value = "/sysUserRoles", method = {RequestMethod.POST})
    public ResponseResult<List<SysRoleResponse>> sysUserRoles() {
        try {
            List<SysRoleResponse> sysRoles = new ArrayList<>();
            List<Role> roles = roleService.sysUserRoles();
            if (roles != null && roles.size() > 0) {
                roles.stream().forEach(e -> {
                    SysRoleResponse sr = new SysRoleResponse();
                    sr.setRoleId(e.getRoleId());
                    sr.setRoleName(e.getRoleName());
                    sysRoles.add(sr);
                });
            }
            return ResponseResult.success(sysRoles);
        } catch (Exception e) {
            return ResponseResult.error("sysUserRoles," + e.getMessage());
        }

    }

    @UserLoginToken
    @RequestMapping(value = "/allUserRoles", method = {RequestMethod.POST})
    public ResponseResult<List<SysRoleResponse>> allUserRoles() {
        try {
            List<Role> roles = roleService.allUserRoles();
            List<SysRoleResponse> sysRoles = new ArrayList<>();
            if (roles != null && roles.size() > 0) {
                roles.forEach(e -> {
                    SysRoleResponse sr = new SysRoleResponse();
                    sr.setRoleId(e.getRoleId());
                    sr.setRoleName(e.getRoleName());
                    sr.setRoleKey(e.getRoleKey());
                    sysRoles.add(sr);
                });
            }
            return ResponseResult.success(sysRoles);
        } catch (Exception e) {
            return ResponseResult.error("allUserRoles," + e.getMessage());
        }

    }


    @UserLoginToken
    @RequestMapping(value = "/allUserRolesWithoutAdmin", method = {RequestMethod.POST})
    public ResponseResult<List<SysRoleResponse>> allUserRolesWithoutAdmin() {
        try {
            List<Role> roles = roleService.allUserRoles();
            List<SysRoleResponse> sysRoles = new ArrayList<>();
            if (roles != null && roles.size() > 0) {
                roles.forEach(e -> {
                    if (e.getRoleKey() != 1) {
                        SysRoleResponse sr = new SysRoleResponse();
                        sr.setRoleId(e.getRoleId());
                        sr.setRoleName(e.getRoleName());
                        sr.setRoleKey(e.getRoleKey());
                        sysRoles.add(sr);
                    }
                });
            }
            return ResponseResult.success(sysRoles);
        } catch (Exception e) {
            return ResponseResult.error("allUserRoles," + e.getMessage());
        }

    }


    @UserLoginToken
    @RequestMapping(value = "/allRoleListByNamePageable", method = {RequestMethod.POST})
    public ResponseResult<PageModel> allRoleListByNamePageable(@RequestParam("number") int number,
                                                               @RequestParam("pageSize") int pageSize,
                                                               @RequestParam(value = "roleName", required = false) String roleName) {
        try {
            Page<Role> roleLikeRoleName = pageableService.getRoleLikeRoleName(roleName, number, pageSize);
            List<SysRoleListResponse> list = new ArrayList<>();

            AtomicInteger num = new AtomicInteger(1);
            roleLikeRoleName.getContent().forEach(r -> {
                SysRoleListResponse sr = new SysRoleListResponse();
                sr.setOrderNum(num.incrementAndGet() + "");
                sr.setRoleId(r.getRoleId());
                sr.setRoleName(r.getRoleName());
                sr.setConfigType(r.getConfigType());
                sr.setRoleKey(r.getRoleKey());
                sr.setRoleKeyDesc(r.getRoleKeyDesc());
                sr.setRoleLabel(r.getRoleLabel());
                sr.setCreatedTime(r.getCreatedTime());
                list.add(sr);
            });

            PageModel pageModel = new PageModel();
            pageModel.setPageSize(pageSize);
            pageModel.setContent(list);
            pageModel.setTotalPages(roleLikeRoleName.getTotalPages());
            pageModel.setTotalElements((int) roleLikeRoleName.getTotalElements());
            pageModel.setNumber(roleLikeRoleName.getNumber() + 1);


            return ResponseResult.success(pageModel);
        } catch (Exception e) {
            return ResponseResult.error("allRoleListByName," + e.getMessage());
        }
    }
}
