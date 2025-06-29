import React,{useEffect,useState} from 'react'
import { Link  } from "react-router-dom"
import { Select, Space,Table,Button,Input ,message} from 'antd';
import { useHistory  } from "react-router-dom"

import './index.css'
import './detail.scss'
import http from '../../../server/server.js'
const Historydetail =(props) =>{
	const [respId, setRespId] = useState('');
	const [taskCode, setTaskCode] = useState('');
	const [rsDate, setRsDate] = useState('');
	const [feedbackTime, setFeedbackTime] = useState('');
	const [respLoad, setRespLoad] = useState('');
	const [respSubsidy, setRespSubsidy] = useState('');
	const [respType, setRespType] = useState('');
	const [respLevel, setRespLevel] = useState('');
	const [profit, setProfit] = useState('');
	const [deviceName, setDeviceName] = useState('');
	const [page, setPage] = useState(1);
	const [strategyId, setStrategyId] = useState('');
	const [deviceStatusSort, setDeviceStatusSort] = useState(1);
	const [deviceRatedPowerSort, setDeviceRatedPowerSort] = useState('');
	const [dataSource, setDataSource] = useState([]);
	const [startDate, setStartDate] = useState('');
	const [endDate, setEndDate] = useState('');
	const [total,setTotal] = useState('');
	const history = useHistory()
	console.log(props)


	useEffect(() =>{
		if(props.location.state){
			const query = props.location.state.detail
			// console.log(location)
			setRespId(query.respId)
			setTaskCode(query.taskCode);
			setRsDate(query.rsDate + ' ' +query.rsTime+'~'+query.reTime);
			setFeedbackTime(query.feedbackTime);
			setRespLoad(query.respLoad);
			setRespSubsidy(query.respSubsidy);
			setRespType(query.respType==1?'削峰响应':'填谷响应');
			setRespLevel(renderRespLevel(query.respLevel));
			setProfit(Number(query.profit).toFixed(2));
			setEndDate(props.location.state.endDate1);
			setStartDate(props.location.state.startDate1)
		}else{
			history.push('/History')
		}
	},[])
	
	const renderRespLevel = (text) => {
		if(text ==1){
			return '日前响应'
		}else if(text ==2){
			return '小时响应'
		}else if(text ==3){
			return '分钟响应'
		}else if(text ==4){
			return '秒级响应'
		}
	}

	useEffect(() =>{
		if(respId){
			getDeviceListByRespId()
		}
		
	},[page,respId])
	// 获取详情列表
	const getDeviceListByRespId =() =>{
		http.post('demand_resp/resp_history/getDeviceListByRespId',{
			"deviceName": deviceName,
			"deviceRatedPowerSort": deviceRatedPowerSort,
			"deviceStatusSort": deviceStatusSort,
			"number": page,
			"pageSize": 10,
			"respId": respId,
			"sid": "",
			"strategyId": strategyId
		}).then(res =>{
			console.log(res)
			if(res.data.code=200){
				if(res.data.data){
					setDataSource(res.data.data.content);
					setTotal(res.data.data.totalElements)
				}
				
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	const Historya =()=>{
		history.push({
			pathname: '/History',
			state:{
				startDate2:startDate,
				endDate2:endDate
				
			},
			
		})
	}
		const columns = [
			{
				title: '序号',
				dataIndex: 'name',
				key: 'name',
				render:(value, item, index) => (page - 1) * 10 + index+1,
			},
			{
				title: '节点',
				dataIndex: 'nodeName',
				key: 'nodeName',
			},
			{
				title: '户号',
				dataIndex: 'noHouseholds',
				key: 'noHouseholds',
			},
			{
				title: '申报负荷(kW)',
				dataIndex: 'declareLoad',
				key: 'declareLoad',
				render:(value, item, index) =>{
					if(value==null||value==undefined||value===""||value=='-'){
						return '-'
					}else{
						return Number(value).toFixed(2)
					}
					
				}
			},
			
			{
				title: '收益(元)',
				dataIndex: 'profit',
				key: 'profit',
				render:(value, item, index) =>{
					if(value==null||value==undefined||value===""||value=='-'){
						return '-'
					}else{
						return Number(value).toFixed(2)
					}
					
				}
			},
			
			{
				title: '状态',
				dataIndex: 'drsStatus',
				key: 'drsStatus',
				render: (text,record,_,action) =>{
					return text==11?<span>未申报</span>:text==12?<span>执行中未申报</span>:text==15?<span>已结束未申报</span>:text==21?<span>待出清已申报</span>:text==22?<span>出清成功已申报</span>:
					text== 23?<span>出清失败已申报</span>:text==24?<span>执行中已申报</span>:text==25?<span>已结束已申报</span>:''
					
				}
			},
		];
		const handlePagination = page => {
			setPage(page);
		};
		return (
			<div className="detail">
				<div className="header">
					<span onClick={Historya} href="#"><img src={require('../../../style/xu/return.png')} />返回</span>
				</div>
				<div className="detailbody">
					<h4>任务编码：{taskCode}</h4>
					<ul className="detailul">
						<li>响应时段：{rsDate}</li>
						<li>响应类型：{respType}</li>
						<li>响应级别：{respLevel}</li>
						<li>响应补贴(元/kWh)：{respSubsidy}</li>
						<li>收益(元)：{profit}</li>
					</ul>
					<Table dataSource={dataSource} columns={columns}
						pagination={
							{
								total: total,//数据的总条数
								defaultCurrent: 1,//默认当前的页数
								defaultPageSize: 10,//默认每页的条数
								showQuickJumper:false,
								onChange: (page)=>handlePagination(page),
							}
						}
					/>
				</div>
			</div>
		)
	}


export default Historydetail