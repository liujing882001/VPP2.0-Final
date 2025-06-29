package com.example.vvpservice.globalapi.service;

import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.globalapi.model.*;

import java.util.List;
import java.util.Map;

public interface GlobalApiService {

    //查看某节点的节点顶级节点
    StationNode findTopByStationId(String stationId);
    //根据父节点列表查子节点
    List<StationNode> findAllByParents(List<String> stationIds);
    //查用户权限节点列表
    List<String> useNodes();
    //查区域和节点类型下的项目节点
    CityATProNodesVO cityATypeProNodes();
    //根据用户权限查看节点树
    List<NodeDevCountTreeDTO> findTreeByRole();
    //查询某个节点及其储能下属
    List<String> findSubEnergyIdsByStationIds(String stationId);
    //查询某个节点及其它下属
    List<String> findSubLoadIdsByStationIds(String stationId);
    //查询某个节点及光伏下属
    List<String> findSubPvIdsByStationIds(String stationId);
    //查询某个节点及其下属的树关系
    List<StationNodeTreeDTO> findSubTreeByStationId(String stationId);
    //查询某些节点及其所有下属
    List<Object[]> findSubTreeByStationIds(List<String> stationIds);
    //查询某个节点的树关系
    List<StationNodeTreeDTO> findTreeByStationId(String stationId);

    //查看权限下全部运营中的项目节点名称
    List<String> getPermissionNodeNames();
    List<String> findAllStationId();

    //根据权限查全部项目节点列表
    List<String> listActSNForUser();
    //查运营中全部项目节点列表
    List<StationNode> listActiveSN();
    //全部节点树（不分类）并根据stationTypeId和stationState动态查询
    List<ListProjSubVo> listProjAndSub(String query,Map<String,Object> keyword);
    //全部节点树（分类储能、光伏、负荷）并根据stationTypeId和stationState动态查询
    List<ListProjSubCategoryVo> listProjAndSubCategory(String query,Map<String,Object> keyword);
    //根据节点id查询此节点上下级树（分类储能和光伏、负荷）并根据stationTypeId和stationState动态查询
    List<ListProjSubCategoryVo> listNodeProjAndSubCategory(String nodeId,Map<String,Object> keyword);
    //根据子节点id查询此节点上下级树（分类储能和光伏）并根据stationTypeId和stationState动态查询
    ListProjSubEnergyAndPvVo stationTreeEnergyAndPv(String nodeId);
    //根据项目ID查询下级树（分类储能和光伏），不查上级
    ListProjSubEnergyAndPvVo stationTreeEnergyAndPvNoM(String nodeId);



}
