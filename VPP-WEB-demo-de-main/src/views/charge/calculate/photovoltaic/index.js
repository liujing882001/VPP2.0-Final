import React,{useEffect,useState,useRef} from 'react'
import Icon from '@ant-design/icons';

import { Tree,DatePicker,ConfigProvider,Button,Table ,Input,message,Spin } from 'antd';
import type { DataNode, TreeProps } from 'antd/lib/tree';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import * as echarts from 'echarts';
import http from '../../../../server/server.js'
import axios from 'axios'
import type { DatePickerProps } from 'antd';
import { Resizable } from 're-resizable';

const { Search } = Input;
const { RangePicker } = DatePicker;

const dateFormat = 'YYYY-MM-YY';
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
			if( text==null||text==undefined||text==="" || text === 'null'){
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
		title: '预测（kW）',
		dataIndex: 'currentForecastValue',
		key: 'currentForecastValue',	
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===""){
				return '-'
			}else if(text=='-'){
				return '-'
			}else{
				return Number(text).toFixed(2)
			}
		}
	},
];
const Photovoltaic =({pCount}) =>{
	const [currentIndex, setCurrentIndex] = useState(0);
	const [current, setCurrent] = useState(1);
	const [dateType, setDateType] = useState(1);
	const [forecastType, setForecastType] = useState('PvForecasting');
	const [treeData, setTreeData] = useState([]);
	const [id, setId] = useState('');
	const [type, setType] = useState('');
	const [dates, setDates] = useState('');
	const [echartsdate, setEchartsdate] = useState([]);
	const [activePower, setActivePower] = useState([]);
	const [aiTimePrice, setAiTimePrice] = useState([]);
	const [number, setNumber] = useState('');
	const [expandedKeys, setExpandedKeys] = useState([]);
	const [searchValue, setSearchValue] = useState('');
	const [autoExpandParent, setAutoExpandParent] = useState(true);
	const [timeStamp, setTimeStamp] = useState([]);
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
	const [setwidth, setSetwidth] = useState('');
	const [startString, setStartString] = useState('');
	const [copyTree, setCopyTree] = useState([]); // 备份treeData
	const [copyExpandedKeys, setCopyExpandedKeys] = useState([]); // 备份展开key
	const [systemId, setSystemId] = useState('');
	const [page, setPage] = useState(1);
	const [currentForecastValue, setCurrentForecastValue] = useState([]);
	const [mediumTermForecastValue, setMediumTermForecastValue] = useState([]);
	const [realValue, setRealValue] = useState([]); // 实际值
	const [ultraShortTermForecastValue, setUltraShortTermForecastValue] = useState([]);
	const [nums, setNums] = useState('');
	const [currentNum, setCurrentNum] = useState(1);
	const [isEmpty, setIsEmpty] = useState(false);
	const [dataSource,setDataSource] = useState([]);
	const [nodeId,setNodeId] = useState('');
	const selectRef = useRef(null);
	const adjustableRef = useRef(null);
	const [loaded, setLoaded] = useState(false);
	const [pageHeight,setPageHeight] = useState('')
	
	useEffect(() =>{
		tabFn(1);
		tree();
		setPageHeight(document.body.clientHeight - 104);
	},[])
	
	useEffect(() =>{
		statements()
	},[timeStamp,ultraShortTermForecastValue,realValue,currentForecastValue,mediumTermForecastValue,isEmpty])
	useEffect(() =>{
		if (loaded&&systemId) {
			pvPredictionListPage()
		} else {
			setLoaded(true);
		}
	},[page,currentNum])
	const extractNodes =(data) => {
	    return data.map(item => {
			if (item.children) {
				return {
					...item,
					title: `${item.stationName}`,
					key: item.stationId,
					children: extractNodes(item.children),
					// icon:item.parentId == ""?<HeartIcon />:<SystemIcon  />
					
				};
			} else {
				return {
					...item,
					title: `${item.stationName}`,
					key: item.stationId,
					// icon:item.parentId == ""?<HeartIcon />:<SystemIcon  />
				};
			}
		});
	}
	// 树
	const tree =() =>{
		setTreeLoading(true)
		http.post('tree/areaPvForestShortView').then(res =>{
			if(res.data.code ==200){
				let data = res.data.data
				let number = 0
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
				let a = expandedKeysFun(res.data.data);
				setTreeData(res.data.data);
				setNumber(number);
				setTreeLoading(false);
				setTreeList(res.data.data);
				setCopyTree(res.data.data);
				setNums(true);
				
			}
		})
			
		// setTreeLoading(true)
		// http.post('stationNode/stationPageQuery',{
		// 	page:1,
		// 	size:100000,
		// 	query:'',
		// 	"keyword" : {
		// 		"stationTypeId" : [],
		// 		"stationState" : [],
		// 		"stationCategory":'pv'
		// 	}	
		// }).then(res =>{
		// 	console.log(res)
		// 	if(res.data.code==200){
		// 		const newData = extractNodes(res.data.data.content);
		// 		console.log(newData)
		// 		setNumber(newData.length);
		// 		setTreeData(newData);
		// 		setTreeLoading(false);
		// 		setTreeList(newData);
		// 		setCopyTree(newData);
		// 		setNums(true);
		// 	}
		// }).catch(err =>{
		// 	console.log(err)
		// })
		
	}
	
	const tabFn =(index) => {
		console.log(index)
		setCurrent(index);
		setDateType(index);
		setKeyValue(new Date());
		setDateString('');
		setDateStringend('');
	}
	const clsFn =(index, curCls, cls) => {
		let { current } = this.state;
		return current === index ? curCls : cls;
	}
	const dateChange =(data,dataString) =>{
		console.log(dataString)
		setDateString(dataString[0]);
		setDateStringend(dataString[1])
	}
	// 获取父节点
	const getParentNode =(tree, childId) => {
		
		// 遍历树节点
		for (let node of tree) {
	    // 如果当前节点就是目标节点的父节点，直接返回当前节点id
			if (node.children && node.children.some(child => child.key === childId)) {
				return node.id;
			}
			// 否则继续遍历当前节点的子节点
			if (node.children) {
				const parentId = getParentNode(node.children, childId);
				if (parentId !== null) {
					return parentId;
				}
			}
		}
		// 如果没有找到父节点，则返回null
		return null;
	}
	// 搜索
	const arround = () =>{
		
		
		if(dateString){
			setLoading(true);
			setPage(1);
			setCurrentNum(1);
			pvPredictionListPage()
			
			var chartDom = document.getElementById('statements');
			var myChart = echarts.init(chartDom);
			window.addEventListener("resize", () => {
				
				myChart.resize();
			});
			myChart.showLoading({
				text: '数据加载中...',
				color: '#FFF',
				textColor: '#FFF',
				maskColor: 'rgba(255, 255, 255, 0)',
				zlevel: 0
			});
			http.post('load_management/ai_prediction/pvPredictionChart',{
				"endTs": dateStringend,
				"nodeId": nodeId,
				"startTs": dateString,
				"systemId":systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					if(res.data.data){
						let data = res.data.data
						let	currentForecastValue = []	//日前
						let mediumTermForecastValue = []	//	中期预测
						let realValue = []	//实际
						let ultraShortTermForecastValue = []	//预测
						let timeStamp = []
						res.data.data.map(item =>{
							timeStamp.push(item.timeStamp)
							mediumTermForecastValue.push(item.mediumTermForecastValue)	//
							currentForecastValue.push(item.currentForecastValue)	//
							realValue.push(item.realValue)
							ultraShortTermForecastValue.push(item.currentForecastValue)
						})
						
						setTimeStamp(timeStamp);
						setUltraShortTermForecastValue(ultraShortTermForecastValue);
						setRealValue(realValue);
						setCurrentForecastValue(currentForecastValue);
						setMediumTermForecastValue(mediumTermForecastValue);
						setLoading(false);
						setIsEmpty(data.length>0?true:false)
					}else{
						setIsEmpty(false)
						myChart.hideLoading()
					}
					
				}else{
					message.info(res.data.msg)
				}
				
			})
			
		}else{
			message.info('请选择日期')
		}
		
	}
	const pvPredictionListPage=() =>{
		http.post('load_management/ai_prediction/pvPredictionListPage',{
			"endTs": dateStringend,
			"nodeId": nodeId,
			"number": page,
			"pageSize": 20,
			"startTs": dateString,
			"systemId":systemId
		}).then(res =>{
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
	
	const onSelect= (selectedKeys, info) => {
		if(info.node.type=='SYSTEM'){
			
			setId(getParentNode(treeData,info.node.key));
			setSystemId(info.node.id);
			setNodeId(getParentNode(treeData,info.node.key))
		}else{
			setSystemId('');
			setNodeId(info.node.id);
		}
		
		
		
		
	};
	const devices =() =>{
		if(dateString==""||id==""){
			debugger
			message.info('时间或节点不能为空')
		}else{
			axios({
				method: 'post',
				url: 'load_management/ai_prediction/pvPredictionListExcel',
				responseType: 'arraybuffer',
				data:{
					endTs: dateStringend,
					nodeId: id,
					startTs: dateString,
					systemId:systemId
				}
			}).then(res =>{
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
		var chartDom = document.getElementById('statements');
		var myChart = echarts.init(chartDom);
		
		myChart.setOption({
			backgroundColor:'#212029',
			color: ['#246CF9', '#00BF8F','#FFCF40','#6236FF'],
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
					var relVal = params[0].name;
					for (var i = 0, l = params.length; i < l; i++) {
						if(params[i].value===null||params[i].value===undefined||params[i].value==''||params[i].value=='-' || params[i].value=='null'){
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
			title: {
			    text: '单位：kW',
				left:20,
				textStyle:{
					color:'#FFF',
			　　　　 fontSize:14
				}
			},
			legend: {
				icon: 'rect',
				itemWidth: 12,
				itemHeight: 12,
				textStyle: { color: '#FFF'},
				x:120,
				y:'top',
			    data: ['实际', '预测']
			},
			grid: {
				top:40,
				left: '3%',
				right: '3%',
				bottom: '20%',
				containLabel: true
			},
			xAxis: [
				{
					type: 'category',
					axisTick: {
					        alignWithLabel: true
					},

					// boundaryGap: false,
					data:timeStamp,
					axisLabel:{//x坐标轴刻度标签
						show:true,
						color:'#FFF',//'#ccc'，设置标签颜色
						formatter: `{value}`
					},
					  
				}
			],
			yAxis: [
				{
					type: 'value',
					boundaryGap: [0, '100%'],
					axisLabel:{//x坐标轴刻度标签
						show:true,
						color:'#FFF',//'#ccc'，设置标签颜色
					},
					splitLine:{
						show:true,
						lineStyle:{
							type:'dashed',
							color:'#DFE1E5'
						}
				  }
					
				}
			],
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
					name: '预测',
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
					data:ultraShortTermForecastValue
				  // data: [140, 232, 101, 264, 90]
				},
			  ]
		});

		window.addEventListener("resize", () => {
			
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
				setNums(true)
			} else {
				const subs =arrayTreeFilter(node.children, predicate, filterText);
				
				if ((subs && subs.length) || predicate(node, filterText)) {
					node.children = subs;
					newChildren.push(node);
					
				}
				setNums(false)
			}
		}
		return newChildren;
	}
	 
	const filterFn = (data, filterText) => { 
		if (!filterText) {
			return true;
		}
		return (
			new RegExp(filterText, "i").test(data.title) 
		);
	}
	const flatTreeFun = (treeData) => { 
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
		let value = e;
		if (value == "") { 
			
			tree()
			setExpandedKeys(copyExpandedKeys);
			setNums(true)
		} else {
			 http.post('tree/runAreaPvForestShortView').then(res =>{
			 	if(res.data.code ==200){
			 		let data = res.data.data
			 		let number = 0
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
			 		let a = expandedKeysFun(res.data.data);
					setTreeData(res.data.data);
					setNumber(number);
					setTreeLoading(false);
					setTreeList(res.data.data);
					setCopyTree(res.data.data);
					setNums(true);
			 		let newres = arrayTreeFilter(copyTree, filterFn, value);
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
			 		let expkey = expandedKeysFun(newres);
			 		
					setTreeData(newres);
					setExpandedKeys(expkey);
					setNumber(num);
			 	}
			 })
			
		}
 
	}
	
		
	const onCheck: TreeProps['onCheck'] = (checkedKeys, info) => {
		console.log('onCheck', checkedKeys, info);
	};
	
	const onExpand = expandedKeys => {
		setExpandedKeys(expandedKeys);
		setAutoExpandParent(false)
	};
	// 分页
	const onChange: PaginationProps['onChange'] = page => {
		setPage(page.current)
		setCurrentNum(page.current)
		
	};
	const disabledDate: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023-01')) || current > dayjs().add('3','month')
		
	};
	const minWidth = 240;
	const maxWidth = 550
	return(
		<div className="adjustablecont" style={{display:'flex'}}>
			<Resizable
		      defaultSize={{
		        width: 300,
		      }}
			  maxWidth={maxWidth}
		      handleSize={[20, 20]}
		      minConstraints={[100, 100]}
		      maxConstraints={[500, 400]}
		      enable={{ top: false, right: true, bottom: false, left: false, topRight: false, bottomRight: false, bottomLeft: false, topLeft: false }}
		      onResize={(e, direction, ref, d) => {
				var chartDom = document.getElementById('statements');
				var myChart = echarts.init(chartDom);
				myChart.resize()
		      }}
		      onResizeStop={(e, direction, ref, d) => {
		        // console.log('Stop resizing:', ref, d);
		      }}
		    >
				<div className="navigation" style={{width:'100%',marginRight:'16px'}}>
					<div className="packingtion">
						<Search 
							style={{ marginBottom: 8,marginTop: 24 }}
							placeholder="查询节点" onSearch={onChanges} />
					</div>
					<p className="numbers">全部节点（{nums==true?number:nums==false?0:''}）</p>
					<Spin spinning={treeLoading}  size="middle">
						<Tree
						showIcon
						  defaultExpandAll
						  onSelect={onSelect}
						  onCheck={onCheck}
						  onExpand={onExpand}
						  expandedKeys={expandedKeys}
						  autoExpandParent={autoExpandParent}
						  treeData={treeData}
						  // treeData={treeData2}
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
				
				<div id="statements" style={{height:300,background:'#212029'}} ></div>
				<div style={{padding:15}} ref={selectRef}>
					<Table dataSource={dataSource} 
					columns={columns}
					scroll={{ y: 300 }}
					loading={loading}
					onChange={onChange}
					pagination={{
						total: total,//数据的总条数
						defaultCurrent: 1,//默认当前的页数
						defaultPageSize: 20,//默认每页的条数
						showSizeChanger:false,
						current:currentNum
					}}
					rowClassName={
					(record, index) => {
					  let className = ''
					  className = index % 2 ===0 ? 'ou' : 'ji'
					  return className
					}
				  }
					 
					 />
				</div>

			</div>
		</div>
	)
}
	


export default Photovoltaic