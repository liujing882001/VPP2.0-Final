import { tableTimer,isWindows } from '../../../../utils'
import { ViewModal } from '../view-modal'
import { EditModal } from '../edit-modal'
import { DatePicker, Popover,Button } from 'antd'
import { useState,useRef, useEffect } from 'react'
import Typed from 'typed.js';
import http from '../../../../../../server/server.js'
import classNames from 'classnames'
import dayjs from 'dayjs';
import './index.scss'

export const Energy = (props) => {
    let today = new Date();
    today.setDate(today.getDate() + 1);
    const { tableData = [],electricityPrice,propertyTotal,startDate,endDate,nodeId,systemId,strategyList,strategyStartDate } = props;
    const [isEdit,setEdit] = useState(true);
    const [date,setDate] = useState(strategyStartDate)
    const [selected,setSelected] = useState([]);
    const [editModalVisible,setEditModalVisible] = useState(false)
    const [currentSelect,setCurrentSelect] = useState({})
    const [viewModalVisible,setViewModalVisible] = useState(false)
    const [renderElectricityPrice,setRenderElectricityPrice] = useState(electricityPrice)
    const [renderTableData,setRenderTableData] = useState([...tableData])
    const [renderPropertyTotal,setRenderPropertyTotal] = useState(propertyTotal)

    const [isShow,setIsShow] = useState(true)
    const [movable,setMovable] = useState({
        dom: null,
        top: 0,
        left: 0,
        width: 0,
        height: 0
    })
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

    let push = (dom, timer,indey) => {
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
                    tempObj.push({timer:key, index:i,data:item})
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

    
    const onRefresh = async (nodeId,systemId,startDate,endDate) => {
        const energyPowerRes = await http.post('system_management/energy_model/energy_storage_model/findAIStorageEnergystrategy',{
            nodeId,
            systemId,   
            startDate,
            endDate
        })
        const { energyStorageSubViews,energyStoragePropertyList,propertyTotal } = energyPowerRes?.data?.data || {}            
        setRenderTableData(energyStorageSubViews)
        setRenderElectricityPrice(energyStoragePropertyList)
        setRenderPropertyTotal(propertyTotal)
    }


    useEffect(() => {
        document.getElementById('energy-scroll-wrap').addEventListener('scroll', function() {
            setMovable({
                dom: null,
                top: 0,
                left: 0,
                width: 0,
                height: 0
            })
        });   
       

        if(message1?.current){
            // eslint-disable-next-line react-hooks/exhaustive-deps
            typed1 = new Typed(message1?.current, {
                strings: ['调度策略内容'],
                typeSpeed: 30,
                backSpeed: 100,
                showCursor: true,
                cursorChar: '',
                onComplete(self) {
                    setTimeout(() => {
                        setIsShow(false)
                    },2000)
                },
            });
        }
    },[])

    useEffect(() => {
        const scroll = document.getElementById('energy-scroll-wrap');
        scroll.scrollTop = scroll.scrollHeight;
    },[isShow])


    return (
        <>
            {
                isShow ? 
                <div className="energy-content-wrapper">
                    <div className='energy-content-wrapper-top'>
                        <span ref={message1}></span>
                    </div>
                    <div className="loading-wrapper">
                        <i className='loading-icon'/>
                    </div>
                </div> : 
                <div className="energy-content-wrapper">
                    <div className='energy-content-wrapper-top'>
                        <span ref={message1}></span>
                        <div style={{display:'flex',alignItems:'center'}}>
                            <DatePicker style={{width:'134px',height:'26px',marginRight:'16px'}} onChange={(_date,dateString) => {
                                setDate(dateString)
                            }} defaultValue={dayjs(strategyStartDate, 'YYYY-MM-DD')} />
                            <Button className='energy-content-wrapper-query-btn' onClick={() => onRefresh(nodeId,systemId,date,date)}>查询</Button>
                        </div>
                    </div>
                    <div className='energy-content-wrapper-bottom'>
                        <div className='energy-content-center'>
                            <div className='energy-content-center-left'>
                            <span className='unit'>单位：元/kWh</span>
                                <div className='type-wrap'>
                                    <span className='type-dot-01'></span>
                                    <span className='text-type'>尖：{renderPropertyTotal?.priceHigh}</span>
                                </div>
                                <div className='type-wrap'>
                                    <span className='type-dot-02'></span>
                                    <span className='text-type'>峰：{renderPropertyTotal?.pricePeak}</span>
                                </div>
                                <div className='type-wrap'>
                                    <span className='type-dot-03'></span>
                                    <span className='text-type'>平：{renderPropertyTotal?.priceStable}</span>
                                </div>
                                <div className='type-wrap'>
                                    <span className='type-dot-04'></span>
                                    <span className='text-type'>谷：{renderPropertyTotal?.priceLow}</span>
                                </div>

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
                                <div className='dots-text-wrap'>
                                        <span className='man-small'></span>
                                        <span className='dot-text'>人工</span>
                                </div>
                            </div>
                        </div>
                        
                        <div className='energy-content-table'>
                            <div className='table-bg'>
                                <div className='table-bg-timer'>
                                    {
                                        tableTimer.map((item,i) => {
                                            return <div className='table-bg-timer-item'>
                                                {item}
                                            </div>
                                        })
                                    }
                                </div>
                                <div className='table-bg-top'>
                                    {
                                        new Array(24).fill(0).map(() => {
                                            return <div className='table-bg-top-item'></div>
                                        })
                                    }
                                </div>  
                                <div className='table-bg-bottom'>
                                    {
                                        new Array(24).fill(0).map(() => {
                                            return <div className='table-bg-bottom-item'></div>
                                        })
                                    }
                                </div>
                                {
                                    renderElectricityPrice && renderElectricityPrice.length ? 
                                    <div className='electricity-wrap'>
                                        <span className='electricity-text'>电价</span>
                                        <div className='electricity-price'>
                                            {
                                                renderElectricityPrice.map((item) => {
                                                    let color = ''
                                                    switch(item.property){
                                                        case "谷":
                                                            color = '#EEF0FD';
                                                            break
                                                        case "平":
                                                            color = '#86BAF6';
                                                            break
                                                        case "峰":
                                                            color = '#2C98FB';
                                                            break
                                                        case "尖":
                                                            color = '#186AC3';
                                                            break
                                                        default:
                                                            break
                                                    }

                                                    const content = <div className='popover-item-wrap'>
                                                                        <p className='popover-item'><i className='dots'/>{item.property}</p>
                                                                        <p className='popover-item'>{item?.timeFrame?.split('-')[0]} <i className='arrow-icon'/> {item?.timeFrame?.split('-')[1]}</p>
                                                                        <p className='popover-item'>{item?.priceHour?.toFixed(4)}元/kWh</p>
                                                                    </div>
                                                
                                                    return <Popover content={content} placement='right' overlayClassName='popover-customer-wrapper'>
                                                                <div className='electricity-price-item' style={{background:color}}/>
                                                           </Popover> 
                                                })  
                                            }
                                        </div>
                                    </div>
                                    :null
                                }
                                
                                <div className='energy-content-table-bottom' id={`energy-content-table-bottom-${timer}`} onMouseMove={isEdit ? onMouseMove :undefined} onMouseDown={isEdit ? onMouseDown: undefined} onMouseUp={isEdit ? onMouseUp : undefined}>
                                    {isEdit ? <div className={classNames('move')} key={new Date().getTime()} style={{top:movable.top,left:movable.left,width:movable.width,height:movable.height,display:isShowMove ? 'block' :'none',zIndex:'999'}}></div> : null} 
                                    {
                                        [...renderTableData]?.map((item,index) => { 
                                            let week = new Date(item.date).getDay()
                                            let weekStr = ''
                                        
                                            switch (week) {
                                                case 0 :
                                                    weekStr += "周日";
                                                    break;
                                                case 1 :
                                                    weekStr += "周一";
                                                    break;
                                                case 2 :
                                                    weekStr += "周二";
                                                    break;
                                                case 3 :
                                                    weekStr += "周三";
                                                    break;
                                                case 4 :
                                                    weekStr += "周四";
                                                    break;
                                                case 5 :
                                                    weekStr += "周五";
                                                    break;
                                                case 6 :
                                                    weekStr += "周六";
                                                    break;
                                                default:
                                                    weekStr += "";
                                                    break;
                                            }

                                            return (
                                                
                                                <div className='table-content-item-wrap' key={index}>
                                                    <span className={classNames('timer',{'isWindow':isWindows()})}>{item.date} {weekStr}</span>
                                                    {
                                                        item?.nodeChargeDischargeInfos.map((ite,i) => {
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
                                                                <div className={classNames('table-content-item',{'selected':selected.some(s => s.timer === item.date && s.index === i),
                                                                                'is-active':currentSelect?.timer === item.date && currentSelect?.index === i,
                                                                                'no-edit-status':!isEdit,
                                                                                'is-man': ite?.policyModel === 1
                                                                                })
                                                                            } 
                                                                        ref={(dom) => push(dom,item.date,i)} 
                                                                        key={i} 
                                                                        data-info={JSON.stringify({date:item.date,data:ite})}
                                                                        style={{height:'24px',background:color}}
                                                                        onClick={isEdit ? undefined : () => {
                                                                            setCurrentSelect({timer:item.date,index:i,data:ite})
                                                                            setViewModalVisible(true)
                                                                        }}
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
                </div>
            }
            
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
                    onQuery={() => onRefresh(nodeId,systemId,date,date)}
                    disabledStartDate={startDate} 
                    disabledEndDate={endDate}
                    selected={selected}
                    editModalVisible={editModalVisible} 
                    strategyList={strategyList}
                    currentNodeId={nodeId}
                    currentSystem={systemId}    
                    setTableData={setRenderTableData}
                    tableData={renderTableData}
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