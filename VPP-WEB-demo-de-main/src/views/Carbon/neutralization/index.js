import React, { useEffect,useState } from 'react';
import { Tabs,Table,Select,Input,Button,DatePicker,ConfigProvider,Space,Modal,Form,message  } from 'antd';

import {
  FormOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import './neutralization.scss'
import http from '../../../server/server.js'




const { MonthPicker, RangePicker } = DatePicker;
const dateFormat = 'YYYY-MM-DD';
const monthFormat = 'YYYY-MM';
const { Option } = Select;
const { confirm } = Modal;


const Neutralization =() =>{
	const [columns, setColumns] = useState([
				{
					title: '碳配额公司',
					dataIndex: 'company',
					key: 'company',
					// render: text => <a>{text}</a>,
				},
				{
					title: '交易量（tco2）',
					dataIndex: 'tradingVolume',
					key: 'tradingVolume',
					render: (text,record,_,action) =>{
						return Number(text).toFixed(2)
					}
				},
			  {
			    title: '交易金额（元）',
			    dataIndex: 'tradeAmount',
			    key: 'tradeAmount',
				render: (text,record,_,action) =>{
					return Number(text).toFixed(2)
				}
			  },
			  {
			    title: '交易时间',
			    dataIndex: 'tradeDate',
			    key: 'tradeDate',
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
				          
						  
			  },
			 
			]);
	const [confirmLoading, setConfirmLoading] = useState(false);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [nodeId, setNodeId] = useState('');
	const [nodeList, setNodeList] = useState([]);
	const [tradeDate, setTradeDate] = useState('');
	const [content, setContent] = useState([]);
	const [edit, setEdit] = useState(0);
	const [createdTime, setCreatedTime] = useState('');
	const [tradeId, setTradeId] = useState('');
	const [tstatus, setTstatus] = useState('');
	const [tradeType, setTradeType] = useState(1);
	const [type, setType] = useState('1');
	const [greenVisible, setGreenVisible] = useState(false);
	const [Greensyndrome, setGreensyndrome] = useState(false);
	const [page, setPage] = useState(1);
	const [total, setTotal] = useState('');
	const [edits, setEdits] = useState(1);
	const [setLoadings, setSetLoadings] = useState(false);
	const [currentNum, setCurrentNum] = useState(1);
	const [currentloading, setCurrentloading] = useState(true);
	const [loading,setLoading] = useState(false);
	const [currentUnit,setCurrentUnit] = useState('');
	const [myForm] = Form.useForm();
	const [myForm1] = Form.useForm();
	const [myForm2] = Form.useForm();
	useEffect(() =>{
		getnodeList()
	},[])
	useEffect(() =>{
		if(nodeId!==''&&currentloading){
			getTradeList()
		}
	},[nodeId,page,currentNum])
	const quxiao =() =>{
		setIsModalVisible(false);
		myForm2.resetFields()
	}
	const quxiao1 =() =>{
		setGreensyndrome(false);
		myForm1.resetFields()
	}
	const quxiao2 =() =>{
		setGreenVisible(false);
		myForm.resetFields();
	}
	// 添加交易
	const openlist =() =>{
		// 碳交易
		if(type=='1'){
			setIsModalVisible(true);
			setEdit(1);
			setEdits(1)
		}else if(type=='2'){
			//绿电交易
			setGreenVisible(true);
			setEdit(1);
			setEdits(1)
		}else if(type=='3'){
			//绿证交易
			setGreensyndrome(true);
			setEdit(1);
			setEdits(1);
		}
	}
	
		
	// 查询各交易列表
	const getTradeList=() =>{
		setLoading(true)
		http.post('carbon/carbon_assets/getTradeList',{
			"number": page,
			"pageSize": 10,
			"tradeType": tradeType,
			"nodeId":nodeId
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setContent(res.data.data.content);
				setLoading(false);
				setTotal(res.data.data.totalElements)
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 获取节点列表
	const getnodeList=() =>{
		// 节点
		setCurrentloading(true)
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				setNodeList(res.data.data);
				setCurrentUnit(res.data.data[0].id);
				setNodeId(res.data.data[0].id);
				setCurrentloading(false);
				
			}
		})
	}
	// 节点选择
	const handlepoint =(val) =>{
		console.log(val)
		setNodeId(val)
	}
	
	// 修改
	const editbtn = (e) =>{
		console.log(e)
		console.log(type)
		if(type=='1'){
			//绿电交易
			setEdit(0);
			setCreatedTime(e.createdTime);
			setTradeId(e.tradeId);
			setTstatus(e.tstatus);
			setEdits(0);
			setIsModalVisible(true);
			myForm2.setFieldsValue({
				company:e.company,
				tradingVolume:e.tradingVolume,
				amount:e.tradeAmount,
				date:dayjs(e.tradeDate,'YYYY-MM-DD'),
			})
		}else if(type=='2'){
			setEdit(0);
			setEdits(0);
			setGreenVisible(true);
			setCreatedTime(e.createdTime);
			setTradeId(e.tradeId);
			setTstatus(e.tstatus);
			myForm.setFieldsValue({
				company:e.company,
				tradingVolume:e.tradingVolume,
				amount:e.tradeAmount,
				greenType:e.greenType,
				date:dayjs(e.tradeDate,'YYYY-MM-DD'),
			})
		}else if(type=='3'){
			//绿证交易
			setEdit(0);
			setEdits(0);
			setGreensyndrome(true);
			setCreatedTime(e.createdTime);
			setTradeId(e.tradeId);
			setTstatus(e.tstatus);
			myForm1.setFieldsValue({
				company:e.company,
				certificateType:e.certificateType,
				tradingVolume:e.tradingVolume,
				amount:e.tradeAmount,
				date:dayjs(e.tradeDate,'YYYY-MM-DD'),
			})
		}
		
	}
	// 删除
	const del =(val) =>{
		confirm({
		    title: '确定要删除吗?',
		    icon: <ExclamationCircleOutlined />,
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    // content: 'Some descriptions',
		    onOk() {
		      console.log('OK');
			  http.post('carbon/carbon_assets/delTrade?tradeId='+val.tradeId).then(res =>{
			  	console.log(res)
			  	if(res.data.code==200){
			  		message.success('删除成功')
					setPage(1)
					setCurrentNum(1)
					getTradeList()
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
	// 类型选择
	const handledeal =(val) =>{
		console.log(val)
		setType(val);
		setTradeType(val)
			
	}
	useEffect(() =>{
		if(type=='1'){
			// 碳交易
			setColumns([
			  {
			    title: '碳配额公司',
			    dataIndex: 'company',
			    key: 'company',
			    // render: text => <a>{text}</a>,
			  },
			  {
			    title: '交易量（tco2）',
			    dataIndex: 'tradingVolume',
			    key: 'tradingVolume',
				render: (text,record,_,action) =>{
					return Number(text).toFixed(2)
				}
			  },
			  {
			    title: '交易金额（元）',
			    dataIndex: 'tradeAmount',
			    key: 'tradeAmount',
				render: (text,record,_,action) =>{
					return Number(text).toFixed(2)
				}
			  },
			  {
			    title: '交易时间',
			    dataIndex: 'tradeDate',
			    key: 'tradeDate',
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
				          
						  
			  },
			 
			])
			setContent([])
		}else if(type =='2'){
			// 绿电交易
			setColumns([
				{
					title: '发电公司',
					dataIndex: 'company',
					key: 'company',
					// render: text => <a>{text}</a>,
				},
				{
					title: '绿电类型',
					dataIndex: 'greenType',
					key: 'greenType',
					render: (text) => {
						return text==1?'光伏':'风能'
					}
				},
				{
					title: '交易量（kWh）',
					dataIndex: 'tradingVolume',
					key: 'tradingVolume',
					render: (text,record,_,action) =>{
						return Number(text).toFixed(2)
					}
				},
				{
					title: '交易金额（元）',
					dataIndex: 'tradeAmount',
					key: 'tradeAmount',
					render: (text,record,_,action) =>{
						return Number(text).toFixed(2)
					}
				},
				{
					title: '交易时间',
					dataIndex: 'tradeDate',
					key: 'tradeDate',
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
				},
			 
			])
			setContent([])
		}else if(type =='3'){
			// 绿证交易
			setColumns([
					{
						title: '发电公司',
						dataIndex: 'company',
						key: 'company',
						// render: text => <a>{text}</a>,
					},
					{
						title: '绿证类型',
						dataIndex: 'certificateType',
						key: 'certificateType',
						render: (text) => {
							return text==1?'有补贴':'无补贴'
						}
					},
					{
						title: '交易量（kWh）',
						dataIndex: 'tradingVolume',
						key: 'tradingVolume',
						render: (text,record,_,action) =>{
							return Number(text).toFixed(2)
						}
					},
					{
						title: '交易金额（元）',
						dataIndex: 'tradeAmount',
						key: 'tradeAmount',
						render: (text,record,_,action) =>{
							return Number(text).toFixed(2)
						}
					},
					{
						title: '交易时间',
						dataIndex: 'tradeDate',
						key: 'tradeDate',
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
							  
					},
				 
				])
				setContent([])
		}
		
	},[type])
	// 搜索
	const search =() =>{
		getTradeList()
	}
	
	const handleOk = () => {

	};
	const handleOk1 = () =>{
		
	}
	const handleCancel = () => {
		setIsModalVisible(false);
		setSetLoadings(false);
		myForm2.resetFields()
	 };
	// 录入碳交易信息
	
	const onFinish = (values) => {
		console.log('Success:', values);
		let startdate = values.date.format('YYYY-MM-DD')
		// console.log(startdate)
		setSetLoadings(true)
		if(edit==1){
			// 新增
			http.post('carbon/carbon_assets/addTrade',{
				"certificateType": '',	//绿证类型
				"company": values.company,	//碳配额公司/发电公司名称
				"createdTime": "",	//创建时间
				"greenType": '',	//绿电类型
				"nodeId": nodeId,	//
				"tradeAmount": values.amount,	//交易金额(元)
				"tradeDate": startdate,	//交易时间
				"tradeId": "",	//交易id
				"tradeType": 1,	//交易类型
				"tradingVolume": values.tradingVolume,	//交易量
				"tstatus": "",//碳交易
				"updateTime": ""
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					setIsModalVisible(false);
					setSetLoadings(false);
					getTradeList()
					message.success('添加成功')
					myForm2.resetFields()
				}else{
					setSetLoadings(false);
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}else{
			// 修改
			http.post('carbon/carbon_assets/editTrade',{
				"certificateType": '',	//绿证类型
				"company": values.company,	//碳配额公司/发电公司名称
				"createdTime": createdTime,	//创建时间
				"greenType": '',	//绿电类型
				"nodeId": nodeId,	//
				"tradeAmount": values.amount,	//交易金额(元)
				"tradeDate": startdate,	//交易时间
				"tradeId": tradeId,	//交易id
				"tradeType": 1,	//交易类型
				"tradingVolume": values.tradingVolume,	//交易量
				"tstatus": tstatus,//碳交易
				"updateTime": ""
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					setIsModalVisible(false);
					setSetLoadings(false);
					message.success('修改成功')
					myForm2.resetFields()
					getTradeList()
				}else{
					setSetLoadings(false);
					message.info(res.data.msg);
					
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
		
	};
	
	const onFinishFailed = (errorInfo) => {
		console.log('Failed:', errorInfo);
	};
	// 绿电
	const onFinish1 = (values) => {
		console.log('Success:', values);
		let startdate = values.date.format('YYYY-MM-DD')
		console.log(startdate)
		setSetLoadings(true)
		if(edit==1){
			// 新增
			http.post('carbon/carbon_assets/addTrade',{
				"certificateType": '',	//绿证类型
				"company": values.company,	//发电公司名称
				"createdTime": "",	//创建时间
				"greenType": values.greenType,	//绿电类型
				"nodeId": nodeId,	//
				"tradeAmount": values.amount,	//交易金额(元)
				"tradeDate": startdate,	//交易时间
				"tradeId": "",	//交易id
				"tradeType": 2,	//绿电交易类型
				"tradingVolume": values.tradingVolume,	//交易量
				"tstatus": "",//碳交易
				"updateTime": ""
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					setGreenVisible(false);
					setSetLoadings(false);
					getTradeList()
					message.success('添加成功')
					myForm.resetFields()
				}else{
					setSetLoadings(false);
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}else{
			// 修改
			http.post('carbon/carbon_assets/editTrade',{
				"certificateType": '',	//绿证类型
				"company": values.company,	//碳配额公司/发电公司名称
				"createdTime": createdTime,	//创建时间
				"greenType":  values.greenType,	//绿电类型
				"nodeId": nodeId,	//
				"tradeAmount": values.amount,	//交易金额(元)
				"tradeDate": startdate,	//交易时间
				"tradeId": tradeId,	//交易id
				"tradeType": 2,	//交易类型
				"tradingVolume": values.tradingVolume,	//交易量
				"tstatus": tstatus,//碳交易
				"updateTime": ""
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					setGreenVisible(false);
					setSetLoadings(false);
					getTradeList()
					message.success('修改成功')
					myForm.resetFields()
				}else{
					setSetLoadings(false);
					message.info(res.data.msg);
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
	}
	const handleCancel1 =() =>{
		setGreenVisible(false);
		setSetLoadings(false);
		myForm.resetFields()
	}
	const handleCancel2 =() =>{
		setGreensyndrome(false);
		setSetLoadings(false);
		myForm1.resetFields();
	}
	const onFinishFailed2 =() =>{
		
	}
	// 绿证
	const onFinish2 = (values) => {
		console.log('Success:', values);
		let startdate = values.date.format('YYYY-MM-DD')
		console.log(startdate)
		setSetLoadings(true)
		if(edit==1){
			// 新增
			http.post('carbon/carbon_assets/addTrade',{
				"certificateType": values.certificateType,	//绿证类型
				"company": values.company,	//发电公司名称
				"createdTime": "",	//创建时间
				"greenType": '',	//绿电类型
				"nodeId": nodeId,	//
				"tradeAmount": values.amount,	//交易金额(元)
				"tradeDate": startdate,	//交易时间
				"tradeId": "",	//交易id
				"tradeType": 3,	//绿电交易类型
				"tradingVolume": values.tradingVolume,	//交易量
				"tstatus": "",//碳交易
				"updateTime": ""
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					
					setGreensyndrome(false);
					setSetLoadings(false);
					getTradeList()
					myForm1.resetFields()
					message.success('添加成功')
				}else{
					setSetLoadings(false);
					message.info(res.data.msg)
					
				}
			}).catch(err =>{
				console.log(err)
			})
		}else{
			// 修改
			http.post('carbon/carbon_assets/editTrade',{
				"certificateType": values.certificateType,	//绿证类型
				"company": values.company,	//碳配额公司/发电公司名称
				"createdTime": createdTime,	//创建时间
				"greenType":  '',	//绿电类型
				"nodeId": nodeId,	//
				"tradeAmount": values.amount,	//交易金额(元)
				"tradeDate": startdate,	//交易时间
				"tradeId": tradeId,	//交易id
				"tradeType": 3,	//交易类型
				"tradingVolume": values.tradingVolume,	//交易量
				"tstatus": tstatus,//碳交易
				"updateTime": ""
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					setGreensyndrome(false);
					setSetLoadings(false);
					getTradeList()
					myForm1.resetFields()
					message.success('修改成功')
				}else{
					setSetLoadings(false);
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
	}
	
	const tableChange =(page) =>{
		console.log(page)
		setPage(page.current);
		setCurrentNum(page.current)
		
	}
	return(
		<div className="allcontent2">
			<div className="header">
				节点：
				<Select
				  // defaultValue={nodeList[0].nodeName}
				  style={{ width: 217 }}
				  onChange={handlepoint}
				  defaultValue={currentUnit}
				  loading={currentloading}
				  key={currentUnit}
				  
				>
					{
						nodeList.length&&nodeList.map(res =>{
							return <Option value={res.id}>{res.nodeName}</Option>
						})
					}
				</Select>
				类型：
				<Select
				  defaultValue="类型"
				  style={{ width: 217}}
				  onChange={handledeal}
				  defaultValue="1"
				  options={[
					{
					  value: '1',
					  label: '碳交易',
					},
					{
					  value: '2',
					  label: '绿电交易',
					},
					
					{
					  value: '3',
					  label: '绿证交易',
					},
				  ]}
				/>
				
				<Button type="primary" onClick={search} style={{marginLeft:'24px'}}>查询</Button>
			</div>
			<div className="neutralization">
				<div style={{marginTop:'15px'}}>
					<p>交易列表
					<Button type="primary" onClick={openlist} 
					style={{float:'right',marginTop:'15px'}}>添加交易</Button>
					</p>
					<Table columns={columns} dataSource={content}
						// pagination={false} 
						loading={loading}
						pagination={
							{
								total: total,//数据的总条数
								defaultCurrent: page,//默认当前的页数
								defaultPageSize: 10,//默认每页的条数
								showSizeChanger:false,
								current:currentNum
							}
						}
						onChange={tableChange}
						rowClassName={
							(record, index) => {
							  let className = ''
							  className = index % 2 ===0 ? 'ou' : 'ji'
							  // console.log(className)
							  return className
							}
						  }
					 
					 />
				</div>
			</div>
			<ConfigProvider locale={locale}>
			<Modal
				title={edits==1?'添加交易':'编辑'}
				visible={isModalVisible}
				onOk={handleOk}
				confirmLoading={confirmLoading}
				maskClosable={false}
				onCancel={handleCancel}
				footer={null}>
				<Form
					  name="basic"
					  labelCol={{ span: 6 }}
					  wrapperCol={{ span: 18 }}
					  initialValues={{ remember: true }}
					  onFinish={onFinish}
					  onFinishFailed={onFinishFailed}
					  autoComplete="off"
					  form={myForm2}
					>
						<Form.Item
							label="碳配额公司"
							name="company"
							rules={ [{required: true,  pattern: new RegExp(/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/),message: '请输入6～12个汉字、字母或数字、_' }]}
						>
							<Input maxLength={50} />
						</Form.Item>
						<Form.Item label="交易量" wrapperCol={{ offset: 0, span: 18 }} required>
							<Form.Item name="tradingVolume" style={{width:'80%',float:'left',margin:'0px'}}
								// rules={ [{ pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
								rules={ [{required: true,pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}
							>
								<Input maxLength={50}/>
							</Form.Item>
						   <span  className="peraie" style={{width:'calc(14.5% - 1px)'}}>tco2</span>
						</Form.Item>
						<Form.Item label="交易金额" wrapperCol={{ offset: 0, span: 19 }} required>
							<Form.Item name="amount" style={{width:'88.8%',float:'left',margin:'0px'}}
							
								// rules={ [{ pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
								rules={ [{ required: true,pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}
							>
								<Input maxLength={50} />
							</Form.Item>
						   <span className="peraie">元</span>
						</Form.Item>
						<Form.Item
							label="交易日期"
							name="date"
							rules={ [{required: true, message: '请选择日期' }]}
						>	
							<DatePicker style={{width:'100%',marginLeft:'0px'}} />
						</Form.Item>
						<Form.Item wrapperCol={{ offset: 8, span: 16 }} style={{textAlign:'right'}}>
							<Button ghost onClick={quxiao}>
							  取消
							</Button>
							<Button loading={setLoadings} style={{marginLeft:'10px'}} type="primary" htmlType="submit">
							  确定
							</Button>
							
						</Form.Item>
					</Form>
			</Modal>
			<Modal
				title={edits==1?'添加交易':'编辑'}
				visible={greenVisible}
				onOk={handleOk1}
				// confirmLoading={confirmLoading}
				onCancel={handleCancel1}
				
				footer={null}>
				<Form
					  name="basic"
					  labelCol={{ span: 6 }}
					  wrapperCol={{ span: 18 }}
					  initialValues={{ remember: true }}
					  onFinish={onFinish1}
					  onFinishFailed={onFinishFailed}
					  autoComplete="off"
					 form={myForm}
					>
						<Form.Item
							label="绿电类型"
							style={{marginLeft:'0px'}}
							rules={ [{required: true, message: '请选择绿电类型' }]}
							name="greenType">
							<Select
								defaultValue="类型"
								options={[
									{
									  value:1,
									  label: '光伏',
									},
									
									{
									  value: 2,
									  label: '风能',
									},
								]}
							/>
						</Form.Item>
						<Form.Item label="发电公司" name="company"
							rules={ [{required: true,pattern: new RegExp(/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/),message: '请输入6～12个汉字、字母或数字、_' }]}
						>
							<Input maxLength={50} />
						</Form.Item>
						
						<Form.Item label="交易量" wrapperCol={{ offset: 0, span: 18 }} required>
							<Form.Item name="tradingVolume" style={{width:'86.5%',float:'left',margin:'0px'}}
								rules={ [{ required: true,pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '输入正确范围内的数字' }]}
								// rules={ [{ pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
							>
								<Input maxLength={50} />
							</Form.Item>
						   <span className="peraie" style={{width:'calc(13.5% - 1px)',padding:0,textAlign:'center'}}>kwh</span>
						</Form.Item>
						<Form.Item label="交易金额" wrapperCol={{ offset: 0, span: 18 }} required>
							<Form.Item name="amount" style={{width:'88.8%',float:'left',margin:'0px'}}
								// rules={ [{ pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
								rules={ [{required: true, pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '输入正确范围内的数字' }]}
							>
								<Input maxLength={50} />
							</Form.Item>
						   <span className="peraie">元</span>
						</Form.Item>
						<Form.Item
							label="交易日期"
							name="date"
							rules={ [{required: true, message: '请选择日期' }]}
						>
							<DatePicker style={{width:'100%',marginLeft:'0px'}} />
								
							
						</Form.Item>
						<Form.Item wrapperCol={{ offset: 8, span: 16 }}  style={{textAlign:'right'}}>
							<Button ghost onClick={quxiao2} >
							  取消
							</Button>
							<Button type="primary" style={{marginLeft:'10px'}} htmlType="submit">
							  确定
							</Button>
							
						</Form.Item>
					</Form>
			</Modal>
			<Modal
				title={edits==1?'添加交易':'编辑'}
				visible={Greensyndrome}
				// onOk={handleOk2}
				// confirmLoading={confirmLoading}
				onCancel={handleCancel2}
				footer={null}>
				<Form
					  name="basic"
					  labelCol={{ span: 6 }}
					  wrapperCol={{ span: 18 }}
					  initialValues={{ remember: true }}
					  onFinish={onFinish2}
					  onFinishFailed={onFinishFailed2}
					  autoComplete="off"
					  form={myForm1}
					>
						<Form.Item
							label="绿证类型"
							rules={ [{required: true, message: '请选择绿证类型' }]}
							name="certificateType">
							<Select								 
							  options={[
								{
								  value: 1,
								  label: '有补贴',
								},
								{
								  value:2,
								  label: '无补贴',
								}
							  ]}
							/>
						</Form.Item>
						<Form.Item label="发电公司" name="company"
							rules={ [{required: true,pattern: new RegExp(/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/),message: '请输入6～12个汉字、字母或数字、_' }]}
						>
							<Input/>
						</Form.Item>
						
						<Form.Item label="交易量" wrapperCol={{ offset: 0, span: 18 }} required>
							<Form.Item name="tradingVolume" style={{width:'86.5%',float:'left',margin:'0px'}}
								rules={ [{ required: true,pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '输入正确范围内的数字' }]}
							>
								<Input maxLength={50}/>
							</Form.Item>
						   <span  className="peraie" style={{width:'calc(13.5% - 1px)',padding:0,textAlign:'center'}}>kwh</span>
						</Form.Item>
						<Form.Item label="交易金额" wrapperCol={{ offset: 0, span: 18 }} required>
							<Form.Item name="amount" style={{width:'88.8%',float:'left',margin:'0px'}}
								rules={ [{ required: true,pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '输入正确范围内的数字' }]}
							>
								<Input maxLength={50}/>
							</Form.Item>
						   <span  className="peraie">元</span>
						</Form.Item>
						<Form.Item
							label="交易日期"
							name="date"
							rules={ [{required: true, message: '请选择日期' }]}
						>
							<DatePicker style={{width:'100%',marginLeft:'0px'}} />
								
							
						</Form.Item>
						<Form.Item wrapperCol={{ offset: 8, span: 16 }} style={{textAlign:'right'}}>
							<Button ghost onClick={quxiao1}>
							  取消
							</Button>
							<Button type="primary" style={{marginLeft:'10px'}} htmlType="submit">
							  确定
							</Button>
							
						</Form.Item>
					</Form>
			</Modal>
			</ConfigProvider>
		</div>
	)
}
	


export default Neutralization