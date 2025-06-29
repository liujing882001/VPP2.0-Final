import './index.scss'
import { useEffect, useRef, useState } from 'react'
import { DatePicker, TimePicker,Modal,Radio,Input, message } from 'antd';
import dayjs from 'dayjs';
import { daysBetween } from '../../utils'
import http from '../../../../../server/server';

const { RangePicker } = DatePicker;
export const CopyModal = (props) => {
    const { copyModalVisible,closeCopyModal,queryStartDate,queryEndDate,currentNodeId,currentSystem,onQuery } = props || {}
    const days = daysBetween(queryStartDate,queryEndDate)
    const end = dayjs(queryStartDate).subtract(1, 'days').format('YYYY-MM-DD')
    const start = dayjs(queryStartDate).subtract(days+1, 'days').format('YYYY-MM-DD')

    const [startDate,setStartDate] = useState('')
    const [endDate,setEndDate] = useState('')
    const disabledDate = (current) => {
        if (current >= dayjs(queryStartDate) && current <= dayjs(queryEndDate)) {
            return true;
        }
        return false;
    }

    useEffect(() => {
        setStartDate(start)
        setEndDate(end)
    },[])

    return (
        <Modal 
            centered 
            className='copy-modal' 
            title="复制策略" 
            visible={copyModalVisible} 
            onCancel={closeCopyModal} 
            maskClosable={false}
            onOk={() => {
                const betweenDay = daysBetween(startDate,endDate)
                if(betweenDay !== days){
                    message.warning(`查询时间为${days+1}天,复制策略时间也应为${days+1}天,请重新选择！`)
                    return
                }

                http.post('system_management/energy_model/energy_storage_model/copyStorageEnergyStrategy',{
					fromStartDate:startDate,
                    fromEndDate:endDate,
                    toStartDate:queryStartDate,
                    toEndDate:queryEndDate,
                    nodeId:currentNodeId,
                    systemId:currentSystem?.value
				}).then(res =>{
                    if(res.data.code==200){
                        message.success('策略复制成功')
                        closeCopyModal()
                        onQuery()
                    }else{
                        message.error(res.data.msg)
                    }
                   
				}).catch(err =>{
                    message.error('copyStorageEnergyStrategy接口错误')
                })

            }} 
            cancelButtonProps={{className:'cancel-btn'}} 
            okButtonProps={{className:!startDate || !endDate  ? 'disabled-btn confirm-btn':'confirm-btn'}}
            destroyOnClose
        >
            <p className='copy-modal-item'>时间选择：
                {
                    queryStartDate && queryEndDate ?
                    <RangePicker  
                        value={[startDate && dayjs(startDate, 'YYYY-MM-DD'), endDate && dayjs(endDate, 'YYYY-MM-DD')]}
                        onChange={(_date, dateString) => {
                            setStartDate(dateString[0])
                            setEndDate(dateString[1])
                        }}
                        disabledDate={disabledDate} 
                    /> : null
                }
            </p>
       </Modal>
    )
}