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
  PlusCircleOutlined,
  MobileOutlined
} from '@ant-design/icons';
import BaiduMap from './BaiduMap';
import http from '../../../../server/server.js'
import axios from 'axios'
import './index.css'
import qs from 'qs'
const { Option } = Select;

const { Search } = Input;




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

class electric extends Component {
	constructor(props) {
		super(props)
		this.state={
			isModalVisible:false,
			address: '',
			dataSource:[],
			mapData:[],
			areas:[],
			contactpenlist:[{
				text:'124',
				powerUserNumber:[]
			}],
			types:0,
			edit:0	,//0修改，1新增
			id:'',
			look:false,
			ebussindess:'',
			person:'',
			caddress:'',
			tel:'',
			buss:'',
			nodePowerUserDataList:[],
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
	componentWillUnmount() {
	    this.setState = (state, callback) => {
	      return
	  }
	}
	// 节点列表
	nodeNameList(){
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				// res.data.data
				this.setState({
					areas:res.data.data
				})
			}
		})
	}
	// 电力用户列表
	customer(){
		this.setState({
			loading:true
		})
		http.post('system_management/market_subject/customerList?customerType=electricityConsumers',{
			
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
	// 搜索
	onSearch =(e) =>{
		console.log(e)
		if(e){
			http.post('system_management/market_subject/customerListByCName?customerType=electricityConsumers'+ "&tenantName="+e,{
				
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					this.setState({
						dataSource:res.data.data
					})
				}
			})
		}else{
			this.customer()
		}
		
	}
	// 添加电力用户号
	handleOk =() =>{
		console.log("formData:",this.refs.myForm.getFieldValue());
		console.log(this.state.mapData)
		let {mapData} = this.state;
		let contactpenlist=this.state.contactpenlist
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
				if(assetModel.length ==0){
					message.info('请新增节点和电力用户号')
				}
				for(var s=0;s<assetModel.length;s++){
					console.log(assetModel[s])
					if(assetModel[0].assetId===undefined){
						return message.info('请选择节点')
					}
					if(assetModel[s].assetId &&assetModel[s].electricityUserIds.length ==0){
						return message.info('请输入电力用户号')
					}else{
						return http.post('system_management/market_subject/AddCustomer',{
							"address":this.state.address,
							"assetModel": assetModel,
							"business": values.business,
							"contact": values.contact,
							"id": "",
							"name":values.names,
							"phone": values.phone,
							"customerType": 'electricityConsumers'
						}).then(res =>{
							console.log(res,'000000')
							if(res.data.code ==200){
								this.refs.myForm.resetFields()
								this.setState({
									isModalVisible:false,
									address:'',
									contactpenlist:[{
										powerUserNumber:[]
									}],
									
								},() => {
									this.customer()
									message.success('添加成功')
								})
							}else{
								message.success(res.data.msg)
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
					console.log(contactpenlist[i])
					console.log(values[`jiedian${i}`])
					let s1 ;
					// s1 = contactpenlist[i].nodeId
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
						console.log(cl)
						let zi = []
						for(var j=0;j<cl.length;j++){
							console.log(cl[j])
							console.log(values[`zjiedian${i}${j}`])
							if(values[`zjiedian${i}${j}`]){
								// zi.push(cl[j])
								zi.push(values[`zjiedian${i}${j}`])
								
							}else if(values[`zjiedian${i}${j}`]===undefined){
								zi.push(cl[j])
							}else{
								zi=[]
								
							}
							// 
						}
						console.log(s1)
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
						return http.post('system_management/market_subject/UpdateCustomer',{
							"address":this.state.address,
							"assetModel": assetModel,
							"business": values.business,
							"contact": values.contact,
							"id": this.state.id,
							"name":values.names,
							"phone": values.phone,
							"customerType": 'electricityConsumers'
						}).then(res =>{
							console.log(res,'000000')
							if(res.data.code ==200){
								this.refs.myForm.resetFields()
								this.setState({
									isModalVisible:false,
									address:'',
									contactpenlist:[{
										powerUserNumber:[]
									}]
								},() => {
									this.customer()
									message.success('编辑成功')
								})
							}else{
								message.success(res.data.msg)
							}
						})
					}
				}
				// if(assetModel.)
				
			})
		}
		

	}
	// 添加
	showModal =() =>{
		this.setState({
			isModalVisible:true,
			edit:1
		})
	}
	handleCancel =() =>{
		this.setState({
			isModalVisible:false,
			address:'',
			contactpenlist:[{
				powerUserNumber:[]
			}]
		},() =>{
			this.refs.myForm.resetFields()
		})
	}
	getMap = (e) => {
		console.log(e)
		this.setState({	   
			address: e.addr,
			mapData:e
		},() =>{
			
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
			okText:"确定",
			cancelText:"取消",
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    // content: 'Some descriptions',
		    onOk() {
				console.log('OK');
				
				http.post('system_management/market_subject/DeleteCustomer?customerId='+id +'&customerType=' +'electricityConsumers').then(res =>{
					console.log(res,'-----')
					if(res.data.code ==200){
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
	//修改
	edit =(e) =>{
		console.log(e)
		this.setState({
			edit:0,
			isModalVisible:true,
			id:e.id,
			address: e.address,
			istrue:true
		},() =>{
			http.post('system_management/market_subject/customerById?tenantId='+e.id).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					let data = res.data.data
					this.setState({
						// ebussindess:data.name,
						// person:e.name,
						// caddress:e.address,
						// tel:e.phone,
						// buss:e.business,
						contactpenlist:data.nodePowerUserDataList
					},() =>{
						console.log(this.state.contactpenlist)
					})
				}
			}).catch(err =>{
				console.log(err)
			})
			this.refs.myForm.setFieldsValue({
				names:e.name,
				username:e.name,
				business:e.business,
				contact:e.contact,
				phone:e.phone,
				
				
			})
		})
		
	}
	// 查看
	lookver =(e) =>{
		console.log(e)
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
	handleChange =(e) =>{
		console.log(e)
		this.setState({
			nodeIds:e
		})
		// this.refs.myForm.setFieldsValue({ sights: [] });
	}
	handleOk1 =() =>{
		this.setState({
			look:false
		})
	}
	handleCancel1 =() =>{
		this.setState({
			look:false
		})
	}
	add = (item,index)=>{
		console.log(item,'item')
		const {contactpenlist} = this.state;
		item.powerUserNumber.push("");

	}
	remove =(index1,index,item1) =>{
		const {contactpenlist} = this.state;
		console.log(index1,'index1')
		console.log(index,'index')
		console.log(item1,'item1')
		// item1.children.splice(index,1)
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
		console.log(da)
		this.state.contactpenlist.push(da)
		console.log(this.state.contactpenlist)
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
								<a onClick={() => this.lookver(record)}>查看</a>
								<a onClick={() => this.edit(record)}>编辑</a>
								<a onClick={() => this.delet(record)}>删除</a>
							</Space>
				}
			},
			
		];
		let {isModalVisible,address,dataSource,areas,contactpenlist,types,loading,
		
		edit,look,nodePowerUserDataList} = this.state
		const prefixSelector = (
		    <Form.Item name="prefix" noStyle>
				86
		    </Form.Item>
		);
		return(
			<div className="allcontent allcharget">
				<div className="electric">
					<div className="electrictitle">
						<Button type="primary" onClick={this.showModal}><PlusOutlined />添加电力用户</Button>
						<Search placeholder="按公司名称搜索"  onSearch={this.onSearch} style={{ width: 200 }} />
					</div>
					<div style={{marginTop:24}}>
						<Table dataSource={dataSource} columns={columns} loading={loading} />

					</div>
				</div>
				<Modal title={edit==0?'修改':'添加电力用户'} 
				width={740}
				visible={isModalVisible} 
				okText="确定"
				cancelText="取消"
				forceRender={true}
				onOk={this.handleOk} onCancel={this.handleCancel}>
					<Form
						name="basics"
						labelCol={{ span: 4 }}
						wrapperCol={{ span: 16 }}
						ref="myForm"
						// {
						// 	// contactpenlist.length &&contactpenlist.map((res,index4) =>{
						// 	// 	return initialValues={{
						// 	// 				"jiedian" +index4: 'dedecb309fe641d3a1b7cf5e8c498038',
						// 	// 			}}
						// 	// })
						// }
						
					>	
						<Form.Item
							label="角色"
							name="role"
						>
							<div>
								<Typography.Text className="ant-form-text" type="secondary">
									电力用户
								</Typography.Text>
								
							</div>
						</Form.Item>
						<Form.Item
							name="names"
							label='公司名称'
							rules={[{ required: true, message: '请输入用户名' }]} 
						>
							<Input  />
						</Form.Item> 
					    
						<Form.Item 
							name="address"
							label='地址'>
						  <BaiduMap 
						  istrue={this.state.istrue} 
							changeData={this.ChildrenChange}
						  getMap={this.getMap} address={address} />
						</Form.Item>
					    <Form.Item
							name="business"
							label='公司业务'>
							<Input.TextArea showCount maxLength={200} />
					    </Form.Item> 
						
						<Form.Item
							name="contact"
							label='联系人'
							rules={[{ required: true, message: '请输入联系人' }]} 
						>
							<Input />
						</Form.Item> 
						<Form.Item
							name="phone"
							label="电话"
							rules={[{ required: true, message: '' }]} 
						>
						<Form.Item name="phone"
							validateTrigger='onBlur'    //仅在光标离开输入框时才做验证
							rules={[                    
								// {   required: true,
								// 	message: '请输入电话号码'    //提示语
								// },
								{
									required: true,        //在用户输入了内容后，再进行此项验证，所以要设为false
									pattern: new RegExp(/^1(3|4|5|6|7|8|9)\d{9}$/, "g"),
									message: '请输入正确格式号码'
								},
								// rules={[{
								//             pattern:
								//                 /^(?![^a-zA-Z]+$)(?!\\D+$)(?=.*[^a-zA-Z0-9]).{8,100}$/,
								//             message: "不能小于8位字符，必须包括字母,数字和特殊字符",
								//         }]}
							]} 
						
						>
						    <Input addonBefore={prefixSelector} style={{ width: '100%' }} />
						</Form.Item>	
						    
						</Form.Item>
						{
						 contactpenlist.map((item, index1) =>  {   //二级循环
						 
							return (
								<Form.Item  style={{marginBottom:'0px'}}  
									name="hybrid"
									
									label={index1==0?'节点':''}
									{...(index1 === 0 ? formItemLayoutWithOutLabel1  : formItemLayout1)}   >
									
									
										<Form.Item name={"jiedian" +index1} 
										
										style={{marginBottom:'0x'}}>
											<Select
												placeholder="请选择节点"
												onChange={this.handleChange}
												defaultValue={item.nodeId}
												// value={item.}
												key={item.nodeId}
											  >
											  {
												  areas.map((item,index) =>{
													  return <Option key={item.id +index1} value={item.id}>{item.nodeName}</Option>
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
					okText="确定"
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
export default electric


// PvForecasting光伏    LoadForecastingg符合    

