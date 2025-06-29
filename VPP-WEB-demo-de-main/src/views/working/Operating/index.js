import React,{useEffect,useState} from 'react'
import { Select,Table,Button,Space,Modal,DatePicker ,ConfigProvider,Cascader  } from 'antd'
import './index.css'
import './index.scss'
import http from '../../../server/server.js'
import axios from 'axios'
import {
  AreaChartOutlined
} from '@ant-design/icons';
import * as echarts from 'echarts';
// import echarts  from '../../echarts.js'
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
const { Option } = Select;
const { RangePicker } = DatePicker;
const Operating= () => {	
	const [nodeNameList,setNodeNameList] = useState([]);
	const [nodeSystemList,setNodeSystemList] = useState([]);
	const [nodeId,setNodeId] = useState('');
	const [systemId,setSystemId] = useState('');
	const [deviceList,setDeviceList] = useState([]);
	const [dataSource,setDataSource] = useState([]);
	const [deviceId,setDeviceId] = useState('');
	const [page,setPage] = useState(1);
	const [sysvalue,setSysvalue] = useState('请选择系统');
	const [devicevalue,setDevicevalue] = useState('请选择设备');
	const [isModalVisible,setIsModalVisible] = useState(false);
	const [endTs,setEndTs] = useState('');
	const [pointSn,setPointSn] = useState('');
	const [startTs,setStartTs] = useState('');
	const [dayL,setDayL] = useState([]);
	const [timevalue,setTimevalue] = useState([]);
	const [loading,setLoading] = useState(false);
	const [selloading,setSelloading] = useState(false);
	const [options,setOptions] = useState([]);
	const [total,setTotal] = useState('');
	const [currentNum,setCurrentNum] = useState(1);
	const [optionsloading,setOptionsloading] = useState(false);
	const [isEmpty,setIsEmpty] = useState(false);
	useEffect(() =>{
		searchlist()
		nodeTree()
	},[]);
	useEffect(() =>{
		if(isModalVisible==true){
			weight()
		}
		
	},[dayL,timevalue,isEmpty]);
	
	// 查看
	const examine =(e) =>{
		console.log(e)
		setIsModalVisible(true)
		setPointSn(e.pointSn)
	}
	// 节点列表tree/nodeTree
	const nodeTree = () =>{
		setOptionsloading(true)
		http.post('tree/nodeTree').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let count = 0;
				const main = function(data) {
				    for (let i in data) {
				        data[i].label = data[i].title;
						data[i].value = data[i].id;
						
				        if (data[i].children) {
				            count++;
				            main(data[i].children);
				        }
				    }
				}
				main(data);				
				setOptions(data)
				setOptionsloading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 节点
	const getnodeNameList=() =>{
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setNodeNameList(res.data.data)
				setSelloading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 系统
	const handleChange =(e) =>{
		console.log(e)
		if(e){
			setNodeId(e[e.length-1])
			setNodeSystemList([])
			setDeviceList([])
			setPage(1);
			setCurrentNum(1)
			http.post('system_management/node_model/nodeSystemList?nodeId=' +e[e.length-1]).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					setSysvalue('请选择系统');
					setNodeSystemList(res.data.data);
					setDevicevalue('请选择设备')
					setDeviceId('')
					setSystemId('')
				}
			}).catch(err =>{
				console.log(err)
			})
		}else{
			setNodeId('');
			setNodeSystemList([]);
			setDeviceList([]);
			setPage(1);
			setDevicevalue('');
			setCurrentNum(1)
			http.post('system_management/node_model/nodeSystemList?nodeId=' +'').then(res =>{
				console.log(res)
				if(res.data.code ==200){
					setSysvalue('请选择系统');
					setNodeSystemList(res.data.data);
					setDevicevalue('请选择设备')
					setDeviceId('')
					setSystemId('')
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
		
	}
	// 设备
	const sysChange =(e) =>{
		console.log(e)
		setSysvalue(e);
		setDeviceList([]);
		setSystemId(e);
		setPage(1)
		const params = new URLSearchParams();
		params.append('nodeId', nodeId);
		params.append('systemId', e);
		params.append('number', 1);
		params.append('pageSize', 10000000);
		
		http.post('system_management/device_model/deviceListPageable',params).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data.content
				setDeviceList(res.data.data.content);
				setLoading(false);
				setSystemId(e);
				setDevicevalue('请选择设备');
				setDeviceId('')
			}
		})
	}
	// 选择设备
	const deviceChange =(e) =>{
		console.log(e)
		setDeviceId(e)
		setDevicevalue(e)
		setPage(1)
	}
	// 查询
	const searchlist = () =>{
		setLoading(true)
		setCurrentNum(1)
		setPage(1)
		http.post('platFrom/latestCollectDevicesData',{
			"deviceId": deviceId,
			"nodeId": nodeId,
			"number": page,
			"pageSize": 10,
			"systemId": systemId
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setDataSource(res.data.data.content.length &&res.data.data.content)
				setLoading(false)
				setTotal(res.data.data.totalElements)
			}
		}).catch(err =>{
			console.log(err)
		})
		
	}
	// 选择时间
	const Changedate =(date,dataString) =>{
		console.log(dataString)
		setEndTs(dataString[1]);
		setStartTs(dataString[0])
	}
	// 查询图表
	const polling =() =>{
		// document.getElementById('weight').innerHTML = ""
		if(startTs){
			var chartDom = document.getElementById('weight');
			
			var myChart = echarts.init(chartDom);
			myChart.clear()
			myChart.showLoading({
				text: '数据加载中...',
				color: '#FFF',
				textColor: '#FFF',
				maskColor: 'rgba(255, 255, 255, 0)',
				zlevel: 0
			});
			http.post('platFrom/pointDataList',{
				"endTs": endTs,
				"pointSn": pointSn,
				"startTs": startTs
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let data = res.data.data
					let dayL = []
					let timevalue =[]
					data.map(res =>{
						dayL.push(res.timeStamp)
						timevalue.push(res.value)
					})
					
					setDayL(dayL);
					setTimevalue(timevalue);
					setIsEmpty(data.length>0?true:false)
					// weight()
				}
			}).catch(err =>{
				console.log(err)
			})
		}
	}
	// 导出
	const leading =() =>{
		if(startTs){
			axios({
				method: 'post',
				url: 'platFrom/pointDataListExcel',
				responseType: 'arraybuffer',
				data:{
						"endTs": endTs,
						"pointSn": pointSn,
						"startTs": startTs
				}
			}).then(res =>{
				console.log(res)
				if(res.status ==200){
					const url = window.URL.createObjectURL(new Blob([res.data]));
					const link = document.createElement('a'); //创建a标签
					link.style.display = 'none';
					link.href = url; // 设置a标签路径
					link.download = '报表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
					document.body.appendChild(link);
					link.click();
					URL.revokeObjectURL(link.href); // 释放 URL对象
					document.body.removeChild(link);
					
				}
			})
		}
	}
	// 查看图表
	const weight=() =>{
		var chartDom = document.getElementById('weight');
		var myChart = echarts.init(chartDom);
		
		var option;
		option = {
			color:["#0092FF"],
			graphic: {
			    elements: [
					{
						type: 'image',
						z: 100,
						left: 'center',
						top: 'middle',
						style: {
							image:require('../../../style/damao/empty.png')  ,
							// image: 'https://example.com/image.jpg',
							width: 100,
							height: 100
						},
						invisible:isEmpty
					},
					{
					    type: "text",
					    left: "center", // 相对父元素居中
					    top: "260", // 相对父元素上下的位置
						z: 100,
					    style: {
							fill: '#FFF',
							fontSize: 12,
					        text: "暂无数据",
					    },
						invisible:isEmpty
					},
			
				]
			},
			tooltip: {
			    trigger: 'axis',
				backgroundColor: '#302F39',
				borderColor: 'transparent',
				textStyle: {
					color: '#fff' // 设置 tooltip 的文字颜色为白色
				},
				formatter: function (params) {
				  // console.log(params,1234567)
				  // console.log(unit[params[0].dataIndex],1234567)
				  // return [
				  //       '时间: ' + unit[params[0].dataIndex],
				  //        name + ':' + params[0].data + eu,
				  //     ].join('<br/>')
							
				  // console.log(this.time,1234567)
				  // console.log(this.setData.timeData,1234567)
				  // let newdata = this.setData.timeData;
				  // let str ='时间: ' + unit[params[0].dataIndex] + "<br />" ;
					let str = params[0].marker+params[0].axisValue + "</br>"
				  params.forEach((item) => {
					  // console.log(item)
						str+= (parseFloat(item.data).toFixed(2))+ "</br>" 
				  });
				  return str;
				},
			},
			grid: {
				top:40,
				left: 40,
				right: 30,
				bottom: 66,
				containLabel: true
			},
			xAxis: {
				type: 'category',
				boundaryGap: false,
				axisLabel:{//x坐标轴刻度标签
					show:true,
					color:'#FFF',//'#ccc'，设置标签颜色
					formatter: `{value}`
				},
				axisLine: { 
					show: true, // X轴 网格线 颜色类型的修改
					lineStyle: {
						color: '#2A2B40'
					}  
				 },  
				 data:dayL
				// data: ['2022-11-03', '2022-11-05', '2022-11-07', '2022-11-09', '2022-11-11', '2022-11-13', '2022-11-15']
			},
			yAxis: {
				type: 'value',
				splitLine:{
					show:true,
					lineStyle:{
						type:'dashed',
						color:'rgba(255, 255, 255, 0.2)'
					}
				},
				axisLabel:{//x坐标轴刻度标签
					show:true,
					color:'#FFF',//'#ccc'，设置标签颜色
					formatter: `{value}`
				},
			},
			dataZoom: [
				{
					type: 'inside',
					start: 0,
					end: 10
				},
				{
					start: 0,
					end: 10
				}
			],
			series: [
				{
					data: timevalue,
					type: 'line',
					lineStyle: {
						width: 2,
						color:'#0092FF'
					},
					stack: 'Total',
					smooth: true,
					showSymbol: false,
					areaStyle: {
						color: {
							type: 'linear',
							x: 0,
							y: 0,
							x2: 0,
							y2: 1,
							colorStops: [{
								offset: 0, color: 'rgba(0, 146, 255, 1)' // 0% 处的颜色
							}, {
								offset: 1, color: 'rgba(0, 146, 255, 0)' // 100% 处的颜色
							}],
							global: false // 缺省为 false
						}
						
					}
				}
			]
		};
		myChart.hideLoading()
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
	};
	
	
		const columns =[
			{
				title: '序号',
				dataIndex: 'name',
				key: 'name',
				width:80,
				render:(value, item, index) => (page - 1) * 10 + index+1,
			},
			{
				title: '设备名称',
				dataIndex: 'deviceName',
				key: 'deviceName',
				// width:150,
				// render: text => <a>{text}</a>,
			},
			{
				title: '节点名称',
				dataIndex: 'nodeName',
				key: 'nodeName',
				// width:300,
				// render: text => <a>{text}</a>,
			},
			{
				title: '系统名称',
				dataIndex: 'systemName',
				key: 'systemName',
				// width:150,
				render: (s, record, index) =>{
					return s?s:'-'
				}
				// render: text => <a>{text}</a>,
			},
			   
			{
				title: '点位名称',
				dataIndex: 'pointName',
				key: 'pointName',
				// width:150,
				render: (s, record, index) =>{
					return s?s:'-'
				}
				// render: text => <a>{text}</a>,
			},
			{
				title: '点位值',
				dataIndex: 'pointValue',
				key: 'pointValue',
				// width:120,
				render: (s, record, index) =>{
					if(s=='On'||s=='Off'){
						return s
					}else{
						return s?Number(s).toFixed(2):'-'
					}
					
				}
				// render: text => <a>{text}</a>,
			},
			{
				title: '点位编码',
				dataIndex: 'pointSn',
				key: 'pointSn',
				// width:280,
				render: (s, record, index) =>{
					return s?s:'-'
				}
				// render: text => <a>{text}</a>,
			},
			{
				title: '数据点单位',
				dataIndex: 'pointUnit',
				key: 'pointUnit',
				// width:120,
				render: (s, record, index) =>{
					return s?s:'-'
				}
				// render: text => <a>{text}</a>,
			},
			{
				title: '参数键名',
				dataIndex: 'pointDesc',
				key: 'pointDesc',
				// width:150,
				render: (s, record, index) =>{
					return s?s:'-'
				}
				// render: text => <a>{text}</a>,
			},
			{
				title: '采集时间',
				dataIndex: 'ts',
				key: 'ts',
				// width:230,
				render: (s, record, index) =>{
					return s?s:'-'
				}
			},
			{
				title: '设备状态',
				dataIndex: 'online',
				key: 'online',
				// width:100,
				render: (text,record) =>{
					return record.online==false?'离线':'在线'
				}
			},
			{
				title: '操作',
				dataIndex: 'action',
				key: 'action',
				fixed: 'right',
				// width:100,
				render: (text,record,_,action) =>{
					return 	<Space size="middle">
					
								<a onClick={() =>examine(record)}><AreaChartOutlined />查看</a>
							</Space>
				}
			},
		]
		
		
		const handleOk = () => {
			setIsModalVisible(false)
			setStartTs('');
			setEndTs('')
			var chartDom = document.getElementById('weight');
			var myChart = echarts.init(chartDom);
			myChart.clear()
		};
		
		const handleCancel = () => {
			
			// var chartDom = document.getElementById('weight');
		    
			setIsModalVisible(false);
			setStartTs('');
			setEndTs('');
			var chartDom = document.getElementById('weight');
			var myChart = echarts.init(chartDom);
			myChart.clear()
		};
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current && current > dayjs().endOf('day');
		};
		const onChangepage = (page, pageSize) =>{
			console.log(page)
			setPage(page);
			setCurrentNum(page);
			setLoading(true)
			http.post('platFrom/latestCollectDevicesData',{
				"deviceId": deviceId,
				"nodeId": nodeId,
				"number": page,
				"pageSize": 10,
				"systemId": systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					setDataSource(res.data.data.content.length &&res.data.data.content);
					setLoading(false);
					setTotal(res.data.data.totalElements)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		const dateFormat = 'YYYY-MM-DD';
		return(
			<div className="allcontent12 Operating" style={{padding:'0px 16px'}} locale={locale}>
				<div className="headeropern">
					<b>节点：</b>
					<Cascader loading={optionsloading} style={{ width: 300 }} options={options} 
					displayRender={(labels, selectedOptions) => labels[labels.length - 1]}
					onChange={handleChange}  placeholder="请选择节点" />
					
					
					<b style={{marginLeft:16}}>系统：</b>
					<Select value={sysvalue} placeholder="请选择系统" 
					style={{ width: 150,color:sysvalue=='请选择系统'?'#8F959E':sysvalue=='全部'?'#2A2B40':'#2A2B40' }} onChange={sysChange}>
						<Option key='id' value=''>全部</Option>
						{
							nodeSystemList.length &&nodeSystemList.map(item =>{
								return <Option key={item.id} value={item.id}>{item.systemName}</Option>
							})
						}
					</Select>
					<b style={{marginLeft:16}}>设备：</b>
					<Select value={devicevalue} placeholder="请选择设备" 
					style={{ width: 150,color:devicevalue=='请选择设备'?'#8F959E':devicevalue=='全部'?'#2A2B40':'#2A2B40' }} onChange={deviceChange}>
					   <Option key='id' value=''>全部</Option>
					   {
					    	deviceList.length &&deviceList.map(item =>{
					    		return <Option key={item.deviceId} value={item.deviceId}>{item.deviceName}</Option>
					    	})
					    }
					</Select>
					<Button style={{marginLeft:16}} type="primary" onClick={searchlist}>查询</Button>
				</div>
				<Table dataSource={dataSource}  columns={columns} loading={loading}
					scroll={{ x: 2200 }}
					className="custom-table"
					pagination={
						{
							total: total,//数据的总条数
							defaultCurrent: 1,//默认当前的页数
							defaultPageSize: 10,//默认每页的条数
							showSizeChanger:false,
							current:currentNum,
							onChange: onChangepage,
						}
					}
				 />
				<Modal title="数据点图" 
					width={700}
					visible={isModalVisible} 
					className="Operating-modal"
					footer={[
					
						<Button onClick={handleCancel} ghost>取消</Button>,
						<Button key="submit" type="primary" onClick={handleOk}>确定</Button> ]}
					onOk={handleOk} onCancel={handleCancel}>
					<div>
					<ConfigProvider locale={locale}>
						<RangePicker style={{background:'none'}} onChange={Changedate} 
						value={startTs!=''&&endTs!='' ? [dayjs(startTs, dateFormat), dayjs(endTs, dateFormat)] : undefined}
						format={dateFormat}
						disabledDate={disabledDate} />
										
					</ConfigProvider>
						
						<Button style={{marginLeft:20}} 
						type="primary" onClick={polling}>查询</Button>
						<Button
							style={{float:'right'}} 
						type="primary" onClick={leading}>导出</Button>
					</div>
					<div id="weight"></div>
				</Modal>
			</div>
		)
	}
	


export default Operating




