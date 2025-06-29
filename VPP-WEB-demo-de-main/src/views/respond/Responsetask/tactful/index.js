import React,{useEffect,useState} from 'react'
import { Link  } from "react-router-dom"

import { Select, Space,Table,Button,Input,message } from 'antd';

import './tactful.scss'
import http from '../../../../server/server.js'
import {
  InfoCircleOutlined,
  
} from '@ant-design/icons';
const { Search } = Input;
const Tactful =(props) =>{
	// console.log(props)
	const [respId, setRespId] = useState(props.user.respId);
	const [tactfulList, setTactfulList] = useState([]);
	const [strategyId, setStrategyId] = useState('');
	const [deviceRatedPowerSort, setDeviceRatedPowerSort] = useState(1);
	const [deviceStatusSort, setDeviceStatusSort] = useState('');
	const [page, setPage] = useState(1);
	const [totalActualLoad, setTotalActualLoad] = useState('-');
	const [totalDeviceRatedPower, setTotalDeviceRatedPower] = useState('-');
	const [deviceName, setDeviceName] = useState('');
	const [oldStrategyId, setOldStrategyId] = useState('');
	const [loading, setLoading] = useState(false);
	const [dataSource, setDataSource] = useState([]);
	const [strategyName, setStrategyName] = useState('');
	const [ymd, setYmd] = useState('');
	const [times, setTimes] = useState('');
	const [power, setPower] = useState('');
	const [val, setVal] = useState('');
	const [total,setTotal] = useState('')
	useEffect(() =>{
		getStrategyList()
	},[])
	useEffect(() =>{
		getDeviceListById()
		strategyDetail()
	},[strategyId])
	useEffect(() =>{
		getDeviceListById()
	},[deviceName,page])
	
	// 查询该用户具有权限的策略名称
	const getStrategyList =() =>{
		http.post('demand_resp/resp_task/getStrategyList?respId='+respId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let obj = res.data.data.strategyList
				let objNew = [];
				for (let i in obj) {
					objNew.push({ 
						label: obj[i],
						value:i
					})
				}		
				console.log('objNew',objNew);
				if(res.data.data.oldStrategyId){
					setVal(res.data.data.oldStrategyId)
					setStrategyId(res.data.data.oldStrategyId)
					setLoading(false)
					
				}
				setTactfulList(objNew)
				setOldStrategyId(res.data.data.oldStrategyId)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// id获取设备列表
	const getDeviceListById=() =>{
		setLoading(true)
		http.post('demand_resp/resp_task/getDeviceListById',{
			"deviceName": deviceName,	//设备名称
			"deviceRatedPowerSort": deviceRatedPowerSort,	//额定负荷排序
			"deviceStatusSort": deviceStatusSort,	//策略状态排序 
			"number": page,
			"pageSize": 5,
			"respId": respId,	//响应任务id
			"sid": "",
			"strategyId": strategyId	//可调负荷运行策略id
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				// console.log(res.data.data.devieInfo.content)
				if(res.data.data.devieInfo){
					setDataSource(res.data.data.devieInfo.content || []);
					setTotal(res.data.data.devieInfo.totalElements);
					setTotalActualLoad(res.data.data.totalActualLoad);
					setTotalDeviceRatedPower(res.data.data.totalDeviceRatedPower);
					setLoading(false);
					
				}else{
					setDataSource([]);
					setTotal('');
					setTotalActualLoad(res.data.data.totalActualLoad);
					setTotalDeviceRatedPower(res.data.data.totalDeviceRatedPower);
					setLoading(false);
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 策略选择
	const strategyChange =(val) =>{
		console.log(val)
		setStrategyId(val);
		setLoading(true);
		setVal(val);
		getDeviceListById()
		strategyDetail()
	}
	// 搜索设备列表
	const getDeviceListByName=() =>{
		http.post('demand_resp/resp_task/getDeviceListByName',{
			"deviceName": deviceName,	//设备名称
			"deviceRatedPowerSort": deviceRatedPowerSort,
			"deviceStatusSort": deviceStatusSort,
			"number": page,
			"pageSize": 10,
			"respId": respId,
			"sid": "",
			"strategyId": strategyId
		}).then(res =>{
			console.log(res)
		}).catch(err =>{
			console.log(err)
		})
	}
	// 搜索
	const onSearch =(val) =>{
		console.log(val)
		setDeviceName(val)
		// getDeviceListById()
	}
	// 编辑策略
	const editStrategy =() =>{
		http.post('demand_resp/resp_task/editStrategy?oldStrategyId='+
		oldStrategyId+'&strategyId='+strategyId+'&respId='+respId).then(res =>{
			console.log(res)
		}).catch(err =>{
			console.log(err)
		})
	}
	// 详情
	const strategyDetail=() =>{
		http.post('run_schedule/run_strategy/strategyDetail?strategyId='+strategyId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				if(data.strategyType==0){
					// 空调
					if(data.runStrategy==0){
						// 一次性
						setYmd(data.onceExe.ymd);
						setTimes(data.onceExe.times);
						setPower(data.airConditioningDTO.power === 'POWER_ON' ? '开启' : '关闭');
						setStrategyName(data.strategyName);
					}else{
						setYmd('');
						setTimes(data.cycleExe.cycleTimes);
						setPower(data.airConditioningDTO.power === 'POWER_ON' ? '开启' : '关闭');
						setStrategyName(data.strategyName);
						
					}
				}else if(data.strategyType==1){
					// 七天
					if(data.runStrategy==0){
						// 一次性
						
						setYmd(data.onceExe.ymd);
						setTimes(data.onceExe.times);
						setPower(data.otherConditioningDTO.power=='POWER_ON'?'开启':'关闭');
						setStrategyName(data.strategyName);
					}else{
						setYmd('');
						setTimes(data.onceExe.times);
						setPower(data.otherConditioningDTO.power=='POWER_ON'?'开启':'关闭');
						setStrategyName(data.strategyName);
					}
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 
	// 
	const ontactful =() =>{
		http.post('demand_resp/resp_task/editStrategy?oldStrategyId='+
		oldStrategyId+'&strategyId='+strategyId+'&respId='+respId
		).then(res =>{
			console.log(res)
			if(res.data.code==200){
				message.success('成功')
				// this.props.history.push({ pathname: '/Responsetask' });
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const columns = [
		{
			title: '编号',
			dataIndex: 'name',
			key: 'name',
			width:80,
			render:(value, item, index) => (page - 1) * 5 + index+1,
		},
		{
			title: '户号',
			dataIndex: 'noHouseholds',
			key: 'noHouseholds',
		},
		{
			title: '节点',
			dataIndex: 'nodeName',
			key: 'nodeName',
		},
		{
			title: '系统',
			dataIndex: 'systemName',
			key: 'systemName',
		},
		{
			title: '设备',
			dataIndex: 'deviceName',
			key: 'deviceName',
		},
		{
			title: '额定负荷（kW）',
			dataIndex: 'deviceRatedPower',
			key: 'deviceRatedPower',
			sorter: true,
			render:(value, item, index) =>{
				if(value==null||value==undefined||value===""||value=='-'){
					return '-'
				}else{
					return Number(value).toFixed(2)
				}
				
			}
		},
		// {
		// 	title: '实时负荷（kW）',
		// 	dataIndex: 'actualLoad',
		// 	key: 'actualLoad',
		// },
		{
			title: '设备状态',
			dataIndex: 'sstatus',
			key: 'sstatus',
			sorter: true,
			render:(text, item, index) =>{
				return text==1?'开启':'关闭'
			}
		},
	];
		 
		// 分页
	const handlePagination = page => {
		
		setPage(page)
		// getDeviceListById()
	};
	// 排序
	const onChangetab = (pagination, filters, sorter) =>{
		
		console.log(sorter)
		if (sorter.order) {
			// deviceRatedPower
			// 
			if(sorter.columnKey=='deviceRatedPower'){
				if(sorter.order == "ascend"){
					// 升序
					setDeviceRatedPowerSort(1);
					setDeviceStatusSort('');
					getDeviceListById()
				}else if(sorter.order == "descend"){
					// 降序
					setDeviceRatedPowerSort(2);
					setDeviceStatusSort('');
					getDeviceListById()
				}
			}
		}
	}
	return (
		<div className="tactful">
			
			<div className="tactfulbody">
				<h4>选择策略</h4>
				<div>
					<div className="strategy">
						<span className="strategyspan">策略名称</span>
						<Select
							defaultValue="请选择"
							style={{ width: 155 }}
							bordered={false}
							options={tactfulList}
							onChange={strategyChange}
							value={val}
						/>
					</div>
				</div>
				<div className="breed">
					<ul className="breedul">
						<li style={{width:'100%'}}>策略类型：{strategyName}</li>
						<li>执行日期：{ymd}</li>
						<li>执行时间：{times}</li>
						<li>执行动作：{power}</li>
					</ul>
				</div>
				<div className="breed" style={{border:'none'}}>
					<div style={{overflow:'hidden'}}>
						<div className="all">合计</div>
						<div className="charges">
							额定负荷(kW)
							<span>{totalDeviceRatedPower}</span>
						</div>
						
						<Search placeholder="搜索设备" onSearch={onSearch} style={{ width: 200,float:'right',marginTop:15 }} />
					</div>
					<div style={{marginTop:'20px'}}>
						<Table dataSource={dataSource} columns={columns} 
							loading={loading}
							onChange={(pagination, filters, sorter) => { onChangetab(pagination, filters, sorter) }}
							
							pagination={
								{
									total:total,//数据的总条数
									defaultCurrent: 1,//默认当前的页数
									defaultPageSize: 5,//默认每页的条数
									showQuickJumper:false,
									onChange: (page)=>handlePagination(page),
									// onChange: handlePagination,
								}
							}
						/>

					</div>
				</div>
				<div className="footerbtn">
					<Button type="primary" onClick={ontactful} disabled={dataSource.length==0?true:false}>确定</Button>
					
				</div>
			</div>
		</div>
	)
}


export default Tactful
// <div className="header">
// 					<Link to='/Responsetask'><a href="#"><img src={require('../../../../style/xu/return.png')} />返回</a></Link>
// 				</div>