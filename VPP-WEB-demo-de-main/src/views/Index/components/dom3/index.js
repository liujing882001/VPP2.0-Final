import { Table } from 'antd'
export const Dom3 = (props) => {
    const { columns1,rowSelection1,totalDeclare,invitation,dataSource } = props
    return (
        <div className='submit-declare-wrap' id='submit-declare-wrap' key={new Date().getTime()}>
            <i className='robot-icon'/>
            <div className='submit-declare'>
            <div className='submit-declare-title'>好的，这是根据策略推荐的申报信息，你可以对“申报负荷(kW)”进行编辑，并提交申报</div>
                <div className='submit-declare-content'>
                    <div className='submit-declare-content-top'>
                        <div className='invite-obj'>
                            <span className='invite-obj-left'>邀约对象</span>
                            <span className='invite-obj-right'>{invitation}</span>
                        </div>
                        <div className='declare-load'>
                            <span className='declare-load-left'>申报负荷(kW)</span>
                            <span className='declare-load-right'>{Number(totalDeclare).toFixed(2)}</span>
                        </div>
                    </div>  
                    <div className='submit-declare-content-bottom'>
                        <Table
                            rowKey='drsId'
                            pagination={false}
                            rowSelection={{
                                type: 'checkbox',
                                ...rowSelection1,
                            }}
                            columns={columns1}
                            dataSource={dataSource}
                            className='submit-declare-table'
                        />
                    </div>           
                </div>
            </div>
        </div>
    )
}