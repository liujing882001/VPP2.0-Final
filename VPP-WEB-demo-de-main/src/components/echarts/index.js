import React, { useEffect, useRef, useState } from 'react';
import * as echarts from 'echarts';
import http from '../../server/server';
import { message ,Spin} from 'antd';
import './index.scss'
export const EChartsMixedChart = ({
	style,
	className,
	argumentsList,
	selectedTime,
	selectedNode,
	argumentsdata
}) => {
	const chartRef = useRef(null);
	const [dataName, setDataName] = useState([]);
	const [socData, setSocdata] = useState([]);
	const [xList, setXlist] = useState([]);
	const [mappingXdata,setMappingXdata] = useState([]);
	const [xdata,setXdata] = useState([]);
	const [strategy, setStrategy] = useState([]);
	const [electricity, setElectricity] = useState([]);
	const [seriesData, setSeriesData] = useState([]);
	const [isChange,setIschange] = useState(null);
	const [argumentsData,sAetargumentsData] = useState([]);
	const [loading, setLoading] = useState(true);
	const [index,setIndex] =  useState(1);
	const isLastItemByValue =(arr, item) => {
		if (arr.length === 0) {
			return false;
		}
		const last = arr[arr.length - 1];
		return Object.keys(item).every(key => last[key] === item[key]);
	}
	useEffect(() => {
		if (argumentsList) {
			setSeriesData([])
			setXlist([])
			setDataName([])
			setMappingXdata([])
			setLoading(true)
			if (argumentsList?.arguments?.dataName &&argumentsList?.arguments?.url!=='/north/queryStationNode') {
				setIschange(null)
				getqueryMetaDataByMapping(argumentsList);
			}
			if (Object.keys(argumentsList?.arguments).length > 0&&
			!argumentsList?.arguments?.dataName && !argumentsList?.arguments?.name &&!argumentsList?.arguments?.category
			&&argumentsList?.arguments?.url!=='/north/queryStationNode') {
				getqueryNodeEPrice(argumentsList);
			}
			setIndex(prevIndex => prevIndex + 1);
			
			
		}
	}, [argumentsList]);
	useEffect(() =>{
		if (argumentsdata.length > 0) {
		      const newData = [...argumentsdata];
		      newData.pop();
			newData?.forEach(res => {
				if (res?.arguments?.dataName) {
					getqueryMetaDataByMapping(res);
					
				}
				if (!res?.arguments?.dataName && !res?.arguments?.name&&!res?.arguments?.category) {
					getqueryNodeEPrice(res);
					
				}
				
			});
		      // 更新状态以反映移除最后一个元素后的新数组
		      // setArgumentsData(newData);
		    }
		
	},[argumentsdata])
	useEffect(() =>{
		setSeriesData([])
		setXlist([])
		setDataName([])
		setMappingXdata([])
		
		if(selectedTime&&argumentsdata || selectedNode&&argumentsdata){
			argumentsdata.forEach(res => {
				if (res?.arguments?.dataName) {
					getqueryMetaDataByMapping(res);
					
				}
				if (!res?.arguments?.dataName && !res?.arguments?.name&&!res?.arguments?.category) {
					getqueryNodeEPrice(res);
					
				}
				
			});
				
			
		}
	},[selectedTime,selectedNode])
	
	const getqueryNodeEPrice = (item) => {
		// 电价
		const chart = echarts.init(chartRef.current);
		
		setIschange(true)
		setLoading(true)
		const stationId = selectedNode ? selectedNode : item.arguments.nodeId;
		let queryParameters = {
		    nodeId: stationId,
		};
		
		if (selectedTime ) {
			const hasEmpty = selectedTime.some(element => element === "");
			if(!hasEmpty){
				queryParameters.st = selectedTime[0];
				queryParameters.et = selectedTime[1];
			}else{
				return
			}
		    
		} else {
		    queryParameters.date = item.arguments.date;
		}
		
		const queryString = new URLSearchParams(queryParameters).toString();
		http.get(item.url + '?' + queryString)
			.then(res => {
				console.log(res);
				if(res.data.code===200){
					let data = res?.data?.data
					if(data){
						const updatedData = addPricesToTimeSlots(data);						
						let dataList = []
						let Xlist = []
						updatedData?.map(res =>{
							dataList.push({
								value:res.price,
								itemStyle: {
									color: res.type=="尖"?'#186AC3':res.type=="峰"?'#2C98FB':
									res.type=="平"?'#86BAF6':res.type=="谷"?'#EEF0FD':''
								},
								name:res.type
							})
							// Xlist.push(res.date)
							setXlist(prevSeriesData => [
								...prevSeriesData,
								res.date
							]);
						})
						setSeriesData(prevSeriesData => [
							...prevSeriesData,
							{
								type: 'bar',
								data: dataList,
								barCategoryGap: '-8%', 
								yAxisIndex: 1,
								barGap: '-3%',
								name: '尖峰平谷',
								itemStyle: {
									borderColor: 'red' ,
									borderWidth:0,
									color:'#186AC3'
								},
							}
						]);
						setDataName(prevDataName => [
							...prevDataName,
							'尖','峰','平','谷'
						]);
						
					}
					setLoading(false)
				}else{
					setLoading(false)
				}
			})
			.catch(err => {
				console.log(err);
				setLoading(false)
			});
	};
	const addPricesToTimeSlots =(data) => {
	    const { priceOffPeak, pricePeak, priceSharp, priceShoulder, timeSlots } = data;
	        const transformedTimeSlots = Object.keys(timeSlots).map(timeSlot => {
	            const type = timeSlots[timeSlot];
	            let price = 0;
	    
	            switch (type) {
	                case '尖':
	                    price = priceSharp;
	                    break;
	                case '峰':
	                    price = pricePeak;
	                    break;
	                case '平':
	                    price = priceShoulder;
	                    break;
	                case '谷':
	                    price = priceOffPeak;
	                    break;
	                default:
	                    // 如果有其他情况，可以在这里处理
	                    break;
	            }
	    
	            return {
	                date: timeSlot,
	                type: type,
	                price: price
	            };
	        });
	    
	        return transformedTimeSlots;
	}
	// 查询默认的值
	const getqueryMetaDataByMapping = (item) => {
		setLoading(true)
		const stationId = selectedNode?selectedNode:item.arguments.stationId
		let queryParameters = {
		    stationId: stationId,
			dataName:item.arguments.dataName
		};
		
		if (selectedTime) {
			const hasEmpty = selectedTime.some(element => element === "");
		    if (!hasEmpty) {
		        queryParameters.st = selectedTime[0] + ' ' + '00:00:00';
		        queryParameters.et = selectedTime[1] + ' ' + '23:59:59';
		    } else {
		        return;
		    }
		} else {
		    queryParameters.st = item.arguments.st
		    queryParameters.et = item.arguments.et;
		}
		
		const queryString = new URLSearchParams(queryParameters).toString();
		const url = `${item.url}?${queryString}`;
		http.get(url).then(res => {
			if (res.data.code === 200) {
				const data = res.data.data;
				if (data) {
					const dates = Object.keys(data);
					const values = Object.values(data);
					// setXlist(dates);
					setMappingXdata(dates)
					setSeriesData(prevSeriesData => [
						...prevSeriesData,
						{
							name: item?.arguments?.dataName,
							type: item?.arguments?.dataName === 'SOC'||item?.arguments?.dataName === 'soc' ? 'line' : 'bar',
							data: values,
							symbol: item?.arguments?.dataName === 'SOC' ||item?.arguments?.dataName === 'soc' ? 'none' : undefined,
							yAxisIndex: item?.arguments?.dataName === 'SOC'||item?.arguments?.dataName === 'soc' ?0:1,
							itemStyle: {
								color: item?.arguments?.dataName === 'SOC' ||item?.arguments?.dataName === 'soc' ?
								'#5AD8A6':item?.arguments?.dataName === '节点电价'?'#FACF05':
								item?.arguments?.dataName === '策略状态'?'#AB98FF':'#03C5B3'
								
							}
						}
					]);
					setDataName(prevDataName => [
						...prevDataName,
						item?.arguments?.dataName
					]);
					
				}
				setLoading(false)
			}else{
				message.info(res.data.msg)
				setLoading(false)
			}
		}).catch(err => {
			console.log(err);
			setLoading(false)
		});
	};
	useEffect(() =>{
		if(xList&&mappingXdata){
			if (xList.length > mappingXdata.length) {
				  setXdata(xList)
			    } else if (xList.length < mappingXdata.length) {
				  setXdata(mappingXdata)
			    } else {
				  setXdata(mappingXdata)
			    }
		}
	},[xList, mappingXdata])
	useEffect(() => {
		// console.log(seriesData,'seriesData')
		const chart = echarts.init(chartRef.current);
		
		const option = {
			tooltip: {
			    trigger: "axis",
			    backgroundColor: "#302F39",
			    borderColor: "transparent",
			    textStyle: {
			        color: "#fff",
			    },
			    position: "top",
			    axisPointer: {
			        type: "cross",
			        snap: true,
			        label: {
			            backgroundColor: "#6a7985",
			        },
			    },
			    formatter(params) {
			        let relVal = params[0].axisValue;
					// console.log(params)
			        for (let i = 0, l = params.length; i < l; i++) {
			            if (
			                params[i].value === null ||
			                params[i].value === undefined ||
			                params[i].value === "" ||
			                params[i].value === "-"
			            ) {
			                relVal += "<br/>" + params[i].marker + params[i].seriesName + " : " + "-";
			            } else {
			                if (params[i].seriesName === 'SOC'||params[i].seriesName === 'soc') {
			                    relVal += "<br/>" + params[i].marker + params[i].seriesName + " : " + (Number(params[i].value) * 100).toFixed(2) + '%';
			                }else if(params[i].seriesName=='尖峰平谷') {
								if(params[i].name=='尖'||params[i].name=='峰'||params[i].name=='平'||params[i].name=='谷'){
									if(params[i].value===""||params[i].value===null||params[i].value===undefined){
										relVal+=  "<br/>" + params[i].marker+params[i].name+ " : " + "-"+ '元/kWh' + '<br/>';
										
									}else{
										relVal +=  "<br/>" +  params[i].marker+params[i].name+ " : " + params[i].value+ '元/kWh' ;
														
									}
								}
							}else{
			                    relVal += "<br/>" + params[i].marker + params[i].seriesName + " : " + params[i].value +' ';
			                }
			            }
			        }
			        return relVal;
			    },
			},
			grid:{
				left:60,
				right:60,
				botttom:60,
				top:40
			},
			dataZoom: [
				{
					type: 'slider',
					start: 0,
					end: 100,
				},
				{
					type: 'inside',
					start: 0,
					end: 100,
				},
			],
			legend: {
			  data: seriesData.map(item => ({
			    name: item.name,
				itemWidth: 12,
				        itemHeight: 12,
			    icon: function () {
			      if (item.name === 'SOC' || item.name === 'soc') {
			        return 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z';
			      } else {
			        return 'roundRect';
			      }
			    }(),
			    textStyle: {
			      color: '#FFF',
			    },
				
				
			  })),
			  
			  show:false
			},

			xAxis: {
				type: 'category',
				data: xdata,
				symbol: 'none',
				axisLabel: {
					formatter: '{value}',
					textStyle: {
						color: '#FFF',
					},
				},
				
			},
			yAxis: [
				{
					type: 'value',
					name: '%',
					yAxisIndex: 0,
					axisLabel : {
						formatter: '{value}',
						textStyle: {
							color: '#FFF'
						}
					},
					nameTextStyle: {
						color: "#fff",
						fontSize: 10,
						padding: [0, 0, 0, -25]
					},
					splitLine: {
						lineStyle: {
							type: "dashed",
							width: 1,
							color: "#8F959E",
						},
						show: true,
					},
					alignTicks: true
				},
				{
					type: 'value',
					name: '元/kWh',
					yAxisIndex: 1,
					axisLabel: {
						formatter: '{value}',
						textStyle: {
							color: '#FFF',
						},
					},
					nameTextStyle: {
						color: "#fff",
						fontSize: 10,
						padding: [0, 0, 0, 40]
					},
					splitLine: {
						lineStyle: {
							type: "dashed",
							width: 1,
							color: "#8F959E",
						},
						show: true,
					},
					alignTicks: true
				},
				
			],
			series: seriesData, // Use the dynamically updated seriesData
		};

		chart.setOption(option);
		
		const resizeHandler = () => {
			chart.resize();
		};
		window.addEventListener('resize', resizeHandler);
		return () => {
			window.removeEventListener('resize', resizeHandler);
			chart.dispose();
		};
	}, [ xdata, socData, seriesData]);

	return (
	<>
		<>
		{
			<div className="Electricity_price">
			{
				dataName.map((res,index) =>{
					return <div key={index} className={res==='尖'||res==='峰'||res==='平'||res==='谷'?'price_electrovalence':
					res==='策略状态'?'price_state':res==='节点电价'?'price_state_node':res==='SOC'?'price_state_soc':
					''}>{res}</div>
				})
			}
				
			</div>
		}
		</>
		<Spin spinning={loading}>
		       <div ref={chartRef} style={{ width: 794, height: '365px', ...style }} className={className} />
		</Spin>
		
	</>
	)
};