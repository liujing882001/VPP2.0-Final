package com.example.vvpservice.prouser.service;


import java.util.List;

public interface IUserService {


    /**
     * 得到用户权限下的节点列表
     *
     * @return
     */
    List<String> getAllowNodeIds();

    List<String> getAllowNodeIds(String userId);

    List<String> getRunAllowNodeIds();

    /**
     * 得到用户权限下光伏的节点列表
     *
     * @return
     */
    List<String> getAllowPvNodeIds();
    /**
     * 得到用户权限下运营中光伏的节点列表
     *
     * @return
     */
    List<String> getAllowRunPvNodeIds();

    /**
     * 得到用户权限下可调负荷的节点列表
     *
     * @return
     */
    List<String> getAllowLoadNodeIds();

    List<String> getAllowRunLoadNodeIds();

    /**
     * 得到可调负荷的节点列表
     *
     * @return
     */
    List<String> getAllLoadNodeIds();

    List<String> getAllChargingPileLoadNodeIds();
    List<String> getAllRunChargingPileLoadNodeIds();


    /**
     * 得到用户权限下储能的节点列表
     *
     * @return
     */
    List<String> getAllowStorageEnergyNodeIds();
    /**
     * 得到用户权限下运营中储能的节点列表
     *
     * @return
     */
    List<String> getAllowRunStorageEnergyNodeIds();


    /**
     * 用户是否是管理员
     *
     * @return
     */
    boolean isManger();


}
