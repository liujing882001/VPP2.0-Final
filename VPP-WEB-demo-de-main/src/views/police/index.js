import React,{useEffect,useState,useRef } from 'react'
import { Select,DatePicker,ConfigProvider ,Table,Button,Cascader, message,Checkbox} from 'antd';
import type { ColumnsType, TableProps } from 'antd/lib/table';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';

import './index.css'
import http from '../../server/server.js'

interface DataType {
  key: React.Key;
  name: string;
  age: number;
  address: string;
}


const { RangePicker } = DatePicker;

const { Option } = Select;
const dateFormat = 'YYYY-MM-DD';
const Police =() =>{
	

	const [condition, setCondition] = useState([]);
	  const [level, setLevel] = useState([
	    {
	      value: '',
	      severityDesc: '全部'
	    }
	  ]);
	  const [hybrid, setHybrid] = useState([]);
	  const [content, setContent] = useState([]);
	  const [startTs,setStartTs] = useState(dayjs().startOf('month').format('YYYY-MM-DD'));
	  const [endTs, setEndTs] = useState(dayjs().format('YYYY-MM-DD'));
	  const [severity, setSeverity] = useState('');
	  const [status, setStatus] = useState('');
	  const [nodeId, setNodeId] = useState([]);
	  const [page, setPage] = useState(1);
	  const [options, setOptions] = useState([]);
	  const [loading, setLoading] = useState(true);
	  const [currentloading, setCurrentloading] = useState(false);
	  const [total,setTotal] = useState('')
	  const [currentNum,setCurrentNum]= useState(1);
	  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
	  const [nodeName,setNodeName] = useState([]);
	  const [nodeIdList, setNodeIdList] = useState([]);
	  const [selectedValues, setSelectedValues] = useState([]);
	  const [isMaxTagPlaceholderVisible, setIsMaxTagPlaceholderVisible] = useState(false);
	useEffect(() =>{
		getlevel()
		statusList()
		nodeTree()
		
	},[])
	
	// 节点列表tree/nodeTree
	const nodeTree=() =>{
		setCurrentloading(true)
		http.post('stationNode/stationPageQuery',{
			page:1,
			size:100000,
			query:'',
			"keyword" : {
				"stationTypeId" : [],
				"stationState" : ["运营中"]
			}		
		}).then(res =>{
			if(res.data.code==200){
				if(res?.data?.data?.content){
					let nodeList = []
					let nodeNameList = []
					
					res?.data?.data?.content?.map((res,index) =>{
						res.value = res.stationName;
						res.lable = res.stationName;
						if(index===0){
							setNodeName('全部')
							
						}
						nodeList.push(res.stationId)
						
					})
					res?.data?.data?.content?.unshift({value:'全部',label:'全部'})
					setNodeId(nodeList)
					setNodeIdList(nodeList)
					setOptions(res?.data?.data?.content);
					
					
				}
				
				setCurrentloading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	//状态
	const list=() =>{
		setLoading(true)
		if(nodeId.length>0&&startTs&&endTs){
			console.log(status)
			const params = {
			  stationId: nodeId.toString(),
			  ...(status!=="" && { alarmStatus: status }),
			  ...(severity!=="" && { alarmLevel: severity }),
			  startTime: `${startTs} 00:00:00`,
			  endTime: `${endTs} 23:59:59`,
			  pageNumber: page,
			  pageSize: 10
			};
			http.get('alarmInformation/findAlarmWin',{params}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					let content = []
					let data = res.data.data.content;
					setContent(data);
					setTotal(res.data.data.totalElements)
					setLoading(false);
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}else{
			message.info('请选择项目节点和时间范围')
			setLoading(false);
		}
		
	}
	// 告警状态
	const statusList=() =>{
		http.get('alarmInformation/alarmEnum?name='+'status').then(res => {
			console.log(res)
			if(res.data.code ==200){
				let condition = [{
					status:'全部',
					statusDesc:''
				}]
				if(res?.data?.data){
					let data = res.data.data
					const statusArray = Object.values(data);
					statusArray.map((item,index) =>{
						condition.push({
							status:item,
							statusDesc:index
						})
					})
					
					console.log(condition,'------------')
					
				}
				setCondition(condition)
				
				
			}
			
		})
	}
	// 报警等级：
	const getlevel=() =>{
		http.get('alarmInformation/alarmEnum?name='+'level').then(res => {
			if(res.data.code ==200){
				console.log(res.data.data)
				let level = [{
					severityDesc:'全部',
					severity:''
				}]
				if(res?.data?.data){
					let data = res.data.data
					const statusArray = Object.values(data);
					statusArray.map((item,index) =>{
						level.push({
							severityDesc:item,
							severity:index
						})
					})
				}
				console.log(level)
				setLevel(level)
			}
		})
	}
	
	const handleChange =(e,selectedOptions)=>{
		if(e){
			let lastObjectList= e[e.length - 1];
			let lastObject = []
			let newData = []
			if(lastObjectList!=='全部'){
				selectedOptions.map(res =>{
					if(res.value){
						if(res.value!=='全部'){
							newData.push(res.stationId)
							lastObject.push(res.stationName)
						}
					}
				})
				setNodeName(lastObject)
				setNodeId(newData)
			}else{
				setNodeName(['全部'])
				setNodeId(nodeIdList)
			}
			
			
			
			
			
			setPage(1);
			setCurrentNum(1)
		}else{
			setNodeId('')
			setNodeName('')
		}
		
		
	}
	useEffect(() =>{
		if(nodeId.length>0&&startTs&&endTs&&loading){
			list()
		}
	},[nodeId,status,severity,page,currentNum,nodeName])
	
	// 搜索
	const onSearch =()=>{
		if(nodeId.length>0&&startTs&&endTs){
			list()
		}else{
			message.info('请选择节点信息和日期')
		}
		
	}
	const handlelevel=(e) =>{
		setSeverity(e===''?null:e)
		
		setPage(1);
		setCurrentNum(1)
		
	}
	const handlecondition =(e) =>{
		setStatus(e===''?null:e)
		setPage(1);
		setCurrentNum(1)
		
	}
	const chosedate =(value, dateString) =>{
		setStartTs(dateString[0]);
		setEndTs(dateString[1])
		setPage(1);
		setCurrentNum(1)
	}
	
		
	const columns: ColumnsType<DataType> = [
		 {
			title: '序号',
			width: 70,
			render:(value, item, index) => (page - 1) * 10 + index+1
		},
		{
			title: '告警类型',
			dataIndex: 'alarmType',
			render: (text,record) => {
				if(text==null||text==undefined||text===''){
				  return '-'
				}else{
				  return text===0?'设备告警':text===1?'性能告警':'-'
				}
			}
		},
		{
			title: '告警等级',
			dataIndex: 'alarmLevel',
			width: 100,
			render: (text,record) => {
				if(text==null||text==undefined||text===''){
				  return '-'
				}else{
				  return <span style={{color:text==0?'#FF5353':text==1?'#FF991C':
				  text==2?'#2566FF':'#2566FF'
				  }}>{text===0?'故障':text===1?'警告':text===2?'提示':'-'}</span>
				}
			}
		},
		{
			title: '项目节点',
			dataIndex: 'stationName',
		},
		
		{
			title: '系统节点',
			dataIndex: 'nodeName'
		},
		
		{
		  title:"告警内容",
		  dataIndex: 'alarmContext',
		  render: (text,record) => {
		  	if(text==null||text==undefined||text===''){
		  	  return '-'
		  	}else{
		  	  return text
		  	}
		  }
		},
		{
			title: '告警状态',
			dataIndex: 'alarmStatus',
			width:100,
			render: (text,record) => {
				if(text==null||text==undefined||text===''){
				  return '-'
				}else{
				  return <span className={text==0?'policeing':text==1?'handle':
				  text==2?'Recovered':''
				  }>{text===0?'告警中':text===1?'处理中':text===2?'已恢复':'-'}</span>
				}
			}
		},
		{
			title: '开始时间',
			dataIndex: 'startTime',
			render: (text,record) => {
				if(text==null||text==undefined||text===''){
				  return '-'
				}else{
				  return dayjs(text).format('YYYY-MM-DD HH:mm:ss')
				}
			}
		},
		{
			title: '结束时间',
			dataIndex: 'endTime',
			render: (text,record) => {
				if(text==null||text==undefined||text===''){
				  return '-'
				}else{
				  return dayjs(text).format('YYYY-MM-DD HH:mm:ss')
				}
			}
		},
		{
			title: '持续时长',
			dataIndex: 'alarmDurationTime',
			render: (text,record) => {
				if(text==null||text==undefined||text===''){
				  return '-'
				}else{
				  return text
				}
			}
		},
		
		// {
		// 	title: '操作',
		// 	dataIndex: 'caozuo'
		// }
	];
	
	
	
	
	const onChange =(page) =>{
		setPage(page.current);
		setCurrentNum(page.current);
		setLoading(true)
	}
	 const rowSelection = {
	    selectedRowKeys
	  };

	const startOfMonth = dayjs().startOf('month');
	const today = dayjs();
	
	const disabledDate = (current) => {
	    const isBeforeStartOfMonth = current.isBefore(startOfMonth);
	    const isAfterToday = current.isAfter(today);
	    return isBeforeStartOfMonth || isAfterToday;
	};
	
	return(
		<div className="allcontent">
			<div className="police">
				<div className="report">
					<div className="report_layout">
						<b>项目节点：</b>
						{
							<Select style={{ width: 270 }} loading={currentloading}
							value={nodeName}
							mode="multiple"
							maxTagCount={1}
							options={options} onChange={handleChange}  placeholder="请选择" />
						}
						
					</div>
					<div className="report_layout">
						<b>告警状态：</b>
						<Select defaultValue="全部"  style={{ width: 150 }} onChange={handlecondition}>
							
							{
								condition.map((item,index) =>{
									return <Option key={index} value={item.statusDesc}>{item.status}</Option>
								})
							}
							
							
						</Select>
					</div>	
					<div className="report_layout">
						<b>告警等级：</b>
						<Select defaultValue="全部"  style={{ width: 150 }} onChange={handlelevel}>
							{
								level.map((item,index) =>{
									return <Option key={index} value={item.severity}>{item.severityDesc}</Option>
								})
							}  
						</Select><br />
					</div>	
					<div className="report_layout">
						<b style={{marginLeft:0}}>时间范围：</b>
						<ConfigProvider locale={locale} >
							<RangePicker
							style={{ width: 240 }}
							// disabledDate={disabledDate} 
							format={dateFormat}
							value={startTs!=''&&endTs!=''? [dayjs(startTs, dateFormat), dayjs(endTs, dateFormat)] : undefined}
							onChange={chosedate} />
						</ConfigProvider>
					</div>	
					
					<Button type="primary" onClick={onSearch}>搜索</Button>
				</div>
				<div style={{marginTop:10}} className="batch_body">
					{
						// <div className="batch_title">
						// 	<Button type="primary">批量确认</Button>
						// </div>
					}
					<Table columns={columns} dataSource={content} 
						onChange={onChange}
						loading={loading}
						showSorterTooltip={false}
						scroll={{ x: 1500 }}
						pagination={
							{
								total: total,//数据的总条数
								defaultCurrent: 1,//默认当前的页数
								defaultPageSize: 10,//默认每页的条数
								showSizeChanger:false,
								current:currentNum
							}
						}
					/>
				</div>
			</div>
		</div>
	)
}
	


export default Police