import { StatusOne } from '../status-one';
import React from './index.scss'
import './index.scss'

export const StatusTwo = (props) => {
    const { data } = props;
    const {profit_total,profit_actual,profit_electricity,profit_operator,energy,pv} = data || {}
    if(pv){
        return (
            <div className="status-two-wrap">
                <i className='status-two-gif'/>
                <div className='real-return-1'>
                    <div>
                        <span style={{marginLeft:"-14px"}}>实际收益</span> <span className='green-text'>{profit_actual}</span>元
                    </div>
                    <div style={{marginTop:'10px'}}>
                        <span>总收益</span> <span className='white-text' style={{marginRight:"10px"}}>{profit_total}</span>元
                    </div>
                </div>
                <div className='revenue-operator-1'>
                    <span>运营方收益</span> <span className='green-text'>{profit_operator}</span>元
                </div>
                <div className='energy-storage-revenue-1'>
                    <span>储能收益</span> <span className='green-text'>{energy}</span>元
                </div> 
                <div className='photovoltaic-revenue-1'>
                    <span>光伏收益</span> <span className='green-text'>{pv}</span>元
                </div>
                <div className='reduce-waive-fees-1'>
                    <span>减免费用</span> <span className='green-text'>{profit_electricity}</span>元
                </div>
            </div>
        )
    }else{
        return <StatusOne data={data}/>
    }
    
}