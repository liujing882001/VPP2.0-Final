import React, { useEffect,useState ,useCallback} from 'react';
import { DatePicker, Space ,Button,Table ,ConfigProvider ,message,InputNumber,
Form, Input, Popconfirm, Typography,Select,Spin ,Modal
} from 'antd';
// import {  Table, Space ,Modal,Button,Form, Input, InputNumber, Popconfirm, Typography } from 'antd';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import './index.css'
import http from '../../../../../server/server.js'
import { PlusOutlined,RedoOutlined,ExclamationCircleOutlined } from '@ant-design/icons';

const { Option } = Select;
const {confirm}  = Modal;

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
  inputType: 'select' | 'text';
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
	// console.log(dataIndex)
const inputNode = inputType === 'select' &&dataIndex=='property' ? <Select style={{ width: 120 }} >
		<Option value="尖">尖</Option>
		<Option value="峰">峰</Option>
		<Option value="平">平</Option>
		<Option value="谷">谷</Option>
    </Select> :inputType === 'select' &&dataIndex=='strategy' ? <Select style={{ width: 120 }} >
		<Option value="充电">充电</Option>
		<Option value="放电">放电</Option>
		<Option value="待机">待机</Option>
    </Select>: <Input maxLength={50} />;
// /^\d+(\.\d+)?$/
// console.log(dataIndex)
  return (
    <td {...restProps}>
      {editing ? (
        <Form.Item
          name={dataIndex}
          style={{ margin: 0 }}
			rules={ [{required: true,
				pattern:dataIndex=='priceHour'?new RegExp(/^([0-9]{1,2}$)|(^[0-9]{1,2}\.[0-9]{1,6}$)|100$/):'',
				message:'电价范围0.000001～99.999999'
			}]}
			// 电价范围0.000001～99.999999
        >	
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};
const Telegram =(props) =>{
	const [dataSource, setDataSource] = useState([]);
	const [nodeId, setNodeId] = useState('');
	const [systemId, setSystemId] = useState('');
	const [start, setStart] = useState('');
	const [page, setPage] = useState('');
	const [currentNum, setCurrentNum] = useState('');
	const [editingKey, setEditingKey] = useState('');
	const [data, setData] = useState(originData);
	const [id, setId] = useState('');
	const [thismonth, setThisMonth] = useState(dayjs().format("YYYY-MM"));
	const [loading, setLoading] = useState(false);
	const [priceHour, setPriceHour] = useState('');
	const [fromTs, setFromTs] = useState(''); //上一个月
	const [strategyExpiryDate,setStrategyExpiryDate] = useState('');
	const [total,setTotal] = useState('');
	const [myForm] = Form.useForm();
	const [isFirst,setIsFirst] =  useState(null);


	useEffect(() =>{
		if(props.istrue==true&&props.systemId){
			// setNodeId(props.nodeId);
			// setSystemId(props.systemId);
			setStrategyExpiryDate(props.strategyExpiryDate);
			setPage(1);
			setCurrentNum(1)
			myForm.setFieldsValue({
				year: props.shareProportionExpiryDate,
			});
			// setIsFirst(true)
		}	
	},[props.istrue,props.systemId]);
	useEffect(() =>{
		if(isFirst==true){
			getlist()
		}
		
	},[isFirst])
	useEffect(() => {
		if(page&&props.systemId&&start&&fromTs){
			setIsFirst(true); 
		}
	}, [page,currentNum, props.systemId]);
	useEffect(() =>{
		if(page&&start&&fromTs&&props.systemId){
			getlist()
		}
	},[start,fromTs])
	// 获取数据
	const getlist =() =>{
		setLoading(true)
		http.post('system_management/energy_model/energy_storage_model/findElectricityPrices',{
			"number":page,
			"pageSize": 10,
			"ts": start,
			"nodeId": props.nodeId,
			"systemId": props.systemId
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				let dataSource = []
				content.map(res =>{
					dataSource.push({
						createdTime:res.createdTime,
						effectiveDate:res.createdTime,
						etime:res.etime,
						id:res.id,
						multiplyingPower:res.multiplyingPower,
						nodeId:res.nodeId,
						order:res.order,
						etime:res.etime,
						priceHour:res.priceHour,
						property:res.property,
						stime:res.stime,
						strategy:res.strategy,
						systemId:res.systemId,
						timeFrame:res.timeFrame,
						updateTime:res.updateTime,
						key:res.id,
						strategyHour:res.strategyHour,
						strategyForecasting:res.strategyForecasting
					})
				});
				setDataSource(dataSource);
				setTotal(res.data.data.totalElements);
				setLoading(false)
				if(props.istrue==true){
					props.changeData()
				}
				setIsFirst(false)
			}else{
				setLoading(false)
				message.error(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const onChange =(date, dateString) =>{
		const selectedDate = dayjs(dateString, 'YYYY-MM');
		const lastMonth = selectedDate.subtract(1, 'month').format('YYYY-MM');
		setStart(dateString);
		setFromTs(lastMonth);
		setCurrentNum(1);
		setPage(1)
	}
	
	
	// AI 智能调度
	const AIclick =() =>{
		if(start ==''){
			message.info('时间不能为空')
		}else{
			
			setLoading(true)
			http.post('system_management/energy_model/energy_storage_model/aiScheduling',{
				"count_Date": start,
				"nodeId": props.nodeId,
				"systemId": props.systemId
			}).then(res =>{
				console.log(res)
				if(res.data.code ==200){
					message.success('成功')
					getlist()
					setLoading(false)
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		
	}
	// 复制
	const copy =() =>{
		http.post('system_management/energy_model/energy_storage_model/copyStorageEnergyStrategy',{
			"fromTs": fromTs,
			"nodeId":props.nodeId,
			"systemId": props.systemId,
			"toNodeId": props.nodeId,
			"toSystemId": props.systemId,
			"toTs": start
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				message.success('成功')
			}else{
				message.info(res.data.msg)
				// start
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 下发策略
	const Distribute =() =>{
		
		if(start){
			confirm({
				title: '提示',
				icon: <ExclamationCircleOutlined />,
				content: '是否下发此策略？',
				cancelText:'取消',
				okText:'确定',
				onOk() {
					console.log('OK');
					http.post('system_management/energy_model/energy_storage_model/distributionStorageEnergyStrategy',{
						"nodeId": props.nodeId,
						"systemId": props.systemId,
						"ts": start
					}).then(res =>{
						console.log(res)
						if(res.data.code==200){
							message.success('下发策略成功')
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
			
		}else{
			message.info('请选择日期')
		}
		
	}

	const columns = [

	  {
		title: '时间范围',
		dataIndex: 'timeFrame',
		key: 'timeFrame',
	  },
	  {
		title: '属性',
		dataIndex: 'property',
		key: 'property',
		 editable: true,
	  },
	  {
		title: '每小时电价',
		dataIndex: 'priceHour',
		key: 'priceHour',
		 editable: true,
	  },
	  
	  {
		title: '操作',
		dataIndex: 'operation',
		render: (_: any, record: Item) => {
			// if(thismonth <= start){
				const editable = isEditing(record);
				return editable ? (
				  <span>
					<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
					  确定
					</Typography.Link>
					<Typography.Link onClick={() => cancel(record.key)}>
					  取消
					</Typography.Link>
				  </span>
				) : (
				  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
												编辑
				  </Typography.Link>
				);
			// }
		  
		},
	  },
	];
	// 修改
	const isEditing = (record: Item) => record.id === editingKey;
		
	const edit = (record: Partial<Item> & { key: React.Key }) => {
		console.log(record)
		myForm.setFieldsValue({loadProp:'', ...record });
		setEditingKey(record.id);
		setId(record.id);
	};
		
	const cancel = () => {
		setEditingKey('');
	};
	
	// 	确认修改
	const save = async (key: React.Key) => {
		try {
			const row = (await myForm.validateFields());
			console.log(row)
			const newData = [...dataSource];
			console.log(newData)
			const index = newData.findIndex(item => key === item.key);
			console.log()
			if (index > -1) {
				const item = newData[index];
				newData.splice(index, 1, {
				  ...item,
				  ...row,
				});
				http.post('system_management/energy_model/energy_storage_model/updateStorageEnergyStrategy',{
					"id": id,
					"priceHour": row.priceHour,
					"property": row.property,
					"strategy": row.strategy,
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						message.success('修改成功')
						// this.getlist()
						// this.setState({
						// 	editingKey:'',
						// 	page:this.state.page,
						// 	dataSource:newData
						// },() =>{
						// 	this.getlist()
						// })
						setEditingKey('');
						setPage(page);
						setDataSource(newData);
						getlist()
					}
				}).catch(err =>{
					console.log(err)
				})
				
			} else {
				
				newData.push(row);
				
				setEditingKey('');
				setData(newData);
	
			}
		} catch (errInfo) {
			console.log('Validate Failed:', errInfo);
		}
	};
	  
	const mergedColumns = columns.map(col => {
		  if (!col.editable) {
			return col;
		  }
		  // console.log(col)
		  return {
			...col,
			onCell: (record: Item) => ({
				
			  record,
			  inputType: col.dataIndex === 'strategy' ? 'select' : col.dataIndex === 'property' ? 'select' :'text',
			  dataIndex: col.dataIndex,
			  title: col.title,
			  editing: isEditing(record),
			}),
		  };
		});
	const onChangepage = (page, pageSize) =>{
		console.log(page)
		setPage(page.current);
		setCurrentNum(page.current);
		
	}
	return(
		<div className="allcontenta1" style={{padding:'15px 15px'}}>
			<div className="modalheader">
				<ConfigProvider  locale={locale}>
					<DatePicker onChange={onChange} picker="month" />
				</ConfigProvider>
				{/* <Button type="primary" style={{marginLeft:16}} onClick={copy}>复制项目节点电价</Button> */}
				{/* <Button type="primary" style={{marginLeft:12}} onClick={this.copy}>复制上月电价及策略</Button> */}
				{/* <Button style={{marginLeft:12,float:'right'}} type="primary">获取策略</Button>
				<Button style={{marginLeft:12,float:'right'}} onClick={this.Distribute} type="primary">下发策略</Button> */}
				
			</div>
			<Spin spinning={loading}  size="middle">
			<Form form={myForm} component={false}>
				  <Table
					components={{
						body: {
							cell: EditableCell,
						},
					}}
					dataSource={dataSource}
					columns={mergedColumns}
					rowKey={record =>record.id}
					onChange={onChangepage}
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
				</Form>
			</Spin>
			
			

		</div>
	)
}


export default Telegram


// <Button onClick={this.AIclick} type="primary" style={{float:'right'}}>AI智能调度</Button>