import * as echarts from 'echarts';
import { useEffect, useState,useRef, useMemo } from 'react';
import './index.scss'
import { Button, DatePicker } from 'antd';
import { Weather } from '../weather'
import dayjs from 'dayjs';
import Typed from 'typed.js';
import http from '../../../../../../server/server'
import { Income } from '../income'
const { RangePicker } = DatePicker;

export const Strategy = (props) => {
    const n = `main-${new Date().getTime()}`
    const { strategyStartDate,strategyEndDate,energyBlockList:list ,nodeId,systemId,tempList,isShow2,isShow3} = props || {}
    const childRef = useRef(null)
    const childRef1 = useRef(null)
    const startDate = useRef(strategyStartDate)
    const endDate = useRef(strategyEndDate)
    const [isShow,setIsShow] = useState(true)

    const [energyBlockList,setEnergyBlockList] = useState([...list])

    const colors = []

    const loadList = energyBlockList.filter((item) => item.type === 'load')

    const photovoltaicList = energyBlockList.filter((item) => item.type === 'pv')

    const energyList = energyBlockList.filter((item) => item.type === 'energy')

    const socList = energyBlockList.filter((item) => item.type === 'soc')

    const socOption = socList.map((item,i) => {
        const renderColor = ['#AB98FF','#6846FF','#DFBCFF','#7C3BFF']
        colors.push(renderColor[i])
        return  {
            icon: 'path://M1281.997965 588.427164a471.731873 471.731873 0 0 1-913.759491 0Q360.930284 589.370157 353.622094 589.370157H117.874031q-11.551655 0-22.985436-2.357481-11.433781-2.121733-22.160318-6.600946-10.608663-4.479213-20.392207-10.962285-9.547797-6.365198-17.798979-14.616379-8.251182-8.251182-14.61638-17.798979-6.483072-9.665671-10.962285-20.392208-4.479213-10.726537-6.718819-22.160318Q0 483.04778 0 471.496125t2.357481-22.985436q2.121733-11.433781 6.600945-22.160318 4.479213-10.608663 10.962285-20.392207 6.365198-9.547797 14.61638-17.798979 8.251182-8.251182 17.798979-14.61638 9.665671-6.483072 20.392207-10.962285 10.726537-4.479213 22.160318-6.718819Q106.322376 353.622094 117.874031 353.622094h235.748063q7.30819 0 14.61638 0.942992a471.731873 471.731873 0 0 1 913.759491 0Q1289.306155 353.622094 1296.614345 353.622094h235.748062q11.551655 0 22.985437 2.357481 11.433781 2.121733 22.160317 6.600945 10.608663 4.479213 20.392208 10.962285 9.665671 6.365198 17.798979 14.61638 8.251182 8.251182 14.616379 17.798979 6.483072 9.665671 10.962285 20.392207 4.479213 10.726537 6.71882 22.160318Q1650.236439 459.94447 1650.236439 471.496125t-2.357481 22.985436q-2.121733 11.433781-6.600946 22.160318-4.479213 10.608663-10.844411 20.392208-6.483072 9.547797-14.734253 17.798979-8.251182 8.251182-17.798979 14.616379-9.665671 6.483072-20.392208 10.962285-10.726537 4.479213-22.160317 6.71882Q1543.914062 589.370157 1532.362407 589.370157h-235.748062q-7.30819 0-14.61638-0.942993zM1060.866282 471.496125a235.748063 235.748063 0 1 0-471.496125 0 235.748063 235.748063 0 0 0 471.496125 0z', name: `${item.name} ${item.suffix}`
        }
    })

    const loadOption = loadList.map((item,i) => {
        const renderColor = ['#03C5B3','#47FFFF','#8EDFFF','#1586FF','#00B7FF','#014CE2']
        colors.push(renderColor[i])
        if(item?.suffix?.includes("实际")){
            return {
                icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z', name: `${item.name}${item.suffix}`
            }
        }else{
            return {
                icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z', name: `${item.name}${item.suffix}`
            }
        }  
    })
    
    const energyOption = energyList.map((item,i) => {
        const renderColor = ['#ACFDB9','#6CFF5F','#15FF00','#00FF95','#00BC6E','#51FFD6']
        colors.push(renderColor[i])
        if(item?.suffix?.includes("实际")){
            return {
                icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z', name: `${item.name}${item.suffix}`
            }
        }else{
            return {
                icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z', name: `${item.name}${item.suffix}`
            }
        }  
    })

    const photovoltaicOption = photovoltaicList.map((item,i) => {
        const renderColor = ['#FFFEA6','#FFFB00','#FACF05','#FAAD14','#FFAD92','#FF7002']
        colors.push(renderColor[i])
        if(item?.suffix?.includes("实际")){
            return {
                icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z', name: `${item.name}${item.suffix}`
            }
        }else{
            return {
                icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z', name: `${item.name}${item.suffix}`
            }
        }  
    })

    const socSeries = socList.map((item) => {
        return  {
            name:`${item?.name} ${item?.suffix}`,
            type: 'line',
            smooth: false,
            lineStyle: {
                width: 1,
                type: item?.suffix?.includes("实际") ? 'solid' :'solid',
            },
            showSymbol:true,
            emphasis: {
                focus: 'series'
            },
            show:item?.show,
            yAxisIndex: 1,
            data:item?.dataList?.map((item) => {
                if(item.value === null){
                    return item.value
                }else{
                    return parseFloat((item.value * 100).toFixed(2));
                }
            })
        }
    })

    const loadSeries = loadList.map((item,i) => {
        const areaStyleColor = ['rgba(3, 197, 179, .5)','rgba(71, 255, 255, .5)','rgba(142, 223, 255,.5)','rgba(21, 134, 255,.5)','rgba(0, 183, 255,.5)','rgba(1, 76, 226,.5)']
        const areaStyle = {
            opacity: 0.7,
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                    offset: 0,
                    color: areaStyleColor[i]
                },
                {
                    offset: 1,
                    color: 'rgba(0, 146, 255, 0)'
                }
            ])
        }
        return  {
            name:`${item?.name}${item?.suffix}`,
            type: 'line',
            smooth: false,
            lineStyle: {
                width: 1,
                type: item?.suffix?.includes("实际") ? 'solid' :'dashed',
            },
            showSymbol: false,
            emphasis: {
                focus: 'series'
            },
            areaStyle: areaStyle,
            show:item?.show,
            yAxisIndex: 0,
            data:item?.dataList?.map((item) => item.value?.toFixed(2))
        }
    })
    const energySeries = energyList.map((item,i) => {
        return  {
            name:`${item?.name}${item?.suffix}`,
            type: 'line',
            smooth: false,
            lineStyle: {
                width: 1,
                type: item?.suffix?.includes("实际") ? 'solid' :'dashed',
            },
            showSymbol: false,
            emphasis: {
                focus: 'series'
            },
            areaStyle: null,
            show:item?.show,
            yAxisIndex: 0,
            data:item?.dataList?.map((item) => item.value?.toFixed(2))
        }
    })

    const photovoltaicSeries = photovoltaicList.map((item,i) => {
        return  {
            name:`${item?.name}${item?.suffix}`,
            type: 'line',
            smooth: false,
            lineStyle: {
                width: 1,
                type: item?.suffix?.includes("实际") ? 'solid' :'dashed',
            },
            showSymbol: false,
            emphasis: {
                focus: 'series'
            },
            areaStyle: null,
            show:item?.show,
            yAxisIndex: 0,
            data:item?.dataList?.map((item) => item.value?.toFixed(2))
        }
    })
    const defaultSelected = {}

    energyBlockList.forEach((item) => {
        if(!item?.show){
            if(item.type === 'soc'){
                defaultSelected[`${item.name} ${item.suffix}`] = false 
            }else{
                defaultSelected[`${item.name}${item.suffix}`] = false
            }
        }
    })
    
    const timeList = (energyBlockList[0]?.dataList || []).map((item) => {
        return item.date
    })

    const swapElements = (arr, index1, index2) => {
        if (index1 >= 0 && index1 < arr.length && index2 >= 0 && index2 < arr.length) {
          var temp = arr[index1];
          arr[index1] = arr[index2];
          arr[index2] = temp;
        }
        return arr;
    }
    
    const message1 = useRef(null);
    let typed1 = ''

    const refresh = async (nodeId,systemId,startDate,endDate,energyFore) => {
        if(childRef?.current){
            childRef?.current?.onRefresh(nodeId,systemId,startDate,endDate);
        }
        if(childRef1?.current){
            childRef1?.current?.onRefresh(nodeId,systemId,startDate,endDate);
        }
        const energyBlockRes = await http.post('system_management/energy_model/energy_storage_model/energyBlockTrendNew',{
            nodeId,
            systemId,
            startDate,
            endDate,
            energyFore
        })
        const energyBlockList = energyBlockRes?.data?.data || []      
        setEnergyBlockList([...energyBlockList])
    }
    
    useEffect(() => {
        var chartDom = document.getElementById(n);
        if(chartDom){
            var myChart = echarts.init(chartDom);
            if (myChart != null && myChart.dispose) {
                myChart.dispose();
            }
            myChart = echarts.init(chartDom);
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
                    top:'25%'
                },
                color: colors,
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
                            let l = params[i].seriesName.includes('soc')||params[i].seriesName.includes('SOC') ? ' %' : ' kW' 
                            

                            if(isNaN(params[i].value)){
                              relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + '-' + l
                            }else{
                              relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + params[i].value + l 
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
                yAxis: [
                    {
                        type: 'value',
                        position: 'left',
                        alignTicks: true,
                        name:"kW",
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
                    {
                        type: 'value',
                        position: 'right',
                        alignTicks: true,
                        name:"%",
                        nameTextStyle:{
                            color:"#FFF",
                            padding: [0,0,0,30]
                        },
                        minInterval: 5,
                        splitNumber:4,
                        boundaryGap: [0, '0%'],
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
                ],
                legend: {
                    inactiveColor:  'rgba(174, 174, 174, 1)', // 不可用状态的颜色为半透明红色
                    top:15,
                    left:30,
                    padding:[0,5,5],
                    itemGap:20,
                    itemWidth:15,
                    itemHeight: 15,
                    selected: defaultSelected,
                    textStyle:{
                        color:"#fff"
                    },
                    data: [
                        ...socOption,
                        ...loadOption,
                        ...energyOption,
                        ...photovoltaicOption
                    ],
                    height:'100px',
                },
                dataZoom: [
                    {
                        type: 'slider',
                        xAxisIndex: [0],
                        filterMode: 'filter',
                        showDetail:true,
                        borderColor: '#fff',
                        selectedDataBackground:{
                            lineStyle:{
                                color:'#0092FF'
                            },
                            areaStyle:{
                                opacity:0
                            }
                        },
                        startValue:0,
                        endValue:95,
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
                    ...socSeries,
                    ...loadSeries,
                    ...energySeries,
                    ...photovoltaicSeries
                ]
            };
            option && myChart.setOption(option);

            energyBlockList?.forEach((item) => {
                           if(item?.show){
                    myChart.dispatchAction({
                        type: 'legendSelect',
                        name: item.type === 'soc' ? `${item.name} ${item.suffix}` : `${item.name}${item.suffix}`
                    })
                }else{
                    myChart.dispatchAction({
                        type: 'legendUnSelect',
                        name: item.type === 'soc' ? `${item.name} ${item.suffix}` : `${item.name}${item.suffix}`
                    })
                }
            })
        }

        const onresize = function() {
            myChart.resize();
        };

        window.addEventListener('resize',onresize, false)

        if(message1?.current){
            // eslint-disable-next-line react-hooks/exhaustive-deps
            typed1 = new Typed(message1?.current, {
                strings: ['能量块趋势分析'],
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
    },[socSeries,loadSeries,energySeries,photovoltaicSeries,energyBlockList,list,loadList,photovoltaicList,energyList,socList])

    useEffect(() => {
        const scroll = document.getElementById('energy-scroll-wrap');
        scroll.scrollTop = scroll.scrollHeight;
    },[isShow])

    return (
        <div className='strategy-chart-wrap'>
            {
                isShow ?  
                <>  
                    <div className='strategy-chart-title-wrap'>
                        <span className='strategy-chart-title' ref={message1}></span>
                    </div>
                    <div className="loading-wrapper">
                        <i className='loading-icon'/>
                    </div>
                </> : 
                <>
                    <div className='strategy-chart-title-wrap'>
                        <span className='strategy-chart-title'>能量块趋势分析</span>
                        <div className='strategy-chart-query-wrap'>
                            <RangePicker className='strategy-chart-query-time'
                                defaultValue={[startDate.current && dayjs(startDate.current, 'YYYY-MM-DD'),endDate.current && dayjs(endDate.current, 'YYYY-MM-DD')]} 
                                onChange={(data,dataString) => {
                                    const start = dataString[0]
                                    const end = dataString[1]
                                    startDate.current = start
                                    endDate.current = end
                                }}
                            />
                            <Button type="Button" className='strategy-chart-query-btn' onClick={() => refresh(nodeId,systemId,startDate.current,endDate.current,tempList)}>查询</Button>
                        </div>
                    </div>
                    <div className='strategy-chart-content-wrap'>
                            <div style={{display:'flex',justifyContent:'center'}}>
                                {
                                    <div className="main" id={n} style={{height:'370px',display:'flex',justifyContent:'center'}}></div>
                                    // <div className="empty-container">
                                    //     <i className="empty-icon"/>
                                    //     <span className="empty-text">暂无数据</span>
                                    // </div>
                                }   
                            </div>
                    </div>
                    {isShow2 ? <Weather {...props} isShow2={isShow2} ref={childRef} /> : null}
                    {isShow2 && isShow3 ? <Income {...props} ref={childRef1}/> : null} 
                </>
            }
  
        </div>
    )
}

