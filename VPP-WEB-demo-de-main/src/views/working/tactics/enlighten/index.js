import React, {Component } from 'react';
import http from '../../../../server/server.js'
import { ConfigProvider, Input ,Form,Table, Button ,Radio ,Modal ,Select  ,message} from 'antd';
// import { AudioOutlined } from '@ant-design/icons';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import {
  FormOutlined
} from '@ant-design/icons';
// import "antd/dist/antd.css";
// import 'antd/dist/antd.min.css';

// <FormOutlined />

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
			groupId:'',
			grouping:[],
			columns:[
					{
					    title: '设备名称',
					    dataIndex: 'deviceFullName'
					},
					{
						title: '分组',
						dataIndex: 'groupName',
						render:(text, record, index) =>{
							if(record.groupName ==null){
								// alert(0)
								return record.groupName =''
							}else{
								return record.groupName
							}
						},
						// filters:this.state.grouping,
						// filteredValue: filteredInfo.value || null,
						// onFilter: (value, record) => {
						// 	console.log(record.name)
						// 	// record.name.includes(value)
						// },
						// sortDirections: ['descend'],
						// sortDirections: ['descend'],
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
			deviceGroupType:0,
			queryContent:'',
			filteredInfo: null,
			sortedInfo: null,
			setlist:[],
			pageIndex:1
			
		}
		
	}
	componentDidMount() {
		
		this.getdevicrlist()
	
		//获取组列表
		http.post('lightingStrategy/getAllGroupList' ,{
			
		}).then((response) => {
			console.log(response.data.data.list)
			let list = response.data.data.list
			let newvalue = ''
			let newlist = [{
				text:'全部',
				value:'全部'
			}]
			let setlist = []
			list.map(data =>{
				newlist.push({
					text:data.groupName,
					value:data.id
				})
				setlist.push({
					groupId:data.id,
					groupName:data.groupName
				})
			})
			let { sortedInfo, filteredInfo } = this.state;
			    sortedInfo = sortedInfo || {};
			    filteredInfo = filteredInfo || {};
			console.log(newlist)
			this.setState({
				grouping:newlist,
				setlist:setlist
			},() =>{
				this.setState({
					columns:[
					{
					    title: '设备名称',
					    dataIndex: 'deviceFullName'
					},
					{
						title: '分组',
						dataIndex: 'groupName',
						render:(text, record, index) =>{
							if(record.groupName ==null){
								// alert(0)
								return record.groupName =''
							}else{
								return record.groupName
							}
						},
						filters:this.state.grouping,
						filteredValue: filteredInfo.value || null,
						onFilter: (value, record) => {
							console.log(record.name)
							// record.name.includes(value)
						},
						sortDirections: ['descend'],
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
	getdevicrlist(){
		http.post('lightingStrategy/getlightingDeviceList', {
			queryContent: this.state.queryContent,
			deviceGroupType:this.state.deviceGroupType,
			groupId:this.state.groupId,
			pageSize:'10',
			pageIndex:this.state.pageIndex
		}).then((response) => {
			console.log(response.data.data.list)
			let list = response.data.data.list
			let groupList = response.data.data.groupList
			let newlist = []
			list.map((data,index) =>{
				console.log(data)
				newlist.push({
					deviceId:data.deviceId,
					deviceFullCode:data.deviceFullCode,
					deviceFullName:data.deviceFullName,
					groupName:data.groupName,
					deviceModel:data.deviceModel,
					deviceBrand:data.deviceBrand,
					devicePosition:data.devicePosition,
					key:index
					// deviceId:1,
					// deviceFullCode:1,
					// deviceFullName:2,
					// groupName:4,
					// deviceModel:5,
					// deviceBrand:6,
					// devicePosition:7,
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
				device:groupList
			})
			
			
		})
	}
	onSearch = value =>{
		console.log(value)
		this.setState({
			queryContent:value
		},() =>{
			this.getdevicrlist()
		})
	}
	onSusInputChange= value =>{
		console.log(value.currentTarget.value)
		this.setState({
			queryContent:''
		},() =>{
			this.getdevicrlist()
		})
	}
	handleChange = value =>{
		console.log(`selected ${value}`);
		this.setState({
			deviceGroupType:value
		},() =>{
			this.getdevicrlist()
			// console.log(this.state.groupId)
		})
	}
	//弹框分组设备
	showModal = (arg1) => {
		console.log(arg1)
		if(this.state.chose.length ==0){
			message.warning("请选择一条数据")
		}else{
			this.setState({
				visible: true,
			});
		}
	    
	};
	//调整分组
	handleOk = e => {
		console.log(this.props.form.getFieldsValue().radiogroup)
		console.log(this.state.deviceId)
		http.post('lightingStrategy/updateDeviceGroup', {
			id: this.state.deviceId,  //设备id
			groupId:this.props.form.getFieldsValue().radiogroup,	//组id
		}).then((response) => {
			console.log(response)
			if(response.data.returnMsg =='请求成功'){
				this.getdevicrlist()
				message.success('调整分组成功')
			}else{
				message.error(response.data.data)
			}
		})
	   this.setState({
			visible: false,
	   });
	    
	};
	handleCancel = e => {
	    this.setState({
	    	visible: false,
	    });
	};
	//筛选
	handlefilter = (pagination, filters, sorter) =>{
		console.log('Various parameters', pagination, filters, sorter);
		// console.log(filters.groupName.toString())
		// alert(0)
		if(filters.groupName){
			if(filters.groupName.toString().indexOf('全部') != '-1'){
				// alert(1)
				this.setState({
					groupId:''
				},() =>{
					this.getdevicrlist()
				})
			}else if(filters.groupName.toString() !=''){
				// alert(0)
				this.setState({
					groupId:filters.groupName.toString()
				},() =>{
					console.log(this.state.groupId,'00000')
					this.getdevicrlist()
				})
			}else{
				// alert(2)
				this.setState({
					groupId:filters.groupName.toString()
				},() =>{
					console.log(this.state.groupId)
					this.getdevicrlist()
				})
			}
		}else{
			this.setState({
				pageIndex: pagination.current ,
			},() =>{
				console.log(this.state.pageIndex)
				this.getdevicrlist()
			})
		}
		
		
	}
	render(){
		// console.log(this.state.columns)
		const { loading, selectedRowKeys,setlist } = this.state;
		const rowSelection = {
		  onChange: (selectedRowKeys: React.Key[], selectedRows: DataType[]) => {
		    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
			// this.setState({
			// 	chose:selectedRows
			// }, () =>{
			// 	console.log(this.state.chose)
			// 	let id = ''
			// 	let a = ''
			// 	let name=''
			// 	if(this.state.chose.length>1){
			// 		this.state.chose.map((item,index) =>{
			// 			console.log(item,'-----')
			// 			id+=item.deviceFullCode+','
			// 		})
			// 		console.log(id)
			// 		a = id.substring(0, id.lastIndexOf(','))
			// 		console.log(a)
					
			// 	}else if(this.state.chose.length ==1){
			// 		console.log(this.state.chose[0].groupId)
			// 		let chose = this.state.chose[0]
			// 		console.log(chose.groupName)
			// 		let groupName = ''
			// 		for(var i=0;i<setlist.length;i++){
			// 			if(setlist[i].groupName == chose.groupName){
			// 				groupName = setlist[i].groupId
			// 			}
			// 		}
			// 		this.props.form.setFieldsValue({
			// 			'radiogroup':groupName
			// 		})
			// 		this.state.chose.map((item,index) =>{
			// 			console.log(item)
			// 			id=item.deviceFullCode
			// 			name = item.groupName
						
			// 		})
			// 		a = id
					
			// 	}
			// 	console.log(id)
			// 	this.setState({
			// 		deviceId:a
					
			// 	})
			// })
			
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
					<Select style={{ width: 120 }} 
						defaultValue="全部设备"
						onChange={this.handleChange}>
					    <Option key="1" value="0">全部设备 </Option>
						<Option key="2" value="1">未分组设备 </Option>
						<Option key="3" value="2">已分组设备 </Option>
					</Select>
				</div>
				<div>
					<Table rowSelection={rowSelection} 
					columns={this.state.columns} 
					dataSource={this.state.dataSource} 
					onChange={this.handlefilter} />
				</div>
			</div>
			
		)
		
	}
}
export default airequipment;






























