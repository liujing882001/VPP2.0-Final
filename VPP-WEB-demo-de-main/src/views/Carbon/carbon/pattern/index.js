import React, { useEffect,useState } from 'react';
import { Tabs,Table,Select,Input,Button,DatePicker,ConfigProvider,Space,Modal,Form,message } from 'antd';

import './pattern.scss'
import {
  FormOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';
// import http from '../../../../axios/serve.js'
import http from '../../../../server/server.js'

const { Option } = Select;
const { confirm } = Modal;
const Pattern =() =>{
	const [loading, setLoading] = useState(false);
	const [nodeList, setNodeList] = useState([]);
	const [nodeId, setNodeId] = useState('');
	const [scopeType, setScopeType] = useState('');
	const [content, setContent] = useState([]);
	const [status, setStatus] = useState(0);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [systemList, setSystemList] = useState([]);
	const [systemId, setSystemId] = useState('');
	const [deviceList, setDeviceList] = useState([]);
	const [pointViewList, setPointViewList] = useState([]);
	const [dataPointName, setDataPointName] = useState('');
	const [collectionModelId, setCollectionModelId] = useState('');
	const [createdTime, setCreatedTime] = useState('');
	const [emissionFactorNum, setEmissionFactorNum] = useState('');
	const [hige, setHige] = useState('');
	const [scopeType1, setScopeType1] = useState('');
	const [edit, setEdit] = useState('');
	const [currentUnit,setCurrentUnit] = useState('');
	const [systemName,setSystemName] = useState('');
	const [deviceName,setDeviceName] = useState('');
	const [myForm] = Form.useForm()
	useEffect(() =>{
		getnodeList()
	},[])
	const handlepoint =(val) =>{
		console.log(val)
		setScopeType(val);
		setScopeType1(val)
	}
	useEffect(() =>{
		if(currentUnit!==''){
			
			getCollectionModelList()
			nodeSystemList()
			
			
		}
	},[currentUnit])
	// 获取节点列表
	const getnodeList =() =>{
		// http.post('carbon/collectionModel/nodeList',{
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				setNodeList(res.data.data);
				setNodeId(res.data.data.length>0?res.data.data[0].id:'');
				setCurrentUnit(res.data.data.length>0?res.data.data[0].id:'');
				
			}else{
				Modal.error({
					title:'错误'
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 获取采集模型列表
	const getCollectionModelList =() =>{
		setLoading(true)
		http.post('carbon/collectionModel/getCollectionModelList?nodeId='+nodeId+'&scopeType='+scopeType).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setContent(res.data.data.content);
				setLoading(false);
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 搜索
	const search =() =>{
		getCollectionModelList()
	}
	// 选择节点
	const handleChange =(e) =>{
		console.log(e)
		setNodeId(e)
	}
	// 编辑
	const editbtn =(e) =>{
		console.log(e)
		getAllDevice1(e.systemId)
		if(e.collectMode==0){
			setIsModalVisible(true);
			setCollectionModelId(e.collectionModelId);
			setEmissionFactorNum(e.emissionFactorNum);
			setScopeType1(e.scopeType);
			setDataPointName(e.dataPointName);
			setHige(e.collectMode);
			setEdit(0);
			myForm.setFieldsValue({
				emissionFactorName:e.emissionFactorName,
				collectMode:e.collectMode,
				systemId:e.systemId,
				deviceId:e.deviceId,
				dataPointId:e.dataPointId,
			})
		}else if(e.collectMode==1){
			setIsModalVisible(true);
			setCollectionModelId(e.collectionModelId);
			setEmissionFactorNum(e.emissionFactorNum);
			setScopeType1(e.scopeType);
			setDataPointName(null);
			setHige(e.collectMode);
			setEdit(0);
			myForm.setFieldsValue({
				emissionFactorName:e.emissionFactorName,
				collectMode:e.collectMode,
				systemId:null,
				deviceId:null,
				dataPointId:null,
			})
		}else if(e.collectMode===null){
			setIsModalVisible(true);
			setCollectionModelId(e.collectionModelId);
			setEmissionFactorNum(e.emissionFactorNum);
			setScopeType1(e.scopeType);
			setDataPointName(null);
			setHige(e.collectMode);
			setEdit(0);
			myForm.setFieldsValue({
				emissionFactorName:e.emissionFactorName,
				collectMode:e.collectMode,
				systemId:null,
				deviceId:null,
				dataPointId:null,
			})
		}
		
	}
	// 根据节点获取系统列表
	const nodeSystemList =() =>{
		http.post('carbon/collectionModel/nodeSystemList?nodeId='+nodeId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setSystemList(res.data.data)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 根据节点和系统获取设备列表及点位
	const getAllDevice =() =>{
		http.post('carbon/collectionModel/getAllDevice?nodeId='+nodeId+'&systemId='+systemId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setDeviceList(res.data.data)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 根据节点和系统获取设备列表及点位
	const getAllDevice1=(e) =>{
		console.log(e)
		http.post('carbon/collectionModel/getAllDevice?nodeId='+nodeId+'&systemId='+e).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let deviceList = res.data.data
				console.log(deviceList,'deviceList')
				for(var i=0;i<deviceList.length;i++){
					// console.log(deviceList[i].pointViewList)
					if(deviceList[i].systemId==e){
						setPointViewList(deviceList[i].pointViewList)
					}
				}
				setDeviceList(res.data.data)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 选择系统
	const chosenode =(val,e) =>{
		setSystemId(val);
		setSystemName(e.children);
		getAllDevice()
		myForm.setFieldsValue({
			deviceId:null,
			dataPointId:null,
		})
	}
	// 选择设备
	const choseSystem =(val,e) =>{
		console.log(val,e)
		setDeviceName(e.children);
		myForm.setFieldsValue({
			dataPointId:null,
		})
		for(var i=0;i<deviceList.length;i++){
			if(deviceList[i].deviceId==val){
				setPointViewList(deviceList[i].pointViewList)
			}
		}
	}
	// 选择数据点
	const chosepoint =(val,e) =>{
		setDataPointName(e.children)
	}
	// 取消
	const quxiao =() =>{
		setIsModalVisible(false)
	}
	const del =(e) =>{
		console.log(e)
		confirm({
			title: '提示',
			icon: <ExclamationCircleOutlined />,
			content: '确定要删除吗',
			cancelText:'取消',
			okText:'确定',
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
			onOk() {
				console.log('OK');
				http.post('carbon/collectionModel/removeCollectionModel?collectionModelId='+e.collectionModelId
				).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						message.success(res.data.msg)
						getCollectionModelList()
						
					}else{
						message.success(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
			},
			onCancel() {
					console.log('Cancel');
			},
		});
	}
	// 选择采集方式
	const choseHigh =(e) =>{
		console.log(e)
		if(e==1){
			myForm.setFieldsValue({
				
				systemId:null,
				deviceId:null,
				dataPointId:null,
			})
			setDataPointName(null);
			setDeviceName(null);
			setSystemName(null);
		}
		setHige(e);
	}

	const columns = [
		{
			title: '类型',
			dataIndex: 'emissionFactorName',
			key: 'emissionFactorName',
			// render: (text,record,_,action) =>{
			// 	return  text==1?'固定燃烧源的燃烧排放':text==2?'移动燃烧源的燃烧排放':text==3?'逸散型排放源的排放':''
			// 	console.log(text)
			// }
		},
		{
			title: '采集方式',
			dataIndex: 'collectMode',
			key: 'collectMode',
			render: (text,record) => {
			  return  <span>
						{text==0 ? '自动': text==1 ?'手动':'-'}
					  </span>
			}
			
		},
		{
			title: '系统',
			dataIndex: 'systemName',
			key: 'systemName',
			render: (text,record,_,action) =>{
				console.log(text)
				if( text==null||text==undefined||text==''){
					return '-'
				}else{
					return text
				}
			}
		},
		{
			title: '设备',
			dataIndex: 'deviceName',
			key: 'deviceName',
			render: (text,record,_,action) =>{
				if( text==null||text==undefined||text==''){
					return '-'
				}else{
					return text
				}
			}
		},
		
		{
			title: '数据点',
			dataIndex: 'dataPointName',
			key: 'dataPointName',
			render: (text,record,_,action) =>{
				if( text==null||text==undefined||text==''){
					return '-'
				}else{
					return text
				}
			}
		},
		{
		  title: '操作',
		  dataIndex: 'dates',
		  key: 'dates',
			render: (text,record,_,action) =>{
				return  <Space size="middle">
					<a onClick={() => editbtn(record)}>编辑</a>
					<a onClick={() => del(record)}>删除</a>
				</Space>
			}
		}
	];
	const handleOk = () => {
	
	};
	
	const handleCancel = () => {
		console.log('Clicked cancel button');
		setIsModalVisible(false);
		myForm.resetFields()
	};
	const onFinish = (values) => {
		console.log('Success:', values);
		http.post('carbon/collectionModel/editCollectionModel',{
			"collectMode": values.collectMode,			//采集方式
			"collectionModelId": collectionModelId,		//采集模型ID
			"createdTime": createdTime,	//创建时间
			"dataPointId": values.dataPointId,	//数据点ID
			"dataPointName": dataPointName,	//数据点名称
			"deviceId": values.deviceId,	//	设备ID
			"deviceName":deviceName,	//设备名
			"emissionFactorName": values.emissionFactorName,	//排放因子名称
			"emissionFactorNum":emissionFactorNum,		//排放因子编号
			"nodeId": nodeId,	//楼宇节点id
			"scopeType": scopeType1,	//范围
			"sstatus": '',
			"systemId": values.systemId,	//系统ID
			"systemName": systemName,	//系统名
			"updateTime": ""
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){				
				setIsModalVisible(false);
				myForm.resetFields()
				message.success('编辑成功')
				getCollectionModelList()
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const onFinishFailed = (errorInfo) => {
		console.log('Failed:', errorInfo);
	};
	return(
		<div className="allcontent1">
			<div className="header">
				节点：
				<Select
					placeholder="节点"
					style={{width:217}}
					onChange={handleChange}
					defaultValue={currentUnit}
					key={currentUnit}
				>
					{
						nodeList&&nodeList.map(res =>{
							return <Option value={res.id}>{res.nodeName}</Option>
						})
					}
					
					
				</Select>
				范围：
				<Select
					defaultValue=""
					style={{ width: 217 }}
					onChange={handlepoint}
					options={[
						{
							value: '',
							label: '全部',
						},
						{
							value: '1',
							label: '范围一',
						},
						{
							value: '2',
							label: '范围二',
						},
						
						{
							value: '3',
							label: '范围三',
						},
					]}
				/>
				<Button type="primary" onClick={search}>查询</Button>
			</div>
			<div className="mechanism">
				<Table dataSource={content} columns={columns} loading={loading}
				 // onChange={onChangetab}
				 pagination={
					{
					  // total: this.state.total,//数据的总条数
					  defaultCurrent: 1,//默认当前的页数
					  defaultPageSize: 10,//默认每页的条数
					  showSizeChanger:false,
					
					}
				 }
				 />
			</div>
			<Modal
				title='编辑'
				visible={isModalVisible}
				// confirmLoading={confirmLoading}
				onCancel={handleCancel}
				footer={null}
			>
				<Form
					name="basic"
					labelCol={{ span: 6 }}
					wrapperCol={{ span: 16 }}
					initialValues={{ remember: true }}
					onFinish={onFinish}
					onFinishFailed={onFinishFailed}
					autoComplete="off"
					form={myForm}
				>
						
					<Form.Item label="类型" name="emissionFactorName" >
							<Input disabled={true} />
					</Form.Item>
					
					<Form.Item
						label="采集方式"
						name="collectMode"
					>
						<Select
							placeholder="采集方式"
							onChange={choseHigh}
						>
							<Option value={0}>自动</Option>
							<Option value={1}>手动</Option>
						</Select>
					</Form.Item>
					<Form.Item
						label="系统"
						name="systemId"
					>
						<Select
							placeholder="系统"
							onChange={chosenode}
							disabled={hige==1?true:false}
						>
							{
								systemList.length&&systemList.map(res =>{
									return <Option value={res.id}>{res.systemName}</Option>
								})
							}
						</Select>
					</Form.Item>
					<Form.Item
						label="设备"
						name="deviceId"
					>
						<Select
							placeholder="设备"
							onChange={choseSystem}
							disabled={hige==1?true:false}
						>
						{
							deviceList.length&&deviceList.map(res =>{
									return <Option value={res.deviceId}>{res.deviceName}</Option>
								})
							}
						
						
						</Select>
					</Form.Item>
					<Form.Item
						label="数据点"
						name="dataPointId"
					>
						<Select
							placeholder="数据点"
							onChange={chosepoint}
							disabled={hige==1?true:false}
						>
							{
								pointViewList.length&&pointViewList.map(res =>{
									return <Option value={res.pointId}>{res.pointName}</Option>
								})
							}
							
						</Select>
					</Form.Item>
					
					<Form.Item wrapperCol={{ offset: 8, span: 16 }}>
						<Button type="primary" htmlType="submit">
						  确定
						</Button>
						<Button onClick={quxiao} style={{marginLeft:'10px'}}>
						  取消
						</Button>
					</Form.Item>
				</Form>
			</Modal>
		</div>
	)
}
	


export default Pattern