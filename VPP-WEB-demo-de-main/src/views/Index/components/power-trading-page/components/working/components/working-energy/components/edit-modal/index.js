import './index.scss'
import { useEffect, useState } from 'react'
import dayjs from 'dayjs'
import { betweenDate,getTimeSlots } from '../../utils'
import { DatePicker, TimePicker,Modal,Radio,Input,message } from 'antd';
import http from '../../../../../../../../../../server/server'
const { RangePicker } = DatePicker;

export const EditModal = (props) => {
    const { editModalVisible,closeEditModal,selected,startDate,endDate,setTableData,taskCode,currentDate,workingStrategyList,minPower,maxPower,setIntervalList,setMaxPower,setMinPower } = props || {}
    const nodeNameArr =[...new Set(selected.map((item) => item?.nodeName))] 

    const dateArr =[...new Set(selected.map((item) => item?.node?.split(' ')[0]))] 
    const [power,setPower] = useState('')
    const [type, setType] = useState("充电");
    const [startTime,setStartTime] = useState(null)
    const [endTime,setEndTime] = useState(null)
    const nodeIdArr = [...new Set(selected.map((item) => JSON.parse(item.data.dom.dataset.info)?.nodeId))]

    const date = [...new Set(selected.map((item) => item?.node))][0]

    const timeArr = []

    selected.map((item) => {
        if(item.node === date){
            timeArr.push(item.data)
        }
    })

    const sortTimeArr = timeArr.sort((a,b) => JSON.parse(a.dom.dataset.info)?.index - JSON.parse(b.dom.dataset.info)?.index ? true : false)
    
    const onChange = (e) => {
        setType(e.target.value);
        if(e.target.value === '待机'){
            setPower(0)
        }
    };
    useEffect(() => {
        if(selected && selected.length === 1){
            const {data } = JSON.parse(selected[0].data.dom.dataset.info) || {}
            const {power,type} = data || {}
            setPower(power)
            setType(type)
        }
    },[])
    
    
    useEffect(() => {
        setStartTime(JSON.parse(selected[0].data?.dom?.dataset?.info)?.data?.stime)
        setEndTime(JSON.parse(selected[selected?.length - 1]?.data?.dom?.dataset?.info)?.data?.etime)
    },[])


    const onOk = () => {
        if(!Number.isFinite(+power)){
            message.warning('请输入正确数字！')
            return
        }
          
        http.post('tradePower/editDeclareForOperation',{
            nodeIds:nodeIdArr,
            editDate:currentDate,
            "startTime":startTime,
            "endTime": endTime,
            taskCode,
            power:Number(power),
            minPower,
            maxPower
        }).then(res =>{
            if(res?.data?.code === 501){
                onOk()
                return
            }else{
                workingStrategyList.current = res.data.data?.list
                const tempList = res.data.data?.list?.filter((item) => item.date === currentDate)
                const intervalList = res?.data?.data?.intervalInfo?.interval
                const maxPower = res?.data?.data?.intervalInfo?.maxPower
                const minPower = res?.data?.data?.intervalInfo?.minPower
                setMinPower(minPower)
                setMaxPower(maxPower)
                setIntervalList(intervalList)
                setTableData(tempList)
                closeEditModal()
            }
        })
    }
    return (
        <Modal className='edit-modal-1' 
            title="编辑" 
            centered
            destroyOnClose
            open={editModalVisible} 
            onCancel={closeEditModal} 
            onOk={onOk} 
            cancelButtonProps={{className:'cancel-btn'}}
            okButtonProps={{className:(!power && type !== '待机') || !startTime || !endTime ? 'disabled-btn confirm-btn':'confirm-btn'}}
        >
            <p className='edit-modal-item'>节点：{nodeNameArr?.join('、')}
            </p> 
            <p className='edit-modal-item'>日期：{currentDate}
            </p> 
            <p className='edit-modal-item'>时间范围：
                <TimePicker.RangePicker 
                    format="HH:mm"   
                    defaultValue={[startTime && dayjs(startTime, 'HH:mm'),endTime && dayjs(endTime, 'HH:mm')]} 
                    value={[startTime && dayjs(startTime, 'HH:mm'),endTime && dayjs(endTime, 'HH:mm')]} 
                    minuteStep={15}
                    onChange={(_date,formatString) => {
                        setStartTime(formatString[0])
                        setEndTime(formatString[1])   
                    }}
                />
            </p>
            <p className='edit-modal-item'>申报负荷：<Input type='tel' style={{width:120,background:'transparent'}} value={power} onChange={(e) => setPower(e.target.value)}/> kW</p>
       </Modal>
    )
}