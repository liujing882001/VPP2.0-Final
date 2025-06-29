import React, { useEffect,useState } from 'react';
import http from '../../../../../server/server.js'
// import {,InputNumber,Input} from 'antd';
import {  Table, Space ,Modal,Button,Form, Input, InputNumber,
 Popconfirm, Typography ,message} from 'antd';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
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
  const inputNode = inputType === 'number' ? <InputNumber /> : <Input />;

  return (
    <td {...restProps}>
		{editing ? (
			<Form.Item
				name={dataIndex}
				style={{ margin: 0 }}
				rules={ [{required: true, pattern: new RegExp(/^(?:0|[1-9][0-9]?|100)$/),message: '请输入0-100范围内的整数' }]}
				// rules={[
				// 	{
				// 		required: true,
				// 		message: `请输入 ${title}!`,
				// 	},
				// ]}
				// rules={ [{required: true, pattern: new RegExp(/^(0.\d+|0|1)$/),message: '请输入正确范围内的数字' }]}
			 
			>
				{inputNode}
			</Form.Item>
		) : (
			children
		)}
    </td>
  );
};
const Divide =(props) =>{
	const [total, setTotal] = useState('');
	const [nodeId, setNodeId] = useState('');
	const [systemId, setSystemId] = useState('');
	const [page, setPage] = useState(1);
	const [currentNum,setCurrentNum] = useState('');
	const [dataSource, setDataSource] = useState([]);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [editingKey, setEditingKey] = useState('');
	const [data, setData] = useState(originData);
	const [id, setId] = useState('');
	const [thismonth, setThismonth] = useState(dayjs().format("YYYYMM"));
	const [loading, setLoading] = useState(false);
	const [showModal,setShowModal] = useState(false);
	const [myForm] = Form.useForm()
	const [isFirst,setIsFirst] =  useState(null);
	useEffect(() =>{
		if(props.istrue ==true&&props.nodeId&&props.systemId){
			// getlist()
			// changeData()
			setPage(1)
			setCurrentNum(1)
			setIsFirst(true)
		}
		console.log(props.istrue)
	},[props.istrue,props.systemId])
	useEffect(() =>{
		if(props.istrue==false){
			getlist()
		}
	},[page,currentNum])
	useEffect(() =>{
		if(isFirst==true&&props.systemId){
			getlist()
		}
		console.log(isFirst,'isFirst')
	},[isFirst])
	
	const getlist=() =>{
		setLoading(true)
		http.post('system_management/energy_model/photovoltaic_model/findPvPowerUser',{
			"nodeId": props.nodeId,
			"number":page,
			"pageSize": 10,
			"systemId": props.systemId
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				let dataSource = []
				content.length &&content.map(res =>{
					dataSource.push({
						createdTime: res.createdTime,
						id: res.id,
						key:res.id,
						nodeId: res.nodeId,
						order: res.order,
						powerUserProp: res.powerUserProp,
						systemId: res.systemId,
						updateTime: res.updateTime,
						loadProp:res.loadProp,
						operatorProp:res?.operatorProp
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
				console.log(res.data.msg)
				// message.info(res.data.m)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// const edit =(e) =>{
	// 	console.log(e)
	// 	setShowModal(true)
		
	// }
	
		
	const columns = [
		{
			title: '年序号',
			width: '10%',
			dataIndex: 'order',
			key: 'order',
		},
		{
			title: '虚拟电厂运营商折扣比例（%）',
			dataIndex: 'loadProp',
			key: 'loadProp',
			editable: true,
			render: (text,record) =>{
				return 	Number(record.loadProp *100).toFixed(2) 
			}
		},
		{
			title: '电力用户购电折扣（%）',
			dataIndex: 'powerUserProp',
			key: 'powerUserProp',
			editable: true,
			render: (text,record) =>{
				return Number(record.powerUserProp *100).toFixed(2)	
			}
		},
		{
			title: '运营方（%）',
			dataIndex: 'operatorProp',
			key: 'operatorProp',
			editable: true,
			render: (text,record) => {
				return Number(text * 100).toFixed(2)	
			}
		},
		{
			title: '操作',
			dataIndex: 'operation',
			render: (_: any, record: Item) => {
				if(thismonth <= record.order){
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
				}
			
			},
		},
	  ];
	// const showModal = () => {
	// 	setIsModalVisible(true)
	// };
	
	const handleOk = () => {
		setIsModalVisible(false)
	};
	
	const handleCancel = () => {
		setIsModalVisible(false)
	};
	const isEditing = (record: Item) => record.key === editingKey;

	const edit = (record: Partial<Item> & { key: React.Key }) => {
		myForm.setFieldsValue({
			loadProp:Number(record.loadProp)*100,
			powerUserProp:Number(record.powerUserProp)*100
		});
		setEditingKey(record.key);
		setId(record.id)
	};

	const cancel = () => {
		setEditingKey('');
	};
	// 修改
	const save = async (key: React.Key) => {
		try {
			const row = (await myForm.validateFields());
			
			const newData = [...dataSource];
			const index = newData.findIndex(item => key === item.key);
			console.log(index)
			if (index > -1) {
				const item = newData[index];
				newData.splice(index, 1, {
				  ...item,
				  ...row,
				});
				if(Number(row.powerUserProp) +Number(row.loadProp) ==100){
					http.post('system_management/energy_model/photovoltaic_model/updatePvPowerUser',{
						"id": id,
						"powerUserProp": Number(row.powerUserProp)/100,
						"loadProp":Number(row.loadProp)/100
					}).then(res =>{
						console.log(res)
						if(res.data.code ==200){
							message.success('修改成功')
							getlist()
							setEditingKey('');
						}else{
							message.warning(res.data.msg)
						}
					}).catch(err =>{
						
					})
				}else{
					message.info('虚拟电厂运营商加电力用户购电折扣相加应等于100%')
				}
				
				
			} else {
				newData.push(row);
				setEditingKey('');
				setData(newData)

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
			  // inputType: col.dataIndex === 'age' ? 'number' : 'text',
			  dataIndex: col.dataIndex,
			  title: col.title,
			  editing: isEditing(record),
			}),
		  };
		});
	const tableChange =(page) =>{
		console.log(page)
		setPage(page.current);
		setCurrentNum(page.current)
		
	}
	return (
		<div style={{padding:'20px 15px'}}>
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
				loading={loading}
				onChange={tableChange}
				pagination={
					{
					  total: total,//数据的总条数
					  defaultCurrent: 1,//默认当前的页数
					  defaultPageSize: 10,//默认每页的条数
					  showSizeChanger:false,
						current:currentNum
					}
				}
				// pagination={false}
			  />
			</Form>
		</div>
		
	)
}
	


export default Divide

