import React, { useEffect, useState, useRef } from "react";
import "./index.scss";
import * as echarts from "echarts";
import http from "../../../../server/server.js";
import { Spin } from "antd";

export const Energyblock = ({
  nodeId,
  startTs,
  endTs,
  energyval,
  isoperation,
}) => {
  const [timeStamp, setTimeStamp] = useState("");
  const [spinning, setSpinning] = useState(false);
  const divRef = useRef(null);
  useEffect(() => {
    if (energyval && startTs && endTs && isoperation) {
      energyBlockTrend();
    }
    console.log(energyval);
  }, [energyval, startTs, endTs, isoperation]);
  useEffect(() => {
    if (!isoperation) {
      setSpinning(false);
      line([], [], [], [], [], [], [], [], "", false, []);
    }
  }, [isoperation]);
  const energyBlockTrend = () => {
    setSpinning(true);
    http
      .post("homePage/energyBlockTrend", {
        endDate: endTs,
        nodeId: energyval.toString(),
        startDate: startTs,
        systemId: "nengyuanzongbiao",
      })
      .then((res) => {
        console.log(res);
        if (res.data.code == 200) {
          let data = res.data.data;
          setSpinning(false);
          if (!data?.length) {
            line([], [], [], [], [], [], [], [], "", false, []);
            return;
          }
          const loadList = data.filter((item) => item.type === "load");
          const photovoltaicList = data.filter((item) => item.type === "pv");
          const energyList = data.filter((item) => item.type === "energy");
          const socList = data.filter((item) => item.type === "soc");
          let colors = [];

          const photovoltaicOption = photovoltaicList.map((item, i) => {
            const renderColor4 = [
              "#FFFEA6",
              "#FFFB00",
              "#FACF05",
              "#FAAD14",
              "#FFAD92",
              "#FF7002",
            ];
            colors.push(renderColor4[i]);
            if (item?.name?.includes("实际")) {
              return {
                icon: "path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z",
                name: `${item.name}`,
              };
            } else {
              return {
                icon: "path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z",
                name: `${item.name}`,
              };
            }
          });

          const loadOption = loadList.map((item, i) => {
            const renderColor2 = [
              "#03C5B3",
              "#47FFFF",
              "#8EDFFF",
              "#1586FF",
              "#00B7FF",
              "#014CE2",
            ];
            colors.push(renderColor2[i]);
            if (item?.name?.includes("实际")) {
              return {
                icon: "path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z",
                name: `${item.name}`,
              };
            } else {
              return {
                icon: "path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z",
                name: `${item.name}`,
              };
            }
          });
          const energyOption = energyList.map((item, i) => {
            const renderColor3 = [
              "#ACFDB9",
              "#6CFF5F",
              "#15FF00",
              "#00FF95",
              "#00BC6E",
              "#51FFD6",
            ];
            colors.push(renderColor3[i]);
            if (item?.name?.includes("实际")) {
              return {
                icon: "path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z",
                name: `${item.name}`,
              };
            } else {
              return {
                icon: "path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z",
                name: `${item.name}`,
              };
            }
          });

          const socOption = socList.map((item, i) => {
            const renderColor1 = ["#AB98FF", "#6846FF", "#DFBCFF", "#7C3BFF"];
            colors.push(renderColor1[i]);
            return {
              icon: "path://M1281.997965 588.427164a471.731873 471.731873 0 0 1-913.759491 0Q360.930284 589.370157 353.622094 589.370157H117.874031q-11.551655 0-22.985436-2.357481-11.433781-2.121733-22.160318-6.600946-10.608663-4.479213-20.392207-10.962285-9.547797-6.365198-17.798979-14.616379-8.251182-8.251182-14.61638-17.798979-6.483072-9.665671-10.962285-20.392208-4.479213-10.726537-6.718819-22.160318Q0 483.04778 0 471.496125t2.357481-22.985436q2.121733-11.433781 6.600945-22.160318 4.479213-10.608663 10.962285-20.392207 6.365198-9.547797 14.61638-17.798979 8.251182-8.251182 17.798979-14.61638 9.665671-6.483072 20.392207-10.962285 10.726537-4.479213 22.160318-6.718819Q106.322376 353.622094 117.874031 353.622094h235.748063q7.30819 0 14.61638 0.942992a471.731873 471.731873 0 0 1 913.759491 0Q1289.306155 353.622094 1296.614345 353.622094h235.748062q11.551655 0 22.985437 2.357481 11.433781 2.121733 22.160317 6.600945 10.608663 4.479213 20.392208 10.962285 9.665671 6.365198 17.798979 14.61638 8.251182 8.251182 14.616379 17.798979 6.483072 9.665671 10.962285 20.392207 4.479213 10.726537 6.71882 22.160318Q1650.236439 459.94447 1650.236439 471.496125t-2.357481 22.985436q-2.121733 11.433781-6.600946 22.160318-4.479213 10.608663-10.844411 20.392208-6.483072 9.547797-14.734253 17.798979-8.251182 8.251182-17.798979 14.616379-9.665671 6.483072-20.392208 10.962285-10.726537 4.479213-22.160317 6.71882Q1543.914062 589.370157 1532.362407 589.370157h-235.748062q-7.30819 0-14.61638-0.942993zM1060.866282 471.496125a235.748063 235.748063 0 1 0-471.496125 0 235.748063 235.748063 0 0 0 471.496125 0z",
              name: `${item.name}`,
            };
          });

          const socSeries = socList.map((item) => {
            return {
              name: `${item?.name}`,
              type: "line",
              smooth: false,
              lineStyle: {
                width: 1,
                type: item?.name?.includes("实际") ? "solid" : "dashed",
              },
              showSymbol: true,
              emphasis: {
                focus: "series",
              },
              show: item?.show,
              yAxisIndex: 1,
              data: item?.dataList?.map((item) => item.value),
            };
          });

          const loadSeries = loadList.map((item, i) => {
            const areaStyleColor8 = [
              "rgba(3, 197, 179, .8)",
              "rgba(71, 255, 255, .8)",
              "rgba(142, 223, 255,.8)",
              "rgba(21, 134, 255,.8)",
              "rgba(0, 183, 255,.8)",
              "rgba(1, 76, 226,.8)",
            ];
            const areaStyleColor5 = [
              "rgba(3, 197, 179, .5)",
              "rgba(71, 255, 255, .5)",
              "rgba(142, 223, 255,.5)",
              "rgba(21, 134, 255,.5)",
              "rgba(0, 183, 255,.5)",
              "rgba(1, 76, 226,.5)",
            ];
            const areaStyleColor2 = [
              "rgba(3, 197, 179, .2)",
              "rgba(71, 255, 255, .2)",
              "rgba(142, 223, 255,.2)",
              "rgba(21, 134, 255,.2)",
              "rgba(0, 183, 255,.2)",
              "rgba(1, 76, 226,.2)",
            ];

            const areaStyle = {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: areaStyleColor8[i],
                },
                {
                  offset: 0.5,
                  color: areaStyleColor5[i],
                },
                {
                  offset: 1,
                  color: areaStyleColor2[i],
                },
              ]),
            };
            return {
              name: `${item?.name}`,
              type: "line",
              smooth: false,
              lineStyle: {
                width: 2,
                type: item?.name?.includes("实际") ? "solid" : "dashed",
              },
              showSymbol: false,
              emphasis: {
                focus: "series",
              },
              areaStyle: areaStyle,
              show: item?.show,
              yAxisIndex: 0,
              data: item?.dataList?.map((item) => item.value),
            };
          });
          const energySeries = energyList.map((item, i) => {
            return {
              name: `${item?.name}`,
              type: "line",
              smooth: false,
              lineStyle: {
                width: 1,
                type: item?.name?.includes("实际") ? "solid" : "dashed",
              },
              showSymbol: false,
              emphasis: {
                focus: "series",
              },
              areaStyle: null,
              show: item?.show,
              yAxisIndex: 0,
              data: item?.dataList?.map((item) => item.value),
            };
          });

          const photovoltaicSeries = photovoltaicList.map((item, i) => {
            const areaStyleColor8 = [
              "rgba(255, 254, 166, .8)",
              "rgba(255, 251, 0, .8)",
              "rgba(250, 207, 5,.8)",
              "rgba(250, 173, 20,.8)",
              "rgba(255, 173, 146,.8)",
              "rgba(255, 112, 2,.8)",
            ];
            const areaStyleColor5 = [
              "rgba(255, 254, 166, .5)",
              "rgba(255, 251, 0, .5)",
              "rgba(250, 207, 5,.5)",
              "rgba(250, 173, 20,.5)",
              "rgba(255, 173, 146,.5)",
              "rgba(255, 112, 2,.5)",
            ];
            const areaStyleColor2 = [
              "rgba(255, 254, 166, .2)",
              "rgba(255, 251, 0, .2)",
              "rgba(250, 207, 5,.2)",
              "rgba(250, 173, 20,.2)",
              "rgba(255, 173, 146,.2)",
              "rgba(255, 112, 2,.2)",
            ];

            const areaStyle = {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: areaStyleColor8[i],
                },
                {
                  offset: 0.5,
                  color: areaStyleColor5[i],
                },
                {
                  offset: 1,
                  color: areaStyleColor2[i],
                },
              ]),
            };
            return {
              name: `${item?.name}`,
              type: "line",
              smooth: false,
              lineStyle: {
                width: 2,
                type: item?.name?.includes("实际") ? "solid" : "dashed",
              },
              showSymbol: false,
              emphasis: {
                focus: "series",
              },
              areaStyle: areaStyle,
              show: item?.show,
              yAxisIndex: 0,
              data: item?.dataList?.map((item) => item.value),
            };
          });

          const allDates = new Set();
          data.forEach((item) => {
            item.dataList?.forEach((dataItem) => {
              allDates.add(dataItem.date);
            });
          });
          const sortedDates = Array.from(allDates).sort();
          setTimeStamp(sortedDates);
          sortedDates.forEach((date) => {
            data.forEach((group) => {
              if (!group.dataList?.some((item) => item.date === date)) {
                group.dataList.push({ date, value: null });
              }
            });
          });
          setSpinning(false);
          line(
            socOption,
            loadOption,
            energyOption,
            photovoltaicOption,
            socSeries,
            loadSeries,
            energySeries,
            photovoltaicSeries,
            sortedDates,
            true,
            colors
          );
        } else {
          line([], [], [], [], [], [], [], [], "", false, []);
          setSpinning(false);
          // message.info(res.data.msg)
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  useEffect(() => {
    const resizeObserver = new ResizeObserver((entries) => {
      for (let entry of entries) {
        const { width } = entry.contentRect;
        var myChart = echarts.init(document.getElementById("main"));
        myChart.resize();
      }
    });

    if (divRef.current) {
      resizeObserver.observe(divRef.current);
    }

    return () => {
      resizeObserver.disconnect();
    };
  }, []);
  const line = (
    socOption,
    loadOption,
    energyOption,
    photovoltaicOption,
    socSeries,
    loadSeries,
    energySeries,
    photovoltaicSeries,
    sortedDates,
    isEmpty,
    colors
  ) => {
    var myChart = echarts.init(document.getElementById("main"));
    if (myChart != null && myChart.dispose) {
      myChart.dispose();
    }
    myChart = echarts.init(document.getElementById("main"));

    window.addEventListener("resize", function () {
      myChart.resize();
    });
    myChart.setOption({
      title: {
        text: "单位：kW",
        textStyle: {
          color: "#FFF",
          fontSize: 12,
          fontWeight: "normal",
        },
        top: 40,
        left: 20,
      },
      color: colors,
      tooltip: {
        trigger: "axis",
        backgroundColor: "#302F39",
        borderColor: "transparent",
        textStyle: {
          color: "#fff", // 设置 tooltip 的文字颜色为白色
        },
        position: "top",
        axisPointer: {
          type: "cross",
          snap: true,
          label: {
            backgroundColor: "#6a7985",
          },
        },
        formatter(params) {
          var relVal = params[0].name;
          for (var i = 0, l = params.length; i < l; i++) {
            if (
              params[i].value === null ||
              params[i].value === undefined ||
              params[i].value === "" ||
              params[i].value == "-"
            ) {
              relVal +=
                "<br/>" + params[i].marker + params[i].seriesName + " : " + "-";
            } else {
              relVal +=
                "<br/>" +
                params[i].marker +
                params[i].seriesName +
                " : " +
                Number(params[i].value).toFixed(2);
            }
          }
          return relVal;
        },
      },
      graphic: {
        elements: [
          {
            type: "image",
            z: 100,
            left: "center",
            top: "middle",
            style: {
              image: require("../../img/null.png"),
              // image: 'https://example.com/image.jpg',
              width: 76,
              height: 60,
            },
            invisible: isEmpty,
            silent: true,
          },
          {
            type: "text",
            left: "center", // 相对父元素居中
            top: "160", // 相对父元素上下的位置
            z: 100,
            style: {
              fill: "#FFF",
              fontSize: 12,
              text: "暂无数据",
            },
            invisible: isEmpty,
            silent: true,
          },
        ],
      },
      legend: {
        inactiveColor: "rgba(174, 174, 174, 1)",
        top: 15,
        left: "right",
        itemGap: 20,
        data: [
          ...photovoltaicOption,
          ...loadOption,
          ...energyOption,
          ...socOption,
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
      grid: {
        left: "3%",
        right: 10,
        top: 70,
        bottom: 0,
        containLabel: true,
      },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: sortedDates,
        type: "category",
        axisLabel: {
          formatter: "{value}",
          textStyle: {
            color: "#FFF",
            fontSize: 10,
          },
        },
      },
      yAxis: {
        type: "value",
        axisLabel: {
          formatter: "{value}",
          color: "#FFF",
          fontSize: 10,
        },
        splitLine: {
          show: true,
          lineStyle: {
            type: "dotted",
            color: "rgba(255,255,255,0.2)",
          },
        },
      },
      series: [
        ...photovoltaicSeries,
        ...loadSeries,
        ...energySeries,
        ...socSeries,
      ],
    });
  };
  return (
    <div className="Energy-block-content">
      <Spin spinning={spinning}>
        <div className="Energy-block" id="main" ref={divRef}></div>
      </Spin>
    </div>
  );
};
