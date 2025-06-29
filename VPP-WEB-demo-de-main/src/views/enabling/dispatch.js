import React,{Component} from 'react'
import './endPV.css'
import './dispatch.css'
import {BrowserRouter as Router, Route, Link,withRouter} from 'react-router-dom'

class dispatch extends Component {
	constructor(props) {
		super(props)
		this.state={
			
		}
	}
	chakan =() =>{
		
		this.props.history.push('/tactics')
	}
	render(){
		return(
		<Router>
			<div>
				<div className="endPV">
					<h4>调度决策算法
						<span onClick={this.chakan}>查看可调负荷运行策略</span>
					</h4>
					<img style={{width:786,height:'358px'}} src={require('../../style/damao/diaodu.png')} />
				</div>
				<div className="pVcapacity" style={{background:'#FFF'}}>
					<h4>调度决策步骤</h4>
					<div className="pvdispacth">
						<div>
							<p>
								<img src={require('../../style/imgs/diao.png')} />
								调度目标
							</p>
							<div className="diaodu">
								<img style={{marginTop:80}} src={require('../../style/imgs/diaodu1.png')} />
								<span>调度下发目标指令</span>
							</div>
						</div>
						<div className="two">
							<p>
								<img src={require('../../style/imgs/ce.png')} />
								调度策略分解
							</p>
							<div className="diaodu">
								<img  src={require('../../style/damao/celue.png')} />
								<ul className="pvload">
									<li><b>源<br /></b>
										<p>光伏系统</p>
									</li>
									<li><b>荷<br /></b>
									<p>可调负荷</p>
									</li>
									<li><b>储<br /></b>
										<p>储能系统</p>
									</li>
								</ul>
							</div>
						</div>
						<div className="two">
							<p>
								<img src={require('../../style/imgs/fen.png')} />
								收益放大
							</p>
							<div className="diaodu">
								<img style={{marginTop:40 }} src={require('../../style/imgs/shouyi.png')} />
								<ul className="pvload pvloada">
									<li style={{paddingLeft:20}}>AI调度决策收益</li>
									<li style={{paddingLeft:20}}>人工调度决策收益</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</Router>
		)
	}
	
}

// export default dispatch
export default withRouter(dispatch);