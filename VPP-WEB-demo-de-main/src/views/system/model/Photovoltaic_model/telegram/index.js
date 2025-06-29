import React, { useEffect,useState } from 'react';
import { DatePicker, Space ,Button,Table ,ConfigProvider ,message,InputNumber,
Form, Input, Popconfirm, Typography,Select
} from 'antd';
// import {  Table, Space ,Modal,Button,Form, Input, InputNumber, Popconfirm, Typography } from 'antd';

import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import './index.css'
import http from '../../../../../server/server.js'
const { Option } = Select;

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
const inputNode = inputType === 'select' ? <Select style={{ width: 120 }} >
		<Option value="尖">尖</Option>
		<Option value="峰">峰</Option>
		<Option value="平">平</Option>
		<Option value="谷">谷</Option>
    </Select> : <Input />;
// property
// console.log(dataIndex)
  return (
    <td {...restProps}>
      {editing ? (
        <Form.Item
			name={dataIndex}
			style={{ margin: 0 }}
			rules={ [{required: true,
				pattern:dataIndex=='priceHour'?new RegExp(/^([0-9]{1,2}$)|(^[0-9]{1,2}\.[0-9]{1,4}$)|100$/):'',
				message:'电价范围0.0001～99.9999'
			}]}
         
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
	const [thismonth, setThismonth] = useState(dayjs().format("YYYY-MM"));
	const [loading, setLoading] = useState(false);
	const [fromTs, setFromTs] = useState('');
	const [strategyExpiryDate,setStrategyExpiryDate] = useState('');
	const [total,setTotal] = useState('');
	const [myForm] = Form.useForm();
	const [isFirst,setIsFirst] =  useState(null);
	useEffect(() =>{
		if(props.istrue==true&&props.systemId){
			setStrategyExpiryDate(props.strategyExpiryDate);
			setPage(1);
			setCurrentNum(1)
			myForm.setFieldsValue({
				year: props.shareProportionExpiryDate,
			});
		}	
	},[props.istrue,props.systemId]);
	useEffect(() =>{
		if(isFirst==true){
			getlist()
		}
		
	},[isFirst])
	useEffect(() => {
		if(page&&currentNum&&props.systemId&&start&&fromTs){
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
		// alert(0)
		setLoading(true)
		http.post('system_management/energy_model/photovoltaic_model/findPvTimeDivision',{
			"nodeId": props.nodeId,
			"number":page,
			"pageSize": 10,
			"systemId":props.systemId,
			"effectiveDate": start
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
						priceHour:res.priceHour,
						property:res.property,
						stime:res.stime,
						strategy:res.strategy,
						systemId:res.systemId,
						timeFrame:res.timeFrame,
						updateTime:res.updateTime,
						key:res.id,
						
					})
				})
				setDataSource(dataSource);
				setTotal(res.data.data.totalElements);
				setLoading(false)
				if(props.istrue==true){
					props.changeData()
				}
				setIsFirst(false)
			}else{
				setLoading(false)
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const onChange =(date, dateString) =>{
		if(dateString==''){
			message.info('请选择月份')
		}else{
			const selectedDate = dayjs(dateString, 'YYYY-MM');
			const lastMonth = selectedDate.subtract(1, 'month').format('YYYY-MM');
			
			setStart(dateString);
			setFromTs(lastMonth);
			setPage(1);
			setCurrentNum(1)
			// getlist()
		}
		
	}
	// 复制
	const copy =() =>{
		http.post('system_management/energy_model/photovoltaic_model/copyPvTimeDivision',{
			"fromEffectiveDate": fromTs,
			"nodeId": props.nodeId,
			"systemId": props.systemId,
			"toNodeId": props.nodeId,
			"toSystemId": props.systemId,
			"toEffectiveDate": start
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
	
	const columns = [
		{
			title: '序号',
			dataIndex: 'order',
			key: 'order',
			width:100,
			render:(value, item, index) => (page - 1) * 10 + index+1,
		},
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
			let currentDate = dayjs().subtract(1, 'month');
			let currentMonth = dayjs().format('YYYY-MM');
			if(start ===currentMonth ||start===currentDate.format('YYYY-MM')){
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
			
			}else{
				
			}
		  
		},
	  },
	];
	// 修改
	const isEditing = (record: Item) => record.id === editingKey;
		
	const edit = (record: Partial<Item> & { key: React.Key }) => {
		myForm.setFieldsValue({loadProp:'', ...record });
		setEditingKey(record.id);
		setId(record.id)
	};
		
	const cancel = () => {
		setEditingKey('')
	};
	// 	确认修改
	const save = async (key: React.Key) => {
		try {
			const row = (await myForm.validateFields());
			// console.log(row)
			const newData = [...dataSource];
			// console.log(newData)
			const index = newData.findIndex(item => key === item.key);
			// console.log()
			if (index > -1) {
				const item = newData[index];
				newData.splice(index, 1, {
				  ...item,
				  ...row,
				});
				http.post('system_management/energy_model/photovoltaic_model/updatePvTimeDivision',{
					"id": id,
					"priceHour": row.priceHour,
					"property": row.property,
				}).then(res =>{
					console.log(res)
					if(res.data.code ==200){
						message.success('修改成功')
						getlist()
						setEditingKey('');
						setDataSource(newData)
					}
				}).catch(err =>{
					console.log(err)
				})
				
			} else {
				
				newData.push(row);
				setEditingKey('');
				setDataSource(newData)
	
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
			  inputType: col.dataIndex === 'strategy' ? 'select' : col.dataIndex === 'property' ? 'select' :'text',
			  dataIndex: col.dataIndex,
			  title: col.title,
			  editing: isEditing(record),
			}),
		  };
		});
	// 分页
	const tableChange =(page) =>{
		console.log(page)
		setPage(page.current)
		setCurrentNum(page.current)
		
	}
	return(
		<div className="allcontent photo" style={{padding:'0px 15px'}}>
			<div className="modalheader">
				<ConfigProvider  locale={locale}>
					<DatePicker onChange={onChange} picker="month" />
				</ConfigProvider>
				<Button style={{marginLeft:15}} type="primary" onClick={copy}>复制上月电价</Button>
			</div>
			<Form form={myForm} component={false}>
				  <Table
					components={{
					  body: {
						cell: EditableCell,
					  },
					}}
					// bordered
					dataSource={dataSource}
					columns={mergedColumns}
					rowClassName="editable-row"
					onChange={tableChange}
					loading={loading}
					// pagination={false}
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
			

		</div>
	)
}


export default Telegram