import React, { useEffect,useState } from 'react';
import { Tabs,Table,Select,Input,Button,DatePicker,ConfigProvider,
Space,InputNumber,Form,message,Typography,Popconfirm  } from 'antd';
import {
  InfoCircleOutlined
} from '@ant-design/icons';


// import locale from 'antd/locale/zh_CN';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';

import 'dayjs/locale/zh-cn';
import './footmark.scss'
import http from '../../../server/server.js'
import {
  FormOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';
const { Search } = Input;
const { Text } = Typography;

const { TabPane } = Tabs;
const { Option } = Select;
const { MonthPicker, RangePicker } = DatePicker;
const dateFormat = 'YYYY-MM-DD';
const monthFormat = 'YYYY-MM';
// const

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
		  rules={ [{required: true, pattern: new RegExp(/^(([1-9][0-9]{0,5})|0|1000000)$/),message: '请输入正确范围内的整数' }]}
          // rules={[
          //   {
          //     required: true,
          //     message: `请输入 ${title}!`,
          //   },
          // ]}
        >
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};
let  dischargeTypeone=[
	{
		tian: '', caMonth: '1', chai: '',mei:'',key:'1'
	},{
		tian: '', caMonth: '2', chai: '',mei:'',key:'2'
	},{
		tian: '', caMonth: '3', chai: '',mei:'',key:'3'
	},{
		tian: '', caMonth: '4', chai: '',mei:'',key:'4'
	},{
		tian: '', caMonth: '5', chai: '',mei:'',key:'5'
	},{
		tian: '', caMonth: '6', chai: '',mei:'',key:'6'
	},{
		tian: '', caMonth: '7', chai: '',mei:'',key:'7'
	},{
		tian: '', caMonth: '8', chai: '',mei:'',key:'8'
	},{
		tian: '', caMonth: '9', chai: '',mei:'',key:'9'
	},{
		tian: '', caMonth: '10', chai: '',mei:'',key:'10'
	},{
		tian: '', caMonth: '11', chai: '',mei:'',key:'11'
	},{
		tian: '', caMonth: '12', chai: '',mei:'',key:'12'
	}
]
let  dischargeTypeone2=[
	{
		tian: '', caMonth: '1', feiji: '',zilai:'',che:'',zhizhang:'',key:'1'
	},{
		huoche: '', caMonth: '2', feiji: '',zilai:'',che:'',zhizhang:'',key:'2'
	},{
		huoche: '', caMonth: '3', feiji: '',zilai:'',che:'',zhizhang:'',key:'3'
	},{
		huoche: '', caMonth: '4', feiji: '',zilai:'',che:'',zhizhang:'',key:'4'
	},{
		huoche: '', caMonth: '5', feiji: '',zilai:'',che:'',zhizhang:'',key:'5'
	},{
		huoche: '', caMonth: '6', feiji: '',zilai:'',che:'',zhizhang:'',key:'6'
	},{
		huoche: '', caMonth: '7', feiji: '',zilai:'',che:'',zhizhang:'',key:'7'
	},{
		huoche: '', caMonth: '8', feiji: '',zilai:'',che:'',zhizhang:'',key:'8'
	},{
		huoche: '', caMonth: '9', feiji: '',zilai:'',che:'',zhizhang:'',key:'9'
	},{
		huoche: '', caMonth: '10', feiji: '',zilai:'',che:'',zhizhang:'',key:'10'
	},{
		huoche: '', caMonth: '11', feiji: '',zilai:'',che:'',zhizhang:'',key:'11'
	},{
		huoche: '', caMonth: '12', feiji: '',zilai:'',che:'',zhizhang:'',key:'12'
	}
]
let  dischargeTypetwo=[
	{
		dian: '', caMonth: 1, re: '',key:21
	},{
		dian: '', caMonth: 2, re: '',key:22
	},{
		dian: '', caMonth: 3, re: '',key:23
	},{
		dian: '', caMonth: 4, re: '',key:24
	},{
		dian: '', caMonth: 5, re: '',key:25
	},{
		dian: '', caMonth: 6, re: '',key:26
	},{
		dian: '', caMonth: 7, re: '',key:27
	},{
		dian: '', caMonth: 8, re: '',key:28
	},{
		dian: '', caMonth: 9, re: '',key:29
	},{
		dian: '', caMonth: 10, re: '',key:210
	},{
		dian: '', caMonth: 11, re: '',key:211
	},{
		dian: '', caMonth: 12, re: '',key:212
	}
]
let  dischargeTypethree=[
	{
		zhi: '', caMonth: 1, kong: '',mie:'',key:1
	},{
		zhi: '', caMonth: 2, kong: '',mie:'',key:2
	},{
		zhi: '', caMonth: 3, kong: '',mie:'',key:3
	},{
		zhi: '', caMonth: 4, kong: '',mie:'',key:4
	},{
		zhi: '', caMonth: 5, kong: '',mie:'',key:5
	},{
		zhi: '', caMonth: 6, kong: '',mie:'',key:6
	},{
		zhi: '', caMonth: 7, kong: '',mie:'',key:7
	},{
		zhi: '', caMonth: 8, kong: '',mie:'',key:8
	},{
		zhi: '', caMonth: 9, kong: '',mie:'',key:9
	},{
		zhi: '', caMonth: 10, kong: '',mie:'',key:10
	},{
		zhi: '', caMonth: 11, kong: '',mie:'',key:11
	},{
		zhi: '', caMonth: 12, kong: '',mie:'',key:12
	}
]
let two = [
	{
		caMonth: 1, dian: '',re:'',key:1
	},
	{
		caMonth: 2, dian: '',re:'',key:2
	},
	{
		caMonth: 3, dian: '',re:'',key:3
	},
	{
		caMonth: 4, dian: '',re:'',key:4
	},
	{
		caMonth: 5, dian: '',re:'',key:5
	},
	{
		caMonth: 6, dian: '',re:'',key:6
	},
	{
		caMonth: 7, dian: '',re:'',key:7
	},
	{
		caMonth: 8, dian: '',re:'',key:8
	},
	{
		caMonth: 9, dian: '',re:'',key:9
	},
	{
		caMonth: 10, dian: '',re:'',key:10
	},
	{
		caMonth: 11, dian: '',re:'',key:11
	},
	{
		caMonth: 12, dian: '',re:'',key:12
	}
]
let three=[
	{
		caMonth: '1',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 1
	},
	{
		caMonth: '2',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 2
	},
	{
		caMonth: '3',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 3
	},
	{
		caMonth: '4',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 4
	},
	{
		caMonth: '5',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 5
	},
	{
		caMonth: '6',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 6
	},
	{
		caMonth: '7',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 7
	},
	{
		caMonth: '8',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 8
	},
	{
		caMonth: '9',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 9
	},
	{
		caMonth: '12',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 10
	},
	{
		caMonth: '11',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 11
	},
	{
		caMonth: '12',feiji: '',  huoche: '',
		che:'',zilai:'',zhizhang:'',wushui:'',key: 12
	}
]
const Footmark =() =>{
	const [index, setIndex] = useState(1);
	const [isShow, setIsShow] = useState('display');
	const [nodeList, setNodeList] = useState([]);
	const [nodeId, setNodeId] = useState('');
	const [currentUnit, setCurrentUnit] = useState('');
	const [scopeType, setScopeType] = useState('1');
	const [dischargeType, setDischargeType] = useState('1');
	const [year, setYear] = useState('');
	const [content, setContent] = useState([]);
	const [tempKeyArr, setTempKeyArr] = useState([]);
	const [editingKey, setEditingKey] = useState('');
	const [disabled, setDisabled] = useState(true);
	const [data, setData] = useState([]);
	const [caMonth, setCaMonth] = useState('');
	const [newdatas, setNewDatas] = useState([
				{"caMonth":1,"key":1,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":2,"key":2,"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":3,"key":3,"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":4,"key":4,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":5,"key":5,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":6,"key":6,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":7,"key":7,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":8,"key":8,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":9,"key":9,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":10,"key":10,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":11,"key":11,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"},
				{"caMonth":12,"key":12,
				"caYear":2022,"caYearMonth":202211,"createdTime":"2023-01-06 12:45:41",
				"dischargeEntity":11,"dischargeType":1,"dischargeValue":7.0,
				"nodeId":"6ad13b894ef64dbc882ba0aa7fe1c52b","refrigerator":null,
				"scopeId":"2022-11-1-111-6ad13b894ef64dbc882ba0aa7fe1c52b",
				"scopeThree":null,"scopeTwo":null,"scopeType":1,"sstatus":1,
				"updateTime":"2023-01-14 18:22:39"}
			]);
	const [editNull, setEditNull] = useState('');
	const [loading, setLoading] = useState('');
	const [currentLoading, setCurrentLoading] = useState(false);
	
	const [columns,setColumns] = useState([
		{
			title: '月份',
			dataIndex: 'caMonth',
			key: 'caMonth',
			
		},
		{
			title: '天然气（㎥）',
			dataIndex: 'tian',
			key: 'tian',
			editable: true,
			render: (text,record,_,action) =>{
				if( text==null||text==undefined||text===''){
					return '-'
				}else if(text!==''){
					return text
				}else{
					return text
				}
			}
			
		},
		{
			title: '煤气（㎥）',
			dataIndex: 'mei',
			key: 'mei',
			editable: true,
			render: (text,record,_,action) =>{
				if( text==null||text==undefined||text===''){
					return '-'
				}else if(text!==''){
					return text
				}else{
					return text
				}
			}
			
		},
		{
			title: '柴油（㎥）',
			dataIndex: 'chai',
			key: 'chai',
			editable: true,
			render: (text,record,_,action) =>{
				if( text==null||text==undefined||text===''){
					return '-'
				}else if(text!==''){
					return text
				}else{
					return text
				}
			}
		},
		{
			title: '操作',
			dataIndex: 'operation',
			render: (_: any, record: Item) => {
				const editable = isEditing(record);
				console.log(editable,'-----')
				return editable ? (
				  <span>
					<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
					  确定
					</Typography.Link>
					<Popconfirm title="确定要取消吗" onClick={() => cancel(record.key)}>
					  <a>取消</a>
					</Popconfirm>
				  </span>
				) : (
				  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
					编辑
				  </Typography.Link>
				);
			},
		}
		
	 
	]);
	const [myForm] = Form.useForm()
	useEffect(() =>{
		getnodeList()
	},[])
	const define =() =>{
		
	}
	const quxiao =() =>{
		
	}
	const onChangeyear = (value,year) =>{
		console.log(year)
		setYear(year)
	}
	// 选择燃烧源
	const combustionbtn =(val) =>{
		console.log(val)
		if(year){
		
			
			setIndex(val);
			setDischargeType(val);
			
		}else{
			message.info('请选择年份')
		}
	}
	
	// 获取节点列表
	const getnodeList =() =>{
		// 节点
		setCurrentLoading(true)
		http.post('system_management/node_model/nodeNameList').then(res =>{
			console.log(res)
			if(res.data.code==200){
				setNodeList(res.data.data);
				setCurrentUnit(res.data.data.length>0?res.data.data[0].id:'');
				setNodeId(res.data.data.length>0?res.data.data[0].id:'');
				setCurrentLoading(false)
			}else{
				message.info(res.data.msg)
			}
		})
	}
	// 查询碳足迹信息
	const getFootmark=() =>{
		setLoading(true)
		http.post('carbon/footmark/getFootmark',{
			"dischargeType": scopeType==1?dischargeType:'',	//燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
			"nodeId": nodeId,
			"scopeType": scopeType,
			"year": year
		}).then(res =>{
			console.log(res)
			console.log(res.data.data)
			let data = res.data.data
			console.log(data)
			let tempKeyArr = []
			if(res.data.code==200){
				if(data){
					setEditNull('存在');
					setLoading(false)
					
					if(data['1']){
						let new1 = data['1']
						console.log(new1)
						let obj = new Object;
						let list = []
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放  ）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 1
									} if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key = 1
									} if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key = 1
									}
								}
								console.log(tempKeyArr)
								console.log(obj,'/////////////////')
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
								
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 1
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 1
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 1
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 1
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							console.log(tempKeyArr)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									// console.log(item)
									// console.log(value)
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
							
						}else if(scopeType=='2'){
							// 范围二
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 1
									
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key = 1
								}
							}
							tempKeyArr.push(obj)
							console.log(tempKeyArr)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							// 范围三
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 1
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 1
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key =1
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 1
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 1
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} 
					if(data['2']){
						let new1 = data['2']
						console.log(new1)
						let obj = new Object;
						let list  = []
						console.log(list)
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							let list = []
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.key = 2
										obj.caMonth = new1[i].caMonth
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key = 2
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key = 2
									}
								}
								tempKeyArr.push(obj)
								// console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 2
									}
								}
								tempKeyArr.push(obj)
								// console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 2
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 2
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 2
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										list.push(value)
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 2
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 2
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 2
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 2
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 2
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 2
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 2
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} if(data['3']){
						let new1 = data['3']
						console.log(new1)
						let obj = new Object;
						let list  = []
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放  ）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 3
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key = 3
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key = 3
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 3
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 3
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 3
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 3
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
							
						}else if(scopeType=='2'){
							// 范围二
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 's'+3
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 's'+3
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							// 范围三
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 3
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 3
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 3
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 3
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 3
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} if(data['4']){
						let new1 = data['4']
						console.log(new1)
						let obj = new Object;
						let list  = []			
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key=4
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key=4
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key=4
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 4
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 4
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 4
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key =4
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key='s'+ 4
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 's'+4
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 4
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 4
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 4
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 4
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 4
								}
							}
							tempKeyArr.push(obj)
							console.log(tempKeyArr)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} if(data['5']){
						let new1 = data['5']
						console.log(new1)
						let obj = new Object;
						let list  = []			
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key= 5
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key= 5
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 5
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key =5
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 5
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 5
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 5
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 5
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 5
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 5
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 5
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 5
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 5
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} if(data['6']){
						let new1 = data['6']
						console.log(new1)
						let obj = new Object;
						let list  = []			
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key= 6
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key= 6
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key= 6
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 6
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 6
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 6
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 6
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 6
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 6
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 6
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 6
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 6
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 6
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 6
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} if(data['7']){
						let new1 = data['7']
						console.log(new1)
						let obj = new Object;
						let list  = []			
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key= 7
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key= 7
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key= 7
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 7
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 7
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 7
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 7
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 7
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 7
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 7
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 7
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 7
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 7
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 7
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} if(data['8']){
						let new1 = data['8']
						console.log(new1)
						let obj = new Object;
						let list  = []			
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key= 8
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key= 8
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key= 8
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 8
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 8
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 8
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 8
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 8
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 8
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 8
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 8
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 8
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 8
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 8
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} if(data['9']){
						let new1 = data['9']
						console.log(new1)
						let obj = new Object;
						let list  = []			
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key= 9
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key= 9
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key= 9
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 9
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 9
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 9
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 9
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 9
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 9
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 9
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 9
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 9
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 9
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 9
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} if(data['10']){
						let new1 = data['10']
						console.log(new1)
						let obj = new Object;
						let list  = []			
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key= 10
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key= 10
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key= 10
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 10
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 10
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 10
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 10
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 10
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 10
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 10
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 10
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 10
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 10
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 10
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					} if(data['11']){
						let new1 = data['11']		
						let obj = new Object;
						let list  = []			
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
									}
								}
								tempKeyArr.push(obj)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 11
									}
								}
								tempKeyArr.push(obj)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 11
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 11
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 11
									}
								}
								tempKeyArr.push(obj)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
							
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 11
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 11
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									tempKeyArr.push(item);　　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							console.log(tempKeyArr)
							setContent(tempKeyArr)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 11
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 11
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 11
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 11
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 11
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}

					} if(data['12']){
						let new1 = data['12']
						let list = []
						console.log(new1)
						let obj = new Object;
						if(scopeType=='1'){
							// 1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
							
							if(dischargeType==1){
								// 1-固定燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='11'){
										obj.tian = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key= 12
									}else if(new1[i].dischargeEntity=='12'){
										obj.mei = new1[i].dischargeValue
										obj.key= 12
									}else if(new1[i].dischargeEntity=='13'){
										obj.chai = new1[i].dischargeValue
										obj.key= 12
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==2){
								// 2-移动燃烧源的燃烧排放
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 11-天然气 12-煤气 13-柴油
									if(new1[i].dischargeEntity=='21'){
										obj.gong = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 12
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}else if(dischargeType==3){
								for(var i=0;i<new1.length;i++){
									console.log(new1[i])
									// 31- 32- 33-
									if(new1[i].dischargeEntity=='31'){
										obj.zhi = new1[i].dischargeValue
										obj.caMonth = new1[i].caMonth
										obj.key = 12
									} if(new1[i].dischargeEntity=='32'){
										obj.kong = new1[i].dischargeValue
										obj.key = 12
									} if(new1[i].dischargeEntity=='33'){
										obj.mie = new1[i].dischargeValue
										obj.key = 12
									}
								}
								tempKeyArr.push(obj)
								console.log(tempKeyArr)
							}
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										// tempKeyArr.push(item)
										list.push(value);　
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									// console.log(item)
									list.push(item);　
									　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(list)
							console.log(list.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						
						}else if(scopeType=='2'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeTwo==2){
									// 外购热力
									obj.re = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key= 12
								}else if(new1[i].scopeTwo==1){
									// 外购电力
									obj.dian = new1[i].dischargeValue
									obj.key= 12
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth === value.caMonth) {
										isExit = true
										return　　// 使用return是因为在forEach循环中continue和break用不了
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									tempKeyArr.push(item);　　　　// 不等于的数据就添加进list数组
								}
							})
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							console.log(tempKeyArr)
							
							setContent(tempKeyArr)
						}else if(scopeType=='3'){
							for(var i=0;i<new1.length;i++){
								console.log(new1[i])
								if(new1[i].scopeThree==1){
									// 飞机
									obj.feiji = new1[i].dischargeValue
									obj.caMonth = new1[i].caMonth
									obj.key = 12
								}else if(new1[i].scopeThree==2){
									// 火车
									obj.huoche = new1[i].dischargeValue
									obj.key = 12
								}else if(new1[i].scopeThree==3){
									// 私家车
									obj.che = new1[i].dischargeValue
									obj.key = 12
								}else if(new1[i].scopeThree==4){
									// 自来水
									obj.zilai = new1[i].dischargeValue
									obj.key = 12
								}else if(new1[i].scopeThree==5){
									// 纸张消耗
									obj.zhizhang = new1[i].dischargeValue
									obj.key = 12
								}
							}
							tempKeyArr.push(obj)
							newdatas.forEach((item,index)=>{
								let isExit = false　// 给一个状态值好进行判断
								tempKeyArr.forEach((value,index)=>{
									if(item.caMonth == value.caMonth) {
										console.log(item)
										console.log(value)
										isExit = true
										list.push(value);　
										return　　
									}
								})
								if(!isExit) {
									item["isRead"] = 1　　// 是否已读
									list.push(item);　
								}
							})
							console.log(list)
							console.log(tempKeyArr.sort((a,b) => {return a.caMonth - b.caMonth}))
							setContent(list)
						}
					}
				}else{
					setLoading(false)
					if(scopeType=='1'){
						// 范围一
						if(dischargeType=='1'){
							setContent(dischargeTypeone);
							setEditNull('')
						}else if(dischargeType=='2'){
							setContent(dischargeTypetwo);
							setEditNull('')
						} if(dischargeType=='3'){
							setContent(dischargeTypethree);
							setEditNull('')
						}
					}else if(scopeType=='2'){
						setContent(two);
						setEditNull('')
					}else if(scopeType=='3'){
						// 范围三
						setContent(three);
						setEditNull('')
					}
					
				}
			}else{
				message.info(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 选择范围
	const handlepoint1= (val) =>{
		console.log(val)
		
		setScopeType(val)
	}
	useEffect(() =>{
		if(index==1){
			setColumns([
					{
						title: '月份',
						dataIndex: 'caMonth',
						key: 'caMonth',
					},
					{
						title: '天然气（㎥）',
						dataIndex: 'tian',
						key: 'tian',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined||text===''){
								return '-'
							}else if(text!==''){
								return text
							}else{
								return text
							}
						}
						
					},
					{
						title: '煤气（㎥）',
						dataIndex: 'mei',
						key: 'mei',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined||text===''){
								return '-'
							}else if(text!==''){
								return text
							}else{
								return text
							}
						}
						
					},
					{
						title: '柴油（㎥）',
						dataIndex: 'chai',
						key: 'chai',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined||text===''){
								return '-'
							}else if(text!==''){
								return text
							}else{
								return text
							}
						}
						
					},
					{
						title: '操作',
						dataIndex: 'operation',
						render: (_: any, record: Item) => {
							const editable = isEditing(record);
							console.log(editable,'-----')
							return editable ? (
							  <span>
								<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
								  确定
								</Typography.Link>
							   <Typography.Link onClick={() => cancel(record.key)} >
								 取消
							   </Typography.Link>
							  </span>
							) : (
							  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
								编辑
							  </Typography.Link>
							);
						},
					}
				 
				])
		}else if(index==2){
			setColumns([
				{
					title: '月份',
					dataIndex: 'caMonth',
					key: 'caMonth',
				},
				{
					title: '公务车（辆）',
					dataIndex: 'gong',
					key: 'gong',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '操作',
					dataIndex: 'operation',
					render: (_: any, record: Item) => {
						const editable = isEditing(record);
						console.log(editable,'-----')
						return editable ? (
						  <span>
							<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
							  确定
							</Typography.Link>
							<Typography.Link onClick={() => cancel(record.key)} >
							  取消
							</Typography.Link>
						  </span>
						) : (
						  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
							编辑
						  </Typography.Link>
						);
					},
				}
		 
			])
		}else if(index==3){
			setColumns([
				{
					title: '月份',
					dataIndex: 'caMonth',
					key: 'caMonth',
				},
				{
					title: '制冷剂（kg）',
					dataIndex: 'zhi',
					key: 'zhi',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '空调氟利昂（kg）',
					dataIndex: 'kong',
					key: 'kong',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '灭火器（个）',
					dataIndex: 'mie',
					key: 'mie',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '操作',
					dataIndex: 'operation',
					render: (_: any, record: Item) => {
						const editable = isEditing(record);
						console.log(editable,'-----')
						return editable ? (
						  <span>
							<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
							  确定
							</Typography.Link>
							<Typography.Link onClick={() => cancel(record.key)} >
							  取消
							</Typography.Link>
						  </span>
						) : (
						  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
							编辑
						  </Typography.Link>
						);
					},
				}
		 
			])
		}
		getFootmark()
	},[index])
	useEffect(() =>{
		if(scopeType==1){
			setIsShow('inline-block')
			setColumns([
				{
					title: '月份',
					dataIndex: 'caMonth',
					key: 'caMonth',
				},
				{
					title: '天然气（㎥）',
					dataIndex: 'tian',
					key: 'tian',
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							return text
						}else{
							return text
						}
					}
					
				},
				{
					title: '煤气（㎥）',
					dataIndex: 'mei',
					key: 'mei',
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '柴油（㎥）',
					dataIndex: 'chai',
					key: 'chai',
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							return text
						}else{
							return text
						}
					}
					
				},
				{
				    title: '操作',
				    dataIndex: 'operation',
				    render: (_: any, record: Item) => {
				        const editable = isEditing(record);
						console.log(editable,'-----')
				        return editable ? (
				          <span>
				            <Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
				              确定
				            </Typography.Link>
				            <Typography.Link onClick={() => cancel(record.key)} >
				              取消
				            </Typography.Link>
				          </span>
				        ) : (
				          <Typography.Link disabled={editingKey !== ''} onClick={() => 
						  edit(record)}>
				            编辑
				          </Typography.Link>
				        );
				      },
				    }
			 
			])
		}else if(scopeType==2){
			setIsShow('none')
			setColumns([
				{
					title: '月份',
					dataIndex: 'caMonth',
					key: 'caMonth',
					editable: true,
				},
				{
					title: '外购电力（kwh）',
					dataIndex: 'dian',
					key: 'dian',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '外购热力（KJ）',
					dataIndex: 're',
					key: 're',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							return text
						}else{
							return text
						}
					}
				},
				
				{
					title: '操作',
					dataIndex: 'operation',
					render: (_: any, record: Item) => {
						const editable = isEditing(record);
						console.log(editable,'-----')
						return editable ? (
						  <span>
							<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
							  确定
							</Typography.Link>
							<Typography.Link onClick={() => cancel(record.key)} >
							  取消
							</Typography.Link>
						  </span>
						) : (
						  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
							编辑
						  </Typography.Link>
						);
					},
				}
		 
			])
		}else if(scopeType==3){
			
			setIsShow('none')
			setColumns([
				{
					title: '月份',
					dataIndex: 'caMonth',
					key: 'caMonth',
				},
				{
					title: '差旅-火车（km）',
					dataIndex: 'huoche',
					key: 'huoche',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
					
				},
				{
					title: '差旅-飞机（km）',
					dataIndex: 'feiji',
					key: 'feiji',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
					
				},
				{
					title: '自来水（t）',
					dataIndex: 'zilai',
					key: 'zilai',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
			
				{
					title: '私家车（辆）',
					dataIndex: 'che',
					key: 'che',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '纸张消耗（张）',
					dataIndex: 'zhizhang',
					key: 'zhizhang',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '操作',
					dataIndex: 'operation',
					render: (_: any, record: Item) => {
						const editable = isEditing(record);
						console.log(editable,'-----')
						return editable ? (
						  <span>
							<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
							  确定
							</Typography.Link>
							<Typography.Link onClick={() => cancel(record.key)} >
							  取消
							</Typography.Link>
						  </span>
						) : (
							<Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
								编辑
							</Typography.Link>
						);
					},
				}
						 
			])
		}
		console.log(scopeType)
	},[scopeType])
	useEffect(() =>{
		console.log(columns)
	},[columns])
	// 选择节点
	const handlepoint = (val) =>{
		console.log(val)
		setNodeId(val)
	}
	// 查询
	const search =() =>{
		if(year){
			getFootmark()
			
		}else{
			message.info('请选择年份')
		}
		
	}
	
	// 确定
	
	// const isEditing = (record) => record.key === editingKey;
	const isEditing=(record) => {
		// console.log(record)
		// console.log(editingKey)
	  return record.key === editingKey
	}
	useEffect(() =>{
		console.log(editingKey,'editingKey')
		if(scopeType=='1'){
			if(dischargeType=='1'){
				// setEditingKey(record.key);
				// setCaMonth(record.caMonth);
				console.log(editingKey)
				// console.log(isEditing(record))
				setColumns([
					{
						title: '月份',
						dataIndex: 'caMonth',
						key: 'caMonth',
						
					},
					{
						title: '天然气（㎥）',
						dataIndex: 'tian',
						key: 'tian',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined||text===''){
								return '-'
							}else if(text!==''){
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '煤气（㎥）',
						dataIndex: 'mei',
						key: 'mei',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined||text===''){
								return '-'
							}else if(text!==''){
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '柴油（㎥）',
						dataIndex: 'chai',
						key: 'chai',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined||text===''){
								return '-'
							}else if(text!==''){
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '操作',
						dataIndex: 'operation',
						render: (_: any, record: Item) => {
							const editable = isEditing(record);
							console.log(editable,'-----')
							return editable ? (
							  <span>
								<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
								  确定
								</Typography.Link>
								<Typography.Link onClick={() => cancel(record.key)} >
								  取消
								</Typography.Link>
							  </span>
							) : (
							  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
								编辑
							  </Typography.Link>
							);
						  },
					}
					
				 
				])
			}else if(dischargeType=='2'){
				// myForm.setFieldsValue({ gong: '',...record });
				// setEditingKey(record.key);
				// setCaMonth(record.caMonth)
				setColumns([
					{
						title: '月份',
						dataIndex: 'caMonth',
						key: 'caMonth',
					},
					{
						title: '公务车（辆）',
						dataIndex: 'gong',
						key: 'gong',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined){
								return '-'
							}else if(text!==''){
								// console.log(text)
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '操作',
						dataIndex: 'operation',
						render: (_: any, record: Item) => {
							const editable = isEditing(record);
							console.log(editable,'-----')
							return editable ? (
							  <span>
								<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
								  确定
								</Typography.Link>
							   <Typography.Link onClick={() => cancel(record.key)} >
								 取消
							   </Typography.Link>
							  </span>
							) : (
							  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
								编辑
							  </Typography.Link>
							);
						},
					}
							 
				])
			}else if(dischargeType=='3'){
				// myForm.setFieldsValue({ zhi: '',kong:'',mie:'',...record });
				// setEditingKey(record.key);
				// setCaMonth(record.caMonth)
				setColumns([
					{
						title: '月份',
						dataIndex: 'caMonth',
						key: 'caMonth',
					},
					{
						title: '制冷剂（kg）',
						dataIndex: 'zhi',
						key: 'zhi',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined){
								return '-'
							}else if(text!==''){
								// console.log(text)
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '空调氟利昂（kg）',
						dataIndex: 'kong',
						key: 'kong',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined){
								return '-'
							}else if(text!==''){
								// console.log(text)
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '灭火器（个）',
						dataIndex: 'mie',
						key: 'mie',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined){
								return '-'
							}else if(text!==''){
								// console.log(text)
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '操作',
						dataIndex: 'operation',
						render: (_: any, record: Item) => {
							const editable = isEditing(record);
							console.log(editable,'-----')
							return editable ? (
							  <span>
								<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
								  确定
								</Typography.Link>
							   <Typography.Link onClick={() => cancel(record.key)} >
								 取消
							   </Typography.Link>
							  </span>
							) : (
							  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
								编辑
							  </Typography.Link>
							);
						},
					}
			 
				])
			}
			
		}else if(scopeType=='2'){
			// myForm.setFieldsValue({ re: '', dian: '', ...record });
			// setEditingKey(record.key);
			// setCaMonth(record.caMonth);
			setColumns([
				{
					title: '月份',
					dataIndex: 'caMonth',
					key: 'caMonth',
				},
				{
					title: '外购电力（kwh）',
					dataIndex: 'dian',
					key: 'dian',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '外购热力（KJ）',
					dataIndex: 're',
					key: 're',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '操作',
					dataIndex: 'operation',
					render: (_: any, record: Item) => {
						const editable = isEditing(record);
						console.log(editable,'-----')
						return editable ? (
						  <span>
							<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
							  确定
							</Typography.Link>
							<Typography.Link onClick={() => cancel(record.key)} >
							  取消
							</Typography.Link>
						  </span>
						) : (
						  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
							编辑
						  </Typography.Link>
						);
					  },
					}
				
			 
			])
		}else if(scopeType=='3'){
			// myForm.setFieldsValue({ huoche: '', feiji: '',zilai:'',che:'',zhi:'', ...record });
			// setEditingKey(record.key);
			// setCaMonth(record.caMonth);
			setColumns([
				{
					title: '月份',
					dataIndex: 'caMonth',
					key: 'caMonth',
				},
				{
					title: '差旅-火车（km）',
					dataIndex: 'huoche',
					key: 'huoche',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '差旅-飞机（km）',
					dataIndex: 'feiji',
					key: 'feiji',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '自来水（t）',
					dataIndex: 'zilai',
					key: 'zilai',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
		
				{
					title: '私家车（辆）',
					dataIndex: 'che',
					key: 'che',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '纸张消耗（张）',
					dataIndex: 'zhizhang',
					key: 'zhizhang',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '操作',
					dataIndex: 'operation',
					render: (_: any, record: Item) => {
						const editable = isEditing(record);
						console.log(editable,'-----')
						return editable ? (
						  <span>
							<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
							  确定
							</Typography.Link>
							<Typography.Link onClick={() => cancel(record.key)} >
							  取消
							</Typography.Link>
						  </span>
						) : (
						  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
							编辑
						  </Typography.Link>
						);
					},
				}
						 
			])
		}
		
	},[editingKey,scopeType])
	const save = async (key: React.Key) => {
		try {
			const row = (await  myForm.validateFields());
			
			const newData = data;
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
				newData.push(row)
				if(scopeType=='1'){
					// 范围一
					if(dischargeType=='1'){						
						let monthList = new Object
						monthList[caMonth]  = [
							{
								"dischargeEntity": 11,	//11-天然气 12-煤气 13-柴油 21-公务车 31-冷机 32-分体空调 33-灭火器
								"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
								"dischargeValue": row.tian,	//值
								"refrigerator": '',	//冷机参数（1-冷机制
								"scopeThree": '',	//范围三类型
								"scopeTwo": ''	//范围二购买内容
							},
							{
								"dischargeEntity": 12,	//燃烧排放实体（11-煤气 
								"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
								"dischargeValue": row.mei,	//值
								"refrigerator": '',	//冷机参数（1-冷机制
								"scopeThree": '',	//范围三类型
								"scopeTwo": ''	//范围二购买内容
							},
							{
								"dischargeEntity": 13,	//燃烧排放实体（11-柴油 
								"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
								"dischargeValue": row.chai,	//值
								"refrigerator": '',	//冷机参数（1-冷机制
								"scopeThree": '',	//范围三类型
								"scopeTwo": ''	//范围二购买内容
							}]
						console.log(monthList)
						http.post('carbon/footmark/editFootmark',{
							"dischargeType": dischargeType,	//燃烧排放类型
							"nodeId": nodeId,
							"scopeType": scopeType,
							"year": year,
							monthList
							
						}).then(res =>{
							console.log(res)
							if(res.data.code==200){
								if(dischargeType==1){
									
									
									message.success('修改成功')
									getFootmark()
								}else if(dischargeType==2){
									setColumns([
											{
												title: '月份',
												dataIndex: 'caMonth',
												key: 'caMonth',
											},
											{
												title: '公务车（辆）',
												dataIndex: 'gong',
												key: 'gong',
												editable: true,
												render: (text,record,_,action) =>{
													if( text==null||text==undefined){
														return '-'
													}else if(text!==''){
														// console.log(text)
														return text
													}else{
														return text
													}
												}
											},
											{
												title: '操作',
												dataIndex: 'operation',
												render: (_: any, record: Item) => {
													const editable = isEditing(record);
													console.log(editable,'-----')
													return editable ? (
													  <span>
														<Typography.Link onClick={() =>save(record.key)} style={{ marginRight: 8 }}>
														  确定
														</Typography.Link>
														<Typography.Link onClick={() =>cancel(record.key)} >
														  取消
														</Typography.Link>
													  </span>
													) : (
													  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
														编辑
													  </Typography.Link>
													);
												},
											}
									 
										])
								}else if(dischargeType==3){
									setColumns([
											{
												title: '月份',
												dataIndex: 'caMonth',
												key: 'caMonth',
											},
											{
												title: '制冷剂（kg）',
												dataIndex: 'zhi',
												key: 'zhi',
												editable: true,
												render: (text,record,_,action) =>{
													if( text==null||text==undefined){
														return '-'
													}else if(text!==''){
														// console.log(text)
														return text
													}else{
														return text
													}
												}
											},
											{
												title: '空调氟利昂（kg）',
												dataIndex: 'kong',
												key: 'kong',
												editable: true,
												render: (text,record,_,action) =>{
													if( text==null||text==undefined){
														return '-'
													}else if(text!==''){
														// console.log(text)
														return text
													}else{
														return text
													}
												}
											},
											{
												title: '灭火器（个）',
												dataIndex: 'mie',
												key: 'mie',
												editable: true,
												render: (text,record,_,action) =>{
													if( text==null||text==undefined){
														return '-'
													}else if(text!==''){
														// console.log(text)
														return text
													}else{
														return text
													}
												}
											},
											{
												title: '操作',
												dataIndex: 'operation',
												render: (_: any, record: Item) => {
													const editable = isEditing(record);
													console.log(editable,'-----')
													return editable ? (
													  <span>
														<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
														  确定
														</Typography.Link>
													   <Typography.Link onClick={() => cancel(record.key)} >
														 取消
													   </Typography.Link>
													  </span>
													) : (
													  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
														编辑
													  </Typography.Link>
													);
												},
											}
									 
										])
								}
								
							}else{
								message.info(res.data.msg)
							}
						})
					}else if(dischargeType=='2'){
						// let caMonth = this.state.caMonth
						
						let monthList = new Object
						monthList[caMonth]  = [
							{
								"dischargeEntity": 21,	//11-天然气 12-煤气 13-柴油 21-公务车 31-冷机 32-分体空调 33-灭火器
								"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
								"dischargeValue": row.gong,	//值
								"refrigerator": '',	//冷机参数（1-冷机制
								"scopeThree": '',	//范围三类型
								"scopeTwo": ''	//范围二购买内容
							}]
						console.log(monthList)
						http.post('carbon/footmark/editFootmark',{
							"dischargeType": dischargeType,	//燃烧排放类型
							"nodeId": nodeId,
							"scopeType": scopeType,
							"year": year,
							monthList
							
						}).then(res =>{
							console.log(res)
							if(res.data.code==200){
								
								
								
								message.success('修改成功')
								getFootmark()
							}else{
								message.info(res.data.msg)
							}
						})
					}else{
						// let caMonth = this.state.caMonth
						
						let monthList = new Object
						monthList[caMonth]  = [
							{
								"dischargeEntity": 31,	//31-冷机 32-分体空调 33-灭火器
								"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
								"dischargeValue": row.zhi,	//值
								"refrigerator": '',	//冷机参数（1-冷机制
								"scopeThree": '',	//范围三类型
								"scopeTwo": ''	//范围二购买内容
							},
							{
								"dischargeEntity": 32,	//燃烧排放实体（32-分体空调 
								"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
								"dischargeValue": row.kong,	//值
								"refrigerator": '',	//冷机参数（1-冷机制
								"scopeThree": '',	//范围三类型
								"scopeTwo": ''	//范围二购买内容
							},
							{
								"dischargeEntity": 33,	//燃烧排放实体（33-灭火器 
								"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
								"dischargeValue": row.mie,	//值
								"refrigerator": '',	//冷机参数（1-冷机制
								"scopeThree": '',	//范围三类型
								"scopeTwo": ''	//范围二购买内容
							}]
						console.log(monthList)
						http.post('carbon/footmark/editFootmark',{
							"dischargeType": dischargeType,	//燃烧排放类型
							"nodeId": nodeId,
							"scopeType": scopeType,
							"year": year,
							monthList
							
						}).then(res =>{
							console.log(res)
							if(res.data.code==200){
								
								
								message.success('修改成功')
								getFootmark()
							}else{
								message.info(res.data.msg)
							}
						})
					}
					
				}else if(scopeType=='2'){
					// 范围二
					let monthList = new Object;
					monthList[caMonth]  = [
						{
							"dischargeEntity": '',	//11-天然气 12-煤气 13-柴油 21-公务车 31-冷机 32-分体空调 33-灭火器
							"dischargeType": '',	//燃烧排放类型（1-固定燃烧源的燃
							"dischargeValue": row.dian,	//值
							"refrigerator": '',	//冷机参数（1-冷机制
							"scopeThree": '',	//范围三类型
							"scopeTwo": 1	//范围二购买内容
						},
						{
							"dischargeEntity": '',	//燃烧排放实体（11-煤气 
							"dischargeType": '',	//燃烧排放类型（1-固定燃烧源的燃
							"dischargeValue": row.re,	//值
							"refrigerator": '',	//冷机参数（1-冷机制
							"scopeThree": '',	//范围三类型
							"scopeTwo": 2	//范围二购买内容
						}]
					console.log(monthList)
					http.post('carbon/footmark/editFootmark',{
						"dischargeType": '',	//燃烧排放类型
						"nodeId": nodeId,
						"scopeType": scopeType,
						"year": year,
						monthList
						
					}).then(res =>{
						console.log(res)
						if(res.data.code==200){
							
							
							message.success('修改成功')
							getFootmark()
						}else{
							message.info(res.data.msg)
						}
					}).catch(err =>{
						console.log(err)
					})
				}else if(scopeType=='3'){
					let monthList = new Object
					monthList[caMonth]  = [
						{
							"dischargeEntity": '',	//11-天然气 12-煤气 13-柴油 21-公务车 31-冷机 32-分体空调 33-灭火器
							"dischargeType": '',	//燃烧排放类型（1-固定燃烧源的燃
							"dischargeValue": row.feiji,	//值
							"refrigerator": '',	//冷机参数（1-冷机制
							"scopeThree": 1,	//范围三类型（1-差旅-飞机(km) 2-差旅-火车（km) 3-差旅-私家车(辆) 4-自来水(t) 5-纸张消耗(张)）
							"scopeTwo": ''	//范围二购买内容
						},
						{
							"dischargeEntity": '',	//燃烧排放实体（11-煤气 
							"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
							"dischargeValue": row.huoche,	//值
							"refrigerator": '',	//冷机参数（1-冷机制
							"scopeThree": 2,	//范围三类型
							"scopeTwo":''	//范围二购买内容
						},
						{
							"dischargeEntity": '',	//燃烧排放实体（11-煤气 
							"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
							"dischargeValue": row.che,	//值
							"refrigerator": '',	//冷机参数（1-冷机制
							"scopeThree": 3,	//范围三类型
							"scopeTwo":''	//范围二购买内容
						},
						{
							"dischargeEntity": '',	//燃烧排放实体（11-煤气 
							"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
							"dischargeValue": row.zilai,	//值
							"refrigerator": '',	//冷机参数（1-冷机制
							"scopeThree": 4,	//范围三类型
							"scopeTwo":''	//范围二购买内容
						},
						{
							"dischargeEntity": '',	//燃烧排放实体（11-煤气 
							"dischargeType": dischargeType,	//燃烧排放类型（1-固定燃烧源的燃
							"dischargeValue": row.zhizhang,	//值
							"refrigerator": '',	//冷机参数（1-冷机制
							"scopeThree": 5,	//范围三类型
							"scopeTwo":''	//范围二购买内容
						}]
					console.log(monthList)
					http.post('carbon/footmark/editFootmark',{
						"dischargeType": '',	//燃烧排放类型
						"nodeId": nodeId,
						"scopeType": scopeType,
						"year": year,
						monthList
						
					}).then(res =>{
						console.log(res)
						if(res.data.code==200){
							
							message.success('修改成功')
							getFootmark()
						}else{
							message.info(res.data.msg)
						}
					}).catch(err =>{
						console.log(err)
					})
				}
				setEditingKey('');
				setData(setData)
	    }
	  } catch (errInfo) {
	    console.log('Validate Failed:', errInfo);
	  }
	};
	const edit = (record: Partial<Item> & { key: React.Key }) => {
		myForm.setFieldsValue({ tian: '', mei: '', chai: '', ...record });
		console.log(record)
	 
		// setEditingKey(record.key);
		if(scopeType=='1'){
			if(dischargeType=='1'){
				setEditingKey(record.key);
				setCaMonth(record.caMonth);
				console.log(editingKey)
				// console.log(isEditing(record))
				
			}else if(dischargeType=='2'){
				myForm.setFieldsValue({ gong: '',...record });
				setEditingKey(record.key);
				setCaMonth(record.caMonth)
				
			}else if(dischargeType=='3'){
				myForm.setFieldsValue({ zhi: '',kong:'',mie:'',...record });
				setEditingKey(record.key);
				setCaMonth(record.caMonth)
				
			}
			
		}else if(scopeType=='2'){
			myForm.setFieldsValue({ re: '', dian: '', ...record });
			setEditingKey(record.key);
			setCaMonth(record.caMonth);
			
		}else if(scopeType=='3'){
			myForm.setFieldsValue({ huoche: '', feiji: '',zilai:'',che:'',zhizhang:'', ...record });
			setEditingKey(record.key);
			setCaMonth(record.caMonth);
			
			
		}
		
		
	};
			
	const cancel = () => {
		setEditingKey('')
		if(scopeType=='1'){
			if(dischargeType==1){
				setColumns([
						{
							title: '月份',
							dataIndex: 'caMonth',
							key: 'caMonth',
							
						},
						{
							title: '天然气（㎥）',
							dataIndex: 'tian',
							key: 'tian',
							editable: true,
							render: (text,record,_,action) =>{
								if( text==null||text==undefined||text===''){
									return '-'
								}else if(text!==''){
									return text
								}else{
									return text
								}
							}
							
						},
						{
							title: '煤气（㎥）',
							dataIndex: 'mei',
							key: 'mei',
							editable: true,
							
						},
						{
							title: '柴油（㎥）',
							dataIndex: 'chai',
							key: 'chai',
							editable: true,
						},
						{
						    title: '操作',
						    dataIndex: 'operation',
						    render: (_: any, record: Item) => {
						        const editable = isEditing(record);
								console.log(editable,'-----')
						        return editable ? (
						          <span>
						            <Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
						              确定
						            </Typography.Link>
						            <Typography.Link onClick={() => cancel(record.key)} >
						              取消
						            </Typography.Link>
						          </span>
						        ) : (
						          <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
						            编辑
						          </Typography.Link>
						        );
						      },
						    }
						
					 
					])
				getFootmark()
			}else if(dischargeType==2){
				setColumns([
					{
						title: '月份',
						dataIndex: 'caMonth',
						key: 'caMonth',
					},
					{
						title: '公务车（辆）',
						dataIndex: 'gong',
						key: 'gong',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined){
								return '-'
							}else if(text!==''){
								// console.log(text)
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '操作',
						dataIndex: 'operation',
						render: (_: any, record: Item) => {
							const editable = isEditing(record);
							console.log(editable,'-----')
							return editable ? (
							  <span>
								<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
								  确定
								</Typography.Link>
								<Typography.Link onClick={() => cancel(record.key)} >
								  取消
								</Typography.Link>
							  </span>
							) : (
							  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
								编辑
							  </Typography.Link>
							);
						},
					}
			 
				])
				getFootmark()
			}else if(dischargeType==3){
				
				setColumns([
					{
						title: '月份',
						dataIndex: 'caMonth',
						key: 'caMonth',
					},
					{
						title: '制冷剂（kg）',
						dataIndex: 'zhi',
						key: 'zhi',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined){
								return '-'
							}else if(text!==''){
								// console.log(text)
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '空调氟利昂（kg）',
						dataIndex: 'kong',
						key: 'kong',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined){
								return '-'
							}else if(text!==''){
								// console.log(text)
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '灭火器（个）',
						dataIndex: 'mie',
						key: 'mie',
						editable: true,
						render: (text,record,_,action) =>{
							if( text==null||text==undefined){
								return '-'
							}else if(text!==''){
								// console.log(text)
								return text
							}else{
								return text
							}
						}
					},
					{
						title: '操作',
						dataIndex: 'operation',
						render: (_: any, record: Item) => {
							const editable = isEditing(record);
							console.log(editable,'-----')
							return editable ? (
							  <span>
								<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
								  确定
								</Typography.Link>
							   <Typography.Link onClick={() => cancel(record.key)} >
								 取消
							   </Typography.Link>
							  </span>
							) : (
							  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
								编辑
							  </Typography.Link>
							);
						},
					}
			 
				])
				getFootmark()
			}
		} if(scopeType=='2'){
			setIsShow('none');
			setColumns([
				{
					title: '月份',
					dataIndex: 'caMonth',
					key: 'caMonth',
					editable: true,
				},
				{
					title: '外购电力（kwh）',
					dataIndex: 'dian',
					key: 'dian',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '外购热力（KJ）',
					dataIndex: 're',
					key: 're',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined||text===''){
							return '-'
						}else if(text!==''){
							return text
						}else{
							return text
						}
					}
				},
				
				{
					title: '操作',
					dataIndex: 'operation',
					render: (_: any, record: Item) => {
						const editable = isEditing(record);
						console.log(editable,'-----')
						return editable ? (
						  <span>
							<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
							  确定
							</Typography.Link>
							<Typography.Link onClick={() => cancel(record.key)} >
							  取消
							</Typography.Link>
						  </span>
						) : (
						  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
							编辑
						  </Typography.Link>
						);
					},
				}
		 
			])
			getFootmark()
		} if(scopeType=='3'){
			setColumns([
				{
					title: '月份',
					dataIndex: 'caMonth',
					key: 'caMonth',
				},
				{
					title: '差旅-火车（km）',
					dataIndex: 'huoche',
					key: 'huoche',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '差旅-飞机（km）',
					dataIndex: 'feiji',
					key: 'feiji',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '自来水（t）',
					dataIndex: 'zilai',
					key: 'zilai',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
		
				{
					title: '私家车（辆）',
					dataIndex: 'che',
					key: 'che',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '纸张消耗（张）',
					dataIndex: 'zhizhang',
					key: 'zhizhang',
					editable: true,
					render: (text,record,_,action) =>{
						if( text==null||text==undefined){
							return '-'
						}else if(text!==''){
							// console.log(text)
							return text
						}else{
							return text
						}
					}
				},
				{
					title: '操作',
					dataIndex: 'operation',
					render: (_: any, record: Item) => {
						const editable = isEditing(record);
						console.log(editable,'-----')
						return editable ? (
						  <span>
							<Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
							  确定
							</Typography.Link>
							<Typography.Link onClick={() => cancel(record.key)} >
							  取消
							</Typography.Link>
						  </span>
						) : (
						  <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
							编辑
						  </Typography.Link>
						);
					},
				}
						 
			])
			getFootmark()
		}
	};
	const mergedColumns = columns.map(col => {
		// console.log(!col.editable)
		if (!col.editable) {
		  return col;
		}
		return {
		  ...col,
		  onCell: (record: Item) => ({
			record,
			inputType: col.dataIndex === 'age' ? 'number' : 'text',
			dataIndex: col.dataIndex,
			title: col.title,
			editing: isEditing(record),
		  }),
		};
	  });
	  
	const disabledDate: RangePickerProps['disabledDate'] = current => {
		return current < dayjs(new Date('2023')) || current > dayjs().endOf('year')
	};
	 // disabledDate
	
	 
	// <Button type="primary" onClick={this.edit} className={['editbel',this.state.disabled===true?'className4':'className3']} style={{width:'65px',float:'right'}}>编辑</Button>
	return(
		<div className="allcontent1">
		<ConfigProvider locale={locale}>
			<div className="header">
				节点：
				<Select
					style={{ width: 217 }}
					onChange={handlepoint}
					defaultValue={currentUnit}
					loading={currentLoading}
					key={currentUnit}
				>
					{
						nodeList.length&&nodeList.map(res =>{
							return <Option value={res.id}>{res.nodeName}</Option>
						})
					}
				</Select>
				范围：
				<Select
				  defaultValue="范围一"
				  style={{ width: 217}}
				  onChange={handlepoint1}
				  options={[
					{
					  value: '1',
					  label: '范围一',
					},
					{
					  value: '2',
					  label: '范围二',
					},
					
					{
					  value: '3',
					  label: '范围三',
					},
				  ]}
				/>
				<Space>
					<ConfigProvider locale={locale}>
						 <DatePicker disabledDate={disabledDate}  onChange={onChangeyear} picker="year" />
					</ConfigProvider>
				</Space>
				<Button type="primary" onClick={search} style={{marginLeft:'24px'}}>查询</Button>
				
			</div>
			<div className="combustion">
					<Button style={{display:isShow}} onClick={() => combustionbtn(1)} className={[index===1?"active":'']}>固定燃烧源</Button>
					<Button style={{display:isShow}} onClick={() => combustionbtn(2)} className={[index===2?"active":'']}>移动燃烧源</Button>
					<Button style={{display:isShow}} onClick={() => combustionbtn(3)} className={[index===3?"active":'']} >逸散型排放源</Button>
					
					<Button type="primary" className={['editbel',disabled===true?'className3':'className4']} onClick={quxiao}  style={{width:'65px',float:'right'}}>取消</Button>
					<Button type="primary" 
					className={['editbel',disabled===true?'className3':'className4']}
					 onClick={define}  style={{width:'65px',float:'right'}}>确定</Button>
				<div>
				
				<Form  component={false} form={myForm}>
					<Table
						components={{
						  body: {
							cell: EditableCell,
						  },
						}}
						loading={loading}
						pagination={false}
						bordered
						dataSource={content}
						columns={mergedColumns}
						rowClassName="editable-row"
						rowClassName={
							(record, index) => {
							  let className = ''
							  className = index % 2 ===0 ? 'ou' : 'ji'
							  // console.log(className)
							  return className
							}
						}
						summary={pageData => {
							// console.log(pageData)
							let tian = 0;
							let totalRepayment = 0;
							let totaltian = 0		
							let totalmei = 0
							let totalchai = 0
							let totalgong = 0
							let totalzhi = 0
							let totalmie = 0
							let totalkong = 0
							let totaldian = 0
							let totalre = 0
							let totalche = 0
							let totalfeiji = 0
							let totalhuoche = 0
							let totalwushui = 0
							let totalzhizhang = 0
							let totalzilai = 0
							pageData.forEach(({ tian,mei,chai,gong ,zhi,kong,mie,dian,re,huoche,zhizhang,feiji,zilai,che}) => {
								if(tian){
									totaltian+=Number(tian)
								}
								if(mei){
									totalmei+=Number(mei)
								}
								if(chai){
									totalchai+=Number(chai)
								}
								if(gong){
									totalgong += Number(gong)
								}
								if(zhi){
									totalzhi += Number(zhi)
								}
								if(kong){
									totalkong += Number(kong)
								}
								if(mie){
									totalmie += Number(mie)
								}
								if(dian){
									totaldian += Number(dian)
								}
								if(re){
									totalre += Number(re)
								}
								if(huoche){
									// console.log(huoche)
									totalhuoche += Number(huoche)
								}
								if(feiji){
									totalfeiji += Number(feiji)
								}
								if(zilai){
									totalzilai += Number(zilai)
								}
								if(che){
									totalche+= Number(che)
								}
								if(zhizhang){
									totalzhizhang += Number(zhizhang)
								}
								// totalRepayment += repayment;
							});
							if(scopeType=='1'){
								if(dischargeType==1){
									// 范围一
									return (
										<>
											<Table.Summary.Row>
												<Table.Summary.Cell index={0}>合计</Table.Summary.Cell>
												<Table.Summary.Cell index={1}>
													<Text>{totaltian}</Text>
												</Table.Summary.Cell>
												<Table.Summary.Cell index={2}>
													<Text>{totalmei}</Text>
												</Table.Summary.Cell>
												<Table.Summary.Cell index={3}>
													<Text>{totalchai}</Text>
													
												</Table.Summary.Cell>
												<Table.Summary.Cell index={4}>
													
												</Table.Summary.Cell>
												
												
												
											</Table.Summary.Row>
											
										</>
									);
								}else if(dischargeType==2){
									// 范围二
									return (
										<>
											<Table.Summary.Row>
												<Table.Summary.Cell index={0}>合计</Table.Summary.Cell>
												<Table.Summary.Cell index={1}>
													<Text>{totalgong}</Text>
												</Table.Summary.Cell>
												<Table.Summary.Cell index={2}>
													
												</Table.Summary.Cell>
												
												
												
												
												
											</Table.Summary.Row>
											
										</>
									);
								}else if(dischargeType==3){
									// 范围三
									return (
										<>
											<Table.Summary.Row>
												<Table.Summary.Cell index={0}>合计</Table.Summary.Cell>
												<Table.Summary.Cell index={1}>
													
													<Text>{totalzhi}</Text>
												</Table.Summary.Cell>
												<Table.Summary.Cell index={2}>
													
													<Text>{totalkong}</Text>
												</Table.Summary.Cell>
												<Table.Summary.Cell index={3}>
													
													<Text>{totalmie}</Text>
												</Table.Summary.Cell>
												<Table.Summary.Cell index={4}>
													
												</Table.Summary.Cell>
											</Table.Summary.Row>
											
										</>
									);
								}
								
							}else if(scopeType=='2'){
								// 范围二
								return (
									<>
										<Table.Summary.Row>
											<Table.Summary.Cell index={0}>合计</Table.Summary.Cell>
											<Table.Summary.Cell index={1}>
												<Text>{totaldian}</Text>
											</Table.Summary.Cell>
											<Table.Summary.Cell index={2}>
												<Text>{totalre}</Text>
											</Table.Summary.Cell>
											<Table.Summary.Cell index={3}>
												
												
											</Table.Summary.Cell>
											
											
											
											
										</Table.Summary.Row>
										
									</>
								);
							}else if(scopeType=='3'){
								// 范围三
								return (
									<>
										<Table.Summary.Row>
											<Table.Summary.Cell index={0}>合计</Table.Summary.Cell>
											<Table.Summary.Cell index={1}>
												<Text>{totalhuoche}</Text>
											</Table.Summary.Cell>
											<Table.Summary.Cell index={2}>
												<Text>{totalfeiji}</Text>
											</Table.Summary.Cell>
											<Table.Summary.Cell index={3}>
												<Text>{totalzilai}</Text>
											</Table.Summary.Cell>
											<Table.Summary.Cell index={4}>
												<Text>{totalche}</Text>
											</Table.Summary.Cell>
											<Table.Summary.Cell index={5}>
												<Text>{totalzhizhang}</Text>
											</Table.Summary.Cell>
											<Table.Summary.Cell index={6}>
												
											</Table.Summary.Cell>
											
											
											
										</Table.Summary.Row>
										
									</>
								);
							}
							
						}}
					/>
					</Form>	
				</div>
			</div>
		</ConfigProvider>
		</div>
	)
}
	


export default Footmark