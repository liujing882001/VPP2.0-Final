import React,{useEffect, useState,useRef} from 'react'
import { Tree,DatePicker,ConfigProvider,Button,Table,message ,Spin ,Input,Icon   } from 'antd';
import './index.css'
import './index.scss'
import dayjs from 'dayjs';
// import http from '../../../server/server.js'
import axios from 'axios'
import * as echarts from 'echarts';
// import echarts  from '../../echarts.js'
const { RangePicker } = DatePicker;
const { TreeNode } = Tree;
const { Search } = Input;
let result = []
const dateFormat = 'YYYY-MM-DD';
const Condenser= () => {
	const [daylist,setDaylist] = useState(['负荷','电量']);
	const [currentState,setCurrentState] = useState(0);
	const [current,setCurrent] = useState(1);
	const [forecastType,setForecastType] = useState('PvForecasting');
	const [treeData,setTreeData] = useState([]);
	const [columns,setColumns] = useState([]);
	const [dataSource,setDataSource] = useState([]);
	const [dateString,setDateString] = useState('');
	const [nodeIds,setNodeIds] = useState([]);
	const [kw,setKw] = useState('kW');
	const [subModelList,setSubModelList] = useState([]);
	const [nodeId,setNodeId] = useState('');
	const [dateStringmonth,setDateStringmonth] = useState('');
	const [dateStringday,setDateStringday] = useState('');
	const [loading,setLoading] = useState(false);
	const [keyValue,setKeyValue] = useState('');
	const [monthValue,setMonthValue] = useState('');
	const [treeLoading,setTreeLoading] = useState(true);
	const [endTime,setEndTime] = useState('');
	const [startTime,setStartTime] = useState('');
	const [type,setType] = useState('');
	const [checkStrictly,setCheckStrictly] = useState(true);
	const [selectList,setSelectList] = useState([]);
	const [nodeIndex,setNodeIndex] = useState(1);
	const [nums,setNums] = useState('');
	const [searchValue,setSearchValue] = useState('');
	const [expandedKeys,setExpandedKeys] = useState([]);
	const [autoExpandParent,setAutoExpandParent] = useState(true);
	const [numtotal,setNumtotal] = useState('');
	const [copyTree,setCopyTree] = useState([]);
	const [copyExpandedKeys,setCopyExpandedKeys] = useState([]);
	const [titleName,setTitleName] = useState('最大负荷(kWh)');
	const [checkedKeys,setCheckedKeys] = useState([]);
	const [record,setRecord] = useState(1);
	const [chartList,setChartList] = useState([]);
	const [chartData,setChartData] = useState([]);
	const [allNumber,setAllNumber] = useState('');
	const [isEmpty,setIsEmpty] = useState(false);
	const [seriesdata,setSeriesdata] = useState([]);
	const [dateType,setDateType] = useState('');
	const [treeList,setTreeList] = useState([]);
	const [id,setId] = useState('')
	const [currentNum,setCurrentNum] = useState(1)
	// this.iconems = React.createRef();
	const iconemsRef = useRef(null);
	// ref={divRef} 
	useEffect(() =>{
		tabFn(1)
		typeLoadShortView()
	},[])
	useEffect(() =>{
		if(startTime&&endTime&&nodeIds){
			charts(chartList,seriesdata,isEmpty)
		}
	},[record])
	// 导出
	const derivation =() =>{
		if(currentState ==0){
			// 负荷
			axios({
				method: 'post',
				url: 'load_management/load_resources/analysisLoadListExcel',
				responseType: 'arraybuffer',
				data:{
					"nodeIds": nodeIds,
					"subModelList": subModelList,
					"endTime": endTime,
					"startTime": startTime,
				}
			}).then(res =>{
				if(res.status ==200){
					// alert(0)
					const url = window.URL.createObjectURL(new Blob([res.data]));
					const link = document.createElement('a'); //创建a标签
					link.style.display = 'none';
					link.href = url; // 设置a标签路径
					link.download = '负荷列表.xls'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
					document.body.appendChild(link);
					link.click();
					URL.revokeObjectURL(link.href); // 释放 URL对象
					document.body.removeChild(link);
					
				}else{
					message.info(res.data.msg)
				}
			})
			
		}else if(currentState ==1){
			// ,dateStringmonth,dateStringday
			axios({
				method: 'post',
				url: 'load_management/load_resources/analysisEnergyListExcel',
				responseType: 'arraybuffer',
				data:{
					"nodeIds": nodeIds,
					"subModelList": subModelList,
					"endTime": endTime,
					"startTime": startTime,
				}
			}).then(res =>{
				if(res.status ==200){
					// alert(0)
					const url = window.URL.createObjectURL(new Blob([res.data]));
					const link = document.createElement('a'); //创建a标签
					link.style.display = 'none';
					link.href = url; // 设置a标签路径
					link.download = '电量列表.xls'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
					document.body.appendChild(link);
					link.click();
					URL.revokeObjectURL(link.href); // 释放 URL对象
					document.body.removeChild(link);
					
				}else{
					message.info(res.data.msg)
				}
			})
			
		}
		
	}
	//搜索
	const around =() =>{		
		if(endTime&&startTime&&subModelList.length>0){
			setCurrentNum(1)
			var chartDom = document.getElementById('iconem');
			var myChart = echarts.init(chartDom);
			myChart.showLoading({
				text: '数据加载中...',
				color: '#FFF',
				textColor: '#FFF',
				maskColor: 'rgba(255, 255, 255, 0)',
				zlevel: 0
			});
			setLoading(true)
			if(currentState ==0){
				axios.post('load_management/load_resources/analysisLoadList',{
					"endTime": endTime,
					"startTime": startTime,
					"subModelList": subModelList
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						let tableData =res.data.data.data;
						if(tableData.length>0){
							let newArray = [];
							for (let i = 0; i < tableData.length; i++) {
								let item = tableData[i];
								let keys = Object.keys(item);
								for (let j = 0; j < keys.length; j++) {
								    let key = keys[j];
								    
								    // 如果键是以 "_能源总表" 结尾
								     if (key !== "时间" && key !== "合计") {
								      
								      // 获取新数组中已存在的项
								      let existingItem = newArray.find(obj => obj["name"] === key);
								      
								      // 如果已存在该项，则将当前项的值添加到已存在项中
								      if (existingItem) {
								        existingItem["value"].push(item[key]);
								      } else {
								        // 如果不存在该项，则创建新项并将当前项的值添加到新项中
								        newArray.push({ "name": key, "value": [item[key]] });
								      }
								    }
								}
							}
							
							console.log(newArray);
							let series = [];
							for (let i = 0; i < newArray.length; i++) {
							    let item = newArray[i];
							    let seriesData = [];
							    for (let j = 0; j < item.value.length; j++) {
							        seriesData.push(parseFloat(item.value[j]));
							    }
								
								if( item.name.indexOf("系统") !== -1){
									series.push({
									    // name: item.name.indexOf("_") !== -1?item.name.substring(0, index):item.name,
										 name: item.name,
									    type: 'line',
									    data: seriesData
									});
								}else{
									const index = item.name.indexOf("_");
									series.push({
									    name: item.name.indexOf("_") !== -1?item.name.substring(0, index):item.name,
										 // name: item.name,
									    type: 'line',
									    data: seriesData
									});
								}
							    
							}
							console.log(series);
							
							let dataSource = []
							let chartList = []
							// let chartData = []
							tableData.forEach((e, index) => {		
								// console.log(e)
								dataSource.push(e)
								chartList.push(e.时间)
								// chartData.push(e.合计)
							})
							console.log(dataSource)
							console.log(chartList)
							console.log(series)
							setDataSource(dataSource)
							setLoading(false)
							setNumtotal(res.data.data.total?Number(res.data.data.total).toFixed(2):'');
							setChartList(chartList)
							setSeriesdata(series)
							setIsEmpty(true)
							charts(chartList,series,true)
						}else{
							let chartList = []
							let series = []
							setDataSource([])
							setLoading(false)
							setNumtotal('-');
							setChartList([])
							setSeriesdata([{
								data: [],
								type: 'line'
							}])
							setIsEmpty(false)
							charts(chartList,series,true)
						}
						
					}else{
						
						var chartDom = document.getElementById('iconem');
						var myChart = echarts.init(chartDom);
						myChart.clear()
						// let 
						let that = this
						var emptyData = [];
						emptyData.push({
							data: [],
							type: 'line'
						})
						
						let chartList = []
						setDataSource([])
						setLoading(false)
						setNumtotal('-');
						setChartList([])
						setSeriesdata([{
							data: [],
							type: 'line'
						}])
						setIsEmpty(false)
						charts(chartList,emptyData,false)
						
					}
				})
				
				
			}else if(currentState ==1){
				axios.post('load_management/load_resources/analysisEnergyList',{
					"endTime": endTime,
					"startTime": startTime,
					"subModelList": subModelList
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						let tableData =res.data.data.data;
						if(tableData.length>0){
							let newArray = [];
							for (let i = 0; i < tableData.length; i++) {
								let item = tableData[i];
								let keys = Object.keys(item);
								for (let j = 0; j < keys.length; j++) {
								    let key = keys[j];
								    
								    // 如果键是以 "_能源总表" 结尾
								     if (key !== "时间" && key !== "合计") {
								      
								      // 获取新数组中已存在的项
								      let existingItem = newArray.find(obj => obj["name"] === key);
								      
								      // 如果已存在该项，则将当前项的值添加到已存在项中
								      if (existingItem) {
								        existingItem["value"].push(item[key]);
								      } else {
								        // 如果不存在该项，则创建新项并将当前项的值添加到新项中
								        newArray.push({ "name": key, "value": [item[key]] });
								      }
								    }
								}
							}
							
							console.log(newArray);
							let series = [];
							for (let i = 0; i < newArray.length; i++) {
							    let item = newArray[i];
							    let seriesData = [];
							    for (let j = 0; j < item.value.length; j++) {
							        seriesData.push(parseFloat(item.value[j]));
							    }
								
								if( item.name.indexOf("系统") !== -1){
									series.push({
									    // name: item.name.indexOf("_") !== -1?item.name.substring(0, index):item.name,
										name: item.name,
									    type: 'line',
									    data: seriesData
									});
								}else{
									const index = item.name.indexOf("_");
									series.push({
									    name: item.name.indexOf("_") !== -1?item.name.substring(0, index):item.name,
										 // name: item.name,
									    type: 'line',
									    data: seriesData
									});
								}
							    
							}
							console.log(series);
							let dataSource = []
							let chartList = []
							// let chartData = []
							
							tableData.forEach((e, index) => {		
								console.log(e)
								dataSource.push(e)
								chartList.push(e.时间)
								// chartData.push(e.合计)
							})
							console.log(dataSource)
							var chartDom = document.getElementById('iconem');
							var myChart = echarts.init(chartDom);
							myChart.clear()
							
							setDataSource(dataSource)
							setLoading(false)
							setNumtotal(res.data.data.total?Number(res.data.data.total).toFixed(2):'');
							setChartList(chartList)
							setSeriesdata(series)
							setIsEmpty(true)
							charts(chartList,series,true)
						}else{
							var chartDom = document.getElementById('iconem');
							var myChart = echarts.init(chartDom);
							myChart.clear()
							// let 
							let that = this
							var emptyData = [];
							emptyData.push({
								data: [],
								type: 'line'
							})
							// that.setState({
							// 	dataSource:[],
							// 	chartList:[],
							// 	loading:false,
							// 	seriesdata:emptyData,
							// 	numtotal:'-',
							// 	isEmpty:false
							// },() =>{
							// 	console.log(this.state.seriesdata)
							// 	that.charts()
							// })
							let chartList = []
							setDataSource([])
							setLoading(false)
							setNumtotal(res.data.data.total?Number(res.data.data.total).toFixed(2):'');
							setChartList(chartList)
							setSeriesdata(emptyData)
							setIsEmpty(false)
							charts(chartList,emptyData,false)
						}
						
					}else{
						var chartDom = document.getElementById('iconem');
						var myChart = echarts.init(chartDom);
						myChart.clear()
						// let 
						let that = this
						var emptyData = [];
						emptyData.push({
							data: [],
							type: 'line'
						})
						let chartList = []
						setDataSource([])
						setLoading(false)
						setNumtotal(res.data.data.total?Number(res.data.data.total).toFixed(2):'');
						setChartList(chartList)
						setSeriesdata(emptyData)
						setIsEmpty(false)
						charts(chartList,emptyData,false)
					}
				})
			}
		}else if(endTime==''){
			message.info('请选择时间')
		}else if(subModelList.length==0){
			message.info('请选择节点')
		}
			
			
		
	}
	//左侧tree
	const tree=() =>{
		axios.post('load_management/load_resources/runAreaLoadShortView').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				let num = 0
				function mapTree (data) {
				      data.forEach(items => {    //遍历树 拼入相应的disabled
				        if (items.type === null) {
				          items.disabled = true
				        }
				        if (items.children) {
							// console.log(items)
							if(items.type=='NODE'){
								num++
								console.log(1)
							}
				          mapTree(items.children)
				        }
				      })
				}
				
				mapTree(data)	
				let data2  = []
				let obj1 = new Object()
				obj1['title'] = '全部节点'+'（'+ num +'）'
				obj1['key'] = '0-1'
				data2.push(obj1)
				data2[0].children= res.data.data
				console.log(data2)
				
				
				let data1  = []
				let obj = new Object()
				obj['title'] = '全部节点'+'（'+ num +'）'
				obj['key'] = '0-1'
				data1.push(obj)
				data1[0].children= res.data.data
				console.log(data2)
				console.log(data)
				
				setTreeData(data);
				setTreeLoading(false);
				setCopyTree(data);
				setAllNumber(num);
			}
		})
	}
	// 选择时间范围
	const dataChange =(data,dataString) =>{
		console.log(dataString)
		setStartTime(dataString[0])
		setEndTime(dataString[1])
	}
	const chosedate=(e) =>{
		console.log(e)
		
		setCurrentState(e)
		setTitleName(e==0?'最大负荷(kW)':'累计电量 (kWh)')
		charts(chartList,seriesdata,isEmpty)
	}
	const tabFn=(index) =>{
		console.log(index)
		setCurrent(index);
		setDateType(index);
		setKeyValue(new Date());
	}
	const clsFn =(index, curCls, cls)=> {
		let { current } = this.state;
		// console.log(current)
		return current === index ? curCls : cls;
	}
	// 选择节点类型
	const onNodeType =(e) =>{
		console.log(e)
		
		setNodeIndex(e)
		setTreeLoading(true)
		if(e==1){
			// 节点类型
			typeLoadShortView()
		}else if(e==2){
			// 节点位置
			tree()
		}
	}
	// 节点类型
	const typeLoadShortView=() =>{
		axios.post('load_management/load_resources/typeLoadShortView').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let num = 0
				function mapTree (data) {
				      data.forEach(items => {    //遍历树 拼入相应的disabled
				        if (items.type === null) {
				          items.disabled = true
				        }
				        if (items.children) {
							// console.log(items)
							if(items.type=='NODE'){
								num++
							}
				          mapTree(items.children)
				        }
				      })
				}
				
				mapTree(data)	
				let data1  = []
				let obj = new Object()
				// obj['title'] = '全部节点'+'（'+ num +'）'
				// obj['key'] = '0-1'
				data1.push(obj)
				data1[0].children= res.data.data
				console.log(data1)
				// console.log(num)
				
				setTreeData(data)
				setTreeLoading(false)
				setTreeList(data)
				setAllNumber(num)
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}

	const onCheck: TreeProps['onCheck'] = (checkedKeys, info) => {
		let that = this
		let news = 'PvStatistics';
	    console.log('onCheck', checkedKeys, info);
		let checkedNodes = info.checkedNodes
		console.log(checkedNodes)
		
		let columns =[{
				title: '时间',
				dataIndex: '时间',
				key: '时间',
				width: 180,
			}]
		let newList = []
		let chartData = []
		if(info.checked==true){
			newList.push(info.node)
		}
		console.log(newList,'newList')
		// let result = []
		if(checkedKeys.checked.length>5){
			message.info('最多显示5条');
			
			return false
		}else{
			
			function getParentNode(tree, childId) {
				
				// 遍历树节点
				for (let node of tree) {
			    // 如果当前节点就是目标节点的父节点，直接返回当前节点id
					if (node.children && node.children.some(child => child.key === childId)) {
						// return node.id;
						return result.push({
							id:node.id,
							title:node.title,
							children:node.children,
							type:node.type
						})
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
			let subModelList = []
			function xxx(arr) {
				let list = [];
				return arr.filter((item) => !list.includes(item.id) && list.push(item.id))
			}
			console.log(result)
			let selectId = getParentNode(treeData, info.node.key)
			console.log(selectId)
			console.log(checkedNodes,'checkedNodes')
			let result1 =  xxx(result)
			console.log('newList9',result1);
			if(checkedNodes.length==0){
				
				setNumtotal('')
				setDataSource([])
			}
			for(var i=0;i<checkedNodes.length;i++){
				
				// console.log(checkedNodes[i].id)
				if(checkedNodes[i].type=='SYSTEM'){
					for(var j=0;j<result1.length;j++){
						if(result1[j].children){
							let children = result1[j].children
							for(var a=0;a<children.length;a++){
								if(checkedNodes[i].key==children[a].key){
									// alert(0)
									// console.log(children[a])
									// console.log(result1[j])
									subModelList.push({
										nodeId:result1[j].id,
										systemId:children[a].id,
										type:children[a].type
									})
									columns.push({
										title: result1[j].title+'_'+checkedNodes[i].title,
										dataIndex: result1[j].title+'_'+checkedNodes[i].title,
										key: checkedNodes[i].key,
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
									})
									chartData.push(result1[j].title+'_'+checkedNodes[i].title)
								}
							}
						}
					}
				}else if(checkedNodes[i].type=='NODE'){
					// alert(0)
					columns.push({
						title: checkedNodes[i].title,
						dataIndex: checkedNodes[i].title+'_'+'能源总表',
						key: checkedNodes[i].title,
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
					})
					chartData.push(checkedNodes[i].title)
					subModelList.push({
						"nodeId": checkedNodes[i].id,
						"systemId": "nengyuanzongbiao",
						"type": checkedNodes[i].type
					})
				}else{
					columns.push({
						title: checkedNodes[i].title,
						dataIndex: checkedNodes[i].title,
						key: checkedNodes[i].title,
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
					})
					chartData.push(checkedNodes[i].title)
					subModelList.push({
						"nodeId": checkedNodes[i].id,
						"systemId": "",
						"type": checkedNodes[i].type
					})
				}
				
			}
			setSubModelList(subModelList)
			setColumns(columns)
			setChartData(chartData)
			charts()
		}
		setCheckedKeys(checkedKeys)
		
		
	};
	const arrayTreeFilter = (data, predicate, filterText) => {
	       const nodes = data;
				// console.log(nodes)
	       // 如果已经没有节点了，结束递归
	       if (!(nodes && nodes.length)) {
	           return;
	       }
	       const newChildren = [];
	       for (const node of nodes) {
	           if (predicate(node, filterText)) {
	               // 如果自己（节点）符合条件，直接加入到新的节点集
	               newChildren.push(node);
	               
	           } else {
	               
	               const subs = arrayTreeFilter(node.children, predicate, filterText);
	               
	               if ((subs && subs.length) || predicate(node, filterText)) {
	                   node.children = subs;
	                   newChildren.push(node);
	               }
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
	const onChanges = (e) => { //搜索框 change事件
				console.log(e)
		// let {nodeIndex} = this.state
			if(nodeIndex==2){
				// 节点位置
				let value = e;
				if (value == "") { //为空时要回到最初 的树节点
					console.log(copyTree)
					setExpandedKeys(copyExpandedKeys)
					tree()
				} else {
					// let { copyTree, copyExpandedKeys } = this.state;
					console.log(copyTree)
					axios.post('load_management/load_resources/runAreaLoadShortView').then(res =>{
						// console.log(res)
						if(res.data.code ==200){
							let data = res.data.data
							let num = 0
							function mapTree (data) {
							      data.forEach(items => {    //遍历树 拼入相应的disabled
							        if (items.type === null) {
							          items.disabled = true
							        }
							        if (items.children) {
										// console.log(items)
										if(items.type=='NODE'){
											num++
											console.log(1)
										}
							          mapTree(items.children)
							        }
							      })
							}
							
							mapTree(data)	
							let data2  = []
							let obj1 = new Object()
							obj1['title'] = '全部节点'+'（'+ num +'）'
							obj1['key'] = '0-1'
							data2.push(obj1)
							data2[0].children= res.data.data
							console.log(data2)
							
							
							let data1  = []
							let obj = new Object()
							obj['title'] = '全部节点'+'（'+ num +'）'
							obj['key'] = '0-1'
							data1.push(obj)
							data1[0].children= res.data.data
							console.log(data2)
							setTreeData(data1)
							setTreeLoading(false)
							setCopyTree(data2)
							let res1 = arrayTreeFilter(copyTree, filterFn, value);
							let expkey = expandedKeysFun(res1);
							console.log(res1)
							let nums = 0
							function mapTree1 (data) {
							    data.forEach(items => {    //遍历树 拼入相应的disabled
							        
							        if (items.children) {
										console.log(items)
										if(items.type=='NODE'){
											nums++
										}
							          mapTree1(items.children)
							        }
							    })
							}
							
							mapTree1(res1)	
							res1.forEach((item,index)=>{
								console.log(item)
								if(item.key=='0-1'){
									 item.title='全部节点'+'（'+ nums +'）'
								}
							})
							console.log(res1)
							setTreeData(res1)
							setExpandedKeys(expkey)
							
						}
					})
					
				}
			}else if(nodeIndex==1){
				let value = e;
				if (value == "") { //为空时要回到最初 的树节点
					
					
					setExpandedKeys(copyExpandedKeys)
					typeLoadShortView()
				} else {
				
					axios.post('load_management/load_resources/typeLoadShortView').then(res =>{
						console.log(res)
						if(res.data.code==200){
							let data = res.data.data
							let num = 0
							function mapTree (data) {
							      data.forEach(items => {    //遍历树 拼入相应的disabled
							        if (items.type === null) {
							          items.disabled = true
							        }
							        if (items.children) {
										// console.log(items)
										if(items.type=='NODE'){
											num++
										}
							          mapTree(items.children)
							        }
							      })
							}
							
							mapTree(data)	
							let data1  = []
							let obj = new Object()
							obj['title'] = '全部节点'+'（'+ num +'）'
							obj['key'] = '0-1'
							data1.push(obj)
							data1[0].children= res.data.data
							console.log(data1)
							setTreeData(data1)
							setTreeLoading(false)
							setTreeList(data1)
							let res1 = arrayTreeFilter(treeList, filterFn, value);
							let expkey = expandedKeysFun(res1);
							let nums = 0
							function mapTree1 (data) {
							    data.forEach(items => {    //遍历树 拼入相应的disabled
							        
							        if (items.children) {
										console.log(items)
										if(items.type=='NODE'){
											nums++
										}
							          mapTree1(items.children)
							        }
							    })
							}
							
							mapTree1(res1)	
							res1.forEach((item,index)=>{
								console.log(item)
								if(item.key=='0-1'){
									 item.title='全部节点'+'（'+ nums +'）'
								}
							})
							
							setTreeData(res1)
							setExpandedKeys(expkey)
						}else{
							message.info(res.data.msg)
						}
					}).catch(err =>{
						console.log(err)
					})
					
				}
			}
	       
	
	}
	// 
	const Recordata =(index) =>{
		console.log(index)
		console.log(chartList)
		console.log(seriesdata)
		setRecord(index)
		charts(chartList,seriesdata,false)
	}
	const charts=(chartList,seriesdata,isEmpty) =>{
		console.log(chartList,seriesdata,isEmpty)
		// console.log(this.iconems.current.offsetWidth )
		const chartContainer = document.getElementById('chart-container');
		// 获取图表容器的初始宽度
		const containerWidth = chartContainer.offsetWidth;
		console.log(containerWidth)
		var chartDom = document.getElementById('iconem');
		chartDom.style.width = iconemsRef.current.offsetWidth + 'px';
		
		// 重绘图表
		
		var myChart = echarts.init(chartDom);
		var option;
		
		option = {
			title: {
			    // text: '单位：'+currentState=='1'?'kWh':currentState=='0'?'KW':'',
				x:30,
				y:0,
				textStyle: { 
					fontSize: 12,
					color:'#FFF',
					
				},
				
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
					    top: "235", // 相对父元素上下的位置
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
			legend: {
			    data: chartData,
				icon:'rect',
				itemWidth: 12, // 图例标记的图形宽度。
					itemHeight:12,
					textStyle: {
					color: '#FFF'          // 图例文字颜色
				}
			},
			grid: {
				left: 0,
				right: 25,
				bottom: 40,
				containLabel: true,
				top:50
			},
			tooltip: {
				trigger: "axis",
				backgroundColor: '#302F39',
				borderColor: 'transparent',
				textStyle: {
					color: '#fff' // 设置 tooltip 的文字颜色为白色
				},
				formatter: function (params) {
					
					let str = params[0].axisValue + "</br>"
					params.forEach((item) => {
						// console.log(item)
						if(item.data){
							str+=item.marker+item.seriesName+ " : " + (parseFloat(item.data).toFixed(2))+ "</br>" 
						}else{
							str+=item.marker+item.seriesName+ " : " +'-'+ "</br>" 
						}
					  
					});
					return str;
				},
			},
			xAxis: {
				type: 'category',
				axisLine: {
							//坐标轴轴线相关设置
					lineStyle: {
					  color: "#DFE1E5", //x轴线颜色设置
					},
				},
				axisLabel: {
					// 坐标轴刻度标签的相关设置
					show: true, //控制显隐
					textStyle: {
					  color: "#FFF", //x轴字体颜色
					},
					// interval: 10,
				},
				axisTick: {
					//x轴刻度相关设置
					alignWithLabel: true,
				},
				data: chartList
			  },
			  yAxis: {
				type: 'value',
				axisLabel : {
					formatter: '{value}',
					textStyle: {
						color: '#FFF'
					}
				},
				splitLine: {
					//网格线
					lineStyle: {
					  type: "dashed", //设置网格线类型 dotted：虚线   solid:实线
					  width: 1,
					  color:"#DFE1E5"
					},
					show: true, //隐藏或显示
				}
			  },
			  series: seriesdata
		};
		
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			const chartContainer = document.getElementById('chart-container');
			// 获取图表容器的初始宽度
			// const containerWidth = chartContainer.offsetWidth;
			// console.log(containerWidth)
			// chartDom.style.width = containerWidth + 'px';
			document.getElementById('iconem').style.width = iconemsRef.current.offsetWidth +'px'
			myChart.resize()
			
		})
		myChart.resize();
		myChart.hideLoading()
	}
	
		
		
	const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
		console.log('selected', selectedKeys, info);
	};
	const renderTree = (data: any) => {
		// console.log(data)
		if (data && data.length > 0) {
			return data.map((item: any) => {
				return (
					<TreeNode checkable={item.type=='SYSTEM'||item.type=="NODE"||item.type=='DEVICE'?true:false} 
					id={item.id}
					load={item.load}
					type={item.type} title={item.title} key={item.key}>
						{item.children && renderTree(item.children)}
					</TreeNode>
				);
			});
		} else {
			// return (
			//     <TreeNode>
			//         {data.map((item: any) => {
			//             return (
			//                 <TreeNode
			//                     title={item.title}
			//                     key={item.key}
			//                     // className={s.treeNodeChild}
			//                 ></TreeNode>
			//             );
			//         })}
			//     </TreeNode>
			// );
		}
	};
		
		const onExpand = (expandedKeys,info) => {
			// let list = 
			console.log(expandedKeys)
			console.log(info)
			setExpandedKeys(expandedKeys)
			setAutoExpandParent(false)
			setId(id)
		   
		  };
		
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current < dayjs(new Date('2023-01-01')) || current > dayjs().endOf('day')
			
		};
		const tableChange =(page) =>{
			console.log(page)
			setCurrentNum(page)
		}
		return(
			<div className="allcontent12 condenser" style={{height: 'calc(100% - 15px)'}}>
				<div className="navigation" style={{width:310,marginRight:'16px'}}>
				    <div className="navigationsearch">
						<Search style={{ marginBottom: 8 }} onSearch={onChanges} placeholder="搜索相关信息" />
					</div>

					<ul className="nodeTypes">
						<li onClick={() => onNodeType(1)} className={[nodeIndex===1?"nodeactive":'']}>节点类型</li>
						<li onClick={() =>onNodeType(2)} className={[nodeIndex===2?"nodeactive":'']}>节点位置</li>
					</ul>
					<Spin spinning={treeLoading}  size="middle">
						<div className="Responsebenefits-title">
							<span>全部节点 ({allNumber})</span>
						</div>
						<Tree
							checkable
							defaultExpandAll
							onExpand={onExpand}
							onSelect={onSelect}
							onCheck={onCheck}
							treeData={treeData}
							checkStrictly ={checkStrictly}
							checkedKeys={checkedKeys}
							loading
							expandedKeys={expandedKeys}
							autoExpandParent={autoExpandParent}
						>
							{renderTree(treeData)}
						</Tree>
					</Spin>
				</div>
				<div className="adjustable" style={{marginLeft:316}}>
					
					<div className="adjustableheader headeradgust">
						<ul className="daylists">
							{
								daylist.map((i,index) =>{
									return <li className={index===currentState?"active":null} key={index} onClick={()=>chosedate(index)} >{i}</li>
								})
							}
						</ul>
						<RangePicker disabledDate={disabledDate} onChange={dataChange} />
						<Button type="primary" onClick={around}>查询</Button>
							
							
							
					</div>
					
					<div className="danweiTable">
						<div className="danwei" style={{marginRight:16}}>{titleName}：<span>{numtotal}</span>
							
						</div>
						<div className="incomes" style={{margin:'0px 16px'}}>
							<div className="incomeList">
								<span onClick={() => { Recordata(1) }} className={record==1?'recirdactive':''}>数据</span>
								<span onClick={() => { Recordata(2) }} className={record==2?'recirdactive':''}>图表</span>
							</div>
							
						</div>
						<div style={{padding:'0px 16px'}} id="chart-container">
							<div className="alliconemName"   style={{display:(record==1)?'block':'none'}}>
								
								<Table dataSource={dataSource}
									columns={columns}
									loading={loading}
									
									pagination={
										{
										  // total: this.state.total,//数据的总条数
										  // defaultCurrent: 1,//默认当前的页数
										  // defaultPageSize: 100,//默认每页的条数
										  showSizeChanger:false,
										  current:currentNum,
										  onChange: tableChange
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
							
							<div className="alliconemName" ref={iconemsRef}   style={{display:(record==2)?'block':'none',width:'100%'}}>
								<div className="iconemName">单位：{currentState==1?'kWh':'kW'}</div>
								<div id="iconem"></div>
							</div>
						</div>
					</div>
				</div>
				
			</div>
		)
	
	
}
export default Condenser

// <Button ghost onClick={this.devices}>导出</Button>








