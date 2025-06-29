import React, { Component } from "react";
import { Form, Input, Button, Checkbox, Space, message } from "antd";
import axios from "axios";
// import http from '../../server/server.js'
// import './index.css'
import "./login.scss";
import { UserOutlined, UnlockOutlined } from "@ant-design/icons";
import { receiveData } from "../../action/index.js";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";

import md5 from "js-md5";
import Index from "../Index/index.js";
import resource from "../resource/index.js";
// import storageUtils from '../../untils/storageUtils'

import { createHashHistory } from "history";
const history = createHashHistory();

React.Component.prototype.$md5 = md5;

class login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: "",
      password: "",
      token: "",
    };
  }

  componentDidMount() {
    this.forceUpdate();
    // alert(0)
    // const { receiveData } = this.props;
    // console.log(receiveData,'0000receiveData')
    // receiveData(null, 'auth');
    sessionStorage.removeItem("userid");
    axios({
      url: "vpp/captcha",
      method: "post",
    }).then((response) => {
      if (response.data.msg == "成功") {
        // localStorage.setItem("token",response.data.data.token)
        this.token = response.data.data.verifyCode;
        console.log(response);
        let imgDiv = document.getElementById("secret");
        imgDiv.src = response.data.data.captchaImg; //这个可以
        console.log(imgDiv);
      }
    });
  }
  getCaptcha() {
    axios({
      url: "vpp/captcha",
      method: "post",
    }).then((response) => {
      if (response.data.msg == "成功") {
        console.log(response);
        this.token = response.data.data.verifyCode;
        // this.token = res
        // localStorage.removeItem('token')
        let imgDiv = document.getElementById("secret");
        imgDiv.src = response.data.data.captchaImg; //这个可以
        console.log(imgDiv);
      }
    });
    // axios.post('user/captcha').then(response => {

    // })
  }
  render() {
    const onFinish = (values: any) => {
      // let token = localStorage.getItem('token')
      // console.log(token)
      console.log("Success:", values);
      const { username, password } = values;
      let passwords = md5(values.passWord);
      sessionStorage.setItem("name", values.userName);
      axios({
        url: "vpp/doLogin",
        method: "post",
        data: {
          passWord: passwords,
          verifyCode: this.token,
          userName: values.userName,
          verifyCodeText: values.verificationCode,
        },
      }).then((response) => {
        console.log(response.data);
        if (response.data.msg == "成功") {
          sessionStorage.removeItem("userid");
          sessionStorage.removeItem("tokens");
          sessionStorage.removeItem("permissions");
          sessionStorage.setItem("userid", response.data.data.userId);
          sessionStorage.setItem("tokens", response.data.data.refreshToken);
          sessionStorage.setItem("username", response.data.data.username);

          // history.push({pathname:"/Index",state:{}})
          sessionStorage.setItem("permissions", response.data.data.permissions);
          console.log(sessionStorage.getItem("permissions"), "------");
          this.props.history.push({
            pathname: "/Index",
            // pathname: '/resource',
          });
          //

          // this.props.history.push('/Index');
        } else {
          message.error(response.data.msg);
          this.getCaptcha();
          this.refs.myForm.resetFields();
        }
      });
    };

    const onFinishFailed = (errorInfo: any) => {
      console.log("Failed:", errorInfo);
    };
    // <img id="logo" src={require('../../style/img/xu.png')}  />
    //
    // <img id="logo" src={require('../../style/imgs/login.png')}  />
    // <img id="logo" src={require('../../style/img/xu.png')}  />
    // <img className="logo" src={require('../../style/imgs/loginlogo.png')}  />
    // <img className="logo" style={{width:'auto'}} src={require('../../style/imgs/logo11.png')}  />
    // <h1>欢迎登录</h1>
    // 银河能源云 <img className="logo" style={{width:'auto'}} src={require('../../style/imgs/logo11.png')}  />

    return (
      <div className="alllogin xulogin">
        <div className="header">
          <img src={require("../../style/guo/logo.png")} />
          <ul>
            <li className="xuheader">意见反馈</li>
            <li>关于我们</li>
          </ul>
        </div>

        <div className="content">
          <Form
            name="basics2"
            labelCol={{ span: 0 }}
            wrapperCol={{ span: 25 }}
            initialValues={{ remember: true }}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
            autoComplete="off"
            ref="myForm"
          >
            <Form.Item
              name="userName"
              rules={[{ required: true, message: "请输入账号" }]}
            >
              <Input prefix={<UserOutlined />} />
            </Form.Item>

            <Form.Item
              name="passWord"
              rules={[{ required: true, message: "请输入密码" }]}
              rules={[
                {
                  pattern:
                    /^(?![^a-zA-Z]+$)(?!\\D+$)(?=.*[^a-zA-Z0-9]).{8,100}$/,
                  message: "不能小于8位字符，必须包括字母,数字和特殊字符",
                },
              ]}
            >
              <Input.Password prefix={<UnlockOutlined />} />
            </Form.Item>
            <Form.Item
              name="verificationCode"
              rules={[{ required: true, message: "请输入验证码" }]}
            >
              <Space>
                <Input />
                <img
                  id="secret"
                  src=""
                  alt="点击刷新"
                  style={{ cursor: "pointer" }}
                  onClick={this.getCaptcha}
                />
              </Space>
            </Form.Item>
            <Form.Item name="remember" valuePropName="checked">
              <Checkbox>记住密码</Checkbox>
            </Form.Item>

            <Form.Item>
              <Button type="primary" htmlType="submit">
                登录
              </Button>
            </Form.Item>
          </Form>
        </div>
        <div className="footer">
          国网数字科技控股有限公司（国网雄安金融科技集团有限公司） 版权所有
          <br />
          地址：北京市西城区广安门内大街311号祥龙商务大厦 | 邮编：100053{" "}
        </div>
      </div>
    );
  }
}

export default login;
