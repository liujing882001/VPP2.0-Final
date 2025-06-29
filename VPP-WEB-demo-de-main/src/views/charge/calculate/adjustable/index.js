import React,{useEffect,useState,useRef} from 'react'
import './index.css'
import { Tree,DatePicker,ConfigProvider,Button,Table ,Input,Spin,message } from 'antd';
import type { DataNode, TreeProps } from 'antd/lib/tree';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import * as echarts from 'echarts';
import http from '../../../../server/server.js'
import axios from 'axios'
import { Resizable } from 're-resizable';
import type { DatePickerProp } from 'antd';
import Icon from '@ant-design/icons';

const { Search } = Input;

const dateFormat = 'YYYY-MM-YY';
const { RangePicker } = DatePicker;



const columns = [
	{
		title: '时间',
		dataIndex: 'timeStamp',
		key: 'timeStamp',
		// width: 250,
	},
	{
		title: '实际（kW）',
		dataIndex: 'realValue',
		key: 'realValue',
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===''){
				return '-'
			}else if(text=='-'){
				return '-'
			}else{
				return Number(text).toFixed(2)
			}
		}
	// width: 150,
	},
	{
		title: '预测负荷（kW）',
		dataIndex: 'currentForecastValue',
		key: 'currentForecastValue',
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===''){
				return '-'
			}else if(text=='-'){
				return '-'
			}else{
				return Number(text).toFixed(2)
			}
		}
	},
	{
		title: '基线负荷（kW）',
		dataIndex: 'baselineLoadValue',
		key: 'baselineLoadValue',
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===''){
				return '-'
			}else if(text=='-'){
				return '-'
			}else{
				return Number(text).toFixed(2)
			}
		}
	},
];
const Adjustable =({pCount}) =>{
	// this.selectRef = React.createRef();
	const [currentIndex, setCurrentIndex] = useState(0);
	const [current, setCurrent] = useState(1);
	const [forecastType, setForecastType] = useState('LoadForecasting');
	const [treeData, setTreeData] = useState([]);
	const [id, setId] = useState('');
	const [type, setType] = useState('');
	const [dates, setDates] = useState('');
	const [echartsdate, setEchartsdate] = useState([]);
	const [activePower, setActivePower] = useState([]);
	const [aiTimePrice, setAiTimePrice] = useState([]);
	const [number, setNumber] = useState(5);
	const [dateType, setDateType] = useState(1);
	const [expandedKeys, setExpandedKeys] = useState([]);
	const [searchValue, setSearchValue] = useState('');
	const [autoExpandParent, setAutoExpandParent] = useState(true);
	const [loading, setLoading] = useState(false);
	const [startTs, setStartTs] = useState('');
	const [endTs, setEndTs] = useState('');
	const [total, setTotal] = useState('');
	const [dateString, setDateString] = useState('');
	const [dateStringend, setDateStringend] = useState('');
	const [dateStringmonth, setDateStringmonth] = useState('');
	const [dateStringmonthend, setDateStringmonthend] = useState('');
	const [dateStringday, setDateStringday] = useState('');
	const [dateStringdayend, setDateStringdayend] = useState('');
	const [treeLoading, setTreeLoading] = useState(false);
	const [TreeList, setTreeList] = useState([]);
	const [keyValue, setKeyValue] = useState('');
	const [systemId, setSystemId] = useState('');
	const [setwidth, setSetwidth] = useState('');
	const [newidth, setNewidth] = useState('');
	const [nodeId, setNodeId] = useState('');
	const [page, setPage] = useState(1);
	const [copyTree, setCopyTree] = useState([]);
	const [copyExpandedKeys, setCopyExpandedKeys] = useState([]);
	const [currentForecastValue, setCurrentForecastValue] = useState([]);
	const [mediumTermForecastValue, setMediumTermForecastValue] = useState([]);
	const [realValue, setRealValue] = useState([]);
	const [baselineLoadValue, setBaselineLoadValue] = useState([]);
	const [ultraShortTermForecastValue, setUltraShortTermForecastValue] = useState([]);
	const [currentNum, setCurrentNum] = useState(1);
	const [isEmpty, setIsEmpty] = useState(false);
	const [dataSource,setDataSource] = useState([]);
	const [subSystemIds,setSubSystemIds] = useState('');
	const [nodeIds,setNodeIds] = useState('');
	const chartRef = useRef(null);
	const selectRef = useRef(null);
	const adjustableRef = useRef(null);
	const HeartSvg = () => (
	  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
	    <title>heart icon</title>
		<path d="M71.03 962.129h19.633V441.337l20.783-7.28 228.429-80.063V209.981l17.791-8.384L741.004 20.816l44.16-20.8v8.528l120.798 72.015v877.65h47.007v61.903H723.23V97.47L401.778 249.084v83.23l93.006-32.607L530.8 287.1l0.032-0.976 0.976 0.608 4.08-1.456v3.871l120.11 71.6V956.32h47.727v61.92H474.017V372.361l-321.5 112.67V1024H71.032v-61.871z m117.471 53.935h238.14V907.218c-45.103 2.944-87.806 5.744-128.27 8.416-38.463 2.48-75.07 4.88-109.87 7.168v93.262z m0-402.938c34.8-9.823 71.407-20.24 109.87-31.055a82813.447 82813.447 0 0 1 128.27-36.304V436.761c-45.103 15.696-87.806 30.592-128.27 44.687-38.463 13.408-75.07 26.128-109.87 38.272v93.406z m0 133.086l109.87-20.815 128.27-24.304V592.087c-45.103 11.52-87.806 22.383-128.27 32.703-38.463 9.824-75.07 19.136-109.87 28.048v93.374z m0 133.982c34.8-3.296 71.407-6.768 109.87-10.464 40.448-3.824 83.151-7.903 128.27-12.207V748.596l-128.27 20.656c-38.463 6.16-75.07 12.048-109.87 17.648v93.294z" ></path>	  </svg>
	);
	const HeartIcon = (props: Partial<CustomIconComponentProps>) => (
	  <Icon component={HeartSvg} {...props} />
	);
	const systemSvg = () => (
	  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
	    <title>heart icon</title>
		<path d="M524.480693 818.540998a27.430999 27.430999 0 0 1-13.715499-3.56603L38.129088 545.328252a27.430999 27.430999 0 1 1 27.430999-47.729938l459.194916 261.966037 436.975807-246.878988a27.430999 27.430999 0 0 1 27.430999 47.729938l-451.239927 254.559667a27.430999 27.430999 0 0 1-13.441189 3.56603z" p-id="5451"></path><path d="M524.480693 1023.999177a27.430999 27.430999 0 0 1-13.715499-3.56603L38.129088 750.786431a27.430999 27.430999 0 1 1 27.430999-47.729937l459.194916 261.966036 436.975807-246.878987a27.430999 27.430999 0 0 1 27.430999 47.729937L537.921882 1020.433147a27.430999 27.430999 0 0 1-13.441189 3.56603z" p-id="5452"></path><path d="M513.233983 599.915939a27.430999 27.430999 0 0 1-12.892569-3.29172L14.538429 337.949903a27.430999 27.430999 0 0 1 0-48.004248L499.518484 3.84034a27.430999 27.430999 0 0 1 27.430999 0l486.077295 286.105315a27.430999 27.430999 0 0 1 13.715499 24.413589 27.430999 27.430999 0 0 1-14.538429 23.590659l-486.077295 258.674316a25.785139 25.785139 0 0 1-12.89257 3.29172zM83.664546 312.439074l429.569437 229.048838 429.843748-229.048838-429.843748-253.188117z" p-id="5453"></path>
	</svg>
	);
	const SystemIcon = (props: Partial<CustomIconComponentProps>) => (
	  <Icon component={systemSvg} {...props} />
	);
	useEffect(() =>{
		tabFn(1)
		tree()
	},[])
	useEffect(() =>{
		if(nodeId&&systemId){
			loadPredictionListPage()
		}
		
	},[page,currentNum])
	useEffect(() =>{
		statements()
	},[echartsdate,baselineLoadValue,baselineLoadValue,realValue,currentForecastValue,isEmpty])

	// 导出
	const devices =() =>{
		axios({
			method: 'post',
			url: 'load_management/ai_prediction/loadPredictionListExcel',
			responseType: 'arraybuffer',
			data:{
				"endTs": dateStringend,
				"nodeId": nodeId,
				"startTs": dateString,
				"systemId": systemId
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
	const extractNodes =(data) => {
	    return data.map(item => {
			if (item.children) {
				return {
					...item,
					title: `${item.stationName}`,
					key: item.stationId,
					children: extractNodes(item.children),
					icon:item.parentId == ""?<HeartIcon />:<SystemIcon  />
				};
			} else {
				return {
					...item,
					title: `${item.stationName}`,
					key: item.stationId,
					icon:item.parentId == ""?<HeartIcon />:<SystemIcon  />
				};
			}
		});
	}
	// 树
	const tree =() =>{
		setTreeLoading(true)
		http.post('stationNode/stationPageQuery',{
			page:1,
			size:100000,
			query:'',
			"keyword" : {
				"stationTypeId" : [],
				"stationState" : [],
				"stationCategory":'load'
			}	
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				const newData = extractNodes(res.data.data.content);
				setNumber(newData.length);
				setTreeData(newData);
				setTreeLoading(false);
				setTreeList(newData);
				setCopyTree(newData);
			}
		}).catch(err =>{
			console.log(err)
		})
		
	}
	
	// 选择年月日
	const tabFn =(index) => {
		console.log(index)
		setCurrent(index);
		setDateType(index);
		setKeyValue(new Date());
		setDateStringmonth('');
		setDateStringmonthend('');
		setPage(1);
		setCurrentNum(1)
	}
	const clsFn =(index, curCls, cls) => {
		// console.log(current)
		return current === index ? curCls : cls;
	}
	// 时间选择
	const dateChange =(data,dateString) =>{
		setDateString(dateString[0]);
		setDateStringend(dateString[1]);
	}
	
	//查询
	const arround =() =>{
		
		if(dateString ==''){
			message.info('请选择日期')
		}else{
			setLoading(true);
			setCurrentNum(1);
			setPage(1);
			setIsEmpty(true)
			var chartDom = document.getElementById('statement');
			var myChart = echarts.init(chartDom);
			window.addEventListener("resize", () => {
				myChart.resize({ width: 1000+'px' });
				// myChart.resize();
			});
			myChart.showLoading({
				text: '',
				color: '#FFF',
				textColor: '#FFF',
				maskColor: 'rgba(255, 255, 255, 0)',
				// maskColor: 'rgba(0, 0, 0, 0.1)',
				zlevel: 0
			});
			http.post('load_management/ai_prediction/loadPredictionChart',{
				"endTs": dateStringend,
				"nodeId": nodeId,
				"startTs": dateString,
				// "systemId": systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					
					if(res.data.data){
						let data = res.data.data
						let	currentForecastValue = []	//日前
						let ultraShortTermForecastValue = []	//预测
						let realValue = []	//实际
						let baselineLoadValue = []	//基线负荷
						let timeStamp = []
						res.data.data.map(item =>{
							timeStamp.push(item.timeStamp)
							baselineLoadValue.push(item.baselineLoadValue)	//
							currentForecastValue.push(item.currentForecastValue)	//
							realValue.push(item.realValue)
							// ultraShortTermForecastValue.push(item.ultraShortTermForecastValue)
						})
						
						setEchartsdate(timeStamp);
						setUltraShortTermForecastValue(ultraShortTermForecastValue);
						setBaselineLoadValue(baselineLoadValue);
						setRealValue(realValue);
						setCurrentForecastValue(currentForecastValue);
						setLoading(false);
						setIsEmpty(data.length>0?true:false);
						
					}else{
						setEchartsdate([]);
						setUltraShortTermForecastValue([]);
						setBaselineLoadValue([]);
						setRealValue([]);
						setCurrentForecastValue([]);
						setLoading(false);
						setIsEmpty(false);
					}
					
					
					
				}else{
					message.info(res.data.msg)
				}
				
			})
			
			loadPredictionListPage()
			// const width = this.selectRef.current.clientWidth;
			// console.log(width,'widthwidthwidthwidthwidth')
			
		}
		
	}
	// 类表
		
	const loadPredictionListPage=() =>{
		
		http.post('load_management/ai_prediction/loadPredictionListPage',{
			"endTs": dateStringend,
			"nodeId": nodeId,
			"number": page,
			"pageSize": 20,
			"startTs": dateString,
			// "systemId": systemId
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let echartsdate = []
				let activePower = []
				let aiTimePrice = []
				
				setDataSource(res.data.data.content);
				setTotal(res.data.data.totalElements);
				setLoading(false)
			}else{
				message.info(res.data.msg)
			}
			
		})
	}

	//选择tree
	const onSelect= (selectedKeys, info) => {
		console.log(info)
		setSystemId('nengyuanzongbiao');
		setNodeId(info.node.stationId)	
	};
	// 分页
	const handlePagination =(page) =>{
		setPage(page);
		setCurrentNum(page);
	}
	useEffect(() =>{
		var chartDom = document.getElementById('statements');
		var myChart = echarts.init(chartDom);
		if(selectRef.current){
			const width = selectRef.current.clientWidth;
			myChart.resize({ width: width+'px' });
			if(width==0){
				myChart.resize({ width: pCount-300+'px' });
			}else{
				myChart.resize({ width: width+'px' });
			}
		}else{
			myChart.resize({ width: pCount-300+'px' });
		}
	},[pCount])
	// 光伏发电
	const statements=() =>{
		var chartDom = document.getElementById('statement');
		var myChart = echarts.init(chartDom);
	
		myChart.setOption({
			color: ['#246CF9', '#00BF8F','#FFCF40','#6236FF'],
			backgroundColor:'#212029',
			tooltip: {
				trigger: 'axis',
				backgroundColor: '#302F39',
				borderColor: 'transparent',
				textStyle: {
					color: '#fff' 
				},
				axisPointer: {
					type: 'cross',
					label: {
						backgroundColor: '#6a7985'
					}
				},
				formatter(params) {
					var relVal = params[0].name;
					for (var i = 0, l = params.length; i < l; i++) {
						if(params[i].value===null||params[i].value===undefined||params[i].value==''||params[i].value=='-'){
							relVal +='<br/>' + params[i].marker+params[i].seriesName + ' : ' +'-'
						}else{
							relVal += '<br/>' + params[i].marker +params[i].seriesName + ' : ' + Number(params[i].value).toFixed(2) 
						}
						
					}
					return relVal;
				}
				
			},
			graphic: {
			    elements: [
					{
						type: 'image',
						z: 100,
						left: 'center',
						top: 'middle',
						style: {
							image:require('../../../../style/damao/empty.png')  ,
							// image: 'https://example.com/image.jpg',
							width: 100,
							height: 100
						},
						invisible:isEmpty
					},
					{
					    type: "text",
					    left: "center", // 相对父元素居中
					    top: "190", // 相对父元素上下的位置
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
			grid: {
				top:40,
				left: '3%',
				right: '2%',
				bottom: '20%',
				containLabel: true
			},
			title: {
			    text: '单位：kW',
				left:20,
				top:'top',
				textStyle:{
					color:'#FFF',
			　　　　 fontSize:12
				}
			},
			legend: {
				icon: 'rect',
				itemWidth: 12,
				itemHeight: 12,
				textStyle: { color: '#FFF'},
				x:100,
				y:'top',
			    data: ['实际','预测负荷','基线负荷']
			},
			xAxis: [
				{
					type: 'category',
					axisTick: {
					        alignWithLabel: true
					},
					data:echartsdate,
					axisLabel:{
						  show:true,
						  color:'#fff',
						  formatter: `{value}`
					  },
					  
				}
			  ],
			yAxis: [{
				type: 'value',
				boundaryGap: [0, '100%'],
				axisLabel:{
					show:true,
					color:'#FFF',
				},
				formatter:function(value,index){
					return value.toFixed(2); 
				},
				splitLine:{
					show:true,
					lineStyle:{
						type:'dashed',
						color:'rgba(255, 255, 255, 0.2)'
					}
				}
				
			}],
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
					name: '实际',
					type: 'line',
					// stack: 'Total',
					smooth: true,
					lineStyle: {
					width: 1,
					// color:'#246CF9'
					},
					showSymbol: false,
					areaStyle: {
					opacity: 0.8,
					color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
						{
						offset: 0,
						color: 'rgba(77, 115, 239, 0.2)'
						},
						{
						offset: 1,
						color: 'rgba(77, 115, 239, 0)'
						}
					])
					},
					emphasis: {
					focus: 'series'
					},
					data:realValue
					// data: [120, 282, 111, 234, 220]
				},
				{
					name: '预测负荷',
					type: 'line',
					// stack: 'Total',
					smooth: true,
					lineStyle: {
						width: 1,
						color:'#00BF8F'
					},
					showSymbol: false,
					areaStyle: {
					opacity: 0.8,
						color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
							{
								offset: 0,
								color: 'rgba(8, 205, 191, 0.25)'
							},
							{
								offset: 1,
								color: 'rgba(8, 205, 191, 0)'
							}
						])
					},
					emphasis: {
						focus: 'series'
					},
					data:currentForecastValue
					// data: [140, 232, 101, 264, 90]
				},
				
				{
					name: '基线负荷',
					type: 'line',
					// stack: 'Total',
					smooth: true,
					lineStyle: {
						width: 1,
						color:'#6236FF'
					},
					showSymbol: false,
					areaStyle: {
					opacity: 0.8,
						color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
							{
								offset: 0,
								color: 'rgba(98,54,255, 0.25)'
							},
							{
								offset: 1,
								color: 'rgba(98,54,255, 0)'
							}
						])
					},
					emphasis: {
						focus: 'series'
					},
					data:baselineLoadValue
					// data: [140, 232, 101, 264, 90]
					
				},
				
				]
		});
		window.addEventListener("resize", () => {
			const width = adjustableRef.current.clientWidth;
			if(selectRef.current){
				const width = selectRef.current.clientWidth;				
				if(width==0){
					myChart.resize({ width: pCount-300+'px' });
				}else{
					myChart.resize({ width: width+'px' });
				}
			}else{
				myChart.resize({ width:  pCount-300+'px' });
					
			}
			
			myChart.resize()
		});
		myChart.hideLoading()
	}
	const arrayTreeFilter = (data, predicate, filterText) => {
	   const nodes = data;

	   if (!(nodes && nodes.length)) {
		   return;
	   }
	   const newChildren = [];
	   for (const node of nodes) {
		   if (predicate(node, filterText)) {
			   newChildren.push(node);
		   } else {
			   const subs = arrayTreeFilter(node.children, predicate, filterText);
			   if ((subs && subs.length) || predicate(node, filterText)) {
				   node.children = subs;
				   newChildren.push(node);
			   }
			   
			   setNumber(0)
		   }
	   }
	   return newChildren;
   }
	
	const filterFn = (data, filterText) => { //过滤函数
	   if (!filterText) {
		   return true;
	   }
	   return (
		   new RegExp(filterText, "i").test(data.title) //我是一title过滤 ，你可以根据自己需求改动
	   );
	}
	const flatTreeFun = (treeData) => { //扁平化 tree
	   let arr = [];
	   const flatTree = (treeData) => {
		   treeData.map((item, index) => {
			   arr.push(item);
			   if (item.children && item.children.length > 0) {
				   flatTree(item.children);
				   item.children = [];
			   }
		   })
	   }
	   flatTree(treeData);
	   return arr;
	}
	const expandedKeysFun = (treeData) => { //展开 key函数
	   if (treeData && treeData.length == 0) {
		   return [];
	   }
	   //console.log(treeData)
	   let arr = [];
	   const expandedKeysFn = (treeData) => {
		   treeData.map((item, index) => {
			   arr.push(item.key);
			   if (item.children && item.children.length > 0) {
				   expandedKeysFn(item.children);
			   }
		   })
	   }
	   expandedKeysFn(treeData);
	   return arr;
	}
	const onChanges = (e) => {
			console.log(e)
	   let value = e;
	   if (value == "") { 
		   tree()
		   setExpandedKeys(copyExpandedKeys)
	   } else {
				 console.log(copyTree)
			http.post('tree/runAreaLoadForestShortView').then(res =>{
				if(res.data.code ==200){
					let data = res.data.data
					let number = 0;    
					function f(arr) {
						arr.forEach(item=>{
							if(item.children) {
								f(item.children) 
								let children = item.children
								for(var i=0;i<children.length;i++){
									if(children[i].type=='NODE'){
										number++
									}
								}
							}
						})
					}
					f(data)
					console.log(number)
					let a = expandedKeysFun(res.data.data);
					setTreeData(res.data.data);
					setNumber(number);
					setTreeLoading(false);
					setTreeList(res.data.data);
					setCopyTree(res.data.data)
					let newres = arrayTreeFilter(copyTree, filterFn, value);
					console.log(newres)
					let expkey = expandedKeysFun(newres);
					let num = 0
					if(newres.length>0){
						function f(arr) {
							arr.forEach(item=>{
								if(item.children) {
									f(item.children) 
									let children = item.children
									for(var i=0;i<children.length;i++){
										if(children[i].type=='NODE'){
											num++
										}
									}
								}
							})
						}
						f(newres)
					}
					
					setTreeData(newres);
					setExpandedKeys(expkey);
					setNumber(num)
				}
			})
			
	   }

   }
	
	const onCheck: TreeProps['onCheck'] = (checkedKeys, info) => {
		console.log('onCheck', checkedKeys, info);
	};
	
	const onExpand = (expandedKeys,info) => {
		// let list = 
		console.log(expandedKeys)
		console.log(info)
		
		setExpandedKeys(expandedKeys);
		setAutoExpandParent(false);
		setId(info.node.key)
	};
		
		
		
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current < dayjs(new Date('2023-01')) || current > dayjs().add('3','month')
			
		};
		const minWidth = 240;
		const maxWidth = 550
		return(
			<div className="adjustablecont calculate-adjustable" style={{display:'flex'}}>
			<Resizable
			  defaultSize={{
			    width: 300,
			  }}
			  minWidth={minWidth}
			  maxWidth={maxWidth}
			  handleSize={[20, 20]}
			  minConstraints={[100, 100]}
			  maxConstraints={[500, 400]}
			  enable={{ top: false, right: true, bottom: false, left: false, topRight: false, bottomRight: false, bottomLeft: false, topLeft: false }}
			  onResize={(e, direction, ref, d) => {
				const myChart = echarts.init(chartRef.current);
				myChart.resize()
				
			  }}
			  onResizeStop={(e, direction, ref, d) => {
			    // console.log('Stop resizing:', ref, d);
			  }}
			>
				<div className="navigation" >
					<div className="packingtion">
						<Search 
							style={{marginTop: 16 }}
							placeholder="查询节点" onSearch={onChanges} />
					</div>
					<p className="numbers">全部节点（{number}）</p>
					<Spin spinning={treeLoading}  size="middle">
						<Tree
							showIcon
							defaultExpandAll
							onSelect={onSelect}
							treeData={treeData}
							onCheck={onCheck}
							onExpand={onExpand}
							expandedKeys={expandedKeys}
							autoExpandParent={autoExpandParent}
							// treeData={treeData2}
							checkedKeys
							checkStrictly
						/>
					</Spin>
				</div>
			</Resizable>
				
				<div className="adjustable" ref={adjustableRef}>
					<div className="adjustableheader">
						<div className="chosedatebyn" style={{paddingBottom:0}}>
							<RangePicker disabledDate={disabledDate} onChange={dateChange} />
							<Button type="primary" onClick={arround}>查询</Button>
							<Button style={{float:'right'}} type="primary" onClick={devices}>导出</Button>
						</div>
						
						
						
					</div>
					
					<div id="statement" ref={chartRef} style={{height:300}}></div>
					<div style={{padding:15}} ref={selectRef} >
						
						<Table dataSource={dataSource} 
							columns={columns}
							scroll={{ y: 500 }}
							pagination={
								{
									total: total,//数据的总条数
									defaultCurrent: 1,//默认当前的页数
									defaultPageSize: 20,//默认每页的条数
									showSizeChanger:false,
									onChange: handlePagination,
									current:currentNum
								}
							}
							
							rowClassName={
								(record, index) => {
								  let className = ''
								  className = index % 2 ===0 ? 'ou' : 'ji'
								  return className
								}
							}
							loading={loading}
						/>
					</div>

				</div>
			</div>
		)
	}
	


export default Adjustable