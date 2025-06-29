import React,{Component} from 'react'
import './endPV.css'
import * as echarts from 'echarts';
// import echarts  from '../echarts.js'
import {BrowserRouter as Router, Route, Link,withRouter} from 'react-router-dom'

import electricity from '../charge/electricity/index.js'
class endPV extends Component {
	constructor(props) {
		super(props)
		this.state={
			finalArray:[]
		}
	}
	componentDidMount(){
		
		const initialValue = 10;
		const endValue = 1000;
		const finalArray =[40,60,80,100,120,130,140,150,160,165,170,
						180,190,210,220,230,240,250,260,270,280,290,300,
						310,330,340,355,370,380,390,400,405,410,415,420,425,430,435,440,445,
						450,460,468,470,475,480,485,488]
		
		// for (let n = initialValue; n <= endValue; n += 20) {
		// 	finalArray.push(n);
		// }
		this.setState({
			// finalArray:finalArray
		},() =>{
			this.statement()
		})
		console.log(finalArray.reverse());
	}
	chakan =() =>{
		this.props.history.push('/electricity')
	}
	// 图表
	statement(){
		var chartDom = document.getElementById('statement');
		var myChart = echarts.init(chartDom);
		
		myChart.setOption({
			title: {
			    text: '光伏发电功率（kW）',
				x:30,
				y:20,
				textStyle: { 
					fontSize: 12,
					color:'#2A2B40',
					
				},
				
			},
			color: ['#A276FF', '#00D7E9','#1890FF'],
			  tooltip: {
				trigger: 'axis',
				axisPointer: {
					type: 'cross',
					label: {
						backgroundColor: '#6a7985'
					}
				},
				formatter: function (params) {
					var relVal =params[0].name;
					// console.log(params)
					for (var i = 0;i < params.length; i++) {
						relVal += '<br />' +
						params[i].seriesName+ '：' + parseFloat(params[i].value).toFixed(2)+'kW'
						
					}
					return relVal;
				}
				// formatter:function(datas) {
				// 	console.log(datas)
				// 	for(var i=0;i<datas.length;i++){
				// 		return datas[0].date.toFixed(2)
				// 	}
				// 	// return datas;
				// 	//或者是下面这种，效果是一样的
				// 	// return datas.value.toFixed(2);
				// }
			},
			grid: {
				top:60,
				left: '3%',
				right: 29,
				bottom: 40,
				containLabel: true
			},
			legend: {
			    data: ['AI赋能预测功率', '行业平均预测功率','实际发电功率'],
				textStyle: {
					color: '#2A2B40',
					fontSize: 12,
					padding: [0, 0, 0, 10]
				},
				bottom:0,
				icon:'circle',

			  },
			xAxis: [
				{
					type: 'category',
					boundaryGap: false,
					// data:this.state.echartsdate,
					data: ['00:00','00:15','00:30','00:45',
					'01:00','01:15','01:30','01:45',
					'02:00','02:15','02:30','02:45',
					'03:00','03:15','03:30','03:45',
					'04:00','04:15','04:30','04:45',
					'05:00','05:15','05:30','05:45',
					'06:00','06:15','06:30','06:45',
					'07:00','07:15','07:30','07:45',
					'08:00','08:15','08:30','08:45',
					'09:00','09:15','09:30','09:45',
					'10:00','10:15','10:30','10:45',
					'11:00','11:15','11:30','11:45',
					'12:00','12:15','12:30','12:45',
					'13:00','13:15','13:30','13:45',
					'14:00','14:15','14:30','14:45',
					'15:00','15:15','15:30','15:45',
					'16:00','16:15','16:30','16:45',
					'17:00','17:15','17:30','17:45',
					'18:00','18:15','18:30','18:45',
					'19:00','19:15','19:30','19:45',
					'20:00','20:15','20:30','20:45',
					'21:00','21:15','21:30','21:45',
					'22:00','22:15','22:30','22:45',
					'23:00','23:15','23:30','23:45'],
					axisLabel:{//x坐标轴刻度标签
						  show:true,
						  color:'#2A2B40',//'#ccc'，设置标签颜色
						  formatter: `{value}`
					  },
					  
				}
			],
			  yAxis: [{
					type: 'value',
					max: 1000,
					boundaryGap: [0, '100%'],
					axisLabel:{//x坐标轴刻度标签
						show:true,
						color:'#2A2B40',//'#ccc'，设置标签颜色
					},
					formatter:function(value,index){
						console.log(value)
						return value.toFixed(2); 
					},
					splitLine:{
						show:true,
						lineStyle:{
							type:'dashed',
							color:'#DFE1E5'
						}
					}
					
				}],

				series: [
					{
						name: 'AI赋能预测功率',
						type: 'line',
						// stack: 'Total',
						// smooth: true,
						sampling: 'average',
						large: true,
						lineStyle: {
							width: 1,
							color:'#A276FF'
						},
						showSymbol: false,
						areaStyle: {
							opacity: 0.8,
							color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
								{
									offset: 0,
									color: 'rgba(162,118,255,0.5)'
								},
								{
									offset: 1,
									color:'rgba(162,118,255,0)'
									// color: 'rgba(8, 205, 191, 0)'
								}
							])
						},
						emphasis: {
							focus: 'series'
						},
						// data:this.state.aiTimePrice
						// data:[160,180,200,220,240,260,280,300,320,340,360,380,400,420,450,470,500,520,
						// 550,570,590,610,630,650,670,690,
						// 710,730,740,755,770,780,790,800,805,810,815,820,825,830,835,840,845,
						// 850,860,868,870,875,880,885,888,890, 888, 885, 875,868, 860, 850, 845, 840, 835, 830,
						//  825, 820, 815, 810, 805, 800, 790, 780, 770, 755, 740, 730, 710, 690, 
						//  670, 650, 630, 610, 590, 570, 550, 520, 500, 470, 450, 420, 400, 380, 
						// 360, 340, 320, 300, 280, 260, 240, 220, 200, 180, 160, 130, 100],
						data:[ 40,60,80,100,120,130,140,150,160,165,170,
						180,190,210,220,230,240,250,260,270,280,290,300,
						310,330,340,355,370,380,390,400,405,410,415,420,425,430,435,440,445,
						450,460,468,470,475,480,485,488, 485, 480, 475, 470, 468, 460, 450,
						 445, 440, 435, 430, 425, 420, 415, 410, 405, 400, 390, 380, 370, 355,
						  340, 330, 310, 300, 290, 280, 270, 260, 250, 240, 230, 220, 210, 190, 
						180, 170, 165, 160, 150, 140, 130, 120, 100, 80, 60, 40]
					},
					
					{
						name: '行业平均预测功率',
						type: 'line',
						// stack: 'Total',
						// smooth: true,
						sampling: 'average',
						large: true,
						lineStyle: {
							width: 1,
							color:'#00D7E9'
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
						// data:this.state.aiTimePrice
						
						data:[160,180,200,220,240,260,280,300,320,340,360,380,400,420,450,470,500,520,
						550,570,590,610,630,650,670,690,
						710,730,740,755,770,780,790,800,805,810,815,820,825,830,835,840,845,
						850,860,868,870,875,880,885,888,890, 888, 885, 875,868, 860, 850, 845, 840, 835, 830,
						 825, 820, 815, 810, 805, 800, 790, 780, 770, 755, 740, 730, 710, 690, 
						 670, 650, 630, 610, 590, 570, 550, 520, 500, 470, 450, 420, 400, 380, 
						360, 340, 320, 300, 280, 260, 240, 220, 200, 180, 160, 130, 100],
					},
					{
					name: '实际发电功率',
					type: 'line',
					// stack: 'Total',
					// smooth: true,
					sampling: 'average',
					large: true,
					lineStyle: {
						width: 1,
						color:'#1890FF'
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
						// data:this.state.activePower
						// data:[ 4,6,8,10,20,30,40,50,60,65,70,
						// 80,90,110,120,130,140,150,160,170,180,190,200,
						// 210,230,240,255,270,280,290,300,305,310,315,320,325,330,335,340,345,
						// 350,360,368,370,375,380,385,388, 385, 380, 375, 370, 368, 360, 
						// 350, 345, 340, 335, 330, 325, 320, 315, 310, 305, 300, 290, 280, 
						// 270, 255, 240, 230, 210, 200, 190, 180, 170, 160, 150, 140, 130, 120,
						//  110, 90, 80, 70, 65, 60, 50, 40, 30, 20, 10, 8, 6, 4]
						data:[60,80,100,120,140,160,180,200,210,230,250,270,280,
						290,300,310,320,330,340,350,360,370,380,390,400,
						410,430,440,455,470,480,490,500,505,510,515,520,525,530,535,540,545,
						550,560,568,570,575,580,585,588, 585, 580, 575, 570, 
						568, 560, 550, 545, 540, 535, 530, 525, 520, 515, 510, 505, 500, 
						490, 480, 470, 455, 440, 430, 410, 400, 390, 380, 370, 360, 350,
						340, 330, 320, 310, 300, 290, 280, 270, 
						250, 230, 210, 200, 180, 160, 140, 120, 100, 80, 60],
					},
					
				]
		});
		myChart.hideLoading()
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		
	
	}
	render(){
		return (
		<Router>
			<div>
				<div className="endPV">
					<h4>光伏发电功率预测算法
						<span onClick={this.chakan}>查看光伏资源  ></span>
					</h4>
					<img src={require('../../style/damao/imgs1.png')} />
				</div>
				<div className="pVcapacity">
					<div className="pvstatement">
						<h4>功率对比</h4>
						<div id="statement"></div>
					</div>
					<div className="accurate">
						<h4>准确率</h4>
						<ul className="accurate">
							<li>
								<p>AI赋能</p>
								<img src={require('../../style/imgs/zi.png')} />
								<span>96%</span>
							</li>
							<li>
								<p>行业平均</p>
								<img src={require('../../style/imgs/lv.png')} />
								<span>90%</span>
							</li>
						</ul>
					</div>
				</div>
				<Route path="/electricity" component={electricity} />
			</div>
		</Router>
		)
	}
}

// export default endPV
export default withRouter(endPV);