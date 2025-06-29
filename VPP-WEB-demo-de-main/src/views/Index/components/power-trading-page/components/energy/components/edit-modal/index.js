import './index.scss'
import { useEffect, useState } from 'react'
import dayjs from 'dayjs'
import { betweenDate,getTimeSlots } from '../../../../utils'
import { DatePicker, TimePicker,Modal,Radio,Input,message } from 'antd';
import http from '../../../../../../../../server/server'
const { RangePicker } = DatePicker;

export const EditModal = (props) => {
    const { editModalVisible,closeEditModal,selected,startDate,endDate,taskCode,setStrategyList,setTableData,currentDate } = props || {}
    const nodeNameArr =[...new Set(selected.map((item) => item?.nodeName))] 
    const [power,setPower] = useState('')
    const [type, setType] = useState("充电");
    const [startTime,setStartTime] = useState(null)
    const [endTime,setEndTime] = useState(null)

    const nodeIdArr = [...new Set(selected.map((item) => JSON.parse(item.data.dom.dataset.info)?.nodeId))]

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
          
        http.post('tradePower/schedulingStrategyEditor',{
            nodeIds:nodeIdArr,
            editDate:currentDate,
            "startTime":startTime,
            "endTime": endTime,
            type:type,
            taskCode,
            power:Number(power)
        }).then(res =>{
            if(res?.data?.code === 501){
                onOk()
                return
            }else{
                setStrategyList(res.data.data)
                const tempList = res.data.data?.filter((item) => item.date === currentDate)
                setTableData(tempList)
                closeEditModal()
            }
        })
    }

    return (
        <Modal className='edit-modal' 
            title="编辑" 
            centered
            destroyOnClose
            open={editModalVisible} 
            onCancel={closeEditModal} 
            onOk={onOk} 
            cancelButtonProps={{className:'cancel-btn'}}
            okButtonProps={{className:(!power && type !== '待机') || !startTime || !endTime ? 'disabled-btn confirm-btn':'confirm-btn'}}
        >
                <h6 className='edit-title'>系统仅会对当前时间后的策略编辑生效</h6>
                <p className='edit-modal-item'>当前节点：{nodeNameArr?.join('、')}</p> 
        
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
    
            <p className='edit-modal-item'>调度策略：
                <Radio.Group onChange={onChange} value={type}>
                    <Radio value="充电" className='radio-item'>充电</Radio>
                    <Radio value="放电" className='radio-item'>放电</Radio>
                    <Radio value="待机" className='radio-item'>待机</Radio>
                </Radio.Group>
            </p>
            {
                type === '待机' ?  null : <p className='edit-modal-item'> {type === '充电' ? '充' : ''}{type === '放电' ? '放': null }电功率：<Input type='tel' style={{width:120,background:'transparent'}} value={power} onChange={(e) => setPower(e.target.value)}/> kW</p>
            }
           
       </Modal>
    )
}