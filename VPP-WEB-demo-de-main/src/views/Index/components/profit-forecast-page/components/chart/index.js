import * as echarts from "echarts";
import { useEffect, useState, useRef } from "react";
import { Button, DatePicker, message } from "antd";
import http from "../../../../../../server/server";
import dayjs from "dayjs";
import axios from "axios";

import "./index.scss";
const { RangePicker } = DatePicker;

export const Chart = (props) => {
  const {
    loadData,
    baseInfo,
    setFileId,
    setFileList,
    setRightFileList,
    rightFileList,
    selectOption,
  } = props;
  const [data, setData] = useState(loadData);
  const n = `chart-${new Date().getTime()}`;
  const date = data?.map((item) => item?.date);
  // const [startDate, setStartDate] = useState(date[0]?.split(" ")[0]);
  const [startDate, setStartDate] = useState((date && date[0]) ? date[0].split(" ")[0] : '');

  const [endDate, setEndDate] = useState(date[data.length - 1]?.split(" ")[0]);
  // const [endDate, setEndDate] = useState(
  //   data && data.length > 0 ? data[data.length - 1].split(" ")[0] : ''
  // );

  const [fileName, setFileName] = useState(baseInfo.fileName);
  // const power = data.map((item) => item?.power);
  const power = (data || []).map((item) => item?.power);

  const [isLoading, setIsLoading] = useState(false);
  const fileInputRef = useRef(null);

  useEffect(() => {
    var chartDom = document.getElementById(n);
    if (chartDom) {
      var myChart = echarts.init(chartDom);
      var option;

      let base = +new Date(1988, 9, 3);
      let oneDay = 24 * 3600 * 1000;
      let data = [[base, Math.random() * 300]];
      for (let i = 1; i < 20000; i++) {
        let now = new Date((base += oneDay));
        data.push([
          +now,
          Math.round((Math.random() - 0.5) * 20 + data[i - 1][1]),
        ]);
      }
      option = {
        grid: {
          left: "10%",
          right: "5%",
        },
        color: ["#43E3E3"],
        legend: {
          inactiveColor: "rgba(174, 174, 174, 1)",
          top: 15,
          left: "45%",
          itemGap: 20,
          data: [
            {
              icon: "path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z",
              name: "负荷",
            },
          ],
          itemWidth: 15,
          itemHeight: 15,
          textStyle: {
            color: "#fff",
            fontSize: 12,
            lineHeight: 10,
            padding: [-1, 0, 0, 0],
          },
        },
        xAxis: {
          type: "category",
          axisTick: {
            alignWithLabel: true,
          },
          data: date,
          axisLabel: {
            //x坐标轴刻度标签
            show: true,
            color: "#FFF", //'#ccc'，设置标签颜色
          },
        },
        tooltip: {
          trigger: "axis",
          formatter: function (params) {
            var relVal = params[0].name;
            for (var i = 0, l = params.length; i < l; i++) {
              const l = i === 0 || i === 1 ? "kW" : " kW";
              if (isNaN(params[i].value)) {
                relVal +=
                  "<br/>" +
                  params[i].marker +
                  params[i].seriesName +
                  " : " +
                  "-" +
                  l;
              } else {
                relVal +=
                  "<br/>" +
                  params[i].marker +
                  params[i].seriesName +
                  " : " +
                  params[i].value +
                  l;
              }
            }
            return relVal;
          },
          backgroundColor: "#302F39",
          borderColor: "transparent",
          textStyle: {
            color: "#fff", // 设置 tooltip 的文字颜色为白色
          },
          axisPointer: {
            type: "cross",
            label: {
              backgroundColor: "#6a7985",
            },
          },
        },
        yAxis: {
          type: "value",
          name: "kW",
          nameTextStyle: {
            color: "#FFF",
            padding: [0, 0, 0, -30],
          },
          boundaryGap: ["20%", "20%"],
          splitNumber: 4,
          axisLabel: {
            show: true,
            color: "#DFE1E5FF", //'#ccc'
          },
          splitLine: {
            show: true,
            lineStyle: {
              type: "dashed",
              color: "#8F959E80",
            },
          },
        },
        dataZoom: [
          {
            type: "slider",
            xAxisIndex: [0],
            filterMode: "filter",
            start: 0,
            end: 100,
            showDetail: true,
            borderColor: "#fff",
            selectedDataBackground: {
              lineStyle: {
                color: "#0092FF",
              },
              areaStyle: {
                opacity: 0,
              },
            },
            moveHandleStyle: {
              color: "rgba(143, 149, 158, 1)",
            },
            emphasis: {
              moveHandleStyle: {
                color: "rgba(143, 149, 158, 1)",
              },
            },
            fillerColor: "rgba(0, 146, 255, .1)",
            brushStyle: {
              color: "rgba(0, 146, 255, .1)",
            },
          },
        ],
        series: [
          {
            name: "负荷",
            type: "line",
            smooth: true,
            lineStyle: {
              width: 2,
            },
            showSymbol: false,
            emphasis: {
              focus: "series",
            },
            areaStyle: {
              opacity: 0.5,
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: "rgba(67,227,227)",
                },
                {
                  offset: 1,
                  color: "rgba(0, 146, 255, 0)",
                },
              ]),
            },
            data: power,
          },
        ],
      };
      option && myChart.setOption(option);
    }

    const onresize = function () {
      myChart.resize();
    };

    window.addEventListener("resize", onresize, false);

    return () => {
      window.removeEventListener("resize", onresize, false);
    };
  }, [data]);

  const refresh = async (start, end) => {
    const res = await http.get(
      `/revenueEst/getProjectLoadData?id=${baseInfo.projectId}&startTime=${start} 00:00:00&endTime=${end} 23:45:00`
    );
    setData(res.data.data);
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
    formData.append("projectId", baseInfo?.projectId);

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
        selectOption === "容量评估"
          ? "revenueEstGuo/newUploadDemandExcel"
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
      setFileName(fileName);

      setFileId(res.data.data);
      setRightFileList([
        ...rightFileList,
        {
          fileName,
          fileSize,
        },
      ]);
      setTimeout(() => {
        setIsLoading(false);

        http
          .get(
            selectOption === "容量评估"
              ? `/revenueEstGuo/getProjectDetail?id=${baseInfo.projectId}`
              : `/revenueEst/getProjectDetail?id=${baseInfo.projectId}`
          )
          .then(async (res) => {
            const list = res.data.data.basicInfo.loadData;
            setData(list);
            setStartDate(list[0]?.date?.split(" ")[0]);
            setEndDate(list[list.length - 1]?.date?.split(" ")[0]);
          });
      }, 1000);
    });
  };

  const handleUpload = () => {
    fileInputRef?.current?.click();
  };

  const disabledDate = (current) => {
    return (
      current &&
      (current < dayjs(startDate ? startDate : new Date(), "YYYY-MM-DD") ||
        current > dayjs(endDate ? endDate : new Date(), "YYYY-MM-DD"))
    );
  };

  useEffect(() => {
    const scroll = document.getElementById("scroll-wrap");
    scroll.scrollTop = scroll.scrollHeight;
  }, []);

  return (
    <>
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileChange}
        style={{ display: "none" }}
        max={30}
      />
      <div className="chart-wrapper">
        <div className="chart-file-wrap">
          <div className="chart-file-wrap-left">
            <i className="chart-file-icon" />
            <span className="chart-file-text">{fileName}</span>
            {isLoading ? (
              <div className="file-loading">
                <i className="file-loading-icon" />
                <span className="file-loading-text">解析中...</span>
              </div>
            ) : null}
          </div>
          <div className="chart-file-wrap-right" onClick={handleUpload}>
            重新上传
          </div>
        </div>
        {!loadData.length ? (
          <div className="empty-container-1">
            <i className="empty-icon" />
            <span className="empty-text">暂无数据</span>
          </div>
        ) : (
          <>
            <RangePicker
              disabledDate={disabledDate}
              className="chart-date-picker"
              defaultValue={[
                dayjs(startDate ? startDate : new Date(), "YYYY-MM-DD"),
                dayjs(endDate ? endDate : new Date(), "YYYY-MM-DD"),
              ]}
              onChange={(dates, dateStrings) => {
                refresh(dateStrings[0], dateStrings[1]);
              }}
            />
            <div
              id={n}
              style={{ width: "100%", height: "280px", flex: "1" }}
            ></div>
          </>
        )}
      </div>
    </>
  );
};
