import { StatusFour } from '../status-four';
import React from './index.scss'
import './index.scss'

export const StatusThree = (props) => {
    const { data } = props;
    const {purchase_volume,enterprise_volume,pv_volume,es_charge_volume,es_discharge_volume} = data || {}

    if(pv_volume){
        return (
            <div className="status-three-wrap">
                <i className='status-three-gif'/>
    
                <div className='charging-discharge-capacity'>
                    <div>
                        <span>充电量</span><span className='green-text'>{es_charge_volume}</span>kWh
                    </div>
                    <div style={{marginTop:'10px'}}>
                        <span>放电量</span> <span className='green-text'>{es_discharge_volume}</span>kWh
                    </div>
                </div>
    
                {/* <div className='electricity-purchased-1'>
                    <span >电网购电量</span> <span className='green-text'>{purchase_volume}</span>kWh
                </div> */}
                {/* <div className='enterprise-electricity'>
                    <span>企业用电量</span> <span className='green-text'>{enterprise_volume}</span>kWh
                </div>  */}
                <div className='photovoltaic-power'>
                    <span>光伏发电量</span> <span className='green-text'>{pv_volume}</span>kWh
                </div> 
            </div>
        )
    }else{
        return <StatusFour data={data}/>
    }
    
}