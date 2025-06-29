import React,{useEffect,useState} from 'react'
import {Tree , Button ,message,Input,
Table,Space ,Form,Modal,Typography ,Select,Upload} from 'antd';
import {
  PlusOutlined,
  AudioOutlined ,
  FormOutlined,
  DeleteOutlined,
  FileSearchOutlined,
  ExclamationCircleOutlined,
  MinusCircleOutlined, 
  PlusCircleOutlined,
   UploadOutlined, InboxOutlined
} from '@ant-design/icons';
import './index.css'
import http from '../../../server/server.js'
const { Search } = Input;

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 4 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 24 },
  },
};
const formItemLayoutWithOutLabel = {
  wrapperCol: {
    xs: { span: 24, offset: 0 },
    sm: { span: 20, offset: 4 },
  },
};
const { confirm } = Modal;
const Systematics = () =>{
	const [checkStrictly, setCheckStrictly] = useState(false);
	const [data, setData] = useState([]);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [types, setTypes] = useState(0); // 0 is for system addition, 1 for subsystem, 2 for system editing
	const [system, setSystem] = useState('');
	const [id, setId] = useState('');
	const [name, setName] = useState('');
	const [systemId, setSystemId] = useState('');
	const [systemName, setSystemName] = useState('');
	const [loading, setLoading] = useState(false);
	const [systemKey, setSystemKey] = useState('');
	const [currentState, setCurrentState] = useState(1);
	const [page, setPage] = useState(1);
	const [tied, setTied] = useState(0); // 0 for new creation, 1 for editing
	const [total, setTotal] = useState('');
	const [currentNum, setCurrentNum] = useState(1);
	const [setLoadings, setSetLoadings] = useState(false);
	const [myForm] = Form.useForm();
	const [isFirst,setIsFirst] = useState(null)
	useEffect(() =>{
		if(isModalVisible==false){
			myForm.resetFields()
		}
	},[isModalVisible])
	useEffect(() =>{
		if(loading&&systemName){
			systemListBySystemName()
		}
	},[loading,systemName])
	useEffect(() =>{
		if(!loading){
			list()
		}
	},[page,currentNum]);
	useEffect(() =>{
		if(isFirst){
			list()
		}
	},[isFirst])
	// 系统列表
	const list=() =>{
		setLoading(true)
		http.post('system_management/system_model/queryAllSystemListPageable?number='+page+'&pageSize='+'10').then(res =>{
			if(res.data.code ==200){
				console.log(res)
				setData(res.data.data.content);
				setLoading(false);
				setTotal(res.data.data.totalElements)
				
			}
		})
	}
	// 搜索
	const systemListBySystemName =() =>{
		http.post('system_management/system_model/systemListBySystemName?systemName='+systemName).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				console.log(res)
				setData(res.data.data);
				setLoading(false);
				setTotal(res.data.data.length)
			}else{
				setLoading(false);
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 搜索
	const onSearch =(e) =>{
		console.log(e)
		setSystemName(e);
		
		if(e==''){
			setIsFirst(true)
		}else{
			setLoading(true);
			setPage(1);
			setCurrentNum(1);
			setIsFirst(false);
		}
	}
	// 新建系统
	const showModal =() =>{
		setIsModalVisible(true);
		setTypes(0);
		setCurrentState(0);
		setTied(0)
	}
	// 新建，编辑
	const handleOk =() =>{		
		myForm.validateFields().then(values => {
			console.log(values)
			let systemName1 = []
			if(values.system){
				systemName1.push(values.system)
				setSystemName(systemName1)
				
			}
			if(values.names){
				values.names.push(values.system)
				setSystemName(values.names)
			}
			setSetLoadings(true)
			if(types==0){
				// 新建
					
				http.post('system_management/system_model/addSystemList',{
					systemName:systemName
				}).then(res =>{
					if(res.data.code == 200){
						
						setIsModalVisible(false);
						setSetLoadings(false);
						list()
						message.success('新建成功')
						myForm.resetFields()
						
					}else{
						setSetLoadings(false);
						message.info(res.data.msg);
					}
				})
			}else if(types==2){
				// 编辑
				http.post('system_management/system_model/updateSystemList',{
					"newSystemName": values.system,
					"systemKey": systemKey
				}).then(res =>{
					if(res.data.code ==200){
						
						setIsModalVisible(false);
						setSetLoadings(false);
						message.success('编辑成功')
						setSetLoadings(false);
						myForm.resetFields();
						list();
					}else{
						setSetLoadings(false);
						message.info(res.data.msg);
					}
				})
			}
		})
	}
	
	const createSub =(e) =>{
		console.log(e)
		setTypes(1);
		setIsModalVisible(true);
		setSystem(e.name);
		setId(e.id);
		setName(e.name);
	}
	// 删除
	const del =(e) =>{
		console.log(e)
		

		confirm({
		    title: '确定要删除吗？',
			okText:"确定",
			cancelText:"取消",
		    icon: <ExclamationCircleOutlined />,
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    onOk() {
				console.log('OK');
				let systemKey = e.systemKey
				http.post('system_management/system_model/deleteSystem?systemKey='+systemKey).then(res =>{
					if(res.data.code ==200){
						setIsModalVisible(false);
						setPage(1);
						setCurrentNum(1);
						list();
						message.success('删除成功')
					}else{
						message.info(res.data.msg)
						
					}
				})
				
		    },
		    onCancel() {
		      console.log('Cancel');
		    },
		});
	}
	const edit =(e) =>{
		console.log(e)
		setTypes(2);
		setIsModalVisible(true);
		setSystemKey(e.systemKey);
		setCurrentState(1);
		setTied(1);
		myForm.setFieldsValue({
			system:e.systemName
		})
		
		
	}
	const handleCancel =() =>{
		
		setIsModalVisible(false);
	}
	const onFinish = (values: any) => {
	    console.log('Received values of form:', values);
	};
	
		const columns: ColumnsType<DataType> = [
			{
			     title: '序号',
			     width: '10%',
			    render:(value, item, index) => (page - 1) * 5 + index+1,
			},
			{
				title: '系统名称',
				dataIndex: 'systemName',
				key: 'systemName',
			},
			
			{
				title: '操作', key: 'operation', 
				width:150,
				render: (text, row, index) =>{
					if(row.configType =="Y"){
						return  <Space size="middle">
							<a style={{display:'none'}}  disabled>编辑</a>
							<a style={{display:'none'}} disabled>删除</a>
							
						</Space>
						
					}else{
						return  <Space size="middle">
							<a  onClick={() => edit(row)}>编辑</a>
							<a onClick={() => del(row)}>删除</a>
							
						</Space>
					}
					
					  
					
				}
				
			},
		];
		const handlePagination =(page) =>{
			console.log(page)
			setPage(page);
			setCurrentNum(page);
		}
		return(
			<div className="allcontent12 systematics">
				<div className="systemstem">
					<Button type="primary" onClick={showModal}><PlusOutlined />新建系统</Button>
					<Search placeholder="搜索系统名称"  onSearch={onSearch} 
						style={{ width: 200,float:'right' }} />
				</div>
				<Table
					columns={columns}
					dataSource={data}
					rowKey={record =>{
						return record.systemKey
					}}
					loading={loading}
					
					pagination={
						{
							total: total,//数据的总条数
							defaultCurrent: 1,//默认当前的页数
							defaultPageSize: 10,//默认每页的条数
							showSizeChanger:false,
							onChange: handlePagination,
							current:currentNum
						}
					}
				/>
				<Modal title={tied==0?'新建系统':'编辑'}
					width={600}
					visible={isModalVisible} 
					okText="确定"
					cancelText="取消"
					forceRender={true}
					onOk={handleOk} 
					onCancel={handleCancel}
					maskClosable={false}
					confirmLoading={setLoadings}
					// footer={null}
					footer={[
					// 重点：定义右下角 
					<Button ghost onClick={handleCancel}>取消</Button>,
					<Button key="submit" type="primary" onClick={handleOk}>
					确定
					</Button> ]}
					wrapClassName="newzf"
					cancelButtonProps= {{ background: 'none' }}
					// cancelButtonProps={{disabled: true}}
				>
					<Form name="dynamic_form_item" 
						form={myForm}
						labelCol={{ span: 3 }}
						wrapperCol={{ span: 21 }}
						
					>
						<Form.Item 
							
							name="system" 
							label='系统名称'
							rules={ [{pattern: new RegExp(/^[a-zA-Z0-9_\u4e00-\u9fa5]{0,12}$/),message: '请输入6～12个汉字、字母或数字、_' }]}
						>
							<Input maxLength={50} placeholder="请输入系统"  />
						</Form.Item>
						
					     
					</Form>
				</Modal>
			</div>
		)
	}
	


export default Systematics