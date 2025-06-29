import React,{useEffect,useState} from 'react'
import { Link ,useHistory } from "react-router-dom"
import { Input ,Tree,Form,Select,DatePicker,TimePicker,Checkbox,Radio ,Button
, Col, Row,message,Modal ,Spin
} from 'antd';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import './index.scss'
// import 'antd/dist/antd.min.css';
import http from '../../../../server/server.js'

const { Option } = Select;

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 10 },
};
const tailLayout = {
  wrapperCol: { offset: 4, span: 16 },
};
const { TreeNode } = Tree;

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
const Strategy = (props) =>{
	// console.log(props)
	const history= useHistory();
	const [expandedKeys, setExpandedKeys] = useState(['0-0-0', '0-0-1']);
	const [checkedKeys, setCheckedKeys] = useState([]);
	const [selectedKeys, setSelectedKeys] = useState([]);
	const [autoExpandParent, setAutoExpandParent] = useState(false);
	const [index, setIndex] = useState(0);
	const [treeData, setTreeData] = useState([]);
	const [diveceNum, setDiveceNum] = useState('');
	const [load, setLoad] = useState('');
	const [strategyName, setStrategyName] = useState(''); // 策略名称
	const [strategyId, setStrategyId] = useState(''); // 策略id
	const [strategyStatus, setStrategyStatus] = useState(''); // 状态
	const [strategyType, setStrategyType] = useState(''); // 策略类型
	const [runStrategy, setRunStrategy] = useState(0); // 运行策略类型
	const [createdTime, setCreatedTime] = useState(''); // 创建时间
	const [deviceIdList, setDeviceIdList] = useState([]); // 设备列表
	const [demandResponse, setDemandResponse] = useState(''); // 自动需求响应
	const [ratedPower, setRatedPower] = useState('');
	const [demandValue, setDemandValue] = useState('');
	const [defaultExpandedKeys, setDefaultExpandedKeys] = useState([]);
	const [defaultSelectedKeys, setDefaultSelectedKeys] = useState([]);
	const [defaultCheckedKeys, setDefaultCheckedKeys] = useState([]);
	const [edit, setEdit] = useState(''); // 0编辑，1新增
	const [ownerId, setOwnerId] = useState('');
	const [checkedList, setCheckedList] = useState([]);
	const [indeterminate, setIndeterminate] = useState(false);
	const [checkAll, setCheckAll] = useState(false);
	const [username, setUsername] = useState('');
	const [disableds, setDisableds] = useState('');
	const [zdisabled, setZdisabled] = useState('');
	const [newdisplay, setNewdisplay] = useState('none');
	const [isResponse, setIsResponse] = useState(null);
	const [loading1, setLoading1] = useState(false);
	const [setLoadings, setSetLoadings] = useState(false);
	const [isShow, setIsShow] = useState(2);
	const [deviceNum,setDeviceNum] = useState('');
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
		
		if(strategyId&&edit==0){
			strategyDetail()
		}
	},[strategyId,edit]);
	useEffect(() =>{
		// alert(isResponse)
		if(isResponse!==null){
			areaDeviceView()
		}
	},[isResponse])
	// useEffect(() =>{
	// 	areaDeviceView()
	// },[edit,isResponse])
	// useEffect(() =>{
	// 	areaDeviceView()
	// },[demandResponse])
	// 详情
	const strategyDetail = () =>{
		http.post('run_schedule/run_strategy/strategyDetail?strategyId='+strategyId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				// if(strategyType)
				let runStrategy = data.runStrategy
				console.log(data)
				if(runStrategy==0){
					// 一次性
					let nums = 0
					let airConditioningDTO =  data.airConditioningDTO
					console.log(airConditioningDTO)
					onceForm.setFieldsValue({
						note:dayjs(data.onceExe.ymd,'YYYY-MM-DD'),	//执行日期
						gender: dayjs(data.onceExe.times,'HH:mm'),
						power:data.airConditioningDTO.power=='POWER_ON'?'0':'1',
						temp:airConditioningDTO.temp=='TEMP_16'?"16":airConditioningDTO.temp=='TEMP_17'?"17":
						airConditioningDTO.temp=='TEMP_18'?"18":airConditioningDTO.temp=='TEMP_19'?"19":
						airConditioningDTO.temp=='TEMP_20'?"20":airConditioningDTO.temp=='TEMP_21'?"21":
						airConditioningDTO.temp=='TEMP_22'?"22":airConditioningDTO.temp=='TEMP_23'?"23":
						airConditioningDTO.temp=='TEMP_24'?"24":airConditioningDTO.temp=='TEMP_25'?"25":'26',
						mode:data.airConditioningDTO.mode=="MODE_COOL"?'0':'1',
						windSpeed:airConditioningDTO.windSpeed=="WS_LOW"?'1':data.airConditioningDTO.windSpeed=="WS_MEDIUM"?'2':'3'
					})
					console.log(data.mode)
					
					setStrategyName(data.strategyName)
					setDefaultExpandedKeys(data.deviceIdList);
					setDefaultSelectedKeys(data.deviceIdList);
					setDefaultCheckedKeys(data.deviceIdList);
					setCheckedKeys(data.deviceIdList);
					setDeviceIdList(data.deviceIdList);
					setDemandValue(data.demandResponse === true ? "1" : "2");
					setDisableds(data.airConditioningDTO.power === 'POWER_ON' ? '0' : '1');
					setCreatedTime(data.createdTime);
					setOwnerId(data.ownerId);
					setRatedPower(data.ratedPower);
					setRunStrategy(data.runStrategy);
					setStrategyStatus(data.strategyStatus);
					setStrategyType(data.strategyType);
					setDeviceNum(data.deviceIdList.length);
					setIsShow(data.demandResponse === true ? 1 : 2);
					setIndex(0)
					setIsResponse(data.demandResponse);
					var reg=/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/
					if (reg.test(data.strategyName)) {
						
						setStrategyName(data.strategyName);
						setNewdisplay('none')
					}else{
						setNewdisplay('block');
						setStrategyName(data.strategyName)
					}
					// areaDeviceView()
				}else if(runStrategy==1){
					console.log(data)
					let nums = 0
					cycleForm.setFieldsValue({
						// note:dayjs(data.onceExe.ymd,'YYYY-MM-DD'),	//执行日期
						gender: dayjs(data.cycleExe.cycleTimes,'HH:mm'),
						cycleWeeks:data.cycleExe.cycleWeeks,
						power:data.airConditioningDTO.power=='POWER_ON'?'0':'1',
						temp:data.airConditioningDTO.temp=='TEMP_16'?"16":data.airConditioningDTO.temp=='TEMP_17'?"17":'18',
						mode:data.airConditioningDTO.mode=="MODE_COOL"?'0':'1',
						windSpeed:data.airConditioningDTO.windSpeed=="WS_LOW"?'1':data.airConditioningDTO.windSpeed=="WS_MEDIUM"?'2':'3'

					})
					setIsShow(data.demandResponse === true ? 1 : 2);
					setDemandValue(data.demandResponse === true ? "1" : "2");
					setCreatedTime(data.createdTime);
					setOwnerId(data.ownerId);
					setRatedPower(data.ratedPower);
					setRunStrategy(data.runStrategy);
					setStrategyStatus(data.strategyStatus);
					setStrategyType(data.strategyType);
					
					setDeviceNum(data.deviceIdList.length);
					
					setDefaultExpandedKeys(data.deviceIdList);
					setDefaultSelectedKeys(data.deviceIdList);
					setDefaultCheckedKeys(data.deviceIdList);
					setCheckedKeys(data.deviceIdList);
					setDeviceIdList(data.deviceIdList);
					setIndex(1);
					setIsResponse(data.demandResponse);
					console.log()
					setLoad(data.ratedPower);
					var reg=/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/
					if (reg.test(data.strategyName)) {
						
						setStrategyName(data.strategyName);
						setNewdisplay('none')
					}else{
						setNewdisplay('block');
						setStrategyName(data.strategyName)
					}
					// areaDeviceView()
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 一次性，周期性
	const pattern =(e) =>{
		console.log(e)
		setIndex(e)
		setRunStrategy(e)
	}
	// 获取权限下区域-节点-系统-设备
	const areaDeviceView= () =>{
		setLoading1(true)
		http.post('run_schedule/run_strategy/areaDeviceView?isResponse='+isResponse+'&strategyId='+strategyId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data2 = res.data.data
				let nums = 0
				console.log(deviceIdList)
				if(deviceIdList.length>0){
					// let deviceIdList = this.state.deviceIdList
					
					function f(arr) {
						arr.forEach(item=>{
							// console.log(item)
							if(deviceIdList[i]==item.id){
								nums+=item.load
							}
							f(item.children) 
							
						})
					}
					// let deviceIdList = deviceIdList
					for(var i=0;i<deviceIdList.length;i++){
						
						f(data2)
					}
					setLoad(nums)
					console.log(nums)
				}else{
					setLoad('')
				}
				
				setTreeData(res.data.data)
				setLoading1(false)
			}else{
				setLoading1(false)
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 执行日期
	const onChange =(date, dateString) =>{
		console.log(date,dateString)
	}
	// 是否参与需求响应
	const demandResponseChange =(e) =>{
		console.log(e.target.value)
		setDemandResponse(e.target.value);
		setDemandValue(e.target.value);
		setIsResponse(e.target.value === '1' ? true : false);
		setDeviceIdList([]);
		setDeviceNum('');
		setLoad('');
		setCheckedKeys([]);
		setIsShow(e.target.value);
		onceForm.resetFields()
		cycleForm.resetFields()
		
		// areaDeviceView()
	}
	// 名称
	const inputChange=(e)=>{
		// console.log(e.target.value)
		let val = e.target.value
		setStrategyName(e.target.value)
	}
	// 是否能参加自动需求响应
	const checkDemandResponse= () =>{
		console.log(edit)
		http.post('run_schedule/run_strategy/checkDemandResponse').then(res =>{
			console.log(res)
			if(res.data.code==200){
				setDemandValue(res.data.data===true?"1":res.data.data===false?"2":'');
				setDemandResponse(res.data.data===true?"1":res.data.data===false?"2":'');
				setIsResponse(res.data.data)
				areaDeviceView()
			}else{
				message.info(res.data.msg)
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 提交
	const retset =() =>{
		
		if(edit==0){
			// 编辑
			updateStrategy()
		}else if(edit==1){
			// 新增
			addStrategy()
		}
	}
	//新增
	const addStrategy= () =>{
		let airConditioningDTO = new Object();
		console.log(airConditioningDTO);
		
		// 周期性
		let cycleExe = new Object()
		cycleExe["cycleTimes"] = ""
		cycleExe["cycleWeeks"] = []
		// 一次性
		let onceExe = new Object()
		// 其他空调
		let otherConditioningDTO = new Object()
		let demandResponse1 = ''
		setSetLoadings(true)
		// isShow  1参加，2不参加
		if(strategyType==0){
			if(isShow==1){
		
				// 参加
				let airConditioningDTO = new Object()
				airConditioningDTO["mode"] = 'MODE_COOL'
				airConditioningDTO["power"] = 'POWER_OFF'
				airConditioningDTO["temp"] = "TEMP_16"
				airConditioningDTO["windSpeed"] = 'WS_AUTO'
				console.log(airConditioningDTO)
				// 周期性
				let cycleExe = new Object()
				cycleExe["cycleTimes"] = ""
				cycleExe["cycleWeeks"] = []
				let onceExe = new Object()
				onceExe["times"] = dayjs().format('HH:mm')
				onceExe["ymd"] = dayjs().format('YYYY-MM-DD')
				// onceExe["times"] = ''
				// onceExe["ymd"] = ''
				// 其他空调
				let otherConditioningDTO = new Object()
				// console.log(this.refs.onceForm.getFieldsValue())
				let values = onceForm.getFieldsValue()
				console.log(values)
				console.log(demandResponse)
				console.log(demandValue)
				demandResponse1 = demandResponse=="1"?true:false
				console.log(demandResponse1)
				
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
						history.push({
							pathname: '/tactics'
						})
						setSetLoadings(false);
						message.success('成功');
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
			}else if(isShow==2){
				// 不参加
				// 空调
				if(index==0){
					// 一次性
					let airConditioningDTO = new Object()
					console.log(airConditioningDTO)
					console.log(strategyName)
					
					// 周期性
					let cycleExe = new Object()
					cycleExe["cycleTimes"] = ""
					cycleExe["cycleWeeks"] = []
					let onceExe = new Object()
					// 其他空调
					let otherConditioningDTO = new Object()
					console.log(onceForm.getFieldsValue())
					let values = onceForm.getFieldsValue()
					console.log(values)
					console.log(demandResponse)
					console.log(demandValue)
					demandResponse1 = demandResponse=="1"?true:false
					console.log(demandResponse1)
					// 一次性
					// TEMP_16,TEMP_17,TEMP_18,TEMP_19,TEMP_20,TEMP_21,TEMP_22,TEMP_23,TEMP_24,TEMP_25,TEMP_26
					let temp =  values.temp
					airConditioningDTO["mode"] = values.mode=="0"?'MODE_COOL':values.mode=="1"?'MODE_DRY':''
					airConditioningDTO["power"] = values.power=="0"?'POWER_ON':'POWER_OFF'
					airConditioningDTO["temp"] = temp=="16"?"TEMP_16":temp=="17"?"TEMP_17":temp=="18"?"TEMP_18":temp=="19"?"TEMP_19"
					:temp=="20"?"TEMP_20":temp=="21"?"TEMP_21":temp=="22"?"TEMP_22":temp=="23"?"TEMP_23":temp=="24"?"TEMP_24"
					:temp=="25"?"TEMP_25":temp=="26"?"TEMP_26":""
					airConditioningDTO["windSpeed"] = values.windSpeed=="1"?"WS_LOW":values.windSpeed=="2"?"WS_MEDIUM":values.windSpeed=="3"?'WS_HIGH':''
					console.log(airConditioningDTO)
					// 时间，周期
					onceExe["times"] = values.gender?dayjs(values.gender).format('HH:mm'):""
					onceExe["ymd"] = values.note?dayjs(values.note).format('YYYY-MM-DD'):''
					
					console.log(onceExe)
					if(strategyName==''){
						setSetLoadings(false);
						message.info('请输入策略名称');
					}
					if(deviceIdList.length==0){
						setSetLoadings(false);
						message.info('请选择设备');
					}
					if(newdisplay=='block'){
						setSetLoadings(false);
						message.info('请输入6～12个汉字、字母或数字、_')
					}
					if(isShow==1){
						if(airConditioningDTO["power"]&&newdisplay=='none'){
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
									history.push({
										pathname: '/tactics',
										
									});
									setSetLoadings(false);
									message.success('成功');
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
						}
					}else{
						if(airConditioningDTO["power"]&&onceExe["times"]&&onceExe["ymd"]&&newdisplay=='none'){
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
									history.push({
										pathname: '/tactics',
										
									});
									setSetLoadings(false);
									message.success('成功');
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
					}
					
					
					
				}else{
					// 周期性
					let cycleExe = new Object()
					cycleExe["cycleTimes"] = ""
					cycleExe["cycleWeeks"] = []
					let onceExe = new Object()
					// 其他空调
					let otherConditioningDTO = new Object()
					
					demandResponse1 = demandResponse=="1"?true:false
					console.log(demandResponse1)
					// 其他空调
					console.log(cycleForm.getFieldsValue())
					let values = cycleForm.getFieldsValue()
					airConditioningDTO["mode"] = values.mode=="0"?'MODE_COOL':'MODE_DRY'
					airConditioningDTO["power"] = values.power=="0"?'POWER_ON':'POWER_OFF'
					let temp = values.temp
					// airConditioningDTO["temp"] = values.temp=="16"?"TEMP_16":"TEMP_17"
					airConditioningDTO["temp"] = temp=="16"?"TEMP_16":temp=="17"?"TEMP_17":temp=="18"?"TEMP_18":temp=="19"?"TEMP_19"
					:temp=="20"?"TEMP_20":temp=="21"?"TEMP_21":temp=="22"?"TEMP_22":temp=="23"?"TEMP_23":temp=="24"?"TEMP_24"
					:temp=="25"?"TEMP_25":temp=="26"?"TEMP_26":""
					// airConditioningDTO["windSpeed"] = values.windSpeed=="1"?"WS_AUTO":values.windSpeed=="2"?"WS_LOW":values.windSpeed=="3"?'WS_MEDIUM':''
					airConditioningDTO["windSpeed"] = values.windSpeed=="1"?"WS_LOW":values.windSpeed=="2"?"WS_MEDIUM":values.windSpeed=="3"?'WS_HIGH':''
					console.log(airConditioningDTO)
					cycleExe["cycleTimes"] = dayjs(values.gender).format('HH:mm')
					cycleExe["cycleWeeks"] = values.cycleWeeks
					if(strategyName==''){
						setSetLoadings(false);
						message.info('请输入策略名称');
					}
					
					if(deviceIdList.length==0){
						setSetLoadings(false);
						message.info('请选择设备')
					}
					if(airConditioningDTO["mode"]&&airConditioningDTO["power"]&&airConditioningDTO["temp"]&&
					airConditioningDTO["windSpeed"]&&cycleExe["cycleTimes"]&&cycleExe["cycleWeeks"]&&newdisplay=='none'){
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
									
								});
							}else{
								// message.info(res.data.msg)
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
		console.log(airConditioningDTO)
		console.log(strategyName)
	}
	// 编辑
	const updateStrategy= () =>{
		
		let demandResponse1 = ''
		setSetLoadings(true)
		if(strategyType==0){
			// 空调
			
			// isShow==1参加，2不参加
			if(isShow==1){
				// 参加
				let airConditioningDTO = new Object()
				airConditioningDTO["mode"] = 'MODE_COOL'
				airConditioningDTO["power"] = 'POWER_OFF'
				airConditioningDTO["temp"] = "TEMP_16"
				airConditioningDTO["windSpeed"] = 'WS_AUTO'
				console.log(airConditioningDTO)
				console.log(strategyName)
				let cycleExe = new Object()
				cycleExe["cycleTimes"] = ""
				cycleExe["cycleWeeks"] = []
				let onceExe = new Object()
				// 其他空调
				let otherConditioningDTO = new Object()
				
				demandResponse1 = demandResponse=="1"?true:false
				console.log(demandResponse1)
				http.post('run_schedule/run_strategy/updateStrategy',{
					"airConditioningDTO": airConditioningDTO,
					"createdTime": createdTime,
					"cycleExe": cycleExe,
					"demandResponse": demandResponse1,
					"deviceIdList": deviceIdList,
					"onceExe": onceExe,
					"otherConditioningDTO": otherConditioningDTO,
					"ownerId": ownerId,
					"ratedPower": ratedPower,
					"runStrategy": runStrategy,
					"strategyId": strategyId,
					"strategyName": strategyName,
					"strategyStatus": strategyStatus,
					"strategyType": strategyType,
				}).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('成功')
						history.push({
							pathname: '/tactics',
							
						});
						setSetLoadings(false)
					}else{
						// message.info(res.data.msg)
						// Modal.error(res.data.msg)
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
				if(runStrategy==0){
					
					console.log(strategyName)
					
					// 周期性
					let cycleExe = new Object()
					cycleExe["cycleTimes"] = ""
					cycleExe["cycleWeeks"] = []
					let onceExe = new Object()
					// 其他空调
					let otherConditioningDTO = new Object()
					// console.log(this.refs.onceForm.getFieldsValue())
					let values = onceForm.getFieldsValue()
					console.log(values)
					demandResponse1 = demandResponse=="1"?true:false
					console.log(demandResponse1)
					if(strategyName==''){
						message.info('请输入策略名称')
						// this.setState({
						// 	setLoadings:false
						// })
					}
					
					if(deviceIdList.length==0){
						message.info('请选择设备')
					}
					if(newdisplay=='block'){
						message.info('请输入6～12个汉字、字母或数字、_')
					}
					// 一次性
					let airConditioningDTO = new Object()
					airConditioningDTO["mode"] = values.mode=="0"?'MODE_COOL':'MODE_DRY'
					airConditioningDTO["power"] = values.power=="0"?'POWER_ON':'POWER_OFF'
					let temp = values.temp
					airConditioningDTO["temp"] = temp=="16"?"TEMP_16":temp=="17"?"TEMP_17":temp=="18"?"TEMP_18":temp=="19"?"TEMP_19"
					:temp=="20"?"TEMP_20":temp=="21"?"TEMP_21":temp=="22"?"TEMP_22":temp=="23"?"TEMP_23":temp=="24"?"TEMP_24"
					:temp=="25"?"TEMP_25":temp=="26"?"TEMP_26":""
					// airConditioningDTO["windSpeed"] = values.windSpeed=="1"?"WS_AUTO":values.windSpeed=="2"?"WS_LOW":values.windSpeed=="3"?'WS_MEDIUM':''
					// airConditioningDTO["windSpeed"] = values.windSpeed=="1"?"WS_AUTO":values.windSpeed=="2"?"WS_MEDIUM":'WS_MEDIUM'
					airConditioningDTO["windSpeed"] = values.windSpeed=="1"?"WS_LOW":values.windSpeed=="2"?"WS_MEDIUM":values.windSpeed=="3"?'WS_HIGH':''
					console.log(airConditioningDTO)
					onceExe["times"] = dayjs(values.gender).format('HH:mm')
					onceExe["ymd"] = dayjs(values.note).format('YYYY-MM-DD')
					if(airConditioningDTO["mode"]&&airConditioningDTO["power"]&&onceExe["times"]&&airConditioningDTO["temp"]
					&&airConditioningDTO["windSpeed"]&&onceExe["ymd"]&&newdisplay=='none'){
						http.post('run_schedule/run_strategy/updateStrategy',{
							"airConditioningDTO": airConditioningDTO,
							"createdTime": createdTime,
							"cycleExe": cycleExe,
							"demandResponse": demandResponse1,
							"deviceIdList": deviceIdList,
							"onceExe": onceExe,
							"otherConditioningDTO": otherConditioningDTO,
							"ownerId": ownerId,
							"ratedPower": ratedPower,
							"runStrategy": runStrategy,
							"strategyId": strategyId,
							"strategyName": strategyName,
							"strategyStatus": strategyStatus,
							"strategyType": strategyType,
						}).then(res =>{
							console.log(res)
							if(res.data.code==200){
								message.success('成功')
								history.push({
									pathname: '/tactics',
									
								});
								
								setSetLoadings(false)
							}else{
								// message.info(res.data.msg)
								// Modal.error(res.data.msg)
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
						setSetLoadings(false);
						message.info('请选择一次性信息')
					}
				}else{
					// 周期性
					let airConditioningDTO = new Object()
					console.log(airConditioningDTO)
					console.log(strategyName)
					let cycleExe = new Object()
					cycleExe["cycleTimes"] = ""
					cycleExe["cycleWeeks"] = []
					let onceExe = new Object()
					// 其他空调
					let otherConditioningDTO = new Object()
					
					demandResponse1 = demandResponse=="1"?true:false
					console.log(demandResponse1)
					// 其他空调
					console.log(cycleForm.getFieldsValue())
					let values = cycleForm.getFieldsValue()
					airConditioningDTO["mode"] = values.mode=="0"?'MODE_COOL':'MODE_DRY'
					airConditioningDTO["power"] = values.power=="0"?'POWER_ON':'POWER_OFF'
					// airConditioningDTO["temp"] = values.temp=="16"?"TEMP_16":"TEMP_17"
					let temp = values.temp
					airConditioningDTO["temp"] = temp=="16"?"TEMP_16":temp=="17"?"TEMP_17":temp=="18"?"TEMP_18":temp=="19"?"TEMP_19"
					:temp=="20"?"TEMP_20":temp=="21"?"TEMP_21":temp=="22"?"TEMP_22":temp=="23"?"TEMP_23":temp=="24"?"TEMP_24"
					:temp=="25"?"TEMP_25":temp=="26"?"TEMP_26":""
					airConditioningDTO["windSpeed"] = values.windSpeed=="1"?"WS_AUTO":values.windSpeed=="2"?"WS_LOW":values.windSpeed=="3"?'WS_MEDIUM':''
					console.log(airConditioningDTO)
					cycleExe["cycleTimes"] = dayjs(values.gender).format('HH:mm')
					cycleExe["cycleWeeks"] = values.cycleWeeks
					
					if(strategyName==''){
						message.info('请输入策略名称')
					}
					
					if(deviceIdList.length==0){
						message.info('请选择设备')
					}
					if(airConditioningDTO["mode"]&&airConditioningDTO["power"]&&airConditioningDTO["temp"]&&
					airConditioningDTO["windSpeed"]&&cycleExe["cycleTimes"]&&cycleExe["cycleWeeks"]){
						http.post('run_schedule/run_strategy/updateStrategy',{
							"airConditioningDTO": airConditioningDTO,
							"createdTime": createdTime,
							"cycleExe": cycleExe,
							"demandResponse": demandResponse1,
							"deviceIdList": deviceIdList,
							"onceExe": onceExe,
							"otherConditioningDTO": otherConditioningDTO,
							"ownerId": ownerId,
							"ratedPower": ratedPower,
							"runStrategy": runStrategy,
							"strategyId": strategyId,
							"strategyName": strategyName,
							"strategyStatus": strategyStatus,
							"strategyType": strategyType,
						}).then(res =>{
							console.log(res)
							if(res.data.code==200){
								message.success('成功')
								setSetLoadings(false)
								history.push({
									pathname: '/tactics',
									
								});
							}else{
								
								setSetLoadings(false)
								message.info(res.data.msg)
								
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
	}
	// 焦点触发
	const handleBlur =() =>{
		console.log('失去焦点')
		// let val = e.target.value
		var reg=/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,12}$/
		// var pattern=new RegExp(reg);
		// console.log(pattern.test("123"));
		if (reg.test(strategyName)) {
			setNewdisplay('none')
		}else{
			setNewdisplay('block')
		}
	}
	const onExpand = (expandedKeysValue) => {
		console.log('onExpand', expandedKeysValue);
		setSelectedKeys(expandedKeysValue)
		setAutoExpandParent(false)
	};
	
	const onCheck = (checkedKeys, info) => {
		console.log('onCheck', checkedKeys, info);
		
		setCheckedKeys(checkedKeys)
		if(info.checked==true){
			console.log('onCheck', checkedKeys, info);
			let checkedNodes = info.checkedNodes
				let deviceIdList = []
				let load = 0
				let diveceNum = 0
				for(var i=0;i<checkedNodes.length;i++){
					// console.log(checkedNodes[i])
					if(checkedNodes[i].type=='SYSTEM'){
						// if(checkedNodes[i].children.length==0){
							deviceIdList.push(checkedNodes[i].key)
							load += checkedNodes[i].load
							diveceNum ++
						// }
					}
					if(checkedNodes[i].type=='NODE'){
						// if(checkedNodes[i].children.length==0){
							deviceIdList.push(checkedNodes[i].key)
							load += checkedNodes[i].load
							diveceNum ++
						// }
					}
					if(checkedNodes[i].type=='DEVICE'){
						// if(checkedNodes[i].children.length==0){
							deviceIdList.push(checkedNodes[i].key)
							load += checkedNodes[i].load
							diveceNum ++
						// }
					}
					
				}
				console.log(deviceIdList)
				console.log()
				setDeviceIdList(deviceIdList);
				setLoad(load);
				setDeviceNum(diveceNum);
				setRatedPower(load);
			
			
		}else if(info.checked==false){
			console.log('onCheck', checkedKeys, info);
			let checkedNodes = info.checkedNodes
			let deviceIdList = []
			let load = 0
			let diveceNum = 0
			for(var i=0;i<checkedNodes.length;i++){
				// console.log(checkedNodes[i])
				if(checkedNodes[i].type=='SYSTEM'){
					// if(checkedNodes[i].children.length==0){
						deviceIdList.push(checkedNodes[i].key)
						load += checkedNodes[i].load
						diveceNum += 1
					// }
				}
				if(checkedNodes[i].type=='NODE'){
					// if(checkedNodes[i].children.length==0){
						deviceIdList.push(checkedNodes[i].key)
						load += checkedNodes[i].load
						diveceNum ++
					// }
				}
				if(checkedNodes[i].type=='DEVICE'){
					// if(checkedNodes[i].children.length==0){
						deviceIdList.push(checkedNodes[i].key)
						load += checkedNodes[i].load
						diveceNum ++
					// }
				}
				
			}
			console.log(deviceIdList)
			console.log(diveceNum)
			setDeviceIdList(deviceIdList);
			setLoad(load);
			setDeviceNum(diveceNum);
			setRatedPower(load);
		}
		
		
		
	};
		
		const onSelect = (selectedKeysValue, info) => {
		    console.log('onSelect', selectedKeysValue, info);
			
			setSelectedKeys(selectedKeysValue)
		};
		const onFinish = (values: any) => {
		    console.log(values);
		  };
		
		const onCheckAllChange = (e: CheckboxChangeEvent) => {
			console.log(e)
		    // setCheckedList(e.target.checked ? plainOptions : []);
			
			setCheckedList(e.target.checked ? [1,2,3,4,5,6,7] : []);
			setIndeterminate(false);
			setCheckAll(e.target.checked)
			cycleForm.setFieldsValue({
				cycleWeeks:e.target.checked ? [1,2,3,4,5,6,7] : []
			})
		};
		const onChange1 = (list) => {
			console.log(list)
			setCheckedList(list);
			setIndeterminate(!!list.length && list.length < plainOptions.length);
			setCheckAll(list.length === plainOptions.length)
		
		   
		};
		// 动作
		const powerChange =(e) =>{
			console.log(e.target.value,runStrategy)
			let val = e.target.value
			if(runStrategy==0){
				// 一次性
				if(val==0){
					// 开启
					
					onceForm.setFieldsValue({
						power:val
					})
					setDisableds(val)
				}else if(val==1){
					// alert(0)
					// 关闭
					setDisableds(val)
					onceForm.setFieldsValue({
						
						temp:'16',
						mode:'0',
						windSpeed:'1'

					})
					
				}
				
			}else if(runStrategy==1){
				if(val==0){
					// 开启
					setZdisabled(val)
					cycleForm.setFieldsValue({
						power:val
					})
				}else if(val==1){
					// alert(0)
					// 关闭
					setZdisabled(val)
					cycleForm.setFieldsValue({

						// power:'0',
						temp:'16',
						mode:'0',
						windSpeed:'1'
						
					})
					
				}
			}
			
			
		}
		const renderTree = (data: any) => {
			// console.log(data)
		    if (data && data.length > 0) {
		        return data.map((item: any) => {
		            return (
		                <TreeNode checkable={item.type=='DEVICE'?true:false} load={item.load} type={item.type} title={item.title} key={item.key}>
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
		const format = 'HH:mm';
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
						<span className="regs" style={{display:newdisplay}}>请输入6～12个汉字、字母或数字、_</span>
					</div>
					<div className="title">
						<span className="strategytitle">*</span>是否参与自动需求响应：
						<Radio.Group value={demandValue}   onChange={demandResponseChange}>
							<Radio value="1"> 参与 </Radio>
							<Radio value="2"> 不参与 </Radio>
						</Radio.Group>
					</div>
					<div className="title">
						<span className="strategytitle">*</span>选择设备：
						<p>已选设备：{deviceNum} <span>已选总额定负荷(kW)：{load}</span></p>
						<Spin spinning={loading1}>
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
					<div className="title" style={{newdisplay:isShow==2?'block':'none'}}>
						<span className="strategytitle">*</span>运行策略：
						<div className="finesse">
							<span onClick={() => pattern(0)} className={index == 0 ? 'active' : 'noactive'}>一次性</span>
							<span onClick={() => pattern(1)} className={index == 1 ? 'active' : 'noactive'}>周期性</span>
						</div>
						<div className="period" style={{width:808,display:index==0?'block':'none'}}>
							<Form labelCol={{ span: 4 }}
								wrapperCol={{ span: 14 }}  
								layout="horizontal"
								// ref="onceForm"
								form={onceForm}
								name="control-hooks" onFinish={onFinish}>
								
								<Form.Item name="note" label="执行日期：" rules={[{ required: isShow==2?true:false, message: '请选择'}]} >
									<DatePicker style={{width:'336px',float:'left'}} />
								</Form.Item>
								<Form.Item name="gender" label="执行时间："  rules={[{ required: isShow==2?true:false, message: '请选择'}]}>
									<TimePicker  style={{width:'210px'}} format={format} defaultOpenValue={dayjs('00:00:00', 'HH:mm:ss')} />
								</Form.Item>
							    <Form.Item label="执行动作：" name="power" 
								rules={[{ required: isShow==2?true:false, message: '请选择'}]}>
									<Radio.Group onChange={powerChange} style={{marginLeft:20}}>
								        <Radio value="0"> 开启 </Radio>
								        <Radio value="1"> 关闭 </Radio>
								    </Radio.Group>
								</Form.Item>
								<Form.Item label="温度设定：" 
									required
								>
									<Form.Item name="temp" style={{width:'100px',float:'left'}}
										rules={[
											{
												required: isShow==2?true:false,
												pattern: /^(1[6-9]|(2[0-6]))$/,
												message: "请输入正确的温度",
											}
										]}
									 >
										<Input style={{width:'85px',marginLeft:20}} disabled={disableds==0?false:true} />
									</Form.Item>
									<span style={{color:'#FFF',margin:'5px 0px 0px 20px'}}>°C（温度范围：16°C-26°C）</span>
								</Form.Item>
								
							    <Form.Item label="运行模式："  name="mode" rules={[{ required: isShow==2?true:false, message: '请选择'}]}>
							    	<Radio.Group disabled={disableds==0?false:true} style={{marginLeft:20}}>
							            <Radio value="0"> 制冷 </Radio>
							            <Radio value="1"> 制热 </Radio>
							        </Radio.Group>
							    </Form.Item> 
								<Form.Item label="风速档位：" name="windSpeed" rules={[{ required: isShow==2?true:false, message: '请选择'}]}>
									<Radio.Group disabled={disableds==0?false:true} style={{marginLeft:20}}>
								        <Radio value="1"> 一档 </Radio>
								        <Radio value="2"> 二档 </Radio>
										<Radio value="3"> 三档 </Radio>
								    </Radio.Group>
								</Form.Item> 
							</Form>
						</div>
						<div className="period" style={{width:808,display:index==1?'block':'none'}}>
							<Form labelCol={{ span: 4 }}
								wrapperCol={{ span: 20 }}  
								layout="horizontal"
								// ref="cycleForm"
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
									<Radio.Group onChange={powerChange} style={{marginLeft:16}}>
								        <Radio value="0"> 开启 </Radio>
								        <Radio value="1"> 关闭 </Radio>
								    </Radio.Group>
								</Form.Item>
								<Form.Item label="温度设定：" required>
									<Form.Item name="temp" style={{width:'100px',float:'left'}} 
										rules={[
											
											{
												 required: true,
												pattern: /^(1[6-9]|(2[0-6]))$/,
												message: "请输入正确的温度",
											}
										]}
									>
										<Input disabled={zdisabled==0?false:true} style={{width:'85px',marginLeft:16}} />
									</Form.Item>
									<span style={{color:'#FFF',margin:'5px 0px 0px 20px'}}>°C（温度范围：16°C-26°C）</span>
								</Form.Item>
								
							    <Form.Item label="运行模式：" name="mode" rules={[{ required: true, message: '请选择'}]}>
							    	<Radio.Group disabled={zdisabled==0?false:true} style={{marginLeft:16}}>
							            <Radio value="0"> 制冷 </Radio>
							            <Radio value="1"> 制热 </Radio>
							        </Radio.Group>
							    </Form.Item> 
								<Form.Item label="风速档位：" name="windSpeed" required>
									<Radio.Group disabled={zdisabled==0?false:true} style={{marginLeft:16}}>
								        <Radio value="1"> 一档 </Radio>
								        <Radio value="2"> 二档 </Radio>
										<Radio value="3"> 三档 </Radio>
								    </Radio.Group>
								</Form.Item> 
							</Form>
						</div>
					</div>
					
				</div>
				<div className="bottom">
					<Button loading={setLoadings} type="primary" onClick={retset}>确定</Button>
					<Button ghost><Link to='/tactics'>取消</Link></Button>
				</div>
			</div>
		)
	}


export default Strategy

// <span onClick={() => this.pattern(1)} className={this.state.index == 1 ? 'active' : 'noactive'}>周期性</span>
// <Tree
// 	checkable
// 	defaultCheckedKeys={this.state.defaultCheckedKeys}
// 	defaultExpandedKeys = {this.state.defaultCheckedKeys}
// 	checkedKeys={checkedKeys} //选中的key
// 	onSelect={onSelect}
// 	onCheck={onCheck}
// 	treeData={treeData}
// />