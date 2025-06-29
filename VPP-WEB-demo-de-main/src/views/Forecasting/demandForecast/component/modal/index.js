
import { Button, Modal } from 'antd';
import { useState } from 'react';
import { renderNodeStatus,renderResponseType,renderResponseLevel } from '../../utils'
import classNames from 'classnames'
import './index.scss'
import ReportModal from '../report-modal'
const CommonModal = (props) => {
    const { isModalOpen,setIsModalOpen,taskInfo,nodeList } = props || {}
    const [isTipsModalOpen, setIsTipsModalOpen] = useState(false);

    const handleCancel = () => {
      setIsModalOpen(false);
    };

    return (
        <>
            <Modal className="expand-modal" visible={isModalOpen} footer={null} closable={false}>
                <i className='close-icon' onClick={() => handleCancel()}/>
                <div className='demand-forecast_content_right_content'>
                    <div className='demand-forecast-right-top'>
                        <div className='right-img'></div>
                        <span className='right-text'>需求响应平台</span>
                    </div>
                    <div className='demand-forecast-right-center'>
                        <div className='center-img-top'></div>
                        <div className='model-wrap'>
                            <div className='modal-img'></div>
                            <span className='model-text'>能源大模型</span>
                        </div>
                     
                    </div>
                    <div className='demand-forecast-right-bottom'>
                        <div className='arrow-bottom'></div>
                    </div>
                    <div className='left-text-wrap'>
                        <p className='left-text'>响应时段：{taskInfo?.rsDate}</p>
                        <p className='left-text'>负荷需求(kW)：{taskInfo?.respLoad}kW</p>
                        <p className='left-text'>响应类型：{renderResponseType(taskInfo?.respType)}</p>
                        <p className='left-text'>响应级别：{renderResponseLevel(taskInfo?.respLevel)}</p>
                    </div>
                    <div className='right-text-wrap'>
                        <p className='other-right-text'>需求响应状态：{111}</p>
                        <p className='other-right-text'>申报负荷(kW)：{taskInfo?.declareLoad}kW</p>
                        <p className='other-right-text'>调节负荷(kW)：{taskInfo?.adjustLoad}kW</p>
                        <p className='other-right-text'>预测负荷(kW)：{taskInfo?.forecastLoad}kW</p>
                    </div>
                </div>  
                <div className='dots-wrapper'>
                    {
                        nodeList?.map((item,i) => {
                            return (
                                <div className='dots-1' key={i}>
                                    <div className='dots-1-header'>
                                        <div className={classNames('round',{'online-status':item?.online})}></div>
                                        <span className='dots-1-header-text'>{item?.nodeName}</span>
                                    </div>
                                    <div className='dots1-content'>
                                        <p className='dots1-text'>状态：{renderNodeStatus(item?.drsStatus) || '-'}</p>
                                        <p className='dots1-text'>申报负荷(kW)：{item?.declareLoad}kW</p>
                                        <p className='dots1-text'>调节负荷(kW)：{item?.adjustLoad}kW</p>
                                        <p className='dots1-text'>预测负荷(kW)：{item?.forecastLoad}kW</p>
                                        <p className='dots1-text'>响应负荷(kW)：{item?.deviceRatedLoad}kW</p>
                                        <p className='dots1-text'>响应电量(kW)：{item?.reality}kW</p>
                                        <p className='dots1-text'>预估收益（元）：{item?.income}元</p>
                                    </div>
                                </div>
                            )
                        })
                    }
        
                </div>
            </Modal>
            <ReportModal isTipsModalOpen={isTipsModalOpen} setIsTipsModalOpen={setIsTipsModalOpen}/>
        </>
    );
  };
export default CommonModal;


