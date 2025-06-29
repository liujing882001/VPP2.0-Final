import './index.scss'
import * as echarts from 'echarts';
import { useEffect,useState,useRef, useMemo } from 'react'
import { DatePicker,Button,Progress } from 'antd';
import Typed from 'typed.js';
import moment from 'moment';
import {DatePickerRender} from '../fake-date-picker'
import { FakeDatePicker } from '../fake-date-picker'
import dayjs from 'dayjs';
import classNames from 'classnames';
import { getTodayDate } from '../../utils'
const { RangePicker } = DatePicker


export const MainChart = (props) => {
    const { weatherChartList:list,nodeId,systemId,isFinish,setIsFinish,showDatePick,dateRange,priceList,weatherList,powerList,getMainChartData,predStartDate,predEndDate } = props || {}
    const [startDate,setStartDate] = useState('')
    const [endDate,setEndDate] = useState('')
    const [collapsed,setCollapsed] = useState(false)
    const [weatherChartList,setWeatherChartList] = useState([])
    const [isShow,setIsShow] = useState(true)
    const message1 = useRef(null);
    let typed1 = ''

    const priceTimeList = priceList[0]?.priceList?.map((item) => item.ts)
    const longTermList = priceList?.filter(item => item.name === '长协价格')[0]?.priceList?.map((item) => item.price)
    const clearPriceList = priceList?.filter(item => item.name === '日前出清价格')[0]?.priceList?.map((item) => item.price)
    const predClearPriceList = priceList?.filter(item => item.name === '预测日前价格')[0]?.priceList?.map((item) => item.price)

    const weatherListTimeList = weatherList?.map((item) => item.ts)
    const predTtList = weatherList?.map((item) => item.predTt2)
    const rtTtList = weatherList?.map((item) => item.rtTt2)

    const powerTimeList = powerList[0]?.dataList?.map((item) => item.date)
    const actualPowarGenerationList = powerList.filter(item => item.name === '实际发电功率')[0]?.dataList?.map((item) => item.value)
    const predPowarGenerationList = powerList.filter(item => item.name === '预测发电功率')[0]?.dataList?.map((item) => item.value)
    const actualPowarDischargeList = powerList.filter(item => item.name === '实际用电功率')[0]?.dataList?.map((item) => item.value)
    const predPowarDischargeList = powerList.filter(item => item.name === '预测用电功率')[0]?.dataList?.map((item) => item.value)

    const n1 = useRef(`main1-${new Date().getTime()}`)
    const n2 = useRef(`main2-${new Date().getTime()}`)
    const n3 = useRef(`main3-${new Date().getTime()}`)
    const scrollDom = useRef(`main-chart-scroll-${new Date().getTime()}`)
    const [count1, setCount1] = useState(0);
    const [count2, setCount2] = useState(0);
    const [count3, setCount3] = useState(0);
    const [count4, setCount4] = useState(0);
    const [count5, setCount5] = useState(0);
    const [step1Show,setStep1Show] = useState(false)
    const [step2Show,setStep2Show] = useState(false)
    const [step3Show,setStep3Show] = useState(false)

    const swapElements = (arr, index1, index2) => {
        if (index1 >= 0 && index1 < arr.length && index2 >= 0 && index2 < arr.length) {
          var temp = arr[index1];
          arr[index1] = arr[index2];
          arr[index2] = temp;
        }
        return arr;
    }

    useEffect(() => {
        var chartDom = document.getElementById(n1.current);
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
                color: ['#FAAD14','#0092FF','#09FFE6'],
                legend: {
                    inactiveColor:  'rgba(174, 174, 174, 1)', 
                    top:15,
                    left:'center',
                    itemGap:20,
                    // backgroundColor:'#fff',
                    data: [
                        {icon:'path://M1281.997965 588.427164a471.731873 471.731873 0 0 1-913.759491 0Q360.930284 589.370157 353.622094 589.370157H117.874031q-11.551655 0-22.985436-2.357481-11.433781-2.121733-22.160318-6.600946-10.608663-4.479213-20.392207-10.962285-9.547797-6.365198-17.798979-14.616379-8.251182-8.251182-14.61638-17.798979-6.483072-9.665671-10.962285-20.392208-4.479213-10.726537-6.718819-22.160318Q0 483.04778 0 471.496125t2.357481-22.985436q2.121733-11.433781 6.600945-22.160318 4.479213-10.608663 10.962285-20.392207 6.365198-9.547797 14.61638-17.798979 8.251182-8.251182 17.798979-14.61638 9.665671-6.483072 20.392207-10.962285 10.726537-4.479213 22.160318-6.718819Q106.322376 353.622094 117.874031 353.622094h235.748063q7.30819 0 14.61638 0.942992a471.731873 471.731873 0 0 1 913.759491 0Q1289.306155 353.622094 1296.614345 353.622094h235.748062q11.551655 0 22.985437 2.357481 11.433781 2.121733 22.160317 6.600945 10.608663 4.479213 20.392208 10.962285 9.665671 6.365198 17.798979 14.61638 8.251182 8.251182 14.616379 17.798979 6.483072 9.665671 10.962285 20.392207 4.479213 10.726537 6.71882 22.160318Q1650.236439 459.94447 1650.236439 471.496125t-2.357481 22.985436q-2.121733 11.433781-6.600946 22.160318-4.479213 10.608663-10.844411 20.392208-6.483072 9.547797-14.734253 17.798979-8.251182 8.251182-17.798979 14.616379-9.665671 6.483072-20.392208 10.962285-10.726537 4.479213-22.160317 6.71882Q1543.914062 589.370157 1532.362407 589.370157h-235.748062q-7.30819 0-14.61638-0.942993zM1060.866282 471.496125a235.748063 235.748063 0 1 0-471.496125 0 235.748063 235.748063 0 0 0 471.496125 0z',name:"长协价格"},
                        {icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z', name: '日前出清价格'},
                        {icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z', name: '预测日前价格'},
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
                    data:priceTimeList,
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
                            relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + '-' + ' 元/kWh'
                          }else{
                            relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + params[i].value + ' 元/kWh'
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
                    {
                        name:'长协价格',
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 1,
                        },
                        showSymbol: true,
                        // areaStyle: {
                        //     opacity: 0.5,
                        //     color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        //         {
                        //             offset: 0,
                        //             color: '#FAAD14'
                        //         },
                        //         {
                        //             offset: 1,
                        //             color: 'rgba(0, 146, 255, 0)'
                        //         }
                        //     ])
                        // },
                        emphasis: {
                            focus: 'series'
                        },
                        data:longTermList
                    },
                    {
                        name:'日前出清价格',
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 1,
                        },
                        showSymbol: false,
                        // areaStyle: {
                        //     opacity: 0.5,
                        //     color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        //         {
                        //             offset: 0,
                        //             color: '#1586FF'
                        //         },
                        //         {
                        //             offset: 1,
                        //             color: 'rgba(0, 146, 255, 0)'
                        //         }
                        //     ])
                        // },
                        emphasis: {
                            focus: 'series'
                        },
                        data:clearPriceList
                    },
                    {
                        name:'预测日前价格',
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
                        // areaStyle: {
                        //     opacity: 0.5,
                        //     color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        //         {
                        //             offset: 0,
                        //             color: 'rgba(67,227,227)'
                        //         },
                        //         {
                        //             offset: 1,
                        //             color: 'rgba(0, 146, 255, 0)'
                        //         }
                        //     ])
                        // },
                        data:predClearPriceList
                    },
         
                ]
            };
            option && myChart.setOption(option);
        }  
        const onresize = function() {
            myChart?.resize();
        };

        window.addEventListener('resize',onresize, false)

        // if(message1?.current){
        //     // eslint-disable-next-line react-hooks/exhaustive-deps
        //     typed1 = new Typed(message1?.current, {
        //         strings: ['24小时天气信息'],
        //         typeSpeed: 30,
        //         backSpeed: 100,
        //         showCursor: true,
        //         cursorChar: '',
        //         onComplete(self) {
        //             setTimeout(() => {
        //                 setIsShow(false)
        //             },2000)
        //         },
        //     });
        // }

        return () => {
            window.removeEventListener('resize', onresize, false)
        } 

    },[step1Show,priceList])


    useEffect(() => {
        var chartDom = document.getElementById(n2.current);
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
                color: ['#43E3E3','#1586FF'],
                legend: {
                    inactiveColor:  'rgba(174, 174, 174, 1)', 
                    top:15,
                    left:'center',
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
                    data:weatherListTimeList,
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
                          const l = i === 0 || i === 1 ? ' ℃' : ' mm' 
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
                    {
                        name:'实际温度',
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 2,
                        },
                        showSymbol: false,
                        areaStyle: {
                            opacity: 0.5,
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                {
                                    offset: 0,
                                    color: '#43E3E3'
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
                        data:rtTtList
                    },
                    {
                        name:'预测温度',
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 2,
                            type:"dashed"
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
                                    color: '#1586FF'
                                },
                                {
                                    offset: 1,
                                    color: 'rgba(0, 146, 255, 0)'
                                }
                            ])
                        },
                        data:predTtList
                    },
         
                ]
            };
            option && myChart.setOption(option);
        }
  
        const onresize = function() {
            myChart?.resize();
        };

        window.addEventListener('resize',onresize, false)

        // if(message1?.current){
        //     // eslint-disable-next-line react-hooks/exhaustive-deps
        //     typed1 = new Typed(message1?.current, {
        //         strings: ['24小时天气信息'],
        //         typeSpeed: 30,
        //         backSpeed: 100,
        //         showCursor: true,
        //         cursorChar: '',
        //         onComplete(self) {
        //             setTimeout(() => {
        //                 setIsShow(false)
        //             },2000)
        //         },
        //     });
        // }

        return () => {
            window.removeEventListener('resize', onresize, false)
        }

    },[step2Show,weatherList])


    useEffect(() => {
        var chartDom = document.getElementById(n3.current);
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
                color: ['#43E3E3','#0092FF','#8AC2FF','#257DDC'],
                legend: {
                    inactiveColor:  'rgba(174, 174, 174, 1)', 
                    top:15,
                    left:'center',
                    itemGap:20,
                    // backgroundColor:'#fff',
                    data: [
                        {icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z', name: '实际发电功率'},
                        {icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z', name: '预测发电功率'},
                        {icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z', name: '实际用电功率'},
                        {icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z', name: '预测用电功率'},
                    
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
                    data:powerTimeList,
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
                          const l = i === 0 || i === 1 ? ' ℃' : ' mm' 
                          if(isNaN(params[i].value)){
                            relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + '-' + ' kW' 
                          }else{
                            relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + params[i].value  + ' kW' 
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
                    {
                        name:'实际发电功率',
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 1,
                        },
                        showSymbol: false,
                        // areaStyle: {
                        //     opacity: 0.5,
                        //     color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        //         {
                        //             offset: 0,
                        //             color: '#1586FF'
                        //         },
                        //         {
                        //             offset: 1,
                        //             color: 'rgba(0, 146, 255, 0)'
                        //         }
                        //     ])
                        // },
                        emphasis: {
                            focus: 'series'
                        },
                        data:actualPowarGenerationList
                    },
                    {
                        name:'预测发电功率',
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 1,
                            type:"dashed"
                        },
                        showSymbol: false,
                        // areaStyle: {
                        //     opacity: 0.5,
                        //     color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        //         {
                        //             offset: 0,
                        //             color: '#1586FF'
                        //         },
                        //         {
                        //             offset: 1,
                        //             color: 'rgba(0, 146, 255, 0)'
                        //         }
                        //     ])
                        // },
                        emphasis: {
                            focus: 'series'
                        },
                        data:predPowarGenerationList
                    },
                    {
                        name:'实际用电功率',
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 1,
                        },
                        showSymbol: false,
                        emphasis: {
                            focus: 'series'
                        },
                        // areaStyle: {
                        //     opacity: 0.5,
                        //     color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        //         {
                        //             offset: 0,
                        //             color: 'rgba(67,227,227)'
                        //         },
                        //         {
                        //             offset: 1,
                        //             color: 'rgba(0, 146, 255, 0)'
                        //         }
                        //     ])
                        // },
                        data:actualPowarDischargeList
                    },
                    {
                        name:'预测用电功率',
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
                        // areaStyle: {
                        //     opacity: 0.5,
                        //     color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        //         {
                        //             offset: 0,
                        //             color: 'rgba(67,227,227)'
                        //         },
                        //         {
                        //             offset: 1,
                        //             color: 'rgba(0, 146, 255, 0)'
                        //         }
                        //     ])
                        // },
                        data:predPowarDischargeList
                    },
                ]
            };
            option && myChart.setOption(option);
        }
  
        const onresize = function() {
            myChart?.resize();
        };

        window.addEventListener('resize',onresize, false)

        // if(message1?.current){
        //     // eslint-disable-next-line react-hooks/exhaustive-deps
        //     typed1 = new Typed(message1?.current, {
        //         strings: ['24小时天气信息'],
        //         typeSpeed: 30,
        //         backSpeed: 100,
        //         showCursor: true,
        //         cursorChar: '',
        //         onComplete(self) {
        //             setTimeout(() => {
        //                 setIsShow(false)
        //             },2000)
        //         },
        //     });
        // }

        return () => {
            window.removeEventListener('resize', onresize, false)
        }

    },[step3Show,powerList,powerTimeList,actualPowarGenerationList,predPowarGenerationList,actualPowarDischargeList,predPowarDischargeList])

    // useEffect(() => {
    //     const scroll = document.getElementById('energy-scroll-wrap');
    //     scroll.scrollTop = scroll?.scrollHeight;
    // },[isShow])

    useEffect(() => {
        setStartDate(predStartDate)
        setEndDate(predEndDate)
        const interval1 = setInterval(() => {
          setCount1(prevCount1 => {
            if(prevCount1 === 100){
                clearInterval(interval1)
                setStep1Show(true)
                const interval2 = setInterval(() => {
                    setCount2(prevCount2 => {
                        if(prevCount2 === 100){
                            clearInterval(interval2)
                            const scroll = document.getElementById(scrollDom.current);
                            scroll.scrollTo({
                                top: 310,
                                behavior: 'smooth' // 平滑滚动
                            });         
                            const interval3 = setInterval(() => {
                                setCount3(prevCount3 => {
                                    if(prevCount3 === 100){
                                        clearInterval(interval3)
                                        setStep2Show(true)
                                        const interval4 = setInterval(() => {
                                            setCount4(prevCount4 => {
                                                if(prevCount4 === 100){
                                                    clearInterval(interval4)
                                                    const scroll = document.getElementById(scrollDom.current);
                                                    scroll.scrollTo({
                                                        top: 610,
                                                        behavior: 'smooth' // 平滑滚动
                                                    });
                                                    const interval5 = setInterval(() => {
                                                        setCount5(prevCount5 => {
                                                            if(prevCount5 === 100){
                                                                clearInterval(interval5)
                                                                setStep3Show(true)
                                                                setTimeout(() => {
                                                                    setIsFinish(true)
                                                                    setCollapsed(true)                                                              
                                                                }, 1500);
                                                                return 100
                                                            }
                                                            return prevCount5 + 1
                                                        })
                                                    },15)
                                                    return 100
                                                }
                                                return prevCount4 + 1
                                            });
                                        },30)

                                        return 100
                                    }
                                    return prevCount3 + 1
                                });
                            }, 15);  
                            return 100
                        }
                        return prevCount2 + 1
                    });
                }, 30);
                return 100
            }
            return prevCount1 + 1
          });
        }, 15);
        
        return () => clearInterval(interval1);
    }, []);

    return (
        <div className='main-chart-wrap'>
            <div className='animate-header-wrap'>
                <div 
                    className='animate-header' 
                    onClick={() => {
                        setCollapsed(!collapsed)
                    }}
                >   
                    <div>
                        <span className='animate-header-title'>{isFinish ? '数据趋势分析':'数据分析中...'} </span>
                        <i className={classNames('up-arrow',{'down-arrow':collapsed})} />
                    </div>
                    {
                        isFinish && collapsed ? null :       
                        <div className='animate-header-query-wrap' onClick={(e) => e.stopPropagation()}>
                            {
                                <RangePicker
                                    style={{height:'26px',marginRight:'16px'}}
                                    defaultValue={[dayjs(predStartDate, 'YYYY-MM-DD'), dayjs(predStartDate, 'YYYY-MM-DD')]}
                                    onChange={(dates,dateStrings)=> {
                                            setStartDate(dateStrings[0])
                                            setEndDate(dateStrings[1])
                                    }}
                                    format={'YYYY-MM-DD'}
                                />
                            }
                        
                            <Button type="Button" className='animate-header-query-btn' onClick={() => getMainChartData(startDate,endDate)}>查询</Button>
                        </div>
                    }
                </div>
            </div>
            <div className={classNames('animate-wrapper',{'collapsed':collapsed,'no-collapsed':!collapsed})}>
                <div className='animate-content'>
                    <div className='price-wrap' onClick={() => {
                        const scroll = document.getElementById(scrollDom.current);
                        scroll.scrollTo({
                            top: 0,
                            behavior: 'smooth'
                        });     
                    }}>
                        <Progress type="circle" percent={count1} size={30} strokeWidth={8} strokeColor="#0092FF" trailColor="#C1C8D3" format={(percent) => {
                            return percent === 100 ?  <i className='money-icon'/> : <i className='grey-money-icon'/>
                        }}/> 
                        <div className={classNames('price-text',{'important-text':step1Show})}>市场价格分析</div>
                    </div>
                    <Progress style={{width:'100px','flexGrow':'1','position':'relative',top:'-5px'}} percent={count2}  strokeColor="#0092FF" trailColor="#C1C8D3" size={{height:1}} format={() => null}/>
                    <div className='weather-wrap' onClick={() => {
                        const scroll = document.getElementById(scrollDom.current);
                        scroll.scrollTo({
                            top: 300,
                            behavior: 'smooth'
                        });     
                    }}>
                        <Progress type="circle" percent={count3} size={30} strokeWidth={8} strokeColor="#0092FF" trailColor="#C1C8D3" format={(percent) => {
                            return percent === 100 ?  <i className='weather-icon'/> : <i className='grey-weather-icon'/>
                        }}/>  
                        <div className={classNames('price-text',{'important-text':step2Show})}>气象数据分析</div>
                    </div>
                    <Progress style={{width:'100px','flexGrow':'1','position':'relative',top:'-5px'}} percent={count4} strokeColor="#0092FF" trailColor="#C1C8D3" size={{height:1}} format={() => null}/>
                    <div className='power-wrap' onClick={() => {
                        const scroll = document.getElementById(scrollDom.current);
                        scroll.scrollTo({
                            top: 600,
                            behavior: 'smooth' 
                        });     
                    }}>
                        <Progress style={{flexGrow:'0'}} type="circle" percent={count5} size={30} strokeWidth={8} strokeColor="#0092FF" trailColor="#C1C8D3" format={(percent) => {
                            return percent === 100 ?  <i className='power-icon'/> : <i className='grey-power-icon'/>
                        }}/>
                        <div className={classNames('price-text',{'important-text':step3Show})}>发用电功率分析</div>
                    </div>
                </div>
                <div className='main-chart-scroll' id={scrollDom.current}>
                    <div className='trade-price-chart-wrap'>
                        <div className='trade-price-chart-title'>市场价格分析</div>
                        {
                            !step1Show ? <div className='default-loading-icon-wrap' style={{height:'280px'}}>
                                <i className='default-loading-icon'/>
                            </div>: 
                            <div className='trade-price-chart-content-wrap'>
                                <div style={{display:'flex',justifyContent:'center'}}>
                                    <div className="main1" id={n1.current} style={{width:'100%',height:'280px',display:'flex',justifyContent:'center'}}></div>
                                    {/* {
                                        forecastTemperatureList?.length && temperatureList?.length ? 
                                            <div className="main1" id={n} style={{width:'100%',height:'280px',display:'flex',justifyContent:'center'}}></div>    
                                            : 
                                            <div className="empty-container">
                                                <i className="empty-icon"/>
                                                <span className="empty-text">暂无数据</span>
                                            </div>
                                    }    */}
                                </div>
                            </div>
                        }
                    </div>
                    <>
                        {
                            step1Show ?       
                            <div className='weather-chart-wrap'>
                                <div className='weather-price-chart-title'>气象数据分析</div>
                                {
                                    !step2Show ? <div className='default-loading-icon-wrap' style={{height:'280px'}}>
                                        <i className='default-loading-icon'/>
                                    </div>: 
                                    <div className='weather-chart-content-wrap'>
                                        <div style={{display:'flex',justifyContent:'center'}}>
                                            <div className="main2" id={n2.current} style={{width:'100%',height:'280px',display:'flex',justifyContent:'center'}}></div>
                                            {/* {
                                                forecastTemperatureList?.length && temperatureList?.length ? 
                                                    <div className="main1" id={n} style={{width:'100%',height:'280px',display:'flex',justifyContent:'center'}}></div>    
                                                    : 
                                                    <div className="empty-container">
                                                        <i className="empty-icon"/>
                                                        <span className="empty-text">暂无数据</span>
                                                    </div>
                                            }    */}
                                        </div>
                                    </div>
                                }
                            </div>
                            : null
                        }
                    </>
                    {
                        step1Show && step2Show ?       
                        <div className='power-chart-wrap'>
                            <div className='power-chart-title'>发电功率分析</div>
                            {
                                !step3Show ? <div className='default-loading-icon-wrap' style={{height:'280px'}}>
                                    <i className='default-loading-icon'/>
                                </div>: 
                                <div className='power-chart-content-wrap'>
                                    <div style={{display:'flex',justifyContent:'center'}}>
                                        <div className="main3" id={n3.current} style={{width:'100%',height:'280px',display:'flex',justifyContent:'center'}}></div>
                                        {/* {
                                            forecastTemperatureList?.length && temperatureList?.length ? 
                                                <div className="main1" id={n} style={{width:'100%',height:'280px',display:'flex',justifyContent:'center'}}></div>    
                                                : 
                                                <div className="empty-container">
                                                    <i className="empty-icon"/>
                                                    <span className="empty-text">暂无数据</span>
                                                </div>
                                        }    */}
                                    </div>
                                </div>
                            }
                        </div>
                        : null
                    }
                </div>
            </div>
            {/* {
                isShow ? 
                <>
                    <div className='trade-price-chart-title-wrap'>
                        <span className='trade-price-chart-title' ref={message1}></span>
                    </div>
                    <div className="loading-wrapper">
                        <i className='loading-icon'/>
                    </div>
                </> : 
            } */}
        </div>
    )
}