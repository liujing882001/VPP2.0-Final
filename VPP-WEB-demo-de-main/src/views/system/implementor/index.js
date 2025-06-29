import React from 'react'
import { Table, Input, Select, Button, Tree, Transfer, Modal, Form, Upload, message } from 'antd';
// import basePath from '../../axios/common';
import axios from 'axios';
import Edit from './Edit';
import Sys from './sys';
// import './style.css'
const { Search } = Input;
const { Option } = Select;
const { TreeNode } = Tree;
const { confirm } = Modal;

let basePath = 'http://52.81.223.42:8099/langfang/'



// 类型 （1:设备名称 2：楼宇 3：系统 4：子系统） 
const list = [{
  index: '',
  name: '默认'
},{
  index: 1,
  name: '设备名称'
}, {
  index: 2,
  name: '楼宇'
}, {
  index: 3,
  name: '系统'
}, {
  index: 4,
  name: '子系统'
}]

const isChecked = (selectedKeys, eventKey) => {
  return selectedKeys.indexOf(eventKey) !== -1;
};

const generateTree = (treeNodes = [], checkedKeys = []) => {
  return treeNodes.map(({ children, ...props }) => {
    return <TreeNode {...props} disabled={checkedKeys.includes(props.key) || typeof props.key === 'number' || checkedKeys.length > 0} checkable={typeof props.key !== 'number'} key={props.key}>
      {generateTree(children, checkedKeys)}
    </TreeNode>
  });
};

// const TreeTransfer = ({ dataSource, targetKeys, ...restProps }) => {
//   const transferDataSource = [];
//   function flatten(list = []) {
//     list.forEach(item => {
//       transferDataSource.push(item);
//       flatten(item.children);
//     });
//   }
//   flatten(dataSource);
//   console.log(123, restProps)
//   return (
//     <Transfer
//       {...restProps}
//       targetKeys={targetKeys}
//       dataSource={transferDataSource}
//       className="tree-transfer"
//       render={item => item.title}
//       showSelectAll={false}
//     >
//       {({ direction, onItemSelect, selectedKeys }) => {
//         if (direction === 'left') {
//           const checkedKeys = [...selectedKeys, ...targetKeys];
//           return (
//             <Tree
//               blockNode
//               checkable
//               checkStrictly
//               defaultExpandAll
//               checkedKeys={checkedKeys}
//               onCheck={(
//                 _,
//                 {
//                   node: {
//                     props: { eventKey },
//                   },
//                 },
//               ) => {

//                 onItemSelect(eventKey, !isChecked(checkedKeys, eventKey));
//               }}
//               onSelect={(
//                 _,
//                 {
//                   node: {
//                     props: { eventKey },
//                   },
//                 },
//               ) => {
//                 onItemSelect(eventKey, !isChecked(checkedKeys, eventKey));
//               }}
//             >
//               {generateTree(dataSource, targetKeys)}
//             </Tree>
//           );
//         }
//       }}
//     </Transfer>
//   );
// };

class Implementor extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
		visible: false,
		editvisible: false,
		sysvisible: false,
		id: '',
		pageIndex: 1,
		pageCount: 0,
		id: '',
		searchValue: '',
		operType: '',
		listData: [],
		targetKeys: [],
		treeData: [],
		sysList: [],
		selectArr: [],
		imvisible:false,
		modelarr:[],
		building:[],
		towerId:'',
		floorlist:[],
		space:[],
		system:[],
		childsystem:[],
		systemCode:'',
		equipment:[],
		editid:'',
		editdeviceFullName:'',
		edittowerId:'',
		editfloorId:'',
		editareaId:'',
		editsystemCode:'',
		editsubSystemCode:'',
		editremark:'',
		deviceFullCode:'',	//设备全码
		deviceFullName:'',	//设备名称
		towerId:'',		//楼宇编码
		floorId:'',		//楼层编码
		areaId:'',	//空间编码
		systemCode:'',	//系统编码
		subSystemCode:''	//子系统编码
		
		
    };
  }
  componentDidMount() {
    this.getDeviceList()
	this.getmodelList()
    // this.getAllTowerList()
    // this.getSysList()
	
  }
  search = (e) => {
    this.setState({
      searchValue: e,
      pageIndex: 1
    }, () => this.getDeviceList())
  }
  getSysList = () => {
    axios.get(basePath + 'sysinfo/getSysList').then(response => {
      let arr = response.data.data.map(i => {
        i.title = i.systemName
        i.key = i.systemCode

        return i
      })
      this.setState({
        sysList: arr
      })
    })
  }
  getAllTowerList = () => {
    axios.get(basePath + 'towerInfo/getAllTowerList').then(response => {
      let arr = response.data.data.map(i => {
        i.title = i.towerName
        i.key = i.towerId
        console.log(i.floorList)
        i.children = i.floorList && i.floorList.map((k, index) => {
          k.title = k.floorName
          k.key = `${i.towerId}-${k.floorId}`
          return k
        }) || []
        return i
      })
	  // console.log(arr)
      // this.setState({
      //   treeData: arr
      // })
    })
  }
  getDeviceList = () => {
    axios.post(basePath + 'eqLeder/getDeviceList', {
      pageIndex: this.state.pageIndex,
      pageSize: 10,
      operType: this.state.operType,
      queryContent: this.state.searchValue
    }).then(response => {

      this.setState({
        visible: false,
        editvisible: false,
        sysvisible: false,
        listData: response.data.data.data,
        pageCount: response.data.data.pageCount
      })
    })
  }
  createSub = (e) => {

  }
  del = (e) => {
	  console.log(e)
	  confirm({
	      title: '确定要删除吗?',
	      okText: '确定',
	      cancelText: '取消',
		  cancelButtonProps: {className:'newbutton',style: { background: 'none'} }, 
	      onOk() {
				axios.post(basePath + 'eqLeder/delDevice',{
					id:e.id
				}).then(response => {
				  if (response.data.return_status === 'SUCCESS') {
				    Modal.success({
				      title: '提示',
				      content: "删除成功！",
				    });
				    this.getDeviceList()
				  } else {
				    Modal.error({
				      title: '提示',
				      content: response.data.data,
				    });
				  }
				})
	        
	      },
	      onCancel() {
	  		console.log('Cancel');
	      },
	    });
    

  }
  edit = (e) => {
	  console.log(e)
    this.setState({
		editvisible: true,
		id: e.id,
		deviceFullCode:e.deviceFullCode,
		deviceFullName:e.deviceFullName,
		towerId:e.towerId,
		towerName:e.towerName,
		floorId:e.floorId,
		floor:e.floor,
		areaId:e.areaId,
		systemCode:e.systemCode,
		systemName:e.systemName,
		subSystemCode:e.subSystemCode,
		subSystem:e.subSystem,
		deviceBrand:e.deviceBrand,
		deviceType:e.deviceType,
		remark:e.remark
	  
    })
  }
  //绑定楼宇
  handleSubmit = () => {
    const { selectArr, targetKeys } = this.state
    try {
      if (targetKeys.length === 1) {
        let arr = targetKeys[0].split('-')
        let str = []
		console.log(selectArr)
        selectArr.forEach(i => str.push(i.deviceFullCode))
        axios.post(basePath + 'eqLeder/editDeviceTower', {
          deviceFullCode: str.toString(),
          towerId: arr[0],
          floorId: arr[1],
        }).then(response => {
          if (response.data.return_status === 'SUCCESS') {
            Modal.success({
              title: '提示',
              content: '绑定成功！'
            });
            this.getDeviceList()
            this.onCancel()
          } else {
            Modal.error({
              title: '提示',
              content: response.data.data,
            });
          }
        })
      } else {
        Modal.error({
          title: '提示',
          content: '只能勾选一项',
        });
      }
    } catch (e) {
      console.error(e)
    }
  }
  changePage = ({ current }) => {
    this.setState({ pageIndex: current }, () => this.getDeviceList())
  }
  onCancel = () => {
    this.setState({
      visible: false,
      sysvisible: false,
      editvisible: false,
    })
  }
  handleBindBuild = () => {
    this.setState({
      visible: true
    })
  }
  handleBindsys = () => {
    this.setState({
      sysvisible: true
    })
  }
  onChange = (targetKeys, direction, moveKeys) => {
    this.setState({ targetKeys });
  };
  //获取楼宇信息
	getmodelList(){
		axios.get(basePath +'towerInfo/getAllTowerList').then(response =>{
			if(response.data.returnMsg =='请求成功'){
				let buildings = []
				let list = response.data.data
				console.log(list)
				list.map(data =>{
					console.log(data)
					buildings.push({
						towerName:data.towerName,
						towerId:data.towerId
					})
				})
				console.log(buildings)
				this.setState({
					building:buildings
				})
			}
			
			
		})
	}
  
	//绑定设备信息
	handleimplementor =() =>{
		// const { setFieldsValue } = this.props.form;
		let {selectArr} = this.state
		if(this.state.selectArr.length =='0'){
			message.warning("请选择一条")
		}else{
			this.setState({
				imvisible: true
			},() =>{
				console.log(this.state.selectArr[0])
				this.refs.myForm.setFieldsValue({
					deviceFullName:selectArr[0].deviceFullName,
					towerName:selectArr[0].towerName,
					floor:selectArr[0].floor,
					// floorName:data.floorName,
					// systemName:data.systemName,
					// subSystem:data.subSystem,
					// deviceModel:data.deviceModel,//设备型号
					// deviceBrand:data.deviceBrand,
					// remark:data.remark,
				})
			})
			
			this.getmodelList()
			this.getsystemlist()
			
			// console.log(newfloor)
		}
		
	}
	onimpCancel =() =>{
		this.setState({
		  imvisible: false,
		})
	}
	//绑定设备信息
	handleimpSubmit =() =>{
		
		axios.post(basePath+'eqLeder/editDevice',{
			deviceFullCode:this.state.deviceFullCode,	
			towerId:this.state.towerId,
			floorId:this.state.floorId,
			areaId:this.props.areaId,
			systemCode:this.state.systemCode,
			subSystemCode:this.state.subSystemCode
			
		}).then(respone =>{
			console.log(respone)
			if(respone.data.returnMsg =='请求成功'){
				message.success('绑定成功')
				this.getDeviceList()
			}
		})
		this.setState({
		  imvisible: false,
		})
		
	}
	onFinish =()=>{
		
	}
	onFinishFailed =() =>{
		
	}
	//系统
	getsystemlist(){
		axios.get(basePath +'sysinfo/getSysList').then(response =>{
			console.log(response)
			if(response.data.returnMsg =='请求成功'){
				let system = []
				let list = response.data.data
				console.log(list)
				list.map(data =>{
					system.push({
						systemName:data.systemName,
						systemCode:data.systemCode
					})
				})
				this.setState({
					system:system
				})
			}
		})
	}
	//选择系统
	onsystemChange =(e) =>{
		console.log(e)
		this.setState({
			systemCode:e
		},() =>{
			axios.post(basePath +'sysinfo/getSubSysList',{
				systemCode: this.state.systemCode
			}).then(response =>{
				console.log(response)
				if(response.data.returnMsg =='请求成功'){
					let child = []
					let list = response.data.data
					console.log(list)
					if(list.length =='0'){
						
					}else{
						list.map(data =>{
							child.push({
								subSystemCode:data.subSystemCode,
								subSystemName:data.subSystemName
							})
						})
						console.log(child)
						this.setState({
							childsystem:child
						})
					}
					
					
				}
				
				
			})
		})
	}
	//选择楼宇
	onbuildChange =(e) =>{
		console.log(e)
		this.setState({
			towerId:e
		},() =>{
				console.log(this.state.towerId)
				axios.get(basePath +'towerInfo/getTowerAllFloorList',{
					params: {  
					   towerId: this.state.towerId,
					}
				}).then(response =>{
					console.log(response)
					if(response.data.returnMsg =='请求成功'){
						let floors = []
						let list = response.data.data
						console.log(list)
						if(list.length =='0'){
							
						}else{
							list.map(data =>{
								floors.push({
									floorId:data.floorId,
									floorName:data.floorName
								})
							})
							console.log(floors)
							this.setState({
								floorlist:floors
							})
						}
						
						
					}
					
					
				})
			
		})
	}
	//选择楼层
	onGenderChange =(arg,e) =>{
		console.log(e)
		this.setState({
			floorId:e
		})
	}
	//选择子系统
	subsystemChange =e =>{
		console.log(e)
		this.setState({
			subSystemCode:e
		})
	}
  render() {
    let _this = this
    const { targetKeys, selectArr } = this.state;
	// const { getFieldDecorator } = this.props.form;
    const { treeData, } = this.state
    const columns = [
      { title: '设备名称', dataIndex: 'deviceFullName', key: 'deviceFullName', },
      { title: '楼宇', dataIndex: 'towerName', key: 'towerName', },
      { title: '楼层', dataIndex: 'floor', key: 'floor', },
      { title: '系统', dataIndex: 'systemName', key: 'systemName', },
      { title: '子系统', dataIndex: 'subSystem', key: 'subSystem', },
      { title: '设备全码', dataIndex: 'deviceFullCode', key: 'deviceFullCode', },
      { title: '设备型号', dataIndex: 'deviceModel', key: 'deviceModel', },
      { title: '设备品牌', dataIndex: 'deviceBrand', key: 'deviceBrand', },
      { title: '备注', dataIndex: 'remark', key: 'remark', },
      {
        title: '操作', key: 'operation',width:'160px', render: (text, row, index) => <div>
          <a onClick={() => this.edit(row)}>编辑</a>
          <a style={{ marginLeft: 16 }} onClick={() => this.del(row)}>删除</a>
        </div>
      },
    ];

    const selectBefore = (
      <Select className='eq-select' defaultValue={this.state.operType}  onChange={(e) => {
        this.setState({
          operType: e,
          selectArr: []
        })
      }}>
        {
          list.map(i => <Option value={i.index} key={i.index}>{i.name}</Option>)
        }

      </Select>
    );
    const rowSelection = {
      hideDefaultSelections: true,
      onChange: (selectedRowKeys, selectedRows) => {
        this.setState({
          selectArr: selectedRows
        },() =>{
			console.log(this.state.selectArr)
		})
      },
    };
    const props = {
      name: 'file',
      showUploadList: false,
      action: basePath + 'eqLeder/importDevice',
      headers: {
      },
      onChange(info) {
		  console.log(info)
        if (info.file.status !== 'uploading') {
			console.log(info.file, info.fileList);
        }
        if (info.file.status === 'done') {
			message.success('上传成功')
			_this.getDeviceList()
        }
      },
    };
	// <Button disabled={selectArr.length === 0}  shape="round" style={{ marginLeft: 20 }} onClick={() => this.handleBindBuild()}>
	//   绑定楼宇
	// </Button>
	// <Button  disabled={selectArr.length === 0} shape="round" style={{ marginLeft: 20 }} onClick={() => this.handleBindsys()}>
	//   绑定系统
	// </Button>
    return (<div className='card' style={{padding:'12px 24px 24px'}}>
      <div className='hrader' style={{height:'50px'}}>
		<Button style={{float:'left',margin:'0px'}} onClick={() => this.handleimplementor()}>绑定设备信息</Button>
		<Modal
		  className='buildModal'
		  title={`绑定设备信息`}
		  visible={this.state.imvisible}
		  okText='保存'
		  onOk={this.handleimpSubmit}
		  onCancel={this.onimpCancel}
		  cancelText='取消'
		  // disabled={this.state.selectArr.length === 0}
		>
		<Form
		      name="basic"
		      labelCol={{ span: 8 }}
		      wrapperCol={{ span: 16 }}
		      initialValues={{ remember: true }}
		      onFinish={this.onFinish}
		      onFinishFailed={this.onFinishFailed}
		      autoComplete="off"
			  ref="myForm"
		    >
		      <Form.Item
		        label="设备名称"
		        name="deviceFullName"
		        rules={[{ required: true, message: 'Please input your username!' }]}
		      >
					<Input disabled />
		      </Form.Item>
		
				<Form.Item
					label="楼宇"
					name="towerName"
					rules={[{ required: true, message: 'Please input your username!' }]}
				>
					<Select
						placeholder="楼宇"
						onChange={this.onbuildChange}
						allowClear
					>
					{
						this.state.building.map(i =>{
							// console.log(i)
							return <Option key={i.towerId} value={i.towerId}>{i.towerName}</Option>
						})
					}
						
						
					</Select>
		        
		      </Form.Item>
			  <Form.Item
			    label="楼层"
				name="floor"
			  >
				<Select
					placeholder="楼层"
					onChange={this.onGenderChange}
					allowClear
					// disabled={this.state.floor.length == 0}
				>
				 {
					 
					 this.state.floorlist.map(i =>{
						 return <Option key={i.floorId} value={i.floorId}>{i.floorName}</Option>
					 })
				 }
				</Select>
			    
			  </Form.Item>
			 <Form.Item
			        label="空间"
			        name="username"
			      >
			      <Select
			       placeholder="空间"
			       onChange={this.onGenderChange}
			       allowClear
			       // disabled={this.state.space.length == 0}
			       
			      >
			        {
			         this.state.space.map(i =>{
			          return <Option key={i.areaId} value={i.areaId}>{i.areaName}</Option>
			         })
			        }
			      </Select>
			        
			      </Form.Item>
			  <Form.Item
			    label="系统"
			    name="username"
			    rules={[{ required: true, message: 'Please input your username!' }]}
			  >
			  <Select
			  	placeholder="系统"
			  	onChange={this.onsystemChange}
			  	
			  	
			  >
			    {
			  	  this.state.system.map(i =>{
			  		  return <Option key={i.systemCode} value={i.systemCode}>{i.systemName}</Option>
			  	  })
			    }
			  </Select>
			    
			  </Form.Item>
			   <Form.Item
			    label="子系统"
			    name="username"
			    rules={[{ required: true, message: 'Please input your username!' }]}
				
			  >
			  <Select
			  	placeholder="子系统"
			  	onChange={this.subsystemChange}
			  	allowClear
			  	// disabled={this.state.childsystem.length == 0}
			  	
			  >
			    {
			  	  this.state.childsystem.map(i =>{
			  		  return <Option key={i.subSystemCode} value={i.subSystemCode}>{i.subSystemName}</Option>
			  	  })
			    }
			  </Select>
			   
			  </Form.Item>
		</Form>
		</Modal>
       
        
        <Search
          addonBefore={selectBefore}
          className='search'
          placeholder="请输入"
          onSearch={value => this.search(value)}
          style={{ width: 300 ,float:'right'}}
        />
		<Upload {...props} className='search' style={{float:'right'}}>
			<Button shape="round" style={{ margin: 0,float:'right' }} >
				导入设备
			</Button>
		</Upload>
      </div>
      <Table
        columns={columns}
        rowKey={record =>{
        	return record.id
        }}
        rowSelection={rowSelection}
        dataSource={this.state.listData}
        onChange={this.changePage}
        pagination={{ total: this.state.pageCount }}
      />
      <Modal
        className='buildModal'
        title={`绑定楼宇`}
        visible={this.state.visible}
        okText='保存'
        onOk={this.handleSubmit}
        onCancel={this.onCancel}
        cancelText='取消'
      >
		</Modal>
			<Sys sysList={this.state.sysList} 
			getDeviceList={this.getDeviceList} 
			selectArr={selectArr} 
			sysvisible={this.state.sysvisible} />
			{
				this.state.editvisible && 
				<Edit 
					sysList={this.state.sysList} 
					getDeviceList={this.getDeviceList} 
					editvisible={this.state.editvisible} 
					deviceFullCode={this.state.deviceFullCode}
					deviceFullName={this.state.deviceFullName}
					towerId={this.state.towerId}
					floorId={this.state.floorId}
					areaId={this.state.areaId}
					systemCode={this.state.systemCode}
					systemName={this.state.systemName}
					subSystemCode={this.state.subSystemCode}
					subSystem={this.state.subSystem}
					deviceBrand={this.state.deviceBrand}
					deviceType={this.state.deviceType}
					remark={this.state.remark}
					floor={this.state.floor}
					building={this.state.building}
					towerName={this.state.towerName}
					 />
			}

    </div>)
  }
}
export default Implementor;