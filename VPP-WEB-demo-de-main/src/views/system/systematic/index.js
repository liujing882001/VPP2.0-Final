import React from 'react'
import { Table, Input, Select, Button, Icon, Modal, Form ,confirm} from 'antd';
// import basePath from '../../axios/common';
import './index.css'

import axios from 'axios';
import {
  PlusOutlined,
  AudioOutlined ,
  FormOutlined,
  DeleteOutlined,
  FileSearchOutlined,
  ExclamationCircleOutlined,
  MinusCircleOutlined, 
  PlusCircleOutlined,
   UploadOutlined, InboxOutlined
} from '@ant-design/icons';

const formItemLayoutWithOutLabel = {
  wrapperCol: {
    xs: { span: 28, offset: 0 },
    sm: { span: 26, offset: 0 },
  },
};
// import './style.css'
const { Search } = Input;
const { Option } = Select;
const formItemLayout = {
  labelCol: {
    xs: { span: 30 },
    sm: { span: 4 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};
let basePath = 'http://52.81.223.42:8099/langfang/'
class System extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      visible: false,
      pageIndex: 1,
      pageCount: 0,
      id: '',
      systemId: '',
      systemCode: '',
      subSystemCode: '',
      searchValue: '',
      type: '',//'':全部 1 :系统 2子系统
      sys: 1, //1 :系统 2子系统
      edit: 0,  //1 编辑 0 新增
      listData: [],
	  systemName:'',
	  systematic:'添加系统'
    };
  }
  componentDidMount() {
    this.getSysAndSubSysList()
  }
  search = (e) => {
    this.setState({
      searchValue: e,
      pageIndex: 1
    }, () => this.getSysAndSubSysList())
  }
  getSysAndSubSysList = () => {
    axios.post(basePath + 'sysinfo/getSysAndSubSysList', {
      pageIndex: this.state.pageIndex,
      pageSize: 10,
      type: this.state.type,
      queryContent: this.state.searchValue
    }).then(response => {
      let arr = response.data.data.data.map((i, index) => {
        i.index = index
        if (i.subSysEntityList && i.subSysEntityList.length > 0) {
          i.children = i.subSysEntityList.map((k, index2) => {
            k.systemName = k.subSystemName
            k.systemCode = i.systemCode
            k.systemId = i.systemId
            k.index = `${index}-${index2}`
            return k
          })
        }
        return i
      })
      this.setState({
        listData: arr,
        pageCount: response.data.data.pageCount
      })
    })
  }
  createSub = (e) => {
	  console.log(e)
    this.setState({
      id: e.systemId + '',
      systemId: e.systemId + '',
      systemCode: e.systemCode+'',
      subSystemCode: e.subSystemCode+'',
      sys: 2,
      visible: true,
	  systemName:e.systemName,
	  systematic:'新建子系统'
    })
  }
  //删除
  del = (e) => {
	  let _this = this
	 Modal.confirm({
	      title: '确定要删除吗?',
	      okText: '确定',
	      okType: '',
	      cancelText: '取消',
	      onOk() {
	        console.log('OK');
			axios.post(basePath + 'sysinfo/addOrEditOrDelSysInfo', {
			  sysType: e.subSystemCode ? 2 : 1,
			  id: e.subSystemId || e.systemId,
			  operType: 2,
			  systemCode: e.systemCode,
			  subSystemCode: e.subSystemCode,
			  //[e.subSystemCode ? 'subSystemCode' : "systemCode"]: e.subSystemCode || e.systemCode,
			}).then(response => {
			  if (response.data.returnStatus === 'SUCCESS') {
			    Modal.success({
			      title: '提示',
			      content: "删除成功！",
			    });
				_this.onCancel()
			    _this.getSysAndSubSysList()
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
    this.setState({
      id: (e.subSystemId || e.systemId) + '',
      systemId: (e.subSystemId || e.systemId) + '',
      systemCode: e.systemCode,
      subSystemCode: e.subSystemCode,
      sys: e.subSystemId ? 2 : 1,
      visible: true,
      edit: 1,  //1 编辑 0 新增
	  
    })
	console.log()
    this.refs.myForm.setFieldsValue({
		value: e.systemName
    })
  }
  //新增
  handleSubmit = () => {
    const { sys, edit } = this.state
	console.log(this.refs.myForm.validateFields())
	this.refs.myForm.validateFields().then(values => {
		console.log(values)
		axios.post(basePath + 'sysinfo/addOrEditOrDelSysInfo', {
			sysType: sys,
			id: this.state.id,
			operType: edit,
			systemCode: this.state.systemCode,
			subSystemCode: this.state.subSystemCode,
			[sys === 1 ? 'systemName' : "subSystemName"]: values.value,
		}).then(response => {
			console.log(response)
			if (response.data.returnStatus === 'SUCCESS') {
				Modal.success({
				  title: '提示',
				  content: edit === 0 ? '新建成功！' : "修改成功！",
				});
				this.getSysAndSubSysList()
				this.onCancel()
			} else {
				Modal.error({
				  title: '提示',
				  content: response.data.data,
				});
			}
		})
    
	}).catch(errorInfo => {
		console.log(errorInfo)
	});


  }
  changePage = ({ current }) => {
    this.setState({ pageIndex: current }, () => this.getSysAndSubSysList())
  }
  onCancel = () => {
    this.setState({
      visible: false,
      sys: 1, //1 :系统 2子系统
      edit: 0,  //1 编辑 0 新增
      systemId: ''
    })
    // this.props.form.resetFields()
  }
  render() {
    // const { getFieldDecorator } = this.props.form;

    const { sys, edit ,systematic} = this.state
    const columns = [
      { title: '结构', dataIndex: 'systemName', key: 'systemId', },
      {
        title: '操作', key: 'operation', render: (text, row, index) => <div>
          <a onClick={() => this.edit(row)}>编辑</a>
          <a style={{ marginLeft: 15 }} onClick={() => this.del(row)}>删除</a>
          {
            !row.subSystemName && <a style={{ marginLeft: 15 }} onClick={() => this.createSub(row)}>新建子系统</a>
          }
        </div>
      },
    ];

    const selectBefore = (
      <Select defaultValue='' style={{ width: 110 }} onChange={(e) => {
        this.setState({
          type: e
        })
      }}>
        <Option value={''}>全部</Option>
        <Option value={1}>系统</Option>
        <Option value={2}>子系统</Option>
      </Select>
    );
    return (<div className='card' style={{padding:'12px 24px 24px'}}>
      <div className='hrader' style={{height:'50px'}}>
        <span style={{ fontWeight: 600,color:'#FFFFFF',float:'left' }}>系统/子系统管理</span>

        <Button shape="round" style={{ margin: 0,float:'left' }} onClick={() => this.setState({ visible: true,systematic:'新建子系统' })}>
          新建系统
        </Button>
        <Search
          addonBefore={selectBefore}
          className='search'
          placeholder="请输入"
          onSearch={value => this.search(value)}
          style={{ width: 300,float:'right' }}
        />
      </div>
      <Table
        columns={columns}
        rowKey='index'
        dataSource={this.state.listData}
        onChange={this.changePage}
        pagination={{ total: this.state.pageCount }}
      />
      <Modal
        className='buildModal'
        title={!edit ? `新建${sys === 1 ? '' : '子'}系统` : `编辑${sys === 1 ? '' : '子'}系统`}
        visible={this.state.visible}
        okText='保存'
        onOk={this.handleSubmit}
        onCancel={this.onCancel}
        cancelText='取消'
      >
		<Form.Item>
		    <p style={{color:'#FFF'}} className={sys === 1 ? 'none' : 'block'}><span>系统</span>：{this.state.systemName}</p>
		</Form.Item>
		
        <Form 
			ref="myForm"
			{...formItemLayout} name="newforms" style={{ margin: '30px 0' }}>
			<Form.Item name="value" label={sys === 1 ? '系统' : '子系统'}>
				<Input placeholder='请输入名称' />
				
			</Form.Item>
			<Form.List
				name="names"
			>
				{(fields, { add, remove }, { errors }) => (
				  <>
					{fields.map((field, index) => (
					  <Form.Item
						{... formItemLayoutWithOutLabel}
						label={index === 0 ? '' : ''}
						required={false}
						key={field.key}
					  >
						<Form.Item
						  {...field}
						  name="sysname"
						  validateTrigger={['onChange', 'onBlur']}
						  rules={[
							{
							  required: true,
							  whitespace: true,
							  message: "请输入名称",
							},
						  ]}
						  noStyle
						>
							<Input placeholder="请输入名称" />
						</Form.Item>
						  <MinusCircleOutlined
							className="dynamic-delete-button"
							onClick={() => remove(field.name)}
						  />
					  </Form.Item>
					))}
					<Form.Item>
						<Button
							onClick={() => add()}
							style={{ width: '60%' }}
							icon={<PlusOutlined />}
							className="btns"
						>
							{systematic}
						</Button>
					  
						<Form.ErrorList errors={errors} />
					</Form.Item>
				  </>
				)}
			</Form.List>
        </Form>
		
		
      </Modal>
    </div>)
  }
}
export default System;