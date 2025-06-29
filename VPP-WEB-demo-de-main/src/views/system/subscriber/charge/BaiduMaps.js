import React from 'react'
import { Table, Input, Select, Button, Icon, Modal, Form } from 'antd';
// import './style.css'
class BaiduMaps extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      value: '',
	  newaddress:''
    }
  }
  componentDidMount() {
		
		function G(id) {
		  return document.getElementById(id);
		}
		var map = new window.BMap.Map("l-map");
		map.centerAndZoom("北京",12);                   // 初始化地图,设置城市和地图级别。
		
		var ac = new window.BMap.Autocomplete(    //建立一个自动完成的对象
			{"input" : "suggestId1"
			,"location" : map
		});
		this.ac = ac
		let _this = this
		ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
			var str = "";
					var _value = e.fromitem.value;
					var value = "";
					if (e.fromitem.index > -1) {
						value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
					}    
					str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;
					
					value = "";
					if (e.toitem.index > -1) {
						_value = e.toitem.value;
						value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
					}    
					str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
					G("searchResultPanel").innerHTML = str;
				});
			
				var myValue;
				ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
				var _value = e.item.value;
					myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
					G("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
					
					setPlace();
		});

			
			function setPlace(){
				map.clearOverlays();    //清除地图上所有覆盖物
				function myFun(){
					var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
					_this.setBizValueForPoint(pp);
					console.log(pp)
					let lon = pp.lng;
					let lat = pp.lat;
					// this.props.getMap({ lon: lon, lat: lat})
					map.centerAndZoom(pp, 18);
					map.addOverlay(new window.BMap.Marker(pp));    //添加标注
				}
				var local = new window.BMap.LocalSearch(map, { //智能搜索
				  onSearchComplete: myFun
				});
				_this.setState({
				  // value: local,
				  newaddress:myValue
				})
				local.search(myValue);
			}
			
    

  }
  // shouldComponentUpdate(current,nextState) {
	 //  // let _this = this
	 //  // console.log(current.address)
	 //  // console.log(nextState)
	 //  // if (this.props.address !== current.address) {
		// 	// this.ac.setInputValue(current.address)
	 //  //       return true;
	 //  //     }
	 //  //   //   if (this.state.value !== nextState) {
		// 	//   // // this.ac.setInputValue(current.address)
	 //  //   //     return true;
	 //  //   //   }
	 //  //     return false;
	 //  // return nextState !== this.props.address
  //   if (current.address!=='null' && current.address) {
		// // console.log(this.ac)
		// this.props.address=current.address
  //     this.ac.setInputValue(current.address)
  //     return false
  //   }else{}
  //   return false
  // }
  // shouldComponentUpdate(current) {
  //   if (current.address!=='null' && current.address) {
  //     this.ac.setInputValue(current.address)
  //     return true
  //   }
  //   return false
  // }
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
  setBizValueForPoint(point) {
    let _this = this
    var geoc = new window.BMap.Geocoder();
    let lon = point.lng;
    let lat = point.lat;
    geoc.getLocation(point, function (rs) {
      var addComp = rs.addressComponents;
      console.log(addComp)
      // let addr = addComp.province + addComp.city + addComp.district + addComp.street + addComp.streetNumber;
      // if (typeof setBizFun == "function") {
      //   setBizFun({ lon: lon, lat: lat, addr: addr });
      // }
	  // console.log(addr)
	  let addr = _this.state.newaddress
      _this.props.getMap({ lon: lon, lat: lat, addr: addr })
    });
  
  }
  //根据事件，设置经纬度和地址
  setBizValue(e) {
    var point = e.point;
    this.setBizValueForPoint(point);
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
  render() {
    const { address } = this.props
    return <div>
      <div id="r-result">
        <input type="text" id="suggestId1" size="20" style={{ width: '100%', height: 30, color: "#2A2B40", background: 'none', padding: '4px 11px', border: '1px solid #d9d9d9' }} placeholder="请输入地址" />
      </div>
      <div id="l-map"></div>
      <div id="searchResultPanel" style={{ border: '1px solid #C0C0C0', width: 150, height: 'auto', position: 'relative', display: 'none', zIndex: 999999999 }}></div>

    </div>
  }
}

export default BaiduMaps
