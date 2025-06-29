import React, { useEffect,useState } from 'react';
import { Tabs,Select ,Input,Button,Table ,message,Cascader } from 'antd';
import http from '../../../../server/server.js'
import './index.css'
import Separate from './separate/index.js' //基本信息
// import Telegram from './telegram/index.js'	//分时电价
import Electrovalence from './electrovalence/index.js'

import Divide from './divide/index.js'		//电力用户购电折扣比例
// import './separate/index.scss'
import './index.scss'
// import ToTabContent from '../ToTabContent.js'  //组件
const { TabPane } = Tabs;
const { Option } = Select;

const Photovoltaic_model =() =>{
	const [dataSource, setDataSource] = useState([]);
	const [power, setPower] = useState(12);
	const [volume, setVolume] = useState(12);
	const [isShow, setIsShow] = useState(false);
	const [val, setVal] = useState('编辑');
	const [username, setUsername] = useState('');
	const [nodeNameList, setNodeNameList] = useState([]);
	const [nodeSystemList, setNodeSystemList] = useState([]);
	const [systemName, setSystemName] = useState('请选择');
	const [nodeId, setNodeId] = useState('');
	const [systemId, setSystemId] = useState('');
	const [istrue, setIstrue] = useState(false);
	const [istrue1, setIstrue1] = useState(false);
	const [photovoltaicInstalledCapacity, setPhotovoltaicInstalledCapacity] = useState(''); //光伏基本信息
	const [timeDivisionExpiryDate, setTimeDivisionExpiryDate] = useState(''); //分时电价年限
	const [timeDivisionStartTime, setTimeDivisionStartTime] = useState(''); //分时电价开始时间
	const [powerUserExpiryDate, setPowerUserExpiryDate] = useState(''); //电力用户购电折扣比例 年限
	const [powerUserStartTime, setPowerUserStartTime] = useState(''); //电力用户购电折扣比例 开始时间
	const [Array, setArray] = useState([
		{
			title: '基本信息',
			name: 'Separate',
		},
		{
			title: '充放电策略',
			name: '1',
		},
		{
			title: '分成比例',
			name: '1',
		}
	]);
	const [options, setOptions] = useState([]);
	const [sysloading, setSysloading] = useState(false);
	const [currentloading, setCurrentloading] = useState(false);

	
	useEffect(() =>{
		storageEnergyNodeNameList()
		pvNodeTree()
	},[])
	const ChildrenChange=()=>{
		setIstrue(false)
	}
	
	// 节点列表
	const pvNodeTree =() =>{
		setCurrentloading(true)
		http.post('tree/pvNodeTree').then(res =>{
			console.log(res)
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
				setOptions(data);
				setCurrentloading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 节点
	const storageEnergyNodeNameList=() =>{
		http.post('system_management/node_model/pvNodeNameList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setNodeNameList(res.data.data)
			}else{
				message.info(res.data.msg)
			}
		})
	}
	// 光伏系统
	const systemPVList=() =>{
		http.post('system_management/system_model/systemPVList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setNodeSystemList(res.data.data)
			}else{
				message.info(res.data.msg)
			}
		})
	}
	// 选择系统
	
	const onChange1 = (val) =>{
		console.log(val)
		let length = val.length
		setNodeId(val[length-1]);
		setSystemName('');
		setSystemId('');
		systemPVList();
	}
	// 选择系统
	const sysChange =(e) =>{
		console.log(e,'-----')
		if(e){
			let length = e.length
			setSystemName(e);
			setSystemId(e)
			const params = new URLSearchParams();
			params.append('nodeId', nodeId);
			params.append('systemId', e);
			// 基本信息
			http.post('system_management/energy_model/photovoltaic_model/findPhotovoltaicBaseInfo',params).then(res =>{
				console.log(res)
				// console.log(res.data.data.shareProportionExpiryDate)
				if(res.data.code ==200){
					if(res.data.data){
						setPhotovoltaicInstalledCapacity(res.data.data.photovoltaicInstalledCapacity);
						setIstrue(true)
					}else{
						setPhotovoltaicInstalledCapacity('');
						setIstrue(true)
					}
					
				}else{
					message.info('光伏电站'+res.data.msg)
					setPhotovoltaicInstalledCapacity('');
					setIstrue(true);
					return false
				}
			})
			// 分时电价
			http.post('system_management/energy_model/photovoltaic_model/findPhotovoltaicTimeDivisionBaseInfo',params).then(res =>{
				console.log(res)
				// console.log(res.data.data.shareProportionExpiryDate)
				if(res.data.code ==200){
					if(res.data.data){
						setTimeDivisionExpiryDate(res.data.data.timeDivisionExpiryDate);
						setTimeDivisionStartTime(res.data.data.timeDivisionStartTime);
						setIstrue(true)
					}else{
						setTimeDivisionExpiryDate('');
						setTimeDivisionStartTime('');
						setIstrue(true)
					}
					
				}else{
					message.info('分时电价'+res.data.msg)
					setTimeDivisionExpiryDate('');
					setTimeDivisionStartTime('');
					setIstrue(true)
					return false
				}
			})
			// 电力用户购电折扣比例
			http.post('system_management/energy_model/photovoltaic_model/findPhotovoltaicPowerUserBaseInfo',params).then(res =>{
				console.log(res)
				// console.log(res.data.data.shareProportionExpiryDate)
				if(res.data.code ==200){
					if(res.data.data){
						setPowerUserExpiryDate(res.data.data.powerUserExpiryDate);
						setPowerUserStartTime(res.data.data.powerUserStartTime);
						setIstrue(true)
					}else{
						setPowerUserExpiryDate('');
						setPowerUserStartTime('');
						setIstrue(true)
					}
					
				}else{
					message.info('电力用户购电折扣比例'+res.data.msg)
					setPowerUserExpiryDate('');
					setPowerUserStartTime('');
					setIstrue(true)
					return false
				}
			})
						
						
			
		}
		
	}
	const onChange =(key) =>{
		console.log(key)
		if(key ==1){
			// 基本信息
			http.post('system_management/energy_model/photovoltaic_model/findPhotovoltaicBaseInfo?nodeId='+nodeId +'&systemId='+systemId).then(res =>{
				console.log(res)
				// console.log(res.data.data.shareProportionExpiryDate)
				if(res.data.code ==200){
					if(res.data.data){
						setPhotovoltaicInstalledCapacity(res.data.data.photovoltaicInstalledCapacity);
						setIstrue(true)
					}else{
						setPhotovoltaicInstalledCapacity('');
						setIstrue(true)
					}
					
				}else{
					message.info('光伏电站'+res.data.msg)
					setPhotovoltaicInstalledCapacity('');
					setIstrue(true)
					return false
				}
			})
			// \电力用户购电折扣比例
			http.post('system_management/energy_model/photovoltaic_model/findPhotovoltaicPowerUserBaseInfo?nodeId='+nodeId +'&systemId='+systemId).then(res =>{
				console.log(res)
				// console.log(res.data.data.shareProportionExpiryDate)
				if(res.data.code ==200){
					if(res.data.data){
						setPowerUserExpiryDate(res.data.data.powerUserExpiryDate);
						setPowerUserStartTime(res.data.data.powerUserStartTime);
						setIstrue(true)
					}else{
						setPowerUserExpiryDate('');
						setPowerUserStartTime('');
						setIstrue(true)
					}
					
				}else{
					message.info('电力用户购电折扣比例'+res.data.msg)
					setPowerUserExpiryDate('');
					setPowerUserStartTime('');
					setIstrue(true)
					return false
				}
			})
			// 分时电价
			http.post('system_management/energy_model/photovoltaic_model/findPhotovoltaicTimeDivisionBaseInfo?nodeId='+nodeId +'&systemId='+systemId).then(res =>{
				console.log(res)
				// console.log(res.data.data.shareProportionExpiryDate)
				if(res.data.code ==200){
					if(res.data.data){
						
						setTimeDivisionExpiryDate(res.data.data.timeDivisionExpiryDate);
						setTimeDivisionStartTime(res.data.data.timeDivisionStartTime);
						setIstrue(true);
					}else{
						setTimeDivisionExpiryDate('');
						setTimeDivisionStartTime('');
						setIstrue(true);
					}
					
				}else{
					message.info('分时电价'+res.data.msg)
					setTimeDivisionExpiryDate('');
					setTimeDivisionStartTime('');
					setIstrue(true);
					return false
				}
			})
		}else{
			setIstrue(true);
			
		}
	}
	// const 
	
	return(
	<div className="Photovoltaic_model">
		<div className="accheader">
			<span className="panel">节点：</span>
			<Cascader style={{ width: 300 }} loading={currentloading} options={options} 
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
					photovoltaicInstalledCapacity={photovoltaicInstalledCapacity}
					timeDivisionExpiryDate={timeDivisionExpiryDate}
					timeDivisionStartTime={timeDivisionStartTime}
					powerUserExpiryDate={powerUserExpiryDate}
					powerUserStartTime={powerUserStartTime}
					istrue={istrue}
					changeData={ChildrenChange}
				/>	
			</TabPane>
			<TabPane tab="分时电价" key="2">
				<Electrovalence
					nodeId={nodeId}
					systemId={systemId}
					istrue={istrue}
					changeData={ChildrenChange}
				 />
			</TabPane>
			<TabPane tab="电力用户购电折扣比例" key="3">
				<Divide 
					nodeId={nodeId}
					systemId={systemId}
					istrue={istrue}
					changeData={ChildrenChange}
					// powerUserExpiryDate=
				/>
			</TabPane>
		  </Tabs>
		  
	</div>
	)
	
}


export default Photovoltaic_model