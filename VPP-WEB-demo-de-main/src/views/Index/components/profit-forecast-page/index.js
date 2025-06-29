import "./index.scss";
import { Select, Tooltip, message, Input, Cascader, Progress } from "antd";
import React, { useEffect, useRef, useState } from "react";
import { Dom1 } from "../dom1";
import { Dom2 } from "../dom2";
import { Dom3 } from "../dom3";
import { formatDate, renderResponseStatus, calcEndDate } from "../../utils";
import { flushSync } from "react-dom";
import { LookProject } from "./components/look-project";
import { RecommendStrategy } from "./components/recommend-strategy";
import Typed from "typed.js";
import http from "../../../../server/server";
import { helper, use } from "echarts";
import { EditProject } from "./components/edit-project";
import { GenerateReport } from "./components/generate-report";
import { InitWrap } from "./components/init-wrap";
import { SelectWrap } from "./components/select-wrap";
import axios from "axios";
import { NoFormStyle } from "antd/es/form/context";

export const ProfitForecastPage = (props) => {
  let childRef = useRef(null);
  const { setIsProfitPage } = props;
  const [status, setStatus] = useState("init");
  const [scrollList, setScrollList] = useState([]);
  const [isOneCanCLick, setIsOneCanCLick] = useState(false);
  const [isTwoCanCLick, setIsTwoCanCLick] = useState(false);
  const [isThreeCanCLick, setIsThreeCanCLick] = useState(false);
  const [radioValue, setRadioValue] = useState(null);
  const [cityOptions, setCityOptions] = useState([]);
  const [selectedCity, setSelectedCity] = useState("");
  const fileInputRef = useRef(null);
  const [fileList, setFileList] = useState([]);
  const [fileId, setFileId] = useState("");
  const [projectName, setProjectName] = useState("");
  const [volume, setVolume] = useState("");
  const [power, setPower] = useState("");
  const [baseInfo, setBaseInfo] = useState({});
  const [advanceInfo, setAdvanceInfo] = useState({});
  const [rightFileList, setRightFileList] = useState([]);
  const [projectOption, setProjectOption] = useState([]);
  const [selectProject, setSelectProject] = useState({});
  const selectOption = useRef("");
  const [isLoading, setIsLoading] = useState(false);
  const [updateBaseInfo, setUpdateBaseInfo] = useState({});
  const [typeOptions, setTypeOptions] = useState([]);
  const [electricType, setElectricType] = useState("");
  const [voltageLevelOptions, setVoltageLevelOptions] = useState([]);
  const [voltageLevel, setVoltageLevel] = useState("");
  const [noDemandFile, setNoDemandFile] = useState(true);
  const [capacityRangeLow, setCapacityRangeLow] = useState();
  const [countingPeriod, setCountingPeriod] = useState();
  const [irrMin, setIrrMin] = useState();
  const [capacityRangeUp, setCapacityRangeUp] = useState();
  const message1 = useRef(null);
  let typed1 = "";
  const message2 = useRef(null);
  let typed2 = "";
  const message3 = useRef(null);
  const message5 = useRef(null);
  const message6 = useRef(null);
  let typed3 = "";
  let typed5 = "";
  let typed6 = "";
  const [updateAllInfo, setUpdateAllInfo] = useState({});
  const timerId = useRef(null);
  const onSend1 = (name, id, radioValue1) => {
    setSelectProject({
      id,
      name,
    });
    setProjectName(name);
    setScrollList((prestate) => {
      return [
        ...prestate,
        {
          index: 1,
          dom: (
            <div className="right-default-wrap">
              <div className="right-message">
                <span>
                  我想要查看 {name} 项目的
                  {selectOption.current === "容量评估" ? "容量" : "收益测算"}
                  评估
                </span>
              </div>
            </div>
          ),
        },
      ];
    });

    look1(id, radioValue1);
    setStatus("look");
  };

  const look1 = async (id, radioValue1) => {
    setScrollList((prestate) => {
      return [
        ...prestate,
        {
          index: 2,
          dom: (
            <div className="look-project-wrap">
              <i className="robot-gif-icon" />
              <div
                className="look-project-first"
                style={{ marginLeft: "70px" }}
              >
                <div className="look-project-header">
                  <span className="pre-loading-text">
                    {radioValue1 === 2 ? "正在查询中..." : "正在生成中..."}{" "}
                  </span>
                </div>
                <div className="look-project-content loading-icon-wrap">
                  <i className="loading-icon" />
                </div>
              </div>
            </div>
          ),
        },
      ];
    });
    http
      .get(
        selectOption.current === "容量评估"
          ? `/revenueEstGuo/getProjectDetail?id=${id}`
          : `/revenueEst/getProjectDetail?id=${id}`
      )
      .then(async (res) => {
        if (res.data.code !== 200) {
          setScrollList((prestate) => {
            const list = [...prestate];
            list.pop();
            return [
              ...list,
              {
                index: 5,
                dom: (
                  <div className="energy-left-default-wrap">
                    <i className="energy-left-robot-icon" />
                    <div className="energy-left-message">
                      <span ref={message1}></span>
                    </div>
                  </div>
                ),
              },
            ];
          });
          setTimeout(() => {
            if (message1.current) {
              typed1 = new Typed(message1?.current, {
                strings: [res.data.msg],
                typeSpeed: 10,
                backSpeed: 50,
                showCursor: true,
                cursorChar: "",
                onComplete(self) {
                  self.cursor.style.display = "none"; // 隐藏光标
                  setStatus("init");
                  setRadioValue(null);
                  setFileList([]);
                  setFileId("");
                  setProjectName("");
                  setPower("");
                  setVolume("");
                  setSelectedCity("");
                  setScrollList((prestate) => {
                    return [
                      ...prestate,
                      {
                        index: 0,
                        dom: (
                          <SelectWrap
                            doInit={doInit}
                            setRadioValue={setRadioValue}
                          />
                        ),
                      },
                    ];
                  });
                },
              });
            }
          }, 300);
          return;
        }

        const res1 = await http.get("/revenueEst/queryCityEleSystem");
        let demandArr = [];
        if (selectOption.current === "容量评估") {
          const res2 = await http.post("/revenueEstGuo/listProjectResult", {
            projectId: id,
          });
          demandArr = res2?.data?.data?.result;
        }
        const options = res1.data.data;
        const list1 = options.map((item) => {
          return {
            label: item.value,
            value: item.value,
          };
        });

        setBaseInfo(res.data.data.basicInfo);
        setAdvanceInfo(res.data.data.advanceInfo);
        setCapacityRangeLow(
          res.data.data.advanceInfo?.advanceParameter
            ? res.data.data.advanceInfo?.advanceParameter[0]?.capacityRangeLow
            : ""
        );
        setCapacityRangeUp(
          res.data.data.advanceInfo?.advanceParameter
            ? res.data.data.advanceInfo?.advanceParameter[0]?.capacityRangeUp
            : ""
        );
        setIrrMin(
          res.data.data.advanceInfo?.advanceParameter
            ? res.data.data.advanceInfo?.advanceParameter[0]?.irrMin
            : ""
        );
        setCountingPeriod(
          res.data.data.advanceInfo?.advanceParameter
            ? res.data.data.advanceInfo?.advanceParameter[0]?.countingPeriod
            : ""
        );
        setTimeout(() => {
          setScrollList((prestate) => {
            const list = [...prestate];
            list.pop();
            return [
              ...list,
              {
                index: 2,
                dom: (
                  <div className="look-project-wrap">
                    <i className="robot-icon" />
                    <div
                      className="look-project-first"
                      style={{ marginLeft: "70px" }}
                    >
                      <div className="look-project-header">
                        <span ref={message1}></span>
                      </div>
                      <div className="look-project-content loading-icon-wrap">
                        <i className="loading-icon" />
                      </div>
                    </div>
                  </div>
                ),
              },
            ];
          });
          typed1 = new Typed(message1?.current, {
            strings: [
              `好的，已展示项目最新信息，您可修改信息内容或直接${
                selectOption.current === "容量评估" ? "开始评估" : "生成报告"
              }`,
            ],
            typeSpeed: 10,
            backSpeed: 50,
            showCursor: true,
            cursorChar: "",
            onComplete(self) {
              self.cursor.style.display = "none"; // 隐藏光标
              setIsOneCanCLick(true);
              setScrollList((prestate) => {
                const list = [...prestate];
                list.pop();
                return [
                  ...list,
                  {
                    index: 5,
                    dom: (
                      <LookProject
                        updateBaseInfo={updateBaseInfo}
                        setUpdateBaseInfo={setUpdateBaseInfo}
                        baseInfo={res.data.data.basicInfo}
                        advanceInfo={res.data.data.advanceInfo}
                        cityOptions={list1}
                        setFileId={setFileId}
                        setFileList={setFileList}
                        setRightFileList={setRightFileList}
                        rightFileList={rightFileList}
                        selectOption={selectOption.current}
                        setNoDemandFile={setNoDemandFile}
                        demandArr={demandArr}
                        capacityRangeLow={
                          res.data.data.advanceInfo?.advanceParameter
                            ? res.data.data.advanceInfo?.advanceParameter[0]
                                ?.capacityRangeLow
                            : ""
                        }
                        countingPeriod={
                          res.data.data.advanceInfo?.advanceParameter
                            ? res.data.data.advanceInfo?.advanceParameter[0]
                                ?.countingPeriod
                            : ""
                        }
                        setCountingPeriod={setCountingPeriod}
                        irrMin={
                          res.data.data.advanceInfo?.advanceParameter
                            ? res.data.data.advanceInfo?.advanceParameter[0]
                                ?.irrMin
                            : ""
                        }
                        setCapacityRangeLow={setCapacityRangeLow}
                        setIrrMin={setIrrMin}
                        capacityRangeUp={
                          res.data.data.advanceInfo?.advanceParameter
                            ? res.data.data.advanceInfo?.advanceParameter[0]
                                ?.capacityRangeUp
                            : ""
                        }
                        setCapacityRangeUp={setCapacityRangeUp}
                      />
                    ),
                  },
                ];
              });
            },
          });
        }, 500);
      });
  };

  const onSend = () => {
    if (radioValue === 1) {
      if (!fileList.length) {
        message.error({
          content: "请先上传excel文件！",
          icon: <i className="customer-error-message-icon"></i>,
          className: "customer-error-message",
        });
        return;
      }
      setScrollList([
        ...scrollList,
        {
          index: 1,
          dom: (
            <div className="right-default-wrap">
              <div className="right-message">
                <span>
                  帮我测算一个新项目，项目名称是：{projectName} ，所在地区：
                  {selectedCity} ，
                  <>
                    {selectOption.current === "容量评估"
                      ? `用电类型：${electricType} ，电压等级 ：${voltageLevel}`
                      : `储能容量：${volume} kWh，储能功率 ：${power} kW`}
                  </>
                </span>
              </div>
              <div className="file-container">
                <div className="file-item">
                  <i className="file-item-icon" />
                  <>
                    {
                      <div className="file-item-info">
                        <span className="file-item-info-text">
                          {
                            rightFileList.filter(
                              (item) => item.fileName === fileList[0].fileName
                            )[0]?.fileName
                          }
                        </span>
                        <span className="file-item-info-size">
                          {
                            rightFileList.filter(
                              (item) => item.fileName === fileList[0].fileName
                            )[0]?.fileSize
                          }
                        </span>
                      </div>
                    }
                  </>
                </div>
              </div>
            </div>
          ),
        },
      ]);
      selectOption.current === "容量评估" ? createGuodian() : create();
    }
    if (radioValue === 2) {
      setScrollList([
        ...scrollList,
        {
          index: 1,
          dom: (
            <div className="right-default-wrap">
              <div className="right-message">
                <span>
                  我想要查看 <>{selectProject?.name}</> 项目的
                  {selectOption.current === "容量评估" ? "容量" : "收益测算"}
                  评估
                </span>
              </div>
            </div>
          ),
        },
      ]);
      look();
    }
    setStatus("look");
  };

  const look = async () => {
    setScrollList((prestate) => {
      return [
        ...prestate,
        {
          index: 2,
          dom: (
            <div className="look-project-wrap">
              <i className="robot-gif-icon" />
              <div
                className="look-project-first"
                style={{ marginLeft: "70px" }}
              >
                <div className="look-project-header">
                  <span className="pre-loading-text">
                    {radioValue === 2 ? "正在查询中..." : "正在生成中..."}
                  </span>
                </div>
                <div className="look-project-content loading-icon-wrap">
                  <i className="loading-icon" />
                </div>
              </div>
            </div>
          ),
        },
      ];
    });

    http
      .get(
        selectOption.current === "容量评估"
          ? `/revenueEstGuo/getProjectDetail?id=${selectProject.id}`
          : `/revenueEst/getProjectDetail?id=${selectProject.id}`
      )
      .then(async (res) => {
        if (res.data.code !== 200) {
          setScrollList((prestate) => {
            const list = [...prestate];
            list.pop();
            return [
              ...list,
              {
                index: 5,
                dom: (
                  <div className="energy-left-default-wrap">
                    <i className="energy-left-robot-icon" />
                    <div className="energy-left-message">
                      <span ref={message1}></span>
                    </div>
                  </div>
                ),
              },
            ];
          });
          setTimeout(() => {
            if (message1.current) {
              typed1 = new Typed(message1?.current, {
                strings: [res.data.msg],
                typeSpeed: 10,
                backSpeed: 50,
                showCursor: true,
                cursorChar: "",
                onComplete(self) {
                  self.cursor.style.display = "none"; // 隐藏光标
                  setStatus("init");
                  setRadioValue(null);
                  setFileList([]);
                  setFileId("");
                  setProjectName("");
                  setPower("");
                  setVolume("");
                  setSelectedCity("");
                  setScrollList((prestate) => {
                    return [
                      ...prestate,
                      {
                        index: 0,
                        dom: (
                          <SelectWrap
                            doInit={doInit}
                            setRadioValue={setRadioValue}
                          />
                        ),
                      },
                    ];
                  });
                },
              });
            }
          }, 300);
          return;
        }

        const res1 = await http.get("/revenueEst/queryCityEleSystem");
        let demandArr = [];
        if (selectOption.current === "容量评估") {
          const res2 = await http.post("/revenueEstGuo/listProjectResult", {
            projectId: selectProject.id,
          });
          demandArr = res2?.data?.data?.result;
        }
        const options = res1.data.data;
        const list1 = options.map((item) => {
          return {
            label: item.value,
            value: item.value,
          };
        });

        setBaseInfo(res.data.data.basicInfo);
        setAdvanceInfo(res.data.data.advanceInfo);
        setCapacityRangeLow(
          res.data.data.advanceInfo?.advanceParameter
            ? res.data.data.advanceInfo?.advanceParameter[0]?.capacityRangeLow
            : ""
        );
        setCapacityRangeUp(
          res.data.data.advanceInfo?.advanceParameter
            ? res.data.data.advanceInfo?.advanceParameter[0]?.capacityRangeUp
            : ""
        );
        setIrrMin(
          res.data.data.advanceInfo?.advanceParameter
            ? res.data.data.advanceInfo?.advanceParameter[0]?.irrMin
            : ""
        );
        setCountingPeriod(
          res.data.data.advanceInfo?.advanceParameter
            ? res.data.data.advanceInfo?.advanceParameter[0]?.countingPeriod
            : ""
        );
        setTimeout(() => {
          setScrollList((prestate) => {
            const list = [...prestate];
            list.pop();
            return [
              ...list,
              {
                index: 2,
                dom: (
                  <div className="look-project-wrap">
                    <i className="robot-icon" />
                    <div
                      className="look-project-first"
                      style={{ marginLeft: "70px" }}
                    >
                      <div className="look-project-header">
                        <span ref={message5}></span>
                      </div>
                      <div className="look-project-content loading-icon-wrap">
                        <i className="loading-icon" />
                      </div>
                    </div>
                  </div>
                ),
              },
            ];
          });
          typed5 = new Typed(message5?.current, {
            strings: [
              `好的，已展示项目最新信息，您可修改信息内容或直接${
                selectOption.current === "容量评估" ? "开始评估" : "生成报告"
              }`,
            ],
            typeSpeed: 10,
            backSpeed: 50,
            showCursor: true,
            cursorChar: "",
            onComplete(self) {
              self.cursor.style.display = "none"; // 隐藏光标
              setIsOneCanCLick(true);
              setScrollList((prestate) => {
                const list = [...prestate];
                list.pop();
                return [
                  ...list,
                  {
                    index: 5,
                    dom: (
                      <LookProject
                        updateBaseInfo={updateBaseInfo}
                        setUpdateBaseInfo={setUpdateBaseInfo}
                        baseInfo={res.data.data.basicInfo}
                        advanceInfo={res.data.data.advanceInfo}
                        cityOptions={list1}
                        setFileId={setFileId}
                        setFileList={setFileList}
                        setRightFileList={setRightFileList}
                        rightFileList={rightFileList}
                        selectOption={selectOption.current}
                        setNoDemandFile={setNoDemandFile}
                        demandArr={demandArr}
                        capacityRangeLow={
                          res.data.data.advanceInfo?.advanceParameter
                            ? res.data.data.advanceInfo?.advanceParameter[0]
                                ?.capacityRangeLow
                            : ""
                        }
                        countingPeriod={
                          res.data.data.advanceInfo?.advanceParameter
                            ? res.data.data.advanceInfo?.advanceParameter[0]
                                ?.countingPeriod
                            : ""
                        }
                        setCountingPeriod={setCountingPeriod}
                        irrMin={
                          res.data.data.advanceInfo?.advanceParameter
                            ? res.data.data.advanceInfo?.advanceParameter[0]
                                ?.irrMin
                            : ""
                        }
                        setCapacityRangeLow={setCapacityRangeLow}
                        setIrrMin={setIrrMin}
                        capacityRangeUp={
                          res.data.data.advanceInfo?.advanceParameter
                            ? res.data.data.advanceInfo?.advanceParameter[0]
                                ?.capacityRangeUp
                            : ""
                        }
                        setCapacityRangeUp={setCapacityRangeUp}
                      />
                    ),
                  },
                ];
              });
            },
          });
        }, 2000);
      });
  };

  const create = async () => {
    setScrollList((prestate) => {
      return [
        ...prestate,
        {
          index: 2,
          dom: (
            <div className="look-project-wrap">
              <i className="robot-gif-icon" />
              <div
                className="look-project-first"
                style={{ marginLeft: "70px" }}
              >
                <div className="look-project-header">
                  <span className="pre-loading-text">正在生成中...</span>
                </div>
                <div className="look-project-content loading-icon-wrap">
                  <i className="loading-icon" />
                </div>
              </div>
            </div>
          ),
        },
      ];
    });

    const res = await http.post("/revenueEst/createProject", {
      projectId: fileId,
      projectName,
      area: selectedCity,
      volume,
      power,
    });
    if (res.data.code === 400) {
      setScrollList((prestate) => {
        const list = [...prestate];
        list.pop();
        return [
          ...list,
          {
            index: 5,
            dom: (
              <div className="energy-left-default-wrap">
                <i className="energy-left-robot-icon" />
                <div className="energy-left-message">
                  <span ref={message1}></span>
                </div>
              </div>
            ),
          },
        ];
      });
      setTimeout(() => {
        if (message1.current) {
          typed1 = new Typed(message1?.current, {
            strings: [res.data.msg],
            typeSpeed: 10,
            backSpeed: 50,
            showCursor: true,
            cursorChar: "",
            onComplete(self) {
              self.cursor.style.display = "none"; // 隐藏光标
              setStatus("init");
              setRadioValue(null);
              setFileList([]);
              setFileId("");
              setProjectName("");
              setPower("");
              setVolume("");
              setSelectedCity("");
              setScrollList((prestate) => {
                return [
                  ...prestate,
                  {
                    index: 0,
                    dom: (
                      <SelectWrap
                        doInit={doInit}
                        setRadioValue={setRadioValue}
                      />
                    ),
                  },
                ];
              });
            },
          });
        }
      }, 300);
      return;
    }
    setSelectProject({
      id: res.data.data.basicInfo.projectId,
      name: res.data.data.basicInfo.projectName,
    });
    const res1 = await http.get("/revenueEst/queryCityEleSystem");
    let demandArr = [];
    if (selectOption.current === "容量评估") {
      const res2 = await http.post("/revenueEstGuo/listProjectResult", {
        projectId: fileId,
      });
      demandArr = res2?.data?.data?.result;
    }

    const options = res1.data.data;
    const list1 = options.map((item) => {
      return {
        label: item.value,
        value: item.value,
      };
    });
    const typeOptions = await http.get(
      `/revenueEst/queryElectricityType?city=${res.data.data.basicInfo.area}`
    );

    setBaseInfo(res.data.data.basicInfo);
    setAdvanceInfo(res.data.data.advanceInfo);
    setCapacityRangeLow(
      res.data.data.advanceInfo?.advanceParameter
        ? res.data.data.advanceInfo?.advanceParameter[0]?.capacityRangeLow
        : ""
    );
    setCapacityRangeUp(
      res.data.data.advanceInfo?.advanceParameter
        ? res.data.data.advanceInfo?.advanceParameter[0]?.capacityRangeUp
        : ""
    );
    setIrrMin(
      res.data.data.advanceInfo?.advanceParameter
        ? res.data.data.advanceInfo?.advanceParameter[0]?.irrMin
        : ""
    );
    setCountingPeriod(
      res.data.data.advanceInfo?.advanceParameter
        ? res.data.data.advanceInfo?.advanceParameter[0]?.countingPeriod
        : ""
    );
    setTimeout(() => {
      setScrollList((prestate) => {
        const list = [...prestate];
        list.pop();
        return [
          ...list,
          {
            index: 2,
            dom: (
              <div className="look-project-wrap">
                <i className="robot-icon" />
                <div
                  className="look-project-first"
                  style={{ marginLeft: "70px" }}
                >
                  <div className="look-project-header">
                    <span ref={message2}></span>
                  </div>
                  <div className="look-project-content loading-icon-wrap">
                    <i className="loading-icon" />
                  </div>
                </div>
              </div>
            ),
          },
        ];
      });
      typed3 = new Typed(message2.current, {
        strings: [
          `好的，已生成新项目信息，您可修改信息内或直接${
            selectOption.current === "容量评估" ? "开始评估" : "生成报告"
          }`,
        ],
        typeSpeed: 10,
        backSpeed: 50,
        showCursor: true,
        cursorChar: "",
        onComplete(self) {
          self.cursor.style.display = "none"; // 隐藏光标
          setIsOneCanCLick(true);
          setScrollList((prestate) => {
            const list = [...prestate];
            list.pop();
            return [
              ...list,
              {
                index: 5,
                dom: (
                  <LookProject
                    updateBaseInfo={updateBaseInfo}
                    radioValue={radioValue}
                    setUpdateBaseInfo={setUpdateBaseInfo}
                    baseInfo={res.data.data.basicInfo}
                    advanceInfo={res.data.data.advanceInfo}
                    cityOptions={list1}
                    setFileId={setFileId}
                    setFileList={setFileList}
                    setRightFileList={setRightFileList}
                    rightFileList={rightFileList}
                    selectOption={selectOption.current}
                    setNoDemandFile={setNoDemandFile}
                    demandArr={demandArr}
                    capacityRangeLow={
                      res.data.data.advanceInfo?.advanceParameter
                        ? res.data.data.advanceInfo?.advanceParameter[0]
                            ?.capacityRangeLow
                        : ""
                    }
                    countingPeriod={
                      res.data.data.advanceInfo?.advanceParameter
                        ? res.data.data.advanceInfo?.advanceParameter[0]
                            ?.countingPeriod
                        : ""
                    }
                    setCountingPeriod={setCountingPeriod}
                    irrMin={
                      res.data.data.advanceInfo?.advanceParameter
                        ? res.data.data.advanceInfo?.advanceParameter[0]?.irrMin
                        : ""
                    }
                    setCapacityRangeLow={setCapacityRangeLow}
                    setIrrMin={setIrrMin}
                    capacityRangeUp={
                      res.data.data.advanceInfo?.advanceParameter
                        ? res.data.data.advanceInfo?.advanceParameter[0]
                            ?.capacityRangeUp
                        : ""
                    }
                    setCapacityRangeUp={setCapacityRangeUp}
                  />
                ),
              },
            ];
          });
        },
      });
    }, 2000);
  };

  const createGuodian = async () => {
    setScrollList((prestate) => {
      return [
        ...prestate,
        {
          index: 2,
          dom: (
            <div className="look-project-wrap">
              <i className="robot-gif-icon" />
              <div
                className="look-project-first"
                style={{ marginLeft: "70px" }}
              >
                <div className="look-project-header">
                  <span className="pre-loading-text">正在生成中...</span>
                </div>
                <div className="look-project-content loading-icon-wrap">
                  <i className="loading-icon" />
                </div>
              </div>
            </div>
          ),
        },
      ];
    });

    const res = await axios.post("/revenueEstGuo/createProject", {
      projectId: fileId,
      projectName,
      area: selectedCity,
      eleType1: electricType?.split("_")[0],
      eleType2: electricType?.split("_")[1],
      volType1: voltageLevel.split("_")[0],
      volType2: voltageLevel?.split("_")[1],
    });
    if (res.data.code === 400) {
      setScrollList((prestate) => {
        const list = [...prestate];
        list.pop();
        return [
          ...list,
          {
            index: 5,
            dom: (
              <div className="energy-left-default-wrap">
                <i className="energy-left-robot-icon" />
                <div className="energy-left-message">
                  <span ref={message3}></span>
                </div>
              </div>
            ),
          },
        ];
      });
      setTimeout(() => {
        setTimeout(() => {
          if (message3.current) {
            typed1 = new Typed(message3?.current, {
              strings: [res.data.msg],
              typeSpeed: 10,
              backSpeed: 50,
              showCursor: true,
              cursorChar: "",
              onComplete(self) {
                self.cursor.style.display = "none"; // 隐藏光标
                setStatus("init");
                setRadioValue(null);
                setFileList([]);
                setFileId("");
                setProjectName("");
                setPower("");
                setVolume("");
                setSelectedCity("");
                setScrollList((prestate) => {
                  return [
                    ...prestate,
                    {
                      index: 0,
                      dom: (
                        <SelectWrap
                          doInit={doInit}
                          setRadioValue={setRadioValue}
                        />
                      ),
                    },
                  ];
                });
              },
            });
          }
        }, 300);
      }, 300);
      return;
    }
    setSelectProject({
      id: res.data.data.basicInfo.projectId,
      name: res.data.data.basicInfo.projectName,
    });
    const res1 = await http.get("/revenueEst/queryCityEleSystem");
    let demandArr = [];
    if (selectOption.current === "容量评估") {
      const res2 = await http.post("/revenueEstGuo/listProjectResult", {
        projectId: res.data.data.basicInfo.projectId,
      });
      demandArr = res2?.data?.data?.result;
    }
    const options = res1.data.data;
    const list1 = options.map((item) => {
      return {
        label: item.value,
        value: item.value,
      };
    });
    const typeOptions = await http.get(
      `/revenueEst/queryElectricityType?city=${res.data.data.basicInfo.area}`
    );

    setBaseInfo(res.data.data.basicInfo);
    setAdvanceInfo(res.data.data.advanceInfo);
    setCapacityRangeLow(
      res.data.data.advanceInfo?.advanceParameter
        ? res.data.data.advanceInfo?.advanceParameter[0]?.capacityRangeLow
        : ""
    );
    setCapacityRangeUp(
      res.data.data.advanceInfo?.advanceParameter
        ? res.data.data.advanceInfo?.advanceParameter[0]?.capacityRangeUp
        : ""
    );
    setIrrMin(
      res.data.data.advanceInfo?.advanceParameter
        ? res.data.data.advanceInfo?.advanceParameter[0]?.irrMin
        : ""
    );
    setCountingPeriod(
      res.data.data.advanceInfo?.advanceParameter
        ? res.data.data.advanceInfo?.advanceParameter[0]?.countingPeriod
        : ""
    );
    setTimeout(() => {
      setScrollList((prestate) => {
        const list = [...prestate];
        list.pop();
        return [
          ...list,
          {
            index: 2,
            dom: (
              <div className="look-project-wrap">
                <i className="robot-icon" />
                <div
                  className="look-project-first"
                  style={{ marginLeft: "70px" }}
                >
                  <div className="look-project-header">
                    <span ref={message2}></span>
                  </div>
                  <div className="look-project-content loading-icon-wrap">
                    <i className="loading-icon" />
                  </div>
                </div>
              </div>
            ),
          },
        ];
      });
      typed3 = new Typed(message2.current, {
        strings: [
          `好的，已生成新项目信息，您可修改信息内或直接${
            selectOption.current === "容量评估" ? "开始评估" : "生成报告"
          }`,
        ],
        typeSpeed: 10,
        backSpeed: 50,
        showCursor: true,
        cursorChar: "",
        onComplete(self) {
          self.cursor.style.display = "none"; // 隐藏光标
          setIsOneCanCLick(true);
          setScrollList((prestate) => {
            const list = [...prestate];
            list.pop();
            return [
              ...list,
              {
                index: 5,
                dom: (
                  <LookProject
                    updateBaseInfo={updateBaseInfo}
                    radioValue={radioValue}
                    setUpdateBaseInfo={setUpdateBaseInfo}
                    baseInfo={res.data.data.basicInfo}
                    advanceInfo={res.data.data.advanceInfo}
                    cityOptions={list1}
                    setFileId={setFileId}
                    setFileList={setFileList}
                    setRightFileList={setRightFileList}
                    rightFileList={rightFileList}
                    selectOption={selectOption.current}
                    setNoDemandFile={setNoDemandFile}
                    demandArr={demandArr}
                    capacityRangeLow={
                      res.data.data.advanceInfo?.advanceParameter
                        ? res.data.data.advanceInfo?.advanceParameter[0]
                            ?.capacityRangeLow
                        : ""
                    }
                    countingPeriod={
                      res.data.data.advanceInfo?.advanceParameter
                        ? res.data.data.advanceInfo?.advanceParameter[0]
                            ?.countingPeriod
                        : ""
                    }
                    setCountingPeriod={setCountingPeriod}
                    irrMin={
                      res.data.data.advanceInfo?.advanceParameter
                        ? res.data.data.advanceInfo?.advanceParameter[0]?.irrMin
                        : ""
                    }
                    setCapacityRangeLow={setCapacityRangeLow}
                    setIrrMin={setIrrMin}
                    capacityRangeUp={
                      res.data.data.advanceInfo?.advanceParameter
                        ? res.data.data.advanceInfo?.advanceParameter[0]
                            ?.capacityRangeUp
                        : ""
                    }
                    setCapacityRangeUp={setCapacityRangeUp}
                  />
                ),
              },
            ];
          });
        },
      });
    }, 2000);
  };

  const edit = () => {
    http
      .get(`/revenueEst/getProjectDetail?id=${selectProject.id}`)
      .then(async (res) => {
        const res1 = await http.get("/revenueEst/queryCityEleSystem");
        const options = res1.data.data;
        const list1 = options.map((item) => {
          return {
            label: item.value,
            value: item.value,
          };
        });

        setIsOneCanCLick(false);
        setScrollList([
          ...scrollList,
          {
            index: 3,
            dom: (
              <div className="right-default-wrap">
                <div className="right-message">
                  <span>编辑高级配置</span>
                </div>
              </div>
            ),
          },
        ]);

        setScrollList((prestate) => {
          return [
            ...prestate,
            {
              index: 2,
              dom: (
                <div className="look-project-wrap">
                  <i className="robot-gif-icon" />
                  <div
                    className="look-project-first"
                    style={{ marginLeft: "70px" }}
                  >
                    <div className="look-project-header">
                      <span className="pre-loading-text">正在查询中...</span>
                    </div>
                    <div className="look-project-content loading-icon-wrap">
                      <i className="loading-icon" />
                    </div>
                  </div>
                </div>
              ),
            },
          ];
        });

        setTimeout(() => {
          setScrollList((prestate) => {
            const list = [...prestate];
            list.pop();
            return [
              ...list,
              {
                index: 2,
                dom: (
                  <div className="look-project-wrap">
                    <i className="robot-icon" />
                    <div
                      className="look-project-first"
                      style={{ marginLeft: "70px" }}
                    >
                      <div className="look-project-header">
                        <span ref={message3}></span>
                      </div>
                      <div className="look-project-content loading-icon-wrap">
                        <i className="loading-icon" />
                      </div>
                    </div>
                  </div>
                ),
              },
            ];
          });
          typed3 = new Typed(message3.current, {
            strings: [
              `好的，已展示项目最新信息，您可修改信息内容或直接${
                selectOption.current === "容量评估" ? "开始评估" : "生成报告"
              }`,
            ],
            typeSpeed: 10,
            backSpeed: 50,
            showCursor: true,
            cursorChar: "",
            onComplete(self) {
              self.cursor.style.display = "none";
              setIsTwoCanCLick(true);
              setScrollList((prestate) => {
                const list = [...prestate];
                list.pop();
                return [
                  ...list,
                  {
                    index: 5,
                    dom: (
                      <EditProject
                        setUpdateAllInfo={setUpdateAllInfo}
                        updateAllInfo={updateAllInfo}
                        advanceInfo={res.data.data.advanceInfo}
                        baseInfo={res.data.data.basicInfo}
                        cityOptions={list1}
                        setFileId={setFileId}
                        setFileList={setFileList}
                        setRightFileList={setRightFileList}
                        rightFileList={rightFileList}
                      />
                    ),
                  },
                ];
              });
            },
          });
        }, 2000);

        setStatus("edit");
      });
  };

  const doGenerate = async () => {
    setStatus("end");
    setIsOneCanCLick(false);
    setScrollList([
      ...scrollList,
      {
        index: 3,
        dom: (
          <div className="right-default-wrap">
            <div className="right-message">
              <span>
                {selectOption.current === "容量评估" ? "开始评估" : "生成报告"}
              </span>
            </div>
          </div>
        ),
      },
    ]);
    setScrollList((prestate) => {
      return [
        ...prestate,
        {
          index: 2,
          dom: (
            <div className="look-project-wrap">
              <i className="robot-gif-icon" />
              <div
                className="look-project-first"
                style={{ marginLeft: "70px" }}
              >
                <div className="look-project-header">
                  <span className="pre-loading-text">正在生成中...</span>
                </div>
                <div className="look-project-content loading-icon-wrap">
                  <i className="loading-icon" />
                </div>
              </div>
            </div>
          ),
        },
      ];
    });
  };
  const [interval, setInterval] = useState(null);

  async function blockingOperation(timeInMS) {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve("操作完成");
      }, timeInMS);
    });
  }
  async function blockingOperation2(timeInMS) {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve("操作完成");
      }, timeInMS);
    });
  }
  let count = 0;
  const generate = async () => {
    const res = await http.post("/revenueEst/firstPredictEnergyQuery", {
      projectId: selectProject.id,
    });
    ++count;
    if (count < 100) {
      if (res.data.code === 202) {
        await blockingOperation(3000);
        generate();
      } else if (res.data.code === 400) {
        setScrollList((prestate) => {
          const list = [...prestate];
          list.pop();
          return [
            ...list,
            {
              index: 5,
              dom: (
                <div className="energy-left-default-wrap">
                  <i className="energy-left-robot-icon" />
                  <div className="energy-left-message">
                    <span ref={message1}></span>
                  </div>
                </div>
              ),
            },
          ];
        });
        setTimeout(() => {
          if (message1.current) {
            typed1 = new Typed(message1?.current, {
              strings: [res.data.msg],
              typeSpeed: 10,
              backSpeed: 50,
              showCursor: true,
              cursorChar: "",
              onComplete(self) {
                self.cursor.style.display = "none"; // 隐藏光标
                setStatus("init");
                setRadioValue(null);
                setFileList([]);
                setFileId("");
                setProjectName("");
                setPower("");
                setVolume("");
                setSelectedCity("");
                setScrollList((prestate) => {
                  return [
                    ...prestate,
                    {
                      index: 0,
                      dom: (
                        <SelectWrap
                          doInit={doInit}
                          setRadioValue={setRadioValue}
                        />
                      ),
                    },
                  ];
                });
              },
            });
          }
        }, 300);
      } else {
        generateEnd(res.data.data);
        clearTimeout(interval);
      }
    } else {
      setScrollList((prestate) => {
        const list = [...prestate];
        list.pop();
        return [
          ...list,
          {
            index: 5,
            dom: (
              <div className="energy-left-default-wrap">
                <i className="energy-left-robot-icon" />
                <div className="energy-left-message">
                  <span ref={message1}></span>
                </div>
              </div>
            ),
          },
        ];
      });
      setTimeout(() => {
        if (message1.current) {
          typed1 = new Typed(message1?.current, {
            strings: ["测算失败，请重试！"],
            typeSpeed: 10,
            backSpeed: 50,
            showCursor: true,
            cursorChar: "",
            onComplete(self) {
              self.cursor.style.display = "none"; // 隐藏光标
              setStatus("init");
              setRadioValue(null);
              setFileList([]);
              setFileId("");
              setProjectName("");
              setPower("");
              setVolume("");
              setSelectedCity("");
              setScrollList((prestate) => {
                return [
                  ...prestate,
                  {
                    index: 0,
                    dom: (
                      <InitWrap
                        radioValue={null}
                        setRadioValue={setRadioValue}
                        setProjectOption={setProjectOption}
                        onSend1={onSend1}
                        initOptions={() => {
                          setElectricType("");
                          setVoltageLevel("");
                        }}
                      />
                    ),
                  },
                ];
              });
            },
          });
        }
      }, 300);
      return;
    }
  };
  let count2 = 0;
  const generate2 = async () => {
    const res = await http.post("/revenueEst/predictEnergyQuery", {
      projectId: selectProject.id,
    });
    ++count2;
    if (count2 < 100) {
      if (res.data.code === 202) {
        await blockingOperation2(3000);
        generate2();
      } else if (res.data.code === 400) {
        setScrollList((prestate) => {
          const list = [...prestate];
          list.pop();
          return [
            ...list,
            {
              index: 5,
              dom: (
                <div className="energy-left-default-wrap">
                  <i className="energy-left-robot-icon" />
                  <div className="energy-left-message">
                    <span ref={message1}></span>
                  </div>
                </div>
              ),
            },
          ];
        });
        setTimeout(() => {
          if (message1.current) {
            typed1 = new Typed(message1?.current, {
              strings: [res.data.msg],
              typeSpeed: 10,
              backSpeed: 50,
              showCursor: true,
              cursorChar: "",
              onComplete(self) {
                self.cursor.style.display = "none"; // 隐藏光标
                setStatus("init");
                setRadioValue(null);
                setFileList([]);
                setFileId("");
                setProjectName("");
                setPower("");
                setVolume("");
                setSelectedCity("");
                setScrollList((prestate) => {
                  return [
                    ...prestate,
                    {
                      index: 0,
                      dom: (
                        <SelectWrap
                          doInit={doInit}
                          setRadioValue={setRadioValue}
                        />
                      ),
                    },
                  ];
                });
              },
            });
          }
        }, 300);
      } else {
        generateEnd(res.data.data);
      }
    } else {
      setScrollList((prestate) => {
        const list = [...prestate];
        list.pop();
        return [
          ...list,
          {
            index: 5,
            dom: (
              <div className="energy-left-default-wrap">
                <i className="energy-left-robot-icon" />
                <div className="energy-left-message">
                  <span ref={message1}></span>
                </div>
              </div>
            ),
          },
        ];
      });
      setTimeout(() => {
        if (message1.current) {
          typed1 = new Typed(message1?.current, {
            strings: [res.data.msg],
            typeSpeed: 10,
            backSpeed: 50,
            showCursor: true,
            cursorChar: "",
            onComplete(self) {
              self.cursor.style.display = "none"; // 隐藏光标
              setStatus("init");
              setRadioValue(null);
              setFileList([]);
              setFileId("");
              setProjectName("");
              setPower("");
              setVolume("");
              setSelectedCity("");
              setScrollList((prestate) => {
                return [
                  ...prestate,
                  {
                    index: 0,
                    dom: (
                      <SelectWrap
                        doInit={doInit}
                        setRadioValue={setRadioValue}
                      />
                    ),
                  },
                ];
              });
            },
          });
        }
      }, 300);
    }
  };

  const generateEnd = async (res) => {
    let demandArr = [];
    if (selectOption.current === "容量评估") {
      const res2 = await http.post("/revenueEstGuo/listProjectResult", {
        projectId: selectProject.id,
      });
      demandArr = res2?.data?.data?.result;
    }
    setTimeout(() => {
      setScrollList((prestate) => {
        const list = [...prestate];
        list.pop();
        return [
          ...list,
          {
            index: 2,
            dom: (
              <div className="look-project-wrap">
                <i className="robot-icon" />
                <div
                  className="look-project-first"
                  style={{ marginLeft: "70px" }}
                >
                  <div className="look-project-header">
                    <span ref={message3}></span>
                  </div>
                  <div className="look-project-content loading-icon-wrap">
                    <i className="loading-icon" />
                  </div>
                </div>
              </div>
            ),
          },
        ];
      });
      typed3 = new Typed(message3.current, {
        strings: [
          selectOption.current === "容量评估"
            ? "好的，评估结果已产生"
            : "好的，已生成最新的储能收益测算报告",
        ],
        typeSpeed: 10,
        backSpeed: 50,
        showCursor: true,
        cursorChar: "",
        onComplete(self) {
          self.cursor.style.display = "none"; // 隐藏光标
          setIsThreeCanCLick(true);
          setScrollList((prestate) => {
            const list = [...prestate];
            list.pop();
            return [
              ...list,
              {
                index: 5,
                dom: (
                  <GenerateReport
                    data={res}
                    projectName={projectName}
                    projectId={selectProject.id}
                    selectOption={selectOption.current}
                    demandArr={demandArr}
                  />
                ),
              },
            ];
          });
        },
      });
    }, 2000);
  };

  const gdtGnerateEnd = async (res) => {
    if (res.data.code === 201) {
      doGenerate();
      setStatus("end");
      getResult(res);
      setIsThreeCanCLick(true);
    } else {
      generateResult(res);
    }
  };

  const getResult = async (data) => {
    props.isExit.current = false;
    const res = await http.post("/revenueEstGuo/checkProjectResult", {
      projectId: selectProject.id,
    });
    const minute = res?.data?.data?.minute;
    const progress = res?.data?.data?.progress;

    setScrollList((prestate) => {
      const list = [...prestate];
      list.pop();
      return [
        ...list,
        {
          index: 5,
          dom: (
            <div className="energy-left-default-wrap gdt">
              <i className="energy-left-robot-icon" />
              <div className="energy-left-message">
                <p className="gdt-finish">
                  好的，正在测算中，大约还需要
                  <span className="gdt-minute">{minute}分钟</span>
                </p>
                <Progress
                  className="gdt-progress"
                  percent={progress * 100}
                  size="small"
                  showInfo={false}
                  strokeColor="#0092FF"
                  trailColor="#616672"
                />
              </div>
            </div>
          ),
        },
      ];
    });

    if (res.data.code !== 200) {
      timerId.current = setTimeout(() => {
        if (!props.isExit.current) {
          getResult(data);
        }
      }, 15000);
    } else {
      generateResult(data);
    }
  };

  const generateResult = async (res) => {
    doGenerate();
    setStatus("end");
    let demandArr = [];
    if (selectOption.current === "容量评估") {
      const res2 = await http.post("/revenueEstGuo/listProjectResult", {
        projectId: selectProject.id,
      });
      demandArr = res2?.data?.data?.result;
    }
    setTimeout(() => {
      setScrollList((prestate) => {
        const list = [...prestate];
        list.pop();
        return [
          ...list,
          {
            index: 2,
            dom: (
              <div className="look-project-wrap">
                <i className="robot-icon" />
                <div
                  className="look-project-first"
                  style={{ marginLeft: "70px" }}
                >
                  <div className="look-project-header">
                    <span ref={message3}></span>
                  </div>
                  <div className="look-project-content loading-icon-wrap">
                    <i className="loading-icon" />
                  </div>
                </div>
              </div>
            ),
          },
        ];
      });
      typed3 = new Typed(message3.current, {
        strings: [
          selectOption.current === "容量评估"
            ? "好的，评估结果已产生"
            : "好的，已生成最新的储能收益测算报告",
        ],
        typeSpeed: 10,
        backSpeed: 50,
        showCursor: true,
        cursorChar: "",
        onComplete(self) {
          self.cursor.style.display = "none"; // 隐藏光标
          setIsThreeCanCLick(true);
          setScrollList((prestate) => {
            const list = [...prestate];
            list.pop();
            return [
              ...list,
              {
                index: 5,
                dom: (
                  <GenerateReport
                    gdtData={res?.data?.data}
                    projectName={projectName}
                    projectId={selectProject.id}
                    selectOption={selectOption.current}
                    demandArr={demandArr}
                  />
                ),
              },
            ];
          });
        },
      });
    }, 2000);
  };

  const reset = () => {
    setStatus("init");
    setRadioValue(null);
    setFileList([]);
    setFileId("");
    setProjectName("");
    setPower("");
    setVolume("");
    setSelectedCity("");
    props.isExit.current = true;
    if (interval) {
      clearTimeout(interval);
    }
    initSelect();
  };

  const onChange = (val, options) => {
    setSelectedCity(val[0]);
    initType(val[0]);
    setElectricType("");
    setVoltageLevel("");
  };

  useEffect(() => {
    // init()
    initSelect();
    return () => {
      if (interval) {
        clearTimeout(interval);
      }
      if (timerId.current) {
        clearTimeout(timerId.current);
      }
    };
  }, []);

  const initSelect = () => {
    setScrollList([
      ...scrollList,
      {
        index: 0,
        dom: <SelectWrap doInit={doInit} setRadioValue={setRadioValue} />,
      },
    ]);
  };

  const doInit = (type) => {
    selectOption.current = type;
    setScrollList((prestate) => {
      return [
        ...prestate,
        {
          index: 1,
          dom: (
            <div className="right-default-wrap">
              <div className="right-message">
                <span>{type}</span>
              </div>
            </div>
          ),
        },
      ];
    });
    init(type);
  };

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

  const initType = async (city) => {
    const res2 = await http.get(
      `/revenueEst/queryElectricityType?city=${city}`
    );
    const option1 = res2.data.data;
    const list = transformData(option1);
    // const va11 = list[0].value;
    // const va12 = list[0]?.children[0]?.value;
    // if (va12) {
    //   setElectricType(`${va11}_${va12}`);
    // } else {
    //   setElectricType(`${va11}`);
    // }
    setTypeOptions(list);
  };

  const initVoltageLevel = async (city, type1, type2) => {
    const res2 = await http.get(
      `/revenueEst/queryElectricityType?city=${city}&type1=${type1}&type2=${type2}`
    );
    const option1 = res2.data.data;
    const list = transformData(option1);
    setVoltageLevelOptions(list);
  };

  const init = async (type) => {
    const res = await http.get("/revenueEst/queryCityEleSystem");
    const options = res.data.data;
    const list = options.map((item) => {
      return {
        label: item.value,
        value: item.value,
      };
    });
    setCityOptions(list);
    setScrollList((prestate) => {
      return [
        ...prestate,
        {
          index: 0,
          dom: (
            <InitWrap
              radioValue={null}
              setRadioValue={setRadioValue}
              setProjectOption={setProjectOption}
              onSend1={onSend1}
              selectOption={type}
              initOptions={() => {
                setElectricType("");
                setVoltageLevel("");
              }}
            />
          ),
        },
      ];
    });
  };

  const handleFileChange = async (event) => {
    function bytesToKb(bytes) {
      return (bytes / 1024).toFixed(2) + "KB";
    }

    const list = event.target.files;
    const formData = new FormData();
    const file = list[0];
    const fileName = file.name;
    const fileSize = bytesToKb(file.size);
    setFileList([
      {
        fileName,
        fileSize,
      },
    ]);
    setIsLoading(true);
    if (fileName?.split(".")[1] !== "xlsx") {
      message.error({
        content: "抱歉，只能上传excel文件！",
        icon: <i className="customer-error-message-icon"></i>,
        className: "customer-error-message",
      });
      setFileList([]);
      setFileId("");
      setIsLoading(false);
      return;
    }
    formData.append("file", file);
    if (list.length > 1) {
      message.error({
        content: "抱歉，只能上传一个文件！",
        icon: <i className="customer-error-message-icon"></i>,
        className: "customer-error-message",
      });
      setFileList([]);
      setFileId("");
      setIsLoading(false);
      return;
    }

    axios({
      method: "POST",
      headers: { "content-type": "multipart/form-data" },
      url:
        selectOption.current === "容量评估"
          ? "revenueEstGuo/uploadExcel"
          : "revenueEst/uploadExcel",
      data: formData,
    }).then((res) => {
      if (res.data.code === 500) {
        message.error({
          content: "文件解析失败，请重新上传！",
          icon: <i className="customer-error-message-icon"></i>,
          className: "customer-error-message",
        });
        if (fileInputRef && fileInputRef?.current) {
          fileInputRef.current.value = null;
        }
        setFileList([]);
        setFileId("");
        setIsLoading(false);
        return;
      }
      setFileId(res.data.data);
      setRightFileList([
        ...rightFileList,
        {
          fileName,
          fileSize,
        },
      ]);
      setIsLoading(false);
    });
  };

  const handleUpload = () => {
    fileInputRef?.current?.click();
  };

  useEffect(() => {
    const scroll = document.getElementById("scroll-wrap");
    // 设置滚动的顶点坐标为滚动的总高度
    scroll.scrollTop = scroll.scrollHeight;
  }, [scrollList]);

  return (
    <>
      <div className="profit-forecast-page">
        <div className="profit-forecast-page-header">
          <span
            className="profit-forecast-page-header-title"
            onClick={() => {
              setIsProfitPage(false);
              props.isExit.current = true;
            }}
          >
            AI虚拟电厂Copilot
          </span>
        </div>
        <div className="profit-chat-page-content">
          <div className="scroll-wrap1" id="scroll-wrap">
            <div
              className="scroll-content"
              style={{ width: "947px", margin: "0 auto" }}
            >
              {scrollList.map((item, i) => {
                return (
                  <div
                    style={
                      i === scrollList.length - 1
                        ? {}
                        : { pointerEvents: "none" }
                    }
                  >
                    {item.dom}
                  </div>
                );
              })}
            </div>
          </div>
          <div className="profit-btn-bottom-wrap">
            {status === "init" && (radioValue === 1 || radioValue === 2) ? (
              <div className="all-init-wrap">
                {radioValue === 1 && fileList.length ? (
                  <>
                    <div className="file-container">
                      {fileList.map((item) => {
                        return (
                          <div className="file-item">
                            <div className="file-item-left">
                              <div className="file-icon"></div>
                              <div className="file-info-container">
                                <span className="file-name">
                                  {item.fileName}
                                </span>
                                {!isLoading ? (
                                  <span className="file-size">
                                    {item.fileSize}
                                  </span>
                                ) : (
                                  <div className="file-loading">
                                    <i className="file-loading-icon" />
                                    <span className="file-loading-text">
                                      解析中...
                                    </span>
                                  </div>
                                )}
                              </div>
                            </div>
                            <div
                              className="delete-icon"
                              onClick={() => {
                                if (fileInputRef && fileInputRef?.current) {
                                  fileInputRef.current.value = null;
                                }
                                setFileList([]);
                                setFileId("");
                              }}
                            ></div>
                          </div>
                        );
                      })}
                    </div>
                  </>
                ) : null}
                <div className="profit-init-wrap">
                  {radioValue === 1 ? (
                    <div className="add-project-input">
                      <div style={{ height: "40%" }}>
                        帮我测算一个新项目，项目名称是：
                        <Input
                          value={projectName}
                          onChange={(e) => setProjectName(e.target.value)}
                          style={{
                            width: "121px",
                            borderBottom: "1px solid #ffffff",
                            height: "22px",
                            borderRadius: "0",
                          }}
                          placeholder="输入项目名称"
                          variant="borderless"
                        />
                        ， 所在地区：
                        <Cascader
                          options={cityOptions}
                          value={selectedCity}
                          onChange={onChange}
                          className="city-cascader"
                          style={{ width: 100, height: "22px" }}
                          placeholder="选择地区"
                        />
                        ，
                        {selectOption.current === "收益测算" ? (
                          <>
                            储能容量：
                            <Input
                              value={volume}
                              onChange={(e) =>
                                setVolume(
                                  e.target.value.replace(/[^\-?\d.]/g, "")
                                )
                              }
                              type="tel"
                              style={{
                                width: "85px",
                                borderBottom: "1px solid #ffffff",
                                height: "22px",
                                borderRadius: "0",
                              }}
                              placeholder="输入容量"
                              variant="borderless"
                            />
                            kWh
                          </>
                        ) : (
                          <>
                            <span>用电类型:</span>
                            <Cascader
                              className="type-cascader"
                              options={typeOptions}
                              value={electricType}
                              disabled={!selectedCity}
                              placeholder="请选择用电类型"
                              allowClear={false}
                              onChange={async (value) => {
                                setElectricType(`${value[0]}_${value[1]}`);
                                initVoltageLevel(
                                  selectedCity || "",
                                  value[0],
                                  value[1]
                                );
                              }}
                            />
                          </>
                        )}
                        ，
                      </div>
                      <div style={{ height: "40%" }}>
                        {selectOption.current === "收益测算" ? (
                          <>
                            储能功率：
                            <Input
                              type="tel"
                              value={power}
                              onChange={(e) =>
                                setPower(
                                  e.target.value.replace(/[^\-?\d.]/g, "")
                                )
                              }
                              style={{
                                width: "85px",
                                borderBottom: "1px solid #ffffff",
                                height: "22px",
                                borderRadius: "0",
                              }}
                              placeholder="输入功率"
                              variant="borderless"
                            />
                            kW
                          </>
                        ) : (
                          <>
                            <span>电压等级:</span>
                            <Cascader
                              className="type-cascader"
                              options={voltageLevelOptions}
                              value={voltageLevel}
                              disabled={!selectedCity || !electricType}
                              placeholder="请选择电压等级"
                              allowClear={false}
                              onChange={(value) => {
                                if (value[1]) {
                                  setVoltageLevel(`${value[0]}_${value[1]}`);
                                } else {
                                  setVoltageLevel(`${value[0]}`);
                                }
                              }}
                            />
                          </>
                        )}
                      </div>
                      <i
                        className="send_icon"
                        onClick={onSend}
                        style={
                          (selectOption.current === "收益测算" &&
                            selectedCity &&
                            projectName &&
                            power &&
                            volume &&
                            !isLoading) ||
                          (selectOption.current === "容量评估" &&
                            selectedCity &&
                            projectName &&
                            electricType &&
                            voltageLevel &&
                            !isLoading)
                            ? {}
                            : { opacity: "0.5", pointerEvents: "none" }
                        }
                      />
                      <Tooltip
                        placement="top"
                        title={"支持上传文件xlsx格式（限100MB）"}
                        overlayClassName="customer-Tooltip"
                      >
                        <i
                          className="upload_icon"
                          onClick={handleUpload}
                          style={
                            fileList.length
                              ? { opacity: "0.5", pointerEvents: "none" }
                              : {}
                          }
                        />
                      </Tooltip>
                    </div>
                  ) : null}
                  {radioValue === 2 ? (
                    <div className="look-project-input">
                      我想要查看
                      <Select
                        showSearch
                        placeholder="可选择项目"
                        filterOption={(input, option) => {
                          return (option?.label ?? "").includes(input);
                        }}
                        options={projectOption.map((item) => {
                          return {
                            label: item?.name,
                            value: item?.id,
                          };
                        })}
                        onChange={(val, options) => {
                          setSelectProject({
                            id: options.value,
                            name: options.label,
                          });
                          setProjectName(options.label);
                        }}
                        style={{ width: 180, margin: "0 10px" }}
                        className="init-select-option"
                      />
                      项目的
                      {selectOption.current === "收益测算"
                        ? "收益测算"
                        : "容量"}
                      评估
                      <i
                        className="send_icon"
                        onClick={onSend}
                        style={
                          !selectProject?.id
                            ? { opacity: "0.5", pointerEvents: "none" }
                            : {}
                        }
                      />
                    </div>
                  ) : null}
                </div>
                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleFileChange}
                  style={{ display: "none" }}
                  max={30}
                />
              </div>
            ) : null}
            {status === "look" ? (
              <div
                className="profit-look-wrap"
                style={isOneCanCLick ? {} : { pointerEvents: "none" }}
              >
                <div
                  className="reset-strategy-btn"
                  onClick={async () => {
                    reset();
                  }}
                >
                  重新选择
                </div>
                {selectOption.current === "收益测算" ? (
                  <div
                    className="reset-strategy-btn"
                    onClick={async () => {
                      const res = await http.post(
                        "/revenueEst/updateProject ",
                        {
                          projectId: selectProject?.id,
                          baseInfo: updateBaseInfo,
                        }
                      );
                      edit();
                    }}
                  >
                    编辑高级配置
                  </div>
                ) : null}
                <div
                  className="generate-strategy-btn"
                  style={
                    selectOption.current === "容量评估" && noDemandFile
                      ? { pointerEvents: "none" }
                      : {}
                  }
                  onClick={async () => {
                    if (selectOption.current === "容量评估") {
                      const res = await http.post(
                        "/revenueEstGuo/generateReportProject",
                        {
                          projectId: selectProject?.id,
                          capacityRangeLow: capacityRangeLow,
                          capacityRangeUp: capacityRangeUp,
                          countingPeriod: countingPeriod,
                          irrMin: irrMin,
                        }
                      );

                      if (res.data.code === 400) {
                        setScrollList((prestate) => {
                          const list = [...prestate];
                          list.pop();
                          return [
                            ...list,
                            {
                              index: 5,
                              dom: (
                                <div className="energy-left-default-wrap">
                                  <i className="energy-left-robot-icon" />
                                  <div className="energy-left-message">
                                    <span ref={message1}></span>
                                  </div>
                                </div>
                              ),
                            },
                          ];
                        });

                        setTimeout(() => {
                          if (message1.current) {
                            typed1 = new Typed(message1?.current, {
                              strings: [res.data.msg],
                              typeSpeed: 10,
                              backSpeed: 50,
                              showCursor: true,
                              cursorChar: "",
                              onComplete(self) {
                                self.cursor.style.display = "none"; // 隐藏光标
                                setStatus("init");
                                setRadioValue(null);
                                setFileList([]);
                                setFileId("");
                                setProjectName("");
                                setPower("");
                                setVolume("");
                                setSelectedCity("");
                                setScrollList((prestate) => {
                                  return [
                                    ...prestate,
                                    {
                                      index: 0,
                                      dom: (
                                        <SelectWrap
                                          doInit={doInit}
                                          setRadioValue={setRadioValue}
                                        />
                                      ),
                                    },
                                  ];
                                });
                              },
                            });
                          }
                        }, 300);
                        return;
                      }
                      setIsThreeCanCLick(false);
                      gdtGnerateEnd(res);
                    } else {
                      const res = await http.post("/revenueEst/updateProject", {
                        projectId: selectProject?.id,
                        baseInfo: updateBaseInfo,
                      });
                      await http.post("/revenueEst/generateReport", {
                        projectId: selectProject?.id,
                      });
                      setIsThreeCanCLick(false);
                      doGenerate();
                      if (radioValue === 1) {
                        generate();
                      } else {
                        generate2();
                      }
                    }
                  }}
                >
                  {selectOption.current === "容量评估"
                    ? "开始评估"
                    : "生成报告"}
                </div>
              </div>
            ) : null}
            {status === "edit" ? (
              <div
                className="profit-edit-wrap"
                style={isTwoCanCLick ? {} : { pointerEvents: "none" }}
              >
                <div
                  className="generate-strategy-btn"
                  onClick={async () => {
                    const res = await http.post("/revenueEst/updateProject", {
                      projectId: selectProject?.id,
                      baseInfo: updateAllInfo,
                    });
                    await http.post("/revenueEst/generateReport", {
                      projectId: selectProject?.id,
                    });
                    setIsTwoCanCLick(false);
                    doGenerate();
                    if (radioValue === 1) {
                      generate();
                    } else {
                      generate2();
                    }
                  }}
                >
                  {selectOption.current === "容量评估"
                    ? "开始评估"
                    : "生成报告"}
                </div>
              </div>
            ) : null}
            {status === "end" ? (
              <div
                className="profit-edit-wrap"
                style={isThreeCanCLick ? {} : { pointerEvents: "none" }}
              >
                <div className="generate-strategy-btn" onClick={() => reset()}>
                  重新选择
                </div>
              </div>
            ) : null}
          </div>
        </div>
      </div>
    </>
  );
};
