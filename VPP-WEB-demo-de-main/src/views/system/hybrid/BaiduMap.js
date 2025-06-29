import React, { useEffect, useState } from "react";
import { Table, Input, Select, Button, Icon, Modal, Form } from "antd";
// import './style.css'
const BaiduMap = (props) => {
  const [value, setValue] = useState("");
  const [sadd, setSadd] = useState("");
  const [city, setCity] = useState("");
  const [addr, setAddr] = useState("");
  const [map, setMap] = useState(null);
  const [ac, setAc] = useState(null);

  useEffect(() => {
    // if(window.BMap){
    // 	const map = new window.BMap.Map("l-map");
    //     setMap(map);

    // }
    function loadJScript() {
      var script = document.createElement("script");
      script.type = "text/javascript";
      script.src =
        "//api.map.baidu.com/api?type=webgl&v=1.0&ak=N6wH5jFd6P3h82VpvutGnO9MwuL6hBqK&callback=init";
      document.body.appendChild(script);
    }
    function init() {
      var map = new window.BMap.Map("l-map"); // 创建Map实例
      setMap(map);
      var point = new window.BMap.Point(116.404, 39.915); // 创建点坐标
      map.centerAndZoom(point, 10);
      map.enableScrollWheelZoom(); // 启用滚轮放大缩小
    }
    window.onload = loadJScript; // 异步加载地图
    init();
  }, []);

  useEffect(() => {}, [map]);
  useEffect(() => {
    if (ac) {
      ac.addEventListener("onhighlight", function (e) {
        //鼠标放在下拉列表上的事件
        var str = "";
        var _value = e.fromitem.value;
        var value = "";
        if (e.fromitem.index > -1) {
          value =
            _value.city + _value.district + _value.street + _value.business;
        }
        str =
          "FromItem<br />index = " +
          e.fromitem.index +
          "<br />value = " +
          value;

        value = "";
        if (e.toitem.index > -1) {
          _value = e.toitem.value;
          value =
            _value.city + _value.district + _value.street + _value.business;
        }
        str +=
          "<br />ToItem<br />index = " +
          e.toitem.index +
          "<br />value = " +
          value;
        G("searchResultPanel").innerHTML = str;
      });

      var myValue;
      ac.addEventListener("onconfirm", function (e) {
        //鼠标点击下拉列表后的事件
        console.log(e);
        var _value = e.item.value;
        const { province, city, district } = _value;
        console.log(_value);
        myValue = _value.business;
        // var _value = e.item.value;
        // myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
        G("searchResultPanel").innerHTML =
          "onconfirm<br />index = " +
          e.item.index +
          "<br />myValue = " +
          myValue;
        setSadd(myValue);
        setAddr(myValue);
        ac.hide();
        // const address = props.address
        // const regex = /^(?<province>[^市]+省|[^市]+市)?(?<city>[^区]+市)?(?<district>[^区]+县|[^区]+区)?/;
        // const match = address.match(regex);
        // if (match && match.groups) {
        // 	// 提取省市区部分
        // 	const { province, city,district } = match.groups;
        // 	props.setAddressInfo({
        // 		province:province,
        // 		city :city ? city : province,
        // 		district
        // 	})
        // } else {
        // 	console.log("No match found.");
        // }
      });

      function G(id) {
        return document.getElementById(id);
      }
    }
  }, [ac]);
  const setPlace = () => {
    map.clearOverlays(); //清除地图上所有覆盖物
    function myFun() {
      var pp = local.getResults().getPoi(0).point; //获取第一个智能搜索的结果
      map.centerAndZoom(pp, 18);
      map.addOverlay(new window.BMap.Marker(pp)); //添加标注
      setBizValueForPoint(pp);
    }
    var local = new window.BMap.LocalSearch(map, {
      //智能搜索
      onSearchComplete: myFun,
    });
    local.search(addr);
  };
  useEffect(() => {
    if (addr) {
      setPlace();
    }
  }, [addr]);

  useEffect(() => {
    if (ac) {
      if (props.istrue == true) {
        if (props.address !== "null" && props.address) {
          // 编辑
          props.changeData();
          ac.setInputValue(props.address);
          console.log(props.address);
        }
        if (props.isModalVisible == true) {
          if (props.mapLable) {
            if (props.cityVale == 0) {
            } else if (props.cityVale == 1) {
              setCity(props.mapLable);
              props.changeData();
              setCity(props.mapLable);
              // city:nextProps.mapLable
            }
          }
        }
      }
    }
  }, [props, ac]);

  // 设置经纬度
  const setBizValueForPoint = (point) => {
    if (point) {
      var geoc = new window.BMap.Geocoder();
      let lon = point.lng;
      let lat = point.lat;
      geoc.getLocation(point, function (rs) {
        console.log(rs);
        var addComp = rs.addressComponents;
        console.log(addComp);
        // console.log(_this.state.addr)
        let str = addr.substr(0, 3);
        // console.log(str)
        // let addr = addComp.province +addComp.city +_this.state.addr
        console.log(addr);

        if (addComp.province == "北京市") {
          console.log(addComp.province + addComp.district + addr);
          props.getMap({
            lon: lon,
            lat: lat,
            addr: addComp.province + addComp.district + addr,
          });
        } else if (addComp.province == "天津市") {
          props.getMap({
            lon: lon,
            lat: lat,
            addr: addComp.province + addComp.district + addr,
          });
        } else if (addComp.province == "上海市") {
          props.getMap({
            lon: lon,
            lat: lat,
            addr: addComp.province + addComp.district + addr,
          });
        } else if (addComp.province == "重庆市") {
          props.getMap({
            lon: lon,
            lat: lat,
            addr: addComp.province + addComp.district + addr,
          });
        } else {
          console.log(
            addComp.province + addComp.city + addComp.district + addr
          );
          props.getMap({
            lon: lon,
            lat: lat,
            addr: addComp.province + addComp.city + addComp.district + addr,
          });
        }
      });
    }
  };
  //地址转坐标
  const setaddress = (ty) => {
    props.getMap({ lon: "", lat: "", addr: ty });
  };

  useEffect(() => {
    setTimeout(() => {
      let address = props.address;
      let provinceEnd = "";
      if (address.indexOf("省") !== -1) {
        provinceEnd = address.indexOf("省") + 1;
      } else if (address.indexOf("自治区") !== -1) {
        provinceEnd = address.indexOf("自治区") + 3;
      } else if (address.indexOf("行政区") !== -1) {
        provinceEnd = address.indexOf("行政区") + 3;
      } else if (address.indexOf("市") !== -1) {
        provinceEnd = address.indexOf("市") + 1;
      } else if (address.indexOf("香港") !== -1) {
        provinceEnd = address.indexOf("香港") + 2;
      }
      let province = address.slice(0, provinceEnd);
      address = address.substring(provinceEnd, address.length - 1);

      // “福建省”的结束位置
      let cityEnd = "";
      if (address.indexOf("市") !== -1) {
        cityEnd = address.indexOf("市") + 1;
      } else if (address.indexOf("自治州") !== -1) {
        cityEnd = address.indexOf("自治州") + 3;
      } else if (address.indexOf("香港") !== -1) {
        cityEnd = address.indexOf("香港") + 2;
      }

      let city = address.slice(0, cityEnd);
      address = address.substring(cityEnd, address.length - 1);

      let districtEnd = "";
      if (address.indexOf("县") !== -1) {
        districtEnd = address.indexOf("县") + 1;
      } else if (address.indexOf("市") !== -1) {
        districtEnd = address.indexOf("市") + 1;
      } else if (address.indexOf("区") !== -1) {
        districtEnd = address.indexOf("区") + 1;
      }

      // 提取省、市、区
      let district = address.slice(0, districtEnd);
      address = address.substring(districtEnd, address.length - 1);

      props.setAddressInfo({
        province: province === "香港" ? "香港特别行政区" : province,
        city: city === "香港" ? "香港特别行政区" : city ? city : province,
        district,
      });
    }, 500);
  }, [props.address]);

  // const { address } = this.props
  return (
    <div>
      <div id="r-result">
        <input
          type="text"
          id="suggestId"
          size="20"
          style={{
            width: "calc(100% - 24px)",
            height: 64,
            color: "#FFF",
            background: "none",
            padding: "4px 11px",
            border: "1px solid #d9d9d9",
          }}
          placeholder="请输入详细地址（例如***街***号）"
          value={props.address}
          onChange={(e) => setaddress(e.target.value)}
          onFocus={() => {
            if (!ac) {
              var marker;
              var zoomSize = 18;
              var lon = 116.404; //默认为北京市
              var lat = 39.915;
              var point = new window.BMap.Point(lon, lat);
              console.log(point);
              // if(props.mapLable){}
              map.centerAndZoom(props.mapLable, zoomSize);
              // console.log(props.mapLable)
              //标注
              marker = new window.BMap.Marker(point); // 创建标注
              // this.marker = marker
              map.addOverlay(marker); // 将标注添加到地图中
              marker.enableDragging(); // 可拖拽

              //输入地址事件处理 start
              var ac1 = new window.BMap.Autocomplete({ //建立一个自动完成的对象
                input: "suggestId",
                location: map,
              });
              setAc(ac1);
            }
          }}
          onBlur={() => {
            ac.hide();
          }}
        />
      </div>
      <div id="l-map"></div>
      <div
        id="searchResultPanel"
        style={{
          border: "1px solid #1890ff",
          width: 150,
          height: "auto",
          position: "relative",
          display: "none",
          zIndex: 99999,
        }}
      ></div>
    </div>
  );
};

export default BaiduMap;
