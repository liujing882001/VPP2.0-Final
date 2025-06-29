import React,{Component} from 'react'
import { Table ,DatePicker ,ConfigProvider,Modal} from 'antd';
import './index.css'
import http from '../../../server/server.js'

class Monitoring extends Component {
	constructor(props) {
		super(props)
		this.state={
			currentState:2,
			dataSource:[],
			isModalVisible:false,
			lookdataSource:[],
			loading:false,
			page:1
			
		}
	}
	componentDidMount(){
		this.getASMonitorList()
	}
	chosedate(e){
		console.log(e)
		
		this.setState({
			currentState:e,
			// type:e
		},() =>{
			// this.pVList()
			this.getASMonitorList()
		})
	}
	// 查询实时监测列表
	getASMonitorList(){
		this.setState({
			loading:true
		})
		http.post('ancillary_services/ancillary_monitor/getASMonitorList',{
			number:this.state.page,
			pageSize:10,
			status:this.state.currentState
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data.content
				data.sort(function(a, b) {
				    return b.taskCode< a.taskCode? -1 : 1
				})
				this.setState({
					dataSource:data,
					loading:false,
					totaltab:res.data.data.totalElements
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	lookover =(e) =>{
		console.log(e)
		this.setState({
			isModalVisible:true
		},() =>{
			http.get('ancillary_services/ancillary_monitor/getASMonitorListByASId?asId=' +e.asId).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					this.setState({
						lookdataSource:res.data.data
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		})
	}
	render(){
		let {dataSource,isModalVisible,lookdataSource,loading,page} = this.state
		const columns =[
			{
				title: '序号',
				width: '5%',
				render:(value, item, index) => (page - 1) * 10 + index+1,
			},
			{
				title: '任务编码',
				dataIndex: 'taskCode',
				key: 'taskCode',
			},
			{
				title: '辅助服务时段',
				dataIndex: 'name',
				key: 'name',
				render: (s, record, index) =>{
					// console.log(record)
					return record.assDate +'    ' +record.assTime+'~' +record.aseTime
				}
			},
			{
				title: '辅助服务规模（kW）',
				dataIndex: 'asLoad',
				key: 'asLoad',
			},
			{
				title: '辅助服务负荷（kW）',
				dataIndex: 'asLoad',
				key: 'asLoad',
				
			},
			{
				title: '辅助服务类型',
				dataIndex: 'asType',
				key: 'asType',
				render: (text,record,_,action) =>{
					if(record.asType ===1){
						return '调峰'
					}else if(record.asType ===2){
						return '调频'
					}else if(record.asType ===3){
						return '备用'
					}
				}
			},
			{
				title: '状态',
				dataIndex: 'astatus',
				key: 'astatus',
				render: (text,record,_,action) =>{
					if(record.astatus ===1){
						return '未开始'
					}else if(record.astatus ===2){
						return '执行中'
					}else if(record.astatus ===3){
						return '已结束'
					}
				}
			},
			{
				title: '详情',
				key: 'action',
				render: (text,record,_,action) =>{
					return <a onClick={() => this.lookover(record)}>查看</a>
				}
			},
		]
		const handleOk = () => {
		    // setIsModalVisible(false);
			this.setState({
				isModalVisible:false
			})
		};
		
		const handleCancel = () => {
			  this.setState({
			  	isModalVisible:false
			  })
		    // setIsModalVisible(false);
		};
		const lookcolumns =[
			{
				title: '节点',
				dataIndex: 'nodeName',
				key: 'nodeName',
				width:'15%'
			},
			{
				title: '系统',
				dataIndex: 'systemName',
				key: 'systemName',
			},
			
			{
				title: '设备',
				dataIndex: 'deviceName',
				key: 'deviceName',
			},
			
			{
				title: '额定负荷（kW）',
				dataIndex: 'deviceRatedPower',
				key: 'deviceRatedPower',
				width:'15%'
			},
			{
				title: '实时负荷（kW）',
				dataIndex: 'actualLoad',
				key: 'actualLoad',
				width:'15%'
			},
			{
				title: '当前状态',
				dataIndex: 'sstatus',
				key: 'sstatus',
				render:(text,record,index)=>{
					if(record.sstatus==1){
						return '开启'
					}else if(record.sstatus==2){
						return '关闭'
					}
				}
			},
			{
				title: '执行策略',
				dataIndex: 'realLoad',
				key: 'realLoad',
				render:(text,record,index)=>{
					if(record.sstatus==1){
						return '开启'
					}else if(record.sstatus==2){
						return '关闭'
					}
				}
			},
		]
		const onChangetab = page => {
		    console.log(page);
			this.setState({
				page:page.current
			},() =>{
				this.getASMonitorList()
			})
		};
		return (
			<div className="dections">
				<div className="allbtns" style={{width:'60%'}}>
					<span className={2===this.state.currentState?"active":'all'} onClick={()=>this.chosedate(2)}>执行中</span>
					<span className={1===this.state.currentState?"active":'all'} onClick={()=>this.chosedate(1)}>未开始</span>
				</div>
				
				<div className="alltablsefg">
					<Table dataSource={dataSource} columns={columns} 
					loading={loading} 
						onChange={onChangetab}
						pagination={
							{
							  total: this.state.totaltab,//数据的总条数
							  defaultCurrent: 1,//默认当前的页数
							  defaultPageSize: 10,//默认每页的条数
							  showSizeChanger:false,
							
							}
						}
					/>
				</div>
				<Modal title="查看详情" visible={isModalVisible} 
				width={720}
				cancelText='取消'
				okText='确定'
				onOk={handleOk} onCancel={handleCancel}>
				       <Table dataSource={lookdataSource} columns={lookcolumns} />;
				
				</Modal>
			</div>
		)
	}
	
}

export default Monitoring