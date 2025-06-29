import http from '../../../../../server/server';
import './index.scss'
import { Modal,message } from 'antd';

export const ExitModal = (props) => {
    const { exitModalVisible,closeExitModal,strategyList,setStrategyListNull,onQuery } = props || {}
    return (
        <Modal className='exit-modal' 
               title={null} 
               destroyOnClose
               visible={exitModalVisible} 
               centered
               onCancel={() => {
                    setStrategyListNull()
                    closeExitModal()
                    onQuery()
               }} 
               onOk={() => {
                    http.post('system_management/energy_model/energy_storage_model/distributionStorageEnergyStrategy',{
					'storageEnergyStrategyDistributionModels':strategyList,
                              "modify": strategyList?.length ? true :false
				}).then(res =>{
                        if(res.data.code==200){
                              message.success('策略保存并下发成功')
                              setStrategyListNull()
                              closeExitModal()
                        }else{
                              onQuery()
                              setStrategyListNull()
                        }
                         
				}).catch(err =>{
                         message.error('copyStorageEnergyStrategy接口错误')
                    })
               }} 
               cancelButtonProps={{className:'cancel-btn'}} 
               okText="保存并下发策略"
        >
            <div className='exit-modal-title'><i className='warn-icon'/>退出编辑</div>
            <div className='tips-modal-content'>是否保存当前编辑策略，并下发？</div>
       </Modal>
    )
}