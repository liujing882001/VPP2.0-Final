import { useEffect, useState, useRef } from "react";
import Typed from "typed.js";
import _ from "lodash";
import { Spin, Cascader, message } from "antd";
import { RecommendStrategy } from "./components/recommend-strategy";
import http from "../../../../server/server";
import { deepTraversal, getNextDay } from "../../utils";
import { Dom4 } from "../dom4";
import "./index.scss";
import classNames from "classnames";
export const EnergyChatPage = (props) => {
  const { setIsEnergyPage } = props;
  const [scrollList, setScrollList] = useState([]);
  const [treeOptions, setTreeOptions] = useState([]);
  const [cascaderValue, setCascaderValue] = useState([]);
  const [status, setStatus] = useState("init");
  const [spinning, setSpinning] = useState(true);
  const strategyList = useRef([]);
  const [tableData, setTableData] = useState([]);
  const [electricityPrice, setElectricityPrice] = useState([]);
  const [propertyTotal, setPropertyTotal] = useState({});
  const [selectedOptionsStr, setSelectedOptionsStr] = useState("");
  const [recommendList, setRecommendList] = useState([]);
  const [isTwoCanCLick, setIsTwoCanCLick] = useState(false);
  const [energyBlockList, setEnergyBlockList] = useState([]);
  const [weatherChartList, setWeatherChartList] = useState([]);
  const [isCanCLick, setIsCanCLick] = useState(true);
  const [isOneCanCLick, setIsOneCanCLick] = useState(false);
  const [currentNodeId, setCurrentNodeId] = useState("");
  const [currentSystemId, setCurrentSystemId] = useState("");
  const [noClick, setNoClick] = useState(false);
  const message1 = useRef(null);
  const message2 = useRef(null);
  const message3 = useRef(null);
  const message4 = useRef(null);
  const message6 = useRef(null);
  const [strategyStartDate, setStrategyStartDate] = useState("");
  const [strategyEndDate, setStrategyEndDate] = useState("");
  const [weatherStartDate, setWeatherStartDate] = useState("");
  const [weatherEndDate, setWeatherEndDate] = useState("");
  const [photovoltaicList, setPhotovoltaicList] = useState([]);
  const [energyList, setEnergyList] = useState([]);
  const [allList, setAllList] = useState([]);
  const tempList = useRef([]);
  const [isShowBtn, setIsShowBtn] = useState(false);
  let typed1 = "";
  let typed2 = "";
  let typed3 = "";

  useEffect(() => {
    getSystem();

    let userId = sessionStorage.getItem("userid");
    if (userId) {
      http.get("/vpp/getCopilotPermission?userId=" + userId).then((res) => {
        res?.data?.data?.forEach((item) => {
          if (item?.menuNameEn === "main_copilot") {
            item?.children?.forEach((item) => {
              if (item?.menuNameEn === "resource_scheduling_copilot") {
                item?.children?.forEach((item) => {
                  if (item?.menuName === "保存下发策略") {
                    setIsShowBtn(true);
                  }
                });
              }
            });
          }
        });
      });
    }
  }, []);

  const getSystem = async () => {
    const nodeListRes = await http.post("tree/runStorageEnergyNodeTree");
    const systemListRes = await http.post(
      "system_management/system_model/systemStorageEnergyList"
    );
    const nodeList = nodeListRes?.data?.data;
    const systemList = systemListRes?.data?.data;
    const tempSystemList = systemList.map((item) => {
      return {
        value: item?.systemKey,
        label: item?.systemName,
      };
    });
    const renderNodeListTree = (nodeList) => {
      nodeList.forEach((item) => {
        item.label = item.title;
        item.value = item.id;

        if (item?.children?.length) {
          renderNodeListTree(item.children);
        }
      });
    };
    const renderSystemListTree = (nodeList) => {
      nodeList.forEach((item) => {
        if (item?.children?.length) {
          renderSystemListTree(item.children);
        } else {
          item.children = [...tempSystemList];
          return;
        }
      });
    };
    renderNodeListTree(nodeList);
    renderSystemListTree(nodeList);
    setTreeOptions(nodeList);
    const paths = deepTraversal(nodeList);
    const lengths = paths.map((item) => item.length);
    const maxNum = Math.max(...lengths);
    const recommendList = paths.filter((item) => item.length === maxNum);
    setRecommendList(recommendList);
    setSpinning(false);
    setScrollList([
      {
        index: 0,
        dom: (
          <div className="energy-left-default-wrap">
            <i className="energy-left-robot-icon" />
            <div className="energy-left-message">
              <span ref={message1}></span>
            </div>
          </div>
        ),
      },
    ]);
    setTimeout(() => {
      if (message1.current) {
        typed1 = new Typed(message1?.current, {
          strings: ["你想要查看哪个节点的调度策略？"],
          typeSpeed: 10,
          backSpeed: 50,
          showCursor: true,
          cursorChar: "",
          onComplete(self) {
            self.cursor.style.display = "none"; // 隐藏光标
            if (!(recommendList || []).length) {
              setIsOneCanCLick(true);
            }
            setTimeout(() => {
              setScrollList((prestate) => {
                const list = [...prestate];
                list.pop();

                return [
                  ...list,
                  {
                    index: 0,
                    dom: (
                      <div className="energy-left-default-wrap">
                        <i className="energy-left-robot-icon" />
                        <div className="energy-right-wrap">
                          <div className="energy-left-message">
                            <span>你想要查看哪个节点的调度策略？</span>
                          </div>
                          {(recommendList || []).length ? (
                            <div className="recommend-strategy-wrap">
                              <RecommendStrategy
                                recommendList={recommendList}
                                setIsOneCanCLick={setIsOneCanCLick}
                                isOneCanCLick={isOneCanCLick}
                                click1={() => click1(recommendList)}
                                click2={() => click2(recommendList)}
                                click3={() => click3(recommendList)}
                                str1={recommendList[0]
                                  ?.map((ite) => ite?.value)
                                  .join("/")}
                                str2={recommendList[1]
                                  ?.map((ite) => ite?.value)
                                  .join("/")}
                                str3={recommendList[2]
                                  ?.map((ite) => ite?.value)
                                  .join("/")}
                              />
                            </div>
                          ) : null}
                        </div>
                      </div>
                    ),
                  },
                ];
              });
            }, 300);
          },
        });
      }
    }, 300);
  };

  const refreshInit = () => {
    setScrollList((prestate) => {
      const list = [...prestate];
      return [
        ...list,
        {
          index: 0,
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
          strings: ["你想要查看哪个节点的调度策略？"],
          typeSpeed: 10,
          backSpeed: 50,
          showCursor: true,
          cursorChar: "",
          onComplete(self) {
            self.cursor.style.display = "none"; // 隐藏光标
            if (!(recommendList || []).length) {
              setIsOneCanCLick(true);
            }
            setTimeout(() => {
              setScrollList((prestate) => {
                const list = [...prestate];
                list.pop();

                return [
                  ...list,
                  {
                    index: 0,
                    dom: (
                      <div className="energy-left-default-wrap">
                        <i className="energy-left-robot-icon" />
                        <div className="energy-right-wrap">
                          <div className="energy-left-message">
                            <span>你想要查看哪个节点的调度策略？</span>
                          </div>
                          {(recommendList || []).length ? (
                            <div className="recommend-strategy-wrap">
                              <RecommendStrategy
                                recommendList={recommendList}
                                setIsOneCanCLick={setIsOneCanCLick}
                                isOneCanCLick={isOneCanCLick}
                                click1={() => click1(recommendList)}
                                click2={() => click2(recommendList)}
                                click3={() => click3(recommendList)}
                                str1={recommendList[0]
                                  ?.map((ite) => ite?.value)
                                  .join("/")}
                                str2={recommendList[1]
                                  ?.map((ite) => ite?.value)
                                  .join("/")}
                                str3={recommendList[2]
                                  ?.map((ite) => ite?.value)
                                  .join("/")}
                              />
                            </div>
                          ) : null}
                        </div>
                      </div>
                    ),
                  },
                ];
              });
            }, 300);
          },
        });
      }
    }, 300);
  };

  const refreshQuery = async () => {
    const nodeListRes = await http.post("tree/runStorageEnergyNodeTree");
    const systemListRes = await http.post(
      "system_management/system_model/systemStorageEnergyList"
    );
    const nodeList = nodeListRes?.data?.data;
    const systemList = systemListRes?.data?.data;
    const tempSystemList = systemList.map((item) => {
      return {
        value: item?.systemKey,
        label: item?.systemName,
      };
    });
    const renderNodeListTree = (nodeList) => {
      nodeList.forEach((item) => {
        item.label = item.title;
        item.value = item.id;

        if (item?.children?.length) {
          renderNodeListTree(item.children);
        }
      });
    };
    const renderSystemListTree = (nodeList) => {
      nodeList.forEach((item) => {
        if (item?.children?.length) {
          renderSystemListTree(item.children);
        } else {
          item.children = [...tempSystemList];
          return;
        }
      });
    };
    renderNodeListTree(nodeList);
    renderSystemListTree(nodeList);
    setTreeOptions(nodeList);
  };

  let isClickable = true;
  const click1 = (recommendList) => {
    if (!isClickable) return;
    isClickable = false;
    setTimeout(() => {
      isClickable = true;
    }, 2000); // 2秒钟后可再次点击

    setIsCanCLick(false);
    let today = new Date();
    // today.setDate(today.getDate() + 1);
    const tomorrow = today.toISOString().split("T")[0];
    const nodeId = recommendList[0][recommendList[0]?.length - 2]?.key;
    const systemId = recommendList[0][recommendList[0]?.length - 1]?.key;
    setCurrentNodeId(nodeId);
    setCurrentSystemId(systemId);
    const strList = recommendList[0].map((ite) => ite?.value);
    setSelectedOptionsStr(strList.join("/"));
    setScrollList((prestate) => {
      const list = [...prestate];
      return [
        ...list,
        {
          index: 2,
          dom: (
            <div className="right-default-wrap">
              <div className="right-message">
                <span>我想要分析 {strList.join("/")} 调度策略</span>
              </div>
            </div>
          ),
        },
      ];
    });
    const debouncedHandleClick = _.throttle(
      () => onQuery(nodeId, systemId, tomorrow, tomorrow),
      100
    );
    debouncedHandleClick();
    setNoClick(true);
  };
  const click2 = (recommendList) => {
    if (!isClickable) return;
    isClickable = false;
    setTimeout(() => {
      isClickable = true;
    }, 2000); // 2秒钟后可再次点击

    setIsCanCLick(false);
    let today = new Date();
    // today.setDate(today.getDate() + 1);
    const tomorrow = today.toISOString().split("T")[0];
    const nodeId = recommendList[1][recommendList[1]?.length - 2]?.key;
    const systemId = recommendList[1][recommendList[1]?.length - 1]?.key;
    setCurrentNodeId(nodeId);
    setCurrentSystemId(systemId);
    const strList = recommendList[1].map((ite) => ite?.value);
    setSelectedOptionsStr(strList.join("/"));
    setScrollList((prestate) => {
      const list = [...prestate];
      return [
        ...list,
        {
          index: 2,
          dom: (
            <div className="right-default-wrap">
              <div className="right-message">
                <span>我想要分析 {strList.join("/")} 调度策略</span>
              </div>
            </div>
          ),
        },
      ];
    });

    const debouncedHandleClick = _.throttle(
      () => onQuery(nodeId, systemId, tomorrow, tomorrow),
      100
    );
    debouncedHandleClick();
    setNoClick(true);
  };
  const click3 = (recommendList) => {
    if (!isClickable) return;
    isClickable = false;
    setTimeout(() => {
      isClickable = true;
    }, 2000); // 2秒钟后可再次点击

    setIsCanCLick(false);
    let today = new Date();
    // today.setDate(today.getDate() + 1);
    const tomorrow = today.toISOString().split("T")[0];
    const nodeId = recommendList[2][recommendList[2]?.length - 2]?.key;
    const systemId = recommendList[2][recommendList[2]?.length - 1]?.key;
    setCurrentNodeId(nodeId);
    setCurrentSystemId(systemId);
    const strList = recommendList[2].map((ite) => ite?.value);
    setSelectedOptionsStr(strList.join("/"));
    setScrollList((prestate) => {
      const list = [...prestate];
      return [
        ...list,
        {
          index: 2,
          dom: (
            <div className="right-default-wrap">
              <div className="right-message">
                <span>我想要分析 {strList.join("/")} 调度策略</span>
              </div>
            </div>
          ),
        },
      ];
    });

    const debouncedHandleClick = _.throttle(
      () => onQuery(nodeId, systemId, tomorrow, tomorrow),
      100
    );
    debouncedHandleClick();
    setNoClick(true);
  };

  useEffect(() => {
    return () => {
      typed1.destroy();
    };
  }, []);

  useEffect(() => {
    const scroll = document.getElementById("energy-scroll-wrap");
    scroll.scrollTop = scroll.scrollHeight;
  }, [scrollList]);

  const onCascaderChange = (value, selectedOptions) => {
    setIsTwoCanCLick(false);
    const selectOptions = selectedOptions.map((item) => item.label);
    const str = selectOptions.join("/");
    setSelectedOptionsStr(str);
    setCascaderValue(selectOptions);
    analysisStrategy(str, value);
    setNoClick(true);
  };

  const analysisStrategy = (str, value) => {
    setScrollList([
      ...scrollList,
      {
        index: 2,
        dom: (
          <div className="right-default-wrap">
            <div className="right-message">
              <span>我想要分析 {str} 调度策略</span>
            </div>
          </div>
        ),
      },
    ]);
    let today = new Date();
    //   today.setDate(today.getDate() + 1);
    const tomorrow = today.toISOString().split("T")[0];
    const nodeId = value[value?.length - 2];
    const systemId = value[value?.length - 1];
    onQuery(nodeId, systemId, tomorrow, tomorrow);
  };

  const onQuery = async (nodeId, systemId, startDate, endDate) => {
    setCurrentNodeId(nodeId);
    setCurrentSystemId(systemId);
    setStatus("complete");
    const energyPowerRes = await http.post(
      "system_management/energy_model/energy_storage_model/findAIStorageEnergystrategy",
      {
        nodeId,
        systemId,
        startDate,
        endDate,
      }
    );
    if (energyPowerRes.data.code !== 200) {
      setScrollList((prestate) => {
        return [
          ...prestate,
          {
            index: 5,
            dom: (
              <div className="energy-left-default-wrap">
                <i className="energy-left-robot-icon" />
                <div className="energy-left-message">
                  <span ref={message4}></span>
                </div>
              </div>
            ),
          },
        ];
      });
      setTimeout(() => {
        if (message4.current) {
          typed1 = new Typed(message4?.current, {
            strings: [`该节点不支持生成,您可以选择其他节点`],
            typeSpeed: 10,
            backSpeed: 50,
            showCursor: true,
            cursorChar: "",
            onComplete(self) {
              self.cursor.style.display = "none"; // 隐藏光标
              setStatus("init");
              setCascaderValue([]);
              setNoClick(false);
              return;
            },
          });
        }
      }, 300);
      return;
    }
    setScrollList((prestate) => {
      return [
        ...prestate,
        {
          index: 3,
          dom: (
            <div className="get-strategy-wrap">
              <i className="robot-gif-icon" />
              <div
                className="get-strategy-first"
                style={{ marginLeft: "70px" }}
              >
                <div className="get-strategy-header">
                  <span className="pre-loading-text" ref={message6}></span>
                </div>
                <div className="get-strategy-content loading-icon-wrap">
                  <i className="loading-icon" />
                </div>
              </div>
            </div>
          ),
        },
      ];
    });
    setTimeout(() => {
      typed3 = new Typed(message6.current, {
        strings: ["储能资源信息、负荷预测、电价数据分析"],
        typeSpeed: 10,
        backSpeed: 50,
        showCursor: true,
        cursorChar: "",
      });
    }, 500);
    const tempDate = energyPowerRes?.data?.data.energyStorageSubViews[0]?.date;
    const temp =
      energyPowerRes?.data?.data.energyStorageSubViews[0]?.nodeChargeDischargeInfos?.map(
        (item) => {
          return {
            date: tempDate + " " + item?.time?.split("-")[0] + ":00",
            value: item.power,
          };
        }
      );
    tempList.current = temp;
    Promise.all([
      http.post(
        "system_management/energy_model/energy_storage_model/energyBlockTrendNew",
        {
          nodeId,
          systemId,
          startDate,
          endDate,
          energyFore: temp,
        }
      ),
      http.post(
        "system_management/energy_model/energy_storage_model/weatherChart",
        {
          nodeId,
          systemId,
          startDate,
          endDate,
        }
      ),
      http.post(
        "system_management/energy_model/energy_storage_model/profitNew",
        {
          nodeId,
          systemId,
          startDate,
          endDate,
        }
      ),
      http.post("system_management/energy_model/photovoltaic_model/profitNew", {
        nodeId,
        systemId,
        startDate,
        endDate,
      }),
      http.post(
        "system_management/energy_model/photovoltaic_model/profitAllNew",
        {
          nodeId,
          systemId,
          startDate,
          endDate,
        }
      ),
    ]).then(([energyBlockRes, weatherChartRes, res1, res2, res3]) => {
      setEnergyList(res1?.data?.data);
      setPhotovoltaicList(res2?.data?.data);
      setAllList(res3?.data?.data);
      setStrategyStartDate(startDate);
      setStrategyEndDate(endDate);
      setWeatherStartDate(startDate);
      setWeatherEndDate(endDate);

      const energyBlockList = energyBlockRes?.data?.data || [];
      const weatherChartList = weatherChartRes?.data?.data || [];
      setEnergyBlockList(energyBlockList);
      setWeatherChartList(weatherChartList);
      const {
        energyStorageSubViews,
        energyStoragePropertyList,
        propertyTotal,
      } = energyPowerRes?.data?.data || {};
      setTableData(energyStorageSubViews);
      setElectricityPrice(energyStoragePropertyList);
      setPropertyTotal(propertyTotal);
      getStrategy(
        energyStorageSubViews,
        energyStoragePropertyList,
        propertyTotal,
        startDate,
        endDate,
        nodeId,
        systemId,
        energyBlockList,
        weatherChartList,
        res1?.data?.data,
        res2?.data?.data,
        res3?.data?.data
      );
    });
  };

  const queryEnergyBlockList = async (nodeId, systemId, startDate, endDate) => {
    const weatherChartRes = await http.post(
      "system_management/energy_model/energy_storage_model/weatherChart",
      {
        nodeId,
        systemId,
        startDate,
        endDate,
      }
    );
    const energyBlockRes = await http.post(
      "system_management/energy_model/energy_storage_model/energyBlockTrendNew",
      {
        nodeId,
        systemId,
        startDate,
        endDate,
        energyFore: tempList.current,
      }
    );
    const weatherChartList = weatherChartRes?.data?.data || [];
    const energyBlockList = energyBlockRes?.data?.data || [];
    setEnergyBlockList(energyBlockList);
    setScrollList((prestate) => {
      const list = [...prestate];
      list.pop();

      return [
        ...list,
        {
          index: 2,
          dom: (
            <Dom4
              tableData={tableData}
              electricityPrice={electricityPrice}
              propertyTotal={propertyTotal}
              weatherStartDate={weatherStartDate}
              weatherEndDate={weatherEndDate}
              strategyStartDate={strategyStartDate}
              strategyEndDate={strategyEndDate}
              setStrategyStartDate={(val) => setStrategyStartDate(val)}
              setStrategyEndDate={(val) => setStrategyEndDate(val)}
              setWeatherStartDate={(val) => setWeatherStartDate(val)}
              setWeatherEndDate={(val) => setWeatherEndDate(val)}
              nodeId={nodeId}
              systemId={systemId}
              strategyList={strategyList}
              energyBlockList={energyBlockList}
              queryEnergyBlockList={queryEnergyBlockList}
              weatherChartList={weatherChartList}
              queryWeatherChartList={queryEnergyBlockList}
              tempList={tempList.current}
              currentNodeId={currentNodeId}
              currentSystemId={currentSystemId}
            />
          ),
        },
      ];
    });
  };

  const getStrategy = (
    energyStorageSubViews,
    energyStoragePropertyList,
    propertyTotal,
    startDate,
    endDate,
    nodeId,
    systemId,
    energyBlockList,
    weatherChartList,
    res1,
    res2,
    res3
  ) => {
    setScrollList((prestate) => {
      const list = [...prestate];
      list.pop();

      return [
        ...list,
        {
          index: 2,
          dom: (
            <Dom4
              tableData={energyStorageSubViews}
              electricityPrice={energyStoragePropertyList}
              propertyTotal={propertyTotal}
              weatherStartDate={startDate}
              weatherEndDate={endDate}
              strategyStartDate={startDate}
              strategyEndDate={endDate}
              setStrategyStartDate={(val) => setStrategyStartDate(val)}
              setStrategyEndDate={(val) => setStrategyEndDate(val)}
              setWeatherStartDate={(val) => setWeatherStartDate(val)}
              setWeatherEndDate={(val) => setWeatherEndDate(val)}
              nodeId={nodeId}
              systemId={systemId}
              strategyList={strategyList}
              energyBlockList={energyBlockList}
              queryEnergyBlockList={queryEnergyBlockList}
              weatherChartList={weatherChartList}
              queryWeatherChartList={queryEnergyBlockList}
              tempList={tempList.current}
              energyList={res1}
              photovoltaicList={res2}
              allList={res3}
            />
          ),
        },
      ];
    });
    setIsTwoCanCLick(true);
    setIsCanCLick(true);
    setNoClick(false);
  };

  const saveStrategy = () => {
    setScrollList([
      ...scrollList,
      {
        index: 3,
        dom: (
          <div className="right-default-wrap">
            <div className="right-message">
              <span>保存并下发</span>
            </div>
          </div>
        ),
      },
    ]);
    http
      .post(
        "system_management/energy_model/energy_storage_model/distributionStorageEnergyStrategy",
        {
          storageEnergyStrategyDistributionModels: !strategyList.current.length
            ? [
                {
                  nodeId: currentNodeId,
                  systemId: currentSystemId,
                },
              ]
            : strategyList.current,
          modify: strategyList.current?.length ? true : false,
        }
      )
      .then((res) => {
        if (res.data.code === 200) {
          setStatus("init");
          strategyList.current = [];
          setCascaderValue([]);
          setScrollList((prestate) => {
            return [
              ...prestate,
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
          http
            .post(
              "system_management/energy_model/energy_storage_model/distributionStorageEnergyStrategy",
              {
                storageEnergyStrategyDistributionModels: !strategyList.current
                  .length
                  ? [
                      {
                        nodeId: currentNodeId,
                        systemId: currentSystemId,
                      },
                    ]
                  : strategyList.current,
                modify: strategyList.current?.length ? true : false,
              }
            )
            .then((res) => {
              if (res.data.code === 200) {
                setStatus("init");
                strategyList.current = [];
                setCascaderValue([]);
                setScrollList((prestate) => {
                  return [
                    ...prestate,
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
                      strings: [
                        `恭喜，${selectedOptionsStr}，策略已成功下发！`,
                      ],
                      typeSpeed: 10,
                      backSpeed: 50,
                      showCursor: true,
                      cursorChar: "",
                      onComplete(self) {
                        self.cursor.style.display = "none"; // 隐藏光标
                        refreshQuery();
                      },
                    });
                  }
                }, 300);
              } else {
                strategyList.current = [];
              }
            }, 300);
        } else {
          strategyList.current = [];
        }
      });
  };

  return (
    <Spin spinning={spinning} wrapperClassName="spinning-wrap">
      <div className="energy-chat-page">
        <div
          className="energy-chat-header"
          onClick={() => {
            setIsEnergyPage(false);
          }}
        >
          <span className="energy-chat-header-title">
            AI虚拟电厂Copilot
          </span>
        </div>
        <div className="energy-chat-content">
          <div className="energy-scroll-wrap" id="energy-scroll-wrap">
            {scrollList.map((item, i) => {
              return (
                <div
                  style={
                    i === scrollList.length - 1 || !noClick
                      ? {}
                      : { pointerEvents: "none" }
                  }
                >
                  {item.dom}
                </div>
              );
            })}
          </div>
          <div className="energy-bottom-wrap">
            {status === "init" ? (
              <div
                className="energy-init-wrap"
                style={!isOneCanCLick ? { pointerEvents: "none" } : {}}
              >
                我想要分析
                <Cascader
                  style={{ width: 150 }}
                  placeholder="可选择节点"
                  options={treeOptions}
                  onChange={onCascaderChange}
                  value={cascaderValue}
                  clearIcon={null}
                />
                调度策略
              </div>
            ) : null}
            {status === "complete" ? (
              <div
                className="energy-complete-wrap"
                style={!isTwoCanCLick ? { pointerEvents: "none" } : {}}
              >
                <div
                  className={classNames("reset-strategy-btn", {
                    "no-show-btn-status": !isShowBtn,
                  })}
                  onClick={() => {
                    setCascaderValue([]);
                    setStatus("init");
                    setIsTwoCanCLick(false);
                    refreshInit();
                  }}
                >
                  重新选择节点
                </div>
                {isShowBtn ? (
                  <div className="generate-strategy-btn" onClick={saveStrategy}>
                    保存并下发
                  </div>
                ) : null}
              </div>
            ) : null}
          </div>
        </div>
      </div>
    </Spin>
  );
};
