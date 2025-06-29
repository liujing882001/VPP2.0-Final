import React,{Component} from 'react'
import { Button ,Input,Table,Space ,Form,Modal,Typography ,Select,message} from 'antd';
import {
  PlusOutlined,
  AudioOutlined ,
  FormOutlined,
  DeleteOutlined,
  FileSearchOutlined,
  ExclamationCircleOutlined,
  MinusCircleOutlined, 
  PlusCircleOutlined
} from '@ant-design/icons';
import BaiduMaps from './BaiduMaps.js';
import http from '../../../../server/server.js'
import axios from 'axios'
import './index.css'
import qs from 'qs'
const { Option } = Select;

const { Search } = Input;
const areas = [
  { label: 'Beijing', value: 'Beijing' },
  { label: 'Shanghai', value: 'Shanghai' },
];

const sights = {
  Beijing: ['Tiananmen', 'Great Wall'],
  Shanghai: ['Oriental Pearl', 'The Bund'],
};
const dataSource = [
  {
    key: '1',
    name: 'XXXXXXXX',
    age: 32,
    address: '西湖区湖底公园1号',
  },
  {
    key: '2',
    name: 'XXXXXXXX',
    age: 42,
    address: '西湖区湖底公园1号',
  },
];


const formItemLayout = {
  labelCol: {
    xs: { span: 28 },
    sm: { span: 4 },
  },
  wrapperCol: {
    xs: { span: 28, offset: 10 },
    sm: { span: 24, offset: 4 },
  },
};
const formItemLayoutWithOutLabel = {
  wrapperCol: {
    xs: { span: 28, offset: 0 },
    sm: { span: 26, offset: 0 },
  },
};
const formItemLayout1 = {
  labelCol: {
    xs: { span: 28 },
    sm: { span: 4 },
  },
  wrapperCol: {
    xs: { span: 16, offset: 4 },
    sm: { span: 26, offset: 4 },
  },
};
const formItemLayoutWithOutLabel1 = {
  wrapperCol: {
    xs: { span: 16, offset: 0 },
    sm: { span: 26, offset: 0 },
  },
};
const { confirm } = Modal;

class charge extends Component {
	constructor(props) {
		super(props)
		this.state={
			isModalVisible:false,
			address1: '',
			dataSource:[],
			mapData:[],
			areas:[],
			contactpenlist:[{
				text:'124',
				powerUserNumber:[]
			}],
			types:0,
			edit:0 ,//0修改1新增
			look:false,
			nodePowerUserDataList:[],
			ebussindess:'',
			person:'',
			caddress:'',
			tel:'',
			buss:'',
			loading:false,
			nodeIds:'',
			istrue:false
		}
	}
	componentDidMount(){
		this.customer()
		this.nodeNameList()
	}
	ChildrenChange=()=>{
		this.setState({
			istrue:false
		})
	}
	nodeNameList(){
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				// res.data.data
				this.setState({
					areas:res.data.data
				})
			}
		})
	}
	customer(){
		this.setState({
			loading:true
		})
		http.post('system_management/market_subject/customerList?customerType=loadAggregator',{
			
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				this.setState({
					dataSource:res.data.data,
					loading:false
				})
			}
		})
	}
	onSearch =(e) =>{
		console.log(e)
		if(e){
			http.post('system_management/market_subject/customerListByCName?customerType=loadAggregator'+ "&tenantName="+e,{
				
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					this.setState({
						dataSource:res.data.data
					})
				}else{
					message.info(res.data.msg)
				}
			})
		}else{
			this.customer()
		}
		
	}
	// 新增
	handleOk =() =>{
		console.log("formData:",this.refs.myForm.getFieldValue());
		// this.getMap()
		console.log(this.state.mapData)
		let {mapData} = this.state;
		let contactpenlist=this.state.contactpenlist
		console.log(contactpenlist)
		let assetModel = []
		if(this.state.edit ==1){
			// 新增
			this.refs.myForm.validateFields().then(values => {
				console.log(values)
				let electricityUserIds = []
				console.log(this.state.contactpenlist)
				for(var i=0;i<contactpenlist.length;i++){
					console.log(values[`jiedian${i}`])
					let s1 ;
					// if(values[`jiedian${i}`]){
					// 	s1 = values[`jiedian${i}`]
					// }else{
					// 	s1 = ''
					// }
					if(values[`jiedian${i}`]){
						s1 = values[`jiedian${i}`]
					}else if(contactpenlist){
						s1 = contactpenlist[i].nodeId
					}else{
						s1 = ''
					}
					if(contactpenlist[i].powerUserNumber.length>0){
						let cl = contactpenlist[i].powerUserNumber
						console.log(cl)
						let zi = []
						for(var j=0;j<cl.length;j++){
							console.log(values[`zjiedian${i}${j}`])
							if(values[`zjiedian${i}${j}`]){
								zi.push(values[`zjiedian${i}${j}`])
								
							}else{
								zi=[]
							}
						}
						console.log(zi)
						assetModel.push({
						   assetId: s1,
						   electricityUserIds:zi
						})
					}else{
						assetModel.push({
						   assetId: s1,
						   electricityUserIds:[]
						})
					}
				}
				console.log(assetModel)
				if(assetModel[0].assetId===undefined){
					return message.info('请选择节点')
				}
				if(assetModel.length ==0){
					message.info('请新增节点和电力用户号')
				}
				
				for(var s=0;s<assetModel.length;s++){
					console.log(assetModel[s])
					if(assetModel[s].assetId &&assetModel[s].electricityUserIds.length ==0){
						return message.info('请输入电力用户号')
					}else{
						return http.post('system_management/market_subject/AddCustomer',{
								"address":mapData.addr,
								"assetModel": assetModel,
								"business": values.business,
								"contact": values.contact,
								"id": "",
								"name":values.username,
								"phone": values.phone,
								"customerType": 'loadAggregator'
							}).then(res =>{
								console.log(res,'000000')
								if(res.data.msg =='成功'){
									this.refs.myForm.resetFields()
									this.setState({
										isModalVisible:false,
										address1:'',
										contactpenlist:[{
											powerUserNumber:[]
										}]
									},() => {
										this.customer()
										message.success('添加成功')
									})
								}else{
									message.info(res.data.msg)
								}
							})
					}
				}
				
			})
		}else if(this.state.edit ==0){
			// 编辑
			this.refs.myForm.validateFields().then(values => {
				console.log(values)
				let electricityUserIds = []
				console.log(this.state.contactpenlist)
				for(var i=0;i<contactpenlist.length;i++){
					console.log(values[`jiedian${i}`])
					let s1 ;
					// if(values[`jiedian${i}`]){
					// 	s1 = values[`jiedian${i}`]
					// } else if(values[`jiedian${i}`]===undefined){
					// 	s1 = contactpenlist[i].nodeId
					// }else{
					// 	s1 = ''
					// }
					if(values[`jiedian${i}`]){
						// s1 = values[`jiedian${i}`]
						s1 = values[`jiedian${i}`]
					} else if(contactpenlist.length){
						s1 = contactpenlist[i].nodeId
					}else{
						s1 = ''
					}
					if(contactpenlist[i].powerUserNumber.length>0){
						let cl = contactpenlist[i].powerUserNumber
						let zi = []
						for(var j=0;j<cl.length;j++){
							console.log(values[`zjiedian${i}${j}`])
							if(values[`zjiedian${i}${j}`]){
								zi.push(values[`zjiedian${i}${j}`])
								
							}else if(values[`zjiedian${i}${j}`] ===undefined){
								zi.push(cl[j])
							}else{
								zi=[]
							}
						}
						console.log(zi)
						assetModel.push({
						   assetId: s1,
						   electricityUserIds:zi
						})
					}else{
						assetModel.push({
						   assetId: s1,
						   electricityUserIds:[]
						})
					}
				}
				console.log(assetModel)
				if(assetModel.length ==0){
					message.info('请新增节点和电力用户号')
				}
				for(var s=0;s<assetModel.length;s++){
					console.log(assetModel[s])
					if(assetModel[s].assetId &&assetModel[s].electricityUserIds.length ==0){
						message.info('请输入电力用户号')
					}else{
						console.log(this.state.mapData)
						return http.post('system_management/market_subject/UpdateCustomer',{
									"address":this.state.address1,
									"assetModel": assetModel,
									"business": values.business,
									"contact": values.contact,
									"id": this.state.id,
									"name":values.username,
									"phone": values.phone,
									"customerType": 'loadAggregator'
								}).then(res =>{
									console.log(res,'000000')
									if(res.data.msg =='成功'){
										this.refs.myForm.resetFields()
										this.setState({
											isModalVisible:false,
											address1:'',
											contactpenlist:[{
												powerUserNumber:[]
											}]
										},() => {
											this.customer()
											message.success('编辑成功')
										})
									}else{
										message.info(res.data.msg)
									}
								})
					}
				}
				
			})
		}
		
	}
	showModal =() =>{
		this.setState({
			isModalVisible:true,
			edit:1
		})
	}
	handleCancel =() =>{
		this.setState({
			isModalVisible:false,
			address1:'',
			contactpenlist:[{
				powerUserNumber:[]
			}]
		},() =>{
			this.refs.myForm.resetFields()
		})
	}
	getMaps = (e) => {
		console.log(e)
		this.setState({	   
			address1: e.addr,
			mapData:e
		})
	}
	//删除
	delet(e){
		console.log(e)
		let id = e.id
		let that = this
		confirm({
		    title: '确定要删除吗？',
		    icon: <ExclamationCircleOutlined />,
			okText:"确认",
			cancelText:"取消",
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    // content: 'Some descriptions',
		    onOk() {
				console.log('OK');
				
				http.post('system_management/market_subject/DeleteCustomer?customerId='+e.id +'&customerType='+'loadAggregator').then(res =>{
					console.log(res,'-----')
					if(res.data.code ==200){
						// alert(0)
						message.success('删除成功')
						that.customer()
					}else{
						message.info(res.data.msg)
					}
				}).catch(error =>{
					console.log(error)
				})
		    },
		    onCancel() {
				console.log('Cancel');
		    },
		  });
		// console.log(e.)
		
	}
	handleChange =(e) =>{
		this.setState({
			nodeIds:e
		})
	}
	handleCancel1 =() =>{
		this.setState({
			look:false
		})
	}
	handleOk1 =() =>{
		this.setState({
			look:false
		})
	}
	// 编辑
	edit =(e) =>{
		console.log(e)
		this.setState({
			edit:0,
			isModalVisible:true,
			id:e.id,
			address1:e.address===null?'':e.address,
			istrue:true
		},() =>{
			http.post('system_management/market_subject/customerById?tenantId='+e.id).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let data = res.data.data
					this.setState({
						
						contactpenlist:data.nodePowerUserDataList
					})
				}
			}).catch(err =>{
				console.log(err)
			})
			this.refs.myForm.setFieldsValue({
				username:e.name,
				business:e.business,
				contact:e.contact,
				phone:e.phone,
				
				
			})
		})
		
	}
	// 查看
	look =(e) =>{
		this.setState({
			look:true
		},() =>{
			http.post('system_management/market_subject/customerById?tenantId='+e.id).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let data = res.data.data
					this.setState({
						ebussindess:data.name,
						person:e.name,
						caddress:e.address,
						tel:e.phone,
						buss:e.business,
						nodePowerUserDataList:data.nodePowerUserDataList
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		})
	}
	
	add = (item,index)=>{
		console.log(item,'item')
		const {contactpenlist} = this.state;
		item.powerUserNumber.push('');
	}
	remove =(index1,index,item1) =>{
		const {contactpenlist} = this.state;
		contactpenlist[index1].powerUserNumber.splice(index,1)
	}
	remove1 =(index,item) =>{
		console.log(index,'index')
		console.log(item,'item')
		const {contactpenlist} = this.state;
		if(contactpenlist.length<=1){
			return
		}
		contactpenlist.splice(index,1)
		console.log(contactpenlist)
		
	}
	add1 =() =>{
		const {contactpenlist,nodeIds} = this.state;
		let da = {value:'节点',powerUserNumber:[]}
		this.state.contactpenlist.push(da)
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				for(var i=0;i<data.length;i++){
					if(nodeIds ==data[i].id){
						data.splice(i, 1);
					}
				}
				console.log(data)
				this.setState({
					areas:res.data.data
				})
			}
		})
		this.setState({
			contactpenlist
		});
	}
	render(){
		const columns = [
			{
				title: '序号',
				width: '10%',
				render:(text,record,index)=> `${index+1}`,
			},
			{
				title: '公司名称',
				dataIndex: 'name',
				key: 'name',
			},
			{
				title: '公司地址',
				dataIndex: 'address',
				key: 'address',
			},
			{
				title: '公司业务',
				dataIndex: 'business',
				key: 'business',
			},
			{
				title: '节点数',
				dataIndex: 'asset_num',
				key: 'asset_num',
			},
			{
				title: '电力用户号数',
				dataIndex: 'electricity_user_num',
				key: 'electricity_user_num',
			},
			{
				title: '联系人',
				dataIndex: 'contact',
				key: 'contact',
			},
			{
				title: '电话',
				dataIndex: 'phone',
				key: 'phone',
			},
			{
				title: '操作',
				dataIndex: 'age',
				key: 'age',
				render: (text,record,_,action) =>{
					return 	<Space size="middle">
								<a onClick={() => this.look(record)}>查看</a>
								<a onClick={() => this.edit(record)}>编辑</a>
								<a onClick={() => this.delet(record)}>删除</a>
							</Space>
				}
			},
			
		];
		let {isModalVisible,address1,dataSource,areas,contactpenlist,types,edit,
		nodePowerUserDataList,look,loading} = this.state
		 const prefixSelector = (
		    <Form.Item name="prefix" noStyle>
				86
		    </Form.Item>
		  );
		
		return( 
			<div className="allcontent allelectric">
				<div className="electric">
					<div className="electrictitle">
						<Button type="primary" onClick={this.showModal}><PlusOutlined />添加虚拟电厂运营商</Button>
						<Search placeholder="按公司名称搜索"  onSearch={this.onSearch} style={{ width: 200 }} />
					</div>
					<div style={{marginTop:24}}>
						<Table dataSource={dataSource} columns={columns} loading={loading} />

					</div>
				</div>
				<Modal title={edit==0?'修改':'添加虚拟电厂运营商'} 
					width={740}
					visible={isModalVisible} 
					okText="确认"
					cancelText="取消"
					forceRender={true}
					onOk={this.handleOk} onCancel={this.handleCancel}>
					<Form
						name="basics"
						labelCol={{ span: 4 }}
						wrapperCol={{ span: 16 }}
						ref="myForm"
					>
					    <Form.Item
					    	label="公司名称"
					    	name="username"
					    	rules={[{ required: true, message: '请输入公司名称' }]} 
					    	extra="公司名称即为电力用户登录名，密码默认为!StxnDc0908，可在登录页面进行密码修改"
					    >
					    	<Input />
					        
					    </Form.Item>
						<Form.Item 
							name="address"
							label='地址'>
						  <BaiduMaps getMap={this.getMaps} 
							istrue={this.state.istrue}
														changeData={this.ChildrenChange}
							address={address1} />
						</Form.Item>
					    <Form.Item
							name="business"
							label='公司业务'>
							<Input.TextArea showCount maxLength={200} />
					    </Form.Item> 
						<Form.Item
						name="contact"
							label='联系人'
							rules={[
								{   required: true,
								message: '请输入联系人'    //提示语
								},
							]}
						>
							<Input />
						</Form.Item> 
						
						<Form.Item name="phone"
							label='电话'
							// validateTrigger='onBlur'    //仅在光标离开输入框时才做验证
							rules={[                    
							{   required: true,
								label:"电话号码",          //这个不写界面没影响，不过在调试console窗口，async-validator会显示“请输入undifined”
								message: '请输入正确格式号码'    //提示语
							},
							{
								required: false,        //在用户输入了内容后，再进行此项验证，所以要设为false
								label:"电话号码",
								pattern: new RegExp(/^1(3|4|5|6|7|8|9)\d{9}$/, "g"),
								message: '请输入正确格式号码'
							}
							]} >
						        <Input addonBefore={prefixSelector} style={{ width: '100%' }} />
						</Form.Item> 
						{
						 contactpenlist.map((item, index1) =>  {   //二级循环
							return (
								<Form.Item style={{marginBottom:'0px'}} label={index1==0?'节点':''} {...(index1 === 0 ? formItemLayoutWithOutLabel1  : formItemLayout1)}   >
										<Form.Item name={"jiedian" +index1} style={{marginBottom:'0x'}}>
											<Select
												placeholder="请选择节点"
												onChange={this.handleChange}
												defaultValue={item.nodeName}
												// value={item.}
												key={item.nodeId}
											  >
											  {
												  areas.map((item,index) =>{
													  return <Option key={item.id} value={item.id}>{item.nodeName}</Option>
												  })
											  }	
											</Select>
											
											
										</Form.Item>
										{index1 >= 1 ? (
											<DeleteOutlined className="removejie"
												onClick={() => this.remove1(item,index1)} /> 
										) : null}
										<Space style={{marginBottom:'15px'}}>
											{
											    item.powerUserNumber &&item.powerUserNumber.map((item1,index) =>{
											        return (
											            <Form.Item style={{marginBottom:'15px'}} 
														
															key={'zjiedian' +index} name={'zjiedian'+index1+index}>
															<div style={{padding:'10px 12px'}}>
																<MinusCircleOutlined
																onClick={() => this.remove(index1,index,item1)} /> 
																<Input  key={item1+index} defaultValue={item1} style={{width:'60%'}}  />
															</div> 
														</Form.Item>
											        )
											            
											    })
											}
											<a className="hu" 
											    style={{display:types==0?'block':'none',color:'#fff'}} 
											    onClick={() => this.add(item)}>
											<PlusOutlined /> &nbsp;&nbsp;&nbsp;添加电力用户号</ a>
										</Space>
										
											
									
								</Form.Item>
								
							)
	
						 })
						}						
					</Form>
					<a style={{marginLeft:110}} onClick={() => this.add1()}><PlusOutlined />&nbsp;&nbsp;&nbsp;添加节点选择</a>
				</Modal>
				<Modal title="查看"
					width={740}
					visible={look} 
					okText="确认"
					cancelText="取消"
					forceRender={true}
					onOk={this.handleOk1} onCancel={this.handleCancel1}>
					<div className="bussiness1">
						<div className="bussiness">
							<h4>{this.state.ebussindess}</h4>
							<div className="busindeecont">
								<ul>
									<li>联系人：{this.state.person}</li>
									<li>公司地址：{this.state.caddress}</li>
									<li>电话：{this.state.tel}</li>
									<li>公司业务：{this.state.buss}</li>
									
								</ul>
							</div>
						</div>
						
						{
							
							nodePowerUserDataList.length&&nodePowerUserDataList.map((item1, index1) =>     //二级循环
								
								<div className="bussiness">
									<h4>{item1.nodeName}</h4>
									<div className="mapdivs">
										{
											item1.powerUserNumber.map((item2, index2) =>{  //三级嵌套
											   return (
													<span>电力户号：{item2}</span>
											   )
												}
											)
										}
									</div>
									
								</div>  
									
								
							)
						}
						
					</div>
				</Modal >
			</div>
		)
	}
	
}
export default charge


// PvForecasting光伏    LoadForecastingg符合    

