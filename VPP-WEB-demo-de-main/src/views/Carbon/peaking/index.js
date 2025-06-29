import React, { useEffect,useState } from 'react';
import { Table, Tag, Space,Icon,Select ,Checkbox,DatePicker,Button,ConfigProvider,Progress,Modal,Statistic ,message } from 'antd';
import '../peaking/index.css';
import * as echarts from 'echarts';

// import '../table/index.css';
import locale from 'antd/locale/zh_CN';
// import echarts  from '../../echarts.js'
import dayjs from 'dayjs';
// import '../from/index.css';
import icon1 from './img/icon1.png'
import icon2 from './img/icon2.png'
import icon3 from './img/icon3.png'
import icon4 from './img/icon4.png'
import icon5 from './img/icon5.png'
import icon6 from './img/icon6.png'

import treeicon from '../../../style/imgs/tree.png'
import "./peaking.scss";
import http from '../../../server/server.js'


const { MonthPicker, RangePicker } = DatePicker;
const dateFormat = 'YYYY-MM-DD';
const monthFormat = 'YYYY-MM';

function onChange(value){
	
}
const { Option } = Select;
const Peaking =() =>{
	
	const [monthlist, setMonthlist] = useState([
			{
				name:'范围一',
				value:'240.92t',
				size:'同比',
				val:'-',
				key:10
			},
			{
				name:'范围二',
				value:'24.09t',
				size:'同比',
				val:'-',
				key:11
			},
			{
				name:'范围三',
				value:'481.85t',
				size:'同比',
				val:'-',
				key:12
			}
	]);	
	const [yearlist, setYearlist] = useState([
			{
				name:'范围一',
				value:'2891.07t',
				size:'同比',
				val:'-',
				key:20
			},
			{
				name:'范围二',
				value:'289.11t',
				size:'同比',
				val:'-',
				key:21
			},
			{
				name:'范围三',
				value:'5782.14t',
				size:'同比',
				val:'-',
				key:22
			}
	]);
	const [tabActiveIndex, setTabActiveIndex] = useState(0);
	const [mode, setMode] = useState(['month', 'month']);
	const [value, setValue] = useState([]);
	const [ranking, setRanking] = useState([
		{
			value:8962.32,
			key:31,
			name:'总碳排量'
		},
		{
			value:2891.07,
			key:32,
			name:'范围一'
			
		},
		{
			value:289.11,
			key:33,
			name:'范围二'
			
		},
		{
			value:5782.14,
			key:34,
			name:'范围三'
			
		}
	]);
	const [List, setList] = useState(['今日','本月','今年']);
	const [index, setIndex] = useState(0);
	const [indexs, setIndexs] = useState(0);
	const [all, setAll] = useState(0.5632);
	const [day, setDay] = useState(0.06);
	const [tree, setTree] = useState(0.5032);
	const [year, setYear] = useState([394,465,744,713,868,930,434,465,899,640,868,651]);
	const [one, setOne] = useState([140.9225,150.9225,240.9225,230.9225,280.9225,300.9225,140.9225,150.9225,290.9225,200.9225,280.9225,210.9225]);
	const [two, setTwo] = useState([14.09225,15.09225,24.09225,23.09225,28.09225,30.09225,14.09225,15.09225,29.09225,20.09225,28.09225,21.09225]);
	const [three, setThree] = useState([240,300,480,460,560,600,280,300,580,400,560,420]);
	const [days, setDays] = useState([0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075,0.803075]);
	const [nodeList, setNodeList] = useState([]);
	const [nodeId, setNodeId] = useState('');
	const [yearcarbon, setYearcarbon] = useState('');
	const [thisMonthEmissionTotal, setThisMonthEmissionTotal] = useState('-');
	const [thisMonthEmissionTotalCompare, setThisMonthEmissionTotalCompare] = useState('');
	const [thisMonthScope1, setThisMonthScope1] = useState('-');
	const [thisMonthScope1Compare, setThisMonthScope1Compare] = useState('');
	const [thisMonthScope2, setThisMonthScope2] = useState('-');
	const [thisMonthScope2Compare, setThisMonthScope2Compare] = useState('');
	const [thisMonthScope3, setThisMonthScope3] = useState('-');
	const [thisMonthScope4Compare, setThisMonthScope4Compare] = useState('');
	const [thisDayEmissionTotal, setThisDayEmissionTotal] = useState('-');
	const [thisDayEmissionTotalCompare, setThisDayEmissionTotalCompare] = useState('');
	const [thisDayScope1, setThisDayScope1] = useState('-');
	const [thisDayScope1Compare, setThisDayScope1Compare] = useState('');
	const [thisDayScope2, setThisDayScope2] = useState('-');
	const [thisDayScope2Compare, setThisDayScope2Compare] = useState('');
	const [thisDayScope3, setThisDayScope3] = useState('-');
	const [thisDayScope3Compare, setThisDayScope3Compare] = useState('');
	const [thisYearEmissionTotal, setThisYearEmissionTotal] = useState('-');
	const [thisYearEmissionTotalCompare, setThisYearEmissionTotalCompare] = useState('');
	const [thisYearScope1, setThisYearScope1] = useState('-');
	const [thisYearScope1Compare, setThisYearScope1Compare] = useState('');
	const [thisYearScope2, setThisYearScope2] = useState('-');
	const [thisYearScope2Compare, setThisYearScope2Compare] = useState('');
	const [thisYearScope3, setThisYearScope3] = useState('-');
	const [thisYearScope3Compare, setThisYearScope3Compare] = useState('');
	const [startTime, setStartTime] = useState('2023-01');
	const [endTime, setEndTime] = useState(dayjs().format('YYYY-MM'));
	const [emissionTotal, setEmissionTotal] = useState('');
	const [scope1, setScope1] = useState('');
	const [scope2, setScope2] = useState('');
	const [scope3, setScope3] = useState('');
	const [emissionList, setEmissionList] = useState([]);
	const [carbonstartTime, setCarbonstartTime] = useState('2023-01');
	const [carbonendTime, setCarbonendTime] = useState(dayjs().format('YYYY-MM'));
	const [greenElectricityBuy, setGreenElectricityBuy] = useState('-');
	const [greenElectricityBuyCompare, setGreenElectricityBuyCompare] = useState('-');
	const [greenSyndromeBuy, setGreenSyndromeBuy] = useState('-');
	const [greenSyndromeBuyCompare, setGreenSyndromeBuyCompare] = useState('-');
	const [transaction, setTransaction] = useState('-');
	const [transactionCompare, setTransactionCompare] = useState('-');
	const [photovoltaicPowerGeneration, setPhotovoltaicPowerGeneration] = useState('-');
	const [windPowerGeneration, setWindPowerGeneration] = useState('-');
	const [photovoltaicPowerGenerationCompare, setPhotovoltaicPowerGenerationCompare] = useState('-');
	const [windPowerGenerationCompare, setWindPowerGenerationCompare] = useState('-');
	const [skinstartTime, setSkinstartTime] = useState('2023-01');
	const [skinendTime, setSkinendTime] = useState(dayjs().format('YYYY-MM'));
	const [greenArea, setGreenArea] = useState('-');
	const [greenEmissions, setGreenEmissions] = useState('-');
	const [total, setTotal] = useState('-');
	const [treeEmissions, setTreeEmissions] = useState('-');
	const [treeNum, setTreeNum] = useState('');
	const [currentUnit, setCurrentUnit] = useState('');
	const [scopetType1, setScopetType1] = useState([]);
	const [scopetType2, setScopetType2] = useState([]);
	const [scopetType3, setScopetType3] = useState([]);
	const [scopetTypeall, setScopetTypeall] = useState([]);
	const [dayTreeCO2, setDayTreeCO2] = useState('-');
	const [dayGreenCO2, setDayGreenCO2] = useState('-');
	const [boy1, setBoy1] = useState('');
	const [girl1, setGirl1] = useState('');
	const [totalCo2, setTotalCo2] = useState('-');
	const [hscope1, setHscope1] = useState('');
	const [hscope2, setHscope2] = useState('');
	const [hscope3, setHscope3] = useState('');
	const [thisMonthScope3Compare,setThisMonthScope3Compare] = useState('');
	const [optionsloading, setOptionsloading] = useState(false);

	const handleTabClick=(obj)=>{
		console.log(obj)
		if(obj =='0'){
			
		}
		setTabActiveIndex(obj)
	}

	useEffect(() =>{
		
		getnodeList();
	},[])
	useEffect(() =>{
		if(nodeId!==''){
			getEmissionDataCount()
			getDisplacementAnalysis()
			getTradeDataCount()
			getCarbonSink()
		}
	},[nodeList,nodeId])
	// 获取节点列表
	const getnodeList=() =>{
		setOptionsloading(true)
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				setNodeList(res.data.data);
				setNodeId(res.data.data[0].id);
				setCurrentUnit(res.data.data[0].id);
				setOptionsloading(false);
				
			}else{
				Modal.error({
					title:'错误'
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	useEffect(() =>{
		if(thisDayScope1!=='-'&&thisDayScope2!=='-'&&thisDayScope3!=='-'){
			energy()
		}
	},[thisDayScope1,thisDayScope2,thisDayScope3]);
	useEffect(() =>{
		if(thisMonthScope1!=='-'&&thisMonthScope2!=='-'&&thisMonthScope3!=='-'){
			energys()
		}
	},[thisMonthScope1,thisMonthScope2,thisMonthScope3]);
	useEffect(() =>{
		if(thisMonthScope1!=='-'&&thisMonthScope2!=='-'&&thisMonthScope3!=='-'){
			energys()
		}
	},[thisMonthScope1,thisMonthScope2,thisMonthScope3]);
	useEffect(() =>{
		if(thisYearScope1!=='-'&&thisYearScope2!=='-'&&thisYearScope3!=='-'){
			energyes()
		}
	},[thisYearScope1,thisYearScope2,thisYearScope3]);
	useEffect(() =>{
		tendency();
	},[scopetType1,scopetType2,scopetType3,emissionList,scopetTypeall])
	
	// 获取碳排放量数据
	const getEmissionDataCount=() =>{
		http.post('carbon/monitoring/getEmissionDataCount?nodeId='+nodeId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				console.log(data.thisYearScope3)
				setThisMonthEmissionTotal(data.thisMonthEmissionTotal==null?'-':Number(data.thisMonthEmissionTotal).toFixed(2));
				setThisMonthEmissionTotalCompare(data.thisMonthEmissionTotalCompare==null?'-':Number(data.thisMonthEmissionTotalCompare).toFixed(2));
				setThisMonthScope1(data.thisMonthScope1==null?'-':Number(data.thisMonthScope1).toFixed(2));
				setThisMonthScope1Compare(data.thisMonthScope1Compare==null?'-':Number(data.thisMonthScope1Compare).toFixed(2));
				setThisMonthScope2(data.thisMonthScope2==null?'-':Number(data.thisMonthScope2).toFixed(2));
				setThisMonthScope2Compare(data.thisMonthScope2Compare==null?'-':Number(data.thisMonthScope2Compare).toFixed(2));
				setThisMonthScope3(data.thisMonthScope3==null?'-':Number(data.thisMonthScope3).toFixed(2));
				setThisMonthScope3Compare(data.thisMonthScope3Compare==null?'-':Number(data.thisMonthScope3Compare).toFixed(2));
				setThisDayEmissionTotal(data.thisDayEmissionTotal==null?'-':Number(data.thisDayEmissionTotal).toFixed(2));
				setThisDayEmissionTotalCompare(data.thisDayEmissionTotalCompare);
				setThisDayScope1(data.thisDayScope1==null?'-':Number(data.thisDayScope1).toFixed(2));
				setThisDayScope1Compare(data.thisDayScope1Compare==null?'-':Number(data.thisDayScope1Compare).toFixed(2));
				setThisDayScope2(data.thisDayScope2==null?'-':Number(data.thisDayScope2).toFixed(2));
				setThisDayScope2Compare(data.thisDayScope2Compare==null?'-':Number(data.thisDayScope2Compare).toFixed(2));
				setThisDayScope3(data.thisDayScope3==null?'-':Number(data.thisDayScope3).toFixed(2));
				setThisDayScope3Compare(data.thisDayScope3Compare==null?'-':Number(data.thisDayScope3Compare).toFixed(2));
				setThisYearEmissionTotal(data.thisYearEmissionTotal==null?'-':Number(data.thisYearEmissionTotal).toFixed(2));
				setThisYearEmissionTotalCompare(data.thisYearEmissionTotalCompare);
				setThisYearScope1(data.thisYearScope1==null?'-':Number(data.thisYearScope1).toFixed(2));
				setThisYearScope1Compare(data.thisYearScope1Compare==null?'-':Number(data.thisYearScope1Compare).toFixed(2));
				setThisYearScope2(data.thisYearScope2==null?'-':Number(data.thisYearScope2).toFixed(2));
				setThisYearScope2Compare(data.thisYearScope2Compare==null?'-':Number(data.thisYearScope2Compare).toFixed(2));
				setThisYearScope3(data.thisYearScope3==null?'-':Number(data.thisYearScope3).toFixed(2));
				setThisYearScope3Compare(data.thisYearScope3Compare==null?'-':Number(data.thisYearScope3Compare).toFixed(2));
				
				// energy()
				// energys()
				// energyes()
			}
		})
	}
	// 排序
	//封装的日期排序方法
	const ForwardRankingDate=(data, p) => {
	    for (var i = 0; i < data.length - 1; i++) {
	        for (var j = 0; j < data.length - 1 - i; j++) {
	            // console.log(Date.parse(data[j][p]));
	            if (Date.parse(data[j][p]) > Date.parse(data[j+1][p])) {
	                var temp = data[j];
	                data[j] = data[j + 1];
	                data[j + 1] = temp;
	            }
	        }
	    }
	    return data;
	}
	// 获取不同范围碳排量分析
	const getDisplacementAnalysis=() =>{
		http.post('carbon/monitoring/getDisplacementAnalysis?nodeId='+nodeId+
		'&startTime='+startTime+'&endTime='+endTime
		).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				console.log(data)
				if(data){
					
					let x = []
					let scopetType1 = []
					let scopetType2 = []
					let scopetType3 = []
					let scopetTypeall = []
					let emissionList = data.emissionList
					let num = 0
					let num1 = 0
					let num2 = 0
					let num3 = 0
					let emissionList1 = []
					let emissionList2 = []
					let emissionList3 = []
					let month = []
					if(data.emissionList[1]){
						emissionList1 = data.emissionList[1]
						console.log(emissionList1)
						emissionList1.map(res =>{
							month.push(res.dateTime)
							scopetType1.push(res.dischargeValue)
							
						})
						
					}
					if(data.emissionList[2]){
						emissionList2 = data.emissionList[2]
						num2 = emissionList2.length
						emissionList2.map(res =>{
							scopetType2.push(res.dischargeValue)
						})
						
					}
					if(data.emissionList[3]){
						emissionList3 = data.emissionList[3]
						num3 = emissionList3.length
						emissionList3.map(res =>{
							scopetType3.push(res.dischargeValue)
						})
					
					}
					console.log(month)
					function unique(arr) {
						var newArr = [];
						for (var i = 0; i < arr.length; i++) {
							if (newArr.indexOf(arr[i]) === -1) {
								newArr.push(arr[i]);
							}
						}
						return newArr;
					}
					month = unique(month)
					// console.log(month,'111111111111111111111111111');
					function sortDownDate(a, b) {
						return Date.parse(a) - Date.parse(b);
					}
					month = month.sort(sortDownDate);
					let json = {scopetType1,scopetType2,scopetType3}; //json中有任意多个数组
					console.log(json)
					let result = [];  
					//遍历json
					for(let key in json){
					    //遍历数组的每一项
					    json[key].forEach((value,index) => {
					        if( isBlank(result[index]) ){
								
					            result[index] = 0 ;
					        }
							// console.log(result[index])
					        result[index] += Number(value) ;        
					    })        
					}
					//打印结果
					console.log(result);  //[22, 35, 47, 59, 71, 83]
					//判断值是否存在函数
					function isBlank(val){
						console.log(val)
					    if(val == null || val == ""|| val == undefined){
					        return true;
					    }
					}
					
					let scope1 = Number(data.scope1)
					let scope2 = Number(data.scope2)
					let scope3 = Number(data.scope3)
					let allscope = Number(scope1+scope2+scope3).toFixed(2)
					console.log(allscope,scope1,scope2,scope1)
					let hscope1 = Number(data.scope1/allscope)*100
					let hscope2 = Number(data.scope2/allscope)*100
					let hscope3 = Number(data.scope3/allscope)*100
					console.log(allscope,hscope1,hscope2,hscope3)
					console.log(Number(scope1+scope2+scope3).toFixed(2))
					setEmissionTotal(allscope);
					setScope1(Number(data.scope1).toFixed(2));
					setScope2(Number(data.scope2).toFixed(2));
					setScope3(Number(data.scope3).toFixed(2));
					setHscope1(hscope1);
					setHscope2(hscope2);
					setHscope3(hscope3);
					setEmissionList(month);
					setScopetType1(scopetType1);
					setScopetType2(scopetType2);
					setScopetType3(scopetType3);
					setScopetTypeall(result);
					// tendency();
				}
				
			}else{
				message.info(res.data.msg)
			}
		})

	}
	// 获取碳中和
	const getTradeDataCount=() =>{
		http.post('carbon/monitoring/getTradeDataCount?nodeId='+nodeId+
		'&startTime='+carbonstartTime+'&endTime='+carbonendTime).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				setGreenElectricityBuy(data.greenElectricityBuy==null?'-':Number(data.greenElectricityBuy).toFixed(2));
				setGreenElectricityBuyCompare(data.greenElectricityBuyCompare==null?'-':Number(data.greenElectricityBuyCompare).toFixed(2));
				setGreenSyndromeBuy(data.greenSyndromeBuy==null?'-':Number(data.greenSyndromeBuy).toFixed(2));
				setGreenSyndromeBuyCompare(data.greenSyndromeBuyCompare==null?'-':Number(data.greenSyndromeBuyCompare).toFixed(2));
				setTransaction(data.transaction==null?'-':Number(data.transaction).toFixed(2));
				setTransactionCompare(data.transactionCompare);
				setPhotovoltaicPowerGenerationCompare(Number(data.photovoltaicPowerGenerationCompare).toFixed(2));
				setPhotovoltaicPowerGeneration(data.photovoltaicPowerGeneration==null?'-':Number(data.photovoltaicPowerGeneration).toFixed(2))
				setWindPowerGeneration(data.windPowerGeneration==null?'-':Number(data.windPowerGeneration).toFixed(2));
				setWindPowerGenerationCompare(Number(data.windPowerGenerationCompare).toFixed(2));
				setTotalCo2(data.totalCo2==null?'-':Number(data.totalCo2).toFixed(2));
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	useEffect(() =>{
		boy()
	},[boy1])
	useEffect(() =>{
		girl()
	},[girl1])
	// 查询碳汇信息
	const getCarbonSink=() =>{
		http.post('carbon/monitoring/getCarbonSink?nodeId='+nodeId+
		'&startTime='+skinstartTime+'&endTime='+skinendTime).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let boy1=Number(data.greenEmissions/data.total).toFixed(2)
				let girl1=Number(data.treeEmissions/data.total).toFixed(2)
				if(isNaN(boy1)){
					setBoy1(0)
				}else{
					setBoy1(Number(data.greenEmissions/data.total).toFixed(2))
				}
				if(isNaN(girl1)){
					setGirl1(0);
				}else{
					setGirl1(Number(data.treeEmissions/data.total).toFixed(2))
				}
				
				setTreeEmissions(data.treeEmissions?Number(data.treeEmissions).toFixed(2):'');
				setGreenArea(Number(data.greenArea).toFixed(2));
				setGreenEmissions(data.greenEmissions?Number(data.greenEmissions).toFixed(2):'');
				setTotal(Number(data.total).toFixed(2));
				setTreeNum(data.treeNum);
				setDayGreenCO2(Number(data.dayGreenCO2).toFixed(2));
				setDayTreeCO2(Number(data.dayTreeCO2).toFixed(2));
				
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	//今日
	const energy=() =>{
		var chartDom = document.getElementById('energy');
		var myChart = echarts.init(chartDom);
		var option;
		option = {
			series: [
				{
				name: 'Access From',
				type: 'pie',
				roseType: 'radius',
				radius: ['40%', '70%'],
				avoidLabelOverlap: false,
				label: {
					// alignTo: 'edge',
					color:'#FFF',
					formatter:function(data){ 
						// console.log(data)
						let label = data.name +'\n'+data.percent.toFixed(1)+"%";
						return label 
					} ,
					// formatter: function (value, index) {           
					
					//                   return value.toFixed(1);      
					
					//                    },
					// formatter: '{name|{b}}\n{time|{c}.toFixed(2) %}',
					 // formatter: function (value, index) { return value.toFixed(1) }      ,
					minMargin: 5,
					edgeDistance: 10,
					lineHeight: 20,
					rich: {
					  time: {
						fontSize: 14,
						color: '#FFF',
						lineHeight: 20
					  }
					}
				},
				labelLine: {
					length: 15,
					length2: 0,
					maxSurfaceAngle: 80
				},
				labelLayout: function (params) {
					const isLeft = params.labelRect.x < myChart.getWidth() / 2;
					const points = params.labelLinePoints;
					// Update the end point.
					points[2][0] = isLeft
					  ? params.labelRect.x
					  : params.labelRect.x + params.labelRect.width;
					return {
					  labelLinePoints: points
					};
				},
				data: [
					{ value: thisDayScope1, name: '范围一',itemStyle: { color: '#1890FF' }  },
					{ value: thisDayScope2, name: '范围二',itemStyle: { color: '#36CBCB' }  },
					{ value: thisDayScope3, name: '范围三',itemStyle: { color: '#FAD337' }  },
					
				]
				
				}
			]
		};
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
	}	
	// 本月
	const energys=() =>{
		var chartDom = document.getElementById('energys');
		var myChart = echarts.init(chartDom);
		var option;
		option = {
			series: [
				{
				name: 'Access From',
				type: 'pie',
				roseType: 'radius',
				radius: ['40%', '70%'],
				avoidLabelOverlap: false,
				label: {
					// alignTo: 'edge',
					color:'#FFF',
					formatter:function(data){
						// console.log(data)
						let label = data.name +'\n'+data.percent.toFixed(1)+"%";
						return label 
					} ,
					// formatter: '{name|{b}}\n{time|{c} %}',
					minMargin: 5,
					edgeDistance: 10,
					lineHeight: 20,
					rich: {
					  time: {
						fontSize: 14,
						color: '#FFF',
						lineHeight: 20
					  }
					}
				},
				labelLine: {
					length: 15,
					length2: 0,
					maxSurfaceAngle: 80
				},
				labelLayout: function (params) {
					const isLeft = params.labelRect.x < myChart.getWidth() / 2;
					const points = params.labelLinePoints;
					// Update the end point.
					points[2][0] = isLeft
					  ? params.labelRect.x
					  : params.labelRect.x + params.labelRect.width;
					return {
					  labelLinePoints: points
					};
				},
				data: [
					{ value: thisMonthScope1, name: '范围一',itemStyle: { color: '#1890FF' }  },
					{ value: thisMonthScope2, name: '范围二',itemStyle: { color: '#36CBCB' }  },
					{ value: thisMonthScope3, name: '范围三',itemStyle: { color: '#FAD337' }  },
					
				]
				
				}
			]
		};
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
	}	
	// 今年
	const energyes=() =>{
		var chartDom = document.getElementById('energyes');
		var myChart = echarts.init(chartDom);
		var option;
		option = {
			series: [
				{
				name: 'Access From',
				type: 'pie',
				roseType: 'radius',
				radius: ['40%', '70%'],
				avoidLabelOverlap: false,
				label: {
					// alignTo: 'edge',
					color:'#FFF',
					// formatter: '{name|{b}}\n{time|{c} %}',
					formatter:function(data){
						// console.log(data)
						let label = data.name +'\n'+data.percent.toFixed(1)+"%";
						return label 
					} ,
					minMargin: 5,
					edgeDistance: 10,
					lineHeight: 20,
					rich: {
					  time: {
						fontSize: 14,
						color: '#FFF',
						lineHeight: 20
					  }
					}
				},
				labelLine: {
					length: 15,
					length2: 0,
					maxSurfaceAngle: 80
				},
				labelLayout: function (params) {
					const isLeft = params.labelRect.x < myChart.getWidth() / 2;
					const points = params.labelLinePoints;
					// Update the end point.
					points[2][0] = isLeft
					  ? params.labelRect.x
					  : params.labelRect.x + params.labelRect.width;
					return {
					  labelLinePoints: points
					};
				},
			  //   label: {
			  //       show: true,
					// color:'#FFF'
			  //   },
			  //   labelLine: {
			  //       show: true
			  //   },
				data: [
					{ value: thisYearScope1, name: '范围一',itemStyle: { color: '#1890FF' }  },
					{ value: thisYearScope2, name: '范围二',itemStyle: { color: '#36CBCB' }  },
					{ value: thisYearScope3, name: '范围三',itemStyle: { color: '#FAD337' }  },
					
				]
				
				}
			]
		};
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
	}	
	// 不同范围碳排量分析
	const tendency=() =>{
		var tendency = echarts.init(document.getElementById('tendency'));
		window.addEventListener('resize', function() {
			tendency.resize()
		})
		tendency.setOption({
			title: {
			    text: '单位：t',
				x:30,
				// y:10,
				textStyle: { 
					fontSize: 12,
					color:'#FFF',
					
				},
				
			},
			tooltip: {
				trigger: "axis",
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
			legend: {
			    data: ['总碳排放量', '范围一', '范围二', '范围三'],
				bottom:10,
				icon:'rect',
				itemWidth: 16, // 图例标记的图形宽度。
					itemHeight:16,
					textStyle: {
					color: '#FFF'          // 图例文字颜色
				}
			},
			grid: {
			    left: '3%',
			    right: '4%',
			    bottom: '14%',
			    containLabel: true
			},
			xAxis: {
			    type: 'category',
			    boundaryGap: false,
			    data: emissionList,
				axisLabel : {
					formatter: '{value}',
					textStyle: {
						color: '#FFF'
					}
				},
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
			   
			series: [
			    {
			      name: '总碳排放量',
			      type: 'line',
			      // stack: 'Total',
				  itemStyle : {  
					  
					  normal : {  
						  color:'#FAD337',
						  lineStyle:{  
							  color:'#FAD337'
						  }  
					  }  
				  },
			      data:scopetTypeall
			    },
			    {
			      name: '范围一',
			      type: 'line',
			      // stack: 'Total',
				  itemStyle : {
					  
					  normal : {  
						  color:'#A894F6',
						  lineStyle:{  
							  color:'#A894F6'  
						  }  
					  }  
				  },
			      data: scopetType1
			    },
			    {
			      name: '范围二',
			      type: 'line',
			      // stack: 'Total',
				  itemStyle : {
					  
					  normal : {  
						  color:'#3BA0FF',
						  lineStyle:{  
							  color:'#3BA0FF'  
						  }  
					  }  
				  },
			      data: scopetType2
			    },
				{
				  name: '范围三',
				  type: 'line',
				  // stack: 'Total',
				  itemStyle : {
					  
					  normal : {  
						  color:'#5DDECF',
						  lineStyle:{  
							  color:'#5DDECF'  
						  }  
					  }  
				  },
				  data: scopetType3
				}
			]
		})
	}
	//选择月份
	const handlePanelChange = (value, mode) => {
		
	};
	// 搜搜
	const search1 =() =>{
		getEmissionDataCount()
		
	}
	// 选择节点
	const handleChange = value => {
		setNodeId(value)
	};
	// 月份
	const onChangemonth =(value,mode) =>{
		console.log(value)
		console.log(mode)
		setStartTime(mode[0]);
		setEndTime(mode[1]);
	}
	// 碳中和
	const onChangecarbon =(value,mode) =>{
		console.log(value)
		console.log(mode)
		setCarbonstartTime(mode[0])
		setCarbonendTime(mode[1]);
	}
	// 碳中和查询
	const searchcarbon=() =>{
		getTradeDataCount()
	}
	// 碳汇选择月份
	const onChangetree =(value,mode) =>{
		
		setSkinstartTime(mode[0]);
		setSkinendTime(mode[1])
	}
	// 碳汇查询
	const skin =() =>{
		getCarbonSink()
	}
	const search =() =>{
		getDisplacementAnalysis()
	}
	//选择节点
	const handlepoint = val =>{
		console.log(val)
	}
	// 绿化面积
	const boy=() =>{
		console.log(boy1)
		var myChart = echarts.init(document.getElementById('boys'));
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		myChart.setOption({
			legend: {
			    data: ['Email', 'Union Ads', 'Video Ads', 'Direct', 'Search Engine']
			},
		    series: [
		        {
		        type: 'gauge',
				radius:'100%' ,
				startAngle: 90,
		        endAngle: -270,
		        min: 0,
		        max: 1,
		        splitNumber: 0,
				 backgroundColor: '#f3f4f5',
		        itemStyle: {
					// show:false,
		            color: '#1890FF',
		            shadowColor: '#212029',
		            shadowBlur: 5,
		            shadowOffsetX: 2,
		            shadowOffsetY: 2
		        },
		        progress: {
		            show: true,
		            roundCap: true,
		            width: 10,
					
		        },
		        pointer: {
					show:false,
		        },
		
				axisLine: {
					lineStyle: {
						width: 10,
						// color: [[1, 'rgba(0,0,0,0.7)']],
						// shadowColor: 'rgba(0, 0, 0, 0.5)',
						color: [[1, 'rgba(255,255,255,.6)']],
						shadowColor: '#212029',
						shadowBlur: 15
					}
				},
		        axisTick: {
					show:false,
		            splitNumber: 2,
		            lineStyle: {
		              width: 2,
		              color: '#f6f8fc'
		            }
		        },
		        splitLine: {
					show:false,
		            length: 12,
		            lineStyle: {
		              width: 3,
		              color: '#FFF'
		            }
		        },
		        axisLabel: {
					show:false
		            
		        },
		        detail: {
		            width: '60%',
		            borderRadius: 8,
					// backgroundColor: '#000',
		            offsetCenter: [0, '35%'],
		            valueAnimation: true,
		            formatter: function (value) {
						console.log(value)
						if(value===''||value==undefined||value==null||value==NaN){
							return '';
						}else{
							return '{value|' + Number(value*100).toFixed(0) + '%}';
						}
						
		            },
		            rich: {
						value: {
							fontSize: 18,
							color: '#FFF',
							padding:[-20,0,0,0]
						},
						unit: {
							fontSize: 12,
							color: '#FFF',
							padding:[-20,0,0,0]
						}
		            }
		        },
		        data: [
		            {
						value: boy1
		            }
		          ]
		        }
		      ]
		});
	}
	// 种植树木
	const girl=() =>{
		var girlsliving = echarts.init(document.getElementById('girls'));
		window.addEventListener('resize', function() {
			girlsliving.resize()
		})
		girlsliving.setOption({
		    series: [
		        {
		        type: 'gauge',
				radius:'100%' ,
				startAngle: 90,
		        endAngle: -270,
		        min: 0,
		        max: 1,
		        splitNumber: 0,
				roundCap: true,
				color:'#000',
				clockwise:true,
		        itemStyle: {
					// show:false,
		            color: '#36CBCB',
		            shadowColor: 'rgba(0,138,255,0.45)',
		            shadowBlur: 5,
		            shadowOffsetX: 2,
		            shadowOffsetY: 2
		        },
		        progress: {
		            show: true,
		            roundCap: true,
		            width: 8
		        },
		        pointer: {
					show:false,
		        },
		        axisLine: {
		        	lineStyle: {
		        		width: 10,
		        		// color: [[1, 'rgba(0,0,0,0.7)']],
		        		// shadowColor: 'rgba(0, 0, 0, 0.5)',
						color: [[1, 'rgba(255,255,255,.6)']],
						shadowColor: '#212029',
		        		shadowBlur: 15
		        	}
		        },
		        axisTick: {
					// show:false,
		            splitNumber: 2,
		            lineStyle: {
		              width: 2,
		              color: '#000'
		            }
		        },
		        splitLine: {
					show:false,
		            length: 12,
		            lineStyle: {
		              width: 3,
		              color: '#000'
		            }
		        },
		        axisLabel: {
					show:false
		        },
		        detail: {
		            width: '60%',
		            borderRadius: 8,
		            offsetCenter: [0, '35%'],
		            valueAnimation: true,
		            formatter: function (value) {
						
						if(value===''||value==undefined||value==null){
							return '';
						}else{
							return '{value|' + Number(value*100).toFixed(0) + '%}';
						}
		            },
		            rich: {
						value: {
							fontSize: 18,
							color: '#FFF',
							padding:[-20,0,0,0]
						},
						unit: {
							fontSize: 12,
							color: '#FFF',
							padding:[-20,0,0,0]
						}
		            }
		        },
		        data: [
		            {
						value: girl1
		            }
		          ]
		        }
		      ]
		});
	}
	
	const disabledDate: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023-01')) || current > dayjs().endOf('month')
	};
	const onChange: DatePickerProps['onChange'] = (date, dateString) => {
	  console.log(date, dateString);
	};
	return(
		<div className="peaking">
			<div className="header">
				节点：
				<Select
					placeholder="节点"
					style={{width:217}}
					onChange={handleChange}
					loading={optionsloading}
					defaultValue={currentUnit}
					key={currentUnit}
				>
					{
						nodeList&&nodeList.map(res =>{
							return <Option value={res.id}>{res.nodeName}</Option>
						})
					}
					
					
				</Select>
				
				<Button type="primary" onClick={search1} style={{marginLeft:'16px'}}>查询</Button>
			</div>
			<ul className="peakinglist">
				<li>
					<h4>日均</h4>
					<p>总碳排放量</p>
					<b>{thisDayEmissionTotal}t</b>
					<div id="energy" style={{width:'100%',height:'240px'}}></div>
					<div className="scope">
						<div className="scopelist" >范围一<span >{thisDayScope1}t</span></div>
						<div className="scopelist" >范围二<span >{thisDayScope2}t</span></div>
						<div className="scopelist" >范围三<span >{thisDayScope3}t</span></div>
					</div>
				</li>
				<li>
					<h4>本月</h4>
					<p>总碳排放量</p>
					<b>{thisMonthEmissionTotal}t</b>
					<div id="energys" style={{width:'100%',height:'240px'}}></div>
					<div className="scope">
						<div className="scopelist" >范围一<span >{thisMonthScope1}t</span></div>
						<div className="scopelist" >范围二<span >{thisMonthScope2}t</span></div>
						<div className="scopelist" >范围三<span >{thisMonthScope3}t</span></div>
					
					</div>
				</li>
				<li>
					<h4>今年</h4>
					<p>总碳排放量</p>
					<b>{thisYearEmissionTotal}t</b>
					<div id="energyes" style={{width:'100%',height:'240px'}}></div>
					<div className="scope">
					
						<div className="scopelist" >范围一<span >{thisYearScope1}t</span></div>
						<div className="scopelist" >范围二<span >{thisYearScope2}t</span></div>
						<div className="scopelist" >范围三<span >{thisYearScope3}t</span></div>
												
					</div>
				</li>
			</ul>
			
			<div className="area">
				<div className="title" style={{marginBottom:20}}>不同范围碳排量分析
					<Space style={{float:'right'}}>
						<ConfigProvider locale={locale}>
							<RangePicker picker="month"
							disabledDate={disabledDate}
							defaultValue={[dayjs(startTime, monthFormat), dayjs(endTime, monthFormat)]}
							format={monthFormat}
							
							onChange={onChangemonth}  />
						</ConfigProvider>
						<Button type="primary" style={{marginLeft:'16px'}} onClick={search}>查询</Button>
					</Space>
				</div>
				<div className="efficiency">
					<div id="tendency"></div>
					
				</div>
				<div className="gross">
					<h4>碳排量总值（范围总值） <span>单位：t</span></h4>
					<div className="progresses">
						<ul>
							<li>
								<b className="gressone"></b>
								<div className="gress">
									<span>总碳排量</span>
									<Progress  
										size="small"
										strokeColor={{
											from: '#108ee9',
											to: '#108ee9',
											
										}} 
										percent={emissionTotal*100} 
										showInfo={false} />
								</div>
								<span>{emissionTotal}</span>
							</li>
							<li>
								<b className="gresstwo"></b>
								<div className="gress">
								<span>范围一</span>
								<Progress  
								size="small"
								strokeColor={{
									from: '#108ee9',
									to: '#108ee9',
									
								  }} percent={hscope1?hscope1:hscope1} showInfo={false} />
									</div>
									<span>{scope1}</span>
							</li>
							
							<li>
								<b className="gressthree"></b>
								<div className="gress">
								<span>范围二</span>
								<Progress  
								size="small"
								strokeColor={{
									from: '#108ee9',
									to: '#108ee9',
									
								  }} percent={hscope2?hscope2:hscope2} showInfo={false} />
								</div><span>{scope2}</span>
							</li>
							<li><b className="gressfour"></b><div className="gress">
								<span>范围三</span>
								<Progress  
								size="small"
								strokeColor={{
									from: '#108ee9',
									to: '#108ee9',
									
								  }} percent={hscope3?hscope3:hscope3} showInfo={false} />
								</div><span>{scope3}</span>
							</li>
						</ul>
						
					</div>
				</div>
			</div>
			<div className="management">
				<h4>碳中和
				<Space style={{float:'right'}}>
					<ConfigProvider locale={locale}>
						<RangePicker picker="month" 
						disabledDate={disabledDate}
						defaultValue={[dayjs(carbonstartTime, monthFormat), dayjs(carbonendTime, monthFormat)]}
						format={monthFormat}
						onChange={onChangecarbon}  />
					</ConfigProvider>
					<Button type="primary" onClick={searchcarbon}  style={{margin:'0px 16px 0px 16px'}}>查询</Button>
				</Space>
				
				
				</h4>
				<div className="allmanagementlist">
				<ul className="managementlist">
					<li>
						<div className="quantity">
							<img src={icon3} />
							<div className="imglist">
								<span>绿电购买量  kWh</span>
								<p>{greenElectricityBuy}</p>
								
							</div>
						</div>
						
					</li>
					<li>
						<div className="quantity  addborder" > 
							<img src={icon2} />
							<div className="imglist">
								<span>光伏发电量  kWh</span>
								<p>{photovoltaicPowerGeneration}</p>
								
							</div>
						</div>
					</li>
					
					
				</ul>
				<ul className="managementlist">
					<li>
						<div className="quantity" style={{margin:'0 auto',marginTop:30}}>
							<img src={icon1} />
							<div className="imglist">
								<span>风能发电量  kWh</span>
								<p>{windPowerGeneration}</p>
								
							</div>
						</div>
					</li>
					<li>
						<div className="quantity addborder" style={{margin:'0 auto',marginTop:30}}>
							<img src={icon4} />
							<div className="imglist">
								<span>绿证购买量  kWh</span>
								<p>{greenSyndromeBuy}</p>
								
							</div>
						</div>
						
					</li>
					
				</ul>
				<ul className="managementlist managementlists">
					
					<li>
						<div className="quantity" > 
							<img src={icon5} />
							<div className="imglist">
								<span>碳交易量  t</span>
								<p>{transaction}</p>
								
							</div>
						</div>
						
					</li>
					<li>
						<div className="quantity addborder" > 
							<img src={icon6} />
							<div className="imglist">
								<span>总碳中和  t</span>
								<p>{totalCo2}</p>
								
							</div>
						</div>
						
					</li>
				</ul>
				</div>
			</div>
			<div className="Carbon" style={{marginBottom:30}}>
				<h4>碳汇
				<Space style={{float:'right'}}>
					<ConfigProvider locale={locale}>
						<RangePicker picker="month" onChange={onChangetree} 
						disabledDate={disabledDate}
						defaultValue={[dayjs(skinstartTime, monthFormat), dayjs(skinendTime, monthFormat)]}
						format={monthFormat}  
						/>
					</ConfigProvider>
					<Button type="primary" onClick={skin} style={{margin:'0px 16px 0px 16px'}}>查询</Button>
				</Space>
				</h4>
				<div className="allboys">
				<div className="alltree">
					<div className="controlperson">
						<div className="boyperson">
							<div className="boyliving">
							<div id="boys"></div>
								<ul className="living">
									<li>
										<b>逐月累计绿化面积 {greenArea}㎡</b>
										<p style={{color:'#1890FF'}}>{greenEmissions}t</p>
										<span>吸收的碳排放量</span>
									</li>
									
								</ul>
								
							</div>
						</div>
						<div className="girlperson">
							<div id="girls"></div>
							<ul className="living living1">
								<li>
									<b>逐月累计种植树木   {treeNum}棵</b>
									<p style={{color:'#36CBCB'}}>{treeEmissions}t</p>
									<span>吸收的碳排放量</span>
								</li>
							</ul>
							
						</div>
					</div>
					<img  src={treeicon} />
					<div className="amounts">
						<div className="addup">总</div>
						<p>{total}t</p>
						<span>总吸收碳排放量</span>
					</div>
					<ul className="absorb" style={{borderLeft: '1px dashed rgba(255,255,255,0.2)'}}>
						   <li>
							<p>{dayTreeCO2}kg</p>
							<span>种一棵树一天吸收CO2</span>
						   </li>
						   <li>
							<p>{dayGreenCO2}kg</p>
							<span>1/㎡草坪一天吸收CO2</span>
						   </li>
						  </ul>
				</div>
					
				
				</div>
			</div>
		</div> 
	)
}

export default Peaking