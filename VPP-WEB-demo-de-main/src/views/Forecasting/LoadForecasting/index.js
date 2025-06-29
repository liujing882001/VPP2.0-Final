import React,{ Component,useEffect,useState } from 'react'
import {jsPlumb} from "jsplumb"; 

export const LoadForecasting = () => {
	const [renderFlag,setRenderFlag] = useState(true)

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

	const init = () => {
		//连接图一和图二
		instance.connect({
			source: "flowChart1",//图一的id
			target: "flowChart2",//图二的id
			endpoint: "Blank", //端点的形状设置为空
			connector: ["Straight"],
			anchor: ["Right", "Left"],//连接端点的位置，起始节点的右侧和结束节点的左侧
			paintStyle: { stroke: "#457DA5", strokeWidth: 2 },//线的样式
			overlays: [
				["Arrow", { width: 12, length: 12, location: 0.5 }],//小箭头样式及位置
			],
		});
		//连接图二和图三
		instance.connect({
			source: "flowChart2",
			target: "flowChart3",
			endpoint: "Blank",
			connector:["Straight"],
			anchor: ["Right", "Left"],
			paintStyle: { stroke: "#474554", strokeWidth: 2 },
			overlays: [
				["Arrow", { width: 12, length: 12, location: 0.5  }],
			],
		});
		//连接图四和图三
		instance.connect({
			source: "flowChart3",
			target: "flowChart4",
			endpoint: "Blank",
			connector:["Straight"],
			anchor: ["Bottom", "Top"],
			paintStyle: { stroke: "#27951D", strokeWidth: 2 },
			overlays: [
				["Arrow", { width: 12, length: 12, location: 0.5  }],
			],
		});
		//连接图四和图二
		instance.connect({
			source: "flowChart4",
			target: "flowChart2",
			endpoint: "Blank",
			connector:["Straight"],
			anchor: ["Bottom", "Top"],
			paintStyle: { stroke: "#27951D", strokeWidth: 2 },
			overlays: [
				["Arrow", { width: 12, length: 12, location: 0.5}],
			],
		});
	};


	const init2 = () => {
		instance.deleteEveryConnection();
	};


	useEffect(() => {
		instance.deleteEveryConnection();
		setTimeout(() => {
			init();
		},3000)
		return () => {
			// clear();
			// window.removeEventListener("resize", resize);
		};
	}, [renderFlag]);

	return (
		<div style={{width:'100%',display:"flex",justifyContent:"space-between"}}>
			<div id={'flowChart1'} style={{border: "5px solid #457DA5",position:'relative',left:'100px',top:'500px'}}>FlowChart1</div>
			<div id={'flowChart2'} style={{border: "5px solid #474554",position:'relative',left:'100px',top:'500px'}}>FlowChart2</div>
			<div id={'flowChart3'} style={{border: "5px solid #9C8D41"}}>FlowChart3</div>
			<div id={'flowChart4'} style={{border: "5px solid #27951D"}}>FlowChart4</div>
			<div style={{background:'#fff'}} onClick={() => {
				
				instance.deleteEveryConnection();
				setTimeout(() => {
					init();
				},3000)
			}}>1111</div>
		</div>
	);
}
 export default LoadForecasting