import React,{useEffect,useState} from 'react'
import { Link,useHistory  } from "react-router-dom"
import { Select, Space,Table,Button,Input,Form,Typography,Popconfirm,message,Modal } from 'antd';
import './ploy.scss'
import http from '../../../../server/server.js'
const { Search } = Input;

interface Item {
  key: string;
  name: string;
  age: number;
  address: string;
}

const originData: Item[] = [];
for (let i = 0; i < 100; i++) {
  originData.push({
    key: i.toString(),
    name: `Edrward ${i}`,
    age: 32,
    address: `London Park no. ${i}`,
  });
}
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
		  rules={ [{required: true, pattern: new RegExp(/^(?!1000000$)(?:\d{1,6}(?:\.\d{1,2})?|0\.\d{1,2}|[1-9]\d{0,5}(?:\.\d{1,2})?)$/),message: '请输入正确范围内的数字' }]}
          // rules={[
          //   {
          //     required: true,
          //     message: `Please Input ${title}!`,
          //   },
          // ]}
        >
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};


const { Option } = Select;
const data: DataType[] = [];
for (let i = 0; i < 46; i++) {
  data.push({
    key: i,
    name: `Edward King ${i}`,
    age: 32,
    address: `London, Park Lane no. ${i}`,
	drsId:i
  });
}
const Ploy =(props) =>{
	// console.log(props,'---------------')
	const {user} = props
	const [currentState, setCurrentState] = useState(0);
	const [selectedRowKeys, setSelectedRowKeys] = useState([]);
	const [dataSource, setDataSource] = useState([]);
	const [deviceName, setDeviceName] = useState('');
	const [deviceRatedPowerSort, setDeviceRatedPowerSort] = useState(1);
	const [deviceStatusSort, setDeviceStatusSort] = useState('');
	const [respId, setRespId] = useState(user.respId);
	const [strategyId, setStrategyId] = useState('');
	const [page, setPage] = useState(1);
	const [invitation, setInvitation] = useState('');
	const [totalDeclare, setTotalDeclare] = useState('');
	const [drsIds, setDrsIds] = useState([]);
	const [editingKey, setEditingKey] = useState('');
	const [data, setData] = useState([]);
	const [declareLoad, setDeclareLoad] = useState('');
	const [declarePrice, setDeclarePrice] = useState('');
	const [drsIdid, setDrsIdid] = useState('');
	const [taskCode, setTaskCode] = useState(user.taskCode);
	const [rsDate, setRsDate] = useState(user.rsDate + ' ' +user.rsTime+'~'+user.reTime);
	const [feedbackTime, setFeedbackTime] = useState(user.feedbackTime);
	const [respLoad, setRespLoad] = useState(user.respLoad);
	const [respSubsidy, setRespSubsidy] = useState(user.respSubsidy);
	const [respType, setRespType] = useState(user.respType==1?'削峰响应':'填谷响应');
	const [respLevel, setRespLevel] = useState(user.respLevel==1?'日前响应':user.respLevel==2?'小时响应':user.respLevel==3?'分钟响应':'秒级响应');
	const [editdisabled, setEditDisabled] = useState(false);
	const [loading, setLoading] = useState(false);
	const [pages, setPages] = useState(1);
	const [platformId, setPlatformId] = useState('');
	const [btnloading, setBtnLoading] = useState(false);
	const [sendloading, setSendLoading] = useState(false);
	const [loadingName, setLoadingName] = useState('发送');
	const [id, setId] = useState('');
	const [deviceInfo, setDeviceInfo] = useState([]);
	const [platId, setPlatId] = useState('');
	const [dataForm, setDataForm] = useState(true);
	const [osType, setOsType] = useState(sessionStorage.getItem('osType'));
	const [status, setStatus] = useState('');
	const [fields, setFields] = useState([]);
	const [total,setTotal] = useState('');
	const [currentNum,setCurrentNum] = useState(1);
	const [selectedRows,setSelectedRows] = useState([])
	const [form] = Form.useForm();
	const history= useHistory()
	useEffect(() =>{
		thirdPartyEnergyPlat()
	},[])
	useEffect(() =>{
		if(currentState==0){
			getDeclareList()
		}
	},[page,currentState])
	useEffect(() =>{
		if(currentState==1){
			getCSPGDeclareList()
		}
	},[pages,currentState])
	// 第三方智慧能源平台
	const thirdPartyEnergyPlat =() =>{
		http.post('system_management/systemParam/thirdPartyEnergyPlat?id='+'12').then(res =>{
			console.log(res)
			if(res.data.code==200){
				// thirdPartymyForm
				let status = res.data.data.status
				setCurrentState(status=='0'?1:0)
				setFields(res.data.data.param)
				setStatus(res.data.data.status)
			}else{
				// 资源类后台没部署时使用
				// this.getDeclareList()
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 选择策略和第三方
	const current1=(e) =>{
		console.log(e)

		setCurrentState(e)

		cancel()
		setSelectedRowKeys([])
		setPage(1)
		setCurrentNum(1)
		
	}
	// 查询申报负荷列表
	const getDeclareList =() =>{
		setLoading(true)
		http.post('demand_resp/resp_task/getDeclareList',{
			"deviceName": deviceName,
			"deviceRatedPowerSort": deviceRatedPowerSort,
			"deviceStatusSort": deviceStatusSort,
			"number": page,
			"pageSize": 5,
			"respId": respId,
			"sid": "",
			"strategyId": strategyId,
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				if(res.data.data.devieInfo){
					let data = res.data.data.devieInfo.content
					data.map((res,index) =>{
						return res.key = index
					})
					console.log(data)
					setData(res.data.data.devieInfo.content);
					setInvitation(res.data.data.invitation);
					setTotalDeclare(res.data.data.totalDeclare);
					setTotal(res.data.data.devieInfo.totalElements);
					setDataForm(true)
					setLoading(false)
				}else{
					setData([]);
					setInvitation(res.data.data.invitation);
					setTotalDeclare(res.data.data.totalDeclare);
					setTotal('');
					setLoading(false);
					setDataForm(false)
				}
				
			}else{
				message.info(res.data.msg)
				setData([]);
				setInvitation('');
				setTotalDeclare('');
				setTotal('');
				setLoading(false);
				setDataForm(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 编辑
	// const edit =(e) =>{
	// 	console.log(e)
	// }
	// 获取第三方系统能源平台-弃用
	const findSmartEnergySysParam =() =>{
		// let {id} = this.state
		http.post('demand_resp/resp_task/getSmartEnergySysParamList?sysParamKey='+'5').then(res =>{
			console.log(res)
			if(res.data.code==200){
				if(res.data.data){
					
					setDeviceInfo(res.data.data);
					thirdPlatformDeclareList()
				}
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 申报负荷提交
	const tactful =() =>{
		if(currentState==0){
			let drsIds = []
			selectedRows.map(res =>{
				console.log(res)
				drsIds.push(res.drsId)
			})
			console.log(drsIds)
			http.post('demand_resp/resp_task/declareSubmit?drsIds=' +drsIds+'&respId='+respId).then(res =>{
				console.log(res)
				if(res.data.code==200){
					message.success('成功')
					// history.push({ pathname: '/Responsetask' });
					getDeclareList()
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}else if(currentState==1){
			// this.thirdPlatformDeclareSubmit()
			let drsIds = []
			selectedRows.map(res =>{
				console.log(res)
				drsIds.push(res.drsId)
			})
			console.log(drsIds)
			http.post('demand_resp/resp_task/declareSubmit?drsIds=' +drsIds+'&respId='+respId).then(res =>{
				console.log(res)
				if(res.data.code==200){
					message.success('成功')
					// history.push({ pathname: '/Responsetask' });
					getCSPGDeclareList()
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
	}
	// 第三方智慧能源平台-查询申报负荷列表-弃用
	const thirdPlatformDeclareList=() =>{
		setLoading(true)
		http.post('demand_resp/resp_task/thirdPlatformDeclareList',{
			"deviceName": deviceName,
			"deviceRatedPowerSort": deviceRatedPowerSort,
			"deviceStatusSort": deviceStatusSort,
			"number": pages,
			"pageSize": 10,
			"platformId": platformId,
			"respId": respId,
			"sid": "",
			"strategyId": strategyId
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				if(res.data.data){
					let content = res.data.data.devieInfo.content
					content.map(res =>{
						return res.key = res.drsId
					})
					setData(res.data.data.devieInfo.content);
					setInvitation(res.data.data.invitation);
					setTotalDeclare(res.data.data.totalDeclare);
					setTotal(res.data.data.devieInfo.totalElements);
					setLoading(false)
				}
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 第三方智慧能源平台-发送-弃用
	const thirdPlatformSend=() =>{
		setBtnLoading(true);
		setLoadingName('发送中');
		const params = new URLSearchParams();
		params.append('platformId', platformId);
		params.append('respId',respId)
		http.post('demand_resp/resp_task/thirdPlatformSend',params,{
			headers:{
			    'content-type': 'application/x-www-form-urlencoded'
			}
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setBtnLoading(false);
				setLoadingName('发送');
				setSendLoading(true);
				message.success('发送成功')
			}else{
				setBtnLoading(false);
				setLoadingName('发送');
				setSendLoading(false);
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 第三方智慧能源平台-申报负荷提交-弃用
	const thirdPlatformDeclareSubmit  =() =>{
		const params = new URLSearchParams();
		params.append('platformId', platformId);
		params.append('respId',respId)
		params.append('drsIds',drsIds)
		http.post('demand_resp/resp_task/thirdPlatformDeclareSubmit',params).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setSelectedRowKeys([]);
				message.success('成功')
				thirdPlatformDeclareList()
				
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 第三方智慧能源平台-查询申报负荷列表
	const getCSPGDeclareList =() =>{
		http.post('demand_resp/resp_task/getCSPGDeclareList',{
			"deviceName": deviceName,
			"deviceRatedPowerSort": deviceRatedPowerSort,
			"deviceStatusSort": deviceStatusSort,
			"number": pages,
			"pageSize": 10,
			"platformId": platformId,
			"respId": respId,
			"sid": "",
			"strategyId": strategyId,
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				if(res.data.data){
					let content = res.data.data.devieInfo.content
					content.map((res,index) =>{
						// return res.key = res.drsId
						return res.key = index
					})
					
					setData(res.data.data.devieInfo.content);
					setInvitation(res.data.data.invitation);
					setTotalDeclare(res.data.data.totalDeclare);
					setTotal(res.data.data.devieInfo.totalElements);
					setLoading(false)
				}
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 刷新
	const Refresh = () =>{
		if(status=='0'){
			thirdPlatformDeclareList()
		}else{
			getCSPGDeclareList()
		}
		
	}
	// 去选择策略
	const strategyOpen =() =>{
		const data = 'Hell';		
		props.sendDataToParent(data); // 调用父组件传递的回调函数并传递参数
		
	}


	const config = {
	  title: '已申报',
	  
	};
	// console.log(data)
	const onSearch = (value) => {
		
	}

	const columns = [
		{
			title: '序号',
			dataIndex: 'name',
			key: 'name',
			render:(value, item, index) => (page - 1) * 5 + index+1,
		},
		{
			title: '节点',
			dataIndex: 'nodeName',
			key: 'nodeName',
			// editable: true,
		},
		{
			title: '户号',
			dataIndex: 'noHouseholds',
			key: 'noHouseholds',
			// editable: true,
		},
		{
			title: '申报负荷(kW)',
			dataIndex: 'declareLoad',
			// width: '40%',
			editable: true,
			render:(value, item, index) =>{
				if(value==null||value==undefined||value===""||value=='-'){
					return '-'
				}else{
					return Number(value).toFixed(2)
				}
				
			}
		},
		{
			title: '基线平均负荷(kW)',
			dataIndex: 'baseLoad',
			render:(value, item, index) =>{
				if(value==null||value==undefined||value===""||value=='-'){
					return '-'
				}else{
					return Number(value).toFixed(2)
				}
				
			}
			// width: '40%',
			// editable: true,
		},
		// {
		// 	title: '价格',
		// 	dataIndex: 'declarePrice',
		// 	// width: '40%',
		// 	editable: true,
		// 	render:(value, item, index) =>{
		// 		if(value==null||value==undefined||value===""||value=='-'){
		// 			return '-'
		// 		}else{
		// 			return Number(value).toFixed(2)
		// 		}
				
		// 	}
		// },
		
		{
			title: '操作',
			dataIndex: 'operation',
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
					<Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
						编辑
					</Typography.Link>
				);
			},
		}
		
	];
	const onSelectChange = (newSelectedRowKeys,selectedRows) => {
		console.log('selectedRowKeys changed: ', newSelectedRowKeys);
		console.log('selectedRows changed: ', selectedRows);
		// setSelectedRowKeys(newSelectedRowKeys);
			setSelectedRowKeys(newSelectedRowKeys)
			setDrsIds(newSelectedRowKeys)
	};
	const rowSelection = {
		selectedRowKeys: selectedRowKeys,
		preserveSelectedRowKeys: true, // 关键代码
		onChange: (selectedRowKeys, selectedRows) => {
		  // setSelectedRowKeys(selectedRowKeys);
		  
		  setSelectedRowKeys(selectedRowKeys);
		  setDrsIds(selectedRowKeys)
		  
		  selectedRowKeys &&setSelectedRows(selectedRows)
		},
		getCheckboxProps: record => ({
			  disabled: record.drsStatus == 21,    // 配置无法勾选的列
			}),
		
	  };
	const handleChange = (value) => {
		console.log(`selected ${value}`);
		if(value){
			var indexs = value.indexOf("。")
			var resolves = value.substring(indexs + 1, value.length);
			console.log(resolves)
			if(resolves.indexOf('高新兴数字能源') !== -1){
				
				setPlatformId('demand_response_invitation_response')
				thirdPlatformDeclareList()
			}else{
				
				setPlatformId(value)
				thirdPlatformDeclareList()
			}
		}
		
		// if()
		
	};
	const isEditing = (record: Item) => record.key === editingKey;
	
	const edit = (record: Partial<Item> & { key: React.Key }) => {
		console.log(record,'-------')
		// record.map(res)
		// if()
		record.declareLoad = record.declareLoad?Number(record.declareLoad).toFixed(2):'-'
		if(record.drsStatus==21){
			// 已申报
			Modal.info(config);
			// this.setState({
			// 	editdisabled:true
			// })
		}else{
			
			form.setFieldsValue({ declareLoad: '',declarePrice:'', ...record });
			// setEditingKey(record.key);
			
			setEditingKey(record.key);
			setDrsIdid(record.drsId);
			setDeclareLoad(record.declareLoad);
			setDeclarePrice(record.declarePrice);
			setEditDisabled(false)
		}
	   
	};
	
	const cancel = () => {
		
		setEditingKey('')
	};
	
	const save = async (key: React.Key) => {
		try {
			const row = (await form.validateFields());
	
			const newData = [...data];
			const index = newData.findIndex(item => key === item.key);
			if (index > -1) {
			  // alert(1)
				const item = newData[index];
				newData.splice(index, 1, {
				...item,
				...row,
				});
			// alert(1)
			console.log(row)
			if(currentState==0){
				const params = new URLSearchParams();
				params.append('drsIdid', drsIdid);
				params.append('declareLoadBefore',declareLoad)
				params.append('declareLoad',row.declareLoad)
				params.append('declarePrice',Number(row.declarePrice) || 0)
				http.post('demand_resp/resp_task/editDeclare?drsId='+drsIdid +'&declareLoad='+row.declareLoad+
				'&declarePrice='+(row.declarePrice || 0) +'&declareLoadBefore='+declareLoad).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('成功')
						getDeclareList()
					}else{
						message.info(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
			}else{
				// alert(0)
				http.post('demand_resp/resp_task/editDeclare?drsId='+drsIdid +'&declareLoad='+row.declareLoad+
				'&declarePrice='+0+'&declareLoadBefore='+declareLoad).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('成功')
						// this.thirdPlatformDeclareList()
					}else{
						message.info(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
				
			}
			
			
				setEditingKey('');
				setData(newData)
			} else {
				newData.push(row);
				setData(newData);
				setEditingKey('');
			}
		} catch (errInfo) {
		  console.log('Validate Failed:', errInfo);
		}
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
	const handlePagination =(page) =>{
		console.log(page)
		if(currentState==0){
			
			setPage(page);
			setCurrentNum(page);
			
		}else if(currentState==1){
			
			setPages(page);
			setCurrentNum(page);
			
		}
		
	};
	return(
		<div className="ploy">
			<div className="ploybody" style={{marginTop:0}}>
				<div className="ploybody-Left">
					<h4>响应任务编号：{taskCode}</h4>
					<ul>
						<li>响应时间： {rsDate}</li>
						<li>反馈截止时间： {feedbackTime}</li>
						<li>负荷需求：{respLoad}KW</li>
						<li>响应补贴： {respSubsidy}元/kWh</li>
						<li>响应类型：{respType}</li>
						<li>响应级别：{respLevel}</li>
					</ul>
				</div>
				
			</div>
			<div className="declaration">
				{
					status=='0'?
					<div className="declarationbody" style={{textAlign:'center'}}>
						<span onClick={() => current1(1)} className={1===currentState?"active":'noactive'}>第三方智慧能源平台</span>
					</div>:
					<div className="declarationbody">
						<span onClick={() => current1(0)} className={0===currentState?"active":'noactive1'}>手动申报</span>
						<span onClick={() => current1(1)} className={1===currentState?"active":'noactive'}> AI推荐申报</span>
					</div>
				}
				
				<div className="breed" style={{border:'none'}}>
					
					<div className="breedcjose" style={{display:currentState==1?'block':'none'}}>
						{
							status=='0'?
							<div style={{float:'left',marginRight:16}}>
								<Select
									defaultValue="请选择"
									style={{ width: 260,float:'left',marginTop:12 }}
									onChange={handleChange}
									
									// options={[
										
									// ]}
								>
									{
										fields&&fields.map(res =>{
											return <Option key={res.name} value={res.name}>{res.name}</Option>
										})
									}
								</Select>
								<Button type="primary" onClick={Refresh}  style={{marginTop:12}}>刷新</Button>
							</div>
							
							:''
						}
						
						<div className="charges">
							邀约对象
							<span>{invitation}</span>
						</div>
						<div className="charges">
							申报负荷(kW)
							<span>{totalDeclare}</span>
						</div>
						{
							status=='0'?'':<Button type="primary" onClick={Refresh}  style={{marginTop:12}}>刷新</Button>
						}
						{
							status=='0'?<Search placeholder="搜索相关信息" onSearch={onSearch} style={{ width: 200,float:'right',marginTop:12 }} />:''
						}
					</div>
					<div style={{marginTop:'20px'}} className="chargetitle">
						<Form 
						// ref="myForm" 
						form={form}
						component={false}>
							<Table
								components={{
								  body: {
									cell: EditableCell,
								  },
								}}
								rowSelection={rowSelection} 
								rowKey={record => record.drsId}
								// bordered
								dataSource={data}
								columns={mergedColumns}
								rowClassName="editable-row"
								loading={loading}
								// loading={!data.length}
								pagination={
									{
										total: total,//数据的总条数
										defaultCurrent: 1,//默认当前的页数
										defaultPageSize: 5,//默认每页的条数
										showQuickJumper:false,
										current:currentNum,
										onChange:handlePagination
									}
								}
							/>
						</Form>
						<div className="footerbtn">
							<Button type="primary" disabled={selectedRowKeys.length==0?true:false} onClick={tactful}>确定</Button>
							
						</div>
						<div className="chargesbody" style={{display:dataForm==false&&currentState==0?'block':'none'}}>
							<p>该响应任务尚未选择可调负荷运行策略，请前往响应任务列表<br />“运行策略”中选择相应可调负荷运行策略</p>
							<Button type="primary" onClick={strategyOpen}>前往选择策略</Button>
						</div>
					</div>
				</div>
			</div>
		</div>
	)
}

// <div className="ploybody-Right">
// 						<h4>能量优化助手</h4>
// 						<div className="assistant">
// 							<div>
// 								<p>90934</p>
// 								<span>推荐申报负荷(kW)</span>
// 							</div>
// 							<div>
// 								<p>90934～91880</p>
// 								<span>推荐申报负荷范围(kW)</span>
// 							</div>
// 						</div>
// 						<div className="recommend">使用大模型推荐申报负荷</div>
// 					</div>
export default Ploy

{/* <Table rowSelection={rowSelection} rowKey={record => record.drsId} dataSource={dataSource} columns={columns} />
<div className="declarationbody">
						<span onClick={() => this.current1(0)} className={0===this.state.currentState?"active":'noactive1'}>可调负荷运行策略</span>
						<span onClick={() => this.current1(1)} className={1===this.state.currentState?"active":'noactive'}>智慧能源平台</span>
					</div> */}
// <Select
// 							    defaultValue="高新兴数字能源平台"
// 							    style={{ width: 260 }}
// 							    onChange={handleChange}
// 							    // options={[
							        
// 							    // ]}
// 							>
// 								{
// 									devieInfo&&devieInfo.map(res =>{
// 										return <Option key={res.address} value={res.address+'。'+res.paramName}>{res.paramName}</Option>
// 									})
// 								}
// 							</Select>