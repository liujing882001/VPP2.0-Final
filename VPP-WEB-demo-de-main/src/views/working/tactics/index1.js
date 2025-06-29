import React,{Component} from 'react'
import './index.css'
import Conditioner from './Conditioner/index.js'
import Airequipment from './airequipment/index.js'
import Light from './light/index.js'
import Enlighten from './enlighten/index.js'
import http from '../../../server/server.js'
import {Button,Select,Input ,Switch  ,Modal ,Form,
Radio,Tabs,Table,ConfigProvider,message ,
Checkbox,DatePicker,TimePicker, } from 'antd';
import { PlusOutlined ,FormOutlined,DeleteOutlined,ExclamationCircleOutlined,EditOutlined} from '@ant-design/icons';
import './index.css'
import moment from 'moment';
import 'moment/locale/zh-cn';
import locale from 'antd/lib/locale/zh_CN';
import 'antd/dist/antd.min.css';
import axios from 'axios'
import { Scrollbars } from 'react-custom-scrollbars';

const { Search } = Input;


const { Option } = Select;
const { TabPane } = Tabs;
class tactics extends Component {
	constructor(props) {
		super(props)
		this.state={
			loadNodeNameList:[],	//节点
			nodeName:'请选择节点',
			nodeSystemList:[]	,//系统
			sysnodeName:""	,//系统值
			aname:'策略',
			dataSource:[],
			strategyList:[],	//空调策略列表
			
			isModalVisible:false,
			edit: '', // 0 新增 1 编辑
			switchs:false,
			switchs1:false,
			show:'',
			shows:'',
			cronText:'* * * * * *',
			cronType:['second','minute','hour','day','month','week'],
			status:null,
			starting:false,
			closeing:false,
			demandResponse:null,
			onWeeksval:[],
			offWeeksval:[],
			strategyId:'',
			groups:false,
			checkedval:'',
			selectedRows:[],
			tactfulList:[],
			deviceIds:null	,//设备id
			checked:false,
			grouping:[],
			filteredInfo: null,
			edit:'',
			loading:false
		}
		
	}
	componentDidMount(){
		this.loadNodeNameList()
	}
	// 获取节点
	loadNodeNameList(){
		this.setState({
			loading:true
		})
		http.post('system_management/node_model/loadNodeNameList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				this.setState({
					loadNodeNameList:res.data.data,
					loading:false
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 节点，
	// 选择系统
	handleChange =(val) =>{
		console.log(val)
		this.setState({
			nodeName:val,
			nodeSystemList:[],
			aname:'策略',
			// sysnodeName
		},() =>{
			http.post('system_management/node_model/nodeSystemListControl?nodeId='+val).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let data =  []
					let nodeSystemList = res.data.data
					nodeSystemList.map(res =>{
						data.push({
							id:res.id ,
							systemName:res.systemName +'策略'  ,
						})
					})
					console.log(nodeSystemList)
					for(var i=0;i<nodeSystemList.length;i++){
						console.log(nodeSystemList[i])
						data.push({
							id:nodeSystemList[i].id,
							systemName:nodeSystemList[i].systemName +'设备' ,
						})
					}
					for(var i=0;i<data.length ;i++){
						data[i].id = data[i].id+'-' + data[i].systemName+i
					}
					console.log(data)
					let cache = {};  
					let indices = []; 
					data.map((item,index)=>{
						let systemName = item.systemName.substr(0,4);
						let _index = cache[systemName];
						if(_index!==undefined){
							 indices[_index].push(index)
						}else{
							cache[systemName] = indices.length
							indices.push([index])
						}
					})
					let result = [];
					indices.map((item)=>{
						item.map((index)=>{
							result.push(data[index])
						})
					})
					console.log(result)
					let key = result[0].id
					var index = key.indexOf("-")
					var resolve = key.substring(0, index);
					console.log(resolve)
					
					this.setState({
						sysnodeName:resolve,
						nodeSystemList:result
					},() =>{
						http.post('run_schedule/run_strategy/strategyList?nodeId='+this.state.nodeName+
						'&systemId='+resolve
						).then(res =>{
							console.log(res)
							if(res.data.code ==200){
								let data = res.data.data
								if(data.length>0){
									for(var i=0;i<data.length;i++){
										if(data[i].onWeeks &&data[i].onWeeks.length >0){
											let onWeeks =  data[i].onWeeks
											for(var j=0;j<onWeeks.length;j++){
												if(onWeeks[j] =='1'){
													onWeeks[j] ='星期日'
												}else if(onWeeks[j] =='2'){
													onWeeks[j] ='星期一'
												}else if(onWeeks[j] =='3'){
													onWeeks[j] ='星期二'
												}else if(onWeeks[j] =='4'){
													onWeeks[j] ='星期三'
												}else if(onWeeks[j] =='5'){
													onWeeks[j] ='星期四'
												}else if(onWeeks[j] =='6'){
													onWeeks[j] ='星期五'
												}else if(onWeeks[j] =='7'){
													onWeeks[j] ='星期六'
												}
											}
											let offWeeks = data[i].offWeeks
											if(offWeeks){
												for(var j=0;j<offWeeks.length;j++){
													if(offWeeks[j] =='1'){
														offWeeks[j] ='星期日'
													}else if(offWeeks[j] =='2'){
														offWeeks[j] ='星期一'
													}else if(offWeeks[j] =='3'){
														offWeeks[j] ='星期二'
													}else if(offWeeks[j] =='4'){
														offWeeks[j] ='星期三'
													}else if(offWeeks[j] =='5'){
														offWeeks[j] ='星期四'
													}else if(offWeeks[j] =='6'){
														offWeeks[j] ='星期五'
													}else if(offWeeks[j] =='7'){
														offWeeks[j] ='星期六'
													}
												}
											}
											
										}
										console.log(data)
										this.setState({
											strategyList:data
										})
									}
									let grouping = [{
										value:'',
										text:'全部'
									}]
									data.map(res =>{
										grouping.push({
											value:res.strategyId,
											text:res.strategyName
										})
									})
									console.log(grouping)
									this.setState({
										grouping:grouping
									},() =>{
										console.log(this.state.grouping)
									})
								}else{
									this.setState({
										strategyList:res.data.data
									})
								}
								
							}
						}).catch(err =>{
							console.log(err)
						})
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		})
	}
	// 获取策略
	strategyList(){
		let {nodeName,sysnodeName} = this.state
		http.post('run_schedule/run_strategy/strategyList?nodeId='+nodeName+'&systemId='+sysnodeName).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				if(data.length>0){
					for(var i=0;i<data.length;i++){
						if(data[i].onWeeks &&data[i].onWeeks.length >0){
							let onWeeks =  data[i].onWeeks
							for(var j=0;j<onWeeks.length;j++){
								if(onWeeks[j] =='1'){
									onWeeks[j] ='星期日'
								}else if(onWeeks[j] =='2'){
									onWeeks[j] ='星期一'
								}else if(onWeeks[j] =='3'){
									onWeeks[j] ='星期二'
								}else if(onWeeks[j] =='4'){
									onWeeks[j] ='星期三'
								}else if(onWeeks[j] =='5'){
									onWeeks[j] ='星期四'
								}else if(onWeeks[j] =='6'){
									onWeeks[j] ='星期五'
								}else if(onWeeks[j] =='7'){
									onWeeks[j] ='星期六'
								}
							}
							let offWeeks = data[i].offWeeks
							for(var j=0;j<offWeeks.length;j++){
								if(offWeeks[j] =='1'){
									offWeeks[j] ='星期日'
								}else if(offWeeks[j] =='2'){
									offWeeks[j] ='星期一'
								}else if(offWeeks[j] =='3'){
									offWeeks[j] ='星期二'
								}else if(offWeeks[j] =='4'){
									offWeeks[j] ='星期三'
								}else if(offWeeks[j] =='5'){
									offWeeks[j] ='星期四'
								}else if(offWeeks[j] =='6'){
									offWeeks[j] ='星期五'
								}else if(offWeeks[j] =='7'){
									offWeeks[j] ='星期六'
								}
							}
						}
						console.log(data)
						this.setState({
							strategyList:data,
							tactfulList:data
						})
					}
				}else{
					this.setState({
						strategyList:res.data.data
					})
				}
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 策略搜索
	onSearch =(val) =>{
		console.log(val)
		const params = new URLSearchParams();
		params.append('nodeId', this.state.nodeName);
		params.append('systemId', this.state.sysnodeName);
		params.append('strategyName', val);
		http.post('run_schedule/run_strategy/strategyListByName',params).then(res =>{
			console.log(res)
			// if(res.data.code ==200){
			// 	this.setState({
			// 		strategyList:res.data.data
			// 	})
			// }
			let data = res.data.data
			if(data.length>0){
				for(var i=0;i<data.length;i++){
					if(data[i].onWeeks &&data[i].onWeeks.length >0){
						let onWeeks =  data[i].onWeeks
						for(var j=0;j<onWeeks.length;j++){
							if(onWeeks[j] =='1'){
								onWeeks[j] ='星期日'
							}else if(onWeeks[j] =='2'){
								onWeeks[j] ='星期一'
							}else if(onWeeks[j] =='3'){
								onWeeks[j] ='星期二'
							}else if(onWeeks[j] =='4'){
								onWeeks[j] ='星期三'
							}else if(onWeeks[j] =='5'){
								onWeeks[j] ='星期四'
							}else if(onWeeks[j] =='6'){
								onWeeks[j] ='星期五'
							}else if(onWeeks[j] =='7'){
								onWeeks[j] ='星期六'
							}
						}
						let offWeeks = data[i].offWeeks
						for(var j=0;j<offWeeks.length;j++){
							if(offWeeks[j] =='1'){
								offWeeks[j] ='星期日'
							}else if(offWeeks[j] =='2'){
								offWeeks[j] ='星期一'
							}else if(offWeeks[j] =='3'){
								offWeeks[j] ='星期二'
							}else if(offWeeks[j] =='4'){
								offWeeks[j] ='星期三'
							}else if(offWeeks[j] =='5'){
								offWeeks[j] ='星期四'
							}else if(offWeeks[j] =='6'){
								offWeeks[j] ='星期五'
							}else if(offWeeks[j] =='7'){
								offWeeks[j] ='星期六'
							}
						}
					}
					console.log(data)
					this.setState({
						strategyList:data,
						tactfulList:data
					})
				}
			}else{
				this.setState({
					strategyList:res.data.data
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 设备搜索
	onSearch1 =(val) =>{
		console.log(val)
		const params = new URLSearchParams();
		params.append('nodeId', this.state.nodeName);
		params.append('systemId', this.state.sysnodeName);
		params.append('deviceName', val);
		http.post('run_schedule/run_strategy/deviceListByName',params).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				this.setState({
					dataSource:res.data.data
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 编辑
	edit =(e) =>{
		console.log(e)
		
		if(e.onWeeks){
			let onWeeks = e.onWeeks
			for(var j=0;j<onWeeks.length;j++){
				if(onWeeks[j] =='星期日'){
					onWeeks[j] ='1'
				}else if(onWeeks[j] =='星期一'){
					onWeeks[j] ='2'
				}else if(onWeeks[j] =='星期二'){
					onWeeks[j] ='3'
				}else if(onWeeks[j] =='星期三'){
					onWeeks[j] ='4'
				}else if(onWeeks[j] =='星期四'){
					onWeeks[j] ='5'
				}else if(onWeeks[j] =='星期五'){
					onWeeks[j] ='6'
				}else if(onWeeks[j] =='星期六'){
					onWeeks[j] ='7'
				}
			}
			
		}
		if(e.offWeeks){
			let offWeeks = e.offWeeks
			for(var a=0;a<offWeeks.length;a++){
				console.log(offWeeks[a])
				if(offWeeks[a] =='星期日'){
					offWeeks[a] ='1'
				}else if(offWeeks[a] =='星期一'){
					 offWeeks[a] ='2'
				}else if(offWeeks[a] =='星期二'){
					 offWeeks[a] ='3'
				}else if(offWeeks[a] =='星期三'){
					 offWeeks[a] ='4'
				}else if(offWeeks[a] =='星期四'){
					 offWeeks[a] ='5'
				}else if(offWeeks[a] =='星期五'){
					 offWeeks[a] ='6'
				}else if(offWeeks[a] =='星期六'){
					 offWeeks[a] ='7'
				}
			}
			console.log(offWeeks)
		}
		
		
		
		this.setState({
			isModalVisible:true,
			status:e.status,
			starting:e.onWeeks?true:false,
			closeing:e.offWeeks?true:false,
			demandResponse:e.demandResponse,
			strategyId:e.strategyId,
			edit:1,
			// remember:e.demandResponse.toString(),
			checked:e.demandResponse
		},() =>{
			this.refs.myForm.setFieldsValue({
				nodeName:e.strategyName,
				startingup:e.onTimes===undefined?undefined:e.onTimes===null?undefined:e.onTimes==""?undefined:moment(e.onTimes,'HH:mm'),
				period:e.onWeeks,
				cycle:e.offWeeks,
				offtime:e.offTimes===undefined?undefined:e.offTimes===null?undefined:e.offTimes==""?undefined:moment(e.offTimes,'HH:mm'),
				// remember:true
			})
		})
		
	}
	// 改变需求侧响应
	checkChange =(e) =>{
		console.log(e)
		this.setState({
			checked:e.target.checked
		})
	}
	// 删除
	delete =(e) =>{
		console.log(e)
		Modal.confirm({
		    title: '提示',
		    icon: <ExclamationCircleOutlined />,
		    content: '确定要删除吗',
			okText: '确定',
			cancelText: '取消',
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
			onOk: () => {
				http.post('run_schedule/run_strategy_delete/deleteStrategy?strategyId='+e.strategyId).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success(res.data.msg);
						// this.onChange()
						this.strategyList()
						// this.queryAllSysUser()
						
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
		// http.post('run_schedule/run_strategy_delete/deleteStrategy')
	}
	// 新增
	append =() =>{
		this.setState({
			isModalVisible:true,
			edit:0,
			// checked:false
		})
	}
	// 定时开机
	Checkboxange =(checkedValues) =>{
		let that = this
		console.log(checkedValues)
		if(checkedValues==false){
			this.refs.myForm.setFieldsValue({
				startingup:undefined,
				period:undefined
			})
		}
		that.setState ({
			switchs:checkedValues,
			starting:checkedValues
		})
	}
	// 定时关机
	startingups =(checkedValues) =>{
		console.log(checkedValues)
		this.setState({
			switchs1:checkedValues,
			closeing:checkedValues
		})
	}
	
	// 取消
	handleCancel =() =>{
		this.refs.myForm.resetFields()
		this.strategyList()
		this.setState({
			isModalVisible:false,
			starting:false,
			closeing:false
		})
	}
	// 取消
	quxiao =() =>{
		this.refs.myForm.resetFields()
		this.strategyList()
		this.setState({
			isModalVisible:false,
			starting:false,
			closeing:false,
			groups:false
		})
	}
	quxiao1 =() =>{

		this.setState({
			
			groups:false
		})
	}
	
	// 选择tab
	onChange = (key: string) => {
		console.log(key)
		var index = key.indexOf("-")
		var resolve = key.substring(0, index);
		let resolveindex = key.substring(0, index);
		console.log(resolveindex)
		let {nodeName} = this.state
		this.setState({
			selectedRows:[]
		})
		if(key.indexOf('设备') != -1){
			this.setState({
				aname:'设备',
				sysnodeName:resolve,
			},() =>{
				http.post('run_schedule/run_strategy/deviceList?nodeId='+nodeName+'&systemId='+resolve).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						let data = res.data.data
						http.post('run_schedule/run_strategy/strategyList?nodeId='+this.state.nodeName+
						'&systemId='+resolve
						).then(res =>{
							console.log(res)
							if(res.data.code ==200){
								let data = res.data.data
								if(data.length>0){
									let grouping = [{
										value:'',
										text:'全部'
									}]
									data.map(res =>{
										grouping.push({
											value:res.strategyId,
											text:res.strategyName
										})
									})
									this.setState({
										grouping:grouping
									},() =>{
										console.log(this.state.grouping)
									})
								}
								
							}
						}).catch(err =>{
							console.log(err)
						})
						
						this.setState({
							// grouping:grouping,
							dataSource:res.data.data
						},() =>{
							console.log(this.state.grouping)
						})
					}
				}).catch(err =>{
					console.log(err)
				})
			})
		}else if(key.indexOf('策略') != -1){
			this.setState({
				aname:'策略',
				sysnodeName:resolve,
			},() =>{
				http.post('run_schedule/run_strategy/strategyList?nodeId='+nodeName+'&systemId='+resolve).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						let data = res.data.data
						console.log(data)
						if(data.length>0){
							for(var i=0;i<data.length;i++){
								if(data[i].onWeeks &&data[i].onWeeks.length >0){
									let onWeeks =  data[i].onWeeks
									for(var j=0;j<onWeeks.length;j++){
										if(onWeeks[j] =='1'){
											onWeeks[j] ='星期日'
										}else if(onWeeks[j] =='2'){
											onWeeks[j] ='星期一'
										}else if(onWeeks[j] =='3'){
											onWeeks[j] ='星期二'
										}else if(onWeeks[j] =='4'){
											onWeeks[j] ='星期三'
										}else if(onWeeks[j] =='5'){
											onWeeks[j] ='星期四'
										}else if(onWeeks[j] =='6'){
											onWeeks[j] ='星期五'
										}else if(onWeeks[j] =='7'){
											onWeeks[j] ='星期六'
										}
									}
									let offWeeks = data[i].offWeeks
									if(offWeeks){
										for(var j=0;j<offWeeks.length;j++){
											if(offWeeks[j] =='1'){
												offWeeks[j] ='星期日'
											}else if(offWeeks[j] =='2'){
												offWeeks[j] ='星期一'
											}else if(offWeeks[j] =='3'){
												offWeeks[j] ='星期二'
											}else if(offWeeks[j] =='4'){
												offWeeks[j] ='星期三'
											}else if(offWeeks[j] =='5'){
												offWeeks[j] ='星期四'
											}else if(offWeeks[j] =='6'){
												offWeeks[j] ='星期五'
											}else if(offWeeks[j] =='7'){
												offWeeks[j] ='星期六'
											}
										}
									}
									
								}
								console.log(data)
								this.setState({
									strategyList:data
								})
							}
						}else{
							this.setState({
								strategyList:res.data.data
							})
						}
						
					}
				}).catch(err =>{
					console.log(err)
				})
			})
		}

	};
	// 选择设备
	handlechose =(e) =>{
		console.log(e)
		if(e==1){
			this.deviceList()
		}else if(e =="true"){
			const params = new URLSearchParams();
			params.append('nodeId', this.state.nodeName);
			params.append('systemId', this.state.sysnodeName);
			params.append('strategyId', '');
			params.append('group', true);
			// 
			http.post('run_schedule/run_strategy/strategyGroupList',params).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let data = res.data.data
					console.log(this.state.dataSource)
					
					this.setState({
						dataSource:res.data.data
					})
					
						
				}
			}).catch(err =>{
				console.log(err)
			})
		}else{
			const params = new URLSearchParams();
			params.append('nodeId', this.state.nodeName);
			params.append('systemId', this.state.sysnodeName);
			params.append('strategyId', '');
			params.append('group', false);
			// 
			http.post('run_schedule/run_strategy/strategyGroupList',params).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let data = res.data.data
					console.log(this.state.dataSource)
					
					this.setState({
						dataSource:res.data.data
					})
					
						
				}
			}).catch(err =>{
				console.log(err)
			})
		}
	}
	// 设备列表
	deviceList(){
		http.post('run_schedule/run_strategy/deviceList?nodeId='+this.state.nodeName+'&systemId='+this.state.sysnodeName).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				if(res.data.data.length>0){
					let data = res.data.data
					this.setState({
						dataSource:res.data.data
					},() =>{
						
					})
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 调整分组
	adjust =() =>{
		console.log(this.state.selectedRows)
		let {nodeName,sysnodeName,selectedRows} = this.state
		if(selectedRows.length==0){
			message.info('请选择一条')
		}else{
			this.setState({
				groups:true
			},() =>{
				http.post('run_schedule/run_strategy/strategyList?nodeId='+nodeName+'&systemId='+sysnodeName).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						let data = res.data.data
						if(data.length>0){	
							console.log(data)
							this.setState({
								tactfulList:res.data.data
							},() =>{
								
							})
							
						}else{
							this.setState({
								strategyList:res.data.data
							})
						}
					}
				}).catch(err =>{
					console.log(err)
				})
			})
		}
		
	}
	handleCancel1 =()=>{
		this.setState({
			groups:false
		},() =>{
			
		})
	}
	// 分组列表
	// 开关
	Swicth =(val,item) =>{
		console.log(val)
		console.log(item)
		let status;
		if(item.status =='1'){
			status =0
		}else{
			status=1
		}
		Modal.confirm({
		    title: '提示',
		    icon: <ExclamationCircleOutlined />,
		    content: '确定要改变状态吗',
			okText: '确认',
			cancelText: '取消',
			onOk: () => {
				http.post('run_schedule/run_strategy_delete/changeStrategyStatus?strategyId='+item.strategyId +'&status='+status,{
					
				}).then(res =>{
					console.log()
					if(res.data.code==200){
						message.success('修改状态成功')
						this.setState({
							// item.status
							checkedval:false
						})
						this.strategyList()
					}else{
						message.success(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
				
			}
			,
			onCancel() {
				console.log('Cancel');
			},
		});
		
	}
	// 确定
	handleOk =() =>{
		
	}
	// 筛选
	list(){
		
	}
	render(){
		let {loadNodeNameList,nodeName,nodeSystemList,aname,lists,isModalVisible
		,edit,switchs,switchs1,show,shows,strategyList,status,starting ,closeing,demandResponse,
		onWeeksval,offWeeksval,remember,groups,checkedval,tactfulList,checked,filteredInfo,dataSource,loading
		} = this.state
		// let {  } = this.state;
		filteredInfo = filteredInfo || {};
		const columns =
						[
							{
							    title: '设备名称',
							    dataIndex: 'deviceName',
								render:(text, record, index) =>{
									if(record.deviceName ==null){
										// alert(record.groupName)
										return record.deviceName =''
									}else{
										return record.deviceName
									}
								},
							},
							{
								title: '分组',
								dataIndex: 'strategyName',
								filters:this.state.grouping,
								filteredValue: filteredInfo.value || null,
								onFilter: (value, record) => {
									// console.log(record.name)
									// record.name.includes(value)
								},
								sortDirections: ['descend'],
							},
							// {
							// 	title: '设备全码 ',
							// 	dataIndex: 'deviceSn',
							// },
							{
								title: '设备型号 ',
								dataIndex: 'deviceSn',
						    
							},
							{
								title: '设备品牌 ',
								dataIndex: 'deviceBrand',
							},
							{
								title: '设备位置 ',
								dataIndex: 'deviceLabel',
							},
							
					]
		// 新建项目组
		const onFinish = (fieldsValue) => {
		    console.log('Success:', fieldsValue);
			// console.log('Success:', fieldsValue.startingup.format('HH:mm'));
			if(edit ==0){
				// 新增
				console.log(this.state.strategyList)
				for(var i=0;i<strategyList.length;i++){
					if(fieldsValue.nodeName ==strategyList[i].strategyName){
						return message.info('名字不可重复')
					}
				}
				http.post('run_schedule/run_strategy/addStrategy',{
					"demandResponse": fieldsValue.remember==undefined?false:true,
					"nodeId": this.state.nodeName,
					"nodeName": "",
					"offTimes": fieldsValue.offtime==undefined?'':fieldsValue.offtime.format('HH:mm'),
					"offWeeks": fieldsValue.cycle==undefined?'':fieldsValue.cycle,
					"onTimes":  fieldsValue.startingup==undefined?'':fieldsValue.startingup.format('HH:mm'),
					"onWeeks": fieldsValue.period==undefined?'':fieldsValue.period,
					"status": "",
					"strategyId": "",
					"strategyName": fieldsValue.nodeName,
					"systemId": this.state.sysnodeName,
					"systemName": ""
				}).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('新建成功')
						this.refs.myForm.resetFields()
						this.strategyList()
						this.setState({
							isModalVisible:false,
						})
					}else{
						message.success(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
				
			}else if(edit ==1){
				// 编辑
				http.post('run_schedule/run_strategy/updateStrategy',{
					"demandResponse": this.state.checked,
					"nodeId": this.state.nodeName,
					"nodeName": "",
					"offTimes": fieldsValue.offtime===undefined?'':fieldsValue.offtime.format('HH:mm'),
					"offWeeks": fieldsValue.cycle===undefined?'':fieldsValue.cycle,
					"onTimes":  fieldsValue.startingup===undefined?'':fieldsValue.startingup.format('HH:mm'),
					"onWeeks": fieldsValue.period===undefined?'':fieldsValue.period,
					"status": "",
					"strategyId": this.state.strategyId,
					"strategyName": fieldsValue.nodeName,
					"systemId": this.state.sysnodeName,
					"systemName": ""
				}).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('编辑成功')
						
						this.refs.myForm.resetFields()
						this.setState({
							isModalVisible:false,
							starting:false,
							closeing:false
						},() =>{
							this.strategyList()
						})
					}else{
						message.success(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
			}
			
		};
		
		const onFinishFailed = (errorInfo: any) => {
		    console.log('Failed:', errorInfo);
		};
		// 筛选
		const handlefilter = (pagination, filters, sorter) =>{
			console.log('Various parameters', filters);
			if(filters.strategyName){				
				const params = new URLSearchParams();
				params.append('nodeId', this.state.nodeName);
				params.append('systemId', this.state.sysnodeName);
				params.append('strategyId', filters.strategyName[0]);
				params.append('group', true);
				// 
				http.post('run_schedule/run_strategy/strategyGroupList',params).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						let data = res.data.data
						console.log(this.state.dataSource)
						
						this.setState({
							dataSource:res.data.data
						})
						
							
					}
				}).catch(err =>{
					console.log(err)
				})
			}
			
			
			
		}
		// 选择
		const rowSelection = {
			onChange: (selectedRowKeys: React.Key[], selectedRows: DataType[]) => {
				console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
				let deviceIds = []
				selectedRows.map(item =>{
					deviceIds.push(item.deviceId)
				})
				console.log(deviceIds)
				this.setState({
					selectedRows:selectedRows,
					deviceIds:deviceIds
				})
			},
			getCheckboxProps: (record: DataType) => ({
				disabled: record.name === 'Disabled User', // Column configuration not to be checked
				name: record.name,
			}),
		};
		// 调整分组
		const ongroups =(val) =>{
			console.log(val)
			const params = new URLSearchParams();
			params.append('deviceIds', this.state.deviceIds);
			params.append('strategyId', val.groups);
				axios({
			        url: `run_schedule/run_strategy/updateDeviceStrategyGroup`,
			        method: 'POST',
			        data: params,
			        headers: {
			          'Content-Type': 'application/x-www-form-urlencoded',//类型设置
			        },
			    }).then(res => {
			        console.log(res)
					if(res.data.code ==200){
						message.success('调整分组成功')
						this.setState({
							groups:false
						},() =>{
							this.deviceList()
						})
					}else{
						message.success(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
		}
		const options = [
			{ label: '周一', value: '2' },
			{ label: '周二', value: '3' },
			{ label: '周三', value: '4' },
			{ label: '周四', value: '5' },
		    { label: '周五', value: '6' },
			{ label: '周六', value: '7' },
			{ label: '周日', value: '1' },
		]
		return(
			<div locale={locale}>
				<div className="tacticsheader">
					<Select value={nodeName} style={{ width: 230 }} loading={loading} onChange={this.handleChange}>
						
						{
							loadNodeNameList.length&&loadNodeNameList.map(item =>{
								return <Option key={item.id} value={item.id}>{item.nodeName}</Option>
							})
						}
					</Select>

				</div>
				<div className="tabscroll">
					
					<Tabs defaultActiveKey="1" onChange={this.onChange}>
						{nodeSystemList.map(item => (
							<TabPane tab={`${item.systemName}`} key={item.id} >
								{
									aname=='策略'?(
										<div>
											<div className="Conditionerheader" style={{margin:'25px 0px'}}>
												<Button type="primary" onClick={this.append}><PlusOutlined />新建项目组</Button>
												<Search placeholder="搜索" onSearch={this.onSearch} style={{ width: 200 }} />
											</div>
											<div className="Conditioner">
												<ul className="summarizing">
													{
														strategyList.length &&strategyList.map((item,index) =>{
															// console.log(item)
															return 	<li key={index}>
																<div className="summername">{item.strategyName}
																	<Switch checked={checkedval} checked={item.status==1?true:false} 
																	 checkedChildren="开启" unCheckedChildren="关闭"
																	  onChange={() =>{this.Swicth(item.status,item)}} />
																</div>
																<div className="summering">
																	<div className="sunimg">
																		<img src={require('../../../style/img/air.png')}  />
																	</div>
																	<div className="listing">
																		<ol>
																			<li>定时开机：{item.onTimes}</li>
																			<li>定时关机：{item.offTimes}</li>
																			<li>重复周期：{item.onWeeks==null?'':item.onWeeks.toString()}</li>
																			<li>重复周期：{item.offWeeks==null?'':item.offWeeks.toString()}</li>
																		</ol>
																	</div>
																	<div className="edit">
																		<a onClick={() => { this.edit(item) }} ><FormOutlined />编辑</a><br />
																		<a onClick={() => { this.delete(item) }} ><DeleteOutlined />删除</a>
																	</div>
																</div>
																
															</li>
														})
													}
												</ul>
											</div>
										</div>
									):(
										<div>
											<div style={{margin:'25px 0px'}}>
												<Button type="primary" onClick={this.adjust}><EditOutlined />调整分组</Button>
												
												<Search placeholder="搜索" onSearch={this.onSearch1} style={{ width: 200,float:'right' }} />
												<Select placeholder="请选择" 
												onChange={this.handlechose}
												style={{width:120 ,float:'right',marginRight:20}}>
													<Option key={1}>全部设备</Option>
													<Option key={false}>未分组设备</Option>
													<Option key={true}>已分组设备</Option>
												</Select>
											</div>
											<Table 
												columns={columns} 
												dataSource={dataSource} 
												onChange={handlefilter}
												rowKey={record =>{
													return record.deviceId
												}}
												rowSelection={{
												    ...rowSelection,
												}}
											/>
										</div>
									)
								}
							</TabPane>
						))}
					</Tabs>
					
				</div>
				<Modal
					title={!edit ? `新建项目组` : `编辑`}
					visible={isModalVisible} 
					// onOk={this.handleOk} 
					footer={null}
					onCancel={this.handleCancel}
					// okText="确认"
					// cancelText="取消"
					
				>
				<ConfigProvider locale={locale}>
					<Form
						name="basic"
						labelCol={{ span: 4 }}
						wrapperCol={{ span: 14 }}
						initialValues={{ remember: true }}
						autoComplete="off"
						ref="myForm"
						onFinish={onFinish}
					>
					    <Form.Item
					        label="组名称"
					        name="nodeName"
							style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.2)',paddingBottom:'20px' }}
							rules={[{ required: true, message: '请输入组名称' }]}
					    >
							<Input />
					    </Form.Item>
						<Form.Item  
						 rules={[{ required: true, message: '请选择' }]}
							name="startingup"
						 label="定时开机"  style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.2)' }}>
						    <Form.Item
						        style={{ display: 'inline-block', width: '30%' }}
						    >
								<Switch  checked={starting} onChange={this.Checkboxange} defaultChecked={show}  />
						    </Form.Item>
						      
						    <Form.Item name="startingup"  style={{ display: 'inline-block', width: '70%' }}>
								<TimePicker disabled={!starting} format='HH:mm'  />
						    </Form.Item>
							<Form.Item name="period" label="重复周期">
								<Checkbox.Group disabled={!starting} options={options}  />
							</Form.Item>
						</Form.Item>
						
						<Form.Item  
						 rules={[{ required: true, message: '请选择' }]}
						 							name="offtime"
						 label="定时关机"  style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.2)' }}>
						    <Form.Item
							
						        style={{ display: 'inline-block', width: '30%' }}
						    >
								<Switch checked={closeing} onChange={this.startingups} defaultChecked={shows}  />
						    </Form.Item>
						      
						    <Form.Item locale={locale} name="offtime" style={{ display: 'inline-block', width: '70%' }}>
									<TimePicker disabled={!closeing}  format='HH:mm'  />
						    </Form.Item>
							<Form.Item name="cycle" label="重复周期">
								<Checkbox.Group disabled ={!closeing} options={options}  />
							</Form.Item>
						</Form.Item>
						<Form.Item 
							wrapperCol={{ span: 24 }}
							extra="系统通过AI调度，根据需求响应计划，自动执行需求响应策略" 
							name="remember" >
								<Checkbox defaultChecked={checked} onChange={this.checkChange}>
						           参与需求侧响应
								</Checkbox>
						</Form.Item>
						<Form.Item wrapperCol={{ offset: 8, span: 16 }}>
							<Button type="button" onClick={this.quxiao} style={{marginRight:15}}>
								取消
							</Button>
							<Button type="primary" htmlType="submit">
								确定
							</Button>
							
						</Form.Item>
					</Form>
					</ConfigProvider>
				</Modal>
				<ConfigProvider locale={locale}>
					<Modal
						title='调整分组'
						visible={groups} 
						// onOk={this.handleOk} 
						onCancel={this.handleCancel1}
						footer={null}
						// okText="确认"
						// cancelText="取消"
						
					>
					<Form
						name="basic1"
						labelCol={{ span: 4 }}
						wrapperCol={{ span: 14 }}
						autoComplete="off"
						ref="myForm1"
						onFinish={ongroups}
					>
						<Form.Item
						    // label="组名称"
						    name="groups"
							
						>
							<Radio.Group>
							 {
								 tactfulList.length &&tactfulList.map(item =>{
									 return  <Radio key={item.strategyId} value={item.strategyId}>{item.strategyName}</Radio>
								 })
							 }
									 
									  
							</Radio.Group>
						</Form.Item>
						<Form.Item wrapperCol={{ offset: 8, span: 16 }}>
							<Button type="button" onClick={this.quxiao1} style={{marginRight:15}}>
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

export default tactics





