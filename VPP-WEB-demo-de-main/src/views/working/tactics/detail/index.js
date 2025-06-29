import React,{useEffect,useState} from 'react'
import './detail.scss'
import { Link ,useHistory } from "react-router-dom"
import { Space, Table, Tag,Input  } from 'antd';
import http from '../../../../server/server.js'

const { Search } = Input;
const Tdetail =(props) =>{
	const [index, setIndex] = useState(2);
	const [dataSource, setDataSource] = useState([]);
	const [strategyId, setStrategyId] = useState('');
	const [deviceNum, setDeviceNum] = useState('');
	const [nodeNumber, setNodeNumber] = useState('');
	const [realPower, setRealPower] = useState('');
	const [ratedPower, setRatedPower] = useState('');
	const [page, setPage] = useState(1);
	const [total, setTotal] = useState('');
	const [total1, setTotal1] = useState('');
	const [on, setOn] = useState('');
	const [off, setOff] = useState('');
	const [online, setOnline] = useState(1); //1在线，0离线
	const [deviceName, setDeviceName] = useState('');
	const [loading, setLoading] = useState(false);
	const [currentNum,setCurrentNum] = useState(1);
	const history = useHistory()
	useEffect(() =>{
		if(props.location.state){
			const location = props.location.state
			setStrategyId(location.strategyId)
		}else{
			history.push('/tactics')
		}
	},[])
	useEffect(() =>{
		if(strategyId){
			strategyDetail()
			strategyCalculate()
			strategyDeviceDetailAllNum()
			strategyDeviceDetailAll()
		}
	},[strategyId])
	useEffect(() =>{
		if(index==2){
			// 全部
			strategyDeviceDetailAll()
		}else{
			strategyDeviceDetailOn()
		}
	},[page,currentNum,index,online,deviceName])
	// 详情
	const strategyDetail =() =>{
		http.post('run_schedule/run_strategy/strategyDetail?strategyId='+strategyId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 数量
	const strategyCalculate=() =>{
		http.post('run_schedule/run_strategy/strategyCalculate?strategyId=' +strategyId).then(res =>{
			if(res.data.code==200){
				let data = res.data.data
				setDeviceNum(data.deviceNumber);
				setNodeNumber(data.nodeNumber);
				setRealPower(data.realPower);
				setRatedPower(data.ratedPower);
			}
		})
	}
	// 设备
	const strategyDeviceDetailAllNum=() =>{
		const paramsList = new URLSearchParams() // 切记不要let paramsList = {type: 1, userList: userList}
		paramsList.append('number', 1) // 参数1为参数名，参数2为参数内容
		paramsList.append('pageSize', 1000)
		paramsList.append('strategyId', strategyId)
		paramsList.append('deviceName', deviceName)
		setLoading(true)
		http.post('run_schedule/run_strategy/strategyDeviceDetailAll',paramsList).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data.content
				let on = 0
				let off = 0
				for(var i=0;i<data.length;i++){
					if(data[i].online===true){
						on += 1
					}else{
						off += 1
					}
				}
				setOn(on);
				setOff(off);
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const strategyDeviceDetailAll=() =>{
		const paramsList = new URLSearchParams() // 切记不要let paramsList = {type: 1, userList: userList}
		paramsList.append('number', page) // 参数1为参数名，参数2为参数内容
		paramsList.append('pageSize', 10)
		paramsList.append('strategyId', strategyId)
		paramsList.append('deviceName', deviceName)
		setLoading(true)
		http.post('run_schedule/run_strategy/strategyDeviceDetailAll',paramsList).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data.content
				let on = 0
				let off = 0
				for(var i=0;i<data.length;i++){
					if(data[i].online===true){
						on += 1
					}else{
						off += 1
					}
				}
				// setOn(on);
				// setOff(off);
				setTotal1(res.data.data.totalElements);
				setDataSource(res.data.data.content)
				setLoading(false)
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 在线
	const strategyDeviceDetailOn=() =>{
		setLoading(true)
		const paramsList = new URLSearchParams() // 切记不要let paramsList = {type: 1, userList: userList}
		paramsList.append('number', page) // 参数1为参数名，参数2为参数内容
		paramsList.append('pageSize', 10)
		paramsList.append('strategyId', strategyId)
		paramsList.append('online', online)
		paramsList.append('deviceName', deviceName)
		http.post('run_schedule/run_strategy/strategyDeviceDetailOn',paramsList).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setDataSource(res.data.data.content);
				setTotal(res.data.data.totalElements);
				setLoading(false);
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	const pattern =(e) =>{
		console.log(e)
		setIndex(e);
		setOnline(e)
		setPage(1)
		setCurrentNum(1)
	}
	// 搜索
	const onSearch =(val) =>{
		console.log(val)
		setDeviceName(val)
		
	}
	
	
	const columns = [
		{
			title: '序号',
			dataIndex: 'name',
			key: 'name',
			render:(value, item, index) => (page - 1) * 2 + index+1,
		},
		{
			title: '节点',
			dataIndex: 'nodeName',
			key: 'nodeName',
		},
		{
			title: '设备编号',
			dataIndex: 'deviceSn',
			key: 'deviceSn',
		},
		{
			title: '设备名称',
			dataIndex: 'deviceName',
			key: 'deviceName',
		},
		{
			title: '设备型号',
			dataIndex: 'deviceModel',
			key: 'deviceModel',
		},
		{
			title: '额定负荷',
			dataIndex: 'deviceRatedPower',
			key: 'deviceRatedPower',
		},
		// {
		// 	title: '实时负荷',
		// 	dataIndex: 'deviceRealPower',
		// 	key: 'deviceRealPower',
		// },
		{
			title: '设备品牌',
			dataIndex: 'deviceBrand',
			key: 'deviceBrand',
		},
		{
			title: '更新时间',
			dataIndex: 'updateTime',
			key: 'updateTime',
		},
		{
			title: '设备状态',
			dataIndex: 'online',
			key: 'online',
			render: (text,record,_,action) =>{
				return <a style={{'color':text==true?'#44D7B6':'#6D7278'}}>{text==true?'在线':'离线'}</a>
			}
		},
	]
	// 分页
	const handlePagination =(page) =>{
		console.log(page)
		console.log(online)
		setPage(page);
		setCurrentNum(page)
		
	}
	return(
		<div className="Tdetail">
			<div className="header">
				<Link to='/tactics'><a href="#"><img src={require('../../../../style/xu/return.png')} />返回</a></Link>
			</div>
			<div className="Tdetailtitle">
				<div>
					<p>节点总数（个）
						<span>{nodeNumber}</span>
					</p>
				</div>
				<div>
					<p>设备总数(个)
						<span>{deviceNum}</span>
					</p>
				</div>
				<div>
					<p>额定负荷(kW)
						<span>{ratedPower}</span>
					</p>
				</div>
				<div>
					<p>实时负荷(kW)
						<span>{realPower}</span>
					</p>
				</div>
			</div>
			<div className="Tdetailbody">
				<div className="conditions">
					<ul style={{float:'left'}}>
						<li onClick={() => pattern(2)} className={index == 2 ? 'active' : null}>总数({total1})</li>
						<li onClick={() => pattern(1)} className={index == 1 ? 'active' : null}>在线({on})</li>
						<li onClick={() => pattern(0)} className={index == 0 ? 'active' : null}>离线({off})</li>
					</ul>
					<Search placeholder="搜索相关信息" onSearch={onSearch} style={{ width: 200,float:'right' }} />
				</div>
				<Table dataSource={dataSource} columns={columns} 
					pagination={
						{
							total: index==2?total1:total,//数据的总条数
							defaultCurrent: 1,//默认当前的页数
							defaultPageSize: 10,//默认每页的条数
							showSizeChanger:false,
							onChange: handlePagination,
							current:currentNum
						}
					}
					rowKey={index}
				/>

			</div>
		</div>
	)
}



export default Tdetail