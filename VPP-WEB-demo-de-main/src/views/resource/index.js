import React, {useEffect, useState,useRef } from 'react'
import {Route,Link,Redirect,Switch,List	} from "react-router-dom";
import * as echarts from 'echarts';
// import echarts  from '../echarts.js'
import ECharts from 'echarts-for-react';
// import 'antd/dist/antd.min.css';
import './index.css'
import './index.scss'
import {DatePicker,Button,Space,ConfigProvider,message,Empty ,Spin,Carousel} from 'antd';
// import Slider from "react-slick";
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';

import {
	RightOutlined,
	LeftOutlined
	
} from '@ant-design/icons';
// import http from '../../server/server.js'
import axios from 'axios'
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import {
	createHashHistory
} from "history";
import Chart from './charts.js';



import accumulation from '../charge/accumulation/index.js'
import condenser from '../charge/condenser/index.js'
import shang from '../../style/icon/shang.png'	//商业综合体
import factoryimg from '../../style/icon/gong.png'	//工厂园区
import chunengimg from '../../style/icon/chu.png' 	//储能电站
import guangfuimg from '../../style/icon/guang.png'	//光伏电站
import tong from '../../style/icon/tong.png'	//通信基站
import gui from '../../style/icon/gui.png' //轨道交通
import shu from '../../style/icon/shu.png' //数据中心
import zheng from '../../style/icon/zheng.png' //政府办公
import chong from '../../style/icon/chong.png' //充电桩项目
import other from '../../style/icon/other.png' //其他

const history = createHashHistory();
const {
	RangePicker
} = DatePicker;
const now = dayjs();
// class resource extends Component {
const Resource= () => {
	const [daylist,setDaylist] = useState(['近1天', '近7天', '近一月'])
	const [currentState,setCurrentState] = useState(0)
	const [mode] = useState(['month', 'month'])
	const [dayL,setDayL] = useState([])
	const [startTime,setStartTime] = useState('')
	const [endTime,setEndTime] = useState('')
	const [all,setAll] = useState('')
	const [actual,setActual] = useState('')
	const [building,setBuilding] = useState('')
	const [weight,setWeight] = useState([])
	const [ts_e,setTs_e] = useState('')
	const [ts_s,setTs_s] = useState('')
	const [gongchang,setGongchang] = useState('-')
	const [idc,setIdc] = useState('')
	const [jizhan,setJizhan] = useState('')
	const [louyu,setLouyu] = useState('-')
	const [storageEnergyCapacity,setStorageEnergyCapacity] = useState('')
	const [storageEnergyLoad,setStorageEnergyLoad] = useState('')
	const [storageEnergyNum,setStorageEnergyNum] = useState('')
	const [storageEnergySOC,setStorageEnergySOC] = useState('')
	const [storageEnergySOH,setStorageEnergySOH] = useState('')
	const [pvCapacity,setPvCapacity] = useState('')
	const [pvLoad,setPvLoad] = useState('')
	const [pvNum,setPvNum] = useState('')
	const [loadCapacity,setLoadCapacity] = useState('')
	const [load,setLoad] = useState('')
	const [louYuNumber,setLouYuNumber] = useState('')
	const [louload,setLouload] = useState('')
	const [totalLoad,setTotalLoad] = useState('')
	const [ktotalLoad,setKtotalLoad] = useState('-')
	const [kload,setKload] = useState('-')
	const [csoc,setCsoc] = useState('-')
	const [csoh,setCsoh] = useState('-')
	const [ccapacity,setCcapacity] = useState('-')
	const [cinEnergy,setCinEnergy] = useState('-')
	const [cload,setCload] = useState('-')
	const [cmaxInLoad,setCmaxInLoad] = useState('-')
	const [cmaxOutLoad,setCmaxOutLoad] = useState('')
	const [gongChangNumber,setGongChangNumber] = useState('')
	const [gload,setGload] = useState('')
	const [gother,setGother] = useState('')
	const [gtotalLoad,setGtotalLoad] = useState('')
	const [gucapacity,setGucapacity] = useState('')
	const [guenergy,setGuenergy] = useState('')
	const [guload,setGuload] = useState('')
	const [gunowEnergy,setGunowEnergy] = useState('')
	const [storedenergy,setStoredenergy] = useState('')
	const [photovoltaic,setPhotovoltaic] = useState('')
	const [inCapacity,setInCapacity] = useState('')
	const [loadRatio,setLoadRatio] = useState([])
	const [gloadRatio,setGloadRatio] = useState([])
	const [gongchang1,setGongchang1] = useState('')
	const [jieRuLoad,setJieRuLoad] = useState('')
	const [alltotalLoad,setAlltotalLoad] = useState('')
	const [kjieRuLoad,setKjieRuLoad] = useState('-')
	const [loujieRuLoad,setLoujieRuLoad] = useState('-')
	const [loutotalLoad,setLoutotalLoad] = useState('-')
	const [gongjieRuLoad,setGongjieRuLoad] = useState('-')
	const [chuneng,setChuneng] = useState('-')
	const [guangfu,setGuangfu] = useState('-')
	const [panier,setPanier] = useState([])
	const [sLoading,setSLoading] = useState(false)
	const [setLoading,setSetLoading] = useState(false)
	const [naturalList,setNaturalList] = useState([])
	const [resourcesList,setResourcesList] = useState([])
	const [formattedDates,setFormattedDates] = useState([])
	const [dayList,setDayList] = useState([])
	const [monthList,setMonthList] = useState([])
	const [oneTime,setOneTime] = useState('')
	const [loadNumber,setLoadNumber] = useState('')
	const [natureList,setNatureList] = useState([])
	const [typesnatureList,setTypesnatureList] = useState([])
	const [startIndex,setStartIndex] = useState(0)
	const [currentPage,setCurrentPage] = useState(1)
	const [displayPerPage,setDisplayPerPage] = useState(2)
	const [isEmpty,setIsEmpty] = useState(true)
	const [allloading,setAllloading] = useState(true)
	const [osType,setOsType] = useState(sessionStorage.getItem('osType'))
	const [outEnergy,setOutEnergy] = useState('')
	const divRef = useRef(null);

	
	useEffect(() =>{
		getdatList()
		getLoadCount()
		storageEnergyCount()
		pvCount()
		// weights()
		let day = dayjs().format('YYYY-MM-DD')
		getNodeTypeIdItems()
		if(osType=='loadType'){
			getLoadNatureClassification()
			getLoadTypeClassification()
			
		}
		
	},[])
	// useEffect(() =>{
		
	// 	weights()
		
	// },[dayL,weight,isEmpty])
	
	const toFixedTwo=(value) => {
	  // 检查值是否为null或undefined
		if (value === null||value===undefined||value===''||value=='-') {
			return '-'; // 返回'-'
		}
		// 使用toFixed保留两位小数
		return Number(value).toFixed(2);
	}
	// 负荷性质分类
	const getLoadNatureClassification =() => {
		axios.post('flexible_resource_management/main/getLoadNatureClassification').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				setNatureList(data?data:[])
				nature(data?data:[])
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 负荷类型分类
	const getLoadTypeClassification=() => {
		axios.post('flexible_resource_management/main/getLoadTypeClassification').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				setTypesnatureList(data?data:[])
				typesnature(data?data:[])
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	//  负荷性质分类
	const nature=(data) => {
		var chartDom = document.getElementById('nature');
		var myChart = echarts.init(chartDom);
		var option;
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		option = {
			color:['#AFC8AE','#88AB8D','#0092FF','#EEE7DA'],
			
			legend: { // 对图形的解释部分
			    orient: 'vertical',
			    // right: 10,
				left: '50%',
			    y: 'center',
			    icon: 'circle',   
				itemHeight: 8,
				itemWidth: 8,// 添加
				type: 'scroll',
				selectedMode: false,
				// selectedMode: 'multiple',
			    formatter: function(name) {   // 添加
					let total = 0
					let target;
					var data = option.series[0].data;
					for (let i = 0; i < data.length; i++) {
						total += Number(data[i].value)
						// console.log(data[i].value)
						if (data[i].name === name) {
							target = data[i].value
						}
					}
					if(target==0&&total==0){
						var arr = [
							'{a|' + name + '}',
							'{d|' + '' +'}',
							'{b|' + 0 + '%}',
							'{c|' + target + 'kw}'
						]
					}else{
						var arr = [
							'{a|' + name + '}',
							'{d|' + '' +'}',
							'{b|' + ((target / total)*100).toFixed(2) + '%}',
							'{c|' + (target===''?'-':target===null?'-':target===undefined?'-':Number(target).toFixed(2)) + 'kw}'
						]
					}
					
					return arr.join('  ')
			    },
			    textStyle: {  // 添加
			      // padding: [8, 0, 0, 0],
				  lineHeight:26,
			      rich: {
			        a: {
						fontSize: 14,
						width: 80,
						color: '#FFF',
						
			        },
			        b: {
						fontSize: 14,
						width: 70,
						color: '#FFF',
						// marginLeft:20
						paddingLeft:10
			        },
			        c: {
						fontSize: 14,
						color:'#8F959E'
			        },
					d:{
						height:12,
						borderWidth:1,
						borderColor: '#FFF',
						
					}
			      }
			    }
			  },
			grid:{
				// left: '10%',
				// top: '5%',
			},
			
			series: [
				{
					type: 'pie',
					radius: ['65%', '85%'],
					center: ['20%', '50%'],   //圆的位置
					avoidLabelOverlap: false,
					itemStyle: {
						// borderColor: '#fff',
						borderWidth: 0
					},
					silent: true,
					animation: false, // 禁止动画
					label: { //  饼图图形上的文本标签
						normal: {
							show: true,
							position: 'center',
							formatter: (data, type) => {
							  // let info = data.data;
							  let str = `{a|${'总可调负荷(kW)'}}\n {b|${ktotalLoad}}`; //这里对不同的内容进行标识 a，b，或者可以随便自己起个别的名字
							  return str;
							},
							color: 'white',
							rich: { //在rich中对两个标识进行样式修改
							  a: {
								fontSize: 12,
								color:'#fff',
								lineHeight:30,
							  },
							  b: {
								fontSize: 18,
								color:'#FFF'
							  }
							},
							textStyle:{
								// fontSize:40,
								// color:"#666",
								lineHeight: 20,
							},
							// '{active|实时可调负荷(KW)}'+'\n'+ '{total|' + this.state.kload + '}',
						},
							emphasis: {
								show: false,
								textStyle: {
									fontSize: '30',
									fontWeight: 'bold'
								}
							}
						

					},
					
					// emphasis: {//饼图中间显示配置
					// 	label: {
					// 		show: false,
							
					// 	}
					// },
					labelLine: {
						show: false
					},
					// itemStyle: {
					//       borderWidth: 0, // 设置边框宽度为0
					//       borderColor: 'transparent' // 设置边框颜色为透明色
					//     }
					data: data
				}
			]
		};
		
		option && myChart.setOption(option);
	}
	// 负荷类型分类
	const typesnature=(data) => {
		var chartDom = document.getElementById('typesnature');
		var myChart = echarts.init(chartDom);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		var option;
		
		option = {
			color:['#55697B','#7195BA','#0092FF','#B1CBE2'],
			legend: { // 对图形的解释部分
			    orient: 'vertical',
			    left: '50%',
			    y: 'center',
			    icon: 'circle',    
				itemHeight: 8,
				itemWidth: 8,// 添加
				selectedMode: false,
			    formatter: function(name) {   // 添加
					let total = 0
					let target;
					var data = option.series[0].data;
					for (let i = 0; i < data.length; i++) {
						total += data[i].value
						if (data[i].name === name) {
						target = data[i].value
						}
					}
					if(target==0&&total==0){
						var arr = [
							'{a|' + name + '}',
							'{d|' + '' +'}',
							'{b|' + 0 + '%}',
							'{c|' + target + 'kw}'
						]
					}else{
						var arr = [
							'{a|' + name + '}',
							'{d|' + '' +'}',
							'{b|' + ((target / total)*100).toFixed(2) + '%}',
							'{c|' + (target===''?'-':target===null?'-':target===undefined?'-':Number(target).toFixed(2)) + 'kw}'
						]
					}
			      
					return arr.join('  ')
			    },
			    textStyle: {  // 添加
			      // padding: [12, 0, 0, 0],
				  lineHeight:26,
			      rich: {
			        a: {
						fontSize: 14,
						width: 80,
						color: '#FFF',
						
			        },
			        b: {
						fontSize: 14,
						width: 70,
						color: '#FFF',
						// marginLeft:20
						paddingLeft:10
			        },
			        c: {
						fontSize: 14,
						color:'#8F959E'
			        },
					d:{
						// width:1,
						height:12,
						// background:'#000',
						borderWidth:1,
						borderColor: '#FFF',
						
					}
			      }
			    }
			  },
			grid:{
				left: '10%',
			},

			series: [
				{
					type: 'pie',
					// radius: ['50%', '70%'],
					radius: ['65%', '85%'],
					center: ['20%', '50%'],   //圆的位置
					avoidLabelOverlap: false,
					itemStyle: {
						// borderColor: '#fff',
						borderWidth: 0
					},
					silent: true,
					animation: false, // 禁止动画
					label: { //  饼图图形上的文本标签
						normal: {
							show: true,
							position: 'center',
							formatter: (data, type) => {
							  // let info = data.data;
							  let str = `{a|${'实时可调负荷(kW)'}}\n {b|${kload}}`; //这里对不同的内容进行标识 a，b，或者可以随便自己起个别的名字
							  return str;
							},
							color: 'white',
							rich: { //在rich中对两个标识进行样式修改
							  a: {
								fontSize: 12,
								color:'#FFF',
								lineHeight:30,
							  },
							  b: {
								fontSize: 18,
								color:'#FFF',
							  }
							},
							textStyle:{
								// fontSize:40,
								// color:"#666",
								lineHeight: 20,
							},
							// '{active|实时可调负荷(KW)}'+'\n'+ '{total|' + this.state.kload + '}',
						},
							emphasis: {
								show: false,
								textStyle: {
									fontSize: '30',
									fontWeight: 'bold'
								}
							}
						
					
					},
					labelLine: {
						show: false
					},
					data: data
				}
			]
		};
		
		option && myChart.setOption(option);
	}
	// 月份
	const get_date =(num) =>{
		let dateArray = []
		    //获取今天日期
		    let myDate = new Date()
		    let today = myDate.getFullYear() + '-' + (myDate.getMonth() + 1) + "-" + myDate.getDate();
		    myDate.setDate(myDate.getDate() - num)
		    let dateTemp;  // 临时日期数据
		    let flag = 1;
		    for (let i = 0; i < num; i++) {
		        dateTemp = myDate.getFullYear() + '-' + (myDate.getMonth() + 1) + "-" + myDate.getDate()
		        dateArray.push({
		            date: dateTemp
		        })
		        myDate.setDate(myDate.getDate() + flag);
		    }
		    dateArray.push({
		        date: today
		    })
		    let arr = []
		    let newArr = []
		    dateArray.forEach(item => {
		        arr.push(item.date.split('-'))
		    })
		    for (let i = 0; i < arr.length; i++) {
		        if (arr[i][1] < 10) {
		            arr[i][1] = "0" + arr[i][1]
		        }
		        if (arr[i][2] < 10) {
		            arr[i][2] = "0" + arr[i][2]
		        }
		    }
		    for (let j = 0; j < arr.length; j++) {
		        newArr.push(arr[j].join("-"))
		    }
		    // 当前日期
		    let nowDate = newArr[newArr.length - 1]
		    // 30天前日期 
		    let previousDate = newArr[0]
		   
			setMonthList(newArr)
			getMonthList(newArr)
		  return newArr
	}

	
	const getDay =(startDate, endDate)  =>{
		var result = new Array();
		var ab = startDate.split("-")
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
		return result
	}
	// 日期格式化
	const formatDate = (date, fmt) =>{
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
	// 实时可调负荷
	const chosedate =(e) =>{

		if (e == 0) {
			
			getdatList()
		} else if (e == 1) {
			day7()
			
		} else if (e == 2) {
			get_date(30)
			
		}
		
		setCurrentState(e)
		setTs_s('')
		setTs_e('')
	}

	// 获取当前日期前7天日期集合
	const day7 =() =>{
		// 获取当前日期
		var currentDate = new Date();
		
		// 获取前七天的日期
		var previousDates = [];
		for (var i = 6; i >= 0; i--) {
		  var previousDate = new Date(currentDate);
		  previousDate.setDate(currentDate.getDate() - i);
		  previousDates.push(previousDate);
		}
		
		// 格式化日期
		var formattedDates = previousDates.map(function(date) {
		  var year = date.getFullYear();
		  var month = (date.getMonth() + 1).toString().padStart(2, '0');
		  var day = date.getDate().toString().padStart(2, '0');
		  return year + '-' + month + '-' + day;
		});
		
		var time = [];
		for(var d=0;d<formattedDates.length;d++){
			const m = ["00", "15", "30", "45"];
			for (let i = 0; i < 24; i++) {    
				for(let j=0;j<4;j++){
					if (i === 0 && j === 0) { continue; }
					const t =formattedDates[d]+' ' + (i < 10 ? "0" + i : "" + i) + ":" + m[j];
					
					time.push(t +":00");
				}  
			}
			time.push(formattedDates[d]+' '+"00:00:00");
			


		}
		// property是你需要排序传入的key,bol为true时是升序，false为降序
		function dateData( bol) {
			return function(a, b) {
				var value1 = a;
				var value2 = b;
				if (bol) {
					// 升序
					return Date.parse(value1) - Date.parse(value2);
				} else {
					// 降序
					return Date.parse(value2) - Date.parse(value1)
				}
			}
		}

		setFormattedDates(time.sort(dateData(true)))
		getSevenDaysList(time.sort(dateData(true)))

	  }
	      
	// 实时可调负荷
	// 排序
	const dateData =( bol) =>{
		return function(a, b) {
			var value1 = a;
			var value2 = b;
			if (bol) {
				// 升序
				return Date.parse(value1) - Date.parse(value2);
			} else {
				// 降序
				return Date.parse(value2) - Date.parse(value1)
			}
		}
	}
	const derivation = () => {

			if (currentState == 0) {
				axios({
					method: 'post',
					url:'mainExcel/getNearlyADayListExcel',
					responseType: 'arraybuffer',

				}).then(res => {
					if (res.status == 200) {
						const url = window.URL.createObjectURL(new Blob([res.data]));
						const link = document.createElement('a'); //创建a标签
						link.style.display = 'none';
						link.href = url; // 设置a标签路径
						link.download =
						'报表1.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
						document.body.appendChild(link);
						link.click();
						URL.revokeObjectURL(link.href); // 释放 URL对象
						document.body.removeChild(link);

					}
				})
			} else if (currentState == 1) {
				axios({
					method: 'post',
					url:  'mainExcel/getNearlySevenDaysListExcel',
					responseType: 'arraybuffer',

				}).then(res => {
					if (res.status == 200) {
						const url = window.URL.createObjectURL(new Blob([res.data]));
						const link = document.createElement('a'); //创建a标签
						link.style.display = 'none';
						link.href = url; // 设置a标签路径
						link.download =
						'报表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
						document.body.appendChild(link);
						link.click();
						URL.revokeObjectURL(link.href); // 释放 URL对象
						document.body.removeChild(link);

					}
				})
			} else if (currentState == 2) {
				axios({
					method: 'post',
					url: 'mainExcel/getNearlyAMonthListExcel',
					responseType: 'arraybuffer',

				}).then(res => {
					if (res.status == 200) {
						const url = window.URL.createObjectURL(new Blob([res.data]));
						const link = document.createElement('a'); //创建a标签
						link.style.display = 'none';
						link.href = url; // 设置a标签路径
						link.download =
						'报表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
						document.body.appendChild(link);
						link.click();
						URL.revokeObjectURL(link.href); // 释放 URL对象
						document.body.removeChild(link);

					}
				})

			}else if(currentState==3){
				axios({
					method: 'post',
					url: 'mainExcel/getAutoMonthListExcel',
					responseType: 'arraybuffer',
					data: {
						'ts_s': ts_s,
						'ts_e': ts_e
					}
				}).then(res => {
					if (res.status == 200) {
						const url = window.URL.createObjectURL(new Blob([res.data]));
						const link = document.createElement('a'); 
						link.style.display = 'none';
						link.href = url; 
						link.download = '月报表.xlsx'; 
						link.click();
						URL.revokeObjectURL(link.href); 
						document.body.removeChild(link);
				
					}
				})
			}
		

	}
	
	// 实时可调负荷选择月份
	const changemonth = (e, dataString) => {
		let start = ''
		let end = ''
		if(dataString.length>0){
			var startab = dataString[0].split("-")
			var endab = dataString[1].split("-")
			start = dataString[0]+'-'+'01'
			if(endab[1]=='01'||endab[1]=='03'||endab[1]=='05'||endab[1]=='07'||endab[1]=='08'||endab[1]=='10'||endab[1]=='12'){
				end=dataString[1]+'-'+'31'
			}else if(endab[1]=='02'){
				if(endab[0] % 4 == 0 && endab[0] % 100 != 0 || endab[0] % 400 ==0){
					end=dataString[1]+'-'+'29'
				}else{
					end=dataString[1]+'-'+'28'
				}
			}else{
				end=dataString[1]+'-'+'30'
			}
		}
		// this.getDay()
		setTs_e(dataString[1]) ;
		setTs_s(dataString[0]);
		setCurrentState(3);
		setPanier(getDay(start,end));
		axios.post('flexible_resource_management/main/getAutoMonthList', {
			"ts_e": dataString[1],
			"ts_s": dataString[0]
		}).then(res => {
			console.log(res)
			if (res.data.code == 200) {
				let data = res.data.data
				// $scope.opRouteNew = [1,2,3,4,5];
				// $scope.opRoutes[1,2,3]
				let panier1 = panier
				let list = []
				panier1.forEach((item,index)=>{
					let isExit = false　
					data.forEach((value,index)=>{
						if(item == value.ts) {
							
							isExit = true
							
							list.push(value);　
							return　　
						}
					})
					if(!isExit) {
						list.push({
							ts:item,
							value:''
						});
					}
				})
				let dayL = []
				let weight = []
				let newArr = []
				data.map(res => {
					dayL.push(res.ts)
					weight.push(res.value)
				})
				for(var i=0;i<weight.length;i++){
					newArr.push(weight[i])
				}
				
				setDayL(dayL)
				setWeight(newArr)
				setIsEmpty(data.length>0?true:false)
				weights(dayL,newArr,data.length>0?true:false)
			}
		})
		
	}
	// 实时可调负荷近一天
	const getdatList=() =>  {
		var chartDom = document.getElementById('weight');
		var myChart = echarts.init(chartDom);
		myChart.showLoading({
			text: '数据加载中...',
			color: '#FFF',
			textColor: '#FFF',
			maskColor: 'rgba(255, 255, 255, 0)',
			zlevel: 0
		});
		var time = [];
	   const currentDate = new Date();
	  currentDate.setDate(currentDate.getDate() - 1);
	  
	  const day1 = currentDate.toISOString().split('T')[0]; // 格式化为 "YYYY-MM-DD"
	  let oneDay =  dayjs().format('YYYY-MM-DD')
	  let oneTime = day1 + ' ' + dayjs().format('HH:mm')+':00'
	  let nowTime = dayjs().format('YYYY-MM-DD HH:mm')+':00'
	  let formattedDates = []
	  formattedDates.push(day1)
	  formattedDates.push(oneDay)
	  for(var d=0;d<formattedDates.length;d++){
	  	const m = ["00", "15", "30", "45"];
	  	for (let i = 0; i < 24; i++) {    
	  		for(let j=0;j<4;j++){
	  			if (i === 0 && j === 0) { continue; }
	  			const t =formattedDates[d]+' ' + (i < 10 ? "0" + i : "" + i) + ":" + m[j];
	  			
	  			time.push(t +":00");
	  		}  
	  	}
	  	time.push(formattedDates[d]+' '+"00:00:00");
	  	
	  
	  
	  }
	  
		let timeDate = time.sort(dateData(true))
		let dayList = timeDate.sort(dateData(true))
		
		axios.post('flexible_resource_management/main/getNearlyADayList').then(res => {
			console.log(res)
			if (res.data.code == 200) {
				if(res.data.data){
					let data = res.data.data
					let dayL = []
					let weight = []
					let newArr = []
					let list = []
					let oneList = []
					let bigList = []
					
					dayList.forEach((item,index)=>{
						let isExit = false　// 给一个状态值好进行判断
						data.forEach((value,index)=>{
							if(item == value.ts) {
								isExit = true
								list.push(value);　
								// console.log(list)
								return　　// 使用return是因为在forEach循环中continue和break用不了
							}
						})
						if(!isExit) {
							list.push({
								ts:item,
								value:''
							});　
							　　　// 不等于的数据就添加进list数组
						}
					})
					const time1 = '2022-01-02 09:00:00';
					const time2 = '2022-01-01 09:30:00';
					
					if (time1 < time2) {
					  console.log('time1 比 time2 更早');
					} else if (time1 > time2) {
					  console.log('time1 比 time2 更晚');
					} else {
					  console.log('time1 和 time2 相等');
					}
					for(var i=0;i<list.length;i++){
						
						if(oneTime < list[i].ts){
							oneList.push(list[i])
						}
						
					}
					// console.log(oneList)
					for(var i=0;i<oneList.length;i++){
						if(nowTime>oneList[i].ts){
							// console.log(list[i])
							// console.log(new Date(Date.parse(list[i].ts));)
							bigList.push(oneList[i])
						}
					}
					// console.log(bigList)
					bigList.map(res => {
						dayL.push(res.ts)
						newArr.push(res.value)
					})
					// for(var i=0;i<weight.length;i++){
					// 	newArr.push(weight[i])
					// }
					// console.log(newArr)
					// this.setState({
					// 	dayL: dayL,
					// 	// weight: weight
					// 	weight:newArr,
					// 	// isEmpty:true
					// 	isEmpty:data.length>0?true:false
					// }, () => {
					// 	// alert(0)
					// 	weights()
					// })
					setDayL(dayL)
					setWeight(newArr)
					setIsEmpty(data.length>0?true:false)
					weights(dayL,newArr,data.length>0?true:false)
					
				}else{
					
					setIsEmpty(false)
				}
				
			}else{
				// alert(0)
				myChart.hideLoading()
				// message.success()
			}
		}).catch(err => {
			console.log(err)
		})
	}
	// 实时可调负荷选择月份近7天
	const getSevenDaysList=(formattedDates) =>  {
		// let {formattedDates} = this.state
		var chartDom = document.getElementById('weight');
		var myChart = echarts.init(chartDom);
		myChart.showLoading({
			text: '数据加载中...',
			color: '#FFF',
			textColor: '#FFF',
			maskColor: 'rgba(255, 255, 255, 0)',
			zlevel: 0
		});
		let nowTime = dayjs().format('YYYY-MM-DD HH:mm')+':00'
		axios.post('flexible_resource_management/main/getNearlySevenDaysList').then(res => {
			console.log(res)
			if (res.data.code == 200) {
				let data = res.data.data
				let dayL = []
				let weight = []
				let newArr = []
				let list = []
				// data.map(res => {
				// 	dayL.push(res.ts)
				// 	weight.push(res.value)
				// })
				for(var i=0;i<weight.length;i++){
					newArr.push(weight[i])
				}
				
				formattedDates.forEach((item,index)=>{
					let isExit = false　// 给一个状态值好进行判断
					data.forEach((value,index)=>{
						if(item == value.ts) {
							isExit = true
							
							list.push(value);　
							// console.log(list)
							return　　// 使用return是因为在forEach循环中continue和break用不了
						}
					})
					if(!isExit) {
						
						list.push({
							ts:item,
							value:''
						});　
						　　　// 不等于的数据就添加进list数组
					}
				})
				let oneList = []
				for(var i=0;i<list.length;i++){
					if(nowTime>list[i].ts){
						oneList.push(list[i])
					}
				}
				oneList.map(res => {
					dayL.push(res.ts)
					newArr.push(res.value)
				})
				
				// console.log(dayL)
				// console.log(newArr)
				// this.setState({
				// 	dayL: dayL,
				// 	// weight: weight
				// 	weight:newArr,
				// 	isEmpty:data.length>0?true:false
					
				// }, () => {
				// 	weights()
				// })
				setDayL(dayL)
				setWeight(newArr)
				setIsEmpty(data.length>0?true:false)
				weights(dayL,newArr,data.length>0?true:false)
			}else{
				myChart.hideLoading()
			}
		}).catch(err => {
			console.log(err)
		})
	}
	// 实时可调负荷选择月份近一月
	const getMonthList=(monthList) =>  {
		var chartDom = document.getElementById('weight');
		var myChart = echarts.init(chartDom);
		myChart.showLoading({
			text: '数据加载中...',
			color: '#FFF',
			textColor: '#FFF',
			maskColor: 'rgba(255, 255, 255, 0)',
			zlevel: 0
		});
		axios.post('flexible_resource_management/main/getNearlyAMonthList').then(res => {
			console.log(res)
			if (res.data.code == 200) {
				let data = res.data.data
				let dayL = []
				let weight = []
				let newArr = []
				let list = []
				// data.map(res => {
				// 	dayL.push(res.ts)
				// 	weight.push(res.value)
				// })
				// for(var i=0;i<weight.length;i++){
				// 	newArr.push(weight[i])
				// }
				monthList.forEach((item,index)=>{
					let isExit = false　// 给一个状态值好进行判断
					data.forEach((value,index)=>{
						if(item == value.ts) {
							isExit = true
							list.push(value);　
							// console.log(list)
							return　　// 使用return是因为在forEach循环中continue和break用不了
						}
					})
					if(!isExit) {
						
						list.push({
							ts:item,
							value:''
						});　
						　　　// 不等于的数据就添加进list数组
					}
				})
				list.map(res => {
					dayL.push(res.ts)
					newArr.push(res.value)
				})
				// this.setState({
				// 	dayL: dayL,
				// 	// weight: weight
				// 	weight:newArr,
				// 	isEmpty:data.length>0?true:false
				// }, () => {
				// 	this.weights()
				// })
				setDayL(dayL)
				setWeight(newArr)
				setIsEmpty(data.length>0?true:false)
				weights(dayL,newArr,data.length>0?true:false)
			}else{
				myChart.hideLoading()
			}
		}).catch(err => {
			console.log(err)
		})
	}
	//实时可调负荷
	const weights=(dayL,newArr,isEmpty) =>  {
		var chartDom = document.getElementById('weight');
		var myChart = echarts.init(chartDom);
		
		var option;
		option = {
			color:["rgba(0, 146, 255, 1)"],
			title: {
				text: '单位：kW',
				textStyle: {
					fontSize: 12,
					color: '#D1D2D6',
					fontWeight:'normal'
				},
				left: 16
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
			// tooltip: {
			// 	trigger: 'axis'
			// },
			tooltip: {
				trigger: 'axis',
				backgroundColor: '#302F39',
				borderColor: 'transparent',
				textStyle: {
					color: '#fff' // 设置 tooltip 的文字颜色为白色
				},
				formatter(params) {
					var relVal = params[0].name;
					for (var i = 0, l = params.length; i < l; i++) {
						if(params[i].value===null||params[i].value===undefined||params[i].value===''){
							relVal +='<br/>' + params[i].marker+'-'
						}else{
							relVal += '<br/>' + params[i].marker +'实时可调负荷'+ '<br/>'+ Number(params[i].value).toFixed(2) 
						}
						
					}
					return relVal;
				},
					
			},

			grid: {
				top: 40,
				left: 0,
				right: 0,
				bottom: 0,
				containLabel: true
			},
			xAxis: {
				type: 'category',
				boundaryGap: true,
				axisLabel: { //x坐标轴刻度标签
					show: true,
					color: '#AEAEAE', //'#ccc'，设置标签颜色
					formatter: `{value}`
				},
				axisLine: {
					show: true, // X轴 网格线 颜色类型的修改
					lineStyle: {
						color: '#AEAEAE'
					}
				},
				data: dayL
			},
			yAxis: {
				type: 'value',
				splitLine: {
					show: true,
					lineStyle: {
						type: 'dashed',
						color: '#AEAEAE'
					}
				},
				axisLabel: { //x坐标轴刻度标签
					show: true,
					color: '#AEAEAE', //'#ccc'，设置标签颜色
					formatter: `{value}`
				},
			},
			series: [{
				data: newArr,
				type: 'line',
				smooth: 0.6,
				symbol: 'none',
				lineStyle: {
					color: '#0092FF',
					width: 2
				},
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
			}]
		};
		myChart.hideLoading()
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
	}
	//楼宇/工厂/基站/IDC 数量统 
	const getNodeTypeNumberCount=() => {
			let resultArray  = []
			for(var i=0;i<3;i++){
				axios.post('flexible_resource_management/main/getNodeTypeNumberCount?index='+ i).then(res =>{
					console.log(res,'-------')
					if(res.data.code ==200){
						let data = res.data.data
						resultArray.push(data)

						
					}
					
					
				}).catch(err =>{
					console.log(err)
				})
			}
	}
	// 储能资源
	const storageEnergyCount=() => {
		axios.post('load_management/storage_energy/storageEnergyCount').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				
				setCsoc((Number(data.soc)*100).toFixed(2));
				setCsoh((Number(data.soh)*100).toFixed(2));
				setCcapacity(Number(data.capacity).toFixed(2));
				setInCapacity(data.inCapacity);
				setCload(Number(data.load).toFixed(2));
				setCmaxInLoad(data.maxInLoad);
				setCmaxOutLoad(data.cmaxOutLoad);
				setOutEnergy(data.outEnergy);
				setStoredenergy(data.storedenergy);
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 光伏资源
	const pvCount=() => {
		axios.post('load_management/power_generation/pvCount').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				// this.setState({
				// 	gucapacity: Number(data.capacity).toFixed(2),	//装机容量
				// 	guenergy: data.energy,		//累计发电量
				// 	guload: Number(data.load).toFixed(2),			//实际发电功率
				// 	gunowEnergy: data.nowEnergy,	//当日发电量
				// 	photovoltaic:data.photovoltaic	//光伏数量

				// })
				setGucapacity(Number(data.capacity).toFixed(2));	//装机容量
				setGuenergy(data.energy);		//累计发电量
				setGuload(Number(data.load).toFixed(2));		//实际发电功率
				setGunowEnergy(data.nowEnergy) ;	//当日发电量
				setPhotovoltaic(data.photovoltaic);	//光伏数量
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 可调负荷
	const getLoadCount=() => {
		axios.post('flexible_resource_management/main/getLoadCount').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data;
				let totalLoad = data.totalLoad;
				let load = 0;
				setKtotalLoad(Number(data.totalLoad).toFixed(2));
				setKload(Number(data.load).toFixed(2));
				setKjieRuLoad(Number(data.jieRuLoad).toFixed(2));
				setLoadNumber(data.loadNumber);
				if(osType=='loadType'){
					getLoadNatureClassification()
					getLoadTypeClassification()
					
				}
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	
	
	
	
	// 工厂园区
	const factory=() =>  {
		var chartDom1 = document.getElementById('factory');
		var myChart = echarts.init(chartDom1);
		var option;
		option = {
			tooltip: {
				trigger: 'item'
			},
			legend: {
				formatter: function(name) {
					var data = option.series[0].data;
					var total = 0;
					var tarValue;
					for (var i = 0; i < data.length; i++) {
						total += data[i].value;
						if (data[i].name == name) {
							tarValue = data[i].value;
						}
					}
					
					var v = tarValue;
					// console.log(v)
					if(v===undefined){
						v =''
					}
					var p = Math.round(((tarValue / total) * 100));
					return `${name}  ${v}kW`;
				},
				icon: 'circle',
				orient: 'vertical',
				right: '20%',
				top: 50,
				textStyle: {
					color: '#FFFFFF'
				}
			},
			series: [{
				name: '工厂',
				type: 'pie',
				radius: '60%',
				center: ['25%', '50%'],
				labelLine: {
					show: false
				},
				label: {
					show: false,
					position: 'center'
				},
				data:gloadRatio,
				// data:[
				// 	{ value: 250, name: '空调' },
				// 	{ value: 180, name: '照明' },
				// 	{ value: 1400, name: '充电桩' },
				// 	{ value: 50, name: '热水器' }
				//   ],
				label: {
					normal: {
						show: true,
						position: 'inner', // 数值显示在内部
						formatter: '{d}%', // 格式化数值百分比输出
						color: '#FFFFFF'
					},


				},
				emphasis: {
					itemStyle: {
						shadowBlur: 10,
						shadowOffsetX: 0,
						shadowColor: 'rgba(0, 0, 0, 0.5)'
					},


				}

			}]
		}
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
	}
	// 底下四个
	const getCountChartByIndex=(val)=> {
		// let {naturalList} = this.state
		
		axios.post('flexible_resource_management/main/getCountChartByIndex?index='+val).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let loadRatio =JSON.parse(data.loadRatio)
				resourcesList.push(data)
				let resources = resourcesList
				
				resources.map(res =>{					
					res.newloadRatio = JSON.parse(res.loadRatio)
					res.title = res.name
					res.data = Object.entries(JSON.parse(res.loadRatio)).map(([name, value]) => {
					  return { name, value };
					});
				})
				// if(resourcesList.length==4){
					if(resources.length==naturalList.length){
						resources.sort(function(a, b) {
							return naturalList.findIndex(obj => obj.name === a.name) - naturalList.findIndex(obj => obj.name === b.name);
						});
						
					
					}
				// }
				
				
				setAllloading(false)
				setResourcesList(resources)
			}else{
				setAllloading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 场景分类
	const getNodeTypeIdItems=() => {
		axios.post('flexible_resource_management/main/getNodeTypeIdItems').then(res =>{
			console.log(res)
			// console.log(res.data.data)
			if(res.data.code==200){
				let data = res.data.data
				
				
				const processAllData = async () => {
				    for (let i = 0; i < data.length; i++) {
				      try {
						await axios.get('flexible_resource_management/main/getSceneClassification?nodeTypeId='+data[i]).then(res =>{
							console.log(res)
							if(res.data.code==200){
								let data = res.data.data
								let newDate = []
								newDate.push(data)
								newDate.map(res =>{
									res.newloadRatio = JSON.parse(res.loadRatio)
									res.title = res.name
									res.data = Object.entries(JSON.parse(res.loadRatio)).map(([name, value]) => {
									  return { name, value };
									});
								})
								setResourcesList((prevstate) => {
									// eslint-disable-next-line no-unused-expressions
									return [...prevstate,...newDate]
								})
								setAllloading(false)			
							}
						}).catch(err =>{
							console.log(err)
						})
				      } catch (error) {
				        console.error(`Error processing data ${i+1}:`, error);
				      }
				    }
					
				  };
				
				  processAllData();

			}else{
				message.info(res.data.msg)
				setAllloading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const getSceneClassification =(val) =>{
		let {naturalList} = this.state
		axios.get('flexible_resource_management/main/getSceneClassification?nodeTypeId='+val).then(res =>{
			console.log(res)
			if(res.data.code==200){
				// let data = res.data.data
				// console.log(data)
				// console.log(JSON.parse(data.loadRatio))
				// let loadRatio =JSON.parse(data.loadRatio)
				// this.state.resourcesList.push(data)
				// console.log(this.state.resourcesList)
				
				// let resources = this.state.resourcesList
				
				// resources.map(res =>{					
				// 	res.newloadRatio = JSON.parse(res.loadRatio)
				// 	res.title = res.name
				// 	res.data = Object.entries(JSON.parse(res.loadRatio)).map(([name, value]) => {
				// 	  return { name, value };
				// 	});
				// })
				// console.log(resources,'------')
				
				// this.setState({
				// 	resourcesList:resources
				// },() =>{
				// 	console.log(this.state.resourcesList)
				// })
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
		const range = (start: number, end: number) => {
			const result = [];
			for (let i = start; i < end; i++) {
				result.push(i);
			}
			return result;
		};
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current < dayjs(new Date('2023-01')) || current > dayjs().endOf('month');
		};
		const dateFormat = 'YYYY-MM';	
		const nextSlide = () => {
		   
			setStartIndex((startIndex + 2) % resourcesList.length)
			setCurrentPage((currentPage+1))
		};
		
		const prevSlide = () => {
			
			setStartIndex((startIndex - 2 + resourcesList.length) % resourcesList.length)
			setCurrentPage((currentPage-1))
		};
		useEffect(() => {
		    const resizeObserver = new ResizeObserver(entries => {
		      for (let entry of entries) {
		        const { width } = entry.contentRect;
				var myChart = echarts.init(document.getElementById('weight'));
				myChart.resize()
		      }
		    });
		
		    if (divRef.current) {
		      resizeObserver.observe(divRef.current);
		    }
		
		    return () => {
		      resizeObserver.disconnect();
		    };
		}, []);
		return (

			<div className = "allcontent12 allcontent14 resource" >
			
				<div className = "headerresource" >
					{
						osType=='loadType'?(
							<div className="loadTypes">
								<h4>可调负荷</h4>
								<ul>
									<li>
										<img src = {require('../../style/load/node.png')}/>
										<div>
											<span>{loadNumber}</span>
											<p>节点总数</p>
										</div>
									</li>
									<li>
										<img src = {require('../../style/load/fuhe.png')}/>
										<div>
											<span>{ktotalLoad}</span>
											<p>总可调负荷(kW)</p>
										</div>
									</li>
									<li>
										<img src = {require('../../style/load/jiefuhe.png')}/>
										<div>
											<span>{kjieRuLoad}</span>
											<p>接入负荷(kW)</p>
										</div>
									</li>
									<li>
										<img src = {require('../../style/load/shi.png')}/>
										<div>
											<span>{kload}</span>
											<p>实时可调负荷(kW)</p>
										</div>
									</li>
								</ul>
							</div>
						):(
							<ul className = "tabulation" style={osType=='loadType'? { paddingTop: '15px' } : { paddingTop: '0px' }}>
								{
									naturalList&&naturalList.map(res =>{
										return <li ><img src = {res.name=='商业综合体'?shang:res.name=='工厂园区'?factory:
										res.name=='储能电站'?chunengimg:res.name=='光伏电站'?guangfuimg:res.name=='通信基站'?tong:
										res.name=='充电桩项目'?chong:res.name=='轨道交通'?gui:res.name=='数据中心'?shu
										:res.name=='政府办公'?zheng:res.name=='qita'?other:other}/> 
													<span>{res.name}<br / > 
													< b>{res.number} </b></span >
												</li> 
									})
								}
								
							</ul> 
						)
					}
					
					<div className = "charge" ref={divRef}  style={osType=='loadType'? { marginTop: '16px' } : { marginTop: '0px' }}>
						<h4>实时可调负荷 
							<div className = "chosedates" >
								<Space direction = "vertical"
									size = { 12} >
									<ConfigProvider  >
										<RangePicker locale={locale}  onChange = {changemonth} picker = "month" 
										disabledDate={disabledDate}
										style={{
										      color: '#FFF', // 将placeholder的颜色设置为灰色
										      // 其他需要覆盖的样式...
										    }}
										value={ts_s!=''&&ts_e!=''? [dayjs(ts_s, dateFormat), dayjs(ts_e, dateFormat)] : undefined}
										/>
									</ConfigProvider> 
								</Space> 
								<Button ghost onClick = {derivation} > 导出 </Button> 
							</div> 
							<ul className = "daylists" > 
								{
									daylist.map((i, index) => {
										return <li className = {
											index === currentState ? "active" : null
										}
										key = {index}
										onClick = {() => chosedate(index)} > {i} </li>
									})
								} 
							</ul>
					
						</h4> 
						<div id = "weight"> </div> 
					</div>
				</div>	
				<div>
					{
						osType=='loadType'?(
						<div className = "natural directionnatural" >
							<div className = "directions directions2" >
								<h4 > 负荷性质分类 </h4> 
								<div  className = "quantity" style={{border:'none',marginTop:10}}>
									<div id="nature"></div>
								</div> 	
							</div> 
							<div className = "direction directions2" >
								<h4> 负荷类型分类 </h4>
								<div  className = "quantity" style={{border:'none',marginTop:10}}>
									<div id="typesnature"></div>
								</div> 
							</div>
						</div>
						) :(
							<div className = "natural" >
								<div className = "directions" >
									<h4 > 光伏资源 </h4> 
									<div className = "quantity" style={{paddingBottom:20,border:'none'}}>
										<div className = "quanleft">
											<b>{photovoltaic}</b> <p > 光伏电站数量 </p> </div> <div className = "quanright" >
											<div>
												<p > 装机容量 </p> 
												<b style = {{margin: '15px'}} > </b><br / >
												<span > {gucapacity} kW </span> </div> 
												<div>
													<p > 实时功率 </p>
													<b style = {{margin: '15px'}}> </b><br / >
													<span > {guload} kW </span> 
												</div>
											</div> 
										</div> 
									</div> 
									<div className = "direction" >
										<h4> 储能资源 </h4>
										<div className = "quantity" style={{border:'none'}}>
											<div className = "quanleft" >
												<b> {storedenergy}</b> 
												<p > 储能电站数量 </p>
											 </div> 
											<div className = "quanright" >
												<div>
													<p> 电站总容量 </p> 
													<b> </b><br />
													<span> {ccapacity} kWh </span> 
												</div>
											<div >
												<p> 电站总功率 </p> 
												<b > </b><br / >
												<span> {cload} kW </span> 
											</div> 
											<div>
												<p> SOH </p> <b ></b><br / >
												<span >{csoh} % </span> 
											</div> 
											<div>
												<p> SOC </p> <b> </b><br / >
												<span > {csoc}% </span>
											</div> 
										</div> 
									</div> 
								</div>
							</div>
						)
					}
				</div>
				
				
				<div className="allload">	
					<Spin spinning={allloading}>
					<div className = "load" >					
						<h4> 场景分类 </h4 >						 
					</div> 
					
					<div className="home-lunbo" loading> 
						<div className = "building" style={{height:'auto',overflow:'hidden',paddingBottom:40}}>
							<div className="carousel">
							      <button onClick={prevSlide} className="prev"
									 disabled={currentPage === 1}
								  ><LeftOutlined /></button>
							      <div className="slides">
								  
							        {
										resourcesList.length>0?(
											resourcesList.slice(startIndex, startIndex + 2).map((item,index) => (
													<div key={index} className="slide" style={index % 2 === 0 ? { marginRight: '8px',width:'calc(50% - 8px)' } : { marginLeft: '8px',width:'calc(50% - 8px)' }}>
														<div className = "title" >
															<img src = {item.name=='商业综合体'?shang:item.name=='商业写字楼'?shang:item.name=='工厂园区'?factoryimg:
															item.name=='储能电站'?chunengimg:item.name=='光伏电站'?guangfuimg:item.name=='通信基站'?tong:
															item.name=='充电桩项目'?chong:item.name=='轨道交通'?gui:item.name=='数据中心'?shu
															:item.name=='政府办公'?zheng:item.name=='qita'?other:other}/> 
															{item.name}（{item.number}）
																		 
														</div>
														<div className = "storiedcont" >
															<div >
																<p > 实时可调负荷(kW) </p>
																<span >  {toFixedTwo(item.load)}</span> 
															</div> 
															<div >
																<p >接入负荷（kW） </p> 
																<span > {toFixedTwo(item.jieRuLoad)} </span> 
															</div> 
															<div >
																<p >可调负荷(kW) </p> 
																<span > {toFixedTwo(item.totalLoad)}</span> 
															</div> 
															
															
														</div> 
														{
															item.data.length>0?(
																<div style={{height:230,marginTop:20}} key={index}>
																	<ECharts  key={item.name}
																		option={{
																			tooltip: {
																				trigger: 'item',
																				backgroundColor: '#302F39',
																				borderColor: 'transparent',
																				textStyle: {
																					color: '#fff' 
																				},
																			},
																			color:['#5B8FF9','#5AD8A6','#5D7092','#F6BD16','#E8684A'],
																			legend: {
																				data: item.data.map((item1) => item1.name),
																				formatter: (name) => {
																					const item1 = item.data.find((item) => item.name === name);
																					
																					return `{a|${name}} ${toFixedTwo(item1.value)}`
																				},
																				icon: 'circle',
																				orient: 'vertical',
																				left: '60%',
																				top:'center',
																				itemHeight: 8,
																				itemWidth: 8,
																				selectedMode: false,
																				textStyle: {  
																					lineHeight:20,
																					color: '#FFF',
																					rich: {
																						a: {
																							fontSize: 14,
																							width: 80,
																							color: '#FFF',
																							width:110
																							
																						},
																					}
																				}
																			},
																			grid:{
																				top:'center'
																			},
																			series: [{
																				// name: item.name,
																				type: 'pie',
																				radius: '90%',
																				center: ['25%', '50%'],
																				labelLine: {
																					show: false
																				},
																				label: {
																					show: false,
																					position: 'center'
																				},
																				data:item.data,
																				// data:[
																				// 	{ value: 250, name: '空调' },
																				// 	{ value: 180, name: '照明' },
																				// 	{ value: 1400, name: '充电桩' },
																				// 	{ value: 50, name: '热水器' }
																				//   ],
																				label: {
																					normal: {
																						show: true,
																						position: 'inner', // 数值显示在内部
																						// formatter: '{d}%', // 格式化数值百分比输出
																						color: '#FFF',
																						formatter: function (params) {
																							if(params.value === null||params.value===undefined||params.value===''||params.value=='-') {
																								return '-'
																							} else {
																								return (params.percent).toFixed(2) + '%'
																							}
																						}
																					},
																			
																			
																				},
																				emphasis: {
																					itemStyle: {
																						shadowBlur: 10,
																						shadowOffsetX: 0,
																						shadowColor: 'rgba(0, 0, 0, 0.5)'
																					},
																			
																			
																				}
																			
																			}]
																		  }}
																		  style={{ height: 200 }} // 设置图表的高度
																	/>
																</div>
																
															):(
																<Empty description={false} style={{marginTop:40,height:170}} />
															)
														}
													</div>
													
											))
										):(
											<div>
												<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
											</div>
										)
										
									
									}
							      </div>
							      <button onClick={nextSlide} 
								 disabled={currentPage === Math.ceil(resourcesList.length / displayPerPage)}
								 
								  className="next">
								  <RightOutlined /></button>
							    </div>
							
						</div>
						
					</div>
					</Spin>
				
				</div>
			</div>
		)
	}



export default Resource
