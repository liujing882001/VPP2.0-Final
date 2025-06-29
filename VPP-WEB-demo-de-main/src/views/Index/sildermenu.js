import React, { useState, useEffect } from "react";

import {
  AppstoreOutlined,
  MailOutlined,
  SettingOutlined,
} from "@ant-design/icons";
import {
  Layout,
  Menu,
  Button,
  Avatar,
  Radio,
  Dropdown,
  Space,
  message,
  Modal,
  Checkbox,
  Form,
  Input,
  ConfigProvider,
  Spin,
  Popover,
} from "antd";
import { Link } from "react-router-dom";
import { createFromIconfontCN } from "@ant-design/icons";
import * as Icon from "@ant-design/icons";
import http from "../../server/server.js";
import "./index.scss";
import "./common.css";
import "./index.css";
import expand from "../Index/assets/expand.svg";
import retract from "../Index/assets/retract.svg"; //收起
import { useHistory, useLocation } from "react-router-dom";

const { Header, Content, Footer, Sider } = Layout;

const Sildermenu = (props) => {
  const [stateOpenKeys, setStateOpenKeys] = useState(["2", "23"]);
  const [menuData, setMenuData] = useState([]);
  const [scrollWidth, setScrollWidth] = useState(10);
  const [scrollColor, setScrollColor] = useState("gray");
  const [isAssistMode, setIsAssistMode] = useState(false);
  const [openKeysval, setOpenKeysval] = useState("/generating");
  const [itemKey, setItemKey] = useState("");
  const [isActive, setIsActive] = useState("");
  const [names, setNames] = useState(sessionStorage.getItem("name"));
  const [userName, setUserName] = useState("");
  const [roteName, setRoteName] = useState("");
  const [isretract, setIsretract] = useState(false);
  const [isgenerating, setIsgenerating] = useState(false);
  const [open, setOpen] = useState(false);
  const { isExpanded } = props.collapsed || {};
  let history = useHistory();
  const location = useLocation();

  useEffect(() => {
    if (names !== "") {
      let userId = sessionStorage.getItem("userid");
      if (history.location.pathname == "/index") {
        // 获取路由
        http
          .get("vpp/getUserMenuList?userId=" + userId)
          .then((res) => {
            console.log(res);
            if (res.data.code == 200) {
              let data = res.data.data;

              if (data[0].children.length > 0) {
                setUserName(sessionStorage.userName);
                setRoteName(data[0].children[0].component);
                sessionStorage.setItem(
                  "RoteName",
                  data[0].children[0].component
                );
                setMenuData(data);
                setItemKey(data[0].children[0].menuId);
                props.opencompant(data[0].children[0].component);
              } else {
                setUserName(sessionStorage.userName);
                setRoteName(data[0].component);
                sessionStorage.setItem("RoteName", data[0].component);
                setMenuData(data);
                setItemKey(data[0].menuId);
                props.opencompant(data[0].component);
              }
              console.log(data);
            }
            if (res.data.code === 400) {
              props.isAssistModebtn(true);
              props.setIsShowBtn(false);
            }
          })
          .catch((err) => {
            console.log(err);
          });
      } else {
        // 获取路由
        http
          .get("vpp/getUserMenuList?userId=" + userId)
          .then((res) => {
            console.log(res);
            if (res.data.code == 200) {
              let data = res.data.data;
              if (data[0].children.length > 0) {
                setUserName(sessionStorage.userName);
                // setRoteName(res.data.data[0].children[0].component);
                setMenuData(data);
                setItemKey(data[0].children[0].menuId);
                props.opencompant(data[0].children[0].component);
              } else {
                setUserName(sessionStorage.userName);
                // setRoteName(res.data.data[0].component);
                setMenuData(data);
                setItemKey(data[0].menuId);
                props.opencompant(data[0].component);
              }
              if (res.data.code === 400) {
                props.isAssistModebtn(true);
                props.setIsShowBtn(false);
              }
            }
          })
          .catch((err) => {
            console.log(err);
          });
      }
    }
  }, [names]);

  const loadingShow = () => {
    // setLoading(true)
  };
  useEffect(() => {
    if (history.location.hash === "#/Login") {
      history.push({
        pathname: "/Login",
      });
    }
  }, [history.location.hash]);
  useEffect(() => {
    console.log(location);
    if (location?.pathname === "/generating") {
      setIsretract(true);
      setIsgenerating(true);
      setOpen(true);
    } else {
      setIsretract(false);
      setIsgenerating(false);
      setOpen(false);
    }
  }, [location]);

  useEffect(() => {
    if (history.location !== history.location) {
      window.scrollTo(0, 0);
      loadingShow();
    }
    let RoteNameVal = sessionStorage.getItem("RoteName");
    setRoteName(
      history.location.pathname == "/index" ? "/generating" : RoteNameVal
    );
    props.openKeysvalbtn(
      history.location.pathname == "/index"
        ? "/generating"
        : history.location.pathname
    );
  }, [history.location]);
  // 点击菜单
  const menuItems = (item) => {
    http.put(`/applicationCenter/addApplicationLog?name=${item?.menuName}`);
    if (item.component.indexOf("http") >= 0) {
      window.open(item.component);
    } else {
      setItemKey(item.menuId);
      setRoteName(item.component);
      sessionStorage.setItem("RoteName", item.component);
      setOpenKeysval(item.component);
      props.openKeysvalbtn(item.component);
    }
  };
  useEffect(() => {
    if (roteName == history.location.pathname) {
      setRoteName(history.location.pathname);
    }
  }, [roteName]);
  const content = (
    <div className="ant-popover-inner-content-menu">
      <p>你可以通过点击按钮展示菜单栏进行菜单查看及选择</p>
      <Button
        type="text"
        style={{ float: "right" }}
        onClick={() => {
          setOpen(false);
        }}
      >
        我知道了
      </Button>
    </div>
  );
  const setFirstLoginMarker = () => {
    const currentDate = new Date();
    localStorage.setItem("firstLoginDate", currentDate.toISOString());
  };

  useEffect(() => {
    // const lastLoginDate = localStorage.getItem(firstLoginDate);
    //     const now = new Date();
    //     const sevenDaysAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    //     // 如果没有记录或者记录超过了7天，则更新日期
    //     if (!lastLoginDate || new Date(lastLoginDate) < sevenDaysAgo) {
    //       localStorage.setItem(firstLoginDate, now.toISOString());
    //       // 这里可以执行一些第一次登录或超过7天未登录的逻辑
    //       console.log('这是第一次登录，或者距离上次登录已经超过7天');
    //     } else {
    //       // 这里可以执行用户不是第一次登录或未超过7天的逻辑
    //       console.log('欢迎回来！');
    //     }
  }, []);
  return (
    <Sider className={isretract ? "bgblue" : "bgred"}>
      <Popover content={content} placement="rightTop" open={open}>
        <div
          className={
            isgenerating ? "silderHed-collapsed-active" : "silderHed-collapsed"
          }
          onClick={() => {
            setIsretract(!isretract);
          }}
        >
          <img src={isretract ? expand : retract} />
        </div>
      </Popover>

      <div
        className="silderHed"
        style={{
          width: isretract ? "0px" : "auto",
          transition: "width 2s",
          marginTop: isgenerating ? "50px" : "0px",
          height: isgenerating ? "calc(100% - 50px)" : "100%",
        }}
      >
        <ul>
          {menuData &&
            menuData.map((item, index) => {
              if (item.children.length > 0) {
                return (
                  <li key={item.menuId}>
                    {props.collapsed ? (
                      <p key={index}>
                        {React.createElement(
                          Icon[item?.icon || "PicCenterOutlined"]
                        )}
                      </p>
                    ) : (
                      <p key={item.menuId + index}>
                        {React.createElement(
                          Icon[item?.icon || "PicCenterOutlined"]
                        )}
                        {item.menuName}
                      </p>
                    )}

                    <div
                      className="menuItem"
                      style={{ display: props.collapsed ? "none" : "block" }}
                    >
                      {item.children.map((child) => {
                        return (
                          <Link to={child.component}>
                            <span
                              className={
                                roteName == child.component
                                  ? "active"
                                  : "inactive"
                              }
                              onClick={() => menuItems(child)}
                            >
                              {child.menuName}
                            </span>
                          </Link>
                        );
                      })}
                    </div>
                  </li>
                );
              } else {
                return (
                  <li key={item.menuId}>
                    {props.collapsed ? (
                      <p key={index}>
                        {React.createElement(
                          Icon[item?.icon || "PicCenterOutlined"]
                        )}
                      </p>
                    ) : (
                      <p key={index} style={{ paddingBottom: 16 }}>
                        {React.createElement(
                          Icon[item?.icon || "PicCenterOutlined"]
                        )}
                        <span
                          className={
                            roteName == item.component ? "active" : "inactive"
                          }
                          onClick={() => menuItems(item)}
                        >
                          <Link to={item.component}>{item.menuName}</Link>
                        </span>
                      </p>
                    )}
                  </li>
                );
              }
            })}
        </ul>
      </div>
    </Sider>
  );
};

export default Sildermenu;
