import {renderResponseStatus} from '../../utils'
import { Button, Select,Table } from 'antd';
import Chart from '../chart'

export const Dom2 = (props) => {
    const { taskInfo,notShowConfirmBtn,selectedIds,enSure,rowSelection,columns,strategyList,currentNodeId,allNodeList,setNodeId,enAllSure,refreshChart } = props

    return (
        <div className='edit-strategy-wrap' id='edit-strategy-wrap'>
            <i className='robot-icon'/>
            <div className='edit-strategy'>
                <div className='edit-strategy-header'>
                    <span>好的，策略已生成，你可以对“策略内容”进行编辑，并确认策略</span>
                </div>
                <div className='edit-strategy-content' key={new Date().getTime()}>
                    <Button className='batch-confirm-btn' onClick={() => enAllSure(selectedIds.current)}>批量确认</Button>
                    <Table
                        rowKey='id'
                        className='edit-strategy-table'
                        pagination={false}
                        rowSelection={{
                            type: 'checkbox',
                            ...rowSelection,
                        }}
                        columns={columns}
                        dataSource={strategyList}
                    />
                    <div className='chart-query-title'>
                        <span>能量块趋势分析</span>
                        <div className='chart-query-wrap'>
                            <Select
								popupClassName="energyNodestyle"
								style={{minWidth:126,maxWidth:278,height:26}}
                                value={currentNodeId.current}
                                options={allNodeList}
                                onChange={(value) => {
                                    currentNodeId.current = value
                                    refreshChart()
                                }}
                                style={{ width: 154,height:26 }}
                                size={'small'}  
                                />
                        </div>
                    </div>
                    <Chart timerList={props.timerList} baselineList={props.baselineList} topList={props.topList} bottomList={props.bottomList} predictionList={props.predictionList} />
                </div>
            </div>
        </div>  
    )
}