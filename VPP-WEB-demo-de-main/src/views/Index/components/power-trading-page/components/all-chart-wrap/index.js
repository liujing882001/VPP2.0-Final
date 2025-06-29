import './index.scss'
import { useEffect,useState,useRef } from 'react'
import { MainChart } from '../main-chart'
import moment from 'moment';
import http from '../../../../../../server/server'

import Typed from 'typed.js';
import { Energy } from '../energy'
import { Dispatch } from '../dispatch';
export const AllChartWrap = (props) => {
    const { predStartDate,predEndDate,defaultPriceList,defaultWeatherList,defaultPowerList,taskCode,isReady } = props || {}
    const [isShow1,setIsShow1] = useState(true)
    const [isFinish,setIsFinish] = useState(false)
    const [showDatePick,setShowDatePick] = useState(false);
    const [dateRange,setDateRange] = useState([]);
    const [priceList,setPriceList] = useState(defaultPriceList)
    const [weatherList,setWeatherList] = useState(defaultWeatherList)
    const [powerList,setPowerList] = useState(defaultPowerList)

    const message1 = useRef(null);
    let typed1 = ''

    const getMainChartData = (startDate,endDate) => {
        Promise.all([http.post('/electricityTrading/priceChart',{
           "nodeId": "e4653aad857c96f4c2ea4fd044bffbea",
            "st": startDate,
            "et": endDate
        }),http.post('/tradePower/weatherChart',{
            "startDate": startDate,
            "endDate": endDate
        }),http.post('/tradePower/powerAnalysis',{
            "startDate": startDate,
            "endDate": endDate
        })]).then((res) => {
            setPriceList(res[0]?.data?.priceLists)
            setWeatherList(res[1]?.data?.data)
            setPowerList(res[2]?.data?.data)
        })  
    }


    useEffect(() => {
       setTimeout(() => {
         if(message1?.current){
            setIsShow1(false)
            // eslint-disable-next-line react-hooks/exhaustive-deps
            typed1 = new Typed(message1?.current, {
                strings: ['好的，策略已生成，以下数据均为未来三天的数据，您可查看并编辑。'],
                typeSpeed: 10,
                backSpeed: 50,
                showCursor: true,
                cursorChar: '',
                onComplete(self) {
                  setIsShow1(false)
                  setShowDatePick(true)
                  setDateRange([moment('2023-05-01', 'YYYY-MM-DD'), moment('2023-05-02', 'YYYY-MM-DD')])
                  props?.setIsThreeCanCLick(true)
                },
            });
         }
       }, 12000);
    },[])
    
    return (
        <>
            <div className='allChartWrap-wrapper'>
                { isShow1 ? <i className='allChartWrap-robot-gif-icon'/> : <i className='allChartWrap-robot-icon'/> }
                    <div className='main-strategy'>
                    <div className='main-strategy-title'>
                        <span ref={message1}>{ isShow1 ? '数据分析中...':""}</span>
                    </div>
                    {
                        isReady ?   
                        <div className="main-strategy-content">
                            <MainChart getMainChartData={getMainChartData} priceList={priceList} weatherList={weatherList} powerList={powerList} showDatePick={showDatePick} dateRange={dateRange} isFinish={isFinish} setIsFinish={setIsFinish} {...props} />
                            {isFinish ? <Energy isFinish={isFinish} {...props}/>: null} 
                        </div> : 
                        <div className='default-loading-icon-wrap' style={{height:'280px'}}>
                            <i className='default-loading-icon'/>
                        </div>
                    }
                </div>
            </div> 
        </>
    )
}