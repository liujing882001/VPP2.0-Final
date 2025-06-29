import React,{useEffect,useState} from 'react'
import { Link,useHistory  } from "react-router-dom"
import { Input ,Tree,Form,Select,DatePicker,TimePicker,Checkbox,Radio ,Button
, Col, Row,message,Spin,Modal
} from 'antd';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import './index.scss'
import '../strategy/index.scss'
// import 'antd/dist/antd.min.css';
// import http from '../../server/server.js'
import http from '../../../../server/server.js'

const { Option } = Select;
const { TreeNode } = Tree;

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 10 },
};
const tailLayout = {
  wrapperCol: { offset: 4, span: 16 },
};

const { RangePicker } = DatePicker;
const plainOptions  = [
   {
    value:1,
    label:'星期一'
   },
   {
    value:2,
    label:'星期二'
   },
   {
    value:3,
    label:'星期三'
   },
   {
    value:4,
    label:'星期四'
   },
   {
    value:5,
    label:'星期五'
   },
   {
    value:6,
    label:'星期六'
   },
   {
    value:7,
    label:'星期日'
   }
  ];
 ;
// class period extends Component {
const Period =(props) =>{
	const [expandedKeys, setExpandedKeys] = useState(['0-0-0', '0-0-1']);
	const [checkedKeys, setCheckedKeys] = useState([]);
	const [selectedKeys, setSelectedKeys] = useState([]);
	const [autoExpandParent, setAutoExpandParent] = useState(false);
	const [index, setIndex] = useState(0);
	const [treeData, setTreeData] = useState([]);
	const [diveceNum, setDiveceNum] = useState('');
	const [load, setLoad] = useState('');
	const [strategyType, setStrategyType] = useState('');
	const [strategyName, setStrategyName] = useState(''); //策略名称：
	const [strategyId, setStrategyId] = useState(''); //策略id
	const [strategyStatus, setStrategyStatus] = useState(''); //状态
	const [runStrategy, setRunStrategy] = useState(0); //运行策略类型
	const [createdTime, setCreatedTime] = useState(''); //创建时间
	const [deviceIdList, setDeviceIdList] = useState([]); //
	const [demandResponse, setDemandResponse] = useState(''); //自动需求响应
	const [ratedPower, setRatedPower] = useState('');
	const [edit, setEdit] = useState('');
	const [username, setUsername] = useState('');
	const [display, setDisplay] = useState('');
	const [isResponse, setIsResponse] = useState(null);
	const [loading, setLoading] = useState(false);
	const [setLoadings, setSetLoadings] = useState(false);
	const [isShow, setIsShow] = useState(2);
	const [indeterminate, setIndeterminate] = useState(false);
	const [checkedList, setCheckedList] = useState([]);
	const [checkAll, setCheckAll] = useState(false);
	const [defaultExpandedKeys, setDefaultExpandedKeys] = useState([]);
	const [defaultSelectedKeys, setDefaultSelectedKeys] = useState([]);
	const [defaultCheckedKeys, setDefaultCheckedKeys] = useState([]);
	const [demandValue, setDemandValue] = useState('');
	const [ownerId, setOwnerId] = useState('');
	const history= useHistory();
	const [onceForm] = Form.useForm();
	const [cycleForm] = Form.useForm();
	useEffect(() =>{
		if(props.location.state){
			// 编辑
			const location = props.location.state
			console.log(location)
			if(location.strategyId){
				// 编辑
				setStrategyId(location.strategyId)
				setEdit(0)
				
			}
			if(location.strategyType==0||location.strategyType==1){
				// 新增
				setStrategyType(location.strategyType);
				setEdit(1);
				setDemandValue('2');
				setIsResponse(false);
			}
		}else{
			history.push('/tactics')
		}
		
	},[])
	useEffect(() =>{
		
		if(strategyId){
			strategyDetail()
		}
	},[strategyId,edit]);
	useEffect(() =>{
		// alert(isResponse)
		if(isResponse!==null){
			areaDeviceView()
		}
	},[isResponse])
	// 详情
	const strategyDetail =() =>{
		http.post('run_schedule/run_strategy/strategyDetail?strategyId='+strategyId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				// if(strategyType)
				let runStrategy = data.runStrategy
				console.log(runStrategy)
				let otherConditioningDTO =  data.otherConditioningDTO
				
				if(runStrategy==0){
					// 一次性
					let nums = 0
					onceForm.setFieldsValue({
						note:dayjs(data.onceExe.ymd,'YYYY-MM-DD'),	
						times: dayjs(data.onceExe.times,'HH:mm'),
						power:otherConditioningDTO.power=='POWER_ON'?'0':'1'
					})
					// console.log(this.state.treeData)
					setIndex(0)
					setIndex(data.runStrategy)
					setDefaultExpandedKeys(data.deviceIdList);
					setDefaultSelectedKeys(data.deviceIdList);
					setDefaultCheckedKeys(data.deviceIdList);
					setDeviceIdList(data.deviceIdList);
					setDemandValue(data.demandResponse==true?"1":"2");
					setCheckedKeys(data.deviceIdList);
					setCreatedTime(data.createdTime);
					setOwnerId(data.ownerId);
					setRatedPower(data.ratedPower);
					setRunStrategy(data.runStrategy);
					setStrategyStatus(data.strategyStatus);
					setStrategyType(data.strategyType);
					setDiveceNum(data.deviceIdList.length);
					setLoad(nums);
					setIsShow(data.demandResponse==true?"1":"2");
					setDemandResponse(data.demandResponse==true?"1":"2");
					setIsResponse(data.demandResponse)
					var reg=/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/
					if (reg.test(data.strategyName)) {
						
						setStrategyName(data.strategyName);
						setDisplay('none')
					}else{
						setDisplay('block');
						setStrategyName(data.strategyName)
					}
					
				}else if(runStrategy==1){
					let nums = 0
					cycleForm.setFieldsValue({
						gender:dayjs(data.cycleExe.cycleTimes,'HH:mm'),
						cycleWeeks:data.cycleExe.cycleWeeks,
						power:otherConditioningDTO.power=='POWER_ON'?'0':'1',
					})
					setIndex(1)
					setDefaultExpandedKeys(data.deviceIdList);
					setDefaultSelectedKeys(data.deviceIdList);
					setDefaultCheckedKeys(data.deviceIdList);
					setDeviceIdList(data.deviceIdList);
					setCheckedKeys(data.deviceIdList);
					setDemandValue(data.demandResponse==true?"1":"2");
					setCreatedTime(data.createdTime);
					setOwnerId(data.ownerId);
					// setRatedPower(data.ratedPower);
					setRunStrategy(data.runStrategy);
					setLoad(nums);
					setStrategyStatus(data.strategyStatus);
					setStrategyType(data.strategyType);
					setDiveceNum(data.deviceIdList.length);
					setIsResponse(data.demandResponse);
					setIsShow(data.demandResponse==true?"1":"2");
					setDemandResponse(data.demandResponse==true?"1":"2")
					var reg=/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/
					if (reg.test(data.strategyName)) {
						
						setStrategyName(data.strategyName);
						setDisplay('none')
					}else{
						setDisplay('block');
						setStrategyName(data.strategyName)
					}
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 是否能参加自动需求响应
	const checkDemandResponse=() =>{
		http.post('run_schedule/run_strategy/checkDemandResponse').then(res =>{
			console.log(res)
			if(res.data.code==200){
				
				setDemandValue(res.data.data==true?"1":"2")
				setIsResponse(res.data.data)
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 获取权限下区域-节点-系统-设备
	const areaDeviceView=() =>{
		setLoading(true)
		http.post('run_schedule/run_strategy/areaDeviceView?isResponse='+isResponse+'&strategyId'+strategyId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data2 = res.data.data
				let nums = 0
				if(deviceIdList.length>0){
					function f(arr) {
						arr.forEach(item=>{
							// console.log(item)
							if(deviceIdList[i]==item.id){
								nums+=item.load
							}
							f(item.children) 
							
						})
					}
					// let deviceIdList = this.state.deviceIdList
					for(var i=0;i<deviceIdList.length;i++){
						f(data2)
					}
					
					setLoad(nums)
					console.log(nums)
				}else{
					setLoad('')
				}
				
				setTreeData(res.data.data)
				setLoading(false)
			}else{
				setLoading(false)
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const pattern =(e) =>{
		console.log(e)
		setIndex(e)
		setRunStrategy(e)
	}
	// 执行日期
	const onChange =(date, dateString) =>{
		console.log(date,dateString)
	}
	// 名称
	const inputChange=(e)=>{
		console.log(e.target.value)
		let val = e.target.value
		var reg=/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/
		setStrategyName(e.target.value)
	}
	
	const demandResponseChange =(e) =>{
		console.log(e.target.value)
		setDemandResponse(e.target.value);
		setDemandValue(e.target.value);
		setIsResponse(e.target.value==1?true:false);
		setDeviceIdList([]);
		setDiveceNum('');
		setLoad('');
		setCheckedKeys([]);
		setIsShow(e.target.value);
	}
	// 编辑
	const updateStrategy=() =>{
		setSetLoadings(true);
		// 一次性
		if(isShow==1){
			// 参加
			let demandResponse1 = ''
			let airConditioningDTO = new Object()
			let cycleExe = new Object()
			let onceExe = new Object()
			let otherConditioningDTO = new Object()
			console.log(onceForm.getFieldsValue())
			let values = onceForm.getFieldsValue()
			console.log(demandResponse)
			demandResponse1 = demandResponse=="1"?true:false
			console.log(demandResponse1)
			http.post('run_schedule/run_strategy/updateStrategy',{
				"deviceIdList": deviceIdList,
				"createdTime": createdTime,
				"ratedPower": ratedPower,	//额定负荷
				"runStrategy": runStrategy,	//运行策略类型
				"strategyId": strategyId,
				"strategyName": strategyName,	//名称
				"strategyStatus": false,	//开关状态
				"strategyType": strategyType,	//运行策略
				"demandResponse": demandResponse1,	//自动需求响应
				"airConditioningDTO": airConditioningDTO,
				// 周期
				"cycleExe": cycleExe,
				// 一次性
				"onceExe": onceExe,
				// 其他空调
				"otherConditioningDTO": otherConditioningDTO,
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					message.success('成功')
					history.push({
						pathname: '/tactics',
						// query:{strategyType:this.state.strategyType}
					});
					
					setSetLoadings(false)
				}else{
					
					setSetLoadings(false)
					Modal.info({
					   // title: 'This is a notification message',
					   content: (
					    res.data.msg
					   ),
					   onOk() {},
					 });
				}
			}).catch(err =>{
				console.log(err)
			})
		}else if(isShow==2){
			// 不参加
			if(index==0){
				let demandResponse1 = ''
				let airConditioningDTO = new Object()
				let cycleExe = new Object()
				let onceExe = new Object()
				let otherConditioningDTO = new Object()
				console.log(onceForm.getFieldsValue())
				let values = onceForm.getFieldsValue()
				console.log(demandResponse)
				demandResponse1 = demandResponse=="1"?true:false
				console.log(demandResponse1)				
				
				// console.log(otherConditioningDTO)
				if(strategyName==''){
					message.info('请输入策略名称')
				}
				onceExe["times"] = dayjs(values.times).format('HH:mm')
				onceExe["ymd"] = dayjs(values.note).format('YYYY-MM-DD')
				otherConditioningDTO["power"] = values.power=="0"?"POWER_ON":'POWER_OFF'
				if(onceExe["times"]&&onceExe["ymd"]&&otherConditioningDTO["power"]&&display=='none'){
					http.post('run_schedule/run_strategy/updateStrategy',{
						"deviceIdList": deviceIdList,
						"createdTime": createdTime,
						"ratedPower": ratedPower,	//额定负荷
						"runStrategy": runStrategy,	//运行策略类型
						"strategyId": strategyId,
						"strategyName": strategyName,	//名称
						"strategyStatus": false,	//开关状态
						"strategyType": strategyType,	//运行策略
						"demandResponse": demandResponse1,	//自动需求响应
						"airConditioningDTO": airConditioningDTO,
						// 周期
						"cycleExe": cycleExe,
						// 一次性
						"onceExe": onceExe,
						// 其他空调
						"otherConditioningDTO": otherConditioningDTO,
					}).then(res =>{
						console.log(res)
						if(res.data.code==200){
							message.success('成功')
							history.push({
								pathname: '/tactics',
								// query:{strategyType:this.state.strategyType}
							});
							
							setSetLoadings(false)
						}else{
							
							setSetLoadings(false)
							Modal.info({
							   // title: 'This is a notification message',
							   content: (
							    res.data.msg
							   ),
							   onOk() {},
							 });
						}
					}).catch(err =>{
						console.log(err)
					})
				}else{
	
					setSetLoadings(false)
					message.info('请选择一次性信息')
				}
				
				
			}else if(index==1){
				let demandResponse1 = ''
				console.log(cycleForm.getFieldsValue())
				let values = cycleForm.getFieldsValue()
				let airConditioningDTO = new Object()
				let cycleExe = new Object()
				let onceExe = new Object()
				let otherConditioningDTO = new Object()
				demandResponse1 = demandResponse=="1"?true:false
				console.log(demandResponse1)		
				onceExe["times"] = ""
				onceExe["ymd"] = ""
								
				cycleExe["cycleTimes"] = dayjs(values.gender).format('HH:mm')
				cycleExe["cycleWeeks"] = values.cycleWeeks
				otherConditioningDTO["power"] = values.power=="0"?"POWER_ON":'POWER_OFF'
				
				if(strategyName==''){
					message.info('请输入策略名称')
				}
				if(cycleExe["cycleTimes"]&&cycleExe["cycleWeeks"]&&otherConditioningDTO["power"]&&display=='none'){
					http.post('run_schedule/run_strategy/updateStrategy',{
						"deviceIdList": deviceIdList,
						"createdTime": "",
						"ratedPower": ratedPower,	//额定负荷
						"runStrategy": runStrategy,	//运行策略类型
						"strategyId": strategyId,
						"strategyName": strategyName,	//名称
						"strategyStatus": false,	//开关状态
						"strategyType": strategyType,	//运行策略
						"demandResponse": demandResponse1,	//自动需求响应
						"airConditioningDTO": airConditioningDTO,
						// 周期
						"cycleExe": cycleExe,
						// 一次性
						"onceExe": onceExe,
						// 其他空调
						"otherConditioningDTO": otherConditioningDTO,
					}).then(res =>{
						if(res.data.code==200){
							message.success('成功')
							history.push({
								pathname: '/tactics',
								// query:{strategyType:this.state.strategyType}
							});
							setSetLoadings(false)
							
						}else{
							
							setSetLoadings(false)
							Modal.info({
							   // title: 'This is a notification message',
							   content: (
							    res.data.msg
							   ),
							   onOk() {},
							 });
							
						}
					}).catch(err =>{
						console.log(err)
					})
				}else{
					
					setSetLoadings(false)
					message.info('请选择周期性信息')
				}
				
			}
		}
		
	}
	// 提交
	const retset =() =>{
		setSetLoadings(true)
		if(edit==1){
			// 空调
			let airConditioningDTO = new Object()
			console.log(airConditioningDTO)
				
			// 周期性
			let cycleExe = new Object()
			cycleExe["cycleTimes"] = ""
			cycleExe["cycleWeeks"] = []
			// 一次性
			let onceExe = new Object()
			// 其他空调
			let otherConditioningDTO = new Object()
			let demandResponse1 = ''
			// 其他
			if(strategyType==1){
				// 一次性
				if(isShow==1){
					// 参加
					
					let airConditioningDTO = new Object()
					let cycleExe = new Object()
					
					let otherConditioningDTO = new Object()
					console.log(onceForm.getFieldsValue())
					let values = onceForm.getFieldsValue()
					console.log(demandResponse)
					demandResponse1 = demandResponse=="1"?true:false
					console.log(demandResponse1)		
					let onceExe = new Object()
					onceExe["times"] = dayjs().format('HH:mm')
					onceExe["ymd"] = dayjs().format('YYYY-MM-DD')
					http.post('run_schedule/run_strategy/addStrategy',{
						"deviceIdList": deviceIdList,
						"createdTime": "",
						"ratedPower": ratedPower,	//额定负荷
						"runStrategy": runStrategy,	//运行策略类型
						"strategyId": "",
						"strategyName": strategyName,	//名称
						"strategyStatus": false,	//开关状态
						"strategyType": strategyType,	//运行策略
						"demandResponse": demandResponse1,	//自动需求响应
						"airConditioningDTO": airConditioningDTO,
						// 周期
						"cycleExe": cycleExe,
						// 一次性
						"onceExe": onceExe,
						// 其他空调
						"otherConditioningDTO": otherConditioningDTO,
						
					}).then(res =>{
						console.log(res)
						if(res.data.code==200){
							message.success('成功')
							history.push({
								pathname: '/tactics',
								// query:{strategyType:this.state.strategyType}
							});
							
							setSetLoadings(false)
						}else{
							setSetLoadings(false)
							Modal.info({
							   // title: 'This is a notification message',
							   content: (
							    res.data.msg
							   ),
							   onOk() {},
							});
						}
					}).catch(err =>{
						console.log(err)
					})
				}else if(isShow==2){
					// 不参加
					if(index==0){
						let airConditioningDTO = new Object()
						let cycleExe = new Object()
						
						let otherConditioningDTO = new Object()
						console.log(onceForm.getFieldsValue())
						let values = onceForm.getFieldsValue()
						console.log(demandResponse)
						demandResponse1 = demandResponse=="1"?true:false
						console.log(demandResponse1)				
						// 时间
						let onceExe = new Object()
						otherConditioningDTO["power"] = values.power=="0"?"POWER_ON":'POWER_OFF'
						onceExe["times"] = dayjs(values.times).format('HH:mm')
						onceExe["ymd"] = dayjs(values.note).format('YYYY-MM-DD')
						
						console.log(otherConditioningDTO)
						if(strategyName==''){
							
							setSetLoadings(false)
							message.info('请输入策略名称')
						}
						if(deviceIdList.length==0){
							
							setSetLoadings(false)
							message.info('请选择设备')
						}
						if(display=='block'){
							setSetLoadings(false)
							message.info('请输入6～12个汉字、字母或数字、_')
						}
						
						if(onceExe["times"]&&onceExe["ymd"]&&otherConditioningDTO["power"]&&display=='none'){
							http.post('run_schedule/run_strategy/addStrategy',{
								"deviceIdList": deviceIdList,
								"createdTime": "",
								"ratedPower": ratedPower,	//额定负荷
								"runStrategy": runStrategy,	//运行策略类型
								"strategyId": "",
								"strategyName": strategyName,	//名称
								"strategyStatus": false,	//开关状态
								"strategyType": strategyType,	//运行策略
								"demandResponse": demandResponse1,	//自动需求响应
								"airConditioningDTO": airConditioningDTO,
								// 周期
								"cycleExe": cycleExe,
								// 一次性
								"onceExe": onceExe,
								// 其他空调
								"otherConditioningDTO": otherConditioningDTO,
								
							}).then(res =>{
								console.log(res)
								if(res.data.code==200){
									message.success('成功')
									history.push({
										pathname: '/tactics',
										// query:{strategyType:this.state.strategyType}
									});
									
									setSetLoadings(false)
								}else{
									
									setSetLoadings(false);
									Modal.info({
									   // title: 'This is a notification message',
									   content: (
									    res.data.msg
									   ),
									   onOk() {},
									 });
								}
							}).catch(err =>{
								console.log(err)
							})
						}else{
							
							setSetLoadings(false);
							message.info('请选择一次性信息')
							
						}
						
						
						
					}else{
						// 周期性
						
						console.log(cycleForm.getFieldsValue())
						let values = cycleForm.getFieldsValue()
						let airConditioningDTO = new Object()
						let cycleExe = new Object()
						let onceExe = new Object()
						let otherConditioningDTO = new Object()
						demandResponse1 = demandResponse=="1"?true:false
						console.log(demandResponse1)		
						onceExe["times"] = ""
						onceExe["ymd"] = ""
						// onceExe["times"] = dayjs(values.gender).format('HH:mm')
						// onceExe["ymd"] = dayjs(values.note).format('YYYY-MM-DD')
					
						cycleExe["cycleTimes"] = dayjs(values.gender).format('HH:mm')
						cycleExe["cycleWeeks"] = values.cycleWeeks
						otherConditioningDTO["power"] = values.power=="0"?"POWER_ON":'POWER_OFF'
						
						if(strategyName==''){
							
							setSetLoadings(false);
							message.info('请输入策略名称')
							
						}
						
						if(display=='block'){
							
							setSetLoadings(false);
							message.info('请输入6～12个汉字、字母或数字、_')
						}
						if(cycleExe["cycleTimes"]&&cycleExe["cycleWeeks"]&&otherConditioningDTO["power"]&&display=='none'){
							http.post('run_schedule/run_strategy/addStrategy',{
								"deviceIdList": deviceIdList,
								"createdTime": "",
								"ratedPower": ratedPower,	//额定负荷
								"runStrategy": runStrategy,	//运行策略类型
								"strategyId": "",
								"strategyName": strategyName,	//名称
								"strategyStatus": false,	//开关状态
								"strategyType": strategyType,	//运行策略
								"demandResponse": demandResponse1,	//自动需求响应
								"airConditioningDTO": airConditioningDTO,
								// 周期
								"cycleExe": cycleExe,
								// 一次性
								"onceExe": onceExe,
								// 其他空调
								"otherConditioningDTO": otherConditioningDTO,
								
							}).then(res =>{
								console.log(res)
								if(res.data.code==200){
									message.success('成功')
									history.push({
										pathname: '/tactics',
										// query:{strategyType:this.state.strategyType}
									});
									
									setSetLoadings(false);
								}else{
									
									setSetLoadings(false);
									Modal.info({
									   // title: 'This is a notification message',
									   content: (
									    res.data.msg
									   ),
									   onOk() {},
									 });
								}
							}).catch(err =>{
								console.log(err)
							})
						}else{
							
							setSetLoadings(false);
							message.info('请选择周期性信息')
						}
						
					}
				}
				
			}	
			console.log(airConditioningDTO.mode)
			console.log(strategyName)
		}else if(edit==0){
			updateStrategy()
		}
		
		
		
	
		
			
		
			
	}
	// 焦点触发
	const handleBlur =() =>{
		console.log('失去焦点')
		// let val = e.target.value
		var reg=/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/
		// var pattern=new RegExp(reg);
		// console.log(pattern.test("123"));
		if (reg.test(strategyName)) {
			// alert(0)
			// console.log(val)
			
			setDisplay('none')
		}else{
			
			setDisplay('block')
		}
	}
	
		
	const onExpand = (expandedKeysValue) => {
		console.log('onExpand', expandedKeysValue);
		
		setSelectedKeys(expandedKeysValue);
		setAutoExpandParent(false);
	};

	const onCheck = (checkedKeys, info) => {
		console.log('onCheck', checkedKeys, info);
		setCheckedKeys(checkedKeys)
		if(info.checked==true){
			console.log('onCheck', checkedKeys, info);
			let checkedNodes = info.checkedNodes
			console.log(checkedNodes)
			let deviceIdList = []
			let load = 0
			let diveceNum = 0
			for(var i=0;i<checkedNodes.length;i++){
				if(checkedNodes[i].type=='DEVICE'){
					deviceIdList.push(checkedNodes[i].id)
					load +=checkedNodes[i].load
					diveceNum +=1
				}
			}
			
			setDeviceIdList(deviceIdList)
			setLoad(load);
			setDiveceNum(diveceNum);
			setRatedPower(load)
			console.log(deviceIdList)
			
		}else if(info.checked==false){
			console.log('onCheck', checkedKeys, info);
			let checkedNodes = info.checkedNodes
			let deviceIdList = []
			let load = 0
			let diveceNum = 0
			for(var i=0;i<checkedNodes.length;i++){
				if(checkedNodes[i].type=='DEVICE'){
					deviceIdList.push(checkedNodes[i].id)
					load +=checkedNodes[i].load
					diveceNum +=1
				}
			}
			console.log(deviceIdList)
			
			setDeviceIdList(deviceIdList)
			setLoad(load);
			setDiveceNum(diveceNum);
			setRatedPower(load)
		}
		
		
		
	};
	const onSelect = (selectedKeysValue, info) => {
		console.log('onSelect', info);
		
		setSelectedKeys(selectedKeysValue)
	};
	const onFinish = (values: any) => {
		console.log(values);
	};
	const renderTree = (data: any) => {
		// console.log(data)
		if (data && data.length > 0) {
			return data.map((item: any) => {
				return (
					<TreeNode checkable={item.type=='DEVICE'?true:false} id={item.id} load={item.load} type={item.type} title={item.title} key={item.key}>
						{item.children && renderTree(item.children)}
					</TreeNode>
				);
			});
		} else {
			// return (
			//     <TreeNode>
			//         {data.map((item: any) => {
			//             return (
			//                 <TreeNode
			//                     title={item.title}
			//                     key={item.key}
			//                     // className={s.treeNodeChild}
			//                 ></TreeNode>
			//             );
			//         })}
			//     </TreeNode>
			// );
		}
	};
	const onCheckAllChange = (e: CheckboxChangeEvent) => {
		console.log(e)
		setCheckedList(e.target.checked ? [1,2,3,4,5,6,7] : []);
		setIndeterminate(false);
		setCheckAll(e.target.checked);
		cycleForm.setFieldsValue({
			cycleWeeks:e.target.checked ? [1,2,3,4,5,6,7] : []
		})
	   
	  };
	const format = 'HH:mm';
	const onChange1 = (list) => {
		console.log(list)
		setCheckedList(list);
		setIndeterminate(!!list.length && list.length < plainOptions.length);
		setCheckAll(list.length === plainOptions.length)
	
	   
	};
		return(
			<div className="strategy">
				<div className="header">
					<Link to='/tactics'><a href="#"><img src={require('../../../../style/xu/return.png')} />{edit==0?'编辑策略':'新建策略'}</a></Link>
				</div>
				<div className="strategybody">
					<div className="title">
						<span className="strategytitle">*</span>策略名称：
						<Input maxLength={50} value={strategyName}
						onBlur={handleBlur}
						onChange={inputChange} placeholder="策略名称" style={{width:370}} />
						<span className="regs" style={{display:display}}>请输入6～12个汉字、字母或数字、_</span>
					</div>
					<div className="title">
						<span className="strategytitle">*</span>是否参与自动需求响应：
						<Radio.Group value={demandValue} onChange={demandResponseChange}>
							<Radio value="1"> 参与 </Radio>
							<Radio value="2"> 不参与 </Radio>
						</Radio.Group>
					</div>
					<div className="title">
						<span className="strategytitle">*</span>选择设备：
						<p>已选设备：{diveceNum} <span>已选总额定负荷(kW)：{load}</span></p>
						<Spin spinning={loading}>
							<div className="titleTree">
								<Tree
									checkable
									defaultCheckedKeys={defaultCheckedKeys}
									defaultExpandedKeys = {defaultCheckedKeys}
									checkedKeys={checkedKeys} //选中的key
									onSelect={onSelect}
									onCheck={onCheck}
								>
									{renderTree(treeData)}
								</Tree>
							</div>
						</Spin>
					</div>
					<div className="title" style={{display:isShow==2?'block':'none'}}>
						<span className="strategytitle">*</span>运行策略：
						<div className="finesse">
							<span onClick={() => pattern(0)} className={index == 0 ? 'active' : 'noactive'}>一次性</span>
							<span onClick={() => pattern(1)} className={index == 1 ? 'active' : 'noactive'}>周期性</span>
						</div>
						<div className="period" style={{display:index==0?'block':'none'}}>
							<Form labelCol={{ span: 4 }}
								wrapperCol={{ span: 14 }}  
								layout="horizontal"
								form={onceForm}
								name="control-hooks" onFinish={onFinish}>
								<Form.Item name="note" label="执行日期：" rules={[{ required: true, message: '请选择'}]}>
									<DatePicker style={{width:'336px',float:'left'}} />
								</Form.Item>
								<Form.Item label="执行时间：" name="times" rules={[{ required: true, message: '请选择'}]}>
									<TimePicker format={format} style={{width:'210px'}} defaultOpenValue={dayjs('00:00:00', 'HH:mm:ss')} />
								</Form.Item>
							    <Form.Item label="执行动作：" name="power" rules={[{ required: true, message: '请选择'}]}>
									<Radio.Group style={{marginLeft:16}}>
								        <Radio value="0"> 开启 </Radio>
								        <Radio value="1"> 关闭 </Radio>
								    </Radio.Group>
								</Form.Item>
								
							</Form>
						</div>
						<div className="period periods"style={{width:808,display:index==1?'block':'none'}}>
							<Form labelCol={{ span: 4 }}
								wrapperCol={{ span: 20 }}  
								layout="horizontal"
								form={cycleForm}
								name="control-hooks" onFinish={onFinish}>
							
								<Form.Item name="gender" label="执行时间：" rules={[{ required: true, message: '请选择'}]}>
									<TimePicker  style={{width:'210px'}} defaultOpenValue={dayjs('00:00:00', 'HH:mm:ss')} />
								</Form.Item>
							    <Form.Item label="重复周期：" required>
							        <Form.Item>
							            <Checkbox style={{marginLeft:16}} indeterminate={indeterminate} onChange={onCheckAllChange} checked={checkAll}>全部</Checkbox>
							        </Form.Item>
							        <Form.Item  name="cycleWeeks" rules={[{ required: true, message: '请选择'}]}>
							            <Checkbox.Group style={{marginLeft:16}} options={plainOptions} value={checkedList} onChange={onChange1} />
							        </Form.Item>
							             
							             
							    </Form.Item>
								<Form.Item label="执行动作：" name="power" rules={[{ required: true, message: '请选择'}]}>
									<Radio.Group style={{marginLeft:16}}>
								        <Radio value="0"> 开启 </Radio>
								        <Radio value="1"> 关闭 </Radio>
								    </Radio.Group>
								</Form.Item>
								
							</Form>
						</div>
					</div>
					
				</div>
				<div className="bottom">
					<Button type="primary" loading={setLoadings} onClick={retset}>确定</Button>
					<Button ghost><Link to='/tactics'>取消</Link></Button>
				</div>
			</div>
		)
	}

// <span onClick={() => this.pattern(1)} className={this.state.index == 1 ? 'active' : 'noactive'}>周期性</span>
export default Period
// <Tree
// 							      checkable
// 							      defaultCheckedKeys={this.state.defaultCheckedKeys}
// 							      defaultExpandedKeys = {this.state.defaultCheckedKeys}
// 							      checkedKeys={checkedKeys} //选中的key
// 							      onSelect={onSelect}
// 							      onCheck={onCheck}
// 							      treeData={treeData}
// 							    />