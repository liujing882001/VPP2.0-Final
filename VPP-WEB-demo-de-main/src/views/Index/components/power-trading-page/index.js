import './index.scss'
import { Select,Tooltip,Spin,message, Input } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { Info } from './components/info'
import { Working } from './components/working'
import { formatDate,renderResponseStatus,calcEndDate } from '../../utils'
import { getTodayDate } from './utils'
import { flushSync } from 'react-dom';
import moment from 'moment';
import { AllChartWrap } from './components/all-chart-wrap'
import Typed from 'typed.js';
import http from '../../../../server/server'

export const PowerTradingPage = (props) => {
    const { setIsTradePage } = props
    const [status,setStatus] = useState('init')
    const [scrollList,setScrollList] = useState([])
    const [spinning,setSpinning] = useState(true) 
    const [tradeTaskList,setTradeTaskList] = useState([])
    const [predStartDate,setPredStartDate] = useState('')
    const [predEndDate,setPredEndDate] = useState('')
    const [taskCode,setTaskCode] = useState("")
    const selectedIds = useRef([])
    const currentNodeId = useRef('')
    const [drsIds,setDrsIds] = useState()
    const [isInitCanCLick,setIsInitCanCLick] = useState(false)
    const [isTwoCanCLick,setIsTwoCanCLick] = useState(false)
    const [isThreeCanCLick,setIsThreeCanCLick] = useState(false)
    const [isFourCanCLick,setIsFourCanCLick] = useState(false)
    const workingStrategyList = useRef([])

    let typed1 = ''
    const message1 = useRef(null);  

    const message2 = useRef(null);
    let typed2 = ''

    const message3 = useRef(null);
    let typed3 = ''

    const message4 = useRef(null);
    let typed4 = ''

    const onChange = (_value,options) => {
        let ite = {}
        tradeTaskList.forEach((item) => {
            if(item.id === _value){
                ite = item 
            }
        })
        const { label,value } = options || {}
        
        setStatus('analysis')
        setScrollList([...scrollList,
            {
                index:1,
                dom:(
                    <div className='right-default-wrap'>
                        <div className='right-message'>
                            <span>我想要分析 <span style={{margin:'0 10px'}}> {label} </span>电力市场化交易任务</span>
                        </div>
                    </div>
                )
            },
           ]
        )
        setTaskCode(label)
        getDecisionChainList(ite)
    };

    const getDecisionChainList = (item) => {
        setPredStartDate(item?.sTime?.split(" ")[0])
        setPredEndDate(item?.eTime?.split(" ")[0])
        setIsTwoCanCLick(false)
        setScrollList((prestate) => {
            return [...prestate,{
                index:2,
                dom: (
                    <Info 
                        item={item}
                        setScrollList={setScrollList}
                        setIsTwoCanCLick={() => setIsTwoCanCLick(true)}
                    />
                )
            }]
        })           
    }

    useEffect(() => {
        return () => {
           typed1?.destroy();
           typed2?.destroy();
           typed3?.destroy();
        };
    }, []);

    useEffect(() => {
        const scroll = document.getElementById('scroll-wrap');
        // 设置滚动的顶点坐标为滚动的总高度
        scroll.scrollTop = scroll.scrollHeight;
    },[scrollList])

    useEffect(() => {
        getTradeTaskList()
    },[])

    const getTradeTaskList = async () => {
        const data = await http.get('/electricityTrading/getTasks?pageNumber=1&pageSize=5')
        const list = data.data.data.content
        setTradeTaskList(list)
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
            strings: ['你想分析哪条电力市场化交易任务？'],
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

    const generateStrategy = () => {
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
        generateChart()
    }

    const generateChart = () => {
        setScrollList((prestate) => {
            return [...prestate,{
                index:4,
                dom: (
                    <div className='edit-strategy-wrap'>
                        <i className='robot-gif-icon'/>
                        <div className='edit-strategy-first' style={{marginLeft:'70px',width:'100%'}}>
                            <div className='edit-strategy-header'>
                                <span className='pre-loading-text'>数据分析中...</span>
                            </div>
                            <div className='edit-strategy-content loading-icon-wrap'>
                                <i className='loading-icon'/>
                            </div>
                        </div>
                    </div>  
                )
            }]
        })
        Promise.all([http.post('/electricityTrading/priceChart',{
            "nodeId": "e4653aad857c96f4c2ea4fd044bffbea",
            "st": predStartDate,
            "et": predStartDate
        }),http.post('/tradePower/weatherChart',{
            "startDate": predStartDate,
            "endDate": predStartDate
        }),http.post('/tradePower/powerAnalysis',{
            "startDate": predStartDate,
            "endDate": predStartDate
        })]).then((res) => {
            setScrollList((prestate) => {
                const list = [...prestate]
                list.pop()
                return [...list,{
                    index:4,
                    dom: (
                        <AllChartWrap 
                           isReady={true}
                           setIsThreeCanCLick={setIsThreeCanCLick} 
                           predStartDate={predStartDate} 
                           predEndDate={predEndDate} 
                           taskCode={taskCode} 
                           defaultPriceList={res[0]?.data?.priceLists}
                           defaultWeatherList={res[1]?.data?.data}
                           defaultPowerList={res[2]?.data?.data}
                        />
                    )
                }]
            })
        })    
    }

    const confirmStrategy = () => {
        setStatus('declare')
        http.post('tradePower/saveSend').then((res) => {
            if(res.data.code === 200){
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
                getCSPGDeclareList()    
            }else{
                setScrollList([...scrollList,
                    {
                        index:5,
                        dom:(
                            <div className='right-default-wrap'>
                                <div className='right-message'>
                                    <span>{res?.data?.msg}</span>
                                </div>
                            </div>
                        )
                    },
                ])
            }
           
        })
        
    }

    const taskClick = (item) => {
        setStatus('analysis')
        setScrollList([...scrollList,
            {
                index:1,
                dom:(
                    <div className='right-default-wrap'>
                        <div className='right-message'>
                            <span>我想要分析 <span style={{margin:'0 10px'}}>{item.id} </span>电力市场化交易任务</span>
                        </div>
                    </div>
                )
            },
           ]
        )
        setTaskCode(item.id)
        getDecisionChainList(item)
    }

    const getCSPGDeclareList = async () =>  {
        setScrollList((prestate) => {
            return [...prestate,{
                index:6,
                dom: (
                    <Working setIsFourCanCLick={setIsFourCanCLick} predStartDate={predStartDate} predEndDate={predEndDate} taskCode={taskCode} workingStrategyList={workingStrategyList}/>                           
                )
            }]
        })
    }

    const confirmDeclare = (func) => {
        http.post('/tradePower/confirmDelivery',{
            taskCode:taskCode,
            taskData:workingStrategyList.current
        }).then((res) => {
            if(res.data.code === 200){
                setIsFourCanCLick(false)
                setScrollList([...scrollList,
                    {
                        index:7,
                        dom: (
                            <div className='left-default-wrap'>
                                <i className='left-robot-icon'/>
                                <div className='left-message'>
                                    <span ref={message2}></span>
                                </div>
                            </div>
                        )
                    }
                ])
                setStatus('init') 
                setTimeout(() => {
                    typed2 = new Typed(message2.current, {
                        strings: [`恭喜你完成 ${taskCode}电力市场化交易任务申报 ，是否要进行其他电力市场化交易任务的分析和申报？`],
                        typeSpeed: 10,
                        backSpeed: 50,
                        showCursor: true,
                        cursorChar: '',
                        onComplete(self) {
                            self.cursor.style.display = 'none';
                        },
                    });
                }, 200);
            }else if(res.data.code === 501){
                confirmDeclare()
            }
        })
      
    }
        
    return (
        <Spin spinning={spinning} wrapperClassName='spinning-wrap'>
            <div className="trade-chat-page">
                <div className='trade-chat-page-header'>
                    <span className='trade-chat-page-header-title' onClick={() => {
                        setIsTradePage(false)
                    }}>AI虚拟电厂Copilot</span>
                </div>
                <div className='trade-chat-page-content'>
                    <div className='scroll-wrap' id='scroll-wrap'>
                        <div className='trade-default-header'>
                            {
								// <div className='trade-task-intro-wrap'>
								//     <div className='trade-task-intro trade-task-intro-1'>
								//         <div className='trade-task-intro-header'>
								//         </div>
								//         <div className='trade-task-intro-footer'>
								//             <span>第一步：分析任务</span>
								//         </div>
								//     </div>
								//     <div className='trade-task-intro trade-task-intro-2'>
								//         <div className='trade-task-intro-header'>
								//         </div>
								//         <div className='trade-task-intro-footer'>
								//             <span>第二步：生成策略</span>
								//         </div>
								//     </div>
								//     <div className='trade-task-intro trade-task-intro-3'>
								//         <div className='trade-task-intro-header'>
								//         </div>
								//         <div className='trade-task-intro-footer'>
								//             <span>第三步：生成申报结果</span>
								//         </div>
								//     </div>
								// </div>
							}
                            {
								// <div className='trade-task-list-wrap'>
								//     {
								//         tradeTaskList.length > 0 ?         
								//         <div className='trade-task-list-top' style={ status !== 'init' ? {pointerEvents:'none'}:{}}>
								//             {
								//                 tradeTaskList?.slice(0,2).map((item) => {
								//                     return (
								//                         <div className='trade-task-item' onClick={() => taskClick(item)}> 
								//                             <p className='trade-task-name'>{item?.id}</p>
								//                         </div>
								//                     )
								//                 })
								//             }
								//         </div> : null
								//     }
								//     {
								//         tradeTaskList?.length > 2 ? 
								//         <div className='trade-task-list-bottom' style={ status !== 'init' ? {pointerEvents:'none'}:{}}>
								//             {
								//                 tradeTaskList?.slice(2,5).map((item) => {
								//                     return (
								//                         <div className='trade-task-item' onClick={() => taskClick(item)}>
								//                             <p className='trade-task-name'>{item?.id}</p>
								//                         </div>
								//                     )
								//                 })
								//             }
								//         </div> : null
								//     }      
								// </div>
							}
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
                                    onChange={onChange}
                                    style={{width:180}}
                                    className='init-select-option'
                                    options={tradeTaskList.map((item) => {
                                        return {
                                            label:item.id,
                                            value:item.id
                                        }
                                    })}
                                />
                                电力市场化交易
                            </div> : null
                        }
                        {
                            status === 'analysis' ? 
                            <div className='analysis-wrap' style={!isTwoCanCLick ? {pointerEvents:'none'} : {}}>
                                <div className='reset-strategy-btn' onClick={async() => {
                                    setStatus('init')
                                }}>重新选择任务</div>
                                <div className='generate-strategy-btn' onClick={() => {
                                    setIsThreeCanCLick(false)
                                    generateStrategy()
                                }}>开始生成策略</div>
                            </div> : null
                        }
                        {
                            status === 'confirm' ?  
                            <div className='confirm-wrap' style={!isThreeCanCLick ? {pointerEvents:'none'} : {}}>
                                <div className='reset-strategy-btn' onClick={async() => {
                                    setIsThreeCanCLick(false)
                                    setStatus('init')
                                }}>重新选择任务</div>
                                <div className='generate-strategy-btn' onClick={confirmStrategy}>保存并下发</div>
                            </div> : null
                        } 
                        {
                            status === 'declare' ? 
                            <div className='confirm-wrap' style={!isFourCanCLick ? {pointerEvents:'none'} : {}}>
                                <div className='reset-strategy-btn' onClick={() => {
                                    generateStrategy()
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