import React, { useEffect,useState } from 'react';
import { Tabs,Table,Select,Input,Button,DatePicker,ConfigProvider,Space,Modal,Form,message } from 'antd';
import './dropdown.scss'
import {
  FormOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';
// import http from '../../../../axios/serve.js'
import http from '../../../../server/server.js'

const { Option } = Select;
const { confirm } = Modal;
const Dropdown =() =>{
	const [isModalVisible, setIsModalVisible] = useState(false);
	  const [total, setTotal] = useState('');
	  const [content, setContent] = useState([]);
	  const [stutes, setStutes] = useState(0); // 0新增，1修改
	  const [emissionFactorId, setEmissionFactorId] = useState('');
	  const [emissionFactorNum, setEmissionFactorNum] = useState('');
	  const [province, setProvince] = useState('北京市');
	  const [sstatus, setSStatus] = useState('');
	  const [createdTime, setCreatedTime] = useState('');
	  const [updateTime, setUpdateTime] = useState('');
	  const [node, setNode] = useState('');
	  const [loading, setLoading] = useState(false);
	  const [typeval, setTypeval] = useState('');
	  const [page, setPage] = useState(1);
	  const [currentNum, setCurrentNum] = useState(1);
	  const [setLoadings, setSetLoadings] = useState(false);
	  const [options, setOptions] = useState([]);
	const [dischargeType,setDischargeType] = useState('');
	// const
	const [myForm] = Form.useForm()
	useEffect(() =>{
		regionProvinces()
	},[])
	useEffect(() =>{
		getEmissionFactorList()
	},[page,currentNum])
	const append =() =>{
		setIsModalVisible(true);
		setStutes(0)
	}
	const quxiao =() =>{
		setIsModalVisible(false);
		myForm.resetFields()
	}
	// 查询
	const search =() =>{
		getEmissionFactorList()
	}
	// 获取省份
	// /system_management/sysregion_model/regionProvinces
	const regionProvinces=() =>{
		http.post('system_management/sysregion_model/regionProvinces').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				let options = []
				data&&data.map(res =>{
					options.push({
						label:res.regionName,
						value:res.regionName
					})
				})
				setOptions(options)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 获取碳排放因子列表
	const getEmissionFactorList=() =>{
		setLoading(true)
		http.post('carbon/emission/getEmissionFactorList',{
			"number": page,
			"pageSize": 10,
			"province": province	//省份
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setContent(res.data.data.content);
				setTotal(res.data.data.totalElements);
				setLoading(false);
			}
		}).catch(err =>{
			
		})
	}
	// 编辑
	const edit= (e) =>{
		console.log(e)
		if(e.description=='碳汇'){
			setStutes(1);
			setIsModalVisible(true);
			setEmissionFactorId(e.emissionFactorId);
			setEmissionFactorNum(e.emissionFactorNum);
			setProvince(e.province);
			setSStatus(e.sstatus);
			setCreatedTime(e.createdTime);
			setUpdateTime(e.updateTime);
			setDischargeType(e.dischargeType);
			setNode('');
			setTypeval('碳汇');
			myForm.setFieldsValue({
				type:e.emissionFactorName,
				co:e.co2,
				unit:e.unit,
				scope:'',
				description:e.description,
				Initial:e.initialValue,
				dischargeType:e.dischargeType
			})
		}else{
			if(e.scopeType==1){
				// 范围一
				setStutes(1);
				setIsModalVisible(true);
				setEmissionFactorId(e.emissionFactorId);
				setEmissionFactorNum(e.emissionFactorNum);
				setProvince(e.province);
				setSStatus(e.sstatus);
				setCreatedTime(e.createdTime);
				setUpdateTime(e.updateTime);
				setDischargeType(e.dischargeType);
				setNode(1);
				setTypeval('');
				myForm.setFieldsValue({
					type:e.emissionFactorName,
					co:e.co2,
					unit:e.unit,
					scope:e.scopeType,
					description:e.description,
					Initial:e.initialValue,
					dischargeType:e.dischargeType
				})
			}else{
				setStutes(1);
				setIsModalVisible(true);
				setEmissionFactorId(e.emissionFactorId);
				setEmissionFactorNum(e.emissionFactorNum);
				setProvince(e.province);
				setSStatus(e.sstatus);
				setCreatedTime(e.createdTime);
				setUpdateTime(e.updateTime);
				setDischargeType(null);
				setNode(0);
				setTypeval('');
				myForm.setFieldsValue({
					type:e.emissionFactorName,
					co:e.co2,
					unit:e.unit,
					scope:e.scopeType,
					description:e.description,
					Initial:e.initialValue,
					dischargeType:null
				})
			}
		}
		
		
	}
	// 删除
	const del =(e) =>{
		console.log(e)
		confirm({
		    title: '确定要删除吗?',
		    icon: <ExclamationCircleOutlined />,
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    // content: 'Some descriptions',
		    okText: '确定',
		   
		    cancelText: '取消',
		    onOk() {
				console.log('OK');
				http.post('carbon/emission/removeEmissionFactor?emissionFactorId='+e.emissionFactorId).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('删除成功')
						getEmissionFactorList()
					}
				}).catch(err =>{
					console.log(err)
				})
		    },
		    onCancel() {
				console.log('Cancel');
		    },
		  });
	}
	const chosenode =(val) =>{
		console.log(val)
		setNode(val)
	}
	// 选择省份
	const handlepoint =(val) =>{
		setProvince(val);
		setPage(1);
		setCurrentNum(1)
	}
	// 类型监听
	const changeType = (e) =>{
		console.log(e.target.value)
		let val = e.target.value
		if(val=='草坪' ||val=='树木'){
			setTypeval('碳汇');
			myForm.setFieldsValue({
				scope:''
			})
		}else{
			setTypeval('');
		}
	}

	const columns = [
		{
			title: '类型',
			dataIndex: 'emissionFactorName',
			key: 'emissionFactorName',
			// width:100
			
		},
		{
			title: 'CO2排放量',
			dataIndex: 'co2',
			key: 'co2',
			// width:100
		},
		{
			title: '单位',
			dataIndex: 'unit',
			key: 'unit',
			render: (text,record,_,action) =>{
				if( text==null||text==undefined){
					return '-'
				}else if(text!==''){
					// console.log(text)
					return text
				}else{
					return text
				}
			}
			// width:100
		},
		{
			title: '范围',
			dataIndex: 'scopeType',
			key: 'scopeType',
			render: (text,record,_,action) =>{
				return  text==1?'范围一':text==2?'范围二':text==3?'范围三':'-'
			}
			// width:100
		},
		{
			title: '备注',
			dataIndex: 'description',
			key: 'description',
			width:200,
			render: (text,record,_,action) =>{
				if( text==null||text==undefined){
					return '-'
				}else if(text!==''){
					// console.log(text)
					return text
				}else{
					return text
				}
			}
		},
		{
		  title: '操作',
		  dataIndex: 'dates',
		  key: 'dates',
		  width:150,
			render: (text,record,_,action) =>{
				return  <Space size="middle">
					<a onClick={() => edit(record)}>编辑</a>
					<a onClick={() => del(record)}>删除</a>
				</Space>
			}
		}
	];
	const handleOk = () => {
	
	};
	
	const handleCancel = () => {
		setIsModalVisible(false);
		myForm.resetFields()
	};
	const onFinish = (values) => {
		console.log('Success:', values);
		let emissionFactorNum = ''
		// let values1 = values
		if(values.scope==1){
			// 范围一
			if(values.type=='天然气'){
				emissionFactorNum = 11
			}else if(values.type=='煤气'){
				emissionFactorNum = 12
			}else if(values.type=='柴油'){
				emissionFactorNum = 13
			}else if(values.type=='公务车'){
				emissionFactorNum = 21
			}else if(values.type=='冷机制冷剂'){
				emissionFactorNum = 31
			}else if(values.type=='空调氟利昂'){
				emissionFactorNum = 32
			}else if(values.type=='灭火器'){
				emissionFactorNum = 33
			}
		}else if(values.scope==2){
			// 范围二
			if(values.type=='外购电力'){
				emissionFactorNum = 1
			}else if(values.type=='外购热力'){
				emissionFactorNum = 2
			}
		}else if(values.scope==3){
			// 范围三
			if(values.type=='差旅-火车'){
				emissionFactorNum = 2
			}else if(values.type=='差旅-飞机'){
				emissionFactorNum = 1
			}else if(values.type=='差旅-私家车'){
				emissionFactorNum = 3
			}else if(values.type=='自来水'){
				emissionFactorNum = 4
			}else if(values.type=='纸张消耗'){
				emissionFactorNum = 5
			}
		}
		console.log(emissionFactorNum)
		if(stutes==0){
			// 新增
			setSetLoadings(true)
			http.post('carbon/emission/addEmissionFactor',{
				"co2": values.co,//co2排放量
				"createdTime": "",	//创建时间
				"dischargeType": node==1?values.dischargeType:'',	//燃烧排放类型
				"emissionFactorId": "",	//排放因子id
				"emissionFactorName": values.type,	//排放因子名称
				"emissionFactorNum": emissionFactorNum,	//排放因子编号
				"initialValue": values.Initial,//初始值
				"province": province,	//省份
				"scopeType": values.scope,	//范围
				"sstatus": '',
				"unit": values.unit,	//单位
				"updateTime": "",
				"description":values.description	//备注
				 
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					message.success('成功')
					setIsModalVisible(false);
					setSetLoadings(false);
					myForm.resetFields()
					getEmissionFactorList()
				}else{
					setSetLoadings(false);
					message.info(res.data.msg);
					
				}
			}).catch(err =>{
				console.log(err)
			})
			
		}else if(stutes==1){
			// 编辑
			setSetLoadings(true)
			http.post('carbon/emission/editEmissionFactor',{
				"co2": values.co,//co2排放量
				"createdTime": createdTime,	//创建时间
				"dischargeType": values.dischargeType,	//燃烧排放类型
				"emissionFactorId": emissionFactorId,	//排放因子id
				"emissionFactorName": values.type,	//排放因子名称
				"emissionFactorNum": emissionFactorNum,	//排放因子编号
				"initialValue": values.Initial,//初始值
				"province": province,	//省份
				"scopeType": values.scope,	//范围
				"sstatus": sstatus,
				"unit": values.unit,	//、、单位
				"updateTime": updateTime,
				"description":values.description	//备注
				 
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					message.success('编辑成功')
					setIsModalVisible(false);
					setSetLoadings(false);
					myForm.resetFields()
					getEmissionFactorList()
				}else{
					setSetLoadings(false);
					message.info(res.data.msg);
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
	};
	
	const onFinishFailed = (errorInfo) => {
		console.log('Failed:', errorInfo);
	};
	const onChangetab: PaginationProps['onChange'] = page => {
		console.log(page);
		setPage(page.current);
		setCurrentNum(page.current);
	};
	return(
		<div className="allcontent1">
			<div className="header">
				省份：
				<Select
					defaultValue="北京市"
					style={{ width: 217 }}
					onChange={handlepoint}
					options={options}
				/>
				<Button type="primary" onClick={search}>查询</Button>
			</div>
			<div className="emission">
				<div className="title">
					<span>碳排放因子列表</span>
					<div className="list">
						<Button type="primary" onClick={append}>新增类型</Button>
						
					</div>
				</div>
				
				<Table dataSource={content} columns={columns} loading={loading}
					onChange={onChangetab}
					scroll={{ x: '100%' }}
				 
				 pagination={
					{
						total: total,//数据的总条数
						defaultCurrent: 1,//默认当前的页数
						defaultPageSize: 10,//默认每页的条数
						showSizeChanger:false,
						current:currentNum
					}
				 }
				 />
			</div>
			<Modal
				title={stutes==0?'新增类型':'编辑'}
				visible={isModalVisible}
				width={600}
				// confirmLoading={confirmLoading}
				onCancel={handleCancel}
				maskClosable={false}
				footer={null}
				// ref="myForm"
			>
				<Form
					name="basic"
					labelCol={{ span: 5 }}
					wrapperCol={{ span: 19 }}
					initialValues={{ remember: true }}
					onFinish={onFinish}
					onFinishFailed={onFinishFailed}
					autoComplete="off"
					form={myForm}
				>
						
					<Form.Item label="类型" name="type" 
						rules={ [{required: true, message: '请输入类型' }]}
					>
							<Input onChange={changeType} />
					</Form.Item>
					
					<Form.Item
						label="CO2排放量"
						name="co"
						// rules={ [{ pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
						rules={ [{required: true, pattern: new RegExp(/^([0-9][0-9]{0,5}(\.\d{1,9})?|1000000)$/),message: '请输入正确的数字' }]}
																	  // /^(?:0|[1-9][0-9]{0,9}?|1000000000)$/
					>
						<Input maxLength={50} />
					</Form.Item>
					<Form.Item
						label="单位"
						name="unit"
					>
						<Input maxLength={50} />
					</Form.Item>
					<Form.Item
						label="范围"
						name="scope"
						rules={ [{required: typeval=='碳汇'?false:true, message: '请选择范围' }]}
					>
						<Select
							placeholder="范围"
							onChange={chosenode}
							disabled={typeval=='碳汇'?true:false}
						>
							<Option value={1}>范围一</Option>
							<Option value={2}>范围二</Option>
							<Option value={3}>范围三</Option>
						</Select>
					</Form.Item>
					<Form.Item
						label="燃烧排放类型"
						name="dischargeType"
						className={[node==1?'':'className6']}
						style={{display:(node==1)?'block':'none'}}
						rules={ [{required: node==1?true:'', message: '请选择燃烧排放类型' }]}
						// style={{display:'none'}}
					>
						<Select
							placeholder="燃烧排放类型"
							// onChange={this.chosenode}
						>
							<Option value={1}>固定燃烧源的燃烧排放</Option>
							<Option value={2}>移动燃烧源的燃烧排放</Option>
							<Option value={3}>逸散型排放源的排放</Option>
						</Select>
					</Form.Item>
					<Form.Item
						label="备注"
						name="description"
						rules={ [{ pattern: new RegExp(/^[\S]{0,50}$/),message: '请输入0-50个以内的字符' }]}
					>
						<Input maxLength={50} />
					</Form.Item>
					<Form.Item
						label="初始值"
						name="Initial"
						// rules={ [{ pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
						rules={ [{required: true, pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}
					>
						<Input maxLength={50} />
					</Form.Item>
					<Form.Item wrapperCol={{ offset: 8, span: 16 }} style={{textAlign:'right'}}>
						<Button ghost onClick={quxiao} >
						  取消
						</Button>
						<Button style={{marginLeft:'10px'}} loading={setLoadings} type="primary" htmlType="submit">
						  确定
						</Button>
						
					</Form.Item>
				</Form>
			</Modal>
		</div>
	)
}
	

// <Button type="primary">删除</Button>

export default Dropdown