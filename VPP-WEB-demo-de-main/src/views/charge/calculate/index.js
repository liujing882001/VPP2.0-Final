import React,{useEffect,useState,useRef} from 'react'
import { Tabs } from 'antd';
import './index.css'
import './index.scss'
import Adjustable from './adjustable/index.js'
import Photovoltaic from './photovoltaic/index.js'
import Pv_accurate from './pv_accurate/index.js'
import Ad_accurate from './Ad_accurate/index.js'
const { TabPane } = Tabs;


const Calculate =() =>{
	const [activeKey,setActiveKey] = useState('');
	const [content1,setContent1] = useState('父组件给子组件传的参数');
	const [osType,setOsType] = useState(sessionStorage.getItem('osType'));
	const [newkey,setNewkey] = useState('');
	const [width,setWidth] = useState('');
	const onChange =(key) =>{
		console.log(key)
		setNewkey(key)
	}
	const selectRef1 = useRef(null);
	const selectRef = useRef(null);
	useEffect(() =>{
		
		if(selectRef1.current){
			const width1 = selectRef1.current.clientWidth;
			setWidth(width1)
			
		}
		console.log(width)
	},[width])

	return(
		<div className="allcontent12 calculate" ref={selectRef1}>
		{
			osType=='loadType'?(
				<Tabs defaultActiveKey="2" forceRender={true}  onChange={onChange}>
					
				    <TabPane tab="负荷预测"  key="1">
						<div style={{height:'100%'}} ref={selectRef1}>
							<Adjustable pCount={width} newkey={newkey} />
						</div>
				    </TabPane>
				    
					 
				</Tabs>
			):(
				<Tabs defaultActiveKey="2" forceRender={true}  onChange={onChange}>
				
					<TabPane tab="光伏发电预测"  key="2">
						<div style={{height:'100%'}}  ref={selectRef}>
							<Photovoltaic pCount={width}  />
						</div>
						
					</TabPane>
					
				    <TabPane tab="负荷预测"  key="1">
						<div style={{height:'100%'}} >
							<Adjustable pCount={width} newkey={newkey} />
						</div>
				    </TabPane>
				    
					
					
				    
				</Tabs>
			)
		}
			
		</div>
	)
}
	


export default Calculate
// <TabPane tab="负荷预测精度统计"  key="4" disabled>
// 						<div style={{height:'100%'}} ref={this.selectRef1}>
// 							<Ad_accurate pCount={width} newkey={newkey} />
// 						</div>
// 					</TabPane>
// <TabPane tab="光伏发电预测精度统计"  key="3">
// 					    	<div style={{height:'100%'}} ref={this.selectRef1}>
// 					    		<Pv_accurate pCount={width} newkey={newkey} />
// 					    	</div>
// 					    </TabPane>