import React, { useEffect } from 'react';
import * as echarts from 'echarts';
// import echarts  from '../../../../echarts.js'
import 'echarts-gl';
import './index.scss'
const Chart03 = (props) => {
  const { timerList,baselineList,predictionList } = props || {}
  const init = () => {
    var chartDom = document.getElementById('main-03');
    var myChart = echarts.init(chartDom);
    var option;
    var series = [];

    const data = [
      {
        adjust:false,
        value: [0,800,400]
      },
      {
        adjust:true,
        value: [[1,500,500],[1,400,500]]
      },
      {
        adjust:false,
        value: [2,700,400]
      }
    ]

    const formdata = []

    for(let i = 0;i < data.length;i++){
        if(data[i].adjust){
          const item = data[i].value

          let arr1 = item[0]
          let arr2 = item[1]
          for(let i = 1;i <= arr1[1];i++){
            formdata.push({
              value:[arr1[0],i,arr1[2]],
              itemStyle: {
                  color:'#3370FF'
              }
            })
          }
          for(let i = arr1[1];i <= arr1[1]+arr1[2];i++){
            formdata.push({
               value:[arr2[0],i,arr2[2]],
              itemStyle: {
                  color:'#44D7B6'
              }
            })
          }
        }else{
          const item = data[i]
          for(let i = 1;i <= item.value[1];i++){
            formdata.push({
              value:[item.value[0],i,item.value[2]],
              itemStyle: {
                color:'#B1CBE2'
              }
            })
          }
        }
    }

    series.push({
        stack: true,
        shading: 'lambert',
        emphasis: {
            label: {
                show: false
            }
        },
        type: 'bar3D',
        barSize: [10, 1, 10],
        data: formdata
    });

  //   series.push({
  //     stack: true,
  //     shading: 'lambert',
  //     emphasis: {
  //         label: {
  //             show: false
  //         }
  //     },
  //     type: 'bar3D',
  //     barSize: [10, 10, 10],
  //     data: [[0,0,5],[1,1,0],[2,1,20],[3,1,0],[4,1,0]]
  // });



    var option;
    option = {
        xAxis3D: {
            name: '',
            nameTextStyle:{
                color:'#8F959E',
                fontSize:12
            },
            type: 'category',
            data: [0,1,2,3,4],
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
            type: 'value',
            // axisLine:{
            //     show:true,
            //     interval:0,
            //     lineStyle:{
            //         color:'#8F959E'
            //     }
            // },
            // axisLabel:{
            //     interval:0,
            //     textStyle:{
            //         color:'#8F959E'
            //     }
            // }
        },
        zAxis3D: {
            name: '功率(kW)',
            type: 'value',
            nameGap:30,
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
                beta: 20,
                alpha: 3,
                // 视角距离
                distance: 170
            },
            splitLine: {
                show: false
            },
            axisPointer: {
                show: false
            },
            borderWidth: 0,
            boxWidth: 200,
            boxDepth: 30,
            light: {
            main: {
                intensity: 1.2,
                shadow: true
            },
            ambient: {
                intensity: 0.3
            }
            },
            light: {
              main: {
                intensity: 1.2
              },
              ambient: {
                intensity: 0.3
              }
            }
        },
        series: [...series]
    };

    option && myChart.setOption(option);
  }

  useEffect(() => {
    init()
  },[timerList,baselineList,predictionList])

  
  return <div id='main-03' style={{width:'592px',height:'320px'}}></div>
};

export default Chart03;
