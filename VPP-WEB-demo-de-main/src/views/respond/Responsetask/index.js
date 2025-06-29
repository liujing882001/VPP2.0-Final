import React,{useEffect,useState } from 'react'
import { Link,useHistory  } from "react-router-dom"
import type { TimePickerProps } from 'antd';

import { DatePicker, Space,ConfigProvider,Form, Input ,message
,Select ,Switch,Modal,Button ,Table,TimePicker,Checkbox,Radio,Row,Col ,Typography} from 'antd';

import locale from 'antd/locale/zh_CN';

import { PlusOutlined,RedoOutlined,ExclamationCircleOutlined } from '@ant-design/icons';
import './index.css'
import http from '../../../server/server.js'
import electric from '../../system/subscriber/electric';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';

dayjs.extend(customParseFormat);

interface EditableCellProps extends React.HTMLAttributes<HTMLElement> {
  editing: boolean;
  dataIndex: string;
  title: any;
  inputType: 'number' | 'text';
  record: Item;
  index: number;
  children: React.ReactNode;
}

const EditableCell: React.FC<EditableCellProps> = ({
  editing,
  dataIndex,
  title,
  inputType,
  record,
  index,
  children,
  ...restProps
}) => {
	const inputNode = inputType === 'number' ? <Input /> : <Input />;

	return (
		<td {...restProps}>
		  {editing ? (
			<Form.Item
			  name={dataIndex}
			  style={{ margin: 0 }}
			  rules={ [{required: true, pattern: new RegExp(/^(?:1000000|\d{1,6})(?:\.\d)?$/),message: '请输入正确范围内的数字' }]}
			>
			  {inputNode}
			</Form.Item>
		  ) : (
			children
		  )}
		</td>
  );
};

const { Search } = Input;
const { Option } = Select;
const {confirm}  = Modal;
const tailLayout = {
  wrapperCol: { offset: 8, span: 16 },
};
const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};
const formItemLayoutWithOutLabel = {
  wrapperCol: {
    xs: { span: 24, offset: 6 },
    sm: { span: 24, offset: 4 },
  },
};
const formItemLayout1 = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 4 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 20 },
  },
};
const format = 'HH:mm';
const dateFormat = 'YYYYMMDD';
const timeFormat = 'HHmm';
// const { confirm } = Modal;
const data = ['option1', 'option2', 'option3'];
const Responsetask= () => {	
	const [dataSource, setDataSource] = useState([]);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [editVisible, setEditVisible] = useState(false);
	const [addVisible, setAddVisible] = useState(false);
	const [page, setPage] = useState(1);
	const [edit, setEdit] = useState(0); // 0为编辑 1为新增
	const [respId, setRespId] = useState('');
	const [editDataSource, setEditDataSource] = useState([]);
	const [nodeList, setNodeList] = useState([]);
	const [nodeSystemList, setNodeSystemList] = useState([]);
	const [nodeId, setNodeId] = useState('');
	const [systemId, setSystemId] = useState('');
	const [deviceList, setDeviceList] = useState([]);
	const [nodeName, setNodeName] = useState('');
	const [systemName, setSystemName] = useState('');
	const [deviceName, setDeviceName] = useState('');
	const [deviceId, setDeviceId] = useState('');
	const [value, setValue] = useState('');
	const [deviceNames, setDeviceNames] = useState('');
	const [deviceRatedPower, setDeviceRatedPower] = useState(''); // 额定负荷
	const [sstatus, setSstatus] = useState(''); // 执行策略
	const [sId, setSId] = useState('');
	const [actualLoad, setActualLoad] = useState('');
	const [open1, setOpen1] = useState('');
	const [xiantime, setXiantime] = useState('');
	const [respLoad, setRespLoad] = useState('');
	const [respMode, setRespMode] = useState(''); // 1为约定响应，2为实时响应
	const [loading, setLoading] = useState(false);
	const [alltotal, setAllTotal] = useState(0);
	const [deviceload, setDeviceload] = useState(false);
	const [income, setIncome] = useState('');
	const [proceeds, setProceeds] = useState('');
	const [dstatus, setDStatus] = useState('');
	const [total, setTotal] = useState('');
	const [pageinfo, setPageInfo] = useState(1);
	const [totaltab, setTotalTab] = useState('');
	const [newValue, setNewValue] = useState(1);
	const [feedbackTimeSort, setFeedbackTimeSort] = useState(2); // 反馈截止排序1-升序 2-降序
	const [profitSort, setProfitSort] = useState(''); // 收益排序 1-升序 2-降序
	const [respLevelSort, setRespLevelSort] = useState(''); // 响应级别排序 1-升序 2-降序
	const [respSubsidySort, setRespSubsidySort] = useState(''); // 响应补贴排序 1-升序 2-降序
	const [respTypeSort, setRespTypeSort] = useState(''); // 响应类型排序 1-升序 2-降序
	const [rsTimeSort, setRsTimeSort] = useState(2); // 响应时段排序 1-升序 2-降序
	const [taskCode, setTaskCode] = useState(''); // 任务编码
	const [datatime, setDatatime] = useState('');
	const [rsDate, setRsDate] = useState(''); // 响应日期
	const [rsTime, setRsTime] = useState(''); // 响应开始时段,格式HH:mm
	const [feedbackTime, setFeedbackTime] = useState('');
	const [confirmLoading, setConfirmLoading] = useState(false);
	const [editingKey, setEditingKey] = useState('');
	const [editDeclareStatus, setEditDeclareStatus] = useState(4);
	const [nodeAllList, setNodeAllList] = useState([]);
	const [selectedOptions, setSelectedOptions] = useState([]);
	const [countTime, setCountTime] = useState(dayjs().format('YYYY-MM-DD HH:mm'));
	const [taskCodeNum, setTaskCodeNum] = useState(1); // 1为暗色，0为白色
	const [secondCity,setSecondCity]  = useState('');
	const [visabled,setVisabled] = useState(false)
	// const [myForm] = useForm();
	// const myForm = useRef(null);
	const [myForms] = Form.useForm();
	const [myForm] = Form.useForm();
	const [addmyForm] = Form.useForm();
	const history= useHistory()
	useEffect(() =>{
		getTaskList()
		getNodeList()
		const interval = setInterval(() => {
			setCountTime(dayjs().format('YYYY-MM-DD HH:mm'))
		}, 1000); // 间隔为1秒
		return () => {
		    clearInterval(interval);
		};
	},[])

	// 查询节点列表信息
	const getNodeList =() =>{
		http.get('demand_resp/resp_task/getNodeList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				console.log(res.data.data)
				const nodesArray = [];
				const nodesData = res.data.data
				if(nodesData){
					Object.keys(nodesData).forEach(key => { nodesArray.push(nodesData[key]); });
					console.log(nodesArray);
					
					setNodeAllList(nodesArray)
				}else{
					
					setNodeAllList(res.data.data)
				}
				
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 查询任务列表    
	const getTaskList=() =>{
		setLoading(true)
		http.post('demand_resp/resp_task/getTaskList',{
			"number": page,
			"pageSize": 10,
			"startDate":'',
			"endDate":'',
			"feedbackTimeSort":feedbackTimeSort	,//反馈截止排序1-升序 2-降序
			"profitSort":profitSort,	//收益排序 1-升序 2-降序
			"respLevelSort":respLevelSort,	//响应级别排序 1-升序 2-降序
			"respSubsidySort":respSubsidySort,	//响应补贴排序 1-升序 2-降序
			"respTypeSort":respTypeSort,	//响应类型排序 1-升序 2-降序
			"rsTimeSort":rsTimeSort,	//响应时段排序 1-升序 2-降序
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				if(content.length>0){
					content.map(res =>{
						res.key = res.respId
					})
				}
				setDataSource(content);
				setLoading(false)
				setTotalTab(res.data.data.totalElements)
			}else{
				setDataSource([])
				setLoading(false)
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 录入服务任务
	const enter =() =>{
		setEdit(1)
	}
	const handleOk = () => {
		setIsModalVisible(false)
	};
	
	const handleCancel = () => {
		setIsModalVisible(false)
		setConfirmLoading(false)
		setSelectedOptions([])
		myForm.resetFields()
	};
	// 自动获取任务
	const obtain =() =>{
		http.get('demand_resp/resp_task/getTask').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				message.success('成功')
			}else{
				message.success(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 搜索
	const onSearch =(e) =>{
		console.log(e)
		setDeviceNames(e);
		setDeviceload(true);
		http.post('demand_resp/resp_task/getDeviceListByName',{
			"respId": respId,
			"deviceName": deviceNames,
			"number": pageinfo,
			"pageSize": 10
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				setEditDataSource(content)
				setDeviceload(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const onsearcbtn =() =>{
		http.post('demand_resp/resp_task/getDeviceListByName',{
			"respId": respId,
			"deviceName": deviceNames,
			"number": pageinfo,
			"pageSize": 10
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				
				setEditDataSource(content)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 添加设备取消
	const addCancel =() =>{
		setAddVisible(false)
		addmyForm.resetFields()
	}
	// 添加设备
	const adddevices1 =() =>{
		getNewnodeList()
		setAddVisible(true)
	}
	// 添加设备取消
	const addeCancel =() =>{
		addmyForm.resetFields()
		setAddVisible(false)
	}
	// 编辑
	const edits =(e) =>{
		console.log(e)
		
		// rsTime
		http.post('demand_resp/resp_task/getDemandNodeList?respId='+e.respId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				console.log()
				let data = res.data.data
				const keysArray = Object.keys(data);
				const commonValues = nodeAllList.filter(obj => keysArray.includes(obj.noHouseholds));
				console.log('Common values array: ', commonValues);
				let newcommonValues = []
				commonValues.map(res =>{
					newcommonValues.push(res.nodeName)
				})
				
				const str = e.rsTime;
				const index = str.indexOf(" "); // 
				const rsTime = str.substring(index + 1); // 
				console.log(rsTime); 
				let reTime1 = e.reTime
				const index1 = str.indexOf(" "); // 
				const reTime = reTime1.substring(index1 + 1); // 
				console.log(reTime)
				setEdit(0);
				setRespId(e.respId);
				setIsModalVisible(true)
				setSelectedOptions(newcommonValues)
				setTaskCodeNum(0)
				myForm.setFieldsValue({
					demandrespLoad: e.respLoad,
					respSubsidy: e.respSubsidy,				
					assDate: dayjs(e.rsDate,'YYYY-MM-DD'),
					assTime: [dayjs(rsTime, 'HH:mm'), dayjs(reTime, 'HH:mm')],
					respType:e.respType,
					respMode:e.respMode,
					taskCode: e.taskCode,
					respLevel:e.respLevel,
					node:newcommonValues,
					feedbackTime: e.feedbackTime==null?undefined:dayjs(e.feedbackTime,'YYYY-MM-DD HH:mm:ss'),
				})
			}
		}).catch(err =>{
			console.log(err)
		})
		
		
	}
	// 删除
	const delet =(e) =>{
		console.log(e)
		if(dayjs(countTime,'YYYY-MM-DD HH:mm') >dayjs(e.rsTime,'YYYY-MM-DD HH:mm')){
			message.info('该相应任务已开始，不可删除！')
		}else{
			let that = this
			confirm({
				title: '提示',
				icon: <ExclamationCircleOutlined />,
				content: '确定要删除吗？',
				cancelText:'取消',
				okText:'确定',
				cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
				onOk() {
					console.log('OK');
					http.post('demand_resp/resp_task/delTask?respId=' +e.respId).then(res =>{
						console.log(res)
						if(res.data.code ==200){
							message.success('删除成功')
							setPage(1)
							getTaskList()
							
						}else{
							message.info(res.data.msg)
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
		
	}
	// 策略详情
	const detail =(e) =>{
		console.log(e,'-----------------')
		// this.props.history.push({
		// 	pathname: '/Responsdeatil',
		// 	query:e
		// 	// state: {
		// 	// 	nodeId: this.state.nodeId,
		// 	// 	title:'种植树木',
		// 	// 	startTime:this.state.startTime,
		// 	// 	endTime:this.state.endTime								  
		// 	// }
		// });
		history.push({
		    pathname: "/Responsdeatil",
		    state:{ query: e }
		});
	}
	// 编辑策略
	const editCancel =() =>{
		setEditVisible(false)
		setDeviceload(true)
	}
	// 时间差
	const timeDifference=(startTime,endTime) =>{ 
	    console.log("时间差==="+income);
	
	}
	// 策略查看
	const getDeviceListByName=() =>{
		http.post('demand_resp/resp_task/getDeviceListByName',{
			"respId": respId,
			"deviceName": deviceNames,
			"number": pageinfo,
			"pageSize": 10
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				setEditDataSource(content)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 查看
	const lookover =(e) =>{
		console.log(e)
		setDStatus(e.dstatus)
		var start1=e.rsTime.split(":");
		var startAll=parseInt(start1[0]*60)+parseInt(start1[1]);
		
		var end1=e.reTime.split(":");
		var endAll=parseInt(end1[0]*60)+parseInt(end1[1]);
		let all = Number(endAll-startAll)/60
		console.log(all)
		// this.setState({
			// in
		 // var date3 =e.reTime-e.rsTime //时间差的毫秒数  
		 // console.log(date3)
		setEditVisible(true);
		setRespId(e.respId);
		setXiantime(e.rsTime +'~' +e.reTime);
		setRespLoad(e.respLoad);
		setRespMode(e.respMode);
		setDeviceload(true);
		setIncome(all);
		http.post('demand_resp/resp_task/getDeviceListByName',{
			"respId": e.respId,
			"deviceName": deviceNames,
			"number": pageinfo,
			"pageSize": 10000
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				let num = 0
				for(var i=0;i<content.length;i++){
					num += content[i].deviceRatedPower
					// sum += array[i]
				}
				// console.log(income)
				let proceeds = Number(num*income*e.respSubsidy).toFixed(2)
				console.log(proceeds)
				
				setAllTotal(num);
				setProceeds(proceeds)
			}
		}).catch(err =>{
			console.log(err)
		})
		http.post('demand_resp/resp_task/getDeviceListByName',{
			"respId": e.respId,
			"deviceName": deviceNames,
			"number": pageinfo,
			"pageSize": 10
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				setEditDataSource(content);
				setDeviceload(false);
				setTotal(res.data.data.totalElements)
			}
		}).catch(err =>{
			console.log(err)
		})
		
		
	}
	// 节点
	const getNewnodeList=() =>{
		
		http.post('system_management/node_model/nodeList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				setNodeList(res.data.data)
			}
		}).catch(err =>{
			
		})
	}
	// 选择节点
	const chosenode =(val) =>{
		console.log(val)
		let nodeSystemList = []
		// if()
		for(var i=0;i<nodeList.length;i++){
			if(val ==nodeList[i].id){
				setNodeName(nodeList[i].name)
			}
		}
		setNodeId(val)
		http.post('system_management/node_model/nodeSystemList?nodeId='+val).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				console.log(res.data.data[0].id)
				data.map(res =>{
					nodeSystemList.push({
						id:res.id,
						systemName:res.systemName
					})
				})
				setVisabled(true)
				setNodeSystemList(nodeSystemList)
				setSecondCity('')
				addmyForm.setFieldsValue({
					system:'',
					devices:''
				})
				
			}
		})
	}
	// 选择系统
	const chosesystem =(e) =>{
		console.log(e)
		for(var i=0;i<nodeSystemList.length;i++){
			if(e ==nodeSystemList[i].id){
				setSystemName(nodeSystemList[i].systemName)
			}
		}
		setSystemId(e)
		http.post('system_management/device_model/deviceList?nodeId='+nodeId +'&systemId='+e).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				
				setDeviceList(res.data.data);
				addmyForm.setFieldsValue({
					devices:''
				})
			}
		})
	}

	// deviceId
	// 选择设备
	const chosedevice =(e) =>{
		console.log(e)
		
		setDeviceId(e)
		for(var i=0;i<deviceList.length;i++){
			if(e ==deviceList[i].deviceId){
				setDeviceName(deviceList[i].deviceName)
				setDeviceRatedPower(deviceList[i].deviceRatedPower)
			}
		}
		
	}
		
	
	// 添加响应任务
	const addTask =() =>{
		myForm.resetFields()
		setIsModalVisible(true)
		setEdit(1)
		setTaskCodeNum(1)
	}
	// 时间选择
	const timeChange =(val,time) =>{
		console.log(val,time)
		console.log(val[0].format(timeFormat))
		console.log(val[0].format(timeFormat)+val[1].format(timeFormat))
		setTaskCode(val[0].format(timeFormat)+val[1].format(timeFormat));
		setTaskCodeNum(0);
		
		if(datatime){
			myForm.setFieldsValue({
				taskCode:datatime+val[0].format(timeFormat)+val[1].format(timeFormat)
			})
		}
	}
	// 日期选择
	const dateChange =(val,time) =>{
		console.log(val.format(dateFormat))
		console.log(taskCode)
		if(taskCode){
			myForm.setFieldsValue({
				taskCode:val.format(dateFormat)+taskCode
			})
		}else{
			
		}
		setRsDate(time);
		setDatatime(val.format(dateFormat))
		
	}
	// 反馈截止
	const TYimeonChange =(value,dateString) =>{
		console.log(value,dateString)
		setFeedbackTime(dateString)
	}
	// 申报
	const ploy =(e) =>{
		// history.push({
		// 	// pathname: '/ploy',
		// 	pathname: '/Task',
		// 	query:e
		// })
		history.push({
		    pathname: "/Task",
		    state:{ query: e }
		});
		// history.push('/Task', { query: e });

	}
	// 运行策略
	const tactful =(e) =>{
		console.log(e)
		this.props.history.push({ pathname: '/tactful', state: { respId: e.respId} });
		// this.props.history.push({'/tactful',state: { respId:e.respId}})
	}
	// 编辑价格
	// editstrategy =(e) =>{
	// 	console.log(e)
	// }
	// 是否参加
	const participate = (e) =>{
		console.log(e)
		confirm({
		    title: '确定要改为不参加吗',
		    icon: <ExclamationCircleOutlined />,
		    // content: 'Some descriptions',
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    onOk() {
				console.log('OK');
				const params = new URLSearchParams();
				params.append("respId", e.respId);
				params.append("declareStatus", 4);
				http.post('demand_resp/resp_task/editDeclareStatus',params,{
					
				}).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('设置成功')
						getTaskList()
					}else{
						message.info(res.data.msg)
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
	// import * as _ from 'lodash';  // lodash的深拷贝函数_.cloneDeep
	// 出清
	const Clear =(e) =>{
		console.log(e)
		http.post('demand_resp/resp_task/updateClearing?respId='+e.respId).then(res =>{
			console.log(res)
			if(res.data.code==200){
				message.success('出清成功')
				getTaskList()
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	

		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current && current < dayjs().subtract(1, 'day'); 
		};
		const isEditing = (record: Item) => record.key === editingKey;
		
		const editstrategy = (record: Partial<Item> & { key: React.Key }) => {
		   myForms.setFieldsValue({ respSubsidy: '', ...record });
		    setEditingKey(record.key);
		};
		
		const cancel = () => {
		    setEditingKey('');
		};
		
		const save = async (key: React.Key) => {
		    try {
				const row = (await myForms.validateFields());
		
				const newData = [...dataSource];
				const index = newData.findIndex(item => key === item.key);
				if (index > -1) {
					const item = newData[index];
					newData.splice(index, 1, {
					  ...item,
					  ...row,
					});
					// alert(0)
					// setData(newData);
					// setEditingKey('');
					console.log(row)
					console.log(item)
					http.post('demand_resp/resp_task/editTaskPrice',{
						"feedbackTime": item.feedbackTime,
						"reTime": item.reTime,
						"respId": item.respId,
						"respLevel": item.respLevel,
						"respLoad": item.respLoad,
						"respSubsidy": row.respSubsidy,
						"respType": item.respType,
						"rsDate": item.rsDate,
						"rsTime": item.rsTime,
						"taskCode": item.taskCode,
					}).then(res =>{
						console.log(res)
						if(res.data.code==200){
							message.success('成功')
							getTaskList()
						}else{
							message.info(res.data.msg)
						}
					})
					setEditingKey('')
					setDataSource(newData)
				} else {
					newData.push(row);
					
					setEditingKey('')
					setDataSource(newData)
				}
		    } catch (errInfo) {
		      console.log('Validate Failed:', errInfo);
		    }
		};
		const columns= [
			{
			     title: '序号',
			     width: 60,
			    render:(value, item, index) => (page - 1) * 10 + index+1,
			},
			{
				title: '任务编码',
				dataIndex: 'taskCode',
				key: 'taskCode',
				
				// render: text => {text},
			},
			{
				title: '响应时段',
				dataIndex: 'rsDate',
				key: 'rsDate',
				sorter: true,
				width:270,
				render: (s, record, index) =>{
					// console.log(record)
					return record.rsTime+'~' +record.reTime
				}
			},
			{
				title: '反馈截止',
				dataIndex: 'feedbackTime',
				key: 'feedbackTime',
				sorter: true,
			},
			{
				title: '负荷需求(kW)',
				dataIndex: 'respLoad',
				key: 'respLoad',
				width: 90,
				render:(value, item, index) =>{
					if(value==null||value==undefined||value===""||value=='-'){
						return '-'
					}else{
						return Number(value).toFixed(2)
					}
					
				}
			},
			{
				title: '响应类型',
				dataIndex: 'respType',
				key: 'respType',
				width:100,
				render: (text,record,_,action) =>{
					if(record.respType ==1){
						return '削峰响应'
					}else if(record.respType ==2){
						return '填谷响应'
					}
				}
			},
			{
				title: '响应级别',
				dataIndex: 'respLevel',
				key: 'respLevel',
				width:100,
				render: (text,record,_,action) =>{
					if(text ==1){
						return '日前响应'
					}else if(text ==2){
						return '小时响应'
					}else if(text ==3){
						return '分钟响应'
					}else if(text ==4){
						return '秒级响应'
					}
				}
			},
			{
				title: '响应补贴(元/kWh)',
				dataIndex: 'respSubsidy',
				key: 'respSubsidy',
				editable: true,
				width: 90,
				render:(value, item, index) =>{
					if(value==null||value==undefined||value===""||value=='-'){
						return '-'
					}else{
						return Number(value).toFixed(2)
					}
					
				}
			},
			{
				title: '状态',
				dataIndex: 'declareStatus',
				key: 'declareStatus',
				width:100,
				render: (text,record,_,action) =>{
					if(!record.declareStatus){
						if(record.dstatus === 1){
							return '待申报'
						}
						if(record.dstatus === 2){
							return '执行中'
						}
						if(record.dstatus === 3){
							return '已完成'
						}
						if(record.dstatus === 4 || record.dstatus === 0){
							return '不参加'
						}
					}

					if(!record.dstatus){
						if(record.declareStatus === 1){
							return '待申报'
						}
						if(record.declareStatus === 2){
							return '待出清'
						}
						if(record.declareStatus === 3){
							return '已出清'
						}  
					}

					if(record.declareStatus){
						if(record.declareStatus === 1 && record.dstatus === 1){
							return '待申报'
						}
						if(record.declareStatus === 2 && record.dstatus === 1){
							return '待出清'
						}
						if(record.declareStatus === 3 && record.dstatus === 1){
							return '已出清'
						}
						if(record.declareStatus === 3 && record.dstatus === 2){
							return '执行中'
						}
						if(record.declareStatus === 3 && record.dstatus === 3){
							return '已完成'
						}
						if(record.dstatus === 4 || record.dstatus === 0){
							return '不参加'
						}
					}

					return '不参加'
				}
				// render: (text,record,_,action) =>{
				// 	if(record.drsStatus==4){
				// 		if(record.declareStatus==3){
				// 			return <span>出清成功</span>
				// 		}else{
				// 			return <span>未参加</span>
				// 		}
				// 	}else if(record.dstatus==1){
				// 		// 参加
				// 		if(record.declareStatus==3){
				// 			return <span>出清成功</span>
				// 		}else{
				// 			return <span>已出清</span>
				// 		}
				// 	}
				// 	// return <Space size="middle">
				// 	// 	{record.declareStatus==3?'出清成功':'-'}
						
				// 	// </Space>
				// }

			},
			// {
			// 	title: '运行策略',
			// 	dataIndex: 'address',
			// 	key: 'address',
			// 	width:100,
			// 	render: (text,record,_,action) =>{
			// 		return <Space size="middle">
						
			// 				<Button style={{padding:0,color:'#0092FF'}} type="link" disabled={record.dstatus==4?true:record.declareStatus==3?true:false} onClick={() => this.tactful(record)}>
			// 			      编辑
			// 			    </Button>
			// 		</Space>
			// 	}
			// },
			
			{
				title: '操作',
				key: 'action',
				width:200,
				// render: (text,record,_,action) =>{
				// 	return <Space size="middle">
				// 		<a onClick={() => this.ploy(record)}>申报</a>
				// 		<a onClick={() => this.detail(record)}>详情</a>
				// 		<a onClick={() => this.editstrategy(record)}>编辑</a>
				// 		<a onClick={() => this.delet(record)}>删除</a>
				// 	</Space>
					
				// }
				render: (_: any, record: Item) => {
					const editable = isEditing(record);
					return editable ? (
						<span>
							<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
							  确定
							</Typography.Link>
							
							<Typography.Link onClick={() => cancel(record.key)} >
							  取消
							</Typography.Link>
						</span>
					) : (
						<span className="ployspan">
							<span  disabled={record.declareStatus==3?true:record.dstatus==4?true:false} onClick={() =>ploy(record)}>
							    申报
							</span>
							<span  onClick={() => edits(record)}>
								编辑
							</span>
							<span  onClick={() => detail(record)}>
								详情
							</span>
							<span   disabled={record.dstatus==4?true:record.declareStatus==3?true:false} onClick={() => participate(record)}>
								不参加
							</span>
							<span style={{display:record.declareStatus==2?'inline-block':'none'}} onClick={() => Clear(record)}>
							    出清
							</span>
							<span style={{display: dayjs(countTime,'YYYY-MM-DD HH:mm') < dayjs(record.rsTime,'YYYY-MM-DD HH:mm')?'inline-block':'none'}} onClick={() => delet(record)}>删除</span>
						</span>
					);
				},
			},
		];

		const onFinish = (values: any) => {
		    console.log('Success:', values);
			// console.log(values.assTime[0].format('HHmm'))
			let taskCode ;
			if(values.assTime){
				// alert(0)
				let startdate = values.assDate.format('YYYYMMDD')
				let start = values.assTime[0].format('HHmm')
				let end = values.assTime[1].format('HHmm')
				taskCode = startdate+start+end
				console.log(taskCode)
			}
			setConfirmLoading(true)
			if(edit ==0){
				// 编辑
				const commonValues = nodeAllList.filter(obj => values.node.includes(obj.nodeName));
				console.log('Common values array: ', commonValues);
				let newName = []
				commonValues.map(res =>{
					newName.push(res.noHouseholds)
				})
				let rsTime = values.assDate.format('YYYY-MM-DD') + ' ' +values.assTime[0].format('HH:mm')
				console.log(rsTime)
				if(feedbackTime>rsTime){
					setConfirmLoading(false)
					message.info('反馈时间不能超过响应开始时间')
					return
				}
				// console.log(newName)
				http.post('demand_resp/resp_task/editTask',{
					"feedbackTime":values.feedbackTime.format('YYYY-MM-DD HH:mm:ss'),
					"reTime": values.assTime==null ||undefined ||''?'':values.assDate.format('YYYY-MM-DD') + ' ' +values.assTime[1].format('HH:mm'),
					"rsTime": values.assTime==null ||undefined ||''?'':values.assDate.format('YYYY-MM-DD') + ' ' +values.assTime[0].format('HH:mm'),
					"respId": respId,
					"inviteRange":newName.toString(),
					"respLevel":values.respLevel,	//响应级别
					"respLoad": values.demandrespLoad!==''?Number(values.demandrespLoad):'',	//响应负荷
					"respSubsidy": values.respSubsidy!==''?Number(values.respSubsidy):'',	//响应补贴
					"respType": values.respType,	//响应类型
					"rsDate": values.assDate.format('YYYY-MM-DD'),	//响应日期
					"taskCode": parseInt(taskCode),	//任务编码
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						
						myForm.resetFields()
						setIsModalVisible(false)
						setConfirmLoading(false)
						getTaskList()
						setSelectedOptions([])
						message.success('编辑成功');
						
					}else{
						message.success(res.data.msg)
						setConfirmLoading(false)
					}
				}).catch(err =>{
					console.log(err)
				})
			}else if(edit ==1){
				// 新增
				const commonValues = nodeAllList.filter(obj => values.node.includes(obj.nodeName));
				console.log('Common values array: ', commonValues);
				let newName = []
				commonValues.map(res =>{
					newName.push(res.noHouseholds)
				})
				let rsTime = values.assDate.format('YYYY-MM-DD') + ' ' +values.assTime[0].format('HH:mm')
				console.log(rsTime)
				if(feedbackTime>rsTime){
					setConfirmLoading(false)
					message.info('反馈时间不能超过响应开始时间')
					return
				}

				http.post('demand_resp/resp_task/addTask',{
					"feedbackTime":feedbackTime,		//反馈截止时间
					"reTime": values.assDate.format('YYYY-MM-DD') + ' ' +values.assTime[1].format('HH:mm'),	//响应结束时段
					"inviteRange":newName.toString(),
					"respId": "",
					"respLevel":values.respLevel,	//响应级别
					"respLoad": values.demandrespLoad!==''?Number(values.demandrespLoad):'',	//响应负荷
					"respSubsidy": values.respSubsidy!==''?Number(values.respSubsidy):'',	//响应补贴
					"respType": values.respType,	//响应类型
					"rsDate": values.assDate.format('YYYY-MM-DD'),	//响应日期
					"rsTime":values.assDate.format('YYYY-MM-DD') + ' ' +values.assTime[0].format('HH:mm'),	//响应开始时段,
					"taskCode": parseInt(taskCode),	//任务编码
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						message.success('成功')
						
						
						setIsModalVisible(false)
						setConfirmLoading(false)
						myForm.resetFields()
						setSelectedOptions([])
						getTaskList()
						
						
					}else{
						message.info(res.data.msg)
						setConfirmLoading(false)
					}
				}).catch(err =>{
					console.log(err)
				})
			}
			
		};
		
		const onFinishFailed = (errorInfo: any) => {
		    console.log('Failed:', errorInfo);
		};
		// 添加设备
		const addonFinish = (values) =>{
			console.log('Success:', values);
			http.post('demand_resp/resp_task/addStrategy',{
				"actualLoad": 0,
				"deviceId": values.devices,
				"deviceName": deviceName,
				"deviceRatedPower": deviceRatedPower,
				"nodeId": values.nodeId,
				"nodeName": nodeName,
				"respId": respId,
				"sid": "",
				"dstatus": 0,
				"systemId": values.system,
				"systemName": systemName,
			}).then(res =>{
				console.log(res)
				if(res.data.code == 200){
					message.success('添加设备成功')
					
					setAddVisible(false)
					http.post('demand_resp/resp_task/getDeviceListByName',{
						"respId": respId,
						"deviceName": deviceNames,
						"number": pageinfo,
						"pageSize": 10
					}).then(res =>{
						console.log(res)
						if(res.data.code ==200){
							let content = res.data.data.content
							setEditDataSource(content)
						}
					}).catch(err =>{
						console.log(err)
					})
					addmyForm.resetFields()
					// addmyForm
				}else{
					message.info(res.data.msg)
				}
			})
			
		}
		const onChange: PaginationProps['onChange'] = page => {
		    console.log(page);
			
			setPageInfo(page.current)
			getDeviceListByName()
		};
		
		const onChangetab = (pagination, filters, sorter) => {
			console.log(pagination, filters, sorter)
		    
			//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
			// if (sorter.order) {
				if(sorter.columnKey=='rsDate'){
					// 响应时段排序
					if (sorter.order) {
						//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
						
							if(sorter.order == "ascend"){
								// 升序
								setRsTimeSort(1)
								setFeedbackTimeSort('')
								getTaskList()
								// setRs
							}else if(sorter.order == "descend"){
								// 降序
								setRsTimeSort(2)
								setFeedbackTimeSort('')
								getTaskList()
							}
						
						
					}else{
						// alert(1)
						setRsTimeSort(2)
						setFeedbackTimeSort('')
						getTaskList()
							
						
					}
				}
				if(sorter.columnKey=='feedbackTime'){
					// 反馈截止排序
					if (sorter.order) {
						//当前处于升序或者降序 sorter.order == "descend" ? '降序' : '升序'
						if(sorter.order == "ascend"){
							// 升序
							setFeedbackTimeSort(1);
							setRsTimeSort('');
							getTaskList();
						}else if(sorter.order == "descend"){
							// 降序
							setFeedbackTimeSort(2);
							setRsTimeSort('');
							getTaskList();
						}
					}else{
						setFeedbackTimeSort(2);
						setRsTimeSort('');
						getTaskList();
					}
				}	
			// }
			
			
		}
		// 排序
		const onChange1 = (e) => {
		    console.log('radio checked', e.target.value);
			setNewValue(e.target.value)
		    // setValue(e.target.value);
		};
		// 分页
		const handlePagination = page => {
			setPage(page)
			getTaskList()
			
		};
		const mergedColumns = columns.map(col => {
			if (!col.editable) {
				return col;
			}
			return {
				...col,
				onCell: (record: Item) => ({
					 record,
					 inputType: col.dataIndex === 'age' ? 'number' : 'text',
					 dataIndex: col.dataIndex,
					 title: col.title,
					 editing: isEditing(record),
			   }),
					};
		});
		
		const handleSelectAll = () => {
		    // setSelectedOptions(data);
			let nodeAllListOne = []
			nodeAllList.map(res =>{
				// console.log(res.noHouseholds)
				nodeAllListOne.push(res.nodeName)
			})
			console.log(nodeAllListOne)
			
			setSelectedOptions(nodeAllListOne)
			myForm.setFieldsValue({
				node:nodeAllListOne
			})
		};
		
		const handleDeselectAll = () => {
		    // setSelectedOptions([]);
			setSelectedOptions([])
			myForm.setFieldsValue({
				node:[]
			})
		};
		
		const handleChange = (option) => {
			console.log(option)
			console.log(selectedOptions)
		    if (selectedOptions.includes(option)) {
		      // setSelectedOptions(selectedOptions.filter(item => item !== option));
				console.log(selectedOptions.filter(item => item !== option))
				setSelectedOptions(selectedOptions.filter(item => item !== option))
				myForm.setFieldsValue({
					node:selectedOptions
				})
		    } else {
				setSelectedOptions(prevOptions => {
				    const updatedOptions = [...prevOptions, option];
				    myForm.setFieldsValue({ node: updatedOptions });
				    return updatedOptions;
				});
		    }
		  };

		const dropdownMenu = (
		    <div className="dropdownMenu-scroll">
				<Checkbox  checked={selectedOptions.length === nodeAllList.length} indeterminate={selectedOptions.length > 0 && selectedOptions.length < nodeAllList.length} onChange={e => (e.target.checked ? handleSelectAll() : handleDeselectAll())}>全部</Checkbox>
				{nodeAllList.map(option => (
					<Checkbox style={{display:'block'}} key={option.nodeName} checked={selectedOptions.includes(option.nodeName)} onChange={() => handleChange(option.nodeName)}>{option.nodeName}</Checkbox>
				))}
		    </div>
		  );
		const nodeChange =(val) =>{
			console.log(val)
			setSelectedOptions(val)
		}
		const onOk = (value,mode) => {
			console.log('onOk: ', value,mode);
		};
		return (
			<div className="Responsetask">
				<div className="ServiceTaskheader">
					
					<Button type="primary" icon={<PlusOutlined />} onClick={addTask} >添加响应任务</Button>					
				</div>
				<div>
				<Form  component={false}>
					<Table dataSource={dataSource}
						columns={mergedColumns} 
						loading={loading}
						rowKey={record => record.respId}
						// onChange={onChangetab}
						onChange={(pagination, filters, sorter) => { onChangetab(pagination, filters, sorter) }}
						pagination={
							{
								total: totaltab,//数据的总条数
								defaultCurrent: 1,//默认当前的页数
								defaultPageSize: 10,//默认每页的条数
								showSizeChanger:false,
								onChange: handlePagination,
							}
						}
						components={{
							body: {
								cell: EditableCell,
							},
						}}
						
						rowClassName="editable-row"
						
					
				   />
				</Form>
					

				</div>
				<ConfigProvider locale={locale}>
					
					<Modal title={edit==0?'编辑':'添加响应任务'} visible={isModalVisible} 
						maskClosable={false}
						footer={null}
						width={700}
						onCancel={handleCancel}>
						
						<Form
							name="basic"
							labelCol={{ span: 4 }}
							wrapperCol={{ span: 20 }}
							// initialValues={{ remember: true }}
							onFinish={onFinish}
							onFinishFailed={onFinishFailed}
							autoComplete="off"
							form={myForm}
							initialValues={{
								'respType': 1,
								'respLevel':1
							}}
						>
							<Form.Item
								label="任务编码"
								name="taskCode"
								// extra="命名规范：2022071313001600  编号规则：年月日开始结束时间任务编码根据响应时段自动生成"
							>
								<Input style={{color:taskCodeNum===1?'#8F959E':'#FFF'}} defaultValue="任务编码根据响应时段自动生成" bordered={false} disabled
								  
								  />
							</Form.Item>
							<Form.Item label="响应时段" required
								
								name="assDate">
								<Form.Item
									name="assDate"
									style={{ display: 'inline-block',width:'50%'}}
									rules={[{ required: true, message: '响应时段' }]}
								>
									<DatePicker onChange={dateChange} style={{ width: '100%'}} disabledDate={disabledDate} className="fotdate"   />
								</Form.Item>
							  
								<Form.Item style={{ display: 'inline-block',width: 'calc(50% - 16px)' }}
									name="assTime"
									rules={[{ required: true, message: '响应时段' }]}
								>
									<TimePicker.RangePicker  onChange={timeChange} style={{ width: 'calc(100% - 16px)',marginLeft:16}}   format={format} />
								</Form.Item>
							</Form.Item>
							
							<Form.Item
								label="反馈截止："
								name="feedbackTime"
								rules={[{ required: true, message: '请输入反馈截止' }]}
								
							>
								<DatePicker style={{width: '260px'}} onChange={TYimeonChange} className="fotdate" showTime  onOk={onOk} /> 
							</Form.Item>
							<Form.Item label="负荷需求" 
								required
							>
								<Form.Item name="demandrespLoad" style={{width: '223px',float:'left'}} 
								rules={ [{ required: true, message: '请输入负荷需求' },{pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}>
									<Input maxLength={50}   />
										
									
								</Form.Item>
								<div style={{display:'flex'}}>
									
									<span className="ant-form-text" style={{ marginLeft: 0 }}>
										kW
									</span>
								</div>
							</Form.Item>
							<Form.Item
								label="响应类型"
								name="respType"
								rules={[{ required: true, message: '请输入响应类型' }]}
								
							>
								<Select
									style={{width: '260px'}}
									placeholder="响应类型"
								>
									<Option value={1}>削峰响应</Option>
									<Option value={2}>填谷响应</Option>
								</Select>
							</Form.Item>
							<Form.Item
								label="参与节点："
								name="node"
								rules={[{ required: true, message: '请选择参与节点' }]}
								
							>
								<Select
									mode="multiple"
									style={{ width: '260px' }}
									placeholder="请选择参与节点"
									dropdownRender={() => dropdownMenu}
									value={selectedOptions}
									onChange={nodeChange}
								/>
								
							</Form.Item>
							<Form.Item
								label="响应级别"
								name="respLevel"
								rules={[{ required: true, message: '请输入响应级别' }]}
								
							>
								<Select
									style={{width: '260px'}}
									placeholder="响应级别"
								>
									<Option value={1}>日前响应</Option>
									<Option value={2}>小时响应</Option>
									<Option value={3}>分钟响应</Option>
									<Option value={4}>秒级响应</Option>
								</Select>
							</Form.Item>
							<Form.Item
								label="响应补贴"
								required
							>
								
								<Form.Item
									style={{width:'200px',float:'left'}}
									name="respSubsidy"
										// rules={ [{pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
									rules={ [{ required: true, message: '请输入响应补贴' },{ pattern: new RegExp(/^([0-9]{1,6}$)|(^[0-9]{1,6}说\.[0-9]{1,1}$)|1000000$/),message: '请输入正确范围内的数字' }]}
								>
									<Input maxLength={50} />
								</Form.Item>
								<div >
									
									<span className="ant-form-text"> 元/kWh</span>
								</div>
							</Form.Item>
							<Form.Item
								// label="备注"
								style={{display:edit==0?'block':'none'}}
								// formItemLayoutWithOutLabel
								// formItemLayout1
								// labelCol={{ span: 0 }}
								// wrapperCol={{ span: 20 }}
								wrapperCol={{
								  xs: { span: 24, offset: 3 },
								  // sm: { span: 24, offset: 4 },
								}}
							>
								<span className="comments">备注:对于已完成申报或已完成运行策略编辑的需求响应任务，再次编辑任务可能会导致响应任务执行失败</span>
							</Form.Item>
							<Form.Item {...tailLayout}  style={{textAlign:'right'}}>
								<Button ghost  onClick={handleCancel}>取消</Button>
								<Button loading={confirmLoading}  type="primary" htmlType="submit">
									确定
								</Button>
								
							</Form.Item>
						</Form>    
						
					</Modal>
					
					<Modal title="添加设备" visible={addVisible}
						footer={null}
						width={640}
						onCancel={addCancel}>
						<Form
							name="basic"
							labelCol={{ span: 8 }}
							wrapperCol={{ span: 12 }}
							onFinish={addonFinish}
							onFinishFailed={onFinishFailed}
							// ref="addmyForm"
							form={addmyForm}
						>
							<Form.Item
								label="节点"
								name="nodeId"
								rules={[{ required: true, message: '选择节点' }]}
							>
								<Select
									placeholder="选择节点"
									onChange={chosenode}
								>
									{
										nodeList.length &&nodeList.map(res =>{
											return <Option key={res.id} value={res.id}>{res.name}</Option>
										})
									}

							   </Select>
							</Form.Item>
							<Form.Item
								label="系统"
								name="system"
								rules={[{ required: true, message: '选择系统' }]}
							>
								<Select
									placeholder="选择系统"
									onChange={chosesystem}
									
								>
									{
										nodeSystemList.length&&nodeSystemList.map(item =>{
											return <Option value={item.id} key={item.id}>{item.systemName}</Option>
										})
									}
							   </Select>
							</Form.Item>
							<Form.Item
								label="设备"
								name="devices"
								rules={[{ required: true, message: '选择设备' }]}
							>
								<Select
									placeholder="选择设备"
									onChange={chosedevice}
								>
									{
										deviceList.length &&deviceList.map(item =>{
											return <Option value={item.deviceId} key={item.deviceId}>{item.deviceName}</Option>
										})
									}
							   </Select>
							</Form.Item>
							<Form.Item {...tailLayout} style={{textAlign:'right'}}>
								<Button ghost  onClick={addeCancel} type="primary" >
									取消
								</Button>
								<Button type="primary" htmlType="submit">
									确定
								</Button>
								
							</Form.Item>
						</Form>
					</Modal>
				</ConfigProvider>
			</div>
		)
	}
	


export default Responsetask
// <Button type="primary" icon={<RedoOutlined />} onClick={this.obtain} >自动获取任务</Button>