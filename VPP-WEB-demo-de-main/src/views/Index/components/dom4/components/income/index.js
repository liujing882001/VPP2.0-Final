import * as echarts from 'echarts';
import { useEffect,useState,useRef, useImperativeHandle, forwardRef } from 'react';
import { DatePicker,Button } from 'antd';
import http from '../../../../../../server/server'
import Typed from 'typed.js';

import dayjs from 'dayjs';
import './index.scss'
const { RangePicker } = DatePicker

export const Income = forwardRef((props,ref) => {
    const { weatherStartDate,weatherEndDate,energyList:list1,photovoltaicList:list2,allList:list3 } = props || {}
    const startDate = useRef(weatherStartDate)
    const endDate = useRef(weatherEndDate)
    const [energyList,setEnergyList] = useState([...list1])
    const [photovoltaicList,setPhotovoltaicList] = useState([...list2])
    const [allList,setAllList] = useState([...list3])
    const [isShow,setIsShow] = useState(true)
    const message1 = useRef(null);
    let typed1 = ''

    const timeList = ((allList || [])[0] || (allList || [])[1])?.dataList?.map((item)=> {
        return item?.time
    })

    const colors = []

    const allListOptions = allList.map((item,i) => {
        const renderColor = ['#FF59FC','#A200A0']
        if(item?.name?.includes("实际")){
            colors.push(renderColor[i])
            return {
                icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z',
                name: item.name
            }
        }else if(item?.name?.includes("预测")){
            colors.push(renderColor[i])
            return {
                icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z',
                name: item.name
            }
        }
    })

    const allSeries = allList.map((item) => {
        if(item?.name?.includes("实际")){
            return {
                name:item?.name,
                type: 'line',
                smooth: true,
                lineStyle: {
                    width: 1,
                },
                showSymbol: false,
                emphasis: {
                    focus: 'series'
                },
                data:item.dataList.map((item) => item.value)
            }
        }else if(item?.name?.includes("预测")){
            return  {
                name:item?.name,
                type: 'line',
                smooth: true,
                lineStyle: {
                    width: 1,
                    type:"dashed"
                },
                showSymbol: false,
                emphasis: {
                    focus: 'series'
                },
                data:item.dataList.map((item) => item.value)
            }
        }
    })

    const energyOptions = energyList.map((item,i) => {
        const renderColor = ['#ACFDB9','#6CFF5F','#15FF00','#00FF95','#00BC6E','#51FFD6']

        if(item?.name?.includes("实际")){
            colors.push(renderColor[i]);
            return {
                icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z',
                name: item.name
            }
        }else if(item?.name?.includes("预测")){
            colors.push(renderColor[i]);
            return {
                icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z',
                name: item.name
            }
        }
    })

    const energySeries = energyList.map((item) => {
        if(item?.name?.includes("实际")){
            return {
                name:item?.name,
                type: 'line',
                smooth: true,
                lineStyle: {
                    width: 1,
                },
                showSymbol: false,
                emphasis: {
                    focus: 'series'
                },
                data:item.dataList.map((item) => item.value)
            }
        }else if(item?.name?.includes("预测")){
            return  {
                name:item?.name,
                type: 'line',
                smooth: true,
                lineStyle: {
                    width: 1,
                    type:"dashed"
                },
                showSymbol: false,
                emphasis: {
                    focus: 'series'
                },
                data:item.dataList.map((item) => item.value)
            }
        }
    })

    const photovoltaicOptions = photovoltaicList.map((item,i) => {
        const renderColor = ['#FFFEA6','#FFFB00','#FACF05','#FAAD14','#FFAD92','#FF7002']

        if(item?.name?.includes("实际")){
            colors.push(renderColor[i]);
            return {
                icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z',
                name: item.name
            }
        }else if(item?.name?.includes("预测")){
            colors.push(renderColor[i]);
            return {
                icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z',
                name: item.name
            }
        }
    })

    const photovoltaicSeries = photovoltaicList.map((item) => {
        if(item?.name?.includes("实际")){
            return {
                name:item?.name,
                type: 'line',
                smooth: true,
                lineStyle: {
                    width: 1,
                },
                showSymbol: false,
                emphasis: {
                    focus: 'series'
                },
                data:item.dataList.map((item) => item.value)
            }
        }else if(item?.name?.includes("预测")){
            return  {
                name:item?.name,
                type: 'line',
                smooth: true,
                lineStyle: {
                    width: 1,
                    type:"dashed"
                },
                showSymbol: false,
                emphasis: {
                    focus: 'series'
                },
                data:item.dataList.map((item) => item.value)
            }
        }
    })
    
    useImperativeHandle(ref, () => ({
        onRefresh
    }));

    const n = `main3-${new Date().getTime()}`
    
    const onRefresh = async (nodeId,systemId,startDate,endDate) => {
        const data1 = await http.post('system_management/energy_model/energy_storage_model/profitNew',{
            nodeId,
            systemId,
            startDate,
            endDate
        })
        const data2 = await http.post('system_management/energy_model/photovoltaic_model/profitNew',{
            nodeId,
            systemId,
            startDate,
            endDate
        })
        const data3 = await http.post('/system_management/energy_model/photovoltaic_model/profitAllNew',{
            nodeId,
            systemId,
            startDate,
            endDate
        })

        setEnergyList(data1?.data.data)
        setPhotovoltaicList(data2?.data?.data)
        setAllList(data3?.data.data)
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
                    top:'30%'
                },
                color: colors,
                legend: {
                    inactiveColor:  'rgba(174, 174, 174, 1)', 
                    top:25,
                    left:30,
                    itemGap:20,
                    // backgroundColor:'#fff',
                    data: [
                        ...allListOptions?.slice(0, 2),
                        ...energyOptions?.slice(0, 6),
                        ...photovoltaicOptions?.slice(0, 6)
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
                        var relVal = params[0].name
                        for (var i = 0, l = params.length; i < l; i++) {
                          if(isNaN(params[i].value)){
                            relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + '-' + ' 元' 
                          }else{
                            relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + params[i].value + ' 元' 
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
                    alignTicks: true,
                    name:"元",
                    splitNumber:4,
                    minInterval: 5,
                    nameTextStyle:{
                        color:"#FFF",
                        padding: [0,0,0,-35]
                    },
                    boundaryGap: [0, '100%'],
                    axisLabel:{//x坐标轴刻度标签
                        show:true,
                        color:'#FFF',//'#ccc'，设置标签颜色
                    },
                    splitLine:{
                        show:true,
                        lineStyle:{
                            type:'dashed',
                            color:'#8F959E80'
                        }
                    }       
                },
                dataZoom: [
                    {
                        type: 'slider',
                        xAxisIndex: [0],
                        filterMode: 'filter',
                        startValue:0,
                        endValue:100,
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
                   ...allSeries?.slice(0, 2),
                   ...energySeries?.slice(0, 6),
                   ...photovoltaicSeries.slice(0, 6)
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
                strings: ['收益分析'],
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

    },[allSeries,energySeries,photovoltaicSeries])

    useEffect(() => {
        const scroll = document.getElementById('energy-scroll-wrap');
        scroll.scrollTop = scroll.scrollHeight;
    },[isShow])

    return (
        <div className='income-chart-wrap'>
            {
                isShow ? 
                <>
                    <div className='income-chart-title-wrap'>
                        <span className='income-chart-title' ref={message1}></span>
                    </div>
                    <div className="loading-wrapper">
                        <i className='loading-icon'/>
                    </div>
                </> :
                <>
                    <div className='income-chart-title-wrap'>
                        <span className='income-chart-title'>收益分析</span>
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
                    <div className='income-chart-content-wrap'>
                        <div  style={{display:'flex',justifyContent:'center'}}>
                            {
                                <div className="main1" id={n} style={{width:'100%',height:'370px',display:'flex',justifyContent:'center'}}></div>    
                                    // <div className="empty-container">
                                    //     <i className="empty-icon"/>
                                    //     <span className="empty-text">暂无数据</span>
                                    // </div>
                            }   
                        </div>
                    </div>
                </>
            }
        </div>
    )
})



