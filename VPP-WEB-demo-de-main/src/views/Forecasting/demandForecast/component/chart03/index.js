import React, { useEffect } from 'react';
import * as echarts from 'echarts';
import 'echarts-gl';
import './index.scss'
const Chart03 = (props) => {
  const { timerList,realList,baselineList }= props || {} 

  const init = () => {
    var chartDom = document.getElementById('main-03');
    var myChart = echarts.init(chartDom);
    var option;

    var hours =  [{
        value: '2024-03-05 16:15',
        textStyle: {
            fontSize: 8,
            color:'#8F959E'
        }
    },{
        value: '2024-03-05 16:15',
        textStyle: {
            fontSize: 8,
            color:'#8F959E'
        }
    },{
        value: '2024-03-05 16:15',
        textStyle: {
            fontSize: 8,
            color:'#8F959E'
        }
    },{
        value: '2024-03-05 16:15',
        textStyle: {
            fontSize: 8,
            color:'#0092FF'
        }
    },{
        value: '2024-03-05 16:15',
        textStyle: {
            fontSize: 8,
            color:'#0092FF'
        }
    },{
        value: '2024-03-05 16:15',
        textStyle: {
            fontSize: 8,
            color:'#0092FF'

        }
    },{
        value: '2024-03-05 16:15',
        textStyle: {
            fontSize: 8,
            color:'#8F959E'
        }
    },{
        value: '2024-03-05 16:15',
        textStyle: {
            fontSize: 8,
            color:'#8F959E'
        }
    }]

    var redLineArr = [
        [0, 0, 134],
        [0, 0, 48],
        [2, 0, 224],
        [3, 0, 168],
        [4, 0, 460],
        [5, 0, 534],
        [6, 0, 234],
        [7, 0, 86],
    ]

    function formatterHover(params) {
        const { dataIndex } = params
        const timer = timerList[dataIndex].value
        const real = realList[dataIndex].value[2]
        const redLine = baselineList[dataIndex][2]
        const dataString =  
           `<div style="padding-left:5px;height:30px;line-height:30px;color:#fff;font-size:14px">${timer}</div>`
                +

                `<div style="display:flex;color:#fff">
                    <div style="display:flex;align-items:center;margin-right:125px"> <span style="display:inline-block;width:6px;height:6px;background:#A2C2E3;border-radius:6px;margin-right:6px"></span>实际负荷(kW)</div>
                    <span style="">${real}</span>
                </div>`
    
                +

                `<div style="display:flex;color:#fff">
                    <div style="display:flex;align-items:center;margin-right:124px"> <span style="display:inline-block;width:6px;height:6px;background:#E02020;border-radius:6px;margin-right:6px"></span>基线负荷(kW)</div>
                    <span style="">${redLine}</span>
            </div>`

    
        return dataString
    }   
    var series = [];


    series.push({
        stack: true,
        shading: 'lambert',
        emphasis: {
            label: {
                show: false
            }
        },
        type: 'bar3D',
        barSize: [10, 10, 10],
        // barMaxWidth: 30,
        // barSize: 10,
        data: realList
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
            data: timerList,
            nameTextStyle:{
                color:'#8F959E'
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
  },[timerList,realList,baselineList])

  
  return (
    <div style={{width:'100%'}}>
      <div id='main-03' style={{width:'75%',height:'210px',margin:'0 auto'}}></div>
    </div>
  )
};

export default Chart03;
