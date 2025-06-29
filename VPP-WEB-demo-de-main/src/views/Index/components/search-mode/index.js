import { useEffect, useState } from "react";
import "./index.scss";
import { Input, Popconfirm } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import "../../../working/Energy/index.scss";
import classNames from "classnames";
import { useHistory } from "react-router-dom";
import _ from "lodash";
import http from "../../../../server/server";
export const SearchMode = (props) => {
  const [searchKey, setSearchKey] = useState("");
  const [searchArr, setSearchArr] = useState([]);
  const [allMenu, setAllMenu] = useState([]);
  const [allTabs, setAllTabs] = useState([]);
  const [currentTitle, setCurrentTitle] = useState("最近使用");
  const [renderArr, setRenderArr] = useState([]);
  const [open, setOpen] = useState(false);
  let history = useHistory();

  const onInputChange = async (event) => {
    setSearchKey(event.target.value);
    const res = await http.get(
      `/applicationCenter/queryApplication?name=${event.target.value}`
    );
    setSearchArr(res.data.data);
  };

  const init = async () => {
    const res = await http.get("/applicationCenter/queryAllApplication");
    const data = res.data.data;
    const alltabs = data.map((item) => item.applicationName);
    setAllTabs(["最近使用", "全部应用", ...alltabs]);
    setAllMenu(res.data.data);
  };

  const initRecently = async () => {
    const res = await http.get("/applicationCenter/queryApplicationLog");
    const data = res.data.data;
    setRenderArr(data);
  };

  const initOther = async (type) => {
    const res = await http.get(
      `/applicationCenter/queryApplication?type=${type}`
    );
    const data = res.data.data;
    setRenderArr(data);
  };

  useEffect(() => {
    init();
    initRecently();
  }, []);
  return (
    <>
      <div
        className={classNames("search-half", { "is-expand": props.isExpand })}
        onClick={() => {
          if (open) {
            setOpen(false);
          }
        }}
      >
        {props.isExpand ? (
          <i
            className="retract-icon"
            onClick={() => {
              props.setIsExpand(false);
            }}
          />
        ) : (
          <i
            className="expand-icon"
            onClick={() => {
              props.setIsExpand(true);
            }}
          />
        )}
        <div className="search-mode-top">
          <i className="search-mode-top-icon" />
          <div className="title">
            快速直达您想了解的<span className="sub-title">应用</span>哦！
          </div>
          <Input
            size="large"
            placeholder="快速搜索相关应用"
            className="main-input"
            suffix={<SearchOutlined />}
            onChange={_.debounce(onInputChange, 500)}
          />
          {searchKey ? null : (
            <>
              {props.isExpand ? (
                <div className="slider-bar-warp">
                  <div className="slider-bar">
                    {allTabs?.slice(0, 8)?.map((item, index) => {
                      return (
                        <div
                          className={classNames("slider-bar-item", {
                            active: item === currentTitle,
                          })}
                          onClick={() => {
                            setCurrentTitle(item);
                            if (item === "最近使用") {
                              initRecently();
                            } else if (item === "全部应用") {
                              return;
                            } else {
                              initOther(item);
                            }
                          }}
                        >
                          {item}
                        </div>
                      );
                    })}
                  </div>
                  <Popconfirm
                    overlayClassName="more-app"
                    placement="bottomLeft"
                    open={open}
                    description={
                      <>
                        {allTabs?.slice(8, allTabs.length)?.map((item) => {
                          return (
                            <div
                              className={classNames("more-app-item", {
                                active: item === currentTitle,
                              })}
                              onClick={() => {
                                setCurrentTitle(item);
                                if (item === "最近使用") {
                                  initRecently();
                                } else if (item === "全部应用") {
                                  return;
                                } else {
                                  initOther(item);
                                }
                                setOpen(false);
                              }}
                            >
                              {item}
                            </div>
                          );
                        })}
                      </>
                    }
                  >
                    <div className="more-wrap" onClick={() => setOpen(true)}>
                      <div className="white-dot"></div>
                      <div className="white-dot"></div>
                      <div className="white-dot"></div>
                    </div>
                  </Popconfirm>
                </div>
              ) : (
                <div className="default-slider-bar">
                  {allTabs.map((item, index) => {
                    return (
                      <div
                        className={classNames("slider-bar-item", {
                          active: currentTitle === item,
                        })}
                        onClick={() => {
                          setCurrentTitle(item);
                          if (item === "最近使用") {
                            initRecently();
                          } else if (item === "全部应用") {
                            return;
                          } else {
                            initOther(item);
                          }
                        }}
                      >
                        {item}
                      </div>
                    );
                  })}
                </div>
              )}
            </>
          )}
        </div>
        {searchKey ? (
          <div className="search-wrapper">
            <p className="search-key">“{searchKey}”的搜索結果</p>
            <div className="search-result">
              {searchArr?.map((ite) => {
                return (
                  <div
                    className="ite-item"
                    onClick={() => {
                      props.isSearchModebtn(false);
                      props.setIsExpand(false);
                      http.put(
                        `/applicationCenter/addApplicationLog?name=${ite?.applicationName}`
                      );
                      props.isAssistModebtn(false);
                      props.setIsDemandPage(false);
                      props.setIsEnergyPage(false);
                      props.setIsTradePage(false);
                      props.setIsProfitPage(false);
                      props.setIsProfitManagePage(false);
                      if (ite.applicationNameEn === "demand_response_copilot") {
                        props.isAssistModebtn(true);
                        props.setIsDemandPage(true);
                      } else if (
                        ite.applicationNameEn === "resource_scheduling_copilot"
                      ) {
                        props.isAssistModebtn(true);
                        props.setIsEnergyPage(true);
                      } else if (
                        ite.applicationNameEn === "spot_trading_copilot"
                      ) {
                        props.isAssistModebtn(true);
                        props.setIsTradePage(true);
                      } else if (
                        ite.applicationNameEn === "profit_forecast_copilot"
                      ) {
                        props.isAssistModebtn(true);
                        props.setIsProfitPage(true);
                      } else if (
                        ite.applicationNameEn === "revenue_management_copilot"
                      ) {
                        props.isAssistModebtn(true);
                        props.setIsProfitManagePage(true);
                      } else {
						  console.log(ite)
						  
                        if (ite?.isFrame === 0) {
							
                          window.open(ite?.link);
                        } else {
                          sessionStorage.setItem("RoteName", ite?.link);
                          history.push(ite?.link);
                        }
                      }
                    }}
                  >
                    <img
                      src={
                        ite?.icon
                          ? ite.icon
                          : require("../../assets/default.png")
                      }
                      alt=""
                      className="ite-icon"
                    />
                    <span>{ite?.applicationName}</span>
                  </div>
                );
              })}
            </div>
          </div>
        ) : (
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              width: "100%",
            }}
          >
            <div className="search-content">
              {currentTitle === "全部应用" ? (
                <>
                  {allMenu.map((item, i) => {
                    return (
                      <div
                        className={classNames("search-content-item", {
                          "is-one": i === 0,
                        })}
                      >
                        {item.applicationName ? (
                          <div
                            className="search-content-item-title"
                            style={{ paddingTop: "30px" }}
                          >
                            <img
                              src={item.icon}
                              alt=""
                              className="title-icon"
                            />
                            <span>{item.applicationName}</span>
                          </div>
                        ) : null}
                        <div className="search-content-item-content">
                          {item?.child?.map((ite) => {
                            return (
                              <div
                                className="ite-item"
                                onClick={() => {
                                  props.isSearchModebtn(false);
                                  props.setIsExpand(false);
                                  http.put(
                                    `/applicationCenter/addApplicationLog?name=${ite?.applicationName}`
                                  );
                                  props.isAssistModebtn(false);
                                  props.setIsDemandPage(false);
                                  props.setIsEnergyPage(false);
                                  props.setIsTradePage(false);
                                  props.setIsProfitPage(false);
                                  props.setIsProfitManagePage(false);

                                  if (
                                    ite.applicationNameEn ===
                                    "demand_response_copilot"
                                  ) {
                                    props.isAssistModebtn(true);
                                    props.setIsDemandPage(true);
                                  } else if (
                                    ite.applicationNameEn ===
                                    "resource_scheduling_copilot"
                                  ) {
                                    props.isAssistModebtn(true);
                                    props.setIsEnergyPage(true);
                                  } else if (
                                    ite.applicationNameEn ===
                                    "spot_trading_copilot"
                                  ) {
                                    props.isAssistModebtn(true);
                                    props.setIsTradePage(true);
                                  } else if (
                                    ite.applicationNameEn ===
                                    "profit_forecast_copilot"
                                  ) {
                                    props.isAssistModebtn(true);
                                    props.setIsProfitPage(true);
                                  } else if (
                                    ite.applicationNameEn ===
                                    "revenue_management_copilot"
                                  ) {
                                    props.isAssistModebtn(true);
                                    props.setIsProfitManagePage(true);
                                  } else {
                                    if (ite?.isFrame === 0) {
										
                                      window.open(ite?.link);
                                    } else {
                                      sessionStorage.setItem(
                                        "RoteName",
                                        ite?.link
                                      );
                                      history.push(ite?.link);
                                    }
                                  }
                                }}
                              >
                                <img
                                  src={ite.icon}
                                  alt=""
                                  className="ite-icon"
                                />
                                <span>{ite.applicationName}</span>
                              </div>
                            );
                          })}
                        </div>
                      </div>
                    );
                  })}
                </>
              ) : (
                <div className="search-content-item">
                  <div className="search-content-item-content">
                    {renderArr.map((item) => {
                      return (
                        <div
                          className="ite-item"
                          onClick={() => {
                            props.isSearchModebtn(false);
                            props.setIsExpand(false);
                            http.put(
                              `/applicationCenter/addApplicationLog?name=${item?.applicationName}`
                            );
                            props.isAssistModebtn(false);
                            props.setIsDemandPage(false);
                            props.setIsEnergyPage(false);
                            props.setIsTradePage(false);
                            props.setIsProfitPage(false);
                            props.setIsProfitManagePage(false);

                            if (
                              item.applicationNameEn ===
                              "demand_response_copilot"
                            ) {
                              props.isAssistModebtn(true);
                              props.setIsDemandPage(true);
                            } else if (
                              item.applicationNameEn ===
                              "resource_scheduling_copilot"
                            ) {
                              props.isAssistModebtn(true);
                              props.setIsEnergyPage(true);
                            } else if (
                              item.applicationNameEn === "spot_trading_copilot"
                            ) {
                              props.isAssistModebtn(true);
                              props.setIsTradePage(true);
                            } else if (
                              item.applicationNameEn ===
                              "profit_forecast_copilot"
                            ) {
                              props.isAssistModebtn(true);
                              props.setIsProfitPage(true);
                            } else if (
                              item.applicationNameEn ===
                              "revenue_management_copilot"
                            ) {
                              props.isAssistModebtn(true);
                              props.setIsProfitManagePage(true);
                            } else {
                              if (item?.isFrame === 0) {
								  console.log(item)
                                window.open(item?.link);
                              } else {
                                sessionStorage.setItem("RoteName", item?.link);
								
                                history.push(item?.link);
                              }
                            }
                          }}
                        >
                          <img
                            src={
                              item.icon
                                ? item.icon
                                : require("../../assets/default.png")
                            }
                            alt=""
                            className="ite-icon"
                          />
                          <span>{item.applicationName}</span>
                        </div>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </>
  );
};
