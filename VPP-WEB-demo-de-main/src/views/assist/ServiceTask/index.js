import React,{Component} from 'react'
import { DatePicker, Space,ConfigProvider,Form, Input ,message
,Select ,Switch,Modal,Button ,Table,TimePicker,Checkbox,Radio } from 'antd';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import { PlusOutlined,RedoOutlined,ExclamationCircleOutlined } from '@ant-design/icons';
import './index.css'
import http from '../../../server/server.js'


const { Search } = Input;
const { Option } = Select;
const {confirm}  = Modal;
const tailLayout = {
  wrapperCol: { offset: 8, span: 16 },
};
const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};
const format = 'HH:mm';

class ServiceTask extends Component {
	constructor(props) {
		super(props)
		this.state={
			dataSource:[],
			isModalVisible:false,
			editVisible:false,
			addVisible:false,
			page:1,
			edit :0	,//0为编辑 1为新增
			asId:'',
			editdataSource:[],
			nodeList:[],
			nodeSystemList:[],
			nodeId:'',
			systemId:'',
			deviceList:[],
			nodeName:'',
			systemName:'',
			deviceName:'',
			deviceId:'',
			value:'',
			deviceNames:'',
			asType:'',
			loading:false,
			inconem:'',
			astatus:'',
			total:'',
			pageinfo:1,
			totalinfo:'',
			loading1:false
		}
	}
	componentDidMount(){
		this.getASTaskList()
	}
	// 查询任务列表    
	getASTaskList(){
		let {page} = this.state
		this.setState({
			loading:true
		})
		http.post('ancillary_services/ancillary_task/getASTaskList',{
			"number": page,
			"pageSize": 10
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				content.sort(function(a, b) {
				    return b.taskCode< a.taskCode? -1 : 1
				})
				this.setState({
					dataSource:content,
					loading:false,
					total:res.data.data.totalElements
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 录入服务任务
	enter =() =>{
		this.setState({
			isModalVisible:true,
			edit:1
		})
	}
	handleOk = () => {
		this.setState({
			isModalVisible:false,
			
		})
	};
	
	handleCancel = () => {
		this.setState({
			isModalVisible:false
		})
		this.refs.myForm.resetFields()
	};
	// 自动获取任务
	obtain =() =>{
		http.get('ancillary_services/ancillary_task/getASTask').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				message.success('成功')
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 搜索
	onSearch =(e) =>{
		console.log(e)
		this.setState({
			deviceNames:e,
			loading1:true
		},() =>{
			http.post('ancillary_services/ancillary_task/getASDeviceListByName',{
				"asId": this.state.asId,
				"deviceName": this.state.deviceNames,
				"number":  this.state.pageinfo,
				"pageSize": 10
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let content = res.data.data.content
					this.setState({
						editdataSource:content,
						loading1:false
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		})
	}
	// 添加设备取消
	addCancel =() =>{
		this.setState({
			addVisible:false
		})
		this.refs.addmyForm.resetFields()
	}
	// 添加设备
	adddevices1 =() =>{
		this.nodeList()
		this.setState({
			addVisible:true
		})
	}
	// 添加设备取消
	addeCancel =() =>{
		this.refs.addmyForm.resetFields()
		this.setState({
			addVisible:false
		})
	}
	// 编辑
	edits =(e) =>{
		console.log(e)
		
		this.setState({
			edit:0,
			asId:e.asId,
			isModalVisible:true
		},() =>{
			this.refs.myForm.setFieldsValue({
				asLoad: e.asLoad,
				asSubsidy: e.asSubsidy,
				asType: e.asType,
				// aseTime: "03:00:00"
				assDate: dayjs(e.assDate,'YYYY-MM-DD'),
				assTime: [dayjs(e.assTime, 'HH:mm'), dayjs(e.aseTime, 'HH:mm')],
				// astatus: 1
				// createBy: "1"
				// createTime: "2022-08-14 15:31:18"
				taskCode: e.taskCode
				// updateBy: null
				// updateTime: "2022-08-14 15:31:18"
			})
		})
		
	}
	// 删除
	delet =(e) =>{
		console.log(e)
		let that = this
		confirm({
			title: '提示',
			icon: <ExclamationCircleOutlined />,
			content: '确定要删除吗？',
			cancelText:'取消',
			okText:'确定',
			onOk() {
				console.log('OK');
				http.post('ancillary_services/ancillary_task/delASTask?asId=' +e.asId).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						message.success('删除成功')
						
						that.getASTaskList()
					}else{
						message.info(res.data.msg)
					}
				}).catch(err =>{
					
				})
			},
			onCancel() {
					console.log('Cancel');
			},
		});
	}
	// 编辑策略
	editCancel =() =>{
		this.setState({
			editVisible:false
		})
	}
	// 策略查看
	lookover =(e) =>{
		console.log(e)
		
		this.setState({
			editVisible:true,
			asId:e.asId,
			asType:e.asType,
			loading1:true,
			astatus:e.astatus
		},() =>{
			http.post('ancillary_services/ancillary_task/getASDeviceListByName',{
				"asId": e.asId,
				"deviceName": this.state.deviceNames,
				"number": this.state.pageinfo,
				"pageSize": 100000
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let content = res.data.data.content
					let num = 0
					// inconem
					for(var i=0;i<content.length;i++){
						num += Number(content[i].deviceRatedPower)
					}
					this.setState({
						// editdataSource:content,
						// loading1:false,
						inconem:num,
						// totalinfo:res.data.data.totalElements
					})
				}
			}).catch(err =>{
				console.log(err)
			})
			http.post('ancillary_services/ancillary_task/getASDeviceListByName',{
				"asId": e.asId,
				"deviceName": this.state.deviceNames,
				"number": this.state.pageinfo,
				"pageSize": 10
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let content = res.data.data.content
					
					this.setState({
						editdataSource:content,
						loading1:false,
						totalinfo:res.data.data.totalElements
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		})
		
	}
	// 节点
	nodeList(){
		
		http.post('system_management/node_model/nodeList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				this.setState({
					nodeList:res.data.data
				})
			}
		}).catch(err =>{
			
		})
	}
	// 选择节点
	chosenode =(val) =>{
		console.log(val)
		let {nodeList} = this.state
		let that = this
		let nodeSystemList = []
		// if()
		for(var i=0;i<nodeList.length;i++){
			if(val ==nodeList[i].id){
				this.setState({
					nodeName:nodeList[i].name
				})
			}
		}
		this.setState({
			nodeId:val
		})
		http.post('system_management/node_model/nodeSystemList?nodeId='+val).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				console.log(res.data.data[0].id)
				data.map(res =>{
					nodeSystemList.push({
						id:res.id,
						systemName:res.systemName
					})
				})
				that.setState({
					visabled:true,
					nodeSystemList:nodeSystemList,	
					secondCity:""
				},() =>{
					console.log(that.state.nodeSystemList)
					this.refs.addmyForm.setFieldsValue({
						system:'',
						devices:''
					})
				})
			
				
			}
		})
	}
	// 选择系统
	chosesystem =(e) =>{
		console.log(e)
		let {nodeId,nodeSystemList} = this.state
		for(var i=0;i<nodeSystemList.length;i++){
			if(e ==nodeSystemList[i].id){
				this.setState({
					systemName:nodeSystemList[i].systemName
				})
			}
		}
		this.setState({
			systemId:e
		})
		
		http.post('system_management/device_model/deviceList?nodeId='+nodeId +'&systemId='+e).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				this.setState({
					deviceList:res.data.data
				},() =>{
					this.refs.addmyForm.setFieldsValue({
						devices:''
					})
				})
			}
		})
	}

	// deviceId
	// 选择设备
	chosedevice =(e) =>{
		let {deviceList} = this.state
		this.setState({
			deviceId:e
		})
		for(var i=0;i<deviceList.length;i++){
			if(e ==deviceList[i].deviceId){
				this.setState({
					deviceName:deviceList[i].deviceName
				})
			}
		}
		
	}
	// 编辑策略
	onChange =(e) =>{
		console.log(e)
		let that = this
		if(e){
			const params = new URLSearchParams();
			params.append('actualLoad', e.sstatus==2?e.deviceRatedPower:0);
			params.append('deviceRatedPower', e.deviceRatedPower);
			params.append('sId', e.sid);
			params.append('sStatus', e.sstatus ==1?2:e.sstatus==2?1:'');
			confirm({
				title: '提示',
				icon: <ExclamationCircleOutlined />,
				content: '确定要覆盖吗',
				cancelText:'取消',
				okText:'确定',
				onOk() {
					console.log('OK');
					http.post('ancillary_services/ancillary_task/editASStrategy',params,{
						headers:{
						    'content-type': 'application/x-www-form-urlencoded'
						}
					}).then(res =>{
						console.log(res)
						if(res.data.code ==200){
							message.success(res.data.msg)
							http.post('ancillary_services/ancillary_task/getASDeviceListByName',{
								"asId": that.state.asId,
								"deviceName": that.state.deviceNames,
								"number": that.state.pageinfo,
								"pageSize": 10
							}).then(res =>{
								console.log(res)
								if(res.data.code ==200){
									let content = res.data.data.content
									that.setState({
										editdataSource:content
									},() =>{
										
									})
									
								}
							}).catch(err =>{
								console.log(err)
							})
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
	}
	render(){
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current && current < dayjs().subtract(1, 'day'); 
		};
		let {dataSource,isModalVisible,editVisible,addVisible,nodeName,systemName,deviceName,value,
		edit,asId,editdataSource,nodeList,nodeSystemList,deviceList,asType,loading,inconem,page,loading1} = this.state
		const columns= [
			{
			     title: '序号',
			     width: '10%',
			     render:(value, item, index) => (page - 1) * 10 + index+1,
			},
			{
				title: '任务编码',
				dataIndex: 'taskCode',
				key: 'taskCode',
				render: text => <a>{text}</a>,
			},
			{
				title: '辅助服务时段',
				dataIndex: 'age',
				key: 'age',
				render: (s, record, index) =>{
					// console.log(record)
					return record.assDate+ '    ' +record.assTime+'~' +record.aseTime
				}
			},
			{
				title: '辅助服务规模（kW）',
				dataIndex: 'asLoad',
				key: 'asLoad',
			},
			{
				title: '辅助服务类型',
				dataIndex: 'asType',
				key: 'asType',
				render: (text,record,_,action) =>{
					if(record.asType ==1){
						return '调峰'
					}else if(record.asType ==2){
						return '调频'
					}if(record.asType ==3){
						return '备用'
					}
				}
			},
			{
				title: '补贴（元/kWh）',
				dataIndex: 'asSubsidy',
				key: 'asSubsidy',
			},
			{
				title: '策略',
				dataIndex: 'address',
				key: 'address',
				render: (text,record,_,action) =>{
					// if(record.asType ==3){
					// 	return <a onClick={() => this.lookover(record)}>查看</a>
					// }else {
					// 	return <a onClick={() => this.lookover(record)}>编辑</a>
					// }
					if(record.astatus==1 &&record.asType ==3){
						return <a onClick={() => this.lookover(record)}>查看</a>
					}else if(record.astatus==1 &&record.asType ==2 ){
						return <a onClick={() => this.lookover(record)}>编辑</a>
					}else if(record.astatus==1 &&record.asType ==1 ){
						return <a onClick={() => this.lookover(record)}>编辑</a>
					}else if(record.astatus==2 &&record.asType ==3 ){
						return <a onClick={() => this.lookover(record)}>查看</a>
					}else if(record.astatus==2 &&record.asType ==2 ){
						return <a disabled>编辑</a>
					}else if(record.astatus==2 &&record.asType ==1 ){
						return <a disabled>编辑</a>
					}else if(record.astatus==3 &&record.asType ==3 ){
						return <a onClick={() => this.lookover(record)}>查看</a>
					}else if(record.astatus==3 &&record.asType ==2 ){
						return <a disabled>编辑</a>
					}else if(record.astatus==3 &&record.asType ==1 ){
						return <a disabled>编辑</a>
					}
				}
			},
			{
				title: '操作',
				key: 'action',
				render: (text,record,_,action) =>{
					if(record.astatus ==2 ||record.astatus ==3){
						return  <Space size="middle">
						<a disabled>编辑</a>
						<a disabled>删除</a>
					</Space>
								
					}else if(record.astatus ==1){
						return <Space size="middle">
						<a onClick={() => this.edits(record)}>编辑</a>
						<a onClick={() => this.delet(record)}>删除</a>
					</Space>
					}
				}

			},
		];
		const editcolumns =[
			{
				title: '节点',
				dataIndex: 'nodeName',
				key: 'nodeName',
				width:'15%'
				// render: text => <a>{text}</a>,
			},
			{
				title: '系统',
				dataIndex: 'systemName',
				key: 'systemName',
				width:'15%'
				// render: text => <a>{text}</a>,
			},
			{
				title: '设备',
				dataIndex: 'deviceName',
				key: 'deviceName',
				width:'15%'
				// render: text => <a>{text}</a>,
			},
			{
				title: '额定负荷（kW）',
				dataIndex: 'deviceRatedPower',
				key: 'deviceRatedPower',
				// render: text => <a>{text}</a>,
			},
			{
				title: '实时负荷（kW）',
				dataIndex: 'actualLoad',
				key: 'actualLoad',
				// render: text => <a>{text}</a>,
			},
			{
				title: '当前状态',
				dataIndex: 'sstatus',
				key: 'sstatus',
				render: (s, record, index) =>{
					// console.log(record)
					// return record.assDate +record.assTime+'~' +record.aseTime
					if(record.sstatus ==1){
						return '开启'
					}else if(record.sstatus ==2){
						return '关闭'
					}
				}
			},
			{
				title: '执行策略',
				dataIndex: 'sstatus',
				key: 'sstatus',
				render: (s, record, index) =>{
					return	<Radio.Group onChange={() =>{this.onChange(record)}} 
								value={record.sstatus}
								disabled={this.state.asType ==3?true:record.astatus==2?true:
								record.astatus==3?true:record.astatus==1?false:false}
									
							>
								<Radio value={1}>开启</Radio>
								<Radio value={2}>关闭</Radio>	
							</Radio.Group>
				}
				
			},
		]
		// 录入辅助服务
		const onFinish = (values: any) => {
		    console.log('Success:', values);
			console.log(values.assTime[0].format('HHmm'))
			let startdate = values.assDate.format('YYYYMMDD')
			let start = values.assTime[0].format('HHmm')
			let end = values.assTime[1].format('HHmm')
			let taskCode = startdate+start+end
			if(edit ==0){
				// 编辑
				http.post('ancillary_services/ancillary_task/editASTask',{
					
					"asId": asId,
					"asLoad": values.asLoad,
					"asSubsidy": values.asSubsidy,
					"asType": values.asType,
					"aseTime":values.assTime[1].format('HH:mm'),	//结束
					"assDate": values.assDate.format('YYYY-MM-DD'),
					"assTime": values.assTime[0].format('HH:mm'),			//开始
					"taskCode": taskCode
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						message.success('编辑成功')
						
						this.setState({
							isModalVisible:false
						},() =>{
							this.getASTaskList()
							this.refs.myForm.resetFields()
						})
						
					}else{
						message.success(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
			}else if(edit ==1){
				// 新增
				http.post('ancillary_services/ancillary_task/addASTask',{
					"asId": "",
					"asLoad": values.asLoad,
					"asSubsidy": values.asSubsidy,
					"asType": values.asType,
					"aseTime":values.assTime[1].format('HH:mm'),	//结束
					"assDate": values.assDate.format('YYYY-MM-DD'),
					"assTime": values.assTime[0].format('HH:mm'),	//开始
					"taskCode": taskCode
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						message.success('成功')
						this.getASTaskList()
						this.setState({
							isModalVisible:false
						})
						this.refs.myForm.resetFields()
					}else{
						message.info(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
			}
			
		};
		
		const onFinishFailed = (errorInfo: any) => {
		    console.log('Failed:', errorInfo);
		};
		// 设备
		const addonFinish = (values) =>{
			console.log('Success:', values);
			// <div className="adddevices">
			//     <Button icon={<PlusOutlined />} type="link" 
			// 	 onClick={ this.adddevices1 }
			// 	>添加响应设备</Button>
			// </div>
			http.post('ancillary_services/ancillary_task/addASStrategy',{
				"actualLoad": 0,
				"asId": asId,
				"deviceId": values.devices,
				"deviceName": deviceName,
				"deviceRatedPower": 0,
				"nodeId": values.nodeId,
				"nodeName": nodeName,
				"sid": "",
				"sstatus": 2,
				"systemId": values.system,
				"systemName": systemName
			}).then(res =>{
				console.log(res)
				if(res.data.code == 200){
					message.success('添加设备成功')
					this.setState({
						addVisible:false
					})
					this.refs.myForm.resetFields()
					// addmyForm
				}else{
					message.info(res.data.msg)
				}
			})
			
			 // <Option value="3">备用</Option>
		}
		// 分页
		const onChange: PaginationProps['onChange'] = page => {
		    console.log(page);
			this.setState({
				page:page.current
			},() =>{
				this.getASTaskList()
			})
		};
		// 分页
		const onChangetab: PaginationProps['onChange'] = page => {
		    console.log(page);
			this.setState({
				pageinfo:page.current,
				loading1:true
			},() =>{
				http.post('ancillary_services/ancillary_task/getASDeviceListByName',{
					"asId": this.state.asId,
					"deviceName": this.state.deviceNames,
					"number": this.state.pageinfo,
					"pageSize": 10
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						let content = res.data.data.content
						// let num = 0
						// // inconem
						// for(var i=0;i<content.length;i++){
						// 	num += Number(content[i].actualLoad)
						// }
						this.setState({
							editdataSource:content,
							loading1:false,
							// inconem:num,
							totalinfo:res.data.data.totalElements
						})
					}
				}).catch(err =>{
					console.log(err)
				})
			})
		};
		return (
			<div className="task">
				<div className="ServiceTaskheader">
					<Button type="primary" icon={<RedoOutlined />} onClick={this.obtain} >自动获取任务</Button>
					<Button type="primary" 
						onClick={this.enter}
						style={{float:'right'}} 
						icon={<PlusOutlined />} >录入服务任务</Button>
				</div>
				<div>
					<Table dataSource={dataSource} 
						columns={columns} 
						loading={loading}
						onChange={onChange}
						pagination={
							{
							  total: this.state.total,//数据的总条数
							  defaultCurrent: 1,//默认当前的页数
							  defaultPageSize: 10,//默认每页的条数
							  showSizeChanger:false,
							
							}
						}
					/>

				</div>
			<ConfigProvider locale={locale}>
				
				<Modal title={edit==0?'编辑':'录入服务任务'} visible={isModalVisible} 
					footer={null}
					width={740}
					// onOk={this.handleOk} 
					onCancel={this.handleCancel}>
				    <Form
						name="basic"
						labelCol={{ span: 6 }}
						wrapperCol={{ span: 18 }}
						initialValues={{ remember: true }}
						onFinish={onFinish}
						onFinishFailed={onFinishFailed}
						autoComplete="off"
						ref="myForm"
						>
							
						<Form.Item label="辅助响应时段" 
							rules={[{ required: true, message: '请输入辅助响应时段' }]}
							name="assDate"
						>
							<Form.Item
								name="assDate"
								style={{ display: 'inline-block', width: 'calc(50% - 0px)'}}
							>
								<DatePicker style={{width: '100%',marginLeft:0}} disabledDate={disabledDate}   />
							</Form.Item>
						  
							<Form.Item style={{ display: 'inline-block', width: 'calc(50% - 0px)' }}
								name="assTime"
							>
								<TimePicker.RangePicker defaultValue={dayjs('00:00', format)} format={format} />
							</Form.Item>
						</Form.Item>
						<Form.Item
							label="任务编码"
							name="taskCode"
							extra="命名规范：2022071313001600  编号规则：年月日开始结束时间任务编码根据响应时段自动生成"
						>
							<Input disabled  />
						</Form.Item>
						<Form.Item
							label="辅助服务规模"
							// rules={ [{required:true,message: '请输入辅助服务规模' }]}
							// name="asLoad"
						>
							<Form.Item
								name="asLoad"
								style={{width:'63%',float:'left'}}
								// rules={ [{ pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
								rules={ [{pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的数字' }]}
							>
								<Input />
							</Form.Item>
							<span className="ant-form-text"> kW</span>

						</Form.Item>
						<Form.Item
							label="辅助服务规模类型"
							name="asType"
							rules={[{ required: true, message: '请输入辅助服务规模类型' }]}
							
						>
							<Select
								placeholder="辅助服务规模类型"
								style={{width:'70%'}}
								
							>
								<Option value="1">调峰</Option>
								<Option value="2">调频</Option>
								
							</Select>
						</Form.Item>
						
						<Form.Item
							label="响应补贴"
							// rules={[{ required: true, message: '请输入响应补贴' }]}
							// name="asSubsidy"
						>
						
							<Form.Item
								name="asSubsidy"
								style={{width:'58%',float:'left'}}
								// rules={ [{ pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
								rules={ [{required: true, pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的数字' }]}
							>
								<Input maxLength={50} />
							</Form.Item>
							<span className="ant-form-text"> 元/kWh</span>
						</Form.Item>
						<Form.Item {...tailLayout}>
							<Button style={{marginRight:20}}  onClick={this.handleCancel} >
								取消
							</Button>
							<Button type="primary" htmlType="submit">
								确定
							</Button>
							
						</Form.Item>
					</Form>    
				</Modal>
				<Modal title={asType==3?'查看策略':'编辑策略'} visible={editVisible}
					footer={null}
					// onOk={this.handleOk} 
					width={740}
					onCancel={this.editCancel}>
					<div className="tactics">
						<p>可响应负荷（kW）</p>
						<span>{this.state.inconem}</span>
					</div>
					<div className="serches">
						<Search placeholder="搜索设备" onSearch={this.onSearch} style={{ width: 200,marginRight:20 }} />
					</div>
					<Table dataSource={editdataSource} columns={editcolumns}
						// pagination={false}
						bordered
						loading={loading1}
						onChange={onChangetab}
						pagination={
							{
							  total: this.state.totalinfo,//数据的总条数
							  defaultCurrent: 1,//默认当前的页数
							  defaultPageSize: 10,//默认每页的条数
							  showSizeChanger:false,
							
							}
						}
					 />
					
				</Modal>
				<Modal title="添加设备" visible={addVisible}
					footer={null}
					width={640}
					onCancel={this.addCancel}>
					<Form
					    name="basic"
					    labelCol={{ span: 8 }}
					    wrapperCol={{ span: 12 }}
					    onFinish={addonFinish}
					    onFinishFailed={onFinishFailed}
						ref="addmyForm">
					    <Form.Item
					        label="节点"
					        name="nodeId"
					        rules={[{ required: true, message: '选择节点' }]}
					    >
					        <Select
								placeholder="选择节点"
								onChange={this.chosenode}
					        >
								{
									nodeList.length &&nodeList.map(res =>{
										return <Option key={res.id} value={res.id}>{res.name}</Option>
									})
								}

					       </Select>
					    </Form.Item>
						<Form.Item
						    label="系统"
						    name="system"
						    rules={[{ required: true, message: '选择系统' }]}
						>
						    <Select
								placeholder="选择系统"
								onChange={this.chosesystem}
						    >
								{
									nodeSystemList.length&&nodeSystemList.map(item =>{
										return <Option value={item.id} key={item.id}>{item.systemName}</Option>
									})
								}
						   </Select>
						</Form.Item>
						<Form.Item
						    label="设备"
						    name="devices"
						    rules={[{ required: true, message: '选择设备' }]}
						>
						    <Select
								placeholder="选择设备"
								onChange={this.chosedevice}
						    >
								{
									deviceList.length &&deviceList.map(item =>{
										return <Option value={item.deviceId} key={item.deviceId}>{item.deviceName}</Option>
									})
								}
						   </Select>
						</Form.Item>
						<Form.Item {...tailLayout}>
							<Button style={{marginRight:20}}  onClick={this.addeCancel} type="primary" >
								取消
							</Button>
							<Button type="primary" htmlType="submit">
								确定
							</Button>
							
						</Form.Item>
					</Form>
				</Modal>
			</ConfigProvider>
			</div>
		)
	}
	
}

export default ServiceTask