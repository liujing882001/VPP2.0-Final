import React,{Component} from 'react'

import {Button,Select,Input ,Table,InputNumber,Form,Typography,Popconfirm } from 'antd';

import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import './index.css'
import {
  InfoCircleOutlined
} from '@ant-design/icons';
import http from '../../../server/server.js'
import axios from 'axios'
const { Option } = Select;
const { Search } = Input;


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
          rules={[
            {
              required: true,
              message: `Please Input ${title}!`,
            },
          ]}
        >
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};
const mergeCell = (val, index: number, data: any[]) => {
        const obj = { children: val, props: {}};
        const modules = Array.from(new Set(data.map((d: any) => d.module)));
		
        const detailInfo: any = modules.map((m: string) => data.filter((d: any) => d.module === m));
		// console.log(detailInfo)
        detailInfo.forEach((detail: any) => {
			// console.log(detail)
           const firstIndex = data.findIndex((d: any) => d.module === detail[0].module);
           if (index === firstIndex) {
                obj.props.rowSpan = detail?.length;
				// console.log(obj.props)
				obj.props.children = "<button>编辑</button>"
           }
           if (index > firstIndex && index < firstIndex + detail?.length) {
                obj.props.rowSpan = 0;
           }
        })
        return obj
}
class stored extends Component {
	constructor(props) {
		super(props)
		this.state={
			data:[
		  {
		    key: '1',
		    age: '谷',
			time:'22:00-8:00',
		    tel: '350',
		    phone: '0.4258',
		    address: '充',
			hour:4,
			duration:1400
		  },
		  {
		    key: '2',
			time:'8:00-11:00',
		    tel: '0571-22098333',
		    phone: '1.0171',
		    age: '平',
		    address: '放',
			hour:3,
			duration:1050
		  },
		  {
		    key: '3',
			time:'11:00-13:00',
		    age: '谷',
		    tel: '0575-22098909',
		    phone: '0.4258',
		    address: '充',
			hour:2,
			duration:700
		  },
		  {
		    key: '4',
			time:'13:00-19:00',
		    name: 'Jim Red',
		    age: '平',
		    tel: '0575-22098909',
		    phone: '1.0159',
		    address: '待机',
			hour:0,
			duration:0
		  },
		  {
		    key: '5',
			time:'19:00-21:00',
		    name: 'Jake White',
		    age: '峰',
		    tel: '0575-22098909',
		    phone: '1.364',
		    address: '放',
			hour:2,
			duration:700
		  },
		  {
		    key: '6',
		  	time:'21:00-22:00',
		    name: 'Jake White',
		    age: '平',
		    tel: '0575-22098909',
		    phone: '1.0171',
		    address: '放',
			hour:1,
			duration:350
		  },
		],
			setData:[],
			editingKey:'',
			setEditingKey:''
		}
	}
	onSearch =(val) =>{
		console.log(val)
	}
	dao =() =>{
		axios({
			method: 'get',
			url:'excel/storedEnergyStrategy',
			responseType: 'arraybuffer',
			
		}).then(res =>{
			console.log(res)
			if(res.status ==200){
				const url = window.URL.createObjectURL(new Blob([res.data]));
				const link = document.createElement('a'); //创建a标签
				link.style.display = 'none';
				link.href = url; // 设置a标签路径
				link.download = '报表.xlsx'; //设置文件名， 也可以这种写法 （link.setAttribute('download', '名单列表.xls');
				document.body.appendChild(link);
				link.click();
				URL.revokeObjectURL(link.href); // 释放 URL对象
				document.body.removeChild(link);
				
			}
		})

	}
	render(){
		let {editingKey,data} = this.state 
		const isEditing = (record) => record.key === editingKey;
		
		const edit = (record: Partial<Item> & { key: React.Key }) => {
			console.log(record)
		    this.refs.myForm.setFieldsValue({ hour: '', address: '', ...record });
		    // setEditingKey(record.key);
			this.setState({
				editingKey:record.key
			})
		};
		
		const cancel = () => {
		    // setEditingKey('');
			this.setState({
				editingKey:''
			})
		};
		const save = async (key) => {
			console.log(key)
		    try {
		      const row = ( this.refs.myForm.validateFields());
				console.log(row)
		      const newData = [...data];
			  console.log(newData)
		      const index = newData.findIndex(item => key === item.key);
			  console.log(index)
		      if (index > -1) {
		        const item = newData[index];
				console.log(item)
		        newData.splice(index, 1, {
		          ...item,
		          ...row,
		        });
				console.log(newData)
		        // setData(newData);
				// this.setState({
				// 	data:newData
				// })
				this.setState({
					data:newData,
					editingKey:''
				})
		        // setEditingKey('');
		      } else {
		        newData.push(row);
		        // setData(newData);
		        // setEditingKey('');
				this.setState({
					data:newData,
					editingKey:''
				})
		      }
		    } catch (errInfo) {
		      console.log('Validate Failed:', errInfo);
		    }
		  };
		const columns = [
			{
				title: '时段',
				dataIndex: 'time',
			},
			{
				title: '类型',
				dataIndex: 'age',
				
			},
			{
				title: '电价（元/kWh）',
				dataIndex: 'phone',
			},
			{
				title: '充放电策略',
				dataIndex: 'address',
				editable: true,
			},
			{
				title: '有效利用小时数（h）',
				dataIndex: 'hour',
				editable: true,
			},
			{
				title: '全年充放电天数',
				dataIndex: 'tel',
				render: (item:any, _record:any, index:number) => mergeCell(item,index,data) 
				// rende
			},
			{
				title: '累计时长（h）',
				dataIndex: 'duration',
			},
			{
				title: '操作',
				dataIndex: '',
				render: (_: any, record: Item) => {
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
						<Typography.Link disabled={editingKey !== ''} 
						onClick={() => edit(record)}>
							编辑
						</Typography.Link>
					);
				},
				
				
			}
		
		];
		const mergedColumns = columns.map(col => {
		    if (!col.editable) {
		        return col;
		    }
		    return {
		        ...col,
		        onCell: (record: Item) => ({
					record,
					inputType: col.dataIndex === 'age' ? 'number' : 'text',
					selectType:col.dataIndex === 'address',
					dataIndex: col.dataIndex,
					title: col.title,
					editing: isEditing(record),
		        }),
		    };
		});
		  
		    return (
			
				<div>
					<div className="powerheader s" style={{background:'#09183F',margin:'19px 0px'}}>
						<Select defaultValue="海门支局储能基站" style={{ width: 160 }} onChange={this.handleChange}>
						    <Option value="jack">海门支局储能基站</Option>
						</Select>
						
					</div>
					<div className="prompt"><InfoCircleOutlined />
					以下充放电策略默认为AI模型智能分析结果，如需修改，请自行前往操作区编辑。</div>
					<div className="subject">
						<b>项目地点：浙江省（分时电价来源）</b>
						<Button type="primary" onClick={this.dao} style={{float:'right',marginLeft:'16px'}}>导出</Button>
						
					</div>
					<Form ref="myForm" component={false}>
						<Table
							components={{
								body: {
								  cell: EditableCell,
								},
							}}
							bordered
							dataSource={data}
							columns={mergedColumns}
							rowClassName={
							(record, index) => {
							  let className = ''
							  className = index % 2 ===0 ? 'ou' : 'ji'
							  // console.log(className)
							  return className
								}
							}
							pagination={false}
						/>
					</Form>
				</div>
		    );
		
	}
	
}
export default stored

















