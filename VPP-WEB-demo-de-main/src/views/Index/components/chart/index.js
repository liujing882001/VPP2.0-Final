import React, { useEffect } from 'react';
import * as echarts from 'echarts';
import './index.scss'
import 'echarts-gl';

const Chart = (props) => {
    const { timerList=[],baselineList=[],topList=[],bottomList=[],predictionList=[] } = props || {}
    console.log(topList,bottomList)
    const n = `main-${new Date().getTime()}`
    const init = () => {
    var chartDom = document.getElementById(n);
    var myChart = echarts.init(chartDom);
    var option; 
    
    function formatterHover(params) {
        const { dataIndex } = params
        const timer = timerList[dataIndex].value
        const redLine = baselineList[dataIndex][2]
        const prediction = bottomList[dataIndex].value[2] + topList[dataIndex].value[2] ? bottomList[dataIndex].value[2] + topList[dataIndex].value[2] : predictionList[dataIndex].value[2]
        const bottom = bottomList[dataIndex].value[2]

        const top =  topList[dataIndex].value[2]


        const normalReturn =  
           `<div style="padding-left:5px;height:30px;line-height:30px;color:#fff;font-size:14px">${timer}</div>`
                +

                `<div style="display:flex;color:#fff">
                    <div style="display:flex;align-items:center;margin-right:125px"> <span style="display:inline-block;width:6px;height:6px;background:#A2C2E3;border-radius:6px;margin-right:6px"></span>预测负荷(kW)</div>
                    <span style="">${prediction}</span>
                </div>`
    
                +

                `<div style="display:flex;color:#fff">
                    <div style="display:flex;align-items:center;margin-right:124px"> <span style="display:inline-block;width:6px;height:6px;background:#E02020;border-radius:6px;margin-right:6px"></span>基线负荷(kW)</div>
                    <span style="">${redLine}</span>
            </div>`
        
        const specialReturn = 
            `<div style="padding-left:5px;height:30px;line-height:30px;color:#fff;font-size:14px">${timer}</div>`
                +

                `<div style="display:flex;color:#fff">
                    <div style="display:flex;align-items:center;margin-right:125px"> <span style="display:inline-block;width:6px;height:6px;background:#A2C2E3;border-radius:6px;margin-right:6px"></span>预测负荷(kW)</div>
                    <span style="">${prediction}</span>
                </div>`

                +
                `<div style="display:flex;color:#fff;justify-content:space-between">
                    <div style="display:flex;align-items:center;margin-right:68px"> <span style="display:inline-block;width:6px;height:6px;background:#25C5AF;border-radius:6px;margin-right:6px"></span>预测调节负荷(kW)</div>
                    <span style="">${top}</span>
                </div>`

                +
                
                `<div style="display:flex;color:#fff;justify-content:space-between">
                    <div style="display:flex;align-items:center;margin-right:54px"> <span style="display:inline-block;width:6px;height:6px;background:#1D4075;border-radius:6px;margin-right:6px"></span>预测调节后负荷(kW)</div>
                    <span style="">${bottom}</span>
                </div>`

                +
                
                `<div style="display:flex;color:#fff">
                    <div style="display:flex;align-items:center;margin-right:124px"> <span style="display:inline-block;width:6px;height:6px;background:#E02020;border-radius:6px;margin-right:6px"></span>基线负荷(kW)</div>
                    <span style="">${redLine}</span>
            </div>`

        return timerList[dataIndex]?.textStyle?.color === '#0092FF' ? specialReturn : normalReturn
    }

    var series = [];

    series.push({
        stack: true,
        shading: 'lambert',
        emphasis: {
            label: {
                show: false
            },
        },
        type: 'bar3D',
        barSize: [10, 10, 10],
        data: bottomList
    });

    series.push({
        stack: true,
        shading: 'lambert',
        emphasis: {
            label: {
                show: false
            },
        },
        type: 'bar3D',
        barSize: [10, 10, 10],
        data:topList 
    });

    series.push({
        type: 'line3D',
        data: baselineList,
        stack: 'stack',
        lineStyle: {
            width: 4,
            color: 'red'
        }
    });

    var option;

    option = {
        tooltip: {
            trigger: 'item',
            triggerOn: 'mousemove',
            enterable: true, //鼠标是否可进入提示框浮层中
            formatter: formatterHover, //修改鼠标悬停显示的内容
            backgroundColor:'#302F39',
            borderColor:'#302F39',
            className:'base-tool-tip',
            enterable:true
        },
        xAxis3D: {
            name: '',
            type: 'category',
            nameTextStyle:{
                color:'#8F959E',
                fontSize:13 
            },
            data: timerList,
            axisLine:{
                show:true,
                interval:0,
                lineStyle:{
                    color:'#8F959E'
                }
            },
            axisLabel:{
                interval:0,
                textStyle:{
                    color:'#8F959E'
                }
            }
        },
        yAxis3D: {
            name: '',
            type: 'category',
            data: [''],
            axisLine:{
                show:true,
                interval:0,
                lineStyle:{
                    color:'#8F959E'
                }
            },
            axisLabel:{
                interval:0,
                textStyle:{
                    color:'#8F959E'
                }
            }
        },
        zAxis3D: {
            name: '功率(kW)',
            nameGap:30,
            type: 'value',
            nameTextStyle:{
                color:'#8F959E',
                fontSize:12
            },
            axisLine:{
                show:true,
                interval:0,
                lineStyle:{
                    color:'#8F959E'
                }
            },
            axisLabel:{
                interval:0,
                textStyle:{
                    color:'#8F959E'
                }
            }
        },
        grid3D: {
            top:0,
            show: true,
            axisLine: {
                show: true,
                lineStyle: {
                    width: 1
                }
            },
            viewControl: {
                // 设置默认视角
                beta: 10,
                alpha: 3,
                // 视角距离
                distance: 158
            },
            splitLine: {
                show: false
            },
            axisPointer: {
                show: false
            },
            borderWidth: 0,
            boxWidth: 250,
            boxDepth: 30,
            boxHeight:100,
            light: {
            main: {
                intensity: 1.2,
                shadow: true
            },
            ambient: {
                intensity: 0.3
            }
            }
        },
        series: [...series]
    };

    option && myChart.setOption(option);

    option &&  myChart.dispatchAction({
        type: 'changeView',
        view: {
            rotate: 50,
            distance: 10000000
        }
    });
    
  }

  useEffect(() => {
    init()
  },[timerList,baselineList,topList,bottomList])

  
  return (
    <div style={{width:'100%'}}>
      <div id={n} style={{width:'75%',height:'210px',margin:'0 auto'}}></div>
    </div>
  )
};

export default Chart;


