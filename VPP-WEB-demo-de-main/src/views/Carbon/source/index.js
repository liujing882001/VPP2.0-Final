import React, { useEffect,useState } from 'react';
import { Tabs,Table,Select,Input,Button,DatePicker,ConfigProvider,Space,Modal,message,Typography } from 'antd';
import {
  InfoCircleOutlined,
  CaretDownOutlined,
  CaretUpOutlined
} from '@ant-design/icons';

import './index.css'
import './source.scss'
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import http from '../../../server/server.js'
const { Search } = Input;
const { Text } = Typography;

const { TabPane } = Tabs;
const { Option } = Select;
const { MonthPicker, RangePicker } = DatePicker;
const dateFormat = 'YYYY-MM-DD';
const monthFormat = 'YYYY-MM';


 
const columns = [
  {
    title: '类型',
    dataIndex: 'dischargeEntity',
    key: 'dischargeEntity',
  },
  {
    title: '数量',
    dataIndex: 'num',
    key: 'num',
	render: (s, record, index) =>{
		console.log(record)
		return s?Number(s).toFixed(1):'-'
	}
  },
  {
    title: '单位',
    dataIndex: 'unit',
    key: 'unit',
	render: (text,record,_,action) =>{
		if( text==null||text==undefined){
			return '-'
		}else if(text!==''){
			// console.log(text)
			return text
		}else{
			return text
		}
	}
  },
  {
    title: '碳排放因子',
    dataIndex: 'factor',
    key: 'factor',
  },
  {
    title: 'CO2排放量（t）',
    dataIndex: 'emission',
    key: 'emission',
	render: (text,record,_,action) =>{
		if( text==null||text==undefined){
			return '-'
		}else if(text!==''){
			// console.log(text)
			return Number(text).toFixed(2)
		}else{
			return Number(text).toFixed(2)
		}
	}
  },
  {
    title: '燃烧源',
    dataIndex: 'dischargeType',
    key: 'dischargeType',
	render: (text, record, index) =>{
		console.log(record)
		if( text===null||text===undefined||text===""){
			return '-'
		}else{
			return text==1?'固定燃烧源':text==2?'移动燃烧源':text==3?'逸散型排放源':''
		}
		
	}

  },
];
const Source =() =>{
	const [display, setDisplay] = useState('none');
	const [isToggleOn, setIsToggleOn] = useState(true);
	const [nodeId, setNodeId] = useState('');
	const [nodeList, setNodeList] = useState([]);
	const [currentUnit, setCurrentUnit] = useState('请选择');
	const [scopeType, setScopeType] = useState('1');
	const [startTime, setStartTime] = useState('');
	const [endTime, setEndTime] = useState('');
	const [content, setContent] = useState([]);
	const [loading, setLoading] = useState(false);
	const [currentloading, setCurrentLoading] = useState(false);

	useEffect(() =>{
		nodeList1()
	},[])
	// 获取节点列表
	useEffect(() =>{
		setDisplay(isToggleOn ? 'none' : 'block')
	},[isToggleOn])
	const shrink =() =>{
		setIsToggleOn(!isToggleOn)
	}
	const onChange=() =>{
		
	}
	const onSearch =(e) =>{
		
	}
	// 获取节点列表
	const nodeList1 =() =>{
		// 节点
		setCurrentLoading(true)
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				setNodeList(res.data.data);
				setCurrentLoading(false)
			}
		})
	}
	// 查询碳溯源列表
	const getTraceabilityList =() =>{
		setLoading(true)
		http.post('carbon/traceability/getTraceabilityList',{
			"endTime":endTime,
			"nodeId": nodeId,
			"scopeType": scopeType,
			"startTime": startTime
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setContent(res.data.data);
				setLoading(false)
			}else{
				message.info(res.data.msg)
				setLoading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const onMonth =(val,mode) =>{
		console.log(mode);
		setStartTime(mode[0]);
		setEndTime(mode[1])
	}
	const search =() =>{
		if(startTime&&startTime&&nodeId){
			getTraceabilityList()
		}else{
			message.info('请选择时间和节点')
		}
		
	}
	// 节点
	const handlepoint =(val) =>{
		console.log(val)
		setNodeId(val)
	}
	// 范围
	const handlepoint1 =(val) =>{
		console.log(val)
		setScopeType(val)
	}
	
	const disabledDate: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023-01')) || current > dayjs().endOf('month')
	};
	return(
		<div className="allcontent12" style={{background:'#212029',padding:16,borderRadius:10}}>
		
			<div className="sourceheader">
				<h4><InfoCircleOutlined style={{color:'#0092FF'}} />排放源说明<b className={display =='none'?'no':''} onClick={shrink}>
					{
						display =='none'?<CaretDownOutlined />:<CaretUpOutlined />
					}
					
					
				</b></h4>
				<div className="imgdiv" style={{display: display}}>
					<img src={require('./img/bg.png')}  />
				</div>
			</div>
			<div className="header">
				节点：
				<Select
					style={{ width: 217,marginRight:16 }}
					loading={currentloading}
					onChange={handlepoint}
					defaultValue={currentUnit}
					key={currentUnit}>
					{
						nodeList&&nodeList.map(res =>{
							return <Option key={res.id} value={res.id}>{res.nodeName}</Option>
						})
					}
				</Select>
				范围：
				<Select
				  defaultValue="1"
				  style={{ width: 217,marginRight:16}}
				  onChange={handlepoint1}
				  options={[
					{
					  value: '1',
					  label: '范围一',
					},
					{
					  value: '2',
					  label: '范围二',
					},
					
					{
					  value: '3',
					  label: '范围三',
					},
				  ]}
				/>
				<Space>
					<ConfigProvider locale={locale}>
						<RangePicker disabledDate={disabledDate} picker="month" onChange={onMonth}  />
					</ConfigProvider>
				</Space>
				<Button type="primary" onClick={search} style={{marginLeft:'24px'}}>查询</Button>
		</div>
			<div style={{marginTop:20}}>
				<Table dataSource={content} columns={columns} 
					pagination={false} 
					loading={loading}
					rowClassName={
						(record, index) => {
						  let className = ''
						  className = index % 2 ===0 ? 'ou' : 'ji'
						  // console.log(className)
						  return className
						}
					  }
					summary={pageData => {
						// console.log(pageData)
						let totalBorrow = 0;
						let totalRepayment = 0;
				  
						pageData.forEach(({ emission }) => {
							console.log(emission)
							if(emission){
								totalBorrow += Number(emission);
							}
							
							// totalRepayment += repayment;
						});
				  
						return (
							<>
								<Table.Summary.Row>
									<Table.Summary.Cell index={0}>合计</Table.Summary.Cell>
									<Table.Summary.Cell index={1}>
									</Table.Summary.Cell>
									<Table.Summary.Cell index={2}>
									</Table.Summary.Cell>
									<Table.Summary.Cell index={3}>
									</Table.Summary.Cell>
									<Table.Summary.Cell index={4}>
										<Text>{totalBorrow.toFixed(2)}</Text>
									</Table.Summary.Cell>
									<Table.Summary.Cell index={5}>
									</Table.Summary.Cell>
								</Table.Summary.Row>
								
							</>
						);
					}}
				/>
			</div>
		</div>
	)
}
	


export default Source