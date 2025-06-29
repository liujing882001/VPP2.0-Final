import React,{useEffect,useState,useCallback} from 'react'
import './index.css'
import './index.scss'
import { Tree,DatePicker,ConfigProvider,Button,Table,Input,Space ,Upload ,message
 ,Select ,Typography,Modal,Form,Spin ,Cascader ,ResizeBox  } from 'antd';
import type { TreeDataNode } from 'antd';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import Icon, { UploadOutlined,SyncOutlined,InfoCircleOutlined,MenuUnfoldOutlined,MenuFoldOutlined } from '@ant-design/icons';
import http from '../../../server/server.js'
import type { UploadProps } from 'antd';
import type { GetProps } from 'antd';
import type { DatePickerProps } from 'antd';
import axios from 'axios'
// import Resizable from 're-resizable';
import { Resizable } from 're-resizable';

import devices from '../../../style/devices.xlsx'
import lou from './img/lou.svg'
import systemimg from './img/system.svg'
type CustomIconComponentProps = GetProps<typeof Icon>;

const { DirectoryTree } = Tree;
const { Text } = Typography;

const { Search } = Input;
const { Option } = Select;

const dateFormat = 'YYYY-MM-DD';
const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};
const formItemLayout1 = {
  labelCol: { span: 18 },
  wrapperCol: { span: 6 },
};
const { confirm } = Modal;
const provinceData = ['Zhejiang', 'Jiangsu'];
const cityData = {
  Zhejiang: ['Hangzhou', 'Ningbo', 'Wenzhou'],
  Jiangsu: ['Nanjing', 'Suzhou', 'Zhenjiang'],
};
const Equipment = () =>{
	const [currentIndex, setCurrentIndex] = useState(0);
	const [current, setCurrent] = useState(1);
	const [dateType, setDateType] = useState(1);
	const [expandedKeys, setExpandedKeys] = useState([]);
	const [searchValue, setSearchValue] = useState('');
	const [autoExpandParent, setAutoExpandParent] = useState(true);
	const [navList, setNavList] = useState(['全部', '虚拟电厂运营商', '电力用户']);
	const [treeData, setTreeData] = useState([]);
	const [dataSource, setDataSource] = useState([]);
	const [nodeId, setNodeId] = useState('');
	const [systemId, setSystemId] = useState('');
	const [nodeIds, setNodeIds] = useState([]);
	const [subSystemIds, setSubSystemIds] = useState('');
	const [pointViewList, setPointViewList] = useState([]);
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [columns1, setColumns1] = useState([]);
	const [dataSource1, setDataSource1] = useState([]);
	const [dataSource2, setDataSource2] = useState([]);
	const [visible, setVisible] = useState(false);
	const [visabled, setVisabled] = useState(false);
	const [startTime, setStartTime] = useState(null);
	const [visibleid, setVisibleid] = useState('');
	const [expandedKey, setExpandedKey] = useState('');
	const [cities, setCities] = useState([]);
	const [config, setConfig] = useState('');
	const [required, setRequired] = useState(true);
	const [device_rated_power, setDeviceRatedPower] = useState('');
	const [isShow, setIsShow] = useState('none');
	const [nodePostType, setNodePostType] = useState('');
	const [defaultExpandedRowKeys, setDefaultExpandedRowKeys] = useState('');
	const [treeLoading, setTreeLoading] = useState(false);
	const [selectedKeys1, setSelectedKeys1] = useState('');
	const [type, setType] = useState('');
	const [loading, setLoading] = useState(false);
	const [page, setPage] = useState(1);
	const [options, setOptions] = useState([]);
	const [deviceName, setDeviceName] = useState('');
	const [disableds, setDisableds] = useState('');
	const [fileList, setFileList] = useState([]);
	const [types, setTypes] = useState('');
	const [copyTree, setCopyTree] = useState([]);
	const [configKeyval, setConfigKeyval] = useState('');
	const [lodeList, setLodeList] = useState([]);
	const [loadProperties, setLoadProperties] = useState('');
	const [loadType, setLoadType] = useState('');
	const [loadLoading, setLoadLoading] = useState(false);
	const [nodeNameList,setNodeNameList] = useState([]);
	const [nodeSystemList,setNodeSystemList] = useState([]);
	const [secondCity,setSecondCity] = useState('');
	const [myForm] = Form.useForm();
	const [sysConfigTypeList,setSysConfigTypeList] = useState([]);
	const [visiblae,setVisiblae] = useState(false);
	const [treenum,setTreenum] = useState(0);
	const [collapsed,setCollapsed] = useState(false);
	const [total,setTotal] = useState(0)
	const [currentNum,setCurrentNum] = useState(1)
	const [expandDataSource,setExpandDataSource] = useState([]);
	const [expandedRowKeyslist, setExpandedRowKeyslist] = useState([]);
	const HeartSvg = () => (
	  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
	    <title>heart icon</title>
		<path d="M71.03 962.129h19.633V441.337l20.783-7.28 228.429-80.063V209.981l17.791-8.384L741.004 20.816l44.16-20.8v8.528l120.798 72.015v877.65h47.007v61.903H723.23V97.47L401.778 249.084v83.23l93.006-32.607L530.8 287.1l0.032-0.976 0.976 0.608 4.08-1.456v3.871l120.11 71.6V956.32h47.727v61.92H474.017V372.361l-321.5 112.67V1024H71.032v-61.871z m117.471 53.935h238.14V907.218c-45.103 2.944-87.806 5.744-128.27 8.416-38.463 2.48-75.07 4.88-109.87 7.168v93.262z m0-402.938c34.8-9.823 71.407-20.24 109.87-31.055a82813.447 82813.447 0 0 1 128.27-36.304V436.761c-45.103 15.696-87.806 30.592-128.27 44.687-38.463 13.408-75.07 26.128-109.87 38.272v93.406z m0 133.086l109.87-20.815 128.27-24.304V592.087c-45.103 11.52-87.806 22.383-128.27 32.703-38.463 9.824-75.07 19.136-109.87 28.048v93.374z m0 133.982c34.8-3.296 71.407-6.768 109.87-10.464 40.448-3.824 83.151-7.903 128.27-12.207V748.596l-128.27 20.656c-38.463 6.16-75.07 12.048-109.87 17.648v93.294z" ></path>	  </svg>
	);
	const HeartIcon = (props: Partial<CustomIconComponentProps>) => (
	  <Icon component={HeartSvg} {...props} />
	);
	const systemSvg = () => (
	  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
	    <title>heart icon</title>
		<path d="M524.480693 818.540998a27.430999 27.430999 0 0 1-13.715499-3.56603L38.129088 545.328252a27.430999 27.430999 0 1 1 27.430999-47.729938l459.194916 261.966037 436.975807-246.878988a27.430999 27.430999 0 0 1 27.430999 47.729938l-451.239927 254.559667a27.430999 27.430999 0 0 1-13.441189 3.56603z" p-id="5451"></path><path d="M524.480693 1023.999177a27.430999 27.430999 0 0 1-13.715499-3.56603L38.129088 750.786431a27.430999 27.430999 0 1 1 27.430999-47.729937l459.194916 261.966036 436.975807-246.878987a27.430999 27.430999 0 0 1 27.430999 47.729937L537.921882 1020.433147a27.430999 27.430999 0 0 1-13.441189 3.56603z" p-id="5452"></path><path d="M513.233983 599.915939a27.430999 27.430999 0 0 1-12.892569-3.29172L14.538429 337.949903a27.430999 27.430999 0 0 1 0-48.004248L499.518484 3.84034a27.430999 27.430999 0 0 1 27.430999 0l486.077295 286.105315a27.430999 27.430999 0 0 1 13.715499 24.413589 27.430999 27.430999 0 0 1-14.538429 23.590659l-486.077295 258.674316a25.785139 25.785139 0 0 1-12.89257 3.29172zM83.664546 312.439074l429.569437 229.048838 429.843748-229.048838-429.843748-253.188117z" p-id="5453"></path>
	</svg>
	);
	const SystemIcon = (props: Partial<CustomIconComponentProps>) => (
	  <Icon component={systemSvg} {...props} />
	);
	useEffect(() =>{
		tree()
		nodeTree()
		
	},[])
	useEffect(() =>{
		if(nodeIds){
			// chosetanle()
			devicePageByNL()
		}
		
	},[nodeId,systemId,page,nodeIds])
	const deep = (val) => {
	    if (val.children.length < 0) {
	        return
	    } else {
	        for (var i = 0; i < val.children.length; i++) {
				val.children[i].title = val.children[i].title +'（'+ val.children[i].deviceSize + '）'
				deep(val.children[i])
	        }
	    }
	}
	const extractNodes =(data) => {
		
	    return data.map(item => {
			const titleWithIcon = (
			      <span>
			        <img src={lou} style={{ width: '16px', height: '16px', marginRight: '8px' }} />
			        {item.nodeName}
			      </span>
			    );
			const ChildtitleWithIcon = (
			      <span>
			        <img src={systemimg} style={{ width: '16px', height: '16px', marginRight: '8px' }} />
			        {item.nodeName}
			      </span>
			    );
			if (item.children) {
				return {
					...item,
					title:`${item.nodeName}`,
					key: item.nodeId,
					children: extractNodes(item.children),
					label:`${item.nodeName}`,
					value:item.nodeId,					
					icon:item.parentId == ""?<HeartIcon />:<SystemIcon  />
				};
			} else {
				return {
					...item,
					title: `${item.nodeName}`,
					key: item.nodeId,
					label:`${item.nodeName}`,
					value:item.nodeId,
					icon:item.parentId == ""?<HeartIcon />:<SystemIcon  />
					
				};
			}
			
		});
		
	}
	// 获取左侧树
	const tree =() =>{		
		setTreeLoading(true)
		
		http.post('global/useNodeDevTree').then(res =>{
			if(res.data.code==200){
				const newData = extractNodes(res.data.data);
				setTreeData(newData);
				setTreeLoading(false);
				setCopyTree(newData)
				setOptions(newData)
				setTreenum(newData.length)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 获取导入设备节点
	const nodeTree =() =>{
		http.post('tree/nodeTree').then(res =>{
			if(res.data.code==200){
				let data = res.data.data
				let count = 0;
				const main = function(data) {
				    for (let i in data) {
				        data[i].label = data[i].title;
						data[i].value = data[i].id;
						
				        if (data[i].children) {
				            count++;
				            main(data[i].children);
				        }
				    }
				}
				main(data);
				// setOptions(data)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	//表格
	const chosetanle =() =>{
		setLoading(true);
		
		http.post('device/devicePage',{
			nodeId:nodeId,
			pageNum:page,
			pageSize:1
			
		}).then(res =>{
			if(res.data.code==200){
				let data = res.data.data.content
				setDataSource(res?.data?.data?.content);
				setLoading(false);
				setTotal(res.data.data.totalElements)
			}else{
				setLoading(false);
				message.info(res.data.msg)
			}
		})
	}
	const devicePageByNL =() =>{
		setLoading(true);
		
		http.post('device/devicePageByNL',{
			nodeIds:nodeIds,
			pageNum:page,
			pageSize:10
			
		}).then(res =>{
			if(res.data.code==200){
				let data = res.data.data.content
				setDataSource(res?.data?.data?.content);
				setLoading(false);
				setTotal(res.data.data.totalElements)
			}else{
				setLoading(false);
				message.info(res.data.msg)
			}
		})
	}
	const onExpandTbal = (expanded, record) => {
		if (expanded) {
		    setExpandedRowKeyslist([...expandedRowKeyslist, record.deviceId]);
			http.post('device/devicePageByNL',{
				nodeIds:nodeIds,
				pageNum:page,
				pageSize:10
				
			}).then(res =>{
				if(res.data.code==200){
					let data = res.data.data.content
					setDataSource(res?.data?.data?.content);
					setLoading(false);
					setTotal(res.data.data.totalElements)
				}else{
					setLoading(false);
					message.info(res.data.msg)
				}
			})
		} else {
		    setExpandedRowKeyslist(expandedRowKeyslist.filter(key => key !== record.deviceId) || []);
		}
		
		
	
		
		
		
	}
	
	// 系统列表
	const handleChange=(e) =>{
		if(e){
			setVisibleid(e)
			let nodeSystemList = []
			http.post('system_management/node_model/nodeSystemList?nodeId='+e[e.length-1]).then(res =>{
				if(res.data.code ==200){
					let data = res.data.data
					data.map(res =>{
						nodeSystemList.push({
							id:res.id,
							systemName:res.systemName
						})
					})
					
					setVisabled(true);
					setNodeSystemList(nodeSystemList);
					setSecondCity('')
					myForm.setFieldsValue({
						system:'',
						config_key:'',
						loadType:'',
						loadProperties:''
					})
					
				}
			})
		}else{
			setVisabled(true);
			setNodeSystemList([]);
			setSecondCity('')
			myForm.setFieldsValue({
				system:'',
				config_key:'',
				loadType:'',
				loadProperties:''
			})
		}
	}
	// 选择系统生设备类型
	const sysChange =(e) =>{
		http.post('system_management/energy_model/model_parameter/modelParameterTypeList?systemId='+e).then(res =>{
			if(res.data.code ==200){
				setSysConfigTypeList(res.data.data);
				if(nodePostType =='load' && e !='nengyuanzongbiao'){				
					setIsShow('flex')
				}
				myForm.setFieldsValue({
					config_key:'',
					loadType:'',
					loadProperties:''
				})
			}
		})
	}
	// 选择设备
	const configChange =(e) =>{
		setConfig(e);
		setRequired(e=='load_device'?true:false);
		setDisableds(e=='metering_device'?true:false);
		setLodeList([])
		http.post('system_management/energy_model/model_parameter/loadTypeList?configKey='+e).then(res =>{
			if(res.status==200){
				setLodeList(res.data);
				myForm.setFieldsValue({
					loadType:'',
					loadProperties:''
				})
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 选择负荷类型
	const loadChange =(item,e) =>{
		setLoadType(e.value);
		setLoadProperties(e.key)
		myForm.setFieldsValue({
			loadProperties:e.key
		})
	}
	// 负荷类型
	const loadTypeList=(configKeyval) =>{
		http.post('system_management/energy_model/model_parameter/loadTypeList?configKey='+configKeyval,{
			
		}).then(res =>{
			if(res.status==200){
				setLodeList(res.data);
				myForm.setFieldsValue({
					loadType:'',
					loadProperties:''
				})
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const onChange=(date) =>{
	}
	const handleCancel =() =>{
		setVisiblae(false);
		setFileList([])
		setNodeSystemList([])
		setLoadLoading(false);
		myForm.resetFields()
		myForm.setFieldsValue({
			dragger:undefined
		})
	}
	// 搜索设备
	const onSearch =(e) =>{
		if(e){
			setLoading(true)
			const params = new URLSearchParams();
			params.append('number', page);
			params.append('pageSize', 10);
			params.append('deviceName', e);
			http.post('system_management/device_model/deviceLikeNameListPageable',params).then(res =>{
				if(res.data.code ==200){
					let data = res.data.data.content
					setDataSource(res.data.data.content);
					setLoading(false)
				}
			})
		}else if(e==''){
			chosetanle()
		}
	}
	// 详情
	const detailed =(e) =>{
		let deviceSn = e.deviceSn
		setIsModalVisible(true)
		http.post('platFrom/latestCollectDeviceData?deviceSn='+deviceSn).then(res =>{
			if(res.data.code ==200){
				let data = res.data.data
				
				let columns1 = [
					{
						title: '属性名称',
						dataIndex: 'point_name',
						key: 'point_name'
					},
					{
						title: '设备序列号',
						dataIndex: 'device_sn',
						key: 'device_sn',
						width:250
					},
					{
						title: '设备序列号',
						dataIndex: 'point_desc',
						key: 'point_desc'
					},
					
					{
						title: '属性值',
						dataIndex: 'point_value',
						key: 'point_value'
					},
					{
						title: '属性值类型',
						dataIndex: 'point_value_type',
						key: 'point_value_type'
					},
					
					{
						title: '属性序列号',
						dataIndex: 'point_sn',
						key: 'point_sn'
					},
					{
						title: '采集时间',
						dataIndex: 'ts',
						key: 'ts'
					},
					{
						title: '设备类型',
						dataIndex: 'configKey',
						key: 'configKey'
					}
					
				]
				setColumns1(columns1);
				setDataSource1(res.data.data);
			}
		})
	}
	
	//导入设备
	const leading =() =>{
		setVisiblae(true)
	}
	// 删除
	const del = (e) =>{
		confirm({
		  title: '提示',
		  content: '确定要删除吗？',
		  cancelText:"取消",
		  okText:"确定",
		  cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
		  onOk() {
			http.post('system_management/device_model/deviceDelete?deviceId='+e.deviceId+'&nodeId='+nodeId+'&systemId='+e.systemId).then(res =>{
				if(res.data.code ==200){
					message.success('删除成功')
					const params = new URLSearchParams();
					params.append('nodeId', nodeId);
					params.append('systemId', systemId);
					params.append('number', page);
					params.append('pageSize', 10);
					http.post('system_management/device_model/deviceListPageable',params).then(res =>{
						if(res.data.code==200){
							let data = res.data.data.content
							setDataSource(res.data.data.content)
							tree()
						}else{
							message.error(res.data.msg)
						}
						expandedRowRender()
					})
					
				}else{
					message.info(res.data.msg)
				}
			})
		  },
		  onCancel() {
		    console.log('Cancel');
		  },
		});
		
	}
	
	const quxiao =() =>{
		setVisiblae(false);
		setFileList([])
		setNodeSystemList([])
		setLoadLoading(false);
		myForm.resetFields()
		myForm.setFieldsValue({
			dragger:undefined
		})
	}

	const handleCancel1 =() =>{
		setIsModalVisible(false)
	}
	const arrayTreeFilter = (data, predicate, filterText) => {
	   const nodes = data;
	   if (!(nodes && nodes.length)) {
		   return;
	   }
	   const newChildren = [];
	   for (const node of nodes) {
		   if (predicate(node, filterText)) {
			   newChildren.push(node);
			   node.children = arrayTreeFilter(node.children, predicate, filterText);
		   } else {
			   const subs = arrayTreeFilter(node.children, predicate, filterText);
			   if ((subs && subs.length) || predicate(node, filterText)) {
				   node.children = subs;
				   newChildren.push(node);
			   }
		   }
	   }
	   return newChildren;
	}
	
	const filterFn = (data, filterText) => { //过滤函数
	   if (!filterText) {
		   return true;
	   }
	   return (
		   new RegExp(filterText, "i").test(data.title) //我是一title过滤 ，你可以根据自己需求改动
	   );
	}
	const flatTreeFun = (treeData) => { //扁平化 tree
	   let arr = [];
	   const flatTree = (treeData) => {
		   treeData.map((item, index) => {
			   arr.push(item);
			   if (item.children && item.children.length > 0) {
				   flatTree(item.children);
				   item.children = [];
			   }
		   })
	   }
	   flatTree(treeData);
	   return arr;
	}
	const expandedKeysFun = (treeData) => { //展开 key函数
		if (treeData && treeData.length == 0) {
		   return [];
		}
		let arr = [];
		const expandedKeysFn = (treeData) => {
		   treeData.map((item, index) => {
			   arr.push(item.key);
			   if (item.children && item.children.length > 0) {
				   expandedKeysFn(item.children);
			   }
		   })
		}
		expandedKeysFn(treeData);
		return arr;
	}
	const onChanges = (e) => {
	   let value = e;
	   if (value == "") { 
		   
		   tree()
	   } else {
			http.post('system_management/device_model/areaDeviceShortView').then(res =>{
				if(res.data.code==200){
					let treeData = res.data.data
					treeData.map(res =>{
						
						return res.title = res.title + '（' +res.deviceSize +'）'
					})
					
					for(var i=0;i<treeData.length;i++){
						deep(treeData[i])
					}
					
					setTreeData(res.data.data);
					setTreeLoading(false);
					setCopyTree(res.data.data);
					let Newres = arrayTreeFilter(copyTree, filterFn, value);
					let expkey = expandedKeysFun(Newres);
					setTreeData(Newres);
					setExpandedKeys(expkey)
				}
			})
		   
	   }

   }
	// 下载模板
	const downLoads =() =>{
		axios({
			method: 'get',
			url: 'system_management/device_model/download/deviceTemplate',
			responseType: 'arraybuffer'
		}).then(res =>{
			if(res.status ==200){
				const url = window.URL.createObjectURL(new Blob([res.data]));
				const link = document.createElement('a'); 
				link.style.display = 'none';
				link.href = url; 
				link.download = '模板.xlsx'; 
				document.body.appendChild(link);
				link.click();
				URL.revokeObjectURL(link.href); 
				document.body.removeChild(link);
				
			}
		})

	}
	// 刷新左侧树
	const onLoads =() =>{
		setTreeLoading(true);
		http.post('system_management/device_model/areaDeviceShortView').then(res =>{
			if(res.data.code==200){
				let treeData = res.data.data
				treeData.map(res =>{
					
					return res.title = res.title + '（' +res.deviceSize +'）'
				})
				for(var i=0;i<treeData.length;i++){
					deep(treeData[i])
				}
				
				setTreeData(res.data.data);
				setTreeLoading(false);
				setCopyTree(res.data.data);
				message.success('刷新成功')
			}else{
				message.info(res.data.msg)
			}
		})
	}
	
		const columns: ColumnsType<DataType> = [
		  {
		    title: '设备名称',
		    dataIndex: 'deviceName',
			width:180
		  },
		  {
			title: '设备全码 ',
			dataIndex: 'deviceSn',
			width:130
		  },
		 //  {
		 //    title: '设备型号',
		 //    dataIndex: 'deviceModel',
			// width:200
		 //  },
		  {
		    title: '设备品牌',
		    dataIndex: 'deviceBrand',
			render: (text,record) =>{
				return text?text:'-'
			},
			width:130
		  },{
		    title: '额定负荷（kW）',
		    dataIndex: 'deviceRatedPower',
			width:170,
			render: (text,record,_,action) =>{
				if( text==null||text==undefined){
					return '-'
				}else if(text===''){
					return text
				}else{
					return Number(text).toFixed(2)
				}
			}
		  },
		  {
		  	title: '设备类型',
		  	dataIndex: 'configKey',
		  	key: 'configKey',
			width:140,
			render: (text,record) =>{
				if(record.configKey =='metering_device'){
					return '计量设备'
				}else if(record.configKey =='other'){
					return '非计量设备'
				}
			}
		  },
		  
		  {
		  	title: '设备状态',
		  	dataIndex: 'online',
		  	key: 'online',
			render: (text,record) =>{
				return record.online==false?'离线':'在线'
			},
			width:130,
		  },
		  {
		  	title: '负荷类型',
		  	dataIndex: 'loadType',
		  	key: 'loadType',
		  			width:130,
		  },
		  
		  {
		  	title: '负荷性质',
		  	dataIndex: 'loadProperties',
		  	key: 'loadProperties',
			width:130,
		  },
		  
		  {
		    title: '备注',
		    dataIndex: 'deviceLabel',
			width:150,
			render: (text,record) =>{
				return text?text:'-'
			}
		  }
		  ,{
		    title: '操作',
		    dataIndex: 'address',
		    key: 'address',
			width:100,
			// width:80,
		  	render: (text,record) =>{
				if(record.deviceId){
					return 	<Space size="middle">
								<a  onClick={() => del(record)}>删除</a>
							</Space>
				}
		  		
		  	}
		  	
		  	
		  },
		];
		
		const onExpand = expandedKeys => {
			setExpandedKeys(expandedKeys);
			setAutoExpandParent(false);
		};
		const extractNodeIds=(obj)=> {
			let nodeIds = [];

			function traverse(node) {
				nodeIds.push(node.nodeId); // 将当前节点的nodeId添加到数组中

				if (node.children && Array.isArray(node.children)) {
					node.children.forEach(child => {
						traverse(child); // 递归遍历子节点
					});
				}
			}

			traverse(obj); // 开始遍历
			return nodeIds;
		}
		const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
			setPage(1);
			setCurrentNum(1);
			setExpandedRowKeyslist([])
			setDataSource([])
			const allNodeIds = extractNodeIds(info.node);
			setNodeIds(allNodeIds)		
			setNodeId(info.node.nodeId)
			
		};
		
		
		const handleChange1: UploadProps['onChange'] = ({ fileList }) => {
			if(fileList[0]){
				const fileType = fileList[0]?.name.split(".");
				const fileDate = fileType.slice(-1);
				if (fileDate[0] =='xlsx') {
					setFileList([...fileList])
		   
				}else{
					setFileList([])
					myForm.setFieldsValue({
						dragger:undefined
					})
				}
			}			
		};
		const props: UploadProps = {
			onChange: handleChange1,
		    beforeUpload: file => {
				const files = file;
				const fileType = files?.name.split(".");
				const fileDate = fileType.slice(-1);
				if (fileDate[0] !='xlsx') {
				    message.error('仅支持 xlsx 文件格式：');
				    
				 }
				return false;
		    },
		}

		const onRemove = useCallback((file) => {
		    setFileList(state => {
				const index = state.indexOf(file);
				const newFileList = [...state];
				newFileList.splice(index, 1);
				return newFileList;
		    });
		}, []);
		// 上传设备
		const onFinish = (values) => {
			setLoadLoading(treeData)
			let length = values.node.length
			let configKey = values.config_key
			let nodeId = values.node[length-1]
			let systemId = values.system
			let device_rated_power = values.device_rated_power===undefined?'0':values.device_rated_power===''?'0': values.device_rated_power
			let filse = values.dragger.file
			
			let formData = new FormData()
			fileList.forEach(file => {
			    formData.append('file', file.originFileObj);
			});
			axios({
				method: 'POST',
				headers: { 'content-type': 'application/x-www-form-urlencoded' },
				url: 'system_management/device_model/v2/deviceExcelUpload?configKey='
				+configKey+'&nodeId='+nodeId+'&systemId='+systemId +'&device_rated_power='+device_rated_power+
				'&loadType='+values.loadType+'&loadProperties='+values.loadProperties,
				data:formData
			}).then(res =>{
				if(res.data.code ==200){
					message.success('成功')
					
					setVisiblae(false);
					setFileList([]);
					setLoadLoading(false);
					tree()
					myForm.resetFields()
					myForm.setFieldsValue({
						dragger:undefined
					})
					
				}else{
					message.info(res.data.msg)
					setLoadLoading(false)
				}
			})
		};
		const onFinishFailed = (errorInfo) => {
			let values = errorInfo.values
			let length = values.node.length
			let nodeId = values.node[length-1]
		};
		 //选择系统
		const handleProvinceChange = (value) => {
		    setCities(cityData[value]);
		    setSecondCity(cityData[value][0]);
			
		};
		
		const expandedRowRender = (record) => {
		    const columns = [
		      { title: '属性名称', dataIndex: 'pointName', key: 'pointName', width:180 },
		     {
		       title: '属性编码',
		       dataIndex: 'pointSn',
		       key: 'operation',
			   // width:140
		       
		     },
		      { title: '模型参数', dataIndex: 'pointDesc', key: 'pointDesc'},
		      { title: '', dataIndex: '1', key: 'pointDesc', width:100 },
			  { title: '', dataIndex: '2', key: 'pointDesc', width:100 },
			   { title: '', dataIndex: '3', key: 'pointDesc', width:100 },
		    ];
			
			
		    return <Table columns={columns}  rowKey={record => record.deviceId} 
			dataSource={record.pointViewList}  pagination={false} />
		};
		const toggleCollapsed = () => {
		    setCollapsed(!collapsed);
		};
		const getAllKeys = (data) => {
		  let keys = [];
		  data.forEach((item) => {
		    keys.push(item.key);
		    if (item.children) {
		      keys = keys.concat(getAllKeys(item.children));
		    }
		  });
		  return keys;
		};
		useEffect(() =>{
			if(collapsed){
				const keys = getAllKeys(treeData);
				setExpandedKeys(keys);
			}else{
				setExpandedKeys([]);
			}
		},[collapsed])
		
		const handlePagination =(page) =>{
			setPage(page);
			setCurrentNum(page);
		}
		const minWidth = 240;
		const maxWidth = 550
		return(
			<div className="allcontentequipment">
			<Resizable
			      defaultSize={{
			        width: 300,
			      }}
				  minWidth={minWidth}
				  maxWidth={maxWidth}
			      handleSize={[20, 20]}
			      minConstraints={[100, 100]}
			      maxConstraints={[500, 400]}
			      enable={{ top: false, right: true, bottom: false, left: false, topRight: false, bottomRight: false, bottomLeft: false, topLeft: false }}
			      onResize={(e, direction, ref, d) => {
			        // console.log('Resizing:', ref, d);
			      }}
			      onResizeStop={(e, direction, ref, d) => {
			        // console.log('Stop resizing:', ref, d);
			      }}
			    >
			      <div className="navigation" style={{width:300,marginRight:'16px'}}>
			      	<div style={{padding:16,borderBottom:'1px solid #4f4f4f'}}>
			      		<Search 
			      			classNames={'navigation-search'}
			      			placeholder="搜索相关信息" onSearch={onChanges} />
			      	</div>
			      	<div className="navigation-treenum">全部节点 ({treenum})
			      	<span onClick={toggleCollapsed}>{collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}</span>
			      	
			      	</div>
			      	<Spin spinning={treeLoading}>
			      	
			      		<Tree
			      			showIcon
			      			defaultExpandAll
			      			onSelect={onSelect}
			      			treeData={treeData}
			      			onExpand={onExpand}
			      			expandedKeys={expandedKeys}
			      			autoExpandParent={autoExpandParent}
			      		/>
			      	</Spin>
			      	
			      </div>
			    </Resizable>
				
				<div className="adjustableequipment adjustable ">
					<div className="adjustableheader equipment">
						<Button type="primary" onClick={leading} icon={<UploadOutlined />}>导入设备</Button>
					</div>
					
					<div className="traascroll">
						
						<Table
							columns={columns}
							dataSource={dataSource}
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
							loading={loading}
							rowKey={record => record.deviceId}
							expandable={{ expandedRowRender,onExpand:onExpandTbal,expandRowByClick: true,
							expandedRowKeys:expandedRowKeyslist
							}}
					    />
						
					</div>

				</div>
				<Modal title="导入设备" visible={visiblae} width={500}
					onCancel={handleCancel}
					footer={null}
					maskClosable={false}
					wrapClassName='import_devices'
				>
				    <Form
				        name="validate_other"
				        {...formItemLayout}
				        onFinish={onFinish}
						onFinishFailed={onFinishFailed}
				        form={myForm}
						initialValues={{
						        'device_rated_power': 0,
						      
						      }}
				    >
						
						<Form.Item
							name="node"
							label="归属节点"
							rules={[{required: true,   message: '请选择节点' }]}
						>
							<Cascader options={options} onChange={handleChange} changeOnSelect   placeholder="请选择" />
							
						</Form.Item>
						<Form.Item
							name="system"
							label="系统列表"
							rules={[{required: true,   message: '请选择系统' }]}
						>
							<Select onChange={sysChange} placeholder="请选择系统" >
								{
									nodeSystemList.length&&nodeSystemList.map(item =>{
										return <Option value={item.id} key={item.id}>{item.systemName}</Option>
									})
								}
							</Select>
						</Form.Item>
						<Form.Item
							name="config_key"
							label="导入设备类型"
							rules={[{ required: true, message: '请选择设备类型' }]}
						>
						<Select onChange={configChange} placeholder="请选择设备类型" >
							{
								sysConfigTypeList.length &&sysConfigTypeList.map((item,index) =>{
									return <Option key={index} value={item.config_key}>{item.config_name}</Option>
								})
							}
						</Select>
						</Form.Item>
						<Form.Item
							name="loadType"
							label="负荷类型"
							
							rules={[{ required: true, message: '请选择负荷类型' }]}
						>
						<Select placeholder="" onChange={loadChange}>
							{
								lodeList.length &&lodeList.map((item,index) =>{
									return <Option key={item.load_properties} value={item.load_type}>{item.load_type_name}</Option>
								})
							}
						</Select>
						</Form.Item>
						<Form.Item
							name="loadProperties"
							label="负荷性质"
							className="loadTypes"
							// rules={[{ required: true, message: '请选择负荷性质' }]}
						>
						<Select placeholder=""  disabled bordered={false}>
							{
								lodeList.length &&lodeList.map((item,index) =>{
									return <Option key={item.load_properties} value={item.load_properties}>{item.load_properties_name}</Option>
								})
							}
						</Select>
						</Form.Item>
						<Form.Item label="额定功率" >
							<Form.Item name="device_rated_power"
								style={{width:'90%'}}
							>
								<Input disabled={disableds} />
							</Form.Item>
							<span className="ant-form-text"> kW</span>
						</Form.Item>
						<Form.Item label="文件" required style={{marginBottom:10}}>
							<Form.Item  name="dragger" rules={[{ required: true, message: '请选择文件' }]}
								style={{float:'left'}}
							>
								<Upload maxCount={1} {...props}
									 accept= ".xlsx" 
									 fileList={fileList}
									 onRemove={onRemove}
								>
									<Button type="primary" style={{marginLeft:0}} icon={<UploadOutlined />}>选择文件</Button>
								</Upload>
							</Form.Item>
							<span className="downLoad" onClick={downLoads}>下载模板</span>
							
							
						</Form.Item>
						<Form.Item wrapperCol={{ offset: 6, span: 18 }}>
							<span className="xlsxName"><InfoCircleOutlined style={{marginRight:5}} />该文件仅支持xlsx文件格式</span>
						</Form.Item>
						<Form.Item wrapperCol={{ offset: 14, span: 10 }} className="buttonFooter">
							<Button ghost onClick={quxiao}>
								取消
							</Button>
							<Button type="primary" htmlType="submit" style={{marginLeft:24}} loading={loadLoading}>
								确定
							</Button>
							
						</Form.Item>
					</Form>
				</Modal>
				<Modal title="详情" visible={isModalVisible} width={800} 
					footer={null}
					onCancel={handleCancel1}
				>
				    <Table columns={columns1}
						dataSource={dataSource1}
						pagination={false}
					/>
				</Modal>
			</div>
		)
	}
	


export default Equipment