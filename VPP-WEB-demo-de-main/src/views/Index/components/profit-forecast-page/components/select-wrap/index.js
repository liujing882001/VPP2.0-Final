import { useEffect, useRef, useState } from "react";
import "./index.scss";
import Typed from "typed.js";
import classNames from "classnames";

export const SelectWrap = (props) => {
  const { doInit, setRadioValue } = props || {};
  const message1 = useRef(null);
  const [current, setCurrent] = useState("");
  let typed1 = "";

  useEffect(() => {
    setTimeout(() => {
      // eslint-disable-next-line react-hooks/exhaustive-deps
      typed1 = new Typed(message1.current, {
        strings: ["小达能帮你对储能资源进行容量评估或收益测算"],
        typeSpeed: 10,
        backSpeed: 50,
        showCursor: true,
        cursorChar: "",
        onComplete(_self) {},
      });
    }, 200);
  }, []);

  return (
    <>
      <div className="first-chat-wrap">
        <i className="left-robot-icon" />
        <div className="first-chat-message">
          <span ref={message1}></span>
          <div className="select-wrap">
            <div
              className={classNames("select-item", {
                active: current === "容量评估",
              })}
              onClick={() => {
                doInit("容量评估");
                setCurrent("容量评估");
                setRadioValue(null);
              }}
            >
              容量评估
            </div>
            <div
              className={classNames("select-item", {
                active: current === "收益测算",
              })}
              onClick={() => {
                doInit("收益测算");
                setCurrent("收益测算");
                setRadioValue(null);
              }}
            >
              收益测算
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
