import React, { useEffect } from 'react';
import * as echarts from 'echarts';
// import echarts  from '../../../../echarts.js'
import 'echarts-gl';
import './index.scss'
const Chart02 = (props) => {
  const { timerList,baselineList,predictionList } = props || {}
  const init = () => {
    var chartDom = document.getElementById('main-02');
    var myChart = echarts.init(chartDom);
    var option;
    var series = [];

    const data = [
      {
        adjust:false,     
        value: [0,10,400]
      },
      {
        adjust:true,
        value: [[1,50,500],[1,40,500]]
      },
      {
        adjust:false,
        value: [2,20,400]
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
      // const { dataIndex } = params
      const arr = params.data.value
      const i = arr[0]
      const adjust = data[i].adjust
      let normal = ''
      let special1 = ''
      let special2 = ''
      if(!adjust){
        normal = data[i].value[1]
      }else{
        data[i].value.forEach((item,i)=> {
          if(i === 0){
            special1 = item[1]
          }else{
            special2 = item[1]
          }
        })
      }
      if(adjust){
        const dataString =  
        `<div style="padding-left:5px;height:30px;line-height:30px;color:#fff;font-size:14px"></div>`
             +

             `<div style="display:flex;color:#fff">
                 <div style="display:flex;align-items:center;margin-right:125px"> <span style="display:inline-block;width:6px;height:6px;background:#C9E3FE;border-radius:6px;margin-right:6px"></span>时间：</div>
                 <span style="">${arr[0]}</span>
             </div>`
      
             +

             `<div style="display:flex;color:#fff">
               <div style="display:flex;align-items:center;margin-right:125px"> <span style="display:inline-block;width:6px;height:6px;background:#C9E3FE;border-radius:6px;margin-right:6px"></span>价格：</div>
               <span style="">${arr[2]}</span>
             </div>`

             +

             `<div style="display:flex;color:#fff">
                 <div style="display:flex;align-items:center;margin-right:124px"> <span style="display:inline-block;width:6px;height:6px;background:#E02020;border-radius:6px;margin-right:6px"></span>功率(kW)：</div>
                 <span style="">${special1}</span>
             </div>`
             +

             `<div style="display:flex;color:#fff">
                 <div style="display:flex;align-items:center;margin-right:124px"> <span style="display:inline-block;width:6px;height:6px;background:#E02020;border-radius:6px;margin-right:6px"></span>功率(kW)：</div>
                 <span style="">${special2}</span>
             </div>`
        return dataString
      }else{
        const dataString =  
        `<div style="padding-left:5px;height:30px;line-height:30px;color:#fff;font-size:14px"></div>`
             +

             `<div style="display:flex;color:#fff">
                 <div style="display:flex;align-items:center;margin-right:125px"> <span style="display:inline-block;width:6px;height:6px;background:#C9E3FE;border-radius:6px;margin-right:6px"></span>时间：</div>
                 <span style="">${arr[0]}</span>
             </div>`
      
             +

             `<div style="display:flex;color:#fff">
               <div style="display:flex;align-items:center;margin-right:125px"> <span style="display:inline-block;width:6px;height:6px;background:#C9E3FE;border-radius:6px;margin-right:6px"></span>价格：</div>
               <span style="">${arr[2]}</span>
             </div>`

             +

             `<div style="display:flex;color:#fff">
                 <div style="display:flex;align-items:center;margin-right:124px"> <span style="display:inline-block;width:6px;height:6px;background:#E02020;border-radius:6px;margin-right:6px"></span>功率(kW)：</div>
                 <span style="">${normal}</span>
             </div>`
        return dataString
      }
     
  }


   

    console.log(formdata,'formdata')

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
            // boxDepth: 100,
            boxHeight:40,
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

  
  return <div id='main-02' style={{width:'592px',height:'320px'}}></div>
};

export default Chart02;
