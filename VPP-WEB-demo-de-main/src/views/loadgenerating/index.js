import React,{useEffect, useState,useRef,useCallback} from 'react'
// import 'antd/dist/antd.css'
// import 'antd/dist/antd.min.css';
import './index.css'
import './index.scss'
import { Tree,DatePicker,ConfigProvider,Button,Table,Input  ,Select ,Typography ,message,Spin,Cascader } from 'antd';
import dayjs from 'dayjs';
import http from '../../server/server.js'
import axios from 'axios'
import * as echarts from 'echarts';
// import echarts  from '../echarts.js'
import car from '../../style/electric/icon1.png'
import icon1 from '../../style/electric/icon9.png'
import icon3 from '../../style/electric/icon8.png'
import icon10 from '../../style/electric/icon10.png'
import icon11 from '../../style/electric/icon11.png'
import icon4 from '../../style/electric/icon4.png'
import icon5 from '../../style/electric/icon5.png'
import icon6 from '../../style/electric/icon6.png'
import icon12 from '../../style/electric/icon12.png'
import icon17 from '../../style/electric/icon17.png'
import icon19 from '../../style/electric/icon19.png'
import icon20 from '../../style/electric/icon20.png'
import icon15 from '../../style/electric/icon15.png'
import icon16 from '../../style/electric/icon16.png'
import markers from '../../style/damao/markers.png'
// import loa from '../../style/electric/icon16.png'
// import myStyleJson from '../../style/json/custom_map_config.json';
import myStyleJson from '../../style/json/map.json';
import chinaMaps from './json/chinaMap.json'
import {PlusOutlined} from '@ant-design/icons';
import newdata from './json/city.json';
import yong from './img/yong.svg';
import tan from './img/tan.svg';
import operate from './img/operate.svg'
import operateactive from './img/operateactive.svg'
import constructactive from './img/constructactive.svg'
import loadimg from './img/load.svg'
import construct from './img/construct.svg'
import plan from './img/plan.svg'
import planactive from './img/planactive.svg'
import closeactive from './img/closeactive.svg'
import close from './img/close.svg'
const { Option } = Select;
const { RangePicker } = DatePicker;
const axiosInstance = axios.create({
   baseURL: 'http://localhost:' + window.location.port
});

// class generating extends Component {
const Loadgenerating= () => {
	const [dateString,setDateString] = useState('');
	const [deviceObjmyCompOverlay,setDeviceObjmyCompOverlay] = useState('');
	const [day,setDay] = useState('0.00');
	const [hour,setHour] = useState('0.00');
	const [minute,setMinute] = useState('0.00');
	const [seconds,setSeconds] = useState('0.00');
	const [capacity,setCapacity] = useState('');
	const [energy,setEnergy] = useState('');
	const [load,setLoad] = useState('');
	const [nowEnergy,setNowEnergy] = useState('');
	const [ccapacity,setCcapacity] = useState('');
	const [storedenergy,setStoredenergy] = useState('');
	const [photovoltaic,setPhotovoltaic] = useState('');
	const [gucapacity,setGucapacity] = useState('');
	const [ktotalLoad,setKtotalLoad] = useState('');
	const [kload,setKload] = useState('');
	const [kjieRuLoad,setKjieRuLoad] = useState('');
	const [number,setNumber] = useState('');
	const [markerArr,setMarkerArr] = useState('');
	const [carbonEmission,setCarbonEmission] = useState('');
	const [carbonEmissionReduction,setCarbonEmissionReduction] = useState('');
	const [energyConsumption,setEnergyConsumption] = useState('');
	const [generateElectricity,setGenerateElectricity] = useState('-');
	const [loadCount,setLoadCount] = useState('-');
	const [loadJieRu,setLoadJieRu] = useState('-');
	const [loadKeTiao,setLoadKeTiao] = useState('-');
	const [peakCapacity,setPeakCapacity] = useState('-');
	const [pvCapacity,setPvCapacity] = useState('-');
	const [pvCount,setPvCount] = useState('-');
	const [storageEnergyCapacity,setStorageEnergyCapacity] = useState('-');
	const [storageEnergyCount,setStorageEnergyCount] = useState('-');
	const [storageEnergyPower,setStorageEnergyPower] = useState('-');
	const [m2mEnergy,setM2mEnergy] = useState('');
	const [theLastMonthEnergy,setTheLastMonthEnergy] = useState('');
	const [theSameMonthEnergy,setTheSameMonthEnergy] = useState('');
	const [salesPerSquareMeter,setSalesPerSquareMeter] = useState('');
	const [index,setIndex] = useState(1);
	const [standardSalesPerSquareMeter,setStandardSalesPerSquareMeter] = useState('');
	const [realTimeSalesPerSquareMeter,setRealTimeSalesPerSquareMeter] = useState('');
	const [annualAccumulationSalesPerSquareMeter,setAnnualAccumulationSalesPerSquareMeter] = useState('');
	const [ts,setTs] = useState('');
	const [tableLoading,setTableLoading] = useState(true);
	const [options,setOptions] = useState('');
	const [cityVal,setCityVal] = useState(null);
	const [nodeList,setNodeList] = useState([]);
	const [endTs,setEndTs] = useState(dayjs().subtract(1, 'days'));
	const [startTs,setStartTs] = useState(dayjs().subtract(1, 'days'));
	const [osType,setOsType] = useState(sessionStorage.getItem('osType'));
	const [airdeviceValue,setAirdeviceValue] = useState('');
	const [lightdeviceValue,setLightdeviceValue] = useState('');
	const [otherdeviceValue,setOtherdeviceValue] = useState('');
	const [ChargingdeviceValue,setChargingdeviceValue] = useState('');
	const [mapHeight,setMapHeight] = useState(window.innerHeight,);
	const [mapZoom,setMapZoom] = useState(5);
	const [chinaMap,setChinaMap] = useState(null);
	const [isEmpty,setIsEmpty] = useState(false);
	const [intelligentScheduling,setIntelligentScheduling] = useState('-');
	const [nodeId,setNodeId] = useState('');
	const [timeStamp,setTimeStamp] = useState([])
	const [realValue,setRealValue] = useState([])
	const [baselineLoadValue,setBaselineLoadValue] = useState([])
	const [panier,setPanier] = useState('')
	const [baseline,setBaseline] = useState([])
	const [isFirstRender, setIsFirstRender] = useState(true);
	const [isFirstLoad,setIsFirstLoad] = useState(true)
	const divRef = useRef(null);
	const [baseUlrRef,setBaseUlrRef] = useState('');
	const [activeMarker, setActiveMarker] = useState(null);
	const mapRef = useRef(null);
	useEffect(() => {
		getHomePage()
		getUseEnergyCount()
		getUseEnergyM2MCount()		
		getTodayElectricityAndUseEnergyCount()
		getnodeList()
		getNodeInfoCount()
		getNodeLocation()
		getAirConditioning()
		getLighting()
		getOthers()
		getChargingPiles()
		// console.log(chinaMaps)
	   console.log(newdata)
	   document.getElementById("plantright").style.height = divRef.current.scrollHeight +'px';
	   // 移动端判断
		const removeStyleSheets = (styleSheet) => {
			const links = document.head.querySelectorAll('link[rel="stylesheet"]');
			links.forEach(link => {
				if (link.href.includes(styleSheet)) {
					document.head.removeChild(link);
				}
			});
		};
		var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
		if (isMobile) {
	       // 是移动端设备
	       console.log("移动-------------");
			import('./mobeil-styles.css');
			// getNodeLocation()
		} else {
	       // 是PC端设备
			console.log('pc!!!!!!!!!!')
			import('./pc-styles.css');
	       
		}
		setCityVal('全国');
		
	}, []); // 

	// 设备调节能力
	
	useEffect(() => {
		if(isFirstLoad){
			if (nodeId&&nodeList.length>0) {
			    respond();
			}
		}
	}, [nodeId,nodeList]);
	useEffect(() => {
	    if (!tableLoading) {
			containers();
	    }
	}, [markerArr, tableLoading]);
	
	// useEffect(() => {
	// 	if (nodeList.length>0) {
	// 		setTimeout(() =>{
	// 			line();
	// 		},300)
	// 	}
	   
	// }, [baselineLoadValue,realValue,timeStamp,isEmpty]);
	useEffect(() => {
		if (!isFirstRender && cityVal) {
		    containers()
		}
		// containers()
	},[cityVal,isFirstRender])
	// 判断小数点
	const toFixedTwo =(value) => {
	  // 检查值是否为null或undefined
		if (value === null||value===undefined||value===''||value=='-') {
			return '-'; // 返回'-'
		}
		// 使用toFixed保留两位小数
		return Number(value).toFixed(2);
	}
	// 空调
	const getAirConditioning =() =>{
		http.get('homePage/getAirConditioning').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				setAirdeviceValue(toFixedTwo(data.deviceValue))
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 照明
	const getLighting = () =>{
		http.get('homePage/getLighting').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				
				setLightdeviceValue(toFixedTwo(data.deviceValue))
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 其它
	const getOthers= () =>{
		http.get('homePage/getOthers').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				
				setOtherdeviceValue(toFixedTwo(data.deviceValue))
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 充电桩
	const getChargingPiles=()=>{
		http.get('homePage/getChargingPiles').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				
				setChargingdeviceValue(toFixedTwo(data.deviceValue))
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	const assignNodeId =(nodes) => {
	  let nodeId;
	  let nodelist = [];
	  const commercialBuilding = nodes.find(node => node.nodeName === '某某商业大楼');
		if (commercialBuilding) {
			nodeId = commercialBuilding.id;
			setNodeId(nodeId)
			nodelist.push(commercialBuilding)
			setNodeList(nodelist)
		} else {
			const changLeBuilding = nodes.find(node => node.nodeName === '长乐产投大楼');
			if (changLeBuilding) {
				nodeId = changLeBuilding.id;
				nodelist.push(changLeBuilding)
				console.log(changLeBuilding)
				setNodeId(nodeId)
				setNodeList(nodelist)
			}
		}
		if (nodeId) {
			console.log(`Node ID assigned: ${nodeId}`);
		} else {
			setNodeList(nodes)
			setNodeId(nodes[0].id)
		}
		return nodeId;
	}
	// 基线负荷节点列表
	const getnodeList =() =>{
		http.post('system_management/node_model/nodeNameList',{
			
		}).then(res =>{
			console.log(res)
	
			if(res.data.code==200){
				if(res.data.data.length>0){
					let data = res.data.data;
					if(data.length>0){
						assignNodeId(data);
					}else{
						setNodeList([]);
						setNodeId('')
					}
					
					
				}
				setIsFirstLoad(false)
			}else{
				// Modal.error({
				// 	title:'错误'
				// })
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	
	// 当日资源情况-节点信息分类统计
	const getNodeInfoCount=()=>{
		http.get('homePage/getNodeInfoCount').then(res =>{
			console.log(res)
			if(res.data.code==200){
				
				if(res.data.data){
					let data = res.data.data
					setLoadCount(data.loadCount)
					setLoadJieRu(toFixedTwo(data.loadJieRu))
					setLoadKeTiao(toFixedTwo(data.loadKeTiao))
					setPvCapacity(toFixedTwo(data.pvCapacity))
					setPvCount(data.pvCount)
					setStorageEnergyCapacity(toFixedTwo(data.storageEnergyCapacity))
					setStorageEnergyCount(data.storageEnergyCount)
					setStorageEnergyPower(toFixedTwo(data.storageEnergyPower))
				}
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 今日发电及用能统计-发电/用电/碳排放
	// htt
	const getTodayElectricityAndUseEnergyCount=() =>{
		http.get('homePage/getTodayElectricityAndUseEnergyCount').then(res =>{
			console.log(res)
			if(res.data.code==200){
				
				if(res.data.data){
					let data = res.data.data
					setCarbonEmission(toFixedTwo(data.carbonEmission))
					setEnergyConsumption(toFixedTwo(data.energyConsumption))
					setGenerateElectricity(toFixedTwo(data.generateElectricity))
					
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 响应级别
	const getHomePage=()=>{
		http.post('demand_resp/resp_board/getHomePage').then(res =>{
			console.log(res)
			if(res.data.code==200){
				// text==1?'日前响应':text==2?'小时响应':text==3?'分钟响应':'秒级响应'
				if(res.data.data){
					let data = res.data.data
					if(data['1']){
						// console.log(data['1'])
						
						setDay(Number(data['1']).toFixed(2))
					}
					if(data['2']){
						
						setHour(Number(data['2']).toFixed(2))
					}
					if(data['3']){
						
						setMinute(Number(data['3']).toFixed(2))
					}
					if(data['4']){
						
						setSeconds(Number(data['4']).toFixed(2))
					}
				}
				console.log(res.config.baseURL)
				let configbaseURL = res.config.baseURL
				// if(configbaseURL=='http://47.100.89.197:39090/'||configbaseURL=='http://47.122.37.132:59191'){
				// 	// 测试环境
				// 	setBaseUlrRef(1)
				// }else{
				// 	setBaseUlrRef(0)
				// }
				
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 发电/用电/碳排放/智能调度负荷/碳减排量
	const getUseEnergyCount=()=>{
		http.get('homePage/getUseEnergyCount').then(res =>{
			console.log(res)
			if(res.data.code==200){
				if(res.data.data){
					let data = res.data.data
					setCarbonEmissionReduction(toFixedTwo(data.carbonEmissionReduction))
					setIntelligentScheduling(toFixedTwo(data.intelligentScheduling))
					setPeakCapacity(toFixedTwo(data.peakCapacity))
					
				}
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 用电同比分析
	const getUseEnergyM2MCount=()=>{
		http.get('homePage/getUseEnergyM2MCount').then(res =>{
			console.log(res)
			if(res.data.code==200){
				if(res.data.data){
					let data = res.data.data
					setM2mEnergy(toFixedTwo(data.m2mEnergy))
					setTheLastMonthEnergy(data.theLastMonthEnergy=='-'?data.theLastMonthEnergy:toFixedTwo(data.theLastMonthEnergy))
					setTheSameMonthEnergy(data.theSameMonthEnergy=='-'?data.theSameMonthEnergy:toFixedTwo(data.theSameMonthEnergy))
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 逐日坪效
	const getSalesPerSquareMeterList=(val) =>{
		
		http.get('homePage/getSalesPerSquareMeterList?salesPerSquareMeter='+val).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let standardSalesPerSquareMeter = []	//标准
				let realTimeSalesPerSquareMeter = []	//实时
				let annualAccumulationSalesPerSquareMeter = []	//平均
				let ts = []
				if(res.data.data){
					let data = res.data.data
					if(data.length>0){
						data.map(res =>{
							// console.log(res)
							standardSalesPerSquareMeter.push(res.standardSalesPerSquareMeter)
							realTimeSalesPerSquareMeter.push(res.realTimeSalesPerSquareMeter)
							annualAccumulationSalesPerSquareMeter.push(res.annualAccumulationSalesPerSquareMeter)
							ts.push(res.ts)
						})
						setStandardSalesPerSquareMeter(standardSalesPerSquareMeter)
						setRealTimeSalesPerSquareMeter(realTimeSalesPerSquareMeter)
						setAnnualAccumulationSalesPerSquareMeter(annualAccumulationSalesPerSquareMeter)
						setTs(ts)
						charts()
					}else{
						setStandardSalesPerSquareMeter([])
						setRealTimeSalesPerSquareMeter([])
						setAnnualAccumulationSalesPerSquareMeter([])
						setTs([])
						charts()
					}
				}else{
						setStandardSalesPerSquareMeter([])
						setRealTimeSalesPerSquareMeter([])
						setAnnualAccumulationSalesPerSquareMeter([])
						setTs([])
						charts()
					}
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 获取省份json
	
	// 地图
	const getNodeLocation =() =>{
		
		http.get('homePage/getNodeLocation').then(res =>{
			console.log(res)
			if(res.data.code==200){

				if(res.data.data){
					let data = res.data.data
					
					setMarkerArr(data)
					setTableLoading(false)
					markerArr.length>0&&containers()
					
					
				}else{
					setMarkerArr([])
					setTableLoading(false)
					setTimeout(() => {
					    containers()
					}, 200);
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// /
	const wisdom=() =>{
		// alert(0)
		// window.open('https://workspace.easyv.cloud/shareScreen/eyJzY3JlZW5JZCI6MTY3MjcyNH0=')	//2.1
		// window.open('https://workspace.easyv.cloud/shareScreen/eyJzY3JlZW5JZCI6MjEyMjUzOH0=?timeStamp=189baa815d6')
		// window.open('https://workspace.easyv.cloud/shareScreen/eyJzY3JlZW5JZCI6MjI2ODIwMn0=?timeStamp=18ad5b86de4')
		window.open('https://workspace.easyv.cloud/shareScreen/eyJzY3JlZW5JZCI6MjMyNTQxMX0=?timeStamp=18b459d9cea')
		
	}
	
	const charts=() =>{
		
		
		var chartDom = document.getElementById('main');
		var myChart = echarts.init(chartDom);
		var option;
		
		option = {
			
			tooltip: {
				trigger: 'axis',
				axisPointer: {
					type: 'cross',
					label: {
						backgroundColor: '#6a7985'
					}
				},
				formatter(params) {
					var relVal = params[0].name;
					for (var i = 0, l = params.length; i < l; i++) {
					   // console.log('tooltip数据值',params[i].value)
					   //遍历出来的值一般是字符串，需要转换成数字，再进项tiFixed四舍五入
						relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + Number(params[i].value).toFixed(2)
					}
					return relVal;
				},
			},
			grid: {
				left: 0,
				right: 25,
				bottom: 40,
				containLabel: true,
				top:20
			},
			color:['#F7B500','#6DD400','#E02020'],
			// legend: {
			// 	data: ['标准坪效', '节点年累计平均坪效'],
			// 	textStyle: {
			// 		  color: '#2A2B40',
			// 		  fontSize: '12px',
			// 	},
			// 	icon: 'rect', 	
			// 	itemWidth: 17,
			// 	itemHeight: 3,
			// 	top:10,
			// 	right:10
		 //    },
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
				              color: "#2A2B40", //x轴字体颜色
				            },
				            // interval: 10,
				          },
				          axisTick: {
				            //x轴刻度相关设置
				            alignWithLabel: true,
				          },
				// data: ['01','02','03','04','05','06','07',
				// '08','09','10','11','12','13','14','15']
				data:ts
			},
			yAxis: {
				type: 'value',
				axisLabel : {
					formatter: '{value}',
					textStyle: {
						color: '#2A2B40'
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
		  series: [
		    {
				name: '标准坪效',
				type: 'line',
				// stack: 'Total',
				// data: [106,	106,106,106,106,106,106,106,106,106,106,106,106,106,106],
				data:standardSalesPerSquareMeter,
				smooth: true,
				showSymbol: false,
				itemStyle : {  
					normal : {  
						lineStyle:{  
						 color:'#F7B500'  
						}  
					}  
				},  
		    },
		    {
				name: '节点年累计平均坪效',
				type: 'line',
				// stack: 'Total',
				// data: [167,165,162,161,158,154,151,148,148,150,151,151,149,146],
				data:annualAccumulationSalesPerSquareMeter,
				smooth: true,
				showSymbol: false,
				itemStyle : {
					normal : {  
						lineStyle:{  
						 color:'#6DD400'  
						}  
					}  
				},  
		    },
		   
		  ]
		};
		
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
	}
	// 获取省份
	// /system_management/sysregion_model/regionProvinces
	const regionProvinces =() =>{
		http.post('system_management/sysregion_model/regionProvinces').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let options = []
				data&&data.map(res =>{
					options.push({
						label:res.regionName,
						value:res.regionName
					})
				})
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 选择城市
	const cityChange =(val) =>{
		console.log(val)
		
		if(val.length==2){
			setCityVal(val[1])
			setMapZoom(11)
			containers()
			
		}else if(val.length==1){
			if(val[0]=='全国'){
				setCityVal(val[0])
				setMapZoom(5)
				containers()
				
			}else{
				setCityVal(val[0])
				setMapZoom(11)
				containers()
				
			}
			
		}
	}
	
	const containers =() =>{
		var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
		// let markerArr = setMarkerArr
		// var map = new window.BMap.Map('container',{enableMapClick: false});
		const map = new window.BMap.Map(mapRef.current,{enableMapClick: false});
		var mapContainer = document.getElementById('container');
		
		map.enableDragging(); // 启用地图拖拽功能
		map.enablePinchToZoom();
		var opts = {
			// offset: new window.BMap.Size(1200, 130),
			// anchor: 'BMAP_ANCHOR_TOP_RIGHT'
		}
		var navigationControl = new window.BMap.NavigationControl(opts);
		map.addControl(navigationControl); // 添加导航控件到地图
		
		// 地图样式
		map.setMapStyle({styleJson: myStyleJson }); 
		map.getContainer().style.backgroundColor = '#00364b'
		// 监听手指事件
		var startX, startY;
		var startPoint1, startPoint2; // 
		var startDistance; // 
		var prevDistance; // 
		var startTouch = null;
		var endTouch = null;
		document.getElementById("container").addEventListener('touchstart', function(e){
			console.log(e)
			e.preventDefault(); // 禁止默认事件
		    startX = e.touches[0].clientX;
		    startY = e.touches[0].clientY;
			
			if (e.target.className == 'BMap_button BMap_stdMpZoomOut') {
				// 缩小
			    var zoomLevel = map.getZoom(); // 获取当前缩放级别
			    map.setZoom(zoomLevel -1); // 
			}
			if (e.target.className == 'BMap_button BMap_stdMpZoomIn') {
				// 放大
			    var zoomLevel = map.getZoom(); // 获取当前缩放级别
			    map.setZoom(zoomLevel +1); // 
			}
			if(e.touches.length >= 2) {
			    startDistance = Math.sqrt(Math.pow(e.touches[0].clientX - e.touches[1].clientX, 2) + Math.pow(e.touches[0].clientY - e.touches[1].clientY, 2));
			}
			if(e.target.className =='BMap_Marker BMap_noprint'){
				
			}else{
				map.closeInfoWindow();
			}
		}, { passive: false });
		
		document.getElementById("container").addEventListener('touchmove', function touchmoveHandler1(e){
			e.preventDefault(); // 禁止默认事件
			console.log(e)
			if(e.touches.length==1){
				// console.log(startX,startY)
				var deltaX = e.touches[0].clientX - startX;
				var deltaY = e.touches[0].clientY - startY;
				// console.log(deltaX,deltaY)
				map.panBy(deltaX, deltaY);
			}
			if (e.touches.length == 2) {
				var currentDistance = Math.sqrt(Math.pow(e.touches[0].clientX - e.touches[1].clientX, 2) + Math.pow(e.touches[0].clientY - e.touches[1].clientY, 2));
				        
					if(currentDistance > startDistance){
						console.log("Zoom in");
						var zoomLevel = map.getZoom(); // 
							map.setZoom(zoomLevel + 1); // 
					} else {
						// 手指向内移动，表示缩小
						console.log("Zoom out");
						var zoomLevel = map.getZoom(); // 
									map.setZoom(zoomLevel - 1); // 
					}
		
				document.getElementById("container").removeEventListener("touchmove", touchmoveHandler1);
				setTimeout(function(){
				    document.getElementById("container").addEventListener("touchmove", touchmoveHandler1);
				}, 500);
			}
			
			
		});
		let touchmoveHandler = function(e){
		    document.getElementById("container").removeEventListener("touchmove", touchmoveHandler);
		};
		document.getElementById("container").addEventListener('touchend', function(e){
			console.log(e)
			e.preventDefault(); // 禁止默认事件
			startDistance = null;
			prevDistance = null;
			// touchmoveHandler(e);
		});
		document.getElementById("container").addEventListener('click', function(e){
			// console.log(e)
			// alert(0)
		})
		
		
		document.getElementById("container").style.height = "100%";
		window.addEventListener("resize", () => {
			if(divRef.current){
			  const divHeight = divRef.current.clientHeight;
			  const totalHeight = divRef.current.scrollHeight;
			  document.getElementById("plantright").style.height = divRef.current.scrollHeight +'px';
			}
		});
		// PC端只显示中国地图
		var point = new window.BMap.Point(104.072639, 35.705145);// 120.73717 经度  //31.25795 纬度温州市
		var pStart = new window.BMap.Point(180,90);
		var pEnd = new window.BMap.Point(0,-90);
		var pArray = [
			new window.BMap.Point(pStart.lng,pStart.lat),
			new window.BMap.Point(pEnd.lng,pStart.lat),
			new window.BMap.Point(pEnd.lng,pEnd.lat),
			new window.BMap.Point(pStart.lng,pEnd.lat)
		];
		var maskPath = [
		    
		];
		let chinaPly = chinaMaps.chinaPly
		chinaPly.map((index,value) =>{
			pArray.push(new window.BMap.Point(index[0],index[1]));
			
		})
		
		var plyall = new window.BMap.Polygon(pArray,
		   { 
			   strokeOpacity: 1, strokeColor: "#233347",
			   strokeWeight: 1, fillColor: "#233347",fillOpacity: 1 }); //建立多边形覆盖物
					
		pStart = new window.BMap.Point(180,90);
		pEnd = new window.BMap.Point(0,-90);
		pArray = [
		   new window.BMap.Point(135.077218,48.454352),
		   new window.BMap.Point(pStart.lng,pStart.lat),
		   new window.BMap.Point(pStart.lng,pEnd.lat),
		   new window.BMap.Point(135.077218,48.454352)];
		var boundary = new window.BMap.Polygon(pArray,
		   { strokeOpacity: 1, strokeColor: "#233347",
			   strokeWeight: 1, fillColor: "#233347",fillOpacity: 1}); //建立多边形覆盖物
		if (isMobile) {
		    // 是移动端设备
			
		} else {
		    // 是PC端设备
			map.addOverlay(plyall);
			map.addOverlay(boundary);
			
		}
		map.centerAndZoom(cityVal, mapZoom);
		map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
	
		var operateIcon = new window.BMap.Icon(operate, new window.BMap.Size(40, 40));
		var constructIcon = new window.BMap.Icon(construct, new window.BMap.Size(40, 40));
		var closeIcon = new window.BMap.Icon(close, new window.BMap.Size(40, 40));
		var planIcon = new window.BMap.Icon(plan, new window.BMap.Size(40, 40));
		var markers = [];
		let isSelect = null
		console.log(markerArr,'markerArrmarkerArr')
		for(var i = 0; i < markerArr.length; i++) {
			if(markerArr[i]){
				var p0 = markerArr[i].longitude;
				var p1 = markerArr[i].latitude;
				var myIcon;
				if( markerArr[i].stationCategory=='项目'){
					myIcon = markerArr[i].stationState == '运营中' ? operateIcon :
					markerArr[i].stationState == '建设中' ? constructIcon:
					markerArr[i].stationState == '规划中' ? planIcon:closeIcon;
				}else{
					myIcon = markerArr[i].stationState == '运营中' ? operateIcon :
					markerArr[i].stationState == '建设中' ? constructIcon:
					markerArr[i].stationState == '规划中' ? planIcon:closeIcon;
				}
				
				var pt = new window.BMap.Point(p0, p1);
				var marker = new window.BMap.Marker(pt, { icon: myIcon });
				
				var offset = new window.BMap.Size(0, -20);
				marker.setOffset(offset);
				
				// 保存原始图标
				// operateactive constructactive
				marker.originalIcon = myIcon;
				marker.selectedIcon = new window.BMap.Icon(markerArr[i].stationState === '运营中' ? operateactive :
					markerArr[i].stationState === '建设中' ? constructactive:
					markerArr[i].stationState === '规划中' ? planactive:closeactive, new window.BMap.Size(40, 40), 
				
				
				
				{
				anchor: new window.BMap.Size(26, 22) // 锚点位置
				}); // 新图标的URL
				map.addOverlay(marker);
				markers.push(marker);
				marker.addEventListener('click', function(e) {
					// 恢复所有marker的原始图标
					markers.forEach(function(m) {
						// console.log(m)
						m.setIcon(m.originalIcon);
					});
					// 设置当前点击的marker为新图标
					this.setIcon(this.selectedIcon);
					var p0 = e.target.point.lng;
					var p1 = e.target.point.lat;
					var point = new window.BMap.Point(p0, p1);
					var str = ""
					str+="<div class='soliddiv'>";
					for(var i = 0; i < markerArr.length; i++) {
						if(markerArr[i]){
							if(markerArr[i].latitude==p1&&markerArr[i].longitude== p0){
								if(markerArr[i].nodePostType=='load'){
									// 负荷
									let originalString = markerArr[i].content
									if(originalString.includes('/')){
										var splitString = originalString.split('/');
										var key = splitString[0]; // 
										var value = splitString[1]; // 
										if(markerArr[i].stationState=='运营中'){
											// 运营中
											str += "<h4 class='loadh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='建设中'){
											str += "<h4 class='constructload'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='规划中'){
											str += "<h4 class='planload'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='已关闭'){
											str += "<h4 class='constructloadclose'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}
									}else{
					
										if(markerArr[i].stationState=='运营中'){
											// 运营中
											str += "<h4 class='loadh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='建设中'){
											str += "<h4 class='constructload'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='规划中'){
											str += "<h4 class='planload'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='已关闭'){
											str += "<h4 class='constructloadclose'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}
									}
								}else if(markerArr[i].nodePostType=='pv'){
									// 光伏
									
									let originalString = markerArr[i].content
									if(originalString.includes('/')){
										var splitString = originalString.split('/');
										var key = splitString[0]; // 
										var value = splitString[1]; // 
										if(markerArr[i].stationState=='运营中'){
											str += "<h4 class='pvh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='建设中'){
											str += "<h4 class='npvh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='规划中'){
											str += "<h4 class='planpvh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='已关闭'){
											str += "<h4 class='closepvh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}
									}else{
										if(markerArr[i].stationState=='运营中'){
											str += "<h4 class='pvh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='建设中'){
											str += "<h4 class='npvh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='规划中'){
											str += "<h4 class='planpvh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='已关闭'){
											str += "<h4 class='closepvh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}
									}
									
								}else if(markerArr[i].nodePostType=='storageEnergy'){
									// 储能
									let originalString = markerArr[i].content
									if(originalString.includes('/')){
										var splitString = originalString.split('/');
										var key = splitString[0]; // 
										var value = splitString[1]; // 
										if(markerArr[i].stationState=='运营中'){
											str += "<h4 class='storageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='建设中'){
											str += "<h4 class='nstorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='规划中'){
											str += "<h4 class='planstorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='已关闭'){
											str += "<h4 class='closestorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}
									}else{
										// if(markerArr[i].isEnabled==true){
										// 	str += "<h4 class='storageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
										// 			"<p class='markerp'>"+markerArr[i].content +'</p>';
										// }else{
										// 	str += "<h4 class='nstorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
										// 			"<p class='markerp'>"+markerArr[i].content +'</p>';
										// }
										if(markerArr[i].stationState=='运营中'){
											str += "<h4 class='storageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='建设中'){
											str += "<h4 class='nstorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='规划中'){
											str += "<h4 class='planstorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='已关闭'){
											str += "<h4 class='closestorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}
									}
									
								}else{
									let originalString = markerArr[i].content
									if(originalString.includes('/')){
										var splitString = originalString.split('/');
										var key = splitString[0]; // 
										var value = splitString[1]; // 
										if(markerArr[i].stationState=='运营中'){
											str += "<h4 class='storageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='建设中'){
											str += "<h4 class='nstorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='规划中'){
											str += "<h4 class='planstorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}else if(markerArr[i].stationState=='已关闭'){
											str += "<h4 class='closestorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+key +'</p>'+
													"<p class='markerp'>"+value +'</p>';
										}
									}else{
										if(markerArr[i].stationState=='运营中'){
											str += "<h4 class='storageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='建设中'){
											str += "<h4 class='nstorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='规划中'){
											str += "<h4 class='planstorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}else if(markerArr[i].stationState=='已关闭'){
											str += "<h4 class='closestorageEnergyh4'>" + markerArr[i].nodeName + "</h4>"+
													"<p class='markerp'>"+markerArr[i].content +'</p>';
										}
									}
									
								}
								
							}
						}
					}
					str += "</div>";
					var opts = {
						width: 200,
						// height: 300,
					};
					var infoWindow = new window.BMap.InfoWindow(str, opts); 
					map.openInfoWindow(infoWindow, point);
			
				});
				
				
			}
			
		}	
		
		map.addEventListener('click', function(e) {
		    // 检查点击是否发生在marker上
		    if (!e.overlay) {
		        // 恢复所有marker的原始图标
		        markers.forEach(function(m) {
		            m.setIcon(m.originalIcon);
		        });
		    }
		});
		
	   // 恢复Marker图标
	   const resetMarkerIcon = () => {
		 if (activeMarker) {
		   activeMarker.setIcon(new window.BMap.Icon('http://api.map.baidu.com/images/marker_red.png', new window.BMap.Size(20, 34)));
		   setActiveMarker(null);
		 }
	   };
	   
		//attribute事件
		window.addEventListener("scroll", function() {
			map.resize(); // 在百度地图的 map 对象上调用 resize 方法
		});	
		
		setTimeout(function(){
			// map.panBy(x, y);
			var size = map.getSize();
			var width = size.width;
			var height = size.height;
			console.log(width,height,'----------------')
			var offsetX = 290;
			var offsetY = -height / 2 + 300; 
			map.panBy(offsetX, offsetY);
		}, 500);
		setIsFirstRender(false)
	
	}
	
	// 选择节点
	const nodeChange =(val) =>{
		console.log(val)
		
		setNodeId(val)
	}
	// 基贤负荷日期选择
	const dateChange1 =(date,mode) =>{
		console.log(date,mode)
		setStartTs(mode[0])
		setEndTs(mode[1])
		setPanier(getDay(mode[0],mode[1]))
		
	}
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
	// 
	const onStation =(e,val) =>{
		console.log(e,val)
		setIndex(e)
		getSalesPerSquareMeterList(val)
	}
	const line =(baselineLoadValue,realValue,timeStamp,isEmpty) =>{
		var myChart = echarts.init(document.getElementById('main'));
	
		myChart.showLoading({
			text: '数据加载中...',
			color: '#FFF',
			textColor: '#ffffc2',
			maskColor: 'rgba(255, 255, 255, 0)',
			zlevel: 0
		});
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		myChart.setOption({
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
						invisible:isEmpty,
						silent: true
					},
					{
					    type: "text",
					    left: "center", // 相对父元素居中
					    top: "160", // 相对父元素上下的位置
						z: 100,
					    style: {
							fill: '#FFF',
							fontSize: 12,
					        text: "暂无数据",
					    },
						invisible:isEmpty,
						silent: true
					},

				]
			},
			title: {
			    text: '单位：kW',
				textStyle:{
					color:'#FFF',
					fontSize:12,
					fontWeight:'normal'
				},
			},
			color:['#F05887','#44D7B6'],
			 //  tooltip: {
			 //    trigger: 'axis',
				// axisPointer: {
				// 	type: 'cross',
				// 	  // crossStyle: {
				// 	  //   color: '#999'
				// 	  // }
				// }
				
			 //  },
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
				
				textStyle: {
				        color: '#FFF',
						fontSize:12
				    },
				itemHeight: 6,
				itemWidth: 6,// 添加
				left:100
				// top:10
			},
			grid: {
			    left: '3%',
			    right: 20,
				top:40,
			    bottom: 20,
			    containLabel: true
			},
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
			      data:realValue
			    }
			]
		})
		
		myChart.hideLoading()
		
		myChart.on('click', function (param) {
			console.log(param, param.data);//这里根据param填写你的跳转逻辑
		   
		});
	}
	// 基线负荷搜索
	const respond =() =>{
		console.log(nodeId)
		// alert(nodeId)
		// let {nodeId,endTs,startTs} = this.state
		if(endTs==''){
			message.info('请选择时间')
		}else if(nodeId==''){
			message.info('请选择节点')
		}else{
			var myChart = echarts.init(document.getElementById('main'));
			// var chartDom = document.getElementById('line');
			// var myChart = echarts.init(chartDom);
			myChart.showLoading({
				text: '数据加载中...',
				color: '#FFF',
				textColor: '#FFF',
				maskColor: 'rgba(255, 255, 255, 0)',
				zlevel: 0
			});
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
						line(baselineLoadValue,realValue,timeStamp,true)
						
						
					}else{
						setBaselineLoadValue([])
						setRealValue([])
						setTimeStamp([])
						setIsEmpty(false)
						// line()
						line([],[],[],false)
					}
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
	}	
	

		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current < dayjs(new Date('2023-01-01')) || current > dayjs().endOf('day')
		};
		const dateFormat = 'YYYY-MM-DD'; // 
		return(
			<div className="generating"  id="generating" >
			<div id="container1" style={{width:500,height:500}}></div>
				<div className="plantright" id="plantright" ref={divRef}  style={{ width: '100%', position: 'absolute', top: 0, left: 0,bottom:0}}>
					<Spin className="spinningload" spinning={tableLoading}>
						
					</Spin>
					<div id="container" ref={mapRef}></div>
				
					<div 
						className={baseUlrRef==1 ? 'xuheader' : 'inactive-class'}
					>
						
						<div className="headers1">
							<div>
								<h4><img src={require('../../style/load/icon1.png')} />日前响应(kWh)</h4>
								<p>{day}</p>
							</div>
							<div>
								<h4><img src={require('../../style/load/icon2.png')} />小时响应(kWh)</h4>
								<p>{hour}</p>
							</div>
							<div>
								<h4><img src={require('../../style/load/icon3.png')} />分钟响应(kWh)</h4>
								<p>{minute}</p>
							</div>
							<div>
								<h4><img src={require('../../style/load/icon4.png')} />秒级响应(kWh)</h4>
								<p>{seconds}</p>
							</div>
							<div>
								<h4><img src={require('../../style/damao/icon4.png')} />智能调度(kW)</h4>
								<p>{intelligentScheduling}</p>
							</div>
							<div>
								<h4><img src={require('../../style/damao/icon5.png')} />碳减排量(t)</h4>
								<p>{carbonEmissionReduction}</p>
							</div>
							<div>
								<h4><img src={require('../../style/damao/icon6.png')} />顶峰能力(kW)</h4>
								<p>{peakCapacity}</p>
							</div>
						</div>
						
					</div>
					<div className="wisdomen" 
						style={{display:baseUlrRef==1?'block':'none'}}
					onClick={wisdom}>智慧平台  ></div>
					
					
					
					
					<div className="constructbuild">
						<ul>
							<li>规划中</li>
							<li>建设中</li>
							<li>运营中</li>
							<li>已关闭</li>
						</ul>
					</div>
					<div className="plantleft">
						<div className="weights">
							<div className="weightsload">
								<img style={{marginLeft:8}} src={require('../../style/load/load.png')} />
								<ul>
									<li style={{marginTop: 50}}>
										<span style={{marginLeft:14}}>{loadCount}</span>
										<p>负荷节点数量</p>
									</li>
									<li>
										<span className="bcomments">{loadKeTiao}</span>
										<p>可调负荷(kW)</p>
										<span className="bcomments">{loadJieRu}</span>
										<p>接入负荷(kW)</p>
									</li>
								</ul>
							</div>
							<div className="deviceplant">
									<h4>设备调节能力</h4>
									<ul>
										<li>
											<img src={require('../../style/load/icon5.png')} />
											<div>
												<span>{airdeviceValue}</span>
												<p>空调（kW）</p>
											</div>
										</li>
										<li>
											<img src={require('../../style/load/icon6.png')} />
											<div>
												<span>{ChargingdeviceValue}</span>
												<p>充电桩(kW)</p>
											</div>
										</li>
										<li>
											<img src={require('../../style/load/icon7.png')} />
											<div>
												<span>{lightdeviceValue}</span>
												<p>照明(kW)</p>
											</div>
										</li>
										<li>
											<img src={require('../../style/load/icon8.png')} />
											<div>
												<span>{otherdeviceValue}</span>
												<p>其他(kW)</p>
											</div>
										</li>
									</ul>
							</div>
						</div>
						<div className="deviceplantday">
							<h4>今日用能统计</h4>
							<ul>
								
								<li>
									<img src={yong} />
									<div>
										<span>{energyConsumption}</span>
										<p>用电(万/kWh)</p>
									</div>
									
								</li>
								<li>
									<img src={tan} />
									<div>
										<span>{carbonEmission}</span>
										<p>碳排放(t)</p>
									</div>
									
								</li>
							</ul>
						</div>
					</div>
					
						
						
					
					<div className="cityname">
						<Cascader style={{color:'#FFFFFF',width:156}} value={cityVal} options={newdata.provinces} onChange={cityChange} placeholder="温州市" />
					</div>

				
					<div className="station">
						<div className="stationtone1 stationtwo">
							<h4>用电月同比分析</h4>
							<ul>
								<li>
									<span>当月用电</span>
									<i>kWh</i>
									<b>{theSameMonthEnergy}万</b>
								</li>
								<li>
									<span>上月同期</span>
									
									
									<i>kWh</i>
									<b>{theLastMonthEnergy}万</b>
								</li>
								<li>
									<span>同期对比</span>
									
									<i>%</i>
									<b style={{width:'auto'}}>{m2mEnergy}</b>
								</li>
							</ul>
						</div>
						<div className="stationtone1">
							<h4>基线负荷分析
							<Button type="primary" onClick={respond} style={{marginLeft:16,float:'right'}}>查询</Button>
							<div className="datumtitle">
								<b>节点：</b>
								<Select style={{width:166}}
									onChange={nodeChange}
									size={'small'}
									value={nodeId}
								>
									{
										nodeList&&nodeList.map(res =>{
											return <Option key={res.id} value={res.id}>{res.nodeName}</Option>
										})
									}
								</Select>
								<b>日期：</b><RangePicker  
								value={startTs!=''&&endTs!=''? [dayjs(startTs, dateFormat), dayjs(endTs, dateFormat)] : undefined}
								disabledDate={disabledDate} onChange={dateChange1} 
								style={{float:'inherit',width:240}} />
								
							</div>
							
							</h4>
							<div>
								<div id="main" ></div>
							</div>
							
						</div>
					</div>
				</div>
			</div>
		)
	}


export default Loadgenerating
// <Spin spinning={this.state.tableLoading} tip="加载中"  size="large">
// 						<div className="plantright" id="container"></div>
// 					</Spin>
// <div className="wisdomen" onClick={wisdom}>智慧平台  ></div>