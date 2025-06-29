import * as echarts from "echarts";
import React, { useEffect, useState, useRef } from "react";
import "./index.scss";
export const Loadecharts = ({ dataList, infoNode }) => {
  const { znjt, znzz, zszx, lyyq, zszxPer, znzzPer, znjtPer, lyyqPer } =
    dataList || {};
  const [currentDiv, setCurrentDiv] = useState(1);
  const [per, setPer] = useState(zszxPer);
  const [activeIndex, setActiveIndex] = useState(0);
  const [isHover, setIsHover] = useState(false);
  const timerRef = useRef(null);
  const intervalTime = 4000;
  useEffect(() => {
    if (dataList) {
      // if(timerRef.current){
      // 	clearInterval(timerRef.current);
      // 	timerRef.current = null;
      setActiveIndex(0);
      // }
      const intervalId = setInterval(() => {
        setActiveIndex((prevIndex) => (prevIndex + 1) % 4);
      }, intervalTime);
      timerRef.current = intervalId;
    }

    return () => clearInterval(timerRef.current);
  }, [dataList]);
  useEffect(() => {
    if (infoNode) {
      setActiveIndex(1);
      setIsHover(false);
      var chartDom = document.getElementById("loadmain");
      var myChart = echarts.init(chartDom);
      myChart.dispatchAction({
        type: "downplay",
        seriesIndex: 0,
      });
      clearInterval(timerRef.current);
    }
  }, [infoNode]);
  useEffect(() => {
    var chartDom = document.getElementById("loadmain");
    var myChart = echarts.init(chartDom);
    if (!isHover) {
      myChart.dispatchAction({
        type: "downplay",
        seriesIndex: 0,
        dataIndex:
          activeIndex === 2
            ? 0
            : activeIndex === 3
            ? 1
            : activeIndex === 1
            ? 3
            : 2,
      });

      myChart.dispatchAction({
        type: "highlight",
        seriesIndex: 0,
        dataIndex:
          activeIndex === 1
            ? 0
            : activeIndex === 2
            ? 1
            : activeIndex === 3
            ? 2
            : 3,
      });
    } else {
      myChart.dispatchAction({
        type: "downplay",
        seriesIndex: 0,
      });
      myChart.dispatchAction({
        type: "highlight",
        seriesIndex: 0,
        dataIndex:
          activeIndex === 1
            ? 0
            : activeIndex === 2
            ? 1
            : activeIndex === 3
            ? 2
            : 3,
      });
    }
  }, [activeIndex, isHover]);
  useEffect(() => {
    var chartDom = document.getElementById("loadmain");
    var myChart = echarts.init(chartDom);
    var option;

    option = {
      tooltip: {
        trigger: "none",
      },
      grid: {
        top: 0,
        bottom: 0,
      },
      legend: {
        top: "5%",
        left: "center",
        show: false,
      },
      series: [
        {
          name: "Access From",
          type: "pie",
          radius: ["60%", "70%"],
          avoidLabelOverlap: false,
          padAngle: 5,
          itemStyle: {
            borderRadius: 0,
          },
          label: {
            show: false,
            position: "center",
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 40,
              fontWeight: "bold",
            },
          },
          labelLine: {
            show: false,
          },
          emphasis: {
            label: {
              show: false,
              fontSize: 40,
              fontWeight: "bold",
            },
            // scale:false
          },
          // labelLine: {
          // 	show: false
          // },
          data: [
            {
              value: dataList.zszx,
              name: "智算中心",
              itemStyle: { color: "#EA9A39" },
            },
            {
              value: dataList.znjt,
              name: "智能交通",
              itemStyle: { color: "#0ABAB5" },
            },
            {
              value: dataList.znzz,
              name: "智能制造",
              itemStyle: { color: "#C9EBF5" },
            },

            {
              value: dataList.lyyq,
              name: "楼宇园区",
              itemStyle: { color: "#24BAFF" },
            },
          ],
        },
      ],
    };

    option && myChart.setOption(option);
  }, [dataList]);
  useEffect(() => {
    // 绑定事件

    const myChart = echarts.getInstanceByDom(
      document.getElementById("loadmain")
    );
    myChart.on("mouseover", (params) => {
      clearInterval(timerRef.current); // 清除定时器
      timerRef.current = null;
      setIsHover(true);
      // console.log(znjtPer)
      setActiveIndex(
        params.name === "智算中心"
          ? 1
          : params.name === "智能交通"
          ? 2
          : params.name === "智能制造"
          ? 3
          : 0
      );
      setPer(
        params.name === "智算中心"
          ? zszxPer
          : params.name === "智能交通"
          ? znjtPer
          : params.name === "智能制造"
          ? znzzPer
          : lyyqPer
      );
    });

    myChart.on("mouseout", () => {
      setIsHover(false);
      clearInterval(timerRef.current);
      const intervalId = setInterval(() => {
        setActiveIndex((prevIndex) => (prevIndex + 1) % 4);
      }, intervalTime);
      timerRef.current = intervalId;
    });

    // 清理事件监听器
    return () => {
      myChart.off("mouseover");
      myChart.off("mouseout");
    };
  }, []); // 依赖 timerId 确保事件监听器正确设置和清理
  const handleMouseEnter = (index) => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
    setActiveIndex(index);
    setPer(
      index === 1
        ? zszxPer
        : index === 2
        ? znjtPer
        : index === 3
        ? znzzPer
        : index === 0
        ? lyyqPer
        : ""
    );
    setIsHover(true);
  };
  const handleMouseLeave = (e) => {
    clearInterval(timerRef.current);
    const intervalId = setInterval(() => {
      setActiveIndex((prevIndex) => (prevIndex + 1) % 4);
    }, intervalTime);
    setIsHover(false);
    timerRef.current = intervalId;
  };

  useEffect(() => {
    // console.log(activeIndex);
    setPer(
      activeIndex === 1
        ? zszxPer
        : activeIndex === 2
        ? znjtPer
        : activeIndex === 3
        ? znzzPer
        : activeIndex === 0
        ? lyyqPer
        : ""
    );
  }, [activeIndex]);

  return (
    <div className="loadecharts-centerDiv">
      <div id="centerDiv">
        <div className="circle"></div>
        <div className="centerDiv-skew">{per}%</div>
      </div>
      <div
        className="main"
        id="loadmain"
        style={{
          top: "7px",
          width: 100,
          height: 100,
        }}
      >
        {" "}
      </div>

      <div className="wave-line-loadmaintree">
        <div className="wave-line" id="loadmaintree"></div>
      </div>
      <div className="wave-line-value" onMouseLeave={handleMouseLeave}>
        <div
          className={activeIndex === 1 ? "wave-line-value-active" : "box"}
          id="box1"
          // style={{  fontSize: activeIndex === 1 ? 16 : 12}}
          onMouseOver={() => handleMouseEnter(1)}
        >
          <span>智算中心</span>
          {zszx}个
        </div>
        <div
          id="box2"
          className={activeIndex === 2 ? "wave-line-value-active" : "box"}
          onMouseOver={() => handleMouseEnter(2)}
        >
          <span>智能交通</span>
          {znjt}个
        </div>
        <div
          id="box3"
          className={activeIndex === 3 ? "wave-line-value-active" : "box"}
          onMouseOver={() => handleMouseEnter(3)}
        >
          <span>智能制造</span>
          {znzz}个
        </div>
        <div
          id="box4"
          className={activeIndex === 0 ? "wave-line-value-active" : "box"}
          onMouseOver={() => handleMouseEnter(0)}
        >
          <span>楼宇园区</span>
          {lyyq}个
        </div>
      </div>
      <div className="lines"></div>
    </div>
  );
};
