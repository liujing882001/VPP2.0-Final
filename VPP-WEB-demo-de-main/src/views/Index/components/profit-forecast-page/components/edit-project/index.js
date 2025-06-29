import "./index.scss";
import { Chart } from "../chart";
import classNames from "classnames";
import { useState, useEffect } from "react";
import { Popover, Input, Select, Cascader } from "antd";
import http from "../../../../../../server/server";

export const EditProject = (props) => {
  const {
    advanceInfo,
    baseInfo,
    cityOptions,
    setUpdateAllInfo,
    updateAllInfo,
  } = props;
  const [collapsed1, setCollapsed1] = useState(false);
  const [collapsed2, setCollapsed2] = useState(false);
  const [isShowPopover, setIsShowPopover] = useState(true);
  const [open1, setOpen1] = useState(false);
  const [open2, setOpen2] = useState(false);
  const [open3, setOpen3] = useState(false);
  const [open4, setOpen4] = useState(false);
  const [open5, setOpen5] = useState(false);
  const [open6, setOpen6] = useState(false);
  const [open7, setOpen7] = useState(false);
  const [open8, setOpen8] = useState(false);
  const [open9, setOpen9] = useState(false);
  const [open10, setOpen10] = useState(false);
  const [open11, setOpen11] = useState(false);
  const [open12, setOpen12] = useState(false);
  const [open13, setOpen13] = useState(false);
  const [open14, setOpen14] = useState(false);
  const [open15, setOpen15] = useState(false);
  const [open16, setOpen16] = useState(false);
  const [open17, setOpen17] = useState(false);
  const [open18, setOpen18] = useState(false);
  const [open19, setOpen19] = useState(false);
  const [open20, setOpen20] = useState(false);
  const [open21, setOpen21] = useState(false);
  const [open22, setOpen22] = useState(false);

  const [showInput1, setShowInput1] = useState(false);
  const [showInput2, setShowInput2] = useState(false);
  const [showInput3, setShowInput3] = useState(false);
  const [showInput4, setShowInput4] = useState(false);
  const [showInput5, setShowInput5] = useState(false);
  const [showInput6, setShowInput6] = useState(false);
  const [showInput7, setShowInput7] = useState(false);
  const [showInput8, setShowInput8] = useState(false);
  const [showInput9, setShowInput9] = useState(false);
  const [showInput10, setShowInput10] = useState(false);
  const [showInput11, setShowInput11] = useState(false);
  const [showInput12, setShowInput12] = useState(false);
  const [showInput13, setShowInput13] = useState(false);
  const [showInput14, setShowInput14] = useState(false);
  const [showInput15, setShowInput15] = useState(false);
  const [showInput16, setShowInput16] = useState(false);
  const [showInput17, setShowInput17] = useState(false);
  const [showInput18, setShowInput18] = useState(false);
  const [showInput19, setShowInput19] = useState(false);
  const [showInput20, setShowInput20] = useState(false);

  const handleOpenChange1 = (newOpen) => {
    setOpen1(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };

  const handleOpenChange2 = (newOpen) => {
    setOpen2(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange3 = (newOpen) => {
    setOpen3(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange4 = (newOpen) => {
    setOpen4(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange5 = (newOpen) => {
    setOpen5(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange6 = (newOpen) => {
    setOpen6(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange7 = (newOpen) => {
    setOpen7(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange8 = (newOpen) => {
    setOpen8(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange9 = (newOpen) => {
    setOpen9(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange10 = (newOpen) => {
    setOpen10(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange11 = (newOpen) => {
    setOpen11(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange12 = (newOpen) => {
    setOpen12(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange13 = (newOpen) => {
    setOpen13(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange14 = (newOpen) => {
    setOpen14(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange15 = (newOpen) => {
    setOpen15(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange16 = (newOpen) => {
    setOpen16(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange17 = (newOpen) => {
    setOpen17(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange18 = (newOpen) => {
    setOpen18(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange19 = (newOpen) => {
    setOpen19(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const handleOpenChange20 = (newOpen) => {
    setOpen20(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };

  const handleOpenChange21 = (newOpen) => {
    setOpen21(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };

  const handleOpenChange22 = (newOpen) => {
    setOpen22(newOpen);
    if (!newOpen) {
      setIsShowPopover(false);
    }
  };
  const [cyclesNumber, setCyclesNumber] = useState(
    advanceInfo?.designParameter.filter(
      (item) => item.paramName === "循环次数"
    )[0]?.defaultValue
  );
  const [lifespanYears, setLifespanYears] = useState(
    advanceInfo?.designParameter.filter(
      (item) => item.paramName === "寿命年限"
    )[0]?.defaultValue
  );
  const [availableDepth, setAvailableDepth] = useState(
    advanceInfo?.designParameter.filter(
      (item) => item.paramName === "可用深度"
    )[0]?.defaultValue
  );
  const [batteryAttenuationCoefficient, setBatteryAttenuationCoefficient] =
    useState(
      advanceInfo?.designParameter.filter(
        (item) => item.paramName === "电池衰减系数"
      )[0]?.defaultValue
    );
  const [systemEfficiency, setSystemEfficiency] = useState(
    advanceInfo?.designParameter.filter(
      (item) => item.paramName === "系统效率"
    )[0]?.defaultValue
  );

  const [evaluationPeriod, setEvaluationPeriod] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "评估年限"
    )[0]?.defaultValue
  );
  const [maintenanceRatio5years, setMaintenanceRatio5years] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "前5年维保比例"
    )[0]?.defaultValue
  );
  const [customerSharingRatio, setCustomerSharingRatio] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "电力用户分成比例"
    )[0]?.defaultValue
  );
  const [maintenanceRatio5_10years, setMaintenanceRatio5_10years] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "5-10年维保比例"
    )[0]?.defaultValue
  );
  const [purchasePrice, setPurchasePrice] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "采购价格"
    )[0]?.defaultValue
  );
  const [maintenanceRatio10_25years, setMaintenanceRatio10_25years] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "10-25年维保比例"
    )[0]?.defaultValue
  );
  const [proportionOfEquipmentCost, setProportionOfEquipmentCost] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "设备费用占比"
    )[0]?.defaultValue
  );
  const [insurancePremiumRatio, setInsurancePremiumRatio] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "保险费比例"
    )[0]?.defaultValue
  );
  const [proportionEngineeringCost, setProportionEngineeringCost] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "工程费用占比"
    )[0]?.defaultValue
  );
  const [operatingFeeRatio, setOperatingFeeRatio] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "运营费比例"
    )[0]?.defaultValue
  );
  const [batteryReplacementRatio, setBatteryReplacementRatio] = useState(
    advanceInfo?.economicalParameter.filter(
      (item) => item.paramName === "更换电池比例"
    )[0]?.defaultValue
  );
  const [selectCity, setSelectCity] = useState(null);
  const [projectName, setProjectName] = useState(baseInfo?.projectName);
  const [area, setArea] = useState(baseInfo.area);
  const [electricType, setElectricType] = useState(
    `${baseInfo.type1}_${baseInfo.type2}`
  );
  const [voltageLevel, setVoltageLevel] = useState(baseInfo.vol1);
  const [volume, setVolume] = useState(baseInfo.designCapacity);
  const [power, setPower] = useState(baseInfo.designPower);
  const [typeOptions, setTypeOptions] = useState([]);
  const [selectValue, setSelectValue] = useState([]);
  const [voltageLevelOptions, setVoltageLevelOptions] = useState([]);
  const [type1, settType1] = useState("");
  const [type2, settType2] = useState("");
  const [status, setStatus] = useState(false);
  // 递归函数
  const transformData = (data) => {
    return data.map((item) => {
      // 将当前项的 value 设置为 1
      const newItem = {
        label: item.value,
        value: item.value, // 将 value 改为 1
      };

      // 如果有 children，则递归处理
      if (item.children && item.children.length > 0) {
        newItem.children = transformData(item.children);
      }

      return newItem;
    });
  };

  const init = async () => {
    const res2 = await http.get(
      `/revenueEst/queryElectricityType?city=${baseInfo.area}`
    );
    const option1 = res2.data.data;
    const list = transformData(option1);
    setTypeOptions(list);
  };

  const init1 = async () => {
    const res2 = await http.get(
      `/revenueEst/queryElectricityType?city=${baseInfo.area}&type1=${baseInfo.type1}&type2=${baseInfo.type2}`
    );
    const option1 = res2.data.data;
    const list = transformData(option1);
    setVoltageLevelOptions(list);
  };

  const refresh = async (type1, type2) => {
    const res2 = await http.get(
      `/revenueEst/queryElectricityType?city=${baseInfo.area}&type1=${type1}&type2=${type2}`
    );
    const option1 = res2.data.data;
    const list = transformData(option1);
    setVoltageLevelOptions(list);
  };

  useEffect(() => {
    init();
    init1();
  }, []);

  useEffect(() => {
    setUpdateAllInfo({
      projectName: projectName,
      area: selectCity ? selectCity : baseInfo.area,
      type1: type1 ? type1 : baseInfo.type1,
      type2: type2 ? type2 : baseInfo.type2,
      vol1: voltageLevel,
      designCapacity: volume,
      designPower: power,
      cycleCount: cyclesNumber,
      lifespan: lifespanYears,
      usableDepth: availableDepth,
      systemEfficiency: systemEfficiency,
      batteryDegCoeff: batteryAttenuationCoefficient,
      assessPeriod: evaluationPeriod,
      mainTRatio5Y: maintenanceRatio5years,
      electricityUserShare: customerSharingRatio,
      mainTRatio5_10Y: maintenanceRatio5_10years,
      purchasePrice: purchasePrice,
      mainTRatio10_25Y: maintenanceRatio10_25years,
      equipmentCostRatio: proportionOfEquipmentCost,
      insuranceRate: insurancePremiumRatio,
      engineeringCostRatio: proportionEngineeringCost,
      platformRate: operatingFeeRatio,
      batteryReplRatio: batteryReplacementRatio,
    });
  }, [
    projectName,
    area,
    electricType,
    voltageLevel,
    volume,
    power,
    selectCity,
    cyclesNumber,
    lifespanYears,
    availableDepth,
    systemEfficiency,
    batteryAttenuationCoefficient,
    evaluationPeriod,
    maintenanceRatio5years,
    customerSharingRatio,
    maintenanceRatio5_10years,
    purchasePrice,
    maintenanceRatio10_25years,
    proportionOfEquipmentCost,
    insurancePremiumRatio,
    proportionEngineeringCost,
    operatingFeeRatio,
    batteryReplacementRatio,
    type1,
    type2,
  ]);

  return (
    <div className="edit-project-wrap">
      <i className="robot-icon" />
      <div className="edit-project-first" style={{ marginLeft: "70px" }}>
        <div className="edit-project-header">
          <span>好的，已展示项目最新信息，您可修改信息内容或直接生成报告</span>
        </div>
        <div className="edit-project-content">
          <div className="base-info">
            <div className="base-info-title">基本信息</div>
            <div className="base-info-content">
              <div className="base-info-content-item">
                <span className="base-info-content-item-title">项目名称</span>
                {isShowPopover ? (
                  <Popover
                    content={
                      <>
                        <div className="popover-title">
                          你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                        </div>
                        <span
                          className="popover-ok-btn"
                          onClick={() => {
                            setOpen1(false);
                            setIsShowPopover(false);
                          }}
                        >
                          我知道了
                        </span>
                      </>
                    }
                    trigger="hover"
                    open={open1}
                    onOpenChange={handleOpenChange1}
                    placement="bottomRight"
                    overlayClassName="customer-popover-item"
                  >
                    <Input
                      variant="filled"
                      defaultValue={projectName}
                      value={projectName}
                      onChange={(e) => setProjectName(e.target.value)}
                      className="variant-input"
                    />
                  </Popover>
                ) : (
                  <Input
                    variant="filled"
                    defaultValue={projectName}
                    value={projectName}
                    onChange={(e) => setProjectName(e.target.value)}
                    className="variant-input"
                  />
                )}
              </div>
              <div className="base-info-content-item">
                <span className="base-info-content-item-title">所在地区</span>
                {isShowPopover ? (
                  <Popover
                    content={
                      <>
                        <div className="popover-title">
                          你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                        </div>
                        <span
                          className="popover-ok-btn"
                          onClick={() => {
                            setOpen2(false);
                            setIsShowPopover(false);
                          }}
                        >
                          我知道了
                        </span>
                      </>
                    }
                    trigger="hover"
                    open={open2}
                    onOpenChange={handleOpenChange2}
                    placement="bottomRight"
                    overlayClassName="customer-popover-item"
                  >
                    {showInput1 ? (
                      <Select
                        className="base-info-select"
                        options={cityOptions}
                        defaultValue={selectCity ? selectCity : baseInfo.area}
                        onChange={(value) => {
                          setArea(area);
                          setSelectCity(value);
                        }}
                        onBlur={() => setShowInput1(false)}
                      />
                    ) : (
                      <span
                        className="content"
                        onClick={() => setShowInput1(true)}
                      >
                        {selectCity ? selectCity : baseInfo.area}
                      </span>
                    )}
                  </Popover>
                ) : (
                  <>
                    {showInput1 ? (
                      <Select
                        className="base-info-select"
                        options={cityOptions}
                        defaultValue={selectCity ? selectCity : baseInfo.area}
                        onChange={(value) => {
                          setArea(area);
                          setSelectCity(value);
                        }}
                        onBlur={() => setShowInput1(false)}
                      />
                    ) : (
                      <span
                        className="content"
                        onClick={() => setShowInput1(true)}
                      >
                        {selectCity ? selectCity : baseInfo.area}
                      </span>
                    )}
                  </>
                )}
              </div>
              <div className="base-info-content-item">
                <span className="base-info-content-item-title">用电类型</span>
                {isShowPopover ? (
                  <Popover
                    content={
                      <>
                        <div className="popover-title">
                          你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                        </div>
                        <span
                          className="popover-ok-btn"
                          onClick={() => {
                            setOpen3(false);
                            setIsShowPopover(false);
                          }}
                        >
                          我知道了
                        </span>
                      </>
                    }
                    trigger="hover"
                    open={open3}
                    onOpenChange={handleOpenChange3}
                    placement="bottomRight"
                    overlayClassName="customer-popover-item"
                  >
                    {showInput19 ? (
                      <Cascader
                        className="base-info-select"
                        options={typeOptions}
                        defaultValue={baseInfo.type2}
                        value={electricType}
                        onChange={(value) => {
                          setElectricType(`${value[0]}_${value[1]}`);
                          settType1(value[0]);
                          settType2(value[1]);
                          refresh(value[0], value[1]);
                          setTimeout(() => {
                            setShowInput19(false);
                          }, 200);
                        }}
                        onBlur={() => setShowInput19(false)}
                      />
                    ) : (
                      <span
                        className="content"
                        onClick={() => setShowInput19(true)}
                      >
                        {electricType
                          ? electricType
                          : `${baseInfo.type1}_${baseInfo.type2}`}
                      </span>
                    )}
                  </Popover>
                ) : (
                  <>
                    {showInput19 ? (
                      <Cascader
                        className="base-info-select"
                        options={typeOptions}
                        defaultValue={baseInfo.type2}
                        value={electricType}
                        onChange={(value) => {
                          setElectricType(`${value[0]}_${value[1]}`);
                          settType1(value[0]);
                          settType2(value[1]);
                          refresh(value[0], value[1]);
                          setTimeout(() => {
                            setShowInput19(false);
                          }, 200);
                        }}
                        onBlur={() => setShowInput19(false)}
                      />
                    ) : (
                      <span
                        className="content"
                        onClick={() => setShowInput19(true)}
                      >
                        {electricType
                          ? electricType
                          : `${baseInfo.type1}_${baseInfo.type2}`}
                      </span>
                    )}
                  </>
                )}
              </div>
              <div className="base-info-content-item">
                <span className="base-info-content-item-title">电压等级</span>
                {isShowPopover ? (
                  <Popover
                    content={
                      <>
                        <div className="popover-title">
                          你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                        </div>
                        <span
                          className="popover-ok-btn"
                          onClick={() => {
                            setOpen4(false);
                            setIsShowPopover(false);
                          }}
                        >
                          我知道了
                        </span>
                      </>
                    }
                    trigger="hover"
                    open={open4}
                    onOpenChange={handleOpenChange4}
                    placement="bottomRight"
                    overlayClassName="customer-popover-item"
                  >
                    {showInput20 ? (
                      <Cascader
                        className="base-info-select"
                        options={voltageLevelOptions}
                        defaultValue={baseInfo.vol1}
                        value={voltageLevel}
                        onChange={(value) => {
                          setVoltageLevel(value[0]);
                          setTimeout(() => {
                            setShowInput20(false);
                          }, 200);
                        }}
                        onBlur={() => setShowInput20(false)}
                      />
                    ) : (
                      <span
                        className="content"
                        onClick={() => setShowInput20(true)}
                      >
                        {voltageLevel ? voltageLevel : baseInfo.vol1}
                      </span>
                    )}
                  </Popover>
                ) : (
                  <>
                    {showInput20 ? (
                      <Cascader
                        className="base-info-select"
                        options={voltageLevelOptions}
                        defaultValue={baseInfo.vol1}
                        value={voltageLevel}
                        onChange={(value) => {
                          setVoltageLevel(value[0]);
                          setTimeout(() => {
                            setShowInput20(false);
                          }, 200);
                        }}
                        onBlur={() => setShowInput20(false)}
                      />
                    ) : (
                      <span
                        className="content"
                        onClick={() => setShowInput20(true)}
                      >
                        {voltageLevel ? voltageLevel : baseInfo.vol1}
                      </span>
                    )}
                  </>
                )}
              </div>
              <div className="base-info-content-item">
                <span className="base-info-content-item-title">储能容量</span>
                {isShowPopover ? (
                  <Popover
                    content={
                      <>
                        <div className="popover-title">
                          你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                        </div>
                        <span
                          className="popover-ok-btn"
                          onClick={() => {
                            setOpen5(false);
                            setIsShowPopover(false);
                          }}
                        >
                          我知道了
                        </span>
                      </>
                    }
                    trigger="hover"
                    open={open5}
                    onOpenChange={handleOpenChange5}
                    openClassName="customer-open-popover"
                    placement="bottomRight"
                    overlayClassName="customer-popover-item"
                    className="customer-default-popover"
                  >
                    <>
                      {showInput2 ? (
                        <Input
                          className="base-info-input"
                          type="tel"
                          autoFocus
                          addonAfter="kWh"
                          defaultValue={volume}
                          value={volume}
                          onChange={(e) =>
                            setVolume(e.target.value.replace(/[^\-?\d.]/g, ""))
                          }
                          onBlur={() => setShowInput2(false)}
                        />
                      ) : (
                        <span
                          className="content"
                          onClick={() => setShowInput2(true)}
                        >
                          {volume}kWh
                        </span>
                      )}
                    </>
                  </Popover>
                ) : (
                  <>
                    {showInput2 ? (
                      <Input
                        className="base-info-input"
                        type="tel"
                        autoFocus
                        addonAfter="kWh"
                        defaultValue={volume}
                        value={volume}
                        onChange={(e) =>
                          setVolume(e.target.value.replace(/[^\-?\d.]/g, ""))
                        }
                        onBlur={() => setShowInput2(false)}
                      />
                    ) : (
                      <span
                        className="content"
                        onClick={() => setShowInput2(true)}
                      >
                        {volume}kWh
                      </span>
                    )}
                  </>
                )}
              </div>
              <div className="base-info-content-item">
                <span className="base-info-content-item-title">储能功率</span>
                {isShowPopover ? (
                  <Popover
                    content={
                      <>
                        <div className="popover-title">
                          你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                        </div>
                        <span
                          className="popover-ok-btn"
                          onClick={() => {
                            setOpen6(false);
                            setIsShowPopover(false);
                          }}
                        >
                          我知道了
                        </span>
                      </>
                    }
                    trigger="hover"
                    open={open6}
                    onOpenChange={handleOpenChange6}
                    openClassName="customer-open-popover"
                    placement="bottomRight"
                    overlayClassName="customer-popover-item"
                    className="customer-default-popover"
                  >
                    <>
                      {showInput3 ? (
                        <Input
                          className="base-info-input"
                          type="tel"
                          autoFocus
                          addonAfter="kW"
                          defaultValue={power}
                          value={power}
                          onChange={(e) =>
                            setPower(e.target.value.replace(/[^\-?\d.]/g, ""))
                          }
                          onBlur={() => setShowInput3(false)}
                        />
                      ) : (
                        <span
                          className="content"
                          onClick={() => setShowInput3(true)}
                        >
                          {power}kW
                        </span>
                      )}
                    </>
                  </Popover>
                ) : (
                  <>
                    {showInput3 ? (
                      <Input
                        className="base-info-input"
                        type="tel"
                        autoFocus
                        addonAfter="kW"
                        defaultValue={power}
                        value={power}
                        onChange={(e) =>
                          setPower(e.target.value.replace(/[^\-?\d.]/g, ""))
                        }
                        onBlur={() => setShowInput3(false)}
                      />
                    ) : (
                      <span
                        className="content"
                        onClick={() => setShowInput3(true)}
                      >
                        {power}kW
                      </span>
                    )}
                  </>
                )}
              </div>
              <div className="base-info-content-item">
                <span className="base-info-content-item-title">负荷数据</span>
                <Chart loadData={baseInfo?.loadData} {...props} />
              </div>
            </div>
          </div>
          <div className="senior-setting">
            <p className="senior-setting-title">高级配置</p>
            <div className="design-param">
              <div
                className="design-param-header"
                onClick={() => setCollapsed1(!collapsed1)}
              >
                设计参数
                <i
                  className={classNames("up-arrow", {
                    "down-arrow": collapsed1,
                  })}
                />
              </div>
              <div
                className={classNames("design-param-content-1", {
                  collapsed: collapsed1,
                  "no-collapsed": !collapsed1,
                })}
              >
                <div className="design-param-item">
                  <span className="design-param-item-left">循环次数</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen7(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open7}
                      onOpenChange={handleOpenChange7}
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                    >
                      <Input
                        variant="filled"
                        type="tel"
                        defaultValue={cyclesNumber}
                        value={cyclesNumber}
                        onChange={(e) => setCyclesNumber(e.target.value)}
                        className="design-variant-input"
                      />
                    </Popover>
                  ) : (
                    <Input
                      variant="filled"
                      type="tel"
                      defaultValue={cyclesNumber}
                      value={cyclesNumber}
                      onChange={(e) =>
                        setCyclesNumber(
                          e.target.value.replace(/[^\-?\d.]/g, "")
                        )
                      }
                      className="design-variant-input"
                    />
                  )}
                </div>
                <div className="design-param-item">
                  <span className="design-param-item-left">寿命年限</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen8(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open8}
                      onOpenChange={handleOpenChange8}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput4 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="年"
                            defaultValue={lifespanYears}
                            value={lifespanYears}
                            onChange={(e) =>
                              setLifespanYears(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput4(false)}
                          />
                        ) : (
                          <span
                            className="design-param-item-right"
                            onClick={() => setShowInput4(true)}
                          >
                            {lifespanYears}年
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput4 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="年"
                          defaultValue={lifespanYears}
                          value={lifespanYears}
                          onChange={(e) =>
                            setLifespanYears(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput4(false)}
                        />
                      ) : (
                        <span
                          className="design-param-item-right"
                          onClick={() => setShowInput4(true)}
                        >
                          {lifespanYears}年
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="design-param-item">
                  <span className="design-param-item-left">可用深度</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen9(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open9}
                      onOpenChange={handleOpenChange9}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput5 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="%"
                            defaultValue={availableDepth}
                            value={availableDepth}
                            onChange={(e) =>
                              setAvailableDepth(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput5(false)}
                          />
                        ) : (
                          <span
                            className="design-param-item-right"
                            onClick={() => setShowInput5(true)}
                          >
                            {availableDepth}%
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput5 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="%"
                          defaultValue={availableDepth}
                          value={availableDepth}
                          onChange={(e) =>
                            setAvailableDepth(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput5(false)}
                        />
                      ) : (
                        <span
                          className="design-param-item-right"
                          onClick={() => setShowInput5(true)}
                        >
                          {availableDepth}%
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="design-param-item">
                  <span className="design-param-item-left">系统效率</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen10(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open10}
                      onOpenChange={handleOpenChange10}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput6 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="%"
                            defaultValue={systemEfficiency}
                            value={systemEfficiency}
                            onChange={(e) =>
                              setSystemEfficiency(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput6(false)}
                          />
                        ) : (
                          <span
                            className="design-param-item-right"
                            onClick={() => setShowInput6(true)}
                          >
                            {systemEfficiency}%
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput6 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="%"
                          defaultValue={systemEfficiency}
                          value={systemEfficiency}
                          onChange={(e) =>
                            setSystemEfficiency(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput6(false)}
                        />
                      ) : (
                        <span
                          className="design-param-item-right"
                          onClick={() => setShowInput6(true)}
                        >
                          {systemEfficiency}%
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="design-param-item">
                  <span className="design-param-item-left">电池衰减系数</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen11(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open11}
                      onOpenChange={handleOpenChange11}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput7 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="%"
                            defaultValue={batteryAttenuationCoefficient}
                            value={batteryAttenuationCoefficient}
                            onChange={(e) =>
                              setBatteryAttenuationCoefficient(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput7(false)}
                          />
                        ) : (
                          <span
                            className="design-param-item-right"
                            onClick={() => setShowInput7(true)}
                          >
                            {batteryAttenuationCoefficient}%
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput7 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="%"
                          defaultValue={batteryAttenuationCoefficient}
                          value={batteryAttenuationCoefficient}
                          onChange={(e) =>
                            setBatteryAttenuationCoefficient(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput7(false)}
                        />
                      ) : (
                        <span
                          className="design-param-item-right"
                          onClick={() => setShowInput7(true)}
                        >
                          {batteryAttenuationCoefficient}%
                        </span>
                      )}
                    </>
                  )}
                </div>
              </div>
            </div>
            <div className="economic-param">
              <div
                className="economic-param-header"
                onClick={() => setCollapsed2(!collapsed2)}
              >
                经济参数
                <i
                  className={classNames("up-arrow", {
                    "down-arrow": collapsed2,
                  })}
                />
              </div>
              <div
                className={classNames("economic-param-content", {
                  collapsed: collapsed2,
                  "no-collapsed": !collapsed2,
                })}
              >
                <div className="economic-param-item">
                  <span className="economic-param-item-left">评估年限</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen12(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open12}
                      onOpenChange={handleOpenChange12}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput8 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="年"
                            defaultValue={evaluationPeriod}
                            value={evaluationPeriod}
                            onChange={(e) =>
                              setEvaluationPeriod(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput8(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput8(true)}
                          >
                            {evaluationPeriod}年
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput8 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="年"
                          defaultValue={evaluationPeriod}
                          value={evaluationPeriod}
                          onChange={(e) =>
                            setEvaluationPeriod(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput8(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput8(true)}
                        >
                          {evaluationPeriod}年
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">
                    前5年维保费用
                  </span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen13(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open13}
                      onOpenChange={handleOpenChange13}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput9 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="元/Wh"
                            defaultValue={maintenanceRatio5years}
                            value={maintenanceRatio5years}
                            onChange={(e) =>
                              setMaintenanceRatio5years(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput9(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput9(true)}
                          >
                            {maintenanceRatio5years}元/Wh
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput9 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="元/Wh"
                          defaultValue={maintenanceRatio5years}
                          value={maintenanceRatio5years}
                          onChange={(e) =>
                            setMaintenanceRatio5years(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput9(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput9(true)}
                        >
                          {maintenanceRatio5years}元/Wh
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">
                    电力用户分成比例
                  </span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen14(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open14}
                      onOpenChange={handleOpenChange14}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput10 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="%"
                            defaultValue={customerSharingRatio}
                            value={customerSharingRatio}
                            onChange={(e) =>
                              setCustomerSharingRatio(e.target.value)
                            }
                            onBlur={() => setShowInput10(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput10(true)}
                          >
                            {customerSharingRatio}%
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput10 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="%"
                          defaultValue={customerSharingRatio}
                          value={customerSharingRatio}
                          onChange={(e) =>
                            setCustomerSharingRatio(e.target.value)
                          }
                          onBlur={() => setShowInput10(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput10(true)}
                        >
                          {customerSharingRatio}%
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">
                    5-10年维保费用
                  </span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen15(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open15}
                      onOpenChange={handleOpenChange15}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput11 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="元/Wh"
                            defaultValue={maintenanceRatio5_10years}
                            value={maintenanceRatio5_10years}
                            onChange={(e) =>
                              setMaintenanceRatio5_10years(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput11(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput11(true)}
                          >
                            {maintenanceRatio5_10years}元/Wh
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput11 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="元/Wh"
                          defaultValue={maintenanceRatio5_10years}
                          value={maintenanceRatio5_10years}
                          onChange={(e) =>
                            setMaintenanceRatio5_10years(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput11(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput11(true)}
                        >
                          {maintenanceRatio5_10years}元/Wh
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">采购价格</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen16(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open16}
                      onOpenChange={handleOpenChange16}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput12 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="元/Wh"
                            defaultValue={purchasePrice}
                            value={purchasePrice}
                            onChange={(e) =>
                              setPurchasePrice(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput12(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput12(true)}
                          >
                            {purchasePrice}元/Wh
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput12 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="元/Wh"
                          defaultValue={purchasePrice}
                          value={purchasePrice}
                          onChange={(e) =>
                            setPurchasePrice(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput12(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput12(true)}
                        >
                          {purchasePrice}元/Wh
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">
                    10-25年维保费用
                  </span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen17(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open17}
                      onOpenChange={handleOpenChange17}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput13 ? (
                          <Input
                            className="design-input"
                            autoFocus
                            type="tel"
                            addonAfter="元/Wh"
                            defaultValue={maintenanceRatio10_25years}
                            value={maintenanceRatio10_25years}
                            onChange={(e) =>
                              setMaintenanceRatio10_25years(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput13(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput13(true)}
                          >
                            {maintenanceRatio10_25years}元/Wh
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput13 ? (
                        <Input
                          className="design-input"
                          autoFocus
                          type="tel"
                          addonAfter="元/Wh"
                          defaultValue={maintenanceRatio10_25years}
                          value={maintenanceRatio10_25years}
                          onChange={(e) =>
                            setMaintenanceRatio10_25years(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput13(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput13(true)}
                        >
                          {maintenanceRatio10_25years}元/Wh
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">设备费用占比</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen18(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open18}
                      onOpenChange={handleOpenChange18}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput14 ? (
                          <Input
                            className="design-input"
                            autoFocus
                            type="tel"
                            addonAfter="%"
                            defaultValue={proportionOfEquipmentCost}
                            value={proportionOfEquipmentCost}
                            onChange={(e) =>
                              setProportionOfEquipmentCost(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput14(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput14(true)}
                          >
                            {proportionOfEquipmentCost}%
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput14 ? (
                        <Input
                          className="design-input"
                          autoFocus
                          type="tel"
                          addonAfter="%"
                          defaultValue={proportionOfEquipmentCost}
                          value={proportionOfEquipmentCost}
                          onChange={(e) =>
                            setProportionOfEquipmentCost(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput14(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput14(true)}
                        >
                          {proportionOfEquipmentCost}%
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">保险费比例</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen19(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open19}
                      onOpenChange={handleOpenChange19}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput15 ? (
                          <Input
                            className="design-input"
                            autoFocus
                            type="tel"
                            addonAfter="%"
                            defaultValue={insurancePremiumRatio}
                            value={insurancePremiumRatio}
                            onChange={(e) =>
                              setInsurancePremiumRatio(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput15(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput15(true)}
                          >
                            {insurancePremiumRatio}%
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput15 ? (
                        <Input
                          className="design-input"
                          autoFocus
                          type="tel"
                          addonAfter="%"
                          defaultValue={insurancePremiumRatio}
                          value={insurancePremiumRatio}
                          onChange={(e) =>
                            setInsurancePremiumRatio(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput15(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput15(true)}
                        >
                          {insurancePremiumRatio}%
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">工程费用占比</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen20(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open20}
                      onOpenChange={handleOpenChange20}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput16 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="%"
                            defaultValue={proportionEngineeringCost}
                            value={proportionEngineeringCost}
                            onChange={(e) =>
                              setProportionEngineeringCost(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput16(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput16(true)}
                          >
                            {proportionEngineeringCost}%
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput16 ? (
                        <Input
                          className="design-input"
                          type="tel"
                          autoFocus
                          addonAfter="%"
                          defaultValue={proportionEngineeringCost}
                          value={proportionEngineeringCost}
                          onChange={(e) =>
                            setProportionEngineeringCost(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput16(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput16(true)}
                        >
                          {proportionEngineeringCost}%
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">运营费比例</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen21(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open21}
                      onOpenChange={handleOpenChange21}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput17 ? (
                          <Input
                            className="design-input"
                            autoFocus
                            type="tel"
                            addonAfter="%"
                            defaultValue={operatingFeeRatio}
                            value={operatingFeeRatio}
                            onChange={(e) =>
                              setOperatingFeeRatio(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput17(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput17(true)}
                          >
                            {operatingFeeRatio}%
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput17 ? (
                        <Input
                          className="design-input"
                          autoFocus
                          type="tel"
                          addonAfter="%"
                          defaultValue={operatingFeeRatio}
                          value={operatingFeeRatio}
                          onChange={(e) =>
                            setOperatingFeeRatio(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput17(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput17(true)}
                        >
                          {operatingFeeRatio}%
                        </span>
                      )}
                    </>
                  )}
                </div>
                <div className="economic-param-item">
                  <span className="economic-param-item-left">更换电池比例</span>
                  {isShowPopover ? (
                    <Popover
                      content={
                        <>
                          <div className="popover-title">
                            你可通过悬浮点击对信息内容进行编辑，编辑成功即为保存成功
                          </div>
                          <span
                            className="popover-ok-btn"
                            onClick={() => {
                              setOpen22(false);
                              setIsShowPopover(false);
                            }}
                          >
                            我知道了
                          </span>
                        </>
                      }
                      trigger="hover"
                      open={open22}
                      onOpenChange={handleOpenChange22}
                      openClassName="customer-open-popover"
                      placement="bottomRight"
                      overlayClassName="customer-popover-item"
                      className="customer-default-popover"
                    >
                      <>
                        {showInput18 ? (
                          <Input
                            className="design-input"
                            type="tel"
                            autoFocus
                            addonAfter="%"
                            defaultValue={batteryReplacementRatio}
                            value={batteryReplacementRatio}
                            onChange={(e) =>
                              setBatteryReplacementRatio(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              )
                            }
                            onBlur={() => setShowInput18(false)}
                          />
                        ) : (
                          <span
                            className="economic-param-item-right"
                            onClick={() => setShowInput18(true)}
                          >
                            {batteryReplacementRatio}%
                          </span>
                        )}
                      </>
                    </Popover>
                  ) : (
                    <>
                      {showInput18 ? (
                        <Input
                          className="design-input"
                          autoFocus
                          addonAfter="%"
                          type="tel"
                          defaultValue={batteryReplacementRatio}
                          value={batteryReplacementRatio}
                          onChange={(e) =>
                            setBatteryReplacementRatio(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            )
                          }
                          onBlur={() => setShowInput18(false)}
                        />
                      ) : (
                        <span
                          className="economic-param-item-right"
                          onClick={() => setShowInput18(true)}
                        >
                          {batteryReplacementRatio}%
                        </span>
                      )}
                    </>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
