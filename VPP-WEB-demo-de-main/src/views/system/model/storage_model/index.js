import React, { useEffect,useState } from 'react';
import { Table ,Space,Form,Button, Checkbox, Input,Modal,Select ,message} from 'antd';
import http from '../../../../server/server.js'
import './index.css'
import md5 from 'js-md5';
React.Component.prototype.$md5 = md5;

const { Option } = Select;
const Storage_model =() =>{
	const [dataSource, setDataSource] = useState([]);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [add, setAdd] = useState(0);
	const [RoleType, setRoleType] = useState([]);
	const [userId, setUserId] = useState('');
	const [page, setPage] = useState(1);
	const [total, setTotal] = useState('');
	const [loading, setLoading] = useState(false);
	const [myForm] = Form.useForm();
	
	
	// 列表
	const parameter=() =>{
		setLoading(true)
		const paramsList = new URLSearchParams() // 切记不要let paramsList = {type: 1, userList: userList}
		paramsList.append('number',page) // 参数1为参数名，参数2为参数内容
		paramsList.append('pageSize', 10)
		http.post('system_management/energy_model/model_parameter/modelParameterListPageable',paramsList).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setDataSource(res.data.data.content);
				setTotal(res.data.data.totalElements);
				setLoading(false);
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	
	// 获取类型
	const allRoleTypes=() =>{
		http.post('system_management/system_user/role/allRoleTypes').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setRoleType(res.data.data)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const edit =(e) =>{
		console.log(e)
		setIsModalVisible(true);
		setAdd(1);
		setUserId(e.userId);
		myForm.setFieldsValue({
			username: e.userName,
			userEmail: e.userEmail,
			gender:e.roleName
		});
	}
	// const add = () =>{
		
	// 	setIsModalVisible(true);
	// 	setAdd(0);
	// 	allRoleTypes()
	// }
	const del =(e) =>{
		console.log(e)
		
		http.post('system_management/system_user/user/deleteSysUser?sysUserId='+e.userId).then(res =>{
			if(res.data.code ==200){
				message.success('删除成功')
				parameter()
			}else{
				message.warning(res.data.msg)
			}
		}).catch(err =>{
			
		})
	}
	
	// 新建用户
	const onFinish = (values) => {
		console.log('Success:', values);
		let passwords = md5(values.password)
		if(add==0){
			http.post('system_management/system_user/user/addSysUser',{
				"configType": "",
				"roleId": "",
				"roleType": values.gender,
				"userEmail":values.userEmail,
				"userId": "",
				"userName": values.username,
				"userPassword": passwords
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('添加成功');
					setIsModalVisible(false);
					parameter()
					myForm.resetFields();
				}else{
					message.warning(res.data.msg)
				}
			}).catch(err =>{
				
			})
		}else if(add==1){
			http.post('system_management/system_user/user/updateSysUserRole',{
				"configType": "",
				"roleId": "",
				"roleType": values.gender,
				"userEmail":values.userEmail,
				"userId": userId,
				"userName": values.username,
				"userPassword": passwords
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('编辑成功')
					parameter();
					setIsModalVisible(false);
					myForm.resetFields();
				}else{
					message.warning(res.data.msg)
				}
			}).catch(err =>{
				
			})
		}
		
		
	};
	
	const onFinishFailed = (errorInfo: any) => {
		console.log('Failed:', errorInfo);
	};
	const handleCancel =() =>{
		setIsModalVisible(false);
	}
	const onReset =() =>{
		myForm.resetFields();
		setIsModalVisible(false);
		
	}
	const handlePagination =(page) =>{
		console.log(page)
		setPage(page)
	}
	useEffect(() =>{
		parameter()
	},[page])
	const columns =[

		{
			title: '参数名称',
			dataIndex: 'modelName',
			key: 'modelName',
		},
		{
			title: '参数键名',
			dataIndex: 'modelKey',
			key: 'modelKey',
		},
		{
			title: '参数类型',
			dataIndex: 'modelKeyType',
			key: 'modelKeyType',
			render: (text,record,_,action) =>{
				if(text==1){
					return '设备'
				}else if(text==2){
					return '点位'
				}
			}
		},
		// render()
		// {
		//     title: '系统内置',
		//     dataIndex: 'configType',
		//     key: 'configType',
		// },
		{
			title: '备注',
			dataIndex: 'modelMark',
			key: 'modelMark',
		}
		
	]
	return(
		
		<div className="allcontented">
		<div className="storage_model">
			<Table
				className="tabls"
				columns={columns}
				dataSource={dataSource}
				rowKey={record =>{
					return record.configId
				}}
				loading={loading}
				// pagination={false}
				pagination={
					{
						total: total,//数据的总条数
						defaultCurrent: 1,//默认当前的页数
						defaultPageSize: 10,//默认每页的条数
						showSizeChanger:false,
						onChange: handlePagination,
					}
				}
			/>
		   <Modal title={add==0?'新建用户':'编辑用户'} onCancel={handleCancel} visible={isModalVisible}
				footer={null}
			 >
				<Form
					ref="myForm"
					  name="basic"
					  labelCol={{ span: 8 }}
					  wrapperCol={{ span: 16 }}
					  initialValues={{ remember: true }}
					  onFinish={onFinish}
					  onFinishFailed={onFinishFailed}
					  autoComplete="off"
					>
					  <Form.Item
						label="用户名"
						name="username"
						rules={[{ required: true, message: '请输入用户名' }]}
					  >
						<Input />
					  </Form.Item>
				
						<Form.Item
							label="密码"
							name="password"
							rules={[{ required: true, message: '请输入密码' }]}
						>
						<Input.Password />
					  </Form.Item>
						<Form.Item label="确认密码" name="pw2" rules={[
							{required: true,message: '请输入密码'},
							({getFieldValue})=>({
								validator(rule,value){
									if(!value || getFieldValue('password') === value){
										return Promise.resolve()
									}
									return Promise.reject("两次密码输入不一致")
								}
							})
						]}
						
						>
						<Input.Password palceholder="请确认密码" />
						</Form.Item>
						<Form.Item name="gender" label="角色" rules={[{ required: true ,message: '请选择'}]}>
							<Select
								placeholder="请选择"
								allowClear
							>
								{
									RoleType.length&&RoleType.map((res,index) =>{
										return <Option key={res.roleKey} value={res.roleKey}>{res.roleKeyDesc}</Option>
									})
								}
								
							</Select>
						</Form.Item>
						<Form.Item
						  label="邮箱"
						  name="userEmail"
						  rules={[{
							label:"邮箱",
							pattern: new RegExp(/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/),
							message: '请输入正确格式邮箱'
						  }]}
						  // rules={[{ required: true, message: '' }]}
						>
						  <Input />
						</Form.Item>
					  <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
					  <Button htmlType="button" style={{marginRight:15}} onClick={onReset}>
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
	)
}
	


export default Storage_model


