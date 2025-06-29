import React,{useEffect,useState} from 'react'
import { Link,useHistory  } from "react-router-dom"
import Conditioner from './Conditioner/index.js'
import http from '../../../server/server.js'
import {Button,Select,Input ,Switch  ,Modal ,Form,
Radio,Tabs,Table,ConfigProvider,message ,
Checkbox,DatePicker,TimePicker,Space } from 'antd';
import { PlusOutlined ,FormOutlined,ExclamationCircleOutlined} from '@ant-design/icons';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
// import 'antd/dist/antd.min.css';
import './index.css'
import './index.scss'
const { confirm } = Modal;

const { Search } = Input;
const { Option } = Select;
const { TabPane } = Tabs;

const Tactics =() =>{
	const [dataSource, setDataSource] = useState([]);
	const [selectedRowKeys, setSelectedRowKeys] = useState([]);
	const [isModalOpen, setIsModalOpen] = useState(false);
	const [index, setIndex] = useState(0);
	const [strategyType, setStrategyType] = useState(0);
	const [total, setTotal] = useState('');
	const [loading, setLoading] = useState(true);
	const [page, setPage] = useState(1);
	const [userId, setUserId] = useState(sessionStorage.getItem('userid'));
	const [strategyName, setStrategyName] = useState('');
	const [username, setUsername] = useState(sessionStorage.getItem('username'));
	const [currentNum, setCurrentNum] = useState(1);
	const history= useHistory();
	// useEffect(() =>{
	// 	strategyListByNamePageable()
	// },[])
	useEffect(() =>{
		strategyListByNamePageable()
	},[strategyName,page,currentNum])
	// 搜索
	const onSearch =(val) =>{
		console.log(val)
		setStrategyName(val)
		// setPage(1)
		// setCurrentNum(1)
		
	}
	// 新建策略
	const addTactics =() =>{
		setIsModalOpen(true)
	}
	const pattern =(e) =>{
		console.log(e)
		setIndex(e)
		setStrategyType(e)
	}
	// 更改状态
	const onChange =(check,record) =>{
		console.log(check,record)
		const paramsList = new URLSearchParams() // 切记不要let paramsList = {type: 1, userList: userList}
		paramsList.append('strategyId',record.strategyId) // 参数1为参数名，参数2为参数内容
		paramsList.append('strategyStatus', check)
		http.post('run_schedule/run_strategy_delete/changeStrategyStatus',paramsList).then(res =>{
			console.log(res)
			if(res.data.code==200){
				message.success('成功')
				strategyListByNamePageable()
			}else{
				strategyListByNamePageable()
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 查看
	const look =(record) =>{
		console.log(record)
		history.push({
			pathname: '/Tdetail',
			state: { strategyId:record.strategyId}
		})
	}
	// 分页获取可调负荷运行策略
	const strategyListByNamePageable =() =>{
		const paramsList = new URLSearchParams() // 切记不要let paramsList = {type: 1, userList: userList}
		paramsList.append('number', page) // 参数1为参数名，参数2为参数内容
		paramsList.append('pageSize', 10)
		paramsList.append('strategyName', strategyName)
		setLoading(true)
		http.post('run_schedule/run_strategy/strategyListByNamePageable',paramsList,{
			
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setDataSource(res.data.data.content);
				setTotal(res.data.data.totalElements);
				setLoading(false)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 编辑
	const edit =(record) =>{
		console.log(record)
		if(record.strategyType==0){
			// 空调
			history.push({
				pathname: '/strategy',
				state: { strategyId:record.strategyId}
			})
		}else if(record.strategyType==1){
			// 其他
			history.push({
				pathname: '/period',
				state: { strategyId:record.strategyId}
			})
		}
		
	}
	// 删除
	const del =(e) =>{
		console.log(e)
		confirm({
		    title: '确定要删除吗?',
		    icon: <ExclamationCircleOutlined />,
			okText:'确定',
			cancelText:'取消',
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    // content: 'Some descriptions',
		    onOk() {
				console.log('OK');
				http.post('run_schedule/run_strategy_delete/deleteStrategy?strategyId='+e.strategyId).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('删除成功')
						strategyListByNamePageable()
						setPage(1);
						setCurrentNum(1)
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
	// 批量删除
	const delet =() =>{
		confirm({
		    title: '确定要批量删除吗?',
		    icon: <ExclamationCircleOutlined />,
			okText:'确定',
			cancelText:'取消',
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    // content: 'Some descriptions',
		    onOk() {
				console.log('OK');
				http.post('run_schedule/run_strategy_delete/batchDeleteStrategy?strategyIdList='+selectedRowKeys).then(res =>{
					console.log(res)
					if(res.data.code==200){
						message.success('删除成功')
						strategyListByNamePageable()
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
	const columns = [
		{
			title: '序号',
			dataIndex: 'name',
			key: 'name',
			render:(value, item, index) => (page - 1) * 10 + index+1,
		},
		{
			title: '名称',
			dataIndex: 'strategyName',
			key: 'strategyName',
			// render: (text,record,_,action) =>{
			// 	return text+'_'+username
			// }
		},
		{
			title: '创建者',
			dataIndex: 'ownerName',
			key: 'ownerName',
			// render: (text,record,_,action) =>{
			// 	return text+'_'+username
			// }
		},
		{
			title: '类型',
			dataIndex: 'runStrategy',
			key: 'runStrategy',
			render: (text,record,_,action) =>{
				return text==0?'一次性':'周期性'
			}
		},
		{
			title: '额定负荷（kW）',
			dataIndex: 'ratedPower',
			key: 'ratedPower',
			render: (text,record,_,action) =>{
				return Number(text).toFixed(2)
			}
		},
		{
			title: '自动需求响应',
			dataIndex: 'demandResponse',
			key: 'demandResponse',
			render: (text,record,_,action) =>{
				return text==true?'是':'否'
			}
		},
		{
			title: '创建时间',
			dataIndex: 'createdTime',
			key: 'createdTime',
			render: (text,record,_,action) =>{
				return text?text:'-'
			}
		},
		{
			title: '设备详情',
			dataIndex: 'name',
			key: 'name',
			render: (text,record,_,action) =>{
				return	<a onClick={() => look(record)}>查看</a>
			}
		},
		{
			title: '状态',
			dataIndex: 'strategyStatus',
			key: 'strategyStatus',
			render: (text,record,_,action) =>{
				return	<Switch checkedChildren="开" unCheckedChildren="关"  
				// value={record.strategyStatus}
				key={record.strategyStatus} 
				defaultChecked={record.strategyStatus}
				checked={record.strategyStatus}
				onClick={(check) =>onChange(check,record)} />
			}
		},
		{
			title: '操作',
			dataIndex: 'address',
			key: 'address',
			render: (text,record,_,action) =>{
				return	<Space size="middle">
					<a onClick={() => edit(record)} style={{display:userId==record.ownerId?'block':'none'}}>编辑</a>
					<a onClick={() => del(record)} >删除</a>
				</Space>
			}
			
		},
	];
	const onSelectChange = (newSelectedRowKeys: React.Key[]) => {
		console.log('selectedRowKeys changed: ', newSelectedRowKeys);
		// setSelectedRowKeys(newSelectedRowKeys);
		setSelectedRowKeys(newSelectedRowKeys)
	};
	
	const rowSelection = {
		selectedRowKeys,
		onChange: onSelectChange,
	};
	const showModal = () => {
		// setIsModalOpen(true);
		setIsModalOpen(true)
	};
	
	const handleOk = () => {
		
		// period
		if(index==0){
			
			setIsModalOpen(false)
			history.push({
				pathname: '/strategy',
				state:{strategyType:strategyType}
			});
		}else{
			setIsModalOpen(false)
			history.push({
				pathname: '/period',
				state:{strategyType:strategyType}
			});
		}
		
		
	};
	
	const handleCancel = () => {
		setIsModalOpen(false)
	};
	const handlePagination =(page) =>{
		setPage(page);
		setCurrentNum(page);
		// strategyListByNamePageable()
	}
	return(
		<div className="tacticsbody">
			<div className="header">
				<Button type="primary" onClick={addTactics} icon={<PlusOutlined />}>新建策略组</Button>
				<Search placeholder="搜索策略" onSearch={onSearch} style={{ width: 200,float:'right' }} />
			</div>
			<div>
				<Table 
					// rowSelection={rowSelection}
					dataSource={dataSource} columns={columns}
					rowClassName={
						(record, index) => {
							let className = ''
							className = index % 2 ===0 ? 'ou' : 'ji'
							// console.log(className)
							return className
						}
					}
					loading={loading}
					pagination={
						{
							total: total,//数据的总条数
							defaultCurrent: 1,//默认当前的页数
							defaultPageSize: 10,//默认每页的条数
							showSizeChanger:false,
							onChange: handlePagination,
							current:currentNum
						}
					}
					rowKey={record => record.strategyId}
				 />

			</div>
			<Modal title="选择策略类型" visible={isModalOpen} 
				onOk={handleOk} onCancel={handleCancel}
				footer={[
				// 重点：定义右下角 
				<Button ghost onClick={handleCancel}>取消</Button>,
				<Button key="submit" type="primary" onClick={handleOk}>
				确定
				</Button> ]}
				wrapClassName="newzf"
			>
				<ul className="pattern">
					<li onClick={() => pattern(0)} >
						<div className={index == 0 ? 'active' : 'noactive'}>
							<span>√</span>
						</div>
						
						<p style={{color:index == 0 ? '#0092FF' : '#FFF'}}>空调</p>
					</li>
					<li onClick={() => pattern(1)} style={{float:'right'}}>
						<div className={index == 1 ? 'oneactive' : 'onenoactive'}>
							<span>√</span>
						</div>
						
						<p style={{color: index == 1 ? '#0092FF' : '#FFF'}}>其他<br />
									（照明、基站、充电桩等）</p>
					</li>
				</ul>
				
			</Modal>
		</div>
	)
}
	


export default Tactics

