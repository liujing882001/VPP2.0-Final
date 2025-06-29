import { useEffect, useRef, useState } from "react";
import { useHistory } from "react-router-dom";
import "./index.scss";
import { Spin } from "antd";
import http from "../../../../server/server";

export const MainPage = (props) => {
  const [demand, setDemand] = useState({});
  const [energy, setEnergy] = useState({});
  const [trade, setTrade] = useState({});
  const [profitForecast, setProfitForecast] = useState({});
  const [profitManage, setProfitManage] = useState({});
  let history = useHistory();
  const [total, setTotal] = useState("");
  const [totalNode, setTotalNode] = useState("");
  const [totalTradeNode, setTotalTradeNode] = useState("");
  const [generateNode, setGenerateNode] = useState("");
  const [profitForecastNum, setProfitForecastNum] = useState("");
  const [profitManageNum, setProfitManageNum] = useState("");
  const [spinning, setsSpinning] = useState(true);
  const {
    setIsDemandPage,
    setIsEnergyPage,
    setIsTradePage,
    setIsProfitPage,
    setIsProfitManPage,
  } = props;
  const [isShowDemandChatPage, setIsShowDemandChatPage] = useState(false);
  const [isShowEnergyChatPage, setIsShowEnergyChatPage] = useState(false);
  const [isShowPowerTradingPage, setIsShowPowerTradingPage] = useState(false);
  const [isProfitForecastPage, setIsProfitForecastPage] = useState(false);
  const [isProfitManagePage, setIsProfitManagePage] = useState(false);

  let coplitNum = useRef(0);
  useEffect(() => {
    let userId = sessionStorage.getItem("userid");
    if (userId) {
      http.get("/vpp/getCopilotPermission?userId=" + userId).then((res) => {
        res?.data?.data?.forEach((item) => {
          if (item?.menuNameEn === "main_copilot") {
            item?.children?.forEach((item) => {
              if (item?.menuNameEn === "demand_response_copilot") {
                coplitNum.current = coplitNum.current + 1;
                setDemand({ name: item?.menuName, icon: item?.iconLink });
                setIsShowDemandChatPage(true);
              } else if (item?.menuNameEn === "spot_trading_copilot") {
                coplitNum.current = coplitNum.current + 1;
                setIsShowPowerTradingPage(true);
                setTrade({ name: item?.menuName, icon: item?.iconLink });
              } else if (item?.menuNameEn === "resource_scheduling_copilot") {
                coplitNum.current = coplitNum.current + 1;
                setIsShowEnergyChatPage(true);
                setEnergy({ name: item?.menuName, icon: item?.iconLink });
              } else if (item?.menuNameEn === "profit_forecast_copilot") {
                coplitNum.current = coplitNum.current + 1;
                setProfitForecast({
                  name: item?.menuName,
                  icon: item?.iconLink,
                });
                setIsProfitForecastPage(true);
              } else if (item?.menuNameEn === "revenue_management_copilot") {
                coplitNum.current = coplitNum.current + 1;
                setProfitManage({
                  name: item?.menuName,
                  icon: item?.iconLink,
                });
                setIsProfitManagePage(true);
              }
            });
          }
        });
      });
    }

    Promise.all([
      http.post("/AIEnergy/getTaskState"),
      http.post(
        "/system_management/energy_model/energy_storage_model/findEnergyStorageCopilotNode"
      ),
      http.get("/electricityTrading/getTaskCount"),
      http.get("/revenueEst/getProjectCount"),
      http.get("/revenueManage/getProjectCount"),
    ])
      .then((res) => {
        const [res1, res2, res3, res4, res5] = res || [];
        const StorageCopilotList = res2.data.data;
        setTotalTradeNode(res3?.data?.data);
        setProfitForecastNum(res4.data.data);
        setProfitManageNum(res5.data.data);
        StorageCopilotList.map((item) => {
          if (item.nodeType === "总节点数") {
            setTotalNode(item.cnt);
          } else if (item.nodeType === "待生成节点数") {
            setGenerateNode(item.cnt);
          }
        });
        setTotal(res1?.data?.data?.totalElements);
        setsSpinning(false);
      })
      .catch(() => {
        setsSpinning(false);
      });
    setsSpinning(false);
  }, []);
  // console.log(coplitNum.current);
  return (
    <Spin spinning={spinning} wrapperClassName="spinning-wrap">
      <div className="assist-mode-main-page">
        <div className="assist-mode-main-title">
          <i className="robot-icon" />
          <p>AI虚拟电厂Copilot</p>
        </div>
        <div className="assist-mode-main-content-wrap">
          {isShowDemandChatPage ? (
            <div
              className="assist-mode-main-content-item"
              onClick={() => {
                setIsDemandPage(true);
                http.put(
                  `/applicationCenter/addApplicationLog?name=${demand?.name}`
                );
              }}
            >
              <p className="assist-mode-main-content-title">负荷类虚拟电厂</p>
              <div className="assist-mode-main-content-content one">
                <div className="assist-mode-main-content-content-left">
                  <img src={demand?.icon} alt="" />
                  <div className="assist-mode-main-content-left">
                    <div className="assist-mode-main-content-content-title">
                      {demand?.name}
                    </div>
                    <div className="assist-mode-main-content-content-subtitle">
                      能快速帮助您分析需求响应任务、生成策略和生成申报结果
                    </div>
                  </div>
                </div>
                <div className="assist-mode-main-content-content-right">
                  <p className="assist-mode-main-content-content-number">
                    {total}
                  </p>
                  <p className="assist-mode-main-content-content-status">
                    待申报
                  </p>
                </div>
              </div>
            </div>
          ) : null}
          {isShowEnergyChatPage ? (
            <div
              className="assist-mode-main-content-item"
              onClick={() => {
                setIsEnergyPage(true);
                http.put(
                  `/applicationCenter/addApplicationLog?name=${energy?.name}`
                );
              }}
            >
              <p className="assist-mode-main-content-title">
                <span>资源类虚拟电厂</span>
                <div className="auto-wrap">
                  <i className="auto-icon" />
                  <p className="auto-text">自动运行中</p>
                </div>
              </p>

              <div className="assist-mode-main-content-content two">
                <div className="assist-mode-main-content-content-left">
                  <img src={energy?.icon} alt="" />
                  <div className="assist-mode-main-content-left">
                    <div className="assist-mode-main-content-content-title">
                      {energy?.name}
                    </div>
                    <div className="assist-mode-main-content-content-subtitle">
                      能帮您进行源网荷储能量块预测分析，制定最佳调度策略
                    </div>
                  </div>
                </div>
                <div className="assist-mode-main-content-content-right">
                  <p className="assist-mode-main-content-content-number">
                    {totalNode}
                  </p>
                  <p className="assist-mode-main-content-content-status">
                    总节点
                  </p>
                </div>
              </div>
            </div>
          ) : null}
          {isShowPowerTradingPage ? (
            <div
              className="assist-mode-main-content-item"
              onClick={() => {
                setIsTradePage(true);
                http.put(
                  `/applicationCenter/addApplicationLog?name=${trade?.name}`
                );
              }}
            >
              <p className="assist-mode-main-content-title">电力市场化交易</p>
              <div className="assist-mode-main-content-content three">
                <div className="assist-mode-main-content-content-left">
                  <img src={trade?.icon} alt="" />
                  <div className="assist-mode-main-content-left">
                    <div className="assist-mode-main-content-content-title">
                      {trade.name}
                    </div>
                    <div className="assist-mode-main-content-content-subtitle">
                      能帮您分析电力交易、生成策略和生成申报结果
                    </div>
                  </div>
                </div>
                <div className="assist-mode-main-content-content-right">
                  <p className="assist-mode-main-content-content-number">
                    {totalTradeNode}
                  </p>
                  <p className="assist-mode-main-content-content-status">
                    待申报
                  </p>
                </div>
              </div>
            </div>
          ) : null}
          {isProfitForecastPage ? (
            <div
              className="assist-mode-main-content-item"
              onClick={() => {
                setIsProfitPage(true);
                http.put(
                  `/applicationCenter/addApplicationLog?name=${profitForecast?.name}`
                );
              }}
            >
              <p className="assist-mode-main-content-title">资产管理</p>
              <div className="assist-mode-main-content-content four">
                <div className="assist-mode-main-content-content-left">
                  <img src={profitForecast?.icon} alt="" />
                  <div className="assist-mode-main-content-left">
                    <div className="assist-mode-main-content-content-title">
                      {profitForecast?.name}
                    </div>
                    <div className="assist-mode-main-content-content-subtitle">
                      能帮您对储能资源进行收益测算评估
                    </div>
                  </div>
                </div>
                <div className="assist-mode-main-content-content-right">
                  <p className="assist-mode-main-content-content-number">
                    {profitForecastNum}
                  </p>
                  <p className="assist-mode-main-content-content-status">
                    项目数
                  </p>
                </div>
              </div>
            </div>
          ) : null}
          {isProfitManagePage ? (
            <div
              className="assist-mode-main-content-item"
              onClick={() => {
                setIsProfitManPage(true);
                http.put(
                  `/applicationCenter/addApplicationLog?name=${profitManage?.name}`
                );
              }}
            >
              <p className="assist-mode-main-content-title">资产管理</p>
              <div className="assist-mode-main-content-content five">
                <div className="assist-mode-main-content-content-left">
                  <img src={profitManage?.icon} alt="" />
                  <div className="assist-mode-main-content-left">
                    <div className="assist-mode-main-content-content-title">
                      {profitManage?.name}
                    </div>
                    <div className="assist-mode-main-content-content-subtitle">
                      能帮您对项目资源进行收益分析
                    </div>
                  </div>
                </div>
                <div className="assist-mode-main-content-content-right">
                  <p className="assist-mode-main-content-content-number">
                    {profitManageNum}
                  </p>
                  <p className="assist-mode-main-content-content-status">
                    项目数
                  </p>
                </div>
              </div>
            </div>
          ) : null}

          {/* 无逻辑，仅作兼容 */}
          {coplitNum.current === 4 ? (
            <>
              <div
                className="assist-mode-main-content-item"
                // onClick={() => {
                //   setIsProfitManPage(true);
                // }}
                style={{ pointerEvents: "none", opacity: 0 }}
              >
                <p className="assist-mode-main-content-title">资产管理</p>
                <div className="assist-mode-main-content-content two">
                  <div className="assist-mode-main-content-content-left">
                    <i />
                    <div className="assist-mode-main-content-left">
                      <div className="assist-mode-main-content-content-title">
                        收益管理
                      </div>
                      <div className="assist-mode-main-content-content-subtitle">
                        能帮您对项目资源进行收益分析
                      </div>
                    </div>
                  </div>
                  <div className="assist-mode-main-content-content-right">
                    <p className="assist-mode-main-content-content-number">
                      {profitManageNum}
                    </p>
                    <p className="assist-mode-main-content-content-status">
                      项目数
                    </p>
                  </div>
                </div>
              </div>
              <div
                className="assist-mode-main-content-item"
                // onClick={() => {
                //   setIsProfitManPage(true);
                // }}
                style={{ pointerEvents: "none", opacity: 0 }}
              >
                <p className="assist-mode-main-content-title">资产管理</p>
                <div className="assist-mode-main-content-content two">
                  <div className="assist-mode-main-content-content-left">
                    <i />
                    <div className="assist-mode-main-content-left">
                      <div className="assist-mode-main-content-content-title">
                        收益管理
                      </div>
                      <div className="assist-mode-main-content-content-subtitle">
                        能帮您对项目资源进行收益分析
                      </div>
                    </div>
                  </div>
                  <div className="assist-mode-main-content-content-right">
                    <p className="assist-mode-main-content-content-number">
                      {profitManageNum}
                    </p>
                    <p className="assist-mode-main-content-content-status">
                      项目数
                    </p>
                  </div>
                </div>
              </div>
            </>
          ) : null}
          {coplitNum.current === 5 ? (
            <>
              <div
                className="assist-mode-main-content-item"
                // onClick={() => {
                //   setIsProfitManPage(true);
                // }}
                style={{ pointerEvents: "none", opacity: 0 }}
              >
                <p className="assist-mode-main-content-title">资产管理</p>
                <div className="assist-mode-main-content-content two">
                  <div className="assist-mode-main-content-content-left">
                    <i />
                    <div className="assist-mode-main-content-left">
                      <div className="assist-mode-main-content-content-title">
                        收益管理
                      </div>
                      <div className="assist-mode-main-content-content-subtitle">
                        能帮您对项目资源进行收益分析
                      </div>
                    </div>
                  </div>
                  <div className="assist-mode-main-content-content-right">
                    <p className="assist-mode-main-content-content-number">
                      {profitManageNum}
                    </p>
                    <p className="assist-mode-main-content-content-status">
                      项目数
                    </p>
                  </div>
                </div>
              </div>
            </>
          ) : null}
        </div>
      </div>
    </Spin>
  );
};
