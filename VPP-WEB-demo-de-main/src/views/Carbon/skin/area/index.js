import React, { useEffect,useState } from 'react';
import { Tabs,Table,Select,Input,DatePicker,ConfigProvider,Popconfirm,
Button,Space,Modal,Form,Typography,InputNumber,message  } from 'antd';
import {
  InfoCircleOutlined,
  LeftCircleOutlined,
  FormOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined
  
} from '@ant-design/icons';
import './area.scss'
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import { Link,useHistory } from "react-router-dom";
// import http from '../../../../axios/serve.js'
import http from '../../../../server/server.js'

const { Text } = Typography;

const originData: Item[] = [];

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
const inputNode = inputType === 'attrNum' ? <Input /> : <Input />;

  return (
    <td {...restProps}>
      {editing ? (
        <Form.Item
          name={dataIndex}
          style={{ margin: 0 }}
     //      rules={[
     //        {
     //          required: true,
			  // pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),
     //          message: `请输入 ${title}!`,
     //        },
     //      ]}
		  rules={ [{required: true, pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}
		  
        >
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};
const { confirm } = Modal;

const { Search } = Input;
const { RangePicker } = DatePicker;

const { Option } = Select;
const Area =(props) =>{
	  const [confirmLoading, setConfirmLoading] = useState(false);
	  const [isModalVisible, setIsModalVisible] = useState(false);
	  const [editingKey, setEditingKey] = useState('');
	  const [data, setData] = useState(originData);
	  const [title, setTitle] = useState('');
	  const [nodeId, setNodeId] = useState('');
	  const [startTime, setStartTime] = useState('');
	  const [endTime, setEndTime] = useState('');
	  const [content, setContent] = useState([]);
	  const [addTime, setAddTime] = useState('');
	  const [cid, setCid] = useState('');
	  const [createdTime, setCreatedTime] = useState('');
	  const [ctype, setCtype] = useState('');
	  const [updateTime, setUpdateTime] = useState('');
	  const [loading, setLoading] = useState(false);
	  const [page, setPage] = useState(1);
	  const [total, setTotal] = useState('');
	  const [setLoadings, setSetLoadings] = useState(false);
	  const [currentNum, setCurrentNum] = useState(1);
	  const history = useHistory()
	const [maForm1] = Form.useForm()
	const [myForm] = Form.useForm()
	useEffect(() =>{
		console.log(props)
		if(props.location.state){
			let pro = props.location.state
			console.log(pro)
			console.log(pro.nodeId)
			setTitle(pro.title);
			setNodeId(pro.nodeId);
			setEndTime(pro.endTime);
			setStartTime(pro.startTime);
		}else{
			history.push('/skin')
		}
	},[]);
	useEffect(() =>{
		if(nodeId!==''&&endTime!==''&&startTime!==''){
			getTreeOrAreaList()
		}
		console.log(nodeId)
	},[title,nodeId,endTime,startTime])
	useEffect(() =>{
		if(nodeId!==''&&endTime!==''&&startTime!==''){
			getTreeOrAreaList()
		}
		
	},[page,currentNum])
	// 添加面积
	const add =() =>{
		
		setIsModalVisible(true)
	}
	const quxiao =() =>{
		setIsModalVisible(false)
	}
	// 删除
	const del =(val) =>{
		console.log(val)
		confirm({
		    title: '确定要删除吗1?',
		    icon: <ExclamationCircleOutlined />,
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
			okButtonProps:{className:'newOkbutton'},
		    onOk() {
				console.log('OK');
				http.post('carbon/sink/delTreeOrArea?cId='+val.cid).then(res =>{
					console.log(res)
					if(res.data.code==200){
						setPage(1);
						setCurrentNum(1);
						message.success('删除成功');
						getTreeOrAreaList()
						
					}else{
						message.success(res.data.msg)
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
	// 查询树木/绿化面积列表
	const getTreeOrAreaList=() =>{
		setLoading(true)
		http.post('carbon/sink/getTreeOrAreaList',{
			"ctype": title=='绿化面积'?'lvhuamianji':'zhongzhishumu',
			"endTime": endTime,
			"nodeId": nodeId,
			"number": page,
			"pageSize": 10,
			"startTime": startTime
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let content = res.data.data.content
				content.map((res,index) =>{
					return res.key = index
				});
				setContent(res.data.data.content);
				setTotal(res.data.data.totalElements);
				setLoading(false);
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 返回
	const area = () =>{
		props.history.push({
			pathname: '/skin',
			query:{
				nodeId:nodeId,
				endTime:endTime,
				startTime:startTime
			}
			// pathname: '/resource',
		});
	}

	const onFinish = (values) => {
		console.log('Success:', values);
		let startdate = values.addTime.format('YYYY-MM') +'-01'
		console.log(startdate)
		setSetLoadings(true)
		http.post('carbon/sink/addTreeOrArea',{
			"addTime": startdate,
			"attrNum": values.attrNum,
			"cid": "",
			"createdTime":'',
			"ctype": title=='绿化面积'?'lvhuamianji':'zhongzhishumu',
			"nodeId": nodeId,
			"updateTime": ""
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setIsModalVisible(false);
				setSetLoadings(false);
				message.success('添加成功')
				maForm1.resetFields()
				getTreeOrAreaList()
			}else{
				setSetLoadings(false);
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
		
	};
	
	const onFinishFailed = (errorInfo) => {
		console.log('Failed:', errorInfo);
	};
	const handleCancel = () => {
		setIsModalVisible(false);
		setSetLoadings(false);
		maForm1.resetFields();
	};
	const isEditing = (record: Item) => record.key === editingKey;
	
	const edit = (record: Partial<Item> & { key: React.Key }) => {
		console.log(record)
		myForm.setFieldsValue({  attrNum: '', ...record });
		setEditingKey(record.key);
		setAddTime(record.addTime);
		setCid(record.cid);
		setCreatedTime(record.createdTime);
		setCtype(record.ctype);
		setUpdateTime(record.updateTime);
		setNodeId(record.nodeId)
	};
	
	const cancel = (e) => {
		setEditingKey('');

	};
	
	const save = async (key) => {
		try {
			const row = (await myForm.validateFields());
			const newData = [...data];
			const index = newData.findIndex(item => key === item.key);
		if (index > -1) {
			const item = newData[index];
			newData.splice(index, 1, {
			  ...item,
			  ...row,
			});
			setData(newData);
			setEditingKey('');
			
		} else {
			
			// let addTimenew = addTime.substr(0,7)
			newData.push(row);
			setData(newData);
			setEditingKey('');
			// setAddTime(addTimenew+'-01')
			// console.log(addTimenew+'-01')
			// 编辑树木/绿化面积信息
			http.post('carbon/sink/editTreeOrArea',{
				"addTime": addTime,
				"attrNum": row.attrNum,
				"cid": cid,
				"createdTime": createdTime,
				"ctype": title=='绿化面积'?'lvhuamianji':'zhongzhishumu',
				"nodeId": nodeId,
				"updateTime": updateTime
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					message.success('修改成功')
					getTreeOrAreaList()
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		  }
		} catch (errInfo) {
		  console.log('Validate Failed:', errInfo);
		}
	  };
	
	  const columns = [{
			title: '添加日期',
			dataIndex: 'addTime',
			render: (text, record) => {
				// console.log(text)
				return text.substr(0,7)
			}
		},
		{
			title: title=='绿化面积'?'面积统计（m3）':'种植树木统计（棵）',
			dataIndex: 'attrNum',
			editable: true,
		},
		
		{
		  title: '操作',
		  dataIndex: 'operation',
			render: (_: any, record: Item) => {
			const editable = isEditing(record);
			return editable ? (
			  <span>
				<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
				  
				  <a>确定</a>
				</Typography.Link>
			   <Popconfirm onClick={() => cancel(record.key)} >
															  <a>取消</a>
				</Popconfirm>
			  </span>
			) : (
			  <Typography.Link  >
					<a disabled={editingKey !== ''} onClick={() => edit(record)}>编辑</a>
					<a disabled={editingKey !== ''} onClick={() => del(record)} style={{marginLeft:'20px'}}>删除</a>
					
			  </Typography.Link>
			);
		  },
		},
	];
	// 
	const mergedColumns = columns.map(col => {
		if (!col.editable) {
		  return col;
		}
		return {
		  ...col,
		  onCell: (record: Item) => ({
			record,
			inputType: col.dataIndex === 'attrNum',
			dataIndex: col.dataIndex,
			title: col.title,
			editing: isEditing(record),
		  }),
		};
	});
	
	// 分页
	const tableChange =(page) =>{
		console.log(page)
		setPage(page.current);
		setCurrentNum(page.current)
	}
	return(
	<ConfigProvider locale={locale}>
		<div className="area1">
			<div className="header">
				
				<a onClick={area} style={{color:'#DFE1E5'}}><LeftCircleOutlined /></a>
				<span>{title}</span>
				
			</div>
			<div className="area">
				<div className="areadiv" style={{marginBottom:'18px'}}>
				<Button type="primary" onClick={add}>{title=='绿化面积'?'添加':'添加'}</Button></div>
				
				 <Form form={myForm} component={false}>
					  <Table
						components={{
							body: {
								cell: EditableCell,
							},
						}}
						rowKey={record =>{
							return record.cid
						}}
						loading={loading}
						bordered
						dataSource={content}
						columns={mergedColumns}
						rowClassName="editable-row"
						onChange= {tableChange}
						pagination={
							{
							  total: total,//数据的总条数
							  defaultCurrent: 1,//默认当前的页数
							  defaultPageSize: 10,//默认每页的条数
							  showSizeChanger:false,
								current:currentNum
							}
						}
						summary={pageData => {
							// console.log(pageData)
							let totalBorrow = 0;
							let totalRepayment = 0;
											  
							pageData.forEach(({ attrNum }) => {
								// console.log(attrNum)
								totalBorrow += Number(attrNum);
								// totalRepayment += repayment;
							});
											  
							return (
								<>
									<Table.Summary.Row>
										<Table.Summary.Cell index={0}>合计</Table.Summary.Cell>
										<Table.Summary.Cell index={1}>
											<Text>{totalBorrow}</Text>
										</Table.Summary.Cell>
										<Table.Summary.Cell index={2}>
											
										</Table.Summary.Cell>
										
										
										
									</Table.Summary.Row>
									
								</>
							);
						}}
					  />
					</Form>
			</div>
			<ConfigProvider locale={locale}>
			<Modal
				title='添加'
				visible={isModalVisible}
				confirmLoading={confirmLoading}
				onCancel={handleCancel}
				footer={null}
				
			>
				<Form
					name="basic"
					labelCol={{ span: 4 }}
					wrapperCol={{ span: 20 }}
					initialValues={{ remember: true }}
					onFinish={onFinish}
					onFinishFailed={onFinishFailed}
					autoComplete="off"
					form={maForm1}
				>
						
					<Form.Item label={title=='绿化面积'?'面积统计':'种植树木'} >
						<Form.Item name="attrNum" style={{width:'90.5%',float:'left',margin:'0px'}}
							// rules={ [{ pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入数字' }]}
							rules={ [{required: true, pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}
						>
							<Input maxLength={50} style={{marginLeft:'0px'}} />
						</Form.Item>
					   <span className="peraie">{title=='绿化面积'?'m³':'棵'}</span>
					</Form.Item>
					
					<Form.Item
						label="添加日期"
						name="addTime"
					>
						<DatePicker picker="month" style={{width:'100%',marginLeft:'0px'}} />
							
						
					</Form.Item>
					<Form.Item wrapperCol={{ offset: 8, span: 16 }} style={{textAlign:'right'}}>
						<Button ghost onClick={quxiao}>
						  取消
						</Button>
						<Button type="primary" style={{marginLeft:'10px'}} loading={setLoadings} htmlType="submit">
						  确定
						</Button>
						
					</Form.Item>
				</Form>
			</Modal>
			</ConfigProvider>
		</div>
	</ConfigProvider>
	)
}

export default Area