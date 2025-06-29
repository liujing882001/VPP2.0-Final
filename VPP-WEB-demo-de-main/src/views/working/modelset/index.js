import React,{Component} from 'react'
import { Space,ConfigProvider,DatePicker,Button,Select,Input ,Table,message,Cascader } from 'antd';
import * as echarts from 'echarts';
// import echarts  from '../../echarts.js'
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import './index.css'
import http from '../../../server/server.js'



const { Option } = Select;
class manage extends Component {
	constructor(props) {
		super(props)
		this.state={
			current:0,
			currentState:0,
			nodeNameList:[],
			nodeid:'',
			year:'',
			alarmNumber:''	,//数量
			soc:'',
			soh:'',
			storageCapacity:'',
			storageLoad:'',
			urgent:'',//紧急
			ciyao:'',
			tishi:'',
			zhongyao:'',
			inEnergy:'',
			outEnergy:'',
			yearMonth:'',
			yearMonthday:'',
			xdata:[],
			x_in:[],
			x_out:[],
			gurgent:'',//紧急
			gciyao:'',
			gtishi:'',
			gzhongyao:'',
			galarmNumber:'',
			pvLoad:'',
			AccumulatedStartupTime: '',
			energy: '',
			pvCapacity: '',
			pvLoad: '',
			load:''	,//
			totalLoad:''	,//总可调负荷
			alarmRatio:[],	//报警
			loadRatio:[],
			falarmNumber:'',
			loading:true,
			options:[]
			

		}
	}
	componentDidMount() {
		this.tabFn(1)
		this.element()
		this.house()
		this.factory()
		this.nodeTree()
		// this.nodeNameList()
		
		
	}
	// 节点列表tree/nodeTree
	nodeTree(){
		http.post('tree/nodeTree').then(res =>{
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
				console.log(data);
				this.setState({
					options:data
				},() =>{
					// console.log(this.state.options)
					
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 节点
	nodeNameList(){
		
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				this.setState({
					nodeNameList:res.data.data,
					loading:false
				})
			}
		}).catch(err =>{
			console.log(err)
		})
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
		this.setState({
			nodeid:val[val.length-1]
		},() =>{
			this.storageEnergyListNow()
			this.loadListByNow()
			this.storageEnergyListChart()
			this.photovoltaicListNow()
		})
	}
	onChange = (e) =>{
		
	}
	// 分布式储能
	storageEnergyListNow(){
		http.post('runSchedule/runManagement/storageEnergyListNow?nodeId='+this.state.nodeid).then(res =>{
			console.log(res)
			if(res.data.code == 200){
				let data = res.data.data
				let s;
				console.log(Number(s))
				
				this.setState({
					alarmNumber:data.alarmNumber,
					soc:data.soc===null?'':data.soc===undefined?'':(Number(data.soc)*100).toFixed(2),
					soh:data.soh===null?'':data.soh===undefined?'':(Number(data.soh)*100).toFixed(2),
					storageCapacity:data.storageCapacity===null?'':data.storageCapacity===undefined?'':
					Number(data.storageCapacity).toFixed(2),
					storageLoad:data.storageLoad===null?'':data.storageLoad===undefined?'':
					Number(data.storageLoad).toFixed(2),
					urgent:data.紧急,
					ciyao:data.次要,
					tishi:data.提示,
					zhongyao:data.重要,
					inEnergy:data.inEnergy,
					outEnergy:data.outEnergy
				})
			}
			
		}).catch(err =>{
			console.log(err)
		})
	}
	// 可调负荷
	loadListByNow(){
		http.post('runSchedule/runManagement/loadListByNow?nodeId='+this.state.nodeid).then(res =>{
			console.log(res)
			let data = res.data.data
			let loadRatio =JSON.parse(data.loadRatio)
			let list = [];
			 for(var key in loadRatio){
			        var temp = {}
			        temp.name = key;
			        temp.value = loadRatio[key];
			        list.push(temp)
			  }
			console.log(list)
			this.setState({
				totalLoad:data.totalLoad,
				load:data.load,
				falarmNumber:data.alarmNumber,
				alarmRatio:JSON.parse(data.alarmRatio),
				loadRatio:list,
			},() =>{
				this.factory()
				this.house()
			})
		}).catch(err =>{
			console.log(err)
		})
	}
	// 图表
	storageEnergyListChart(){
		http.post('runSchedule/runManagement/storageEnergyListChart?nodeId='+this.state.nodeid).then(res =>{
			console.log(res)
			let data = res.data.data
			if(res.data.code ==200){
				let data = res.data.data
				this.setState({
					xdata:data.y,
					x_in:data.x_in,
					x_out:data.x_out
				},() =>{
					this.element()
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	photovoltaicListNow(){
		http.post('runSchedule/runManagement/photovoltaicListNow?nodeId=' +this.state.nodeid).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				this.setState({
					gurgent:data.紧急,
					gciyao:data.次要,
					gtishi:data.提示,
					gzhongyao:data.重要,
					galarmNumber:data.alarmNumber,
					pvLoad:data.pvLoad,
					AccumulatedStartupTime:data.AccumulatedStartupTime,
					energy:data.energy,
					pvCapacity:data.pvCapacity
				})
			}
		}).catch(err =>{
			console.log(err)
		})
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
		      data: this.state.xdata,
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
				data: this.state.x_in,
			  
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
		      data: this.state.x_out
		    },
		    
		  ]
		};
		
		option && myChart.setOption(option);
		window.addEventListener('resize', function() {
			myChart.resize()
		})

	}
	// 负荷占比
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
					var v = tarValue===undefined?'':tarValue;
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
					data: this.state.loadRatio,
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
	// 报警数量
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
					var v = tarValue===undefined?'':tarValue;
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
					name: '报警数量',
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
					data:this.state.alarmRatio,
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
		let {nodeNameList,alarmNumber,soc,soh,storageCapacity,storageLoad,urgent,inEnergy,outEnergy,
		ciyao,tishi,zhongyao,xdata,x_in,x_out,gurgent,gciyao,gtishi,gzhongyao,galarmNumber,pvCapacity,
		AccumulatedStartupTime,energy,pvLoad,totalLoad,load,falarmNumber,loading,options
		
		} = this.state
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current && current > dayjs().endOf('day');
		};
		return(
			<div className="allcontent12">
				<div className="manageheader">
					
					<Cascader style={{ width: 230 }} options={options} onChange={this.handleChange}  placeholder="请选择" />
						
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
									<b>{storageCapacity}</b>
								</li>
								<li>
									<h4>电池功率(kW)</h4>
									<b>{storageLoad}</b>
								</li>
								<li>
									<h4>SOH</h4>
									<b>{soh}%</b>
								</li>
								<li>
									<h4>SOC</h4>
									<b>{soc}%</b>
								</li>
							</ul>
						</div>
						<div className="hadoop">
						<div className="quantity1">
							<img src={require('../../../style/img/warn.png')}  />
							报警数量
							<span>{alarmNumber}</span>
						</div>
							<ul className="hadooplist warnhadooplist">
								<li>
									<h4>紧急</h4>
									<b>{urgent}</b>
								</li>
								<li>
									<h4>重要</h4>
									<b>{zhongyao}</b>
								</li>
								<li>
									<h4>次要</h4>
									<b>{ciyao}</b>
								</li>
								<li>
									<h4>提示</h4>
									<b>{tishi}</b>
								</li>
							</ul>
						</div>
					</div>
					<div className="battery">
						<div>
							<img src={require('../../../style/img/lv.png')}  />
							<b>{inEnergy}</b><br />
							<span>电池总充电量（kWh）</span>
						</div>
						<div>
							<img src={require('../../../style/img/huang.png')}  />
							<b>{outEnergy}</b><br />
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
									<b>{energy}</b>
								</li>
								<li>
									<h4>满发小时数(h)</h4>
									<b>{AccumulatedStartupTime}</b>
								</li>
								<li>
									<h4>实时功率(kW)</h4>
									<b>{pvLoad}</b>
								</li>
								<li>
									<h4>装机容量(kW)</h4>
									<b>{pvCapacity}</b>
								</li>
							</ul>
						</div>
						<div className="hadoop">
						<div className="quantity1">
							<img src={require('../../../style/img/warn.png')}  />
							报警数量
							<span>{galarmNumber}</span>
						</div>
							<ul className="hadooplist warnhadooplist">
								<li>
									<h4>紧急</h4>
									<b>{gurgent}</b>
								</li>
								<li>
									<h4>重要</h4>
									<b>{gzhongyao}</b>
								</li>
								<li>
									<h4>次要</h4>
									<b>{gciyao}</b>
								</li>
								<li>
									<h4>提示</h4>
									<b>{gtishi}</b>
								</li>
							</ul>
						</div>
					</div>
				</div>
				<div className="load" style={{padding:'0px 20px 30px 20px'}}>
					<h4 style={{paddingLeft:0}}>可调负荷</h4>
					<div className="loadcontent">
						<div className="stations">
							<div>
								<b>{totalLoad}</b><br />
								<span>总可调负荷（kW）</span>
							</div>
							<img src={require('../../../style/img/yellow.png')}  />
						</div>
						<div className="stations">
							<div>
								<b>{load}</b><br />
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
								报警数量 <span className="number">{falarmNumber}</span>
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


// <Button onClick={this.onSearch}  type="primary" style={{float:'right',margin:'23px 0px 0px 20px'}}>搜索</Button>
// 					<div className='tab_con3' style={{float:'right',marginTop:'25px'}}>
// 						<ol className='abb'>
// 							<li onClick={() => { this.tabFn(1) }} className={this.clsFn(1, 'cur3', 'cur31')}>年</li>
// 							<li onClick={() => { this.tabFn(2) }} className={this.clsFn(2, 'cur3', 'cur32')}>月</li>
// 							<li onClick={() => { this.tabFn(3) }} className={this.clsFn(3, 'cur3', 'cur33')}>日</li>
// 						</ol>
						 
// 						<ul className='acc'>
// 							<li className={this.clsFn(1, 'current', '')}>
								
// 								<ConfigProvider locale={locale}>
// 									<DatePicker disabledDate={disabledDate}  onChange={this.onChangeyear} picker="year" />
// 								</ConfigProvider>
// 							</li>
								
// 							<li className={this.clsFn(2, 'current', '')}>
// 								<ConfigProvider locale={locale}>
// 									<DatePicker disabledDate={disabledDate}  onChange={this.onChangemonth} picker="month" />
// 								</ConfigProvider>
								
// 							</li>
// 							<li className={this.clsFn(3, 'current', '')}>
// 								<ConfigProvider locale={locale}>
// 									<DatePicker disabledDate={disabledDate}   onChange={this.onChangemday} />
// 								</ConfigProvider>
													
// 							</li>
// 						</ul>
						
// 					</div>






















