import { tableTimer,isWindows } from '../../../../utils'
import { ViewModal } from './components/view-modal'
import { EditModal } from './components/edit-modal'
import { DatePicker, Button,Popover } from 'antd'
import { useState,useRef, useEffect } from 'react'
import Typed from 'typed.js';
import classNames from 'classnames'
import dayjs from 'dayjs';
import { getBetweenDate } from '../../../../utils'

import './index.scss'

export const WorkingEnergy = (props) => {
    let today = new Date();
    today.setDate(today.getDate() + 1);
    const {tableData,setTableData,nodeNum,workingStrategyList,predStartDate,predEndDate,taskCode,maxPower,minPower,intervalList,setIntervalList,setMaxPower,setMinPower } = props;
    const [isEdit,setEdit] = useState(true);
    const [selected,setSelected] = useState([]);
    const [editModalVisible,setEditModalVisible] = useState(false)
    const [currentSelect,setCurrentSelect] = useState({})
    const [viewModalVisible,setViewModalVisible] = useState(false)
    const [isShow,setIsShow] = useState(true)
    const [currentDate,setCurrentDate] = useState(predStartDate)

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
    const domTimer = new Date().getTime()
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

    let push = (dom, node,indey) => {
        const map = items
        if(!map[node]){
            map[node]= []
        }else{
            map[node][indey] = {selected:false, dom: dom}
        }
        setItems(map)
    }

    const onMouseMove = (e) => {
        moving.current = true
        let parent = document.getElementById(`woking-energy-content-table-bottom-${domTimer}`)
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
         let parent = document.getElementById(`woking-energy-content-table-bottom-${domTimer}`)
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
        let parent = document.getElementById(`woking-energy-content-table-bottom-${domTimer}`)
        
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
                    tempObj.push({nodeName:key, index:i,data:item})
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

    useEffect(() => {
        document.getElementById('woking-energy-scroll-wrap')?.addEventListener('scroll', function() {
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
    
    const disabledDate = (current) => {
        const res =  getBetweenDate(predStartDate,predEndDate)
        return !(current && (current.isSame(res[0]) || current.isSame(res[1]) || current.isSame(res[2])));
    }
    const onQuery = (currentDate) => {
        const tempList = workingStrategyList.current?.filter((item) => item.date === currentDate)
        setTableData(tempList)
    }

    // useEffect(() => {
    //     const scroll = document.getElementById('woking-energy-scroll-wrap');
    //     scroll.scrollTop = scroll.scrollHeight;
    // },[isShow])

    return (
        <>
           <div className="woking-energy-content-wrapper">
                <div className='working-chart-title-wrap'>
                    <span className='working-chart-title'>申报运行曲线</span>
                    <div className='working-chart-query-wrap'>
                        <DatePicker
                            style={{height:'26px',marginRight:'16px'}}
                            defaultValue={dayjs(predStartDate, 'YYYY-MM-DD')}
                            onChange={(dates,dateStrings)=> {
                                setCurrentDate(dateStrings)
                            }}
                            disabledDate={disabledDate}
                            format={'YYYY-MM-DD'}
                        />
                        <Button type="Button" className='working-chart-query-btn' onClick={() => {
                            onQuery(currentDate)
                        }}>查询</Button>
                    </div>
                </div>
                <div className='node-number'>
                    <span>参与节点</span>
                    <span>{nodeNum}</span>
                </div>
                <div className='woking-energy-content-wrapper-bottom'>
                    <div className='woking-energy-content-center'>
                        <div className='woking-energy-content-center-left'>
                        单位：kW
                        </div>
                        <div className='woking-energy-content-center-right'>
                            {
                                intervalList.map((item) => {
                                    let clas = ''
                                    switch (item.name){
                                        case '蓝':
                                            clas = 'blue-dot'
                                            break
                                        case '绿':
                                            clas ='green-dot'
                                            break
                                        case '青':
                                            clas = 'cyan-dot'
                                            break
                                        case '黄':
                                            clas = 'yellow-dot'
                                            break
                                        case '橘':
                                            clas = 'orange-dot'
                                            break
                                        default:
                                            return ''
                                    }
                                    return (
                                        <div className='dots-text-wrap-1'>
                                            <span className={clas}></span>
                                            <span className='dot-text'>{item.value}</span>
                                        </div>
                                    )
                                })
                            }      
                        </div>
                    </div>
                    
                    <div className='woking-energy-content-table'>
                        <div className='table-bg'>
                            <div className='table-bg-timer-1 table-bg-timer-2'>
                                {
                                    tableTimer.map((item,i) => {
                                        return <div className='table-bg-timer-item'>
                                            {item}
                                        </div>
                                    })
                                }
                            </div>
                            <div className='table-bg-top-1 table-bg-top-2'>
                                {
                                    new Array(24).fill(0).map(() => {
                                        return <div className='table-bg-top-item'></div>
                                    })
                                }
                            </div>  
                            <div className='table-bg-bottom-2 table-bg-bottom-3'>
                                {
                                    new Array(24).fill(0).map(() => {
                                        return <div className='table-bg-bottom-item'></div>
                                    })
                                }
                            </div>
                            
                            <div className='woking-energy-content-table-bottom' id={`woking-energy-content-table-bottom-${domTimer}`} onMouseMove={isEdit ? onMouseMove :undefined} onMouseDown={isEdit ? onMouseDown: undefined} onMouseUp={isEdit ? onMouseUp : undefined}>
                                {isEdit ? <div className={classNames('move')} key={new Date().getTime()} style={{top:movable.top,left:movable.left,width:movable.width,height:movable.height,display:isShowMove ? 'block' :'none'}}></div> : null} 
                                {
                                    tableData[0]?.strategy?.map((item,index) => { 
                                        return ( 
                                            <div className='table-content-item-wrap' key={index}>
                                                <span className={classNames('timer','text-overflow-style',{'isWindow':isWindows()})}>{item?.nodeName}</span>    
                                                  {/* <Popover trigger="hover" content={item?.nodeName} overlayClassName='popover-customer-wrapper1'>
                                                    <span className={classNames('timer','text-overflow-style',{'isWindow':isWindows()})}>{item?.nodeName}</span>
                                                  </Popover> */}
                                                {
                                                    item?.list?.map((ite,i) => {
                                                        let color = ''
                                                        switch(ite?.type){
                                                            case "绿":
                                                                color = '#32D64BFF';
                                                                break
                                                            case "蓝":
                                                                color = '#118CFFFF';
                                                                break  
                                                            case "青":
                                                                color = '#09FFE6FF';
                                                                break   
                                                            case "黄":
                                                                color = '#FBFF00';
                                                                break   
                                                            case "橙":
                                                                color = '#FF9500FF';
                                                                break
                                                            default:
                                                                color = '#808080';
                                                        }

                                                        return  (
                                                            <div className={classNames('table-content-item opacity1',{'selected':selected.some(s => s.nodeName === item.nodeName && s.index === i),
                                                                            'is-active':currentSelect?.nodeName === item.nodeName && currentSelect?.index === i,
                                                                            'no-edit-status':!isEdit
                                                                            })
                                                                        } 
                                                                    ref={(dom) => push(dom,item.nodeName,i)} 
                                                                    key={i} 
                                                                    data-info={JSON.stringify({nodeName:item.nodeName,data:ite,index:i,nodeId:item.nodeId})}
                                                                    style={{height:'24px',background:color}}
                                                                    // onClick={isEdit ? undefined : () => {
                                                                    //     setCurrentSelect({node:item.node,index:i,data:ite})
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
            </div>
            
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
                    maxPower={maxPower}
                    minPower={minPower}
                    setIntervalList={setIntervalList}
                    setMaxPower={setMaxPower}
                    setMinPower={setMinPower}
                    startDate={predStartDate} 
                    endDate={predEndDate}
                    selected={selected}
                    editModalVisible={editModalVisible} 
                    workingStrategyList={workingStrategyList}
                    setTableData={setTableData}
                    taskCode={taskCode}
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