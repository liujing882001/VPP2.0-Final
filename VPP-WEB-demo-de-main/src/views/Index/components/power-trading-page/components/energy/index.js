import { tableTimer,isWindows } from '../../../../utils'
import { getBetweenDate } from '../../utils'
import { ViewModal } from './components/view-modal'
import { EditModal } from './components/edit-modal'
import { useEffect,useState,useRef, useMemo } from 'react'
import { DatePicker,Button } from 'antd';
import { Popover } from 'antd'
import { Dispatch } from '../dispatch'
import moment from 'moment';
import Typed from 'typed.js';
import classNames from 'classnames'
import http from '../../../../../../server/server'
import dayjs from 'dayjs';
import { getTomorrowDate } from '../../utils'

import './index.scss'
const { RangePicker } = DatePicker

export const Energy = (props) => {
    let today = new Date();
    today.setDate(today.getDate() + 1);
    const { nodeId,systemId,setIsThreeCanCLick,predStartDate,predEndDate,taskCode } = props;
    const [startDate,setStartDate] = useState('')
    const [endDate,setEndDate] = useState('')
    const [strategyList,setStrategyList] = useState([])
    const [tableData,setTableData] = useState([])
    const [curveDataList,setCurveDataList] = useState([])
    const [isEdit,setEdit] = useState(true);
    const [selected,setSelected] = useState([]);
    const [editModalVisible,setEditModalVisible] = useState(false)
    const [currentSelect,setCurrentSelect] = useState({})
    const [viewModalVisible,setViewModalVisible] = useState(false)
    const [renderTableData,setRenderTableData] = useState([...tableData])
    const [isShow1,setIsShow1] = useState(true)
    const [movable,setMovable] = useState({
        dom: null,
        top: 0,
        left: 0,
        width: 0,
        height: 0
    })
    const [currentDate,setCurrentDate] = useState(predStartDate)
    const message1 = useRef(null);
    let typed1 = ''
    const moving = useRef(false)
    const [items,setItems] = useState({});
    const start = useRef({  x: 0, y: 0})
    const end = useRef({  x: 0, y: 0})
    const current = useRef({  x: 0, y: 0})
    const angleStart = useRef({  x: 0, y: 0})
    const angleCurrent = useRef({  x: 0, y: 0})
    const angleEnd = useRef({  x: 0, y: 0})
    const [isShowMove,setIsShowMove] = useState(false)    
    const isUp = useRef(true)
    const timer = new Date().getTime()

    let point = {
        getPosition() {
            let angle = []; //角
            angle.push(angleStart.current); //起点
            angle.push(angleEnd.current); //终点
            angle.push({
                x: angleEnd.current.x,
                y: angleStart.current.y
            }); //对角
            angle.push({
                x: angleStart.current.x,
                y: angleEnd.current.y
            }); //对角  
            return angle;
        },
        swapArrayElements(array, index0, index1, index2, index3) {
            var temp = array[index0];
            array[index0] = array[index1];
            array[index1] = temp;

            var temp1 = array[index2];
            array[index2] = array[index3];
            array[index3] = temp1;
        },
        isSelected(rect) {
            if(!rect){
                return
            }
            let angle = point.getPosition(); //获取四个角的坐标
            if(isUp.current){
                this.swapArrayElements(angle,0,1,2,3)
            }
            let rectangle = [
                {
                    x: rect.x,
                    y: rect.y
                },
                {
                    x: rect.x + rect.width,
                    y: rect.y + rect.height
                },
                {
                    x: rect.x + rect.width,
                    y: rect.y
                },
                {
                    x: rect.x,
                    y: rect.y + rect.height
                }
            ];
            for (let pos of rectangle) {

                if (pos.x > angle[0].x &&
                    pos.x < angle[1].x &&
                    pos.y > angle[0].y &&
                    pos.y < angle[1].y) {
                 return true;
                }
            }
            for (let pos of angle) {
                if (pos.x > rectangle[0].x &&
                    pos.x < rectangle[1].x &&
                    pos.y > rectangle[0].y &&
                    pos.y < rectangle[1].y) {

                    return true;
                }
            }
            if (angle[0].x < rectangle[0].x &&
                angle[1].x > rectangle[1].x &&
                angle[0].y > rectangle[0].y &&
                angle[  1].y < rectangle[1].y) {

                return true;
            }
            if (angle[0].x > rectangle[0].x &&
                angle[1].x < rectangle[1].x &&
                angle[0].y < rectangle[0].y &&
                angle[1].y > rectangle[1].y) {
                return true;
            }
            return false;
        }
    }

    let push = (dom, timer, indey) => {
        const map = items
        if(!map[timer]){
            map[timer]= []
        }else{
            map[timer][indey] = {selected:false, dom: dom}
        }
        setItems(map)
    }

    const onMouseMove = (e) => {
        moving.current = true
        let parent = document.getElementById(`energy-content-table-bottom-${timer}`)
        const clientX = Math.floor(e.clientX - parent.getBoundingClientRect().left)
        const clientY = Math.floor(e.clientY - parent.getBoundingClientRect().top)
        if (!moving.current) return;
        if (clientX - start.current.x > 0 && clientY - start.current.y) {
         isUp.current = false
        } else {
         isUp.current = true
        }
        current.current.x = clientX;
        current.current.y = clientY;
        angleCurrent.current.x = e.clientX
        angleCurrent.current.y = e.clientY
        
        setMovable({
         dom: movable.dom,
         top: Math.min(start.current.y, current.current.y) + "px",
         left: Math.min(start.current.x, current.current.x) + "px",
         width:  Math.abs(current.current.x - start.current.x),
         height:  Math.abs(current.current.y - start.current.y)
        })      
    }
 
    const onMouseDown = (e) =>{
         setIsShowMove(true)
         let parent = document.getElementById(`energy-content-table-bottom-${timer}`)
         const clientX = Math.floor(e.clientX - parent.getBoundingClientRect().left)
         const clientY = Math.floor(e.clientY - parent.getBoundingClientRect().top)
 
         start.current.x = clientX;
         start.current.y = clientY;
         angleStart.current.x = e.clientX
         angleStart.current.y = e.clientY
         setMovable({
             dom: null,
             top: start.current.y + "px",
             left: start.current.x + "px",
             width: 0,
             height: 0
         })  
    }
 
    const onMouseUp = (e) => {
        let parent = document.getElementById(`energy-content-table-bottom-${timer}`)
        
        const clientX = Math.floor(e.clientX - parent.getBoundingClientRect().left)
        const clientY = Math.floor(e.clientY - parent.getBoundingClientRect().top)

        if (moving.current){
            setMovable({
                dom: movable.dom,
                top: Math.min(start.current.y, current.current.y) + "px",
                left: Math.min(start.current.x, current.current.x) + "px",
                width:  Math.abs(current.current.x - start.current.x),
                height:  Math.abs(current.current.y - start.current.y)
            })         
            end.current.x = clientX;
            end.current.y = clientY;
            angleEnd.current.x = e.clientX
            angleEnd.current.y = e.clientY
            moving.current = false
    
            setSelected({}); 
            
            let keys = Object.keys(items)   
            const tempObj = []
            for(const key of keys){
                const value = items[key]
                value.forEach((item, i) => {
                    if (point.isSelected(item?.dom?.getBoundingClientRect())) {
                        tempObj.push({nodeName:key, index:i,data:item,})
                    }
                })
            }
            setSelected(tempObj)
            if(tempObj.length){
                setEditModalVisible(true)
            }else{
                start.current.y = 0;
                start.current.x = 0;
                end.current.y = 0;
                end.current.x = 0;
                moving.current = false
                setIsShowMove(false)
                setMovable({
                    dom: null,
                    top: 0,
                    left: 0,
                    width: 0,
                    height: 0
                })
            }
        } 
    }

    const getStrategyData = async (startDate,endDate) => {
        const res = await http.post('/tradePower/schedulingStrategy',{
            "queryDate": startDate,
            taskCode:taskCode
        })
        if(res?.data?.code === 501){
            getStrategyData(startDate,endDate)
            return
        }
        const list = res?.data?.data
        const res1 = await http.post('/tradePower/dispatchCurve',{
            "queryDate": startDate,
            "strategyData": list
        })
        const tempList = list?.filter((item) => item.date === startDate)
        setTableData(tempList)
        setStrategyList(list)
        setCurveDataList(res1?.data?.data)
    }

    const getCurveData = async (queryDate,strategyData) => {
        const res = await http.post('/tradePower/dispatchCurve',{
            "queryDate": queryDate,
            "strategyData": strategyData
        })
        if(res?.data.code === 501){
            getCurveData(queryDate,strategyData)
        }
        setCurveDataList(res?.data?.data)
    }

    const disabledDate = (current) => {
        const res =  getBetweenDate(predStartDate,predEndDate)
        return !(current && (current.isSame(res[0]) || current.isSame(res[1]) || current.isSame(res[2])));
    }
      
    
    useEffect(() => {
        getStrategyData(predStartDate,predEndDate)

        if(message1?.current){
            // eslint-disable-next-line react-hooks/exhaustive-deps
            typed1 = new Typed(message1?.current, {
                strings: ['调度策略内容生成中....'],
                typeSpeed: 30,
                backSpeed: 100,
                showCursor: true,
                cursorChar: '',
                onComplete(self) {
                    setTimeout(() => {
                        setIsShow1(false)
                        document.getElementById('energy-scroll-wrap')?.addEventListener('scroll', function() {
                            setMovable({
                                dom: null,
                                top: 0,
                                left: 0,
                                width: 0,
                                height: 0
                            })
                        });   
                    },2000)
                },
            });
        }
    },[])

    // useEffect(() => {
    //     const scroll = document.getElementById('energy-scroll-wrap');
    //     scroll.scrollTop = scroll.scrollHeight;
    // },[isShow])

    return (
        <>
            {
                <div className="energy-content-wrapper">
                    <div className='energy-content-wrapper-top'>
                         <span ref={message1}>{isShow1 ? '':"调度策略内容"}</span>
                         <div className='energy-query-wrap'>
                            <DatePicker
                               style={{height:'26px',marginRight:'16px'}}
                               defaultValue={dayjs(predStartDate, 'YYYY-MM-DD')}
                               onChange={(dates,dateStrings)=> {
                                    setCurrentDate(dateStrings)
                               }}
                               disabledDate={disabledDate}
                               format={'YYYY-MM-DD'}
                             />
                            <Button type="Button" className='energy-query-btn' onClick={() => {
                                const tempList = strategyList?.filter((item) => item.date === currentDate)
                                setTableData(tempList)
                                getCurveData(currentDate,strategyList)
                            }}>查询</Button>
                        </div>
                    </div>
                    {
                        isShow1 ? <div className='loading-wrapper'><i className='loading-icon'/></div> :
                        <div className='energy-content-wrapper-bottom'>
                            <div className='energy-content-center'>
                                <div className='energy-content-center-left'>
                                </div>
                                <div className='energy-content-center-right'>
                                    <div className='dots-text-wrap'>
                                            <span className='green-dot'></span>
                                            <span className='dot-text'>充电</span>
                                    </div>
                                    <div className='dots-text-wrap'>
                                            <span className='yellow-dot'></span>
                                            <span className='dot-text'>放电</span>
                                    </div>
                                    <div className='dots-text-wrap'>
                                            <span className='grey-dot'></span>
                                            <span className='dot-text'>待机</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div className='energy-content-table'>
                                <div className='table-bg'>
                                    <div className='table-bg-timer table-bg-timer1'>
                                        {
                                            tableTimer.map((item,i) => {
                                                return <div className='table-bg-timer-item'>
                                                    {item}
                                                </div>
                                            })
                                        }
                                    </div>
                                    <div className='table-bg-top table-bg-top1'>
                                        {
                                            new Array(24).fill(0).map(() => {
                                                return <div className='table-bg-top-item'></div>
                                            })
                                        }
                                    </div>  
                                    <div className='table-bg-bottom table-bg-bottom1'>
                                        {
                                            new Array(24).fill(0).map(() => {
                                                return <div className='table-bg-bottom-item'></div>
                                            })
                                        }
                                    </div>
                                    <div className='energy-content-table-bottom1' style={{top:'17px'}} id={`energy-content-table-bottom-${timer}`} onMouseMove={isEdit ? onMouseMove :undefined} onMouseDown={isEdit ? onMouseDown: undefined} onMouseUp={isEdit ? onMouseUp : undefined}>
                                        {isEdit ? <div className={classNames('move')} key={new Date().getTime()} style={{top:movable.top,left:movable.left,width:movable.width,height:movable.height,zIndex:'999',display:isShowMove ? 'block' :'none'}}></div> : null} 
                                        {
                                            tableData[0]?.strategy?.map((item,index) => {
                                                return (
                                                    <div className='table-content-item-wrap special-table-content-item-wrap' key={index}>
                                                        <span className={classNames('timer',{'isWindow':isWindows()})} style={{paddingLeft:'10px'}}>{index === 0 ? '储能001':'储能002'}</span>
                                                        {
                                                            item?.list.map((ite,i) => {
                                                                let color = ''
                                                                switch(ite?.type){
                                                                    case "充电":
                                                                        color = '#32D64B';
                                                                        break
                                                                    case "待机":
                                                                        color = '#214469';
                                                                        break   
                                                                    case "放电":
                                                                        color = '#FF9500';
                                                                        break
                                                                    default:
                                                                    break
                                                                }

                                                                return  (
                                                                    <div className={classNames('table-content-item table-content-item1',{'selected':selected.some(s => s.nodeName === item.nodeName && s.index === i),
                                                                                    'is-active':currentSelect?.nodeName === item.nodeName && currentSelect?.index === i,
                                                                                    })
                                                                                } 
                                                                            ref={(dom) => push(dom,index === 0 ? '储能001':'储能002',i)} 
                                                                            key={i} 
                                                                            data-info={JSON.stringify({nodeName:index === 0 ? '储能001':'储能002',data:ite,nodeId:item.nodeId})}
                                                                            style={{height:'24px',background:color}}
                                                                            // onClick={() => {
                                                                            //     setCurrentSelect({nodeName:item.nodeName,index:i,data:ite})
                                                                            //     setViewModalVisible(true)
                                                                            // }}
                                                                        >
                                                                    </div>
                                                                    
                                                                )
                                                            })
                                                        }
                                                    </div>
                                                )
                                            })
                                        }
                                    </div>
                                </div>
                            </div>
                        </div> 
                    }           
                </div> 
            }
            
            <>
                {
                    useMemo(() => {
                        return <> {isShow1 ? null : <Dispatch curveDataList={curveDataList}/>}</>
                    },[isShow1,curveDataList])
                }
            </>

           

            <ViewModal 
                currentSelect={currentSelect}
                viewModalVisible={viewModalVisible}
                closeViewModal={() => {
                  setViewModalVisible(false)  
                  setCurrentSelect({})
                }}
            />
            
            {
                editModalVisible ?  
                <EditModal 
                    startDate={predStartDate} 
                    endDate={predEndDate}
                    selected={selected}
                    editModalVisible={editModalVisible} 
                    strategyList={strategyList}
                    setTableData={setTableData}
                    tableData={renderTableData}
                    taskCode={taskCode}
                    setStrategyList={setStrategyList}
                    currentDate={currentDate}
                    closeEditModal={() => {
                        setEditModalVisible(false)
                        setIsShowMove(false)
                        setSelected([])
                        setMovable({
                            dom: null,
                            top: 0,
                            left: 0,
                            width: 0,
                            height: 0
                        })
                    }}
                /> : null
            }
        </>
    )
}