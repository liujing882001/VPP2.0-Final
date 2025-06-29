import React,{Component} from 'react'
import { Table ,DatePicker ,ConfigProvider,Modal } from 'antd';
import './index.css'
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import http from '../../../server/server.js'
const { RangePicker } = DatePicker;



class chronicle extends Component {
	constructor(props) {
		super(props)
		this.state={
			dataSource:[],
			startDate:'',
			endDate:'',
			isModalVisible:false,
			lookdataSource:[],
			page:1,
			totaltab:'',
			loading:false,
			loading1:false
		}
	}
	componentDidMount(){
		// this.getASHistoryList()
	}
	// 查询历史记录列表
	getASHistoryList(){
		this.setState({
			loading:true
		})
		http.post('ancillary_services/ancillary_history/getASHistoryList',{
			number:this.state.page,
			pageSize:10,
			startDate:this.state.startDate,
			endDate:this.state.endDate
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data.content
				data.sort(function(a, b) {
				    return b.taskCode< a.taskCode? -1 : 1
				})
				this.setState({
					dataSource:data,
					totaltab:res.data.data.totalElements,
					loading:false
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	chosedate =(data,datastring) =>{
		this.setState({
			endDate:datastring[1],
			startDate:datastring[0]
		},() =>{
			this.getASHistoryList()
		})
	}
	// 查看
	lookover= (e) =>{
		console.log(e)
		this.setState({
			isModalVisible:true,
			loading1:true
		},() =>{
			http.get('ancillary_services/ancillary_history/getASListByASId?asId='+e.asId).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					this.setState({
						lookdataSource:res.data.data,
						// loading1
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		})
	}
	render(){
		let {dataSource,isModalVisible,lookdataSource,loading,page,loading1} = this.state
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current < dayjs(new Date('2023-01-01')) || current > dayjs().endOf('day')
		};
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
				dataIndex: 'rsDate',
				key: 'rsDate',
				render: (s, record, index) =>{
					// console.log(record)
					return record.assDate +'    '+record.assTime+'~'  +record.aseTime
				}
			},
			{
				title: '辅助服务规模（kW）',
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
				title: '补贴（元/kWh）',
				dataIndex: 'asSubsidy',
				key: 'asSubsidy',
			},
			{
				title: '总调节负荷(kW)',
				dataIndex: 'actualLoad',
				key: 'actualLoad',
			},
			{
				title: '总调节电量(kWh)',
				dataIndex: 'actualPower',
				key: 'actualPower',
			},
			{
				title: '收益预估',
				dataIndex: 'profit',
				key: 'profit',
				render: (text,record,_,action) =>{
					return Number(text).toFixed(2)
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
		const lookcolumns =[
			{
				title: '序号',
				width: '10%',
				render:(text,record,index)=> `${index+1}`
			},
			{
				title: '节点',
				dataIndex: 'nodeName',
				key: 'nodeName',
			},
			{
				title: '实际响应负荷（kW）',
				dataIndex: 'deviceRatedLoad',
				key: 'deviceRatedLoad',
			},
			{
				title: '实际响应电量（kWh）',
				dataIndex: 'regulatePower',
				key: 'regulatePower',
			},
			
			{
				title: '占比（%）',
				dataIndex: 'powerRate',
				key: 'powerRate',
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
		  // 分页
		const onChangetab = page => {
		    console.log(page);
			this.setState({
				page:page.current
			},() =>{
				this.getASHistoryList()
			})
		};
		return(
			<div className="allcontent12">
				<ConfigProvider locale={locale}>
				    <RangePicker disabledDate={disabledDate} onChange={this.chosedate} />
				</ConfigProvider>
				<div style={{margin:'20px 0px'}}>
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
				cancelText='取消'
				okText='确定'
				width={700}
				onOk={handleOk} onCancel={handleCancel}>
				       <Table dataSource={lookdataSource}  columns={lookcolumns} />;

				</Modal>
			</div>
		)
	}
	
}

export default chronicle




