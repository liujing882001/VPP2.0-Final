import { tableTimer,isWindows } from '../utils/index.js'
// import { ViewModal } from '../view-modal'
// import { EditModal } from '../edit-modal'
import { DatePicker, Popover,Button } from 'antd'
import { useState,useRef, useEffect } from 'react'
import Typed from 'typed.js';
import http from '../../../../../../../server/server.js'

import classNames from 'classnames'
import dayjs from 'dayjs';
import './index.scss'

export const Energy = (props) => {
	console.log(props)
    let today = new Date();
    today.setDate(today.getDate() + 1);
    const { tableData = [],electricityPrice,propertyTotal,startDate,endDate,nodeId,systemId,strategyList,strategyStartDate } = props;
    const [isEdit,setEdit] = useState(true);
    const [date,setDate] = useState(strategyStartDate)
    const [selected,setSelected] = useState([]);
    const [editModalVisible,setEditModalVisible] = useState(false)
    const [renderElectricityPrice,setRenderElectricityPrice] = useState(props.data)
    const [renderPropertyTotal,setRenderPropertyTotal] = useState(propertyTotal)

    const [isShow,setIsShow] = useState(false)
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

    
    
	useEffect(() =>{
		console.log(renderElectricityPrice)
	},[renderElectricityPrice])
   

   


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
                    
                    <div className='energy-content-wrapper-bottom'>
                        <div className='energy-content-center'>
                            <div className='energy-content-center-left'>
                                <div className='type-wrap'>
                                    <span className='type-dot-01'></span>
                                    <span className='text-type'>尖</span>
                                </div>
                                <div className='type-wrap'>
                                    <span className='type-dot-02'></span>
                                    <span className='text-type'>峰</span>
                                </div>
                                <div className='type-wrap'>
                                    <span className='type-dot-03'></span>
                                    <span className='text-type'>平</span>
                                </div>
                                <div className='type-wrap'>
                                    <span className='type-dot-04'></span>
                                    <span className='text-type'>谷</span>
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
                                    props.data && props.data.length ? 
                                    <div className='electricity-wrap'>
                                        <div className='electricity-price'>
                                            {
                                                props.data.map((item) => {
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
                                                   
                                                
                                                    return <div className='electricity-price-item' style={{background:color}}/>
                                                })  
                                            }
                                        </div>
                                    </div>
                                    :null
                                }
                                
                                
                            </div>
                        </div>
                    </div> 
                </div>
            }
            
            
            
        </>
    )
}