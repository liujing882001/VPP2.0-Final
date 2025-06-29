import React,{useEffect,useState} from 'react'
import { Select ,Table ,DatePicker,ConfigProvider,Button,message,Cascader,Spin  } from 'antd';
import {
  QuestionCircleOutlined
} from '@ant-design/icons';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import '../power/index.css'
import http from '../../server/server.js'
import * as echarts from 'echarts';
// import echarts  from '../echarts.js'
import axios from 'axios'

const { Option } = Select;
const { RangePicker } = DatePicker;

// class power extends React.Component {
const Power = () =>{
	const [date, setDate] = useState('');
	const [nodeId, setNodeId] = useState('');
	const [profitValue, setProfitValue] = useState('-');
	const [consumerProfitValue, setConsumerProfitValue] = useState('');
	const [inMeterDeviceName, setInMeterDeviceName] = useState('-');
	const [inMeterDeviceNum, setInMeterDeviceNum] = useState('-');
	const [outMeterDeviceName, setOutMeterDeviceName] = useState('-');
	const [outMeterDeviceNum, setOutMeterDeviceNum] = useState('-');
	const [priceHigh, setPriceHigh] = useState('-');
	const [pricePeak, setPricePeak] = useState('-');
	const [priceStable, setPriceStable] = useState('-');
	const [priceLow, setPriceLow] = useState('-');
	const [inElectricityHigh, setInElectricityHigh] = useState('-');
	const [inElectricityPeak, setInElectricityPeak] = useState('-');
	const [inElectricityStable, setInElectricityStable] = useState('-');
	const [inElectricityLow, setInElectricityLow] = useState('-');
	const [outElectricityHigh, setOutElectricityHigh] = useState('-');
	const [outElectricityPeak, setOutElectricityPeak] = useState('-');
	const [outElectricityStable, setOutElectricityStable] = useState('-');
	const [outElectricityLow, setOutElectricityLow] = useState('-');
	const [inElectricityHighPrice, setInElectricityHighPrice] = useState('-');
	const [inElectricityPeakPrice, setInElectricityPeakPrice] = useState('-');
	const [inElectricityStablePrice, setInElectricityStablePrice] = useState('-');
	const [inElectricityLowPrice, setInElectricityLowPrice] = useState('-');
	const [outElectricityHighPrice, setOutElectricityHighPrice] = useState('-');
	const [outElectricityPeakPrice, setOutElectricityPeakPrice] = useState('-');
	const [outElectricityStablePrice, setOutElectricityStablePrice] = useState('-');
	const [outElectricityLowPrice, setOutElectricityLowPrice] = useState('-');
	const [electricityHigh, setElectricityHigh] = useState('-');
	const [electricityHighPrice, setElectricityHighPrice] = useState('-');
	const [electricityLow, setElectricityLow] = useState('-');
	const [electricityLowPrice, setElectricityLowPrice] = useState('-');
	const [electricityPeak, setElectricityPeak] = useState('-');
	const [electricityPeakPrice, setElectricityPeakPrice] = useState('-');
	const [electricityStable, setElectricityStable] = useState('-');
	const [electricityStablePrice, setElectricityStablePrice] = useState('-');
	const [loadProfitValue, setLoadProfitValue] = useState('-');
	const [meterDeviceName, setMeterDeviceName] = useState('-');
	const [meterDeviceNum, setMeterDeviceNum] = useState('-');
	const [nodeNameList, setNodeNameList] = useState([]);
	const [billNodeType, setBillNodeType] = useState('');
	const [options, setOptions] = useState([]);
	const [tableLoading, setTableLoading] = useState(false);
	const [current, setCurrent] = useState(3);
	const [dateType, setDateType] = useState(3);
	const [powerLoading, setPowerLoading] = useState(false);
	const [loading, setLoading] = useState(false);
	const [dataSource, setDataSource] = useState([]);
	const [chartList, setChartList] = useState([]);
	const [chartdate, setChartDate] = useState([]);
	const [dateVal, setDateVal] = useState((dayjs().subtract(1, 'days')).format('YYYY-MM-DD'));
	const [pIndex, setPIndex] = useState('');
	const [isActive, setIsActive] = useState(false);
	const [isActive1, setIsActive1] = useState(false);
	const [isActive2, setIsActive2] = useState(false);
	const [isActive3, setIsActive3] = useState(false);
	const [list, setList] = useState(['尖', '峰', '平', '谷']);
	const [chartData, setChartData] = useState([]);
	const [content, setContent] = useState([]);
	const [pv, setPv] = useState('');
	const [monthcharge, setMonthcharge] = useState('本日电量');
	const [dayCharge, setDayCharge] = useState('日充电电费');
	const [dayFcharge, setDayFcharge] = useState('日放电电费');
	const [socList, setSocList] = useState([]);
	const [outCapacityList, setOutCapacityList] = useState([]);
	const [isEmpty, setIsEmpty] = useState(true);
	const [isFirst, setIsFirst] = useState(false);
	const [powerTrendTitle, setPowerTrendTitle] = useState('储能电量及变化趋势');
	const [value,setValue] = useState('');
	const [isLoad,setIsLoad] = useState(true);
	const [newvalue,setNewvalue] = useState('');
	const [firstPv,setFirstPv] = useState('')
	const [chargingStation,setChargingStation] = useState('')
	useEffect(() =>{
		storageEnergyNodeNameList()
		charts()
	},[]);
	useEffect(() =>{
		if(billNodeType){
			if(isLoad){
				billNodeTree()
			}
			
		}
	},[isFirst]);

	useEffect(() =>{
		charts()
	},[chartdate,chartList,chartData,isEmpty,socList]);
	useEffect(() =>{
		if(isLoad){
			if(billNodeType=='storageEnergy'){
				// alert(0)
				if(dateType==3){
					electricityBillStorageEnergy()
					findStorageEnergyStrategy()
				}
			}
		}
		
	},[nodeId]);
	useEffect(() =>{
		if(firstPv!==''){
			billNodeTree()
			// alert(0)
		}
		
	},[firstPv])
	// 节点
	const storageEnergyNodeNameList =() =>{
		http.post('electricity_bill_management/electricity_bill/billNodeTypeList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				let billNodeType = ''
				data&&data.map(res =>{
					if(res.nodeTypeName=='储能'){
						billNodeType = res.nodeTypeKey
					}
				})
				
				setNodeNameList(res.data.data);
				setBillNodeType(billNodeType);
				setPv(billNodeType);
				setIsFirst(true);
				// billNodeType&&billNodeTree()
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 选择类型
	const handleChange =(e) =>{
		console.log(e,'----')
		setBillNodeType(e);
		setValue('');
		setNodeId('');
		setChartList([]);
		setSocList([]);
		setIsActive(false);
		setIsActive1(false);
		setIsActive2(false);
		setIsActive3(false);
		setList(['尖','峰','平','谷']);
		setIsFirst(false);
		setPv(e);
		setFirstPv(e)
		charts()
	}
	// 选择节点
	const treeChange =(val) =>{
		console.log(val)
		let length = val.length
		setNodeId(val[length-1]);
		setValue(val)
	}
	// 选择时间
	const onChangeYear =(e,dataString) =>{
		console.log(dataString)
		setDateVal(dataString)
	}
	// electricity_bill_management/electricity_bill/billNodeTree
	// 节点
	const billNodeTree=() =>{
		setPowerLoading(true);
		http.post('electricity_bill_management/electricity_bill/runBillNodeTree?billNodeType='+billNodeType).then(res =>{
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
				let array1 = []
				function traverseFirstElement(arr) {
					const firstElement = arr[0];
					console.log(firstElement);
					if(firstElement){
						array1.push(firstElement.key)
					}
					
					for (const key in firstElement) {
						if (Array.isArray(firstElement[key])) {
							// console.log(firstElement[key])
							traverseFirstElement(firstElement[key]);
						}
					}
				}
				if(data.length>0){
					traverseFirstElement(data);
				}
				
				console.log(array1)
				console.log(billNodeType)
				// alert(isFirst)
				let length = array1.length

				setValue(array1);
				setNodeId(length>0&&array1[length-1]);
					// alert(array1[length-1])

				
				setOptions(data);
				setPowerLoading(false);
				setIsLoad(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 光伏尖峰平谷
	const findPvTimeDivision=() =>{
		http.post('system_management/energy_model/photovoltaic_model/findPvTimeDivision',{
			"nodeId":nodeId,
			"number":1,
			"pageSize": 100,
			"systemId": 'nengyuanzongbiao',
			"effectiveDate": dateVal
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				let dataSource = []
				let chartdate = []
				let chartList = []
				let chartData = []
				content.map(res =>{
					
					chartdate.push(res.stime+'-'+res.etime)
					chartList.push(
					{
						value:res.priceHour,
						itemStyle: {
							color: res.property=="尖"?'#186AC3':res.property=="峰"?'#2C98FB':
							res.property=="平"?'#86BAF6':res.property=="谷"?'#EEF0FD':''
						},
						name:res.property,
						// name: region,
						  // type: 'bar',
						  // barMaxWidth: 30,
						  // barGap: '-100%',
					})
					chartData.push(
					{
						value:res.priceHour,
						itemStyle: {
							color: res.property=="尖"?'#186AC3':res.property=="峰"?'#2C98FB':
							res.property=="平"?'#86BAF6':res.property=="谷"?'#EEF0FD':''
						},
						name:res.property
					})
					
				})
				

				console.log(dataSource)
				console.log(chartdate)
				console.log(chartList)
				
				setLoading(false);
				setChartDate(chartdate);
				setChartList(chartList);
				setChartData(chartData);
				setIsEmpty(false);
				// charts()
			}else{
				setLoading(false);
				setIsEmpty(true);
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 光伏
	const electricityBillPv=() =>{
		setTableLoading(true)
		http.post('electricity_bill_management/electricity_bill/electricityBillPv',{
			"date": dateType==1?dateVal+'-12-31':dateType==2?dateVal+'-01':dateVal,
			"nodeId": nodeId,
			"type":dateType==1?"YEAR":dateType==2?"MONTH":'DAY'
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let profitValue = Number(data.profitValue).toFixed(2)
				let loadProfitValue = Number(data.loadProfitValue).toFixed(2)
				let consumerProfitValue = Number(data.consumerProfitValue).toFixed(2)
				console.log(loadProfitValue,'-----',consumerProfitValue)
				let num = Number(loadProfitValue) + Number(consumerProfitValue)
				console.log(num)
				var result ;
				if (num > 0) {
				  result = Math.round(num * 1000) / 1000; // 正数保留两位小数
				  console.log(result.toFixed(2))
				  var number = result;
				  var decimal = number.toString();
				  var decimalPart = decimal.substring(decimal.indexOf('.') + 1); // 截取小数点后面的部分，结果为"14159"
				  console.log(decimalPart)
				  if(decimalPart.length==1){
				  	result  = Number(result).toFixed(2)
				  }
				} else if(num==0){
					result = Number(num).toFixed(2)
				} else if(num<0) {
				  result = Math.ceil(num * 100) / 100; // 负数保留两位小数
					var number = result;
					var decimal = number.toString();
					var decimalPart = decimal.substring(decimal.indexOf('.') + 1); // 截取小数点后面的部分，结果为"14159"
					if(decimalPart.length==1){
						result  = Number(result).toFixed(2)
					}
				} else{
					result = '-'
				}
				setConsumerProfitValue(consumerProfitValue);
				setElectricityHigh(Number(data.electricityHigh).toFixed(2));
				setElectricityHighPrice(Number(data.electricityHighPrice).toFixed(2));
				setElectricityLow(Number(data.electricityLow).toFixed(2));
				setElectricityLowPrice(Number(data.electricityLowPrice).toFixed(2));
				setElectricityPeak(Number(data.electricityPeak).toFixed(2));
				setElectricityPeakPrice(Number(data.electricityPeakPrice).toFixed(2));
				setElectricityStable(Number(data.electricityStable).toFixed(2));
				setElectricityStablePrice(Number(data.electricityStablePrice).toFixed(2));
				setLoadProfitValue(loadProfitValue);
				setMeterDeviceName(data.meterDeviceName);
				setMeterDeviceNum(data.meterDeviceNum);
				setPriceHigh(data.priceHigh);
				setPricePeak(data.pricePeak);
				setPriceStable(data.priceStable);
				setPriceLow(data.priceLow);
				setTableLoading(false);
				setProfitValue(result)
			}else{
				
				setTableLoading(false);
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 储能
	const electricityBillStorageEnergy=() =>{
		setTableLoading(true)
		http.post('electricity_bill_management/electricity_bill/electricityBillStorageEnergy',{
			"date": dateType==1?dateVal+'-01-01':dateType==2?dateVal+'-01':dateVal,
			"nodeId": nodeId,
			"type":dateType==1?"YEAR":dateType==2?"MONTH":'DAY'
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let profitValue = Number(data.profitValue).toFixed(2)
				let loadProfitValue = Number(data.loadProfitValue).toFixed(2)
				let consumerProfitValue = Number(data.consumerProfitValue).toFixed(2)
				// console.log(loadProfitValue,'-----',consumerProfitValue)
				let num = Number(loadProfitValue) + Number(consumerProfitValue)
				console.log(num)
				var result ;
				if (num > 0) {
				  result = Math.round(num * 1000) / 1000; // 正数保留两位小数
				  console.log(result.toFixed(2))
				  var number = result;
				  var decimal = number.toString();
				  var decimalPart = decimal.substring(decimal.indexOf('.') + 1); // 截取小数点后面的部分，结果为"14159"
				  console.log(decimalPart)
				  if(decimalPart.length==1){
				  	result  = Number(result).toFixed(2)
				  }
				} else if(num==0){
					result = Number(num).toFixed(2)
				} else if(num<0) {
				  result = Math.ceil(num * 100) / 100; // 负数保留两位小数
				  // if(result)
					var number = result;
					var decimal = number.toString();
					var decimalPart = decimal.substring(decimal.indexOf('.') + 1); // 截取小数点后面的部分，结果为"14159"
					console.log(decimalPart)
					if(decimalPart.length==1){
						result  = Number(result).toFixed(2)
					}
				} else{
					result = '-'
				}
				console.log(result)
				setProfitValue(result);
				setLoadProfitValue(loadProfitValue);
				setConsumerProfitValue(consumerProfitValue);
				setInMeterDeviceName(data.inMeterDeviceName?data.inMeterDeviceName:'-');
				setInMeterDeviceNum(data.inMeterDeviceNum?data.inMeterDeviceNum:'-');
				setOutMeterDeviceName(data.outMeterDeviceName?data.outMeterDeviceName:'-');
				setOutMeterDeviceNum(data.outMeterDeviceNum?data.outMeterDeviceNum:'-');
				setPriceHigh(data.priceHigh);
				setPricePeak(data.pricePeak);
				setPriceStable(data.priceStable);
				setPriceLow(data.priceLow);
				setInElectricityHigh(Number(data.inElectricityHigh).toFixed(2));
				setInElectricityPeak(Number(data.inElectricityPeak).toFixed(2));
				setInElectricityStable(Number(data.inElectricityStable).toFixed(2));
				setInElectricityLow(Number(data.inElectricityLow).toFixed(2));
				setOutElectricityHigh(Number(data.outElectricityHigh).toFixed(2));
				setOutElectricityPeak(Number(data.outElectricityPeak).toFixed(2));
				setOutElectricityStable(Number(data.outElectricityStable).toFixed(2));
				setOutElectricityLow(Number(data.outElectricityLow).toFixed(2));
				setInElectricityHighPrice(Number(data.inElectricityHighPrice).toFixed(2));
				setInElectricityPeakPrice(Number(data.inElectricityPeakPrice).toFixed(2));
				setInElectricityStablePrice(Number(data.inElectricityStablePrice).toFixed(2));
				setInElectricityLowPrice(Number(data.inElectricityLowPrice).toFixed(2));
				setOutElectricityHighPrice(Number(data.outElectricityHighPrice).toFixed(2));
				setOutElectricityPeakPrice(Number(data.outElectricityPeakPrice).toFixed(2));
				setOutElectricityStablePrice(Number(data.outElectricityStablePrice).toFixed(2));
				setOutElectricityLowPrice(Number(data.outElectricityLowPrice).toFixed(2));
				setTableLoading(false)
				
			}else{
				setTableLoading(false);
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}

	// 充电桩
	const electricityBillChargingStation=() =>{
		setTableLoading(true)
		http.post('electricity_bill_management/electricity_bill/electricityBillChargingPile',{
			"date": dateType==1?dateVal+'-12-31':dateType==2?dateVal+'-01':dateVal,
			"nodeId": nodeId,
			"type":dateType==1?"YEAR":dateType==2?"MONTH":'DAY'
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let profitValue = Number(data.profitValue).toFixed(2)
				let loadProfitValue = Number(data.loadProfitValue).toFixed(2)
				let consumerProfitValue = Number(data.consumerProfitValue).toFixed(2)
				console.log(loadProfitValue,'-----',consumerProfitValue)
				let num = Number(loadProfitValue) + Number(consumerProfitValue)
				console.log(num)
				var result ;
				if (num > 0) {
				  result = Math.round(num * 1000) / 1000; // 正数保留两位小数
				  console.log(result.toFixed(2))
				  var number = result;
				  var decimal = number.toString();
				  var decimalPart = decimal.substring(decimal.indexOf('.') + 1); // 截取小数点后面的部分，结果为"14159"
				  console.log(decimalPart)
				  if(decimalPart.length==1){
				  	result  = Number(result).toFixed(2)
				  }
				} else if(num==0){
					result = Number(num).toFixed(2)
				} else if(num<0) {
				  result = Math.ceil(num * 100) / 100; // 负数保留两位小数
				  // if(result)
					var number = result;
					var decimal = number.toString();
					var decimalPart = decimal.substring(decimal.indexOf('.') + 1); // 截取小数点后面的部分，结果为"14159"
					console.log(decimalPart)
					if(decimalPart.length==1){
						result  = Number(result).toFixed(2)
					}
				} else{
					result = '-'
				}
				console.log(result)
				setChargingStation(data.position)
				setConsumerProfitValue(consumerProfitValue);
				setElectricityHigh(Number(data.electricitySharp).toFixed(2));
				setElectricityHighPrice(Number(data.sharpEnergyCharge).toFixed(2));
				setElectricityLow(Number(data.electricityOffPeak).toFixed(2));
				setElectricityLowPrice(Number(data.offPeakEnergyCharge).toFixed(2));
				setElectricityPeak(Number(data.electricityPeak).toFixed(2));
				setElectricityPeakPrice(Number(data.peakEnergyCharge).toFixed(2));
				setElectricityStable(Number(data.electricityShoulder).toFixed(2));
				setElectricityStablePrice(Number(data.shoulderEnergyCharge).toFixed(2));
				setLoadProfitValue(loadProfitValue);
				setMeterDeviceName(data.meterDeviceName);
				setMeterDeviceNum(data.meterDeviceNum);
				setPriceHigh(data.priceHigh);
				setPricePeak(data.pricePeak);
				setPriceStable(data.priceStable);
				setPriceLow(data.priceLow); 
				setTableLoading(false);
				setProfitValue(profitValue);
			}else{
				
				setTableLoading(false);
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const findStorageEnergyStrategy=() =>{
		
		setLoading(true)
		http.post('system_management/energy_model/energy_storage_model/findStorageEnergyStrategy',{
			"nodeId": nodeId,
			"number":1,
			"pageSize": 1000,
			"systemId":'nengyuanzongbiao',
			"ts": dateVal
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				
				let lineList = res.data.data.line
				if(content.length>0&&lineList.length>0){
					let array1 = res.data.data.content
					var newArray = [];
					var notInTimeFrameArray = [];
					var notInArray1 = [];
					var notInArray2 = [];
					var linelist = []
					var newList = []
					var barList = []
					let socList = []
					let outCapacityList = []
					var array2 = lineList.map(function(item) {
					    return {
					        ...item,
					        ts1: item.ts.split(' ')[1].slice(0, 5)
					    };
					});
					
					console.log(array2);
					array2.forEach(item2 => {
						var found = array1.some(item1 => {
							var startTime = new Date(item2.ts.split(" ")[0] + " " + item1.timeFrame.split("-")[0] +
								":00");
							var endTime = new Date(item2.ts.split(" ")[0] + " " + item1.timeFrame.split("-")[1] + ":00");
					
							var checkTime = new Date(item2.ts.split(" ")[0] + " " + item2.ts1 + ":00");
					
							if (checkTime >= startTime && checkTime <= endTime) {
								newArray.push(item2.ts1);
								// linelist.push()
								return true;
							}
						});
					
						if (!found) {
							notInTimeFrameArray.push(item2);
					
							// 在这里把array1中没有匹配上的item也取出
							if (!array1.some(item1 => {
									var startTime = new Date(item2.ts.split(" ")[0] + " " + item1.timeFrame.split("-")[0] +
										":00");
									var endTime = new Date(item2.ts.split(" ")[0] + " " + item1.timeFrame.split("-")[1] +
										":00");
					
									var checkTime = new Date(item2.ts.split(" ")[0] + " " + item2.ts1 + ":00");
					
									return checkTime >= startTime && checkTime <= endTime;
								})) {
								notInArray1.push(item2);
								newArray.push(item2.ts1);
							}
						}
					});
					
					array1.forEach(item1 => {
						var found = array2.some(item2 => {
							var startTime = new Date(item2.ts.split(" ")[0] + " " + item1.timeFrame.split("-")[0] +
								":00");
							var endTime = new Date(item2.ts.split(" ")[0] + " " + item1.timeFrame.split("-")[1] + ":00");
					
							var checkTime = new Date(item2.ts.split(" ")[0] + " " + item2.ts1 + ":00");
					
							return checkTime >= startTime && checkTime <= endTime;
						});
					
						if (!found) {
							notInArray2.push(item1);
							newArray.push(item1.stime);
							
						}else{
							// console.log(item1,'------------')
							newList.push(item1)
						}
					});
					
					console.log(newArray);
					console.log(newList)
					newArray.sort((a, b) => {
					    const timeA = convertToMinutes(a);
					    const timeB = convertToMinutes(b);
					    
					    return timeA - timeB;
					});
					
					function convertToMinutes(time) {
					    const splitTime = time.split(":");
					    const hours = parseInt(splitTime[0]);
					    const minutes = parseInt(splitTime[1]);
					    
					    return hours * 60 + minutes;
					}
					
					console.log(newArray);
					console.log(notInTimeFrameArray);
					console.log(notInArray1);
					console.log(notInArray2);
					console.log(newList)
					let newArraydata = [];
					for (let i = 0; i < 96; i++) {
						let hour = Math.floor(i / 4); // 计算小时数
						let minute = i % 4 * 15; // 计算分钟数
						let hourStr = hour < 10 ? '0' + hour : hour.toString();
						let minuteStr = minute === 0 ? '00' : minute.toString();
						newArraydata.push(hourStr + ':' + minuteStr);
					}

					console.log(newArraydata);
					
					newArraydata.forEach(time => {
						// console.log(time)
					    if (!array2.some(obj => obj.ts1 == time)) {
							// console.log(1)
					        array2.push({
					            "capacity": 0,
					            "createdTime": "",
					            "id": "",
					            "inCapacity": 0,
					            "isEnabled": false,
					            "load": 0,
					            "maxInLoad": 0,
					            "maxOutLoad": 0,
					            "nodeId": "",
					            "nodeName": "",
					            "online": false,
					            "outCapacity": 0,
					            "soc": '',
					            "soh": 0,
					            "strategy": "-",
					            "ts": "",
					            "ts1": time
					        });
					    }
					});
					
					console.log(array2);
					
					array2.sort((a, b) => {
					    return a.ts1.localeCompare(b.ts1);
					});
					
					// Print sorted data
					array2.forEach(obj => {
					    // console.log(obj);
					});
					array2.map(res =>{
						socList.push(res.soc)
						outCapacityList.push(res.outCapacity)
					})
					console.log(socList)
					console.log(newArraydata)
					const newData = [];
					
					array1.forEach(item => {
					    const timeFrameArray = item.timeFrame.split("-");
					    const startTime = timeFrameArray[0];
					    const endTime = timeFrameArray[1];
					
					    const matchedTimes = newArraydata.filter(time => {
					        const [hour, minute] = time.split(":");
					        const [startHour, startMinute] = startTime.split(":");
					        const [endHour, endMinute] = endTime.split(":");
					
					        const timeInFrame = (hour >= startHour && hour <= endHour) || (hour == startHour && minute >= startMinute) || (hour == endHour && minute <= endMinute);
					        return timeInFrame;
					    });
					
					    if (matchedTimes.length > 0) {
					        matchedTimes.forEach(matchedTime => {
					            newData.push({
					                ...item,
					                timeFrame: matchedTime + '-' + endTime,
					                updateTime: new Date().toISOString()
					            });
					        });
					    } else {
					        newData.push(item);
					    }
					});
					
					// Add unmatched times from data2 to newData
					// console.log(...newList[0])
					newArraydata.forEach(time => {
					    const exists = newData.some(item => item.timeFrame.startsWith(time));
					    if (!exists) {
					        newData.push({
					            ...newList[0],
					            timeFrame: time + '-' + time.substring(3),
					            latitude: null,
					            longitude: null,
					            multiplyingPower: null,
					            priceHour: null,
					            priceTag: null,
					            strategy: null,
					            strategyForecasting: null,
					            strategyHour: null
					        });
					    }
					});
					
					console.log(newData);
					
					newData.map(res =>{
						// barList.push(res.priceHour)
						barList.push(
						{
							value:res.priceHour,
							itemStyle: {
								color: res.property=="尖"?'#186AC3':res.property=="峰"?'#2C98FB':
								res.property=="平"?'#86BAF6':res.property=="谷"?'#EEF0FD':''
							},
							name:res.property
						})
						
					})
					console.log(barList)
					
					setChartDate(newArraydata);
					setChartList(barList);
					setSocList(socList);
					setOutCapacityList(outCapacityList);
					setIsEmpty(false);
					// charts()
				}else if(content.length==0&&lineList.length>0){
					let chartdate = []
					let socList = []
					let outCapacityList = []
					var array2 = lineList.map(function(item) {
					    return {
					        ...item,
					        ts1: item.createdTime.split(' ')[1].slice(0, 5)
					    };
					});
					array2.map(res =>{
						socList.push(res.soc)
						outCapacityList.push(res.outCapacity)
						chartdate.push(res.ts1)
					})
					
					console.log(socList)
					console.log(outCapacityList)
					let chartdatedata = [];
					for (let i = 0; i < 96; i++) {
					    let hour = Math.floor(i / 4); // 计算小时数
					    let minute = i % 4 * 15; // 计算分钟数
					    let hourStr = hour < 10 ? '0' + hour : hour.toString();
					    let minuteStr = minute === 0 ? '00' : minute.toString();
					    chartdatedata.push(hourStr + ':' + minuteStr);
					}
					
					console.log(chartdatedata);
					setSocList(socList);
					setOutCapacityList(outCapacityList);
					setChartList([]);
					setChartDate(chartdatedata);
					setIsEmpty(false);
					// charts()
				}else if(content.length>0&&lineList.length==0){
					let barList = []
					let chartdate = []
					content.map(res =>{
						// barList.push(res.priceHour)
						barList.push(
						{
							value:res.priceHour,
							itemStyle: {
								color: res.property=="尖"?'#186AC3':res.property=="峰"?'#2C98FB':
								res.property=="平"?'#86BAF6':res.property=="谷"?'#EEF0FD':'#000'
							},
							name:res.property
						})
						chartdate.push(res.stime)
						
					})
					console.log(barList)
					// let newArraydata = [];
					// for (let i = 0; i < 96; i++) {
					// 	let hour = Math.floor(i / 4); // 计算小时数
					// 	let minute = i % 4 * 15; // 计算分钟数
					// 	let hourStr = hour < 10 ? '0' + hour : hour.toString();
					// 	let minuteStr = minute === 0 ? '00' : minute.toString();
					// 	newArraydata.push(hourStr + ':' + minuteStr);
					// }
					
					// console.log(newArraydata);
					
					
					setSocList([]);
					setOutCapacityList([]);
					setChartList(barList);
					setChartDate(chartdate);
					setIsEmpty(false);
					// charts()
					
				}else{
					
					setSocList([]);
					setOutCapacityList([]);
					setChartList([]);
					setChartDate([]);
					setIsEmpty(true);
					// charts()
				}
				
				
			}else{
				
				setLoading(false)
				message.error(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 查询
	const searchs =() =>{
		if(dateVal&&nodeId){
			// this.profitTypeList()
			if(billNodeType=='pv'){
				// 光伏
				electricityBillPv()
				
				if(dateType==1){
					
					setChartDate([]);
					setChartList([]);
					setIsEmpty(true);
				}else{
					findPvTimeDivision()
				}
				// charts()
			}else if(billNodeType === 'load'){
				electricityBillChargingStation()
			}else{
				// 储能
				if(dateType==1||dateType==2){			
					setChartDate([]);
					setChartList([]);
					setSocList([]);
					setOutCapacityList([]);
					setIsEmpty(true);
					// charts()
				}else if(dateType==3){
					findStorageEnergyStrategy()
				}
				
				electricityBillStorageEnergy()
				
			}
		}

		if(dateVal==''){
			message.info('请选择时间')
		}
		if(nodeId==''){
			message.info('请选择节点')
		}
	}
	const tabFn=(index) => {
		console.log(index)
		
		setCurrent(index);
		setDateType(index);
		setDateVal('');
		setMonthcharge(index==1?'本年电量':index==2?'本月电量':'本日电量');
		setDayCharge(index==1?'年充电电费':index==2?'月充电电费':'日充电电费');
		setDayFcharge(index==1?'年放电电费':index==2?'月放电电费':'日放电电费');
	}
	const clsFn=(index, curCls, cls) => {
		// console.log(current)
		return current === index ? curCls : cls;
	}
	const onChangemonth =(e,dataString) =>{
		console.log(dataString)
		setDateVal(dataString)
	}
	const onChangeday =(e,dataString) =>{
		console.log(dataString,'---')
		setDateVal(dataString)
	}
	const charts=() =>{
		// const chartContainer = document.getElementById('chart-container');
		// 获取图表容器的初始宽度
		// const containerWidth = chartContainer.offsetWidth;
		// console.log(containerWidth)
		var chartDom = document.getElementById('power');
		if(chartDom){
			var myChart = echarts.init(chartDom);
			var option;
			// var isEmpty = true
			option = {
				tooltip: {
					trigger: "axis",
					// axisPointer: {
					//       type: 'cross'
					//     },
					backgroundColor: 'rgba(0, 0, 0, 0.5)', // 设置tooltip背景颜色为透明黑色
					extraCssText: 'backdrop-filter: blur(3px)', // 设置tooltip模糊效果
					borderWidth: 0,
					textStyle: {
						color: '#fff' // 设置 tooltip 的文字颜色为白色
					},
					// triggerOn: 'none',
					axisPointer: {
						type: 'line',
						// shadowStyle: {
						// 	color: 'rgba(0, 0, 0, .12)', // 设置阴影的颜色为半透明黑色
						// 	// shadowBlur: 10 // 设置阴影的模糊程度为10
						// },
						lineStyle: {
							color: 'rgba(0, 0, 0, .12)',
							// opacity:'0.12',
							width: 16 ,// 设置小圆点大小为3
							type:'solid'
						},
						
					},
					// shadowBlur: 5,
					
					formatter: function (params) {
						// console.log(params)
						// let str = params[0].axisValue + "</br>"
						let str = '<span style="display:block;padding:8px 10px 0px 8px">'+ params[0].axisValue + '</span>'
						params.forEach((item) => {
					
							if(item.seriesName=='尖峰平谷'){
								if(item.name=='尖'||item.name=='峰'||item.name=='平'||item.name=='谷'){
									if(item.value===""||item.value===null||item.value===undefined){
										// str+=item.marker+item.name+ " : " +'-'+ "</br>" 
										str+='<span style="color:#FFF;padding:12px 10px 12px 8px;display:inline-block;">' + item.marker+item.name+ " : " + "-"+ '元/kWh' + '</span><br/>';
										
									}else{
										console.log('1111111111')
										// str+=item.marker+item.name+ " : " + (parseFloat(item.value).toFixed(2))+ '/kWh' +"</br>" 
										str += '<span style="color:#FFF;padding:12px 10px 12px 8px;display:inline-block;">' + item.marker+item.name+ " : " + (parseFloat(item.value).toFixed(2))+ '元/kWh' + '</span><br/>';
						
									}
								}
								
							}else{
								if(item.value===""||item.value===null||item.value===undefined){
									// str+=item.marker+ item.seriesName +" : " +'-'+ "</br>" 
									str += '<span style="color:#FFF;padding:0px 10px 0px 8px;display:inline-block;">' + item.marker+item.seriesName+ " : " + '-' + '</span><br/>';
								}else{
									str += '<span style="color:#FFF;padding:0px 10px 0px 8px;display:inline-block;">' + item.marker+item.seriesName+ " : " + ((parseFloat(item.value)*100).toFixed(2))+ '%' + '</span><br/>';
									
								}
							}
							
						
						});
						return str;
						
					},
				},
				
				grid: {
					left: 0,
					right: 0,
					bottom: 40,
					containLabel: true,
					top:30
				},
				title: {
					show: isEmpty,  // 是否要展示“暂无数据”矢量图
					text: ' {a|}',  // 写入占位符a，以便后续填充内容
					x: 'center',
					y: 50,
					subtext: '暂无数据',  // 子标题
					itemGap: -10,  // 设置主副标题间隔
					textStyle: {
					rich: {
						a: {
						height: 110,  // 设置图片高度
						width: 99, // 设置图片宽度
						backgroundColor: {
							// 引入图片，作为背景图，填写相对路径
							image: require('../../style/damao/empty.png')  
						}
						}
					}
					},
					subtextStyle: {
					// 配置副标题的文字样式
					fontSize: 12,
					color: '#FFF'
					}
				},
			// title: {
				//     text: '元',
				// 	left:0,
				// 	textStyle:{
				// 		color:'#FFF',
				// 　　　　 fontSize:12,
				// 		fontWeight:'normal'
				// 	}
				// },
				xAxis: {
					type: 'category',
					axisLine: {
						//坐标轴轴线相关设置
						lineStyle: {
						color: "#DFE1E5", //x轴线颜色设置
						},
					},
					
					axisLabel: {
						interval: chartdate.length==24?0:3,
						interval:billNodeType=='storageEnergy'&&chartdate.length==24?0:2,
						formatter: function (value, index) {
							// console.log(value.substring(3, 5))
							let str = value.substring(3, 5)
							if (str=='00') {  // 只显示整点的时间数据
								return value;
							} else {
								return '';
							}
								
						}
					},
					axisTick: {
						//x轴刻度相关设置
						// alignWithLabel: true,
						show:false
					},
					
					data: chartdate,
					// data:['01:00',2,3,4],
					// data: ['00:00','00:15','00:30', '00:45','01:00', '02:00', '03:00', '04:00', '05:00', '06:00', '07:00', '08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00', '22:00', '23:00'],

					// barWidth: '100%',
							// barGap: '-100%'
				},
				yAxis: [
					{
						type: 'value',
						axisLabel : {
							// formatter: '{value*100}',
							formatter: function (value) {
								return value*100; // 返回自定义格式化的刻度标签内容
							},
							textStyle: {
								color: '#FFF',
								// textAlign:'left'
							},
							// align: 'left',
							margin:10
							// margin: '0px 50px 0px 0px'
						},
						
						splitLine: {
							//网格线
							lineStyle: {
							type: "dashed", //设置网格线类型 dotted：虚线   solid:实线
							width: 1,
							color:"rgba(255,255,255,.5)"
							},
							show: true, //隐藏或显示
						}
					},
					
					{
						type: 'value',
						// name: '第二个Y轴',
						position: 'right',
						axisLabel: {
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
							// color:"#DFE1E5"
							color:"rgba(255,255,255,.5)"
							},
							show: true, //隐藏或显示
						}
					}
					// {
					//   type: 'value',
					//   // name: 'Temperature',
					//   axisLabel: {
					//           formatter: '{value} °C'
					//         }
					
					// }
				],
				series: [
						{
							type: 'bar',
							data: chartList,
							// data:[1,4,6,7,9,2],
							barCategoryGap: '-3%', // 柱子之间的空隙
							yAxisIndex: 1,
							barGap: '-3%',
							name: '尖峰平谷',
							itemStyle: {
								borderColor: 'red' ,// 设置边框颜色为透明
								borderWidth:0,
								// emphasis: {
								// 	color:'',
								// 	barBorderRadius:0
								// }
							},
							
							
						}, 
						{
							name: 'soc',
							type: 'line',
							showSymbol: false,
							// smooth: true,
							yAxisIndex: 0,
							color:'#5AD8A6',
							data: socList,
							emphasis: {
								symbol: 'circle',
								symbolSize: 8
							},
						}
					],
					
			}
			
			option && myChart.setOption(option);
			window.addEventListener('resize', function() {
				myChart.resize()
			})
			myChart.resize();
		}
		
	}
	const powerIndex =() =>{
		console.log('12121')
		// alert(0)
		
	}
	  // 导出
	const devices =() =>{
		
		if(dateVal&&nodeId){
			// this.profitTypeList()
			if(billNodeType=='pv'){
				// 光伏
				// this.electricityBillPv()
				// this.findPvTimeDivision()
				// axios({
				// 	method: 'post',
				// 	url: 'electricity_bill_management/electricity_bill/electricityBillPvExcel',
				// 	responseType: 'arraybuffer',
				// 	data: {
				// 		"date": dateType==1?dateVal+'-01-01':dateType==2?dateVal+'-01':dateVal,
				// 		"nodeId": nodeId,
				// 		"type":dateType==1?"YEAR":dateType==2?"MONTH":'DAY'
				// 	}
				// }).then(res => {
				// 	if (res.status == 200) {
				// 		const url = window.URL.createObjectURL(new Blob([res.data]));
				// 		const link = document.createElement('a'); //创建a标签
				// 		link.style.display = 'none';
				// 		link.href = url; // 设置a标签路径
				// 		link.download = '列表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
				// 		document.body.appendChild(link);
				// 		link.click();
				// 		URL.revokeObjectURL(link.href); // 释放 URL对象
				// 		document.body.removeChild(link);
				
				// 	}
				// })
				message.info('目前仅支持储能节点导出')
			}else{
				// 储能
				// axios({
				// 	method: 'post',
				// 	url: 'electricity_bill_management/electricity_bill/electricityBillStorageEnergyExcel',
				// 	responseType: 'arraybuffer',
				// 	data: {
				// 		"date": dateType==1?dateVal+'-01-01':dateType==2?dateVal+'-01':dateVal,
				// 		"nodeId": nodeId,
				// 		"type":dateType==1?"YEAR":dateType==2?"MONTH":'DAY'
				// 	}
				// }).then(res => {
				// 	if (res.status == 200) {
				// 		const url = window.URL.createObjectURL(new Blob([res.data]));
				// 		const link = document.createElement('a'); //创建a标签
				// 		link.style.display = 'none';
				// 		link.href = url; // 设置a标签路径
				// 		link.download = '列表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
				// 		document.body.appendChild(link);
				// 		link.click();
				// 		URL.revokeObjectURL(link.href); // 释放 URL对象
				// 		document.body.removeChild(link);
				
				// 	}
				// })
				if(dateType==3){
					axios({
						method: 'post',
						url: 'system_management/energy_model/energy_storage_model/bisExport',
						responseType: 'arraybuffer',
						data: {
							"ts": dateType==1?dateVal+'-01-01':dateType==2?dateVal+'-01':dateVal,
							"nodeId": nodeId,
							"systemId":'nengyuanzongbiao'
							
						}
					}).then(res => {
						if (res.status == 200) {
							const url = window.URL.createObjectURL(new Blob([res.data]));
							const link = document.createElement('a'); //创建a标签
							link.style.display = 'none';
							link.href = url; // 设置a标签路径
							link.download = '列表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
							document.body.appendChild(link);
							link.click();
							URL.revokeObjectURL(link.href); // 释放 URL对象
							document.body.removeChild(link);
					
						}
					})
				}
				
				
			}
		}
		if(dateVal==''){
			message.info('请选择时间')
		}
		if(nodeId==''){
			message.info('请选择节点')
		}
	}
	// tabFn


	const disabledDate: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023-01')) || current > dayjs().add(36,'month')
		
	};
	const disabledDate1: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023-01')) || current > dayjs().add(25,'year')
		
	};
	const dateYear = 'YYYY';
	const dateMonth = 'YYYY-MM';
	const dateDay = 'YYYY-MM-DD';
	const initialValue = '2023'
	return(
		<div className="allbody">
			<div className="power">
				<div className="powerheader">
					<b>类型：</b>
					<Select placeholder="类型" style={{ width: 170 }} value={billNodeType} onChange={handleChange}>
					   {
						   nodeNameList.length &&nodeNameList.map(res =>{
							   return <Option key={res.nodeTypeKey} value={res.nodeTypeKey}>{res.nodeTypeName}</Option>
						   })
					   }
						
					</Select>
					<b >节点：</b>
						<Cascader style={{width:300}} loading={powerLoading} 
							// value="07c3c82df1dd93e9c303644eb79985cb"
							value={value}
							displayRender={(labels, selectedOptions) => labels[labels.length - 1]}
						options={options} onChange={treeChange}  placeholder="节点" />
					
					<div id='iconmes' className=' tab_con3 tab_con4 ' style={{display:'inline-block',width:'320px !important',marginRight:'10px'}}>
						<ol className='abb'>
							<li onClick={() => { tabFn(1) }} className={current==1 ? 'cur3' : 'cur31'}>年</li>
							<li onClick={() => { tabFn(2) }} className={current==2 ? 'cur3' : 'cur31'}>月</li>
							<li onClick={() => { tabFn(3) }} className={current==3 ? 'cur3' : 'cur31'}>日</li>
						</ol>
						 
						<ul className='acc'>
							<li className={clsFn(1, 'current', '')}>
								<ConfigProvider locale={locale}>
									<DatePicker
									value={dateVal==""?undefined:dayjs(dateVal, dateYear)} format={dateYear}
									style={{ width: 200 }} 
									disabledDate={disabledDate1} 
									picker="year" 
									onChange={onChangeYear} />
								</ConfigProvider>
							</li>
							<li className={clsFn(2, 'current', '')}>
								<ConfigProvider locale={locale}>
									<DatePicker 
									value={dateVal==""?undefined:dayjs(dateVal, dateMonth)} format={dateMonth}
									style={{ width: 200 }} disabledDate={disabledDate} picker="month" onChange={onChangemonth} />
								</ConfigProvider>
								
							</li>
							<li className={clsFn(3, 'current', '')}>
								<ConfigProvider locale={locale}>
									<DatePicker 
									value={dateVal==""?undefined:dayjs(dateVal, dateDay)} format={dateDay}
									style={{ width: 200 }} disabledDate={disabledDate} onChange={onChangeday} />
								</ConfigProvider>
								
							</li>
						</ul>
						
					</div>
					<Button type = "primary" onClick={searchs}>查询</Button>
					
				</div>
				<ul className="earnings">
					<li>
						<div className="earn">
							<p>总收益（元）</p>
							<span>{profitValue}</span>
						</div>
						<img src={require('../../style/img/jianfeng.png')}  />
					</li>
					
					<li style={{marginRight:0}}>
						<div className="earn">
							<p>虚拟电厂运营商收益（元）</p>
							<span>{loadProfitValue}</span>
						</div>
						<img src={require('../../style/img/shouyi.png')}  />
					</li>
				
				
					<li style={{marginRight:0}}>
						<div className="earn">
							<p>电力用户收益（元）</p>
							<span>{consumerProfitValue}</span>
						</div>
						<img src={require('../../style/img/yong.png')}  />
					</li>
					
				</ul>
				<Spin spinning={tableLoading} tip="加载中"  size="large">
					<div className="afferss">
					{
						pv=='storageEnergy'?
							<table className="tables">
								<tr>
									<th>
										电表安装地点	
									</th>
									
									<th>
										电表名称		
									</th>
									<th>
										电表编号		
									</th>
									<th>
										表计类型		
									</th>
									<th>
										时段	
									</th>
									<th>
										市电单价（元/kWh）		
									</th>
									<th>
										{monthcharge}（kWh）		
									</th>
									<th>
										电费（元）
							
									</th>
									
								</tr>
								<tr>
									<td colspan="8">{dayCharge}</td>
								</tr>
								<tr>
									<td rowspan="4">储能电站配电箱</td>
									<td rowspan="4">{inMeterDeviceName}</td>
									<td rowspan="4">{inMeterDeviceNum}</td>
									<td>正向有功</td>
									<td>尖</td>
									<td>{priceHigh}</td>
									<td>{inElectricityHigh}</td>
									<td>{inElectricityHighPrice}</td>
								</tr>
								<tr >
									<td > 正向有功</td>
									<td>峰</td>
									<td>{pricePeak}</td>
									<td>{inElectricityPeak}</td>
									<td>{inElectricityPeakPrice}</td>
								</tr>
								<tr>
									<td>正向有功</td>
									<td>平</td>
									<td>{priceStable}</td>
									<td>{inElectricityStable}</td>
									<td>{inElectricityStablePrice}</td>
								</tr>
								<tr>
									<td>正向有功</td>
									<td>谷</td>
									<td>{priceLow}</td>
									<td>{inElectricityLow}</td>
									<td>{inElectricityLowPrice}</td>
								</tr>
								
								<tr>
									<td colspan="8">{dayFcharge}</td>
								</tr>
								<tr>
									<td rowspan="4">储能电站配电箱</td>
									<td rowspan="4">{outMeterDeviceName}</td>
									<td rowspan="4">{outMeterDeviceNum}</td>
									<td>反向有功</td>
									<td>尖</td>
									<td>{priceHigh}</td>
									<td>{outElectricityHigh}</td>
									<td>{outElectricityHighPrice}</td>
								</tr>
								<tr>
									<td>反向有功</td>
									<td>峰</td>
									<td>{pricePeak}</td>
									<td>{outElectricityPeak}</td>
									<td>{outElectricityPeakPrice}</td>
								</tr>
								<tr>
									<td>反向有功	</td>
									<td>平</td>
									<td>{priceStable}</td>
									<td>{outElectricityStable}</td>
									<td>{outElectricityStablePrice}</td>
								</tr>
								<tr>
									<td>反向有功</td>
									<td>谷</td>
									<td>{priceLow}</td>
									<td>{outElectricityLow}</td>
									<td>{outElectricityLowPrice}</td>
								</tr>
							</table>:
						<>
							{
								pv=='load' ?
								<table className="tables">
									<tr>
										<th>
											电表安装地点	
										</th>
										
										<th>
											电表名称		
										</th>
										<th>
											电表编号		
										</th>
										<th>
											表计类型		
										</th>
										<th>
											时段	
										</th>
										<th>
											市电单价（元/kWh）		
										</th>
										<th>
											{monthcharge}（kWh）		
										</th>
										<th>
											电费（元）
										</th>
										
									</tr>
									<tr>
										<td colspan="8">  </td>
									</tr>
									<tr>
										                        {/* 功率显示组件预留位置 */}
										<td rowspan="4">{chargingStation}</td>
										<td rowspan="4">{meterDeviceName}</td>
										<td rowspan="4">{meterDeviceNum}</td>
										<td>正向有功</td>
										<td>尖</td>
										<td>{priceHigh}</td>
										<td>{electricityHigh}</td>
										<td>{electricityHighPrice}</td>
									</tr>
									<tr >
										<td>正向有功</td>
										<td>峰</td>
										<td>{pricePeak}</td>
										<td>{electricityPeak}</td>
										<td>{electricityPeakPrice}</td>
									</tr>
									<tr>
										<td>正向有功</td>
										<td>平</td>
										<td>{priceStable}</td>
										<td>{electricityStable}</td>
										<td>{electricityStablePrice}</td>
									</tr>
									<tr>
										<td>正向有功</td>
										<td>谷</td>
										<td>{priceLow}</td>
										<td>{electricityLow}</td>
										<td>{electricityLowPrice}</td>
									</tr>
									
									
								</table> : 
								<table className="tables">
								<tr>
									<th>
										电表安装地点	
									</th>
									
									<th>
										电表名称		
									</th>
									<th>
										电表编号		
									</th>
									<th>
										表计类型		
									</th>
									<th>
										时段	
									</th>
									<th>
										市电单价（元/kWh）		
									</th>
									<th>
										{monthcharge}（kWh）		
									</th>
									<th>
										电费（元）
							
									</th>
									
								</tr>
								<tr>
									<td colspan="8">  </td>
								</tr>
								<tr>
									<td rowspan="4">光伏电站配电箱</td>
									<td rowspan="4">{meterDeviceName}</td>
									<td rowspan="4">{meterDeviceNum}</td>
									<td>正向有功</td>
									<td>尖</td>
									<td>{priceHigh}</td>
									<td>{electricityHigh}</td>
									<td>{electricityHighPrice}</td>
								</tr>
								<tr >
									<td > 正向有功</td>
									<td>峰</td>
									<td>{pricePeak}</td>
									<td>{electricityPeak}</td>
									<td>{electricityPeakPrice}</td>
								</tr>
								<tr>
									<td>正向有功</td>
									<td>平</td>
									<td>{priceStable}</td>
									<td>{electricityStable}</td>
									<td>{electricityStablePrice}</td>
								</tr>
								<tr>
									<td>正向有功</td>
									<td>谷</td>
									<td>{priceLow}</td>
									<td>{electricityLow}</td>
									<td>{electricityLowPrice}</td>
								</tr>
								
								
								</table>
							}
						</>
					}
						
					</div>
					{
						billNodeType === 'load' ? <div className='power-trend' style={{height:'300px'}}></div> : 
						<div className="power-trend">
							<h4>
								<b>{billNodeType=='storageEnergy'?'电价信息及SOC变化趋势':'电价信息'}</b>
								<div className="power-trendTop">
									
									<ul className="powerLeng">
										<li className={isActive ? 'poweractive' : ''}><span></span>尖</li>
										<li className={isActive1 ? 'poweractive' : ''}><span></span>峰</li>
										<li className={isActive2 ? 'poweractive' : ''}><span></span>平</li>
										<li className={isActive3 ? 'poweractive' : ''}><span></span>谷</li>
										
										{
											billNodeType=='storageEnergy'?<li className={isActive3 ? 'poweractive' : ''}><span></span>SOC</li>:''
										}
									</ul>
									<Button type="primary" onClick={devices} style={{float:'right',marginTop:4}}>导出</Button>
									
									
								</div>
								
							</h4>
							<h4 className="power-unit" style={{fontSize:12,paddingLeft:10}}>
								
								{billNodeType=='storageEnergy'?'%':''}
								<b>元/kWh</b>
							</h4>
							<div id="power"></div>
						</div>
					}
					
					
				</Spin>
				
			</div>
			
		</div>
	)
}
	



export default Power


// <b >日期：</b>
						 
// 						<ConfigProvider  locale={locale}>
// 							<DatePicker disabledDate={disabledDate} onChange={this.onChange}
							
// 							picker="month" />
// 						</ConfigProvider>




