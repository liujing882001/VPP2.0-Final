import * as echarts from 'echarts';
import { useEffect,useState,useRef, useImperativeHandle, forwardRef } from 'react';
import { DatePicker,Button } from 'antd';
import http from '../../../../../../server/server'
import Typed from 'typed.js';
import { Income } from '../income'
import dayjs from 'dayjs';
import './index.scss'
const { RangePicker } = DatePicker

export const Weather = forwardRef((props,ref) => {
    const { weatherChartList:list,weatherStartDate,weatherEndDate,setWeatherStartDate,setWeatherEndDate,queryWeatherChartList,nodeId,systemId } = props || {}
    const startDate = useRef(weatherStartDate)
    const endDate = useRef(weatherEndDate)
    const [weatherChartList,setWeatherChartList] = useState([...list])
    const [isShow,setIsShow] = useState(true)
    const message1 = useRef(null);
    const [refresh,setRefresh] = useState(false)
    let typed1 = ''

    useImperativeHandle(ref, () => ({
        onRefresh
    }));

    const n = `main1-${new Date().getTime()}`

    const timeList = (weatherChartList || []).map((item) => {
        return item.ts
    })

    const rainList = (weatherChartList || []).map((item) => {
        if(isNaN(item.rtRain)){
            return ''
        }else{
            return item.rtRain
        }
    })

    const temperatureList = (weatherChartList || []).map((item) => {
        if(isNaN(item.rtTt2)){
            return ''
        }else{
            return item.rtTt2
       }
    })

    
    const swapElements = (arr, index1, index2) => {
        if (index1 >= 0 && index1 < arr.length && index2 >= 0 && index2 < arr.length) {
          var temp = arr[index1];
          arr[index1] = arr[index2];
          arr[index2] = temp;
        }
        return arr;
    }
    
    const forecastRainList = (weatherChartList || []).map((item) => {
        if(isNaN(item.predRain)){
            return ''
        }else{
            return item.predRain
        }
    })

    const forecastTemperatureList = (weatherChartList || []).map((item) => {
        if(isNaN(item.predTt2)){
            return ''
        }else{
            return item.predTt2
       }
    })

    const onRefresh = async (nodeId,systemId,startDate,endDate) => {
        const weatherChartRes = await http.post('system_management/energy_model/energy_storage_model/weatherChart',{
            nodeId,
            systemId,
            startDate,
            endDate
        })
      
        const weatherChartList = weatherChartRes?.data?.data || []      
        setWeatherChartList(weatherChartList)
        setRefresh(!refresh)
    }

    useEffect(() => {
        var chartDom = document.getElementById(n);
        if(chartDom){
            var myChart = echarts.init(chartDom);
            var option;
    
            let base = +new Date(1988, 9, 3);
            let oneDay = 24 * 3600 * 1000;
            let data = [[base, Math.random() * 300]];
            for (let i = 1; i < 20000; i++) {
            let now = new Date((base += oneDay));
                data.push([+now, Math.round((Math.random() - 0.5) * 20 + data[i - 1][1])]);
            }
            option = {
                grid:{
                    left:'5%',
                    right:'5%',
                },
                color: ['#1586FF','#43E3E3'],
                legend: {
                    inactiveColor:  'rgba(174, 174, 174, 1)', 
                    top:15,
                    left:30,
                    itemGap:20,
                    // backgroundColor:'#fff',
                    data: [
                        {icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z', name: '实际温度'},
                        {icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z', name: '预测温度'},
                    
                    ],
                    itemWidth:15,
                    itemHeight: 15,
                    textStyle:{
                        color:"#fff",
                        fontSize:12,
                        // backgroundColor:'red',
                        lineHeight:10,
                        padding:[-1,0,0,0]
                    },
                },
                xAxis:{
                    type: 'category',
                    axisTick: {
                        alignWithLabel: true
                    },
                    // boundaryGap: false,
                    data:timeList,
                    axisLabel:{//x坐标轴刻度标签
                        show:true,
                        color:'#FFF',//'#ccc'，设置标签颜色
                        // formatter: `{value}`
                    },
                      
                },
                tooltip: {
                    trigger: 'axis',
                        formatter: function (params) {
                        const list =  swapElements(params,0,1)
                        var relVal = list[0].name
                        for (var i = 0, l = list.length; i < l; i++) {
                          const l = i === 0 || i === 1 ? ' ℃' : ' mm' 
                          if(isNaN(list[i].value)){
                            relVal += '<br/>' + list[i].marker + list[i].seriesName + ' : ' + '-' + l 
                          }else{
                            relVal += '<br/>' + list[i].marker + list[i].seriesName + ' : ' + list[i].value + l 
                          }  
                        }
                        return relVal
                    },
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
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: ['20%', '20%'],
                    splitNumber:4   ,
                    axisLabel:{//x坐标轴刻度标签
                        show:true,
                        color:'#DFE1E5FF',//'#ccc'，设置标签颜色
                    },
                    splitLine:{
                        show:true,
                        lineStyle:{
                            type:'dashed',
                            color:'#8F959E80'
                        }
                    },    
                },
                dataZoom: [
                    {
                        type: 'slider',
                        xAxisIndex: [0],
                        filterMode: 'filter',
                        startValue:0,
                        endValue:22,
                        showDetail:true,
                        borderColor:    '#fff',
                        selectedDataBackground:{
                            lineStyle:{
                                color:'#0092FF'
                            },
                            areaStyle:{
                                opacity:0
                            }
                        },
                        moveHandleStyle:{
                            color:'rgba(143, 149, 158, 1)'
                        },
                        emphasis:{moveHandleStyle:{
                               color:'rgba(143, 149, 158, 1)'
                        }},
                        fillerColor: 'rgba(0, 146, 255, .1)',
                        brushStyle:{
                            color:'rgba(0, 146, 255, .1)'
                        }
                    },      
                ],
                series: [
                    {
                        name:'预测温度',
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 1,
                            type:"dashed"
                        },
                        showSymbol: false,
                        areaStyle: {
                            opacity: 0.5,
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                {
                                    offset: 0,
                                    color: '#1586FF'
                                },
                                {
                                    offset: 1,
                                    color: 'rgba(0, 146, 255, 0)'
                                }
                            ])
                        },
                        emphasis: {
                            focus: 'series'
                        },
                        data:forecastTemperatureList
                    },
                    {
                        name:'实际温度',
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 1,
                        },
                        showSymbol: false,
                        emphasis: {
                            focus: 'series'
                        },
                        areaStyle: {
                            opacity: 0.5,
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                {
                                    offset: 0,
                                    color: 'rgba(67,227,227)'
                                },
                                {
                                    offset: 1,
                                    color: 'rgba(0, 146, 255, 0)'
                                }
                            ])
                        },
                        data: temperatureList
                    },
         
                ]
            };
            option && myChart.setOption(option);
        }
  
        const onresize = function() {
            myChart.resize();
        };

        window.addEventListener('resize',onresize, false)

        if(message1?.current){
            // eslint-disable-next-line react-hooks/exhaustive-deps
            typed1 = new Typed(message1?.current, {
                strings: ['气象数据分析'],
                typeSpeed: 30,
                backSpeed: 100,
                showCursor: true,
                cursorChar: '',
                onComplete(self) {
                    setTimeout(() => {
                        setIsShow(false)
                    },2000)
                },
            });
        }

        return () => {
            window.removeEventListener('resize', onresize, false)
        }

    },[rainList, temperatureList, timeList, weatherChartList,forecastTemperatureList])

    useEffect(() => {
        const scroll = document.getElementById('energy-scroll-wrap');
        scroll.scrollTop = scroll.scrollHeight;
    },[isShow])

    return (
        <>
            <div className='weather-chart-wrap'>
            {
                isShow ? 
                <>
                    <div className='weather-chart-title-wrap'>
                        <span className='weather-chart-title' ref={message1}></span>
                    </div>
                    <div className="loading-wrapper">
                        <i className='loading-icon'/>
                    </div>
                </> :
                <>
                    <div className='weather-chart-title-wrap'>
                        <span className='weather-chart-title'>气象数据分析</span>
                        {/* <div className='weather-chart-query-wrap'>
                            <RangePicker className='weather-chart-query-time'
                                defaultValue={[startDate.current && dayjs(startDate.current, 'YYYY-MM-DD'),endDate.current && dayjs(endDate.current, 'YYYY-MM-DD')]} 
                                onChange={(data,dataString)=>{
                                    const start = dataString[0]
                                    const end = dataString[1]
                                    startDate.current = start
                                    endDate.current = end
                                }}
                                
                            />
                            <Button type="Button" className='weather-chart-query-btn' onClick={() => {
                                onRefresh(nodeId,systemId,startDate.current,endDate.current)
                            }}>查询</Button>
                        </div> */}
                    </div>
                    <div className='weather-chart-content-wrap' key={refresh}>
                        <div style={{display:'flex',justifyContent:'center'}}>
                            {
                                forecastTemperatureList?.length && temperatureList?.length ? 
                                    <div className="main1" id={n} style={{width:'100%',height:'280px',display:'flex',justifyContent:'center'}}></div>    
                                    : 
                                    <div className="empty-container">
                                        <i className="empty-icon"/>
                                        <span className="empty-text">暂无数据</span>
                                    </div>
                            }   
                        </div>
                    </div>
                </>
            }
            </div>
        </>
    )
})



