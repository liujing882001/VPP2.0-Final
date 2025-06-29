import './index.scss'
import { Select,DatePicker,Button,Popover,Cascader,message  } from 'antd'
import { tableTimer,isWindows,getLastMonthDate,getCurrentWeekStartAndEnd } from './utils'
import { useEffect, useRef, useState } from 'react'
import classNames from 'classnames'
import dayjs from 'dayjs';
import http from '../../../server/server'
import { ViewModal } from './components/view-modal'
import { EditModal } from './components/edit-modal'
import { CopyModal } from './components/copy-modal'
import { TipsModal } from './components/tips-modal'
import { ExitModal } from './components/exit-modal'
const { RangePicker } = DatePicker
const { Option } = Select

const Energy = () => {
    const [movable,setMovable] = useState({
        dom: null,
        top: 0,
        left: 0,
        width: 0,
        height: 0
    })
    const [items,setItems] = useState({});
    const moving = useRef(false)
    const start = useRef({  x: 0, y: 0})
    const current = useRef({  x: 0, y: 0})
    const end = useRef({  x: 0, y: 0})
    const angleStart = useRef({  x: 0, y: 0})
    const angleCurrent = useRef({  x: 0, y: 0})
    const angleEnd = useRef({  x: 0, y: 0})
    const [selected,setSelected] = useState([]);
    const [isEdit,setEdit] = useState(false) 
    const [currentSelect,setCurrentSelect] = useState({})
    const [viewModalVisible,setViewModalVisible] = useState(false)
    const [editModalVisible,setEditModalVisible] = useState(false)
    const [copyModalVisible,setCopyModalVisible] = useState(false)
    const [tipsModalVisible,setTipsModalVisible] = useState(false)
    const [exitModalVisible,setExitModalVisible] = useState(false)
    const [startDate,setStartDate] = useState(null)
    const [endDate,setEndDate] = useState(null)
    const [treeOptions,setTreeOptions] = useState([])
    const [nodeSystemList,setNodeSystemList] = useState([])
    const [cascaderValue,setCascaderValue] = useState([])
    const [electricityPrice,setElectricityPrice] = useState([])
    const [currentSystem,setCurrentSystem] = useState({})
    const [currentNodeId,setCurrentNodeId] = useState('')
    const [tableData,setTableData] = useState([])
    const [propertyTotal,setPropertyTotal] = useState({})
    const [strategyList,setStrategyList]  = useState([])
    const [previousY,setPreviousY] = useState(0)    
    const isUp = useRef(true)
    const [isShowMove,setIsShowMove] = useState(false)    
    const timer = new Date().getTime()
    const [scrollTop,setScrollTop] = useState(0)
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

    useEffect(() => {
        http.post('tree/runStorageEnergyNodeTree').then(res =>{
            let data = res.data.data
            const traverseTree = (data) =>  {
                for (let i in data) {
                    data[i].label = data[i].title;
                    data[i].value = data[i].id;
                    
                    if (data[i].children) {
                        traverseTree(data[i].children);
                    }
                }
            }

            const defaultValue = []
            let defalutNodeId = ''
            const setDefaultValue = (data) => {
                data.forEach((item,i) => {
                    if(i > 0){
                        return
                    }

                    if(!item.children || !item.children.length){
                        if(!defalutNodeId){
                            defalutNodeId = item.id
                        }
                    }

                    defaultValue.push(item.key)

                    if(item.children && item.children.length){
                        setDefaultValue(item.children)
                    }else{
                        return
                    }
                })
            }

            setDefaultValue(data)
            traverseTree(data)
            setCascaderValue(defaultValue)

            const { start,end } = getCurrentWeekStartAndEnd()

            setStartDate(start)
            setEndDate(end)
            setTreeOptions(data)
            setCurrentNodeId(defalutNodeId)

            http.post('system_management/system_model/systemStorageEnergyList').then(res =>{
                setNodeSystemList(res.data.data)
                setCurrentSystem({ value: res.data.data[0]?.systemKey, label: res.data.data[0]?.systemName })
                onQuery(defalutNodeId,res.data.data[0]?.systemKey, start, end,true)
            })

        })

        document.getElementById('energy-content-table-bottom').addEventListener('scroll',(e) => {
            const scrollTop = document.getElementById('energy-content-table-bottom').scrollTop;
            setScrollTop(scrollTop)
        })
    },[])


    const onQuery = (nodeId,systemId,startDate,endDate,isFirst) => {
        if((new Date(startDate).getMonth() !== new Date(endDate).getMonth()) && isFirst){
            return
        }
		console.log(new Date(startDate).getMonth())
		console.log(new Date(endDate).getMonth())
        if(new Date(startDate).getMonth() !== new Date(endDate).getMonth()){
            message.warning("日期不允许跨月选择，请重新选择日期！")
            const lastDayOfMonth = dayjs(startDate).endOf('month').format('YYYY-MM-DD');
            setEndDate(lastDayOfMonth)
            return
        }

        http.post('system_management/energy_model/energy_storage_model/findStorageEnergyPowerInfo',{
            nodeId,
            systemId,
            startDate,
            endDate
        }).then(res =>{     
            const { energyStorageSubViews,energyStoragePropertyList,propertyTotal } = res?.data?.data || {}            
            setTableData(energyStorageSubViews)
            setElectricityPrice(energyStoragePropertyList)
            setPropertyTotal(propertyTotal)
        })
    }
          
    const onMouseMove = (e) => {
       moving.current = true
       let parent = document.getElementById("energy-content-table-bottom")
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
        top: Math.min(start.current.y, current.current.y) + scrollTop + "px",
        left: Math.min(start.current.x, current.current.x) + "px",
        width:  Math.abs(current.current.x - start.current.x),
        height:  Math.abs(current.current.y - start.current.y)
       })      
    }

    const onMouseDown = (e) =>{
        setPreviousY(0)
        setIsShowMove(true)
        let parent = document.getElementById("energy-content-table-bottom")
        const clientX = Math.floor(e.clientX - parent.getBoundingClientRect().left)
        const clientY = Math.floor(e.clientY - parent.getBoundingClientRect().top)

        start.current.x = clientX;
        start.current.y = clientY;
        angleStart.current.x = e.clientX
        angleStart.current.y = e.clientY
        setMovable({
            dom: null,
            top: start.current.y + scrollTop + "px",
            left: start.current.x + "px",
            width: 0,
            height: 0
        })  
    }

    const onMouseUp = (e) => {
        setPreviousY(0)
        let parent = document.getElementById("energy-content-table-bottom")
        const clientX = Math.floor(e.clientX - parent.getBoundingClientRect().left)
        const clientY = Math.floor(e.clientY - parent.getBoundingClientRect().top)

        if (moving.current){
            setMovable({
                dom: movable.dom,
                top: Math.min(start.current.y, current.current.y) + scrollTop + "px",
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

    const onCascaderChange = (value, selectedOptions) => {
        const selectOptions = selectedOptions.map((item) => item.label)
        setCascaderValue(value)
        setCurrentNodeId(value[value?.length - 1])
        http.post('system_management/system_model/systemStorageEnergyList').then(res =>{
            setNodeSystemList(res.data.data)
		})
    }
    
    const onSelectChange = (_value, option) => {
        setCurrentSystem(option)
    }

    return (
        <>
            <div className="energy-wrap">
                <div className={classNames('energy-header',{'is-edit':isEdit})} >
                    <div className='option-item'>
                        <span className='energy-header-text'>节点：</span>
                        <Cascader style={{width:300}} placeholder="请选择" options={treeOptions} 
						displayRender={(labels, selectedOptions) => labels[labels.length - 1]}
						onChange={onCascaderChange} value={cascaderValue} clearIcon={null} />
                    </div>
                    <div className='option-item'>
                        <span className='energy-header-text'>系统：</span>
                        <Select
                            style={{width:223}}
                            placeholder="请选择系统"
                            defaultValue={currentSystem.value}
                            value={currentSystem?.value}
                            onChange={onSelectChange}
                        >
                            {
                                nodeSystemList.length &&nodeSystemList.map(res =>{
                                    return <Option value={res.systemKey} key={res.systemKey}>{res.systemName}</Option>
							    })
						    }
                        </Select>
                    </div>
                    <div className='option-item'>
                        <span className='energy-header-text'>日期：</span>
                        <RangePicker 
                            style={{width:240}} 
                            format='YYYY-MM-DD'
                            value={[startDate && dayjs(startDate, 'YYYY-MM-DD'), endDate && dayjs(endDate, 'YYYY-MM-DD')]}
                            onChange={(_date, dateString) => {
                                setStartDate(dateString[0])
                                setEndDate(dateString[1])
                            }}
                            clearIcon={false}
                        />
                    </div>
                    <Button type="Button" className='query-btn' onClick={() => onQuery(currentNodeId,currentSystem?.value,startDate,endDate,false)}>查询</Button>
                </div>
                <div className="energy-content">
                    <div className='energy-content-top'>
                        {
                            tableData && tableData.length && (getLastMonthDate() < new Date(endDate).getTime() && getLastMonthDate() < new Date(startDate).getTime()) ?
                            <> 
                                { 
                                    isEdit ?  
                                    <Button  type="Button" className='exit-edit-btn' onClick={() => {
                                        if(strategyList.length){
                                            setExitModalVisible(true) 
                                        }else{
                                            setEdit(false)
                                            setMovable({
                                                dom: null,
                                                top: 0,
                                                left: 0,
                                                width: 0,
                                                height: 0,
                                            })
                                        }
                                    }}>
                                        退出编辑
                                    </Button> : 
                                    <Button type="Button" className='edit-btn' onClick={() => {
                                        setEdit(true)
                                    }}>编辑</Button>
                                    
                                } 
                            </> : null
                        }

                        {
                            tableData && tableData.length ?
                            <Button  type="Button" className='copy-btn' onClick={()=> {
                                setCopyModalVisible(true)
                            }}>复制策略</Button> : null
                        }

                        {
                            strategyList.length && isEdit ?   
                            <Button type="Button"
                                className='strategy-distribute' 
                                onClick={() => {
                                    setTipsModalVisible(true)
                                }}
                            >保存并下发</Button> : null
                        }
                       
                    </div>

                    <div className='energy-content-center'>
                        <div className='energy-content-center-left'>
                           <span className='unit'>单位：元/kWh</span>
                            <div className='type-wrap'>
                                <span className='type-dot-01'></span>
                                <span className='text-type'>尖：{propertyTotal?.priceHigh}</span>
                            </div>
                            <div className='type-wrap'>
                                <span className='type-dot-02'></span>
                                <span className='text-type'>峰：{propertyTotal?.pricePeak}</span>
                            </div>
                            <div className='type-wrap'>
                                <span className='type-dot-03'></span>
                                <span className='text-type'>平：{propertyTotal?.priceStable}</span>
                            </div>
                            <div className='type-wrap'>
                                <span className='type-dot-04'></span>
                                <span className='text-type'>谷：{propertyTotal?.priceLow}</span>
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
                                electricityPrice && electricityPrice.length ? 
                                <div className='electricity-wrap'>
                                    <span className='electricity-text'>电价</span>
                                    <div className='electricity-price'>
                                        {
                                            electricityPrice.map((item) => {
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
                            
                            <div className='energy-content-table-bottom' id="energy-content-table-bottom" onMouseMove={isEdit ? onMouseMove :undefined} onMouseDown={isEdit ? onMouseDown: undefined} onMouseUp={isEdit ? onMouseUp : undefined}>
                                {isEdit ? <div className={classNames('move')} style={{top:movable.top,left:movable.left,width:movable.width,height:movable.height,display:isShowMove ? 'block' :'none',zIndex:'999  '}}></div> : null} 
                                {
                                    tableData?.map((item,index) => { 
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
                                                                            'is-man':ite?.policyModel === 1
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
                    onQuery={() => onQuery(currentNodeId,currentSystem?.value,startDate,endDate,false)} 
                    disabledStartDate={startDate} 
                    disabledEndDate={endDate}
                    selected={selected}
                    editModalVisible={editModalVisible} 
                    strategyList={strategyList}
                    currentNodeId={currentNodeId}
                    currentSystem={currentSystem}    
                    setTableData={setTableData}
                    tableData={tableData}
                    setStrategyList={setStrategyList}
                    closeEditModal={() => {
                        setEditModalVisible(false)
                        setSelected([])
                        setMovable({
                            dom: null,
                            top: 0,
                            left: 0,
                            width: 0,
                            height: 0
                        })
                        setIsShowMove(false)
                    }}
                    
                /> : null
            }

            {
                copyModalVisible ?  
                <CopyModal  
                    copyModalVisible={copyModalVisible} 
                    closeCopyModal={() => setCopyModalVisible(false)} 
                    onQuery={() => onQuery(currentNodeId,currentSystem?.value,startDate,endDate,false)} 
                    queryStartDate={startDate}
                    queryEndDate={endDate}
                    currentNodeId={currentNodeId}
                    currentSystem={currentSystem}
                /> : null
            }
           

            <TipsModal 
                strategyList={strategyList} 
                tipsModalVisible={tipsModalVisible} 
                closeTipsModal={() => setTipsModalVisible(false)} 
                onQuery={() => onQuery(currentNodeId,currentSystem?.value,startDate,endDate,false)} 
                setStrategyListNull={() => setStrategyList([])}   
                setEditFalse={() => setEdit(false)} 
                setTableData={setTableData}
                tableData={tableData}
            />

            <ExitModal 
                strategyList={strategyList} 
                exitModalVisible={exitModalVisible} 
                onQuery={() => onQuery(currentNodeId,currentSystem?.value,startDate,endDate,false)} 
                closeExitModal={() => {
                    setExitModalVisible(false)
                    setEdit(false)
                }}
                setTableData={setTableData}
                tableData={tableData}
                setStrategyListNull={() => setStrategyList([])}    
            />
        </>
    )
}
export default Energy;