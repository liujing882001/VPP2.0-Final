import "./index.scss";
import { Chart } from "../chart";
import classNames from "classnames";
import { useEffect, useState } from "react";
import { Popover, Input, message, Button } from "antd";
import axios from "axios";

export const GenerateReport = (props) => {
  const { data, projectId, projectName, selectOption, gdtData, demandArr } =
    props;
  const n = `table-${new Date().getTime()}`;
  const [year, setYear] = useState("");

  useEffect(() => {
    if (selectOption !== "容量评估") {
      const tableData = [
        {
          name: "",
          value: [],
        },
        {
          name: "现金流入（万元）",
          value: ["", ...data?.cashInflow],
        },
        {
          name: "资产方收入（万元）",
          value: ["", ...data?.assetIncomeAfterShare],
        },
        {
          name: "现金流出（万元）",
          value: ["", ...data?.cashOutflow],
        },
        {
          name: "建设投资（万元）",
          value: ["", ...data?.constInvest],
        },
        {
          name: "经营成本（万元）",
          value: ["", ...data?.operatingCost],
        },
        {
          name: "增值税（万元）",
          value: ["", ...data?.vat],
        },
        {
          name: "净现金流量（万元）",
          value: ["", ...data?.netCashFlowBeforeTax],
        },
      ];

      const rows = tableData?.length;
      const cols = tableData[1]?.value?.length;
      setYear(data?.cashInflow?.length - 1);
      if (tableData?.length) {
        // 创建表格元素
        var table = document.createElement("table");
        // 根据行数和列数循环生成表格内容
        for (var i = 0; i < rows; i++) {
          // 创建表格行元素
          var tr = document.createElement("tr");
          tr.classList.add(`tr-${i}`);
          for (var j = 0; j < cols; j++) {
            // 创建表格列元素
            var td = document.createElement("td");
            if (i === 0 && j === 0) {
              td.style =
                "width: 160px;white-space: nowrap;word-wrap: break-word;word-break: break-all";
              // td.textContent = ''
            } else if (j === 0) {
              td.style =
                "width: 160px;white-space: nowrap;word-wrap: break-word;word-break: break-all";
              td.textContent = tableData[i]?.name;
            } else if (j === 1 && i === 0) {
              td.textContent = "初始";
            } else if (i === 0 && j >= 2) {
              td.textContent = `第${j - 1}年`;
            } else {
              td.textContent = tableData[i].value[j];
            }
            tr.appendChild(td);
          }
          // 将行元素添加到表格中
          table.appendChild(tr);
        }
        // 将生成的表格添加到页面中

        document.getElementById(n).appendChild(table);
      }
    }
  }, []);

  const exportUpdate = () => {
    axios({
      method: "get",
      params: {
        projectId: projectId,
      },
      url: "revenueEst/exportExcel",
      responseType: "blob",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (response.status === 200) {
          const contentDisposition = response.headers["content-disposition"];
          let filename = "收益报告_" + projectName + ".xlsx";
          if (contentDisposition) {
            const filenameMatch =
              contentDisposition.match(/filename="?(.+?)"?;/);
            if (filenameMatch.length > 1) {
              filename = filenameMatch[1];
            }
          }

          const blobUrl = window.URL.createObjectURL(new Blob([response.data]));
          const downloadLink = document.createElement("a");
          downloadLink.href = blobUrl;
          downloadLink.setAttribute("download", filename);
          document.body.appendChild(downloadLink);
          downloadLink.click();
          document.body.removeChild(downloadLink);
          window.URL.revokeObjectURL(blobUrl);
        } else {
          message.info("错误");
        }
      })
      .catch((error) => {
        console.error("下载文件时发生错误:", error);
      });
  };

  return (
    <>
      {selectOption === "容量评估" ? (
        <div className="edit-project-wrap">
          <i className="robot-icon" />
          <div className="edit-project-first" style={{ marginLeft: "70px" }}>
            <div className="edit-project-header">
              <span className="edit-project-header-title">
                好的，评估结果已产生
              </span>
            </div>
            {/* <div
              className="generate-project-content"
              style={{ padding: "16px 24px 26px 24px" }}
            >
              <div className="generate-project-top">
                <div className="generate-project-top-left">
                  <div className="generate-project-top-left-title">
                    参数配置建议
                  </div>
                  <div className="assessment-content">
                    <div
                      className="assessment-content-item"
                      style={{ marginRight: "30px" }}
                    >
                      <div className="assessment-content-item-top">
                        储能功率
                      </div>
                      <div className="assessment-content-item-bottom">
                        {gdtData?.power}
                        <i>kW</i>
                      </div>
                    </div>
                    <div className="assessment-content-item">
                      <div className="assessment-content-item-top">
                        储能容量
                      </div>
                      <div className="assessment-content-item-bottom">
                        {gdtData?.capacity}
                        <i>kW</i>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="generate-project-top-right">
                  <div className="generate-project-top-right-title">
                    方案对应收益
                  </div>
                  <div className="assessment-content">
                    <div
                      className="assessment-content-item"
                      style={{ marginRight: "40px" }}
                    >
                      <div className="assessment-content-item-top">
                        全投资IRR（税前）
                      </div>
                      <div className="assessment-content-item-bottom">
                        {gdtData?.irr}
                        <i>万元</i>
                      </div>
                    </div>
                    <div className="assessment-content-item">
                      <div className="assessment-content-item-top">
                        全投资收益总额（税前）
                      </div>
                      <div className="assessment-content-item-bottom">
                        {gdtData?.totalRevenue}
                        <i>万元</i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div> */}
            <div className="end-table" style={{ marginBottom: "30px" }}>
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
          </div>
        </div>
      ) : (
        <div className="edit-project-wrap">
          <i className="robot-icon" />
          <div className="edit-project-first" style={{ marginLeft: "70px" }}>
            <div className="edit-project-header">
              <span className="edit-project-header-title">
                好的，已生成最新的储能收益测算报告
              </span>
              <Button
                type="primary"
                className="export_update"
                onClick={exportUpdate}
              >
                导出
              </Button>
            </div>
            <div className="generate-project-content">
              <div className="generate-project-top">
                <div className="generate-project-top-left">
                  <div className="generate-project-top-left-title">
                    储能IRR（税前）
                  </div>
                  <div className="generate-project-top-left-content">
                    <span style={{ position: "relative", top: "5px" }}>
                      {data?.storageIRRPreTax}
                    </span>
                    <small>%</small>
                  </div>
                </div>
                <div className="generate-project-top-right">
                  <div className="generate-project-top-right-title">
                    储能投资回报周期
                  </div>
                  <div className="generate-project-top-right-content">
                    {data?.storagePayback}年
                  </div>
                </div>
              </div>
              <div className="generate-project-center-1">
                <div className="generate-project-center-1-item">
                  <span className="generate-project-center-1-item-title">
                    储能总收入<small>(万元)</small>
                  </span>
                  <span className="generate-project-center-1-item-number">
                    {data?.storageTotalRev}
                  </span>
                </div>
                <div className="generate-project-center-1-item">
                  <span className="generate-project-center-1-item-title">
                    储能总成本<small>(万元)</small>
                  </span>
                  <span className="generate-project-center-1-item-number">
                    {data?.storageTotalCost}
                  </span>
                </div>
                <div className="generate-project-center-1-item">
                  <span className="generate-project-center-1-item-title">
                    储能利润总额<small>(万元)</small>
                  </span>
                  <span className="generate-project-center-1-item-number">
                    {data?.storageTotalProfit}
                  </span>
                </div>
              </div>
              <div className="generate-project-center-2">
                <div className="investors">
                  <div className="investors-title">投资方</div>
                  <div className="investors-content">
                    <div className="investors-content-item">
                      <span className="investors-content-item-title">
                        {year}年总收益(万元)
                      </span>
                      <span className="investors-content-item-content">
                        {data?.invTotalRevXYears}
                      </span>
                    </div>
                    <div className="investors-content-item">
                      <span className="investors-content-item-title">
                        平均年收益(万元)
                      </span>
                      <span className="investors-content-item-content">
                        {data?.invAvgAnnualRev}
                      </span>
                    </div>
                  </div>
                </div>
                <div className="electricity-users">
                  <div className="electricity-users-title">电力用户</div>
                  <div className="electricity-users-content">
                    <div className="electricity-users-content-item">
                      <span className="electricity-users-content-item-title">
                        {year}年总收益(万元)
                      </span>
                      <span className="electricity-users-content-item-content">
                        {data?.usrTotalRevXYears}
                      </span>
                    </div>
                    <div className="electricity-users-content-item">
                      <span className="electricity-users-content-item-title">
                        平均年收益(万元)
                      </span>
                      <span className="electricity-users-content-item-content">
                        {data?.usrAvgAnnualRev}
                      </span>
                    </div>
                    <div className="electricity-users-content-item">
                      <span className="electricity-users-content-item-title">
                        分成比例(%){" "}
                      </span>
                      <span className="electricity-users-content-item-content">
                        {data?.customerShare}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              <div className="cash-table-wrapper">
                <div className="cash-table-title-wrapper">
                  <span className="cash-table-title">现金流量表</span>
                </div>
                <div id={n} className="table-div"></div>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};
