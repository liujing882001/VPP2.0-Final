import React, { useEffect,useState } from 'react';
import { Tabs,Select ,Input,Button,Table ,message,Cascader} from 'antd';
import http from '../../../../server/server.js'
import './index.css'
import Separate from './separate/index.js'	//基本信息
// import Telegram from './telegram/index.js'	//充放电策略
import Electrovalence from './electrovalence/index.js'

import Divide from './divide/index.js'	//分成比例
import Ai from './ai/index.js'	//ai调度
const { TabPane } = Tabs;
const { Option } = Select;
const Accumulation_model =() =>{
	const [dataSource, setDataSource] = useState([]);
	const [power, setPower] = useState(12);
	const [volume, setVolume] = useState(12);
	const [isShow, setIsShow] = useState(false);
	const [val, setVal] = useState('编辑');
	const [username, setUsername] = useState('');
	const [nodeNameList, setNodeNameList] = useState([]);
	const [nodeSystemList, setNodeSystemList] = useState([]);
	const [systemName, setSystemName] = useState('请选择');
	const [istrue, setIstrue] = useState(false);
	const [istrue1, setIstrue1] = useState(false);
	const [nodeId, setNodeId] = useState(''); //节点id
	const [systemId, setSystemId] = useState(''); //系统id
	const [storageEnergyCapacity, setStorageEnergyCapacity] = useState(''); //储能电池容量
	const [storageEnergyLoad, setStorageEnergyLoad] = useState(''); //储能电站功率 kw
	const [strategyExpiryDate, setStrategyExpiryDate] = useState(''); //充放电策略年限
	const [strategyStartTime, setStrategyStartTime] = useState(''); //充放电策略开始时间
	const [shareProportionExpiryDate, setShareProportionExpiryDate] = useState(''); //分成比例年限
	const [shareProportionStartTime, setShareProportionStartTime] = useState(''); //开始时间
	const [pvNodeNameList, setPvNodeNameList] = useState([]); //节点
	const [deviceSnName, setDeviceSnName] = useState([]);
	const [battery_status_device_sn, setBattery_status_device_sn] = useState(''); //电池
	const [charging_device_sn, setCharging_device_sn] = useState(''); //充
	const [discharging_device_sn, setDischarging_device_sn] = useState(''); //放
	const [battey, setBattey] = useState([]); //电池
	const [loading, setLoading] = useState(false);
	const [sysloading, setSysLoading] = useState(false);
	const [options, setOptions] = useState([]);
	const [currentloading, setCurrentLoading] = useState(false);
	const [name, setName] = useState('');
	const [args, setArgs] = useState(''); //参数
	const [requestUrl, setRequestUrl] = useState('');
	const [name1, setName1] = useState('');
	const [args1, setArgs1] = useState(''); //参数
	const [requestUrl1, setRequestUrl1] = useState('');
	const [nodeIdVal, setNodeIdVal] = useState('');
	const [nodeSystemVal, setNodeSystemVal] = useState('');
	const [maxChargePercent,setMaxChargePercent] = useState('');
	const [minDischargePercent,setMinDischargePercent] = useState('');
	const [isFirst, setIsFirst] = useState(false);


	useEffect(() =>{
		getpvNodeNameList()
		storageEnergyNodeTree()
	},[])
	useEffect(() =>{
		if(nodeId){
			systemStorageEnergyList();
		}
	},[nodeId])
	useEffect(() =>{
		if(systemId!==''&&systemName!==''){
			onQuery(systemId)
		}
	},[systemId,systemName])
	const ChildrenChange=(val)=>{
		setIstrue(false)
	}
	// 获取节点
	const storageEnergyNodeTree=()=>{
		setCurrentLoading(true)
		http.post('tree/storageEnergyNodeTree').then(res =>{
			if(res.data.code==200){
				let data = res.data.data
				let count = 0;
				const main = function(data) {
				    for (let i in data) {
				        data[i].label = data[i].title;
						data[i].value = data[i].id;
						
				        if (data[i].children) {
				            count++;
				            main(data[i].children);
				        }
				    }
				}
				main(data);
				console.log(data);
				let array1 = []
				function traverseFirstElement(arr) {
					const firstElement = arr[0];
					console.log(firstElement);
					if(firstElement){
						array1.push(firstElement.key)
					}
					for (const key in firstElement) {
						if (Array.isArray(firstElement[key])) {
							// console.log(firstElement[key])
							traverseFirstElement(firstElement[key]);
						}
					}
				}
				if(data.length>0){
					traverseFirstElement(data);
				}
				let length = array1.length
				console.log(array1)
				setOptions(data);
				setCurrentLoading(false);
				setNodeIdVal(array1);
				setIsFirst(true);
				setNodeId(length>0&&array1[length-1]);
				// systemStorageEnergyList()
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 获取节点
	const getpvNodeNameList=()=>{
		setLoading(true)
		http.post('system_management/node_model/storageEnergyNodeNameList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setPvNodeNameList(res.data.data);
				setLoading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 获取系统
	const systemStorageEnergyList=()=>{
		http.post('system_management/system_model/systemStorageEnergyList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setNodeSystemList(res.data.data);
				setSysLoading(false)
				
			}else{
				message.info(res.data.msg)
			}
		})
	}
	// 选择tab
	const onChange = (key) => {
		console.log(key)
		setIsFirst(false)
		if(key ==1){
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyBaseInfo?nodeId='+nodeId +'&systemId='+systemId).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					if(res.data.data){
						setStorageEnergyCapacity(res.data.data.storageEnergyCapacity);
						setStorageEnergyLoad(res.data.data.storageEnergyLoad);
						setMinDischargePercent(res.data.data.minDischargePercent)
						setMaxChargePercent(res.data.data.maxChargePercent)
						setIstrue(true);
					}else{
						setStorageEnergyCapacity('');
						setStorageEnergyLoad('');
						setMinDischargePercent('')
						setMaxChargePercent('')
						setIstrue(true);
					}
					
				}else{
					message.info(res.data.msg)
					setStorageEnergyCapacity('');
					setStorageEnergyLoad('');
					setMinDischargePercent('')
					setMaxChargePercent('')
					setIstrue(false);
					
					return false
				}
			})
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyStrategyBaseInfo?nodeId='+nodeId +'&systemId='+systemId).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					
					if(res.data.data){
						setStrategyExpiryDate(res.data.data.strategyExpiryDate);
						setStrategyStartTime(res.data.data.strategyStartTime);
						setIstrue(true);
					}else{
						setStrategyExpiryDate('');
						setStrategyStartTime('');
						setIstrue(true);
					}
					
				}else{
					message.info(res.data.msg)
					setStrategyExpiryDate('');
					setStrategyStartTime('');
					setIstrue(false);
					return false
				}
			})
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyStrategyBaseInfo?nodeId='+nodeId +'&systemId='+systemId).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					if(res.data.data){
						setStrategyExpiryDate(res.data.data.strategyExpiryDate);
						setStrategyStartTime(res.data.data.strategyStartTime);
						setIstrue(true)
					}else{
						setStrategyExpiryDate('');
						setStrategyStartTime('');
						setIstrue(true)
					}
					
				}else{
					message.info(res.data.msg)
					setStrategyExpiryDate('');
					setStrategyStartTime('');
					setIstrue(false)
					return false
				}
			})
			// 充放电列表
			http.post('system_management/energy_model/energy_storage_model/getAllMeteringDeviceList?nodeId='+nodeId+'&systemId='+systemId ).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let device = res.data.data
					setDeviceSnName(res.data.data)
					http.post('system_management/energy_model/energy_storage_model/getAllMeteringDeviceOtherList?nodeId='+nodeId+'&systemId='+systemId ).then(res =>{
						console.log(res)
						if(res.data.code==200){
							let data = res.data.data
							if(data){
								for(var i=0;i<device.length;i++){
									if(device[i].deviceSn == data.battery_status_device_sn){
										setBattery_status_device_sn(device[i].deviceName);
										setIstrue(true)
									}
									if(device[i].deviceSn == data.charging_device_sn){
										setCharging_device_sn(device[i].deviceName);
										setIstrue(true)
									}
									if(device[i].deviceSn == data.discharging_device_sn){
										setDischarging_device_sn(device[i].deviceName)
										setIstrue(true)
									}
								}
								for(var i=0;i<device.length;i++){
									if(device[i].deviceName == data.battery_status_device_sn){
										setBattery_status_device_sn(device[i].deviceName);
										setIstrue(true)
									}
									if(device[i].deviceName == data.charging_device_sn){
										setCharging_device_sn(device[i].deviceName);
										setIstrue(true)
									}
									if(device[i].deviceName == data.discharging_device_sn){
										setDischarging_device_sn(device[i].deviceName);
										setIstrue(true);
									}
								}
								
							}else{
								message.info(res.data.msg);
								setBattery_status_device_sn('');
								setCharging_device_sn('');
								setDischarging_device_sn('');
								setIstrue(false)
								return false
							}
						}
							
							
							
					}).catch(err =>{
						console.log(err)
					})
				}
			}).then(err =>{
				console.log(err)
			})
		 }else if(key ==2){
			
		 }else if(key ==3){
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyShareProportionBaseInfo?nodeId='+nodeId +'&systemId='+systemId).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					
					if(res.data.data){
						setShareProportionExpiryDate(res.data.data.shareProportionExpiryDate);
						setShareProportionStartTime(res.data.data.shareProportionStartTime);
						setIstrue(true)
					}else{
						setShareProportionExpiryDate('');
						setShareProportionStartTime('');
						setIstrue(true)
					}
					
				}else{
					message.info(res.data.msg)
					setShareProportionExpiryDate('');
					setShareProportionStartTime('');
					setIstrue(false);
					return false
				}
			})
		 }
	  
	};
	// 选择节点
	const onChange1 =(e) =>{
		console.log(e)
		setNodeId(e[3]);
		setSystemName('');
		setSystemId('')
	}
	const sysChange =(e) =>{
		setSystemName(e);
		setSystemId(e);
	}
	useEffect(() =>{
		console.log(istrue)
	},[istrue])

	const onQuery = (e) => {
		// 查询储能基本信息
		http.post('system_management/energy_model/energy_storage_model/findStorageEnergyBaseInfo?nodeId='+nodeId +'&systemId='+e).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				if(res.data.data){
					setStorageEnergyCapacity(res.data.data.storageEnergyCapacity);
					setStorageEnergyLoad(res.data.data.storageEnergyLoad);
					setMinDischargePercent(res.data.data.minDischargePercent)
					setMaxChargePercent(res.data.data.maxChargePercent)
					setIstrue(true)
				}else{
					setStorageEnergyCapacity('');
					setStorageEnergyLoad('');
					setMinDischargePercent('')
					setMaxChargePercent('')
					setIstrue(true)
				}
				
			}else{
				message.info(res.data.msg)
				setStorageEnergyCapacity('');
				setStorageEnergyLoad('');
				setMinDischargePercent('')
				setMaxChargePercent('')
				setIstrue(false);
				return false
			}
		})
		// 查询储能基本信息-充放电策略
		http.post('system_management/energy_model/energy_storage_model/findStorageEnergyStrategyBaseInfo?nodeId='+nodeId +'&systemId='+e).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				
				if(res.data.data){
					setStrategyExpiryDate(res.data.data.strategyExpiryDate);
					setStrategyStartTime(res.data.data.strategyStartTime);
					setIstrue(true)
				}else{
					setStrategyExpiryDate('');
					setStrategyStartTime('');
					setIstrue(true)
				}
				
			}else{
				message.info(res.data.msg)
				setStrategyExpiryDate('');
				setStrategyStartTime('');
				setIstrue(false)
				return false
			}
		})
		// 查询储能基本信息-分成比例
		http.post('system_management/energy_model/energy_storage_model/findStorageEnergyShareProportionBaseInfo?nodeId='+nodeId +'&systemId='+e).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				
				if(res.data.data){
					setShareProportionExpiryDate(res.data.data.shareProportionExpiryDate);
					setShareProportionStartTime(res.data.data.shareProportionStartTime);
					setIstrue(true);
				}else{
					setShareProportionExpiryDate('');
					setShareProportionStartTime('');
					setIstrue(true)
				}
				
			}else{
				message.info(res.data.msg)
				setShareProportionExpiryDate('');
				setShareProportionStartTime('');
				setIstrue(false)
				return false
			}
		})
		
		// 充放电列表
		http.post('system_management/energy_model/energy_storage_model/getAllMeteringDeviceList?nodeId='+nodeId+'&systemId='+e ).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let device = res.data.data;
				setDeviceSnName(res.data.data)
				// 查询设备
				http.post('system_management/energy_model/energy_storage_model/getAllMeteringDeviceOtherList?nodeId='+nodeId+'&systemId='+e ).then(res =>{
					console.log(res)
					if(res.data.code==200){
						let data = res.data.data
						if(data){
							console.log(device)
								for(var i=0;i<device.length;i++){
									if(device[i].deviceSn == data.battery_status_device_sn){
										setBattery_status_device_sn(device[i].deviceName);
										setIstrue(true)
									}
									if(device[i].deviceSn == data.charging_device_sn){
										setCharging_device_sn(device[i].deviceName);
										setIstrue(true)
									}
									if(device[i].deviceSn == data.discharging_device_sn){
										setDischarging_device_sn(device[i].deviceName);
										setIstrue(true)
									}
								}
								for(var i=0;i<device.length;i++){
									if(device[i].deviceName == data.battery_status_device_sn){
										setBattery_status_device_sn(device[i].deviceName);
										setIstrue(true)
									}
									if(device[i].deviceName == data.charging_device_sn){
										setCharging_device_sn(device[i].deviceName);
										setIstrue(true);
									}
									if(device[i].deviceName == data.discharging_device_sn){
										setDischarging_device_sn(device[i].deviceName);
										setIstrue(true);
									}
								}
							if(!data.battery_status_device_sn){
								setBattery_status_device_sn('');
							}
							if(!data.charging_device_sn){
								setCharging_device_sn('');
							}
							if(!data.discharging_device_sn){
								setDischarging_device_sn('')
							}
						
						}else{
							message.info(res.data.msg)
							setBattery_status_device_sn('');
							setCharging_device_sn('');
							setDischarging_device_sn('');
							setIstrue(false);
							return false
						}
					}
						
						
						
				}).catch(err =>{
					console.log(err)
				})
			}
		}).then(err =>{
			console.log(err)
		})
		// 查询储能下发配置
		http.post('system_management/energy_model/energy_storage_model/findStorageEnergySynCfg?nodeId='+nodeId+'&systemId='+e ).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				if(res.data.data){
					let data = res.data.data;
					setArgs(data.args);
					setName(data.name);
					setRequestUrl(data.requestUrl);
					setIstrue(true);
				}else{
					setArgs('');
					setName('');
					setRequestUrl('');
					setIstrue(true);
				}
				
			}else{
				message.info(res.data.msg)
				setArgs('');
				setName('');
				setRequestUrl('');
				setIstrue(false);
				return false
			}
		}).then(err =>{
			console.log(err)
		})
		// 查询储能下发配置
		http.post('system_management/energy_model/energy_storage_model/findStorageEnergyDistributeCfg?nodeId='+nodeId+'&systemId='+e ).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				if(res.data.data){
					let data = res.data.data;
					setArgs1(data.args);
					setName1(data.name);
					setRequestUrl1(data.requestUrl);
					setIstrue(true);
				}else{
					setArgs1('');
					setName1('');
					setRequestUrl1('');
					setIstrue(true);
				}
				
			}else{
				message.info(res.data.msg)
				setArgs1('');
				setName1('');
				setRequestUrl1('');
				setIstrue(false);
				return false
			}
		}).then(err =>{
			console.log(err)
		})
	}
	
	const handleChange =(newValue) =>{
		console.log(newValue,'修改过得值')
	}
	return(
		<div className="allcontent allcontentesae">
			<div className="accheader">
				<span className="panel">节点：</span>
				<Cascader style={{width:300}} options={options} loading={currentloading} 
				displayRender={(labels, selectedOptions) => labels[labels.length - 1]}
				onChange={onChange1} placeholder="请选择" />
				<span className="panel panels">系统：</span>
				<Select defaultValue="请选择" value={systemName} loading={sysloading} 
				style={{ width: 150,color:systemName=='请选择'?'#8F959E':systemName==''?'#8F959E':'#2A2B40' }} onChange={sysChange}>
					{
						nodeSystemList.length &&nodeSystemList.map(res =>{
							return <Option value={res.systemKey} key={res.systemKey}>{res.systemName}</Option>
						})
					}
				</Select>
			</div>
			<Tabs defaultActiveKey="1" onChange={onChange}>
				<TabPane tab="基本信息" key="1">
					<Separate 
						nodeId={nodeId}
						systemId={systemId}
						istrue={istrue}
						changeData={ChildrenChange}
						storageEnergyCapacity={storageEnergyCapacity}
						storageEnergyLoad={storageEnergyLoad}
						minDischargePercent={minDischargePercent}
						maxChargePercent={maxChargePercent}
						strategyStartTime={strategyStartTime}
						strategyExpiryDate={strategyExpiryDate}
						shareProportionStartTime ={shareProportionStartTime}
						shareProportionExpiryDate={shareProportionExpiryDate}
						deviceSnName={deviceSnName}
						discharging_device_sn={discharging_device_sn}	//放
						charging_device_sn={charging_device_sn}	//冲
						battery_status_device_sn={battery_status_device_sn}		//电池
						battey={battey}
						name={name}
						requestUrl={requestUrl}
						args={args}
						name1={name1}
						requestUrl1={requestUrl1}
						args1={args1}
						onQuery={onQuery}
					/>
					
				</TabPane>
				<TabPane tab="分时电价" key="2">
					<Electrovalence
					 nodeId={nodeId}
					 systemId={systemId}
					 strategyStartTime={strategyStartTime}
					 strategyExpiryDate={strategyExpiryDate}
					 changeData={ChildrenChange}
					 istrue={istrue} />
				</TabPane>
				<TabPane tab="分成比例" key="3">
					<Divide 
						nodeId={nodeId}
						systemId={systemId}
						istrue={istrue}
						changeData={ChildrenChange}
						// count={nodeId}
						// handleIncrement={ChildrenChange}
					/>
					
				</TabPane>
				
			  </Tabs>
		</div>
	)
}


export default Accumulation_model