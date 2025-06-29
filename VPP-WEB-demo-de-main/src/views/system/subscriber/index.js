import React,{Component} from 'react'
import { Tabs } from 'antd';
import Electric from './electric/index.js'
import Charge from './charge/index.js'
const { TabPane } = Tabs;




class subscriber extends Component {
	constructor(props) {
		super(props)
		this.state={
			
		}
	}
	onChange(){
		
	}
	componentWillUnmount() {
	    this.setState = (state, callback) => {
	      return
	  }
	}
	render(){
		return(
			<div className="allcontent">
				<Tabs defaultActiveKey="1" onChange={this.onChange}>
				    <TabPane tab="电力用户" key="1">
						<Electric />
				    </TabPane>
				    <TabPane tab="虚拟电厂运营商 " key="2">
						<Charge />
				    </TabPane>
				    
				  </Tabs>
			</div>
		)
	}
	
}
export default subscriber



