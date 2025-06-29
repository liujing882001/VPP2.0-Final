import {
	useEffect,
	useState,
	useRef
} from "react";
import styles from "./MapContainer.scss";
import AMapLoader from "@amap/amap-jsapi-loader";
import http from '../../../../server/server.js'
import intelligent_plan from './imgmap/intelligent_plan.svg'
import intelligent_construct from './imgmap/intelligent_construct.svg'
import intelligent_operation from './imgmap/intelligent_operation.svg'
import intelligent_close from './imgmap/intelligent_close.svg'
// 楼宇园区
import building_plan from './imgmap/building_plan.svg'
import building_construct from './imgmap/building_construct.svg'
import building_operation from './imgmap/building_operation.svg'
import building_close from './imgmap/building_close.svg'
// 智能制造
import intelligence_plan from './imgmap/intelligence_plan.svg'
import intelligence_construct from './imgmap/intelligence_construct.svg'
import intelligence_operation from './imgmap/intelligence_operation.svg'
import intelligence_close from './imgmap/intelligence_close.svg'
// 智能交通
import traffic_plan from './imgmap/traffic_plan.svg'
import traffic_construct from './imgmap/traffic_construct.svg'
import traffic_operation from './imgmap/traffic_operation.svg'
import traffic_close from './imgmap/traffic_close.svg'
// 地图图标
import mapintelligent_plan from './img/intelligent_plan.svg'
import mapintelligent_construct from './img/intelligent_construct.svg'
import mapintelligent_operation from './img/intelligent_operation.svg'
import mapintelligent_close from './img/intelligent_close.svg'
// 楼宇园区
import mapbuilding_plan from './img/building_plan.svg'
import mapbuilding_construct from './img/building_construct.svg'
import mapbuilding_operation from './img/building_operation.svg'
import mapbuilding_close from './img/building_close.svg'
// 智能制造
import mapintelligence_plan from './img/intelligence_plan.svg'
import mapintelligence_construct from './img/intelligence_construct.svg'
import mapintelligence_operation from './img/intelligence_operation.svg'
import mapintelligence_close from './img/intelligence_close.svg'
// 智能交通
import maptraffic_plan from './img/traffic_plan.svg'
import maptraffic_construct from './img/traffic_construct.svg'
import maptraffic_operation from './img/traffic_operation.svg'
import maptraffic_close from './img/traffic_close.svg'
window._AMapSecurityConfig = {
	securityJsCode: "3cf309c14b2dc3a0f33bcce6eb47630d",
};
export const MapContainer = ({
	nodeIds,
	selectCity,
	filtetname
}) => {
	const [markerArr, setMarkerArr] = useState([])
	const mapInstanceRef = useRef(null);
	const markersRef = useRef([]); // 用于存储marker实例
	let map, district, polygons = [],
		citycode;
	const mapRef = useRef(null)
	const [zoomLevel, setZoomLevel] = useState(4.5);
	const infoWindowRef = useRef(null);
	useEffect(() => {
		if (nodeIds.length > 0) {
			getNodesLocation()
		}
	}, [nodeIds])
	const getNodesLocation = () => {
		http.post('homePage/getNodesLocation', {
			nodes: nodeIds
		}).then(res => {
			if (res.data.code === 200) {
				let data = res.data.data
				data.map(pointData => {
					pointData.position = [pointData.longitude, pointData.latitude]
					res.imageUrl = intelligent_plan
					if (pointData.nodePostType === '智算中心') {
						if (pointData.stationState == '建设中') {
							pointData.imageUrl = intelligent_construct
							pointData.icon = mapintelligent_construct
						} else if (pointData.stationState == '规划中') {
							pointData.imageUrl = intelligent_plan
							pointData.icon = mapintelligent_plan
						} else if (pointData.stationState == '运营中') {
							pointData.imageUrl = intelligent_operation
							pointData.icon = mapintelligent_operation
						} else {
							pointData.imageUrl = intelligent_close
							pointData.icon = mapintelligent_operation
						}
					} else
					if (pointData.nodePostType === '楼宇园区') {
						if (pointData.stationState == '建设中') {
							pointData.imageUrl = building_construct
							pointData.icon = mapbuilding_construct
						} else if (pointData.stationState == '规划中') {
							pointData.imageUrl = building_plan
							pointData.icon = mapbuilding_plan
						} else if (pointData.stationState == '运营中') {
							pointData.imageUrl = building_operation
							pointData.icon = mapbuilding_operation
						} else {
							pointData.imageUrl = building_close
							pointData.icon = mapbuilding_operation
						}

					} else
					if (pointData.nodePostType === '智能制造') {
						if (pointData.stationState == '建设中') {
							pointData.imageUrl = intelligence_construct
							pointData.icon = mapintelligence_construct
						} else if (pointData.stationState == '规划中') {
							pointData.imageUrl = intelligence_plan
							pointData.icon = mapintelligence_plan
						} else if (pointData.stationState == '运营中') {
							pointData.imageUrl = intelligence_operation
							pointData.icon = mapintelligence_plan
						} else {
							pointData.imageUrl = intelligence_close
							pointData.icon = mapintelligence_plan
						}
					} else {
						if (pointData.stationState == '建设中') {
							pointData.imageUrl = traffic_construct
							pointData.icon = maptraffic_construct
						} else if (pointData.stationState == '规划中') {
							pointData.imageUrl = traffic_plan
							pointData.icon = maptraffic_plan
						} else if (pointData.stationState == '运营中') {
							pointData.imageUrl = traffic_operation
							pointData.icon = maptraffic_plan
						} else {
							pointData.imageUrl = traffic_close
							pointData.icon = maptraffic_plan
						}
					}


				})
				setMarkerArr(data)

			}
		}).catch(err => {
			console.log(err)
		})
	}
	const clearMarkers = () => {
		markersRef.current.forEach(marker => {
			marker.setMap(null); 
		});
		markersRef.current = []; 
	};
	const addMarkers = (map) => {
		if (map) {
			clearMarkers()
		}
		markerArr && markerArr.forEach((markerData, index) => {

			const markerContent = `
			        <div class="custom-marker">
			          <img src="${markerData.imageUrl}" alt="Marker" />
			        </div>
			      `;
				
			const marker = new window.AMap.Marker({
				position: new window.AMap.LngLat(...markerData.position),
				content: markerContent,
				offset: new window.AMap.Pixel(-10, -20),
			});
			const infoWindow = new window.AMap.InfoWindow({
			    content: `<div class="custom-info-window">
							  
								<p><img src="${markerData.icon}" /><span>${markerData.nodeName}</span></p>
								<div><b>可调负荷</b>：${markerData.loadKeTiao != null && markerData.loadKeTiao !== undefined&& markerData.loadKeTiao !== '' ? Number(markerData.loadKeTiao).toFixed(2) + 'kW' : '-'}</div>
								<div><b>接入负荷</b>：${markerData.loadJieRu != null && markerData.loadJieRu !== undefined&& markerData.loadJieRu !== '' ? Number(markerData.loadJieRu).toFixed(2) + 'kW' : '-'}</div>
						  </div>`,
			    offset: new window.AMap.Pixel(0, -20) // 信息窗口的偏移量
			});

			marker.on('click', () => {
				infoWindow.open(map, marker.getPosition());
				infoWindowRef.current = infoWindow;
			});
			map.on('click', () => {
				infoWindow.close();
			});

			map.add(marker);
			markersRef.current.push(marker);
		});
	};
	useEffect(() => {

		maps()
		return () => {
			map?.destroy();
			// markersRef.current?.off('complete', drawChinaBoundary);
		};
	}, []);
	useEffect(() => {

		if (markerArr.length > 0 && mapInstanceRef.current) {
			addMarkers(mapInstanceRef.current);

		}
	}, [markerArr])
	useEffect(() => {
		console.log(selectCity)
		if (mapInstanceRef.current) {
			if (selectCity) {
				maps()
			}
		}

	}, [selectCity])

	const maps = (cityval, filtetname) => {
		AMapLoader.load({
				key: "b66677117f1e2eec8c44517d9bc0fe14",
				version: "2.0",
				plugins: ["AMap.DistrictSearch", 'AMap.Geocoder', 'AMap.CitySearch', 'AMap.ToolBar'],
			})
			.then((AMap) => {

				map = new AMap.Map("container", {
					zoom: 4.2,
					center: [116.391275, 39.90765],
					mapStyle: "amap://styles/grey",
					logo: false,
					copyright: false,
					resizeEnable: true
				});
				map.panBy(20, -150);
				var opts = {
					subdistrict: 0,
					extensions: "all",
				};
				if (map) {
					const district2 = new AMap.DistrictSearch({
						subdistrict: 0, // 不返回下级行政区
						extensions: 'all', // 返回行政区边界坐标组等具体信息
					});

					district2.search('中国', (status, result) => {
						if (status === 'complete') {
							const bounds = result.districtList[0].boundaries;
							if (bounds) {
								bounds.forEach(boundary => {
									// 创建多边形覆盖物
									const polygon = new AMap.Polygon({
										path: boundary,
										strokeColor: '#6C7587', // 边框颜色
										strokeWeight: 1, // 边框宽度
										fillColor: '#FF0000', // 填充颜色
										fillOpacity: 0 // 填充透明度
									});
									polygon.on('click', function(event) {
										if (infoWindowRef.current) {
											infoWindowRef.current.close();
										}
									});
									polygon.setMap(map); // 添加到地图
								});
							}
						}
					});
				}
				console.log(selectCity)
				if (selectCity !== '') {
					console.log(selectCity)
					const district = new AMap.DistrictSearch(opts);
					district.search(selectCity, function(status, result) {
						console.log(status)
						if (status === 'complete') {
							if (result.districtList.length > 0) {
								var district = result.districtList[0];
								
								map.setZoomAndCenter(10, district.center);
							} else {
								console.log('没有找到指定的城市');
							}
						} else {
							console.log('查询失败');
						}
					})

				}
				var toolBar = new AMap.ToolBar({
					// visible: false,
					position: {
						bottom: '366px',
						left: '16px'
					}
				})
				// 在地图中添加ToolBar
				map.addControl(toolBar);


				mapInstanceRef.current = map;
				// map.on('complete', drawChinaBoundary);

			})
			.catch((e) => {
				console.log(e);
			});
	}

	return ( <div id = "container" className = {styles.container}
		ref={mapRef} > </div>
	);
}