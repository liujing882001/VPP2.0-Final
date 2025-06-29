import React,{Component} from 'react'
import { Button,Tabs  } from 'antd';
import http from '../../server/server.js'

import './index.css'
import EndPV from './endPV.js'
import Dispatch from './dispatch.js'
import Charge from './charge.js'
const { TabPane } = Tabs;

class enabling extends Component {
	constructor(props) {
		super(props)
		this.state={
			model:'PvForecasting',
			osType:''
		}
	}
	componentDidMount(){
		console.log(sessionStorage.getItem('osType'))
		this.setState({
			osType:sessionStorage.getItem('osType')
		})
	}
	
	render(){
		const onChange = (key: string) => {
		  console.log(key);
		};

		return(
			<div className="allcontent12 calculate">
			{
				this.state.osType=='loadType'?(
					<Tabs defaultActiveKey="1" onChange={onChange}>
					    <TabPane tab="负荷电量预测" key="2">
							<Charge />
					    </TabPane>
					    <TabPane tab="调度决策算法" key="3">
							<Dispatch />
					    </TabPane>
					</Tabs>
				):(
					<Tabs defaultActiveKey="1" onChange={onChange}>
					    <TabPane tab="光伏发电预测" key="1">
							<EndPV />
					    </TabPane>
					    <TabPane tab="负荷电量预测" key="2">
							<Charge />
					    </TabPane>
					    <TabPane tab="调度决策算法" key="3">
							<Dispatch />
					    </TabPane>
					</Tabs>
				)
			}
				
			</div>
		)
	}
	
}
export default enabling