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
import { useHistory } from "react-router-dom";
import md5 from "js-md5";
import "./index.css";
import { use } from "echarts";

const Header = (props) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [isAssistMode, setIsAssistMode] = useState(false);
  const [imgSrc, setImgSrc] = useState("");
  const [currentPath, setCurrentPath] = useState("");
  const [names, setNames] = useState(sessionStorage.getItem("name"));
  const [myForm] = Form.useForm();
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [date, setDate] = useState(new Date());
  const [loading, setLoading] = useState(false);
  const [isShowCoplitEntry, setIsShowCoplitEntry] = useState(false);

  const history = useHistory();
  const toggleCollapsed = () => {
    setIsExpanded(!isExpanded);
  };
  useEffect(() => {
    props.toggle(isExpanded);
  }, [isExpanded]);
  useEffect(() => {
    let userId = sessionStorage.getItem("userid");
    http.get("/vpp/getCopilotPermission?userId=" + userId).then((res) => {
      res?.data?.data?.forEach((item) => {
        if (item.menuNameEn === "main_copilot") {
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
        className={classNames("headers", {
          shangtang: domainName?.includes("shangtang"),
        })}
      >
        <span
          className={classNames("collspanbtn", {
            "hind-collspanbtn": isAssistMode,
          })}
          onClick={toggleCollapsed}
        >
          {isExpanded ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
        </span>

        <>
          <img
            className="logos"
            src={imgSrc}
            style={{ margin: "14px 10px 14px 10px" }}
          />
          <img
            className="logos-changle"
            src={require("./assets/changye-logo.png")}
            style={{ margin: "14px 10px 14px 10px" }}
          />
        </>
        {isShowCoplitEntry ? (
          <>
            {!isAssistMode ? (
              <span
                className="in-assist-mode"
                onClick={() => {
                  setCurrentPath(
                    history.location.pathname + history.location.hash
                  );
                  setIsAssistMode(true);
                  props.isAssistModebtn(true);
                }}
              >
                <i className="in-assist-mode-icon" />
                进入Copilot
              </span>
            ) : (
              <span
                className="out-assist-mode"
                onClick={() => {
                  setIsAssistMode(false);
                  props.isAssistModebtn(false);
                  history.push(currentPath);
                }}
              >
                <i className="out-assist-mode-icon" />
                退出Copilot
              </span>
            )}
          </>
        ) : null}

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
              <h3 className="date">{dayjs().format("YYYY-MM-DD HH:mm:ss")}</h3>
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
