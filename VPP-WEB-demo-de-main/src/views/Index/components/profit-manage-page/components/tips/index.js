/* eslint-disable react-hooks/exhaustive-deps */
import Typed from 'typed.js';
import { useState,useRef,useEffect } from 'react'
import './index.scss'
export const Tips = (props) => {
    const { str1,str2,str3,click,isShowStopBtn,changText,scrollToBottom } = props || {}
    const [show1,setShow1] = useState(false)
    const [show2,setShow2] = useState(false)
    const [show3,setShow3] = useState(false)
    const [showChangeBtn,setShowChangBtn] = useState(false)
    const message1 = useRef(null);
    const message2 = useRef(null);
    const message3 = useRef(null);
    let typed1 = null
    let typed2 = null
    let typed3 = null

    useEffect(() => {
        setTimeout(() => {
            setShowChangBtn(true)
        }, 1700);
        setTimeout(() => {
           str1 && setShow1(true)
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
                    setTimeout(() => {
                        str2 && setShow2(true)
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
                                 setTimeout(() => {
                                    str3 && setShow3(true)
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
                                            scrollToBottom();
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
            <div style={isShowStopBtn ? { pointerEvents:'none',marginTop:'24px' }:{ marginTop:'24px' }}>
                { show1 && str1 ? <div className='default-tips' ref={message1} onClick={() => click(str1)}></div> : <i className='p-loading-icon' style={{margin:'10px 0 0 60px'}}/>} 
                {
                    show1 && str2 ? <>{show2 ? <div className='default-tips' ref={message2} onClick={() => click(str2)}></div> : <i className='p-loading-icon' style={{margin:'10px 0 0 60px'}}/>} </> : null
                }
                {
                    show1 && show2 && str3 ? <>{show3 ? <div className='default-tips' ref={message3} onClick={() => click(str3)}></div> : <i className='p-loading-icon' style={{margin:'10px 0 0 60px'}}/>} </> : null
                }
            </div>  
            {
                showChangeBtn ? 
                <div className='change-btn' onClick={changText}>
                    <i className='change-btn-icon'/>
                    <span className='chang-btn-text'>换一换</span>
                </div> : null
            }
   
        </>
 
    )
}