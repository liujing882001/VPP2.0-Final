import React, { Component } from 'react';
import { Form, Input, Button ,Upload,message,
Table,Divider,Modal,Checkbox ,Select} from 'antd';
// import basePath from '../../../axios/common';
import axios from 'axios';
// import api from '../../../utils/api'
import http from '../../../server/server.js'
import '../personnel/personnel.css';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import md5 from 'js-md5';

React.Component.prototype.$md5 = md5;
const { confirm } = Modal;

// import { SearchOutlined,PlusOutlined } from '@ant-design/icons';
const { Search } = Input;

const data = [
  {
    key: '1',
    name: 'John Brown',
    age: 32,
    address: 'New York No. 1 Lake Park',
  },
  {
    key: '2',
    name: 'Jim Green',
    age: 42,
    address: 'London No. 1 Lake Park',
  },
  {
    key: '3',
    name: 'Joe Black',
    age: 32,
    address: 'Sidney No. 1 Lake Park',
  },
  {
    key: '4',
    name: 'Disabled User',
    age: 99,
    address: 'Sidney No. 1 Lake Park',
  },
];
const formItemLayout = {
	labelCol: {
		xs: { span: 28 },
		sm: { span: 8 },
	},
	wrapperCol: {
		xs: { span: 20 },
		sm: { span: 16 },
	},
};
const tailLayout = {
  wrapperCol: { offset: 8, span: 16 },
};
const { Option } = Select;

const rowSelection = {
	onChange: (selectedRowKeys, selectedRows) => {
		console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
	}

};
function getBase64(img, callback) {
  const reader = new FileReader();
  reader.addEventListener('load', () => callback(reader.result));
  reader.readAsDataURL(img);
}

function beforeUpload(file) {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    message.error('You can only upload JPG/PNG file!');
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    message.error('Image must smaller than 2MB!');
  }
  return isJpgOrPng && isLt2M;
}
function del(e){
	confirm({
	    title: '提示',
	    content: '本次操作将会删除员工信息，是否继续？',
		cancelText:'取消',
		okText:'确定',
	    onOk() {
	      console.log('OK');
	    },
	    onCancel() {
	      console.log('Cancel');
	    },
	  });	
}
class Personnel extends Component {
	constructor() {
		super()
		this.state={
			ModalText: 'Content of the modal',
			visible: false,
			confirmLoading: false, 
			loading: false,
			imageUrl :'',
			dataSource:[],
			
		}
	}
	componentDidMount(){
		this.queryAllSysUser()
	}
	del =(e) =>{
		console.log(e)
		let userId = e.userId
		Modal.confirm({
		    title: '确定要删除吗',
		    icon: <ExclamationCircleOutlined />,
		    // content: 'Bla bla ',
		    okText: '确定',
		    cancelText: '取消',
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
			onOk: () => {
				http.post('sysUser/deleteSysUser?sysUserId='+userId).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success(res.data.msg);
						this.queryAllSysUser()
						
					}else{
						message.info(res.data.msg);
				
					}
				})
			}
			,
			onCancel() {
				console.log('Cancel');
			},
		  });
		// http.post('sysUser/deleteSysUser?sysUserId='+userId).then(res =>{
		// 	console.log(res)
		// 	if(res.data.code==200){
		// 		message.success(res.data.msg);
		// 	}else{
		// 		message.info(res.data.msg);

		// 	}
		// })
		// alert(0)
	}
	queryAllSysUser(){
		http.post('sysUser/queryAllSysUser').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let dataSource = []
				let data  = res.data.data
				this.setState({
					dataSource:res.data.data
				})
			}
		})
	}
	handleSubmit = e => {
	    e.preventDefault();
	    this.props.form.validateFields((err, values) => {
	      if (!err) {
	        console.log('Received values of form: ', values);
	      }
	    });
	}
	showModal = () => {
	    this.setState({
			visible: true,
	    });
	};
	handleOk = (values) => {
		console.log(values)
		console.log(this.refs.myForm.getFieldsValue())
		let data = this.refs.myForm.getFieldsValue()
	};
	
	handleCancel = () => {
	    console.log('Clicked cancel button');
	    this.setState({
	      visible: false,
	    });
	};
	normFile = e => {
	    console.log('Upload event:', e);
	    if (Array.isArray(e)) {
			return e;
	    }
	    return e && e.fileList;
	};
	handleChange = info => {
	    if (info.file.status === 'uploading') {
			this.setState({ loading: true });
			return;
	    }
	    if (info.file.status === 'done') {
	      // Get this url from response in real world.
			getBase64(info.file.originFileObj, imageUrl =>
				this.setState({
					imageUrl,
					loading: false,
				}),
			);
	    }
	};
	cancel =() =>{
		this.setState({
			visible: false,
		});
	}
	search = (val) =>{
		console.log(val)
		http.post('sysUser/checkSysUserName?sysUserName='+val).then(res =>{
			console.log(res)
		})
	}
	del(){
		confirm({
		    title: '提示',
		    content: '本次操作将会删除员工信息，是否继续？',
			cancelText:'取消',
			okText:'确定',
		    onOk() {
		      console.log('OK');
		    },
		    onCancel() {
		      console.log('Cancel');
		    },
		});	
	}
	point(){
		confirm({
		    title: '提示',
		    content: '本次操作将会覆盖所有员工信息，是否继续？',
			cancelText:'取消',
			okText:'确定',
		    onOk() {
		      console.log('OK');
		    },
		    onCancel() {
		      console.log('Cancel');
		    },
		});	
	}
	render(){
		const columns = [
			{
				title: '序号',
				width: '10%',
				render:(text,record,index)=> `${index+1}`,
			},
			{
				title: '用户名',
				dataIndex: 'userName',
				// render: text => <a>{text}</a>,
			},
			{
				title: '邮箱',
				dataIndex: 'userEmail',
			},
			{
				title: '管理员或普通用户',
				dataIndex: 'roleType',
				render:(text, record) =>{
					if(record.roleType =='ADMIN'){
						return '管理员'
					}else if(record.roleType =='CONSUMER'){
						return '普通用户'
					}
				}
			},
			
			{
			    title: '操作',
			    key: 'action',
			    render: (text, record) => (
					<span>
						<a onClick={() => this.del(record)}>删除</a>
					</span>
			    ),
			  },
		];
		// const { getFieldDecorator } = this.props.form;
		// const prefixSelector = getFieldDecorator('prefix', {
		//      initialValue: '中国86',
		// })
		// const uploadButton = (
		//       <div>
		//         <div className="ant-upload-text">Upload</div>
		//       </div>
		// );
		const onFinish = (values) => {
			console.log(values)
			let passwords = md5(values.password)
			let user = values.username
			http.post('sysUser/checkSysUserName?sysUserName='+user,{
				
			}).then(res =>{
				if(res.data.code ==200){
					http.post('sysUser/addSysUser',{
						"configType": "",
						"roleType": values.roletype,
						"userEmail": values.email,
						"userId": "",
						"userName":values.username,
						"userPassword": passwords
					}).then(res =>{
						console.log(res)
						if(res.data.code ==200){
							message.success('添加成功')
							setTimeout(() => {
								this.setState({
									visible: false,
								});
							}, 1000);
							this.refs.myForm.resetFields()
							this.queryAllSysUser()
						}else{
							message.info(res.data.msg)
						}
					})
				}else{
					message.info(res.data.msg)
				}
			})
			
		}
		const { imageUrl } = this.state;
		const { visible, confirmLoading, ModalText ,dataSource} = this.state;
		return(
			<div className="personal">
				<div className="person">
					<Button type="primary" onClick={this.showModal}>新建用户</Button>
					<Search
						placeholder="模糊搜索"
						onSearch={this.search}
						style={{ width: 200 }}
						disabled 
					/>
					
				</div>
				<div className="alltable">
					<Table rowSelection={rowSelection} columns={columns} dataSource={dataSource} />
				</div>
				<Modal
					title="新建用户"
					cancelText="取消"
					okText=""
					visible={visible}
					onOk={this.handleOk}
					confirmLoading={confirmLoading}
					onCancel={this.handleCancel}
					style={{ top: 'center' }}
					footer={null}
				>	
					<Form onSubmit={this.handleSubmit} {...formItemLayout} 
					onFinish={onFinish}
					ref="myForm" className="newform">
						<Form.Item label="用户名" name="username"
						rules={[{ required: true, message: '请输入用户名' }]}
						getValueFromEvent={(event) => {
							//正则匹配的是汉字和数字
							return event.target.value.replace(/[\u4e00-\u9fa50-9]/g,'')
						}}
						>
							
								<Input
								 
								  placeholder="请输入用户名"
								/>
							
						</Form.Item>
						<Form.Item label="密码" name="password"
							rules={[{ required: true, message: '请输入用密码' }]}
						>
							
								 <Input.Password />
							
						</Form.Item>
					    <Form.Item label="邮箱" name="email" 
							rules={[{
								label:"邮箱",
								pattern: new RegExp(/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/),
								message: '请输入正确格式邮箱'
							}]}
							// rules={[{ required: true, message: '请输入用邮箱' }]}
						
						>
						   <Input
						    
						     placeholder="请输入邮箱"
						   />
						   							
						</Form.Item> 
					
						<Form.Item label="管理员或普通用户" name="roletype">
							<Select
								placeholder="请选择"
							>
								<Option value="ADMIN">管理员</Option>
								<Option value="CONSUMER">普通用户</Option>
							</Select>
							
						</Form.Item>
						<Form.Item {...tailLayout}>
						  <Button type="primary" htmlType="submit">
						    确定
						  </Button>
						  <Button type="button"  onClick={this.cancel}>
						    取消
						  </Button>
						</Form.Item>
					</Form>
					
				</Modal>
			</div>
		)
	}
		
}
export default Personnel;