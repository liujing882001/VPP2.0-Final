import "./index.scss";
import { useEffect, useState } from "react";
import { UploadOutlined } from "@ant-design/icons";
import { Chart } from "../chart";
import {
  Popover,
  Input,
  Select,
  Cascader,
  Upload,
  message,
  Button,
} from "antd";
import http from "../../../../../../server/server";
import classNames from "classnames";
import { RequestUrl } from "../../../../../../server/server";
export const LookProject = (props) => {
  const {
    baseInfo,
    cityOptions,
    setUpdateBaseInfo,
    radioValue,
    selectOption,
    setNoDemandFile,
    advanceInfo,
    demandArr,
    setCapacityRangeUp,
    setCapacityRangeLow,
    setCountingPeriod,
    setIrrMin,
    capacityRangeUp,
    countingPeriod,
    capacityRangeLow,
    irrMin,
  } = props;
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
  const [blur1, setBlur1] = useState(false);
  const [blur2, setBlur2] = useState(false);
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
  const [autoFocus, setAutoFocus] = useState(true);
  const [capacityRangeUp1, setCapacityRangeUp1] = useState(capacityRangeUp);
  const [countingPeriod1, setCountingPeriod1] = useState(countingPeriod);
  const [irrMin1, setIrrMin1] = useState(irrMin);
  const [capacityRangeLow1, setCapacityRangeLow1] = useState(capacityRangeLow);

  const [selectCity, setSelectCity] = useState(null);
  const [isShowPopover, setIsShowPopover] = useState(true);
  const [collapsed1, setCollapsed1] = useState(false);

  const [projectName, setProjectName] = useState(baseInfo?.projectName);
  const [area, setArea] = useState(baseInfo.area);
  const [electricType, setElectricType] = useState(
    `${baseInfo.type1}_${baseInfo.type2}`
  );
  const [voltageLevel, setVoltageLevel] = useState(
    baseInfo.vol2 ? `${baseInfo.vol1}_${baseInfo.vol2}` : baseInfo.vol1
  );
  const [volume, setVolume] = useState(baseInfo.designCapacity);
  const [power, setPower] = useState(baseInfo.designPower);
  const [typeOptions, setTypeOptions] = useState([]);
  const [selectValue, setSelectValue] = useState([]);
  const [voltageLevelOptions, setVoltageLevelOptions] = useState([]);
  const [type1, settType1] = useState("");
  const [type2, settType2] = useState("");
  const [priceType, setPriceType] = useState(baseInfo.priceType);
  const [demandDataFileList, setDemandDataFileList] = useState([]);
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

  useEffect(() => {
    if (baseInfo.demandFileName) {
      setDemandDataFileList([
        {
          uid: baseInfo.demandFileName,
          name: baseInfo.demandFileName,
          status: "done",
          url: "",
        },
      ]);
      setNoDemandFile(false);
    }
  }, [baseInfo]);

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
      `/revenueEst/queryElectricityType?city=${selectCity}&type1=${type1}&type2=${type2}`
    );
    const option1 = res2.data.data;
    const list = transformData(option1);
    const va11 = list[0].value;
    const va12 = list[0]?.children ? list[0]?.children[0]?.value : "";
    if (va12) {
      setVoltageLevel(`${va11}_${va12}`);
    } else {
      setVoltageLevel(`${va11}`);
    }
    setVoltageLevelOptions(list);
  };

  const initVoltageLevel = async (city, type1, type2) => {
    const res2 = await http.get(
      `/revenueEst/queryElectricityType?city=${city}&type1=${type1}&type2=${type2}`
    );
    const option1 = res2.data.data;
    const list = transformData(option1);
    const va11 = list[0].value;
    const va12 = list[0]?.children ? list[0]?.children[0]?.value : "";
    if (va12) {
      setVoltageLevel(`${va11}_${va12}`);
    } else {
      setVoltageLevel(`${va11}`);
    }
    setVoltageLevelOptions(list);
  };

  const initType = async (city) => {
    const res2 = await http.get(
      `/revenueEst/queryElectricityType?city=${city}`
    );
    const option1 = res2.data.data;
    const list = transformData(option1);
    const va11 = list[0].value;
    const va12 = list[0]?.children[0]?.value;
    if (va12) {
      setElectricType(`${va11}_${va12}`);
    } else {
      setElectricType(`${va11}`);
    }
    initVoltageLevel(city, va11, va12);
    setTypeOptions(list);
  };

  useEffect(() => {
    init();
    init1();
  }, []);

  useEffect(() => {
    setUpdateBaseInfo({
      projectName: projectName,
      area: selectCity ? selectCity : baseInfo.area,
      type1: type1 ? type1 : baseInfo.type1,
      type2: type2 ? type2 : baseInfo.type2,
      vol1: voltageLevel,
      designCapacity: volume,
      designPower: power,
    });
  }, [
    projectName,
    area,
    electricType,
    voltageLevel,
    volume,
    power,
    selectCity,
    type1,
    type2,
  ]);

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

  const demandDataProps = {
    name: "file",
    data: {
      projectId: baseInfo?.projectId,
    },
    action: RequestUrl + "revenueEstGuo/uploadDemandExcel",
    multiple: true,
    onChange(info) {
      if (info.file.status === "done") {
        message.success(`${info.file.name} 上传成功`);
      } else if (info.file.status === "error") {
        message.error(`${info.file.name} 上传失败`);
      }
      let newFileList = [...info.fileList];
      newFileList = newFileList?.slice(-1);
      newFileList = newFileList?.map((file) => {
        if (file.response) {
          file.uid = file?.response.msg;
          file.url = "";
        }
        return file;
      });
      if (info?.file?.response) {
        // setCapacityRangeLow(info?.file?.response?.data?.capacityRangeLow);
        // setCapacityRangeLow1(info?.file?.response?.data?.capacityRangeLow);
        // setCapacityRangeUp(info?.file?.response?.data?.capacityRangeUp);
        // setCapacityRangeUp1(info?.file?.response?.data?.capacityRangeUp);
        // setIrrMin1(info?.file?.response?.data?.irrMin);
        // setIrrMin(info?.file?.response?.data?.irrMin);
        // setCountingPeriod(info?.file?.response?.data?.countingPeriod);
        // setCountingPeriod1(info?.file?.response?.data?.countingPeriod);
      }
      if (newFileList.length) {
        setNoDemandFile(false);
      } else {
        setNoDemandFile(true);
      }
      setDemandDataFileList(newFileList);
    },
  };

  const isNull = (value) => {
    return typeof value === "object" && value === null;
  };

  return (
    <div className="look-project-wrap">
      <i className="robot-icon" />
      <div className="look-project-first" style={{ marginLeft: "70px" }}>
        <div className="look-project-header">
          <span>
            {radioValue === 1
              ? "好的，已生成新项目信息，您可修改信息内或直接生成报告"
              : "好的，已展示项目最新信息，您可修改信息内容或直接生成报告"}
          </span>
        </div>
        {radioValue !== 1 &&
        selectOption === "容量评估" &&
        demandArr?.length ? (
          <div className="end-table">
            <div className="end-table-header">
              <div className="end-table-header-item">容量</div>
              <div className="end-table-header-item">全投资IRR（税前）</div>
              <div className="end-table-header-item">
                全投资收益总额（税前）
              </div>
              <div className="end-table-header-item">年循环次数</div>
            </div>
            <div className="end-table-content">
              {demandArr.map((item) => {
                return (
                  <div className="end-table-content-line">
                    <div className="end-table-content-item">
                      {item?.capacity}
                    </div>
                    <div className="end-table-content-item">{item?.irr}</div>
                    <div className="end-table-content-item">
                      {item?.totalRevenue}
                    </div>
                    <div className="end-table-content-item">
                      {item?.yearCycle}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        ) : null}

        <div className="look-project-content">
          <div className="base-info">
            <div className="base-info-title">基本信息</div>
            <div className="base-info-content">
              {selectOption === "容量评估" ? (
                <>
                  <div className="base-info-content-item">
                    <span className="base-info-content-item-title">
                      项目名称
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
                          onChange={(e) => {
                            setProjectName(e.target.value);
                          }}
                          className="variant-input"
                        />
                      </Popover>
                    ) : (
                      <Input
                        variant="filled"
                        defaultValue={projectName}
                        value={projectName}
                        onChange={(e) => {
                          setProjectName(e.target.value);
                        }}
                        className="variant-input"
                      />
                    )}
                  </div>
                  <div className="base-info-content-item">
                    <span className="base-info-content-item-title">
                      是否突破需量
                    </span>
                    <span className="base-info-content-item-content">否</span>
                    {/* {isShowPopover ? (
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
                        {showInput4 ? (
                          <Select
                            className="base-info-select"
                            options={[
                              { value: 1, label: "是" },
                              { value: 0, label: "否" },
                            ]}
                            defaultValue={1}
                            onBlur={() => setShowInput4(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput4(true)}
                          >
                            是
                          </span>
                        )}
                      </Popover>
                    ) : (
                      <>
                        {showInput4 ? (
                          <Select
                            className="base-info-select"
                            options={[
                              { value: 1, label: "是" },
                              { value: 0, label: "否" },
                            ]}
                            onChange={(value) => {}}
                            onBlur={() => setShowInput4(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput4(true)}
                          >
                          </span>
                        )}
                      </>
                    )} */}
                  </div>
                  <div className="base-info-content-item">
                    <span className="base-info-content-item-title">
                      所在地区
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
                            defaultValue={
                              selectCity ? selectCity : baseInfo.area
                            }
                            onChange={(value) => {
                              setArea(area);
                              setSelectCity(value);
                              initType(value);
                              setTimeout(() => {
                                setShowInput1(false);
                              }, 200);
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
                            defaultValue={
                              selectCity ? selectCity : baseInfo.area
                            }
                            onChange={(value) => {
                              setArea(area);
                              setSelectCity(value);
                              initType(value);
                              setTimeout(() => {
                                setShowInput1(false);
                              }, 200);
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
                    <></>
                  </div>
                  <div className="base-info-content-item">
                    <span className="base-info-content-item-title">
                      基础电费类型
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
                        {showInput2 ? (
                          <Select
                            className="base-info-select"
                            options={[
                              { value: "按容收费", label: "按容收费" },
                              { value: "按需收费", label: "按需收费" },
                            ]}
                            value={priceType}
                            onChange={(value) => {
                              setPriceType(value);
                            }}
                            onBlur={() => setShowInput2(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput2(true)}
                          >
                            {priceType}
                          </span>
                        )}
                      </Popover>
                    ) : (
                      <>
                        {showInput2 ? (
                          <Select
                            className="base-info-select"
                            options={[
                              { value: "按容收费", label: "按容收费" },
                              { value: "按需收费", label: "按需收费" },
                            ]}
                            value={priceType}
                            onChange={(value) => {
                              setPriceType(value);
                            }}
                            onBlur={() => setShowInput2(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput2(true)}
                          >
                            {priceType}
                          </span>
                        )}
                      </>
                    )}
                  </div>
                  <div className="base-info-content-item">
                    <span className="base-info-content-item-title">
                      用电类型
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
                        {showInput3 ? (
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
                                setShowInput3(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput3(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput3(true)}
                          >
                            {electricType
                              ? electricType
                              : `${baseInfo.type1}_${baseInfo.type2}`}
                          </span>
                        )}
                      </Popover>
                    ) : (
                      <>
                        {showInput3 ? (
                          <Cascader
                            className="base-info-select"
                            options={typeOptions}
                            defaultValue={baseInfo.type2}
                            value={electricType}
                            onChange={(value) => {
                              setElectricType(`${value[0]}_${value[1]}`);
                              refresh(value[0], value[1]);
                              settType1(value[0]);
                              settType2(value[1]);
                              setTimeout(() => {
                                setShowInput3(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput3(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput3(true)}
                          >
                            {electricType
                              ? electricType
                              : `${baseInfo.type1}_${baseInfo.type2}`}
                          </span>
                        )}
                      </>
                    )}
                  </div>
                  {priceType === "按需收费" ? (
                    <div className="base-info-content-item">
                      <span className="base-info-content-item-title">
                        需量数据
                      </span>
                      <Upload
                        accept=".xlsx"
                        {...demandDataProps}
                        fileList={demandDataFileList}
                      >
                        <Button
                          icon={<UploadOutlined />}
                          className="upload-btn"
                        >
                          上传需量数据
                        </Button>
                        {demandDataFileList.length === 0 ? (
                          <div className={"tips"}>目前仅支持xlsx文件</div>
                        ) : null}
                      </Upload>
                    </div>
                  ) : null}

                  <div className="base-info-content-item">
                    <span
                      className="base-info-content-item-title"
                      style={
                        priceType === "按容收费"
                          ? { marginRight: "50px" }
                          : { marginRight: "24px" }
                      }
                    >
                      电压等级
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
                        placement="bottomRight"
                        overlayClassName="customer-popover-item"
                      >
                        {showInput4 ? (
                          <Cascader
                            className="base-info-select"
                            options={voltageLevelOptions}
                            defaultValue={baseInfo.vol1}
                            value={voltageLevel}
                            onChange={(value) => {
                              if (value[1]) {
                                setVoltageLevel(`${value[0]}_${value[1]}`);
                              } else {
                                setVoltageLevel(value[0]);
                              }
                              setTimeout(() => {
                                setShowInput4(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput4(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput4(true)}
                          >
                            {voltageLevel}
                          </span>
                        )}
                      </Popover>
                    ) : (
                      <>
                        {showInput4 ? (
                          <Cascader
                            className="base-info-select"
                            options={voltageLevelOptions}
                            defaultValue={baseInfo.vol1}
                            value={voltageLevel}
                            onChange={(value) => {
                              if (value[1]) {
                                setVoltageLevel(`${value[0]}_${value[1]}`);
                              } else {
                                setVoltageLevel(value[0]);
                              }
                              setTimeout(() => {
                                setShowInput4(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput4(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput4(true)}
                          >
                            {voltageLevel}
                          </span>
                        )}
                      </>
                    )}
                  </div>
                </>
              ) : (
                <>
                  <div className="base-info-content-item">
                    <span className="base-info-content-item-title">
                      项目名称
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
                          onChange={(e) => {
                            setProjectName(e.target.value);
                          }}
                          className="variant-input"
                        />
                      </Popover>
                    ) : (
                      <Input
                        variant="filled"
                        defaultValue={projectName}
                        value={projectName}
                        onChange={(e) => {
                          setProjectName(e.target.value);
                        }}
                        className="variant-input"
                      />
                    )}
                  </div>
                  <div className="base-info-content-item">
                    <span className="base-info-content-item-title">
                      所在地区
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
                        {showInput4 ? (
                          <Select
                            className="base-info-select"
                            options={cityOptions}
                            defaultValue={
                              selectCity ? selectCity : baseInfo.area
                            }
                            onChange={(value) => {
                              setArea(area);
                              setSelectCity(value);
                              initType(value);
                              setTimeout(() => {
                                setShowInput4(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput4(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput4(true)}
                          >
                            {selectCity ? selectCity : baseInfo.area}
                          </span>
                        )}
                      </Popover>
                    ) : (
                      <>
                        {showInput4 ? (
                          <Select
                            className="base-info-select"
                            options={cityOptions}
                            defaultValue={
                              selectCity ? selectCity : baseInfo.area
                            }
                            onChange={(value) => {
                              setArea(area);
                              setSelectCity(value);
                              initType(value);

                              setTimeout(() => {
                                setShowInput4(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput4(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput4(true)}
                          >
                            {selectCity ? selectCity : baseInfo.area}
                          </span>
                        )}
                      </>
                    )}
                    <></>
                  </div>
                  <div className="base-info-content-item">
                    <span className="base-info-content-item-title">
                      用电类型
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
                        {showInput5 ? (
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
                                setShowInput5(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput5(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput5(true)}
                          >
                            {electricType
                              ? electricType
                              : `${baseInfo.type1}_${baseInfo.type2}`}
                          </span>
                        )}
                      </Popover>
                    ) : (
                      <>
                        {showInput5 ? (
                          <Cascader
                            className="base-info-select"
                            options={typeOptions}
                            defaultValue={baseInfo.type2}
                            value={electricType}
                            onChange={(value) => {
                              setElectricType(`${value[0]}_${value[1]}`);
                              refresh(value[0], value[1]);
                              settType1(value[0]);
                              settType2(value[1]);
                              setTimeout(() => {
                                setShowInput5(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput5(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput5(true)}
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
                    <span className="base-info-content-item-title">
                      电压等级
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
                        {showInput6 ? (
                          <Cascader
                            className="base-info-select"
                            options={voltageLevelOptions}
                            defaultValue={baseInfo.vol1}
                            value={voltageLevel}
                            onChange={(value) => {
                              if (value[1]) {
                                setVoltageLevel(`${value[0]}_${value[1]}`);
                              } else {
                                setVoltageLevel(value[0]);
                              }
                              setTimeout(() => {
                                setShowInput6(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput6(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput6(true)}
                          >
                            {voltageLevel ? voltageLevel : baseInfo.vol1}
                          </span>
                        )}
                      </Popover>
                    ) : (
                      <>
                        {showInput6 ? (
                          <Cascader
                            className="base-info-select"
                            options={voltageLevelOptions}
                            defaultValue={baseInfo.vol1}
                            value={voltageLevel}
                            onChange={(value) => {
                              if (value[1]) {
                                setVoltageLevel(`${value[0]}_${value[1]}`);
                              } else {
                                setVoltageLevel(value[0]);
                              }
                              setTimeout(() => {
                                setShowInput6(false);
                              }, 200);
                            }}
                            onBlur={() => setShowInput6(false)}
                          />
                        ) : (
                          <span
                            className="content"
                            onClick={() => setShowInput6(true)}
                          >
                            {voltageLevel ? voltageLevel : baseInfo.vol1}
                          </span>
                        )}
                      </>
                    )}
                  </div>
                  <div className="base-info-content-item">
                    <span className="base-info-content-item-title">
                      储能容量
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
                              autoFocus
                              addonAfter="kWh"
                              defaultValue={volume}
                              value={volume}
                              onChange={(e) => {
                                setVolume(
                                  e.target.value.replace(/[^\-?\d.]/g, "")
                                );
                              }}
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
                            autoFocus
                            addonAfter="kWh"
                            defaultValue={volume}
                            value={volume}
                            onChange={(e) => {
                              setVolume(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              );
                            }}
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
                    <span className="base-info-content-item-title">
                      储能功率
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
                              autoFocus
                              addonAfter="kW"
                              defaultValue={power}
                              value={power}
                              onChange={(e) => {
                                setPower(
                                  e.target.value.replace(/[^\-?\d.]/g, "")
                                );
                              }}
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
                            autoFocus
                            addonAfter="kW"
                            defaultValue={power}
                            value={power}
                            onChange={(e) => {
                              setPower(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              );
                            }}
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
                </>
              )}
              <div className="base-info-content-item">
                <span className="base-info-content-item-title">负荷数据</span>
                <Chart
                  loadData={baseInfo?.loadData}
                  baseInfo={baseInfo}
                  {...props}
                />
              </div>
            </div>
          </div>

          {selectOption === "容量评估" ? (
            <div className="look-design-param ">
              <div
                className="design-param-header not"
                onClick={() => setCollapsed1(!collapsed1)}
              >
                不突破需量-计算范围
                <i
                  className={classNames("up-arrow", {
                    "down-arrow": collapsed1,
                  })}
                />
              </div>
              <div
                className={classNames("design-param-content", {
                  collapsed: collapsed1,
                  "no-collapsed": !collapsed1,
                })}
              >
                {!isNull(capacityRangeUp) && !isNull(capacityRangeLow) ? (
                  <div className="design-param-item">
                    <span className="design-param-item-left-1">
                      容量范围(kW)
                    </span>
                    <span className="design-param-item-right-1">
                      {showInput7 ? (
                        <>
                          <Input
                            className="base-info-input-1"
                            defaultValue={capacityRangeLow1}
                            value={capacityRangeLow1}
                            autoFocus={autoFocus}
                            onChange={(e) => {
                              setCapacityRangeLow1(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              );
                              setCapacityRangeLow(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              );
                            }}
                            onBlur={() => {
                              setShowInput7(false);
                              setAutoFocus(!autoFocus);
                            }}
                          />
                          -
                          <Input
                            className="base-info-input-2"
                            defaultValue={capacityRangeUp1}
                            autoFocus={!autoFocus}
                            value={capacityRangeUp1}
                            onChange={(e) => {
                              setCapacityRangeUp(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              );
                              setCapacityRangeUp1(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              );
                            }}
                            onBlur={() => {
                              setShowInput7(false);
                              setAutoFocus(!autoFocus);
                            }}
                          />
                        </>
                      ) : (
                        <div
                          className="content"
                          onClick={() => {
                            setShowInput7(true);
                            setBlur1(false);
                            setBlur2(false);
                          }}
                        >
                          {capacityRangeLow1} - {capacityRangeUp1}
                        </div>
                      )}
                    </span>
                  </div>
                ) : (
                  <div className="design-param-item">
                    <span className="design-param-item-left-1">
                      容量范围(kW)
                    </span>
                    <span className="design-param-item-right-1">-</span>
                  </div>
                )}
                {!isNull(irrMin) ? (
                  <div className="design-param-item">
                    <span className="design-param-item-left-1">
                      IRR最小值(%)
                    </span>
                    <span className="design-param-item-right-1">
                      {showInput8 ? (
                        <Input
                          className="base-info-input-3"
                          autoFocus={true}
                          defaultValue={irrMin1}
                          value={irrMin1}
                          onChange={(e) => {
                            setIrrMin(e.target.value.replace(/[^\-?\d.]/g, ""));
                            setIrrMin1(
                              e.target.value.replace(/[^\-?\d.]/g, "")
                            );
                          }}
                          onBlur={() => setShowInput8(false)}
                        />
                      ) : (
                        <div
                          className="content"
                          onClick={() => {
                            setShowInput8(true);
                          }}
                        >
                          {irrMin1}
                        </div>
                      )}
                    </span>
                  </div>
                ) : (
                  <div className="design-param-item">
                    <span className="design-param-item-left-1">
                      IRR最小值(%)
                    </span>
                    <span className="design-param-item-right-1">-</span>
                  </div>
                )}

                {!isNull(countingPeriod) ? (
                  <div className="design-param-item">
                    <span className="design-param-item-left-1">
                      计算间隔(kW)
                    </span>
                    <span className="design-param-item-right-1">
                      {showInput9 ? (
                        <>
                          <Input
                            className="base-info-input-3"
                            defaultValue={countingPeriod1}
                            value={countingPeriod1}
                            autoFocus={true}
                            onChange={(e) => {
                              setCountingPeriod(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              );
                              setCountingPeriod1(
                                e.target.value.replace(/[^\-?\d.]/g, "")
                              );
                            }}
                            onBlur={() => setShowInput9(false)}
                          />
                        </>
                      ) : (
                        <div
                          className="content"
                          onClick={() => {
                            setShowInput9(true);
                          }}
                        >
                          {countingPeriod1}
                        </div>
                      )}
                    </span>
                  </div>
                ) : (
                  <div className="design-param-item">
                    <span className="design-param-item-left-1">
                      计算间隔(kW)
                    </span>
                    <span className="design-param-item-right-1">-</span>
                  </div>
                )}
              </div>
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
};
