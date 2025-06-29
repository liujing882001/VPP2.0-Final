/* eslint-disable react-hooks/rules-of-hooks */
import './index.scss'
import * as echarts from 'echarts';
import {MainChart} from '../main-chart'
import Typed from 'typed.js';
import { Energy } from '../energy'
import { Dispatch } from '../dispatch';
import { useState,useRef, useEffect } from 'react'
import { DatePicker,Button } from 'antd';
import { WorkingEnergy } from './components/working-energy';
import http from '../../../../../../server/server'
import dayjs from 'dayjs';
import { getTodayDate } from '../../utils'

const { RangePicker } = DatePicker


export const Working = (props) => {
    const { predStartDate,predEndDate,taskCode,workingStrategyList } = props || {}
    const [startDate,setStartDate] = useState(predStartDate)
    const [endDate,setEndDate] = useState(predEndDate)
    const [timeList,setTimeList] = useState([])
    const [allPowerList,setAllPowerList] = useState([])
    const [prevAllPower,setPrevAllPower] = useState([])
    const [strategyList,setStrategyList] = useState([])
    const [tableData,setTableData] = useState([])
    const [nodeNum,setNodeNum] = useState(' ')

    const [isShow,setIsShow] = useState(true)
    const [isShow1,setIsShow1] = useState(false)
    const [isShow2,setIsShow2] = useState(false)

    const [minPower,setMinPower] = useState('')
    const [maxPower,setMaxPower] = useState('')
    const [intervalList,setIntervalList] = useState([])
    const message1 = useRef(null);
    const message2 = useRef(null);
    const message3 = useRef(null)
    let typed1 = ''
    let typed2 = ''
    let typed3 = ''
    const n5 = `main5-${new Date().getTime()}`
    
    const swapElements = (arr, index1, index2) => {
        if (index1 >= 0 && index1 < arr.length && index2 >= 0 && index2 < arr.length) {
          var temp = arr[index1];
          arr[index1] = arr[index2];
          arr[index2] = temp;
        }
        return arr;
    }

    const getRunningCurveList = async (start,end) => {
        const res = await http.post('/tradePower/runningCurve',{
            taskCode,
            "startDate": start,
            "endDate": end,
        })
        if(res?.data?.code === 501){
            getRunningCurveList(start,end)
            return
        }
        const allPower = res?.data?.data?.filter((item) => item.name === '节点总功率')[0]
        const prevAllPower = res?.data?.data?.filter((item) => item.name === '节点总预测功率')[0]
        const timeList1 = allPower?.dataList?.map(item => item.date)
        const powerList1 = allPower?.dataList?.map(item => item.value)
        const prevPowerList1 = prevAllPower?.dataList?.map(item => item.value)
        setTimeList(timeList1)
        setAllPowerList(powerList1)
        setPrevAllPower(prevPowerList1)
    }

    const getDeclareForOperation = async (start,end) => {
        const res = await http.post('/tradePower/declareForOperation',{
            taskCode,
            queryDate: predStartDate,
        })
        if(res?.data?.code === 501){
            getDeclareForOperation(start,end)
            return
        }
        const tempList = res?.data?.data?.list?.filter((item) => item.date === predStartDate)
        const intervalList = res?.data?.data?.intervalInfo?.interval
        const maxPower = res?.data?.data?.intervalInfo?.maxPower
        const minPower = res?.data?.data?.intervalInfo?.minPower
        setMinPower(minPower)
        setMaxPower(maxPower)
        setIntervalList(intervalList)
        setTableData(tempList)
        workingStrategyList.current = res?.data?.data?.list
        setNodeNum(res.data.data?.nodeNum)
    }

    useEffect(() => {
        getRunningCurveList(predStartDate,predStartDate)
        getDeclareForOperation(predStartDate,predEndDate)
    },[])

    useEffect(() => {
        var chartDom = document.getElementById(n5);
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
                color: ['#43E3E3','#257DDC'],
                legend: {
                    inactiveColor:  'rgba(174, 174, 174, 1)', 
                    top:15,
                    left:30,
                    itemGap:20,
                    // backgroundColor:'#fff',
                    data: [
                        {icon: 'path://M512 0Q461.824 0 412.16 10.24 362.496 18.944 315.904 38.4 269.824 58.368 227.328 87.04 185.856 114.176 150.016 150.016q-35.84 35.84-63.488 77.312Q58.368 269.312 38.912 315.904 19.456 362.496 10.24 412.16 0 461.824 0 512t10.24 99.84q9.216 49.664 28.672 96.256 19.456 46.08 47.616 88.576 27.648 41.472 63.488 77.312 35.84 35.84 77.312 63.488 41.984 28.16 88.576 47.616 46.592 19.456 96.256 29.184Q461.824 1024 512 1024h5120a512 512 0 1 0 0-1024H512z', name: '节点总功率'},
                        {icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z', name: '节点总预测功率'},
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
                            relVal += '<br/>' + list[i].marker + list[i].seriesName + ' : ' + '-' + ' kW' 
                          }else{
                            relVal += '<br/>' + list[i].marker + list[i].seriesName + ' : ' + list[i].value + ' kW' 
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
                        name:'节点总功率',
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
                        data:allPowerList
                    },
                    {
                        name:'节点总预测功率',
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
                                    color: '#257DDC'
                                },
                                {
                                    offset: 1,
                                    color: 'rgba(0, 146, 255, 0)'
                                }
                            ])
                        },
                        data:prevAllPower
                    },
                ]
            };
            option && myChart.setOption(option);
        }
  
        const onresize = function() {
            myChart.resize();
        };

        window.addEventListener('resize',onresize, false)
        return () => {
            window.removeEventListener('resize', onresize, false)
        }

    },[timeList,allPowerList,prevAllPower,isShow1])

    // useEffect(() => {
    //     const scroll = document.getElementById('energy-scroll-wrap');
    //     scroll.scrollTop = scroll.scrollHeight;
    // },[isShow])

    useEffect(() => {
        setTimeout(() => {
            if(message2?.current){
                setIsShow1(true)
                const scroll = document.getElementById('scroll-wrap');
                // 设置滚动的顶点坐标为滚动的总高度
                scroll.scrollTop = scroll.scrollHeight;
                // eslint-disable-next-line react-hooks/exhaustive-deps
                typed2 = new Typed(message2?.current, {
                    strings: ['运行曲线分析'],
                    typeSpeed: 10,
                    backSpeed: 50,
                    showCursor: true,
                    cursorChar: '',
                    onComplete(self) {
                    },
                });
            }
            
            setTimeout(() => {
                setIsShow2(true)
                const scroll = document.getElementById('scroll-wrap');
                // 设置滚动的顶点坐标为滚动的总高度
                scroll.scrollTop = scroll.scrollHeight;
                if(message1?.current){
                    setIsShow(false)
                    const scroll = document.getElementById('scroll-wrap');
                    // 设置滚动的顶点坐标为滚动的总高度
                    scroll.scrollTop = scroll.scrollHeight;
                    // eslint-disable-next-line react-hooks/exhaustive-deps
                    typed1 = new Typed(message1?.current, {
                        strings: ['好的，这是根据策略推荐的申报信息，你可以对“申报负荷（kW）”进行编辑，并提交申报'],
                        typeSpeed: 10,
                        backSpeed: 50,
                        showCursor: true,
                        cursorChar: '',
                        onComplete(self) {
                          setIsShow(false)
                          const scroll = document.getElementById('scroll-wrap');
                          // 设置滚动的顶点坐标为滚动的总高度
                          scroll.scrollTop = scroll.scrollHeight;
                          props?.setIsFourCanCLick(true)
                        },
                    });
                }

                if(message3?.current){
                    setIsShow2(true)
                    const scroll = document.getElementById('scroll-wrap');
                    // 设置滚动的顶点坐标为滚动的总高度
                    scroll.scrollTop = scroll.scrollHeight;
                    // eslint-disable-next-line react-hooks/exhaustive-deps
                    typed2 = new Typed(message2?.current, {
                        strings: ['申报运行曲线'],
                        typeSpeed: 10,
                        backSpeed: 50,
                        showCursor: true,
                        cursorChar: '',     
                        onComplete(self) {
                        },
                    });
                }
            }, 2000);
        }, 2000);
    },[])
    
    return (
        <>
            <div className='working-wrapper'>
                <>{ isShow ? <i className='working-robot-gif-icon'/> : <i className='working-robot-icon'/> }</>
                    <div className='main-strategy'>
                        <div className='main-strategy-title'>
                            <span ref={message1}>{isShow ? '数据分析中...' : ''}</span>
                        </div>
                        <div className="main-strategy-content">
                            <div className='main-chart-wrap'>
                            <div className='working-chart-wrap'>
                                <div className='working-chart-title-wrap'>
                                    <span className='working-chart-title' ref={message2}>{!isShow1 ? '运行曲线分析中...' : ''}</span>
                                    <div className='working-chart-query-wrap'>
                                        <RangePicker
                                            style={{height:'26px',marginRight:'16px'}}
                                            defaultValue={[dayjs(predStartDate, 'YYYY-MM-DD'), dayjs(predStartDate, 'YYYY-MM-DD')]}
                                            onChange={(dates,dateStrings)=> {
                                                setStartDate(dateStrings[0])
                                                setEndDate(dateStrings[1])
                                            }}
                                            format={'YYYY-MM-DD'}
                                        />
                                        <Button type="Button" className='working-chart-query-btn' onClick={() => {
                                            getRunningCurveList(startDate,endDate)
                                        }}>查询</Button>
                                    </div>
                                </div>
                                {
                                    !isShow1 ? 
                                    <div className='working-loading-icon-wrap' style={{height:'28px'}}>
                                        <i className='working-loading-icon'/>
                                    </div>: 
                                    <div className='working-chart-content-wrap'>
                                        <div style={{display:'flex',justifyContent:'center'}}>
                                            <div className="main1" id={n5} style={{width:'100%',height:'280px',display:'flex',justifyContent:'center'}}></div>
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
                            {
                                isShow1 ? 
                                <>
                                    <div className='working-chart-wrap'>
                                        {
                                             !isShow2 ? 
                                             <>
                                                <div className='working-chart-title-wrap'>
                                                    <span className='working-chart-title' ref={message3}>{!isShow2 ? '申报运行曲线分析中...' : ''}</span>
                                                </div>
                                                <div className='working-loading-icon-wrap' style={{height:'28px'}}>
                                                    <i className='working-loading-icon'/>
                                                </div>
                                             </>
                                              : <WorkingEnergy 
                                                  maxPower={maxPower}
                                                  minPower={minPower}
                                                  intervalList={intervalList}
                                                  tableData={tableData} 
                                                  nodeNum={nodeNum} 
                                                  setTableData={setTableData} 
                                                  strategyList={strategyList}
                                                  predStartDate={predStartDate}
                                                  predEndDate={predEndDate}
                                                  taskCode={taskCode}
                                                  setStrategyList={setStrategyList}
                                                  workingStrategyList={workingStrategyList}
                                                  setIntervalList={setIntervalList}
                                                  setMaxPower={setMaxPower}
                                                  setMinPower={setMinPower}
                                                /> 
                                        }
                                    </div>
                                </> : null
                            }
                        </div>
                    </div>
                </div>
            </div> 
        </>
    )
}