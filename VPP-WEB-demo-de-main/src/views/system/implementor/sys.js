import React from 'react'
import { Table, Input, Select, Button, Tree, Transfer, Modal, Form, Upload, message } from 'antd';
import axios from 'axios';
// import basePath from '../../axios/common';
let basePath = 'http://52.81.223.42:8099/langfang/'

const { TreeNode } = Tree;

const isChecked = (selectedKeys, eventKey) => {
  return selectedKeys.indexOf(eventKey) !== -1;
};

const generateTree = (treeNodes = [], checkedKeys = []) => {
  return treeNodes.map(({ children, ...props }) => {
    return <TreeNode {...props} disabled={checkedKeys.includes(props.key) || !props.key.includes('-') || checkedKeys.length > 0} checkable={props.key.includes('-') } key={props.key} dataRef={props}>
      {generateTree(children, checkedKeys)}
    </TreeNode>
  });
};

const TreeTransfer = ({ dataSource, targetKeys, setData, ...restProps }) => {
  const transferDataSource = [];
  function flatten(list = []) {
    list.forEach(item => {
      transferDataSource.push(item);
      flatten(item.children);
    });
  }
  flatten(dataSource);
  const onLoadData = (treeNode) => new Promise((resolve, reject) => {
    const { props } = treeNode
    axios.post(basePath + 'sysinfo/getSubSysList', {
      systemCode: props.systemCode,
    }).then(response => {
      let arr = response.data.data.map((k, index) => {
        k.title = k.subSystemName
        k.key = `${treeNode.props.eventKey}-${k.subSystemCode}`
        k.isLeaf = true
        return k
      }) || []
      treeNode.props.dataRef.children = arr
      setData(treeNode.props.dataRef)
      resolve();
    })
  })
  return (
    <Transfer
      {...restProps}
      targetKeys={targetKeys}
      dataSource={transferDataSource}
      className="tree-transfer"
      render={item => item.title}
      showSelectAll={false}
    >
      {({ direction, onItemSelect, selectedKeys }) => {
        if (direction === 'left') {
          const checkedKeys = [...selectedKeys, ...targetKeys];
          return (
            <Tree
              blockNode
              checkable
              checkStrictly
              checkedKeys={checkedKeys}
              loadData={onLoadData}
              onCheck={(
                _,
                {
                  node: {
                    props: { eventKey },
                  },
                },
              ) => {

                onItemSelect(eventKey, !isChecked(checkedKeys, eventKey));
              }}
              onSelect={(
                _,
                {
                  node: {
                    props: { eventKey },
                  },
                },
              ) => {
                onItemSelect(eventKey, !isChecked(checkedKeys, eventKey));
              }}
            >
              {generateTree(dataSource, targetKeys)}
            </Tree>
          );
        }
      }}
    </Transfer>
  );
};
class Sys extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      sysvisible: false,
      targetKeys: [],
      dataSource: []
    }
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      dataSource: nextProps.sysList,
      sysvisible: nextProps.sysvisible,
    })
  }
  handleSubmit = () => { 
    const {  targetKeys, dataSource } = this.state
    const { selectArr} = this.props
    try {
      if (targetKeys.length === 1) {
        let arr = targetKeys[0].split('-')
        let str = []
        selectArr.forEach(i => str.push(i.deviceFullCode))
        axios.post(basePath + 'eqLeder/editDeviceSystem', {
          deviceFullCode: str.toString(),
          systemCode: arr[0],
          subSystemCode: arr[1],
        }).then(response => {
          if (response.data.return_status === 'SUCCESS') {
            Modal.success({
              title: '提示',
              content: '绑定成功！'
            });
            this.props.getDeviceList()
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
  onCancel = () => {
    this.setState({
      sysvisible: false,
      targetKeys: [],
    })
  }
  onChange = (targetKeys, direction, moveKeys) => {
    this.setState({ targetKeys });
  };
  setData = (item) => {
    const { sysList } = this.props
    sysList.map(i => {
      if (i.key === item.key) {
        i.children = item.children
      }
    })
    this.setState({
      dataSource: sysList
    })
  }
  render() {
    const { targetKeys, dataSource } = this.state
    return <Modal
      className='buildModal'
      title={`绑定系统`}
      visible={this.state.sysvisible}
      okText='保存'
      onOk={this.handleSubmit}
      onCancel={this.onCancel}
      cancelText='取消'
    >
      <TreeTransfer style={{ margin: '30px 0' }} setData={this.setData} dataSource={dataSource} targetKeys={targetKeys}
        onChange={this.onChange} />
    </Modal>
  }
}

export default Sys