// // 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口。
// // import * as echarts from "echarts/core";
// import * as echarts from 'echarts/core';

// // 引入各种图表，图表后缀都为 Chart
// import { BarChart, PieChart,LineChart,RadarChart } from "echarts/charts";  //这里我引用两个类型的图表
// // 引入提示框，标题，直角坐标系等组件，组件后缀都为 Component
// import {
//   TitleComponent,
//   TooltipComponent,
//   GridComponent,
//   LegendComponent,
//   // GeoCoComponent
// } from "echarts/components";
// // 引入 Canvas 渲染器，注意引入 CanvasRenderer 或者 SVGRenderer 是必须的一步
// import { CanvasRenderer } from "echarts/renderers";

// // 注册必须的组件
// echarts.use([
//   TitleComponent,
//   TooltipComponent,
//   GridComponent,
//   LegendComponent,
//   BarChart,
//   PieChart,
//   LineChart,
//   // RadarChart,
//   // ScatterChart,
//   // PictorialBarChart,

//   CanvasRenderer

// ]);
// 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口。
import * as echarts from 'echarts/core';
// 引入柱状图图表，图表后缀都为 Chart
import { BarChart,PieChart,LineChart } from 'echarts/charts';
// 引入标题，提示框，直角坐标系，数据集，内置数据转换器组件，组件后缀都为 Component
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent
} from 'echarts/components';
// 标签自动布局、全局过渡动画等特性
import { LabelLayout, UniversalTransition } from 'echarts/features';
// 引入 Canvas 渲染器，注意引入 CanvasRenderer 或者 SVGRenderer 是必须的一步
import { CanvasRenderer } from 'echarts/renderers';

// 注册必须的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  BarChart,
  LabelLayout,
  UniversalTransition,
  CanvasRenderer
]);
export default echarts;
