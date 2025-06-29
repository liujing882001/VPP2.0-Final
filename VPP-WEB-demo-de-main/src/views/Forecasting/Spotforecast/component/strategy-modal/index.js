
import { Button, Modal, DatePicker,Input,Radio,message,Tooltip } from 'antd';
import axios from 'axios'
import './index.scss'
import { useEffect, useState } from 'react';
import { formatDate } from  '../../utils'
import http from '../../../../../server/server';
const StrategyModal = (props) => {
    const {setIsStrategyModalOpen,selectedStrategyItem,queryStrategy } = props || {}
    const { id,nodeName,noHouseholds,time,deviceName,content } = selectedStrategyItem || {}
    const [temperature,setTemperature] = useState( content ? content?.substring(4,content?.length-1) : null)
    const handleCancel = () => {
        setTemperature(content ? content?.substring(4,content?.length) :"")
        setIsStrategyModalOpen(false);
    };

    const onOK = () => {
        if(temperature > 20 || temperature < 14){
            message.error('出水温度只能在14到20之前');
            return
        }
        http.post("AIEnergy/strategyUpdate", {
            id,
            strategyContent:temperature
        }, {
            headers: {
              'Content-Type': 'application/json'
            }
          })
        .then(function (response) {
            message.success('修改成功');
            handleCancel()
            queryStrategy()
        })
        .catch(function (error) {
            message.error(`接口错误strategyUpdate`)
        });
    }

    
    return (
        <Modal className="strategy-modal" visible={true} footer={null} title="冷机出水温度调节" onCancel={handleCancel}>
            <div className='strategy-modal-content'>
                <div className='strategy-modal-content-left'>
                    <p className='strategy-modal-content-left-item'>节点</p>
                    <p className='strategy-modal-content-left-item'>户号</p>
                    <p className='strategy-modal-content-left-item'>时间点</p>
                    <p className='strategy-modal-content-left-item'>控制设备</p>
                    <p className='strategy-modal-content-left-item'>出水温度</p>
                </div>
                <div className='strategy-modal-content-right'>
                    <p className='strategy-modal-content-right-item'>{nodeName}</p>
                    <p className='strategy-modal-content-right-item'>{noHouseholds}</p>
                    <p className='strategy-modal-content-right-item'>{formatDate(time)}</p>
                    <Tooltip placement="top" title={deviceName?.join(',')} color='#38373F'>
                        <span className='strategy-modal-content-right-item'>{deviceName?.join(',')}</span>
                    </Tooltip>
                    <p className='strategy-modal-content-right-item'> 
                        <Input
                            value={temperature}
                            className='temperature-input'
                            onChange={(e) => {
                                setTemperature(e.target.value)
                            }}
                            style={{width:'65px',height:'32px'}}
                        />
                        <span className='temperature-icon'>°C</span>
                        <span className='rang-text'>（范围限制：14°C-20°C）</span>
                    </p>
                </div>
            </div>
            <div className='btn-wrapper'>
                <Button className='cancel-btn' onClick={handleCancel}>
                    取消
                </Button>
                <Button className='ok-btn' onClick={onOK}>
                    确定
                </Button>
            </div>
        </Modal>
    );
  };
  export default StrategyModal;