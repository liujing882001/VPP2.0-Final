import React, { Component } from 'react';
import { Tabs,Select ,Input,Button,Table ,message,Cascader} from 'antd';
import http from '../../../../server/server.js'
import './index.css'
import Separate from './separate/index.js'	//基本信息
import Telegram from './telegram/index.js'	//充放电策略
import Divide from './divide/index.js'	//分成比例
import Ai from './ai/index.js'	//ai调度
const { TabPane } = Tabs;
const { Option } = Select;

class accumulation_model extends Component {
	constructor(props) {
		super(props)
		this.state={
			dataSource:[],
			power:12,
			volume:12,
			isShow:false,
			val:'编辑',
			username:'',
			nodeNameList:[],
			nodeSystemList:[],
			systemName:'',
			istrue:false,
			istrue1:false,
			nodeId:'',	//节点id
			systemId:'',	//系统id
			storageEnergyCapacity:'',	//储能电池容量
			storageEnergyLoad:''		,//储能电站功率 kw
			strategyExpiryDate:'',	//充放电策略年限
			strategyStartTime:'',	//充放电策略开始时间
			shareProportionExpiryDate:''  ,//分成比例年限
			shareProportionStartTime:''	,//开始时间
			pvNodeNameList:[]	,//节点
			deviceSn:[],
			battery_status_device_sn:'',	//电池
			charging_device_sn:'',	//充
			discharging_device_sn:'',	//放
			battey:[]	,//电池
			loading:false,
			sysloading:false,
			options:[],
			optionsloading:false,
			nodeIdVal:'',
			nodeSystemVal:'',
			isFirst:false
			
		}
	}
	componentDidMount(){
		// this.nodeNameList()
		// this.pvNodeNameList()
		this.storageEnergyNodeTree()
	}
	ChildrenChange=()=>{
		this.setState({
			istrue:false
		})
	}
	//归属节点列表
	nodeNameList(){
		
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				this.setState({
					nodeNameList:res.data.data,
					// loading:false
				})
			}else{
				message.info(res.data.msg)
			}
		})
	}
	// 获取节点
	storageEnergyNodeTree(){
		this.setState({
			optionsloading:true
		})
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
				this.setState({
					options:data,
					optionsloading:false,
					nodeIdVal:array1,
					isFirst:true,
					nodeId:length>0&&array1[length-1],
				},() =>{
					// console.log(this.state.options)
					this.systemStorageEnergyList()
					
				})
			}
		}).catch(err =>{
			
		})
	}
	// 选择节点
	treeChange =(val) =>{
		console.log(val)
		let length = val.length
		this.setState({
			nodeId:val[length-1],
			systemName:'',
			sysloading:true,
			isFirst:false,
			nodeSystemVal:''
		},() =>{
			
			this.systemStorageEnergyList()
		})
		
	}
	// // 获取节点
	// pvNodeNameList(){
	// 	this.setState({
	// 		loading:true
	// 	})
	// 	http.post('system_management/node_model/storageEnergyNodeNameList').then(res =>{
	// 		console.log(res)
	// 		if(res.data.code ==200){
	// 			this.setState({
	// 				pvNodeNameList:res.data.data,
	// 				loading:false
	// 			},() =>{
					
	// 			})
	// 		}
	// 	}).catch(err =>{
	// 		console.log(err)
	// 	})
	// }
	// 获取系统
	systemStorageEnergyList(){
		let {isFirst} = this.state
		http.post('system_management/system_model/systemStorageEnergyList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				this.setState({
					// systemName:'请选择',
					nodeSystemList:res.data.data,
					sysloading:false,
				})
				if(isFirst==true){
					this.setState({
						nodeSystemVal:res.data.data.length>0&&res.data.data[0].systemKey,
						systemName:res.data.data.length>0&&res.data.data[0].systemKey,
						systemId:res.data.data.length>0&&res.data.data[0].systemKey,
					})
				}
			}else{
				message.info(res.data.msg)
			}
		})
	}
	onChange = (key) => {
		console.log(key);
		if(key ==1){
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyBaseInfo?nodeId='+this.state.nodeId +'&systemId='+this.state.systemId).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					if(res.data.data){
						this.setState({
							storageEnergyCapacity:res.data.data.storageEnergyCapacity,
							storageEnergyLoad:res.data.data.storageEnergyLoad,
							istrue:true,
							
						})
					}else{
						this.setState({
							storageEnergyCapacity:'',
							storageEnergyLoad:'',
							istrue:true,
							
						})
					}
					
				}else{
					message.info(res.data.msg)
					this.setState({
						storageEnergyCapacity:'',
						storageEnergyLoad:'',
						
						istrue:false,
						
					})
					return false
				}
			})
		 }else if(key ==2){
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyStrategyBaseInfo?nodeId='+this.state.nodeId +'&systemId='+this.state.systemId).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					
					if(res.data.data){
						console.log(res.data.data.strategyExpiryDate)
						this.setState({
							strategyExpiryDate:res.data.data.strategyExpiryDate,
							
							strategyStartTime:res.data.data.strategyStartTime,
							istrue:true,
							
						})
					}else{
						this.setState({
							strategyExpiryDate:'',
							strategyStartTime:'',
							istrue:true,
							
						})
					}
					
				}else{
					message.info(res.data.msg)
					this.setState({
						strategyExpiryDate:'',
						strategyStartTime:'',
						istrue:false,
						
					})
					return false
				}
			})
		 }else if(key ==3){
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyShareProportionBaseInfo?nodeId='+this.state.nodeId +'&systemId='+this.state.systemId).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					
					if(res.data.data){
						console.log(res.data.data.shareProportionExpiryDate)
						this.setState({
							shareProportionExpiryDate:res.data.data.shareProportionExpiryDate,
							shareProportionStartTime:res.data.data.shareProportionStartTime,
							istrue:true,
							
						})
					}else{
						this.setState({
							istrue:true,
							shareProportionExpiryDate:'',
							shareProportionStartTime:'',
						})
					}
					
				}else{
					message.info(res.data.msg)
					this.setState({
						shareProportionExpiryDate:'',
						shareProportionStartTime:'',
						istrue:false,
						
					})
					return false
				}
			})
		 }
	  
	};
	// 选择系统
	handleChange =(e) =>{
		console.log(e)
		this.setState({
			nodeId:e,
			systemName:'',
			sysloading:true,
			
		},() =>{
			
			this.systemStorageEnergyList()
		})

	}
	sysChange =(e) =>{
		console.log(e)
		this.setState({
			systemName:e,
			systemId:e,
			nodeSystemVal:e
		},() =>{
			// 查询储能基本信息
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyBaseInfo?nodeId='+this.state.nodeId +'&systemId='+e).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					if(res.data.data){
						this.setState({
							storageEnergyCapacity:res.data.data.storageEnergyCapacity,
							storageEnergyLoad:res.data.data.storageEnergyLoad,
							istrue:true,
							
						})
					}else{
						this.setState({
							storageEnergyCapacity:'',
							storageEnergyLoad:'',
							istrue:true,
							
						})
					}
					
				}else{
					message.info(res.data.msg)
					this.setState({
						storageEnergyCapacity:'',
						storageEnergyLoad:'',
						
						istrue:false,
						
					})
					return false
				}
			})
			// 查询储能基本信息-充放电策略
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyStrategyBaseInfo?nodeId='+this.state.nodeId +'&systemId='+e).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					
					if(res.data.data){
						console.log(res.data.data.strategyExpiryDate)
						this.setState({
							strategyExpiryDate:res.data.data.strategyExpiryDate,
							
							strategyStartTime:res.data.data.strategyStartTime,
							istrue:true,
							
						})
					}else{
						this.setState({
							strategyExpiryDate:'',
							
							strategyStartTime:'',
							istrue:true,
							
						})
					}
					
				}else{
					message.info(res.data.msg)
					this.setState({
						strategyExpiryDate:'',
						strategyStartTime:'',
						istrue:false,
						
					})
					return false
				}
			})
			// 查询储能基本信息-分成比例
			http.post('system_management/energy_model/energy_storage_model/findStorageEnergyShareProportionBaseInfo?nodeId='+this.state.nodeId +'&systemId='+e).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					
					if(res.data.data){
						console.log(res.data.data.shareProportionExpiryDate)
						this.setState({
							shareProportionExpiryDate:res.data.data.shareProportionExpiryDate,
							shareProportionStartTime:res.data.data.shareProportionStartTime,
							istrue:true,
							
						})
					}else{
						this.setState({
							shareProportionExpiryDate:'',
							shareProportionStartTime:'',
							istrue:true,
							
						})
					}
					
				}else{
					message.info(res.data.msg)
					this.setState({
						shareProportionExpiryDate:'',
						shareProportionStartTime:'',
						istrue:false,
						
					})
					return false
				}
			})	
			// 充放电列表
			http.post('system_management/energy_model/energy_storage_model/getAllMeteringDeviceList?nodeId='+this.state.nodeId+'&systemId='+e ).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					this.setState({
						deviceSn:res.data.data
					},()=>{
						// 查询设备
						http.post('system_management/energy_model/energy_storage_model/getAllMeteringDeviceOtherList?nodeId='+this.state.nodeId+'&systemId='+e ).then(res =>{
							console.log(res)
							if(res.data.code==200){
								let data = res.data.data
								console.log(data,'-----')
								if(res.data.data){
									this.setState({
										battery_status_device_sn:data.battery_status_device_sn,
										charging_device_sn:data.charging_device_sn,
										discharging_device_sn:data.discharging_device_sn,
										istrue:true,
									})
								}else{
									message.info(res.data.msg)
									this.setState({
										battery_status_device_sn:'',
										charging_device_sn:'',
										discharging_device_sn:'',
										istrue:false,
										
									})
									return false
								}
							}							
						}).catch(err =>{
							console.log(err)
						})
					})
				}
			}).then(err =>{
				console.log(err)
			})
		})
	}
	
	render(){
		let {power,volume,val,isShow,nodeNameList,nodeSystemList,nodeIdVal,nodeSystemVal,
		pvNodeNameList,deviceSn,loading,sysloading,options,optionsloading,systemName
		} = this.state
		return(
			<div className="allcontent allcontentesae accumulation_model">
				<div className="accheader">
					<span className="panel">节点：</span>
					
					<Cascader style={{width:300}} 
					value={nodeIdVal}
					loading={optionsloading} options={options} onChange={this.treeChange} placeholder="请选择" />
					<span className="panel panels">系统：</span>
					<Select	 defaultValue="请选择"  loading={sysloading} 
					value={nodeSystemVal}
					style={{ width: 150 ,color:systemName=='请选择'?'#8F959E':systemName==''?'#8F959E':'#2A2B40'}} onChange={this.sysChange}>
					    {
							nodeSystemList.length &&nodeSystemList.map(res =>{
								return <Option value={res.systemKey} key={res.systemKey}>{res.systemName}</Option>
							})
						}
					</Select>
				</div>
				<Tabs defaultActiveKey="1" onChange={this.onChange}>
				    
				    <TabPane tab="储能分时电价" key="2">
						<Telegram
						 nodeId={this.state.nodeId}
						 systemId={this.state.systemId}
						 strategyStartTime={this.state.strategyStartTime}
						 strategyExpiryDate={this.state.strategyExpiryDate}
						 />
				    </TabPane>
					{/* <TabPane tab="AI调度" key="4">
						<Ai 
							nodeId={this.state.nodeId}
							systemId={this.state.systemId}
							istrue={this.state.istrue}
							changeData={this.ChildrenChange}
						/>
					</TabPane> */}
				   
					
				  </Tabs>
			</div>
		)
	}
}

export default accumulation_model
// <TabPane tab="AI调度" key="4" disabled>
// 					 	<Ai 
// 							nodeId={this.state.nodeId}
// 							systemId={this.state.systemId}
// 							istrue={this.state.istrue}
// 							changeData={this.ChildrenChange}
// 						/>
// 					</TabPane>