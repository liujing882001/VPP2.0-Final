import React from './index.scss'
import './index.scss'

export const StatusOne = (props) => {
    const { data } = props;
    const {profit_total,profit_actual,profit_electricity,profit_operator,energy} = data || {}

    return (
        <div className="status-one-wrap">
            <i className='status-one-gif'/>
            <div className='real-return'>
                <div>
                    <span style={{marginLeft:"-14px"}}>实际收益</span> <span className='green-text'>{profit_actual}</span>元
                </div>
                <div style={{marginTop:'10px'}}>
                    <span>总收益</span> <span className='white-text' style={{marginRight:"10px"}}>{profit_total}</span>元
                </div>
            </div>
            <div className='revenue-operator'>
                <span >运营方收益</span> <span className='green-text'>{profit_operator}</span>元
            </div>
            <div className='energy-storage-revenue'>
                <span>储能收益</span> <span className='green-text'>{energy}</span>元
            </div> 
            <div className='reduce-waive-fees'>
                <span>减免费用</span> <span className='green-text'>{profit_electricity}</span>元
            </div>
        </div>
    )
}