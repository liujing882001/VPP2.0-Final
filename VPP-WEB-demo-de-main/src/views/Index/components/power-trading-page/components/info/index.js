/* eslint-disable react-hooks/exhaustive-deps */
import Typed from 'typed.js';
import classNames from 'classnames';
import React, { useEffect, useRef,useState } from 'react';
import { useSpring, animated } from 'react-spring';
import './index.scss'

export const Info = (props) => {
    const { item,setIsTwoCanCLick } = props || {}
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
                    `任务编码：${item?.id}`
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
                                 `运行时段：${item?.sTime?.split(' ')[0]}-${item?.eTime?.split(' ')[0]}`
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
                                            `任务类型：${item?.tradeType}`
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
                                                        `节点：${item?.station}`
                                                    ],
                                                    typeSpeed: 10,
                                                    backSpeed: 50,
                                                    showCursor: true,
                                                    cursorChar: '',
                                                    onComplete(self) {
                                                        self.cursor.style.display = 'none'; // 隐藏光标
                                                        setTimeout(() => {
                                                            setShowTitle(true)
                                                            setIsTwoCanCLick()
                                                        })                                                  
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
    },[show1,show2,show3,show4,showTitle])

    return (
        <div className='generate-strategy-wrap'>
            {showTitle ?  <i className='robot-icon'/> :  <i className='robot-gif-icon'/>}
            <animated.div className='generate-strategy' style={style}>
                <div className='generate-strategy-header'>
                    {showTitle ? <span>好的，任务信息如下，是否要生成策略？</span> : <span>正在分析中...</span>}
                </div>
                <div className='generate-strategy-content'>
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
                </div> 
            </animated.div>
        </div>   
    )
}