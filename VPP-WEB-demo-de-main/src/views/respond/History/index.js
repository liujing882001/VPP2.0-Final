import React,{useEffect,useState} from 'react'
import { Table ,DatePicker ,ConfigProvider,Modal,Input,Button,message } from 'antd';
import { Link,useHistory  } from "react-router-dom"

import './index.css'
import dayjs from 'dayjs';
import http from '../../../server/server.js'



const { RangePicker } = DatePicker;
const { Search } = Input;


const History = (props) =>{
	const [currentState, setCurrentState] = useState(2);
	const [dataSource, setDataSource] = useState([]);
	const [startDate, setStartDate] = useState(dayjs().startOf('year').format('YYYY-MM-DD'));
	const [endDate, setEndDate] = useState(dayjs().format('YYYY-MM-DD'));
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [lookdataSource, setLookdataSource] = useState([]);
	const [loading, setLoading] = useState(false);
	const [page, setPage] = useState(1);
	const [totaltab, setTotaltab] = useState('');
	const [feedbackTimeSort, setFeedbackTimeSort] = useState('');
	const [profitSort, setProfitSort] = useState('');
	const [respLevelSort, setRespLevelSort] = useState('');
	const [respSubsidySort, setRespSubsidySort] = useState('');
	const [respTypeSort, setRespTypeSort] = useState('');
	const [rsTimeSort, setRsTimeSort] = useState(2);
	const [currentNum, setCurrentNum] = useState(1);
	const [isFirst,setIsFirst] = useState(false);
	const history = useHistory()
	  console.log(props)
	useEffect(() =>{
		if(props.location.state){
			let query = props.location.state
			setEndDate(query.endDate2);
			setStartDate(query.startDate2)
			setIsFirst(true)
		}else{
			setIsFirst(false)
		}
	},[])
	useEffect(() =>{
		if(props.location.state){
			if(isFirst){
				getASHistoryList()
			}
			
		}else{
			if(!isFirst){
				getASHistoryList()
					
			}
		}
		
	},[page,currentNum,rsTimeSort,respTypeSort,isFirst])
	// 查询历史记录列表
	const getASHistoryList =() =>{
		setLoading(true)
		http.post('demand_resp/resp_history/getTaskList',{
			number:page,
			pageSize:10,
			startDate:startDate,
			endDate:endDate,
			feedbackTimeSort:feedbackTimeSort,
			profitSort:profitSort,
			respLevelSort:respLevelSort,
			respSubsidySort:respSubsidySort,
			respTypeSort:respTypeSort,
			rsTimeSort:rsTimeSort
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content =  res.data.data.content
				
				setDataSource(content);
				setLoading(false);
				setTotaltab(res.data.data.totalElements)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const needChange =(data,datastring) =>{
		console.log(datastring)
		if(datastring[0]){
			
			setEndDate(datastring[1]);
			setStartDate(datastring[0])
		}else{
			setEndDate('');
			setStartDate('')
		}
		
	}
	// 查看
	const lookover= (e) =>{
		console.log(e)
		debugger
		history.push({
			pathname: '/Historydetail',
			state:{
				detail:e,
				startDate1:startDate,
				endDate1:endDate
			},
			
		})
		setIsFirst(false)
		
	}
	// 搜索
	const onSearch =(val) =>{
		console.log(val)
	}
	// 搜索
	const needSearch =() =>{
		console.log()
		if(startDate&&endDate){
			
			setCurrentNum(1)
			setPage(1)
			getASHistoryList()
		}else{
			message.info('请选择时间范围')
		}
		
	}

		const columns =[
			{
				title: '序号',
				width: 90,
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
					if(record.respType ===1){
						return '削峰响应'
					}else if(record.respType ===2){
						return '填谷响应'
					}
				}
			},
			{
				title: '响应级别',
				dataIndex: 'respLevel',
				key: 'respLevel',
				render: (text,record,_,action) =>{
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
			},
			
			{
				title: '响应补贴（元/kWh）',
				dataIndex: 'respSubsidy',
				key: 'respSubsidy',
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
		// 分页
		const onChangetable = page => {
		    
			setPage(page)
			setCurrentNum(page)
			
		};
		const onChangetab = (pagination, filters, sorter) => {
			console.log(pagination, filters, sorter)
		    
			//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
			// if (sorter.order) {
				if(sorter.columnKey=='rsDate'){
					if (sorter.order) {
						if(sorter.order == "ascend"){
							// 升序
							
							setRsTimeSort(1);
							setRespTypeSort('');
							// getASHistoryList()
						}else if(sorter.order == "descend"){
							// 降序
							setRsTimeSort(2);
							setRespTypeSort('');
							// getASHistoryList()
						}
					}else{
						setRsTimeSort(2);
						setRespTypeSort('');
						// getASHistoryList()
					}
				}
				if(sorter.columnKey=="respType"){
					// 反馈截止排序
					if (sorter.order) {
						//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
						if(sorter.order == "ascend"){
							// 升序
							
							setRsTimeSort('');
							setRespTypeSort(1);
							// getASHistoryList()
						}else if(sorter.order == "descend"){
							// 降序
							setRsTimeSort('');
							setRespTypeSort(2);
							// getASHistoryList()
						}
					}else{
						setRsTimeSort('');
						setRespTypeSort(2);
						// getASHistoryList()
					}
				}	
			// }
			
			
		}
		const dateFormat = 'YYYY-MM-DD';
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current < dayjs(new Date('2023-01-01')) || current > dayjs().endOf('day')
			
		};
		return(
			<div className="historys">
				<div className="" style={{overflow:'hidden'}}>
					 <RangePicker style={{marginRight:20}} onChange={needChange} 
					 disabledDate={disabledDate}
						value={startDate!=''&&endDate!='' ? [dayjs(startDate, dateFormat), dayjs(endDate, dateFormat)] : undefined}
						format={dateFormat}
					/>
					<Button type="primary" onClick={needSearch}>查询</Button>
				</div>
				<div style={{margin:'20px 0px'}}>
					<Table dataSource={dataSource} columns={columns} loading={loading}
						onChange={(pagination, filters, sorter) => { onChangetab(pagination, filters, sorter) }}
						pagination={
							{
								total: totaltab,//数据的总条数
								defaultCurrent: 1,//默认当前的页数
								defaultPageSize: 10,//默认每页的条数
								showSizeChanger:false,
								current:currentNum,
								// showQuickJumper:true,
								onChange:onChangetable
							}
						}
					 />
				</div>
				
			</div>
		)
	}
	


export default History




