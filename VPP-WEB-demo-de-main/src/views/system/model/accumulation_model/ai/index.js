import React, { Component } from 'react';
import { DatePicker, Space ,Button,Table ,ConfigProvider ,message,InputNumber,
Form, Input, Popconfirm, Typography,Select
} from 'antd';
// import {  Table, Space ,Modal,Button,Form, Input, InputNumber, Popconfirm, Typography } from 'antd';

import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import './index.css'
import http from '../../../../../server/server.js'
const { Option } = Select;

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
  inputType: 'select' | 'text';
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
	// console.log(dataIndex)
const inputNode = inputType === 'select' &&dataIndex=='property' ? <Select style={{ width: 120 }} >
		<Option value="尖">尖</Option>
		<Option value="峰">峰</Option>
		<Option value="平">平</Option>
		<Option value="谷">谷</Option>
    </Select> :inputType === 'select' &&dataIndex=='strategy' ? <Select style={{ width: 120 }} >
		<Option value="充电">充电</Option>
		<Option value="放电">放电</Option>
		<Option value="待机">待机</Option>
    </Select>: <Input />;

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
class ai extends Component {
	constructor(props) {
		super(props)
		this.state={
			dataSource:[],
			nodeId:'',
			systemId:'',
			start:'',
			page:1,
			editingKey:'',
			data:[],
			id:'',
			thismonth:''
			
		}
	}
	// 监听值
	componentWillReceiveProps(nextProp){
		
		if(this.props.istrue ==true){
			console.log(nextProp)
			this.setState({
				nodeId:nextProp.nodeId,
				systemId:nextProp.systemId,
				strategyExpiryDate:nextProp.strategyExpiryDate
			},() =>{
				console.log(this.state.nodeId)
				this.refs.myForm.setFieldsValue({
					year: nextProp.shareProportionExpiryDate,
				});
				this.props.changeData()
			})
			
		}else{
			return false
		}
		
	}
	componentDidMount(){
		// if(this.props.nodeId){
		// 	this.getlist()
		// }
		this.setState({
			data:originData,
			thismonth:dayjs().format("YYYY-MM")
		})
	}
	// 获取数据
	getlist(){
		http.post('system_management/energy_model/energy_storage_model/findStorageEnergyStrategy',{
			"number":this.state.page,
			"pageSize": 10,
			"ts": this.state.start
		}).then(res =>{
			console.log(res)
			if(res.data.code ==200){
				let content = res.data.data.content
				let dataSource = []
				content.map(res =>{
					dataSource.push({
						createdTime:res.createdTime,
						effectiveDate:res.createdTime,
						etime:res.etime,
						id:res.id,
						multiplyingPower:res.multiplyingPower,
						nodeId:res.nodeId,
						order:res.order,
						etime:res.etime,
						priceHour:res.priceHour,
						property:res.property,
						stime:res.stime,
						strategy:res.strategy,
						systemId:res.systemId,
						timeFrame:res.timeFrame,
						updateTime:res.updateTime,
						key:res.id,
						strategyHour:res.strategyHour,
						strategyForecasting:res.strategyForecasting
					})
				})
				this.setState({
					dataSource:dataSource,
					total:res.data.data.totalElements
				})
			}else{
				message.error(res.data.msg)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	// 日期
	onChange =(date, dateString) =>{
		let day = dateString.substr(0,7)
		this.setState({
			start:day
		},() =>{
			this.getlist()
		})
	}

	render(){
		let {dataSource,editingKey,data} = this.state
		const columns = [
		  {
			title: '时间范围',
			dataIndex: 'timeFrame',
			key: 'timeFrame',
		  },
		  {
			title: '属性',
			dataIndex: 'property',
			key: 'property',
			render: (text,record,_,action) =>{
				return text?text:'-'
			}
		  },
		  {
			title: '每小时电价',
			dataIndex: 'priceHour',
			key: 'priceHour',
			render: (text,record,_,action) =>{
				return text?text:'-'
			}
		  },
		  {
			title: '充放电策略设置',
			dataIndex: 'strategy',
			key: 'strategy',
			render: (text,record,_,action) =>{
				return text?text:'-'
			}
		  },
		  {
			title: '充放电策略预测',
			dataIndex: 'strategyForecasting',
			key: 'strategyForecasting',
			render: (text,record,_,action) =>{
				return text?text:'-'
			}
			// render: (_: any, record: Item) => {
			// 	return ''
			// }
		  },
		  {
				title: '储能小时',
				dataIndex: 'strategyHour',
				key: 'strategyHour',
				render: (text,record,_,action) =>{
					return text?text:'-'
				}
		  },
		  {
		  			title: '充放电倍率',
		  			dataIndex: 'multiplyingPower',
		  			key: 'multiplyingPower',
					render: (text,record,_,action) =>{
						return text?text:'-'
					}
					
		  },
		  
		];
		const onChangepage=(page) =>{
			console.log(page)
			this.setState({
				page:page.current
			},() =>{
				this.getlist()
			})
		}
		
		return(
			<div className="allcontenta1" style={{padding:'15px 15px'}}>
				<div className="modalheader">
					<ConfigProvider  locale={locale}>
						<DatePicker onChange={this.onChange} />
					</ConfigProvider>

				</div>
				<Form ref="myForm" component={false}>
				      <Table
				        components={{
				          body: {
				            cell: EditableCell,
				          },
				        }}
				        // bordered
				        dataSource={dataSource}
				        columns={columns}
				        // rowClassName="editable-row"
						rowKey={record =>record.id}
						pagination={
							{
							  total: this.state.total,//数据的总条数
							  defaultCurrent: 1,//默认当前的页数
							  defaultPageSize: 10,//默认每页的条数
							  showSizeChanger:false,
							 
							}
						}
						onChange={onChangepage}
				        // pagination={false}
				      />
				    </Form>
				

			</div>
		)
	}
}

export default ai