import './index.scss'
import { Modal,message } from 'antd';
import http from '../../../../../server/server';

export const TipsModal = (props) => {
    const { tipsModalVisible,closeTipsModal,strategyList,onQuery,setStrategyListNull,setEditFalse,setTableData,tableData } = props || {}
    
    return (
        <Modal className='tips-modal' destroyOnClose centered title={null} visible={tipsModalVisible} onCancel={ () => {
            setEditFalse()
            onQuery()
            closeTipsModal()
        }} onOk={() => {
            try {
                http.post('system_management/energy_model/energy_storage_model/distributionStorageEnergyStrategy',{
                    'storageEnergyStrategyDistributionModels':strategyList,
                    "modify": strategyList?.length ? true :false
                }).then(res =>{
                    if(res.data.code==200){
                        message.success('策略保存并下发成功')
                        setStrategyListNull()
                        onQuery()
                        closeTipsModal()
                        setEditFalse()
                    }else{
                        onQuery()
                        setStrategyListNull()
                    }
                    
                }).catch(err =>{
                    message.error('copyStorageEnergyStrategy接口错误')
                })
            } catch (error) {
                message.error('copyStorageEnergyStrategy接口错误')
            }
           
            
        }} cancelButtonProps={{className:'cancel-btn'}} okButtonProps={{className:'confirm-btn'}}>
            <div className='tips-modal-title'><i className='warn-icon'/>是否下发此策略？</div>
            <div className='tips-modal-content'>系统仅能保存并下发当前时间后的策略。</div>
       </Modal>
    )
}   