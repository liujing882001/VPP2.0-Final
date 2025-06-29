import { renderResponseType,renderResponseLevel,calcNum } from '../../utils'
import Typed from 'typed.js';
import React, { useEffect, useRef,useState } from 'react';
import { useSpring, animated } from 'react-spring';
import './index.scss'
import classNames from 'classnames';
export const Dom1 = (props) => {
    const { taskCode,rsTime,reTime,respType,respLevel,respLoad,nodeList,setIsTwoCanCLick } = props || {}
    const nodeNameList = nodeList?.map((item) => item?.nodeName)
    const message1 = useRef(null);
    const message2 = useRef(null);
    const message3 = useRef(null);
    const message4 = useRef(null);
    const message5 = useRef(null);
    const message6 = useRef(null);
    const [show1,setShow1] = useState(false)
    const [show2,setShow2] = useState(false)
    const [show3,setShow3] = useState(false)
    const [show4,setShow4] = useState(false)
    const [show5,setShow5] = useState(false)
    const [show6,setShow6] = useState(false)
    let typed1 = null
    let typed2 = null
    let typed3 = null
    let typed4 = null
    let typed5 = null
    let typed6 = null
    const [pause,setPause] = useState(false)
    const [showTitle,setShowTitle] = useState(false)
    const [height, setHeight] = useState(130);

    const style = useSpring({
        to: { height: `${height}px` },
        from: { height: '42px' },
        config: { duration: 300 },
    });
     
    useEffect(() => {
        setTimeout(() => {
           setShow1(true)
           typed1 = new Typed(message1.current, {
                strings: [
                    `任务编码：${taskCode}`
                ],
                typeSpeed: 10,
                backSpeed: 50,
                showCursor: true,
                cursorChar: '',
                onComplete(self) {
                    self.cursor.style.display = 'none'; // 隐藏光标
                    setTimeout(() => {
                        setHeight(165)
                        setShow2(true)
                        typed2 = new Typed(message2.current, {
                             strings: [
                                 `响应时间段：${rsTime}-${reTime?.split(' ')[1]}`
                             ],
                             typeSpeed: 10,
                             backSpeed: 50,
                             showCursor: true,
                             cursorChar: '',
                             onComplete(self) {
                                 self.cursor.style.display = 'none'; // 隐藏光标
                                 setTimeout(() => {
                                    setHeight(195)
                                    setShow3(true)
                                    typed3 = new Typed(message3.current, {
                                        strings: [
                                            `响应类型：${renderResponseType(respType)}`
                                        ],
                                        typeSpeed: 10,
                                        backSpeed: 50,
                                        showCursor: true,
                                        cursorChar: '',
                                        onComplete(self) {
                                        self.cursor.style.display = 'none'; // 隐藏光标
                                        setTimeout(() => {
                                            setHeight(225)
                                            setShow4(true)
                                            typed4 = new Typed(message4.current, {
                                                    strings: [
                                                        `响应级别：${renderResponseLevel(respLevel)}`
                                                    ],
                                                    typeSpeed: 10,
                                                    backSpeed: 50,
                                                    showCursor: true,
                                                    cursorChar: '',
                                                    onComplete(self) {
                                                        self.cursor.style.display = 'none'; // 隐藏光标
                                                        setTimeout(() => {
                                                        setHeight(260)
                                                        setShow5(true)
                                                     
                                                        typed5 = new Typed(message5.current, {
                                                                strings: [
                                                                    `负荷需求(kW)：${calcNum(respLoad)}`
                                                                ],
                                                                typeSpeed: 10,
                                                                backSpeed: 50,
                                                                showCursor: true,
                                                                cursorChar: '',
                                                                onComplete(self) {
                                                                    self.cursor.style.display = 'none'; // 隐藏光标
                                                                    
                                                                    setTimeout(() => {
                                                                        setHeight(264)
                                                                        setShow6(true)
                                                                        typed6 = new Typed(message6.current, {
                                                                                strings: [
                                                                                    `参与节点：${nodeNameList?.join('、')}`
                                                                                ],
                                                                                typeSpeed: 10,
                                                                                backSpeed: 50,
                                                                                showCursor: true,
                                                                                cursorChar: '',
                                                                                onComplete(self) {
                                                                                    self.cursor.style.display = 'none'; // 隐藏光标
                                                                                    setShowTitle(true)
                                                                                    setIsTwoCanCLick()
                                                                                    setPause(true)
                                                                                                                                    
                                                                                }
                                                                            });

                                                                    }, 300);
                                                                    setTimeout(() => {
                                                                        const scroll = document.getElementById('scroll-wrap');
                                                                        // 设置滚动的顶点坐标为滚动的总高度
                                                                        scroll.scrollTop = scroll.scrollHeight 
                                                                    }, 400);
                                                                }
                                                            });
                                                        }, 300);
                                                    }
                                                });
                                            }, 300);
                                        }
                                    });
                                 }, 300);
                             }
                        });
                    }, 300);
                }
            });
        }, 300);
        
    },[])

    useEffect(() => {
        const scroll = document.getElementById('scroll-wrap');
        // 设置滚动的顶点坐标为滚动的总高度
        scroll.scrollTop = scroll.scrollHeight;
    },[show1,show2,show3,show4,show5])

    return (
        <div className='generate-strategy-wrap'>
            {showTitle ?  <i className='robot-icon'/> :  <i className='robot-gif-icon'/>}
            <animated.div className='generate-strategy' style={style}>
                <div className='generate-strategy-header'>
                    {showTitle ? <span>好的，任务信息如下，是否要生成策略？</span> : <span>正在分析中...</span>}
                </div>
                <div className='generate-strategy-content' style={{paddingBottom:0}}>
                    {show1 ? <p ref={message1}></p>:<i className='p-loading-icon'/>} 
                    {
                        show1 ? <>{show2 ? <p ref={message2}></p>:<i className='p-loading-icon'/>} </> : null
                    }
                    {
                        show1 && show2 ? <>{show3 ? <p ref={message3}></p>:<i className='p-loading-icon'/>} </> : null
                    }
                    {
                        show1 && show2 && show3  ? <>{show4 ? <p ref={message4}></p>:<i className='p-loading-icon'/>} </> : null
                    }
                    {
                        show1 && show2 && show3 && show4  ? <>{show5 ? <p ref={message5}></p>:<i className='p-loading-icon'/>} </> : null
                    }
                    {
                        show1 && show2 && show3 && show4 && show5  ? <>{show6 ? <p ref={message6}></p>:<i className='p-loading-icon'/>} </> : null
                    }
                </div> 
            </animated.div>
        </div>
        
    )
}