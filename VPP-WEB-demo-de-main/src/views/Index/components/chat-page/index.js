import './index.scss'
import { Button, Select,Table,Tooltip,Spin,message, Input } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import ReactDOMServer from 'react-dom/server';
import { Dom1 } from '../dom1'
import { Dom2 } from '../dom2'
import { Dom3 } from '../dom3'
import { renderResponseType,renderResponseLevel,formatDate,renderResponseStatus,calcEndDate } from '../../utils'
import { flushSync } from 'react-dom';

import Typed from 'typed.js';
import Chart from '../chart';
import http from '../../../../server/server'
import { Label } from 'recharts';
export const ChatPage = (props) => {
    const { setIsMainPage } = props
    const [status,setStatus] = useState('init')
    const [isAnalysis,setIsAnalysis] = useState(false)
    const [scrollList,setScrollList] = useState([])
    const [spinning,setSpinning] = useState(true) 
    const [taskList,setTaskList] = useState([])
    const [taskInfo,setTaskInfo] = useState()
    const [strategyList,setStrategyList] = useState([])
    const [currentRespId, setCurrentRespId] = useState(null);
    const [currentTaskCode, setCurrentTaskCode] = useState(null);
    const [notShowConfirmBtn,setNotShowConfirmBtn] = useState(false)
    const selectedIds = useRef([])
    const currentNodeId = useRef('')
    const [allNodeList,setAllNodeList] = useState([])
    const [colorTimerList,setColorTimerList] = useState([])
    const [timerList,setTimerList] = useState([])
    const [predictionList,setPredictionList] = useState([])
    const [baselineList,setBaselineList] = useState([])
    const [realList,setRealList] = useState([])
    const [topList,setTopList] = useState([])
    const [bottomList,setBottomList] = useState([])
    const [startTime, setStartTime] = useState(null);
    const [endTime, setEndTime] = useState(null);
    const [currentTime,setCurrentTime] = useState('')
    const [sTime,setSTime] =  useState('')
    const [eTime,setETime] =  useState('')
    const [drsIds,setDrsIds] = useState()
    const message1 = useRef(null);

    let typed1 = ''
    const [isInitCanCLick,setIsInitCanCLick] = useState(false)
    const [isTwoCanCLick,setIsTwoCanCLick] = useState(false)
    const [isThreeCanCLick,setIsThreeCanCLick] = useState(false)
    const [isFourCanCLick,setIsFourCanCLick] = useState(false)
    const [temperature,setTemperature] = useState(0)
    const message2 = useRef(null);
    let typed2 = ''

    const message3 = useRef(null);
    let typed3 = ''

    const message4 = useRef(null);
    let typed4 = ''

    const onChange = (_value,options) => {
        const { label,value } = options || {}
        const item =  taskList.find((item) => item.respId === value)
        const {startTime,endDate,endTime } = calcEndDate(item?.rsTime)
        setStartTime(startTime)
        setEndTime(endTime)
        setCurrentTime(endDate) 
        setSTime(item?.rsTime)
        setETime(item?.reTime)

        setIsAnalysis(true)
        setStatus('analysis')
        setCurrentRespId(value)
        setCurrentTaskCode(label)
        setScrollList([...scrollList,
            {
                index:1,
                dom:(
                    <div className='right-default-wrap'>
                        <div className='right-message'>
                            <span>我想要分析 <span style={{margin:'0 10px'}}> {label} </span>响应任务</span>
                        </div>
                    </div>
                )
            },
           ]
        )
        getDecisionChainList(value,label)
    };

    const getDecisionChainList = (respId,label) => {
        const params = new URLSearchParams();
        params.append("respId", respId);
        http.post("AIEnergy/getDecisionChainList",params)
        .then(function (response) {
            const data = response.data.data
            setTaskInfo(data?.respTask)
 
            setScrollList((prestate) => {
                return [...prestate,{
                    index:2,
                    dom: (
                        <Dom1 
                            taskCode={data?.respTask?.taskCode} 
                            rsTime={data?.respTask?.rsTime} 
                            reTime={data?.respTask?.reTime} 
                            respType={data?.respTask?.respType} 
                            respLevel={data?.respTask?.respLevel}
                            respLoad={data?.respTask?.respLoad}
                            nodeList={data?.nodeList}
                            setScrollList={setScrollList}
                            setIsTwoCanCLick={() => setIsTwoCanCLick(true)}
                        />
                    )
                }]
            })            
        })
        .catch(function (error) {
            message.error(`接口错误getDecisionChainList`)
        });
    }

    const onRefreshQueryChart = (respId) => {
        http.post("AIEnergy/strategyQuery", {respId}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const strategyList = response.data.data?.map((item) => {
               return {
                  nodeName:item?.nodeName,
                  id:item?.id,
                  content:item?.strategyContent,
                  time:item?.timePoint,
                  noHouseholds:item?.noHouseholds,
                  deviceName:item?.deviceName,
                  ensure :item?.ensure,
               }
            })
            const isShowConfirmBtn = response.data.data?.every((item) => item.ensure === 1)
            setNotShowConfirmBtn(isShowConfirmBtn)
            setStrategyList(strategyList)
            http.post('AIEnergy/demandNodeQuery', {respId}, {
                headers: {
                  'Content-Type': 'application/json'
                }
            }).then(function (response) {
                const allNodeList = response.data.data || []
                setAllNodeList(allNodeList)
                currentNodeId.current = allNodeList[0].value
                onRefreshQueryChart(respId,allNodeList[0].value,allNodeList,isShowConfirmBtn,strategyList)
            }).catch(function (error) {
                // message.error(`接口错误getTaskCodeList`)
            });
        })
        .catch(function (error) {
            message.error(`接口错误strategyQuery`)
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
            render: (text = '',record) => {
                var before = text?.substring(0, 4);
                var end = text?.substring(-1);
                setTemperature(parseFloat(text?.slice(4)))
                return (
                   <div className='content-input-wrapper'>{text?.slice(0,4)}<Input defaultValue={parseFloat(text?.slice(4))} onBlur={(e) => {
                        if(Number(e.target.value) === parseFloat(text?.slice(4))){
                            return
                        }
                        if(!e.target.value){
                            message.error("温度不能为空")
                            return
                        }
                        if(isNaN(e.target.value)){
                            message.error("请输入准确数字！")
                            return
                        }
                        changeContent(record.id,parseFloat(text?.slice(4)),e.target.value)
                   }}/>{text?.slice(-1)}</div>
                )
            },
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
            render: (_text, record) => {
                return  (<>
                    {
                        renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' || record?.ensure === 1  ? null: <a className='edit-btn' onClick={() => {
                            enSure([record?.id])
                        }}>确认</a>
                    }
                </>)
            }
        },
    ];

    const columns1 = [
        {
            title: '节点',
            dataIndex: 'nodeName',
            key: 'nodeName',
        },
        {
            title: '户号',
            dataIndex: 'noHouseholds',
            key: 'noHouseholds',
        },
        {
            title: '申报负荷（kW）',
            dataIndex: 'declareLoad',
            editable: true,
            render:(value, item, index) =>{
                // if(value==null||value==undefined||value===""||value=='-'){
                //     return '-'
                // }else{
                //     return Number(value).toFixed(2)
                // }
                return (
                    <Input defaultValue={ value ? Number(value).toFixed(2) : '-'} className='edit-declareLoad-input' onBlur={(e) => {
                        if(Number(e.target.value) === Number(Number(value).toFixed(2))){
                            return
                        }
                        if(!e.target.value){
                            message.error("申报负荷不能为空")
                            return
                        }
                        if(isNaN(e.target.value)){
                            message.error("请输入准确数字！")
                            return
                        }

                        http.post('demand_resp/resp_task/editDeclare?drsId='+item.drsId +'&declareLoad='+e.target.value+
                        '&declarePrice='+ 0 +'&declareLoadBefore='+value).then(res =>{
                            if(res.data.code==200){
                                message.success('修改成功')
                                getSecondCSPGDeclareList(currentRespId)
                            }else{
                                message.info(res.data.msg)
                            }
                        }).catch(err =>{
                            console.log(err)
                        })
                   }}/>
                )
            }
        },
        {
            title: '预测调节负荷（kW）',
            dataIndex: 'adjustLoad',
            render:(value, item, index) =>{
                if(value==null||value==undefined||value===""||value=='-'){
                    return '-'
                }else{
                    return Number(value).toFixed(2)
                }
                
            }
        },
    ]

    const rowSelection = {
        onChange: (selectedRowKeys, selectedRows) => {
           console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
           const ids = selectedRows.map((item) => item?.id)
           selectedIds.current = [...ids]
        },
        getCheckboxProps: (record) => {
            return  ({
                disabled: record?.ensure === 1
            })
        },
    };

    const rowSelection1 = {
        getCheckboxProps: record => ({
            disabled: record.drsStatus == 21,    // 配置无法勾选的列
        }),
        onChange: (selectedRowKeys, selectedRows) => {
            console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
            const list  = selectedRows?.map((item) => item?.drsId)
            setDrsIds(list)
        },
    }

    useEffect(() => {
        return () => {
           typed1.destroy();
           typed2.destroy();
           typed3.destroy();
        };
    }, []);

    useEffect(() => {
        const scroll = document.getElementById('scroll-wrap');
        scroll.scrollTop = scroll.scrollHeight;

    },[scrollList])

    useEffect(() => {
        getTaskList()
    },[])

    const getTaskList = async () => {
        const data = await http.post('AIEnergy/getTaskState')
        const list = data.data.data.content
        const options = list?.map((item) => {
           return {
              label:item?.taskCode,
              value:item?.respId
           }
        })
        setTaskList(list)
        const respId = options[0]?.value
        const {startTime,endDate,endTime } = calcEndDate(list[0]?.rsTime)
        setStartTime(startTime)
        setEndTime(endTime)
        setCurrentTime(endDate) 
        setSTime(list[0]?.rsTime)
        setETime(list[0]?.reTime)

        setCurrentRespId(respId)
        setSpinning(false)

        setScrollList([
            {
                index:0,
                dom: (
                    <div className='left-default-wrap'>
                        <i className='left-robot-icon'/>
                        <div className='left-message'>
                            <span ref={message1}></span>
                        </div>
                    </div>
                )
            }
        ])

        typed1 = new Typed(message1.current, {
            strings: ['你想分析哪条需求响应任务？'],
            typeSpeed: 10,
            backSpeed: 50,
            showCursor: true,
            cursorChar: '',
            onComplete(self) {
                self.cursor.style.display = 'none'; // 隐藏光标
                setIsInitCanCLick(true)
            },
        });
    }

    const refreshTaskList = async () => {
        const data = await http.post('AIEnergy/getTaskState')
        const list = data.data.data.content
        setTaskList(list)
        setStatus('init')
    }

    /*第二步最复杂*/
    const generateStrategy = (respId) => {
        setScrollList([...scrollList,{
            index:3,
            dom: (
                <div className='right-default-wrap'>
                    <div className='right-message'>
                        <span>开始生成策略</span>
                    </div>
                </div>
            )
        }])

        setStatus('confirm')
        queryFirstStrategy(respId)
    }

    const queryFirstStrategy = (respId) => {
        http.post("AIEnergy/strategyQuery", {respId}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const strategyList = response.data.data?.map((item) => {
               return {
                  nodeName:item?.nodeName,
                  id:item?.id,
                  content:item?.strategyContent,
                  time:item?.timePoint,
                  noHouseholds:item?.noHouseholds,
                  deviceName:item?.deviceName,
                  ensure :item?.ensure,
               }
            })
            const isShowConfirmBtn = response.data.data?.every((item) => item.ensure === 1)
            setNotShowConfirmBtn(isShowConfirmBtn)
            setStrategyList(strategyList)
            queryFirstNodeList(respId,isShowConfirmBtn,strategyList) 

        })
        .catch(function (error) {
            message.error(`接口错误strategyQuery`)
        });
    }

    const queryFirstNodeList = (respId,isShowConfirmBtn,strategyList) => {
        http.post('AIEnergy/demandNodeQuery', {respId}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const allNodeList = response.data.data || []
            setAllNodeList(allNodeList)
            currentNodeId.current = allNodeList[0].value
            queryFirstChart(respId,allNodeList[0].value,allNodeList,isShowConfirmBtn,strategyList)
        })
        .catch(function (error) {
            // message.error(`接口错误getTaskCodeList`)
        });
    }

    const queryFirstChart = (respId,nodeId,allNodeList,isShowConfirmBtn,strategyList) => {
        http.post("AIEnergy/demandForecastChart",{
            respId,
            nodeId,
            systemId: "kongtiao",
            rsTime : `${currentTime} ${startTime}`,
            reTime : `${currentTime} ${endTime}`,
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

            setColorTimerList(tempColorTimerList)
            setTimerList(tempTimerList)
            setPredictionList(tempPredictionList)
            setBaselineList(tempBaselineList)
            setRealList(tempRealList)
            setTopList(tempTopList)
            setBottomList(tempBottomList)

            setScrollList((prestate) => {
                return [...prestate,{
                    index:4,
                    dom: (
                         <div className='edit-strategy-wrap'>
                            <i className='robot-gif-icon'/>
                            <div className='edit-strategy-first' style={{marginLeft:'70px'}}>
                                <div className='edit-strategy-header'>
                                    <span className='pre-loading-text'>正在生成中...</span>
                                </div>
                                <div className='edit-strategy-content loading-icon-wrap'>
                                    <i className='loading-icon'/>
                                </div>
                            </div>
                        </div>  
                    )
                }]
            })

            setTimeout(() => {
                setScrollList((prestate) => {
                    const list = [...prestate]
                    list.pop()
                    return [...list,{
                        index:4,
                        dom: (
                            <div className='edit-strategy-wrap'>
                                <i className='robot-icon'/>
                                <div className='edit-strategy-first' style={{marginLeft:'70px'}}>
                                    <div className='edit-strategy-header'>
                                        <span ref={message3}></span>
                                    </div>
                                    <div className='edit-strategy-content loading-icon-wrap'>
                                        <i className='loading-icon'/>
                                    </div>
                                </div>
                            </div>  
                        )
                    }]
                })
                typed3 = new Typed(message3.current, {
                    strings: ['好的，策略已生成，你可以对“策略内容”进行编辑，并确认策略'],
                    typeSpeed: 10,
                    backSpeed: 50,
                    showCursor: true,
                    cursorChar: '',
                    onComplete(self) {
                        self.cursor.style.display = 'none'; // 隐藏光标
                        setScrollList((prestate) => {
                            const list = [...prestate]
                            list.pop()
                            return [...list,{
                                index:5,
                                dom: (
                                    <Dom2 
                                        taskInfo={taskInfo} 
                                        notShowConfirmBtn={notShowConfirmBtn}
                                        selectedIds={selectedIds}
                                        enSure={enSure}
                                        rowSelection={rowSelection}
                                        columns={columns}
                                        strategyList={strategyList}
                                        allNodeList={allNodeList}
                                        timerList={tempColorTimerList}
                                        baselineList={tempBaselineList}
                                        topList={tempTopList}
                                        bottomList={tempBottomList}
                                        predictionList={tempPredictionList}
                                        enAllSure={enAllSure}
                                        refreshChart={refreshChart}
                                        currentNodeId={currentNodeId}
                                    />
                                )
                            }]
                        })
                        setIsThreeCanCLick(true)
                    },
                });
            }, 2000);
        })
        .catch(function (error) {
            message.error(`接口错误demandForecastChart`)
        });
    }      

    const refreshChart = () => {
        http.post("AIEnergy/strategyQuery", {respId:currentRespId}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const strategyList = response.data.data?.map((item) => {
               return {
                  nodeName:item?.nodeName,
                  id:item?.id,
                  content:item?.strategyContent,
                  time:item?.timePoint,
                  noHouseholds:item?.noHouseholds,
                  deviceName:item?.deviceName,
                  ensure :item?.ensure,
               }
            })
            const isShowConfirmBtn = response.data.data?.every((item) => item.ensure === 1)
            setNotShowConfirmBtn(isShowConfirmBtn)
            setStrategyList(strategyList)
            querySecondNodeList(currentRespId,isShowConfirmBtn,strategyList) 
        })
        .catch(function (error) {
            message.error(`接口错误strategyQuery`)
        });
    }

    const querySecondNodeList = (respId,isShowConfirmBtn,strategyList) => {
        http.post('AIEnergy/demandNodeQuery', {respId}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            const allNodeList = response.data.data || []
            setAllNodeList(allNodeList)
            querySecondChart(respId,currentNodeId.current,allNodeList,isShowConfirmBtn,strategyList)
        })
        .catch(function (error) {
            // message.error(`接口错误getTaskCodeList`)
        });
    }

    const querySecondChart = (respId,nodeId,allNodeList,isShowConfirmBtn,strategyList) => {
        http.post("AIEnergy/demandForecastChart",{
            respId,
            nodeId,
            systemId: "kongtiao",
            rsTime : `${currentTime} ${startTime}`,
            reTime : `${currentTime} ${endTime}`,
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

            setColorTimerList(tempColorTimerList)
            setTimerList(tempTimerList)
            setPredictionList(tempPredictionList)
            setBaselineList(tempBaselineList)
            setRealList(tempRealList)
            setTopList(tempTopList)
            setBottomList(tempBottomList)

            setScrollList((prestate) => {
                const list = [...prestate]
                list.pop()
                return [...list,{
                    index:5,
                    dom: (
                        <Dom2 
                            taskInfo={taskInfo} 
                            notShowConfirmBtn={notShowConfirmBtn}
                            selectedIds={selectedIds}
                            enSure={enSure}
                            rowSelection={rowSelection}
                            columns={columns}
                            strategyList={strategyList}
                            currentNodeId={currentNodeId}
                            allNodeList={allNodeList}
                            timerList={tempColorTimerList}
                            baselineList={tempBaselineList}
                            topList={tempTopList}
                            bottomList={tempBottomList}
                            predictionList={tempPredictionList}
                            enAllSure={enAllSure}
                            refreshChart={refreshChart}
                        />
                    )
                }]
            })
            setIsThreeCanCLick(true)
        })
        .catch(function (error) {
            message.error(`接口错误demandForecastChart`)
        });
    }      

    const enSure = (ids) => {
        http.post("AIEnergy/strategyEnsure", {ids}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            message.success('确认成功')
            selectedIds.current = []
            refreshChart()
        })
        .catch(function (error) {
            message.error('接口错误strategyLogQuery')
        });
    }

    const enAllSure = (ids) => {
        if(!ids.length){
            message.warning('请选择一条节点')
            return
        }
        http.post("AIEnergy/strategyEnsure", {ids}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then(function (response) {
            message.success('确认成功')
            selectedIds.current = []
            flushSync(() => {
                refreshChart(currentRespId)
            })
        
        })
        .catch(function (error) {
            message.error('接口错误strategyLogQuery')
        });
    }

    const changeContent = (id,strategyContentBefore,strategyContent) => {
        if(strategyContent > 12 || strategyContent < 7){
            message.error('出水温度范围需在7℃到12℃之间');
            return
        }
        http.post("AIEnergy/strategyUpdate", {
            id,
            strategyContentBefore,
            strategyContent,
        }, {
            headers: {
              'Content-Type': 'application/json'
            }
          })
        .then(function (response) {
            setTimeout(() => {
                refreshChart()
            }, 2000);
            message.success('修改成功');
        })
        .catch(function (error) {
            message.error(`接口错误strategyUpdate`)
        });
    }
    
    const confirmStrategy = () => {
        setStatus('declare')
        setScrollList([...scrollList,
            {
                index:5,
                dom:(
                    <div className='right-default-wrap'>
                        <div className='right-message'>
                            <span>确认完成，就使用这些策略</span>
                        </div>
                    </div>
                )
            },
           ]
        )
        getCSPGDeclareList(currentRespId)    
    }

    const taskClick = (item) => {
        setIsAnalysis(true)
        setCurrentTaskCode(item.taskCode)
        const item1 = taskList.find((item1) => item1.taskCode === item.taskCode)
        const {startTime,endDate,endTime } = calcEndDate(item1?.rsTime)
        setStartTime(startTime)
        setEndTime(endTime)
        setCurrentTime(endDate) 
        setSTime(item1?.rsTime)
        setETime(item1?.reTime)
        setStatus('analysis')
        setScrollList([...scrollList,
            {
                index:1,
                dom:(
                    <div className='right-default-wrap'>
                        <div className='right-message'>
                            <span>我想要分析 <span style={{margin:'0 10px'}}>{item.taskCode} </span>响应任务</span>
                        </div>
                    </div>
                )
            },
           ]
        )
        setCurrentRespId(item?.respId)
        getDecisionChainList(item?.respId,item?.taskCode)
    }

    const getCSPGDeclareList = async (respId) =>  {
        const data = await http.post('demand_resp/resp_task/getCSPGDeclareList',{
			"deviceRatedPowerSort": '1',
			"number": 1,
			"pageSize": 10,
			"respId": respId,
			"sid": "",
		})
        const tempList = data.data.data.devieInfo.content
        const invitation = data.data.data.invitation
        const totalDeclare = data.data.data.totalDeclare
        
        setScrollList((prestate) => {
            return [...prestate,{
                index:6,
                dom: (
                     <div className='edit-strategy-wrap'>
                        <i className='robot-gif-icon'/>
                        <div className='edit-strategy-first' style={{marginLeft:'70px'}}>
                            <div className='edit-strategy-header'>
                            <span className='pre-loading-text'>正在生成中...</span>
                            </div>
                            <div className='edit-strategy-content loading-icon-wrap'>
                                <i className='loading-icon'/>
                            </div>
                        </div>
                    </div>  
                )
            }]
        })

        setTimeout(() => {
            setScrollList((prestate) => {
                const list = [...prestate]
                list.pop()
                return [...list,{
                    index:6,
                    dom: (
                        <div className='submit-declare-wrap'>
                            <i className='robot-icon'/>
                            <div className='submit-declare'>
                                <div className='submit-declare-title'>
                                    <span ref={message4}></span>
                                </div>
                            </div>
                        </div>
                    )
                }]
            })
    
            typed3 = new Typed(message4.current, {
                strings: ['好的，这是根据策略推荐的申报信息，你可以对“申报负荷(kW)”进行编辑，并提交申报'],
                typeSpeed: 10,
                backSpeed: 50,
                showCursor: true,
                cursorChar: '',
                onComplete(self) {
                    self.cursor.style.display = 'none'; // 隐藏光标
                    setIsFourCanCLick(true)
                    setScrollList((prestate) => {
                        const list = [...prestate]
                        list.pop()
                        return [...list,{
                            index:2,
                            dom: (
                                <Dom3 
                                    invitation={invitation}
                                    totalDeclare={totalDeclare}
                                    columns1={columns1}
                                    rowSelection1={rowSelection1}
                                    dataSource={tempList}
                                />
                            )
                        }]
                    })
                },
            });
        }, 2000);

    }

    const getSecondCSPGDeclareList = async (respId) =>  {
        const data = await http.post('demand_resp/resp_task/getCSPGDeclareList',{
			"deviceRatedPowerSort": '1',
			"number": 1,
			"pageSize": 10,
			"respId": respId,
			"sid": "",
		})
        const tempList = data.data.data.devieInfo.content
        const invitation = data.data.data.invitation
        const totalDeclare = data.data.data.totalDeclare
    
        setScrollList((prestate) => {
            const list = [...prestate]
            list.pop()
            return [...list,{
                index:2,
                dom: (
                    <Dom3 
                        invitation={invitation}
                        totalDeclare={totalDeclare}
                        columns1={columns1}
                        rowSelection1={rowSelection1}
                        dataSource={tempList}
                    />
                )
            }]
        })

    }


    const confirmDeclare = () => {
        http.post('demand_resp/resp_task/declareSubmit?drsIds=' +drsIds+'&respId='+currentRespId).then(res =>{
            if(res.data.code==200){
                setIsFourCanCLick(false)
                refreshTaskList()
                setScrollList([...scrollList,
                    {
                        index:0,
                        dom: (
                            <div className='left-default-wrap'>
                                <i className='left-robot-icon'/>
                                <div className='left-message'>
                                    <span ref={message1}></span>
                                </div>
                            </div>
                        )
                    }
                ])
        
                typed1 = new Typed(message1.current, {
                    strings: [`恭喜你完成 ${currentTaskCode}响应任务申报 ，是否要进行其他需求响应任务的分析和申报？`],
                    typeSpeed: 10,
                    backSpeed: 50,
                    showCursor: true,
                    cursorChar: '',
                    onComplete(self) {
                        self.cursor.style.display = 'none'; // 隐藏光标
                    },
                });
            }else{
                message.info(res.data.msg)
            }
        }).catch(err =>{
            console.log(err)
        })
    }
        
    return (
        <Spin spinning={spinning} wrapperClassName='spinning-wrap'>
            <div className="chat-page">
            <div className='chat-page-header'>
                <span className='chat-page-header-title' onClick={() => {
                    setIsMainPage(true)
                }}>AI虚拟电厂Copilot</span>
            </div>
            <div className='chat-page-content'>
                <div className='scroll-wrap' id='scroll-wrap'>
                    <div className='default-header'>
                        {
							<div className='task-intro-wrap'>
							    <div className='task-intro task-intro-1'>
							        <div className='task-intro-header'>
							        </div>
							        <div className='task-intro-footer'>
							            <span>第一步：分析任务</span>
							        </div>
							    </div>
							    <div className='task-intro task-intro-2'>
							        <div className='task-intro-header'>
							        </div>
							        <div className='task-intro-footer'>
							            <span>第二步：生成策略</span>
							        </div>
							    </div>
							    <div className='task-intro task-intro-3'>
							        <div className='task-intro-header'>
							        </div>
							        <div className='task-intro-footer'>
							            <span>第三步：生成申报结果</span>
							        </div>
							    </div>
							</div>
						}
                        
						<div className='task-list-wrap' style={ isAnalysis || !isInitCanCLick ? {pointerEvents:'none'}:{}}>
                            {
                                taskList.length > 0 ?         
                                <div className='task-list-top'>
                                    {
                                        taskList.slice(0,2).map((item) => {
                                            return (
                                                <div className='task-item' onClick={() => taskClick(item)}> 
                                                    <p className='task-name'>{item?.taskCode}</p>
                                                </div>
                                            )
                                        })
                                    }
                                </div> : null
                            }
                            {
                                taskList.length > 2 ? 
                                <div className='task-list-bottom'>
                                    {
                                        taskList.slice(2,5).map((item) => {
                                            return (
                                                <div className='task-item' onClick={() => taskClick(item)}>
                                                    <p className='task-name'>{item?.taskCode}</p>
                                                </div>
                                            )
                                        })
                                    }
                                </div> : null
                            }      
                        </div>
                    </div>
                    <div className='scroll-content'>
                        {
                            scrollList.map((item,i) => {
                                return (
                                   <div style={ i !== scrollList.length-1 ? {pointerEvents:'none'}:{}}>
                                        {item.dom}
                                   </div>
                                )
                            })
                        }
                    </div>
                </div>
                <div className='btn-bottom-wrap'>
                    {
                        status === 'init' ?      
                        <div className='init-wrap' style={!isInitCanCLick ? {pointerEvents:'none'} : {}}>
                            我想要分析
                            <Select
                                showSearch
                                placeholder="可输入：任务编码"
                                optionFilterProp="children"
                                onChange={onChange}
                                style={{width:180}}
                                className='init-select-option'
                                filterOption={(input, option) => {
                                    return (String(option?.label) ?? '').toLowerCase().includes(input.toLowerCase())
                                }}
                                options={taskList.map((item) => {
                                    return {
                                        label:item.taskCode,
                                        value:item.respId
                                    }
                                })}
                            />
                            响应任务
                        </div> : null
                    }
                    {
                        status === 'analysis' ? 
                        <div className='analysis-wrap' style={!isTwoCanCLick ? {pointerEvents:'none'} : {}}>
                            <div className='reset-strategy-btn' onClick={() => {
                                refreshTaskList()
                                setStatus('init')
                            }}>重新选择任务</div>
                            <div className='generate-strategy-btn' onClick={() => generateStrategy(currentRespId)}>开始生成策略</div>
                        </div> : null
                    }
                    {
                        status === 'confirm' ?  
                        <div className='confirm-wrap' style={!isThreeCanCLick ? {pointerEvents:'none'} : {}}>
                            <div className='reset-strategy-btn' onClick={() => {
                                refreshTaskList()
                                setStatus('init')
                            }}>重新选择任务</div>
                            <div className='generate-strategy-btn' onClick={confirmStrategy}>确认完成，就使用这些策略</div>
                        </div> : null
                    } 
                    {
                        status === 'declare' ? 
                        <div className='confirm-wrap' style={!isFourCanCLick ? {pointerEvents:'none'} : {}}>
                            <div className='reset-strategy-btn' onClick={() => {
                                generateStrategy(currentRespId)
                                setIsThreeCanCLick(false)
                                setIsFourCanCLick(false)
                            }}>重新生成策略</div>
                            <div className='generate-strategy-btn' onClick={() => {
                                confirmDeclare()
                            }}>确认申报</div>
                        </div> : null
                    }
                </div>
            </div>
            </div>
        </Spin> 
    )
}