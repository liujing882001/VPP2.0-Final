import React,{useEffect,useState} from 'react'
import * as echarts from 'echarts';
// import * as echarts from 'echarts/lib/echarts';
import 'echarts/lib/chart/pie';
import {Table ,Button ,Input,message} from 'antd';
import http from '../../../server/server.js'
import axios from 'axios'
import './index.css'
const { Search } = Input;
const Accumulation= () => {
	const [currentState,setCurrentState] = useState(0);
	const [dataSource,setDataSource] = useState([]);
	const [soc,setSoc] = useState('');
	const [soh,setSoh] = useState('');
	const [capacity,setCapacity] = useState('-');
	const [inEnergy,setInEnergy] = useState('-');
	const [load,setLoad] = useState('-');
	const [maxInLoad,setMaxInLoad] = useState('-');
	const [maxOutLoad,setMaxOutLoad] = useState('-');
	const [outEnergy,setOutEnergy] = useState('-');
	const [page,setPage] = useState(1);
	const [type,setType] = useState(0);
	const [stationName,setStationName] = useState('');
	const [normal,setNormal] = useState('');
	const [offline,setOffline] = useState('');
	const [totalPages,setTotalPages] = useState('');
	const [loading,setLoading] = useState(false);
	const [total,setTotal] = useState('');
	const [construct,setConstruct] = useState(0);
	const [currentNum,setCurrentNum] = useState(1);
	
	useEffect(() =>{
		storageEnergyCount()
		storageEnergyList()
		different()
		different1()
		storageEnergyList1()
	},[])
	useEffect(() =>{
		storageEnergyList1()
	},[page,currentState])
	// 上方容量
	const storageEnergyCount = () => {
		var chartDom = document.getElementById('different');
		var myChart = echarts.init(chartDom);
		myChart.showLoading({
			text: '数据加载中...',
			color: '#2A2B40',
			textColor: '#ffffc2',
			maskColor: 'rgba(255, 255, 255, 0)',
			zlevel: 0
		});
		var chartDom = document.getElementById('different1');
		var myChart1 = echarts.init(chartDom);
		myChart1.showLoading({
			text: '数据加载中...',
			color: '#2A2B40',
			textColor: '#ffffc2',
			maskColor: 'rgba(255, 255, 255, 0)',
			zlevel: 0
		});
		http.post('load_management/storage_energy/storageEnergyCount').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				setSoc((Number(data.soc)*100).toFixed(2))
				setSoh((Number(data.soh)*100).toFixed(2))
				setCapacity(Number(data.capacity).toFixed(2))
				setInEnergy(Number(data.inCapacity).toFixed(2))
				setLoad(Number(data.load).toFixed(2))
				setMaxInLoad(Number(data.maxInLoad).toFixed(2))
				setMaxOutLoad(Number(data.maxOutLoad).toFixed(2))
				setOutEnergy(Number(data.outCapacity).toFixed(2))
				different(data.outCapacity,data.inCapacity)
				different1(data.maxOutLoad,data.maxInLoad)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 表格
	const storageEnergyList = () => {
		setLoading(true)
		http.post('load_management/storage_energy/storageEnergyList',{
			"number": page,
			"pageSize": 10000,
			"stationName": stationName,
			"type": type
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let normal = 0
				let offline = 0
				let construct = 0
				if(res.data.data){
					let content = res.data.data.content
					
					content.map(res =>{
						if(res.online ==true){
							if(res.isEnabled==false){
								construct++
							}else{
								normal++
							}
						}else if(res.online ==false){
							if(res.isEnabled==false){
								construct++
							}else{
								offline++
							}
						}
						
					})
					setTotalPages(res.data.data.totalElements)
					setNormal(normal)
					setOffline(offline)
					setConstruct(construct)
					setLoading(false)
				}else{
					setTotalPages(0)
					setNormal(normal)
					setOffline(offline)
					setConstruct(construct)
					setLoading(false)
				}
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 
	const storageEnergyList1 = () => {
		setLoading(true)
		http.post('load_management/storage_energy/storageEnergyList',{
			"number": page,
			"pageSize": 3,
			"stationName": stationName,
			"type": type
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				if(res.data.data){
					let content = res.data.data.content
					setDataSource(content)
					setLoading(false)
					setTotal(res.data.data.totalElements)
				}else{
					setDataSource([])
					setLoading(false)
					setTotal(0)
				}
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 总数
	const chosedate =(e) =>{
		console.log(e)
		setCurrentState(e)
		setType(e)
		setLoading(true)
		setPage(1)
		setCurrentNum(1)
		// storageEnergyList1()
	}
	// 搜索
	const onSearch =(val) =>{
		console.log(val)
		setStationName(val)
		setCurrentNum(1)
		setPage(1)
		setLoading(true)
		http.post('load_management/storage_energy/storageEnergyList',{
			"number": 1,
			"pageSize": 1000000,
			"stationName": val,
			"type": type
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				if(res.data.data){
					let content = res.data.data.content
					
					setDataSource(content)
					setPage(1)
					setLoading(false)
					setTotal(res.data.data.totalElements)
				}else{
					
					setDataSource([])
					setPage(1)
					setLoading(false)
					setTotal(0)
				}
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 导出
	const derive =() =>{
		axios({
			method: 'post',
			url: 'load_management/storage_energy/storageEnergyListExcel',
			responseType: 'arraybuffer',
			data: {
				"number": page,
				"pageSize": 100000,
				"stationName": stationName,
				"type": 0
			}
		}).then(res => {
			if (res.status == 200) {
				const url = window.URL.createObjectURL(new Blob([res.data]));
				const link = document.createElement('a'); //创建a标签
				link.style.display = 'none';
				link.href = url; // 设置a标签路径
				link.download = '储能资源报表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
				document.body.appendChild(link);
				link.click();
				URL.revokeObjectURL(link.href); // 释放 URL对象
				document.body.removeChild(link);
		
			}
		})
	}
	// 当前容量
	const different = (outEnergy,inEnergy) => {
		var chartDom = document.getElementById('different');
		var myChart = echarts.init(chartDom);
		var option;
		option = {
			tooltip: {
				trigger: 'item',
				backgroundColor: '#302F39',
				borderColor: 'transparent',
				textStyle: {
					color: '#fff' // 设置 tooltip 的文字颜色为白色
				},
				formatter: '{a} <br/>{b} : {c} ({d}%)'
			},
			grid: {
		         left: '40%',
		 //         right: '8%',
		         bottom: '1%',
		         containLabel: true
		    },
			color: ['#FAD337','#36CBCB'], // 自定义颜色范围

			series: [{
				name: '当前容量',
				type: 'pie',
				width:300,
				top:50,
				left:'20%',
				bottom:50,
				radius: [40, 80],
				center: ['50%', '50%'],
				// roseType: 'area',
				label: {
			        alignTo: 'edge',
			        formatter: '{name|{b}}\n{time|{d} %}',
			        minMargin: 15,
			        edgeDistance: 10,
			        lineHeight: 26,
					color:'#FFF',
			        rich: {
						time: {
							fontSize: 14,
							color: '#999'
						}
			        }
			    },
				labelLine: {
					length: 0.01,
					length2: 0,
					maxSurfaceAngle: 80
				},
			    labelLayout: function (params) {
					console.log(params)
					if(params.labelLinePoints){
						const isLeft = params.labelRect.x < myChart.getWidth() / 2;
						const points = params.labelLinePoints;
						 // Update the end point.
						points[2][0] = isLeft
						   ? params.labelRect.x
						   : params.labelRect.x + params.labelRect.width;
						return {
						   labelLinePoints: points
						};
					}
			       
			    },
				data: [
				 	{ value: outEnergy, name: '可放容量' },
				 	{ value: inEnergy, name: '可充容量' },
				 	
				 
				 ]
				//  [
				// 	{ value: 0, name: '可放容量' },
				// 	{ value: 800, name: '可充容量' },
					
		        
				// ]
		    }
		  ]
		};
		myChart.hideLoading()
		option && myChart.setOption(option);

	}
	// 最大功率（kW）
	const different1 = (maxOutLoad,maxInLoad) => {
		var chartDom1 = document.getElementById('different1');
		var myChart1 = echarts.init(chartDom1);
		var option;
		option = {
			tooltip: {
				trigger: 'item',
				formatter: '{a} <br/>{b} : {c} ({d}%)'
			},
			grid: {
		 //        left: 50,
		        right: '5%',
				// top:150,
				
		         bottom: '1%',
		         containLabel: true
		    },
			color: ['#FAD337','#36CBCB'], // 自定义颜色范围
		
			series: [{
				name: '当前容量',
				type: 'pie',
				width:300,
				left:'20%',
				// left:150,
				// right:50,
				bottom:80,
				top:80,
				radius: [40, 80],
				center: ['50%', '50%'],
				// roseType: 'area',
				label: {
			        alignTo: 'edge',
			        formatter: '{name|{b}}\n{time|{d} %}',
			        minMargin: 15,
			        edgeDistance: 10,
			        lineHeight: 26,
					color:'#FFF',
			        rich: {
						time: {
							fontSize: 14,
							color: '#999'
						}
			        }
			    },
				labelLine: {
					length: 0.01,
					length2: 0,
					maxSurfaceAngle: 80
				},
			    labelLayout: function (params) {
					console.log(params)
					if(params.labelLinePoints){
						const isLeft = params.labelRect.x < myChart1.getWidth() / 2;
						const points = params.labelLinePoints;
						 // Update the end point.
						points[2][0] = isLeft
						   ? params.labelRect.x
						   : params.labelRect.x + params.labelRect.width;
						return {
						   labelLinePoints: points
						};
					}
			       
			    },
				data:  [
					{ value: maxOutLoad, name: '可放功率' },
					{ value: maxInLoad, name: '可充功率' },
					
		        
				]
				
				// [
				// 	{ value: 0, name: '可放功率' },
				// 	{ value: 400, name: '可充功率' },
					
		        
				// ]
		    }
		  ]
		};
		myChart1.hideLoading()
		option && myChart1.setOption(option);
		
	
	}

		const columns = [
			{
		         title: '序号',
		         width: 70,
		         // render:(text,record,index)=> `${index+1}`,
				 render:(value, item, index) => (page - 1) * 3 + index+1,
			},
		  {
		    title: '电站状态',
		    dataIndex: 'online',
		    key: 'online',
			width: 100,
			render: (text,record,_,action) =>{
				// console.log(record)
				if(record.online ==true){
					// return '正常'
					if(record.isEnabled==false){
						return '建设中'
					}else{
						return '正常'
					}
				}else if(record.online ==false){
					// return '离线'
					if(record.isEnabled==false){
						return '建设中'
					}else{
						return '离线'
					}
				}
			}
			
		  },
		  {
		    title: '策略状态',
		    dataIndex: 'strategy',
		    key: 'strategy',
			width:100
		  },
		  {
		    title: '储能电站名称',
		    dataIndex: 'nodeName',
		    key: 'nodeName',
			width:150
		  },
		  {
		    title: '电站功率(kW)',
		    dataIndex: 'load',
		    key: 'load',
			width:100,
			render: (text: string) => <span>{Number(text).toFixed(2)}</span>,
		  },
		  {
		    title: '电站容量(kWh)',
		    dataIndex: 'capacity',
		    key: 'capacity',
			width:100,
			render: (text: string) => <span>{Number(text).toFixed(2)}</span>,
		  },
		  {
		    title: 'SOC',
		    dataIndex: 'soc',
		    key: 'soc',
			render: (text: string) => <span>{Number(text*100).toFixed(2)}%</span>,
		  },
		  {
		    title: 'SOH',
		    dataIndex: 'soh',
		    key: 'soh',
			render: (text: string) => <span>{Number(text*100).toFixed(2)}%</span>,
		  },
		  {
		    title: '当前可充容量(kWh)',
		    dataIndex: 'inCapacity',
		    key: 'inCapacity',
			width:160,
			render: (text: string) => <span>{Number(text).toFixed(2)}</span>,
		  },
		  {
		    title: '当前可放容量(kWh)',
		    dataIndex: 'outCapacity',
		    key: 'outCapacity',
			width:160,
			render: (text: string) => <span>{Number(text).toFixed(2)}</span>,
		  },
		  {
		    title: '储能实际功率(kW)',
		    dataIndex: 'actualLoad',
		    key: 'actualLoad',
			width:150,
			render: (text,record,_,action) =>{
				if(text!==null&&text!==undefined&&text!==''){
					return Number(text).toFixed(2)
				}else{
					return '-'
				}
			}
		  },
		  {
		    title: '充放电状态',
		    dataIndex: 'actualStrategy',
		    key: 'actualStrategy',
			width:150,
			render: (text,record,_,action) =>{
				// if(text)
				return text?text:'-'
			}
		  },
		  {
		    title: '更新时间',
		    dataIndex: 'ts',
		    key: 'ts',
			width:150,
		  }
		];
		const onChange: PaginationProps['onChange'] = page => {
		    console.log(page);
			setPage(page.current)
			setCurrentNum(page.current)
			// storageEnergyList1()
		};
		
		return(
			<div className="allcontent12">
				<div className="accumulation">
					<div>
						<h4>电站总容量（kWh）</h4>
						<p>{capacity}</p>
					</div>
					<div>
						<h4>电站总功率（kW）</h4>
						<p>{load}</p>
					</div>
					<div>
						<h4>SOH</h4>
						<p>{soh}%</p>
					</div>
					<div>
						<h4>SOC</h4>
						<p>{soc}%</p>
					</div>
				</div>
				<div className="volume">
					<div className="capacity">
						<h4>当前容量（kWh）</h4>
						<div className="capacitance">
							<div className="capability">
								<p style={{marginTop:30}}>可充容量</p>
								<b>{inEnergy}</b>
								<p style={{marginTop:'20px'}}>可放容量</p>
								<b>{outEnergy}</b>
							</div>
							<div id="different"></div>
						</div>
					</div>
					<div className="capacity">
						<h4>最大功率（kW）</h4>
						<div className="capacitance">
							<div className="capability">
								<p style={{marginTop:30}}>可充功率</p>
								<b>{maxInLoad}</b>
								<p style={{marginTop:'20px'}}>可放功率</p>
								<b>{maxOutLoad}</b>
							</div>
							<div id="different1"></div>
						</div>
					</div>
				</div>
				<div className="totality">
					<div className="total total1">
						<div className="allbtns" style={{float:'left',width:436}}>
							<span className={0===currentState?"active":'all'} onClick={()=>chosedate(0)}>总数({totalPages})</span>
							<span className={1===currentState?"active":'all'} onClick={()=>chosedate(1)}>在线({normal})</span>
							<span className={3===currentState?"active":'all'} onClick={()=>chosedate(3)}>建设中({construct})</span>
							<span className={2===currentState?"active":'all'} onClick={()=>chosedate(2)}>离线({offline})</span>
							
						</div>
						<Search placeholder="搜索电站名称" onSearch={onSearch} style={{ width: 200 }} />
						<Button ghost style={{float:'right',marginLeft:'16px'}} onClick={derive}>导出</Button>
						
						
					</div>
					<div style={{marginTop:'20px'}}>
						<Table dataSource={dataSource} columns={columns}
						onChange={onChange}
						loading={loading}
						pagination={
							{
								total: total,//数据的总条数
								defaultCurrent: 1,//默认当前的页数
								defaultPageSize: 3,//默认每页的条数
								showSizeChanger:false,
								current:currentNum
							 
							}
						}
						 rowClassName={
						 (record, index) => {
						   let className = ''
						   className = index % 2 ===0 ? 'ou' : 'ji'
						   // console.log(className)
						   return className
						 	}
						 }
						 />
				
					</div>
				</div>
			</div>
		)
	}
	


export default Accumulation












