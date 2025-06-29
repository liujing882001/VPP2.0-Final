import React,{useEffect,useRef,useState} from 'react'
import {Tree , Button ,message,Input,Col,Row,
Table,Space ,Form,Modal,Typography ,Select,Upload,Cascader, 
DatePicker,Checkbox } from 'antd';
import {
	PlusOutlined,
	ExclamationCircleOutlined,
	UploadOutlined, InboxOutlined,
	DownloadOutlined,
	InfoCircleOutlined
	
} from '@ant-design/icons';
import { useHistory,useParams,useNavigate,useLocation } from 'react-router-dom'
import HybridElectricityPrice from './hybridElectricityPrice/index.js'
import './index.css'
import BaiduMap from './BaiduMap.js';
import http from '../../../server/server.js'
import Responsetask from '../../respond/Responsetask/index.js'
import { calc } from 'antd/es/theme/internal';
import provinceList from './provinceList.js'
import classNames from 'classnames';
import lang from 'element-react/src/locale/lang/bg';
const { Search } = Input;
const { confirm } = Modal;
const { Option } = Select;
const tailLayout = {
  wrapperCol: { offset: 18, span: 6 },
};

const Hybrid =() =>{
	const history = useHistory()
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [systemVisible, setSystemVisible] = useState(false);
	const [address, setAddress] = useState('');
	const [dataSource, setDataSource] = useState([]);
	const [nodeTypeList, setNodeTypeList] = useState([]);
	const [sysNodeTypeList, setSysNodeTypeList] = useState([]);
	const [sys, setSys] = useState([]);
	const [mapData, setMapData] = useState([]);
	const [contactPenlist, setContactPenlist] = useState([]);
	const [options, setOptions] = useState([]);
	const [loading, setLoading] = useState(false);
	const [lon, setLon] = useState('');
	const [lat, setLat] = useState('');
	const [page, setPage] = useState(1);
	const [editVisible, setEditVisible] = useState(false);
	const [provinceRegionId, setProvinceRegionId] = useState('');
	const [regionList, setRegionList] = useState([]);
	const [cityList, setCityList] = useState([]);
	const [cityRegionId, setCityRegionId] = useState('');
	const [areaList, setAreaList] = useState([]);
	const [cityValue, setCityValue] = useState('市');
	const [areaValue, setAreaValue] = useState('区');
	const [regionName, setRegionName] = useState('');
	const [provinceRegionName, setProvinceRegionName] = useState('');
	const [cityRegionName, setCityRegionName] = useState('');
	const [countyRegionName, setCountyRegionName] = useState('');
	const [edit, setEdit] = useState(0);
	const [lookVisible, setLookVisible] = useState(false);
	const [sysLookVisible,setSysLookVisible] = useState(false)
	const [newname, setNewname] = useState('');
	const [nodeTypeName, setNodeTypeName] = useState('');
	const [systemNames, setSystemNames] = useState('');
	const [nodeArea, setNodeArea] = useState('');
	const [newaddress, setNewaddress] = useState('');
	const [latitude, setLatitude] = useState('');
	const [longitude, setLongitude] = useState('');
	const [noHouseholds, setNoHouseholds] = useState('');
	const [nodename, setNodename] = useState('');
	const [newList, setNewList] = useState([]);
	const [istrue, setIstrue] = useState(false);
	const [total, setTotal] = useState('');
	const [nodeList, setNodeList] = useState([]);
	const [nodeName, setNodeName] = useState('');
	const [nodeTypeKey, setNodeTypeKey] = useState('');
	const [valList, setValList] = useState([]);
	const [mapLable, setMapLable] = useState('');
	const [cityVale, setCityVale] = useState('');
	const [newaddress1, setNewaddress1] = useState('');
	const [newVla, setNewVla] = useState([]);
	const [currentNum, setCurrentNum] = useState(1);
	const [setLoadings, setSetLoadings] = useState(false);
	const [osType, setOsType] = useState(sessionStorage.getItem('osType'));
	const [nodeId,setNodeId] = useState('');
	const [myForm] = Form.useForm();
	const [systemForm] = Form.useForm();
	const [checkStrictly, setCheckStrictly] = useState(false);
	const [nodeStageList,setNodeStageList] = useState([])
	const [selectedStationNode,setSelectedStationNode] = useState({})
	const [sysOption,setSysOption] = useState([])
	const [sysNodeStageList,setSysNodeStageList] = useState([])
	const [defaultValue,setDefaultValue] = useState()
	const [sysDefaultValue,setSysDefaultValue] = useState([])
	const [addressInfo,setAddressInfo] = useState({})
	const [checked1, setChecked1] = useState(true);
	const [checked2, setChecked2] = useState(true);
	const [searchName,setSearchName] = useState('')
	const [isChildProject,setIschildProject] = useState(false)
	const [useElectricType,setUseElectricType] = useState('')
	const [usetypeList,setUsetypeList] = useState([])
	const [etypename,setEtypename] = useState('')
	const [electricityCompanyList,setElectricityCompanyList] = useState([])
	useEffect(() =>{
		getnodeTypeList()
		getsys()
		getNodeStageList()
		nodeNameList()
		regionProvinces()
		getSysDeviceCategoryQuery()
		getSysNodeTypeList()
		getSysNodeStageList()
	},[]);
	
	useEffect(() =>{
			nodeListPageable(searchName,stationTypeIdArr,stationStateArr)
	},[])

	const getNodeStageList = () => {
		http.post('stationNode/nodeStageList').then(res =>{
			if(res.data.code === 200){
				let data = res.data.data
				data.map((res,index) =>{
					return {
						label : res.key,
						value : res.key
					}
				})
				
				setNodeStageList(res.data.data)
			}
		})
	}

	const getSysNodeStageList = () => {
		http.post('stationNode/nodeStageList').then(res =>{
			if(res.data.code === 200){
				let data = res.data.data
				data.map((res,index) =>{
					return {
						label : res.key,
						value : res.key
					}
				})
				
				setSysNodeStageList(res.data.data)
			}
		})
	}

	const getSysNodeTypeList = () =>{
		http.post('stationNode/sysNodeTypeQuery').then(res =>{
			if(res.data.code ==200){
				setSysNodeTypeList(res.data.data);
			}
		})
	}

	//系统设备分类查询
	const getSysDeviceCategoryQuery = () => {
		http.post('stationNode/sysDeviceCategoryQuery').then(res =>{
			if(res?.data?.data?.code === 200){
				setSysOption(res?.data?.data?.data);
			}
		})
	}

	// 节点列表
	const nodeListPageable =(query,stationTypeId=[],stationState=[],pageOne) =>{
		let keyword = ''
		if(checked1 && checked2){
			keyword = ''
		}else if(checked1){
			keyword = '建设中'
		}else if(checked2){
			keyword = '运营中'
		}else if (!checked1 && !checked2){
			keyword = null
		}
		setLoading(true)
		http.post('stationNode/stationPageQuery',{
			page:pageOne ? pageOne: page,
			size:10,
			query:query ? query: '',
			"keyword" : {
				"stationTypeId" : stationTypeId,
				"stationState" : stationState
    		}		
		}).then(res =>{
			if(res.data.code==200){
				setDataSource(res.data.data.content);
				setLoading(false);
				setTotal(res.data.data.totalElements);
			}
		}).catch(err =>{
			console.log(err)
		})
	}

	// 节点
	const nodeNameList=() =>{
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				let data = res.data.data
				data.map((res,index) =>{
					return 	res.label = res.nodeName,
							res.value = res.id
				})
				
				setNodeList(res.data.data)
			}
		})
	}

	const showModal =() =>{
		setProvinceRegion({})
		setCityRegion({})
		setCountyRegion({})
		setNodeType("")
		setIsModalVisible(true);
		setEdit(1);
		setAddress('');
		setCityValue(1)
		myForm.resetFields()
		myForm.setFieldsValue({
			sysIds:['nengyuanzongbiao']
		})
	}

	const nodehide =() => {
		setIsModalVisible(false);
		setValList([]);
		
		setUseVoltageOptions([]);
		setUseElectricType('')
		myForm.resetFields();
	}
	const handleCancel =() =>{
		
		setIsModalVisible(false);
		setValList([]);
		myForm.resetFields();
		setUseVoltageOptions([]);
		setUseElectricType('')
	}
	
	// 类型
	const getnodeTypeList=() =>{
		http.post('stationNode/projectNodeTypeQuery').then(res =>{
			if(res.data.code ==200){
				setNodeTypeList(res.data.data);
				setNewVla(['nengyuanzongbiao'])
			}
		})
	}
	// 系统
	const getsys =(val) =>{
		http.post('stationNode/projectDeviceCategoryQuery').then(res =>{
			if(res?.data?.data?.code === 200){
				setOptions(res?.data?.data?.data);
				const arr = res?.data?.data?.data.filter((item) => {
					return item.systemName === '能源总表'
				})
				myForm.setFieldsValue({
					sysIds:[arr[0]?.systemKey]
				})
		
				setDefaultValue(arr[0]?.systemKey)
				// setValList(['nengyuanzongbiao'])
			}
		})
	}

	// 删除
	const delet =(e) => {
		let nodeId = e.id
		
		confirm({
		    title: '确定要删除吗',
		    icon: <ExclamationCircleOutlined />,
			cancelText:"取消",
			okText:"确定",
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    onOk() {
				let formData = new FormData()
				formData.append('nodeId', e?.stationId);
		
				http.post('stationNode/deleteProjectNode',formData).then(res =>{
					if(res.data.code ==200){
						message.success('删除成功');
						getnodeTypeList()
						getsys()
						getNodeStageList()
						nodeNameList()
						regionProvinces()
						nodeListPageable(searchName,stationTypeIdArr,stationStateArr)
					}else{
						message.error(res.data.msg)
					}
				})
		    },
		    onCancel() {
				console.log('Cancel');
		    },
		});
		
	}

	const sysDelet =(e) => {
		let nodeId = e.id
		
		confirm({
		    title: '确定要删除吗',
		    icon: <ExclamationCircleOutlined />,
			cancelText:"取消",
			okText:"确定",
			cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		    onOk() {
				let formData = new FormData()
				formData.append('nodeId', e?.stationId);
		
				http.post('stationNode/deleteSysNode',formData).then(res =>{
					if(res.data.code ==200){
						message.success('删除成功');
						getnodeTypeList()
						getsys()
						getNodeStageList()
						nodeNameList()
						regionProvinces()
						nodeListPageable(searchName,stationTypeIdArr,stationStateArr)

					}else{
						message.error(res.data.msg)
					}
				})
		    },
		    onCancel() {
				console.log('Cancel');
		    },
		});
		
	}

	// 地图
	const getMap = (e) => {		
		setAddress(e.addr);
		setMapData(e)
		myForm.setFieldsValue({
			longitude:e.lon,
			latitude:e.lat,
			address:e.addr,
		})
	}
	// 编辑
	const editcell =(e) =>{
		// setIsModalVisible(true);
		// setNodeId(e.id);
		// setAddress(e.address);
		// setEdit(0);
		// setProvinceRegionName(e.provinceRegionName);
		// setCityRegionName(e.cityRegionName);
		// setCountyRegionName(e.countyRegionName);
		// setIstrue(true);
		// setMapLable(e.cityRegionName);
		// setCityVale(0);
		// setNewaddress1(e.provinceRegionName+e.cityRegionName);
		// getsys(e.nodeTypeId)
		// regionProvinces()
		queryCityInfo(e.provinceRegionName,e.cityRegionName)
		setEtypename(e.etype)
		if(e.provinceRegionName==='河北省'){
			queryElectricityCompany(e.provinceRegionName)
		}
		let formData = new FormData()
		formData.append('nodeId', e?.stationId);
		http.post('stationNode/viewFunction',formData).then(res =>{
			if(res?.data?.code === 200){
				setSelectedStationNode(res?.data?.data)
				setProvinceRegion({name:res?.data?.data?.provinceRegionName,id:res?.data?.data?.provinceRegionId})
				setCityRegion({name:res?.data?.data?.cityRegionName,id:res?.data?.data?.cityRegionId})
				setCountyRegion({name:res?.data?.data?.countyRegionName,id:res?.data?.data?.countyRegionId})
				setAddress(res?.data?.data?.address);
				setIsModalVisible(true);
				setEdit(0);
				setNodeType(res?.data?.data?.stationType)
				setUseElectricType(res?.data?.data?.etype)
				myForm.setFieldsValue({
					nodeName:res?.data?.data?.stationName,
					longitude:res?.data?.data?.longitude,
					latitude:res?.data?.data?.latitude,
					nodeTypeId:res?.data?.data?.stationTypeId,
					sysIds:JSON.parse(res?.data?.data?.systemIds),
					stationState:res?.data?.data?.stationState,
					address:res?.data?.data?.address,
					nodeArea:res?.data?.data?.nodeArea,
					noHouseholds:res?.data?.data?.noHouseholds,
					vol:res?.data?.data?.vol,
					etype:res?.data?.data?.etype,
					basicBill:res?.data?.data?.basicBill,
					electricityCompany:res?.data?.data?.electricityCompany
				})

			}
		})
	}

	const [parentId,setParentId] = useState('')
	
	const sysEditcell =(e) =>{
		setEdit(0)
		let formData = new FormData()
		formData.append('nodeId', e?.parentId);

		http.post('stationNode/viewFunction',formData).then(res =>{
			if(res?.data?.code === 200){
				const parentId = res?.data?.data?.stationId
				setParentId(parentId)
				systemForm.setFieldsValue({
					stationName:res?.data?.data?.stationName,
				})
			}
		})

		let formData1 = new FormData()
		formData1.append('nodeId', e?.stationId);
		http.post('stationNode/viewFunction',formData1).then(res =>{
			if(res?.data?.code === 200){
				// const children = res?.data?.data[0]?.children
				// const item = children.filter((item) => {
				//    return item.stationId === e.stationId
				// })
				systemForm.setFieldsValue({
					nodeName:res?.data?.data?.stationName,
					nodeTypeId:res?.data?.data?.stationTypeId,
					sysIds:JSON.parse(res?.data?.data?.systemIds),
					stationState:res?.data?.data?.stationState,
				})
				setSysNodeType(res?.data?.data?.stationType)
				setSelectedStationNode(res?.data?.data)
				setSystemVisible(true);
			}
		})

	}
	
	const handleChange1 = (e) =>{
		setNodeId(e.toString())
	}
	// 查看
	const look =(e) =>{
		setEdit(3);
		let formData = new FormData()
		formData.append('nodeId', e?.stationId);
		http.post('stationNode/viewFunction',formData).then(res =>{
			if(res?.data?.code === 200){
				setSelectedStationNode(res?.data?.data)
				console.log(res?.data?.data)
				setLookVisible(true);
			}
		})
		// setNewname(e.name);
		// setNodeTypeName(e.nodeTypeName);
		// setSystemNames(e.systemNames);
		// setNodeArea(e.nodeArea);
		// setNewaddress(e.address);
		// setLatitude(e.latitude);
		// setLongitude(e.longitude);
		// setNoHouseholds(e.noHouseholds)
	}

	// 查看
	const sysLook =(e) =>{
		setEdit(3);
		let formData = new FormData()
		formData.append('nodeId', e?.parentId);
		http.post('stationNode/viewFunction',formData).then(res =>{
			if(res?.data?.code === 200){
				const parentId = res?.data?.data[0]?.stationId
				const children = res?.data?.data[0]?.children
				const item = children.filter((item) => {
				   return item.stationId === e.stationId
				})
				setSelectedStationNode(item[0])
				setSysLookVisible(true);
			}
		})
		// setNewname(e.name);
		// setNodeTypeName(e.nodeTypeName);
		// setSystemNames(e.systemNames);
		// setNodeArea(e.nodeArea);
		// setNewaddress(e.address);
		// setLatitude(e.latitude);
		// setLongitude(e.longitude);
		// setNoHouseholds(e.noHouseholds)
	}
	
	// 
	const edithide =() =>{
		setIsModalVisible(false)
	}
	// 编辑取消
	const editCancel =() =>{
		setIsModalVisible(false)
	}

	function extractTree(data) {
		const result = (data || []).map(node => {
			return {
				label: node.regionName,
				value: node.regionId,
				children: node?.children && node?.children?.length > 0 ? extractTree(node.children) : []
			  }
		});

		return result
	  }
	// 省、自治区、直辖市基本信息
	const regionProvinces=() =>{
		http.post('stationNode/regionalTreeQuery').then(res =>{

			if(res.data.code===200){
				const nodes = res.data.data
				const data = extractTree(nodes)
				setRegionList(data)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 省份选择
	const regionChange =(val,item) =>{		
		setProvinceRegionId(val);
		setProvinceRegionName(item.children);
		setCityValue('市');
		setAreaValue('区');
		setAreaList([]);
		myForm.setFieldsValue({
			cityRegionId:'',
			countyRegionId:''
		
		})
	}
	// 城市选择
	const cityChange =(val,item) =>{
		console.log(val,item)
		
		setCityRegionId(val);
		setCityRegionName(item.children);
		setCityValue(val);
		setAreaValue('区');
		setMapLable(item.children);
		setIstrue(true);
		setNewaddress1(provinceRegionName+item.children);
	}
	// 县选择
	const areaChange =(val,item) =>{
		setAreaValue(val);
		setCountyRegionName(item.children);
	}

	// 查看取消
	const lookCancel =() =>{
		setLookVisible(false)
	}
	const lookSysCancel = () => {
		setSysLookVisible(false)
	}
	// 选择节点
	const noedChange =(val,data) =>{
		setNodename(data.children)
	}
	// 查询
	const SearchName =(val) =>{
		console.log(val)
		setPage(1);
		setCurrentNum(1)
		setSearchName(val)
		nodeListPageable(val,stationTypeIdArr,stationStateArr)
	}
	
	const ChildrenChange=()=>{
		setIstrue(false)
	}

	const addSystemNode = (record,val) => {
		setParentId('')
		if(!val){
			setSysNodeType('')
		}
		setSelectedStationNode(record)
		setSystemVisible(true);
	}
		
	const autoMaticMapping = (record) => {
		http.post('point_model/auto_bind',{
			"stationId":record?.stationId
		}).then(res =>{
			if(res?.data?.code === 200){
				message.success("映射成功")
				getnodeTypeList()
				getsys()
				getNodeStageList()
				nodeNameList()
				regionProvinces()
				getSysDeviceCategoryQuery()
				getSysNodeTypeList()
				getSysNodeStageList()
			}else{
				message.error("映射失败")
			}
		})
	}

	const columns = [
		{
			title: '节点名称',
			dataIndex: 'stationName',
			key: 'stationName',
			// width: 300,
			// render: text => <div className="min-width-columnstationName">{text}</div>,
		},
		{
			title: '节点分类',
			dataIndex: 'stationCategory',
			key: 'stationCategory',
			width: 100,
		},
		{
			title: '节点类型',
			dataIndex: 'stationType',
			key: 'stationType',
			// width: 200,
		},
		{
			title: '设备分类',
			dataIndex: 'systemNames',
			key: 'systemNames',
			render: text => <div className="min-width-column">{text}</div>,
		},
		// {
		// 	title: '数据点',
		// 	dataIndex: 'mappings',
		// 	key: 'mappings',
		// 	width:'10%',
		// 	render: (text,record,_,action) =>{
		// 		console.log(text,record,_,action)
		// 		let returnStr = ''
		// 		if(record?.mappings && record?.mappings.length){
		// 			record?.mappings.forEach((item) => {
		// 				returnStr += item?.modelNameZh +'、'
		// 			})
		// 		}
		// 		return returnStr;
		// 	}
		// },
		{
			title: '节点阶段',
			dataIndex: 'stationState',
			key: 'stationState',
			width: 100,
		},
		{
			title: '分时电价',
			dataIndex: 'timerPrice',
			key: 'timerPrice',
			width: 100,
			render: (text,record,_,action) =>{
				if(record.stationCategory==='项目'){
					return 	<Space size="middle">
							<a onClick={(e) => {
								history.push('/HybridElectricityPrice', 
								{id: record.stationId ,
									name:record.stationName
								});
		
							}}>编辑</a>
						</Space>
				}else{
					return null
				}
				
			}
		},
		{
			title: '操作',
			dataIndex: 'operate',
			key: 'operate',
			width: 500,
			render: (text,record,_,action) =>{
				if(record?.stationCategory ==='项目'){
					return 	<Space size="middle">
								<a style={{cursor:'pointer',pointerEvents:'auto'}} onClick={() => {
									setIschildProject(true)
									setEdit(1)
									setSysDefaultValue([])
									setSysNodeType(record?.stationType)
									systemForm.resetFields()
									systemForm.setFieldsValue({
										stationName:record?.stationName,
										sysIds:['nengyuanzongbiao'],
										nodeTypeId:record?.stationTypeId,
									})
									
									addSystemNode(record,true)
								}}>新建子项目节点</a>
								<a style={{cursor:'pointer',pointerEvents:'auto'}} onClick={() => {
									setEdit(1)
									setSysDefaultValue([])
									systemForm.resetFields()
									systemForm.setFieldsValue({
										stationName:record?.stationName,
										sysIds:[],
									})
									setIschildProject(false)
									addSystemNode(record,false)
								}}>新建系统节点</a>
								<a style={{cursor:'pointer',pointerEvents:'auto'}} onClick={() => look(record)}>查看</a>
								<a style={{cursor:'pointer',pointerEvents:'auto'}}  onClick={() => editcell(record)}>编辑</a>
								<a style={{cursor:'pointer',pointerEvents:'auto'}}  onClick={() => delet(record)}>删除</a>
								<a style={{cursor:'pointer',pointerEvents:'auto'}}  onClick={() => autoMaticMapping(record)}>自动映射</a>
							</Space>
				}else {
					return <Space size="middle">
							<a style={{cursor:'pointer',pointerEvents:'auto'}} onClick={() => {
									setEdit(1)
									setSysDefaultValue([])
									systemForm.resetFields()
									systemForm.setFieldsValue({
										stationName:record?.stationName,
										sysIds:[]
									})
									setIschildProject(false)
									addSystemNode(record,false)
							}}>新建系统节点</a>
							<a style={{cursor:'pointer',pointerEvents:'auto'}}  onClick={() => {
								console.log(record)
								setIschildProject(record.stationCategory==='子项目'?true:false)
								sysEditcell(record)
								
							}}>编辑</a>
							<a style={{cursor:'pointer',pointerEvents:'auto'}}  onClick={() => sysDelet(record)}>删除</a>
							<a style={{cursor:'pointer',pointerEvents:'auto'}}  onClick={() => autoMaticMapping(record)}>自动映射</a>
						</Space>
				}
			}
		},
		
	];

	const [provinceRegion,setProvinceRegion] = useState({})
	const [cityRegion,setCityRegion] = useState({})
	const [countyRegion,setCountyRegion] = useState({})
	const [nodeType,setNodeType] = useState('')
	const [sysNodeType,setSysNodeType] = useState('')
	// 新建
	const onFinish = (values) => {
		const { address,latitude,longitude,noHouseholds,nodeArea,nodeName,nodeTypeId,stationState,sysIds,vol,etype,basicBill ,electricityCompany} = values
		setSetLoadings(true)
		if(provinceRegion?.name === addressInfo.province && cityRegion?.name && addressInfo.city && countyRegion?.name === addressInfo.district){
			if(edit === 0){
				http.post('stationNode/editProjectNode',{
					"nodeId":selectedStationNode?.stationId,
					address,
					latitude,
					longitude,
					noHouseholds,
					nodeArea,
					nodeName,
					nodeTypeId,
					stationState,
					sysIds,
					"provinceRegionId": provinceRegion?.id,
					"provinceRegionName": provinceRegion?.name,
					"cityRegionId": cityRegion?.id,
					"cityRegionName": cityRegion?.name,
					"countyRegionId":countyRegion?.id,
					"countyRegionName": countyRegion?.name,
					nodeType,
					vol,etype:useElectricType,
					basicBill,
					electricityCompany
				}).then(res =>{
					if(res.data.code === 200){
						setIsModalVisible(false)
						getnodeTypeList()
						getsys()
						getNodeStageList()
						nodeNameList()
						regionProvinces()
						nodeListPageable(searchName,stationTypeIdArr,stationStateArr)
					}else{
						message.error(res.data.msg)
					}
				})
			}else{
				http.post('stationNode/addProjectNode',{
					address,
					latitude,
					longitude,
					noHouseholds,
					nodeArea,
					nodeName,
					nodeTypeId,
					stationState,
					sysIds,
					"provinceRegionId": provinceRegion?.id,
					"provinceRegionName": provinceRegion?.name,
					"cityRegionId": cityRegion?.id,
					"cityRegionName": cityRegion?.name,
					"countyRegionId":countyRegion?.id,
					"countyRegionName": countyRegion?.name,
					nodeType,
					vol,etype:useElectricType,basicBill,
					electricityCompany
				}).then(res =>{
					if(res.data.code === 200){
						setIsModalVisible(false)
						getnodeTypeList()
						getsys()
						getNodeStageList()
						nodeNameList()
						regionProvinces()
						nodeListPageable(searchName,stationTypeIdArr,stationStateArr)
					}else{
						message.error(res.data.msg)
					}
				})
			}
		}else{
			debugger
			message.info("请选择相同的省份城市")
		}
		
	}
	const normFile = (e: any) => {
		if (Array.isArray(e)) {
			return e;
		}
		return e?.fileList;
	}
	// 多选
	const handleChange = (value) => {
		let values = value.toString()
		let list = []
		list.push(values)
		var splitAdd = values.split(",");
		if(splitAdd.length==1){
			if(splitAdd[0] =='nengyuanzongbiao'){
				setNewVla(['nengyuanzongbiao']);
				setValList([])
			}
		}
		for(var i=0;i<splitAdd.length;i++){
			value = splitAdd[i]
			if(value =='guangfu'){
				return Modal.info({
					title: '提示',
					content: "选择光伏系统后，节点类型将锁定，无法再选择其他系统，如需添加其他系统，请创建新的节点",
					onOk: function () {
						myForm.setFieldsValue({
							system:["nengyuanzongbiao","guangfu"]
						
						})
					}
				});
			}else if(value =='chuneng'){
				return Modal.info({
					title: '提示',
					content: "选择储能系统后，节点类型将锁定，无法再选择其他系统，如需添加其他系统，请创建新的节点",
					onOk: function () {
						myForm.setFieldsValue({
							system:["nengyuanzongbiao","chuneng"]
						
						})
					}
				});
			}
			if(splitAdd[i]=='guangfu'){
				value = 'guangfu'
				
						return	myForm.setFieldsValue({
								system:["nengyuanzongbiao","guangfu"]
							
							})
						
				
				
			}else if(splitAdd[i]=='chuneng'){
				
						return	myForm.setFieldsValue({
								system:["nengyuanzongbiao","chuneng"]
							
							})
				
				
				
			}
		}
	};
	
	const handlePagination =(page) =>{		
		setPage(page);
		setCurrentNum(page);
		nodeListPageable(searchName,stationTypeIdArr,stationStateArr,page)
	}

	const onFinishFailed = (errorInfo) => {
		console.log('Failed:', errorInfo);

	};

	// 选择节点地址
	const onChangeAddress =(val,va2) =>{
		setProvinceRegion({name:va2[0].label,id:va2[0].value})
		setCityRegion({name:va2[1].label,id:va2[1].value})
		setCountyRegion({name:va2[2].label,id:va2[2].value})
		queryCityInfo(va2[0].label,va2[1].label)
		if(va2[0].label==='河北省'){
			queryElectricityCompany(va2[0].label)
			myForm.setFieldsValue({
				electricityCompany:''
			})
		}else{
			myForm.setFieldsValue({
				etype:'',
				vol:'',
				basicBill:''
			})
		}
		
	}

	const systemonFinish =(values) =>{
		const { nodeName,nodeTypeId,sysIds,stationState } = values
		setSetLoadings(true)
		if(edit === 0){
			http.post('stationNode/editSysNode',{
				nodeId:parentId,
				sysNodeId: selectedStationNode?.stationId,
				nodeName,
				nodeTypeId,
				nodeType:sysNodeType,
				sysIds,
				stationState,
				nodeType:sysNodeType,
				stationCategory:isChildProject ? "子项目":"系统"
			}).then(res =>{
				if(res.data.code === 200){
					setSystemVisible(false)
					getnodeTypeList()
					getsys()
					getNodeStageList()
					nodeNameList()
					regionProvinces()
					nodeListPageable(searchName,stationTypeIdArr,stationStateArr)
				}else{
					message.error(res.data.msg)
				}
			})
		}else{
			http.post('stationNode/addSysNode',{
				nodeId:selectedStationNode?.stationId,
				nodeName,
				nodeTypeId,
				nodeType:sysNodeType,
				sysIds,
				stationState,
				stationCategory:isChildProject ? "子项目":"系统"
			}).then(res =>{
				if(res.data.code === 200){
					setSystemVisible(false)
					getnodeTypeList()
					getsys()
					getNodeStageList()
					nodeNameList()
					regionProvinces()
					nodeListPageable(searchName,stationTypeIdArr,stationStateArr)
				}else{
					message.error(res.data.msg)
				}
			})
		}
	}

	const systemhide =() =>{
		setSystemVisible(false);
		systemForm.resetFields()
	}

	const rowSelection: TableRowSelection<DataType> = {
	  onChange: (selectedRowKeys, selectedRows) => {
	    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
	  },
	  onSelect: (record, selected, selectedRows) => {
	    console.log(record, selected, selectedRows);
	  },
	  onSelectAll: (selected, selectedRows, changeRows) => {
	    console.log(selected, selectedRows, changeRows);
	  },
	};

	const urlParams = new URLSearchParams(window.location.search);

	const [selectNodeTypeState,setSelectNodeTypeState] = useState(false)
	const [selectNodeType,setSelectNodeType] = useState([])
	const [selectNodeStageState,setSelectNodeStageState] = useState(false)
	const [selectNodeStage,setSelectNodeStage] = useState([])

	const [stationTypeIdArr,setStationTypeIdArr] = useState([])
	const [stationStateArr,setStationStateArr] = useState([])
	const [isQuery,setIsQuery] = useState(false)
	const ref1 = useRef()

	let status = ''
	switch (selectedStationNode?.stationState){
		case '建设中':
			status = 'build'
			break
		case '运营中':
			status = 'operate'
			break
		case '已关闭':
			status = 'close'
			break
		case '规划中':
			status = 'plan'
			break
		default:
	}

	const [useElectricTypeOptions,setUseElectricTypeOptions] = useState([])
	const [useVoltageOptions,setUseVoltageOptions] = useState([])
	
	// 获取用电类型
	
	const queryCityInfo =(provinceRegion,cityRegion) =>{
		http.get('stationNodeEP/queryCityInfo?city='+cityRegion +'&province='+provinceRegion).then(res =>{
			console.log(res)
			if(res.data.code===200){
				const newData = addLabelProperty(res.data.data);
				const dataWithTwoLevels = removeThirdLevel(newData);
				setUseElectricTypeOptions(dataWithTwoLevels)
				setUsetypeList(newData)
				if(edit===0){
					function splitString(str) {
					    return str.split('-');
					}
					const parts = splitString(etypename);
					const lastChild = findLastChildByValues(newData, parts[0], parts[1]);
					setUseVoltageOptions(lastChild)
				}
			}
			
		}).catch(err =>{
			console.log()
		})
	}
	const removeThirdLevel =(options) => {
	  return options.map(option => {
	          const { children, ...rest } = option;
	          return {
	              ...rest,
	              children: children.map(child => {
	                  const { children: childChildren, ...childRest } = child;
	                  return childRest;
	              })
	          };
	      });
	}
	const addLabelProperty=(data) => {
		return data.map(item => {
			const newItem = {...item, label: item.value};
			if (item.children) {
				newItem.children = addLabelProperty(item.children);
			}
			return newItem;
		});
	}
	const findLastChildByValues=(data, type1Value, type2Value) => {
	    for (const item of data) {
	        if (item.value === type1Value) {
	            for (const child of item.children) {
	                if (child.value === type2Value) {
	                    return child.children
	                }
	            }
	        }
	    }
	    return null;
	}
	// 
	const queryElectricityCompany =(val) =>{
		http.get('stationNodeEP/queryElectricityCompany?province='+val).then(res =>{
			console.log(res)
			if(res.data.code===200){
				const processedItems = res?.data?.data.map(item =>{
					return {
					    value: item ,
						label:item
					};
				})
				setElectricityCompanyList(processedItems)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	

	return(
		<>
		
				<div className='hybrid-header'>
					<div className='query1'>
						节点类型：
						<Select	
							style={{width:'200px',minHeight:'32px'}}
							placeholder="请选择节点"
							mode="tags"
							onSelect={(val,option) => {
								if(val === ''){
									setSelectNodeType([''])
									setStationTypeIdArr([])
								}
							}}
							maxTagCount={'responsive'}
							onChange={(_val,val1)=> {
								let arr = val1.map((item) => item.value)
								if(arr.length === nodeTypeList.length){
									setSelectNodeTypeState(true)
									setStationTypeIdArr([])
									setSelectNodeType([''])
								}else{
									setSelectNodeType(arr)
									setStationTypeIdArr(arr)
								}
								if(arr.length > 1 && arr.includes('')){
									arr = arr.filter(item => item !== ''); 
									setSelectNodeType(arr)
									setStationTypeIdArr(arr)
								}
								// // else{
								// 	setSelectNodeTypeState(false)
								// 	setStationTypeIdArr(arr)
								// }
								// setSelectNodeType(arr)
							}}
							value={selectNodeType}

							// dropdownRender={allSelectValue => (
							// 	<div>
							// 		<div style={{ cursor: 'pointer',display:'flex',justifyContent:'space-between',alignItems:'center',padding:'5px 12px',background:selectNodeTypeState ? 'rgba(0, 146, 255, 0.15)': 'none' }} onClick={() => {
							// 			setSelectNodeTypeState(!selectNodeTypeState)
							// 			setStationTypeIdArr([])
							// 			if(!selectNodeTypeState){
							// 				const arr = nodeTypeList.map(item => item.id)
							// 				setSelectNodeType(arr)
							// 			}else{
							// 				setSelectNodeType([])
							// 			}
							// 		}}>
							// 		全选
							// 		{selectNodeTypeState && <span role="img"  aria-label="check" className="anticon anticon-check"><svg color='rgb(0, 146, 255)' viewBox="64 64 896 896" focusable="false" data-icon="check" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 00-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z"></path></svg></span>}
							// 		</div>
							// 		{allSelectValue}
							// 	</div>
							// 	)}
							// >
						>	
							{
								[{id:'',name:'全部'} ,...nodeTypeList].map((item,index) =>{
									return <Option key={index} value={item.id}>{item.name}</Option>
								})
							}
						</Select>
					</div>
					<div className='query2'>
						节点阶段：<Select
									style={{width:'200px',minHeight:'32px'}}
									allowClear={false}
									placeholder="请选择节点阶段"
									mode="tags"
									onSelect={(val,option) => {
										if(val === '全部'){
											setSelectNodeStage(['全部'])
											setStationStateArr([])
										}
									}}
									onChange={(_val,val1)=> {
										let arr = val1.map((item) => item.value)
										if(arr.length === nodeStageList.length){
											setSelectNodeStageState(true)
											setStationStateArr([])
											setSelectNodeStage(['全部'])
										}else{
											setSelectNodeStageState(false)
											setStationStateArr(arr)
											setSelectNodeStage(arr)
										}
										if(arr.length > 1 && arr.includes('全部')){
											arr = arr.filter(item => item !== '全部'); 
											setSelectNodeStage(arr)
											setStationStateArr(arr)
										}
									}}
									value={selectNodeStage}
									maxTagCount={'responsive'}
									// onChange={handleChange}
									// dropdownRender={allSelectValue => (
									// 	<div>
									// 		<div style={{ cursor: 'pointer',display:'flex',justifyContent:'space-between',alignItems:'center',padding:'5px 12px',background:selectNodeStageState ? 'rgba(0, 146, 255, 0.15)': 'none' }} onClick={() => {
									// 			setSelectNodeStageState(!selectNodeStageState)
									// 			setStationStateArr([])
									// 			if(!selectNodeStageState){
									// 				const arr = nodeStageList.map(item => item.value)	
									// 				setSelectNodeStage(arr)
									// 			}else{
									// 				setSelectNodeStage([])
									// 			}
									// 		}}>
									// 		全选
									// 		{selectNodeStageState && <span role="img"  aria-label="check" className="anticon anticon-check"><svg color='rgb(0, 146, 255)' viewBox="64 64 896 896" focusable="false" data-icon="check" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 00-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z"></path></svg></span>}
									// 		</div>
									// 		{allSelectValue}
									// 	</div>
									// 	)}
									options={[{id:'',value:'全部'},...nodeStageList]}
								></Select>
					</div>
					<Button className='query-btn1' type="primary" onClick={() => {
						setCurrentNum(1)
						nodeListPageable(searchName,stationTypeIdArr,stationStateArr,1)
					}}>查询</Button>
				</div>
				<div className="hybrid">
					<div className="hybridBody">
						<div className="electrictitle1" style={{display:'flex',justifyContent:'space-between',alignContent:'center'}}>
							<Button type="primary" style={{'cursor':'pointer'}}  onClick={showModal}><PlusOutlined />新建项目节点</Button>
							<div style={{display:'flex',justifyContent:'space-between',alignContent:'center'}}>
								{/* <Checkbox style={{alignItems:'center',marginRight:"15px"}} checked={checked1} onChange={() => {
									setChecked1(!checked1)
									refreshNodeListPageable(searchName,!checked1,checked2)
								}}>建设中</Checkbox>
								<Checkbox style={{alignItems:'center',marginRight:"15px"}} checked={checked2} onChange={() => {
									setChecked2(!checked2)
									refreshNodeListPageable(searchName,checked1,!checked2)
								}}>运营中</Checkbox> */}
								<Search placeholder="搜索节点名称" style={{float:'right',width: 200 }} onChange={(e) => {
									setNodeName(e.target.value)
									setSearchName(e.target.value)
								}} onSearch={SearchName} />
							</div>
							
						</div>
						<div style={{marginTop:16}} className="hybridname">
							<Table 
								dataSource={dataSource}
								columns={columns}
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
								rowKey={record => record.id}
								loading={loading} 
								scroll={{
									x: 'max-content',
								}}
							/>
						
						</div>

						<Modal title={edit==0?'编辑':'新建项目节点'} visible={isModalVisible}
							onCancel={handleCancel}
							width={696}
							cancelText="取消"
							okText="确定"
							maskClosable={false}
							className='new-modal'
							footer={null}>
							
							<Form
								name="basic4"
								labelCol={{ span: 4 }}
								wrapperCol={{ span: 20 }}
								onFinish={onFinish}
								onFinishFailed={onFinishFailed}
								form={myForm} 
								className="basic1"
								
							>
								<Form.Item label="项目名称" name="nodeName"
									style={{marginLeft:'-32px'}}
									rules={ [{ required: true,pattern: new RegExp(/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,16}$/),message: '请输入6～16个汉字、字母或数字、_' }]}
									validateTrigger="onBlur"					   
								>
									
									<Input maxLength={50} placeholder='请输入项目名称'/>
								</Form.Item>
								<Form.Item label="节点类型" style={{marginLeft:'-32px'}} name="nodeTypeId" rules={[{ required: true, message: '请选择节点类型' }]}>
									<Select
										placeholder="请选择节点"
										onChange={(_val,val1)=> {
											setNodeType(val1?.children)
										}}>
										{
											nodeTypeList.map((item,index) =>{
												return <Option key={index} value={item.id}>{item.name}</Option>
											})
										}
									</Select>
								</Form.Item>
								<Form.Item label="设备分类" required style={{marginLeft:'-32px'}}>
									<Form.Item
										name="sysIds"
										rules={[{ required: true, message: '请选择设备分类'}]}>
										<Select
											mode="multiple"
											allowClear={false}
											style={{ width: '100%' }}
											placeholder="请选择设备分类"
											defaultValue={defaultValue}
											// onChange={handleChange}
										>
											{
												options.map((item,index) =>{
												return <Option key={index} value={item.systemKey}>{item.systemName}</Option>
											})
										}
										</Select>
						
									</Form.Item> 
									{/* <span className="InfoCircle" >
										<InfoCircleOutlined style={{float:'left',marginTop:10}} />
										
										{
											osType=='loadType'?(
											<h4>
												1、能源总表为节点默认系统，不能删除<br />
												</h4>
											):(
											<h4>
												1、当节点类型为”光伏电站“时，系统中应包含“光伏系统”，否则此节点将被做为负荷资源<br />
												2、当节点类型为”储能电站“时，系统中应包含“储能系统”，否则此节点将被做为负荷资源
											</h4>
											)
										}
										
									</span> */}
								</Form.Item>
								<Form.Item label="节点阶段" required style={{marginLeft:'-32px'}}>
									<Form.Item
										name="stationState"
										rules={[{ required: true, message: '请选择节点阶段'}]}>
										<Select
											allowClear={false}
											style={{ width: '100%' }}
											placeholder="请选择节点阶段"
											// onChange={handleChange}
											options={nodeStageList}
										/>
									</Form.Item> 
								</Form.Item>
								<Form.Item label='节点地址' required style={{marginLeft:'-32px'}}>
									<Cascader value={provinceRegion?.name && cityRegion?.name && countyRegion?.name && [provinceRegion?.name, cityRegion?.name, countyRegion?.name]}
											 options={regionList} onChange={onChangeAddress} placeholder="请选择省/市/区县" />
									<Form.Item style={{marginTop:12}}>
										<Form.Item required >
											
											<Form.Item name="address"  required
												rules={ [{ required: true,message:'请输入地址'}]}
											>	
												<BaiduMap getMap={getMap} address={address} 
													istrue={istrue}
													mapLable={mapLable}
													isModalVisible={isModalVisible}
													cityVale={cityVale}
													changeData={ChildrenChange}
													setAddressInfo={setAddressInfo}
												/>
											</Form.Item>
										</Form.Item>
										
										<Form.Item style={{marginTop:'-1px'}}>
											
											<Form.Item  name="longitude"
												style={{ display: 'inline-block', width: '50%' }}
												rules={ [{ required: true,pattern: new RegExp(/^(\-|\+)?(((\d|[1-9]\d|1[0-7]\d|0{1,3})\.\d{0,15})|(\d|[1-9]\d|1[0-7]\d|0{1,3})|180\.0{0,15}|180)$/),message: '请输入正确格式的经度' }]}
											>
												{/* longitude */}
												<Input maxLength={50} placeholder='经度' />							
											</Form.Item>
											<Form.Item  name="latitude"
												rules={ [{ required: true,pattern: new RegExp(/^(\-|\+)?(((\d|[1-9]\d|1[0-7]\d|0{1,3})\.\d{0,15})|(\d|[1-9]\d|1[0-7]\d|0{1,3})|180\.0{0,15}|180)$/),message: '请输入正确格式的纬度' }]}
												style={{ display: 'inline-block', width: '50%',float: 'right' }}
											>
												{/* latitude */}
												<Input maxLength={50} placeholder='纬度' />
											</Form.Item>
										
										</Form.Item>
																
									</Form.Item>
									
								</Form.Item>
								{
									provinceRegion.name==='河北省'?<Form.Item label="供电公司" style={{marginLeft:'-32px'}} 
									name="electricityCompany" rules={[{ required: true, message: '请选择供电公司' }]}>
										<Select
											options={electricityCompanyList} 
											onChange={(_val,val1) => {
												if(useElectricType?.includes('两部制') ){
													myForm.setFieldsValue({
														basicBill:undefined
													})
												}
												
												
											}}
										/>
									</Form.Item>:null
								}
								{					
									<>
									<Form.Item label="用电类型" style={{marginLeft:'-32px'}} name="etype" rules={[{ required: true, message: '请输入用电类型' }]}>
										<Cascader className="base-info-select" 
											options={useElectricTypeOptions} 
											// value={useElectricType} 
											// defaultValue={useElectricType}
											onChange={(_val,val1) => {
												setUseElectricType(`${_val[0]}-${_val[1]}`)
												function splitString(str) {
												    return str.split('-');
												}
												const parts = splitString(`${_val[0]}-${_val[1]}`);
												const lastChild = findLastChildByValues(usetypeList, parts[0], parts[1]);
												setUseVoltageOptions(lastChild)
												myForm.setFieldsValue({
													vol:undefined
												})
												
											}}
											displayRender={(labels) => labels.join("-")}
										/>
									</Form.Item>
									<Form.Item label="电压等级" style={{marginLeft:'-32px'}} name="vol" rules={[{ required: true, message: '请输入电压等级' }]}>
										<Select
											options={useVoltageOptions} 
											onChange={(_val,val1) => {
												if(useElectricType?.includes('两部制') ){
													myForm.setFieldsValue({
														basicBill:undefined
													})
												}
												
												
											}}
										/>
									</Form.Item>
									</>				
								}
								{
									useElectricType?.includes('两部制') ? 
									<Form.Item label="基本电费" required style={{marginLeft:'-32px'}} name="basicBill"
									rules={[{ required: useElectricType?.includes('两部制')?true:false, message: '请选择基本电费'}]}
									>	
										<Select
											placeholder="请选择基本电费"
											
											options={[
												{ value: '按容收费', label: '按容收费' },
												{ value: '按需收费', label: '按需收费' },
											]}
										>	
										</Select>							
									</Form.Item> : null
								}
								<Form.Item label="建筑面积" required style={{marginLeft:'-32px'}}>	
									<Form.Item  name="nodeArea"
										rules={ [{ required: true,  pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}
										validateTrigger="onBlur"
										style={{width:'calc(100% - 38px)',display:'inline-block'}}
									>

										<Input maxLength={50} placeholder='请输入建筑面积' />							
									</Form.Item>
									<span className="hybrid_logn">m²</span>											
								</Form.Item>

								<Form.Item label="节点户号" 
									style={{marginLeft:'-32px'}}
									validateTrigger="onBlur"
									rules={ [{ required: true, pattern: new RegExp(/^\d*(\.\d{1,20})?$/),message: '请输入正确的户号' }]}  name="noHouseholds" >
									<Input maxLength={50} placeholder='请输入户号' />							
								</Form.Item>
								<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
									<Button ghost onClick={nodehide}>
										取消
									</Button>
									<Button type="primary" htmlType="submit">
										确定
									</Button>
									
								</Form.Item>
							</Form>    
						</Modal>



						<Modal title="查看" visible={lookVisible}
							onCancel={lookCancel}
							width={520}
							cancelText="取消"
							centered={true}
							okText="确定"
							className="hybrid-modal"
							footer={null}>

							<div className='node-model-wrap'>
								<div className='project-name'>
									{selectedStationNode?.stationName}
									{selectedStationNode?.stationState && <span className={status}>{selectedStationNode?.stationState}</span>} 
								</div>
								<div className='project-name-info'>
									<p><span>节点类型：{selectedStationNode?.stationType}</span></p>
									<p><span>设备分类：{selectedStationNode?.systemNames}</span></p>
									<p><span>节点户号：{selectedStationNode?.noHouseholds}</span></p>
									<p><span>用电类型：{selectedStationNode?.etype}</span></p>
									<p style={selectedStationNode?.etype === '单一制' ? {marginBottom:0}:{}}><span>电压等级：{selectedStationNode?.vol}</span></p>
									{selectedStationNode?.etype === '两部制' ? <p style={{marginBottom:0}}><span>基本电费：{selectedStationNode?.basicBill}</span></p> : null} 
								</div>
								<div className='base-info'>
									基本信息
								</div>
								<div className='project-name-info'>
									<p><span>建筑面积：{selectedStationNode?.nodeArea}m²</span></p>
									<p><span>节点地址：{selectedStationNode?.address}</span></p>
									<p style={{marginBottom:0}}><span>经纬度：{selectedStationNode?.longitude}，{selectedStationNode?.latitude}</span></p>
								</div>
							</div>
						</Modal>

						<Modal title={edit==0?'编辑':isChildProject ? '新建子项目节点' : '新建系统节点' } open={systemVisible} centered={true} footer={null}
							onCancel={systemhide}
							className='new-sys-modal'
						>
							<Form
								name="basic4"
								onFinish={systemonFinish}
								onFinishFailed={onFinishFailed}
								form={systemForm}
								className="basic1"
								
							>
								<Form.Item
									label="上级节点"
									name="stationName"
									style={{marginLeft:"10px"}}
								>
									<Input  disabled className='disabled-input' />
								</Form.Item>
								<Form.Item name="nodeName" label= {isChildProject ? '项目名称' :"系统名称"} rules={ [{ required: true,pattern: new RegExp(/^[a-zA-Z0-9_\u4e00-\u9fa5]{6,16}$/),message: '请输入6～16个汉字、字母或数字、_' }]}>
									<Input  placeholder={isChildProject ? '请输入项目节点名称' :"请输入系统节点名称"} />
								</Form.Item>
								<Form.Item label="节点类型" name="nodeTypeId" rules={[{ required: true, message: '请选择节点类型' }]}>
									{
										!isChildProject?(
											<Select
												placeholder="请选择节点类型"
												disabled={isChildProject?true:false}
												onChange={(_val,val1)=> {
													setSysNodeType(val1?.children)
													let str = ''
													if(val1?.children.length > 2){
														str = val1?.children.slice(0, -2); // 删除最后两个字符
													}else{
														str = val1?.children
													}
													const arr = sysOption.filter((item) => {
														return item.systemName?.includes(str)
													})
											
													setSysDefaultValue(['nengyuanzongbiao',arr[0]?.systemKey])
											
													systemForm.setFieldsValue({
														sysIds:['nengyuanzongbiao',arr[0]?.systemKey]
													})
												}}>
												{
													sysNodeTypeList.map((item,index) =>{
														return <Option key={index} value={item.nodeTypeKey}>{item.nodeTypeName}</Option>
													})
												}
											</Select>
										):(
										<Select
											placeholder="请选择节点类型"
											disabled={isChildProject?true:false}
											onChange={(_val,val1)=> {
												setSysNodeType(val1?.children)
												let str = ''
												if(val1?.children.length > 2){
													str = val1?.children.slice(0, -2); // 删除最后两个字符
												}else{
													str = val1?.children
												}
												const arr = sysOption.filter((item) => {
													return item.systemName?.includes(str)
												})
										
												setSysDefaultValue(['nengyuanzongbiao',arr[0]?.systemKey])
										
												systemForm.setFieldsValue({
													sysIds:['nengyuanzongbiao',arr[0]?.systemKey]
												})
											}}>
											{
												nodeTypeList.map((item,index) =>{
													return <Option key={index} value={item.id}>{item.name}</Option>
												})
											}
										</Select>
											
										)
									}
									
								</Form.Item>
								<Form.Item
									name="sysIds"
									label="设备分类" 
									rules={[{ required: true, message: '请选择设备分类'}]}>
										<Select
											mode="multiple"
											allowClear={false}
											style={{ width: '100%' }}
											placeholder="请选择设备分类"
											defaultValue={sysDefaultValue}
											// onChange={handleChange}
										>
											{
												sysOption?.map((item,index) =>{
												return <Option key={index} value={item.systemKey}>{item.systemName}</Option>
											})
										}
										</Select>
								</Form.Item> 
								<Form.Item
									label="节点阶段"
									name="stationState"
									rules={[{ required: true, message: '请选择节点阶段'}]}>
									<Select
										allowClear={false}
										style={{ width: '100%' }}
										placeholder="请选择节点阶段"
										// onChange={handleChange}
										options={sysNodeStageList}
									/>
								</Form.Item> 
								<Form.Item wrapperCol={{ offset: 14, span: 10 }} style={{textAlign:'right'}}>
									<Button ghost onClick={systemhide}>
									取消
									</Button>
									<Button type="primary" htmlType="submit">
										确定
									</Button>
									
								</Form.Item>
							</Form>
						</Modal>
					</div>
				</div> 
			
		</>
	
	
	)
}
	

export default Hybrid



















