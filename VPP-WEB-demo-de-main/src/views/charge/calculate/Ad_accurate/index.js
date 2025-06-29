import React,{Component} from 'react'
// import 'antd/dist/antd.min.css';
// import 'antd/dist/antd.css'
import './index.css'
import { Tree,DatePicker,ConfigProvider,Button,Table ,Input,Spin,message } from 'antd';
import type { DataNode, TreeProps } from 'antd/lib/tree';
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import * as echarts from 'echarts';
// import echarts  from '../../../echarts.js'
import http from '../../../../server/server.js'
import axios from 'axios'

import type { DatePickerProp } from 'antd';
const { Search } = Input;

const dateFormat = 'YYYY-MM-YY';
const { RangePicker } = DatePicker;



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
		if( text==null||text==undefined){
			return '-'
		}else if(text!==''){
			// console.log(text)
			return Number(text).toFixed(2)
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
		if( text==null||text==undefined){
			return '-'
		}else if(text!==''){
			// console.log(text)
			return Number(text).toFixed(2)
		}else{
			return Number(text).toFixed(2)
		}
	}
  },
];
class Ad_accurate extends Component {
	constructor(props) {
		super(props)
		this.state={
			currentIndex:0,
			current:1,
			forecastType:'LoadForecasting',
			treeData:[],
			id:'',
			type:'',
			dates:'',
			echartsdate:[],
			activePower:[],
			aiTimePrice:[],
			number:5,
			dateType:1,
			expandedKeys:[],
			setExpandedKeys:[],
			searchValue:'',
			setSearchValue:'',
			autoExpandParent:true,
			setAutoExpandParent:true,
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
			systemId:'',
			setwidth:'',
			newidth:'',
			nodeId:'',
			page:1,
			copyTree:[],
			copyExpandedKeys:[] //备份 展开key 
		}
		this.selectRef = React.createRef();
	}
	componentDidMount() {
		this.tabFn(1)
		this.tree()
		// 后端传过来的数据，这里应该是res.data，这里为了方便大家观看，用模拟数据
		
				// CSDN coderYYY

		// this.statement()
		// console.log(this.props)
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

	// 导出
	devices =() =>{
		let {dateString,dateStringend,nodeId,systemId,page} = this.state
		axios({
			method: 'post',
			url: 'load_management/ai_prediction/loadPredictionPrecisionStatisticsListPageExcel',
			responseType: 'arraybuffer',
			data:{
				"endTs": dateStringend,
				"nodeId": this.state.nodeId,
				"number": page,
				"pageSize": 10,
				"startTs": dateString,
				"systemId": systemId
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
	
	//左侧tree
	tree(){
		let {forecastType} = this.state
		this.setState({
			treeLoading:true
		})
		http.post('tree/areaLoadForestShortView').then(res =>{
			if(res.data.code ==200){
				let data = res.data.data
				let number = 0;    
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
	// 选择年月日
	tabFn(index) {
		console.log(index)
		this.setState({
			current: index,
			dateType:index,
			keyValue: new Date(),
			dateStringmonth:'',
			dateStringmonthend:''
		})
	}
	clsFn(index, curCls, cls) {
		let { current } = this.state;
		// console.log(current)
		return current === index ? curCls : cls;
	}
	// 时间选择
	dateChange =(data,dateString) =>{
		this.setState({
			dateString:dateString[0],
			dateStringend:dateString[1]
		})
	}
	
	//查询
	arround =() =>{
		let {dateType,dateString,dateStringend,dateStringmonth,nodeId,systemId,page} = this.state
		
		
		if(dateString ==''){
			message.info('请选择日期')
		}else{
			this.setState({
				loading:true
			})
			
			
			this.loadPredictionListPage()
			
		}
		
	}
	// 类表
		
	loadPredictionListPage(){
		let {dateString,dateStringend,page,systemId} = this.state
		http.post('load_management/ai_prediction/loadPredictionPrecisionStatisticsListPage',{
			"endTs": dateStringend,
			"nodeId": this.state.nodeId,
			"number": page,
			"pageSize": 10,
			"startTs": dateString,
			"systemId": systemId
		}).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let echartsdate = []
				let activePower = []
				let aiTimePrice = []
				this.setState({
					dataSource:res.data.data.content,
					total:res.data.data.totalElements,
					loading:false,
				})
			}else{
				message.info(res.data.msg)
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
	//选择tree
	onSelect= (selectedKeys, info) => {
		// console.log(dates)
		let {dates,current,treeData} =this.state
		console.log(dates,current)
	    console.log('selected', selectedKeys, info);
		// this.setState({
		// 	id: info.node.id,
		// },() =>{
		// 	console.log(this.state.id)
		// })
		console.log(this.getParentNode(treeData,info.node.key))
		if(info.node.type=='SYSTEM'){
			this.setState({
				systemId:info.node.id,
				nodeId:this.getParentNode(treeData,info.node.key)
			})
		}else if(info.node.type=='NODE'){
			this.setState({
				systemId:'',
				nodeId:info.node.id
			})
		}
		
		
		
		
		
	};
	// 分页
	handlePagination =(page) =>{
		console.log(page)
		this.setState({
			page:page
		},() =>{
			this.loadPredictionListPage()
		})
	}

	
		
		
	
	arrayTreeFilter = (data, predicate, filterText) => {
	       const nodes = data;
				// console.log(nodes)
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
	render(){
		let {treeData,current,dates,number,searchValue,dataSource,loading,
		autoExpandParent,expandedKeys,setwidth
		} = this.state	
		const onCheck: TreeProps['onCheck'] = (checkedKeys, info) => {
		    console.log('onCheck', checkedKeys, info);
		};
		
		const onExpand = (expandedKeys,info) => {
			// let list = 
			console.log(expandedKeys)
			console.log(info)
		    this.setState({
		      expandedKeys,
		      autoExpandParent: false,
			  id:info.node.key
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
		
		const disabledDate: RangePickerProps['disabledDate'] = current => {
			return current < dayjs(new Date('2023-01')) || current > dayjs().add('3','month')
			
		};
		return(
			<div className="adjustablecont">
				<div className="navigation" style={{width:300,marginRight:'16px'}}>
					<div className="packingtion">
						<Search 
							style={{marginTop: 16 }}
							placeholder="查询节点" onSearch={this.onChanges} />
					</div>
					<p className="numbers">全部节点（{number}）</p>
					<Spin spinning={this.state.treeLoading}  size="middle">
						<Tree
						  defaultExpandAll
						  onSelect={this.onSelect}
						  treeData={treeData}
						  onCheck={onCheck}
						  onExpand={onExpand}
						  expandedKeys={expandedKeys}
						  autoExpandParent={autoExpandParent}
						  // treeData={treeData2}
						  checkedKeys
						  checkStrictly
						  
						  
						/>
					</Spin>
				</div>
				<div className="adjustable" style={{marginLeft:315}}>
					<div className="adjustableheader" style={{paddingBottom:'0px'}}>
						<div className="chosedatebyn" style={{paddingBottom:'0px'}}>
							<RangePicker disabledDate={disabledDate} onChange={this.dateChange} />
							<Button type="primary" onClick={this.arround}>查询</Button>
							
						</div>
						
						
						
					</div>
					<div style={{padding:16}} ref={this.selectRef} >
						
						<Table dataSource={dataSource} 
							columns={columns}
							// scroll={{ y: 500 }}
							pagination={
								{
									total: this.state.total,//数据的总条数
									defaultCurrent: 1,//默认当前的页数
									defaultPageSize: 10,//默认每页的条数
									showSizeChanger:false,
									onChange: this.handlePagination,
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
							loading={loading}
						/>
					</div>

				</div>
			</div>
		)
	}
	
}

export default Ad_accurate
// <Button style={{float:'right'}} type="primary" >最新预测</Button>
// <Button type="primary"  style={{float:'right'}} onClick={this.arround}>查询</Button>