import './index.scss'
import * as echarts from 'echarts';
import { useEffect,useState,useRef } from 'react'
import { DatePicker,Button } from 'antd';
import Typed from 'typed.js';

import dayjs from 'dayjs';
const { RangePicker } = DatePicker

export const Dispatch = (props) => {
    const { curveDataList } = props || {}
    const [isShow2,setIsShow2] = useState(true)
    const message1 = useRef(null);
    let typed1 = ''
    const n4 = `main4-${new Date().getTime()}`


    const timeList = (curveDataList || [])[0]?.dataList?.map((item) => {
        return item.date.split(' ')[1]
    })

    const pred001 = curveDataList?.filter(item => item?.name === '储能预测功率')[0]?.dataList?.map((item) => item.value)

    const real001 =  curveDataList?.filter(item => item?.name === '储能实际功率')[0]?.dataList?.map((item) => item.value)
    
    const swapElements = (arr, index1, index2) => {
        if (index1 >= 0 && index1 < arr.length && index2 >= 0 && index2 < arr.length) {
          var temp = arr[index1];
          arr[index1] = arr[index2];
          arr[index2] = temp;
        }
        return arr;
    }

    useEffect(() => {
        var chartDom = document.getElementById(n4);
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
                color: ['#A1FFB0'],
                legend: {
                    inactiveColor:  'rgba(174, 174, 174, 1)', 
                    top:15,
                    left:30,
                    itemGap:20,
                    // backgroundColor:'#fff',
                    data: [
                        {icon: 'path://M0 479.483806Q0 432.494393 9.589676 385.984464 17.740901 339.474534 35.961285 295.841508 54.661154 252.687966 81.512247 212.89081 106.924889 174.052621 140.488755 140.488755q33.563866-33.563866 72.402055-59.455992Q252.208482 54.661154 295.841508 36.440769 339.474534 18.220385 385.984464 9.589676 432.494393 0 479.483806 0h479.483805q46.989413 0 93.499343 9.589676 46.509929 8.630709 90.142955 26.851093 43.153543 18.220385 82.950698 44.591994 38.838188 25.892126 72.402055 59.455992 33.563866 33.563866 59.455992 72.402055 26.371609 39.317672 44.591994 82.950698 18.220385 43.633026 27.330577 90.142956Q1438.451417 432.494393 1438.451417 479.483806t-9.589676 93.499342q-8.630709 46.509929-26.851093 90.142955-18.220385 43.153543-44.591994 82.950699-25.892126 38.838188-59.455992 72.402054-33.563866 33.563866-72.402055 59.455992-39.317672 26.371609-82.950698 44.591994-43.633026 18.220385-90.142955 27.330577Q1005.957024 958.967611 958.967611 958.967611H479.483806q-46.989413 0-93.499342-9.589676-46.509929-8.630709-90.142956-26.851093-43.153543-18.220385-82.950698-44.591994-38.838188-25.892126-72.402055-59.455992-33.563866-33.563866-59.455992-72.402054-26.371609-39.317672-44.591994-82.950699-18.220385-43.633026-27.330577-90.142955Q0 526.473219 0 479.483806z m2157.677126 0a479.483806 479.483806 0 0 0 479.483805 479.483805h479.483806a479.483806 479.483806 0 0 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483805 479.483806zM4315.354251 479.483806a479.483806 479.483806 0 0 0 479.483806 479.483805h479.483806a479.483806 479.483806 0 1 0 0-958.967611h-479.483806a479.483806 479.483806 0 0 0-479.483806 479.483806z', name: '储能预测功率'},                    
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
                            relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + '-' + ' kW'
                          }else{
                            relVal += '<br/>' + params[i].marker + params[i].seriesName + ' : ' + params[i].value + ' kW'
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
                        // selectedDataBackground:{
                        //     lineStyle:{
                        //         color:'#0092FF'
                        //     },
                        //     areaStyle:{
                        //         opacity:0
                        //     }
                        // },
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
                        name:'储能预测功率',
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
                        data:pred001
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

    },[curveDataList, n4, pred001, real001, timeList])
    // useEffect(() => {
    //     const scroll = document.getElementById('energy-scroll-wrap');
    //     scroll.scrollTop = scroll.scrollHeight;
    // },[isShow])

    useEffect(() => {
        if(message1?.current){
            // eslint-disable-next-line react-hooks/exhaustive-deps
            typed1 = new Typed(message1?.current, {
                strings: ['调度趋势分析中...'],
                typeSpeed: 30,
                backSpeed: 100,
                showCursor: true,
                cursorChar: '',
                onComplete(self) {
                    setTimeout(() => {
                        setIsShow2(false)
                        const scroll = document.getElementById('scroll-wrap');
                        // 设置滚动的顶点坐标为滚动的总高度
                        scroll.scrollTop = scroll.scrollHeight;
                    },2000)
                },
            });
        }
    },[])

    return (

        <div className='dispatch-chart-wrap'>
            <div className='dispatch-chart-title-wrap'>
                <span ref={message1} className='dispatch-chart-title'>{isShow2 ? '':"调度趋势分析"}</span>
            </div>
            {
                isShow2 ? <div className='loading-wrapper'><i className='loading-icon'/></div> :
                        <div className='dispatch-chart-content-wrap'>
                            <div key={new Date().getTime()} style={{display:'flex',justifyContent:'center'}}>
                                <div className="main1" id={n4} style={{width:'100%',height:'280px',display:'flex',justifyContent:'center'}}></div>
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
    )
}