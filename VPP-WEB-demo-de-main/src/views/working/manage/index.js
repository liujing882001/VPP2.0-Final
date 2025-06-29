import React,{Component} from 'react'
import { Space,ConfigProvider,DatePicker,Button,Select,Input ,Table } from 'antd';
import * as echarts from 'echarts';
// import echarts  from '../../echarts.js'

import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import './index.css'




const { Option } = Select;
class manage extends Component {
	constructor(props) {
		super(props)
		this.state={
			current:0,
			currentState:0
		}
	}
	componentDidMount() {
		this.tabFn(1)
		this.element()
		this.house()
		this.factory()
	}
	tabFn(index) {
		console.log(index)
		this.setState({
			current: index,
			dateType:index
		})
	}
	clsFn(index, curCls, cls) {
		let { current } = this.state;
		// console.log(current)
		return current === index ? curCls : cls;
	}
	handleChange =(val) =>{
		console.log(val)
	}
	onChange = (e) =>{
		
	}
	onChangemonth = (e) =>{
		
	}
	onChangemonth = (e) =>{
		
	}
	element(){
		var chartDom = document.getElementById('element');
		var myChart = echarts.init(chartDom);
		var option;
		
		option = {
			color: ['rgba(0, 191, 143, 0.5)','rgba(255, 207, 64, 0.5)'],
		  tooltip: {
		    trigger: 'axis',
		    axisPointer: {
		      type: 'cross',
		      crossStyle: {
		        color: '#999'
		      }
		    },
			
		  },
		 grid:{
			 top:10,
			 bottom:60,
			 left:'5%',
			 right:'5%'
		 },
		  legend: {
		    data: ['充电', '放电'],
			textStyle:{
				color:'#FFFFFF'
			},
			bottom:0
			
		  },
		  xAxis: [
		    {
		      type: 'category',
		      data: ['1', '2', '3', '4', '5', '6'],
		      axisPointer: {
		        type: 'shadow'
		      },
			  axisLabel:{//x坐标轴刻度标签
			  	show:true,
			  	color:'#FFF',//'#ccc'，设置标签颜色
			  	formatter: `{value}月`
			  },
		    }
		  ],
		  yAxis: [
		    {
		      type: 'value',
		      // name: 'Precipitation',
		      min: 0,
		      max: 250,
		      interval: 50,
		      axisLabel: {
		        formatter: '{value}',
				color:'#FFFFFF'
		      },
			  splitLine:{
			  	show:true,
			  	lineStyle:{
			  		type:'dashed',
			  		color:'rgba(255, 255, 255, 0.2)'
			  	}
			  },
			  
		    },
		    
		  ],
		  series: [
		    {
		      name: '充电',
		      type: 'bar',
		      tooltip: {
		        valueFormatter: function (value) {
		          return value + ' kWh';
		        }
		      },
			  barWidth : 26,
			  itemStyle: {
				normal: {
					barBorderRadius:[4, 4, 0, 0],
					borderWidth:1,
					borderColor:"#00BF8F",
				}
			  },
				data: [
					20, 49, 70, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 64, 33
				],
			  
		    },
		    {
		      name: '放电',
		      type: 'bar',
		      tooltip: {
		        valueFormatter: function (value) {
		          return value + ' kWh';
		        }
		      },
			  itemStyle: {
				  normal: {
					  barBorderRadius:[4, 4, 0, 0],
					  borderWidth:1,
					  borderColor:"#FFCF40",
				  }
			  },
			   barWidth : 26,
		      data: [
		        200, 59, 90, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 188, 60, 23
		      ]
		    },
		    
		  ]
		};
		
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})

	}
	house(){
		var chartDom1 = document.getElementById('house');
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
					var p = Math.round(((tarValue / total) * 100));
					return `${name}     ${v}kW`;
				},
				icon:'circle',
				orient: 'vertical',
			    right: '20%',
				top:50,
				textStyle:{
					color:'#FFFFFF'
				}
			},
			series: [
			    {
					name: '负荷占比',
					type: 'pie',
					radius: '80%',
					center: ['25%', '50%'],
					labelLine: {
						show: false
					},
					label: {
						show: false,
						position: 'center'
					},
					data: [
						{ value: 3004, name: '空调' },
						{ value: 2000, name: '照明' },
						{ value: 1600, name: '电动汽车' },
						{ value: 1200, name: '充电桩' },
						{ value: 1200, name: '其它' }
					],
					label: {
						normal: {
							show: true,
							position: 'inner', // 数值显示在内部
							formatter: '{d}%', // 格式化数值百分比输出
							color:'#FFFFFF'
						},
					},
					emphasis: {
						itemStyle: {
							shadowBlur: 10,
							shadowOffsetX: 0,
							shadowColor: 'rgba(0, 0, 0, 0.5)'
						}
					  
					}
			    }
			]
		}
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		
	}
	factory(){
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
					var p = Math.round(((tarValue / total) * 100));
					return `${name}  ${v}个`;
				},
				icon:'circle',
				orient: 'vertical',
			    right: '20%',
				top:50,
				textStyle:{
					color:'#FFFFFF'
				}
			},
			series: [
			    {
					name: 'Access From',
					type: 'pie',
					radius: '80%',
					center: ['25%', '50%'],
					labelLine: {
						show: false
					},
					label: {
						show: false,
						position: 'center'
					},
					data: [
						{ value: 10, name: '空调' },
						{ value: 2, name: '照明' },
						{ value: 4, name: '电动汽车' },
						{ value: 5, name: '充电桩' },
						{ value: 3, name: '其它' }
					],
					label: {
						normal: {
							show: true,
							position: 'inner', // 数值显示在内部
							formatter: '{d}%', // 格式化数值百分比输出
							color:'#FFFFFF'
						},
						
						
					},
					emphasis: {
						itemStyle: {
							shadowBlur: 10,
							shadowOffsetX: 0,
							shadowColor: 'rgba(0, 0, 0, 0.5)'
						},
						
				    
					}
			      
			    }
			]
		}
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})
	}
	
	
	render(){
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current && current > dayjs().endOf('day');
		};
		return(
			<div className="allcontent">
				<div className="manageheader">
					<Select defaultValue="铜牛大厦" style={{ width: 150 }} onChange={this.handleChange}>
					    <Option value="jack">铜牛大厦</Option>
					</Select>
					<div className='tab_con3'>
							<ol className='abb'>
								<li onClick={() => { this.tabFn(1) }} className={this.clsFn(1, 'cur3', 'cur31')}>年</li>
								<li onClick={() => { this.tabFn(2) }} className={this.clsFn(2, 'cur3', 'cur32')}>月</li>
								<li onClick={() => { this.tabFn(3) }} className={this.clsFn(3, 'cur3', 'cur33')}>日</li>
							</ol>
							 
							<ul className='acc'>
								<li className={this.clsFn(1, 'current', '')}>
									
									<ConfigProvider locale={locale}>
									    <DatePicker disabledDate={disabledDate} disabled  onChange={this.onChangeyear} picker="year" />
									</ConfigProvider>
								</li>
									
								<li className={this.clsFn(2, 'current', '')}>
									<ConfigProvider locale={locale}>
										<DatePicker disabledDate={disabledDate} disabled  onChange={this.onChangemonth} picker="month" />
									</ConfigProvider>
									
								</li>
								<li className={this.clsFn(3, 'current', '')}>
									<ConfigProvider locale={locale}>
										<DatePicker disabledDate={disabledDate} disabled  onChange={this.onChange} />
									</ConfigProvider>
														
								</li>
							</ul>
							
					</div>
					
				</div>
				<div className="Distributed">
					<h4>分布式储能实时运行状态</h4>
					<div className="manage">
						<div className="hadoop">
							<div className="quantity1">
								
							</div>
							<ul className="hadooplist">
								<li>
									<h4>电池容量(kWh)</h4>
									<b>1000 </b>
								</li>
								<li>
									<h4>电池功率(kW)</h4>
									<b>500</b>
								</li>
								<li>
									<h4>SOH</h4>
									<b>99.33%</b>
								</li>
								<li>
									<h4>SOC</h4>
									<b>60.32%</b>
								</li>
							</ul>
						</div>
						<div className="hadoop">
						<div className="quantity1">
							<img src={require('../../../style/img/warn.png')}  />
							报警数量
							<span>24</span>
						</div>
							<ul className="hadooplist warnhadooplist">
								<li>
									<h4>紧急</h4>
									<b>1</b>
								</li>
								<li>
									<h4>重要</h4>
									<b>4</b>
								</li>
								<li>
									<h4>次要</h4>
									<b>10</b>
								</li>
								<li>
									<h4>提示</h4>
									<b>9</b>
								</li>
							</ul>
						</div>
					</div>
					<div className="battery">
						<div>
							<img src={require('../../../style/img/lv.png')}  />
							<b>1,394,777</b><br />
							<span>电池总充电量（kWh）</span>
						</div>
						<div>
							<img src={require('../../../style/img/huang.png')}  />
							<b>1,258,646</b><br />
							<span>电池总放电量（kWh）</span>
						</div>
					</div>
					<div id="element"></div>
				</div>
				<div className="cell">
					<h4>光伏资源</h4>
					<div className="manage">
						<div className="hadoop">
							<div className="quantity1">
								
							</div>
							<ul className="hadooplist">
								<li>
									<h4>发电量(kWh)</h4>
									<b>600</b>
								</li>
								<li>
									<h4>满发小时数(h)</h4>
									<b>1200</b>
								</li>
								<li>
									<h4>实时功率(kW)</h4>
									<b>2600</b>
								</li>
								<li>
									<h4>装机容量(kW)</h4>
									<b>600</b>
								</li>
							</ul>
						</div>
						<div className="hadoop">
						<div className="quantity1">
							<img src={require('../../../style/img/warn.png')}  />
							报警数量
							<span>24</span>
						</div>
							<ul className="hadooplist warnhadooplist">
								<li>
									<h4>紧急</h4>
									<b>1</b>
								</li>
								<li>
									<h4>重要</h4>
									<b>4</b>
								</li>
								<li>
									<h4>次要</h4>
									<b>10</b>
								</li>
								<li>
									<h4>提示</h4>
									<b>9</b>
								</li>
							</ul>
						</div>
					</div>
				</div>
				<div className="load">
					<h4>可调负荷</h4>
					<div className="loadcontent">
						<div className="stations">
							<div>
								<b>11394</b><br />
								<span>总可调负荷（kW）</span>
							</div>
							<img src={require('../../../style/img/yellow.png')}  />
						</div>
						<div className="stations">
							<div>
								<b>9094</b><br />
								<span>实时可调负荷（kW）</span>
							</div>
							<img src={require('../../../style/img/bo.png')}  />
						</div>
					</div>
					<div className="building" style={{marginTop:'60px'}}>
						<div className="storied">
							<div className="title">
								<img src={require('../../../style/img/fu.png')}  />
								负荷占比
							</div>
							
							<div id="house"></div>
						</div>
						<div className="storied">
							<div className="title">
								<img src={require('../../../style/img/warn.png')}  />
								报警数量 <span className="number">24</span>
							</div>
							
							<div id="factory"></div>
						</div>
					</div>
				</div>
			</div>
		)
	}
	
}


export default manage

























