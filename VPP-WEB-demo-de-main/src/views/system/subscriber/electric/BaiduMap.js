import React from 'react'
import { Table, Input, Select, Button, Icon, Modal, Form } from 'antd';
// import './style.css'
class BaiduMap extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      value: '',
	  sadd:''
    }
  }
  componentDidMount() {

    var marker;
    var zoomSize = 18;
    this.zoomSize = zoomSize
    var map = new window.BMap.Map("l-map");
    this.map = map
    var lon = 116.404;  //默认为北京市
    var lat = 39.915;

    //确定中心位置
    var point = new window.BMap.Point(lon, lat);
    map.centerAndZoom(point, zoomSize);

    //标注
    marker = new window.BMap.Marker(point);// 创建标注
    this.marker = marker
    map.addOverlay(marker);             // 将标注添加到地图中
    marker.enableDragging();           // 可拖拽
    marker.addEventListener("dragend", this.setBizValue);

    // 添加带有定位的导航控件
    var navigationControl = new window.BMap.NavigationControl({
      // 靠左上角位置
      anchor: 'BMAP_ANCHOR_TOP_LEFT',
      // LARGE类型
      type: 'BMAP_NAVIGATION_CONTROL_LARGE',
      // 启用显示定位
      enableGeolocation: true
    });
    map.addControl(navigationControl);



    //输入地址事件处理 start
    var ac = new window.BMap.Autocomplete(    //建立一个自动完成的对象
      {
        "input": "suggestId",
        "location": map
      });
    this.ac = ac
    let _this = this
  
    ac.addEventListener("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
      var str = "";
      var _value = e.fromitem.value;
      var value = "";
      if (e.fromitem.index > -1) {
        value = _value.city + _value.district + _value.street + _value.business;
      }
      str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

      value = "";
      if (e.toitem.index > -1) {
        _value = e.toitem.value;
        value = _value.city + _value.district + _value.street + _value.business;
      }
      str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
      G("searchResultPanel").innerHTML = str;
    });

    var myValue;
    ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
	
      var _value = e.item.value;
	  console.log(_value)
      myValue =  _value.city + _value.district + _value.street + _value.business;
      G("searchResultPanel").innerHTML = "onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
      _this.setState({
      	sadd:myValue
      })
	  setPlace();
    });
    function setPlace() {
		_this.map.clearOverlays();    //清除地图上所有覆盖物
		function myFun() {
			var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
			console.log(pp)
			_this.setBizValueForPoint(pp);
			_this.map.centerAndZoom(pp, zoomSize);
			_this.marker = new window.BMap.Marker(pp);
			_this.marker.enableDragging();           // 可拖拽
			_this.map.addOverlay(marker);    //添加标注
			_this.marker.addEventListener("dragend", _this.setBizValue);
		}
		var local = new window.BMap.LocalSearch(_this.map, { //智能搜索
			onSearchComplete: myFun
		});
		console.log(local)
		_this.setState({
			value: local,
		})
		// _this.props.getMap({addr: myValue })
		console.log(local)
		console.log(myValue)
		local.search(myValue);
    }

    function G(id) {
		return document.getElementById(id);
    }

	}
	componentWillReceiveProps(nextProps) {
		// console.log(nextProps)
		// console.log(this.props)
		if(this.props.istrue ==true){
			if(nextProps.address!=='null' && nextProps.address){
				this.ac.setInputValue(nextProps.address)
				this.props.changeData()
			}
		}
		
	}
	// shouldComponentUpdate(current) {
	// 	console.log(current)
	// 	if (current.address!=='null' && current.address) {
	// 		this.ac.setInputValue(current.address)
	// 		return true
	// 	}
	// 	return false
	// }
  // 设置经纬度
  setBizValueForPoint(point) {
    let _this = this
    var geoc = new window.BMap.Geocoder();
    let lon = point.lng;
    let lat = point.lat;
    geoc.getLocation(point, function (rs) {
		console.log(rs)
      var addComp = rs.addressComponents;
      console.log(addComp)
      let addr = addComp.province + addComp.city + addComp.district + addComp.street + addComp.streetNumber;
      // if (typeof setBizFun == "function") {
      //   setBizFun({ lon: lon, lat: lat, addr: addr });
      // }
      _this.props.getMap({ lon: lon, lat: lat,addr:_this.state.sadd})
	  // _this.props.getMap({ lon: lon, lat: lat })
    });

  }
  //根据事件，设置经纬度和地址
  setBizValue(e) {
    var point = e.point;
    // this.setBizValueForPoint(point);
  }
  //地址转坐标
  addrSearch(serachAddr) {
    // 创建地址解析器实例
    var myGeo = new window.BMap.Geocoder();
    // 将地址解析结果显示在地图上,并调整地图视野
    if (!serachAddr) {
      // serachAddr = $("#suggestId").val();
    }
    myGeo.getPoint(serachAddr, function (point) {
      if (point) {
        this.setBizValueForPoint(point);
        this.map.clearOverlays();
        this.map.centerAndZoom(point, this.zoomSize);
        this.marker = new window.BMap.Marker(point);
        this.marker.enableDragging();           // 可拖拽
        this.map.addOverlay(this.marker);    //添加标注
        this.marker.addEventListener("dragend", this.setBizValue);

      } else {
        console.log("search click no results!")
      }
    });

  }
  // setaddress (ty,ev)
  render() {
    const { address } = this.props
    return <div>
      <div id="r-result">
        <input type="text" id="suggestId" size="20" 
			style={{ width: '100%', height: 30, color: "#2A2B40", 
			background: 'none', padding: '4px 11px',
			border: '1px solid #d9d9d9' }} 
			placeholder="请输入地址"
			// onChange={(e) =>this.setaddress(e.target.value)}
		/>
      </div>
      <div id="l-map"></div>
      <div id="searchResultPanel" 
	  style={{ border: '1px solid #C0C0C0', width: 150, 
	  height: 'auto', position: 'relative', 
	  display: 'none', zIndex: 99999 }}></div>

    </div>
  }
}

export default BaiduMap
