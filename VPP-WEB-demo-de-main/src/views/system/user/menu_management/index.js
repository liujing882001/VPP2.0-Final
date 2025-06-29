import React,{useEffect,useState} from 'react'
import { Table,Button,Space ,Modal,Form,Input,message} from 'antd';
import http from '../../../../server/server.js'

import './index.css'
const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 20 },
};
const tailLayout = {
  wrapperCol: { offset: 16, span: 8 },
};
const Menu_management =() =>{
	const [dataSource,setDataSource] = useState([]);
	const [Visible, setVisible] = useState(false);
	const [menuId, setMenuId] = useState('');
	const [loading, setLoading] = useState(false);
	const [myForm]  = Form.useForm()
	useEffect(() =>{
		menuList()
	},[])
	const menuList =() =>{
		setLoading(true)
		http.post('system_management/system_user/menu/menuList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setDataSource(res.data.data);
				setLoading(false)
			}
		}).catch(err =>{
			
		})
	}
	const edit =(e) =>{
		console.log(e)
		setVisible(true);
		setMenuId(e.menuId);
		myForm.setFieldsValue({
			menuName:e.menuName,
		})
	}
		
	
	let columns = [{
			title: '名称',
			dataIndex: 'menuName',
			key: 'menuName',
			render: text => <a>{text}</a>,
		},
		{
			title: '图标',
			dataIndex: 'icon',
			key: 'icon',
			render: (text,record) =>{
				return text?text:'-'
			}
		},
		{
			title: '类型',
			dataIndex: 'menuType',
			key: 'menuType',
		},
		{
			title: '排序',
			dataIndex: 'orderNum',
			key: 'orderNum',
		},

		{
			title: '组件路径',
			dataIndex: 'component',
			key: 'component',
		},
		{
			title: '状态',
			dataIndex: 'icon',
			key: 'icon',
			render: (text,record) =>{
				return text?text:'-'
			}
		},
		{
			title: '权限标识',
			dataIndex: 'perms',
			key: 'perms',
			render: (text,record) =>{
				return text?text:'-'
			}
		},
		
		{
			title: '操作',
			dataIndex: 'action',
			key: 'action',
			render: (_, record) => (
				<Space size="middle">
					<a  onClick={() => edit(record)}>编辑 </a>
					
				</Space>
			),
		},
	]
	const handleCancel =() =>{
		myForm.resetFields();
		setVisible(false);
	}
	// 编辑
	const onFinish1 =(values) =>{
		console.log(values)
		let menuName = values.menuName
		const params = new URLSearchParams();
		params.append('menuName', menuName);
		params.append('menuId', menuId);
		http.post('system_management/system_user/menu/updateMenuName',params).then(res =>{
			if(res.data.code ==200){
				message.success('编辑成功')
				
				setVisible(false)
				menuList()
				myForm.resetFields();
				
			}else{
				message.warning(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const onReset1 =() =>{
		myForm.resetFields();
		
		setVisible(false)
	}
	return(
		<div className="allcontent12 menu_management" style={{padding:16}}>
			<Table rowKey={record => record.menuId} 
			 loading={loading}
			columns={columns} dataSource={dataSource} />
			<Modal title="编辑"
				footer={null}
				visible={Visible} 
				
				onCancel={handleCancel}>
				<Form form={myForm} {...layout} onFinish={onFinish1}>
					<Form.Item name="menuName" label="菜单名称" rules={[{ required: true ,message: '请填写名称' }]}>
						<Input />
					</Form.Item>
					
						 
					<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
					
						<Button ghost onClick={onReset1}>
							取消
						</Button>
						  <Button type="primary" htmlType="submit">
							确定
						  </Button>
					</Form.Item>
				</Form>
			</Modal>
		</div>
	)
}
	

export default Menu_management