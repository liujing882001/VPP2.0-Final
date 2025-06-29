import React,{Component} from 'react'
import { DatePicker, Space,ConfigProvider ,Select ,Switch,Modal } from 'antd';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import type { DataNode } from 'antd/lib/tree';

import './index.css'
const { RangePicker } = DatePicker;
const { Option } = Select;
const { confirm } = Modal;

class assist extends Component {
	constructor(props) {
		super(props)
		
		this.state={
			lists:[{
				time:'2021-5-30 9:30',
				mold:'调峰',
				price:0.33,
				date:'2022-6-1 11:00～16:00',
				scope:30,
				status:'未执行',
				scale:'100mWh',
				tariff:'3300',
				partake:0,
				ai:0
			},{
				time:'2022-5-30 9:30',
				mold:'调频',
				price:40,
				date:'2022-6-2 15:00～16:00',
				scope:51,
				status:'已完成',
				scale:'-',
				tariff:'-',
				partake:1,
				ai:1
			},
			{
				time:'2022-5-30 9:30',
				mold:'调频',
				price:40,
				date:'2022-6-2 15:00～16:00',
				scope:50,
				status:'执行中',
				scale:'-',
				tariff:'-',
				partake:1,
				ai:0
			}
			],
			partake:0,
			status :1,
			setVal:0,
			bths:['1','2'],
			goodsText:[
				
				    {
				        text: '商品描述',
				        show: false,
				        title: '',
				        msg: '着就是商品的描述啊着就是商品的描述啊着就是商品的描述啊着就是商品的描述啊着就是商品的描述啊着就是商品的描述啊着就是商品的描述啊着就是商品的描述啊着就是商品的描述啊'
				    },
				    {
				        text: '订单与配送',
				        show: true,
				        title: '',
				        msg: '我们的目标是确保您尽快收到订单。成功提交订单后，仓库将立即处理您的订单。然后挑选，包装和发送。一旦快件发出，您将通过微信收到跟踪信息以及预计交货日期的信息。您应该在2-4个工作日之后收到订单。订单不在周末和当地公众假期运输(除中国地区外) 。请注意，预计交货时间(2- 4个工作日)是预计时间，可能因Petit Projects以外的外部因素而有所不同。这些因素可能包括但不限于极端天气条件，技术故障，海关延误等。此外， 请注意，在促销活动期间，交付时间可能比平时更长。运费取决于运费选项，包裹和目的地国家的重量，在您完成订单之前，结账时会清楚地显示实际运费',
				        title: '当日配送服务'
				    },
				    {text: '退货需知', show: true, title: '', msg: '退货需知退货需知退货需知退货需知退货需知退货需知退货需知退货需知退货需知退货需知'}
				
			]
		}
		// this.tab = this.tab.bind(this);

	}
	componentDidMount() {
		
	}
	setVal =() =>{
		
	}
	handleChange =(val) =>{
		console.log(val)
	}
	handlestatus =(val) =>{
		console.log(val)
	}
	// onpartake =(e) =>{
	// 	console.log(e)
	// }
	onpartake=(e) =>{
		// console.log(e)
		// return
		confirm({
		  title: '提示',
		  icon: <ExclamationCircleOutlined />,
		  content: '本次操作将立即生效，是否继续？',
		 cancelText:'取消',
		 okText:'确定',
		  onOk() {
		    console.log('OK');
		  },
		  onCancel() {
		    console.log('Cancel');
		  },
		});
	}

	changeMenu(menuIndex,e) {
		let that = this
		let goodsInfo = that.state.lists // 给对象赋值出来
		console.log(goodsInfo[menuIndex])
		confirm({
			title: '提示',
			icon: <ExclamationCircleOutlined />,
			content: '本次操作将改变策略模式，是否继续？',
			cancelText:'取消',
			okText:'确定',
			onOk() {
				console.log('OK');
				if(goodsInfo[menuIndex].ai ==0){
					goodsInfo[menuIndex].ai =1
				}else{
					goodsInfo[menuIndex].ai =0
				}
			    that.setState({
			        lists: goodsInfo
			    })
		  },
		  onCancel() {
		    console.log('Cancel');
		  },
		});
	}
	changeMenus(menuIndex) {
	        let goodsInfo = this.state.goodsText // 给对象赋值出来
	        goodsInfo[menuIndex].show = !goodsInfo[menuIndex].show // 在新对象里面修改，然后赋值给需要改变的对象
	        this.setState({
	            goodsMsg: goodsInfo
	        })
	    }
	render(){
		let {lists,partake,setVal} = this.state;
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current && current > dayjs().endOf('day');
		};
		return(
			<div className="allcontent">
				<div className="assist">
					<div className="assistheader">
						<ConfigProvider  locale={locale}>
						    <RangePicker disabledDate={disabledDate}  />
							是否参与：
							<Select defaultValue="请选择" style={{ width: 120 }} onChange={this.handleChange}>
							</Select>
							状态：
							<Select defaultValue="请选择" style={{ width: 120 }} onChange={this.handlestatus}>
							</Select>
						</ConfigProvider>
					</div>
					
					<ul className="assistlist">						{							this.state.lists.map((item,i) =>{								return <li key={i}>									<div>										<div className="affairs">											<ol>												<li>													发布时间：{item.time}												</li>												<li>													辅助类型：{item.mold}												</li>												<li>													单价：{item.price} 元/kWh 												</li>												<li>													执行时间：{item.date}												</li>												<li>													规模：{item.scope}mWh												</li>											</ol>										</div>										<div className="partake">											<div>是否参与											<Switch onClick={this.onpartake} 												defaultChecked={item.partake==0?true:false} />											</div>											<div style={{marginTop:'20px'}}>参与策略												<div className={item.ai==0?'active':'ai'} onClick={(e) => { this.changeMenu(i,item)} } >AI智能调度</div>												<div className={item.ai==1?'active':'ai'} onClick={(e) => { this.changeMenu(i,item)} }>运行策略调整</div>											</div>										</div>																			</div>									<div className="condition">										<div><span 										className={item.status=='已完成'?'finish':item.status=='未执行'?'unexecuted':'ongoing'}></span>状态：{item.status}</div>										<div>实际执行规模：{item.scale}</div>										<div>收益（元）：{item.tariff}</div>									</div>								</li>							})						}											</ul>
				</div>
			</div> 
		)
	}
	
}



export default assist