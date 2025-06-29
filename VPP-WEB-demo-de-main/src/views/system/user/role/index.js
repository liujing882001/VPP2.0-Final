import React,{useEffect,useState} from 'react'
import { Input,Button ,Table,Space ,Modal,Form,Select,message ,Tree } from 'antd';
import http from '../../../../server/server.js'
import { ExclamationCircleOutlined,PlusOutlined } from '@ant-design/icons';

import './index.css'

const { confirm } = Modal;
const { Search } = Input;

const { Option } = Select;
const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 20 },
};
const tailLayout = {
  wrapperCol: { offset: 17, span: 7 },
};
const { TextArea } = Input;
const Role =() =>{
	const [dataSource, setDataSource] = useState([]);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [RoleType, setRoleType] = useState([]);
	const [Visible, setVisible] = useState(false);
	const [roleId, setRoleId] = useState('');
	const [Visible1, setVisible1] = useState(false);
	const [treeData, setTreeData] = useState([]);
	const [selectedKeys, setSelectedKeys] = useState([]);
	const [trees, setTrees] = useState('');
	const [autoExpandParent, setAutoExpandParent] = useState('');
	const [defaultExpandAll, setDefaultExpandAll] = useState(true);
	const [random, setRandom] = useState('');
	const [checkedKeys, setCheckedKeys] = useState([]);
	const [checkStrictly, setCheckStrictly] = useState(true);
	const [loading, setLoading] = useState(true);
	const [look, setLook] = useState(1);
	const [page, setPage] = useState(1);
	const [total, setTotal] = useState('');
	const [roleName, setRoleName] = useState('');
	const [edits, setEdits] = useState(0);
	const [expandedKeys,setExpandedKeys] =useState([]);
	const [currentNum,setCurrentNum] =useState(1)
	const [myForm1] =  Form.useForm();
	const [myForm] =  Form.useForm();
	
	useEffect(() =>{
		allUserRoles()
	},[page])
	useEffect(() =>{
		if(roleName){
			allUserRoles()
		}else if(roleName==''){
			allUserRoles()
		}
	},[roleName])
	// 获取角色列表
	const allUserRoles =() =>{
		setLoading(true)
		const params = new URLSearchParams();
		params.append('number', page);
		params.append('pageSize', 10);
		params.append('roleName', roleName);
		http.post('system_management/system_user/role/allRoleListByNamePageable',params).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setDataSource(res.data.data.content);
				setLoading(false);
				setTotal(res.data.data.totalElements)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 获取类型
	const allRoleTypes =() =>{
		http.post('system_management/system_user/role/allRoleTypes').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setRoleType(res.data.data)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 修改
	const edit =(e) =>{

		setVisible(true);
		setRoleId(e.roleId);
		setEdits(1)
		myForm1.setFieldsValue({
			sname:e.roleName,
			roleLabel:e.roleLabel
		})
	}
	// 删除
	const del =(e) =>{
		console.log(e)
		let roleId = e.roleId
		
		confirm({
		    title: '确定要删除吗?',
		    icon: <ExclamationCircleOutlined />,
			cancelText:'取消',
			okText:'确定',
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    onOk() {
				console.log('OK');
				http.post('system_management/system_user/role/deleteRoleById?roleId='+roleId).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('删除成功')
						allUserRoles()
					}else{
						message.warning(res.data.msg)
					}
				})
		    },
		    onCancel() {
				console.log('Cancel');
		    },
		  });
	}
	// 搜索
	const onSearch =(e) =>{
		console.log(e)
		setPage(1);
		setCurrentNum(1)
		setRoleName(e);
		
	}
	// 分配权限
	const power =(e) =>{
		console.log(e)
		let arr  = []
		setVisible1(true);
		setRoleId(e.roleId);
		setLook(0)
		http.post('system_management/system_user/menu/menuTree').then(res =>{
			if(res.data.code ==200){
				console.log(res)
				let data = res.data.data
				console.log(data)
				http.post('system_management/system_user/menu/menuListByRoleId?roleId='+e.roleId).then(res =>{
					if(res.data.code ==200){
						console.log(res)
						setSelectedKeys(res.data.data)
					}
				})
				http.post('system_management/system_user/menu/menuListByRoleId?roleId='+e.roleId).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						let tree = res.data.data
						for(var i=0;i<data.length;i++){
							if(data[i].children){
								console.log(data[i].children)
								for(var j=0;j<tree.length;j++){
									if(tree[j] ==data[i].key){
										tree.splice(j,1)
									}
								}
								let children = data[i].children
								for(var s=0;s<children.length;s++ ){
									if(children[s].children){
										console.log(children[s],'-----')
										for(var j=0;j<tree.length;j++){
											if(tree[j] ==children[s].key){
												console.log(children[s])
												if(children[s].children){
													console.log(children[s].children)
													tree.splice(j,1)
												}
												
		
											}
										}
										let schildren = children[s].children
										console.log(schildren,'key')
										if(schildren){
											for(var a = 0;a<schildren.length;a++){
												console.log(schildren[a])
												for(var c=0;c<tree.length;c++){
													if(tree[c] == schildren[a].key){
														if(schildren[a].children){
															tree.splice(c,1)
														}
			
													}
												}
											}
										}
									}
								}
							}else{
								for(var j=0;j<tree.length;j++){
									if(tree[j] ==data[i].key){
										console.log(data[i].key)
										console.log(tree[j])
										console.log(tree)
										
									}
								}
							}
							
						}
						setTrees([...res.data.data]);
						setTreeData([...data]);
						setCheckedKeys([...res.data.data])
						
						
					}
				}).catch(err =>{
					console.log(err)
				})
			}
		}).catch(err =>{
			console.log(err)
		})
		
	}
	
	const onExpand=(expandedKeys) => {

		setExpandedKeys(expandedKeys)
	};
	const onCheck=(checkedKeys,info)=>{ //选中事件
	   console.log(checkedKeys)
	   console.log(info)
	   let checkedNodes = []
	   checkedKeys.map(item =>{
		   checkedNodes.push(item)
	   })
	   console.log(checkedNodes)
	   if(info.halfCheckedKeys &&info.halfCheckedKeys.length>0){
		   info.halfCheckedKeys.map(res =>{
			   checkedNodes.push(res)
		   })
		   
	   }
	   setCheckedKeys(checkedKeys);
	   setSelectedKeys(checkedNodes)
	 } 
	// 新建
	const add =() =>{
		setVisible(true);
		setEdits(0)
	}
	 // 查看
	const lookover =(e) =>{
		let arr  = []
		setVisible1(true);
		setRoleId(e.roleId);
		setLook(1)
		http.post('system_management/system_user/menu/menuTree').then(res =>{
			if(res.data.code ==200){
				let data = res.data.data
				console.log(data)
				http.post('system_management/system_user/menu/menuListByRoleId?roleId='+e.roleId).then(res =>{
					if(res.data.code ==200){
						// console.log(res.data.data)
						let tree = res.data.data
						for(var i=0;i<data.length;i++){
							if(data[i].children){
								console.log(data[i].children)
								for(var j=0;j<tree.length;j++){
									if(tree[j] ==data[i].key){
										tree.splice(j,1)
									}
								}
								let children = data[i].children
								for(var s=0;s<children.length;s++ ){
									if(children[s].children){
										console.log(children[s],'-----')
										for(var j=0;j<tree.length;j++){
											if(tree[j] ==children[s].key){
												console.log(children[s])
												if(children[s].children){
													console.log(children[s].children)
													tree.splice(j,1)
												}
												
				
											}
										}
										let schildren = children[s].children
										console.log(schildren,'key')
										if(schildren){
											for(var a = 0;a<schildren.length;a++){
												console.log(schildren[a])
												for(var c=0;c<tree.length;c++){
													if(tree[c] == schildren[a].key){
														if(schildren[a].children){
															// alert(0)
															tree.splice(c,1)
														}
														console.log(schildren[a].title,'jjjjjjjjjjjjjjjj')
			
													}
												}
											}
										}
									}
								}
							}else{
								for(var j=0;j<tree.length;j++){
									if(tree[j] ==data[i].key){
										// var index = this.indexOf(data[i].key);
										// console.log(index)
										// tree.splice(j,1)
										console.log(data[i].key)
										console.log(tree[j])
										console.log(tree)
									}
								}
							}
							
						}
						
						setTrees([...res.data.data]);
						setTreeData([...data]);
						setCheckedKeys([...res.data.data])
						
					}
				}).catch(err =>{
					console.log(err)
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	const columns =[
		{
			title: '序号',
			dataIndex: 'orderNum',
			key: 'orderNum',
			render:(value, item, index) => (page - 1) * 10 + index+1,
		},
		{
			title: '角色名称',
			dataIndex: 'roleName',
			key: 'roleName',
		},
		
		{
			title: '备注',
			dataIndex: 'roleLabel',
			key: 'roleLabel',
		},
		{
			title: '创建时间',
			dataIndex: 'createdTime',
			key: 'createdTime',
		},
		{
			title: '操作',
			dataIndex: 'action',
			key: 'action',
			render: (text, record, index) =>{
				if(record.configType =="Y"){
					return <Space size="middle">
									<a onClick={() => power(record)}>分配权限 </a>
									<a style={{display:'none'}} disabled>编辑 </a>
									
									<a style={{display:'none'}} disabled>删除</a>
								</Space>
								
					
				}else{
					return  <Space size="middle">
								<a onClick={() => power(record)}>分配权限 </a>
								<a onClick={() => edit(record)}>编辑 </a>
								<a onClick={() => del(record)}>删除</a>
							</Space>
				}
				
				  
				
			}

		}
	]
	// <a onClick={() => this.lookover(record)}>查看 </a>
	const showModal = () => {
		setIsModalVisible(true);
		allRoleTypes();
	};		
	const handleCancel = () => {
		setIsModalVisible(false)
	};

	// 新增
	const onFinish1 =(values) =>{
		console.log(values,'-------')
		if(edits==0){
			// 新增
			http.post('system_management/system_user/role/addRole',{
				"roleId": "",
				"roleKey": "",
				"roleLabel": values.roleLabel?values.roleLabel:'',
				"roleName": values.sname
				
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('添加成功')
					allUserRoles()
					myForm1.resetFields();
					setVisible(false)
				}else{
					message.warning(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})

		}else if(edits==1){
			// 编辑
			const params = new URLSearchParams();
			params.append('roleId', roleId);
			params.append('roleName', values.sname);
			params.append('roleLabel', values.roleLabel);
			http.post('system_management/system_user/role/updateRoleName',params).then(res =>{
				if(res.data.code ==200){
					message.success('编辑成功')
					setVisible(false)
					allUserRoles()
					myForm1.resetFields();
					
				}else{
					message.warning(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
	}
	const onFinishFailed = (errorInfo: any) => {
		console.log('Failed:', errorInfo);
	};
	const onReset = () => {
		myForm.resetFields();
		setIsModalVisible(false)
	};
	const onReset1 = () => {
		myForm1.resetFields();
		setVisible(false)
	};
	const handleCancel2 =() =>{
		myForm1.resetFields();
		setVisible(false);
	}
	const handleCancel3 =() =>{
		setVisible1(false);
	}
	const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
		console.log('selected', selectedKeys, info);
	};
	// 分配权限
	const handleOk = () => {
		http.post('system_management/system_user/role/updateRoleMenuIds',{
			"menuIds": selectedKeys,
			"roleId": roleId
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				message.success('成功')
				setTrees([]);
				setTreeData([]);
				setVisible1(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	
	};
	const TableChange =(page) =>{
		console.log(page)
		
		setPage(page.current)
		setCurrentNum(page.current)
	}
	// <Button type="primary" onClick={showModal}>添加角色</Button>
	return(
		<div className="role">
			<div className="roleheader">
			<Button type="primary" onClick={add}><PlusOutlined />新建角色</Button>
			<Search placeholder="名称" onSearch={onSearch} style={{ width: 200,float:'right' }} />
				
				
			</div>
			<Table dataSource={dataSource} columns={columns} loading={loading}
				pagination={
					{
						total: total,//数据的总条数
						defaultCurrent: 1,//默认当前的页数
						defaultPageSize: 10,//默认每页的条数
						showSizeChanger:false,
						current:currentNum
						
					}
				}
				onChange={TableChange}
			  />
			
			<Modal title={edits==0?'新增':'编辑'}
				footer={null}
				maskClosable={false}
				visible={Visible} 
				onCancel={handleCancel2}>
				<Form form={myForm1} {...layout} onFinish={onFinish1}>
					<Form.Item name="sname" label="角色名称" 
					// rules={[{ required: true ,message: '请填写名称' }]}
						rules={ [{ required: true,pattern: new RegExp(/^[a-zA-Z0-9_\u4e00-\u9fa5]{0,20}$/),message: '请填写正确的名称（20个以内的汉字、字母或数字、_）'}]}
					>
						<Input maxLength={50} />
					</Form.Item>
					<Form.Item name="roleLabel" label="备注">
						<TextArea maxLength={50} />
					</Form.Item>
						 
					<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
					
						<Button ghost onClick={onReset1} style={{marginRight:10}}>
							取消
						</Button>
						<Button  type="primary" htmlType="submit">
							确定
						</Button>
					</Form.Item>
				</Form>
			</Modal>
			<Modal title={look==1?'查看':'分配权限'}
				visible={Visible1} 
				className="menu_managementForm"
				// onOk={handleOk}
				onCancel={handleCancel3}
				okText="确定"
				cancelText="取消"
				footer={[
				<Button ghost onClick={handleCancel3}>取消</Button>,
				<Button key="submit" type="primary" onClick={handleOk}>确定</Button> ]}
				>
				
				{
					
					treeData.length && <Tree
					checkable
					multiple
					autoExpandParent
					defaultExpandAll
					defaultCheckedKeys={trees}
					defaultExpandedKeys = {trees}
					checkedKeys={checkedKeys} //选中的key
					onCheck = {onCheck}
					treeData={treeData}
					onExpand={onExpand}
					disabled={look==1?true:false}
				/>
				}
				
			</Modal>
		</div>
	)
}
	// treeData.length &&

export default Role