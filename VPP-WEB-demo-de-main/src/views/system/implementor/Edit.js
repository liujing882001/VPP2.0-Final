import React from 'react'
import { Table, Input, Select, Button, Tree, Transfer, Modal, Form, Upload, message } from 'antd';
import axios from 'axios';
// import basePath from '../../axios/common';
const { Option } = Select;
const { TextArea } = Input;
let basePath = 'http://52.81.223.42:8099/langfang/'

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 4 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};
class Edit extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
		editvisible: false,
		item:{},
		sysSubList: [],
		targetKeys: [],
		floorList: [],
		towerList: [],
		dataSource: [],
		sysList:[],
		towerIds:'',
		floorIds:'',
		areaIds:'',
		systemCodes:'',
		subSystemCodes:'',
		deviceBrands:'',
		deviceTypes:'',
		deviceFullCodes:''
    }
  }

  componentDidMount() {
    this.getAllTowerList()
	this.getSysList()
	console.log(this.props.towerId)
    this.setState({
		editvisible: this.props.editvisible,
		deviceFullCodes:this.props.deviceFullCode,
		towerIds:this.props.towerId,
		floorIds:this.props.floorId,
		areaIds:this.props.areaId,
		systemCodes:this.props.systemCode,
		subSystemCodes:this.props.subSystemCode,
		deviceBrands:this.props.deviceBrand,
		deviceTypes:this.props.deviceType,
		remarks:this.state.remark
    })
	this.props.form.setFieldsValue({
		deviceFullCode:this.props.deviceFullCode,
		deviceFullName:this.props.deviceFullName,
		towerId:this.props.towerId,
		towerName:this.props.towerName,
		floorId:this.props.floorId,
		floor:this.props.floor,
		areaId:this.props.areaId,
		systemCode:this.props.systemCode,
		systemName:this.props.systemName,
		subSystemCode:this.props.subSystemCode,
		subSystem:this.props.subSystem,
		deviceBrand:this.props.deviceBrand,
		deviceType:this.props.deviceType,
		remark:this.props.remark
	});
  }
	//获取楼宇
	getAllTowerList = () => {
		axios.get(basePath + 'towerInfo/getAllTowerList').then(response => {
			this.setState({
				towerList: response.data.data
				
			}, () => {})
		})
	}
	//选择楼宇
	handleSelectChange = (value) => {
		console.log(value)
		let floor = this.state.towerList.find(i => i.towerId === value)
		console.log(floor.floorList)
		if(floor.floorList == null){
			
			this.props.form.setFieldsValue({
			    floor:''
			})
		}else{
			this.setState({
				floorList: floor.floorList || [],
				towerIds:value
			},() =>{
				console.log(this.state.floorList)
			})
		}
		
	}
	//选择楼层
	handfloor =(e) =>{
	  console.log(e)
	  this.setState({
		  floorIds:e
	  })
	}
	//获取系统
	getSysList(){
		axios.get(basePath + 'sysinfo/getSysList').then(response => {
			this.setState({
				sysList: response.data.data
				
			}, () => {})
		})
	}
	//选择系统
	handleSelectSysChange = (value)=>{
		console.log(value)
		this.setState({
			systemCodes:value
		})
		axios.post(basePath + 'sysinfo/getSubSysList', {
			systemCode: value,
		}).then(response => {
			this.setState({
				sysSubList: response.data.data || []
			})
		})
	}
	//选择子系统
	handlesysChange =(e) =>{
		this.setState({
			subSystemCodes:e
		})
	}
  //修改
	handleSubmit=()=>{
		const { item}=this.state
		this.props.form.validateFields((err, values) => {
			console.log(values)
			if (!err) {
				axios.post(basePath + 'eqLeder/editDeviceDetail', {
					deviceFullCode:this.state.deviceFullCodes,
					deviceFullName:values.deviceFullName,
					towerId:this.state.towerIds,
					floorId:this.state.floorIds,
					areaId:this.state.areaIds,
					systemCode:this.state.systemCodes,
					subSystemCode:this.state.subSystemCodes,
					deviceBrand:this.state.deviceBrands,
					deviceType:this.state.deviceTypes,
					remark:values.remark
				}).then(response => {
				  if (response.data.returnStatus === 'SUCCESS') {
					Modal.success({
					  title: '提示',
					  content: '修改成功！'
					});
					this.onCancel()
				  } else {
					Modal.error({
					  title: '提示',
					  content: response.data.data,
					});
				  }
				})
			}
		});
	}
  onCancel = () => {
    this.props.getDeviceList()
    this.setState({
      editvisible: false,
    })
  }
  render() {
    // const { getFieldDecorator } = this.props.form;
    // const { sysList } = this.props;
    const { towerList, floorList, sysSubList ,building ,sysList  } = this.state
    return <Modal
      className='buildModal'
      title='编辑'
      visible={this.state.editvisible}
      okText='保存'
      onOk={this.handleSubmit}
      onCancel={this.onCancel}
      cancelText='取消'
    >
      <Form {...formItemLayout} style={{ margin: '30px 0' }}>
        <Form.Item label={'设备名称'}>
          {getFieldDecorator('deviceFullName', {
          
          })(<Input placeholder='请输入' />)}
        </Form.Item>
        <Form.Item label={'楼宇'}>
          {getFieldDecorator('towerName', {
         
          })(<Select onChange={this.handleSelectChange}>
            {
              towerList.map(i => <Option value={i.towerId} key={i.towerId}>{i.towerName}</Option>)
            }
          </Select>)}
        </Form.Item>
        <Form.Item label={'楼层'}>
			{getFieldDecorator('floor', {
				initialValue:this.props.floor,
			})(<Select onChange={this.handfloor} >
            {
              floorList.map(i => <Option value={i.floorId} key={i.floorId}>{i.floorName}</Option>)
            }
          </Select>)}
        </Form.Item>
        <Form.Item label={'系统'}>
          {getFieldDecorator('systemName', {
         
          })(<Select onChange={this.handleSelectSysChange}>
            {
              
              sysList.map(i => <Option value={i.systemCode} key={i.systemCode}>{i.systemName}</Option>)
            }
          </Select>)}
        </Form.Item>
        <Form.Item label={'子系统'}>
          {getFieldDecorator('subSystem', {
          
          })(<Select onChange={this.handlesysChange}>
            {
              sysSubList.map(i => <Option value={i.subSystemCode} key={i.subSystemCode}>{i.subSystemName}</Option>)
            }
          </Select>)}
        </Form.Item>
        <Form.Item label={'备注'}>
          {getFieldDecorator('remark', {
       
          })(<TextArea placeholder='请输入' />)}
        </Form.Item>
      </Form>
    </Modal>
  }
}

export default Edit;
