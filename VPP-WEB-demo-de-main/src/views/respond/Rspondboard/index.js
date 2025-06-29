import React,{useEffect,useState} from 'react'
import { Table ,DatePicker ,ConfigProvider,Cascader,Select,Button,message,Modal  } from 'antd';
import * as echarts from 'echarts';
// import echarts  from '../../echarts.js'

import dayjs from 'dayjs';
import http from '../../../server/server.js'
import './index.css'
import { InfoCircleOutlined } from '@ant-design/icons';
const { RangePicker } = DatePicker;
const { SHOW_CHILD } = Cascader;

interface Option {
  value: string | number;
  label: string;
  children?: Option[];
}
const dateFormat = 'YYYY-MM-DD';



const Rspondboard= () => {
	const [dataSource,setDataSource] = useState([]);
	const [startDate,setStartDate] = useState('2023-01-01');
	const [endDate,setEndDate] = useState(dayjs().format('YYYY-MM-DD'));
	const [actualCutPower,setActualCutPower] = useState('');
	const [avgActualLoad,setAvgActualLoad] = useState('');
	const [avgCutPower,setAvgCutPower] = useState('');
	const [avgCutPowerRate,setAvgCutPowerRate] = useState('');
	const [avgLoad,setAvgLoad] = useState('');
	const [avgPartakeLoad,setAvgPartakeLoad] = useState('');
	const [avgProfit,setAvgProfit] = useState('-');
	const [demandNum,setDemandNum] = useState('-');
	const [list,setList] = useState([]);
	const [totalCutPower,setTotalCutPower] = useState('-');
	const [totalProfit,setTotalProfit] = useState('');
	const [Xlist,setXlist] = useState([]);
	const [baseLoad,setBaseLoad] = useState([]);
	const [actualLoad,setActualLoad] = useState([]);
	const [pagetotal,setPagetotal] = useState(1);
	const [feedbackTimeSort,setFeedbackTimeSort] = useState('');
	const [profitSort,setProfitSort] = useState(1);
	const [respLevelSort,setRespLevelSort] = useState('');
	const [respSubsidySort,setRespSubsidySort] = useState('');
	const [respTypeSort,setRespTypeSort] = useState('');
	const [rsTimeSort,setRsTimeSort] = useState('');
	const [total,setTotal] = useState('');
	const [loading,setLoading] = useState(false);
	const [endDates,setEndDates] = useState(dayjs().format('YYYY-MM-DD'));
	const [startDates,setStartDates] = useState('2023-01-01');
	const [actualComplianceRate,setActualComplianceRate] = useState('');
	const [avgDeclareLoad,setAvgDeclareLoad] = useState('-');
	const [avgPlatformProfit,setAvgPlatformProfit] = useState('-');
	const [avgUserProfit,setAvgUserProfit] = useState('-');
	const [platformProfit,setPlatformProfit] = useState('-');
	const [totalCutProfit,setTotalCutProfit] = useState('-');
	const [totalFillPower,setTotalFillPower] = useState('-');
	const [totalFillProfit,setTotalFillProfit] = useState('-');
	const [totalVolumeProfit,setTotalVolumeProfit] = useState('-');
	const [userProfit,setUserProfit] = useState('-');
	const [nodeList,setNodeList] = useState([]);
	const [endTs,setEndTs] = useState(dayjs().subtract(1, 'days'));
	const [startTs,setStartTs] = useState(dayjs().subtract(1, 'days'));
	const [nodeId,setNodeId] = useState('');
	const [realValue,setRealValue] = useState([]);
	const [baselineLoadValue,setBaselineLoadValue] = useState([]);
	const [timeStamp,setTimeStamp] = useState([]);
	const [currentNum,setCurrentNum] = useState(1);
	const [panier,setPanier] = useState([]);
	const [visible,setVisible] = useState(false);
	const [baselineDate,setBaselineDate] = useState('');
	const [loadNumber,setLoadNumber] = useState('');
	const [isEmpty,setIsEmpty] = useState(false);
	const [page,setPage] = useState(1)
	const [isFirstLoad,setIsFirstLoad] = useState(true)
	
	useEffect(() =>{
		getTaskAndStrategyList()
		getDemandStatistics()
		line()
		getnodeList()
		getLoadCount()
		
	},[])
	
	// useEffect(() =>{
	// 	if(nodeId&&startTs&&endTs){
	// 		// alert(nodeId)
	// 		respond()
	// 	}
		
	// },[nodeList,nodeId])
	 useEffect(() => {
	    if (isFirstLoad) {
	      console.log('Component is first loaded');
		  // alert(nodeId)
		  if(nodeId){
			  respond()
		  }
	       // 设置isFirstLoad为false，表示不再是首次加载
	    }
	}, [isFirstLoad,nodeList,nodeId]);
	useEffect(() =>{
		line()
	},[realValue,baselineLoadValue,timeStamp,isEmpty])
	// 可调负荷
	const getLoadCount = () =>{
		http.post('flexible_resource_management/main/getLoadCount').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setLoadNumber(res.data.data.loadNumber)
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 获取节点列表
	const getnodeList = () =>{
		// http.post('carbon/collectionModel/nodeList',{
		http.post('system_management/node_model/nodeNameList',{
			
		}).then(res =>{
			console.log(res)
	
			if(res.data.code==200){
				setNodeList(res.data.data)
				setNodeId(res.data.data.length>0?res.data.data[0].id:'')	
				setIsFirstLoad(false)			
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 查询需求响应任务及策略列表
	const getTaskAndStrategyList = () =>{
		setLoading(true)
		http.post('demand_resp/resp_board/getTaskAndStrategyList',{
			"endDate": endDate,
			"feedbackTimeSort": feedbackTimeSort,
			"number": pagetotal,
			"pageSize": 5,
			"profitSort": profitSort,
			"respLevelSort": respLevelSort,
			"respSubsidySort": respSubsidySort,
			"respTypeSort": respTypeSort,
			"rsTimeSort": rsTimeSort,
			"startDate": startDate
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				setDataSource(content)
				setTotal(res.data.data.totalElements)
				setLoading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 日期
	const getDay =(startDate, endDate) =>{
		var result = new Array();
		var ab = startDate.split("-")
		console.log(ab)
		var ae = endDate.split("-")
		var db = new Date()
		db.setUTCFullYear(ab[0], ab[1]-1, ab[2])
		var de = new Date()
		de.setUTCFullYear(ae[0], ae[1]-1, ae[2])
		var unixDb=db.getTime()
		var unixDe=de.getTime()
		for(var k=unixDb;k<=unixDe;){
			result.push(formatDate(new Date(parseInt(k)), 'yyyy-MM-dd'))
			k=k+24*60*60*1000
		}
		console.log(result)
		return result
	}
	// 日期格式化
	const formatDate  =(date, fmt) =>{
	  if (date === "" || date === null || date === undefined) {
	    return null;
	  }
	  if (fmt === "" || fmt === null || fmt === undefined) {
	    fmt = "yyyy-MM";
	  }
	  date = new Date(date);
	  var o = {
	    "M+": date.getMonth() + 1, // 月份
	    "d+": date.getDate(), // 日
	    "h+": date.getHours(), // 小时
	    "m+": date.getMinutes(), // 分
	    "s+": date.getSeconds(), // 秒
	    "q+": Math.floor((date.getMonth() + 3) / 3), // 季度
	    S: date.getMilliseconds(), // 毫秒
	  };
	  if (/(y+)/.test(fmt))
	    fmt = fmt.replace(
	        RegExp.$1,
	        (date.getFullYear() + "").substr(4 - RegExp.$1.length)
	    );
	  for (var k in o) {
	    if (new RegExp("(" + k + ")").test(fmt))
	      fmt = fmt.replace(
	          RegExp.$1,
	          RegExp.$1.length === 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length)
	      );
	  }
	  return fmt;
	}
	// 保留小数
	const formatNumber = (number) => {
		if (number === null || number === '' || number === undefined) {
			return '-';
		}
		const num = parseFloat(number);
		if (Number.isInteger(num)) {
			return num.toFixed(2);
		} else {
			const [integerPart, decimalPart = ''] = num.toString().split('.');
			const formattedDecimal = decimalPart.length >= 2 ? decimalPart.slice(0, 2) : decimalPart.padEnd(2, '0');
			return `${integerPart}.${formattedDecimal}`;
		}
	};

	// 需求响应统计信息
	const getDemandStatistics = () =>{
		
		var chartDom = document.getElementById('line');
		var myChart = echarts.init(chartDom);
		myChart.showLoading({
			text: '数据加载中...',
			color: '#FFF',
			textColor: '#FFF',
			maskColor: 'rgba(255, 255, 255, 0)',
			zlevel: 0
		});
		http.post('demand_resp/resp_board/getDemandStatistics',{
			"endDate": endDates,
			"feedbackTimeSort": feedbackTimeSort,
			"number": page,
			"pageSize": 10,
			"profitSort": profitSort,
			"respLevelSort": respLevelSort,
			"respSubsidySort": respSubsidySort,
			"respTypeSort": respTypeSort,
			"rsTimeSort": rsTimeSort,
			"startDate": startDates
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				if(res.data.data){
					let data = res.data.data
					let list = res.data.data.list
					let newlist = []
					let Xlist  = []
					let baseLoad = []	//基线
					let actualLoad = []
					if(list){
						list.length &&list.map(res =>{
							Xlist.push(res.loadDate)
							baseLoad.push(res.baseLoad)
							actualLoad.push(res.actualLoad)
						})
						
						setBaseLoad(baseLoad)
						setActualLoad(actualLoad)
						setXlist(Xlist)
					}else{
						setList([])
						setXlist([])
						setBaseLoad([])
						setActualLoad([])
						line()
					}
					setDemandNum(formatNumber(data.demandNum));
					setTotalCutPower(formatNumber(data.totalCutPower));
					setTotalFillProfit(formatNumber(data.totalFillProfit));
					setTotalFillPower(formatNumber(data.totalFillPower));
					setAvgDeclareLoad(formatNumber(data.avgDeclareLoad));
					setTotalProfit(formatNumber(data.totalProfit));
					setTotalCutProfit(formatNumber(data.totalCutProfit))
					setTotalVolumeProfit(formatNumber(data.totalVolumeProfit))
					setUserProfit(formatNumber(data.userProfit))
					setPlatformProfit(formatNumber(data.platformProfit))
					setAvgProfit(formatNumber(data.avgProfit))
					setAvgUserProfit(formatNumber(data.avgUserProfit))
					setAvgPlatformProfit(formatNumber(data.avgPlatformProfit))
					line()
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 选择日期
	const chosedate =(data,datastring) =>{
		console.log(data)
		console.log(datastring)
		setStartDate(datastring[0])
		setEndDate(datastring[1])
		getDemandStatistics()
	}
	
	// 基贤负荷
	const line = () =>{
		var myChart = echarts.init(document.getElementById('line'));
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		myChart.setOption({
			title: {
			    text: '单位：kW',
				textStyle:{
					color:'#FFF',
					fontSize:12,
					fontWeight:'normal'
				},
			},
			color:['#F05887','#44D7B6'],
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
					    top: "240", // 相对父元素上下的位置
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
					console.log()
					var relVal = params[0].name;
					for (var i = 0, l = params.length; i < l; i++) {
						// console.log('tooltip数据值',params[i].value)
						if(params[i].value===null||params[i].value===undefined||params[i].value==''||params[i].value=='-'){
							relVal +='<br/>' + params[i].marker+params[i].seriesName + ' : ' +'-'
						}else{
							relVal += '<br/>' + params[i].marker +params[i].seriesName + ' : ' + Number(params[i].value).toFixed(2) 
						}
						//遍历出来的值一般是字符串，需要转换成数字，再进项tiFixed四舍五入
						
					}
					return relVal;
				}
			  },
			  legend: {
			    data: ['基线负荷', '实际负荷'],
				icon: 'circle',
				itemHeight: 6,
				itemWidth: 6,// 添加
				textStyle: {
				    color: '#FFF'
				},
				left:100
			  },
			  grid: {
			    left: '3%',
			    right: 20,
			    bottom: '13%',
			    containLabel: true
			  },
			// dataZoom: [
			// 	{
			// 		type: 'inside',
			// 		start: 0,
			// 		end: 10
			// 	},
			// 	{
			// 		start: 0,
			// 		end: 10
			// 	}
			// ],
			xAxis: {
			    type: 'category',
			    boundaryGap: false,
			    data: timeStamp,
				type: 'category',
				// boundaryGap: false,
				// data: ['01', '02', '03', '04', '05', '06', '07','08','09','10','11','12'],
				axisLabel : {
					formatter: '{value}',
					textStyle: {
						color: '#FFF'
					}
				},
			},
			  yAxis: {
			    type: 'value',
				// max: 800,
				axisLabel: {
					formatter: '{value}',
					color:'#FFF'
				},
				splitLine:{
					show:true,
					lineStyle:{
						type:'dashed',
						color:'#DFE1E5'
					}
				},
			  },
			  series: [
			    {
			      name: '基线负荷',
			      type: 'line',
			      // stack: 'Total',
			      data: baselineLoadValue,
				  smooth: true,
				  lineStyle: {
				          color: '#F05887',
				          width: 1,
				          type: 'dashed'
				        },
			    },
			    {
			      name: '实际负荷',
			      type: 'line',
			      // stack: 'Total',
				  smooth: true,
			      data: realValue
			    }
			  ]
		})
		
		myChart.hideLoading()
		
		myChart.on('click', function (param) {
			console.log(param, param.data);//这里根据param填写你的跳转逻辑
		   
		});
	}
	const onChange =(e) =>{
		console.log(e)
	}
	// 基贤负荷日期选择
	const dateChange1 =(date,mode) =>{
		console.log(date,mode)
		setEndTs(mode[1])
		setStartTs(mode[0])
		setPanier(getDay(mode[0],mode[1]))
	}
	// 查询
	const Search =() =>{
		if(startDate&&endDate){
			setPagetotal(1)
			setCurrentNum(1)
			getTaskAndStrategyList()
			
		}else{
			message.info('请选择时间')
		}
		
		
	}
	// 需求响应统计选择
	const needChange =(date,mode) =>{
		console.log(mode)
		setEndDates(mode[1])
		setStartDates(mode[0])
	}
	// 需求响应统计查询
	const needSearch =() =>{
		
		if(startDates&&endDates){
			getDemandStatistics()
		}else{
			message.info('请选择时间')
		}
		
	}
	// 基贤负荷分析查询
	const respond =() =>{
		console.log(1)
		if(endTs==''){
			message.info('请选择时间')
		}else if(nodeId==''){
			message.info('请选择节点')
		}else{
			http.post('load_management/ai_prediction/loadPredictionChart',{
				"endTs": endTs,
				"nodeId": nodeId,
				"startTs": startTs,
				"systemId": "nengyuanzongbiao"
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					let data = res.data.data
					let realValue = []
					let baselineLoadValue = []
					let timeStamp = []
					if(data.length>0){
						data.map(res =>{
							realValue.push(res.realValue)
							baselineLoadValue.push(res.baselineLoadValue)
							timeStamp.push(res.timeStamp)
						})
						
						setBaselineLoadValue(baselineLoadValue)
						setRealValue(realValue)
						setTimeStamp(timeStamp)
						setIsEmpty(true)
						// line()
					}else{
						setBaselineLoadValue([])
						setRealValue([])
						// setTimeStamp(timeStamp)
						setIsEmpty(false)
						// line()
						
					}
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
	}
	// 选择节点
	const nodeChange =(val) =>{
		console.log(val)
		setNodeId(val)
	}
	const dateChange =(e,mode) =>{
		console.log(mode)
		setStartDate(mode[0])
		setEndDate(mode[1])
		
	}
	// 重新加载
	const straining =() =>{
		setVisible(true)
	}
	const handleOk = (e) => {
	    console.log(e);
		if(baselineDate==''){
			message.info('请选择日期')
		}else{
			http.post('demand_resp/calendar/generateBaseline?baselineDate='+baselineDate).then(res =>{
				console.log(res)
				if(res.data.code==200){
					
					setVisible(false)
					setBaselineDate('')
					message.success('成功')
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
			
		}
		
	};
	
	const handleCancel = (e) => {
	    console.log(e)
		setVisible(false)
		setBaselineDate('')
	};
	const dateonChange =(date, dateString) =>{
		console.log(date)
		console.log(dateString)
		setBaselineDate(dateString)
	}

		
	const disabledDate: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023-01-01')) || current > dayjs().endOf('day')
	};
	const columns = [
		{
			title: '编号',
			width: 100,
			// render:(text,record,index)=> `${index+1}`
			render:(value, item, index) => (pagetotal - 1) * 5 + index+1,
		},
	
	  
	  {
		title: '任务编码',
		dataIndex: 'taskCode',
		key: 'taskCode',
	  },
	  {
		title: '需求响应时间段',
		dataIndex: 'rsDate',
		key: 'rsDate',
		render: (s, record, index) =>{
			// console.log(record)
			return record.rsDate +'    ' +record.rsTime+'~' +record.reTime
		}
		
	  },
	  {
		title: '负荷需求(kW)',
		dataIndex: 'respLoad',
		key: 'respLoad',
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===""||text==="-"){
				return '-'
			}else{
				return text
			}
		}
	  },
	  {
		title: '响应类型',
		dataIndex: 'respType',
		key: 'respType',
		render: (text,record,_,action) =>{
			if(record.respType ==1){
				return '削峰响应'
			}else if(record.respType ==2){
				return '填谷响应'
			}else{
				return '-'
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
		title: '响应补贴(元/kWh)',
		dataIndex: 'respSubsidy',
		key: 'respSubsidy',
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===""||text==="-"){
				return '-'
			}else{
				return Number(text).toFixed(2)
			}
		}
	  },
	  {
		title: '申报负荷(kW)',
		dataIndex: 'declareLoad',
		key: 'declareLoad',
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===""||text==="-"){
				return '-'
			}else{
				return Number(text).toFixed(2)
			}
		}
		// render: (s, record, index) =>{
		// 	// console.log(record)
		// 	return 'AI智能调度'
		// }
	  },
	  {
		title: '预估收益（元）',
		dataIndex: 'profit',
		key: 'profit',
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===""||text==="-"){
				return '-'
			}else{
				return Number(text).toFixed(2)
			}
		}
	  },
	];
	
	const onChangetab =(page) =>{
		console.log(page)
		setPagetotal(page.current)
		setCurrentNum(page.current)
		getTaskAndStrategyList()
	}
	return(
		<div className="allcontent12">
			
			
			<div className="respondheader">
				<h4>需求响应统计
					<div className="seaarchBtn">
						<Button type="primary" onClick={needSearch} style={{float:'right'}}>查询</Button>
						<RangePicker disabledDate={disabledDate}  style={{marginRight:20}} onChange={needChange} 
							defaultValue={[dayjs(startDates, dateFormat), dayjs(endDates, dateFormat)]}
							format={dateFormat}
						/>
					</div>
				</h4>
				<div className="numerical">
					<div className="numericaltop">
						<div className="numericalleft">
							{
								// <img src={require('../../../style/load/ci.png')}  />
							}
							<div>
								<span>申报次数</span>
								<p>{demandNum}</p>
							</div>
						</div>
						<div className="numericalright">
							<div className="numelectricity" style={{paddingBottom:24}}>
								<ul>
									<li>
										<p>参与节点数</p>
										<span>{loadNumber}</span>
									</li>
									<li>
										<p>削峰电量(kWh)</p>
										<span>{totalCutPower}</span>
									</li>
									<li>
										<p>填谷电量(kWh)</p>
										<span>{totalFillPower}</span>
									</li>
								</ul>
							</div>
							<div className="numelectricity" style={{border:'none'}}>
								<ul style={{marginTop:'24px'}}>
									<li>
										<p>平均申报负荷（kW/次）</p>
										<span>{avgDeclareLoad}</span>
									</li>
									
								</ul>
							</div>
						</div>
					</div>
					<div className="numericaltop numericaltops" style={{marginTop:24,height:'auto'}}>
						<div className="numericalleft">
							{
								// <img src={require('../../../style/load/yuan.png')}  />
							}
							<div>
								<span>总收益(元)</span>
								<p>{totalProfit}</p>
							</div>
							<b> <InfoCircleOutlined />  此收益为预估收益，实际收益以电网账单为准</b>
						</div>
						<div className="numericalright">
							<div className="numelectricity" style={{paddingBottom:24}}>
								<ul>
									<li>
										<p>削峰收益（元）</p>
										<span>{totalCutProfit}</span>
									</li>
									<li>
										<p>填谷收益（元）</p>
										<span>{totalFillProfit}</span>
									</li>
									<li>
										<p>容量收益（元）</p>
										<span>{totalVolumeProfit}</span>
									</li>
								</ul>
							</div>
							<div className="numelectricity" style={{paddingBottom:24,margin:'24px 0px'}}>
								<ul>
									<li>
										<p>用户收益</p>
										<span>{userProfit}</span>
									</li>
									<li>
										<p>平台收益</p>
										<span>{platformProfit}</span>
									</li>
									
								</ul>
							</div>
							<div className="numelectricity" style={{border:'none'}}>
								<ul >
									<li>
										<p>户均收益（元/户）</p>
										<span>{avgProfit}</span>
									</li>
									<li>
										<p>户均用户收益（元/户）</p>
										<span>{avgUserProfit}</span>
									</li>
									<li>
										<p>户均平台收益（元/户）</p>
										<span>{avgPlatformProfit}</span>
									</li>
									
								</ul>
							</div>
						</div>
					</div>
					
				</div>
				
				
			</div>

			<div className="respondheader" style={{marginTop:16}}>
				<h4>基线负荷分析</h4>
				<div className="datumtitle">
					节点：
					<Select  style={{width:200}}
						onChange={nodeChange}
						value={nodeId}
					>
						{
							nodeList&&nodeList.map(res =>{
								return <Option key={res.id} value={res.id}>{res.nodeName}</Option>
							})
						}
					</Select>
					<b>日期：</b><RangePicker disabledDate={disabledDate} onChange={dateChange1} 
					defaultValue={[dayjs(startTs, dateFormat), dayjs(endTs, dateFormat)]}
					format={dateFormat}
					style={{float:'inherit'}} />
					
					<Button type="primary" onClick={respond} style={{marginLeft:16}}>查询</Button>
					<Button type="primary" onClick={straining} style={{marginLeft:16}}>重新加载</Button>
				</div>
				<div id="line"></div>
				<Modal
					title="重新加载日期"
					visible={visible}
					onOk={handleOk}
					onCancel={handleCancel}
					width={400}
					footer={null}
					// style={{paddingBottom:24}}
				>
					<DatePicker style={{width:'100%',background:'none'}} 
					value={baselineDate!=''? dayjs(baselineDate) : undefined}
					disabledDate={disabledDate} onChange={dateonChange} />
					<div className="Rspondboardbtn" style={{marginBottom:24,textAlign:'right'}}>
						<Button ghost onClick={handleCancel}>取消</Button>
						<Button type="primary" onClick={handleOk}>确定</Button>
					</div>
				</Modal>
			</div>
		</div>
	)
}
	

export default Rspondboard