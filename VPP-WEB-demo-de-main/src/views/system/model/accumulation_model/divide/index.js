import React, { useEffect,useState } from 'react';
import http from '../../../../../server/server.js'
// import {,InputNumber,Input} from 'antd';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import {  Table, Space ,Modal,Button,Form, Input, InputNumber,
 Popconfirm, Typography ,message} from 'antd';

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
			
        >
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};
const Divide =({nodeId,systemId,istrue,changeData}) =>{
	const [total, setTotal] = useState('');
	const [page, setPage] = useState('');
	const [currentNum, setCurrentNum] = useState('');
	const [dataSource, setDataSource] = useState([]);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [editingKey, setEditingKey] = useState('');
	const [data, setData] = useState(originData);
	const [id, setId] = useState('');
	const [thismonth, setThisMonth] = useState(dayjs().format("YYYYMM"));
	const [loading, setLoading] = useState(false);
	const [showModal,setShowModal] = useState(false);
	const [myForm] = Form.useForm()
	const [isFirst,setIsFirst] =  useState(null);
	// console.log(nodeId,systemId,istrue,changeData)
	useEffect(() =>{
		if(istrue ==true&&nodeId&&systemId){
			setPage(1)
			setCurrentNum(1)
			setIsFirst(true)
		}
		console.log(istrue,'istrue')
	},[istrue,systemId])
	useEffect(() =>{
		if(istrue==false){
			getlist()
		}
	},[page,currentNum])
	useEffect(() =>{
		if(isFirst==true&&systemId){
			getlist()
		}
		console.log(isFirst,'isFirst')
	},[isFirst])
	const getlist=() =>{
		setLoading(true)
		http.post('system_management/energy_model/energy_storage_model/findStorageEnergyShareProportion',{
			"nodeId": nodeId,
			"number":page,
			"pageSize": 10,
			"systemId": systemId
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
						loadProp:res.loadProp,
						nodeId: res.nodeId,
						order: res.order,
						powerUserProp: res.powerUserProp,
						operatorProp: res.operatorProp,
						systemId: res.systemId,
						updateTime: res.updateTime
					})
				});
				setDataSource(dataSource);
				setTotal(res.data.data.totalElements);
				setLoading(false);
				setIsFirst(false)
				if(istrue==true){
					changeData()
				}
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	const columns = [
		{
			title: '年序号',
			dataIndex: 'order',
			key: 'order',
			
		},
		{
			title: '虚拟电厂运营商（%）',
			dataIndex: 'loadProp',
			key: 'loadProp',
			editable: true,
			render: (text,record) =>{
				// return 	record.loadProp *100 +'%'
				return Number(record.loadProp *100)
			}
		},
		{
			title: '电力用户（%）',
			dataIndex: 'powerUserProp',
			key: 'powerUserProp',
			editable: true,
			render: (text,record) =>{
				return Number(record.powerUserProp *100)
			}
		},
		{
			title: '运营方（%）',
			dataIndex: 'operatorProp',
			key: 'operatorProp',
			editable: true,
			render: (text,record) =>{
				return Number(record.operatorProp *100)
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
	
	
	const handleOk = () => {
		setIsModalVisible(false);
	};
	
	const handleCancel = () => {
		setIsModalVisible(false);
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
		setEditingKey('')
	};
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
				console.log(row)
				if(Number(row.loadProp) +Number(row.powerUserProp) ==100){
					http.post('system_management/energy_model/energy_storage_model/updateStorageEnergyShareProportion',{
						"id": id,
						"loadProp": Number(row.loadProp)/100,
						"powerUserProp": Number(row.powerUserProp)/100
					}).then(res =>{
						console.log(res)
						if(res.data.code ==200){
							message.success('修改成功')
							getlist()
							setEditingKey('')
						}else{
							message.warning(res.data.msg)
						}
					}).catch(err =>{
						
					})
				}else{
					message.info('虚拟电厂运营商和电力用户相加应为100%')
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
		console.log(page);
		setPage(page.current);
		setCurrentNum(page.current)
	}
	
	return (
		<div style={{padding:20}}>
			<p className="titleyear">服务年限列表</p>
			<Form form={myForm} component={false}>
				<Table
					components={{
						body: {
							cell: EditableCell,
						},
					}}
					rowKey={record =>{
						return record.id
					}}
					// bordered
					dataSource={dataSource}
					columns={mergedColumns}
					rowClassName="editable-row"
					onChange={tableChange}
					loading={loading}
					pagination={
						{
						  total: total,//数据的总条数
						  defaultCurrent: 1,//默认当前的页数
						  defaultPageSize: 10,//默认每页的条数
						  showSizeChanger:false,
						  current:currentNum
						 
						}
					}
					// onChange={onChange}
					// pagination={false}
					
				/>
			</Form>
		</div>
		
		
	)
}
	


export default Divide

