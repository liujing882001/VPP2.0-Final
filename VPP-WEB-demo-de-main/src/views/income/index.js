import React,{useEffect,useState} from 'react'
// import 'antd/dist/antd.css'
// import 'antd/dist/antd.min.css';
import './index.css'
import { Tree,DatePicker,ConfigProvider,Button,Table,Input  ,Select ,Typography ,message,Spin } from 'antd';
import type { DataNode, TreeProps } from 'antd/lib/tree';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import http from '../../server/server.js'
import axios from 'axios'
import * as echarts from 'echarts';
import type { DatePickerProps } from 'antd';
import Index from '../Index';
import './index.scss'
const { Text } = Typography;
const { RangePicker } = DatePicker;

const { Search } = Input;
const { Option } = Select;

const dateFormat = 'YYYY-MM-DD';


const Income = () =>{
	const [currentIndex, setCurrentIndex] = useState(0);
	const [current, setCurrent] = useState(1);
	const [dateType, setDateType] = useState(1);
	const [expandedKeys, setExpandedKeys] = useState([]);
	const [searchValue, setSearchValue] = useState('');
	const [autoExpandParent, setAutoExpandParent] = useState(true);
	const [navList, setNavList] = useState(['全部', '虚拟电厂运营商', '电力用户']);
	const [treeData, setTreeData] = useState([]);
	const [profitTypeList, setProfitTypeList] = useState([]);
	const [code, setCode] = useState('0');
	const [nodeId, setNodeId] = useState([]);
	const [startTs, setStartTs] = useState('');
	const [endTs, setEndTs] = useState('');
	const [columns, setColumns] = useState([]);
	const [dataSource, setDataSource] = useState([]);
	const [loading, setLoading] = useState(false);
	const [treeLoading, setTreeLoading] = useState(false);
	const [checkedKeys, setCheckedKeys] = useState([]);
	const [record, setRecord] = useState(1);
	const [chartList, setChartList] = useState([]);
	const [chartData, setChartData] = useState([]);
	const [infrx, setInfrx] = useState('');
	const [newindex, setNewindex] = useState('');
	const [isEmpty, setIsEmpty] = useState(false);
	const [chartData1, setChartData1] = useState([]);
	
	useEffect(() =>{
		tabFn(1)
		getprofitTypeList()
	},[]);
	useEffect(() =>{
		tree()
	},[code]);
	
	useEffect(() =>{
		if(newindex>0){
			charts()
		}
		if(infrx>0){
			charts()
		}
	},[newindex,infrx,dataSource])
	useEffect(() =>{
		charts()
	},[record,currentIndex])
	// 收益类型
	const getprofitTypeList =() =>{
		http.post('profit_management/node_profit/profitTypeList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				
				setProfitTypeList(res.data.data)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	//左侧tree
	const tree=() =>{
		setTreeLoading(true)
		http.post('profit_management/node_profit/runAreaNodeNameListByProfitType?code='+code).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				// data.length &&data.map(res =>{
				// 	return res.title = res.nodeName,res.key = res.id
				// })
				console.log(data)
				
				setTreeData(data);
				setTreeLoading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
		
	}
	// 选择数据
	const Recordata =(index) =>{
		
		setRecord(index);
	}
	// 导出
	const devices =() =>{
		if(current==1){
			// 年
			if(nodeId==""){
				message.info('节点不能为空')
			}else if(endTs =="" || endTs==""){
				message.info('时间不能为空')
			}else{
				axios({
					method: 'post',
					url: 'profit_management/node_profit/getYearProfitListExcel',
					responseType: 'arraybuffer',
					data: {
							"endTs": endTs,
							"nodeId": nodeId,
							"startTs": startTs,
							"userType": currentIndex==0?'USER_ALL':currentIndex==1?'LOAD_INTEGRATOR':currentIndex==2?'CONSUMER':''
					}
				}).then(res => {
					if (res.status == 200) {
						const url = window.URL.createObjectURL(new Blob([res.data]));
						const link = document.createElement('a'); //创建a标签
						link.style.display = 'none';
						link.href = url; // 设置a标签路径
						link.download = '报表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
						document.body.appendChild(link);
						link.click();
						URL.revokeObjectURL(link.href); // 释放 URL对象
						document.body.removeChild(link);
				
					}else{
						message.info(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
				
			}
			
		}else if(current==2){
			if(nodeId==""){
					message.info('节点不能为空')
				}else if(endTs =="" || endTs==""){
					message.info('时间不能为空')
				}else{
					axios({
						method: 'post',
						url: 'profit_management/node_profit/getMonthProfitListExcel',
						responseType: 'arraybuffer',
						data: {
								"endTs": endTs,
								"nodeId": nodeId,
								"startTs": startTs,
								"userType": currentIndex==0?'USER_ALL':currentIndex==1?'LOAD_INTEGRATOR':currentIndex==2?'CONSUMER':''
						}
					}).then(res => {
						if (res.status == 200) {
							const url = window.URL.createObjectURL(new Blob([res.data]));
							const link = document.createElement('a'); //创建a标签
							link.style.display = 'none';
							link.href = url; // 设置a标签路径
							link.download = '报表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
							document.body.appendChild(link);
							link.click();
							URL.revokeObjectURL(link.href); // 释放 URL对象
							document.body.removeChild(link);
					
						}else{
							message.info(res.data.msg)
						}
					}).catch(err =>{
						console.log(err)
					})
			}
		}else{
			if(nodeId==""){
					message.info('节点不能为空')
				}else if(endTs =="" || endTs==""){
					message.info('时间不能为空')
				}else{
					axios({
						method: 'post',
						url: 'profit_management/node_profit/getDayProfitListExcel',
						responseType: 'arraybuffer',
						data: {
								"endTs": endTs,
								"nodeId": nodeId,
								"startTs": startTs,
								"userType": currentIndex==0?'USER_ALL':currentIndex==1?'LOAD_INTEGRATOR':currentIndex==2?'CONSUMER':''
						}
					}).then(res => {
						if (res.status == 200) {
							const url = window.URL.createObjectURL(new Blob([res.data]));
							const link = document.createElement('a'); //创建a标签
							link.style.display = 'none';
							link.href = url; // 设置a标签路径
							link.download = '报表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
							document.body.appendChild(link);
							link.click();
							URL.revokeObjectURL(link.href); // 释放 URL对象
							document.body.removeChild(link);
					
						}else{
							message.info(res.data.msg)
						}
					}).catch(err =>{
						console.log(err)
					})
			}
		}
				
	}
	// 搜索
	const onSearch =() =>{
		if(nodeId==""){
			message.info('节点不能为空')
		}else if(endTs =="" || endTs==""){
			message.info('时间不能为空')
		}else{
			
			setLoading(true)
			if(current ==2){
				http.post('profit_management/node_profit/getMonthProfitList',{
					"endTs": endTs,
					"nodeId":nodeId,
					"startTs": startTs,
					"userType": currentIndex==0?'USER_ALL':currentIndex==1?'LOAD_INTEGRATOR':currentIndex==2?'CONSUMER':''
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						
						setLoading(false)
						let data = res.data.data
						let columns = []
						let dataSource = []
						let list = [];
						let num={};
						if(data.length >0){
							data.forEach((item,index) =>{
								console.log(item)
								dataSource.push(item)
							})
							for(var i=0;i<data.length-1;i++){
								for(var key in data[i]){
									var temp = {}
									var newarry = {}
									temp.name = key;
									temp.value = data[i][key];
									list.push(temp)
								}
							}
							for(var i=0;i<dataSource.length;i++){
								// if(dataSource[i].虚拟电厂运营商){
								// 	dataSource.forEach((item,index) => {
								// 		console.log(item)
								// 		// if(item.负荷聚合商){
								// 		// 	delete item.节点;
								// 		// }
								// 	})
								// }
							}
							
							console.log(list)
							console.log(list.push(list.shift()))		
							list.map(res =>{
								columns.push({
									title:res?.name,
									dataIndex:res?.name
								})
							})
							console.log(columns)
							// if()
							for(var i=0;i<columns.length;i++)
							var newArr = []
							let arrId = []
							for(let item of columns){
							    if(arrId.indexOf(item['title']) == -1){
							        arrId.push(item['title'])
							        newArr.push(item)
							    }
							}
							console.log(arrId)
							console.log(newArr)
							let ceshi;
							for(var i = 0;i<newArr.length;i++){
							   if(newArr[i]?.title == "合计"){
								   // newArr.splice(0, 0,newArr[j]);
								   // newArr.splice(j+1,1);
									ceshi = newArr[i] //声明一个对象保存符合要求的数据
									// newArr.splice(0, 0,newArr[i]);
									newArr.splice(i,1)//先把符合条件的数据从当前数组中删除
									break;
							   }
							}
							
							newArr.push(ceshi)//通过unshift函数把符合要求的数据放到第一位
							console.log(newArr);
							newArr.sort(function (a, b) {
								return a.title > b.title ? 1 : -1;
							})
							console.log(newArr)
							let chartList = []
							for(var i = 0;i<newArr.length;i++){
								chartList.push(newArr[i])
							   if(newArr[i]?.title == "合计"){
									newArr.splice(0, 0,newArr[i]);
									newArr.splice(i+1,1);
							   }
							 }
							for(var j = 0;j<newArr.length;j++){
								if(newArr[j]?.title =='节点'){
									newArr.splice(0, 0,newArr[j]);
									newArr.splice(j+1,1);
								}
							}
							for(var i = 0;i<newArr.length;i++){
							   if(newArr[i]?.title =='虚拟电厂运营商'||newArr[i]?.title =='电力用户'){
							   	// newArr.splice(1, 0,newArr[i]);
							   	newArr.splice(i, 1);
							   	break;
							   }
							 }
							newArr.sort((a, b) => {
							    // 将键名为 "合计" 的对象元素移动到数组的最后
							    if (a.dataIndex === "合计") {
							        return 1;
							    }
							    if (b.dataIndex === "合计") {
							        return -1;
							    }
							  
							    return 0;
							});
							function removeTotals(arr) {
								arr.forEach(obj => {
									delete obj["合计"];
									delete obj["节点"];
								});
								return arr;
							}
							let chartList1 = []
							dataSource&&dataSource.map(res =>{
								if(res.节点=='合计'){
									chartList1.push(res)
								}
							})
							console.log(chartList1);
							newArr.map((res,index) =>{
								if(res?.title=='节点'){
									res.render = (text, record,index1) => (
									// console.log(newArr[index].title,'--------------')
									<p style={{width:'170px',color:'#fff'}}
										
										>{text}</p>
									
									)
								}else if(res?.title=='合计'){
									// res.fixed = 'right'
									res.fixed = 'right';
									res.render = (text, record,index1) => (
									// console.log(newArr[index].title,'--------------')
									<p style={{width:'80px'}}
										
										>{Number(text).toFixed(2)}</p>
									
									)
								}else{
									if(res){
										res.render = (text, record,index1) => (
											// console.log(newArr[index].title,'--------------')
											<p
												style={{width:'100px',
														  color: new Date(newArr[index].title) > new Date(dayjs().format('YYYY-MM')) ? '#00BF8F' : '#1890FF',
														}}
												>{Number(text).toFixed(2)}</p>
											
										)
									}
								}
								
							})
							// console.log(newArr)
							setColumns(newArr)
							setDataSource(dataSource)
							// charts()
						}
						
						
						
					}
				}).catch(err =>{
					console.log(err)
				})
			}else if(current==1){
				http.post('profit_management/node_profit/getYearProfitList',{
					"endTs": endTs,
					"nodeId": nodeId,
					"startTs": startTs,
					"userType": currentIndex==0?'USER_ALL':currentIndex==1?'LOAD_INTEGRATOR':currentIndex==2?'CONSUMER':''
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						
						setLoading(false)
						let data = res.data.data
						let columns = []
						let dataSource = []
						let list = [];
						let num={};
						if(data.length >0){
							data.forEach((item,index) =>{
								console.log(item)
								dataSource.push(item)
							})
							console.log(dataSource)
							for(var i=0;i<data.length-1;i++){
								for(var key in data[i]){
									var temp = {}
									var newarry = {}
									temp.name = key;
									temp.value = data[i][key];
									list.push(temp)
								}
							}
							
							console.log(list)
							for(var i=0;i<dataSource.length;i++){
								// if(dataSource[i].虚拟电厂运营商){
								// 	dataSource.forEach((item,index) => {
								// 		console.log(item)
								// 		// if(item.负荷聚合商){
								// 		// 	delete item.节点;
								// 		// }
								// 	})
								// }
							}
							
							console.log(list)
							console.log(dataSource)
							console.log(list.push(list.shift()))		
							list.map(res =>{
								columns.push({
									title:res.name,
									dataIndex:res.name
								})
							})
							console.log(columns)
							let newArr = []
							let arrId = []
							for(let item of columns){
							    if(arrId.indexOf(item['title']) == -1){
							        arrId.push(item['title'])
							        newArr.push(item)
							    }
							}
							console.log(arrId)
							console.log(newArr)
							let ceshi;
							// for(var i = 0;i<newArr.length;i++){
							//    if(newArr[i].title == "合计"){
							// 		ceshi = newArr[i] //声明一个对象保存符合要求的数据
							// 		// newArr.splice(0, 0,newArr[i]);
							// 		newArr.splice(i,1)//先把符合条件的数据从当前数组中删除
							// 		break;
							//    }
							//  }
							
							// newArr.push(ceshi)//通过unshift函数把符合要求的数据放到第一位
							console.log(newArr);
							newArr.sort(function (a, b) {
								return a.title > b.title ? 1 : -1;
							})
							console.log(newArr)
							for(var i = 0;i<newArr.length;i++){
							   if(newArr[i].title == "合计"){
									newArr.splice(0, 0,newArr[i]);
									newArr.splice(i+1,1);
							   }
							 }
							for(var j = 0;j<newArr.length;j++){
								if(newArr[j].title =='节点'){
									newArr.splice(0, 0,newArr[j]);
									newArr.splice(j+1,1);
								}
							}
							
							for(var i = 0;i<newArr.length;i++){
							   if(newArr[i].title =='虚拟电厂运营商'||newArr[i].title =='电力用户'){
									// newArr.splice(1, 0,newArr[i]);
									newArr.splice(i, 1);
									break;
							   }
							 }
							newArr.sort((a, b) => {
							    // 将键名为 "合计" 的对象元素移动到数组的最后
							    if (a.dataIndex === "合计") {
							        return 1;
							    }
							    if (b.dataIndex === "合计") {
							        return -1;
							    }
							  
							    return 0;
							});
							
							console.log(newArr);
							
							
							newArr.map((res,index) =>{
								if(res.title=='节点'){
									// res.width = 100
									res.render = (text, record,index1) => (
									// console.log(newArr[index].title,'--------------')
									<p style={{width:'170px',color:'#FFF'}}
										
										>{text}</p>
									
									)
								}else if(res.title=='合计'){
									// res.fixed = 'right'
									res.fixed = 'right';
									res.render = (text, record,index1) => {
										// console.log(text,'--------------')
										return <p style={{width:'80px'}}>{Number(text).toFixed(2)}</p>
									}
									
									// fixed: 'right',
								}else{
									res.render = (text, record,index1) => (
									// console.log(newArr[index].title,'--------------')
									<p
										style={{width:'100px',
										          color: new Date(newArr[index].title) > new Date(dayjs().format('YYYY-MM')) ? '#00BF8F' : '#1890FF',
										        }}
										>{Number(text).toFixed(2)}</p>
									
									)
									
								}
								
							})
							console.log(newArr)
							
							setColumns(newArr);
							setDataSource(dataSource)
							
						}
						
						
						
					}
				}).catch(err =>{
					console.log(err)
				})
			}else if(current==3){
				http.post('profit_management/node_profit/getDayProfitList',{
					"endTs": endTs,
					"nodeId": nodeId,
					"startTs": startTs,
					"userType": currentIndex==0?'USER_ALL':currentIndex==1?'LOAD_INTEGRATOR':currentIndex==2?'CONSUMER':''
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						
						setLoading(false)
						let data = res.data.data
						let columns = []
						let dataSource = []
						let list = [];
						let num={};
						if(data.length >0){
							data.forEach((item,index) =>{
								console.log(item)
								dataSource.push(item)
							})
							console.log(dataSource)
							for(var i=0;i<data.length-1;i++){
								for(var key in data[i]){
									var temp = {}
									var newarry = {}
									temp.name = key;
									temp.value = data[i][key];
									list.push(temp)
								}
							}
							
							console.log(list)
							for(var i=0;i<dataSource.length;i++){
								// if(dataSource[i].虚拟电厂运营商){
								// 	dataSource.forEach((item,index) => {
								// 		console.log(item)
								// 		// if(item.负荷聚合商){
								// 		// 	delete item.节点;
								// 		// }
								// 	})
								// }
							}
							
							console.log(list)
							console.log(dataSource)
							console.log(list.push(list.shift()))		
							list.map(res =>{
								columns.push({
									title:res.name,
									dataIndex:res.name
								})
							})
							console.log(columns)
							let newArr = []
							let arrId = []
							for(let item of columns){
							    if(arrId.indexOf(item['title']) == -1){
							        arrId.push(item['title'])
							        newArr.push(item)
							    }
							}
							console.log(arrId)
							console.log(newArr)
							let ceshi;
							
							console.log(newArr);
							newArr.sort(function (a, b) {
								return a.title > b.title ? 1 : -1;
							})
							console.log(newArr)
							for(var i = 0;i<newArr.length;i++){
							   if(newArr[i].title == "合计"){
									newArr.splice(0, 0,newArr[i]);
									newArr.splice(i+1,1);
							   }
							 }
							for(var j = 0;j<newArr.length;j++){
								if(newArr[j].title =='节点'){
									newArr.splice(0, 0,newArr[j]);
									newArr.splice(j+1,1);
								}
							}
							
							for(var i = 0;i<newArr.length;i++){
							   if(newArr[i].title =='虚拟电厂运营商'||newArr[i].title =='电力用户'){
									// newArr.splice(1, 0,newArr[i]);
									newArr.splice(i, 1);
									break;
							   }
							 }
							newArr.sort((a, b) => {
							    // 将键名为 "合计" 的对象元素移动到数组的最后
							    if (a.dataIndex === "合计") {
							        return 1;
							    }
							    if (b.dataIndex === "合计") {
							        return -1;
							    }
							  
							    return 0;
							});
							
							console.log(newArr);
							function removeTotals(arr) {
								arr.forEach(obj => {
									delete obj["合计"];
									delete obj["节点"];
								});
								return arr;
							}
							let chartList1 = []
							dataSource&&dataSource.map(res =>{
								if(res.节点=='合计'){
									chartList1.push(res)
								}
							})
							
							console.log(chartList1);
							newArr.map((res,index) =>{
								if(res.title=='节点'){
									// res.width = 100
									res.render = (text, record,index1) => (
									// console.log(newArr[index].title,'--------------')
									<p style={{width:'170px',color:'#fff'}}
										
										>{text}</p>
									
									)
								}else if(res.title=='合计'){
									// res.fixed = 'right'
									res.fixed = 'right';
									res.render = (text, record,index1) => (
									// console.log(newArr[index].title,'--------------')
									<p style={{width:'80px'}}>{Number(text).toFixed(2)}</p>
									
									)
									// fixed: 'right',
								}else{
									res.render = (text, record,index1) => (
									// console.log(newArr[index].title,'--------------')
									<p
										style={{
											width:'100px',
										          color: new Date(newArr[index].title) > new Date(dayjs().format('YYYY-MM-DD')) ? '#00BF8F' : '#1890FF',
										        }}
										>{Number(text).toFixed(2)}</p>
									
									)
									
								}
								
							})
							console.log(newArr)
							setColumns(newArr);
							setDataSource(dataSource);
							
						}
						
						
						
					}
				}).catch(err =>{
					console.log(err)
				})
			}
			
		}
		
	}
	useEffect(() =>{
		let chartList1 = []
		dataSource&&dataSource.map(res =>{
			if(res.节点=='合计'){
				chartList1.push(res)
			}
		})
		
		if (chartList1 && chartList1.length > 0) {
			
			// 获取数据数组
			const dataArray1 = Object.values(chartList1[0]);
			
			console.log(dataArray1);
			const getDateArray = () => {
			    // 提取日期键名并创建新的数组
			    const dateArray = Object.keys(chartList1[0]);
			
			    return dateArray;
			};
			
			console.log(getDateArray());
			
			
			// console.log(getDateArray());
			let array =  getDateArray()
			if(array.length>0){
				for (let i = array.length - 1; i >= 0; i--) {
					// 检查是否为 "电力用户"
					if (array[i] === "电力用户") {
						// 如果是 "电力用户"，则删除
						array.splice(i, 1);
					}
					if (array[i] === "虚拟电厂运营商") {
						// 如果是 "虚拟电厂运营商"，则删除
						array.splice(i, 1);
					}
				}
			}
			console.log(array)
			let infrx = 0
			let newindex = 0
			for(var i=0;i<array.length;i++){
				console.log(array[i])
				if(array[i]<=dayjs().format('YYYY-MM-DD')){
					infrx ++
				}else{
					newindex ++
				}
			}
			// console.log(infrx)
			// console.log(newindex)
			// console.log(array);
			
			const newArray = array.filter(item => item !== "合计" && item !== "节点");
			// const dataArray2 = dataArray1.filter(item => item !== "合计" );
			// // 输出新数组
			// // console.log(newArray);
			// let dataArray2 = []
			console.log(dataArray1)
			const keys = [];
			const values = [];
			
			// 遍历数据数组中的每个对象
			dataSource.forEach(obj => {
			    if (obj.节点 === "合计") {
			        // 获取当前对象的属性名称和值，排除 "合计" 和 "节点"
			        Object.keys(obj).forEach(key => {
			            if (key !== "合计" && key !== "节点") {
			                keys.push(key);
			                values.push(obj[key]);
			            }
			        });
			    }
			});
			
			// 输出属性名称和属性值的数组
			console.log("属性名称:", keys);
			console.log("属性值:", values);
			setChartList(keys);
			setChartData(values);
			setChartData1([])
			setInfrx(infrx);
			setNewindex(newindex);
			setIsEmpty(dataSource.length>0?true:false);
		}
		
		// charts(array,dataArray1,infrx,newindex);
		console.log(dataSource,'------')
	},[dataSource])
	const tabFn =(index) => {
		setCurrent(index);
		setDateType(index);
		setStartTs('');
		setEndTs('')
	}
	const clsFn=(index, curCls, cls) =>{
		// console.log(current)
		return current === index ? curCls : cls;
	}
	const onChangeyear =(date,dataString) =>{
		setStartTs(dataString[0]);
		setEndTs(dataString[1])
	}
	const onChange=(date) =>{
		console.log(date)
	}
	const onChangemonth =(date,dataString) =>{
		console.log(date)
		
		setStartTs(dataString[0]);
		setEndTs(dataString[1])
	}
	// 选择日
	const onChangeday =(date,dataString) =>{
		console.log()
		
		setStartTs(dataString[0]);
		setEndTs(dataString[1])
	}
	// 
	// 收益管理
	const cutChange =(e) =>{
		console.log(e)
		
		setCode(e);
		setCheckedKeys([]);
		setDataSource([]);
		setNodeId([]);
		// tree()
		
	}
	const handleClick =(e) =>{
		console.log(e)
		const nodeName = e.target.nodeName.toUpperCase()
		let tag = e.target;
		if (nodeName === 'LI') {
			let index = parseInt(tag.getAttribute('index'))
			console.log(index)
			
			setCurrentIndex(index);
			setDataSource([])
		}
	}
	const onExpand = expandedKeys => {
		setExpandedKeys(expandedKeys);
		setAutoExpandParent(false);
	};
	
	const charts =() =>{
		const chartContainer = document.getElementById('chart-container');
		// 获取图表容器的初始宽度
		const containerWidth = chartContainer.offsetWidth;
		console.log(containerWidth)
		var chartDom = document.getElementById('iconem');
		chartDom.style.width = containerWidth + 'px';
		
		// 重绘图表
		
		var myChart = echarts.init(chartDom);
		var option;
		
		option = {
			grid: {
				left: 0,
				right: 25,
				bottom: 40,
				containLabel: true,
				top:20
			},
			graphic: {
				elements: [
					{
						type: 'image',
						z: 100,
						left: 'center',
						top: 'middle',
						style: {
							image:require('../../style/damao/empty.png')  ,
							// image: 'https://example.com/image.jpg',
							width: 100,
							height: 100
						},
						invisible:isEmpty
					},
					{
						type: "text",
						left: "center", // 相对父元素居中
						top: "200", // 相对父元素上下的位置
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
					   // console.log('tooltip数据值',params[i].value)
					   //遍历出来的值一般是字符串，需要转换成数字，再进项tiFixed四舍五入
						relVal += '<br/>' + params[i].marker   + Number(params[i].value).toFixed(2)
					}
					return relVal;
				},
			},
			xAxis: {
				type: 'category',
				axisLine: {
							//坐标轴轴线相关设置
					lineStyle: {
					  color: "#d7d7d7", //x轴线颜色设置
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
				visualMap: {
					show: false,
					dimension:0,
					
					// type: 'piecewise',
					// seriesIndex:2,//控制series 对应的区域
					// infrx//实际
					pieces: [
						{
							// console.log()
							// 实际
							// gt: this.state.infrx==0?this.state.newindex:this.state.infrx-1,
							gt:-1,
							lte:infrx==0?infrx:infrx-1,
							color: '#1890FF'
						}, 
					
						{
							// 预测
							gt: newindex==0?infrx:infrx-1,
							// lte: this.state.newindex==0?this.state.infrx:this.state.chartData.length,
							color:'#00BF8F'
							// color: this.state.newindex==0?'#00BF8F':'#1890FF'
						},
					]
				},
				
				series: [
					{
					  // data: [150, 230, 224, 218, 135, 147, 260],
					  data:chartData,
					  type: 'line'
					}
				]
		};
		
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		myChart.resize();
	}

		
		
	const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
		console.log('selected', selectedKeys, info);
	};
	// 选择节点
	const onCheck: TreeProps['onCheck'] = (checkedKeys, info) => {
		console.log('onCheck', checkedKeys, info);
		console.log(checkedKeys.toString())
		
		setCheckedKeys(checkedKeys);
		setNodeId(checkedKeys)
	};
		
	const dataList = [];
	const generateList = data => {
	  for (let i = 0; i < data.length; i++) {
		const node = data[i];
		// console.log(node)
		const { key,title } = node;
		dataList.push({ key, title: title });
		if (node.children) {
		  generateList(node.children);
		}
	  }
	};
	generateList(treeData);
	
	const getParentKey = (key, tree) => {
	  let parentKey;
	  for (let i = 0; i < tree.length; i++) {
		const node = tree[i];
		if (node.children) {
		  if (node.children.some(item => item.key === key)) {
			parentKey = node.key;
		  } else if (getParentKey(key, node.children)) {
			parentKey = getParentKey(key, node.children);
		  }
		}
	  }
	  return parentKey;
	};
	const loop = data =>
		data.map(item => {
		  // console.log(item)
			const index = item.title.indexOf(searchValue);
			const beforeStr = item.title.substr(0, index);
			const afterStr = item.title.substr(index + searchValue.length);
			const title =
			  index > -1 ? (
				<span>
				  {beforeStr}
				  <span style={{color: '#f50'}}>{searchValue}</span>
				  {afterStr}
				</span>
			  ) : (
				<span>{item.title}</span>
			  );
			if (item.children) {
				return { title, key: item.key, children: loop(item.children) };
			}
	
			return {
				title,
				key: item.key,
			};
		});
	
	
	const onChanges = e => {
		const { value } = e.target;
		const expandedKeys = dataList
		  .map(item => {
			if (item.title.indexOf(value) > -1) {
			  return getParentKey(item.key, treeData);
			}
			return null;
		  })
		  .filter((item, i, self) => item && self.indexOf(item) === i);
		
		setExpandedKeys(expandedKeys);
		setSearchValue(value);
		setAutoExpandParent(true);
	};
	const disabledDate = (current) => {
	  const startYear = dayjs('2022-01-01').year();
	  const endYear = dayjs('2047-12-31').year();
	
	  const year = current.year();
	  return year < startYear || year > endYear;
	};
	const disabledDate1: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023-01')) || current > dayjs().add(36,'month')
	};
	
	const dateFormat = 'YYYY';
	return(
		<div className="allcontent12 income">
			<div className="navigation" style={{width:310,marginRight:'16px'}}>
				<Spin spinning={treeLoading}  size="middle">
					<Select defaultValue="全部" style={{ width: 280 ,margin:'15px 0px 0px 15px'}} onChange={cutChange}>
						{
							profitTypeList.length &&profitTypeList.map(item =>{
								return  <Option value={item.code} key={item.code}>{item.name}</Option>
							})
						}
					</Select>
					<Tree
						checkable
						defaultExpandAll
						onSelect={onSelect}
						onCheck={onCheck}
						checkedKeys={checkedKeys}
						onExpand={onExpand}
						autoExpandParent={autoExpandParent}
						treeData={treeData}
						// treeData={loop(treeData)}
						
					/>
				</Spin>
			</div>
			<div className='adjustable-wrapper'>	
				<div className='query-header'>
					<div className="adjustableheader">
						<ul className="proheaderlist" onClick={(e)=>handleClick(e)}>
							{
								navList.map((item,index) =>{
										return (
										<li key={index} index={index} className={currentIndex === index ? 'currents' : ''}>{item}</li>
									)
								})
							}
						</ul>
						
						<div className='tab_con3 tab_con5' style={{float:'left',marginRight:12}}>
							<ol className='abb'>
								<li onClick={() => { tabFn(1) }} className={clsFn(1, 'cur3', 'cur31')}>年</li>
								<li onClick={() => { tabFn(2) }} className={clsFn(2, 'cur3', 'cur32')}>月</li>
								<li onClick={() => { tabFn(3) }} className={clsFn(3, 'cur3', 'cur32')}>日</li>
							</ol>
								
							<ul className='acc'>
								<li className={clsFn(1, 'current', '')}>
									
									<ConfigProvider locale={locale}>
										<RangePicker  disabledDate={disabledDate} picker="year" 
										value={startTs!=''&&endTs!='' ? [dayjs(startTs, dateFormat), dayjs(endTs, dateFormat)] : undefined}
										style={{ width: 250 }} onChange={onChangeyear} />
									</ConfigProvider>
								</li>
								<li className={clsFn(2, 'current', '')}>
									<ConfigProvider locale={locale}>
										<RangePicker style={{ width: 250 }} disabledDate={disabledDate1} picker="month" 
										value={startTs!=''&&endTs!='' ? [dayjs(startTs, 'YYYY-MM'), dayjs(endTs, 'YYYY-MM')] : undefined}
										onChange={onChangemonth}  />
									</ConfigProvider>
									
								</li>
								<li className={clsFn(3, 'current', '')}>
									<ConfigProvider locale={locale}>
										<RangePicker style={{ width: 250 }} disabledDate={disabledDate1} 
										value={startTs!=''&&endTs!='' ? [dayjs(startTs, 'YYYY-MM-DD'), dayjs(endTs, 'YYYY-MM-DD')] : undefined}
										onChange={onChangeday}  />
									</ConfigProvider>
									
								</li>
							</ul>
							
						</div>
							
						<Button style={{marginLeft:0}} onClick={onSearch} type="primary">查询</Button>
						</div>
						
						<div className="income">
							
							<div className="incomeList">
								<span onClick={() => { Recordata(1) }} className={record==1?'recirdactive':''}>数据</span>
								<span onClick={() => { Recordata(2) }} className={record==2?'recirdactive':''}>图表</span>
							</div>
							<Button ghost onClick={devices}>导出</Button>
							<div className="predictions">
								<span>实际</span>
								<span>预测</span>
								<b>单位：元</b>
							</div>
					</div>
				</div>
				<div className="adjustable" style={{marginLeft:320,overflow:'auto'}}>
					<div style={{padding:'0px 16px'}} id="chart-container">
						<div style={{display:(record==1)?'block':'none'}}>
							<Table
								rowHoverable={false}
								columns={columns}
								dataSource={dataSource}
								pagination={false}
								loading={loading}
								rowClassName={(record, index) => {
									let className = ''
									className = index % 2 ===0 ? 'ou' : 'ji'
									// console.log(className)
									return className
								}}
							
							/>
						</div>
						
						<div style={{display:(record==2)?'block':'none',width:'100%'}}>
							<div id="iconem"></div>
						</div>
						
					</div>

				</div>
			</div>
		</div>
	)
}
	


export default Income


// <ul className="realistic">
// 	<li>实际</li>
// 	<li>预测</li>
// </ul>