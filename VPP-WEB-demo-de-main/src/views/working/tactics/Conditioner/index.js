import React,{Component} from 'react'
import {Button,Select,Input ,Switch  ,Modal ,Form,
Radio,
Checkbox,DatePicker,TimePicker, } from 'antd';
import { PlusOutlined ,FormOutlined,DeleteOutlined} from '@ant-design/icons';
import './index.css'
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
const { Search } = Input;
const { RangePicker } = DatePicker;
class Conditioner extends Component {
	  formRef = React.createRef()

	constructor(props) {
		super(props)
		this.state={
			lists:[{
				id:'001',
				name:'空调二组（111台）',
				model:'制热',
				temperature:'28',
				windspeed:'一档',
				startingup:'13:00',
				machine:'16:00',
				period:'周一,周二',
				cycle:'周一,周二,周三'
				
			},
			{
				id:'002',
				name:'空调一组（131台）',
				model:'制热',
				temperature:'28',
				windspeed:'一档',
				startingup:'',
				machine:'',
				period:'周一',
				cycle:'周一,周二,周三'
				
			}],
			isModalVisible:false,
			edit: '', // 0 新增 1 编辑
			switchs:false,
			switchs1:false,
			show:'',
			shows:''
		}
	}
	componentDidMount(){
		
	}
	onSearch =(val) =>{
		
	}
	Swicth = (val) =>{
		
	}
	edit =(e) =>{
		console.log(e.cycle)
		var str_1 = e.period.replace(/[ \n]|,,/g, ",");
		var stringResult = str_1.substr(0,str_1.length).split(',');
		for(var i=0;i<stringResult.length;i++){
			console.log(stringResult[i])
			if(stringResult[i] =='周一'){
				 stringResult[i] ='1'
				console.log(stringResult)
			}else if(stringResult[i] =='周二'){
				 stringResult[i] ='2'
			}else if(stringResult[i] =='周三'){
				 stringResult[i] ='3'
			}else if(stringResult[i] =='周四'){
				 stringResult[i] ='4'
			}else if(stringResult[i] =='周五'){
				 stringResult[i] ='5'
			}else if(stringResult[i] =='周六'){
				 stringResult[i] ='6'
			}else if(stringResult[i] =='周日'){
				 stringResult[i] ='7'
			}
		}
		let s = stringResult.toString()
		let cycle = e.cycle.replace(/[ \n]|,,/g, ",");
		let newcycle = cycle.substr(0,cycle.length).split(',');
		for(var i=0;i<newcycle.length;i++){
			if(newcycle[i] =='周一'){
				 newcycle[i] ='1'
			}else if(newcycle[i] =='周二'){
				 newcycle[i] ='2'
			}else if(newcycle[i] =='周三'){
				 newcycle[i] ='3'
			}else if(newcycle[i] =='周四'){
				 newcycle[i] ='4'
			}else if(newcycle[i] =='周五'){
				 newcycle[i] ='5'
			}else if(newcycle[i] =='周六'){
				 newcycle[i] ='6'
			}else if(newcycle[i] =='周日'){
				 newcycle[i] ='7'
			}
		}
		let s1 = newcycle.toString()
		this.setState({
			isModalVisible:true,
			edit:1,
			show:e.startingup!=''?true:false,
			switchs:e.startingup!=''?true:false,
			shows:e.machine!=''?true:false,
			switchs1:e.machine!=''?true:false,
		},() =>{
			this.refs.myForm.setFieldsValue({
			    name:e.name,
				startingup:dayjs(e.startingup,'HH:mm'),
				starting:e.startingup==''?true:false,
				temperature:e.temperature,
				period:s,
				cycle:s1,
				model:e.model,
				windspeed:e.windspeed
			})
		})
		
	}
	delete =(e) =>{
		console.log(e)
	}
	
	
	handleOk = () => {
		this.setState({
			isModalVisible:false
		})
		console.log("formData:",this.refs.myForm.getFieldValue());
	};
	
	handleCancel = () => {
	    this.setState({
	    	isModalVisible:false
	    })
	};
	Checkboxange =(checkedValues) =>{
		let that = this
		console.log(checkedValues)
		that.setState ({
			switchs:checkedValues
		})
	}
	startingups =(checkedValues) =>{
		console.log(checkedValues)
		this.setState({
			switchs1:checkedValues,
			
		})
	}
	append =() =>{
		this.setState({
			isModalVisible:true,
			edit:0
		})
	}
	render(){
		let {lists,isModalVisible,edit,switchs,switchs1,show,shows} =this.state
		const onFinish = (values: any) => {
		    console.log('Success:', values);
		};
		
		const onFinishFailed = (errorInfo: any) => {
		    console.log('Failed:', errorInfo);
		};
		const config = {
			rules: [{ type: 'object',message: 'Please select time!' }],
		};
		const options = [
			{ label: '周一', value: '1' },
			{ label: '周二', value: '2' },
			{ label: '周三', value: '3' },
			{ label: '周四', value: '4' },
		    { label: '周五', value: '5' },
			{ label: '周六', value: '6' },
			{ label: '周日', value: '7' },
		]
		return(
			<div>
				<div className="Conditionerheader">
					<Button type="primary" onClick={this.append}><PlusOutlined />新建项目组</Button>
					<Search placeholder="搜索" onSearch={this.onSearch} style={{ width: 200 }} />
				</div>
				<div className="Conditioner">
					<ul className="summarizing">
						{
							lists.map((item,index) =>{
								return 	<li >
									<div className="summername">{item.name}
										<Switch checkedChildren="开启" unCheckedChildren="关闭" onChange={this.Swicth} />
									</div><div className="summering">
										<div className="sunimg">
											<img src={require('../../../../style/img/air.png')}  />
										</div>
										<div className="listing">
											<ol>
												<li>运行模式：{item.model}</li>
												<li>定时开机：{item.startingup}</li>
												<li>定时关机：{item.temperature}</li>
												<li>温度设定：{item.temperature}</li>
												<li>重复周期：{item.cycle}</li>
												<li>重复周期：{item.period}</li>
												<li>风速档位：{item.windspeed}</li>
											</ol>
										</div>
										<div className="edit">
											<a onClick={() => { this.edit(item) }} ><FormOutlined />编辑</a><br />
											<a onClick={() => { this.delete(item) }} ><DeleteOutlined />删除</a>
										</div>
									</div>
									
								</li>
							})
						}
					</ul>
				</div>
				<Modal 
					title={!edit ? `新建项目组` : `编辑`}
					visible={isModalVisible} 
					onOk={this.handleOk} 
					onCancel={this.handleCancel}
					okText="确定"
					cancelText="取消"
				>
					<Form
						name="basic"
						labelCol={{ span: 4 }}
						wrapperCol={{ span: 14 }}
						initialValues={{ remember: true }}
						onFinish={onFinish}
						onFinishFailed={onFinishFailed}
						autoComplete="off"
						ref="myForm"
					>
					    <Form.Item
					        label="组名称"
					        name="name"
							style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.2)',paddingBottom:'20px' }}
					    >
							<Input />
					    </Form.Item>
						<Form.Item   label="定时开机"  style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.2)' }}>
						    <Form.Item name="starting"
						        style={{ display: 'inline-block', width: '30%' }}
						    >
								<Switch onChange={this.Checkboxange} defaultChecked={show}  />
						    </Form.Item>
						      
						    <Form.Item name="startingup"  style={{ display: 'inline-block', width: '70%' }}>
								<TimePicker disabled={edit==1&&show==true?!switchs:''} format='HH:mm'  />
						    </Form.Item>
						</Form.Item>
						<Form.Item name="period" label="重复周期">
							<Checkbox.Group disabled ={!switchs} options={options}  />
						</Form.Item>
						<Form.Item  name="machine" label="定时关机"  style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.2)' }}>
						    <Form.Item
						        style={{ display: 'inline-block', width: '30%' }}
						    >
								<Switch onChange={this.startingups} defaultChecked={shows}  />
						    </Form.Item>
						      
						    <Form.Item style={{ display: 'inline-block', width: '70%' }}>
								<TimePicker format='HH:mm'  disabled={edit==1&&shows==true?!switchs1:''} />
						    </Form.Item>
						</Form.Item>
						<Form.Item name="cycle" label="重复周期">
							<Checkbox.Group disabled ={!switchs1} options={options}  />
						</Form.Item>
						<Form.Item
						    label="温度设定"
						    name="temperature"
						>
							<Input style={{ width: 90 }}   />
						</Form.Item>
						<Form.Item name="model" label="运行模式">
						    <Radio.Group>
						        <Radio value="制冷">制冷</Radio>
						        <Radio value="制热">制热</Radio>
						    </Radio.Group>
						</Form.Item>
						<Form.Item name="windspeed" label="风速档位" style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.2)' }}>
						    <Radio.Group>
								<Radio value="一档">一档</Radio>
						        <Radio value="二档">二档</Radio>
								<Radio value="三档">三档</Radio>
						    </Radio.Group>
						</Form.Item>
						<Form.Item name="windspeed" 
						wrapperCol={{ span: 24 }}
						extra="系统通过AI调度，根据需求响应计划，自动执行需求响应策略" >
							<Checkbox>
							    参与需求侧响应
							</Checkbox>
						</Form.Item>
					</Form>
				</Modal>
			</div>
		)
	}
	
}

export default Conditioner