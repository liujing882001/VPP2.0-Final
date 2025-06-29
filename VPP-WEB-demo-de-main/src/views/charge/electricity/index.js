import React,{useEffect,useState} from 'react'
import { Space,ConfigProvider,DatePicker,Button,Select,Input ,Table,message,Cascader  } from 'antd';
import * as echarts from 'echarts';
// import echarts from '../../echarts.js';

import http from '../../../server/server.js'
import dayjs from 'dayjs';
import './index.css'
// import http from '../../../server/server.js'
import axios from 'axios'
const { Search } = Input;
const { RangePicker } = DatePicker;
const Electricity= () => {
	const [daylist,setDaylist] = useState(['总数','能耗']);
	const [currentState,setCurrentState] = useState(0);
	const [dayLL,setDayLL] = useState([])
	const [dataSource,setDataSource] = useState([])
	const [load,setLoad] = useState('-')
	const [capacity,setCapacity] = useState('-')
	const [energy,setEnergy] = useState('-')
	const [nowEnergy,setNowEnergy] = useState('-')
	const [type,setType] = useState(0);
	const [stationName,setStationName] = useState('');
	const [page,setPage] = useState(1);
	const [nodeNameList,setNodeNameList] = useState([])
	const [year,setYear] = useState('');
	const [yearMonth,setYearMonth] = useState('');
	const [yearMonthday,setYearMonthday] = useState('');
	const [totalPages,setTotalPages] = useState('');
	const [normal,setNormal] = useState('');
	const [offline,setOffline] = useState('');
	const [construct,setConstruct] = useState('');
	const [loading,setLoading] = useState(false);
	const [startTs,setStartTs] = useState('');
	const [endTs,setEndTs] = useState('');
	const [nodeId,setNodeId] = useState('');
	const [activePower,setActivePower] = useState([]);
	const [timeStamp,setTimeStamp] = useState([]);
	const [keyValue,setKeyValue] = useState('');
	const [total,setTotal] = useState('');
	const [options,setOptions] = useState([]);
	const [currentNum,setCurrentNum] = useState(1);
	const [optionLoading,setOptionLoading] = useState(true);
	const [cascaderValue,setCascaderValue] = useState('');
	const [isEmpty,setIsEmpty] = useState(false);
	const [current,setCurrent] = useState('')
	const [dateType,setDateType] = useState('')
	
	useEffect(() =>{
		tabFn(1)
		pvCount()
		pVList()
		ditpVList()
		pvNodeTree()
		

	},[])
	useEffect(() =>{
		ditpVList()
	},[page,currentState])
	// 日期范围
	const generateTimePoints =(startDate, endDate) =>{
	    const timePoints = [];
	    const currentDate = new Date(startDate);
	    while (currentDate <= endDate) {
	        const daysInMonth = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0).getDate();
	        
	        for (let i = 0; i < 96; i++) {
	            const newDate = new Date(currentDate);
	            newDate.setMinutes(newDate.getMinutes() + i * 15);
	            const timePoint = {
	                activePower: '',
	                timeStamp: newDate.toISOString().slice(0, 16).replace("T", " ")
	            };
	            timePoints.push(timePoint);
	        }
	        
	        if (currentDate.getDate() === daysInMonth) {
	            currentDate.setMonth(currentDate.getMonth() + 1);
	            currentDate.setDate(1);
	        } else {
	            currentDate.setDate(currentDate.getDate() + 1);
	        }
	    }
	
	    return timePoints;
	}
	
	
	// 节点tree/pvNodeTree
	const pvNodeTree = () => {
		
		axios.post('tree/runPvNodeTree').then(res =>{
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
				console.log(data);
				setOptions(data)
				setOptionLoading(false)
				

				const defaultValue = []
				let defalutNodeId = ''

				const setDefaultValue = (data) => {
					data.forEach((item,i) => {
						if(i > 0){
							return
						}

						if(!item.children || !item.children.length){
							if(!defalutNodeId){
								defalutNodeId = item.id
							}
						}

						defaultValue.push(item.key)

						if(item.children && item.children.length){
							setDefaultValue(item.children)
						}else{
							return
						}
					})
				}
				setDefaultValue(data)
				console.log(defaultValue)
				let startDate = ''
				if(new Date().getDate() - 1 < 0){
					startDate = dayjs().startOf('day').subtract(new Date().getDate(), 'd').format('YYYY-MM-DD')
				}else{
					startDate = dayjs().startOf('day').subtract(1, 'd').format('YYYY-MM-DD')
				}
				let length = defaultValue.length
				setStartTs(startDate)
				setEndTs(dayjs().endOf('day').format('YYYY-MM-DD'))
				setCascaderValue(defaultValue)
				setNodeId(defalutNodeId)
				onSearch1(startDate,dayjs().endOf('day').format('YYYY-MM-DD'),defalutNodeId,true)
			}
			
		}).catch(err =>{
			console.log(err)
		})
	}
	// 选择节点
	const treeChange =(val,selectedOptions) =>{
		console.log(val)
		if(val){
			const selectOptions = selectedOptions.map((item) => item.label)
			setCascaderValue(val)
			let length = val.length
			console.log(val[length-1])
			
			setNodeId(val[length-1])
		}else{
			setNodeId('')
			setTimeStamp([])
			setActivePower([])
			setCascaderValue('')
			// weight()
		}
		
	}

	// 选择时间范围
	const dataChange = (date,dateString) =>{
		console.log(dateString)
		setStartTs(dateString[0])
		setEndTs(dateString[1])
	}
	
	// 实时功率
	const pvCount= () => {
		axios.post("load_management/power_generation/pvCount").then(res =>{
			if(res.data.code ==200){
				let data = res.data.data
				setCapacity(data.capacity)
				setEnergy(data.energy)
				setLoad(data.load)
				setNowEnergy(data.nowEnergy)
			}
		}).catch(err =>{
			console.log(err)
		})

	}
	// 表格
	const ditpVList= () => {		
		axios.post("load_management/power_generation/pVList",{
			"number": page,
			"pageSize": 3,
			"stationName": stationName,
			"type": type
		}).then(res => {
			console.log(res)
			if(res.data.code ==200){
				if(res.data.data){
					let content = res.data.data.content
					setDataSource(content)
					setLoading(false)
					setTotal(res.data.data.totalElements)
				}else{
					setDataSource([])
					setLoading(false)
					setTotal(0)
				}
			}else{
				setLoading(false)
				message.info(res.data.msg)
			}
		}).catch(err => {
		    console.log(err)
		})

		
	}
	const tree= () => {
		axios.post('tree/pvForestShortView').then(res =>{
			if(res.data.code ==200){
				setNodeNameList(res.data.data)
			}
		})
		
	}
	// 表格
	const pVList= () => {
		axios.post('load_management/power_generation/pVList',{
			"number": page,
			"pageSize": 10000,
			"stationName": stationName,
			"type": type
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				
				let normal = 0
				let offline = 0
				let construct = 0
				if(res.data.data){
					let content = res.data.data.content
					content.map(res =>{
						if(res.online ==true){
							// normal++
							// 在线
							if(res.isEnabled==false){
								construct++
							}else{
								normal++
							}
							
						}else if(res.online ==false){
							if(res.isEnabled==false){
								construct++
							}else{
								offline++
							}
						}
					})
					console.log(normal)
					
					setTotalPages(res.data.data.totalElements)
					setNormal(normal)
					setOffline(offline)
					setConstruct(construct)
					setLoading(false)
				}else{
					setTotalPages(0)
					setNormal(normal)
					setOffline(offline)
					setConstruct(construct)
					setLoading(false)
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	const tabFn =(index) => {
		console.log(index)
		setCurrent(index)
		setDateType(index)
		setStartTs('')
		setKeyValue(new Date())
	}
	const clsFn=(index, curCls, cls) =>{
		let { current } = this.state;
		// console.log(current)
		return current === index ? curCls : cls;
	}
	const tbafn =(index, curCls, cls) =>{
		// console.log(index)
		let { current } = this.state;
		// console.log(current)
		return current === index ? curCls : cls;
	}

	const onSearch = (value) =>{
		console.log(value);
		setStationName(value)
		setCurrent(1)
		setPage(1)
		setLoading(true)
		axios.post("load_management/power_generation/pVList",{
			"number": 1,
			"pageSize": 100000,
			"stationName": value,
			"type": type
		}).then(res => {
			console.log(res)
			if(res.data.code ==200){
				if(res.data.data){
					let content = res.data.data.content
					setDataSource(content)
					setPage(1)
					setLoading(false)
					setTotal(res.data.data.totalElements)
				}
				
			}
		}).catch(err => {
		    console.log(err)
		})
	}
	// 图表
	const pvChartList= (startTs,endTs,nodeId) => {
		axios.post('load_management/power_generation/pvChartList',{
			"endTs": endTs,
			"nodeId": nodeId,
			"startTs": startTs
		}).then(res =>{
			console.log(res)
			let timeStamp = []
			let activePower = []
			let content = res.data.data
			if(content.length>0){
				const startDate = new Date(startTs);
				const endDate = new Date(endTs);
				const timePointsArray = generateTimePoints(startDate, endDate);
				console.log(timePointsArray);
				let new2TimeStamps = timePointsArray.map(item => item.timeStamp);
				timePointsArray.forEach(item2 => {
					if (!content.find(item1 => item1.timeStamp === item2.timeStamp)) {
						content.push(item2);
					}
				});
				content.sort((a, b) => {
					return new Date(a.timeStamp) - new Date(b.timeStamp);
				});
				
				console.log(content);
				content.length>0 &&content.map(res =>{
					timeStamp.push(res.timeStamp)
					activePower.push(res.activePower)
				})
				setTimeStamp(timeStamp)
				setActivePower(activePower)
				setIsEmpty(content.length>0?true:false)
				weight(timeStamp,activePower,content.length>0?true:false);
			}else{
				weight([],[],false);
			}
			
		}).catch(err =>{
			console.log(err)
		})
	}
	// 搜索
	const onSearch1=(newstartTs,newendTs,newnodeId,isFirst = false) =>{
		if(newstartTs&&newendTs&&newnodeId){
			if(newnodeId &&newstartTs){
				var chartDom = document.getElementById('weight');
				var myChart = echarts.init(chartDom);
				myChart.showLoading({
					text: '数据加载中...',
					color: '#FFF',
					textColor: '#FFF',
					maskColor: 'rgba(255, 255, 255, 0)',
					zlevel: 0
				});
				pvChartList(newstartTs,newendTs,newnodeId)
				
				
			}else{
				if(startTs==""){
					message.info('请选择时间')
				}else{
					message.info('请选择节点')
				}
				
			}
		}else{
			if(nodeId &&startTs){
				var chartDom = document.getElementById('weight');
				var myChart = echarts.init(chartDom);
				myChart.showLoading({
					text: '数据加载中...',
					color: '#FFF',
					textColor: '#FFF',
					maskColor: 'rgba(255, 255, 255, 0)',
					zlevel: 0
				});
				pvChartList(startTs,endTs,nodeId)			
			}else{
				if(!isFirst){
					if(startTs==""){
						message.info('请选择时间')
					}else{
						message.info('请选择节点')
					}
				}
				
				
			}
		}
		
		
	}
	// charts导出
	const devices =() =>{
		if(startTs==""||nodeId==""){
			message.info('时间或节点不能为空')
		}else{
			axios({
				method: 'post',
				url: 'load_management/power_generation/pvChartListExcel',
				responseType: 'arraybuffer',
				data:{
					endTs: endTs,
					nodeId: nodeId,
					startTs: startTs
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
	// 点击离线
	const chosedate =(e) =>{
		console.log(e)
	
		setCurrentState(e)
		setType(e)
		setLoading(true)
		setPage(1)
		setCurrentNum(1)
		// ditpVList()
	}
	// 列表导出
	const derive =() =>{
		axios({
			method: 'post',
			url: 'load_management/power_generation/pVListExcel',
			responseType: 'arraybuffer',
			data: {
				"number": page,
				"pageSize": 1000000,
				"stationName": stationName,
				"type": 0
			}
		}).then(res => {
			if (res.status == 200) {
				const url = window.URL.createObjectURL(new Blob([res.data]));
				const link = document.createElement('a'); //创建a标签
				link.style.display = 'none';
				link.href = url; // 设置a标签路径
				link.download = '光伏资源报表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
				document.body.appendChild(link);
				link.click();
				URL.revokeObjectURL(link.href); // 释放 URL对象
				document.body.removeChild(link);
		
			}
		})
	}
	// 功率
	const weight =(timeStamp,activePower,isEmpty) =>{

		var chartDom = document.getElementById('weight');
		var myChart = echarts.init(chartDom);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		var option;
		option = {
			title: {
			    text: '单位：kW',
				textStyle:{
					fontSize:12,
					color:'#fff',
					fontWeight:100
				},
				left:30,
				top:0
			},
			graphic: {
				// show:false,
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
					    top: "180", // 相对父元素上下的位置
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
				axisPointer: {
					type: 'cross',
					label: {
						backgroundColor: '#6a7985'
					}
				},
				
				formatter(params) {
					// console.log(params)
					var relVal = params[0].name;
					for (var i = 0, l = params.length; i < l; i++) {
						if(params[i].value===''){
							relVal += '<br/>' + params[i].marker + '实时功率：' + ''
						}else{
							relVal += '<br/>' + params[i].marker + '实时功率：' + Number(params[i].value).toFixed(2)
						}
						
					}

					return relVal;
				},
			},

			grid: {
				top:40,
				left: 30,
				right: 30,
				bottom: 0,
				containLabel: true
			},
			xAxis: {
				type: 'category',
				boundaryGap: false,
				axisLabel:{//x坐标轴刻度标签
					show:true,
					color:'#fff',//'#ccc'，设置标签颜色
					formatter: `{value}`
				},
				axisLine: { 
					show: true, // X轴 网格线 颜色类型的修改
					lineStyle: {
						color: '#DFE1E5'
					}  
				 },  
				data: timeStamp,
				// data: ['2022-11-03', '2022-11-05', '2022-11-07', '2022-11-09', '2022-11-11', '2022-11-13', '2022-11-15']
			},
			yAxis: {
				type: 'value',
				splitLine:{
					show:true,
					lineStyle:{
						type:'dashed',
						color:'#DFE1E5'
					}
				},
				axisLabel:{//x坐标轴刻度标签
					show:true,
					color:'#fff',//'#ccc'，设置标签颜色
					formatter: `{value}`
				},
			},
			series: [
				{
					data: activePower,
					type: 'line',
					lineStyle: {
						width: 1,
						color:'#246CF9'
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
								offset: 0, color: 'rgba(77, 115, 239, 0.2)' // 0% 处的颜色
							}, {
								offset: 1, color: 'rgba(77, 115, 239, 0)' // 100% 处的颜色
							}],
							global: false // 缺省为 false
						}
						
					}
				}
			]
		};
		myChart.hideLoading()
		option && myChart.setOption(option);
		
	}
	
	const { Option } = Select;
	// const 
	
	const columns = [
	  {
			 title: '序号',
			 // width: '10%',
			  width: 70,
			 // render:(text,record,index)=> `${index+1}`,
			 render:(value, item, index) => (page - 1) * 3 + index+1,
		   },
	  {
		title: '电站状态',
		dataIndex: 'online',
		key: 'online',
		render: (text,record,_,action) =>{
			// console.log(record)
			
			if(record.online ==true){
				// return '正常'
				if(record.isEnabled==false){
					return '建设中'
				}else{
					return '正常'
				}
			}else if(record.online ==false){
				// return '离线'
				if(record.isEnabled==false){
					return '建设中'
				}else{
					return '离线'
				}
			}
		}
	  },
	  {
		title: '光伏电站名称',
		dataIndex: 'nodeName',
		key: 'nodeName',
	  },
	  {
		title: '当日发电量（kWh）',
		dataIndex: 'nowEnergy',
		key: 'nowEnergy',
		render: (text: string) => <span>{Number(text).toFixed(2)}</span>,
	  },
	  {
		title: '累计发电量（kWh）',
		dataIndex: 'energy',
		key: 'energy',
		render: (text: string) => <span>{Number(text).toFixed(2)}</span>,
	  },
	  {
		title: '实时功率（kW）',
		dataIndex: 'load',
		key: 'load',
		render: (text: string) => <span>{Number(text).toFixed(2)}</span>,
	  },
	  {
		title: '装机容量（kW）',
		dataIndex: 'capacity',
		key: 'capacity',
		render: (text: string) => <span>{Number(text).toFixed(2)}</span>,
	  },
	  {
		title: '更新时间',
		dataIndex: 'ts',
		key: 'ts',
	  },
	];

	// 分页
	const onChange: PaginationProps['onChange'] = page => {
		console.log(page);
		setPage(page.current)
		setCurrentNum(page.current)
		// ditpVList()
	};
	const disabledDate: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023-01-01')) || current > dayjs().endOf('day')
		
	};
	const displayRender = (labels, selectedOptions) => {
	    if (selectedOptions.length === 0) {
	      return '请选择';
	    }
	    return labels[labels.length - 1];
	  };
	return(
		<div className="electricitys">
			<div className="headers1" style={{background:'none',padding:'0px 0px 16px 0px'}}>
				<ul className="tabulation">
					<li>
						<img src={require('../../../style/img/icon5.png')}  />
						<span>实时功率（kW）<br /><b>{load}</b></span>
					</li>
					<li>
						<img src={require('../../../style/img/icon6.png')}  />
						<span>装机容量（kW)<br /><b>{capacity}</b></span>
					</li>
					<li>
						<img src={require('../../../style/img/icon7.png')}  />
						<span>当日发电量（kWh）<br /><b>{nowEnergy}</b></span>
					</li>
					<li>
						<img src={require('../../../style/img/icon8.png')}  />
						<span>累计发电量（kWh）<br /><b>{energy}</b></span>
					</li>
				</ul>
				
			</div>	
			<div className="entire newweight">
				<div className="adjustableheader adgudet">
					
					
						<Cascader style={{width:300,marginRight:16}} loading={optionLoading}
						value={cascaderValue}
						displayRender={(labels, selectedOptions) => labels[labels.length - 1]}
						options={options} onChange={treeChange}  clearIcon={null}  placeholder="请选择" />
					
					<RangePicker disabledDate={disabledDate} onChange={dataChange} value={[startTs && dayjs(startTs, 'YYYY-MM-DD'), endTs && dayjs(endTs, 'YYYY-MM-DD')]}/>
					<Button className="buds" onClick={onSearch1}  type="primary" >查询</Button>
					<Button ghost style={{float:'right'}}  onClick={devices}>导出</Button>
					
					
					
				</div>
				<div id="weight"></div>
				
			</div>
			<div className="totality">
				<div className="total">
					<div className="allbtns" style={{width:'auto'}}>
						<span className={0===currentState?"active":'all'} onClick={()=>chosedate(0)}>总数({totalPages})</span>
						<span className={1===currentState?"active":'all'} onClick={()=>chosedate(1)}>正常({normal})</span>
						
						<span className={3===currentState?"active":'all'} onClick={()=>chosedate(3)}>建设中({construct})</span>
						<span className={2===currentState?"active":'all'} onClick={()=>chosedate(2)}>离线({offline})</span>
						
					</div>
					<Search placeholder="搜索电站名称" onSearch={onSearch} style={{ width: 200 }} />
					<Button ghost onClick={derive} style={{float:'right',marginLeft:'16px'}}>导出</Button>
					
					
				</div>
				<div style={{marginTop:'20px'}}>
					<Table dataSource={dataSource} columns={columns}
					onChange={onChange}
					loading={loading}
					pagination={
						{
							total: total,//数据的总条数
							defaultCurrent: 1,//默认当前的页数
							defaultPageSize: 3,//默认每页的条数
							showSizeChanger:false,
							current:currentNum
						}
					}
					 rowClassName={
					 (record, index) => {
					   let className = ''
					   className = index % 2 ===0 ? 'ou' : 'ji'
					   // console.log(className)
					   return className
						}
					 }
					 />

				</div>
			</div>
		</div>
	)
}
	

export default Electricity




















