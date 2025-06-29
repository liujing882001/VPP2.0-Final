import { useEffect, useState } from "react"
import http from '../../../../../server/server';
import classNames from 'classnames'
import {renderResponseType,renderResponseLevel,renderNodeStatus} from '../../utils'
import { jsPlumb } from "jsplumb"; 
import './index.scss'
import {message,Tooltip} from 'antd'
import { useHistory,useLocation } from "react-router-dom";
import { calcNum,renderResponseStatus } from '../../utils'

const AllNodePage = () => {
    const [nodeList,setNodeList] = useState([])
    const [taskInfo,setTaskInfo] = useState([])
    const history = useHistory();
    const location = useLocation();

    let timer;
	const instance = jsPlumb.getInstance();
	const resize = () => {
		instance.repaintEverything();
	};
	const clear = () => {
		if (timer) {
			clearInterval(timer);
		}
		instance.deleteEveryConnection();
	};
   
	const init = (nodeList,respTask) => {
        let m = 0.01;
        let l = 0.98;
		timer = setInterval(() => {
			m += 0.02;
			if (m > 0.98) {
				m = 0.012;
			}
            l -= 0.02;
			if (l < 0.012) {
				l = 0.98
			}
			instance.deleteEveryConnection();

            const topStatusText = renderResponseStatus(respTask?.dstatus,respTask?.declareStatus)

            if(topStatusText === "待申报" || topStatusText === "已出清"){
                instance.connect({
                    source: "node-top",//图一的id
                    target: "node-center",//图二的id
                    endpoint: "Blank", //端点的形状设置为空
                    connector: ['Straight'],
                    anchor: ["Bottom", "Top"],//连接端点的位置，起始节点的右侧和结束节点的左侧
                    paintStyle: { stroke: "#0092FF", strokeWidth: 2},//线的样式
                    overlays: [
                        ["Arrow",{ width: 12, length: 12, location:m}],//小箭头样式及位置
                    ],
                });
            }

            if(topStatusText === "待出清" || topStatusText === "执行中"){
                instance.connect({
                    source: "node-top",//图一的id
                    target: "node-center",//图二的id
                    endpoint: "Blank", //端点的形状设置为空
                    connector: ['Straight'],
                    anchor: ["Bottom", "Top"],
                    paintStyle: { stroke: "#0092FF", strokeWidth: 2},//线的样式
                    overlays: [
                        ["Arrow",{ width: 12, length: 12, location:l,direction:-1}],//小箭头样式及位置
                    ],
                });
            }

            if(topStatusText === "已完成"){
                instance.connect({
                    source: "node-top",//图一的id
                    target: "node-center",//图二的id
                    endpoint: "Blank", //端点的形状设置为空
                    connector: ['Straight'],
                    anchor: ["Bottom", "Top"],//连接端点的位置，起始节点的右侧和结束节点的左侧
                    paintStyle: { stroke: "#0092FF", strokeWidth: 2},//线的样式
                });
            }

            nodeList?.forEach((item,i) => {
                if(topStatusText === '待申报' || topStatusText === '执行中'){
                    instance.connect({
                        source: "node-center-bottom",//图一的id
                        target: `node-item-${i}`,//图二的id
                        endpoint: "Blank", //端点的形状设置为空
                        connector: ['Straight'],
                        anchor: [ "Top","Bottom"],//连接端点的位置，起始节点的右侧和结束节点的左侧
                        paintStyle: { stroke: "#0092FF", strokeWidth: 2 },//线的样式
                        overlays: [
                            ["Arrow", { width: 12, length: 12, location:l,direction:-1}],//小箭头样式及位置
                        ],
                    });
                }
                if(topStatusText === '待出清' || topStatusText === '已出清'){
                    instance.connect({
                        source: "node-center-bottom",//图一的id
                        target: `node-item-${i}`,//图二的id
                        endpoint: "Blank", //端点的形状设置为空
                        connector: ['Straight'],
                        anchor: ["Bottom", "Top"],//连接端点的位置，起始节点的右侧和结束节点的左侧
                        paintStyle: { stroke: "#0092FF", strokeWidth: 2 },//线的样式
                        overlays: [
                            ["Arrow", { width: 12, length: 12, location:m}],//小箭头样式及位置
                        ],
                    });
                }
                if(topStatusText === '已完成'){
                    instance.connect({
                        source: "node-center-bottom",//图一的id
                        target: `node-item-${i}`,//图二的id
                        endpoint: "Blank", //端点的形状设置为空
                        connector: ['Straight'],
                        anchor: ["Bottom", "Top"],//连接端点的位置，起始节点的右侧和结束节点的左侧
                        paintStyle: { stroke: "#0092FF", strokeWidth: 2 },//线的样式
                    });
                }
            })

		}, 200);

	};


    const getDecisionChainList = (respId) => {
        const params = new URLSearchParams();
        params.append("respId", respId);
    
        http.post("AIEnergy/getDecisionChainList",params)
        .then(function (response) {
            const data = response.data.data
            setNodeList(data?.nodeList)
            setTaskInfo(data?.respTask)
            init(data?.nodeList,data?.respTask)
        })
        .catch(function (error) {
            message.error(`接口错误getDecisionChainList`)
        });

    }


    useEffect(() => {
        getDecisionChainList(location.state.respId)
        window.addEventListener("resize", resize);
		return () => {
			clear();
			window.removeEventListener("resize", resize);
		};
    },[])


    return (
        <div className="all-node-page">
            <i className='close-icon' onClick={() => {
                history.push('/demandForecast')
            }}/>
            <div className='spot-forecast_content_right_content'>
                <div className='spot-forecast-right-top'>
                    <div className='right-img'></div>
                    <span className='right-text' id='node-top'>需求响应平台</span>
                </div>
                <div style={{height:'1px'}} id="spot-forecast-right-top"></div>

                <div className='spot-forecast-right-center'>
                    <div className='center-img-top'></div>
                    <div style={{height:'1px'}} id="model-wrap"></div>
                    <div className='model-wrap'>
                        <div className='modal-img' id='node-center'></div>
                        <span className='model-text' id='node-center-bottom'>能源大模型</span>
                    </div>
                    
                </div>
                <div className='spot-forecast-right-bottom'>
                    <div className='arrow-bottom'></div>
                </div>
                <div className='left-text-wrap'>
                    <p className='left-text'>响应时段：{taskInfo?.rsTime}-{taskInfo?.reTime?.split(' ')[1]}</p>
                    <p className='left-text'>负荷需求(kW)：{calcNum(taskInfo?.respLoad)}</p>
                    {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已出清' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' || renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' ? <p className='left-text'>出清负荷(kW)：{calcNum(taskInfo?.declareLoad)}</p> : null}
                    <p className='left-text'>响应类型：{renderResponseType(taskInfo?.respType)}</p>
                    <p className='left-text'>响应级别：{renderResponseLevel(taskInfo?.respLevel)}</p>
                </div>
                <div className='right-text-wrap'>
                    <p className='other-right-text'>需求响应状态：{renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus)}</p>
                    <p className='other-right-text'>申报负荷(kW)：{renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '待申报' ? 0 : calcNum(taskInfo?.declareLoad)}</p>
                    <p className='other-right-text'>调节负荷(kW)：{renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '待申报' ? 0 : calcNum(taskInfo?.adjustLoad)}</p>
                    {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '待申报' ? null :<p className='other-right-text'>预测负荷(kW)：{calcNum(taskInfo?.forecastLoad)}</p>}
                    {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '执行中' ? <>
                            <p className='other-right-text'>实际负荷(kW)：{calcNum(taskInfo?.actualLoad)}</p>
                            <p className='other-right-text'>基线负荷(kW)：{calcNum(taskInfo?.baseLoad)}</p>
                        </>:null}
                    {renderResponseStatus(taskInfo?.dstatus,taskInfo?.declareStatus) === '已完成' ? <>
                        <p className='other-right-text'>响应负荷(kW)：{calcNum(taskInfo?.actualLoad)}</p>
                        <p className='other-right-text'>响应电量(kW)：{calcNum(taskInfo?.baseLoad)}</p>
                        <p className='other-right-text'>预估收益（元）：{calcNum(taskInfo?.baseLoad)}元</p>
                    </>:null}

                </div>
            </div>
            <div className='dots-wrapper'>
                {
                    nodeList?.map((item,i) => {
                        return (
                            <div className='dots-1' key={i}>
                                    <div className='dots-1-header' id={`node-item-${i}`}> 
                                        <div className={classNames('round',{'online-status':item?.online})}></div>
                                        <Tooltip placement="top" title={item.nodeName} color='#38373F'>
                                            <span className='dots-1-header-text'>{item?.nodeName}</span>
                                        </Tooltip>
                                    </div>
                                    <div className='dots1-content'>
                                        <p className='dots1-text'>状态：{renderNodeStatus(item?.drsStatus) || '-'}</p>
                                        <>
                                            <p className='dots1-text'>申报负荷(kW)：{renderNodeStatus(item?.drsStatus) ==='待申报' ? 0: calcNum(item?.declareLoad)}</p>
                                            <p className='dots1-text'>调节负荷(kW)：{renderNodeStatus(item?.drsStatus) ==='待申报' ? 0: calcNum(item?.adjustLoad)}</p>
                                            <p className='dots1-text'>预测负荷(kW)：{calcNum(item?.forecastLoad)}</p>
                                        </>
                                        {
                                            item?.drsStatus === 24 ? 
                                            <>
                                                <p className='dots1-text'>实际负荷(kW)：{calcNum(item?.deviceRatedLoad)}</p>
                                                <p className='dots1-text'>基线负荷(kW)：{calcNum(item?.reality)}</p>
                                            </> : null
                                        }
                                        {
                                            item?.drsStatus === 25 ? 
                                            <>
                                                <p className='dots1-text'>响应负荷(kW)：{calcNum(item?.responseLoad)}</p>
                                                <p className='dots1-text'>响应电量(kW)：{calcNum(item?.realTimeLoad)}</p>
                                                <p className='dots1-text'>预估收益（元）：{calcNum(item?.income)}元</p>
                                            </> : null
                                        }
                                    </div>
                                </div>
                        )
                    })
                }
            </div>
        </div>
    )
   
}

export default AllNodePage