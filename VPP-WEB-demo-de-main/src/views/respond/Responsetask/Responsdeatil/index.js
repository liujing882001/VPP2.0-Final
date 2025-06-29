import React,{useEffect,useState} from 'react'
import { Link,useHistory  } from "react-router-dom"
import { Table ,message} from 'antd';
import './index.scss'
import '../ploy/ploy.scss'
import http from '../../../../server/server.js'
const location = null
const Responsdeatil =(props) =>{
	const [taskCode, setTaskCode] = useState('');
	const [feedbackTime, setFeedbackTime] = useState('');
	const [rsDate, setRsDate] = useState('');
	const [respLoad, setRespLoad] = useState('');
	const [respSubsidy, setRespSubsidy] = useState('');
	const [respType, setRespType] = useState('');
	const [respLevel, setRespLevel] = useState('');
	const [content, setContent] = useState([]);
	const [page, setPage] = useState(1);
	const [currentNum, setCurrentNum] = useState(1);
	const [total, setTotal] = useState('');
	const [loading, setLoading] = useState(false);
	const [respId, setRespId] = useState('');
	const history = useHistory()
	
	
	useEffect(() =>{
		
		if(props.location.state){
			const location = props.location.state.query
			setRespId(location.respId);
			setTaskCode(location.taskCode);
			setRsDate(location.rsTime+'~'+location.reTime);
			setFeedbackTime(location.feedbackTime);
			setRespLoad(location.respLoad);
			setRespSubsidy(location.respSubsidy);
			setRespType(location.respType==1?'削峰响应':'填谷响应');
			setRespLevel(location.respLevel==1?'日前响应':location.respLevel==2?'小时响应':location.respLevel==3?'分钟响应':'秒级响应');
		}else{
			history.push('/Responsetask')
		}
		
	},[])
	useEffect(() =>{
		if(respId){
			getPriceList()
		}
	},[respId])
	
	// 获取详情明细
	const getPriceList =() =>{
		setLoading(true)
		http.post('demand_resp/resp_task/getPriceList',{
			respId:respId,
			number:page,
			pageSize:10
			
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setContent(res.data.data.content)
				setTotal(res.data.data.totalElements)
				setLoading(false)
			}
		}).catch(err =>{
			console.log(err)
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
		    title: '时间',
		    dataIndex: 'responseDate',
		    key: 'responseDate',
			render:(value, item, index) =>{
				// console.log(item.responseTime)
				return value+' ' +item.responseTime
			}
			
		  },
		  {
		    title: '负荷需求(kW)',
		    dataIndex: 'totalPower',
		    key: 'totalPower',
			render:(value, item, index) =>{
				if(value==null||value==undefined||value===""||value=='-'){
					return '-'
				}else{
					return Number(value).toFixed(2)
				}
				
			}
		  },
		  {
		    title: '响应补贴(元/kWh)',
		    dataIndex: 'price',
		    key: 'price',
			render:(value, item, index) =>{
				if(value==null||value==undefined||value===""||value=='-'){
					return '-'
				}else{
					return Number(value).toFixed(2)
				}
				
			}
		  },
		];
		const handlePagination =(page, pageSize) =>{
			console.log(page,pageSize)
			
			setPage(page)
			setCurrentNum(page)
			setLoading(true)
			getPriceList()
		}
		return(
			<div className="ploy">
				<div className="header">
					<Link to='/Responsetask'><a href="#"><img src={require('../../../../style/xu/return.png')} />返回</a></Link>
				</div>
				<div className="ploybody">
					<h4>响应任务编号：{taskCode}</h4>
					<ul>
						<li>响应时间： {rsDate}</li>
						<li>反馈截止时间： {feedbackTime}</li>
						<li>负荷需求总值：{respLoad}KW</li>
						<li>响应补贴平均值： {respSubsidy}元/kWh</li>
						<li>响应类型：{respType}</li>
						<li>响应级别：{respLevel}</li>
					</ul>
				</div>
				<div className="detailed">
					<div className="detailedLeft">
						<h4>明细表</h4>
						<div>
							<Table dataSource={content} columns={columns}
								loading={loading}
								pagination={
									{
										total: total,//数据的总条数
										defaultCurrent: 1,//默认当前的页数
										defaultPageSize: 10,//默认每页的条数
										showQuickJumper:false,
										current:currentNum,
										onChange:handlePagination
									}
								}
							/>
						</div>
					</div>
					
				</div>
			</div>
		)
	}


export default Responsdeatil