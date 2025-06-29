import React, { useEffect, useState } from 'react';
import { Select, Button, DatePicker,Tooltip,message,TimePicker,Skeleton } from 'antd'
import { useHistory } from "react-router-dom";
import { calcNum } from './utils/index'
import CommonModal from './component/modal'
import StrategyModal from './component/strategy-modal/index'
import Chart01 from './component/chart01'
import Chart02 from './component/chart02'
import Chart03 from './component/chart03'

// import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
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
} from './utils'
import axios from 'axios'
import './index.scss'
import weekday from "dayjs/plugin/weekday"
import localeData from "dayjs/plugin/localeData"
import http from '../../../server/server';
dayjs.extend(weekday)
dayjs.extend(localeData)


const { RangePicker } = TimePicker;

const Spotforecast = () => {
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
    const [sTime,setSTime] =  useState(null)
    const [eTime,setETime] =  useState(null)
    
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

        const {startTime,endDate,endTime } = calcEndDate(item?.rsTime)
        setStartTime(startTime)
        setSTime(`${endDate} ${startTime}`)
        setETime(`${endDate} ${endTime}`)
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
            const list = response.data.data?.map((item) => {
               return {
                  nodeName:item?.nodeName,
                  id:item?.id,
                  content:item?.strategyContent,
                  time:item?.timePoint,
                  noHouseholds:item?.noHouseholds,
                  deviceName:item?.deviceName,
               }
            })
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
        if(!start || !end){
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

    useEffect(() => {
        const leftDom = document.getElementsByClassName('spot-forecast_content_left')[0]
        const rightDom = document.getElementsByClassName('spot-forecast_content_right')[0]
        rightDom?.setAttribute('style', `height:${leftDom.clientHeight}px`)
        getTaskCodeList(taskStatus,responseType)
        return () => {
			clear();
			window.removeEventListener("resize", resize);
		};
    }, [])

    return (    
        <>
            {/* <Spin spinning={spinning} fullscreen /> */}
            <div className="spot-forecast">
                <div className="spot-forecast_content">
                    <div className={classNames('spot-forecast_content_left')}>
                    <div className='spot-forecast_content_left_header'>
                        <DatePicker format='YYYY-MM-DD' allowClear={false} defaultValue={dayjs(currentTime, 'YYYY-MM-DD')} value={dayjs(currentTime, 'YYYY-MM-DD')} style={{width:'150px',marginRight:'10px'}} onChange={(date, dateString) => {
                            setCurrentTime(dateString)
                        }}/>
                        <RangePicker 
                            format='HH:mm'
                            style={{width:'150px',marginRight:'5px'}}
                            defaultValue={[dayjs(startTime, 'HH:mm'),dayjs(endTime, 'HH:mm')]} 
                            value={[dayjs(startTime, 'HH:mm'),dayjs(endTime, 'HH:mm')]}
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
                        <Button className='left-query-btn' style={{width:'63px'}} onClick={() => onQueryChart(currentRespId,currentTime + ' ' + startTime,currentTime + ' ' + endTime,sTime,eTime)}>查询</Button>
                        </div>
                        <div className='spot-forecast_content_left_content'>
                            <div>
                                <div className='chart-wrap'>
                                    <i className='chart-icon' />
                                    <span className='chart-title'>基础现货功率价格预测能量块</span>
                                </div>
                                <Chart01 timerList={colorTimerList} baselineList={baselineList} predictionList={predictionList}/>
                            </div>
                            <div>
                                <div className='chart-wrap'>
                                    <i className='chart-icon' />
                                    <span className='chart-title'>基于策略的功率价格预测能量块</span>
                                </div>
                                <Chart02 timerList={colorTimerList} baselineList={baselineList} topList={topList} bottomList={bottomList} predictionList={predictionList}/>
                            </div>
                            <div>
                                <div className='chart-wrap'>
                                    <i className='chart-icon' />
                                    <span className='chart-title'>实际现货功率价格能量块</span>
                                </div>
                                <Chart03 timerList={colorTimerList} realList={realList} baselineList={baselineList}/>
                            </div>                   
                        </div>
                    </div>
                    <div className='spot-forecast_content_right'>
                        <>
                            <div className='spot-forecast_content_right_header'>
                                <div className='title-wrapper'>
                                    <i className='spot-forecast-dots' /> 
                                    <span className='spot-forecast-title'>拓扑图</span>
                                </div>
                                <i className='expand-icon' onClick={() => {
                                    history.push({
                                        pathname: "/AllNodePage",
                                        state:{ respId: currentRespId }
                                    });
                                }} />
                            </div>
                            <div className='spot-forecast_content_right_content'>
                                    <div className='spot-forecast-right-top'>
                                        <div className='right-img'></div>
                                    <span className='right-text' id='node-top'>需求响应平台</span>
                                    </div>
                                    <div className='spot-forecast-right-center'>
                                        <div className={classNames('center-img-top', 
                                                        { 'overturn':renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="待出清" || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="执行中" },
                                                        { 'completed':renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="已完成" })}>

                                        </div>
                                        <div className='model-wrap'>
                                            <div className='modal-img'  id='node-center'></div>
                                            <span className='model-text' id='node-center-bottom'>能源大模型</span>
                                        </div>
                                        <div className='left-text-wrap'>
                                            <p className='left-text'>响应时段：{taskInfo?.rsTime}-{taskInfo?.reTime?.split(' ')[1]}</p>
                                            <p className='left-text'>负荷需求(kW)：{calcNum(taskInfo?.respLoad)}</p>
                                            {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已出清' || 
                                            renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || 
                                            renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' ? 
                                            <p className='left-text'>出清负荷(kW)：{calcNum(taskInfo?.declareLoad)}</p> : null
                                        }
                                            <p className='left-text'>响应类型：{renderResponseType(taskInfo?.respType)}</p>
                                            <p className='left-text'>响应级别：{renderResponseLevel(taskInfo?.respLevel)}</p>
                                        </div>
                                        <div className={classNames('right-text-wrap',{'special-right-text-wrap' : renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' || nodeList?.length >= 3})}>
                                            <p className='other-right-text'>需求响应状态：{renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)}</p>
                                            <p className='other-right-text'>申报负荷(kW)：{renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '待申报' ? 0 : calcNum(taskInfo?.declareLoad)}</p>
                                            <p className='other-right-text'>调节负荷(kW)：{renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '待申报' ? 0 : calcNum(taskInfo?.adjustLoad)}</p>
                                            {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '待申报' ? null :<p className='other-right-text'>预测负荷(kW)：{calcNum(taskInfo?.forecastLoad)}</p>}
                                            {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' ? <>
                                                <p className='other-right-text'>实际负荷(kW)：{calcNum(taskInfo?.nowLoad)}</p>
                                                <p className='other-right-text'>基线负荷(kW)：{calcNum(taskInfo?.baseLoad)}</p>
                                            </>:null}
                                            {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' ? <>
                                                <p className='other-right-text'>响应负荷(kW)：{calcNum(taskInfo?.actualLoad)}</p>
                                                <p className='other-right-text'>响应电量(kW)：{calcNum(taskInfo?.actualPower)}</p>
                                                <p className='other-right-text'>预估收益（元）：{calcNum(taskInfo?.profit)}元</p>
                                            </>:null}

                                        </div>
                                    </div>
                                    <div className={`spot-forecast-right-bottom-${nodeList?.length}`}>
                                        { nodeList?.slice(0,3)?.map((item,i) => {
                                            return <div  className={classNames(`bottom-gif-${i+1}`, 
                                            { 'overturn':renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="待申报" || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="执行中" },
                                            { 'completed':renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)==="已完成" })} ></div>
                                        })}
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
                                                                    <p className='dots1-text'>申报负荷(kW)：{renderNodeStatus(item?.drsStatus) ==='待申报' ? calcNum(taskInfo?.declareLoad - item?.declareLoad): calcNum(item?.declareLoad)}</p>
                                                                    <p className='dots1-text'>调节负荷(kW)：{renderNodeStatus(item?.drsStatus) ==='待申报' ? calcNum(taskInfo?.adjustLoad- item?.adjustLoad) : calcNum(item?.adjustLoad)}</p>
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
                                                                        <p className='dots1-text'>响应电量(kW)：{calcNum(item?.realTimeLoad)}</p>
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
                                <div className='strategy-wrap'>
                                    <div className='strategy-title-wrap'>
                                        <div className='strategy-title'>
                                            <span className='strategy-wrap-title active'>策略</span>
                                            <i className='line' />
                                        </div>
                                    </div>
                                    <div className='table-wrap'>
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
                                    </div>
                                </div>
                            </div>
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
                /> : null
            }
      
        </> 
    )

}
export default Spotforecast

