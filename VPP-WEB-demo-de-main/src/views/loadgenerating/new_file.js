import React,{Component} from 'react'
// import 'antd/dist/antd.css'
// import 'antd/dist/antd.min.css';
import './index.css'
import './index.scss'
import { Tree,DatePicker,ConfigProvider,Button,Table,Input  ,Select ,Typography ,message,Spin } from 'antd';
import type { DataNode, TreeProps } from 'antd/lib/tree';
import dayjs from 'dayjs';
import http from '../../server/server.js'
import * as echarts from 'echarts';
// import echarts  from '../echarts.js'
import car from '../../style/electric/icon1.png'
import icon1 from '../../style/electric/icon9.png'
import icon3 from '../../style/electric/icon8.png'
import icon10 from '../../style/electric/icon10.png'
import icon11 from '../../style/electric/icon11.png'
import icon4 from '../../style/electric/icon4.png'
class generating extends Component {
	constructor(props) {
		super(props)
		this.state={
			// icon2:require('../../style/electric/icon1.png')
			dateString:'',
			newdata:[],
			standard:[106,106,106,106,106,106,106,106,106,106,106,106,106,106,106]	,//标准
			panel:[167,165,162,161,158,154,151,148,148,150,151,151,149,146]		,//节点
			actual:[162,163,162,158,154,150,148,147,150,152,151,149,146,143],
			deviceObjmyCompOverlay:''
		}
	}
	componentDidMount(){
		this.charts()
		this.container()
		this.nowDatetime()
		
	}
	nowDatetime() {
	 
	    var date = new Date();
	    var month = (date.getMonth()+1) > 9 ? (date.getMonth()+1) : "0" + (date.getMonth()+1);
	    var day = (date.getDate()) > 9 ? (date.getDate()) : "0" + (date.getDate());
	    var hours = (date.getHours()) > 9 ? (date.getHours()) : "0" + (date.getHours());
	    var minutes = (date.getMinutes()) > 9 ? (date.getMinutes()) : "0" + (date.getMinutes());
	    var seconds = (date.getSeconds()) > 9 ? (date.getSeconds()) : "0" + (date.getSeconds());
	    
	    var dateString =
	        date.getFullYear() + "-" +
	        month + "-" + day
		this.setState({
			dateString:dateString
		},() =>{
			 console.log(this.state.dateString)
			 this.genDaysArr()
		})
	    // return dateString; 
	}
	genDaysArr(timestamp) {
		let {standard,panel,actual} = this.state
		let now =  Date.now();     //1645099205343
		console.log(this.state.dateString)
		const d = new Date(now)
		const y = d.getFullYear()
		const m = d.getMonth()+1
		const m_str = m>10?m:'0'+m
	
		// 获取指定月份天数
		const days = new Date(y,m,0).getDate()
		const arr = []
		for (let i = 1; i <= days; i ++) {
			const day_str = i>=10?i:'0'+i
			arr.push( `${y}-${m_str}-${day_str}`)
		}
		// console.log(arr)
		// return arr
		let sliceA = []
		for(var i=0;i<arr.length;i++){
			if(arr[i]==this.state.dateString){
				console.log(i)
				sliceA=arr.slice(0,i+1)
				// console.log('sliceA',sliceA)//得到[1,2,3]
			}
		}
		let newArray = []
		if(standard.length<sliceA.length){
			let leth = sliceA.length  - standard.length
			console.log(leth)
			let now = 146
			let actualnow = 143
			for(var i=0;i<leth;i++){
				console.log(i)
				now = now-3
				actualnow = actualnow - 3
				this.state.standard.push(106)
				this.state.panel.push(now)
				this.state.actual.push(actualnow)
			}
			console.log(this.state.standard)
			console.log(this.state.panel)
			console.log(this.state.actual)
		}
		this.setState({
			newdata:sliceA,
			standard,
			panel,
			actual
		},() =>{
			// console.log(this.state.standard)
			this.charts()
		})
	}
	charts(){
		
		
		var chartDom = document.getElementById('main');
		var myChart = echarts.init(chartDom);
		var option;
		
		option = {
		 
		 //  tooltip: {
		 //    trigger: 'axis',
			
			// // formatter: function (params) {
			// // 	console.log(params)
			// // 	//console.log(JSON.stringify(params.data.value[2]));
			// // 	return params.data.name + "市<br/>跨境网络零售:" + params.data.value[2] + "亿元";
			// // }
		 //  },
			tooltip: {
		          trigger: "axis",
		   //        formatter: function (params) {
		           
					// let str = params[0].axisValue + "</br>"
		   //          params.forEach((item) => {
		   //            str+=item.marker+item.seriesName+ " : " + (parseFloat(item.data).toFixed(2))+ "</br>" 
		   //          });
		   //          return str;
		   //        },
		        },
			grid: {
				left: 0,
				right: 25,
				bottom: '20%',
				containLabel: true,
				top:30
			},
			color:['#F7B500','#6DD400','#E02020'],
			legend: {
				data: ['标准坪效', '节点年累计平均坪效', '节点实时坪效'],
				textStyle: {
					  color: '#FFFFFF',
					  fontSize: '12px',
				},
				icon: 'rect', 	
				itemWidth: 17,
				itemHeight: 3,
				bottom:10
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
				// data: ['01','02','03','04','05','06','07',
				// '08','09','10','11','12','13','14','15']
				data:this.state.newdata
			},
			yAxis: {
				type: 'value',
				axisLabel : {
					formatter: '{value}1',
					textStyle: {
						color: '#fff'
					}
				},
				splitLine: {
					//网格线
					lineStyle: {
					  type: "dashed", //设置网格线类型 dotted：虚线   solid:实线
					  width: 1,
					  color:"rgba(255, 255, 255, 0.2)"
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
				data:this.state.standard,
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
				data:this.state.panel,
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
		    {
				name: '节点实时坪效',
				type: 'line',
				// stack: 'Total',
				// data: [162,163,162,158,154,150,148,147,150,152,151,149,146,143	],
				data:this.state.actual,
				smooth: true,
				showSymbol: false,
				itemStyle : {
					normal : {  
						lineStyle:{  
						 color:'#E02020'  
						}  
					}  
				}, 
		    }
		  ]
		};
		
		option && myChart.setOption(option);

	}
	container(){
		let {icon2} = this.state
		var map = new window.BMap.Map('container');
		
		var point = new window.BMap.Point(121.456694,29.864493);// 120.73717 经度  //31.25795 纬度
		map.centerAndZoom(point, 12);
		map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
		map.enableScrollWheelZoom();
		// map = this.map;
		// 复杂的自定义覆盖物
		function addMapDeviceMarker (deviceObj) {
			console.log(deviceObj)
		    let _this = this;
		    function ComplexCustomOverlay (point, text, mouseoverText) {
		      this._point = point;
		      this._text = text;
		      this._overText = mouseoverText;
		    }
		 
		    ComplexCustomOverlay.prototype = new window.BMap.Overlay();
		 
		    ComplexCustomOverlay.prototype.initialize = function (map) {
		      this._map = map;
		      // 覆盖物容器样式
		      var div = this._div = document.createElement('div');
		      div.style.position = 'absolute';
			  // 29.864493
			  div.className = deviceObj.lat=='29.864493'?'openWindow':'activeopen'
		      div.style.zIndex = new window.BMap.Overlay.getZIndex(this._point.lat);
		      // div.style.backgroundColor = 'transparent';
		      // div.style.border = 'none';
		      div.style.color = 'white';
		      // div.style.height = '55px';
		      // div.style.width = '43px';
		      div.style.cursor = 'pointer'
		      // div.style.padding = '0';
		      div.style.lineHeight = '18px';
		      div.style.whiteSpace = 'nowrap';
		      div.style.MozUserSelect = 'none';
		      div.style.fontSize = '14px';
		      div.style.transform = 'translate(-10px, -30px)';
		 
		      var span = this._span = document.createElement('span');
			  span.position = ''
		      div.appendChild(span);
		      span.appendChild(document.createTextNode(this._text));
		      var that = this;
				
			  // var h4 = this._h4 = document.createElement('h4')
			  // h4.style.border = '1px solid #000'
			  // div.appendChild(h4);
			  // map.getPanes().labelPane.appendChild(div);
			  
		      // 覆盖物箭头样式，但是我并不需要，所以就只设置了大小
		      var arrow = this._arrow = document.createElement('div');
		      // arrow.style.position = 'absolute';
		      // arrow.style.width = '11px';
		      // arrow.style.height = '10px';
		      // arrow.style.top = '22px';
		      // arrow.style.left = '10px';
			  arrow.className = 'arrow'
		      arrow.style.overflow = 'hidden';
		      div.appendChild(arrow);
		      map.getPanes().labelPane.appendChild(div);
			  var arrow1 = this._arrow = document.createElement("div");
		       // arrow1.style.background = "url(//map.baidu.com/fwmap/upload/r/map/fwmap/static/house/images/label.png) no-repeat";
		       arrow1.style.position = "absolute";
		       arrow1.style.width = "100%";
		       // arrow1.style.height = "10px";
		       arrow1.style.top = "22px";
		       arrow1.style.left = "10px";
		       arrow1.style.overflow = "hidden";
		       div.appendChild(arrow1);
		      // 覆盖物图片样式
		      let imgDiv = div.children[1];
		      imgDiv.style.top = 0;
		      imgDiv.style.left = 0;
			  arrow.style.position = 'absolute';
			  arrow.style.top = '9px';
			  arrow.style.left = '10px';
		      imgDiv.style.padding = '0';
			  imgDiv.className = 'activeimg'
			 		 
			 
		      // imgDiv.style.background = "url(icon4)"
		      return div;
		    }
		 
		    ComplexCustomOverlay.prototype.draw = function () {
		      var pixel = map.pointToOverlayPixel(this._point);
		      this._div.style.left = pixel.x - parseInt(this._arrow.style.left) + "px";
		      this._div.style.top  = pixel.y - 30 + "px";
		    }
		 
		 
		    let myCompOverlay = 
			new ComplexCustomOverlay(new window.BMap.Point(deviceObj.lng,deviceObj.lat ), '宁波市诚凯箱包有限公司', '');
		    map.addOverlay(myCompOverlay);
		 
		 	// 将覆盖物对象存储起来，方便以后删除
		    deviceObj.myCompOverlay = myCompOverlay;
			// this.setState({
			// 	deviceObjmyCompOverlay:myCompOverlay
			// })
			console.log(deviceObj.myCompOverlay)
			
		 }  
		    
		    
		// 将标注添加到地图中
		// map.setMapStyleV2({     
		//   styleId: '7aefceb2581e7e97dcd113f7b77264c7',
		  
		// });
		// var mapStyle={  style : "mapbox" }  ;
		var myStyleJson= [
			{
			"featureType": "land",
			"elementType": "geometry",
			"stylers": {
				"visibility": "on",
				"color": "#223247ff"
			}
			}, {
				"featureType": "water",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#223247ff"
				}
			}, {
				"featureType": "building",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#172638ff"
				}
			}, {
				"featureType": "building",
				"elementType": "geometry.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#08141fff"
				}
			}, {
				"featureType": "water",
				"elementType": "geometry",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "village",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "town",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "district",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "country",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "city",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "continent",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "poilabel",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "poilabel",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "scenicspotslabel",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "scenicspotslabel",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "transportationlabel",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "transportationlabel",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "airportlabel",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "airportlabel",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "road",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#3c4e65ff"
				}
			}, {
				"featureType": "road",
				"elementType": "geometry.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#1e2b3dff"
				}
			}, {
				"featureType": "road",
				"elementType": "geometry",
				"stylers": {
					"weight": "3"
				}
			}, {
				"featureType": "green",
				"elementType": "geometry",
				"stylers": {
					"visibility": "on",
					"color": "#143e47ff"
				}
			}, {
				"featureType": "scenicspots",
				"elementType": "geometry",
				"stylers": {
					"visibility": "on",
					"color": "#223247ff"
				}
			}, {
				"featureType": "scenicspots",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "scenicspots",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "continent",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "country",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "city",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "city",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "scenicspotslabel",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "airportlabel",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "transportationlabel",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "railway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "subway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "highwaysign",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "nationalwaysign",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "nationalwaysign",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "provincialwaysign",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "provincialwaysign",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "tertiarywaysign",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "tertiarywaysign",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "subwaylabel",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "subwaylabel",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "road",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff",
					"weight": "90"
				}
			}, {
				"featureType": "road",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "shopping",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "scenicspots",
				"elementType": "labels",
				"stylers": {
					"visibility": "on"
				}
			}, {
				"featureType": "scenicspotslabel",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "manmade",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "manmade",
				"elementType": "labels",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "highwaysign",
				"elementType": "labels.icon",
				"stylers": {
					"visibility": "off"
				}
			}, {
				"featureType": "water",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#143e4700"
				}
			}, {
				"featureType": "road",
				"stylers": {
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"stylers": {
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"stylers": {
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"stylers": {
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "road",
				"elementType": "labels.text",
				"stylers": {
					"fontsize": "24"
				}
			}, {
				"featureType": "highway",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "highway",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#3c4e65ff"
				}
			}, {
				"featureType": "highway",
				"elementType": "geometry.stroke",
				"stylers": {
					"color": "#1c4f7eff"
				}
			}, {
				"featureType": "highway",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "highway",
				"elementType": "geometry",
				"stylers": {
					"weight": "3"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#3c4e65ff"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "geometry.stroke",
				"stylers": {
					"color": "#1c4f7eff"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "geometry",
				"stylers": {
					"weight": "3"
				}
			}, {
				"featureType": "provincialway",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#3c4e65ff"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#3c4e65ff"
				}
			}, {
				"featureType": "arterial",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#3c4e65ff"
				}
			}, {
				"featureType": "tertiaryway",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#3c4e65ff"
				}
			}, {
				"featureType": "fourlevelway",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#3c4e65ff"
				}
			}, {
				"featureType": "local",
				"elementType": "geometry.fill",
				"stylers": {
					"visibility": "on",
					"color": "#3c4e65ff"
				}
			}, {
				"featureType": "provincialway",
				"elementType": "geometry.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#1e2b3dff"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "geometry.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#1e2b3dff"
				}
			}, {
				"featureType": "arterial",
				"elementType": "geometry.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#1e2b3dff"
				}
			}, {
				"featureType": "tertiaryway",
				"elementType": "geometry.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#1e2b3dff"
				}
			}, {
				"featureType": "fourlevelway",
				"elementType": "geometry.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#1e2b3dff"
				}
			}, {
				"featureType": "local",
				"elementType": "geometry.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#1e2b3dff"
				}
			}, {
				"featureType": "local",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "local",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "fourlevelway",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "tertiaryway",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "arterial",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "provincialway",
				"elementType": "labels.text.fill",
				"stylers": {
					"visibility": "on",
					"color": "#2a4865ff"
				}
			}, {
				"featureType": "provincialway",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "arterial",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "tertiaryway",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "fourlevelway",
				"elementType": "labels.text.stroke",
				"stylers": {
					"visibility": "on",
					"color": "#959aa1ff",
					"weight": "1"
				}
			}, {
				"featureType": "fourlevelway",
				"elementType": "geometry",
				"stylers": {
					"weight": "1"
				}
			}, {
				"featureType": "tertiaryway",
				"elementType": "geometry",
				"stylers": {
					"weight": "1"
				}
			}, {
				"featureType": "local",
				"elementType": "geometry",
				"stylers": {
					"weight": "1"
				}
			}, {
				"featureType": "provincialway",
				"elementType": "geometry",
				"stylers": {
					"weight": "3"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "geometry",
				"stylers": {
					"weight": "3"
				}
			}, {
				"featureType": "arterial",
				"elementType": "geometry",
				"stylers": {
					"weight": "3"
				}
			}, {
				"featureType": "highway",
				"stylers": {
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"stylers": {
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"stylers": {
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"stylers": {
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "highway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"stylers": {
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"stylers": {
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"stylers": {
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"stylers": {
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "nationalway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "provincialway",
				"stylers": {
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "8-10"
				}
			}, {
				"featureType": "provincialway",
				"stylers": {
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "8-10"
				}
			}, {
				"featureType": "provincialway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "8-10"
				}
			}, {
				"featureType": "provincialway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "8-10"
				}
			}, {
				"featureType": "provincialway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "8-10"
				}
			}, {
				"featureType": "provincialway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "8-10"
				}
			}, {
				"featureType": "cityhighway",
				"stylers": {
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"stylers": {
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"stylers": {
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"stylers": {
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "6",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "7",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "8",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "cityhighway",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "6-9"
				}
			}, {
				"featureType": "arterial",
				"stylers": {
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "9-9"
				}
			}, {
				"featureType": "arterial",
				"elementType": "geometry",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "9-9"
				}
			}, {
				"featureType": "arterial",
				"elementType": "labels",
				"stylers": {
					"visibility": "off",
					"level": "9",
					"curZoomRegionId": "0",
					"curZoomRegion": "9-9"
				}
			}];
			map.setMapStyle({styleJson: myStyleJson });
		// map.setMapStyle(mapStyle);
		map.addControl(new window.BMap.NavigationControl());    
	
		
		// 复杂的自定义覆盖物
		
		var myIcon = new window.BMap.Icon(icon1, new window.BMap.Size(72,110))
		myIcon.setImageSize(new window.BMap.Size(90,90));//设置图标大小
		var myIcon1 = new window.BMap.Icon(icon3, new window.BMap.Size(72,110))
		myIcon1.setImageSize(new window.BMap.Size(90,90));//设置图标大小
		var myIcon2 = new window.BMap.Icon(icon10, new window.BMap.Size(72,110))
		myIcon2.setImageSize(new window.BMap.Size(90,90));//设置图标大小
		var myIcon3 = new window.BMap.Icon(icon11, new window.BMap.Size(72,110))
		myIcon3.setImageSize(new window.BMap.Size(90,90));//设置图标大小
		
		// var marker = new window.BMap.Marker(new window.BMap.Point(121.456694,29.864493),
		// {icon: myIcon});
		// 和义大道购物中心
		var marker1 = new window.BMap.Marker(new window.BMap.Point(121.561815,29.879922),
		{icon: myIcon});
		// 天一国际购物中心
		var marker2 = new window.BMap.Marker(new window.BMap.Point(121.560551,29.877176),
		{icon: myIcon3});	//和义大道购物中心
		// 太平鸟
		var marker3 = new window.BMap.Marker(new window.BMap.Point(121.408999,29.856561),
		{icon: myIcon2});	//天一国际购物中心
		// 杉山工厂
		var marker4 =new window.BMap.Marker(new window.BMap.Point(121.464836,29.841368),
		{icon: myIcon});	//太平鸟
		//杉井奥特莱斯广场
		var marker5 =new window.BMap.Marker(new window.BMap.Point(121.456694,29.864493),
		{icon: myIcon1});	//杉山工厂
		
		var marker6 =new window.BMap.Marker(new window.BMap.Point(121.631994,29.868575));
		// 宁波市诚凯箱包有限公司
		var marker7 =new window.BMap.Marker(new window.BMap.Point(121.394819,29.869262),
		{icon: myIcon});	
		
		// 宁波市诚凯箱包有限公司
		var marker8 =new window.BMap.Marker(new window.BMap.Point(121.395537,29.871117),
		{icon: myIcon});	
		
		
		
		// 在地图上添加点标记
		map.addOverlay(marker1);
		// 在地图上添加点标记
		// map.addOverlay(marker);
		map.addOverlay(marker1);
		map.addOverlay(marker2);
		map.addOverlay(marker3);
		map.addOverlay(marker4);
		map.addOverlay(marker5);
		map.addOverlay(marker6);
		map.addOverlay(marker7);
		marker7.onmouseover = function(e){
			// console.log(e.point,'12222222')
			// console.log(e)
			addMapDeviceMarker(e.point)
		}
		marker7.onmouseout = function(e){
			console.log(e.point,'12222222')
			// console.log(e)
			var allOverlay = map.getOverlays();
				for (var i = 0; i < allOverlay.length -1; i++){
					// console.log(allOverlay[i])
					if(allOverlay[i]._text == "宁波市诚凯箱包有限公司"){
						map.removeOverlay(allOverlay[i]);
						return false;
					}
				}
		
		}
		

		
		
      
	
	}
	render(){
		return(
			<div className="generating">
				<div className="headerimg">宁波市海曙区综合智慧零碳电厂</div>
				<div className="headers1">
					<div>
						<h4>日前响应</h4>
						<p>-</p>
					</div>
					<div>
						<h4>小时级响应</h4>
						<p>-</p>
					</div>
					<div>
						<h4>分钟级响应</h4>
						<p>-</p>
					</div>
					<div>
						<h4>秒级响应</h4>
						<p>-</p>
					</div>
					<div>
						<h4>智能调度负荷(MW)</h4>
						<p>6</p>
					</div>
					<div>
						<h4>碳减排量(t)</h4>
						<p>50.2</p>
					</div>
				</div>
				<div className="plant">
					<div className="plantleft">
						<div>
							<img src={require('../../style/electric/icon1.png')} />
							<ul>
								<li>
									<p>1</p>
									<span>储能电站数量</span>
								</li>
								<li>
									<p>0.8</p>
									<span>容量(MWh)</span>
								</li>
							</ul>
						</div>
						<div>
							<img src={require('../../style/electric/icon2.png')} />
							<ul>
								<li>
									<p>-</p>
									<span>光伏电站数量</span>
								</li>
								<li>
									<p>-</p>
									<span>装机容量(MW)</span>
								</li>
							</ul>
						</div>
						<div>
							<img src={require('../../style/electric/icon3.png')} />
							<ul>
								<li>
									<p>4</p>
									<span>负荷节点数量</span>
								</li>
								<li>
									<p>10</p>
									<span>容量(MW)</span>
								</li>
							</ul>
						</div>
					</div>
					<div className="plantright" id="container"></div>
					<div className="constructbuild">
						<ul>
							<li>已接入</li>
							<li>建设中</li>
						</ul>
						
					</div>
					<div className="cityname">宁波市</div>
				</div>
				<div className="station">
					<div className="stationtone stationtone1">
						<h4>今日发电及用能统计</h4>
						<ul>
							<li>
								<img src={require('../../style/electric/icon4.png')} />
								<p>60</p>
								<span>发电(kWh)</span>
							</li>
							<li>
								<img src={require('../../style/electric/icon5.png')} />
								<p>5.12</p>
								<span>用电(万kWh)</span>
							</li>
							<li>
								<img src={require('../../style/electric/icon6.png')} />
								<p>29.75</p>
								<span>碳排放(t)</span>
							</li>
						</ul>
					</div>
					<div className="stationtone1 stationtwo">
						<h4>用电月同比分析</h4>
						<ul>
							<li>
								<span>当月用电</span>
								<b>78.28万</b>
								kWh
							</li>
							<li>
								<span>上月同期</span>
								<b>80.18万</b>
								kWh
							</li>
							<li>
								<span>同期对比</span>
								<b>2.4</b>
								%
								<img src={require('../../style/electric/down.png')} />
							</li>
						</ul>
					</div>
					<div className="stationtone1">
						<h4>逐日坪效 （kWh / m² • 年）</h4>
						<div id="main"></div>
					</div>
				</div>
			</div>
		)
	}
}

export default generating