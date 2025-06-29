package com.example.vvpcommom.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配合表sys_dict_node使用
 * 列举电费账单类型
 * @author yym
 */

@Getter
@AllArgsConstructor
public enum ElectricityBillNodeEnum {
	storageEnergy(null, "储能", NodePostTypeEnum.storageEnergy.getNodePostType()),
	pv(null, "光伏", NodePostTypeEnum.pv.getNodePostType()),
	chargingPile("chongdianzhuang", "充电桩", NodePostTypeEnum.load.getNodePostType());


	private final String nodeTypeId;

	private final String nodeTypeName;

	private final String nodePostType;

}
