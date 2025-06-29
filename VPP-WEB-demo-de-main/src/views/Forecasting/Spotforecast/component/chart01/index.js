import React, { useEffect } from 'react';
import * as echarts from 'echarts';
// import echarts  from '../../../../echarts.js'
import 'echarts-gl';
import './index.scss'
const Chart01 = (props) => {
  const { timerList,baselineList,predictionList } = props || {}
  const init = () => {
    var chartDom = document.getElementById('main-01');
    var myChart = echarts.init(chartDom);
    var option;
    var series = [];

    const data = [
      {
        adjust:false,
        value: [0,40,120]
      },
      {
        adjust:true,
        value: [[1,10,100],[1,20,100]]
      },
      {
        adjust:false,
        value: [2,20,110]
      }
    ]

    const formdata = [
      
    ]

    for(let i = 0;i < data.length;i++){
        if(data[i].adjust){
          const item = data[i].value

          let arr1 = JSON.parse(JSON.stringify(item[0]))
          let arr2 = JSON.parse(JSON.stringify(item[1]))
          for(let i = 1;i <= arr1[1];i++){
            formdata.push({
              value:[arr1[0],i,arr1[2]],
              itemStyle: {
                  color:'#3370FF'
              }
            })
          }
          for(let i = arr1[1];i <= arr1[1]+arr2[1];i++){
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

    function formatterHover(params) {
      const { dataIndex } = params
      const timer = timerList[dataIndex].value
      const add =[]
      const redLine = baselineList[dataIndex][2]
      const prediction = predictionList[dataIndex].value[2]

      const dataString =  
         `<div style="padding-left:5px;height:30px;line-height:30px;color:#fff;font-size:14px">${timer}</div>`
              +

              `<div style="display:flex;color:#fff">
                  <div style="display:flex;align-items:center;margin-right:125px"> <span style="display:inline-block;width:6px;height:6px;background:#C9E3FE;border-radius:6px;margin-right:6px"></span>预测负荷(kW)</div>
                  <span style="">${prediction}</span>
              </div>`
       
              +

              `<div style="display:flex;color:#fff">
                  <div style="display:flex;align-items:center;margin-right:124px"> <span style="display:inline-block;width:6px;height:6px;background:#E02020;border-radius:6px;margin-right:6px"></span>基线负荷(kW)</div>
                  <span style="">${redLine}</span>
          </div>`
      return dataString
  }

    // series.push({
    //     stack: true,
    //     shading: 'realistic',
    //     emphasis: {
    //         label: {
    //             show: false
    //         }
    //     },
    //     type: 'bar3D',
    //     barSize: [10, 1, 10],
    //     data: formdata
    // });

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
            nameGap:30,
            type: 'value',
            axisLine:{
                show:true,
                interval:0,
                lineStyle:{
                    color:'#8F959E'
                }
            },
            minInterval:0,
            interval:50,            
            axisLabel:{
                interval:5  ,
            }
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
          boxDepth: 100,
          boxHeight: 40,
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

  
  return <div id='main-01' style={{width:'580px',height:'220px'}}></div>
};

export default Chart01;
