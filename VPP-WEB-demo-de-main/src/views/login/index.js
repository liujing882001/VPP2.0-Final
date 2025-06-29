import React, { useEffect, useState } from "react";
import { Form, Input, Button, Checkbox, Space, message, Modal } from "antd";

import axios from "axios";
// import http from '../../server/server.js'
import "./index.css";
import "./login.scss";

import { UserOutlined, UnlockOutlined } from "@ant-design/icons";
import { receiveData } from "../../action/index.js";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";

import md5 from "js-md5";
import Index from "../Index/index.js";
// import resource from '../resource/index.js'
// import storageUtils from '../../untils/storageUtils'

import { createHashHistory } from "history";
import logowhite from '../Index/assets/deep_engine-white.png'

const history = createHashHistory();

React.Component.prototype.$md5 = md5;

const Login = (props) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [token, setToken] = useState("");
  const [rememberVal, setRememberVal] = useState("");
  const [loading, setLoading] = useState(false);
  const [imgSrc, setImgSrc] = useState("");
  const [logoSrc, setLogoSrc] = useState("");
  const [osType, setOsType] = useState("");
  const [dataText, setDataText] = useState("");
  const [platform, setPlatform] = useState("AI虚拟电厂Copilot");
  const [myForm] = Form.useForm();
  useEffect(() => {
    sessionStorage.removeItem("userid");
    axios({
      url: "vpp/captcha",
      method: "post",
    }).then((response) => {
      if (response.data.code == 200) {
        setToken(response.data.data.verifyCode);
        let imgDiv = document.getElementById("secret");
        imgDiv.src = response.data.data.captchaImg;
      } else {
        message.info(response.data.msg);
      }
    });
    checkRememberPassword();
    mainLogoSysParam();
    // console.log(window.location)
    // // alert(new URLSearchParams(window.location))
    // if(window.location.search){
    // 	// alert(0)
    // }
    // if()
	
  }, []);

  const getCaptcha = () => {
    axios({
      url: "vpp/captcha",
      method: "post",
    }).then((response) => {
      console.log(response);
      if (response.data.code == 200) {
        setToken(response.data.data.verifyCode);
        console.log(response);
        let imgDiv = document.getElementById("secret");
        imgDiv.src = response.data.data.captchaImg;
      } else {
        message.info(response.data.msg);
      }
    });
  };
  const setCookie = (name, value, expiryDate) => {
    let currentDate = new Date();
    currentDate.setDate(currentDate.getDate() + expiryDate);
    document.cookie = name + "=" + value + "; expires=" + currentDate;
  };
  const getCookie = (name) => {
    let arr = document.cookie.split("; ");
    for (let i = 0; i < arr.length; i++) {
      let arr2 = arr[i].split("=");
      if (arr2[0] === name) {
        return arr2[1];
      }
    }
    return "";
  };
  const removeCookie = (name) => {
    setCookie(name, 1, -1);
  };
  const isPasswordEncrypted = (password) => {
    try {
      // 尝试解码 Base64 字符串
      const decodedPassword = atob(password);

      // 检查解码后的密码长度或其他特征等
      // 这里仅作为示例，可以根据具体情况进行判断
      return true;
    } catch (error) {
      // 解码失败，说明密码没有经过 atob 加密
      return false;
    }
  };
  const checkRememberPassword = () => {
    if (getCookie("userName") !== "" && getCookie("passWord") !== "") {
      const plainPassword = getCookie("passWord");
      let isTrue = isPasswordEncrypted(plainPassword);
      myForm.setFieldsValue({
        userName: getCookie("userName"),
        passWord:
          plainPassword == false
            ? getCookie("passWord")
            : window.atob(getCookie("passWord")),
      });
      setRememberVal(true);
    }
  };

  // 记住密码
  const remberPassword = (e) => {
    const { userName, passWord } = myForm.getFieldsValue();
    let values = myForm.getFieldsValue();
    setRememberVal(e.target.checked);
    if (e.target.checked) {
      setCookie("userName", values.userName, 1);
      setCookie("passWord", btoa(values.passWord), 1);
    } else {
      removeCookie("userName");
      removeCookie("passWord");
    }
  };
  // 首页插画logo
  const mainLogoSysParam = () => {
    axios
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
  // 得到系统LOGO
  const sysLogoSysParam = () => {
    axios
      .get("system_management/systemParam/sysLogoSysParam")
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
  // 得到首页公司信息
  const footInfo = () => {
    axios
      .get("system_management/systemParam/v2/footInfo")
      .then((res) => {
        console.log(res);
        if (res.data.code == 200) {
          let text = res.data.data;
          var formattedText = text
            .split("<br/>")
            .map((line, index) => <p key={index}>{line}</p>);
          setDataText(formattedText);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  // 得到平台名称
  const platformNameSysParam = () => {
    axios
      .get("system_management/systemParam/v2/platformNameSysParam")
      .then((res) => {
        console.log(res);
        if (res.data.code == 200) {
          setPlatform(res.data.data);
        } else {
          message.info(res.data.msg);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const onFinish = (values: any) => {
    const { username, password } = values;
    let passwords = md5(values.passWord);
    sessionStorage.setItem("name", values.userName);
    if (rememberVal == true) {
      setCookie("userName", values.userName, 1);
      setCookie("passWord", btoa(values.passWord), 1);
    }
    setLoading(true);
    axios({
      url: "vpp/doLogin",
      method: "post",
      data: {
        passWord: passwords,
        verifyCode: token,
        userName: values.userName,
        verifyCodeText: values.verificationCode,
      },
    }).then((response) => {
      console.log(response.data, "-----------------");
      if (response.data.code == 200) {
        sessionStorage.removeItem("userid");
        sessionStorage.removeItem("tokens");
        sessionStorage.removeItem("permissions");
        sessionStorage.removeItem("osType");
        sessionStorage.setItem("userid", response.data.data.userId);
        sessionStorage.setItem("tokens", response.data.data.refreshToken);
        sessionStorage.setItem("permissions", response.data.data.permissions);
        sessionStorage.setItem("username", response.data.data.username);
        sessionStorage.setItem("osType", response.data.data.osType);
        setLoading(false);
        props.history.push({
          pathname: "/index",
          // pathname: '/resource',
        });
      } else if (response.data.code == 418) {
        return Modal.error({
          title: "您的账号已过期",
          content: "您的授权服务已过期，请立即联系销售人员申延期",
          onOk: function () {
            window.location.reload();
          },
          okText: "知道了",
        });
      } else {
        setLoading(false);
        message.error(response.data.msg);
        getCaptcha();
        myForm.setFieldsValue({
          verificationCode: "",
        });
      }
    });
  };

  const onFinishFailed = (errorInfo: any) => {
    console.log("Failed:", errorInfo);
  };
  const onFilings = () => {
    window.open("https://beian.miit.gov.cn/");
  };

  const domainName = window.location.hostname;

  return (
    <div className="xulogin">
      <div className="header">
        <>
          {domainName?.includes("shangtang") ? (
            <div className="shangtang-wrap">
              <div className="shangtang-logo"></div>
              {/* <div className='damao-logo'></div> */}
            </div>
          ) : (
            <img className="logo" src={logowhite} />
          )}
        </>
      </div>

      <div className="content">
        {
			// <div className="Record_number" onClick={onFilings}>
			//   © 上海达卯科技有限公司 上海市徐汇区丰谷路315弄24号1-3层
			//   沪ICP备2024047354号
			// </div>
		}
        <Form
          name="basics2"
          labelCol={{ span: 0 }}
          wrapperCol={{ span: 25 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
          autoComplete="off"
          form={myForm}
        >
          <h1 style={{ textAlign: "center", fontSize: 24 }}>{platform}</h1>
          <Form.Item
            name="userName"
            rules={[{ required: true, message: "请输入账号" }]}
            autoComplete="off"
          >
            <Input autoComplete="off" prefix={<UserOutlined />} />
          </Form.Item>

          <Form.Item
            name="passWord"
            autoComplete="new-password"
            rules={[{ required: true, message: "请输入密码" }]}
            rules={[
              {
                pattern: /^(?![^a-zA-Z]+$)(?!\\D+$)(?=.*[^a-zA-Z0-9]).{8,100}$/,
                message: "不能小于8位字符，必须包括字母,数字和特殊字符",
              },
            ]}
          >
            <Input.Password
              autoComplete="new-password"
              prefix={<UnlockOutlined />}
            />
          </Form.Item>
          <Form.Item
            name="verificationCode"
            rules={[{ required: true, message: "请输入验证码" }]}
          >
            <Space>
              <Input style={{ height: 40 }} />
              <img
                id="secret"
                src=""
                alt="点击刷新"
                style={{ cursor: "pointer" }}
                onClick={getCaptcha}
              />
            </Space>
          </Form.Item>
          <Form.Item>
            <Checkbox onChange={remberPassword} checked={rememberVal}>
              记住密码
            </Checkbox>
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              loading={loading}
              style={{ height: 48, fontSize: 16 }}
              htmlType="submit"
            >
              登录
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  );
};

export default Login;
// const mapStateToPorps = state => {
//   const { auth } = state.httpData;
//   return { auth };
// };
// const mapDispatchToProps = dispatch => ({
//   // fetchData: bindActionCreators(fetchData, dispatch),
//   receiveData: bindActionCreators(receiveData, dispatch)
// });
// export default connect(mapDispatchToProps)(login);
// export default connect(null, mapDispatchToProps)((login));
