import React, {Component,useState } from 'react';
import http from '../../../../server/server.js'
import { ConfigProvider, Input, Space,Form ,Table, Button ,Radio ,
Modal ,Select ,Divider ,message} from 'antd';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';

// import "antd/dist/antd.css";
// import 'antd/dist/antd.min.css';

import {
  FormOutlined
} from '@ant-design/icons';
import './index.css'

// import Airadjust from '../airadjust/index.js'

// 空调设备

const { Search } = Input;
const { Option } = Select;
//表格


class airequipment extends Component {
	constructor(props){
		super(props);
		this.state={
			selectedRowKeys: [],
			filteredInfo: null,
			sortedInfo: null,
			visible:false,
			dataSource:[],
			device:[],
			deviceId:'',
			chose:[],
			columns:[
					{
					    title: '设备名称',
					    dataIndex: 'deviceFullName',
						render:(text, record, index) =>{
							if(record.deviceFullName ==null){
								// alert(record.groupName)
								return record.deviceFullName =''
							}else{
								return record.deviceFullName
							}
						},
					},
					{
						title: '分组',
						dataIndex: 'groupName',
						// filters:this.state.grouping,
						// filteredValue: filteredInfo.value || null,
						// onFilter: (value, record) => {
						// 	console.log(record.name)
						// 	// record.name.includes(value)
						// },
						sortDirections: ['descend'],
					},
					{
						title: '设备全码 ',
						dataIndex: 'deviceFullCode',
					},
					{
						title: '设备型号 ',
						dataIndex: 'deviceModel',
				    
					},
					{
						title: '设备品牌 ',
						dataIndex: 'deviceBrand',
					},
					{
						title: '设备位置 ',
						dataIndex: 'deviceLabel',
					},
				],
			grouping:[],
			deviceGroupType:0,
			queryContent:'',
			setlist:[],
			groupId:'',
			filteredInfo: null,
			sortedInfo: null,
			pageCount:0,
			pageIndex: 1,
			
		}
		
	}
	componentDidMount() {
		
		this.getairlist()
		
		//获取组列表
		http.post('airConditionerStrategy/getAllAirGroupList' ,{
			
		}).then((response) => {
			console.log(response.data.data.list)
			let list = response.data.data.list
			let newlist = [{
				text:'全部',
				value:'全部'
			}]
			let setlist = []
			list.map(data =>{
				newlist.push({
					text:data.groupName,
					value:data.id,
					groupId:data.id,
				})
				setlist.push({
					groupId:data.id,
					groupName:data.groupName
				})
			})
			// let newlist = []
			// list.map(data =>{
			// 	newlist.push({
			// 		groupId:data.id,
			// 		groupName:data.groupName
			// 	})
			// })
			let { sortedInfo, filteredInfo } = this.state;
			    sortedInfo = sortedInfo || {};
			    filteredInfo = filteredInfo || {};
			console.log(newlist)
			this.setState({
				setlist:setlist,
				grouping:newlist
			},() =>{
				console.log(this.state.setlist)
				this.setState({
					columns:[
					{
					    title: '设备名称',
					    dataIndex: 'deviceFullName',
						render:(text, record, index) =>{
							if(record.deviceFullName ==null){
								// alert(record.groupName)
								return record.deviceFullName =''
							}else{
								return record.deviceFullName
							}
						},
					},
					{
						title: '分组',
						dataIndex: 'groupName',
						filters:this.state.grouping,
						filteredValue: filteredInfo.value || null,
						onFilter: (value, record) => {
							console.log(record.name)
							// record.name.includes(value)
						},
						sortDirections: ['descend'],
					},
					{
						title: '设备全码 ',
						dataIndex: 'deviceFullCode',
					},
					{
						title: '设备型号 ',
						dataIndex: 'deviceModel',
				    
					},
					{
						title: '设备品牌 ',
						dataIndex: 'deviceBrand',
					},
					{
						title: '设备位置 ',
						dataIndex: 'deviceLabel',
					},
				]
				})
			})
			console.log(newlist)
		})
	
	}
	//获取数据
	getairlist(){
		http.post('airConditionerStrategy/getAirDeviceList', {
			queryContent: this.state.queryContent,
			deviceGroupType:this.state.deviceGroupType,
			groupId:this.state.groupId,
			pageSize:'10',
			pageIndex:this.state.pageIndex
		}).then((response) => {
			console.log(response)
			console.log(response.data.data.list)
			if(response.data.returnStatus == 'SUCCESS'){
				let list = response.data.data.list
				let groupList = response.data.data.groupList
				if(list ==''&&groupList ==''){
					let newlist = []
					newlist.push({
					
					})
					console.log(newlist)
					this.setState({
						dataSource:[],
						device:[]
					})
				}else{
					let newlist = []
					list.map((data,index) =>{
						// console.log(data)
						newlist.push({
							deviceId:data.deviceId,
							deviceFullCode:data.deviceFullCode,
							deviceFullName:data.deviceFullName,
							groupName:data.groupName,
							deviceModel:data.deviceModel,
							deviceBrand:data.deviceBrand,
							devicePosition:data.devicePosition,
						})
					})
				
					console.log(newlist)
					let newgroupList = []
					groupList.map(data =>{
						newgroupList.push({
							groupId:data.groupId,
							groupName:data.groupName
						})
					})
					console.log(groupList)
					this.setState({
						dataSource:newlist,
						device:groupList,
						pageCount:response.data.data.pageCount
					})
				}
			}
			
			
		})
	}
	
	
	handleChange = value =>{
		console.log(`selected ${value}`);
		this.setState({
			// groupId:value,
			deviceGroupType:value
		},() =>{
			this.getairlist()
		})
	}
	//搜索
	onSearch = value =>{
		console.log(value)
		this.setState({
			queryContent:value
		},() =>{
			this.getairlist()
		})
	}
	onSusInputChange= value =>{
		console.log(value.currentTarget.value)
		this.setState({
			queryContent:''
		},() =>{
			this.getairlist()
		})
	}
	//弹框
	showModal = (arg1) => {
		console.log(arg1)
		if(this.state.chose.length == '0'){
			message.warning("请至少选择一条数据")
		}else{
			this.setState({
				visible: true,
			});
		}
	    
	};
	
	//调整分组
	handleOk = e => {
		// console.log(this.props.form.getFieldsValue().radiogroup)
		http.post('airConditionerStrategy/updateAirDeviceGroup', {
			id: this.state.deviceId,  //设备id
			groupId:this.props.form.getFieldsValue().radiogroup,	//组id
		}).then((response) => {
			if(response.data.returnMsg =='请求成功'){
				message.success("调整分组成功")
				this.setState({
					// groupId:this.props.form.getFieldsValue().radiogroup
				})
				this.getairlist()
			}else{
				// alert(0)
				message.error(response.data.data)
			}
			console.log(response.data.data)
		})
		this.setState({
			visible: false,
		});
	}
		
	    
	
	handleCancel = e => {
	    this.setState({
	    	visible: false,
	    });
	}
	//筛选
	handlefilter = (pagination, filters, sorter) =>{
		console.log('Various parameters', pagination);
		console.log(pagination.current)
		if(filters.groupName){
			
			if(filters.groupName.toString().indexOf('全部') !='-1'){
				this.setState({
					groupId:''
				},() =>{
					this.getairlist()
				})
			}else if(filters.groupName.toString() !=''){
				this.setState({
					groupId:filters.groupName.toString()
				},() =>{
					this.getairlist()
				})
			}else{
				
			}
		}else{
			this.setState({
				pageIndex: pagination.current ,
			},() =>{
				console.log(this.state.pageIndex)
				this.getairlist()
			})
		}
		
		
	}
	// changePage = ({ current }) => {
	//   
	// }
	render(){
		// const { getFieldDecorator } = this.props.form;
		const { loading, selectedRowKeys,setlist } = this.state;
		const rowSelection = {
			onChange: (selectedRowKeys: React.Key[], selectedRows: DataType[]) => {
		    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
			
			
		  },
		  getCheckboxProps: (record: DataType) => ({
		    disabled: record.name === 'Disabled User', // Column configuration not to be checked
		    name: record.name,
		  })
		};


		return(
			<div className="allnews" style={{padding:'12px 24px 24px 24px'}}>
				<div className="btsall">
				<Button type="primary" className="button" onClick={this.showModal}><FormOutlined />调整分组</Button>
					<ConfigProvider locale={locale}>
						<Modal
							title="调整分组"
							visible={this.state.visible}
							onOk={this.handleOk}
							onCancel={this.handleCancel}
							width={687}
						>
							<Form.Item >
									<Radio.Group>
										{
											setlist.map((i,index) =>{
												return   <Radio value={i.groupId} key={index}>{i.groupName}</Radio>
											})
										}
					
									</Radio.Group>,
								
							</Form.Item>
						</Modal>
					</ConfigProvider>
					<Search placeholder="输入关键字搜索" className="searchname" 
					onSearch={this.onSearch} 
					onChange={this.onSusInputChange}
					style={{ width: 200 }} />
					<Select defaultValue="请选择" style={{ width: 120 }} onChange={this.handleChange}>
					    <Option key="1" value="0">全部设备 </Option>
					    <Option key="2" value="1">未分组设备 </Option>
					    <Option key="3" value="2">已分组设备 </Option>
					</Select>
				</div>
				<div>
					<Table rowSelection={rowSelection} 
						rowKey={record =>{
							return record.deviceId
						}}
						columns={this.state.columns} 
						dataSource={this.state.dataSource} 
						pagination={{ total: this.state.pageCount }}
						onChange={this.handlefilter} 
					/>
				>
				</div>
			</div>
		)
	}
}
export default airequipment;






























