import React, { useEffect,useState } from 'react';
import { Button, Checkbox, Form, Input,DatePicker ,Col,Row,ConfigProvider,message,InputNumber ,
Table,Spin 
} from 'antd';

import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
// import locale from 'antd/lib/locale/zh_CN';


import './index.css'
import './index.scss'
import http from '../../../../../server/server.js'
const { RangePicker,MonthPicker } = DatePicker;
const monthFormat = 'YYYY-MM';

const tailLayout = {
  wrapperCol: { offset: 8, span: 16 },
};
const Separate=(props) => {
	const [dataSource, setDataSource] = useState([]);
	const [power, setPower] = useState(props.storageEnergyLoad);
	const [volume, setVolume] = useState('');
	const [isShow, setIsShow] = useState(false);
	const [val, setVal] = useState('编辑');
	const [username, setUsername] = useState('');
	const [nodeNameList, setNodeNameList] = useState([]);
	const [nodeSystemList, setNodeSystemList] = useState([]);
	const [systemName, setSystemName] = useState('请选择1');
	const [nodeId, setNodeId] = useState('');
	const [systemId, setSystemId] = useState('');
	const [yearlimit, setYearlimit] = useState('');
	const [startyear, setStartyear] = useState('2022-02');
	const [storageEnergyLoad, setStorageEnergyLoad] = useState('');
	const [storageEnergyCapacity, setStorageEnergyCapacity] = useState('-'); //储能电池容量
	const [shareProportionExpiryDate, setShareProportionExpiryDate] = useState(''); //分层比例年限
	const [shareProportionStartTime, setShareProportionStartTime] = useState(''); //分层比例开始时间
	const [monthday, setMonthday] = useState('');
	const [page, setPage] = useState(1);
	const [total, setTotal] = useState('');
	const [devied, setDevied] = useState('编辑');
	const [charge, setCharge] = useState('编辑');
	const [photovoltaicInstalledCapacity, setPhotovoltaicInstalledCapacity] = useState('-');
	const [timeDivisionExpiryDate, setTimeDivisionExpiryDate] = useState('-'); //分时电价年限
	const [timeDivisionStartTime, setTimeDivisionStartTime] = useState('-'); //分时电价开始时间
	const [powerUserExpiryDate, setPowerUserExpiryDate] = useState('-'); //电力用户购电折扣比例 年限
	const [powerUserStartTime, setPowerUserStartTime] = useState('-'); //电力用户购电折扣比例 开始时间
	const [treeLoading, setTreeLoading] = useState(false);
	const [fenshi, setFenshi] = useState('');
	const [myForms] = Form.useForm();
	const [myForm] = Form.useForm();
	const [myForm12] = Form.useForm();
	useEffect(() => {
	    // 当props变化时执行的操作
	    // console.log('Props have been updated');
		// console.log(props)
	    // 更新组件内部状态
		if(props.istrue ==true){
			setNodeId(props.nodeId)
			setSystemId(props.systemId)
			setPhotovoltaicInstalledCapacity(props.photovoltaicInstalledCapacity?props.photovoltaicInstalledCapacity:'-');
			setPowerUserExpiryDate(props.powerUserExpiryDate?props.powerUserExpiryDate:'-');
			setPowerUserStartTime(props.powerUserStartTime?props.powerUserStartTime:'-');
			setTimeDivisionExpiryDate(props.timeDivisionExpiryDate?props.timeDivisionExpiryDate:'-');
			setTimeDivisionStartTime(props.timeDivisionStartTime?props.timeDivisionStartTime:'-')
			props.changeData()
		}else{
			return false
		}
	}, [props]); // 只有在props发生变化时才会触发effect

	
	// 获取父组件的值
	// componentWillReceiveProps(nextProp){
	// 	// console.log(nextProp)
	// 	if(this.props.istrue ==true){
	// 		// 电力用户购电折扣比例			
	// 		this.setState({
	// 			nodeId:nextProp.nodeId,
	// 			systemId:nextProp.systemId,
	// 			photovoltaicInstalledCapacity:nextProp.photovoltaicInstalledCapacity?nextProp.photovoltaicInstalledCapacity:'-',
	// 			powerUserExpiryDate: nextProp.powerUserExpiryDate?nextProp.powerUserExpiryDate:'-',
	// 			powerUserStartTime:nextProp.powerUserStartTime?nextProp.powerUserStartTime:'-',
	// 			timeDivisionExpiryDate: nextProp.timeDivisionExpiryDate?nextProp.timeDivisionExpiryDate:'-',
	// 			timeDivisionStartTime:nextProp.timeDivisionStartTime?nextProp.timeDivisionStartTime:'-'
				    
				
	// 		},() =>{
				
	// 			this.props.changeData()
	// 		})
			
	// 	}else{
	// 		return false
	// 	}
		
	// }
	// 额定装机容量
	const check=()=>{
		setIsShow(!isShow)
		
		if(isShow ==true){
			// alert(0)
			let values = myForms.getFieldsValue()
			console.log(values)
			
			setTreeLoading(true)
			http.post('system_management/energy_model/photovoltaic_model/savePhotovoltaicBaseInfo',{
				"nodeId": nodeId,
				"photovoltaicInstalledCapacity":values.photovoltaicInstalledCapacity,
				"systemId": systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功')
					setPhotovoltaicInstalledCapacity(values.photovoltaicInstalledCapacity);
					setTreeLoading(false);
					setIsShow(false)
				}else{
					message.warning(res.data.msg)
					setTreeLoading(false);
				}
				
			}).catch(err =>{
				console.log(err)
			})
		}else if(isShow ==false){
			setIsShow(true)
			if(photovoltaicInstalledCapacity&&photovoltaicInstalledCapacity!='-'){
				myForms.setFieldsValue({
					photovoltaicInstalledCapacity:photovoltaicInstalledCapacity,	//电站功率
					
				})
			}
		}
		
	}
	// 监听储能电站功率
	const inputChange=(e) =>{
		console.log(e.target.value)
		setPower(e.target.value)
	}
	// 取消
	const qqxiao =() =>{
		setIsShow(!isShow);
		setPhotovoltaicInstalledCapacity(photovoltaicInstalledCapacity)
	}
		// 电力用户购电折扣比例
	const onFinish = (fieldsValue: any) => {
		console.log(devied)				
		if(devied=='确认'){
			setTreeLoading(true)
			let fieldsValue = myForm.getFieldsValue()
			console.log(myForm.getFieldsValue())
			http.post('system_management/energy_model/photovoltaic_model/savePhotovoltaicBasePowerUserInfo',{
				"nodeId": nodeId,
				"powerUserExpiryDate":fieldsValue.powerUserExpiryDate, 
				"powerUserStartTime":fieldsValue.powerUserStartTime?fieldsValue.powerUserStartTime.format('YYYY-MM'):'',
				"systemId": systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功')
					
					setTreeLoading(false);
					setPowerUserExpiryDate(fieldsValue.powerUserExpiryDate);
					setPowerUserStartTime(fieldsValue.powerUserStartTime?fieldsValue.powerUserStartTime.format('YYYY-MM'):undefined);
					setDevied('编辑')
				}else{
					setTreeLoading(false);
					message.warning(res.data.msg)
					
				}
			}).catch(err =>{
				console.log(err)
			})
		}else{
			console.log(powerUserExpiryDate)
			setDevied('确认');
			myForm.setFieldsValue({
				powerUserExpiryDate: powerUserExpiryDate&&powerUserExpiryDate!='-'?powerUserExpiryDate:'',
				powerUserStartTime:powerUserStartTime&&powerUserStartTime!='-'?dayjs(powerUserStartTime,'YYYY-MM'):undefined
				
			})
			
		}
		
	};
	// 运营年限编辑
	const submits =() =>{
		if(charge=='编辑'){
			setCharge('确认')
			let fieldsValue = myForm12.getFieldsValue()
			console.log(fieldsValue)
			// console.log(this.state.timeDivisionExpiryDate)
			// console.log(this.state.timeDivisionStartTime)
			myForm12.setFieldsValue({
				timeDivisionExpiryDate: timeDivisionExpiryDate&&timeDivisionExpiryDate!='-'?timeDivisionExpiryDate:'',
				timeDivisionStartTime:timeDivisionStartTime&&timeDivisionStartTime!='-'?dayjs(timeDivisionStartTime,'YYYY-MM'):undefined
				   
			})
		}else if(charge=='确认'){
			
			let fieldsValue = myForm12.getFieldsValue()
			console.log(fieldsValue)
			
			http.post('system_management/energy_model/photovoltaic_model/savePhotovoltaicBaseTimeDivisionInfo',{
				"nodeId": nodeId,
				"timeDivisionExpiryDate":fieldsValue.timeDivisionExpiryDate,
				"timeDivisionStartTime":fieldsValue.timeDivisionStartTime?fieldsValue.timeDivisionStartTime.format('YYYY-MM'):undefined,
				"systemId": systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功')
					setTreeLoading(false);
					setTimeDivisionStartTime(fieldsValue.timeDivisionStartTime?fieldsValue.timeDivisionStartTime.format('YYYY-MM'):undefined);
					setTimeDivisionExpiryDate(fieldsValue.timeDivisionExpiryDate);
					setCharge('编辑')
				}else{
					message.warning(res.data.msg)
					setTreeLoading(false);
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
	}
	// 折扣比例取消
	const onReset = () => {
		myForm.resetFields()
		setDevied('编辑');
		setPowerUserExpiryDate(powerUserExpiryDate);
		setPowerUserStartTime(powerUserStartTime);
		
		
	};
	// 分时电价取消
	const onReset1 = () => {
		myForm12.resetFields()
		setCharge('编辑');
		setTimeDivisionExpiryDate(timeDivisionExpiryDate);
		setTimeDivisionStartTime(timeDivisionStartTime)
	};
	// 分时电价日期
	const onChange =(date, dateString) => {
		console.log(date, dateString);
		setMonthday(dateString)
	}
	// console.log(strategyStartTime)
	return(
		<div className="allcontents Photovoltaic_model_separate">
		<Spin spinning={treeLoading}  size="middle">
			<ConfigProvider locale={locale}>
				<div className="separatepolicy">
					<div className="serve">
					<div className="title">电站规模
					<Button style={{display:isShow==true?'inline-block':'none'}}
					 onClick={qqxiao} ghost>取消</Button>
						<Button type="primary"  onClick={check}>{isShow==false?'编辑':'确认'}</Button>
						
					</div>
						<div className="cont">
							{
								isShow?(
									<Form
										// onFinish={onFinish}
										name="basics1"
										form={myForms}
										initialValues={{ remember: true }}  
									>
										<Form.Item label="光伏电站额定装机容量"
											style={{width:'100%',marginTop:20}}
										>
											<Form.Item
												name="photovoltaicInstalledCapacity"		
												style={{width:144,marginTop:0}}
												rules={ [{required: true, pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}
											>
												<Input style={{width:142}} />
											</Form.Item>
											<span className="units">kW</span>
										</Form.Item>
										
										
										
									</Form>
								):
							   (<div className="serverst"><div className="axxset">光伏电站额定装机容量：{photovoltaicInstalledCapacity}kW</div>
								</div>)
							}
						</div>
						
					</div>
					<div className="serve" style={{margin:'0px 15px 0px 15px'}}>
						<div className="title">运营年限
							
							<Button  ghost
							style={{marginLeft:'15px',display:charge=='确认'?'inline-block':'none'}} onClick={onReset1}>
								取消
							</Button>
							<Button type="primary" onClick={submits}>{charge}</Button>
							
						</div>
						<div className="cont">
						{
							charge=='编辑'?(
								<div className="manoeuvre">
									<p>运营年限： {timeDivisionExpiryDate} 年</p>
									<p>开始日期：{timeDivisionStartTime}</p>
								</div>
							):(
								<Form
									// onFinish={onFinish1}
									name="basics1"
									form={myForm12}
									initialValues={{ remember: true }}  
								>	
									<Form.Item label="运营年限">
										<Form.Item
											style={{width:140,marginTop:0}}
											name="timeDivisionExpiryDate"	
											rules={ [{pattern: new RegExp(/^[1-2][0-9]$|^[3][0]$|^[1-9]$/),message: '请输入正确范围内的数字' }]}							
										>
											<Input style={{width:138}} disabled={charge=='编辑'?true:false} />
										</Form.Item>
										<span className="units">年</span>
									</Form.Item>
									
									<Form.Item
										label="开始日期"
										name="timeDivisionStartTime"	
																
									>
										<DatePicker style={{width:168}} disabled={charge=='编辑'?true:false}
											onChange={onChange} picker="month" />
									</Form.Item>
									
								</Form>
							)
						}
							
						</div>
						
					</div>
					<div className="serve">
						<div className="title">
						电力用户折扣年限
						<Button ghost
						style={{marginLeft:'15px',display:devied=='确认'?'inline-block':'none'}}
						onClick={onReset}>
							取消
						</Button>
							<Button type="primary" onClick={onFinish}>
								{devied}
							</Button>
							
						</div>
						<div className="cont">
							{
								devied=='编辑'?(
									<div className="manoeuvre">
										<p>折扣年限： {powerUserExpiryDate} 年</p>
										<p>开始日期：{powerUserStartTime}</p>
									</div>
								):(
									<Form
										// onFinish={onFinish}
										name="basics1"
										form={myForm}
										initialValues={{ remember: true }}  
									>
										<Form.Item label="折扣年限"
											style={{width:'100%',marginTop:20}}
										>
											<Form.Item
												name="powerUserExpiryDate"		
												style={{width:144,marginTop:0}}
												rules={ [{pattern: new RegExp(/^[1-2][0-9]$|^[3][0]$|^[1-9]$/),message: '请输入正确范围内的数字' }]}	
											>
												<Input disabled={devied=='编辑'?true:false} style={{width:142}} />
											</Form.Item>
											<span className="units">年</span>
										</Form.Item>
										
										<Form.Item
											label="开始日期"
											name="powerUserStartTime"							
										>
											<DatePicker style={{width:172}} disabled={devied=='编辑'?true:false}
												onChange={onChange} picker="month" />
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