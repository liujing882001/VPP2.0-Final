import React, {
	useEffect,
	useState,
	useRef
} from 'react'
import './index.scss'
import * as echarts from 'echarts';
import http from '../../../../server/server.js'
import { Spin, message } from 'antd';

export const Incomeblock = ({
	incomeval,
	revenueindex,
	drawnlineindex,
	isIncomeblock
}) => {
	const [xlist, setXlist] = useState([])
	const [spinning,setSpinning] = useState(true)
	const divRef = useRef(null);
	const revenueAnalysis = () => {
		setSpinning(true)
		http.post('homePage/revenueAnalysis', {
			nodeId: incomeval,
			time: drawnlineindex === 1 ? 'month' : 'year'
		}).then(res => {
			if (res.data.code === 200) {
				
				// 
				
				if(drawnlineindex===1){  // 月
					
					if(revenueindex===1){
						// 收益
						let mouthRevenue = JSON.parse(res?.data?.data?.mouthRevenue)
						if (mouthRevenue&&mouthRevenue.length>0) {
							let xlist = []
							let dynamiclist = []
							let Yieldlist = []
							let fixedlist = []
							let upliftlist = []
							mouthRevenue?.sort(function(a, b) {
							    return new Date(a.time) - new Date(b.time);
							});
							mouthRevenue?.map(res => {
								
								xlist.push(res.time)
								dynamiclist.push(res.dynamic)
								fixedlist.push(res.fixed)
							})
							
							income(xlist, dynamiclist, fixedlist,revenueindex,revenueindex?true:false)
						} else {
							income([], [], [],revenueindex,false)
						}
					}else{
						let mouthCount = JSON.parse(res?.data?.data?.mouthCount)
						if (mouthCount&&mouthCount.length>0) {
							let xlist = []
							let dynamiclist = []
							let Yieldlist = []
							let fixedlist = []
							let upliftlist = []
							mouthCount?.sort(function(a, b) {
							    return new Date(a.time) - new Date(b.time);
							});
							mouthCount?.map(res => {
								
								xlist.push(res.time)
								dynamiclist.push(res.dynamic)
								fixedlist.push(res.fixed)
							})
							
							income(xlist, dynamiclist, fixedlist,revenueindex,mouthCount?true:false)
						} else {
							income([], [], [],revenueindex,false)
						}
					}
					
				}else{
					if(revenueindex===1){
						let yearRevenue = JSON.parse(res?.data?.data?.yearRevenue)
						if (yearRevenue&&yearRevenue.length>0) {
							let xlist = []
							let dynamiclist = []
							let Yieldlist = []
							let fixedlist = []
							let upliftlist = []
							yearRevenue?.sort(function(a, b) {
							    return new Date(a.time) - new Date(b.time);
							});
							yearRevenue?.map(res => {
								
								xlist.push(res.time)
								dynamiclist.push(res.dynamic)
								fixedlist.push(res.fixed)
							})
							income(xlist, dynamiclist, fixedlist,revenueindex,yearRevenue?true:false)
						} else {
							income([], [], [],revenueindex,false)
						}
					}else{
						let yearCount = JSON.parse(res?.data?.data?.yearCount)
						if (yearCount&&yearCount.length>0) {
							let xlist = []
							let dynamiclist = []
							let Yieldlist = []
							let fixedlist = []
							let upliftlist = []
							yearCount?.sort(function(a, b) {
							    return new Date(a.time) - new Date(b.time);
							});
							yearCount?.map(res => {
								
								xlist.push(res.time)
								dynamiclist.push(res.dynamic)
								fixedlist.push(res.fixed)
							})
							income(xlist, dynamiclist, fixedlist,revenueindex,yearCount?true:false)
						} else {
							income([], [], [],revenueindex,false)
						}
					}
					
				}
				
				setSpinning(false)
				
			}else{
				// message.info(res.data.msg)
				income([], [], [],false)
				setSpinning(false)
			}
		}).catch(err => {
			console.log(err)
		})
	}
	useEffect(() => {
		if (incomeval && drawnlineindex&&isIncomeblock) {
			revenueAnalysis()
		}
		if(!isIncomeblock){
			income([], [], [],false)
		}
	}, [incomeval, drawnlineindex,revenueindex,isIncomeblock])
	useEffect(() => {
	    const resizeObserver = new ResizeObserver(entries => {
	      for (let entry of entries) {
	        const { width } = entry.contentRect;
			var myChart = echarts.init(document.getElementById('Incomemain'));
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
	const income = (xlist, dynamiclist, fixedlist,revenueindex,isEmpty) => {
		var chartDom = document.getElementById('Incomemain');
		var myChart = echarts.init(chartDom);
		
		var option;
		option = {
			grid: {
				left: 40,
				right: 0,
				bottom: 26,
				top: 70
			},
			legend: {
				
				data: revenueindex===1?[
					{
						name: '储能动态策略收益',
						itemStyle: {
							color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
									offset: 0,
									color: "rgba(10, 186, 181, 1)", // 起始颜色
								},
								{
									offset: 1,
									color: "rgba(255, 255, 255, 0)", // 结束颜色
								},
							])
						},
					}
					// ,
					// {
					// 	name: '储能固定策略收益',
					// 	itemStyle: {
					// 		color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
					// 				offset: 0,
					// 				color: "rgba(22, 92, 255, 1)", // 起始颜色
					// 			},
					// 			{
					// 				offset: 1,
					// 				color: "rgba(255, 255, 255, 0)", // 结束颜色
					// 			},
					// 		])
					// 	}

					// }
				]:revenueindex===2?[
					{
						name: '储能动态策略充放电次数',
						itemStyle: {
							color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
									offset: 0,
									color: "rgba(10, 186, 181, 1)", // 起始颜色
								},
								{
									offset: 1,
									color: "rgba(255, 255, 255, 0)", // 结束颜色
								},
							])
						},
					}
					// ,
					// {
					// 	name: '储能固定策略充放电次数',
					// 	itemStyle: {
					// 		color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
					// 				offset: 0,
					// 				color: "rgba(22, 92, 255, 1)", // 起始颜色
					// 			},
					// 			{
					// 				offset: 1,
					// 				color: "rgba(255, 255, 255, 0)", // 结束颜色
					// 			},
					// 		])
					// 	}

					// }
				]:[],
				left: 'right',
				top: 10,
				itemHeight: 6,
				itemWidth: 6,
				itemStyle: {
					margin: 10,
					borderRadius: 5,
					color: '#FFF'
				},
				textStyle: {
					color: "#fff",
					fontSize: 12,
					// lineHeight:10,
					// padding:[-1,0,0,0]
				},
			},
			xAxis: {
				type: 'category',
				data: xlist,
				axisLabel: {
					formatter: '{value}',
					color: '#FFF',
					fontSize:12,
					interval:xlist.length>5? 3:0
				},
				
				
				
			},
			tooltip: {
				trigger: 'axis',
				backgroundColor: '#302F39',
				borderColor: 'transparent',
				textStyle: {
					color: '#fff' 
				},
				position: 'top',
				axisPointer: {
					type: 'cross',
					snap: true ,
					label: {
						backgroundColor: '#6a7985'
					}
				},
			},
			graphic: {
			    elements:[
					{
						type: 'image',
						z: 100,
						left: 'center',
						top: 'middle',
						style: {
							image:require('../../img/null.png')  ,
							// image: 'https://example.com/image.jpg',
							width: 76,
							height: 60
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
			yAxis: [

				{
					type: 'value',
					name: revenueindex===1?'元':'',
					nameTextStyle: {
						color: "#fff",
						fontSize: 10,
						padding: [0, 0, 0, -25]
					},
					splitLine: {
						show: true,
						lineStyle: {
							type: 'dotted',
							color: 'rgba(255, 255, 255, 0.20)'
						}
					},
					axisLabel: {
						color: '#FFF',
						fontSize:10
						// formatter: '{value} °C'
					},
					
				}

			],
			series: [{
					type: 'bar',
					barWidth:10,
					itemStyle: {
						// 使用线性渐变颜色
						color: new echarts.graphic.LinearGradient(
							0, 0, 0, 1, // 渐变的方向，从上到下
							[{
									offset: 0,
									color: '#0ABAB5'
								}, // 0% 处的颜色
								{
									offset: 0.3,
									color: 'rgba(10, 186, 181, .5)'
								}, // 50% 处的颜色
								{
									offset: 0.5,
									color: 'rgba(10, 186, 181, .2)'
								}, // 50% 处的颜色
								{
									offset: 0.6,
									color: 'rgba(10, 186, 181, .1)'
								}, // 50% 处的颜色
								{
									offset: 0.6,
									color: 'rgba(10, 186, 181, .1)'
								}, // 50% 处的颜色
								{
									offset: 1,
									color: 'rgba(10, 186, 181, 0)'
								} // 100% 处的颜色
							]
						)
					},

					data: dynamiclist,
					name: revenueindex===1?'储能动态策略收益':'储能动态策略充放电次数'
				},
				// {
				// 	type: 'bar',
				// 	name:  revenueindex===1?'储能固定策略收益':'储能固定策略充放电次数',
				// 	barWidth:10,
				// 	itemStyle: {
				// 		// 使用线性渐变颜色
				// 		color: new echarts.graphic.LinearGradient(
				// 			0, 0, 0, 1, // 渐变的方向，从上到下
				// 			[{
				// 					offset: 0,
				// 					color: 'rgba(22, 92, 255, 1)'
				// 				}, // 0% 处的颜色
				// 				{
				// 					offset: 0.3,
				// 					color: 'rgba(22, 92, 255, .5)'
				// 				}, // 50% 处的颜色
				// 				{
				// 					offset: 0.5,
				// 					color: 'rgba(22, 92, 255, .2)'
				// 				}, // 50% 处的颜色
				// 				{
				// 					offset: 0.6,
				// 					color: 'rgba(22, 92, 255, .1)'
				// 				}, // 50% 处的颜色
				// 				{
				// 					offset: 0.6,
				// 					color: 'rgba(22, 92, 255, .1)'
				// 				}, // 50% 处的颜色
				// 				{
				// 					offset: 1,
				// 					color: 'rgba(22, 92, 255, 0)'
				// 				} // 100% 处的颜色
				// 			]
				// 		)
				// 	},
				// 	// yInde
				// 	data: fixedlist
				// },
				
			]
		};

		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
	}
	return ( 
		<div className="Income-block-content">
		<Spin spinning={spinning}>
		       <div className = "Income-block" id = "Incomemain"  ref={divRef}> </div>
		</Spin>
			
		</div>
	)
}