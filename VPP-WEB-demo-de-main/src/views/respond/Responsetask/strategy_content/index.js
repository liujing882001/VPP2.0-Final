import React,{useEffect,useState} from 'react'
import { Space, Table, Tag,Modal,message,Input,Form,Button } from 'antd';
import http from '../../../../server/server.js'
import './index.scss'
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import {
	InfoCircleOutlined,CloseOutlined
 } from '@ant-design/icons';
const Strategy_content = (props) =>{
	const [strategyList, setStrategyList] = useState([]);
	const [respId, setRespId] = useState(props.user.respId);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [nodeName, setNodeName] = useState('-');
	const [noHouseholds, setNoHouseholds] = useState('');
	const [timePoint, setTimePoint] = useState('');
	const [deviceName, setDeviceName] = useState('');
	const [strategyContent, setStrategyContent] = useState('');
	const [id, setId] = useState('');
	const [temperature, setTemperature] = useState('');
	const [inputValue, setInputValue] = useState('');
	const [selectedIds, setSelectedIds] = useState([]);
	const [display, setDisplay] = useState('block');
	const [myForm] = Form.useForm();
	useEffect(() =>{
		if(props.user.respId){
			strategyQuery(props.user.respId)
		}
		
	},[])
	const strategyQuery =(e) =>{
		console.log(e)
		http.post('AIEnergy/strategyQuery',{
			respId:e
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				data.map((res,index) =>{
					return res.key = index
				})
				setStrategyList(res.data.data)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 编辑
	const edit =(e) =>{
		console.log(e)
		let str = e.strategyContent
		const temperatureIndex = str.indexOf("温度");
		const temperatureSubstring1 = null
		if (temperatureIndex !== -1) {
			let temperatureSubstring = str.substring(temperatureIndex + 2); // 截取温度之后的字符
			console.log(temperatureSubstring);
			// console.log( temperatureSubstring.replace("℃", ""))
			// const temperatureSubstring1 = null
			if (temperatureSubstring.includes("℃")) {
				console.log('111')
				temperatureSubstring = temperatureSubstring.replace("℃", "")
			}
			console.log(temperatureSubstring); 
			setInputValue(temperatureSubstring)
			myForm.setFieldsValue({
				temp:temperatureSubstring
			})
		  
		} else {
		  console.log("找不到温度信息。");
		}
		setIsModalVisible(true);
		setNodeName(e.nodeName ? e.nodeName : '-');
		setNoHouseholds(e.noHouseholds);
		setTimePoint(formatDate(e.timePoint));
		setDeviceName(e.deviceName);
		setStrategyContent(e.strategyContent);
		setId(e.id);
		
	}
	const formatDate = (objDate) =>{
		const timestamp = new Date(objDate);
		const year = timestamp.getFullYear();
		const month = (timestamp.getMonth() + 1).toString().padStart(2, '0'); // Adding 1 because getMonth() returns zero-based month
		const day = timestamp.getDate().toString().padStart(2, '0');
		const hours = timestamp.getHours().toString().padStart(2, '0');
		const minutes = timestamp.getMinutes().toString().padStart(2, '0');
		const formattedDate = `${year}-${month}-${day} ${hours}:${minutes}`;
		return formattedDate;
	} 
	const enSure = (ids) => {
        http.post("AIEnergy/strategyEnsure", {ids}, {
            headers: {
              'Content-Type': 'application/json'
            }
        })
        .then((response) => {
            message.success('确认成功')
			strategyQuery(respId)
			setSelectedIds([])
        })
        .catch((error) => {
            message.error('接口错误strategyEnsure')
        });
    }
	// 提示框隐藏
	const confirmation =() =>{
		setDisplay('none')
	}
	const columns= [
		{
			title: '序号',
			dataIndex: 'name',
			key: 'name',
			// render: text => <a>{text}</a>,
			render:(value, item, index) =>  index+1,
		},
		{
			title: '节点',
			dataIndex: 'nodeName',
			key: 'nodeName',
		},
		{
			title: '户号',
			dataIndex: 'noHouseholds',
			key: 'noHouseholds',
		},
		{
			title: '时间点',
			dataIndex: 'timePoint',
			key: 'timePoint',
			render:(value, item, index) =>  formatDate(value),
		},
		{
		  title: '策略内容',
		  dataIndex: 'ensure',
		  key: 'ensure',
		  render:(value) => !!value ? '已确认' : '未确认'
		},
		{
			title: '策略状态',
			dataIndex: 'strategyContent',
			key: 'strategyContent',
		  },
		{
			title: '操作',
			dataIndex: 'address',
			key: 'address',
			render: (text,record,_,action) =>{
				return <>
					<Space size="middle">
						<a onClick={() => edit(record)} style={{marginRight:'8px'}}>编辑</a>
					</Space>
					{
						record?.ensure === 0 ? 
						<Space size="middle">
							<a onClick={() => enSure([record?.id])}>确认</a>
						</Space> :null
					}
					
				</>
		  }
		}
	  
	];

	const handleOk =() =>{
		console.log(myForm.getFieldsValue())
		let temperature = myForm.getFieldsValue().temp
		console.log(temperature)
		if(temperature!==""&&temperature<7||temperature>12){
			message.error('出水温度范围需在7℃到12℃之间')
		}else if(temperature){
			http.post("AIEnergy/strategyUpdate", {
				id,
				strategyContentBefore:inputValue,
				strategyContent:Number(temperature).toFixed(2)
			}).then(res =>{
				
				if(res.data.code==200){
					console.log(res)
					setIsModalVisible(false)
					message.success('修改成功');
					myForm.resetFields()
					strategyQuery(respId)
				}
			}).catch(err =>{
				console.log(err)
			})
		}else{
			message.info('请输入出水温度')
		}
		
	}
	const handleCancel = () =>{
		setIsModalVisible(false)
	}
	const rowSelection = {
		onChange: (selectedRowKeys, selectedRows) => {
			console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
			const ids = selectedRows.map((item) => item?.id)
			setSelectedIds(ids)
		},
		getCheckboxProps: (record) => ({
		  disabled: record?.ensure === 1,
		}),
	 };

	
	const onFinishFailed =(values) =>{
		console.log(values)
	}
	const onFinish =(values) =>{
		console.log(values)
	}
	return(
		<div style={{padding:16,background:'#212029',borderRadius: '2px'}}>
			
			<Button className='confirm-btn' disabled={selectedIds.length === 0} onClick={() => enSure(selectedIds)}>批量确认</Button>
			<Table columns={columns} 
				   dataSource={strategyList}             
				   rowSelection={{
						type: 'checkbox',
						...rowSelection,
					}}>
			</Table>
			<Modal className="ModalRight" title="编辑" visible={isModalVisible} 
				onCancel={handleCancel}
			
				footer={[
			
					<Button ghost onClick={handleCancel}>取消</Button>,
					<Button  type="primary" onClick={handleOk}>确定</Button> 
				]}>
				<div className="editModal">
					<div className="editModal-Left">
						<ul>
							<li>节点</li>
							<li>户号</li>
							<li>时间点</li>
							<li>控制设备</li>
							<li>出水温度</li>
						</ul>
					</div>
					<div className="editModal-Right">
						<ul>
							<li style={{lineHeight:'50px'}}>{nodeName}</li>
							<li style={{lineHeight:'50px'}}>{noHouseholds}</li>
							<li style={{lineHeight:'40px'}}>{timePoint}</li>
							<li>{deviceName}</li>
							<li> 
								<Form
									  name="basic"
									  labelCol={{ span: 5 }}
									  wrapperCol={{ span: 19 }}
									  // initialValues={{ remember: true }}
									  // onFinish={onFinish}
									  // onFinishFailed={onFinishFailed}
									  autoComplete="off"
									  form={myForm}
									>
									<Form.Item
										// required
									>
										<Form.Item name="temp" style={{width:'60px',float:'left'}}
											rules={[
												{
													// required: true,
													// pattern: /^(([7-9](\.\d{1,2})?)|([1][0-9](\.\d{1,2})?)|20)$/,
													message: "",
												}
											]}
										 >
											<Input  />
										</Form.Item>
										<span className="prompt-title" style={{color:'#FFF',margin:'5px 0px 0px 0px'}}> °C
											<b>（范围限制：7°C-12°C）</b>
										</span>
									</Form.Item>
								</Form>
							 </li>
						</ul>
					</div>
				</div>
			</Modal>
		</div>
	)
}
	


export default Strategy_content