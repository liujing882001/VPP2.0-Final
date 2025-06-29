import React,{useEffect,useState} from 'react'
// import 'antd/dist/antd.css'
// import 'antd/dist/antd.min.css';
import './index.css'
import './index.scss'
import { Tree,DatePicker,ConfigProvider,Button,Table,Input  ,Select ,Typography ,message,Spin,Popover } from 'antd';
import type { DataNode, TreeProps } from 'antd/lib/tree';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import http from '../../../server/server.js'
import axios from 'axios'
import * as echarts from 'echarts';
// import echarts  from '../../echarts.js'
import {
  InfoCircleOutlined
} from '@ant-design/icons';

import type { DatePickerProps } from 'antd';
import Item from 'antd/lib/list/Item';
const { Text } = Typography;
const { RangePicker } = DatePicker;

const { Search } = Input;
const { Option } = Select;

const dateFormat = 'YYYY-MM-DD';


const Responsebenefits =() =>{
	const [currentIndex, setCurrentIndex] = useState(0);
	const [current, setCurrent] = useState(1);
	const [dateType, setDateType] = useState(1);
	const [expandedKeys, setExpandedKeys] = useState([]);
	const [searchValue, setSearchValue] = useState('');
	const [autoExpandParent, setAutoExpandParent] = useState(true);
	const [navList, setNavList] = useState(['全部', '虚拟电厂运营商', '电力用户']);
	const [treeData, setTreeData] = useState([]);
	const [code, setCode] = useState('0'); // 收益值
	const [nodeId, setNodeId] = useState([]); // 节点值
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
	const [allNumber, setAllNumber] = useState('');
	const [page, setPage] = useState(1);
	const [total, setTotal] = useState('');
	const [currentNum, setCurrentNum] = useState(1);
	const [isEmpty, setIsEmpty] = useState(false);
	const [loaded, setLoaded] = useState(false);
	useEffect(() =>{
		tabFn(1)
		tree()
	},[]);
	useEffect(() =>{
		charts()
	},[chartList,chartData,isEmpty]);
	useEffect(() =>{
		if (loaded) {
			getDemandIncome()
		} else {
			setLoaded(true);
		}
	},[page,currentNum]);
	// // 生成两个年份之间的所有年
	const getAllYearsBetween =(startYear, endYear) => {
	    let result = [];
	    for (let i = Math.min(startYear, endYear); i <= Math.max(startYear, endYear); i++) {
	        result.push(i);
	    }
	    return result;
	}
	
	const formatDate =(date) => {
	    const year = date.getFullYear();
	    let month = date.getMonth() + 1;
	    let day = date.getDate();
	
	    if (month < 10) {
	        month = '0' + month;
	    }
	    if (day < 10) {
	        day = '0' + day;
	    }
	
	    return `${year}-${month}-${day}`;
	}
	
	const getDatesInRange=(startDate, endDate) => {
	    const dates = [];
	    const start = new Date(startDate);
	    const end = new Date(endDate);
	
	    while (start <= end) {
	        dates.push(formatDate(start));
	        
	        start.setDate(start.getDate() + 1);
	    }
	
	    return dates;
	}
	// 两个月份之间的所有月
	const getAllMonths =(startMonth, endMonth) => {
	    let startDate = new Date(startMonth + '-01');
	    let endDate = new Date(endMonth + '-01');
	    let months = [];
	    
	    while(startDate <= endDate) {
	        let year = startDate.getFullYear();
	        let month = startDate.getMonth() + 1;
	        let monthStr = month < 10 ? '0' + month : month;
	        months.push(year + '-' + monthStr);
	        
	        startDate.setMonth(startDate.getMonth() + 1);
	    }
	    
	    return months;
	}

	
	// 具体收益信息
	const getDemandIncome =() =>{
		setLoading(true)
		http.post('demand_resp/resp_board/getDemandIncome',{
			dateType:current==1?3:current==2?2:1,   //3-年 2-月 1-日
			endDate:endTs,
			startDate:startTs,
			nodeId:nodeId.toString(),
			number:page,
			pageSize:5,
			userType:currentIndex==0?0:currentIndex==1?2:currentIndex==2?1:''
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				
				if(res.data.data&&res.data.data.list.length>0){
					let list = res.data.data.list
					console.log(list)
					
					let columns1 =columns
					// console.log(this.state.columns,'this.state.columns')
					let dataSource = []
					let profitDate = []
					const transformedData = list.map(item => {
						if(item[2]&&item[3]){
							return {
								id: item[0],
								name: item[1],
								profits: JSON.parse(item[2]),
								value: item[3]
							};
						}else{
							return {
								id: item[0],
								name: item[1],
								profits: [{rofitDate: '', totalProfit: ''}],
								value:''
							};
						}
						
					});
					console.log(transformedData,'---');
					transformedData.map((res,index) =>{
						// console.log(res)
						dataSource.push({
							jiedian: res.name,
							time:res.profits,
							total:res.value
						})
						if(res.profits){
							let profits = res.profits
							// console
							profits.map(item =>{
								console.log(item)
								profitDate.push(item.profitDate)
								
							})
						}
					})
					console.log(dataSource)
					const transformedData1 = dataSource.map(obj => {
						// console.log(obj)
					    const newObj = {
					        jiedian: obj.jiedian,
					        time: obj.time,
							total:Number(obj.total).toFixed(2)
					    };
					    obj.time.forEach(item => {
					        newObj[item.profitDate] = Number(item.totalProfit).toFixed(2);
					    });
					    return newObj;
					});
					
					console.log(transformedData1,'---');
					
					
					// 遍历原始数据
					const sumByDate = transformedData1.reduce((acc, obj) => {
					    const keys = Object.keys(obj);
					    keys.forEach(key => {
					        if (key !== "jiedian" && key !== "time" && key !== "total") {
					            if (!acc[key]) {
					                acc[key] = 0;
					            }
					            if (!isNaN(parseFloat(obj[key])) && obj[key] !== "-") {
					                acc[key] += parseFloat(obj[key]);
					            }
					        }
					    });
					    return acc;
					}, {});
					
					const newArray = Object.entries(sumByDate).map(([date, total]) => ({ date, total: total.toFixed(2) }));
					console.log(newArray);
					
					const newColumn = {
					    title:'合计',
					    key:'total',
					    dataIndex:'total',
						// fixed: this.state.current==0?'none':'right',
						width: 100,
					};
					// console.log(columns1)
					if (!columns.some(column => column.title === newColumn.title)) {
						
						setColumns( [...columns1, newColumn])
					} else {
						console.log("新列已存在，不重复添加");
					}
					columns1.push(newColumn)
					console.log(columns1)
					const resultObject = {};
					let count = 0
					newArray.forEach(item => {
						// console.log(item)
					    resultObject[item.date] = Number(item.total);
						resultObject['jiedian'] = '合计'
						
						count += Number(item.total)
					});
					resultObject['total'] = Number(count).toFixed(2)
					console.log(count)
					console.log(resultObject);
					console.log(transformedData1)
					transformedData1.push(resultObject)
					setDataSource(transformedData1);
					setTotal(res.data.data.count);
					setLoading(false)
				}else{
					
					setDataSource([]);
					setTotal('');
					setLoading(false)
				}
				

			}else{
				message.info(res.data.msg)
				setLoading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const getDemandIncomeChart=() =>{
		setLoading(true)
		http.post('demand_resp/resp_board/getDemandIncome',{
			dateType:current==1?3:current==2?2:1,   //3-年 2-月 1-日
			endDate:endTs,
			startDate:startTs,
			// endDate:'202403',
			// startDate:'202401',
			nodeId:nodeId.toString(),
			// nodeId:'bf43880a2be81420cbb37e80e7c95f75,1000045,1000000,1,617c160685a76bdefdfa5188b359838a,1001166,1001160,1000012,3da72e052a0b48759b0f4633df42235a,176c0991f24e30c2b25a9dbf1185b7b9,5eb413037ba16ea6108c12e0d6353be3,e907da09fa81619508b3eb881c94e5f8,c8f342b6d6f8eec8e4075df78cf2083b,1001972,151761f652c43d300928948d7fbbb242,1001976,8a0a9db1304eb368575ce9351130bf51,1001973,94dd2a367a714d0aa1fd23ef2badaa81,1001974,1001969,1000018,dec2d319acb390a9ff146201ca954a83,3cfc4c930f653e7f6d198955566867b0,1000815,1000008,8',
			number:1,
			pageSize:1000000000,
			userType:currentIndex==0?0:currentIndex==1?2:currentIndex==2?1:''
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				
				if(res.data.data&&res.data.data.list.length>0){
					let list = res.data.data.list
					console.log(list)
					
					let columns1 =columns
					let dataSource = []
					let profitDate = []
					const transformedData = list.map(item => {
						if(item[2]&&item[3]){
							return {
							    id: item[0],
							    name: item[1],
							    profits: JSON.parse(item[2]),
							    value: item[3]
							};
						}else{
							return {
							    id: item[0],
							    name: item[1],
							    // profits: [{rofitDate: '', totalProfit: ''}],
							    // value:''
							};
						}
					    
						
					});
					console.log(transformedData,'---');
					transformedData.map((res,index) =>{
						// console.log(res)
						console.log(res)
						dataSource.push({
							jiedian: res.name,
							time:res.profits,
							total:res.value
						})
						if(res.profits){
							let profits = res.profits
							profits.map(item =>{
								// console.log(item)
								profitDate.push(item.profitDate)
								
							})
						}
					})
					console.log(transformedData)
					console.log(dataSource)
					const transformedData1 = dataSource.map(obj => {
						console.log(obj)
						const newObj = {
						    jiedian: obj.jiedian,
						    time: obj.time,
							total:Number(obj.total).toFixed(2)
						};
						if(obj.time){
							obj.time.forEach(item => {
							    newObj[item.profitDate] = Number(item.totalProfit).toFixed(2);
							});
						}
						
						return newObj;
					    
					});
					
					console.log(transformedData1,'---');
					// 遍历原始数据
					const sumByDate = transformedData1.reduce((acc, obj) => {
					    const keys = Object.keys(obj);
					    keys.forEach(key => {
					        if (key !== "jiedian" && key !== "time" && key !== "total") {
					            if (!acc[key]) {
					                acc[key] = 0;
					            }
					            if (!isNaN(parseFloat(obj[key])) && obj[key] !== "-") {
					                acc[key] += parseFloat(obj[key]);
					            }
					        }
					    });
					    return acc;
					}, {});
					console.log(sumByDate)
					const newArray = Object.entries(sumByDate).map(([date, total]) => ({ date, total: total.toFixed(2) }));
					console.log(newArray);
					// console.log(this.state.columns)
					columns.forEach(obj2 => {
					    if (!newArray.find(obj1 => obj1.date == obj2.title)) {
					        newArray.push({ "date": (obj2.title).toString(), "total": "0" });
					    }
					});
					
					console.log(newArray);
					newArray.sort((a, b) => {
					    if (a.date === "序号" || a.date === "节点" || a.date === "合计") {
					        return 1; // 不对序号、节点、合计进行排序，保持它们在最后
					    } else if (b.date === "序号" || b.date === "节点" || b.date === "合计") {
					        return -1; // 不对序号、节点、合计进行排序，保持它们在最后
					    } else {
					        if (a.date === "-") return 1;
					        if (b.date === "-") return -1;
					        
					        const dateA = new Date(a.date);
					        const dateB = new Date(b.date);
					        return dateA - dateB; // 对日期进行排序
					    }
					});
					
					console.log(newArray);
					
					console.log(newArray);
					let chartList = []
					let chartData = []
					newArray.map(res =>{
						if(res.date=='节点'||res.date=='序号'||res.date=='合计'){
							
						}else{
							chartList.push(res.date)
							chartData.push(res.total)
						}
						
					})
					console.log(chartData)
					let numTotal = 0;
					
					chartData.map(res =>{
						if(res=='-'){
							numTotal++
						}
					})
					console.log(numTotal)
					
					setChartList(chartList)
					setChartData(chartData);
					setLoading(false);
					setIsEmpty(true);
					// charts()
				}else{
					setChartList([])
					setChartData([]);
					setLoading(false);
					setIsEmpty(false);
					// charts();
				}

			}
		}).catch(err =>{
			console.log(err)
		})
	}
	//左侧tree
	const tree=() =>{
		
		setTreeLoading(true)
		http.post('tree/runNodeTree').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let count = 0;
				const main = function(data) {
					for (let i in data) {
						data[i].label = data[i].title;
						data[i].value = data[i].id;
						
						if (data[i].children) {
							
							// console.log(data[i].children)
							let children = data[i].children
							children.map(res =>{
								if(res.type=='NODE'){
									count++
								}
							})
							main(data[i].children);
						}
					}
				}
				main(data);
				console.log(data);
				setTreeData(data);
				setTreeLoading(false);
				setAllNumber(count);
			}
		}).catch(err =>{
			console.log(err)
		})
		
	}
	// 选择数据
	const Recordata =(index) =>{
		console.log(index)
		
		setRecord(index)
		if(index==2){
			getDemandIncomeChart()
		}
	}
	// 导出
	const devices =() =>{
		axios({
			method: 'post',
			url: 'demand_resp/resp_board/getDemandIncomeExport',
			responseType: 'arraybuffer',
			data: {
					// "endTs": this.state.endTs,
					// "nodeId": this.state.nodeId,
					// "startTs": this.state.startTs,
					// "userType": currentIndex==0?'USER_ALL':currentIndex==1?'LOAD_INTEGRATOR':currentIndex==2?'CONSUMER':''
					dateType:current==1?3:current==2?2:1,   //3-年 2-月 1-日
					endDate:endTs,
					startDate:startTs,
					// endDate:'202403',
					// startDate:'202401',
					nodeId:nodeId.toString(),
					// nodeId:'bf43880a2be81420cbb37e80e7c95f75,1000045,1000000,1,617c160685a76bdefdfa5188b359838a,1001166,1001160,1000012,3da72e052a0b48759b0f4633df42235a,176c0991f24e30c2b25a9dbf1185b7b9,5eb413037ba16ea6108c12e0d6353be3,e907da09fa81619508b3eb881c94e5f8,c8f342b6d6f8eec8e4075df78cf2083b,1001972,151761f652c43d300928948d7fbbb242,1001976,8a0a9db1304eb368575ce9351130bf51,1001973,94dd2a367a714d0aa1fd23ef2badaa81,1001974,1001969,1000018,dec2d319acb390a9ff146201ca954a83,3cfc4c930f653e7f6d198955566867b0,1000815,1000008,8',
					number:page,
					pageSize:5,
					userType:currentIndex==0?0:currentIndex==1?2:currentIndex==2?1:'',
					isExport:'export'
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
	// 搜索
	const onSearch =() =>{
		if(startTs&&endTs&&nodeId.toString()){
			
			setCurrentNum(1);
			setPage(1);
			getDemandIncome()
			getDemandIncomeChart()
		}else if(startTs==""||endTs==""||nodeId.toString()==""){
			message.info(
				startTs==""?'请选择开始日期':endTs==""?'请选择结束日期':'请选择节点'
			)
		}
		
		
		
	}
	const tabFn =(index) => {
		// console.log(index)
		setCurrent(index);
		setDateType(index);
		setStartTs('');
		setEndTs('');
		setPage(1);
		setCurrentNum(1);
	}
	const clsFn =(index, curCls, cls) => {
		// console.log(current)
		return current === index ? curCls : cls;
	}
	const onChangeyear =(date,dataString) =>{
		console.log(dataString)
		setStartTs(dataString[0]);
		setEndTs(dataString[1])
		let yearsBetween = getAllYearsBetween(dataString[0], dataString[1]);
		let columns = [
			{
				title:'序号',
				key:'index1',
				dataIndex:'index1',
				width:80,
				// render:(value, item, index) => (page - 1) * 5 + index+1,
				render:(value, item, index) =>{
					// console.log(item.jiedian)
					if(item.jiedian =='合计'){
						return ''
					}else{
						return  (page - 1) * 5 + index+1
						
					}
				}
			},{
				title:'节点',
				key:'jiedian',
				dataIndex:'jiedian',
				width:260,
			}]
		yearsBetween.map(res =>{
			columns.push({
				title:res,
				key:res,
				dataIndex:res,
				width:160,
				render:(value, item, index) =>{
					// console.log(item.jiedian)
					if(value===""||value===undefined||value===null){
						return '0.00'
					}else{
						return Number(value).toFixed(2)
					}
				}
			})
		})
		console.log(columns)
		if(yearsBetween==0){
			setDataSource([]);
			setTotal(0)
		}
		setColumns(yearsBetween==0?[]:columns)
	}
	const onChange =(date) =>{
		console.log(date)
	}
	const onChangemonth =(date,dataString) =>{
		console.log(date,dataString)
		setStartTs(date?dayjs(date[0]).format('YYYYMM'):'');
		setEndTs(date?dayjs(date[1]).format('YYYYMM'):'');
		let result = getAllMonths(dataString[0], dataString[1]);
		if(date===null){
			setDataSource([]);
			setTotal(0)
		}
		setPage(1)
		let columns = [
			{
				title:'序号',
				key:'index',
				dataIndex:'index',
				width:80,
				render:(value, item, index) =>{
					// console.log(item.jiedian)
					if(item.jiedian =='合计'){
						return ''
						// let page1 = 1
						// return (page1 - 1) * 5 + index+1
					}else{
						return  (page - 1) * 5 + index+1
					}
					
				}
			},{
				title:'节点',
				key:'jiedian',
				dataIndex:'jiedian',
				width:230,
		}]
		result.map(res =>{
			columns.push({
				title:res,
				key:res,
				dataIndex:res,
				width:130,
				render:(value, item, index) =>{
					if(value===""||value===undefined||value===null){
						// return '-'
						return '0.00'
					}else{
						return Number(value).toFixed(2)
					}
				}
			})
		})
		setColumns(date===null?[]:columns)
		
	}
	// 选择日
	const onChangeday =(date,dataString) =>{
		console.log(dataString)
		
		setStartTs(dataString[0]);
		setEndTs(dataString[1]);
		setPage(1);
		setCurrentNum(1)
		let datesBetween = getDatesInRange(dataString[0], dataString[1]);
		console.log(datesBetween);
		let columns = [
			{
				title:'序号',
				key:'index',
				dataIndex:'index',
				width:80,
				render:(value, item, index) =>{
					// console.log(item.jiedian)
					if(item.jiedian =='合计'){
						return ''
					}else{
						return  (page - 1) * 5 + index+1
					}
				}
				
			},{
				title:'节点',
				key:'jiedian',
				dataIndex:'jiedian',
				width:230
		}]
		datesBetween.map(res =>{
			columns.push({
				title:res,
				key:res,
				dataIndex:res,
				width:130,
				render:(value, item, index) =>{
					// console.log(item.jiedian)
					if(value===""||value===undefined||value===null){
						// return '-'
						return '0.00'
					}else{
						return Number(value).toFixed(2)
					}
				}
			})
		})
		console.log(columns)
		if(datesBetween.length==0){
			setDataSource([]);
			setTotal(0);
		}
		setColumns(datesBetween.length==0?[]:columns)
	}
	// 
	const handleClick =(e) =>{
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
	
	const charts=() =>{
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
			color:["rgba(0, 146, 255, 1)"],
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
				// axisPointer: {
				// 	type: 'cross',
				// 	label: {
				// 		backgroundColor: '#6a7985'
				// 	}
				// },
				backgroundColor: '#302F39',
				borderColor: 'transparent',
				textStyle: {
					color: '#fff' // 设置 tooltip 的文字颜色为白色
				},
				formatter(params) {
					// console.log(params)
					var relVal = params[0].marker +params[0].name;
					for (var i = 0, l = params.length; i < l; i++) {
					   // console.log('tooltip数据值',params[i].value)
					   //遍历出来的值一般是字符串，需要转换成数字，再进项tiFixed四舍五入
					   if(params[i].value=='-'){
						   relVal += '<br/>'    + '-'
					   }else{
						   relVal += '<br/>'    + Number(params[i].value).toFixed(2)
					   }
						
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
					  color: "#AEAEAE", //x轴字体颜色
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
						color: '#AEAEAE'
					}
				},
				splitLine: {
					//网格线
					lineStyle: {
					  type: "dashed", //设置网格线类型 dotted：虚线   solid:实线
					  width: 1,
					  color:"#AEAEAE"
					},
					show: true, //隐藏或显示
				}
			  },
				
				
				series: [
					{
					  // data: [150, 230, 224, 218, 135, 147, 260],
					  data:chartData,
					  type: 'line',
					  symbol: 'circle', //将小圆点改成实心 不写symbol默认空心
						symbolSize: 5, 
					  areaStyle: {
						color: {
							type: 'linear',
							x: 0,
							y: 0,
							x2: 0,
							y2: 1,
							colorStops: [{
								offset: 0,
								color: 'rgba(0, 146, 255, .7)' // 0% 处的颜色
							}, {
								offset: 0.4,
								color: 'rgba(0, 146, 255, 0.6)' // 
							}, {
								offset: 0.5,
								color: 'rgba(0, 146, 255, 0.5)' // 
							}, {
								offset: 0.7,
								color: 'rgba(0, 146, 255, 0.3)' //
							}, {
								offset: 1,
								color: 'rgba(0, 146, 255, 0)' // 100% 处的颜色
							}],
							global: false // 缺省为 false
						},
					  
					  
					  }
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
		let nodeId = []
		let infoall = info.checkedNodes
		if(infoall.length>0){
			infoall.map(res =>{
				if(res.type=='NODE'){
					nodeId.push(res.id)
				}
				
			})
		}
		console.log(nodeId)
		setCheckedKeys(checkedKeys);
		setNodeId(nodeId)
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
		const expandedKeys = dataList.map(item => {
			if (item.title.indexOf(value) > -1) {
			  return getParentKey(item.key, treeData);
			}
			return null;
		}).filter((item, i, self) => item && self.indexOf(item) === i);
		
		setExpandedKeys(expandedKeys);
		setSearchValue(value);
		setAutoExpandParent(true)
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
	
	const dateFormat = 'YYYYMM';
	const handlePagination =(page) =>{
		console.log(page)
		
		setPage(page);
		setCurrentNum(page);
		// getDemandIncome();
	}
	return(
		<div className="allcontent12 Responsebenefits">
			<div className="navigation" style={{width:310,marginRight:'16px'}}>
				<Spin spinning={treeLoading}  size="middle">
					<div className="Responsebenefits-title">
						<span>全部节点 ({allNumber})</span>
					</div>
					
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
			<div className="adjustable" style={{marginLeft:320,overflow:'auto'}}>
				
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
										<RangePicker  
										disabledDate={disabledDate} 
										picker="year" 
										value={startTs!=''&&endTs!='' ? [dayjs(startTs, 'YYYY'), dayjs(endTs, 'YYYY')] : undefined}
										style={{ width: 250 }} onChange={onChangeyear} />
									</ConfigProvider>
								</li>
								<li className={clsFn(2, 'current', '')}>
									<ConfigProvider locale={locale}>
										<RangePicker style={{ width: 250 }} disabledDate={disabledDate1} picker="month" 
										value={startTs!==''&&endTs!=='' ? [dayjs(startTs, 'YYYYMM'), dayjs(endTs, 'YYYYMM')] : undefined}
										// format={dateFormat}
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
				<div className="Responsebenefits-content">	
					<div className="income-Responsebenefits" style={{margin:'0px 20px 20px 20px'}}>
						
						<div className="incomeList">
							<span onClick={() => { Recordata(1) }} className={record==1?'recirdactive':''}>数据</span>
							<span onClick={() => { Recordata(2) }} className={record==2?'recirdactive':''}>图表</span>
						</div>
						<Button ghost onClick={devices}>导出</Button>
						<div className="predictions">
							<span >预估</span>
							<Popover content='此收益为预估收益，实际收益以电网账单为准' title={false} trigger="hover">
								<InfoCircleOutlined />
							</Popover>
							<b>单位：元</b>
							
						</div>
					</div>
					<div style={{padding:'0px 16px'}} id="chart-container">
						<div style={{display:(record==1)?'block':'none'}}>
							<Table
								columns={columns}
								dataSource={dataSource}
								// pagination={false}
								// scroll={{ x:this.state.current==0?500:1200 }}
								scroll={{ y: 500, x: '100%' }}
								pagination={
									{
										total: total,//数据的总条数
										defaultCurrent: 1,//默认当前的页数
										defaultPageSize: 6,//默认每页的条数
										current:currentNum,
										// showSizeChanger:false,
										// onChange: handlePagination,
										// showQuickJumper:true,
										
										onChange: (page) =>handlePagination(page),			
									}
								}
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
	


export default Responsebenefits
