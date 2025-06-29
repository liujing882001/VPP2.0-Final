import React,{Component} from 'react'
	


class login extends Component {
	constructor(props) {
		super(props)
		this.state={
			
		}
	}
	componentDidMount(){
		this.s()
	}
	
	s(){
		var map = new window.BMap.Map('containeras'); // 创建Map实例
		map.centerAndZoom(new window.BMap.Point(116.404, 39.915), 12); // 初始化地图,设置中心点坐标和地图级别
		map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
		// 启用拖拽
		map.enableDragging();
		map.disableDoubleClickZoom();
		 // var scaleCtrl = new window.BMap.ScaleControl();  // 添加比例尺控件
		 //    map.addControl(scaleCtrl);
		 //    var zoomCtrl = new window.BMap.ZoomControl();  // 添加缩放控件
		 //    map.addControl(zoomCtrl);
    
	}
	render(){
		return(
			<div style={{height:'600px'}}>
				<div id="containeras" style={{width:'100%',height:'600px'}}></div>
qwqwqw
			</div>
		)
	}
	
	

}
export default login
