import React from './index.scss'
import './index.scss'

export const StatusFour = (props) => {
    const { data } = props;
    const {purchase_volume,enterprise_volume,pv_volume,es_charge_volume,es_discharge_volume} = data || {}


    return (
        <div className="status-four-wrap">
            <i className='status-four-gif'/>

            <div className='charging-discharge-capacity-1'>
                <div>
                    <span>充电量</span><span className='green-text'>{es_charge_volume}</span>kWh
                </div>
                <div style={{marginTop:'10px'}}>
                    <span>放电量</span><span className='green-text'>{es_discharge_volume}</span>kWh
                </div>
            </div>

            {/* <div className='electricity-purchased-1'>
                <span >电网购电量</span><span className='green-text'>{purchase_volume}</span>kWh
            </div>
            <div className='enterprise-electricity-1'>
                <span>企业用电量</span><span className='green-text'>{enterprise_volume}</span>kWh
            </div>  */}
        </div>
    )
}