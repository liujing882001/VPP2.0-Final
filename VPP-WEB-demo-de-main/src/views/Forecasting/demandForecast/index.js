import React, { useEffect, useState } from 'react';
import { Select, Button, DatePicker,Tooltip,message,TimePicker,Skeleton,Table } from 'antd'
import { useHistory } from "react-router-dom";
import { calcNum } from './utils/index'
import dayjs from 'dayjs';
import CommonModal from './component/modal'
import StrategyModal from './component/strategy-modal/index'
import Chart01 from './component/chart01/index'
import Chart02 from './component/chart02/index'
import Chart03 from './component/chart03/index'
import { jsPlumb } from "jsplumb"; 

import classNames from 'classnames';
import { mapStrategyName,
         formatDate,
         isTimeDifferenceTwoHours,
         renderResponseType,
         renderResponseLevel,
         renderResponseStatus,
         renderNodeStatus,
         calcEndDate,
         renderDeclareLoad,
         renderAdjustLoad
} from './utils'
import axios from 'axios'

import './index.scss'

import weekday from "dayjs/plugin/weekday"
import localeData from "dayjs/plugin/localeData"
import http from '../../../server/server';

dayjs.extend(weekday)
dayjs.extend(localeData)


const { RangePicker } = TimePicker;

const DemandForecast = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isStrategyModalOpen, setIsStrategyModalOpen] = useState(false);
    const [currentRespId, setCurrentRespId] = useState(null);
    const [taskNumberList,setTaskNumberList] = useState([])
    const [taskStatus,setTaskStatus] = useState('-1')
    const [responseType,setResponseType] = useState('-1')
    const [strategyList,setStrategyList] = useState([])
    const [logList,setLogList] = useState([])
    const [selectedStrategyItem,setSelectedStrategyItem] =  useState({})

    const [timerList,setTimerList] = useState([])
    const [colorTimerList,setColorTimerList] = useState([])
    const [baselineList,setBaselineList] = useState([])
    const [predictionList,setPredictionList] = useState([])
    const [realList,setRealList] = useState([])
    const [bottomList,setBottomList] = useState([])
    const [topList,setTopList] = useState([])

    const [startTime, setStartTime] = useState(null);
    const [endTime, setEndTime] = useState(null);
    const [currentTime,setCurrentTime] = useState('')
    const [allTaskList,setAllTaskList] = useState([])
    const [nodeId,setNodeId] = useState('')
    const [nodeList,setNodeList] = useState([])
    const [taskInfo,setTaskInfo] = useState({})
    const [allNodeList,setAllNodeList] = useState([])
    const [sTime,setSTime] =  useState('')
    const [eTime,setETime] =  useState('')
    const [selectedIds,setSelectedIds] = useState([]);
    const [notShowConfirmBtn,setNotShowConfirmBtn] = useState(false)
    const history= useHistory()

    const handleTaskStatusChange = (value) => {
        setTaskNumberList([])
        setCurrentRespId('')
        setTaskStatus(value)
        getTaskCodeList(value,responseType,false)
    };

    const handleResponseChange = (value) => {
        setTaskNumberList([])
        setCurrentRespId('')
        setResponseType(value)
        getTaskCodeList(taskStatus,value,false)
    }

    const handleTaskNumberChange = (value) => {
        const item = allTaskList.find(item => item.respId === value)

        const { startTime,endDate,endTime } = calcEndDate(item?.rsTime)
        setStartTime(startTime)
        setSTime(`${endDate} ${startTime}`)
        setETime(item?.reTime)
        setEndTime(endTime)
        setCurrentTime(endDate)
        setCurrentRespId(value)
    }
    

    const queryAllNodeList = (respId,startTime,endTime,sTime,eTime) => {
        http.post('AIEnergy/demandNodeQuery', {respId}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const list = response.data.data || []
            setAllNodeList(list)
            setNodeId(list[0].value)
            setTimeout(() => {
                queryChart(respId,startTime,endTime,list[0].value,sTime,eTime)
            }, 200);
        })
        .catch(function (error) {
            // message.error(`接口错误getTaskCodeList`)
        });
    }

    const onQuery = (respId,startTime,endTime,sTime,eTime) => {
        queryStrategy(respId)
        setTimeout(() => {
            getDecisionChainList(respId)
        }, 300);
        setTimeout(() => {
            queryAllNodeList(respId,startTime,endTime,sTime,eTime)
        }, 400);
    }   

    const getTaskCodeList = (status,respType,load=true) => {
        const data = { status, respType };
        http.post('AIEnergy/getTaskCodeList', data, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const list = response.data.data.content
            setAllTaskList(list)
            const options = list?.map((item) => {
               return {
                  label:item?.taskCode,
                  value:item?.respId
               }
            })
            
            const respId = options[0]?.value
            const {startTime,endDate,endTime } = calcEndDate(list[0]?.rsTime)
            setStartTime(startTime)
            setEndTime(endTime)
            setCurrentTime(endDate)
            setSTime(list[0]?.rsTime)
            setETime(list[0]?.reTime)
            setCurrentRespId(respId)
            setTaskNumberList(options)
            load && respId && onQuery(respId,list[0]?.rsTime,`${endDate} ${endTime}`,list[0]?.rsTime,list[0]?.reTime)
        })
        .catch(function (error) {
            // message.error(`接口错误getTaskCodeList`)
        });
    }

    const queryStrategy = (respId) => {
        http.post("AIEnergy/strategyQuery", {respId}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const list = response.data.data?.map((item,i) => {
               return {
                  key:i+1,
                  nodeName:item?.nodeName,
                  id:item?.id,
                  content:item?.strategyContent,
                  time:item?.timePoint,
                  noHouseholds:item?.noHouseholds,
                  deviceName:item?.deviceName,
                  ensure:item?.ensure,
               }
            })
            const isShowConfirmBtn = response.data.data?.every((item) => item.ensure === 1)
            setNotShowConfirmBtn(isShowConfirmBtn)
            setStrategyList(list)
        })
        .catch(function (error) {
            message.error(`接口错误strategyQuery`)
        });
    }

    const queryLog = (respId) => {
        http.post("AIEnergy/strategyLogQuery", {respId}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const list = response.data.data?.map((item) => {
               return {
                  node:mapStrategyName(item?.nodeId),
                  id:item?.id,
                  content:item?.strategyContent,
                  time:item?.timePoint
               }
            })
            setLogList(list)
        })
        .catch(function (error) {
            message.error(`接口错误strategyLogQuery`)
        });
    }

    const queryChart = (respId,startTime,endTime,nodeId,sTime,eTime) => {
        http.post("AIEnergy/demandForecastChart",{
            respId,
            nodeId,
            systemId: "kongtiao",
            rsTime : startTime,
            reTime : endTime,
            sTime,
            eTime
        }, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const tempTimerList = response.data.data?.map((item => {
                return {
                    value:item?.timeStamp,
                    textStyle: {
                        fontSize: 8,
                        color: '#8F959E'
                    }
                }
            }))

            const tempColorTimerList = response.data.data?.map((item => {
                return {
                    value:item?.timeStamp,
                    textStyle: {
                        fontSize: 8,
                        color: item.adjust ? '#0092FF' : '#8F959E'
                    }
                }
            }))
            setColorTimerList(tempColorTimerList)

            const tempPredictionList = response.data.data?.map(item => {
                return {
                    value:item?.forecastLoad,
                    itemStyle: {
                        color:'#7CA5CF'
                    }
                }
            })
            const tempRealList = response.data.data?.map(item => {
                return {
                    value:item?.realValue,
                    itemStyle: {
                        color:'#3370FF'
                    }
                }
            })

            const tempBaselineList = response.data.data?.map((item => {
                return item?.baselineLoadValue
            }))

            const tempBottomList = response.data.data?.map(((item,i) => {
               if(item?.adjust){
                  return {
                     value : item?.forecastLoadAfterRegulation ? item?.forecastLoadAfterRegulation : [i,0,0],
                     itemStyle:{
                        color:'#3370FF'
                     }
                  }
               }else{
                  return {
                    value: item?.forecastLoad,
                    itemStyle: {
                       color:'#7CA5CF'
                    }
                }
             }
            }))

            const tempTopList = response.data.data?.map(((item,i) => {
                if(item.adjust){
                   return {
                      value : item?.forecastRegulationLoad ? item?.forecastRegulationLoad : [i,0,0],
                      itemStyle:{
                         color:'#44D7B6'
                      }
                   }
                }else{
                   return {
                     value: [i,0,0],
                     itemStyle: {
                        color:'#7CA5CF'
                     }
                 }
              }
            }))


            setTimerList(tempTimerList)
            setPredictionList(tempPredictionList)
            setBaselineList(tempBaselineList)
            setRealList(tempRealList)
            setTopList(tempTopList)
            setBottomList(tempBottomList)
        })
        .catch(function (error) {
            message.error(`接口错误demandForecastChart`)
        });
    }      

	const instance = jsPlumb.getInstance();
	const resize = () => {
        // const leftDom = document.getElementsByClassName('demand-forecast_content_left')[0]
        // const rightDom = document.getElementsByClassName('demand-forecast_content_right')[0]
        // rightDom?.setAttribute('style', `height:${leftDom.clientHeight}px`)
		instance.repaintEverything();
	};
	const clear = () => { 
		instance.deleteEveryConnection();
	};
    const init = (nodeList,respTask) => {
      
	};

    const getDecisionChainList = (respId) => {
        const params = new URLSearchParams();
        params.append("respId", respId);
        http.post("AIEnergy/getDecisionChainList",params)
        .then(function (response) {
            const data = response.data.data
            setNodeList(data?.nodeList)
            setTaskInfo(data?.respTask)
            init(data?.nodeList,data?.respTask)
        })
        .catch(function (error) {
            message.error(`接口错误getDecisionChainList`)
        });

    }

    const onQueryChart = (respId,start,end,sTime,eTime) => {    
        if(!currentTime){
            message.error('请输入日期');
            return
        } 
        if(!startTime || !endTime){
            message.error('请输入时间');
            return
        }
        if(isTimeDifferenceTwoHours(start,end)){
            message.error('时间范围只允许两个半小时之内');
            return
        }
        // setSpinning(true)
        queryChart(respId,start,end,nodeId,sTime,eTime)
        // setTimeout(() => {
        //     setSpinning(false)
        // }, 1000);
    }

    const enSure = (ids) => {
        http.post("AIEnergy/strategyEnsure", {ids}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            message.success('确认成功')
            queryStrategy(currentRespId)
            setSelectedIds([])
        })
        .catch(function (error) {
            message.error('接口错误strategyLogQuery')
        });
    }

    const columns = [
        {
          title: '节点',
          dataIndex: 'nodeName',
          render: (text) => {
            return (
                <>  
                    {text?.length > 8 ?  
                        <Tooltip placement="top" title={text}>
                            {text?.slice(0,8) + '...'}
                        </Tooltip> : text
                    }
                </>
               
            )
          },
        },
        {
          title: '户号',
          dataIndex: 'noHouseholds',
        },
        {
          title: '时间点',
          dataIndex: 'time',
          render: (text) => formatDate(text),
        },
        {
            title: '策略内容',
            dataIndex: 'content',
        },
        {
            title: '策略状态',
            dataIndex: 'ensure',
            render: (text) => !!text ? '已确认' : '未确认',
        },
        renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' ?
        {

        }:{
            title: '操作',
            dataIndex: '',
            key: 'x',
            render: (_text, record,) => <>
                {
                    renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' || record?.ensure === 1  ? null: <a className='edit-btn' onClick={() => {
                        enSure([record?.id])
                    }}>确认</a>
                }
                {
                    renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成'  ? null : <a className='edit-btn' onClick={() => {
                        setSelectedStrategyItem(record)
                        setIsStrategyModalOpen(true)
                  }}>编辑</a>
                }
                
            </>
        },
    ];
   
    const rowSelection = {
        onChange: (selectedRowKeys, selectedRows) => {
          console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
          const ids = selectedRows.map((item) => item?.id)
          setSelectedIds(ids)
        },
        getCheckboxProps: (record) => ({
            disabled: record?.ensure === 1,
            defaultChecked:false
        }),
     };

    useEffect(() => {
        // const leftDom = document.getElementsByClassName('demand-forecast_content_left')[0]
        // const rightDom = document.getElementsByClassName('demand-forecast_content_right')[0]
        // rightDom?.setAttribute('style', `height:${leftDom.clientHeight}px`)
        getTaskCodeList(taskStatus,responseType)
        return () => {
			clear();
			window.removeEventListener("resize", resize);
		};
    }, [])

    return (    
        <>
            {/* <Spin spinning={spinning} fullscreen /> */}
            <div className="demand-forecast">
                <div className="demand-forecast_header">
                    <div className='select-1'>
                        <span className='select-title'>任务状态：</span>
                        <Select
                            style={{ width: 200, height: 32, color: '#fff' }}
                            onChange={handleTaskStatusChange}
                            value={taskStatus}
                            options={[
                                { value: '-1', label: '全部' },
                                { value: '1', label: '待执行' },
                                { value: '2', label: '执行中' },
                                { value: '3', label: '已完成'},
                            ]}
                        />
                    </div>
                    <div className='select-2'>
                        <span className='select-title'>响应类型：</span>
                        <Select
                            style={{ width: 200, height: 32, color: '#fff' }}
                            value={responseType}
                            onChange={handleResponseChange}
                            options={[
                                { value:'-1', label: '全部' },
                                { value: '1', label: '削峰响应' },
                                { value: '2', label: '填谷响应' },
                            ]}
                        />
                    </div>
                    <div className='select-3'>
                        <span className='select-title'>任务编号：</span>
                        <Select
                            style={{ width: 200, height: 32, color: '#fff' }}
                            options={taskNumberList}
                            value={currentRespId}
                            onChange={handleTaskNumberChange}
                        />
                    </div>
                    <Button type="Button" className='demand-forecast_header_btn' onClick={() => onQuery(currentRespId,currentTime + ' ' + startTime,currentTime + ' ' + endTime,sTime,eTime)}>查询</Button>
                </div>
                <div className="demand-forecast_content">
                    <div className={classNames('demand-forecast_content_left')}>
                    <div className='demand-forecast_content_left_header'>
                        {
                            currentTime ? 
                            <DatePicker 
                                format='YYYY-MM-DD' 
                                allowClear={false} 
                                defaultValue={dayjs(currentTime, 'YYYY-MM-DD')} 
                                value={dayjs(currentTime, 'YYYY-MM-DD')} 
                                style={{width:'150px',marginRight:'10px'}} 
                                onChange={(date, dateString) => {
                                setCurrentTime(dateString)
                                }}
                            />:
                            <DatePicker 
                                format='YYYY-MM-DD' 
                                allowClear={false} 
                                style={{width:'150px',marginRight:'10px'}} 
                                onChange={(date, dateString) => {
                                    setCurrentTime(dateString)
                                }}
                            />
                        }
                        
                        <RangePicker 
                            format='HH:mm'
                            style={{width:'150px',marginRight:'5px'}}
                            defaultValue={[startTime && dayjs(startTime, 'HH:mm'),endTime && dayjs(endTime, 'HH:mm')]} 
                            value={[startTime && dayjs(startTime, 'HH:mm'),endTime && dayjs(endTime, 'HH:mm')]}
                            placeholder={['','']}
                            onChange={(value) => {
                                if (value && value.length === 2) {
                                    const startTimeStr = value[0]?.format('HH:mm')
                                    const endTimeStr = value[1]?.format('HH:mm')
                                    setStartTime(startTimeStr);
                                    setEndTime(endTimeStr);
                                } else {
                                    setStartTime(null);
                                    setEndTime(null);
                                }
                            }}
                            minuteStep={15}
                        />
                        <Select
                            style={{ width: 150, height: 32, marginLeft: 5, color: '#fff', }}
                            // defaultValue={allNodeList[0]?.value}
                            value={nodeId}
                            options={allNodeList}
                            onChange={(value) => {
                                setNodeId(value)
                            }}
                        />
                        <Button type="Button" className='left-query-btn' style={{width:'63px'}} onClick={() => onQueryChart(currentRespId,currentTime + ' ' + startTime,currentTime + ' ' + endTime,sTime,eTime)}>查询</Button>
                        </div>
                        <div className='demand-forecast_content_left_content'>
                            <div className='chart-01-wrap'>
                                <div className='chart-wrap'>
                                    <i className='chart-icon' />
                                    <span className='chart-title'>基础负荷预测能量块</span>
                                </div>
                                <Chart01 timerList={colorTimerList} baselineList={baselineList} predictionList={predictionList}/>
                            </div>
                            <div className='chart-02-wrap'>
                                <div className='chart-wrap'>
                                    <i className='chart-icon' />
                                    <span className='chart-title'>基于需求响应策略的负荷调节能量块</span>
                                </div>
                                <Chart02 timerList={colorTimerList} baselineList={baselineList} topList={topList} bottomList={bottomList} predictionList={predictionList}/>
                            </div>
                            <div className='chart-03-wrap'>
                                <div className='chart-wrap'>
                                    <i className='chart-icon' />
                                    <span className='chart-title'>实际负荷能量块</span>
                                </div>
                                <Chart03 timerList={colorTimerList} realList={realList} baselineList={baselineList}/>
                            </div>
                        </div>
                    </div>
                    <div className='demand-forecast_content_right'>
                        <>
                            <div className='demand-forecast_content_right_header'>
                                <div className='title-wrapper'>
                                    <i className='demand-forecast-dots' /> 
                                    <span className='demand-forecast-title'>任务</span>
                                </div>
                                {/* <i className='expand-icon' onClick={() => {
                                    history.push({
                                        pathname: "/AllNodePage",
                                        state:{ respId: currentRespId }
                                    });
                                }} /> */}
                            </div>
                            <div className='demand-forecast_content_right_content'>
                                {/* <div className='vertical-dashed-line-1'></div>
                                <div className='vertical-dashed-line-2'></div>      */}
                                <div className='right-top-wrapper'>
                                    <div className='right-top-wrapper-left'>
                                        <div className='left-img'></div>
                                        <div className='left-img-text'>需求响应平台</div>
                                        <div className='right-top-title-wrapper'>
                                            <i className='right-top-title-icon'></i>
                                            <span className='right-top-title'>1-任务</span>
                                        </div>            
                                        <div className='left-text-wrap'>
                                            <p className='left-text'>响应时段：{taskInfo?.rsTime}-{taskInfo?.reTime?.split(' ')[1]}</p>
                                            <p className='left-text'>负荷需求(kW)：{taskInfo?.respLoad ? calcNum(taskInfo?.respLoad) : '-'}</p>
                                            {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已出清' || 
                                            renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || 
                                            renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' ? 
                                            <p className='left-text'>出清负荷(kW)：{calcNum(taskInfo?.declareLoad - renderDeclareLoad(nodeList?.slice(0,3)))}</p> : null
                                        }
                                            <p className='left-text'>响应类型：{renderResponseType(taskInfo?.respType)}</p>
                                            <p className='left-text'>响应级别：{ taskInfo?.respLevel ? renderResponseLevel(taskInfo?.respLevel) : '-'}</p>
                                        </div>
                                    </div>
                                    <div className='right-top-wrapper-right'>
                                        <div className='right-img'></div>
                                        <span className='right-img-text' id='node-top'>能源大模型</span>
                                        <div className='right-top-title-wrapper'>
                                            <i className='right-top-title-icon-01'></i>
                                            <span className='right-top-title'>4-生成</span>
                                        </div>
                                        <div className={classNames('right-text-wrap')}>
                                            <p className='other-right-text'>需求响应状态：{renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)}</p>
                                            <p className='other-right-text'>调节负荷(kW)：{renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '待申报' ? 0 : calcNum(taskInfo?.adjustLoad - renderAdjustLoad(nodeList?.slice(0,3)))}</p>
                                            <p className='other-right-text'>申报负荷(kW)：{renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '待申报' ? 0 : calcNum(taskInfo?.declareLoad - renderDeclareLoad(nodeList?.slice(0,3)))}</p>
                                            {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '待申报' ? null :<p className='other-right-text'>预测负荷(kW)：{calcNum(taskInfo?.forecastLoad)}</p>}
                                            {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' ? <>
                                                <p className='other-right-text'>实际负荷(kW)：{calcNum(taskInfo?.nowLoad)}</p>
                                                <p className='other-right-text'>基线负荷(kW)：{calcNum(taskInfo?.baseLoad)}</p>
                                            </>:null}
                                            {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' ? <>
                                                <p className='other-right-text'>响应负荷(kW)：{calcNum(taskInfo?.actualLoad)}</p>
                                                <p className='other-right-text'>响应电量(kWh)：{calcNum(taskInfo?.actualPower)}</p>
                                                <p className='other-right-text'>预估收益（元）：{calcNum(taskInfo?.profit)}元</p>
                                            </>:null}
                                        </div>
                                    </div>
                                </div>

                                <div className='right-center-wrapper'>
                                    <div className='right-center-title-wrapper'>
                                        <i className='right-center-icon'></i>
                                        <span className='right-center-title'>
                                            2-分解
                                        </span>
                                    </div>
                                    <div className='dots-wrapper'>
                                        {
                                            nodeList?.slice(0,3)?.map((item,i) => {
                                                return (
                                                    <>
                                                        <div className='dots-1'>
                                                            <div className='dots-1-header' id={`node-item-${i}`}>
                                                                <div className={classNames('round',{'online-status':item?.online})}></div>
                                                                <Tooltip placement="top" title={item.nodeName} color='#38373F'>
                                                                        <span className='dots-1-header-text'>{item?.nodeName}</span>
                                                                </Tooltip>
                                                            </div>
                                                            <div className='dots1-content'>
                                                                <p className='dots1-text'>状态：{renderNodeStatus(item?.drsStatus) || '-'}</p>
                                                                <>
                                                                        <p className='dots1-text'>申报负荷(kW)：{renderNodeStatus(item?.drsStatus) ==='待申报' ? 0 : calcNum(item?.declareLoad)}</p>
                                                                    <p className='dots1-text'>调节负荷(kW)：{renderNodeStatus(item?.drsStatus) ==='待申报' ? 0 : calcNum(item?.adjustLoad)}</p>
                                                                    <p className='dots1-text'>预测负荷(kW)：{calcNum(item?.forecastLoad)}</p>
                                                                </>
                                                                {
                                                                    item?.drsStatus === 24 ? 
                                                                    <>
                                                                        <p className='dots1-text'>实际负荷(kW)：{calcNum(item?.nowLoad)}</p>
                                                                        <p className='dots1-text'>基线负荷(kW)：{calcNum(item?.baseLoad)}</p>
                                                                    </> : null
                                                                }
                                                                {
                                                                    item?.drsStatus === 25 ? 
                                                                    <>
                                                                        <p className='dots1-text'>响应负荷(kW)：{calcNum(item?.responseLoad)}</p>
                                                                        <p className='dots1-text'>响应电量(kWh)：{calcNum(item?.realTimeLoad)}</p>
                                                                        <p className='dots1-text'>预估收益（元）：{calcNum(item?.profit)}元</p>
                                                                    </> : null
                                                                }
                                                                
                                                            </div>
                                                        </div>
                                                    </>             
                                                )
                                            }) 
                                        }
                                    </div>
                                </div>

                                <div className='right-bottom-wrapper'>
                                    <div className='right-bottom-title-wrapper'>
                                       <div className='right-bottom-title-wrapper-right'>
                                            <i className='right-bottom-icon'></i>
                                            <span className='right-center-title'>
                                                3-策略
                                            </span>
                                       </div>
                                        {
                                            renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' || notShowConfirmBtn ? null : <Button className='confirm-btn' disabled={selectedIds.length === 0} onClick={() => enSure(selectedIds)}>批量确认</Button>
                                        }
                                        
                                    </div>
                                    <div className='strategy-wrap'>
                                        {/* <div className='strategy-title-wrap'>
                                            <div className='strategy-title'>
                                                <span className='strategy-wrap-title active'>策略</span>
                                                <i className='line' />
                                            </div>
                                        </div> */}
                                        {/* <div className='table-wrap'>
                                            <div className='odd-rows first-line'>
                                                <span className='one-arrange'>节点</span>
                                                <span className='two-arrange'>户号</span>
                                                <span className='three-arrange'>时间点</span>
                                                <span className='three-arrange'>策略内容</span>
                                                <span className='four-arrange' style={{display:renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' ? 'none':'block'}}>操作</span>
                                            </div>
                                            {
                                                strategyList?.map((item,i) => {
                                                    return (
                                                        <div key={i} className={classNames(i % 2 === 0 ? 'even-rows':'odd-rows')}>
                                                            <Tooltip placement="top" title={item.nodeName} color='#38373F'>
                                                                <span className='one-arrange'>{item.nodeName}</span>
                                                            </Tooltip>
                                                            <Tooltip placement="top" title={item.noHouseholds} color='#38373F'>
                                                                <span className='two-arrange'>{item.noHouseholds}</span>
                                                            </Tooltip>
                                                            <span className='three-arrange'>{formatDate(item.time)}</span>
                                                            <Tooltip placement="top" title={item.content} color='#38373F'>
                                                                <span className='three-arrange'>{item.content}</span>
                                                            </Tooltip>
                                                            <span className='four-arrange' style={{display:renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' ? 'none':'block'}} onClick={(e) => {
                                                                setSelectedStrategyItem(item)
                                                                setIsStrategyModalOpen(true)
                                                            }}>修改</span>
                                                        </div>
                                                    )
                                                })
                                            }
                                        </div> */}
                                        <Table
                                            pagination={false}
                                            rowSelection={renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' || notShowConfirmBtn ? undefined:{
                                                type: 'checkbox',
                                                ...rowSelection,
                                            }}
                                            columns={columns}
                                            dataSource={strategyList}
                                        />
                                     </div>
                                </div>
                            </div>
          
                                {/* <div className='demand-forecast-right-center'>
                                    {/* <div className={classNames('center-img-top', 
                                                    { 'overturn':renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="待出清" || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="执行中" },
                                                    { 'completed':renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="已完成" })}>

                                    </div> */}

                                {/* </div> */} 

                                {/* <div className={`demand-forecast-right-bottom-${nodeList?.length}`}>
                                    { nodeList?.slice(0,3)?.map((item,i) => {
                                        return <div  className={classNames(`bottom-gif-${i+1}`, 
                                        { 'overturn':renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="待申报" || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="执行中" },
                                        { 'completed':renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="已完成" })} ></div>
                                    })}
                                </div>  */}
                        </>
                    </div>
                </div>
            </div>
            <CommonModal setIsModalOpen={setIsModalOpen} isModalOpen={isModalOpen} nodeList={nodeList} taskInfo={taskInfo}/>
            {
                isStrategyModalOpen ?       
                <StrategyModal 
                    setIsStrategyModalOpen={setIsStrategyModalOpen} 
                    selectedStrategyItem={selectedStrategyItem}
                    queryStrategy={() => queryStrategy(currentRespId)}
                    getDecisionChainList={() => getDecisionChainList(currentRespId)}
                    onQueryChart={() => onQueryChart(currentRespId,currentTime + ' ' + startTime,currentTime + ' ' + endTime,sTime,eTime)}
                /> : null
            }
      
        </> 
    )

}
export default DemandForecast

