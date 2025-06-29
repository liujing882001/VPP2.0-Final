import { RecommendStrategy } from "../recommend-strategy";
import { useEffect, useRef, useState } from "react";
import classNames from "classnames";
import { Radio } from "antd";
import Typed from "typed.js";
import http from "../../../../../../server/server";

export const InitWrap = (props) => {
  const message1 = useRef(null);
  const { setProjectOption, onSend1, selectOption, initOptions } = props;
  let typed1 = "";
  const [showProjectBtns, setShowProjectBtns] = useState(false);
  const [radioValue1, setRadioValue1] = useState(props.radioValue);
  const [recommendList, setRecommendList] = useState([]);

  useEffect(() => {
    setTimeout(() => {
      // eslint-disable-next-line react-hooks/exhaustive-deps
      typed1 = new Typed(message1.current, {
        strings: [
          selectOption === "容量评估"
            ? "小达能帮你对储能资源进行容量评估，你可以选择以下两种方式:"
            : "小达能帮你对储能资源进行收益测算评估，请选择：",
        ],
        typeSpeed: 10,
        backSpeed: 50,
        showCursor: true,
        cursorChar: "",
        onComplete(_self) {
          setShowProjectBtns(true);
          const scroll = document.getElementById("scroll-wrap");
          scroll.scrollTop = scroll.scrollHeight;
        },
      });
    }, 200);
  }, []);

  return (
    <>
      <div className="first-chat-wrap">
        <i className="left-robot-icon" />
        <div className="first-chat-message">
          <span ref={message1}></span>
          {showProjectBtns ? (
            <div className="project-wrap">
              <Radio.Group style={{ display: "flex" }} value={radioValue1}>
                <div
                  className={classNames("new-project", {
                    "is-active": radioValue1 === 1,
                  })}
                  onClick={() => {
                    setRadioValue1(1);
                    props.setRadioValue(1);
                    initOptions();
                    setTimeout(() => {
                      const scroll = document.getElementById("scroll-wrap");
                      scroll.scrollTop = scroll.scrollHeight;
                    }, 100);
                  }}
                >
                  <Radio
                    value={1}
                    style={{ marginRight: "16px", marginBottom: 0 }}
                  />{" "}
                  <span className="project-text">新建项目</span>
                </div>
                <div
                  className={classNames("look-project", {
                    "is-active": radioValue1 === 2,
                  })}
                  onClick={async () => {
                    setRadioValue1(2);
                    props.setRadioValue(2);
                    initOptions();
                    setTimeout(() => {
                      const scroll = document.getElementById("scroll-wrap");
                      scroll.scrollTop = scroll.scrollHeight;
                    }, 100);
                    if (selectOption === "容量评估") {
                      http.get("/revenueEstGuo/getProjectList").then((res) => {
                        setRecommendList(res.data.data);
                        setProjectOption(res.data.data);
                      });
                    } else {
                      http.get("/revenueEst/getProjectList").then((res) => {
                        setRecommendList(res.data.data);
                        setProjectOption(res.data.data);
                      });
                    }
                  }}
                >
                  <Radio
                    value={2}
                    style={{ marginRight: "16px", marginBottom: 0 }}
                  />{" "}
                  <span className="project-text">查看项目</span>
                </div>
              </Radio.Group>
            </div>
          ) : null}
          {radioValue1 === 1 ? (
            <span className="new-project-tips">
              新建项目需上传12个月的负荷数据（包含时间与负荷，时间格式为yyyy-mm-dd
              hh:mm，15分钟粒度的数据）
            </span>
          ) : null}
        </div>
      </div>
      {radioValue1 === 2 ? (
        <RecommendStrategy
          {...props}
          onSend1={onSend1}
          radioValue={radioValue1}
          recommendList={recommendList}
        />
      ) : null}
    </>
  );
};
