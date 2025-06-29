import React,{Component} from 'react'
// import 'antd/dist/antd.css'
// import 'antd/dist/antd.min.css';
// import './index.css'
import { Tree,DatePicker,ConfigProvider,Button,Table ,Input,message,Spin } from 'antd';
import type { DataNode, TreeProps } from 'antd/lib/tree';
import dayjs from 'dayjs';
import * as echarts from 'echarts';
// import echarts  from '../../../echarts.js'
import http from '../../../../server/server.js'
import axios from 'axios'

import type { DatePickerProps } from 'antd';
const { Search } = Input;
const { RangePicker } = DatePicker;

const dateFormat = 'YYYY-MM-YY';
const columns = [
  {
    title: '时间',
    dataIndex: 'timeStamp',
    key: 'timeStamp',
	// width: 250,
  },
  {
    title: '超短期预测准确率（%）',
    dataIndex: 'ultraShortTermForecast',
    key: 'ultraShortTermForecast',
	render: (text,record,_,action) =>{
		if( text==null||text==undefined||text===""){
			return '-'
		}else if(text=='-'){
				return '-'
		}else{
			return Number(text).toFixed(2)
		}
	}
	// width: 150,
  },
	{
		title: '日前预测准确率（%）',
		dataIndex: 'currentForecast',
		key: 'currentForecast',
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===""){
				return '-'
			}else if(text=='-'){
				return '-'
			}else{
				return Number(text).toFixed(2)
			}
		}
	},
	{
		title: '中期预测准确率（%）',
		dataIndex: 'mediumTermForecast',
		key: 'mediumTermForecast',
		render: (text,record,_,action) =>{
			if( text==null||text==undefined||text===""){
				return '-'
			}else if(text=='-'){
				return '-'
			}else{
				return Number(text).toFixed(2)
			}
		}
	},
];
class pv_accurate extends Component {
	constructor(props) {
		super(props)
		this.state={
			currentIndex:0,
			current:1,
			dateType:1,
			forecastType:'PvForecasting',
			treeData:[],
			id:'',
			type:'',
			dates:'',
			echartsdate:[],
			activePower:[],
			aiTimePrice:[],
			number:'',
			dateType:1,
			expandedKeys:[],
			setExpandedKeys:[],
			searchValue:'',
			setSearchValue:'',
			autoExpandParent:true,
			setAutoExpandParent:true,
			timeStamp:[],
			loading: false,
			startTs:'',
			endTs:'',
			total:'',
			dateString:'',
			dateStringend:'',
			dateStringmonth:'',
			dateStringmonthend:'',
			dateStringday:'',
			dateStringdayend:'',
			treeLoading:false,
			TreeList:[],
			keyValue:'',
			setwidth:'',
			startString:'',
			copyTree: [],//备份 treeData
			copyExpandedKeys:[] ,//备份 展开key 
			currentNum:1,
			page:1
			
		}
		this.selectRef = React.createRef();
	}
	componentDidMount() {
		this.tabFn(1)
		this.tree()
	}
	componentDidUpdate = (prevProps,prevState) =>{
		
		// console.log(this.props.pCount)
		// console.log(this.state.setwidth)
		if (prevProps.pCount !== this.state.setwidth) {
			// console.log(prevProps.pCount)
			this.setState({
				setwidth:prevProps.pCount
			},() =>{
				// console.log(this.state.setwidth)
			})
		}
	}
	setPageHeight = () => {
		this.setState({
			pageHeight: document.body.clientHeight - 104
		},() =>{
			console.log(this.state.pageHeight,'-------')
		})
	}

	// componentDidMount(){
		
	// }

	devices =() =>{
		let {systemId} = this.state
		if(this.state.dateString==""||this.state.id==""){
			message.info('时间或节点不能为空')
		}else{
			axios({
				method: 'post',
				url: 'load_management/ai_prediction/pvPredictionPrecisionStatisticsListPageExcel',
				responseType: 'arraybuffer',
				data:{
					"endTs": this.state.dateStringend,
					"nodeId": this.state.nodeId,
					"number": 1,
					"pageSize": 100,
					"startTs": this.state.dateString,
					"systemId":systemId
					// endTs: this.state.dateStringend,
					// nodeId: this.state.id,
					// startTs: this.state.dateString
				}
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
		
	}
	// 树
	tree(){
		let {forecastType} = this.state
		this.setState({
			treeLoading:true
		})
		http.post('tree/areaPvForestShortView').then(res =>{
			if(res.data.code ==200){
				let data = res.data.data
				let number = 0
				function f(arr) {
					arr.forEach(item=>{
						if(item.children) {
							f(item.children) 
							let children = item.children
							for(var i=0;i<children.length;i++){
								if(children[i].type=='NODE'){
									number++
								}
							}
						}
					})
				}
				f(data)
				console.log(number)
				let a = this.expandedKeysFun(res.data.data);
				this.setState({
					treeData:res.data.data,
					number:number,
					treeLoading:false,
					TreeList:res.data.data,
					copyTree:res.data.data,
					// copyExpandedKeys:a
				})
			}
		})
	}
	tabFn(index) {
		console.log(index)
		this.setState({
			current: index,
			dateType:index,
			keyValue: new Date(),
			dateString:'',
			dateStringend:''
		})
	}
	clsFn(index, curCls, cls) {
		let { current } = this.state;
		// console.log(current)
		return current === index ? curCls : cls;
	}
	dateChange =(data,dataString) =>{
		console.log(dataString)
		this.setState({
			dateString:dataString[0],
			dateStringend:dataString[1]
		})
	}

	// 搜索
	arround = () =>{
		let {dateType,dateString,systemId} = this.state
		
		
		if(dateString){
			this.setState({
				loading:true,
				currentNum:1,
				page:1
			},() =>{
				this.pvPredictionPrecisionStatisticsListPage()
			})

			
		}else{
			message.info('请选择日期')
		}
		
	}
	pvPredictionPrecisionStatisticsListPage(){
		http.post('load_management/ai_prediction/pvPredictionPrecisionStatisticsListPage',{
			"endTs": this.state.dateStringend,
			"nodeId": this.state.nodeId,
			"number": this.state.page,
			"pageSize": 10,
			"startTs": this.state.dateString,
			"systemId":this.state.systemId
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				this.setState({
					dataSource:res.data.data.content,
					total:res.data.data.totalElements,
					loading:false,
				},() =>{
					console.log(this.state.dataSource)
				})
			}
			
		})
	}
	
	// 获取父节点
	getParentNode(tree, childId) {
		
		// 遍历树节点
		for (let node of tree) {
	    // 如果当前节点就是目标节点的父节点，直接返回当前节点id
			if (node.children && node.children.some(child => child.key === childId)) {
				return node.id;
			}
			// 否则继续遍历当前节点的子节点
			if (node.children) {
				const parentId = this.getParentNode(node.children, childId);
				if (parentId !== null) {
					return parentId;
				}
			}
		}
		// 如果没有找到父节点，则返回null
		return null;
	}
	onSelect= (selectedKeys, info) => {
		// console.log(dates)
		let {dates,current,treeData} =this.state
		console.log(dates,current)
	    console.log('selected', selectedKeys, info.node);
		if(info.node.type=='SYSTEM'){
			console.log(this.getParentNode(treeData,info.node.key))
			this.setState({
				id: info.node.id,
				systemId:info.node.id,
				nodeId:this.getParentNode(treeData,info.node.key)
			},() =>{
				console.log(this.state.id)
			})
		}else{
			this.setState({
				id: info.node.id,
				systemId:'',
				nodeId:info.node.id
			},() =>{
				console.log(this.state.id)
			})
		}
		
		if(this.state.endTs !=''){
			
			this.setState({
				id: info.node.key,
				loading:true
			},() =>{
				
			})
		}
		
		
	};

	
	arrayTreeFilter = (data, predicate, filterText) => {
		const nodes = data;
		console.log(nodes)
		// 如果已经没有节点了，结束递归
		if (!(nodes && nodes.length)) {
			return;
		}
		const newChildren = [];
		for (const node of nodes) {
			if (predicate(node, filterText)) {
				// 如果自己（节点）符合条件，直接加入到新的节点集
				newChildren.push(node);
				// 并接着处理其 children,（因为父节点符合，子节点一定要在，所以这一步就不递归了）
				// node.children = this.arrayTreeFilter(node.children, predicate, filterText);
			} else {
				// 如果自己不符合条件，需要根据子集来判断它是否将其加入新节点集
				// 根据递归调用 arrayTreeFilter() 的返回值来判断
				const subs = this.arrayTreeFilter(node.children, predicate, filterText);
				// 以下两个条件任何一个成立，当前节点都应该加入到新子节点集中
				// 1. 子孙节点中存在符合条件的，即 subs 数组中有值
				// 2. 自己本身符合条件
				if ((subs && subs.length) || predicate(node, filterText)) {
					node.children = subs;
					newChildren.push(node);
				}
			}
		}
		return newChildren;
	}
	 
	filterFn = (data, filterText) => { //过滤函数
		if (!filterText) {
			return true;
		}
		return (
			new RegExp(filterText, "i").test(data.title) //我是一title过滤 ，你可以根据自己需求改动
		);
	}
	flatTreeFun = (treeData) => { //扁平化 tree
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
	expandedKeysFun = (treeData) => { //展开 key函数
		if (treeData && treeData.length == 0) {
			return [];
		}
		//console.log(treeData)
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
	onChanges = (e) => { //搜索框 change事件
		console.log(e)
		let value = e;
		if (value == "") { //为空时要回到最初 的树节点
			let { copyTree,copyExpandedKeys } = this.state;
			// let res = this.arrayTreeFilter(JSON.parse(copyTree), this.filterFn, value);
			// let expkey = this.expandedKeysFun(res);
			this.tree()
			this.setState({
				// treeData: copyTree,
				expandedKeys:copyExpandedKeys
			})
		} else {
			 let { copyTree, copyExpandedKeys } = this.state;
			 console.log(copyTree)
			let res = this.arrayTreeFilter(copyTree, this.filterFn, value);
			let expkey = this.expandedKeysFun(res);
			let num = 0
			if(res.length>0){
				function f(arr) {
					arr.forEach(item=>{
						if(item.children) {
							f(item.children) 
							let children = item.children
							for(var i=0;i<children.length;i++){
								if(children[i].type=='NODE'){
									num++
								}
							}
						}
					})
				}
				f(res)
			
			
			}
			this.setState({
				treeData: res,
				expandedKeys: expkey,
				number:num
			})
		}
 
	}
	// 获取父节点
	
	render(){
		let {treeData,current,dates,number,searchValue,dataSource,loading
		,autoExpandParent,expandedKeys,dateType,dateString,dateStringmonth,dateStringday,
		dateStringend,dateStringmonthend,dateStringdayend,copyTree
		} = this.state		 
		const onCheck: TreeProps['onCheck'] = (checkedKeys, info) => {
		    console.log('onCheck', checkedKeys, info);
		};
		
		const onExpand = expandedKeys => {
			console.log(expandedKeys)
		    this.setState({
		      expandedKeys,
		      autoExpandParent: false,
		    });
		  };
		
		
		
		const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
		    console.log('selected', selectedKeys, info);
			let s1 = selectedKeys.toString().substring(0,32)
			console.log(s1)
			this.setState({
				nodeId:s1,
				nodeIds:s1,
				subSystemIds:s1
			},() =>{
				this.chosetanle()
			})
		};
		const onChangetab =(page) =>{
			console.log(page)
			this.setState({
				page:page.current,
				currentNum:page.current
			},() =>{
				this.pvPredictionPrecisionStatisticsListPage()
			})
		}
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current < dayjs(new Date('2023-01')) || current > dayjs().add('3','month')
			
		};
		return(
			<div className="adjustablecont">
				<div className="navigation" style={{width:300,marginRight:'16px'}}>
					<div className="packingtion">
						<Search 
							style={{ marginBottom: 8,marginTop: 24 }}
							placeholder="查询节点" onSearch={this.onChanges} />
					</div>
					<p className="numbers">全部节点（{number}）</p>
					<Spin spinning={this.state.treeLoading}  size="middle">
						<Tree
						  defaultExpandAll
						  onSelect={this.onSelect}
						  onCheck={onCheck}
						  onExpand={onExpand}
						  expandedKeys={expandedKeys}
						  autoExpandParent={autoExpandParent}
						  treeData={treeData}
						  // treeData={treeData2}
						/>
					</Spin>
					
				</div>
				<div className="adjustable" style={{marginLeft:315}}>
					<div className="adjustableheader" style={{paddingBottom:'10px'}}>
						
						<div className="chosedatebyn"  style={{paddingBottom:'10px'}}>
							<RangePicker disabledDate={disabledDate} onChange={this.dateChange} />
							<Button type="primary" onClick={this.arround}>查询</Button>
							<Button style={{float:'right'}} type="primary" onClick={this.devices}>导出</Button>
							<Button style={{float:'right'}} type="primary" onClick={this.arround} >最新预测</Button>
							
						</div>
						
						
					</div>
					
					
					<div style={{padding:15}} ref={this.selectRef}>
						<Table dataSource={dataSource} 
							columns={columns}
							// scroll={{ y: 300 }}
							loading={loading}
							onChange={onChangetab}
							pagination={
								{
									total: this.state.total,//数据的总条数
									defaultCurrent: 1,//默认当前的页数
									defaultPageSize: 11,//默认每页的条数
									showSizeChanger:false,
									current:this.state.currentNum
								}
							}
							rowClassName={
								(record, index) => {
									let className = ''
									className = index % 2 ===0 ? 'ou' : 'ji'
									// console.log(className)
									return className
								}
							}
						 
						 />
					</div>

				</div>
			</div>
		)
	}
	
}

export default pv_accurate