/* eslint-disable react-hooks/exhaustive-deps */
import Typed from 'typed.js';
import { useState,useRef,useEffect } from 'react'
export const RecommendStrategy = (props) => {
    const { click1,click2,click3, str1,str2,str3,setIsOneCanCLick,isOneCanCLick,recommendList } = props || {}
    const [isclick,setIsCLick] = useState(isOneCanCLick)
    const [show1,setShow1] = useState(false)
    const [show2,setShow2] = useState(false)
    const [show3,setShow3] = useState(false)
    const message1 = useRef(null);
    const message2 = useRef(null);
    const message3 = useRef(null);
    let typed1 = null
    let typed2 = null
    let typed3 = null

    useEffect(() => {
        setTimeout(() => {
           setShow1(true)
           typed1 = new Typed(message1.current, {
                strings: [
                    str1
                ],
                typeSpeed: 10,
                backSpeed: 50,
                showCursor: true,
                cursorChar: '',
                onComplete(self) {
                    self.cursor.style.display = 'none'; // 隐藏光标
                    const scroll = document.getElementById('energy-scroll-wrap');
                    scroll.scrollTop = scroll.scrollHeight;
            
                    if(recommendList?.length === 1){
                        setIsOneCanCLick(true)
                        setIsCLick(true)
                    }
                    setTimeout(() => {
                        setShow2(true)
                        typed2 = new Typed(message2.current, {
                             strings: [
                                str2
                             ],
                             typeSpeed: 10,
                             backSpeed: 50,
                             showCursor: true,
                             cursorChar: '',
                             onComplete(self) {
                                 self.cursor.style.display = 'none'; // 隐藏光标
                                 const scroll = document.getElementById('energy-scroll-wrap');
                                 scroll.scrollTop = scroll.scrollHeight;

                                 if(recommendList?.length === 2){
                                    setIsOneCanCLick(true)
                                    setIsCLick(true)                            
                                }
            
                                 setTimeout(() => {
                                    setShow3(true)
                                    typed3 = new Typed(message3.current, {
                                        strings: [
                                            str3
                                        ],
                                        typeSpeed: 10,
                                        backSpeed: 50,
                                        showCursor: true,
                                        cursorChar: '',
                                        onComplete(self) {
                                            self.cursor.style.display = 'none'; // 隐藏光标
                                            const scroll = document.getElementById('energy-scroll-wrap');
                                            scroll.scrollTop = scroll.scrollHeight;
                                            setIsOneCanCLick(true)
                                            setIsCLick(true)                                    
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


    return (
        <>
            {
                recommendList.length === 1 ? 
                <div style={!isclick ? {pointerEvents:'none'} : {}}>
                    { show1 ? <div className='recommend-strategy-item' ref={message1} onClick={() => click1()}></div> : <i className='p-loading-icon' style={{marginTop:'10px'}}/>} 
                </div> : null
            }
            
            {  recommendList.length === 2 ? 
                <div style={!isclick ? {pointerEvents:'none'} : {}}>
                    {
                        show1 ? <div className='recommend-strategy-item' ref={message1} onClick={() => click1()}></div> : <i className='p-loading-icon' style={{marginTop:'10px'}}/>
                    }
                    {
                        show1 ? <>{show2 ? <div className='recommend-strategy-item' ref={message2} onClick={() => click2()}></div> : <i className='p-loading-icon' style={{marginTop:'10px'}}/>} </> : null
                    }
                </div> : null        
            }
            
            {  recommendList.length > 2 ? 
                <div style={!isclick ? {pointerEvents:'none'} : {}}>
                     { show1 ? <div className='recommend-strategy-item' ref={message1} onClick={() => click1()}></div> : <i className='p-loading-icon' style={{marginTop:'10px'}}/>} 
                     {
                        show1 ? <>{show2 ? <div className='recommend-strategy-item' ref={message2} onClick={() => click2()}></div> : <i className='p-loading-icon' style={{marginTop:'10px'}}/>} </> : null
                     }
                     {
                        show1 && show2 ? <>{show3 ? <div className='recommend-strategy-item' ref={message3} onClick={() => click3()}></div> : <i className='p-loading-icon' style={{marginTop:'10px'}}/>} </> : null
                     }
                </div> : null        
            }
        </>
     
        // <div style={!isclick ? {pointerEvents:'none'} : {}}>
        //   { show1 ? <div className='recommend-strategy-item' ref={message1} onClick={() => click1()}></div> : <i className='p-loading-icon' style={{marginTop:'10px'}}/>} 
        //   {
        //     show1 ? <>{show2 ? <div className='recommend-strategy-item' ref={message2} onClick={() => click2()}></div> : <i className='p-loading-icon' style={{marginTop:'10px'}}/>} </> : null
        //   }
        //   {
        //     show1 && show2 ? <>{show3 ? <div className='recommend-strategy-item' ref={message3} onClick={() => click3()}></div> : <i className='p-loading-icon' style={{marginTop:'10px'}}/>} </> : null
        //   }
        // </div>
    )
}