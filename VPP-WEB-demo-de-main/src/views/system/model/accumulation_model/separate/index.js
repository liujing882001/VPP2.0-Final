import React, { useEffect,useState } from 'react';
import { Button, Checkbox, Form, Input,DatePicker ,Col,Row,ConfigProvider,message,
Table,Select,Spin 
} from 'antd';

import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';


import './index.css'
import './index.scss'
import http from '../../../../../server/server.js'
const { RangePicker,MonthPicker } = DatePicker;
const monthFormat = 'YYYY-MM';
const { Option } = Select;


const Separate =(props) =>{
	const [dataSource, setDataSource] = useState([]);
	const [power, setPower] = useState('');
	const [volume, setVolume] = useState('');
	const [isShow, setIsShow] = useState(false);
	const [val, setVal] = useState('编辑');
	const [username, setUsername] = useState('');
	const [nodeNameList, setNodeNameList] = useState([]);
	const [nodeSystemList, setNodeSystemList] = useState([]);
	const [systemName, setSystemName] = useState('请选择1');
	const [nodeId, setNodeId] = useState('');
	const [systemId, setSystemId] = useState('');
	const [yearlimit, setYearLimit] = useState('');
	const [startyear, setStartYear] = useState('2022-02');
	const [storageEnergyLoad, setStorageEnergyLoad] = useState('-');
	const [storageEnergyCapacity, setStorageEnergyCapacity] = useState('-'); //储能电池容量
	const [maxPercent,setMaxPercent] = useState('');
	const [minPercent,setMinPercent] = useState('');
	const [maxChargePercent,setMaxChargePercent] = useState('');
	const [minDischargePercent,setMinDischargePercent] = useState('');
	const [shareProportionExpiryDate, setShareProportionExpiryDate] = useState('-'); //分层比例年限
	const [shareProportionStartTime, setShareProportionStartTime] = useState('-'); //分层比例开始时间
	const [monthday, setMonthDay] = useState('');
	const [page, setPage] = useState(1);
	const [total, setTotal] = useState('');
	const [strategyExpiryDate, setStrategyExpiryDate] = useState('-'); //充放电策略年限
	const [strategyStartTime, setStrategyStartTime] = useState('-'); //充放电策略开始时间
	const [devied, setDevied] = useState('编辑');
	const [charge, setCharge] = useState('编辑');
	const [disabled, setDisabled] = useState(true);
	const [fdisabled, setFdisabled] = useState(true);
	const [deviceSnName, setDeviceSnName] = useState([]);
	const [battery_status_device_sn, setBattery_status_device_sn] = useState('-'); //电池
	const [battery_status_device_sn1, setBattery_status_device_sn1] = useState('');
	const [charging_device_sn, setCharging_device_sn] = useState('-'); //充
	const [charging_device_sn1, setCharging_device_sn1] = useState('');
	const [discharging_device_sn, setDischarging_device_sn] = useState('-'); //放
	const [discharging_device_sn1, setDischarging_device_sn1] = useState('');
	const [battey, setBattey] = useState([]);
	const [deviceSnid, setDeviceSnid] = useState('编辑');
	const [s, setS] = useState('');
	const [treeLoading, setTreeLoading] = useState(false);
	const [args, setArgs] = useState('');
	const [name, setName] = useState('');
	const [requestUrl, setRequestUrl] = useState('');
	const [synchronous, setSynchronous] = useState('编辑');
	const [distribute, setDistribute] = useState('编辑');
	const [args1, setArgs1] = useState('');
	const [name1, setName1] = useState('');
	const [requestUrl1, setRequestUrl1] = useState('');
	const [names,setNames] = useState('');
	const [names1,setNames1] = useState('');
	const [myForms] = Form.useForm();
	const [myForm] = Form.useForm();
	const [myForm1] = Form.useForm();
	const [myForm2] = Form.useForm();
	const [synchronousForm] = Form.useForm();
	const [DistributeForm] = Form.useForm();
	useEffect(() =>{
		if(props.istrue ==true){
			setNodeId(props.nodeId);
			setSystemId(props.systemId);
			setStorageEnergyLoad(props.storageEnergyLoad?props.storageEnergyLoad:'-');
			setStorageEnergyCapacity(props.storageEnergyCapacity?props.storageEnergyCapacity:'-');
			setMaxChargePercent(props.maxChargePercent!==null&&props.maxChargePercent!==undefined&&props.maxChargePercent!==''?props.maxChargePercent:'-')
			setMinDischargePercent(props.minDischargePercent!==null&&props.minDischargePercent!==undefined&&props.minDischargePercent!==''?props.minDischargePercent:'-')
			setMinPercent(props.minDischargePercent!==null&&props.minDischargePercent!==undefined&&props.minDischargePercent!==''?props.minDischargePercent:'-')
			setMaxPercent(props.maxChargePercent!==null&&props.maxChargePercent!==undefined&&props.maxChargePercent!==''?props.maxChargePercent:'-')
			setVolume(props.storageEnergyCapacity?props.storageEnergyCapacity:'-');
			setPower(props.storageEnergyLoad?props.storageEnergyLoad:'-');
			setShareProportionExpiryDate(props.shareProportionExpiryDate?props.shareProportionExpiryDate:'-');
			setShareProportionStartTime(props.shareProportionStartTime?props.shareProportionStartTime:'-');
			setStrategyExpiryDate(props.strategyExpiryDate?props.strategyExpiryDate:'-');
			setStrategyStartTime(props.strategyStartTime?props.strategyStartTime:'-');
			setDeviceSnName(props.deviceSnName);
			setBattery_status_device_sn(props.battery_status_device_sn?props.battery_status_device_sn:'-');
			setCharging_device_sn(props.charging_device_sn?props.charging_device_sn:'-');
			setDischarging_device_sn(props.discharging_device_sn?props.discharging_device_sn:'-');
			setBattery_status_device_sn1(props.battery_status_device_sn);
			setCharging_device_sn1(props.charging_device_sn);
			setDischarging_device_sn1(props.discharging_device_sn);
			setName(props.name?props.name:'-');
			setArgs(props.args?props.args:'-');
			setRequestUrl(props.requestUrl?props.requestUrl1:'-');
			setName1(props.name1?props.name1:'-');
			setArgs1(props.args1?props.args1:'-');
			setRequestUrl1(props.requestUrl1?props.requestUrl1:'-');
			
		}else{
			return false
		}
	},[props])
	useEffect(() =>{
		if(requestUrl1){
			props.changeData()
		}
		
	},[requestUrl1])
	
	// 电站容量，电池功率
	const check=()=>{
		
		setIsShow(!isShow);
		if(isShow ==true){
			let values = myForms.getFieldsValue()
			console.log(values)
			http.post('system_management/energy_model/energy_storage_model/saveStorageEnergyBaseInfo',{
				"nodeId": props.nodeId,
				"storageEnergyCapacity": values.storageEnergyCapacity,	//容量
				"storageEnergyLoad": values.storageEnergyLoad,
				"maxChargePercent":values.maxChargePercent,
				"minDischargePercent":values.minDischargePercent,
				"systemId": props.systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功');
					setStorageEnergyLoad(values.storageEnergyLoad);
					setStorageEnergyCapacity(values.storageEnergyCapacity);
					setMinDischargePercent(values.minDischargePercent)
					setMaxChargePercent(values.maxChargePercent)
					setTreeLoading(false)
				}else{
					setTreeLoading(false)
					message.warning(res.data.msg)
				}
				props.onQuery(props.systemId)
			}).catch(err =>{
				console.log(err)
			})
		}else if(isShow ==false){
			myForms.setFieldsValue({
				storageEnergyLoad:power,	//电站功率
				storageEnergyCapacity:volume,
				minDischargePercent:minPercent,
				maxChargePercent:maxPercent
			})
			setIsShow(true)
		}
	}
	
	// 取消
	const qqxiao =() =>{
		setIsShow(false);
		setStorageEnergyLoad(storageEnergyLoad);
		setStorageEnergyCapacity(storageEnergyCapacity)
	}
	// 分成比例确认
	useEffect(() =>{
		if(devied=='确认'&&fdisabled==true&&treeLoading==true){
			let values = myForm.getFieldsValue();
			console.log(values)
			http.post('system_management/energy_model/energy_storage_model/saveStorageEnergyShareProportionBaseInfo',{
				"nodeId": props.nodeId,
				"shareProportionExpiryDate":values.shareProportionExpiryDate, 
				"shareProportionStartTime":values.shareProportionStartTime?values.shareProportionStartTime.format('YYYY-MM'):'',
				"systemId": props.systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功');
					setTreeLoading(false);
					setShareProportionExpiryDate(values.shareProportionExpiryDate);
					setShareProportionStartTime(values.shareProportionStartTime?values.shareProportionStartTime.format('YYYY-MM'):'')
				}else{
					setTreeLoading(false);
					message.warning(res.data.msg)
				}
				setDevied('编辑');
				props.onQuery(props.systemId)
			
			}).catch(err =>{
				console.log(err)
			})
		}else{
			
		}
	},[devied,fdisabled,treeLoading])
	
		// 分成比例
	const devieds =() =>{
		
		if(devied=='编辑'){
			setDevied('确认');
			setFdisabled(false)
			myForm.setFieldsValue({
				shareProportionExpiryDate:shareProportionExpiryDate&&shareProportionExpiryDate!='-'?shareProportionExpiryDate:'',
				shareProportionStartTime:shareProportionStartTime&&shareProportionStartTime!='-'?dayjs(shareProportionStartTime,'YYYY-MM'):undefined
			})
		}else if(devied=='确认'){
			setDevied('确认');
			setFdisabled(true);
			setTreeLoading(true)
			
		}
	}
	// 充电放策略//运营年限
	const submits =() =>{
		console.log(charge)
		if(charge=='编辑'){
			setCharge('确认');
			setDisabled(true);
			myForm1.setFieldsValue({
				strategyExpiryDate:strategyExpiryDate&&strategyExpiryDate!='-'?strategyExpiryDate:'',
				strategyStartTime:strategyStartTime&&strategyStartTime!='-'?dayjs(strategyStartTime,'YYYY-MM'):undefined
			})
		}else if(charge=='确认'){
			let fieldsValue = myForm1.getFieldsValue()
			http.post('system_management/energy_model/energy_storage_model/saveStorageEnergyStrategyBaseInfo',{
				"nodeId": props.nodeId,
				"strategyExpiryDate":fieldsValue.strategyExpiryDate, 
				"strategyStartTime":fieldsValue.strategyStartTime?fieldsValue.strategyStartTime.format('YYYY-MM'):'',
				"systemId": props.systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功');
					setTreeLoading(false);
					setStrategyExpiryDate(fieldsValue.strategyExpiryDate);
					setStrategyStartTime(fieldsValue.strategyStartTime?fieldsValue.strategyStartTime.format('YYYY-MM'):'');
					setCharge('编辑')
				}else{
					setTreeLoading(false);
					setCharge('编辑');
					message.warning(res.data.msg)
				}
				props.onQuery(props.systemId)

			}).catch(err =>{
				console.log(err)
			})
		}
		
	}
	
	// 设备
	const deviceSnids =() =>{
		if(deviceSnid=='编辑'){
			setDeviceSnid('确认');
		}else if(deviceSnid=='确认'){
			setDeviceSnid('编辑');
			setTreeLoading(true);
			let fieldsValue =myForm2.getFieldsValue()
			console.log(fieldsValue)
			for(var i=0;i<deviceSnName.length;i++){
				if(fieldsValue.battery_status_device_sn==deviceSnName[i].deviceName){
					setBattery_status_device_sn(deviceSnName[i].deviceName);
					setBattery_status_device_sn1(deviceSnName[i].deviceSn);
				}
				if(fieldsValue.charging_device_sn==deviceSnName[i].deviceName){
					setCharging_device_sn(deviceSnName[i].deviceName);
					setCharging_device_sn1(deviceSnName[i].deviceSn);
				}
				if(fieldsValue.discharging_device_sn==deviceSnName[i].deviceName){
					setDischarging_device_sn(deviceSnName[i].deviceName);
					setDischarging_device_sn1(deviceSnName[i].deviceSn);
				}
			}
			for(var i=0;i<deviceSnName.length;i++){
				if(fieldsValue.battery_status_device_sn==deviceSnName[i].deviceSn){
					setBattery_status_device_sn(deviceSnName[i].deviceName);
					setBattery_status_device_sn1(deviceSnName[i].deviceSn)
					
				}
				if(fieldsValue.charging_device_sn==deviceSnName[i].deviceSn){
					setCharging_device_sn(deviceSnName[i].deviceName);
					setCharging_device_sn1(deviceSnName[i].deviceSn);
					
				}
				if(fieldsValue.discharging_device_sn==deviceSnName[i].deviceSn){
					setDischarging_device_sn(deviceSnName[i].deviceName);
					setDischarging_device_sn1(deviceSnName[i].deviceSn);
				}
			}
		
		}
	}
	useEffect(() =>{
		if(deviceSnid=='编辑'&&treeLoading){
			http.post('system_management/energy_model/energy_storage_model/saveMeteringDeviceOtherSn',{
				"nodeId": props.nodeId,
				"battery_status_device_sn":battery_status_device_sn1,
				"charging_device_sn":charging_device_sn1,
				"discharging_device_sn": discharging_device_sn1,
				"systemId": props.systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功');
					setTreeLoading(false);
					setBattery_status_device_sn(battery_status_device_sn);
					setCharging_device_sn(charging_device_sn);
					setDischarging_device_sn(discharging_device_sn);
				}else{
					setTreeLoading(false);
					message.warning(res.data.msg)
				}
				props.onQuery(props.systemId)
					
			}).catch(err =>{
				console.log(err)
			})
		}else{
			myForm2.setFieldsValue({
				battery_status_device_sn:battery_status_device_sn&&battery_status_device_sn!='-'?battery_status_device_sn:'',
				charging_device_sn:charging_device_sn&&charging_device_sn!='-'?charging_device_sn:'',
				discharging_device_sn:discharging_device_sn&&discharging_device_sn!='-'?discharging_device_sn:''
			})
		}
		
	},[deviceSnid])
	
	// 设备取消
	const onReset2 = () => {
		myForm2.resetFields();
		setDeviceSnid('编辑');
		setBattery_status_device_sn(battery_status_device_sn);
		setCharging_device_sn(charging_device_sn);
		setDischarging_device_sn(discharging_device_sn);
	};
	// c充放电策略取消
	const onReset1 = () => {
		myForm1.resetFields();
		setCharge('编辑');
		setStrategyExpiryDate(strategyExpiryDate);
		setStrategyStartTime(strategyStartTime)
	};
	// 分成比例取消
	const onReset = () => {
		myForm.resetFields();
		setDevied('编辑');
		setShareProportionExpiryDate(shareProportionExpiryDate);
		setShareProportionStartTime(shareProportionStartTime)
	};
	
	const onChange =(date, dateString) => {
		console.log(date, dateString);
		setMonthDay(dateString)
	}
	const chong = (val,e) =>{
		console.log(e)
		setCharging_device_sn(e.props.children);
		setCharging_device_sn1(e.props.children)
		
	}
	const fang = (val,e) =>{
		console.log(e)
		setDischarging_device_sn(e.props.children);
		setDischarging_device_sn1(e.props.children)
	}
	const dian = (val,e) =>{
		console.log(e)
		setBattery_status_device_sn(e.props.children);
		setBattery_status_device_sn1(e.props.children)
	}
	// 编辑同步配置
	const synchronousClick = () =>{
		if(synchronous=='编辑'){
			setSynchronous('确认');
			setFdisabled(false);
			
		}else if(synchronous=='确认'){
			setFdisabled(true);
			setTreeLoading(true);
			// let values = synchronousForm.getFieldsValue();
			// console.log(values)
		}
	}
	useEffect(() =>{
		console.log(synchronous)
		if(synchronous=='确认'&&fdisabled===true){
			let values = synchronousForm.getFieldsValue();
			console.log(values)
			http.post('system_management/energy_model/energy_storage_model/updateStorageEnergySynCfg',{
				"nodeId": props.nodeId,
				"systemId": props.systemId,
				"name":values.names,
				"requestUrl":values.requestUrl,
				"args":values.args,
				"method":'POST'
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功');
					setTreeLoading(false);
					setFdisabled(false)
					setName(values.names);
					setRequestUrl(values.requestUrl);
					setArgs(values.args)
					setSynchronous('编辑');
				}else{
					setTreeLoading(false);
					message.warning(res.data.msg)
					
				}
				props.onQuery(props.systemId)
			
			}).catch(err =>{
				console.log(err)
			})
		}else {
			synchronousForm.setFieldsValue({
				names:name=='-'?"":name,
				args:args=='-'?'':args,
				requestUrl:requestUrl=='-'?'':requestUrl,
			})
			
		}
	},[synchronous,fdisabled])
	const synchronousReset =() =>{
		synchronousForm.resetFields();
		setSynchronous('编辑');
		setNames(names);
		setArgs(args);
		setRequestUrl(requestUrl)
	}
	// 下发策略
	const DistributeClick =() =>{
		if(distribute=='编辑'){
			setDistribute('确认');
			setFdisabled(false);
			
		}else if(distribute=='确认'){
			setFdisabled(true);
			setTreeLoading(true);
			
		}
	}
	useEffect(() =>{
		if(distribute=='确认'&&fdisabled===true){
			let values = DistributeForm.getFieldsValue();
			console.log(values)
			http.post('system_management/energy_model/energy_storage_model/updateStorageEnergyDistributeCfg',{
				"nodeId": props.nodeId,
				"systemId": props.systemId,
				"name":values.names,
				"requestUrl":values.requestUrl,
				"args":values.args,
				"method":'POST'
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功');
					setTreeLoading(false);
					setName1(values.names);
					setRequestUrl1(values.requestUrl);
					setArgs1(values.args);
					setDistribute('编辑');
				}else{
					setTreeLoading(false);
					message.warning(res.data.msg);
				}
				props.onQuery(props.systemId)
			
			}).catch(err =>{
				console.log(err)
			})
		}else{
			DistributeForm.setFieldsValue({
				names:name1=='-'?"":name1,
				args:args1=='-'?'':args1,
				requestUrl:requestUrl1=='-'?'':requestUrl1,
			})
		}
	},[distribute,fdisabled])
	const DistributeReset =() =>{
		DistributeForm.resetFields();
		setDistribute('编辑');
		setNames1(names1);
		setArgs1(args);
		setRequestUrl1(requestUrl1)
	}
	const layout = {
	  labelCol: { span: 10 },
	  wrapperCol: { span: 16 },
	};
	return(
		<div className="separate">
		<Spin spinning={treeLoading}  size="middle">
		 
			<ConfigProvider locale={locale}>

				
				<div className="separatepolicy">
					<div className="serve">
						<div className="title">电站规模
							<Button size="small" ghost style={{display:isShow==true?'inline-block':'none'}} onClick={qqxiao}>取消</Button>
							<Button size="small" type="primary"  onClick={check}>{isShow==false?'编辑':'确认'}</Button>
						</div>
						<div className="cont">
							{
								isShow?(
									<Form
										// onFinish={onFinish}
										// {...layout}
										name="basicsEnergyStorage"
										form={myForms}
										initialValues={{ remember: true,
											storageEnergyLoad:storageEnergyLoad,
											storageEnergyCapacity:storageEnergyCapacity,
											minDischargePercent:minDischargePercent,
											maxChargePercent:maxChargePercent
										}}  
										labelCol={{ span: 10 }}
										    wrapperCol={{ span: 14 }}
									>
										
										<Form.Item 
											label="储能电站功率" 
											name="storageEnergyLoad"		
											rules={ [{required: true, pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}
										>
											<div style={{display:'flex'}}>
												<Input maxLength={50}  defaultValue={storageEnergyLoad === '-' ? '' : storageEnergyLoad}/>
												<span className="storageEnergyLoad-units">kW</span>
											</div>
										</Form.Item>
										<Form.Item
											label="储能电池容量"
											name="storageEnergyCapacity"		
											rules={ [{required: true, pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}					
										>
											<div style={{display:'flex'}}>
												<Input maxLength={50}  defaultValue={storageEnergyCapacity === '-' ? '' : storageEnergyCapacity}/>
												<span className="storageEnergyCapacity-units">kWh</span>
											</div>
										</Form.Item>
										<Form.Item
											label="最大可充电量百分比" 
											name="maxChargePercent"		
											rules={ [{required: true, pattern: new RegExp(/^([0-9]{1,2}(\.\d{1,2})?|100(\.00?)?)$/),message: '请输入正确范围内的数字' }]}
										>
											<div style={{display:'flex'}}>
												<Input maxLength={50} defaultValue={maxChargePercent === '-' ? '' : maxChargePercent}/>
												<span className="storageEnergyLoad-units">%</span>
											</div>
										</Form.Item>
										<Form.Item
											label="最小放电百分比" 
											name="minDischargePercent"		
											rules={ [{required: true, pattern: new RegExp(/^([0-9]{1,2}(\.\d{1,2})?|100(\.00?)?)$/),message: '请输入正确范围内的数字' }]}
										>
											<div style={{display:'flex'}}>
												<Input maxLength={50} defaultValue={minDischargePercent === '-' ? '' : minDischargePercent}/>
												<span className="storageEnergyLoad-units">%</span>
											</div>
										</Form.Item>
									</Form>
								):
							   (<div className="serverst">
									<div className="axxset">储能电站功率：{storageEnergyLoad}kW</div>
									<div className="axxset">储能电池容量：{ storageEnergyCapacity}kWh</div>
									<div className="axxset">最大可充电量百分比：{ maxChargePercent}%</div>
									<div className="axxset">最小放电百分比：{ minDischargePercent}%</div>
								</div>)
							}
						</div>
					
						
						
					</div>
					<div className="serve" style={{marginLeft:24}}>
						<div className="title">运营年限 
						<Button ghost style={{display:charge=='确认'?'inline-block':'none'}}
						onClick={onReset1}>
							取消
						</Button>
							<Button type="primary" onClick={submits} >{charge}</Button>
							
						</div>
						<div className="cont">
							{
								charge=='编辑'?(
									<div className="manoeuvre">
										<p>运营年限： {strategyExpiryDate} 年</p>
										<p>开始日期：{strategyStartTime}</p>
									</div>
								):(
									<Form
										// onFinish={onFinish1}
										name="basicsEnergyStorage"
										form={myForm1}
										initialValues={{ remember: true }}  
										labelCol={{ span: 6 }}
										    // wrapperCol={{ span: 12 }}
									>	
										<Form.Item 
												label="运营年限" 
												name="strategyExpiryDate"		
												rules={ [{required: true,pattern: new RegExp(/^[1-2][0-9]$|^[3][0]$|^[1-9]$/),message: '请输入正确范围内的数字' }]}>
											<div style={{display:'flex'}}>
												<Input maxLength={50} style={{width:138}} disabled={charge=='编辑'?true:false} defaultValue={strategyExpiryDate === '-' ? '' : strategyExpiryDate} />
												<span className="units">年</span>
											</div>
										</Form.Item>
										
										<Form.Item
											label="开始日期"
											name="strategyStartTime"	
										>
											<DatePicker style={{width:168}} disabled={charge=='编辑'?true:false}
												onChange={onChange} picker="month" />
										</Form.Item>
										
									</Form>
								)
							}
							
						</div>
						
					</div>
					<div className="serve" style={{marginLeft:24}}>
						<div className="title">分成年限
						<Button
						style={{display:devied=='确认'?'inline-block':'none'}}
						onClick={onReset} ghost>
							取消
						</Button>
							<Button type="primary" onClick={devieds} >{devied}</Button>
							
						</div>
						<div className="cont">
							{
								devied=='编辑'?(
									<div className="manoeuvre">
										<p>分成年限： {shareProportionExpiryDate} 年</p>
										<p>开始日期：{shareProportionStartTime}</p>
									</div>
								):(
									<Form
										// onFinish={onFinish}
										name="basicsEnergyStorage"
										form={myForm}
										initialValues={{ remember: true }}  
									>
										
										<Form.Item label="分成年限" 
												name="shareProportionExpiryDate"		
												rules={ [{pattern: new RegExp(/^[1-2][0-9]$|^[3][0]$|^[1-9]$/),message: '请输入正确范围内的数字' }]}>
											<div style={{display:'flex'}}>
												<Input maxLength={50} style={{width:138}} disabled={devied=='编辑'?true:false} defaultValue={shareProportionExpiryDate === '-' ? '' : shareProportionExpiryDate} />
												<span className="units">年</span>
											</div>
										</Form.Item>
										<Form.Item
											label="开始日期"
											name="shareProportionStartTime"		
										>
											<DatePicker style={{width:168}} disabled={devied=='编辑'?true:false}
												onChange={onChange} picker="month" />
										</Form.Item>
										
									</Form>
								)
							}
						</div>
						
						
						
					</div>
				</div>
				<div className="separatepolicy">
					<div className="serve" style={{marginTop:24}}>
						<div className="title">设备
						<Button style={{marginLeft:'15px',display:deviceSnid=='确认'?'inline-block':'none'}}
						onClick={onReset2} ghost>
							取消
						</Button>
							<Button type="primary" onClick={deviceSnids}>{deviceSnid}</Button>
							
						</div>
						<div className="cont">
							{
								deviceSnid=='编辑'?(
									<div className="manoeuvre">
										<p>充电设备：{props.charging_device_sn || '-'}</p>
										<p>放电设备：{props.discharging_device_sn || '-'}</p>
										<p>电池监控：{props.battery_status_device_sn || '-'}</p>
									</div>
								):(
									<Form
										// onFinish={onFinish2}
										name="basicsEnergyStorage"
										form={myForm2}
										initialValues={{ remember: true }}  
									>
										<Form.Item
											label="充电设备"
											name="charging_device_sn"							
										>
											<Select 
												key={1}
												onChange={chong}
												disabled={deviceSnid=='编辑'?true:false}
												placeholder="请选择设备" style={{ width: 180 }} >
												{
													props.deviceSnName.length&&props.deviceSnName.map(item =>{
														return <Option value={item.deviceSn} key={'c'+item.deviceSn}>{item.deviceName}</Option>
													})
												}
											</Select>
										</Form.Item>
										<Form.Item
											label="放电设备"
											name="discharging_device_sn"	
											onChange={fang}						
										>
											<Select  placeholder="请选择设备" 
												disabled={deviceSnid=='编辑'?true:false}
												key={2}
												style={{ width: 180 }} >
													{
														props.deviceSnName.length&&props.deviceSnName.map(item =>{
															return <Option value={item.deviceSn} key={'b'+item.deviceSn}>{item.deviceName}</Option>
														})
													}
												</Select>
										</Form.Item>
										<Form.Item
											label="电池监控"
											name="battery_status_device_sn"		
											onChange={dian}	
										>
											<Select  placeholder="请选择设备" 
												disabled={deviceSnid=='编辑'?true:false}
												key={3}
												style={{ width: 180 }} >
												{
													props.deviceSnName.length&&props.deviceSnName.map(item =>{
														return <Option value={item.deviceSn} key={'a'+item.deviceSn}>{item.deviceName}</Option>
													})
												}
											</Select>
										</Form.Item>
										
									</Form>
								)
							}
						</div>
						
						
					</div>
					<div className="serve" style={{marginLeft:20,marginTop:20}}>
						<div className="title">储能系统同步配置
						<Button ghost style={{marginLeft:'15px',display:synchronous=='确认'?'inline-block':'none'}} onClick={synchronousReset}>
							取消
						</Button>
						<Button type="primary" onClick={synchronousClick}>{synchronous}</Button>
							
						</div>
						<div className="cont">
							{
								synchronous=='编辑'?(
									<div className="manoeuvre">
										<p>名称：{props.name || '-'}</p>
										<p>地址：{props.requestUrl || '-'}</p>
										<p>参数：{props.args || '-'}</p>
									</div>
								):(
									<Form
										// onFinish={onFinish2}
										name="basicsEnergyStorage"
										form={synchronousForm}
										initialValues={{ remember: true,names:'',requestUrl:'',args:'' }}  
										autocomplete="off"
									>
										<Form.Item
											label="名称"
											name="names"
											autoComplete={false}								
										>
											
											<div><Input id='synchronousForm-2' maxLength={50} style={{width:138}} defaultValue={props.name}  /></div>
										</Form.Item>
										<Form.Item
											label="地址"
											name="requestUrl"	
											autoComplete={false}	

											// onChange={fang}						
										>
											<div><Input id='synchronousForm-2' maxLength={50} style={{width:138}} defaultValue={props.requestUrl}  /></div>
										</Form.Item>
										<Form.Item
											label="参数"
											name="args"	
											autoComplete={false}	
											// onChange={dian}	
										>
											<div><Input id='synchronousForm-3' maxLength={50} style={{width:138}} defaultValue={props.args}   /></div>
										</Form.Item>
										
									</Form>
								)
							}
						</div>
					</div>
					<div className="serve" style={{marginLeft:20,marginTop:20}}>
						<div className="title">储能系统下发策略
						<Button ghost style={{marginLeft:'15px',display:distribute=='确认'?'inline-block':'none'}} onClick={DistributeReset}>
							取消
						</Button>
						<Button type="primary" onClick={DistributeClick}>{distribute}</Button>
							
						</div>
						<div className="cont">
							{
								distribute=='编辑'?(
									<div className="manoeuvre">
										<p>名称：{props.name1 || '-'}</p>
										<p>地址：{props.requestUrl1 || '-'}</p>
										<p>参数：{props.args1 || '-'}</p>
									</div>
								):(
									<Form
										// onFinish={onFinish2}
										name="basicsEnergyStorage"
										form={DistributeForm}
										initialValues={{ remember: true,name:'',requestUrl:'',args:'' }}  
										autocomplete="off"
									>
										<Form.Item
											label="名称"
											name="names"							
										>
											<Input id='DistributeForm-1' maxLength={50} style={{width:138}} defaultValue={props.name1}  />
										</Form.Item>
										<Form.Item
											label="地址"
											name="requestUrl"	
											// onChange={fang}						
										>
											<Input id='DistributeForm-2' maxLength={50} style={{width:138}}  defaultValue={props.requestUrl1} />
										</Form.Item>
										<Form.Item
											label="参数"
											name="args"		
											// onChange={dian}	
										>
											<Input id='DistributeForm-3' maxLength={50} style={{width:138}}  defaultValue={props.args1} />
										</Form.Item>
										
									</Form>
								)
							}
						</div>
					</div>
				</div>
				
			</ConfigProvider>	
		</Spin>
		</div>
	)
}

export default Separate