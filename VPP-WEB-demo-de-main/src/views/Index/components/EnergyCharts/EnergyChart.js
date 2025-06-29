import React, { useEffect } from 'react';
import echarts from 'echarts';

const EnergyCharts = (props) => {
  useEffect(() => {
    const chart = echarts.init(document.getElementById('chart'));
    chart.setOption(props.options);
  }, [props.options]);

  return <div id="chart" style={{ width: '100%', height: '400px' }}></div>;
};

export default EnergyCharts;
