import React,{useEffect,useState} from 'react'
import { Breadcrumb ,Tabs } from 'antd';
import './task.scss'
import Ploy from '../ploy/index.js'
import Strategy_content from '../strategy_content/index.js'
import Tactful from '../tactful/index.js'
import { Link,useLocation ,useHistory} from 'react-router-dom';
import http from '../../../../server/server.js'

const Task =() =>{
	const location = useLocation();
	const [query,setQuery] = useState(null);
	const [title,setTitle] = useState('申报');
	const [status,setStatus] = useState('');
	const [osType,setOsType] = useState(sessionStorage.getItem('osType'));
	const [defaultActiveKey,setDefaultActiveKey] = useState('1');
	const history = useHistory()
	useEffect(() =>{
		if (location.state) {
		    setQuery(location.state.query);
		}else{
			history.push('/Responsetask')
		}
		thirdPartyEnergyPlat()
	},[])
	// 第三方智慧能源平台
	const thirdPartyEnergyPlat =() =>{
		http.post('system_management/systemParam/thirdPartyEnergyPlat?id='+'12').then(res =>{
			console.log(res)
			if(res.data.code==200){
				setStatus(res.data.data.status)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 传参
	const handleChildData = (data) => {
	    console.log(data); // 输出子组件传递的参数
		setDefaultActiveKey('3')
	}
	
	const onChange =(key) =>{
		console.log(key)
		console.log(query)
		setTitle(key=='1'?'申报':'策略');
		setDefaultActiveKey(key);
	}
		return (
			<div className="task">
				<div className="task-header">
					<Breadcrumb>
					    <Breadcrumb.Item><Link to="Responsetask">响应任务</Link></Breadcrumb.Item>
					    <Breadcrumb.Item>{title}</Breadcrumb.Item>
					</Breadcrumb>
				</div>
				<div className="task-content">
					{
						status=='0'?
						<Tabs defaultActiveKey="1" onChange={onChange}>
							<Tabs.TabPane tab="申报详情" key="1">
								
								{query && <Ploy user={query} />}
							</Tabs.TabPane>
							
						</Tabs>:
						
						<Tabs defaultActiveKey={defaultActiveKey} activeKey={defaultActiveKey} onChange={onChange}>
							<Tabs.TabPane tab="申报详情" key="1">
								
								{query && <Ploy user={query} sendDataToParent={handleChildData} />}
							</Tabs.TabPane>
							<Tabs.TabPane tab="运行策略" key="3">
								{query && <Tactful user={query} />}
								
							</Tabs.TabPane>
							<Tabs.TabPane tab="AI推荐策略" key="2">
								{query && <Strategy_content user={query} />}
							</Tabs.TabPane>
						</Tabs>
					}
					
				</div>
			</div>
		)
	}
	



export default Task