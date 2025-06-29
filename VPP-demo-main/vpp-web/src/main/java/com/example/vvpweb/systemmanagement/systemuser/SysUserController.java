package com.example.vvpweb.systemmanagement.systemuser;

import com.example.vvpcommom.*;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.usernode.service.IPageableService;
import com.example.vvpweb.systemmanagement.systemuser.model.ResetPassword;
import com.example.vvpweb.systemmanagement.systemuser.model.SysUpdateUserModel;
import com.example.vvpweb.systemmanagement.systemuser.model.SysUserModel;
import com.example.vvpweb.systemmanagement.systemuser.model.SysUserShareRatioModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 系统用户
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/system_management/system_user/user")
@Api(value = "系统管理-系统用户", tags = {"系统管理-系统用户"})
public class SysUserController {

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserRoleRepository userRoleRepository;
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private NodeRepository nodeRepository;
    @Resource
    private UserNodeRepository userNodeRepository;
    @Resource
    private IPageableService userNodeService;

    @Resource
    private ScheduleStrategyRepository scheduleStrategyRepository;


    @ApiOperation("添加用户信息")
    @UserLoginToken
    @PreAuthorize("system:user:add")
    @RequestMapping(value = "addSysUser", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult addSysUser(@RequestBody SysUserModel sysUserModel) {

        try {
            if (sysUserModel == null) {
                return ResponseResult.error("参数异常，请检查！");
            }
            if (org.apache.commons.lang3.StringUtils.isEmpty(sysUserModel.getUserName())) {
                return ResponseResult.error("用户名不能为空！");
            }
            if (StringUtils.isEmpty(sysUserModel.getUserPassword())) {
                return ResponseResult.error("用户密码不能为空！");
            }
            if (StringUtils.isEmpty(sysUserModel.getRoleId())) {
                return ResponseResult.error("用户角色不能为空！");
            }

            // 检查手机号是否为 11 位
            if (sysUserModel.getPhone()!= null && sysUserModel.getPhone().length()!= 11) {
                return ResponseResult.error("请填写正确的手机号");
            }


            boolean isMatch = Pattern.matches(RegexUtils.patternName, sysUserModel.getUserName());
            if (!isMatch) {
                return ResponseResult.error("用户名称不符合 6～12个汉字、字母或数字、_ 要求，请修改！");
            }

            //roleId 位于sys_role表中，1表示系统管理员 2表示普通管理员  4432e0c3da9ae460899684223beb8a143 虚拟电厂运营商
            if (!roleRepository.existsById(sysUserModel.getRoleId())) {
                return ResponseResult.error("用户角色信息不存在！");
            }
            User systemUserByUserName = userRepository.findSystemUserByUserName(sysUserModel.getUserName());
            if (systemUserByUserName != null) {
                return ResponseResult.error("该用户名已经存在,请重新输入");
            }
            String userPassword = sysUserModel.getUserPassword();
            if (userPassword == null || "".equals(userPassword.trim())) {
                return ResponseResult.error("添加用户密码不能为空");
            }


            String userId = MD5Utils.generateMD5(sysUserModel.getUserName());
            User su = new User();

            su.setAddress(sysUserModel.getAddress());
            su.setBusiness(sysUserModel.getBusiness());
            su.setPhone(sysUserModel.getPhone());
            su.setContact(sysUserModel.getContact());
            su.setUserId(userId);
            su.setConfigType("N");
            su.setUserName(sysUserModel.getUserName());
            su.setUserEmail(sysUserModel.getUserEmail());
            su.setUserPassword(userPassword);
            su.setShareRatio(0);

            if ("1".equals(sysUserModel.getRoleId())) {
                sysUserModel.setRoleTypeName("系统管理员");
            } else if ("2".equals(sysUserModel.getRoleId())) {
                sysUserModel.setRoleTypeName("普通管理员");
            }else if ("3432e0c3da9ae460899684223beb8a143".equals(sysUserModel.getRoleId())) {
                sysUserModel.setRoleTypeName("电力用户");
            } else if ("4432e0c3da9ae460899684223beb8a143".equals(sysUserModel.getRoleId())) {
                sysUserModel.setRoleTypeName("虚拟电厂运营商");
            } else {
                sysUserModel.setRoleTypeName("其他");
            }
            su.setRoleTypeName(sysUserModel.getRoleTypeName());
            User save = userRepository.save(su);

            List<UserNode> userNodes = new ArrayList<>();

            List<String> nodeIds = sysUserModel.getNodeIds();
            if (nodeIds != null && !sysUserModel.getNodeIds().isEmpty()) {
                List<Node> allByNodeIdIn = nodeRepository.findAllByNodeIdIn(nodeIds);
                for (Node n : allByNodeIdIn) {
                    if ((n != null)) {
                        UserNode userNode = new UserNode();
                        userNode.setId(save.getUserId() + "_" + n.getNodeId());
                        userNode.setUserId(save.getUserId());
                        userNode.setNodeId(n.getNodeId());
                        userNodes.add(userNode);
                    }
                }
            }

            if (userNodes != null && userNodes.size() > 0) {
                userNodeRepository.saveAll(userNodes);
            }

            UserRole userRole = new UserRole();
            userRole.setId(save.getUserId() + "_" + sysUserModel.getRoleId());
            userRole.setRoleId(sysUserModel.getRoleId());
            userRole.setUserId(save.getUserId());
            userRoleRepository.save(userRole);


        } catch (Exception e) {
            return ResponseResult.error("该用户新增异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @UserLoginToken
    @RequestMapping(value = "checkSysUserName", method = {RequestMethod.POST})
    public ResponseResult checkSysUserName(@RequestParam("sysUserName") String sysUserName) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(sysUserName)) {
            return ResponseResult.error("用户名不能为空！");
        }
        User user = userRepository.findSystemUserByUserName(sysUserName);
        if (user != null) {
            return ResponseResult.error("该用户名已经存在,请重新输入");
        }
        return ResponseResult.success();
    }

    //与 菜单 权限标识对应
    @ApiOperation("删除用户信息")
    @UserLoginToken
    @PreAuthorize("system:user:remove")
    @RequestMapping(value = "deleteSysUser", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult deleteSysUser(@RequestParam("sysUserId") String sysUserId) {

        try {
            if (org.apache.commons.lang3.StringUtils.isEmpty(sysUserId)) {
                return ResponseResult.error("用户编号不能为空！");
            }
            String now_id = RequestHeaderContext.getInstance().getUserId();
            if (now_id.equals(sysUserId)) {
                return ResponseResult.error("用户无法删除自己的信息！");
            }
            User user = userRepository.findById(sysUserId).orElse(null);
            if (user != null) {
                if ("Y".equals(user.getConfigType())) {
                    return ResponseResult.error("系统内置用户不能被删除！");
                }
                List<ScheduleStrategy> allByUserId = scheduleStrategyRepository.findAllByUserId(user.getUserId());
                if (!allByUserId.isEmpty()) {
                    return ResponseResult.error("用户关联可调负荷运行策略，请先删除可调负荷运行策略！");
                }

                userNodeRepository.deleteByUserId(sysUserId);
                userRoleRepository.deleteByUserId(sysUserId);
                userRepository.deleteByUserId(sysUserId);
            }
        } catch (Exception e) {
            return ResponseResult.error("该用户删除异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }


    @ApiOperation("分页获取所有用户信息")
    @UserLoginToken
    @RequestMapping(value = "queryAllSysUserPageable", method = {RequestMethod.POST})
    public ResponseResult<PageModel> queryAllSysUserPageable(@RequestParam("number") int number,
                                                             @RequestParam("pageSize") int pageSize,
                                                             @RequestParam(value = "userName", required = false) String userName) {

        try {
            List<SysUserModel> sysUserModels = new ArrayList<>();

            Page<User> datas = userNodeService.getUserLikeUserName(userName, number, pageSize);

            List<User> sysUsers = datas.getContent();
            sysUsers.forEach(e -> {

                SysUserModel model = new SysUserModel();
                model.setUserId(e.getUserId());
                model.setUserName(e.getUserName());
                model.setUserEmail(e.getUserEmail());
                model.setConfigType(e.getConfigType());
                Role role = e.getRole();
                if (role != null) {
                    model.setRoleKey(e.getRole().getRoleKey());
                    model.setRoleId(role.getRoleId());
                    model.setRoleName(role.getRoleName());
                    model.setRoleTypeName(role.getRoleKeyDesc());
                }

                model.setAddress(e.getAddress());
                model.setPhone(e.getPhone());
                model.setBusiness(e.getBusiness());
                model.setContact(e.getContact());

                List<UserNode> allByUserId = userNodeRepository.findAllByUserId(e.getUserId());
                List<String> nodeIds = new ArrayList<>();
                allByUserId.stream().forEach(l -> nodeIds.add(l.getNodeId()));

                model.setNodeIds(nodeIds);

                model.setShareRatio(e.getShareRatio() + "%");

                sysUserModels.add(model);
            });

            PageModel pageModel = new PageModel();
            pageModel.setPageSize(pageSize);
            pageModel.setContent(sysUserModels);
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);

            return ResponseResult.success(pageModel);

        } catch (Exception e) {
            return ResponseResult.error("查询用户异常 +" + e.getMessage());
        }
    }

    @ApiOperation("编辑用户信息")
    @UserLoginToken
    @PreAuthorize("system:user:edit")
    @RequestMapping(value = "updateSysUserRole", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult updateSysUserRole(@RequestBody SysUpdateUserModel sysUserModel, HttpServletRequest request) {
        String authorizationCode = request.getHeader("authorizationCode");
        UserRole userRole = userRoleRepository.findByUserId(authorizationCode);
        if (userRole == null || (!userRole.getRoleId().equals("1") && !userRole.getRoleId().equals("2"))) {
            return ResponseResult.error("当前用户暂无权限！");
        }
        if (sysUserModel == null) {
            return ResponseResult.error("参数异常，请检查！");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(sysUserModel.getUserId())) {
            return ResponseResult.error("用户编号不能为空！");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(sysUserModel.getUserName())) {
            return ResponseResult.error("用户名不能为空！");
        }
        if (StringUtils.isEmpty(sysUserModel.getRoleId())) {
            return ResponseResult.error("用户角色不能为空！");
        }
        boolean isMatch = Pattern.matches(RegexUtils.patternName, sysUserModel.getUserName());
        if (!isMatch) {
            return ResponseResult.error("用户名称不符合 6～12个汉字、字母或数字、_ 要求，请修改！");
        }
        if (!roleRepository.existsById(sysUserModel.getRoleId())) {
            return ResponseResult.error("用户角色信息不存在！");
        }
        User su = userRepository.findById(sysUserModel.getUserId()).orElse(null);
        if (su == null) {
            return ResponseResult.error("要更新的用户不存在！");
        }
        User systemUserByUserName = userRepository.findSystemUserByUserName(sysUserModel.getUserName());

        if (systemUserByUserName != null
                && !sysUserModel.getUserId().equals(systemUserByUserName.getUserId())) {
            return ResponseResult.error("该用户名已经存在,请重新输入");
        }
        if ("Y".equals(su.getConfigType())) {
            return ResponseResult.error("系统内置用户不能被更新！");
        }

        userRoleRepository.deleteByUserId(sysUserModel.getUserId());
        UserRole ur = new UserRole();
        ur.setId(sysUserModel.getUserId() + "_" + sysUserModel.getRoleId());
        ur.setUserId(sysUserModel.getUserId());
        ur.setRoleId(sysUserModel.getRoleId());
        userRoleRepository.save(ur);


        List<UserNode> userNodes = new ArrayList<>();

        List<String> nodeIds = sysUserModel.getNodeIds();
        if (nodeIds != null && nodeIds.size() > 0) {
            List<Node> allByNodeIdIn = nodeRepository.findAllByNodeIdIn(nodeIds);
            for (Node n : allByNodeIdIn) {
                UserNode userNode = new UserNode();
                userNode.setId(sysUserModel.getUserId() + "_" + n.getNodeId());
                userNode.setUserId(sysUserModel.getUserId());
                userNode.setNodeId(n.getNodeId());
                userNodes.add(userNode);
            }
        }

        su.setAddress(sysUserModel.getAddress());
        su.setBusiness(sysUserModel.getBusiness());
        su.setPhone(sysUserModel.getPhone());
        su.setContact(sysUserModel.getContact());
        su.setConfigType("N");
        su.setUserName(sysUserModel.getUserName());
        su.setUserEmail(sysUserModel.getUserEmail());

        userNodeRepository.deleteByUserId(sysUserModel.getUserId());
        if (userNodes != null && userNodes.size() > 0) {
            userNodeRepository.saveAll(userNodes);
        }
        userRepository.save(su);

        return ResponseResult.success();
    }

    /**
     * 签约需求响应分成比例  例如分成80.6%，写入库为80.6
     */
    @ApiOperation("签约")
    @UserLoginToken
    @PreAuthorize("system:user:shareRatio")
    @RequestMapping(value = "updateSysUserShareRatio", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult updateSysUserShareRatio(@RequestBody SysUserShareRatioModel model) {
        User user = userRepository.findById(model.getUserId()).orElse(null);
        if (user != null) {
            user.setShareRatio(model.getShareRatio());
            userRepository.save(user);
        }
        return ResponseResult.success();
    }


    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @UserLoginToken
    @PreAuthorize("system:user:change_pwd")
    @RequestMapping(value = "resetPassword", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult resetPassword(@RequestBody ResetPassword model) {
        User user = userRepository.findById(model.getUserId()).orElse(null);
        if (user != null) {
            user.setUserPassword(model.getNewPassWord());
            userRepository.save(user);
        }
        return ResponseResult.success();
    }
}