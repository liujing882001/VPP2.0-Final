import React,{Component} from 'react'
import { Table ,DatePicker ,ConfigProvider} from 'antd';
import * as echarts from 'echarts';
// import echarts  from '../echarts.js'
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';

import './index.css'
import { createHashHistory } from "history";
 
 const history = createHashHistory();
const { RangePicker } = DatePicker;

const dataSource = [
  {
    key: '1',
    name: '20220510计划发布',
    age: '20220510计划发布',
    address: '2022-5-17 11:00~13:00',
	volume:'5000',
	price:2,
	reality:'500',
	tactics:'AI智能调度',
	income:10000
  },
  {
    key: '2',
    name: '20220512计划发布',
    age: '20220510计划发布',
    address: '2022-5-19 11:00~13:00',
  	volume:'25000',
  	price:1.8,
  	reality:'20000',
  	tactics:'AI智能调度',
  	income:36000,
  },
];

const columns = [
	{
	        title: '编号',
	        width: '5%',
	        render:(text,record,index)=> `${index+1}`
	      },

  
  {
    title: '响应事件',
    dataIndex: 'age',
    key: 'age',
  },
  {
    title: '需求响应时间段',
    dataIndex: 'address',
    key: 'address',
  },
  {
    title: '响应容量（kW）',
    dataIndex: 'volume',
    key: 'volume',
  },
  {
    title: '响应价格（元/kWh）',
    dataIndex: 'price',
    key: 'price',
  },
  {
    title: '实际响应电量（kWh）',
    dataIndex: 'reality',
    key: 'reality',
  },
  {
    title: '响应策略',
    dataIndex: 'tactics',
    key: 'tactics',
  },
  {
    title: '收益（元）',
    dataIndex: 'income',
    key: 'income',
  },
];
class respond extends Component {
	constructor(props) {
		super(props)
		this.state={
			
		}
	}
	componentDidMount(){
		this.participation()
		this.line()
	}

	participation(){
		var myChart = echarts.init(document.getElementById('participation'));
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		myChart.setOption({
		    series: [
		        {
		        type: 'gauge',
				radius:'100%' ,
				startAngle: 90,
		        endAngle: -270,
		        min: 0,
		        max: 100,
		        splitNumber: 0,
		        itemStyle: {
					// show:false,
		            color: '#44D7B6',
		            shadowColor: '#000',
		            shadowBlur: 5,
		            shadowOffsetX: 2,
		            shadowOffsetY: 2
		        },
		        progress: {
		            show: true,
		            roundCap: true,
		            width: 10
		        },
		        pointer: {
					show:false,
		        },
		
				axisLine: {
					lineStyle: {
						width: 10,
						color: [[1, 'rgba(0,0,0,0.7)']],
						shadowColor: 'rgba(0, 0, 0, 0.5)',
						shadowBlur: 15
					}
				},
		        axisTick: {
					show:false,
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
		              color: '#FFF'
		            }
		        },
		        axisLabel: {
					show:false
		            
		        },
		        detail: {
		            width: '80%',
		            borderRadius: 8,
		            offsetCenter: [0, '35%'],
		            valueAnimation: true,
		            formatter: function (value) {
		              return '{value|' + value + '%}\n{unit|参与率}';
		            },
		            rich: {
						value: {
							fontSize: 18,
							color: '#FFFFFF',
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
						value: 80
		            }
		          ]
		        }
		      ]
		});
	}
	line(){
		var myChart = echarts.init(document.getElementById('line'));
		window.addEventListener('resize', function() {
			myChart.resize()
		})
		myChart.setOption({
			title: {
			    text: '单位：kW',
				textStyle:{
					color:'#FFFFFF',
					fontSize:14
				},
			  },
			  color:['#F05887','#44D7B6'],
			  tooltip: {
			    trigger: 'axis',
				axisPointer: {
					type: 'cross',
					  // crossStyle: {
					  //   color: '#999'
					  // }
				}
				
			  },
			  legend: {
			    data: ['基线负荷', '实际负荷'],
				icon: 'circle',
				textStyle: {
				        color: '#FFFFFF'
				    },
				left:100
			  },
			  grid: {
			    left: '3%',
			    right: '8%',
			    bottom: '3%',
			    containLabel: true
			  },
			
			xAxis: {
			    type: 'category',
			    boundaryGap: false,
			    data: ['2022-05-20 00:00', '2022-05-20 06:00', '2022-05-20 08:00', 
				'2022-05-20 12:00', '2022-05-20 15:00', '2022-05-20 18:00'],
				axisLabel:{//x坐标轴刻度标签
					show:true,
					color:'#FFF',//'#ccc'，设置标签颜色
					formatter : function(params){
					   var newParamsName = "";// 
						var paramsNameNumber = params.length;// 
						var provideNumber = 10;// 
						var rowNumber = Math.ceil(paramsNameNumber / provideNumber);//
						if (paramsNameNumber > provideNumber) {
							/** 循环每一行,p表示行 */
							for (var p = 0; p < rowNumber; p++) {
								var tempStr = "";// 
								var start = p * provideNumber;// 
								var end = start + provideNumber;// 
								if (p == rowNumber - 1) {
									tempStr = params.substring(start, paramsNameNumber);
								} else {
									// 
									tempStr = params.substring(start, end) + "\n";
								}
								newParamsName += tempStr;// 
							}
			
						} else {
							newParamsName = params;
						}
						return newParamsName
					}
				},
			},
			  yAxis: {
			    type: 'value',
				max: 800,
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
			  series: [
			    {
			      name: '基线负荷',
			      type: 'line',
			      // stack: 'Total',
			      data: [240, 320, 440, 780, 785, 790],
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
			      data: [240, 280, 430, 750, 520, 330]
			    }
			  ]
		})
		
		
		
		myChart.on('click', function (param) {
			console.log(param, param.data);//这里根据param填写你的跳转逻辑
		   
		});
	}
	render(){
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current && current > dayjs().endOf('day');
		};
		return(
			<div className="allcontent">
				<div className="respondheader">
					<h4>需求响应计划及策略</h4>
					<Table dataSource={dataSource} columns={columns} />
					
				</div>
				<div className="respondheader">
					<h4>可调负荷<span>* 基于AI智能调度，可调负荷资源自动上报</span></h4>		
					<div className="respond">
						<div className="subscriber">
							<img src={require('../../style/img/icons3.png')}  />
							<div>
								<span>100</span>
								<p>用户总数</p>
							</div>
						</div>
						<div className="subscriber">
							<div>
								<span>80</span>
								<p>参与用户</p>
							</div>
						</div>
						<div className="subscriber" >
							<div id="participation"></div>
						</div>
						<div className="subscriber">
							<img src={require('../../style/img/icons4.png')}  />
							<div>
								<span>08</span>
								<p>可调设备总数</p>
							</div>
						</div>
						<div className="subscriber">
							<div>
								<span>02</span>
								<p>在线设备数</p>
							</div>
						</div>
						<div className="subscriber">
							<img src={require('../../style/img/icons5.png')}  />
							<div>
								<span>13,040</span>
								<p>可调总负荷(kW)</p>
							</div>
						</div>
						<div className="subscriber">
							<div>
								<span>10,040</span>
								<p>实时可调负荷(kW)</p>
							</div>
						</div>
					</div>
				</div>
				<div className="respondheader">
					<h4>需求响应统计
						<ConfigProvider locale={locale}>
						    <RangePicker disabledDate={disabledDate} />
						</ConfigProvider>
					</h4>
					<div className="numerical">
						<div className="statement">
							<div>
								<h4>参与需求响应次数</h4>
								<b>10</b>
							</div>
							<div>
								<h4>需求响应总削减负荷电量(kWh)</h4>
								<b>2,938,576</b>
							</div>
							<div>
								<h4>需求响应总收益(元)</h4>
								<b>246,720</b>
							</div>
						</div>
						<div className="statement">
							<ul className="burden">
								<li>
									<h4>平均可调负荷(kW/次)</h4>
									<b>2,345</b>
								</li>
								<li>
									<h4>平均参与负荷(kW/次)</h4>
									<b>2,100</b>
								</li>
								
								<li>
									<h4>平均实际削减负荷电量(kWh/次)</h4>
									<b>293,857.6</b>
								</li>
								<li>
									<h4>平均需求响应收益(元/次)</h4>
									<b>23,456</b>
								</li>
							</ul>
						</div>
						<div className="statement">
							<ul className="burden">
								<li>
									<h4>平均实际响应负荷(kW/次)</h4>
									<b>1,800</b>
								</li>
								<li>
									<h4>平均削减负荷占比</h4>
									<b>85.71%</b>
								</li>
								
							</ul>
						</div>
					</div>
					<div className="datum">
						<div className="information">
							<h4>所选日期内数据</h4>
							<ul className="chosedate">
								<li>
									<p>可调负荷用户数</p>
									<span>-</span>
								</li>
								<li>
									<p>需求响应收益（元）</p>
									<span>-</span>
								</li>
								<li>
									<p>可调负荷(kW)</p>
									<span>-</span>
								</li>
								<li>
									<p>需求响应削减电量(kWh)</p>
									<span>-</span>
								</li>
								<li>
									<p>参与负荷(kW)</p>
									<span>-</span>
								</li>
								<li>
									<p>实际削减电量(kWh)</p>
									<span>-</span>
								</li>
								<li>
									<p>响应需求负荷(kW)</p>
									<span>-</span>
								</li>
								<li>
									<p>削减负荷占比</p>
									<span>-</span>
								</li>
							</ul>
						</div>
						<div id="line"></div>
						
					</div>
					
				</div>
			</div>
		)
	}
	
}
export default respond