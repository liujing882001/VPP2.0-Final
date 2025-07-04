package com.example.vvpcommom.Enum;

/**
 * 配合表sys_dict_node使用
 * 列举电费账单类型
 * @author yym
 */
public enum ElectricityBillNodeEnum {
	storageEnergy(null, "储能", "storageEnergy"),
	pv(null, "光伏", "pv"),
	chargingPile("chongdianzhuang", "充电桩", "load");

	private final String nodeTypeId;
	private final String nodeTypeName;
	private final String nodePostType;

	ElectricityBillNodeEnum(String nodeTypeId, String nodeTypeName, String nodePostType) {
		this.nodeTypeId = nodeTypeId;
		this.nodeTypeName = nodeTypeName;
		this.nodePostType = nodePostType;
	}

	public String getNodeTypeId() {
		return nodeTypeId;
	}

	public String getNodeTypeName() {
		return nodeTypeName;
	}

	public String getNodePostType() {
		return nodePostType;
	}
}
