import React from 'react';
import echarts from 'echarts';

const Chart = ({ data }) => {
	console.log(data,'5555555555555555')
  return (
    <div>
      {
	  // console.log(chartData,'chartDatachartDatachartDatachartDatachartData')
	  // console.log(index,'index,index,index,index,index,index,index,index,')
        <div>
          <h2>{data.title}</h2>
          <echarts option={data.option} />
          
        </div>
      // )
	  
	  }
    </div>
  );
};

export default Chart;
