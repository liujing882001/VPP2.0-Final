import React, { useEffect, useState } from "react";
import {
  Table,
  Space,
  Form,
  Button,
  Checkbox,
  Input,
  Modal,
  Select,
  message,
  DatePicker,
} from "antd";
import http from "../../../server/server.js";
import "./index.css";
import md5 from "js-md5";
import {
  PlusOutlined,
  RedoOutlined,
  ExclamationCircleOutlined,
  InfoCircleOutlined,
} from "@ant-design/icons";
import BaiduMap from "./BaiduMap.js";
import dayjs from "dayjs";
import "../hybrid/index.css";
React.Component.prototype.$md5 = md5;
const { TextArea } = Input;

const { Search } = Input;

const { confirm } = Modal;

React.Component.prototype.$md5 = md5;

const { Option } = Select;

const Personnel = () => {
  const [dataSource, setDataSource] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [add, setAdd] = useState(0);
  const [RoleType, setRoleType] = useState([]);
  const [userId, setUserId] = useState(sessionStorage.getItem("userid"));
  const [roleId, setRoleId] = useState("");
  const [loading, setLoading] = useState(false);
  const [wether, setWether] = useState("");
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState("");
  const [pages, setPages] = useState(1);
  const [userName, setUserName] = useState("");
  const [search, setSearch] = useState("");
  const [addtext, setAddtext] = useState("");
  const [useredit, setUseredit] = useState(1); // //0为电力用户，1为管理员
  const [address, setAddress] = useState("");
  const [mapData, setMapData] = useState("");
  const [nodeList, setNodeList] = useState([]);
  const [istrue, setIstrue] = useState(false);
  const [isedit, setIsEdit] = useState(0);
  const [rolekey, setRoleKey] = useState(0);
  const [Signing, setSigning] = useState("");
  const [changePaw, setChangePaw] = useState("");
  const [pawVisible, setPawVisible] = useState(false);
  const [pawName, setPawName] = useState("");
  const [singisible, setSingisible] = useState(false);
  const [currentNum, setCurrentNum] = useState(1);
  const [setLoadings, setSetLoadings] = useState(false);
  const [expireTime, setExpireTime] = useState(null);
  const [showExpireTimeInput, setShowExpireTimeInput] = useState(true);
  const [myForm] = Form.useForm();
  const [singForm] = Form.useForm();
  const [pawForm] = Form.useForm();
  const [roleName, setRoleName] = useState("");
  const [isTrue, setIsTrue] = useState(false);

  useEffect(() => {
    let permissions = sessionStorage.getItem("permissions");
    let str = "";
    var m;
    if (permissions) {
      str = permissions.toString();
      m = str.split(",");
      setWether(m);
    }
    queryAllSysUserPageable();
    nodeNameList();
  }, []);
  useEffect(() => {
    if (wether) {
      for (var i = 0; i < wether.length; i++) {
        if (wether[i] == "system:user:query") {
          setSearch("system:user:query");
        }
        if (wether[i] == "system:user:add") {
          setAddtext("system:user:add");
        }
        if (wether[i] == "system:user:shareRatio") {
          // 签约
          setSigning("system:user:shareRatio");
        }
        if (wether[i] == "system:user:change_pwd") {
          // 重置密码

          setChangePaw("system:user:change_pwd");
        }
      }
    }
  }, [wether]);
  useEffect(() => {
    if (userName == "") {
      queryAllSysUserPageable();
    } else {
      http
        .post(
          "system_management/system_user/user/queryAllSysUserPageable?number=" +
            1 +
            "&pageSize=" +
            "1000000" +
            "&userName=" +
            userName
        )
        .then((res) => {
          if (res.data.code == 200) {
            setDataSource(res.data.data.content);
            setLoading(false);
            setTotal(res.data.data.totalElements);
          } else {
            message.info(res.data.msg);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    }
  }, [page, pages, currentNum, userName]);
  const ChildrenChange = () => {
    setIstrue(false);
  };
  // 搜索
  const onSearch = (val) => {
    setUserName(val);
    setCurrentNum(1);
    setPage(1);
  };
  // 分页
  const queryAllSysUserPageable = () => {
    setLoading(true);
    http
      .post(
        "system_management/system_user/user/queryAllSysUserPageable?number=" +
          page +
          "&pageSize=" +
          "10" +
          "&userName=" +
          userName
      )
      .then((res) => {
        if (res.data.code == 200) {
          setDataSource(res.data.data.content);
          setLoading(false);
          setTotal(res.data.data.totalElements);
        } else {
          message.info(res.data.msg);
        }
      })
      .catch((err) => {});
  };

  // 列表
  const parameter = () => {
    http
      .post("system_management/energy_model/model_parameter/modelParameterList")
      .then((res) => {
        // console.log(res)
      })
      .catch((err) => {
        console.log(err);
      });
  };
  // 获取类型
  const allRoleTypes = () => {
    http
      .post("system_management/system_user/role/allUserRolesWithoutAdmin")
      .then((res) => {
        if (res.data.code == 200) {
          setRoleType(res.data.data);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  useEffect(() => {});
  const edit = (e) => {
    allRoleTypes();
    setIsModalVisible(true);
    setAdd(1);
    setRoleId(e.role);
    setUserId(e.userId);
    setAddress(e.address);
    setIstrue(true);
    setExpireTime(e.expirationTime);
    setUseredit(e.roleKey == 1 || e.roleKey == 2 ? 1 : 0);
    if (wether) {
      for (var i = 0; i < wether.length; i++) {
        if (wether[i] == "system:user:edie") {
          setIsTrue(true);
        } else {
          setIsTrue(false);
        }
      }
    }
    myForm.setFieldsValue({
      userName: e.userName,
      userEmail: e.userEmail,
      gender: e.roleName,
      role: e.roleId,
      contact: e.contact,
      phone: e.phone,
      nodeIds: e.nodeIds,
      address: e.address,
      business: e.business,
    });
  };
  const addbtn = () => {
    setExpireTime("");
    setShowExpireTimeInput(true);
    allRoleTypes();
    setIsModalVisible(true);
    setAdd(0);
    setUseredit(1);
  };
  // 删除
  const del = (e) => {
    confirm({
      title: "提示",
      icon: <ExclamationCircleOutlined />,
      content: "确定要删除吗？",
      cancelText: "取消",
      okText: "确定",
      cancelButtonProps: {
        className: "newbutton",
        style: { background: "none" },
      },
      onOk() {
        http
          .post(
            "system_management/system_user/user/deleteSysUser?sysUserId=" +
              e.userId
          )
          .then((res) => {
            if (res.data.code == 200) {
              message.success("删除成功");

              setPage(1);
              setCurrentNum(1);
              queryAllSysUserPageable();
            } else {
              message.warning(res.data.msg);
            }
          })
          .catch((err) => {
            console.log(err);
          });
      },
      onCancel() {
        console.log("Cancel");
      },
    });
  };
  // 角色选择
  const roleChange = (e, mode) => {
    setRoleName(mode.children);
    setIsEdit(e);
    setRoleKey(e);
    setUseredit(mode.value == 1 || mode.value == 2 ? 1 : 0);
  };
  // 地图
  const getMap = (e) => {
    setAddress(e.addr);
    setMapData(e);
  };
  // 节点列表
  const nodeNameList = () => {
    http.post("system_management/node_model/nodeNameList").then((res) => {
      if (res.data.code == 200) {
        // res.data.data
        let data = res.data.data;
        data.map((res, index) => {
          return (res.label = res.nodeName), (res.value = res.id);
        });
        setNodeList(data);
      }
    });
  };
  // 重置
  const resest = (record) => {
    setPawVisible(true);
    setPawName(record.userName);
    setUserId(record.userId);
  };
  // 签约
  const single = (record) => {
    var index = record.shareRatio.indexOf(".");
    var resolve = record.shareRatio.substring(0, index);
    setSingisible(true);
    setUserId(record.userId);
    singForm.setFieldsValue({
      shareRatio: resolve,
    });
  };

  // 新建用户
  const onFinish = (values) => {
    setSetLoadings(true);
    if (add == 0) {
      // 新增
      let passwords = md5(values.userPassword);
      http
        .post("system_management/system_user/user/addSysUser", {
          address: useredit == 0 ? address : "", //地址
          business: useredit == 0 ? values.business : "", //公司业务
          configType: "",
          contact: useredit == 0 ? values.contact : "", //联系人
          nodeIds: useredit == 0 ? values.nodeIds : [], //节点
          phone: values.phone, //电话
          roleId: values.role, //角色id
          roleName: roleName, //角色
          roleTypeName: values.role, //角色类型
          shareRatio: "", //需求响应分成比例
          userEmail: values.userEmail,
          userId: userId,
          userName: values.userName,
          userPassword: passwords,
          expirationTime: expireTime,
        })
        .then((res) => {
          if (res.data.code == 200) {
            message.success("添加成功");
            setIsModalVisible(false);
            setSetLoadings(false);
            queryAllSysUserPageable();
            myForm.resetFields();
          } else {
            setSetLoadings(false);
            message.warning(res.data.msg);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    } else if (add == 1) {
      //  编辑
      http
        .post("system_management/system_user/user/updateSysUserRole", {
          address: useredit == 0 ? address : "", //地址
          business: useredit == 0 ? values.business : "", //公司业务
          contact: useredit == 0 ? values.contact : "", //联系人
          nodeIds: useredit == 0 ? values.nodeIds : [], //节点
          phone: values.phone, //电话
          roleId: values.role, //角色id
          roleName: roleName, //角色
          roleTypeName: values.role, //角色类型
          shareRatio: "", //需求响应分成比例
          userEmail: values.userEmail,
          userId: userId,
          userName: values.userName,
          expirationTime: expireTime,
          // "userPassword": values.userPassword
        })
        .then((res) => {
          if (res.data.code == 200) {
            message.success("编辑成功");
            queryAllSysUserPageable();
            setIsModalVisible(false);
            setSetLoadings(false);
            myForm.resetFields();
          } else {
            myForm.resetFields();
            setIsModalVisible(false);
            setSetLoadings(false);
            message.warning(res.data.msg);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    }
  };

  const onFinishFailed = (errorInfo: any) => {
    console.log("Failed:", errorInfo);
  };
  // 重置密码
  const pawonFinish = (values) => {
    let passwords = md5(values.userPassword);
    http
      .post("system_management/system_user/user/resetPassword", {
        newPassWord: passwords,
        userId: userId,
      })
      .then((res) => {
        if (res.data.code == 200) {
          setPawVisible(false);
          pawForm.resetFields();
          queryAllSysUserPageable();
          message.success("成功");
        } else {
          message.info(res.data.msg);
        }
      });
  };
  const pawonFinishFailed = (errorInfo: any) => {
    console.log("Failed:", errorInfo);
  };
  // pawCancel
  const pawCancel = () => {
    setPawVisible(false);
  };
  const pawonReset = () => {
    setPawVisible(false);
    pawForm.resetFields();
  };
  // 签约
  const singonFinish = (values) => {
    http
      .post("system_management/system_user/user/updateSysUserShareRatio", {
        shareRatio: values.shareRatio,
        userId: userId,
      })
      .then((res) => {
        if (res.data.code == 200) {
          setSingisible(false);
          queryAllSysUserPageable();
          message.success("成功");
        } else {
          message.info(res.data.msg);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  const singonFinishFailed = (errorInfo: any) => {
    console.log("Failed:", errorInfo);
  };
  // pawCancel
  const singCancel = () => {
    setSingisible(false);
    singForm.resetFields();
  };
  const singonReset = () => {
    setSingisible(false);
    singForm.resetFields();
  };
  //
  const handleCancel = () => {
    setIsModalVisible(false);
    myForm.resetFields();
  };
  const onReset = () => {
    setIsModalVisible(false);
    myForm.resetFields();
  };
  // 分页
  const onChange: PaginationProps["onChange"] = (page) => {
    setPage(page.current);
    setPages(page.current);
    setCurrentNum(page.current);
  };
  const prefixSelector = (
    <Form.Item name="prefix" noStyle>
      86
    </Form.Item>
  );
  const columns = [
    {
      title: "序号",
      width: 80,
      render: (value, item, index) => (page - 1) * 10 + index + 1,
    },
    {
      title: "用户名",
      dataIndex: "userName",
      key: "userName",
      width: "10%",
    },

    {
      title: "邮箱",
      dataIndex: "userEmail",
      key: "userEmail",
      render: (text, record) => {
        return text ? text : "-";
      },
    },
    {
      title: "手机号",
      dataIndex: "phone",
      key: "phone",
      render: (text, record) => {
        return text ? text : "-";
      },
    },
    {
      title: "系统内置",
      dataIndex: "configType",
      key: "configType",
      render: (text, record, index) => {
        if (record.configType == "Y") {
          return "系统内置";
        } else if (record.configType == "N") {
          return "自定义";
        }
      },
    },
    {
      title: "角色名称",
      dataIndex: "roleName",
      key: "roleName",
    },
    {
      title: "角色类型",
      dataIndex: "roleTypeName",
      key: "roleTypeName",
    },
    {
      title: "需求响应签约比例",
      dataIndex: "shareRatio",
      key: "shareRatio",
      render: (text, record, index) => {
        var index = text.indexOf(".");
        var resolve = text.substring(0, index);
        return resolve + "%";
      },
    },

    {
      title: "操作",
      dataIndex: "action",
      key: "action",
      width: "20%",
      render: (text, record, index) => {
        if (record.configType == "Y") {
          return (
            <Space className="infono">
              <InfoCircleOutlined />
              此用户不可删除和编辑
            </Space>
          );
        } else {
          return (
            <Space size="middle">
              <a onClick={() => edit(record)}>编辑 </a>
              <a onClick={() => del(record)}>删除</a>
              <a
                onClick={() => resest(record)}
                style={{
                  display:
                    changePaw == "system:user:change_pwd" ? "block" : "none",
                }}
              >
                重置密码
              </a>
              <a
                onClick={() => single(record)}
                style={{ display: record.roleKey == 3 ? "block" : "none" }}
              >
                签约
              </a>
            </Space>
          );
        }
      },
    },
  ];
  return (
    <div className="allcontented parameter">
      <div className="hybridBody">
        <div style={{ overflow: "hidden" }}>
          {wether.length > 0 ? (
            wether.length &&
            wether.map((res) => {
              if (res == "system:user:add") {
                return (
                  <Button
                    type="primary"
                    onClick={addbtn}
                    style={{ marginBottom: 20 }}
                  >
                    <PlusOutlined />
                    新建用户
                  </Button>
                );
              }
            })
          ) : (
            <Button type="primary" disabled style={{ marginBottom: 20 }}>
              新建用户
            </Button>
          )}
          {wether.length > 0 ? (
            wether.length &&
            wether.map((res) => {
              if (res == "system:user:query") {
                return (
                  <Search
                    placeholder="搜索用户名"
                    onSearch={onSearch}
                    style={{ width: 200, float: "right" }}
                  />
                );
              }
            })
          ) : (
            <Search
              placeholder="搜索用户名"
              disabled
              style={{ width: 200, float: "right" }}
            />
          )}
        </div>

        <Table
          className="tabls"
          columns={columns}
          dataSource={dataSource}
          rowKey={(record) => {
            return record.configId;
          }}
          pagination={{
            total: total, //数据的总条数
            defaultCurrent: 1, //默认当前的页数
            defaultPageSize: 10, //默认每页的条数
            showSizeChanger: false,
            current: currentNum,
          }}
          onChange={onChange}
          loading={loading}
        />
        <Modal
          title={add == 0 ? "新建用户" : "编辑用户"}
          onCancel={handleCancel}
          visible={isModalVisible}
          footer={null}
          maskClosable={false}
        >
          <Form
            form={myForm}
            name="basic"
            labelCol={{ span: 4 }}
            wrapperCol={{ span: 20 }}
            initialValues={{ remember: true }}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
            autoComplete="off"
          >
            <Form.Item
              name="role"
              label="角色"
              rules={[{ required: true, message: "请选择" }]}
            >
              <Select
                placeholder="请选择"
                allowClear
                disabled={add == 1 && isTrue ? true : false}
                onChange={roleChange}
              >
                {RoleType.length &&
                  RoleType.map((res, index) => {
                    return <Option value={res.roleId}>{res.roleName}</Option>;
                  })}
              </Select>
            </Form.Item>
            <Form.Item
              label="用户名"
              name="userName"
              rules={[
                {
                  required: true,
                  pattern: new RegExp(/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/),
                  message: "请输入6～12个汉字、字母或数字、_",
                },
              ]}
            >
              <Input autocomplete="off" />
            </Form.Item>

            <Form.Item
              label="用户密码"
              name="userPassword"
              style={{ display: add == 1 ? "none" : "block" }}
              rules={[
                {
                  required: add == 1 ? false : true,
                  pattern:
                    /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[~!.@#$%^&*])[\da-zA-Z~!.@#$%^&*]{8,100}$/,
                  message: "不能小于8位字符，必须包括字母,数字和特殊字符",
                },
              ]}
            >
              <Input.Password autoComplete="new-password" />
            </Form.Item>
            <Form.Item
              label="联系人"
              name="contact"
              rules={[
                { required: useredit == 1 ? false : true, message: "联系人" },
              ]}
              style={{
                display: useredit == 1 || useredit == 2 ? "none" : "block",
              }}
            >
              <Input maxLength={50} />
            </Form.Item>

            <Form.Item
              name="nodeIds"
              label="节点"
              rules={[
                { required: useredit == 1 ? false : true, message: "节点" },
              ]}
              style={{ display: useredit == 1 ? "none" : "block" }}
            >
              <Select
                mode="multiple"
                allowClear
                style={{ width: "100%" }}
                placeholder="节点"
                options={nodeList}
              />
            </Form.Item>
            <Form.Item
              style={{ display: useredit == 1 ? "none" : "block" }}
              label="公司地址"
              name="address"
            >
              <BaiduMap
                getMap={getMap}
                address={address}
                istrue={istrue}
                changeData={ChildrenChange}
              />
            </Form.Item>
            <Form.Item
              name="phone"
              label="手机号"
              rules={[
                {
                  // required:true,
                  label: "手机号",
                  pattern: new RegExp(/^1(3|4|5|6|7|8|9)\d{9}$/, "g"),
                  message: "请输入正确格式的手机号",
                },
              ]}
            >
              <Input addonBefore={prefixSelector} style={{ width: "100%" }} />
            </Form.Item>
            <Form.Item
              label="邮箱"
              name="userEmail"
              rules={[
                {
                  label: "邮箱",
                  pattern: new RegExp(
                    /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/
                  ),
                  message: "请输入正确格式邮箱",
                },
              ]}
            >
              <Input maxLength={50} />
            </Form.Item>
            <Form.Item
              style={{ display: useredit == 1 ? "none" : "block" }}
              label="公司业务"
              rules={[
                {
                  pattern: new RegExp(/^[\S]{0,50}$/),
                  message: "请输入0-50个以内的字符",
                },
              ]}
              name="business"
            >
              <TextArea></TextArea>
            </Form.Item>
            <Form.Item label="到期时间">
              {expireTime || !showExpireTimeInput ? (
                <DatePicker
                  placeholder="请选择到期时间"
                  value={expireTime ? dayjs(expireTime, "YYYY-MM-DD") : null}
                  style={{ width: "100%" }}
                  onChange={(date, dateString) => {
                    setExpireTime(dateString);
                  }}
                  renderExtraFooter={() => (
                    <div
                      style={{
                        display: "flex",
                        alignContent: "center",
                        justifyContent: "center",
                        cursor: "pointer",
                      }}
                      onClick={() => {
                        setExpireTime(null);
                        setShowExpireTimeInput(true);
                      }}
                    >
                      无限制
                    </div>
                  )}
                />
              ) : (
                <Input
                  value="无限制"
                  onClick={() => {
                    setShowExpireTimeInput(false);
                  }}
                  onFocus={() => {}}
                  onBlur={() => {}}
                />
              )}
            </Form.Item>
            <Form.Item
              wrapperCol={{ offset: 14, span: 10 }}
              style={{ textAlign: "right" }}
            >
              <Button ghost onClick={onReset}>
                取消
              </Button>
              <Button
                style={{ marginLeft: 24 }}
                loading={setLoadings}
                type="primary"
                htmlType="submit"
              >
                确定
              </Button>
            </Form.Item>
          </Form>
        </Modal>
        <Modal
          title="重置密码"
          visible={pawVisible}
          onCancel={pawCancel}
          footer={null}
          maskClosable={false}
        >
          <Form
            form={pawForm}
            onFinish={pawonFinish}
            onFinishFailed={pawonFinishFailed}
            autoComplete="off"
          >
            <Form.Item>
              <p className="pasname">请输入“{pawName}”的新密码</p>

              <Form.Item
                name="userPassword"
                rules={[
                  {
                    required: true,
                    pattern:
                      /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[~!.@#$%^&*])[\da-zA-Z~!.@#$%^&*]{8,100}$/,
                    message: "不能小于8位字符，必须包括字母,数字和特殊字符",
                  },
                ]}
              >
                <Input.Password autoComplete="new-password" />
              </Form.Item>
            </Form.Item>
            <Form.Item
              wrapperCol={{ offset: 14, span: 10 }}
              style={{ textAlign: "right" }}
            >
              <Button ghost onClick={pawonReset}>
                取消
              </Button>
              <Button
                type="primary"
                style={{ marginLeft: 24 }}
                htmlType="submit"
              >
                确定
              </Button>
            </Form.Item>
          </Form>
        </Modal>
        <Modal
          title="签约"
          visible={singisible}
          onCancel={singCancel}
          footer={null}
          maskClosable={false}
        >
          <Form
            form={singForm}
            onFinish={singonFinish}
            onFinishFailed={singonFinishFailed}
            autoComplete="off"
            labelCol={{ span: 7 }}
            wrapperCol={{ span: 16 }}
          >
            <Form.Item label="需求响应分成比例">
              <Form.Item
                style={{ width: 260, float: "left" }}
                name="shareRatio"
                rules={[
                  {
                    required: true,
                    pattern: new RegExp(/(^[1-9][0-9]$)|(^100&)|(^[1-9]$)$/),
                    message: "请输入1-100的整数数字",
                  },
                ]}
              >
                <Input />
              </Form.Item>
              <span className="unitName">%</span>
            </Form.Item>

            <Form.Item
              wrapperCol={{ offset: 14, span: 10 }}
              style={{ textAlign: "right" }}
            >
              <Button ghost style={{ marginRight: 15 }} onClick={singonReset}>
                取消
              </Button>
              <Button type="primary" htmlType="submit">
                确定
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default Personnel;
