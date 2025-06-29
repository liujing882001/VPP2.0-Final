import React, { lazy } from "react";
import "../src/views/Index/index.css";
const Index = lazy(() => import("./views/Index/index.js"));
const Login = lazy(() => import("./views/login/index.js"));
const resource = lazy(() => import("./views/resource/index.js"));
const calculate = lazy(() => import("./views/charge/calculate/index.js"));
const condenser = lazy(() => import("./views/charge/condenser/index.js"));
const electricity = lazy(() => import("./views/charge/electricity/index.js"));
const accumulation = lazy(() => import("./views/charge/accumulation/index.js"));
const manage = lazy(() => import("./views/working/manage/index.js"));
const stored = lazy(() => import("./views/working/stored/index.js"));
const tactics = lazy(() => import("./views/working/tactics/index.js"));
const enabling = lazy(() => import("./views/enabling/index.js"));
const respond = lazy(() => import("./views/respond/index.js"));
const assist = lazy(() => import("./views/assist/index.js"));
const income = lazy(() => import("./views/income/index.js"));
const power = lazy(() => import("./views/power/index.js"));
const source = lazy(() => import("./views/Carbon/source/index.js"));
const peaking = lazy(() => import("./views/Carbon/peaking/index.js"));
const patterna = lazy(() => import("./views/Carbon/carbon/pattern/index.js")); //碳资产-碳模型
const skin = lazy(() => import("./views/Carbon/skin/index.js"));
const police = lazy(() => import("./views/police/index.js"));
const subscriber = lazy(() => import("./views/system/subscriber/index.js"));
const hybrid = lazy(() => import("./views/system/hybrid/index.js"));
const systematics = lazy(() => import("./views/system/systematics/index.js"));
const equipment = lazy(() => import("./views/system/equipment/index.js"));
const personnel = lazy(() => import("./views/system/personnel/personnel.js"));
const parameter = lazy(() => import("./views/system/parameter/index.js"));
// const parameter = lazy(() => import('../system/parameter/index.js'));
const accumulation_model = lazy(() =>
  import("./views/system/model/accumulation_model/index.js")
); //储能模型
const Photovoltaic_model = lazy(() =>
  import("./views/system/model/Photovoltaic_model/index.js")
); //光伏
const menu_management = lazy(() =>
  import("./views/system/user/menu_management/index.js")
);
const role = lazy(() => import("./views/system/user/role/index.js"));
const Operating = lazy(() => import("./views/working/Operating/index.js"));
const Energy = lazy(() => import("./views/working/Energy/index.js"));
const ServiceTask = lazy(() => import("./views/assist/ServiceTask/index.js")); //服务任务
const Responsetask = lazy(() =>
  import("./views/respond/Responsetask/index.js")
); //需求响应-响应任务
const modelset = lazy(() => import("./views/working/modelset/index.js"));
const Rspondboard = lazy(() => import("./views/respond/Rspondboard/index.js")); //需求响应-响应看板
const chronicle = lazy(() => import("./views/respond/chronicle/index.js")); //需求响应-历史记录
const Detection = lazy(() => import("./views/respond/Detection/index.js"));
const storage_model = lazy(() =>
  import("./views/system/model/storage_model/index.js")
);
const Monitoring = lazy(() => import("./views/respond/Monitoring/index.js")); //辅助服务-实施监测
const History = lazy(() => import("./views/respond/History/index.js"));
const HtmlPage = lazy(() => import("./views/big/HtmlPage.js"));
    // 产投版本中国地图路由（可选配置）
const Generating = lazy(() => import("./views/generating/index.js"));
// const Generating = lazy(() => import('./views/generating-china/index.js'));
// const Generating = lazy(() => import('./views/generating-chantou/index.js'));
// const Generating = lazy(() => import("./views/generating/index-产投.js"));
const Loadgenerating = lazy(() => import("./views/loadgenerating/index.js"));
const Tactful = lazy(() =>
  import("./views/respond/Responsetask/tactful/index.js")
); //响应任务
const Ploy = lazy(() => import("./views/respond/Responsetask/ploy/index.js"));
const Detail = lazy(() => import("./views/respond/Detection/detail.js"));
const Historydetail = lazy(() => import("./views/respond/History/detail.js"));
const Strategy = lazy(() =>
  import("./views/working/tactics/strategy/index.js")
);
const Period = lazy(() => import("./views/working/tactics/period/index.js"));
const Tdetail = lazy(() => import("./views/working/tactics/detail/index.js"));
// const telegram = lazy(() => import('../system/model/accumulation_model/telegram/index.js'));
const Area = lazy(() => import("./views/Carbon/skin/area/index.js")); //种植树木
const Footmark = React.lazy(() => import("./views/Carbon/footmark/index.js"));
const dropdown = React.lazy(() =>
  import("./views/Carbon/carbon/dropdown/index.js")
);
const Pattern = React.lazy(() =>
  import("./views/Carbon/carbon/pattern/index.js")
);
const EnergyStorage = React.lazy(() =>
  import("./views/system/model/accumulation_model/EnergyStorage.js")
); //储能模型
const Sarguments = React.lazy(() =>
  import("./views/system/Sarguments/index.js")
); //系统参数
const property = React.lazy(() =>
  import("./views/Carbon/neutralization/index.js")
); //碳中和
const newDate = React.lazy(() =>
  import("./views/system/Sarguments/newDate.js")
); //系统参数
const Easy = React.lazy(() => import("./views/linkref/index.js"));
const Responsdeatil = React.lazy(() =>
  import("./views/respond/Responsetask/Responsdeatil/index.js")
);
const demandForecast = React.lazy(() =>
  import("./views/Forecasting/demandForecast/index.js")
); //需求预测
const AllNodePage = React.lazy(() =>
  import("./views/Forecasting/demandForecast/component/all-node-page/index.js")
);
const Spotforecast = React.lazy(() =>
  import("./views/Forecasting/Spotforecast/index.js")
); //现货预测
const LoadForecasting = React.lazy(() =>
  import("./views/Forecasting/LoadForecasting/index.js")
); //负荷预测
const Task = React.lazy(() =>
  import("./views/respond/Responsetask/task/index.js")
); //响应任务
const Strategy_content = React.lazy(() =>
  import("./views/respond/Responsetask/strategy_content/index.js")
);
const Responsebenefits = React.lazy(() =>
  import("./views/income/Responsebenefits/index.js")
); //响应收益
// import { AssistMode } from './components/assist-mode'
const AssistMode = React.lazy(() =>
  import("./views/Index/components/assist-mode")
); //响应收益
const footmark = React.lazy(() => import("./views/Carbon/footmark/index.js"));
const HybridElectricityPrice = React.lazy(() =>
  import("./views/system/hybrid/hybridElectricityPrice/index.js")
);
// const Detail = React.lazy(() => import('../respond/Detection/detail.js'	))
// const AssistMode
export const routerMap = [
  { path: "/", ename: "Login", component: Login, auth: true },
  { path: "/index", name: "index", component: Index },
  { path: "/resource", name: "resource", component: resource },
  { path: "/calculate", name: "calculate", component: calculate },
  { path: "/condenser", name: "condenser", component: condenser },
  { path: "/electricity", name: "electricity", component: electricity },
  { path: "/accumulation", name: "accumulation", component: accumulation },
  { path: "/manage", name: "manage", component: manage },
  { path: "/stored", name: "stored", component: stored },
  { path: "/tactics", name: "tactics", component: tactics },
  { path: "/enabling", name: "enabling", component: enabling },
  { path: "/respond", name: "respond", component: respond },
  { path: "/assist", name: "assist", component: assist },
  { path: "/income", name: "income", component: income },
  { path: "/power", name: "power", component: power },
  { path: "/source", name: "source", component: source },
  { path: "/peaking", name: "peaking", component: peaking },
  { path: "/patterna", name: "patterna", component: patterna },
  { path: "/skin", name: "skin", component: skin },
  { path: "/police", name: "police", component: police },
  { path: "/subscriber", name: "subscriber", component: subscriber },
  { path: "/hybrid", name: "hybrid", component: hybrid },
  {
    path: "/HybridElectricityPrice",
    name: "HybridElectricityPrice",
    component: HybridElectricityPrice,
  },

  { path: "/systematics", name: "systematics", component: systematics },
  { path: "/equipment", name: "equipment", component: equipment },
  { path: "/personnel", name: "personnel", component: personnel },
  { path: "/parameter", name: "parameter", component: parameter },
  {
    path: "/accumulation_model",
    name: "accumulation_model",
    component: accumulation_model,
  },
  {
    path: "/Photovoltaic_model",
    name: "Photovoltaic_model",
    component: Photovoltaic_model,
  },
  {
    path: "/menu_management",
    name: "menu_management",
    component: menu_management,
  },
  { path: "/role", name: "role", component: role },
  { path: "/Operating", name: "Operating", component: Operating },
  { path: "/Energy", name: "Energy", component: Energy },
  { path: "/ServiceTask", name: "ServiceTask", component: ServiceTask },
  { path: "/Responsetask", name: "Responsetask", component: Responsetask },
  { path: "/modelset", name: "modelset", component: modelset },
  { path: "/Rspondboard", name: "Rspondboard", component: Rspondboard },
  { path: "/chronicle", name: "chronicle", component: chronicle },
  { path: "/Detection", name: "Detection", component: Detection },
  { path: "/storage_model", name: "storage_model", component: storage_model },
  { path: "/Monitoring", name: "Monitoring", component: Monitoring },
  { path: "/History", name: "History", component: History },
  { path: "/HtmlPage", name: "HtmlPage", component: HtmlPage },
  { path: "/generating", name: "generating", component: Generating },
  {
    path: "/loadgenerating",
    name: "loadgenerating",
    component: Loadgenerating,
  },
  { path: "/Tactful", name: "Tactful", component: Tactful },
  { path: "/Ploy", name: "Ploy", component: Ploy },
  { path: "/detail", name: "detail", component: Detail },
  { path: "/Historydetail", name: "Historydetail", component: Historydetail },
  { path: "/strategy", name: "Strategy", component: Strategy },
  { path: "/period", name: "Period", component: Period },
  { path: "/Tdetail", name: "Tdetail", component: Tdetail },
  { path: "/area", name: "area", component: Area },
  { path: "/Footmark", name: "Footmark", component: Footmark },
  { path: "/dropdown", name: "dropdown", component: dropdown },
  { path: "/Pattern", name: "Pattern", component: Pattern },
  { path: "/EnergyStorage", name: "EnergyStorage", component: EnergyStorage },
  { path: "/Sarguments", name: "Sarguments", component: Sarguments },
  { path: "/property", name: "property", component: property },
  { path: "/newDate", name: "newDate", component: newDate },
  { path: "/Easy", name: "Easy", component: Easy },
  { path: "/Responsdeatil", name: "Responsdeatil", component: Responsdeatil },
  {
    path: "/demandForecast",
    name: "demandForecast",
    component: demandForecast,
  },
  { path: "/AllNodePage", name: "AllNodePage", component: AllNodePage },
  { path: "/Spotforecast", name: "Spotforecast", component: Spotforecast },
  {
    path: "/LoadForecasting",
    name: "LoadForecasting",
    component: LoadForecasting,
  },
  { path: "/Task", name: "Task", component: Task },
  {
    path: "/Strategy_content",
    name: "Strategy_content",
    component: Strategy_content,
  },
  {
    path: "/Responsebenefits",
    name: "Responsebenefits",
    component: Responsebenefits,
  },
  { path: "/AssistMode", name: "AssistMode", component: AssistMode },
  { path: "/peaking", name: "peaking", component: peaking },
  { path: "/footmark", name: "footmark", component: footmark },
];
// export const routerMap = [
// 	   // {path: "/", ename: "Login", component: Login, auth: true},
//     {path: "/index", name: "index", component: Index},
// 	// accumulation_model
//     // {path: "/", ename: "Login", component: Login, auth: true},
// 	 // {path: "/resource", name: "resource", component: resource},
// ];
