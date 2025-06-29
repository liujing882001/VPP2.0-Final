import React, { Component } from 'react';
import { Table } from 'antd';
import './index.css'
const dataSource = [
  {
    key: '1',
    name: '用电',
    age: '用电量（度）*0.96',
    address: '4度',
	liang:10
  },
  {
    key: '2',
    name: '自来水',
    age: '用电量（度）*0.96',
    address: '3吨',
	liang:27
  },
  {
    key: '3',
    name: '天然气',
    age: '用气量（立方米）*2.17',
    address: '10立方米',
	liang:2.88
  },
  {
    key: '4',
    name: '自驾车汽油',
    age: '耗油量（升）*2.7',
    address: '10升',
  	liang:21.7
  },
  {
    key: '5',
    name: '生活垃圾',
    age: '垃圾（千克）*2.06',
    address: '3千克',
  	liang:6.18
  },
  {
    key: '6',
    name: '一次性筷子',
    age: '筷子数量（双）*0.0228',
    address: '3双',
  	liang:0.0684
  },
  {
    key: '7',
    name: '冰箱',
    age: '冰箱开门时间（分钟）*0.026',
    address: '35分钟',
  	liang:0.13
  },
  {
    key: '8',
    name: '饮水机',
    age: '开启时间（小时）*0.2',
    address: '24小时',
  	liang:4.8
  },
];

const columns = [
  {
    title: '日常行为',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '碳排放系数（kg）',
    dataIndex: 'age',
    key: 'age',
  },
  {
    title: '一天使用量（单位）',
    dataIndex: 'address',
    key: 'address',
  },
  {
    title: '一天碳排放量（单位）',
    dataIndex: 'liang',
    key: 'liang',
  },
];
class pattern extends Component {
	constructor(props) {
		super(props)
		this.state={
			
		}
	}
	
	render(){
		return(
			<div className="allcontent">
				<div className="paatem">碳模型算法</div>
				<Table bordered dataSource={dataSource} columns={columns} />;

			</div>
		)
	}
	
}

export default pattern