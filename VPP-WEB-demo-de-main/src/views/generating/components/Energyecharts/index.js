import './index.scss'
import React, {
	useEffect,
	useRef
} from 'react';
export const Energyecharts = () => {
	const chartRef = useRef(null);

	useEffect(() => {
		// 初始化 ECharts 实例
		
	}, []); // 依赖项为 option，当 option 变化时重新渲染图表

	return (
		<div class="semicircle"></div>
	)
}