import axios from "axios";

import { Modal } from "antd";
import { Route, Redirect } from "react-router-dom";
import Login from "../views/login/index.js";

import { createHashHistory } from "history";
const history = createHashHistory();
const controller = new AbortController();

const si = "402";
var axiosCancel = [];

// export const RequestUrl = "http://47.100.89.197:39090/";
// http://182.92.121.194:19097/vppApi
// export const RequestUrl = "http://182.92.121.194:19097/vppApi/";
// export const RequestUrl = "https://china.damao.tech/vppApi/";
// export const RequestUrl = "https://www.damao.tech/vppApi/";
// export const RequestUrl = "htltp://182.92.237.233:19097/vppApi/";
// export const RequestUrl = "http://deepengine.edgeoniot.com:29097/vppApi/"
//export const RequestUrl = "http://60.205.171.153:59090/"
export const RequestUrl="http://localhost:8080/"


// http://182.92.237.233:19097/vppApi


// axios.defaults.baseURL = 'http://' + window.location.host +'/'
// axios.defaults.baseURL = 'http://47.101.39.138:59091/';	//达卯
axios.defaults.baseURL = RequestUrl; //资源类测试环境
// axios.defaults.baseURL = 'http://127.0.0.1:59090/'; //资源类测试环境
// axios.defaults.baseURL = 'https://shanghai.damao.tech/vppApi/';

// axios.defaults.baseURL = 'http://192.168.110.39:39090/'; //资源类测试环境

//资源类测试环境
// axios.defaults.baseURL = 'http://52107s0i69.yicp.fun/';//资源类测试环境

// axios.defaults.baseURL = 'http://47.122.37.132:59091/';//产投部署测试
// axios.defaults.baseURL = 'https://chantou.damao.tech/vppApi/';//产投部署正式环境
// axios.defaults.baseURL = 'https://china.damao.tech/vppApi/';
// axios.defaults.baseURL = 'https://www.damao.tech/vppApi/'	//演示环境 or demo
// axios.defaults.baseURL = 'https://poc.damao.tech/vppApi/'	//pc演示环境 or demo
// axios.defaults.baseURL = 'http://47.122.59.9:49090/'	//负荷类备份
// axios.defaults.baseURL = 'http://192.168.112.55:59090/'	//资源类开发
// axios.defaults.baseURL = 'https://shangtang.damao.tech/vppApi' //商汤环境

axios.interceptors.request.use(
  (config) => {
    if (!window.sessionStorage.getItem("userid")) {
      if (window.location.pathname === "/") {
      } else {
        window.location.hash = "/Login";
      }
    } else {
      const url = config.url;
      config.cancelToken = new axios.CancelToken((cancel) => {
        axiosCancel.push({
          cancel,
        });
      });
      const key = document.location;
      config.headers.authorization = window.sessionStorage.getItem("tokens");
      config.headers.authorizationCode =
        window.sessionStorage.getItem("userid");
    }
    return config;
  },
  (error) => {
    console.log(error);
    return Promise.reject(error);
  }
);

//在response中
axios.interceptors.response.use(
  (response) => {
    if (response) {
      let token = window.sessionStorage.getItem("tokens");
      if (response.data) {
        let responseData = response.data;
        let theRequest,
          responseList = [];
        if (responseData.code == "401") {
          Modal.error({
            title: "提示",
            content: "登录已失效，请重新登录！",
            onOk: function () {
              window.location.hash = "/login";
            },
          });
        }
      }
    }
    return response;
  },
  (error) => {
    // console.log(error,'======');
    axiosCancel.forEach((ele, index) => {
      ele.cancel();

      delete axiosCancel[index];
    });
    return Promise.reject(error.response);
  }
);

const http = {
  post: "",
  get: "",
  put: "",
  del: "",
};
// axios.interceptors.request.use
http.post = function (api, data) {
  return new Promise((resolve, reject) => {
    axios
      .post(api, data)
      .then((response) => {
        resolve(response);
      })
      .catch((err) => {
        if (err) {
          if (err.data.code == 401) {
            return Modal.error({
              title: "提示",
              content: "登录已失效，请重新登录！",
              onOk: function () {
                history.push({ pathname: "/Login", state: {} });
              },
            });
          } else if (err.data.code == 402) {
            return Modal.error({
              title: "提示",
              content: "用户角色权限不完整，请联系管理员授权！",
              onOk: function () {
                window.location.hash = "/login";
              },
            });
          } else if (err.data.code == 403) {
            Modal.error({
              title: "提示",
              content: "如有需要请联系管理员授权！",
              onOk: function () {},
            });
          } else if (err.data.code == 302) {
            history.push({ pathname: "/Login", state: {} });
          }
        }
      });
  });
};

http.get = function (api, data) {
  return new Promise((resolve, reject) => {
    axios
      .get(api, data)
      .then((response) => {
        resolve(response);
      })
      .catch((err) => {
        if (err) {
          if (err.data.code == 401) {
            return Modal.error({
              title: "提示",
              content: "登录已失效，请重新登录！",
              onOk: function () {
                history.push({ pathname: "/Login", state: {} });
              },
            });
          } else if (err.data.code == 402) {
            return Modal.error({
              title: "提示",
              content: "用户角色权限不完整，请联系管理员授权！",
              onOk: function () {
                history.push({ pathname: "/Login", state: {} });
              },
            });
          } else if (err.data.code == 403) {
            return Modal.error({
              title: "提示",
              content: "如有需要请联系管理员授权！",
              onOk: function () {},
            });
          } else if (err.data.code == 302) {
            history.push({ pathname: "/Login", state: {} });
          }
        }
      });
  });
};

http.delete = function (api, data) {
  return new Promise((resolve, reject) => {
    axios.delete(api, data).then((response) => {
      resolve(response);
    });
  });
};

http.put = function (api, data) {
  return new Promise((resolve, reject) => {
    axios.put(api, data).then((response) => {
      resolve(response);
    });
  });
};

export default http;
