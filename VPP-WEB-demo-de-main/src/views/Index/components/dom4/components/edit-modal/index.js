import './index.scss'
import { useEffect, useState } from 'react'
import dayjs from 'dayjs'
import { betweenDate,getTimeSlots } from '../../../../utils'
import { DatePicker, TimePicker,Modal,Radio,Input,message } from 'antd';
import http from '../../../../../../server/server'
const { RangePicker } = DatePicker;

export const EditModal = (props) => {
    const { editModalVisible,closeEditModal,selected,disabledStartDate,disabledEndDate,strategyList,currentNodeId,currentSystem,setTableData,tableData,onQuery } = props || {}
    const dateArr =[...new Set(selected.map((item) => item?.timer?.split(' ')[0]))] 
    const sortDateArr = dateArr.sort((a,b) => dayjs(a).isBefore(dayjs(b) ? true : false))
    const [startDate,setStartDate] = useState(null)
    const [endDate,setEndDate] = useState(null)
    const [power,setPower] = useState('')
    const [type, setType] = useState("充电");
    const [startTime,setStartTime] = useState(null)
    const [endTime,setEndTime] = useState(null)

    const date = [...new Set(selected.map((item) => item?.timer))][0]

    const timeArr = []

    selected.map((item) => {
        if(item.timer === date){
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

    const disabledDate = current => {
        return (
          current < dayjs(disabledStartDate).startOf('day') || 
          current > dayjs(disabledEndDate).endOf('day')
        );
      };

    useEffect(() => {
        setStartDate(sortDateArr[0])
        setEndDate(sortDateArr[sortDateArr.length - 1])
        if(selected && selected.length === 1){
            const {data } = JSON.parse(selected[0].data.dom.dataset.info) || {}
            const {power,type} = data || {}
            setPower(power)
            setType(type)
        }
    },[])
    
    useEffect(() => {
        setStartTime(JSON.parse(sortTimeArr[0].dom.dataset.info).data.time?.split('-')[0])
        setEndTime(JSON.parse(sortTimeArr[sortTimeArr.length - 1].dom.dataset.info).data.time?.split('-')[1])
    },[])

    const restoreAIMode = () => {
        http.post('system_management/energy_model/energy_storage_model/restoreAIMode',{
            nodeId:currentNodeId,
            systemId:currentSystem,
            startDate:startDate,
            endDate:endDate,
            startTime:startTime,
            endTime:endTime,
            strategy:type,
            power:power
        }).then(res =>{
            onQuery()
            closeEditModal()
        })
    }

    const onOk = () => {
        if(!Number.isFinite(+power)){
            message.warning('请输入正确数字！')
            return
        }
        const dateArr = betweenDate(startDate,endDate);
        const timerArr = getTimeSlots(new Date(`2020-01-01 ${startTime}`),new Date(`2020-01-01 ${endTime}`))
        const tempData = [...tableData]?.map((item) => {
            if(dateArr?.includes(item.date)){
                const nodeChargeDischargeInfos = item.nodeChargeDischargeInfos
                const tempList = nodeChargeDischargeInfos?.map((ite) => {
                    if(timerArr.includes(ite.time)){
                        return {
                            power:power,
                            time: ite.time,
                            type:type,
                            policyModel:1
                        }
                    }else{
                        return ite
                    }
                })
                return {
                    date:item.date,
                    nodeChargeDischargeInfos :tempList
                }
            }else{
                return item
            }
        })
        
        http.post('system_management/energy_model/energy_storage_model/saveStorageEnergyStrategy',{
            nodeId:currentNodeId,
            systemId:currentSystem,
            startDate:startDate,
            endDate:endDate,
            startTime:startTime,
            endTime:endTime,
            strategy:type,
            power:power
        }).then(res =>{
            if(res.data.code !== 200){
                message.error(res.data.msg)
            }else{
                let index = 1
                if(strategyList.current.length){
                    index = strategyList?.current[strategyList?.current.length - 1]?.index + 1
                }
                setTableData(tempData)
                closeEditModal()
                strategyList.current.push({
                    nodeId:currentNodeId,
                    systemId:currentSystem,
                    startDate:startDate,
                    endDate:endDate,
                    startTime: startTime,
                    endTime:endTime,
                    strategy:type,
                    power:power,
                    index
                })
            }
        })
    }

    let isShowBtn = false
    selected.forEach((item) => {
       if(JSON.parse(item.data.dom.dataset.info).data.policyModel === 1){
            isShowBtn = true
            return
       }
    })
    
    return (
        <Modal className='special-edit-modal' 
            title="编辑" 
            centered
            destroyOnClose
            visible={editModalVisible} 
            onCancel={closeEditModal} 
            onOk={onOk} 
            width={420}
            cancelButtonProps={{className:'cancel-btn'}}
            okButtonProps={{className:(!power && type !== '待机') || !startDate || !endDate || !startTime || !endTime ? 'disabled-btn confirm-btn':'confirm-btn'}}
        >
            <h6 className='edit-title'>系统仅会对当前时间后的策略编辑生效</h6>
            <p className='edit-modal-item'>日期范围：
                <DatePicker
                    style={{width:'150px',height:'32px'}}
                    format="YYYY-MM-DD" 
                    defaultValue={[startDate && dayjs(startDate, 'YYYY-MM-DD')]} 
                    value={[startDate && dayjs(startDate, 'YYYY-MM-DD')]}
                    disabledDate={disabledDate}
                    onChange={(_date,formatString) => {
                        setStartDate(formatString[0])
                        setEndDate(formatString[1])
                    }}
                />
            </p> 
    
            <p className='edit-modal-item'>时间范围：
                <TimePicker.RangePicker 
                    style={{width:'150px',height:'32px'}}
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
    
            <p className='edit-modal-item'>调度策略：
                <Radio.Group onChange={onChange} value={type}>
                    <Radio value="充电" className='radio-item'>充电</Radio>
                    <Radio value="放电" className='radio-item'>放电</Radio>
                    <Radio value="待机" className='radio-item'>待机</Radio>
                </Radio.Group>
            </p>
            {
                isShowBtn ?  <div className='ai-tips' onClick={() => {
                    restoreAIMode()
                }}>恢复AI模式</div> : null
            }
            {
                type === '待机' ?  null : <p className='edit-modal-item'> {type === '充电' ? '充' : ''}{type === '放电' ? '放': null }电功率：<Input type='tel'  style={{width:'150px',height:'32px',background:'transparent'}} value={power} onChange={(e) => setPower(e.target.value)} addonAfter={'kW'}/></p>
            } 
           
       </Modal>
    )
}