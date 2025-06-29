import React, { useEffect, useState } from "react";
import {
  Menu,
  Form,
  Button,
  Dropdown,
  Space,
  message,
  ConfigProvider,
  Modal,
  Input,
} from "antd";
import {
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  DownOutlined,
} from "@ant-design/icons";
// import { createHashHistory } from "history";
import dayjs from "dayjs";
// import 'moment/locale/zh-cn';
// import locale from 'antd/lib/locale/zh_CN';
import classNames from "classnames";

import http from "../../server/server.js";
import { useHistory, useLocation } from "react-router-dom";
import md5 from "js-md5";
import "./index.css";
import { SearchMode } from "./components/search-mode/index.js";
import logowhite from './assets/deep_engine-white.png'
const Header = (props) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [imgSrc, setImgSrc] = useState("");
  const [currentPath, setCurrentPath] = useState("");
  const [names, setNames] = useState(sessionStorage.getItem("name"));
  const [myForm] = Form.useForm();
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [date, setDate] = useState(new Date());
  const [loading, setLoading] = useState(false);
  const [isShowCoplitEntry, setIsShowCoplitEntry] = useState(false);
  const location = useLocation();
  const history = useHistory();
  const toggleCollapsed = () => {
    setIsExpanded(!isExpanded);
  };
  useEffect(() => {
    setCurrentPath(history.location.pathname + history.location.hash);
    props.toggle(isExpanded);
  }, [isExpanded]);
  useEffect(() => {
    let userId = sessionStorage.getItem("userid");
    http.get("/vpp/getCopilotPermission?userId=" + userId).then((res) => {
      res?.data?.data?.forEach((item) => {
        if (item?.menuNameEn === "main_copilot") {
          setIsShowCoplitEntry(true);
        }
      });
    });
    loadingShow();
    mainLogoSysParam();
    const timer = setInterval(() => {
      setDate(new Date());
    }, 1000);
    return () => {
      clearTimeout(timer);
      // const timer = setTimeout(() => {
      setLoading(false);
      // }, 600);
    };
  }, []);
  const loadingShow = () => {
    setLoading(true);
  };
  // 得到系统LOGO
  const sysLogoSysParam = () => {
    http
      .get("system_management/systemParam/v2/sysLogoSysParam")
      .then((res) => {
        console.log(res);
        if (res.data.code == 200) {
          setImgSrc(res.data.data);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  // 首页插画logo
  const mainLogoSysParam = () => {
    http
      .get("system_management/systemParam/mainLogoSysParam")
      .then((res) => {
        console.log(res);
        if (res.data.code == 200) {
          setImgSrc(res.data.data);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  // 退出
  const onClick1 = ({ key }) => {
    if (key == 1) {
      http
        .post("vpp/Logout")
        .then((res) => {
          console.log(res);
          if (res.data.code == 200) {
            sessionStorage.removeItem("tokens");
            sessionStorage.removeItem("userid");
            sessionStorage.removeItem("permissions");
            sessionStorage.removeItem("RoteName");
            history.push({
              pathname: "/Login",
            });
            window.location.reload();
            props.isAssistModebtn(false);
            props.openKeysvalbtn("/");
            message.success(res.data.data);
          } else {
            message.success("错误");
          }
        })
        .catch((err) => {
          console.log(err);
        });
    } else if (key == 2) {
      let name = sessionStorage.getItem("name");
      setIsModalVisible(true);
      myForm.setFieldsValue({
        name: name,
      });
    }
  };
  const menu = (
    <Menu
      onClick={onClick1}
      items={[
        {
          label: "修改密码",
          key: "2",
        },
        {
          label: "退出",
          key: "1",
        },
      ]}
    />
  );
  const handleOk = () => {
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };
  // 确认
  const onFinish = (values: any) => {
    console.log("Success:", values);
    console.log(sessionStorage.getItem("name"));
    let passwords = md5(values.password);
    let newpassword = md5(values.password);
    let oldpassword = md5(values.oldpassword);
    http
      .post("vpp/changePassword", {
        newPassWord: newpassword,
        oldPassWord: oldpassword,
        userId: sessionStorage.getItem("userid"),
      })
      .then((res) => {
        console.log(res);
        if (res.data.code == 200) {
          message.success("修改成功");
          history.push({ pathname: "/Login", state: {} });
          // localStorage.removeItem('token')
          sessionStorage.removeItem("tokens");
          sessionStorage.removeItem("userid");
        } else {
          message.info(res.data.msg);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const onFinishFailed = (errorInfo: any) => {
    console.log("Failed:", errorInfo);
  };
  const quxiao = () => {
    setIsModalVisible(false);
    myForm.resetFields();
  };

  const domainName = window.location.hostname;

  return (
    <div>
      <div
        className={classNames("index-headers", {
          shangtang: domainName?.includes("shangtang"),
          generating:
            (location.pathname === "/generating" && props.isSearchMode) ||
            (location.pathname === "/generating" &&
              !props.isSearchMode &&
              !props.isAssistMode),
          other:
            location.pathname !== "/generating" &&
            props.isSearchMode &&
            props.isExpand,
        })}
      >
        <div style={{ flexShrink: "0", display: "flex", alignItems: "center" }}>
          {/* <span className={classNames('collspanbtn',{'hind-collspanbtn':isAssistMode})} onClick={toggleCollapsed}>
						{isExpanded ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
					</span> */}
          {props.isSearchMode ? (
            <i
              className="close-icon"
              onClick={() => {
                props.isSearchModebtn(false);
                props.setIsExpand(false);
              }}
            />
          ) : (
            <i
              className="search-icon"
              onClick={() => {
                props.isAssistModebtn(false);
                props.isSearchModebtn(true);
                props.setOpen(false);
              }}
            />
          )}

          <>
            {domainName?.includes("shangtang") ? (
              <p>
                <i className="shangtang-logo"></i>
                {/* <i className='damao-logo'></i> */}
              </p>
            ) : (
              <>
                <img className="index-logos" src={logowhite} />
                {/* 产投版本的特殊组件 */}
                {
                  // 产投logo
                  // <img
                  //   className="logos-changle"
                  //   src={require("./assets/changye-logo.png")}
                  //   style={{ margin: "14px 10px 14px 10px" }}
                  // />
                }
              </>
            )}
          </>
          {isShowCoplitEntry && props.isShowBtn ? (
            <>
              {!props.isAssistMode ? (
                <span
                  className="in-assist-mode"
                  onClick={() => {
                    setCurrentPath(
                      history.location.pathname + history.location.hash
                    );
                    props.isSearchModebtn(false);
                    props.setIsExpand(false);
                    props.isAssistModebtn(true);
                    props.setOpen(false);
                  }}
                >
                  
                  <span>Copilot</span>
                  <i className="right-icon" />
                </span>
              ) : (
                <span
                  className="out-assist-mode"
                  onClick={() => {
                    props.isAssistModebtn(false);
                    history.push(currentPath);
                    props.isExit.current = true;
                  }}
                >
                  <i className="out-assist-mode-icon" />
                  退出Copilot
                </span>
              )}
            </>
          ) : null}
        </div>

        {location.pathname === "/generating" && props.isExpand ? null : (
          <>
            {location.pathname === "/generating" &&
            !props.isAssistMode &&
            !props.open ? (
              <div className="header-title">
                <i className="header-img-left" />

                <span>AI虚拟电厂</span>

                {/* {<span>全国生成式AI虚拟电厂运行平台</span>} */}

                {/* 产投版本特殊样式 */}
                {/* <span>长乐产投源网荷储一体化虚拟电厂</span> */}

                <i className="header-img-right" />
              </div>
            ) : null}
          </>
        )}

        <div className="loginname">
          <ConfigProvider>
            <div className="lgname">
              <Dropdown overlay={menu} trigger={["click"]}>
                <a onClick={(e) => e.preventDefault()}>
                  <Space>
                    {names}
                    <DownOutlined />
                  </Space>
                </a>
              </Dropdown>
              <span style={{ margin: "0 10px" }}> | </span>
              <span className="date" style={{ marginRight: "12px" }}>
                {dayjs().format("YYYY-MM-DD HH:mm:ss")}
              </span>
            </div>
          </ConfigProvider>
        </div>
      </div>
      <Modal
        title="修改密码"
        visible={isModalVisible}
        footer={null}
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <Form
          name="basic"
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
          autoComplete="off"
          form={myForm}
        >
          <Form.Item
            label="登录名称"
            name="name"
            rules={[{ required: true, message: "请输入用户名" }]}
            autoComplete="off"
          >
            <Input autoComplete="off" disabled />
          </Form.Item>
          <Form.Item
            label="旧密码"
            name="oldpassword"
            // rules={[{ required: true, message: '请输入旧密码' }]}
            autoComplete="off"
            rules={[
              {
                required: true,
                pattern:
                  /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[~!.@#$%^&*])[\da-zA-Z~!.@#$%^&*]{8,100}$/,
                message: "8-16位字符，必须包括字母,数字和特殊字符",
              },
            ]}
          >
            <Input.Password autoComplete="new-password" />
          </Form.Item>
          <Form.Item
            label="新密码"
            name="password"
            required
            rules={[
              { required: true, message: "请输入新密码" },
              {
                pattern:
                  /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[~!.@#$%^&*])[\da-zA-Z~!.@#$%^&*]{8,100}$/,
                message: "不能小于8位字符，必须包括字母,数字和特殊字符",
              },
              ({ getFieldValue }) => ({
                validator(rule, value) {
                  if (value && getFieldValue("oldpassword") === value) {
                    console.log(1);
                    return Promise.reject("用户新旧密码一致，请重新输入新密码");
                  } else {
                    return Promise.resolve();
                  }
                },
              }),
            ]}
          >
            <Input.Password autoComplete="new-password" />
          </Form.Item>
          <Form.Item
            label="确认密码"
            name="pw2"
            rules={[
              { required: true, message: "请输入密码" },
              ({ getFieldValue }) => ({
                validator(rule, value) {
                  console.log(value);
                  if (!value || getFieldValue("password") === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject("两次密码输入不一致");
                },
              }),
            ]}
          >
            <Input.Password palceholder="请确认密码" />
          </Form.Item>
          <Form.Item
            wrapperCol={{ offset: 8, span: 16 }}
            style={{ textAlign: "right" }}
          >
            <Button ghost onClick={quxiao}>
              取消
            </Button>
            <Button type="primary" htmlType="submit">
              确定
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Header;
