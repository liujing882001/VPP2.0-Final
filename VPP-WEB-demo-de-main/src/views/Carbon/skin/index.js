import React, { useEffect,useState } from 'react';
import { Link } from "react-router-dom";

import { Tabs,Table,Select,Input,DatePicker,ConfigProvider,
Button,Space,Spin,message  } from 'antd';
import {
  InfoCircleOutlined,
  
} from '@ant-design/icons';
import './index.css'
import './skin.scss'

import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import http from '../../../server/server.js'
import { createHashHistory } from "history";
 
 // import http from '../../server/server.js'
 const history = createHashHistory();
const { Search } = Input;
const { RangePicker } = DatePicker;
const { Option } = Select;

const dateFormat = 'YYYY-MM-DD'; // 定义你需要的时间格式
const monthFormat = 'YYYY-MM'; // 定义你需要的时间格式
// 获取前一天的时间
const Skin =(props) =>{
	const [nodeList, setNodeList] = useState([]);
	const [currentUnit, setCurrentUnit] = useState('请选择');
	const [nodeId, setNodeId] = useState('请选择');
	const [endTime, setEndTime] = useState('');
	const [startTime, setStartTime] = useState('');
	const [total, setTotal] = useState('-');
	const [greenArea, setGreenArea] = useState('-');
	const [greenEmissions, setGreenEmissions] = useState('-');
	const [treeNum, setTreeNum] = useState('-');
	const [treeEmissions, setTreeEmissions] = useState('-');
	const [setLoading, setSetLoading] = useState(false);
	const [nodeName, setNodeName] = useState('');
	const [currentloading, setCurrentloading] = useState(false);
	const [isFirst,setIsFirst] = useState(false);
	
	useEffect(() =>{
		getnodeList()

	},[])
	useEffect(() =>{
		if(props.location.query){
			if(isFirst){
				getCarbonSink()
			}
			
		}
		
	},[isFirst])
	const onSearch =(e) =>{
		
	}

	// 获取节点列表
	const getnodeList=() =>{
		// 节点
		setCurrentloading(true)
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				
				if(props.location.query){
					let query =props.location.query
					console.log(query)
					setEndTime(query.endTime);
					setStartTime(query.startTime);
					setNodeId(query.nodeId)
					setIsFirst(true)
				}
				setNodeList(res.data.data);
				setCurrentUnit(res.data.data[0].id);
				setCurrentloading(false)
			}
		})
	}
	// 查询碳汇信息
	const getCarbonSink=() =>{
		setSetLoading(true)
		http.post('carbon/sink/getCarbonSink',{
			"ctype": "",
			"endTime": endTime,
			"nodeId": nodeId,
			"number": 1,
			"pageSize": 10,
			"startTime": startTime,
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				if(data){
					setTotal(Number(data.total).toFixed(2));
					setGreenArea(Number(data.greenArea).toFixed(2));
					setGreenEmissions(Number(data.greenEmissions).toFixed(2));
					setTreeNum(Number(data.treeNum).toFixed(2));
					setTreeEmissions(Number(data.treeEmissions).toFixed(2));
					setSetLoading(false)
				}else{
					setTotal('-');
					setGreenArea('-');
					setGreenEmissions('-');
					setTreeNum('-');
					setTreeEmissions('-');
					setSetLoading(false)
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 选择月份
	const hanleMonth =(val,mode) =>{
		console.log(mode)
		setStartTime(mode[0]);
		setEndTime(mode[1])
	}
	// sousuo
	const search =() =>{
		if(endTime&&startTime){
			getCarbonSink()
		}else{
			message.info('请选择时间')
		}
		
	}
	// 选择节点
	const handlepoint =(val) =>{
		console.log(val)
		setNodeId(val);
		setCurrentUnit(val)
	}
	const area = () =>{
		if(endTime&&startTime&&nodeId){
			props.history.push({
				pathname: '/area',
				state: {
					nodeId: nodeId,
					title:'绿化面积',
					startTime:startTime,
					endTime:endTime								  
				}
			});
		}else if(endTime==''){
			message.info('请选择时间')
		}else if(nodeId==''){
			message.info('请选择节点')
		}

	}
	const area1 =() =>{
		if(endTime&&startTime&&nodeId){
			props.history.push({
				pathname: '/area',
				state: {
					nodeId: nodeId,
					title:'种植树木',
					startTime:startTime,
					endTime:endTime								  
				}
			});
		}else if(endTime==''){
			message.info('请选择时间')
		}else if(nodeId==''){
			message.info('请选择节点')
		}
	}

	const disabledDate: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023-01')) || current > dayjs().endOf('month')
	};
	
	const areaquery = {
		pathname: '/home/user/area',
		state: '绿化面积'
	}
	const dateFormat = 'YYYY-MM';
	return(
		<div className="skin">
			<div className="header" >
					节点：
					<Select
						defaultValue={currentUnit}
						style={{ width: 217 }}
						onChange={handlepoint}
						
						loading={currentloading}
						value={nodeId}
					>
						{
							nodeList.length&&nodeList.map(res =>{
								return <Option key={res.id} value={res.id}>{res.nodeName}</Option>
							})
						}
					</Select>
					
					<Space>
						<ConfigProvider locale={locale}>
							<RangePicker onChange={hanleMonth} 
							disabledDate={disabledDate}
							value={startTime!=''&&endTime!=''? [dayjs(startTime, dateFormat), dayjs(endTime, dateFormat)] : undefined}
							format={dateFormat}
							picker="month"  />
						</ConfigProvider>
					</Space>
					<Button type="primary" onClick={search} style={{marginLeft:'24px'}}>查询</Button>
			</div>
			<div className="skin">
				<Spin spinning={setLoading}>
				<div className="allskin">
					<div className="skinbg">总</div>
					<p>总吸收碳排放量  </p>
					<span>{total}t</span>
				</div>
				<div className="skinarea">
					<div className="wider">
						<h4>绿化面积
							<Button ghost onClick={area}>查看详情</Button>
						</h4>
						<div className="afforest">
							<div>
								<img src={require('../../../style/damao/tree.png')}  />
								<p>{greenArea}㎡</p>
								<span>逐月累计绿化面积</span>
							</div>
							<div></div>
							<div>
								<img src={require('../../../style/damao/tan1.png')}  />
								<p>{greenEmissions}t</p>
								<span>吸收的碳排放量</span>
							</div>
						</div>
						<div style={{textAlign:'center',paddingTop:'20px'}}>
							
							
						</div>
					</div>
					<div className="wider">
						<h4>种植树木
							<Button ghost onClick={area1}>查看详情</Button>
						</h4>
						<div className="afforest">
							<div>
								<img src={require('../../../style/damao/tree.png')}  />
								<p>{treeNum}棵</p>
								<span>逐月累计种植树木</span>
							</div>
							<div></div>
							<div>
								<img src={require('../../../style/damao/tan1.png')}  />
								<p>{treeEmissions}t</p>
								<span>吸收的碳排放量</span>
							</div>
						</div>
						<div style={{textAlign:'center',paddingTop:'20px'}}>
							
						</div>
					</div>
				</div>
				</Spin>
			</div>
		</div>
	)
}
	
export default Skin




// <div className="powerheader skinheader" style={{background:'#09183F',margin:'0px 10px 19px 0px'}}>
// 	<Select defaultValue="商汤大厦" style={{ width: 150 }} onChange={this.handleChange}>
// 		<Option value="jack">商汤大厦</Option>
// 		<Option value="lucy">中航技</Option>
// 		<Option value="Yiminghe">储能电站</Option>
// 	</Select>
// 	<ConfigProvider  locale={locale} >
// 		<RangePicker disabledDate={disabledDate} style={{float:'right',marginRight:30}}  />
		
// 	</ConfigProvider>
// </div>























