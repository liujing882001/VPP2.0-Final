import React,{useState,useEffect,useRef}  from 'react';
import { Form, Input, InputNumber, Popconfirm, Typography,message ,Button,DatePicker, Table,Modal  } from 'antd';
import { useHistory,useLocation  } from 'react-router-dom'
import './index.scss'
import { Energy } from './components/energy'
// import http from '../../../../server/server';
import http from '../../../../server/server.js'
import classNames from 'classnames';
import dayjs from 'dayjs'; 

const Electrovalence = (props) => {
    const history = useHistory()
	const location = useLocation()
	const stateParams = location.state;
	const [id,setId] = useState('')
	const [isEdit,setIsEdit] = useState(false)
	const [month,setMonth] = useState('')
	const [priceOffPeak,setPriceOffPeak] = useState('')
	const [pricePeak,setPricePeak] = useState('')
	const [priceSharp,setPriceSharp] = useState('')
	const [priceShoulder,setPriceShoulder] = useState('')
	const [priceList,setPriceList] = useState([])
	const [priceVal,setPriceVal] = useState('')
	const headerbtnghostref = useRef(null)
	const [priceSharpform] = Form.useForm();
	const [savedFormData, setSavedFormData] = useState({});
	const [timeSlots,setTimeSlots] = useState([])
	const [errorval,seterrorval] = useState('')
	const [isTrue,setIsTrue] = useState(false)
	useEffect(() =>{
		let item = location.state
		if(item){
			setId(item.id)
		}else{
			history.push('/hybrid')
		}
		
	},[location.state])
	const queryNodeEPrice = async (nodeId) =>{
		try {
			const response = await http.post('stationNodeEP/queryNodeEPrice', {
				nodeId:nodeId,
				date:month
			});
			if(response.data.code===200){
				const data = response.data.data
				setPriceOffPeak(data.priceOffPeak)
				setPricePeak(data.pricePeak)	
				setPriceSharp(data.priceSharp)
				setPriceShoulder(data.priceShoulder)
				let timeSlots  =data.timeSlots
				const result = Object.keys(timeSlots).map(timeRange => {
				    const priceHour = timeRange
				    return {
				        priceHour: priceHour,
				        property: timeSlots[timeRange],
				        timeRange: timeRange
				    };
				});
				setTimeSlots(result)
			}else{
			  message.info(response.data.msg)
			  setTimeSlots([])
			  setPriceOffPeak('-')
			  setPricePeak('-')	
			  setPriceSharp('-')
			  setPriceShoulder('-')
			}
		  
		} catch (error) {
		  console.error(error);
		}

	}
	
	useEffect(() =>{
		if(month&&id){
			queryNodeEPrice(id)
		}else{
			setTimeSlots([])
			setPriceOffPeak('')
			setPricePeak('')	
			setPriceSharp('')
			setPriceShoulder('')
		}
	},[id,month])
	const monthChange =(date,datemonth) =>{
		setMonth(datemonth)
	}
	//保存
	const updateNodeEpBatch =async(formData) =>{
		if(id&&month){
			let prices = [
				{
					"type": "尖",
					"price": Number(formData.priceSharp)
				},
				{
					"type": "峰",
					"price": Number(formData.pricePeak)
				},
				{
					"type": "平",
					"price": Number(formData.priceShoulder)
				},
				{
					"type": "谷",
					"price": Number(formData.priceOffPeak)
				}
			]
			try {
				const response = await http.post('stationNodeEP/updateNodeEpBatch', {
					nodeId:id,
					date:month,
					prices:prices
				});
				
				if(response.data.code===200){
					message.success('编辑成功')
					queryNodeEPrice(id)
				}else{
					message.info(response.data.msg)
				}
			} catch (error) {
			  console.error(error);
			}
		}else{
			message.info('请选择节点和日期时间')
		}
		
	}
	useEffect(() =>{
		if (isEdit) {
		    priceSharpform.setFieldsValue({
		      priceSharp: priceSharp,
		      pricePeak: pricePeak,
		      priceShoulder: priceShoulder,
		      priceOffPeak: priceOffPeak
		    });
		}
	},[isEdit,priceVal])
	const areValuesNumbers =(obj) =>{
		const numberRegex = /^-?\d+(\.\d{1,8})?$/;
		for (let key in obj) {
			if (obj.hasOwnProperty(key)) {
				const value = obj[key];
				if (typeof value === 'string' && numberRegex.test(value)) {
					obj[key] = Number(value);
				} else if (typeof value !== 'number' || isNaN(value) || !numberRegex.test(String(value))) {
					return false;
				}
			}
		}
		return true;
	}

	const handleSave = () => {
		setIsEdit(!isEdit)
		if(priceVal==='编辑'){
			setPriceVal('保存');
		}else{
			const formData = priceSharpform.getFieldsValue();
			let isObjectValuesNonEmpty = Object.values(formData).every(value => value !== '');
			let isAreValuesNumbers = areValuesNumbers(formData)
			if(isEdit&&isObjectValuesNonEmpty){
				if(isAreValuesNumbers){
					updateNodeEpBatch(formData)
				}else{
					message.info('尖峰平谷只能为数字')
					setIsEdit(true)
				}
				
			}else if(isEdit&&!isObjectValuesNonEmpty){
				message.info('尖峰平谷不能为空')
				setIsEdit(true)
			}
			
		}
		
	};
	
	const Copyprice = async() =>{
		if(id&&month){
			Modal.confirm({
			    title: '提示',
				cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
			    content: '确定后，将覆盖之前的电价信息，是否确定？',
			    okText: '确定',
			    cancelText: '取消',
			    onOk() {
					http.post('stationNodeEP/copyEpLastMonth', {
						nodeId:id,
						date:month
					}).then(response =>{
						if(response.data.code===200){
							message.success('复制成功')
							queryNodeEPrice(id)
						}else{
						  message.info(response.data.msg)
						}
					}).catch(err =>{
						console.log(err)
					})
			    },
			    onCancel() {
					console.log('Cancel');
			    },
			});
			
		}else{
			message.info('请选择节点和日期时间')
		}
		
	}
	const validateCustomPattern =  (rule, value, callback) => {
	    // const regex = /^(?:0(?:\.\d{1,2})?|([1-9]\d{0,5}(?:\.\d{1,8})?))$/;	
		const regex = /^(?:0(?:\.\d{1,8})?|([1-9]\d{0,5}(?:\.\d{1,8})?))$/;	
		let isTrue = null
		if(rule.field&&!errorval){
			isTrue = false
		}else if(rule.field===errorval&&errorval){
			isTrue = true
		}else{
			isTrue = false
		}
		if(!isTrue){
			if(value){
				if (value && !regex.test(value)) {
					message.destroy()
				    message.info('请输入正确范围内的数字');
					seterrorval(rule.field)
					isTrue = true
				}else{
					isTrue = false
					message.destroy()
				}
				
			}
		}
	
	};
	// 从电价库获取电价
	const getTlectricity =async() =>{
		if(id&&month){
			Modal.confirm({
			    title: '提示',
				cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
			    content: '确定后，将覆盖之前的电价信息，是否确定？',
			    okText: '确定',
			    cancelText: '取消',
			    onOk() {
					http.post('stationNodeEP/generateEp', {
						nodeId:id,
						date:month
					}).then(response =>{
						if(response.data.code===200){
							message.success('获取成功')
							queryNodeEPrice(id)
						}else{
						  message.info(response.data.msg)
						}
					}).catch(err =>{
						console.log(err)
					})
			    },
			    onCancel() {
					console.log('Cancel');
			    },
			  });
			
		}else{
			message.info('请选择节点和日期时间')
		}
		
		
		
	}
	useEffect(() =>{
		const currentTime = dayjs().format('YYYY-MM');
		const previousDay = dayjs().subtract(1, 'month').format('YYYY-MM');
		setIsTrue(month===currentTime||month===previousDay||month>currentTime?true:false)
		
	},[month])
	return 	(
      <>
        <div className="bread-time-of-use">
          <span className='bread-time-of-use-title' onClick={() => {
            history.push('/hybrid');
          }}>节点模型</span> / <span className={classNames('bread-time-of-use-title',{'active':true})}>分时电价</span>
        </div>
        <div className='hybrid-time-of-use'>

			<div className='hybrid-time-of-use-header'>
			 <h2>{location.state?location.state.name:'-'}</h2>
				<div className='hybrid-time-of-use-header-right'>
					<DatePicker picker="month" onChange={monthChange} disabled={isEdit}/>
					<Button className='hybrid-time-of-use-header-btn' onClick={getTlectricity}>从电价库获取电价</Button>
					<Button className='hybrid-time-of-use-header-btn' onClick={Copyprice} disabled={isEdit}>复制上月电价信息</Button>
					<Button className='hybrid-time-of-use-header-btn' 
					disabled={id&&month&&isTrue?false:true}
					onClick={handleSave}>{isEdit ? '保存' : '编辑'}</Button>
					<Button className='hybrid-time-of-use-header-btn-ghost'
						style={{display:!isEdit?'none':'inline-block'}}
						onClick={() => {
							setIsEdit(false)
							setPriceVal('退出')
							seterrorval('')
							setPriceSharp(priceSharp)
							setPricePeak(pricePeak)
							setPriceShoulder(priceShoulder)
							setPriceOffPeak(priceOffPeak)
						}}
					>退出</Button>
				</div>
			</div>
			<div className="hybrid-time-of-use-list">
			<Form form={priceSharpform} name="control-hooks" autoComplete="off">
				<div className="hybrid-time-of-use-list-div">尖(元/kWh) 
					{
						isEdit?
							<Form.Item name="priceSharp" 
							rules={[{required: true, validator: validateCustomPattern }]}
							>
							    <Input />
							</Form.Item>
						:
						
						<span>{priceSharp}</span>
					}
				</div>
				<div className="hybrid-time-of-use-list-div">峰(元/kWh) {
					isEdit?<Form.Item name="pricePeak" 
						rules={[{required: true, validator: validateCustomPattern }]}
						>
							<Input />
						</Form.Item>
					:<span>{pricePeak}</span>
					}</div>
				<div className="hybrid-time-of-use-list-div">平(元/kWh)  {
						isEdit?<Form.Item name="priceShoulder" 
							rules={[{required: true, validator: validateCustomPattern }]}
							>
							    <Input />
							</Form.Item>:<span>{priceShoulder}</span>
					}</div>
				<div className="hybrid-time-of-use-list-div">谷(元/kWh) {
					isEdit?<Form.Item name="priceOffPeak" 
							rules={[{required: true, validator: validateCustomPattern }]}
						>
							    <Input />
							</Form.Item>:<span>{priceOffPeak}</span>
					}</div>
			</Form>	
			</div>
			{
				<div className='hybrid-time-of-use-table'>
					<Energy data={timeSlots&&timeSlots}/>
				</div> 
			} 
        </div>
      </>
    
    )
   
}
export default Electrovalence