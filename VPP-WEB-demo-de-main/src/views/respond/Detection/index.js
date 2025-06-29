import React,{useEffect,useState} from 'react'
import { Table ,DatePicker ,ConfigProvider,Modal,Input,Button } from 'antd';
import { Link ,useHistory } from "react-router-dom"

import './index.css'
import http from '../../../server/server.js'
import telegram from '../../system/model/accumulation_model/telegram';
const { Search } = Input;

const Detection =() =>{
	const [currentState, setCurrentState] = useState(2);
	  const [dataSource, setDataSource] = useState([]);
	  const [isModalVisible, setIsModalVisible] = useState(false);
	  const [lookdataSource, setLookdataSource] = useState([]);
	  const [loading, setLoading] = useState(false);
	  const [loading1, setLoading1] = useState(false);
	  const [page, setPage] = useState(1);
	  const [total, setTotal] = useState('');
	  const [feedbackTimeSort, setFeedbackTimeSort] = useState('');
	  const [profitSort, setProfitSort] = useState('');
	  const [respLevelSort, setRespLevelSort] = useState('');
	  const [respSubsidySort, setRespSubsidySort] = useState('');
	  const [respTypeSort, setRespTypeSort] = useState('');
	  const [rsTimeSort, setRsTimeSort] = useState(1);
	  const history = useHistory()
	
	useEffect(() =>{
		getASMonitorList()
	},[page,rsTimeSort,respTypeSort,respSubsidySort,respLevelSort])
	const chosedate =(e) =>{
		console.log(e)
		
		setCurrentState(e)
		setLoading(true);
		getASMonitorList()
	}
	// 查询实时监测列表
	const getASMonitorList =() =>{
		setLoading1(true)
		http.post('demand_resp/resp_monitor/getRespMonitorList',{
			"endDate": "",
			"feedbackTimeSort": feedbackTimeSort,	//反馈截止
			"number": page,
			"pageSize": 10,
			"profitSort": profitSort,	//收益排序
			"respLevelSort": respLevelSort,	//收益排序
			"respSubsidySort": respSubsidySort,	//响应补贴排序
			"respTypeSort": respTypeSort,	//响应类型排序
			"rsTimeSort": rsTimeSort,	//响应时段排序
			"startDate": ""
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data.content
				// data.sort(function(a, b) {
				//     return b.taskCode< a.taskCode? -1 : 1
				// })
				
				setDataSource(data)
				setLoading1(false);
				setTotal(res.data.data.totalElements)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const lookover =(e) =>{
		console.log(e)
		history.push({
			pathname: '/detail',
			query:e
		})
		
	}
	// 搜索
	const onSearch =(val) =>{
		console.log(val)
	}
		
		const columns =[
			{
				title: '序号',
				width: '8%',
				// render:(text,record,index)=> `${index+1}`
				render:(value, item, index) => (page - 1) * 10 + index+1,
			},
			{
				title: '任务编码',
				dataIndex: 'taskCode',
				key: 'taskCode',
			},
			{
				title: '响应时段',
				dataIndex: 'rsDate',
				key: 'rsDate',
				sorter: true,
				render: (s, record, index) =>{
					// console.log(record)
					return record.rsTime+'~' +record.reTime
				}
			},
			{
				title: '响应类型',
				dataIndex: 'respType',
				key: 'respType',
				sorter: true,
				render: (text,record,_,action) =>{
					return text==1?'削峰响应':'填谷响应'
				}
			},
			{
				title: '响应级别',
				dataIndex: 'respLevel',
				key: 'respLevel',
				sorter: true,
				render: (text,record,_,action) =>{
					return text==1?'日前响应':text==2?'小时响应':text==3?'分钟响应':'秒级响应'
				}
			},
			{
				title: '响应补贴(元/kWh)',
				dataIndex: 'respSubsidy',
				key: 'respSubsidy',
				sorter: true,
				render: (text,record,_,action) =>{
					if( text==null||text==undefined){
						return '-'
					}else if(text!==''){
						return text
					}else{
						return text
					}
				}
			},
			{
				title: '详情',
				key: 'action',
				render: (text,record,_,action) =>{
					return <a onClick={() => lookover(record)}>查看</a>
				}
			},
		]
		const handleOk = () => {
			setIsModalVisible(false)
		};
		
		const handleCancel = () => {
		    setIsModalVisible(false);
		};
		
		
		const onChange = (pagination, filters, sorter) => {
			console.log(pagination, filters, sorter)
		    
			//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
			// if (sorter.order) {
				if(sorter.columnKey=='rsDate'){
								// 响应时段排序
					if (sorter.order) {
						//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
						if(sorter.order == "ascend"){
							// 升序
							
							setRsTimeSort(1)
							setRespTypeSort('')
							setRespLevelSort('');
							setRespSubsidySort('');
							// getASMonitorList()
						}else if(sorter.order == "descend"){
							// 降序
							
							setRsTimeSort(2)
							setRespTypeSort('')
							setRespLevelSort('');
							setRespSubsidySort('');
							// getASMonitorList()
						}
					}else{
						
						setRsTimeSort(1)
						setRespTypeSort('')
						setRespLevelSort('');
						setRespSubsidySort('');
						// getASMonitorList()
					}
				}
				if(sorter.columnKey=='respType'){
					// 响应类型排序
					if (sorter.order) {
						//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
						if(sorter.order == "ascend"){
							// 升序
						
							setRsTimeSort('')
							setRespTypeSort(1)
							setRespLevelSort('');
							setRespSubsidySort('');
							// getASMonitorList()
						}else if(sorter.order == "descend"){
							// 降序
							
							setRsTimeSort('')
							setRespTypeSort(2)
							setRespLevelSort('');
							setRespSubsidySort('');
							// getASMonitorList()
						}
					}else{
					
						setRsTimeSort('')
						setRespTypeSort(1)
						setRespLevelSort('');
						setRespSubsidySort('');
						// getASMonitorList()
					}
				}	
				if(sorter.columnKey=='respLevel'){
					// 响应级别排序
					if (sorter.order) {
						//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
						if(sorter.order == "ascend"){
							// 升序
							
							setRsTimeSort('')
							setRespTypeSort('')
							setRespLevelSort(1);
							setRespSubsidySort('');
							// getASMonitorList()
						}else if(sorter.order == "descend"){
							// 降序
							
							setRsTimeSort('')
							setRespTypeSort('')
							setRespLevelSort(2);
							setRespSubsidySort('');
							// getASMonitorList()
						}
					}else{
						
						setRsTimeSort('')
						setRespTypeSort('')
						setRespLevelSort(1);
						setRespSubsidySort('');
						// getASMonitorList()
					}
				}
				if(sorter.columnKey=="respSubsidy"){
					// 响应级别排序
					if (sorter.order) {
						//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
						if(sorter.order == "ascend"){
							// 升序
							
							setRsTimeSort('')
							setRespTypeSort('')
							setRespLevelSort('');
							setRespSubsidySort(1);
							// getASMonitorList()
						}else if(sorter.order == "descend"){
							// 降序
							
							setRsTimeSort('')
							setRespTypeSort('')
							setRespLevelSort('');
							setRespSubsidySort(2);
							// getASMonitorList()
						}
					}else{
						
						setRsTimeSort('')
						setRespTypeSort('')
						setRespLevelSort('');
						setRespSubsidySort(1);
						// getASMonitorList()
					}
				}
			// }
		}
		// 分页
		const handlePagination = page => {
			setPage(page);
		};
		return (
			<div className="dections">
				<div className="alltablsefg">
					<Table dataSource={dataSource} loading={loading1} 
					
						columns={columns} 
						onChange={(pagination, filters, sorter) => { onChange(pagination, filters, sorter) }}
						pagination={
							{
								total: total,//数据的总条数
								defaultCurrent: 1,//默认当前的页数
								defaultPageSize: 10,//默认每页的条数
								showQuickJumper:true,	//跳转
								onChange: (page) =>handlePagination(page),							
							}
						}
					/>
				</div>
				
			</div>
		)
	
	
}

export default Detection