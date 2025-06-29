import './index.scss'
import { useEffect,useState,useRef } from 'react'
import { Energy } from './components/energy'
import { Strategy } from './components/strategy'
import Typed from 'typed.js';
    import { Income } from './components/income';
import http from '../../../../server/server';

export const Dom4 = (props) => {
    const [isShow1,setIsShow1] = useState(false)
    const [isShow2,setIsShow2] = useState(false)
    const [isShow3,setIsShow3] = useState(false)
    const [isShow4,setIsShow4] = useState(false)

    const message1 = useRef(null);
    let typed1 = ''

    useEffect(() => {
       setTimeout(() => {
         setIsShow1(true)
       },2500)
       setTimeout(() => {
        setIsShow2(true)
       },5000)
       setTimeout(() => {
        setIsShow3(true)
       }, 8000);

       setTimeout(() => {
        setIsShow4(true)
        if(message1?.current){
            // eslint-disable-next-line react-hooks/exhaustive-deps
            typed1 = new Typed(message1?.current, {
                strings: ['好的，请查看最新的调度策略内容。'],
                typeSpeed: 10,
                backSpeed: 50,
                showCursor: true,
                cursorChar: '',
                onComplete(self) {
                    setIsShow4(true)
                },
            });
        }
       }, 9500);
    },[])
    
    return (
        <>
            <div className='dom4-wrapper'>
                { !isShow4 ? <i className='dom4-robot-gif-icon'/> : <i className='dom4-robot-icon'/> }
                    <div className='main-strategy'>
                    <div className='main-strategy-title'>
                        <span ref={message1}>{!isShow4 ? '调度策略查询中...' : ''}</span>
                    </div>
                    <div className="main-strategy-content">
                        <Energy {...props} />
                        { isShow1 ? <Strategy {...props} isShow2={isShow2} isShow3={isShow3}/> : null} 
                    </div>
                </div>
            </div> 
        </>
       
    )
}