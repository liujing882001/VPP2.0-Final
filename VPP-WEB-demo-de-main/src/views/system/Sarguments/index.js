import React,{useEffect,useState} from 'react'
import { Table,Button,Space ,Modal,Form,Input,message,Checkbox,Tree,Row,Col,Upload,Select,Radio   } from 'antd';
import { Link ,useHistory} from "react-router-dom";

import http from '../../../server/server.js'
import './index.scss'
import { MenuOutlined,UploadOutlined,MinusCircleOutlined,PlusOutlined,DeleteOutlined } from '@ant-design/icons';
// import { createHashHistory } from "history";
import {connect} from "react-redux";

// const history = createHashHistory();
const { TextArea } = Input;
const { Option } = Select;
const Sarguments =() =>{
	const history = useHistory()
	const [dataSource, setDataSource] = useState([]);
	const [loading, setLoading] = useState(false);
	const [page, setPage] = useState(1);
	const [total, setTotal] = useState('');
	const [isModalOpen, setIsModalOpen] = useState(false);
	const [nodelOpen, setNodelOpen] = useState(false);
	const [id, setId] = useState('');
	const [paramName, setParamName] = useState('');
	const [threeOpen, setThreeOpen] = useState(false);
	const [iotOpen, setIotOpen] = useState(false);
	const [nodeTypeList, setNodeTypeList] = useState([]);
	const [nodeTypeId, setNodeTypeId] = useState([]);
	const [selected, setSelected] = useState([]);
	const [change1, setChange1] = useState('');
	const [change2, setChange2] = useState('');
	const [checkedList, setCheckedList] = useState([]);
	const [selectedList, setSelectedList] = useState([]);
	const [iotAddress, setIotAddress] = useState('');
	const [iotUserName, setIotUserName] = useState('');
	const [iotUserPwd, setIotUserPwd] = useState('');
	const [paramContent, setParamContent] = useState('');
	const [dianOpen, setDianOpen] = useState(false);
	const [peakOpen, setPeakOpen] = useState(false);
	const [logoOpen, setLogoOpen] = useState(false);
	const [fileList, setFileList] = useState([]);
	const [sysfileList, setSysFileList] = useState([]);
	const [mainLogo, setMainLogo] = useState('');
	const [sysLogo, setSysLogo] = useState('');
	const [jixianOpen, setJixianOpen] = useState(false);
	const [baselineList, setBaselineList] = useState([]);
	const [loadList, setLoadList] = useState([]);
	const [pvList, setPvList] = useState([]);
	const [cityOpen, setCityOpen] = useState(false);
	const [platformName, setPlatformName] = useState('');
	const [thirdPartyOpen, setThirdPartyOpen] = useState(false);
	const [fields, setFields] = useState([]);
	const [status, setStatus] = useState('');
	const [formItems, setFormItems] = useState([]);
	const [currentValue, setCurrentValue] = useState('');
	const [ismyForm] = Form.useForm();
	const [myForm] = Form.useForm();
	const [myForm2] = Form.useForm();
	const [iotmyForm] = Form.useForm();
	const [myFormdian] = Form.useForm();
	const [peakmyForm] = Form.useForm();
	const [logomyForm] = Form.useForm();
	const [jixianmyForm] = Form.useForm();
	const [citymyForm] = Form.useForm();
	const [thirdPartymyForm] = Form.useForm();
	
	useEffect(()=>{
		// modelParameterListPageable()
		getnodeTypeList()
	},[])
	// 类型
	const getnodeTypeList =() =>{
		http.post('system_management/node_model/nodeTypeList').then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let data = res.data.data
				data.map(res =>{
					return 	res.label = res.nodeTypeName,
							res.value = res.nodeTypeKey,
							res.disabled = false
					
				});
				setNodeTypeList(res.data.data)
			}
		})
	}
	// 获取列表
	const modelParameterListPageable =() =>{
		const params = new URLSearchParams();
		params.append('number', page);
		params.append('pageSize', 10);
		setLoading(true)
		http.post('system_management/systemParam/modelParameterListPageable',params).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setDataSource(res.data.data.content);
				setLoading(false);
				setTotal(res.data.data.totalElements);
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const edit =(e) =>{
		console.log(e)
		if(e.key==3){
			// 资源概览节点类型排序
			
			setIsModalOpen(true);
			setId(e.id);
			setParamName(e.paramName)
			http.post('system_management/systemParam/findResourceOverviewNodeTypeOrderSysParam?id='+e.id).then(res =>{
				console.log(res)
				if(res.data.code==200){
					console.log(res.data.data.nodeTypeIds)
					let nodeTypeId = res.data.data.nodeTypeIds
					let selected = []
					for(var i=0;i<nodeTypeId.length;i++){
						for(var j=0;j<nodeTypeList.length;j++){
							if(nodeTypeId[i]==nodeTypeList[j].nodeTypeKey){
								selected.push({
									nodeTypeName:nodeTypeList[j].nodeTypeName,
									nodeTypeKey:nodeTypeList[j].nodeTypeKey,
								})
							}
						}
						
					}
					setNodeTypeId(res.data.data.nodeTypeIds);
					setSelected(selected);
					setCheckedList(selected);
					setSelectedList(res.data.data.nodeTypeIds);
					ismyForm.setFieldsValue({
						paramName:res.data.data.nodeTypeIds,
						// address:data.address
					})
				}
			}).catch(err =>{
				console.log(err)
			})
			
		}else if(e.key==4){
			// 节点标准坪效设定
			setNodelOpen(true);
			setId(e.id);
			setParamName(e.paramName);
			http.post('system_management/systemParam/findSalesPerSquareMeterSysParam?id='+e.id).then(res =>{
				console.log(res)
				if(res.data.code==200){
					let data = res.data.data
					myForm.setFieldsValue({
						commercialComplex:data.commercialComplex,
						governmentOfficeGreaterThan20000:data.governmentOfficeGreaterThan20000,
						governmentOfficeLessThan20000:data.governmentOfficeLessThan20000
					
					})
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		if(e.key==5){
			// 三方智慧能源平台	
			setThreeOpen(true);
			setId(e.id);
			setParamName(e.paramName);
			http.post('system_management/systemParam/findSmartEnergySysParam?id='+e.id).then(res =>{
				console.log(res)
				if(res.data.code==200){
					let data = res.data.data
					myForm2.setFieldsValue({
						paramName:data.paramName,
						address:data.address
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		if(e.key==2){
			// IOT平台
			setIotOpen(true);
			setId(e.id);
			setIotAddress(e.iotAddress);
			setIotUserName(e.iotUserName);
			setIotUserPwd(e.iotUserPwd);
			setParamContent(e.paramContent)
			http.post('system_management/systemParam/findIOTParamSysParam?id='+e.id).then(res =>{
				console.log(res)
				if(res.data.code==200){
					let data = res.data.data
					iotmyForm.setFieldsValue({
						// paramContent:data.paramContent,
						address:data.iotAddress,
						paramName:data.paramName,
						iotUserName:data.iotUserName,
						iotUserPwd:data.iotUserPwd
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		if(e.key==1){
			// myFormdian  获取VPP需求响应Agent地址
			setDianOpen(true);
			setId(e.id);
			setParamName(e.paramName);
			http.post('system_management/systemParam/findDemandResponseSysParam?id='+e.id).then(res =>{
				console.log(res)
				if(res.data.code==200){
					let data = res.data.data
					myFormdian.setFieldsValue({
						paramName:data.paramName,
						address:data.address
					})
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		if(e.key==6){
			// 顶峰能力参数配置
			setPeakOpen(true);
			setId(e.id);
			setParamName(e.paramName);
			http.post('system_management/systemParam/findPeakCapacityParamCfgSysParam?id='+e.id).then(res =>{
				console.log(res)
				if(res.data.code==200){
					let data = res.data.data
					peakmyForm.setFieldsValue({
						cdzRatedPower:data.cdzRatedPower,
						ktRatedPower:data.ktRatedPower,
						pvRatedPower:data.pvRatedPower,
						storageEnergyRatedPower:data.storageEnergyRatedPower
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		if(e.key==7){
			// logo
			setLogoOpen(true);
			setId(e.id);
			setParamName(e.paramName);
			http.post('system_management/systemParam/findLogoSysParam?id='+e.id).then(res =>{
				console.log(res)
				if(res.data.code==200){
					let data = res.data.data
					
					
					let fileList1 = []
					let sysfileList = []
					if(data.sysLogo){
						fileList1.push({
							uid: '-1',
							// name: 'image.png',
							status: 'done',
							thumbUrl:data.sysLogo
						})
					}
					if(data.mainLogo){
						sysfileList.push({
							uid: '-2',
							// name: 'image.png',
							status: 'done',
							thumbUrl:data.mainLogo
						})
					}
					console.log(sysfileList)
					
					setFileList(fileList1.length>0?fileList1:[]);
					setSysFileList(sysfileList.length>0?sysfileList:[]);
					setMainLogo(res.data.data.sysLogo);
					setSysLogo(res.data.data.mainLogo)
					logomyForm.setFieldsValue({
						mainLogo:sysfileList.length>0?{fileList:sysfileList}:undefined,
						sysLogo:fileList1.length>0?{fileList:fileList1}:undefined,
						// platformName:data.platformName
						// sysLogo:fileList1.length>0?{fileList:fileList1:}:undefined,
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		if(e.key==8){
			// 基线负荷
			history.push({
				pathname: '/newDate',
				// state: {
				// 	nodeId: this.state.nodeId,
				// 	title:'绿化面积',
				// 	startTime:this.state.startTime,
				// 	endTime:this.state.endTime								  
				// }
			});
		}
		if(e.key==9){
			// 基线及预测算法配置	
			setJixianOpen(true);
			setId(e.id);
			setParamName(e.paramName);
			http.post('system_management/systemParam/baseLineForecastDataList').then(res =>{
				console.log(res)
				if(res.data.code==200){
					setBaselineList(res.data.data);
					http.post('system_management/systemParam/findBaseLineForecastParam?id='+e.id).then(res =>{
						console.log(res)
						if(res.data.code==200){
							let data = res.data.data
							jixianmyForm.setFieldsValue({
								baseLineGetMethod:data.baseLineGetMethod,
								loadForecastGetMethod:data.loadForecastGetMethod,
								pvForecastGetMethod:data.pvForecastGetMethod,
								
								// sysLogo:fileList1.length>0?{fileList:fileList1:}:undefined,
							})
						}
					}).catch(err =>{
						console.log(err)
					})
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		if(e.key==10){
			setCityOpen(true);
			setId(e.id);
			setParamName(e.paramName);
			http.post('system_management/systemParam/demandResponsePlatFormDatalist').then(res =>{
				console.log(res)
				if(res.data.code==200){
					setLoadList(res.data.data)
					http.post('system_management/systemParam/findDemandResponsePlatFormParam?id='+e.id).then(res =>{
						console.log(res)
						if(res.data.code==200){
							let data = res.data.data
							citymyForm.setFieldsValue({
								value:data.value,
								
							})
						}
					}).catch(err =>{
						console.log(err)
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		if(e.key==12){
			setThirdPartyOpen(true);
			setId(e.id);
			setParamName(e.paramName);
			http.post('system_management/systemParam/thirdPartyEnergyPlat?id='+e.id).then(res =>{
				console.log(res)
				if(res.data.code==200){
					setFields(res.data.data.param);
					setStatus(res.data.data.status);
					let param = res.data.data.param
					thirdPartymyForm.setFieldsValue({
						whetherstatus:res.data.data.status
					})
					param.map((res,index) =>{
						thirdPartymyForm.setFieldsValue({
							['field'+'-'+res.name]:res.name,
							['fields'+'-'+res.name]:res.param
						})
					})
				}
			}).catch(err =>{
				console.log(err)
			})
		}
	}
	// quxiao 
	const quxiao =() =>{
		setNodelOpen(false)
	}
	// iot取消
	const iotquxiao =() =>{
		setIotOpen(false)
	}
	// 三方取消
	const threexiao =() =>{
		setThreeOpen(false);
		myForm2.resetFields()
	}
	
	// 拖拽
	
	// 拖拽开始
	const handleDragStart = (ev,info ) => {
		ev.dataTransfer.effectAllowed = 'move';
		ev.dataTransfer.setData('text', ev.target.getAttribute('nodeType'));
		console.log(ev,info)
		const nodes = ev.target.parentNode.childNodes;
		nodes.forEach((item: any) => (item.style.borderBottom = '2px dashed #1890ff'));
		setChange1(info.nodeTypeKey)
	}
	const onDragEnter= (ev: any) => {
		console.log(ev)
	    // const nodes = ev.target.parentNode.childNodes;
	    // nodes.forEach((item: any) => (item.style.borderBottom = '2px dashed #1890ff'));
	}
	const onDragLeave= (ev: any) => {
		console.log(ev)
	    const nodes = ev.target.parentNode.childNodes;
	    nodes.forEach((item: any) => (item.style.borderBottom = ''));
	}
	const handleDrop= (ev,key) => {
	    ev.preventDefault();
	    ev.stopPropagation();
	    const dropCol = ev.target.parentNode;
		console.log(dropCol)
	      // dropCol.parentNode.insertBefore(dragCol, dropCol); // DOM操作
	      // const dropId = Number(dropCol.getAttribute('data-row-key'));
	    const dragIndex = selected.findIndex((item: any) => item.nodeTypeKey === change1); // 注意这里的id
		console.log(dragIndex)
	    const dropIndex = selected.findIndex((item: any) => item.nodeTypeKey === key.nodeTypeKey);
		console.log(dropIndex)
	    const data = [...selected];
	    const item = data.splice(dragIndex, 1); // 移除
		console.log(item)
	    data.splice(dropIndex, 0, item[0]); // 插入
	      // setState(data);
		console.log(data)
		let selectedList = []
		data.map(res =>{
			selectedList.push(res.nodeTypeKey)
		})
		console.log(selectedList)
		setSelected(data);
		setSelectedList(selectedList)
		console.log(dropCol)
		dropCol.childNodes.forEach((item: any) => (item.style.borderBottom = ''));
	}
	const onDragOver= (ev: any) => ev.preventDefault()
	
	
	// 拖拽元素经过放置元素时（）
	const handleDragOver = (e, key) => {
		e.preventDefault();
	};
	const dianquxiao =() =>{
		setDianOpen(false)
	}

	// 选择节点
	const nodeChange =(e,val) =>{
		console.log(e,val)
		
		let selected1 = []
		if(val.length>4){
			message.info('仅可选择四个节点类型')
			// return
			// let 
			val.shift(val[4])
			console.log(val)
		}else{
			if(e.target.checked==true){
				
				checkedList.unshift(val)
				let checkedList1 = checkedList
				console.log(checkedList1)
				if(checkedList1.length>4){
					
					message.info('仅可选择四个节点类型')
					for(var i=0;i<checkedList1.length;i++){
						if(val.nodeTypeKey==checkedList1[i].nodeTypeKey){
							checkedList1.splice(i,1)
						}
					}
					console.log(checkedList1)
					let newArr = []
					checkedList1.map(res =>{
						newArr.push(res.nodeTypeKey)
					})
					
					setSelectedList(newArr)
					ismyForm.setFieldsValue({
						paramName:newArr,
						
					})
					
				}else{
					let newList = []
					for(var i=0;i<checkedList1.length;i++){
						newList.push(checkedList1[i].nodeTypeKey)

					}
					
					setSelectedList(newList)
				}
			}else{
				for(var j=0;j<selected.length;j++){
					if(val.nodeTypeKey==selected[j].nodeTypeKey){
						// alert(0)
						selected.splice(j,1)
					}
				}
				let newArr = []
				selected.map(res =>{
					newArr.push(res.nodeTypeKey)
				});
				setSelected(selected);
				setSelectedList(newArr)
			}
			console.log(selected,val)
		}
		
		console.log(selected1)
		
	}
	const addField = () => {
		const newId = Date.now(); 
		// setFields([...prevState.fields, { name: newId, param: '' }])
		setFields(prevFields => [...prevFields, { name: newId, param: '' }]);
		
	};
	// useEffect(() =>{
	// 	setFields(fields.filter(item => item.name !== id))
	// },[fields])
	const removeField = (id) => {
		console.log(id,'------')
		const index = fields.findIndex(item => item.name === id);
		  
		if (index !== -1) {
			const updatedFields = [...fields];
			updatedFields.splice(index, 1);
			setFields(updatedFields)
		}
		setFields(prevFields => prevFields.filter(item => item.name !== id));
		// this.setState(prevState => ({
		// 	fields: prevState.fields.filter(item => item.name !== id),
		// });
	 //    this.setState(prevState => ({
	 //      // fields: prevState.fields.filter(field => field.name !== id),
		//   fields: this.state.fields.filter(item => item.name !== id)
	 //    }));
		// this.setState(prevState => ({
		//   fields: this.state.fields.filter(item => item.name !== id)
		// }));
		// this.setState(prevState => {
		// 	console.log(prevState);
		// 	return prevState; // 返回与要更新的状态相同的值
		// });
		// console.log(this.state.fields.filter(item => item.name !== id))
		
	};
	
	const handleSubmit = (e) => {
		e.preventDefault();
	};
	const whetherStatus =(e) =>{
		console.log(e)
		setStatus(e.target.value)
	};
	
	const handleInputChange = (id, value) => {
	    const updatedItems = fields.map(item => {
	      if (item.name === id) {
	        return { ...item, value };
	      }
	      return item;
	    });
		setFields(updatedItems)
	  };

	
	let columns = [{
			title: '序号',
			dataIndex: 'menuName',
			key: 'menuName',
			render:(value, item, index) => (page - 1) * 10 + index+1,
		},
		{
			title: '参数',
			dataIndex: 'paramName',
			key: 'paramName',
		},
		{
			title: '内容',
			dataIndex: 'paramContent',
			key: 'paramContent',
			width:'50%'
		},
		
		
		{
			title: '操作',
			dataIndex: 'action',
			key: 'action',
			render: (_, record) => (
			
				<Space size="middle">
					<a  onClick={() => edit(record)} disabled={record.key==11?true:false}>编辑 </a>
					
				</Space>
			),
		},
	]
	useEffect(() =>{
		modelParameterListPageable()
	},[page])
	
	const handleOk = () => {
		setIsModalOpen(false);
		
	};
	
	const handleCancel = () => {
		setNodelOpen(false);
		myForm.resetFields()
	};
	// 节点坪效
	const jieonFinish = (values) =>{
		console.log('Success:', values);
		const params = new URLSearchParams();
		params.append('commercialComplex', values.commercialComplex);
		params.append('governmentOfficeGreaterThan20000', values.governmentOfficeGreaterThan20000);
		params.append('governmentOfficeLessThan20000', values.governmentOfficeLessThan20000);
		params.append('id',id)
		params.append('paramName',paramName)
		http.post('system_management/systemParam/updateSalesPerSquareMeterSysParam',{
				"commercialComplex": values.commercialComplex,
				"governmentOfficeGreaterThan20000": values.governmentOfficeGreaterThan20000,
				"governmentOfficeLessThan20000": values.governmentOfficeLessThan20000,
				"id": id,
				"paramName": paramName
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setNodelOpen(false);
				myForm.resetFields()
				modelParameterListPageable()
				message.success('成功')
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}

	
	const onFinishFailed = (errorInfo: any) => {
		console.log('Failed:', errorInfo);
	};
	// 第三方
	const threeFinish = (values) =>{
		console.log(values)
		http.post('system_management/systemParam/updateSmartEnergySysParam',{
			"address": values.address,
			"id": id,
			"paramName": values.paramName
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				
				setThreeOpen(false);
				message.success('成功')
				modelParameterListPageable()
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const threeFailed = (values) =>{
		console.log(values)
	}
	// iot平台
	const iotFinish = (values) =>{
		console.log(values)
		http.post('system_management/systemParam/updateIOTParamSysParam',{
			"id": id,
			"iotAddress": values.address,
			"iotUserName": values.iotUserName,
			"iotUserPwd": values.iotUserPwd,
			"paramName": values.paramName
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setIotOpen(false);
				message.success('成功')
				iotmyForm.resetFields()
				modelParameterListPageable()
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const iotFailed = (values) =>{
		console.log(values)
	}
	
	// 资源概览
	const isFinish = (values) =>{
		console.log(values)
	}
	const handleOk1 = (values) =>{
		
		// nodeTypeId
		http.post('system_management/systemParam/updateResourceOverviewNodeTypeOrderSysParam',{
			id:id,
			paramName:paramName,
			nodeTypeIds:selectedList
			
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setIsModalOpen(false);
				ismyForm.resetFields()
				modelParameterListPageable()
				message.success('成功')
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const handleCancel1 =() =>{
		setIsModalOpen(false);
		ismyForm.resetFields()
	}
	const isFailed = (values) =>{
		console.log(values)
	}
	// 电网省
	const dianFinish =(values) =>{
		console.log(values)
		
		http.post('system_management/systemParam/updateDemandResponseSysParam',{
			"address": values.address,
			"id": id,
			"paramName": values.paramName
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setDianOpen(false);
				message.success('成功')
				myFormdian.resetFields()
				modelParameterListPageable()
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const dianCancel = (values) =>{
		setDianOpen(false);
		myFormdian.resetFields()
	}
	// iot
	const iotCancel =() =>{
		setIotOpen(false);
		iotmyForm.resetFields()
	}
	const threeCancel =() =>{
		setThreeOpen(false);
		myForm2.resetFields()
	}
	// 拖拽
	 const onDrop = (info) => {
		 console.log(info)
}
	// 顶峰能力

	const peakCancel =() =>{
		setPeakOpen(false)
	}
	const peakquxiao =() =>{
		setPeakOpen(false)
	}
	const peakFinish =(values) =>{
		console.log(values)
		http.post('system_management/systemParam/updatePeakCapacityParamCfgSysParam',{
				"cdzRatedPower": values.cdzRatedPower,
				"id": id,
				"ktRatedPower": values.ktRatedPower,
				"paramName": paramName,
				"pvRatedPower": values.pvRatedPower,
				"storageEnergyRatedPower": values.storageEnergyRatedPower
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setPeakOpen(false);
				message.success('成功')
				peakmyForm.resetFields()
				modelParameterListPageable()
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			
		})
	}
	const peakFailed = (faild) =>{
		console.log(faild)
	}
	// logo配置
	const logoCancel =() =>{
		setLogoOpen(false);
		setFileList([]);
		setSysFileList([])
	}
	const logoFinish =(values) =>{
		// let mainLogo =  values.mainLogo&&values.mainLogo.fileList.length>0?values.mainLogo.fileList[0].thumbUrl:''
		// let sysLogo = values.sysLogo&&values.sysLogo.fileList.length>0?values.sysLogo.fileList[0].thumbUrl:''
		// console.log(mainLogo)
		// console.log(sysLogo)
		
		http.post('system_management/systemParam/updateLogoSysParam',{
			"id": id,
			"mainLogo": '',
			"paramName": paramName,
			"sysLogo": mainLogo
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setLogoOpen(false);
				setFileList([]);
				setSysFileList([]);
				message.success('成功')
				logomyForm.resetFields()
				modelParameterListPageable()
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
		// console.log(this.state.mainLogo)
	

	}
	const logoFailed = (faild) =>{
		console.log(faild)
	}
	const logoquxiao =() =>{
		setLogoOpen(false);
		setFileList([]);
		setSysFileList([])
	}
	// lgog
	const props: UploadProps = {
		onRemove: file => {
			const index = fileList.indexOf(file);
			const newFileList = fileList.slice();
			newFileList.splice(index, 1);
			setFileList(newFileList);
			setMainLogo('')
		},
		beforeUpload: (file) => {
			console.log(file)
			const reader = new FileReader();
			reader.onload = (e) => {
				const img = new Image();
				img.src = e.target.result;
				console.log(img)
				img.onload = () => {
					console.log('图片尺寸：', img.width, 'x', img.height);
				};
				// console.log(img.src)
				setMainLogo(img.src)
			};
			reader.readAsDataURL(file);
			const isImage = file.type.indexOf('image') === 0;
				if (!isImage) {
				  message.error('You can only upload image file!');
				}
				// console.log(isImage)
				const files = file;
				const fileType = files?.name.split(".");
				const fileDate = fileType.slice(-1);
				console.log(fileDate)
				let docsArr  = ['png','jpg','svg']
				if (!docsArr.includes(fileDate[0])) {
					  message.error(`请选择正确的图片格式`);
					  setMainLogo('')
					  return false;
				}else if (file.size/1024  > 200) {
					message.error("文件大小不能超过200kb");
					setMainLogo('')
					return false;
				}else{
					setFileList([...fileList, file])
					return false;
				}
				return isImage;
			
			
		},
		fileList,
	};
	 

	const onChange: UploadProps['onChange'] = ({ fileList: newFileList }) => {	
		if(newFileList.length>0){
			if(newFileList[0].type=='image/png'||newFileList[0].type=='image/jpeg'||newFileList[0].type=='image/svg+xml'){
				if(newFileList[0].size/1024>200){
					
				}else{
					setFileList(newFileList)
				}
				
			}
		}
			
			
	};
	// 系统logo
	const props1: UploadProps = {
		onRemove: file => {
			const index = fileList.indexOf(file);
			const newFileList = fileList.slice();
			newFileList.splice(index, 1);
			setSysFileList(newFileList);
			setSysLogo('')
		},
		beforeUpload: (file) => {
			console.log(file)
			const reader = new FileReader();
			reader.onload = (e) => {
				const img = new Image();
				img.src = e.target.result;
				console.log(img)
				img.onload = () => {
					console.log('图片尺寸：', img.width, 'x', img.height);
				};
				setSysLogo(img.src)
			};
			reader.readAsDataURL(file);
			const files = file;
			const fileType = files?.name.split(".");
			const fileDate = fileType.slice(-1);
			console.log(fileDate)
			let docsArr  = ['png','jpg','svg']
			if (!docsArr.includes(fileDate[0])) {
				message.error(`请选择正确的图片格式`);
				setSysLogo('')
				return false;
			}else if (file.size/1024  > 200) {
				message.error("文件大小不能超过200kb");
				setSysLogo('')
				return false;
			}else{
				setSysLogo([...fileList, file])
				return false;
			}
			
		},
		fileList,
	};
	const onsysChange: UploadProps['onChange'] = ({ fileList: newFileList }) => {
		console.log(newFileList)
		if(newFileList.length>0){
			if(newFileList[0].type=='image/png'||newFileList[0].type=='image/jpeg'||newFileList[0].type=='image/svg+xml'){
				if(newFileList[0].size/1024>200){
					
				}else{
					
					setSysFileList(newFileList)
				}
				
			}
		}
		
	}
	// 基线预测
	const jixianCancel = () =>{
		setJixianOpen(false)
	}
	const jixianFinish =(values) =>{
		console.log(values)
		http.post('system_management/systemParam/updateBaseLineForecastSysParam',{
			"baseLineGetMethod": values.baseLineGetMethod,
			"id": id,
			"loadForecastGetMethod": values.loadForecastGetMethod,
			"paramName": paramName,
			"pvForecastGetMethod": values.pvForecastGetMethod,
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setJixianOpen(false);
				message.success('成功')
				jixianmyForm.resetFields()
				modelParameterListPageable()
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const jixianFailed =(values) =>{
		console.log(values,'filad')
	}
	const jixianCancel1 = () =>{
		setJixianOpen(false)
	}
	// 
	const cityCancel =() =>{
		setCityOpen(false)
	}
	const cityFinish = (values) =>{
		console.log(values)
		const params = new URLSearchParams();
		params.append('id', id);
		params.append('value', values.value);
		http.post('system_management/systemParam/updateDemandResponsePlatFormParam',params).then(res =>{
			console.log(res)
			if(res.data.code==200){
				setCityOpen(false);
				message.success('成功')
				citymyForm.resetFields()
				modelParameterListPageable()
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const cityFailed =(values) =>{
		console.log(values,'filad')
	}
	const jixianCancel2 =() =>{
		setCityOpen(false)
	}
	// 第三方
	const thirdPartyonFinish = (values) =>{
		console.log(values)
		const result = [];
		Object.keys(values).forEach(key => {
			const prefix = key.substring(0, key.indexOf('-'));
			const suffix = key.substring(key.indexOf('-') + 1);
		  
			if (prefix === 'fields' && values[`field-${suffix}`]) {
				result.push({
					name: values[`field-${suffix}`],
					param: values[key]
					// platformId:values[key]
				});
			}
		});
		
		console.log(result);
		http.post('system_management/systemParam/updateThirdPartyEnergyPlat',{
			"id" : id,
			"param":result,
			"status":values.whetherstatus
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				message.success('成功');
				setThirdPartyOpen(false);
				thirdPartymyForm.resetFields()
				modelParameterListPageable()
			}
		}).catch(err =>{
			console.log(err)
		})
		
	}
	const thirdPartyCancel =() =>{
		setThirdPartyOpen(false);
		setFields([]);
		thirdPartymyForm.resetFields();
	}
	
	const handlePagination = (page) =>{
		setPage(page)
	}
	return (
	
		<div className="Sargument">
		<div className="Sargumentbody"></div>
			<Table dataSource={dataSource} columns={columns} loading={loading}
				pagination={
					{
						total: total,//数据的总条数
						defaultCurrent: 1,//默认当前的页数
						defaultPageSize: 10,//默认每页的条数
						showSizeChanger:false,
						onChange: handlePagination,
					}
				}
				
			/>
				<Modal title="编辑" visible={isModalOpen} onOk={handleOk1} className="Sarguments-modal"
					onCancel={handleCancel1}
					maskClosable={false}
					footer={[
					
						<Button ghost onClick={handleCancel1}>取消</Button>,
						<Button key="submit" type="primary" onClick={handleOk1}>确定</Button> ]}
				
					width={680}
				>
					<div className="isModa">
						<div className="isModadiv" style={{marginRight:24}}>
							<p className="chosens">选择：</p>
							<div className="chosens1">
								<Form
									name="basic"
									// labelCol={{ span: 7 }}
									// wrapperCol={{ span: 12 }}
									initialValues={{ remember: true }}
									onFinish={isFinish}
									onFinishFailed={isFailed}
									autoComplete="off"
									form={ismyForm}
								>
									<Form.Item>
										
										<Form.Item  name="paramName" >
											<Checkbox.Group 
												// options={nodeTypeList} 
											 >
												{
													nodeTypeList&&nodeTypeList.map(res =>{
														return <Checkbox onChange={(e) =>nodeChange(e,res)} key={res.nodeTypeKey}  value={res.nodeTypeKey}>{res.nodeTypeName}</Checkbox>
													})
												}
												
											</Checkbox.Group>
										</Form.Item>
									</Form.Item>
									
								</Form>
							</div>
							
						</div>
						<div className="isModadiv">
							<p className="chosens">已选：</p>
							<div className="chosens1" 
							
								
								// onDrop={e => this.handleDrop(e)}
								onDragEnter={onDragEnter}
								onDragLeave={onDragLeave}
							>
								{
									selected&&selected.map(res =>{
										
										return <p draggable
										 // onDragEnter={onDragEnter} 
										// blockNode
										onDragOver={e => handleDrop(e,res)}
										onDragStart={(e) =>handleDragStart(e,res)}
										className="nodeType">{res.nodeTypeName}
										<MenuOutlined style={{float:'right',marginTop:5,cursor: 'pointer'}} />
										</p>
									})
								}
							</div>
						</div>
						
					</div>
				</Modal>
				<Modal title="编辑" visible={nodelOpen} onOk={handleOk} onCancel={handleCancel}
					footer={null}
					maskClosable={false}
					// 节点坪效
				>
					 <Form
							name="basic"
							layout="vertical"
							// labelCol={{ span: 7 }}
							// wrapperCol={{ span: 16 }}
							// initialValues={{ remember: true }}
							onFinish={jieonFinish}
							onFinishFailed={onFinishFailed}
							autoComplete="off"
							form={myForm}
						>
							<Form.Item
								label=" 商业综合体"
								
								// rules={[{ required: true, message: 'Please input your username!' }]}
							>
								<Form.Item name="commercialComplex" style={{float:'left',width:366}}
									rules={ [{pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的数字' }]}
								>
									<Input maxLength={50} />
								</Form.Item>
								<span className="units">kWh / m² • 年 </span>
								
							</Form.Item>
					
							<Form.Item
								label="政府办公≥20000m²"
								// rules={[{ required: true, message: 'Please input your password!' }]}
							>
								<Form.Item name="governmentOfficeGreaterThan20000" 
									style={{float:'left',width:366}}
									rules={ [{pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的数字' }]}
									
								>
									<Input maxLength={50} />
								</Form.Item>
								<span className="units">kWh / m² • 年 </span>
								
							</Form.Item>
							<Form.Item
								label="政府办公<20000m² "
								// rules={[{ required: true, message: 'Please input your password!' }]}
							>
								<Form.Item name="governmentOfficeLessThan20000" 
									rules={ [{pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的数字' }]}
								style={{float:'left',width:366}}>
									<Input maxLength={50} />
								</Form.Item>
								<span className="units">kWh / m² • 年 </span>
								
							</Form.Item>
						  
					
							<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
								<Button ghost onClick={quxiao} >取消</Button>
								<Button type="primary" htmlType="submit">确定</Button>
								
						  </Form.Item>
						</Form>
				</Modal>
				<Modal title="编辑" visible={threeOpen} 
					onCancel={threeCancel}
					footer={null}
					maskClosable={false}
					// 第三方
				>
					<Form
						name="basic"
						// labelCol={{ span: 7 }}
						// wrapperCol={{ span: 16 }}
						// initialValues={{ remember: true }}
						layout="vertical"
						onFinish={threeFinish}
						onFinishFailed={threeFailed}
						autoComplete="off"
						form={myForm2}
					>
						
						<Form.Item label="平台名称" name="paramName" >
							<Input maxLength={50} />
						</Form.Item>
						<Form.Item label="平台参数" name="address" >
							<TextArea maxLength={50}></TextArea>
						</Form.Item>					
						<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
							<Button ghost onClick={threexiao} >取消</Button>
							<Button type="primary" htmlType="submit">确定</Button>							
						</Form.Item>
					   </Form>
				</Modal>
				
				<Modal title="编辑" visible={dianOpen}
					footer={null}
					onCancel={dianCancel}
					maskClosable={false}
						
					// 电网省公司需求响应地址
				>
					<Form
						name="basic"
						layout="vertical"
						// labelCol={{ span: 4 }}
						// wrapperCol={{ span: 18 }}
						// initialValues={{ remember: true }}
						onFinish={dianFinish}
						onFinishFailed={iotFailed}
						autoComplete="off"
						form={myFormdian}
					>
						
						<Form.Item label="平台名称" name="paramName" >
							<Input maxLength={50} />
						</Form.Item>
						<Form.Item label="平台参数" name="address" >
							<TextArea maxLength={50}></TextArea>
						</Form.Item>					
						<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
							<Button ghost onClick={dianquxiao}>取消</Button>
							<Button type="primary" htmlType="submit">确定</Button>							
						</Form.Item>
					   </Form>
				</Modal>
				<Modal title="编辑" visible={iotOpen}
					onCancel={iotCancel}
					footer={null}
					maskClosable={false}
					// iot
				>
					<Form
						name="basic"
						// labelCol={{ span: 7 }}
						// wrapperCol={{ span: 16 }}
						// initialValues={{ remember: true }}
						layout="vertical"
						onFinish={iotFinish}
						onFinishFailed={iotFailed}
						autoComplete="off"
						form={iotmyForm}
					>
						<Form.Item label="平台名称" name="paramName" >
							<Input maxLength={50} />
						</Form.Item>
						<Form.Item label="地址" name="address" >
							<Input maxLength={50} />
						</Form.Item>
						<Form.Item label="用户名" name="iotUserName" >
							<Input maxLength={50} />
						</Form.Item>
						<Form.Item label="密码" name="iotUserPwd" >
							<Input maxLength={50} />
						</Form.Item>
											
						<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
							<Button ghost onClick={iotquxiao}>取消</Button>
							<Button type="primary" htmlType="submit">确定</Button>							
						</Form.Item>
					   </Form>
				</Modal>
				
				<Modal title="编辑" 
					visible={peakOpen}
					onCancel={peakCancel}
					footer={null}
					maskClosable={false}
					// 顶峰能力
				>
					<Form
						name="basic"
						// labelCol={{ span: 7 }}
						// wrapperCol={{ span: 16 }}
						// initialValues={{ remember: true }}
						layout="vertical"
						onFinish={peakFinish}
						onFinishFailed={peakFailed}
						autoComplete="off"
						form={peakmyForm}
					>
						<Form.Item label="光伏系统额定功率">
							<Form.Item  name="pvRatedPower"
								style={{width: 'calc(100% - 32px)',display:'inline-block'}}
								rules={ [{ pattern: new RegExp(/^([0-9]{1,2}$)|(^[0-9]{1,2}\.[0-9]{1,2}$)|100$/),message: '请输入0-100以内的数字' }]}
							>
								<Input maxLength={50} />
							</Form.Item>
							<span className="ant-form-text"> %</span>
						</Form.Item>
						
						<Form.Item label="储能电站功率">
							<Form.Item  name="storageEnergyRatedPower"
								style={{width: 'calc(100% - 32px)',display:'inline-block'}}
								rules={ [{ pattern: new RegExp(/^([0-9]{1,2}$)|(^[0-9]{1,2}\.[0-9]{1,2}$)|100$/),message: '请输入0-100以内的数字' }]}
							>
								<Input maxLength={50} />
							</Form.Item>
							<span className="ant-form-text"> %</span>
						</Form.Item>
						<Form.Item label="充电桩系统额定功率">
							<Form.Item  name="cdzRatedPower"
								style={{width: 'calc(100% - 32px)',display:'inline-block'}}
								rules={ [{ pattern: new RegExp(/^([0-9]{1,2}$)|(^[0-9]{1,2}\.[0-9]{1,2}$)|100$/),message: '请输入0-100以内的数字' }]}
							>
								<Input maxLength={50} />
							</Form.Item>	
							<span className="ant-form-text"> %</span>
						</Form.Item>	
						
						<Form.Item label="空调系统额定功率">
							<Form.Item  name="ktRatedPower"
								style={{width: 'calc(100% - 32px)',display:'inline-block'}}
								rules={ [{ pattern: new RegExp(/^([0-9]{1,2}$)|(^[0-9]{1,2}\.[0-9]{1,2}$)|100$/),message: '请输入0-100以内的数字' }]}
							 >
								<Input maxLength={50} />
							</Form.Item>
							<span className="ant-form-text"> %</span>
						</Form.Item>	
						
						<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
							<Button ghost onClick={peakquxiao}>取消</Button>
							<Button type="primary" htmlType="submit">确定</Button>							
						</Form.Item>
					   </Form>
				</Modal>
				<Modal title="编辑" visible={logoOpen}
					onCancel={logoCancel}
					footer={null}
					maskClosable={false}
					// logo配置
				>
					<Form
						name="basic134"
						// labelCol={{ span: 7 }}
						// wrapperCol={{ span: 16 }}
						// initialValues={{ remember: true }}
						layout="vertical"
						onFinish={logoFinish}
						onFinishFailed={logoFailed}
						autoComplete="off"
						form={logomyForm}
					>
						<Form.Item  label="系统LOGO "
							// extra="longgggggggggggggggggggggggggggggggggg"
							style={{borderBottom:'1px solid rgba(255,255,255,0.2)'}}
						>
							<span className="systemlogo">支持200KB以内的PNG/JPG/SVG格式图片，建议分辨率110*50</span>
							<Row gutter={8} style={{marginTop:10}}>
								<Col span={24}>
									<Form.Item name="sysLogo" >
										
										<Upload {...props}
											fileList={fileList}
											listType="picture-card"
											maxCount={1}
											onChange={onChange}
											accept= ".jpg,.png,.svg" 
											showPreviewIcon={false}
										>
											<Button type="primary" >点击上传</Button>
										</Upload>
									</Form.Item>
								</Col>
								
							</Row>
							
						</Form.Item>
						
						
						
					   <Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
							<Button ghost onClick={logoquxiao} >取消</Button>
							<Button type="primary" htmlType="submit">确定</Button>							
						</Form.Item>
					   </Form>
				</Modal>
				<Modal title="编辑" visible={jixianOpen}
					onCancel={jixianCancel}
					footer={null}
					maskClosable={false}
					// 基线及预测算法配置	
				>
					<Form
						name="basic"
						
						layout="vertical"
						onFinish={jixianFinish}
						onFinishFailed={jixianFailed}
						autoComplete="off"
						form={jixianmyForm}
					>
						<Form.Item label="基线负荷获取方式" name="baseLineGetMethod" >
							<Select>
								{
									baselineList&&baselineList.map((item,index) =>{
										return <Option value={item.name} key={item.id}>{item.name}</Option>
									})
									
								}
							</Select>
							
						</Form.Item>
						<Form.Item label="负荷预测获取方式 " name="loadForecastGetMethod" >
							<Select>
								{
									baselineList&&baselineList.map((item,index) =>{
										return <Option value={item.name} key={item.id}>{item.name}</Option>
									})
									
								}
							</Select>
						</Form.Item>
						<Form.Item label="光伏发电预测获取方式 " name="pvForecastGetMethod" >
							<Select>
								{
									baselineList&&baselineList.map((item,index) =>{
										return <Option value={item.name} key={item.id}>{item.name}</Option>
									})
									
								}
							</Select>
						</Form.Item>
							
						<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
							<Button ghost onClick={jixianCancel1} >取消</Button>
							<Button type="primary" htmlType="submit">确定</Button>							
						</Form.Item>
					   </Form>
				</Modal>
				<Modal title="编辑" visible={cityOpen}
					onCancel={cityCancel}
					footer={null}
					maskClosable={false}
					// 各地电网公司需求响应平台
				>
					<Form
						name="basic"
						
						layout="vertical"
						onFinish={cityFinish}
						onFinishFailed={cityFailed}
						autoComplete="off"
						form={citymyForm}
					>
						<Form.Item label="各地电网公司需求响应平台" name="value" >
							<Select>
								{
									loadList&&loadList.map((item,index) =>{
										return <Option value={item.name} key={index}>{item.name}</Option>
									})
									
								}
							</Select>
							
						</Form.Item>
						
						
							
						<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
							<Button ghost onClick={jixianCancel2} >取消</Button>
							<Button type="primary" htmlType="submit">确定</Button>							
						</Form.Item>
					   </Form>
				</Modal>
				<Modal title="编辑" visible={thirdPartyOpen}
					onCancel={thirdPartyCancel}
					footer={null}
					width={600}
					maskClosable={false}
					// 新增第三方能源平台
				>
					
					<Form name="dynamic_form_nest_item"  
						autoComplete="off"
						onFinish={thirdPartyonFinish}
						layout="vertical"
						form={thirdPartymyForm}
						
					>
						<Form.Item name="whetherstatus" label="是否对接第三方能源平台">
							<Radio.Group onChange={whetherStatus}>
								<Radio value="0">是</Radio>
								<Radio value="1">否</Radio>
							</Radio.Group>
						</Form.Item>
						{fields.map((field,index) => (
							<Form.Item key={field.name}>
								<Form.Item 
									name={`field-${field.name}`}
									label='平台名称'
									style={{ display: 'inline-block', width: '40%'}}
								>
								  <Input
									placeholder="请输入平台名称"
								  />
								</Form.Item>
								<Form.Item
								  label="平台参数"
								  name={`fields-${field.name}`}
								  style={{ display: 'inline-block', width: 'calc(60% - 32px)',marginLeft:8}}
								>
								  <Input placeholder="请输入平台参数" />
								</Form.Item>
								{
									status=='0'&&fields.length>1?<MinusCircleOutlined onClick={() => removeField(field.name)} />:
									status=='1'?<MinusCircleOutlined onClick={() => removeField(field.name)} />:''
								}
								
							
							</Form.Item>
						))}
						<Form.Item>
						  <Button type="dashed" onClick={addField} block icon={<PlusOutlined />}>
								新增第三方能源平台
						  </Button>
						  
						</Form.Item>
						<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
							<Button ghost onClick={thirdPartyCancel}>取消</Button>
							<Button type="primary" htmlType="submit">确认</Button>
							
						</Form.Item>
							
							
					</Form>
				</Modal>
		</div>
	)
}


export default Sarguments
// <Form.Item label="平台名称" name="platformName"
// 								// style={{borderBottom:'1px solid rgba(255,255,255,0.2)'}}
								
// 							>
// 								<Input />
// 							</Form.Item>