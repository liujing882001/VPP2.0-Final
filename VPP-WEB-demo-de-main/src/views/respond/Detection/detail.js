import React,{useEffect,useState} from 'react'
import { Link ,useHistory } from "react-router-dom"
import { Select, Space,Table,Button,Input,message } from 'antd';
import './index.css'
import './detail.scss'
import http from '../../../server/server.js'
const Detail =(props) =>{
	const [respId, setRespId] = useState('');
	const [deviceName, setDeviceName] = useState('');
	const [deviceRatedPowerSort, setDeviceRatedPowerSort] = useState(1);
	const [deviceStatusSort, setDeviceStatusSort] = useState('');
	const [strategyId, setStrategyId] = useState('');
	const [taskCode, setTaskCode] = useState('');
	const [respType, setRespType] = useState('');
	const [respLevel, setRespLevel] = useState('');
	const [respSubsidy, setRespSubsidy] = useState('');
	const [rsDate,setRsDate] = useState('');
	const [loading,setLoading] =  useState(false);
	const [dataSource,setDataSource] = useState([]);
	const [total,setTotal] = useState('');
	const [page,setPage] = useState(1);
	const [rsTimeSort,setRsTimeSort] = useState(1);
	const [feedbackTimeSort,setFeedbackTimeSort] = useState('');
	const history = useHistory()
	useEffect(() =>{
		if(props.location.query){
			// 编辑
			const location = props.location.query
			console.log(location)
			setRespId(location.respId);
			setTaskCode(location.taskCode);
			setRsDate(location.rsDate+' '+location.rsTime+'~'+location.reTime);
			setRespType(location.respType==1?'削峰响应':'填谷响应');
			setRespLevel(location.respLevel==1?'日前响应':location.respLevel==2?'小时响应':location.respLevel==3?'分钟响应':'秒级响应');
			setRespSubsidy(location.respSubsidy)
		}else{
			history.push('/Detection')
		}
	},[])
	useEffect(() =>{
		if(respId){
			getDeviceListByRespId()
		}
		
	},[respId,taskCode,respType,respLevel,respSubsidy,page,feedbackTimeSort,])
	// 获取详情列表
	const getDeviceListByRespId =() =>{
		setLoading(true)
		http.post('demand_resp/resp_monitor/getDeviceListByRespId',{
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
			if(res.data.code==200){
				if(res.data.data){
					setDataSource(res.data.data.content);
					setTotal(res.data.data.totalElements)
					setLoading(false)
				}else{
					
					setDataSource(res.data.data)
					setLoading(false)
				}
				
			}else{
				
				setLoading(false)
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	

		const columns = [
			{
				title: '序号',
				dataIndex: 'number',
				key: 'number',
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
				// sorter: true,
			},
			{
				title: '基线负荷(kW)',
				dataIndex: 'baseLoad',
				key: 'baseLoad',
				render:(value, item, index) =>{
					if(value==null||value==undefined||value===""||value=='-'){
						return '-'
					}else{
						return Number(value).toFixed(2)
					}
					
				}
			},
			{
				title: '实时负荷(kW)',
				dataIndex: 'nowLoad',
				key: 'nowLoad',
				render:(value, item, index) =>{
					if(value==null||value==undefined||value===""||value=='-'){
						return '-'
					}else{
						return Number(value).toFixed(2)
					}
					
				}
				// sorter: true,
			},
			// {
			// 	title: '实时响应负荷(kW)',
			// 	dataIndex: 'realTimeLoad',
			// 	key: 'realTimeLoad',
			// 	// sorter: true,
			// },
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
		const handlePagination =(page) =>{
			console.log(page)
			
			setPage(page);
			// getDeviceListByRespId()
		}
		// 
		const onChangetab = (pagination, filters, sorter) => {
			console.log(sorter)
			if (sorter.order) {
				if(sorter.columnKey=='declareLoad'){
					if (sorter.order) {
						//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
						if(sorter.order == "ascend"){
							// 升序
							
							setRsTimeSort(1);
							setFeedbackTimeSort('');
							getDeviceListByRespId()
						}else if(sorter.order == "descend"){
							// 降序
							
							setRsTimeSort(2);
							setFeedbackTimeSort('');
							getDeviceListByRespId()
						}
					}
				}
				if(sorter.columnKey=='feedbackTime'){
					// 反馈截止排序
					if (sorter.order) {
						//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
						if(sorter.order == "ascend"){
							// 升序
							
							setRsTimeSort('');
							setFeedbackTimeSort(1);
							getDeviceListByRespId()
						}else if(sorter.order == "descend"){
							// 降序
							
							setRsTimeSort('');
							setFeedbackTimeSort(2);
							getDeviceListByRespId()
						}
					}
				}	
			}
		}
		return (
			<div className="detail">
				<div className="header">
					<Link to='/Detection'><a href="#"><img src={require('../../../style/xu/return.png')} />返回</a></Link>
				</div>
				<div className="detailbody">
					<h4>任务编码：{taskCode}</h4>
					<ul className="detailul">
						<li>响应时段：{rsDate}</li>
						<li>响应类型：{respType}</li>
						<li>响应级别：{respLevel}</li>
						<li>响应补贴(元/kWh)：{respSubsidy}</li>
					</ul>
					<Table dataSource={dataSource} columns={columns} loading={loading}
						onChange={(pagination, filters, sorter) => { onChangetab(pagination, filters, sorter) }}
						pagination={
							{
								total: total,//数据的总条数
								defaultCurrent: 1,//默认当前的页数
								defaultPageSize: 10,//默认每页的条数
								showSizeChanger:false,
								// onChange: handlePagination,
								showQuickJumper:true,
								
								onChange: (page) =>handlePagination(page),			
							}
						}
					 />
				</div>
			</div>
		)
	}


export default Detail