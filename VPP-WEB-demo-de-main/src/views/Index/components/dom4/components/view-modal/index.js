import './index.scss'
import { Modal } from 'antd'
export const ViewModal = (props) => {
    const { viewModalVisible,closeViewModal,currentSelect } = props || {}
    const { timer,data } = currentSelect || {}
    const  {power,time,type } = data || {}
    const startTime = time?.split('-')[0]
    const endTime = time?.split('-')[1]
    let className = ''
    switch (type){
        case "充电":
            className = 'charge'
            break
        case "待机":
            className = 'discharge'
            break
        case "放电": 
            className = 'standby'
            break
        default:
            break
    }
    
    return (
        <Modal centered className='view-modal' title="详情" destroyOnClose visible={viewModalVisible} onCancel={closeViewModal} footer={null}>
            <p className='view-modal-item'>日期范围：{timer} <i className='right-arrow'/> {timer}</p>
            <p className='view-modal-item'>时间范围：{startTime} <i className='right-arrow'/> {endTime}</p>
            <p className='view-modal-item'>调度策略：<i className={`dot ${className}`}/><span>{type}</span></p>
            {
                type === '待机' ? null : <p className='view-modal-item'>{ type === '充电' ? '充':null }{ type === '放电' ? '放' : null }电功率：{power}kW</p>
            }
            
       </Modal>
    )
}