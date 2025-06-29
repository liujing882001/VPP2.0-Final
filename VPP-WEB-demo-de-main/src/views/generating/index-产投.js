import React,{useEffect,useState,useRef } from 'react'
import  { Select ,DatePicker,Segmented,Tree  } from 'antd';
import Icon from '@ant-design/icons';

import './index.scss'
import myStyleJson from './json/custom_map_config.json'
import zhisuan from './img/zhisuan.svg'
import jiaotong from './img/jiaotong.svg'
import zhizao from './img/zhizao.svg'
import louyu from './img/louyu.svg'
import pv from './img/pv.svg'
import wind from './img/wind.svg'
import bottom from './img/bottom.svg'
import top from './img/top.svg'
import {Energyblock} from './components/Energyblock/index.js'
import {Incomeblock} from './components/Incomeblock/index.js'
import {Energyecharts}  from './components/Energyecharts/index.js'
import {Loadecharts} from './components/loadecharts/index'
import {MapContainer} from './components/MapContainer/MapContainer.js'
import http from '../../server/server.js'
import dayjs from 'dayjs';
const BMapURL = 'https://api.map.baidu.com/api?v=2.0&type=webgl&ak=N6wH5jFd6P3h82VpvutGnO9MwuL6hBqK';
const { RangePicker } = DatePicker;
type CustomIconComponentProps = GetProps<typeof Icon>;

const Generating = () =>{
	const mapRef = useRef(null);
	const [drawnlineindex,seDrawnlineindex]  = useState(2);
	const [index,setIndex] = useState(0)
	const [treeData,setTreeData] = useState([])
	const [nodeTypeList, setNodeTypeList] = useState([]);
	const [isVisible, setIsVisible] = useState(false);
	const [nodeIds,setNodeIds] = useState([])
	const [loadCount,setLoadCount] = useState('-')
	const [loadJieRu,setLoadJieRu] = useState('-')
	const [loadKeTiao,setLoadKeTiao] = useState('-')
	const [storageEnergyCount,setStorageEnergyCount] = useState('-')
	const [storageEnergyCapacity,setStorageEnergyCapacity] = useState('-')
	const [storageEnergyPower,setStorageEnergyPower] = useState('-')
	const [windCount,setWindCount] = useState('-')
	const [windCapacity,setWindCapacity] = useState('-')
	const [pvCount,setPvCount] = useState('-')
	const [pvCapacity,setPvCapacity] = useState('-')
	const [dataList,setDataList] = useState([])
	const [nodeId,setNodeId] = useState('')
	const [selectval,setSelectval] = useState('')
	const [treeDataList,setTreeDataList] = useState([])
	const [filtetname,setFiltetname] = useState('全部')
	const [revenueindex,setRevenueIndex] = useState(1)
	const [endTs,setEndTs] = useState(dayjs().subtract(1, 'days'));
	const [startTs,setStartTs] = useState(dayjs().subtract(1, 'days'));
	const [energylist,setEnergylist ] = useState([])
	const [energyval,setEnergyval] = useState('')
	const [incomeval,setIncomeval] = useState('')
	const [maplist,setMaplist ] = useState([]);
	const [expandedKeys, setExpandedKeys] = useState([]);
	const [selectedKeys,setSelectedKeys] = useState(['1000012']);
	const [cityval,setCityval] = useState('')
	const [selectCity,setSelectCity] = useState('福建省')
	const [chinaList,setChinaList] = useState([]);
	const [infoNode,setInfoNode] = useState(null);
	const [cityname,setCityname] = useState(null)
	const [isoperation,setIsoperation] = useState(false)
	const [isIncomeblock,setIsIncomeblock] = useState(false)
	const HeartSvg = () => (
	  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
	    <title>heart icon</title>
		<path d="M71.03 962.129h19.633V441.337l20.783-7.28 228.429-80.063V209.981l17.791-8.384L741.004 20.816l44.16-20.8v8.528l120.798 72.015v877.65h47.007v61.903H723.23V97.47L401.778 249.084v83.23l93.006-32.607L530.8 287.1l0.032-0.976 0.976 0.608 4.08-1.456v3.871l120.11 71.6V956.32h47.727v61.92H474.017V372.361l-321.5 112.67V1024H71.032v-61.871z m117.471 53.935h238.14V907.218c-45.103 2.944-87.806 5.744-128.27 8.416-38.463 2.48-75.07 4.88-109.87 7.168v93.262z m0-402.938c34.8-9.823 71.407-20.24 109.87-31.055a82813.447 82813.447 0 0 1 128.27-36.304V436.761c-45.103 15.696-87.806 30.592-128.27 44.687-38.463 13.408-75.07 26.128-109.87 38.272v93.406z m0 133.086l109.87-20.815 128.27-24.304V592.087c-45.103 11.52-87.806 22.383-128.27 32.703-38.463 9.824-75.07 19.136-109.87 28.048v93.374z m0 133.982c34.8-3.296 71.407-6.768 109.87-10.464 40.448-3.824 83.151-7.903 128.27-12.207V748.596l-128.27 20.656c-38.463 6.16-75.07 12.048-109.87 17.648v93.294z" ></path>	  </svg>
	);
	const HeartIcon = (props: Partial<CustomIconComponentProps>) => (
	  <Icon component={HeartSvg} {...props} />
	);
	const systemSvg = () => (
	  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
	    <title>heart icon</title>
		<path d="M1,2.75Q1,2.66403,1.00842673,2.5784700000000003Q1.0168535,2.49291,1.0336258,2.4085900000000002Q1.0503981,2.3242700000000003,1.0753544,2.242Q1.100311,2.1597299999999997,1.133211,2.0803000000000003Q1.166111,2.00088,1.2066379999999999,1.925056Q1.247165,1.849235,1.294928,1.777752Q1.342692,1.706269,1.397232,1.639812Q1.451772,1.5733540000000001,1.512563,1.512563Q1.5733540000000001,1.451772,1.639812,1.397232Q1.706269,1.342692,1.777752,1.294928Q1.849235,1.247165,1.925056,1.2066379999999999Q2.00088,1.166111,2.0803000000000003,1.133211Q2.1597299999999997,1.100311,2.242,1.0753544Q2.3242700000000003,1.0503981,2.4085900000000002,1.0336258Q2.49291,1.0168535,2.5784700000000003,1.00842673Q2.66403,1,2.75,1L12.25,1Q12.336,1,12.4215,1.00842673Q12.5071,1.0168535,12.5914,1.0336258Q12.6757,1.0503981,12.758,1.0753544Q12.8403,1.100311,12.9197,1.133211Q12.9991,1.166111,13.0749,1.2066379999999999Q13.1508,1.247165,13.2222,1.294928Q13.2937,1.342692,13.3602,1.397232Q13.4266,1.451772,13.4874,1.512563Q13.5482,1.5733540000000001,13.6028,1.639812Q13.6573,1.706269,13.7051,1.777752Q13.7528,1.849235,13.7934,1.925056Q13.8339,2.00088,13.8668,2.0803000000000003Q13.8997,2.1597299999999997,13.9246,2.242Q13.9496,2.3242700000000003,13.9664,2.4085900000000002Q13.9831,2.49291,13.9916,2.5784700000000003Q14,2.66403,14,2.75Q14,2.83597,13.9916,2.9215299999999997Q13.9831,3.00709,13.9664,3.09141Q13.9496,3.17573,13.9246,3.258Q13.8997,3.34027,13.8668,3.4197Q13.8339,3.49912,13.7934,3.57494Q13.7528,3.65076,13.7051,3.72225Q13.6573,3.79373,13.6028,3.86019Q13.5482,3.92665,13.4874,3.98744Q13.4266,4.04823,13.3602,4.10277Q13.2937,4.15731,13.2222,4.20507Q13.1508,4.25284,13.0749,4.29336Q12.9991,4.33389,12.9197,4.36679Q12.8403,4.39969,12.758,4.42465Q12.6757,4.4496,12.5914,4.4663699999999995Q12.5071,4.48315,12.4215,4.491569999999999Q12.336,4.5,12.25,4.5L2.75,4.5Q2.66403,4.5,2.5784700000000003,4.491569999999999Q2.49291,4.48315,2.4085900000000002,4.4663699999999995Q2.3242700000000003,4.4496,2.242,4.42465Q2.1597299999999997,4.39969,2.0803000000000003,4.36679Q2.00088,4.33389,1.925056,4.29336Q1.849235,4.25284,1.777752,4.20507Q1.706269,4.15731,1.639812,4.10277Q1.5733540000000001,4.04823,1.512563,3.98744Q1.451772,3.92665,1.397232,3.86019Q1.342692,3.79373,1.294928,3.72225Q1.247165,3.65076,1.2066379999999999,3.57494Q1.166111,3.49912,1.133211,3.4197Q1.100311,3.34027,1.0753544,3.258Q1.0503981,3.17573,1.0336258,3.09141Q1.0168535,3.00709,1.00842673,2.9215299999999997Q1,2.83597,1,2.75ZM3.16406,2.70703Q3.16406,2.7562800000000003,3.15445,2.80458Q3.14485,2.85288,3.126,2.89837Q3.10716,2.94387,3.0798,2.98482Q3.05244,3.02576,3.01762,3.06058Q2.98279,3.09541,2.94185,3.12277Q2.9009,3.15013,2.8554,3.16897Q2.80991,3.18782,2.76161,3.19742Q2.71331,3.20703,2.66406,3.20703Q2.61482,3.20703,2.5665199999999997,3.19742Q2.51822,3.18782,2.47272,3.16897Q2.42722,3.15013,2.38628,3.12277Q2.3453299999999997,3.09541,2.31051,3.06058Q2.27569,3.02576,2.24833,2.98482Q2.2209700000000003,2.94387,2.20212,2.89837Q2.18328,2.85288,2.17367,2.80458Q2.16406,2.7562800000000003,2.16406,2.70703Q2.16406,2.6577900000000003,2.17367,2.60949Q2.18328,2.56119,2.20212,2.51569Q2.2209700000000003,2.47019,2.24833,2.4292499999999997Q2.27569,2.3883,2.31051,2.3534800000000002Q2.3453299999999997,2.31866,2.38628,2.2912999999999997Q2.42722,2.26394,2.47272,2.2450900000000003Q2.51822,2.2262500000000003,2.5665199999999997,2.21664Q2.61482,2.20703,2.66406,2.20703Q2.71331,2.20703,2.76161,2.21664Q2.80991,2.2262500000000003,2.8554,2.2450900000000003Q2.9009,2.26394,2.94185,2.2912999999999997Q2.98279,2.31866,3.01762,2.3534800000000002Q3.05244,2.3883,3.0798,2.4292499999999997Q3.10716,2.47019,3.126,2.51569Q3.14485,2.56119,3.15445,2.60949Q3.16406,2.6577900000000003,3.16406,2.70703ZM4.99805,3.20703Q5.04729,3.20703,5.09559,3.19742Q5.14389,3.18782,5.18939,3.16897Q5.23489,3.15013,5.27583,3.12277Q5.31678,3.09541,5.3516,3.06058Q5.38642,3.02576,5.41378,2.98482Q5.44114,2.94387,5.45999,2.89837Q5.47883,2.85288,5.48844,2.80458Q5.49805,2.7562800000000003,5.49805,2.70703Q5.49805,2.6577900000000003,5.48844,2.60949Q5.47883,2.56119,5.45999,2.51569Q5.44114,2.47019,5.41378,2.4292499999999997Q5.38642,2.3883,5.3516,2.3534800000000002Q5.31678,2.31866,5.27583,2.2912999999999997Q5.23488,2.26394,5.18939,2.2450900000000003Q5.14389,2.2262500000000003,5.09559,2.21664Q5.04729,2.20703,4.99805,2.20703Q4.9488,2.20703,4.9005,2.21664Q4.8522,2.2262500000000003,4.8067,2.2450900000000003Q4.76121,2.26394,4.72026,2.2912999999999997Q4.679320000000001,2.31866,4.644489999999999,2.3534800000000002Q4.6096699999999995,2.3883,4.58231,2.4292499999999997Q4.55495,2.47019,4.53611,2.51569Q4.51726,2.56119,4.50765,2.60949Q4.49805,2.6577900000000003,4.49805,2.70703Q4.49805,2.7562800000000003,4.50765,2.80458Q4.51726,2.85288,4.53611,2.89837Q4.55495,2.94387,4.58231,2.98482Q4.6096699999999995,3.02576,4.644489999999999,3.06058Q4.679320000000001,3.09541,4.72026,3.12277Q4.76121,3.15013,4.8067,3.16897Q4.8522,3.18782,4.9005,3.19742Q4.9488,3.20703,4.99805,3.20703ZM6.59961,7.7599C6.59961,7.39419,6.79935,7.05986,7.11554,6.89631C7.98443,6.44689,8.85744,6.18668,9.64673,6L2.75,6Q2.66403,6,2.5784700000000003,6.00843Q2.49291,6.01685,2.4085900000000002,6.03363Q2.3242700000000003,6.0504,2.242,6.07535Q2.1597299999999997,6.10031,2.0803000000000003,6.13321Q2.00088,6.16611,1.925056,6.20664Q1.849235,6.24716,1.777752,6.29493Q1.706269,6.34269,1.639812,6.39723Q1.5733540000000001,6.45177,1.512563,6.51256Q1.451772,6.57335,1.397232,6.63981Q1.342692,6.70627,1.294928,6.77775Q1.247165,6.84924,1.2066379999999999,6.92506Q1.166111,7.00088,1.133211,7.0803Q1.100311,7.15973,1.0753544,7.242Q1.0503981,7.32427,1.0336258,7.40859Q1.0168535,7.49291,1.00842673,7.57847Q1,7.66403,1,7.75Q1,7.83597,1.00842673,7.92153Q1.0168535,8.00709,1.0336258,8.09141Q1.0503981,8.17573,1.0753544,8.258Q1.100311,8.34027,1.133211,8.41969Q1.166111,8.49912,1.2066379999999999,8.57494Q1.247165,8.65076,1.294928,8.722249999999999Q1.342692,8.79373,1.397232,8.86019Q1.451772,8.92665,1.512563,8.98744Q1.5733540000000001,9.04823,1.639812,9.10277Q1.706269,9.15731,1.777752,9.20507Q1.849235,9.25284,1.925056,9.29336Q2.00088,9.33389,2.0803000000000003,9.36679Q2.1597299999999997,9.39969,2.242,9.42464Q2.3242700000000003,9.4496,2.4085900000000002,9.46637Q2.49291,9.48315,2.5784700000000003,9.49157Q2.66403,9.5,2.75,9.5L6.59961,9.5L6.59961,7.7599ZM7.5332,7.75849L7.5332,9.92969L10.79987,9.92969L10.79987,6.79297Q9.39987,6.79297,7.5332,7.75849ZM15,7.75849L15,9.92969L11.7333,9.92969L11.7333,6.79297Q13.1333,6.79297,15,7.75849ZM3.16406,7.70703Q3.16406,7.75628,3.15445,7.80458Q3.14485,7.85288,3.126,7.89837Q3.10716,7.94387,3.0798,7.98482Q3.05244,8.02576,3.01762,8.06058Q2.98279,8.095410000000001,2.94185,8.12276Q2.9009,8.150120000000001,2.8554,8.16897Q2.80991,8.18782,2.76161,8.197420000000001Q2.71331,8.20703,2.66406,8.20703Q2.61482,8.20703,2.5665199999999997,8.197420000000001Q2.51822,8.18782,2.47272,8.16897Q2.42722,8.150120000000001,2.38628,8.12276Q2.3453299999999997,8.095410000000001,2.31051,8.06058Q2.27569,8.02576,2.24833,7.98482Q2.2209700000000003,7.94387,2.20212,7.89837Q2.18328,7.85288,2.17367,7.80458Q2.16406,7.75628,2.16406,7.70703Q2.16406,7.65778,2.17367,7.60949Q2.18328,7.56119,2.20212,7.51569Q2.2209700000000003,7.47019,2.24833,7.42925Q2.27569,7.3883,2.31051,7.35348Q2.3453299999999997,7.31866,2.38628,7.2913Q2.42722,7.26394,2.47272,7.24509Q2.51822,7.22625,2.5665199999999997,7.21664Q2.61482,7.20703,2.66406,7.20703Q2.71331,7.20703,2.76161,7.21664Q2.80991,7.22625,2.8554,7.24509Q2.9009,7.26394,2.94185,7.2913Q2.98279,7.31866,3.01762,7.35348Q3.05244,7.3883,3.0798,7.42925Q3.10716,7.47019,3.126,7.51569Q3.14485,7.56119,3.15445,7.60949Q3.16406,7.65778,3.16406,7.70703ZM4.99805,8.20703Q5.04729,8.20703,5.09559,8.197420000000001Q5.14389,8.18782,5.18939,8.16897Q5.23489,8.150120000000001,5.27583,8.12276Q5.31678,8.095410000000001,5.3516,8.06058Q5.38642,8.02576,5.41378,7.98482Q5.44114,7.94387,5.45999,7.89837Q5.47883,7.85288,5.48844,7.80458Q5.49805,7.75628,5.49805,7.70703Q5.49805,7.65778,5.48844,7.60949Q5.47883,7.56119,5.45999,7.51569Q5.44114,7.47019,5.41378,7.42925Q5.38642,7.3883,5.3516,7.35348Q5.31678,7.31866,5.27583,7.2913Q5.23488,7.26394,5.18939,7.24509Q5.14389,7.22625,5.09559,7.21664Q5.04729,7.20703,4.99805,7.20703Q4.9488,7.20703,4.9005,7.21664Q4.8522,7.22625,4.8067,7.24509Q4.76121,7.26394,4.72026,7.2913Q4.679320000000001,7.31866,4.644489999999999,7.35348Q4.6096699999999995,7.3883,4.58231,7.42925Q4.55495,7.47019,4.53611,7.51569Q4.51726,7.56119,4.50765,7.60949Q4.49805,7.65778,4.49805,7.70703Q4.49805,7.75628,4.50765,7.80458Q4.51726,7.85288,4.53611,7.89837Q4.55495,7.94387,4.58231,7.98482Q4.6096699999999995,8.02576,4.644489999999999,8.06058Q4.679320000000001,8.095410000000001,4.72026,8.12276Q4.76121,8.150120000000001,4.8067,8.16897Q4.8522,8.18782,4.9005,8.197420000000001Q4.9488,8.20703,4.99805,8.20703ZM7.5332,11.6206L7.5332,10.8952L10.79987,10.8952L10.79987,14.9999Q7.5332,13.0688,7.5332,11.6206ZM15,11.6206L15,10.8952L11.7333,10.8952L11.7333,14.9999Q15,13.0688,15,11.6206ZM6.59961,11.622L6.59961,11L2.75,11Q2.66403,11,2.5784700000000003,11.0084Q2.49291,11.0169,2.4085900000000002,11.0336Q2.3242700000000003,11.0504,2.242,11.0754Q2.1597299999999997,11.1003,2.0803000000000003,11.1332Q2.00088,11.1661,1.925056,11.2066Q1.849235,11.2472,1.777752,11.2949Q1.706269,11.3427,1.639812,11.3972Q1.5733540000000001,11.4518,1.512563,11.5126Q1.451772,11.5734,1.397232,11.6398Q1.342692,11.7063,1.294928,11.7778Q1.247165,11.8492,1.2066379999999999,11.9251Q1.166111,12.0009,1.133211,12.0803Q1.100311,12.1597,1.0753544,12.242Q1.0503981,12.3243,1.0336258,12.4086Q1.0168535,12.4929,1.00842673,12.5785Q1,12.664,1,12.75Q1,12.836,1.00842673,12.9215Q1.0168535,13.0071,1.0336258,13.0914Q1.0503981,13.1757,1.0753544,13.258Q1.100311,13.3403,1.133211,13.4197Q1.166111,13.4991,1.2066379999999999,13.5749Q1.247165,13.6508,1.294928,13.7222Q1.342692,13.7937,1.397232,13.8602Q1.451772,13.9266,1.512563,13.9874Q1.5733540000000001,14.0482,1.639812,14.1028Q1.706269,14.1573,1.777752,14.2051Q1.849235,14.2528,1.925056,14.2934Q2.00088,14.3339,2.0803000000000003,14.3668Q2.1597299999999997,14.3997,2.242,14.4246Q2.3242700000000003,14.4496,2.4085900000000002,14.4664Q2.49291,14.4831,2.5784700000000003,14.4916Q2.66403,14.5,2.75,14.5L8.468340000000001,14.5Q8.08836,14.1731,7.7422,13.8033Q6.59961,12.7902,6.59961,11.622ZM3.16406,12.707Q3.16406,12.7563,3.15445,12.8046Q3.14485,12.8529,3.126,12.8984Q3.10716,12.9439,3.0798,12.9848Q3.05244,13.0258,3.01762,13.0606Q2.98279,13.0954,2.94185,13.1228Q2.9009,13.1501,2.8554,13.169Q2.80991,13.1878,2.76161,13.1974Q2.71331,13.207,2.66406,13.207Q2.61482,13.207,2.5665199999999997,13.1974Q2.51822,13.1878,2.47272,13.169Q2.42722,13.1501,2.38628,13.1228Q2.3453299999999997,13.0954,2.31051,13.0606Q2.27569,13.0258,2.24833,12.9848Q2.2209700000000003,12.9439,2.20212,12.8984Q2.18328,12.8529,2.17367,12.8046Q2.16406,12.7563,2.16406,12.707Q2.16406,12.6578,2.17367,12.6095Q2.18328,12.5612,2.20212,12.5157Q2.2209700000000003,12.4702,2.24833,12.4292Q2.27569,12.3883,2.31051,12.3535Q2.3453299999999997,12.3187,2.38628,12.2913Q2.42722,12.2639,2.47272,12.2451Q2.51822,12.2262,2.5665199999999997,12.2166Q2.61482,12.207,2.66406,12.207Q2.71331,12.207,2.76161,12.2166Q2.80991,12.2262,2.8554,12.2451Q2.9009,12.2639,2.94185,12.2913Q2.98279,12.3187,3.01762,12.3535Q3.05244,12.3883,3.0798,12.4292Q3.10716,12.4702,3.126,12.5157Q3.14485,12.5612,3.15445,12.6095Q3.16406,12.6578,3.16406,12.707ZM4.99805,13.207Q5.04729,13.207,5.09559,13.1974Q5.14389,13.1878,5.18939,13.169Q5.23489,13.1501,5.27583,13.1228Q5.31678,13.0954,5.3516,13.0606Q5.38642,13.0258,5.41378,12.9848Q5.44114,12.9439,5.45999,12.8984Q5.47883,12.8529,5.48844,12.8046Q5.49805,12.7563,5.49805,12.707Q5.49805,12.6578,5.48844,12.6095Q5.47883,12.5612,5.45999,12.5157Q5.44114,12.4702,5.41378,12.4292Q5.38642,12.3883,5.3516,12.3535Q5.31678,12.3187,5.27583,12.2913Q5.23488,12.2639,5.18939,12.2451Q5.14389,12.2262,5.09559,12.2166Q5.04729,12.207,4.99805,12.207Q4.9488,12.207,4.9005,12.2166Q4.8522,12.2262,4.8067,12.2451Q4.76121,12.2639,4.72026,12.2913Q4.679320000000001,12.3187,4.644489999999999,12.3535Q4.6096699999999995,12.3883,4.58231,12.4292Q4.55495,12.4702,4.53611,12.5157Q4.51726,12.5612,4.50765,12.6095Q4.49805,12.6578,4.49805,12.707Q4.49805,12.7563,4.50765,12.8046Q4.51726,12.8529,4.53611,12.8984Q4.55495,12.9439,4.58231,12.9848Q4.6096699999999995,13.0258,4.644489999999999,13.0606Q4.679320000000001,13.0954,4.72026,13.1228Q4.76121,13.1501,4.8067,13.169Q4.8522,13.1878,4.9005,13.1974Q4.9488,13.207,4.99805,13.207Z" fill-rule="evenodd" fill-opacity="1"></path>
	</svg>
	);
	const SystemIcon = (props: Partial<CustomIconComponentProps>) => (
	  <Icon component={systemSvg} {...props} />
	);
	const zixuanSvg = () => (
	  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
	    <title>heart icon</title>
		<path d="M64 176C64 114.176 114.176 64 176 64h608a112 112 0 1 1 0 224h-608A112 112 0 0 1 64 176z m138.688-2.752a32 32 0 1 1-64 0 32 32 0 0 1 64 0z m117.312 32a32 32 0 1 0 0-64 32 32 0 0 0 0 64z m102.4 291.328c0-23.424 12.8-44.8 33.024-55.296 55.488-28.672 111.232-45.312 161.664-57.28H176a112 112 0 1 0 0 224H422.4V496.576z m59.712 0V635.52H691.2V434.752q-89.6 0-209.088 61.824z m477.888 0V635.52h-209.088V434.752q89.6 0 209.088 61.824zM202.688 493.248a32 32 0 1 1-64 0 32 32 0 0 1 64 0z m117.312 32a32 32 0 1 0 0-64 32 32 0 0 0 0 64z m162.112 218.496v-46.336H691.2V960q-209.088-123.584-209.088-216.32z m477.888 0v-46.336h-209.088V960Q960 836.416 960 743.68z m-537.6 0V704H176a112 112 0 1 0 0 224h366.08q-24.32-20.928-46.528-44.672Q422.4 818.56 422.4 743.744z m-219.712 69.504a32 32 0 1 1-64 0 32 32 0 0 1 64 0z m117.312 32a32 32 0 1 0 0-64 32 32 0 0 0 0 64z" p-id="1197"></path>
	</svg>
	);
	const ZixuanIcon = (props: Partial<CustomIconComponentProps>) => (
	  <Icon component={zixuanSvg} {...props} />
	);
	const jiaotongSvg = () => (
	  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
	    <title>heart icon</title>
		<path d="M64 832V464h32a48 48 0 0 0 0-96H64V192q0-6.272 0.64-12.544t1.792-12.416q1.28-6.208 3.072-12.16 1.856-6.08 4.224-11.84 2.432-5.824 5.376-11.392 2.944-5.504 6.464-10.752 3.52-5.248 7.488-10.112 3.968-4.864 8.448-9.28 4.48-4.48 9.28-8.448 4.864-3.968 10.112-7.488 5.248-3.52 10.752-6.4 5.568-3.008 11.392-5.44 5.76-2.368 11.776-4.224 6.016-1.792 12.16-3.072 6.208-1.216 12.48-1.792Q185.728 64 192 64h320q6.272 0 12.544 0.64t12.416 1.792q6.208 1.28 12.16 3.072 6.08 1.856 11.84 4.224 5.824 2.432 11.392 5.376 5.504 2.944 10.752 6.464 5.248 3.52 10.112 7.488 4.864 3.968 9.28 8.448 4.48 4.48 8.448 9.28 3.968 4.864 7.488 10.112 3.52 5.248 6.4 10.752 3.008 5.568 5.44 11.392 2.368 5.76 4.224 11.776 1.792 6.016 3.072 12.16 1.216 6.208 1.792 12.48 0.64 6.272 0.64 12.544v176h-32a48 48 0 0 0 0 96h32v288h128q16 0 16-16V374.72A128 128 0 0 1 704 256V160a48 48 0 0 1 96 0V192h64v-32a48 48 0 0 1 96 0V256a128 128 0 0 1-80 118.72V736q0 46.4-32.832 79.168-32.768 32.832-79.168 32.832H638.976q-0.704 5.76-1.92 11.392-1.28 5.632-3.008 11.136-1.728 5.568-3.968 10.88-2.24 5.312-4.928 10.432-2.752 5.12-5.888 9.984-3.2 4.864-6.72 9.408-3.584 4.48-7.552 8.704-3.968 4.224-8.32 8.064t-8.96 7.232q-4.736 3.456-9.664 6.4-4.992 3.008-10.176 5.504-5.184 2.56-10.624 4.608-5.376 2.048-11.008 3.584-5.568 1.536-11.264 2.56-5.696 1.088-11.456 1.6T512 960H192q-6.272 0-12.544-0.64t-12.416-1.792q-6.208-1.28-12.16-3.072-6.08-1.856-11.84-4.224-5.824-2.432-11.392-5.376-5.504-2.944-10.752-6.464-5.248-3.52-10.112-7.488-4.864-3.968-9.28-8.448-4.48-4.48-8.448-9.28-3.968-4.864-7.488-10.112-3.52-5.248-6.4-10.752-3.008-5.568-5.44-11.392-2.368-5.76-4.224-11.776-1.792-6.08-3.072-12.16-1.216-6.208-1.792-12.48Q64 838.272 64 832z m186.496-408.064C217.408 455.936 240 512 286.08 512H320l-25.408 59.904c-13.696 32.32 27.136 59.968 52.032 35.2l108.736-108.16a51.2 51.2 0 0 0-36.096-87.552h-27.776l25.024-62.656c12.8-32-27.072-58.88-51.904-34.88l-114.112 110.08z"  p-id="1340"></path>
	</svg>
	);
	const JiaotongIcon = (props: Partial<CustomIconComponentProps>) => (
	  <Icon component={jiaotongSvg} {...props} />
	);
	const zhizaoSvg = () => (
	  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
	    <title>heart icon</title>
		<path d="M64 832V464h32a48 48 0 0 0 0-96H64V192q0-6.272 0.64-12.544t1.792-12.416q1.28-6.208 3.072-12.16 1.856-6.08 4.224-11.84 2.432-5.824 5.376-11.392 2.944-5.504 6.464-10.752 3.52-5.248 7.488-10.112 3.968-4.864 8.448-9.28 4.48-4.48 9.28-8.448 4.864-3.968 10.112-7.488 5.248-3.52 10.752-6.4 5.568-3.008 11.392-5.44 5.76-2.368 11.776-4.224 6.016-1.792 12.16-3.072 6.208-1.216 12.48-1.792Q185.728 64 192 64h320q6.272 0 12.544 0.64t12.416 1.792q6.208 1.28 12.16 3.072 6.08 1.856 11.84 4.224 5.824 2.432 11.392 5.376 5.504 2.944 10.752 6.464 5.248 3.52 10.112 7.488 4.864 3.968 9.28 8.448 4.48 4.48 8.448 9.28 3.968 4.864 7.488 10.112 3.52 5.248 6.4 10.752 3.008 5.568 5.44 11.392 2.368 5.76 4.224 11.776 1.792 6.016 3.072 12.16 1.216 6.208 1.792 12.48 0.64 6.272 0.64 12.544v176h-32a48 48 0 0 0 0 96h32v288h128q16 0 16-16V374.72A128 128 0 0 1 704 256V160a48 48 0 0 1 96 0V192h64v-32a48 48 0 0 1 96 0V256a128 128 0 0 1-80 118.72V736q0 46.4-32.832 79.168-32.768 32.832-79.168 32.832H638.976q-0.704 5.76-1.92 11.392-1.28 5.632-3.008 11.136-1.728 5.568-3.968 10.88-2.24 5.312-4.928 10.432-2.752 5.12-5.888 9.984-3.2 4.864-6.72 9.408-3.584 4.48-7.552 8.704-3.968 4.224-8.32 8.064t-8.96 7.232q-4.736 3.456-9.664 6.4-4.992 3.008-10.176 5.504-5.184 2.56-10.624 4.608-5.376 2.048-11.008 3.584-5.568 1.536-11.264 2.56-5.696 1.088-11.456 1.6T512 960H192q-6.272 0-12.544-0.64t-12.416-1.792q-6.208-1.28-12.16-3.072-6.08-1.856-11.84-4.224-5.824-2.432-11.392-5.376-5.504-2.944-10.752-6.464-5.248-3.52-10.112-7.488-4.864-3.968-9.28-8.448-4.48-4.48-8.448-9.28-3.968-4.864-7.488-10.112-3.52-5.248-6.4-10.752-3.008-5.568-5.44-11.392-2.368-5.76-4.224-11.776-1.792-6.08-3.072-12.16-1.216-6.208-1.792-12.48Q64 838.272 64 832z m186.496-408.064C217.408 455.936 240 512 286.08 512H320l-25.408 59.904c-13.696 32.32 27.136 59.968 52.032 35.2l108.736-108.16a51.2 51.2 0 0 0-36.096-87.552h-27.776l25.024-62.656c12.8-32-27.072-58.88-51.904-34.88l-114.112 110.08z"  p-id="1340"></path>
	</svg>
	);
	const ZhizaoIcon = (props: Partial<CustomIconComponentProps>) => (
	  <Icon component={zhizaoSvg} {...props} />
	);
	useEffect(() => {
	   cityATypeProNodes()
	}, []);
	
	
	const onChangeType =(item,val) =>{
		
		setIndex(val)
		setFiltetname(item.nodeType)
		
		setExpandedKeys([])
		// setSelectCity('全国')
		
	}
	const handleButtonClick =() =>{
		setIsVisible(!isVisible);
	}
	const drawnline =(val) =>{
		seDrawnlineindex(val)
	}
	const deepTraversal = (array) => {
		const result = [];
	
		const traverse = (items) => {
			items.forEach(item => {
			  if (item.children) {
				if (item.nodeType) {
				  result.push(item.key);
				}
				traverse(item.children);
			  }
			});
		};
		traverse(array);
		return result;
	};
	const deepTraversalmap = (array) => {
		const result = [];
	
		const traverse = (items) => {
			items.forEach(item => {
			  if (item.children) {
				if (item.nodeType) {
				  result.push(item);
				}
				traverse(item.children);
			  }
			});
		};
		traverse(array);
		return result;
	};
	const deepTraverseArray =(array) => {
		array.forEach(item => {
			if(item.nodeType==='楼宇园区'){
				item.icon = <HeartIcon />
			}else if(item.nodeType==='智能制造'){
				item.icon = <ZhizaoIcon />
			}else if(item.nodeType==='智算中心'){
				item.icon = <ZixuanIcon />
			}else if(item.nodeType==='智能交通'){
				item.icon = <JiaotongIcon />
			}
		    if (item.children && item.children.length > 0) {
		      deepTraverseArray(item.children);
		    }
		  });
		  return array
	}
	// 查区域和节点类型下的项目节点
	const cityATypeProNodes =() =>{
		http.post('global/cityATypeProNodes').then(res =>{
			if(res.data.code===200){
				let cityTree = res.data.data.cityTree
				console.log(cityTree)
				let nodeTypes = res.data.data.nodeTypes
				const datacityTree = deepTraverseArray(cityTree)
				let treeList = []
				cityTree.map(res =>{
					if(res.title==='福建省'){
						treeList.push(res)
					}
				})
				setTreeData(res.data.data.cityTree)
				setTreeDataList(res.data.data.cityTree)
				// setSelectval(res.data.data.cityTree[0].title)
				setSelectval('福建省')
				// const filteredItems = deepTraversal(cityTree);
				// const list = deepTraversalmap(cityTree);
				const filteredItems = deepTraversal(treeList);
				const list = deepTraversalmap(treeList);
				console.log(treeList)
				list.map(res =>{
					res.value = res.id
					res.label = res.title
				})
				const operatingProjects = list.filter(item => item.nodeState === '运营中');
				setEnergylist(operatingProjects?operatingProjects:[])
				let val = '';
				// const resitem = list.find(res => res.nodeState === '运营中');
				const resitem = list?.find(res => res.nodeState === '运营中')
				if (resitem) {
				  val = resitem.id;
					setEnergyval(val)
					setIncomeval(val)
					setIsoperation(true)
					setIsIncomeblock(true)
				}else{
					setEnergyval(list[0].id)
					setIncomeval(list[0].id)
					setIsoperation(false)
					setIsIncomeblock(false)
				}
				
				setNodeId(list[0].value)
				getNodesInfoCount(filteredItems)
				setNodeIds(filteredItems)
				nodeTypes.unshift('全部')
				setChinaList(nodeTypes)
				// setSelectedKeys(['quanguo'])
				// setSelectCity('全国')
				setSelectedKeys(['1000012'])
				setSelectCity('福建省')
				setCityname(treeList?treeList[0]:null)
				const groupedAndCounted = groupAndCountNodeTypesdata(treeList);
				setNodeTypeList(groupedAndCounted)
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	const findBeijingItems =(data) => {
	  let beijingItems = [];
	  
	    data.forEach(item => {
	      if (item.key ===cityval) {
	        beijingItems.push(item);
	      }
	      if (item.children && item.children.length > 0) {
	        beijingItems = beijingItems.concat(findBeijingItems(item.children));
	      }
	    });
	  
	    return beijingItems;
	}
	const findLastChildFirstObject =(node) => {
		if (node.children && node.children.length > 0) {
			return findLastChildFirstObject(node.children[node.children.length - 1]);
		} else {
			return node;
		}
	}
	const extractUniqueNodes =(data) => {
	  const uniqueNodes = new Set();
	
	  // 递归函数来遍历所有节点
	  function traverseNodes(nodes) {
	    nodes.forEach(node => {
	      // 如果nodeType不为null，则添加到Set中
	      if (node.nodeType !== null) {
	        uniqueNodes.add(JSON.stringify(node.nodeType)); // 使用JSON字符串作为唯一标识
	      }
	      // 如果有子节点，递归遍历
	      if (node.children && node.children.length > 0) {
	        traverseNodes(node.children);
	      }
	    });
	  }
	
	  // 从根节点的children开始遍历
	  traverseNodes(data.children);
	
	  // 将Set转换为数组并解析JSON字符串回对象
	  const result = Array.from(uniqueNodes).map(jsonStr => JSON.parse(jsonStr));
	
	  return result;
	}
	const groupAndCountNodeTypes =(data) => {
	    const grouped = {};
	      const counts = [];
	    
	      function traverse(nodes) {
	        nodes.forEach(node => {
	          if (node.nodeType !== null) {
	            if (!grouped[node.nodeType]) {
	              grouped[node.nodeType] = [];
	            }
	            grouped[node.nodeType].push(node);
	          }
	          if (node.children && node.children.length > 0) {
	            traverse(node.children);
	          }
	        });
	      }
	    
	      traverse(data.children);
	    
	      // 将分组后的数据转换为数组，并计算每种nodeType的数量
	      for (const [nodeType, nodes] of Object.entries(grouped)) {
	        counts.push({
	          nodeType: nodeType,
	          count: nodes.length,
	        });
	      }
	    
	      // 计算所有count的总和
	      const totalCount = counts.reduce((sum, current) => sum + current.count, 0);
	    
	      // 添加一个包含总和的对象到counts数组
	      counts.unshift({
	        nodeType: '全部',
	        count: totalCount
	      });
	    
	      return counts;
	}
	const groupAndCountNodeTypesdata =(data) => {
	  const grouped = {};
	    const result = [];
	  
	    // 递归函数遍历所有节点
	    function traverse(nodes) {
	      nodes.forEach(node => {
	        if (node.nodeType) {
	          if (!grouped[node.nodeType]) {
	            grouped[node.nodeType] = {
	              items: [],
	              count: 0
	            };
	          }
	          grouped[node.nodeType].items.push(node);
	          grouped[node.nodeType].count++;
	        }
	        if (node.children && node.children.length > 0) {
	          traverse(node.children);
	        }
	      });
	    }
	  
	    // 开始遍历
	    traverse(data);
	  
	    // 将分组后的数据转换为结果数组
	    for (const nodeType in grouped) {
	      result.push({
	        nodeType: nodeType,
	        count: grouped[nodeType].count
	      });
	    }
	  
	    // 计算所有count的总和
	    const totalCount = result.reduce((sum, current) => sum + current.count, 0);
	  
	    // 添加一个包含总和的对象到结果数组
	    result.unshift({
	      nodeType: '全部',
	      count: totalCount
	    });
	  
	    return result;
	}
	
	const collectNodesWithType =(data) => {
	  let result = [];
	
	  function traverse(nodes) {
	    nodes.forEach(node => {
	      if (node.nodeType !== null) {
	        result.push(node);
	      }
	      if (node.children && node.children.length > 0) {
	        traverse(node.children);
	      }
	    });
	  }
	
	  traverse(data.children); // 从根节点的 children 开始遍历
	  return result;
	}
	const countNodeTypeOccurrences =(treeData, keyToFind) => {
	  let result = [];
	  
	    function traverse(nodes) {
	      nodes.forEach(node => {
	        if (node.key === keyToFind) {
	          result.push(nodes);
	        }
	        if (node.children && node.children.length > 0) {
	          traverse(node.children);
	        }
	      });
	    }
	  
	    traverse(treeData); 
	    return result;
	}
	const findNodesWithParent =(data, targetKey) => {
	  const result = [];
	  
	    function traverse(nodes) {
	      for (const node of nodes) {
	        if (node.key === targetKey) {
	          // result.push(node);
	          return true; 
	        }
	  
	        if (node.children && traverse(node.children)) {
	          result.unshift(node); 
	          return true; 
	        }
	      }
	      return false; 
	    }
	  
	    traverse(data);
	    return result;
	}
	const onSelect = (selectedKeys, info) => {
		console.log(info)
		const uniqueNodes = extractUniqueNodes(info.node);
		setSelectval(info.node.title)
		setSelectedKeys(selectedKeys)
		setIsVisible(false)
		setIndex(0)
		setInfoNode(info.node)
		setCityname(info.node)
		setFiltetname('')
		if(info.node.title==='全国'){
			const filteredItems = deepTraversal(treeData);
			const list = deepTraversalmap(treeDataList);
			list.map(res =>{
				res.value = res.id
				res.label = res.title
			})
			
			const resitem = list.find(res => res.nodeState === '运营中');
			if (resitem) {
				const operatingProjects = list?.filter(item => item.nodeState === '运营中');
				setEnergylist(operatingProjects)
				setEnergyval(resitem.id)
				setIncomeval(resitem.id)
				setIsoperation(true)
				setIsIncomeblock(true)
			}else{
				setEnergyval(filteredItems[0])
				setIncomeval(filteredItems[0])
				setIsoperation(false)
				setIsIncomeblock(false)
				setEnergylist(list)
			}
			
			setNodeIds(filteredItems)
			getNodesInfoCount(filteredItems)
			setExpandedKeys([])
			setSelectedKeys(['quanguo'])
			
			setSelectCity('全国')
			const groupedAndCounted = groupAndCountNodeTypesdata(treeData);
			setNodeTypeList(groupedAndCounted)
			setIndex(0)
			
		}else{
			if(info.node.type!=='项目'){
				setCityval(info.node.id)
				const lastChildFirstObject = findLastChildFirstObject(info.node);
				setSelectCity(info.node.title)
				const groupedAndCounted = groupAndCountNodeTypes(info.node);
				setNodeTypeList(groupedAndCounted)
				const nodesWithType = collectNodesWithType(info.node);
				const resitem = nodesWithType.find(res => res.nodeState === '运营中');
				if (resitem) {
					const operatingProjects = nodesWithType?.filter(item => item.nodeState === '运营中');
					setEnergylist(operatingProjects)
					setIncomeval(resitem.id)
					setEnergyval(resitem.id)
					setIsoperation(true)
					setIsIncomeblock(true)
					
				}else{
					setIncomeval(nodesWithType[0].title)
					setEnergyval(nodesWithType[0].title)
					setIsoperation(false)
					setIsIncomeblock(false)
					setEnergylist([])
				}
			}else{
				setNodeIds(selectedKeys)
				getNodesInfoCount(selectedKeys)
				setNodeId(selectedKeys[0])
				setSelectedKeys(selectedKeys)
				setSelectCity('')
				setCityval('')
				const nodesWithCounts = countNodeTypeOccurrences(treeDataList, info.node.key);
				if(info.node.nodeState==='运营中'){
					setEnergylist([{
						id:info.node.nodeType,
						label:info.node.title,
						key:info.node.key,
						value:info.node.id
					}])
					setEnergyval(info.node.id)
					setIncomeval(info.node.id)
					setIsoperation(true)
					setIsIncomeblock(true)
				}else{
					
					setEnergylist([
						{
							id:info.node.nodeType,
							label:info.node.title,
							key:info.node.key,
							value:info.node.id
						}
					])
					setEnergyval(info.node.id)
					setIncomeval(info.node.id)
					setIsoperation(false)
					setIsIncomeblock(false)
				}
				
				let Countedlist = [{
					nodeType:'全部',
					count:1
				},{
					nodeType:info.node.nodeType,
					count:1
				}]
				setNodeTypeList(Countedlist)
				
			}
			
		}
		
		
	}
	
	useEffect(() =>{
		if(cityval){
			// if(isoperation){
				const beijingItems = findBeijingItems(treeData);
				const filteredItems = deepTraversal(beijingItems);
				setNodeIds(filteredItems)
				getNodesInfoCount(filteredItems)
			// 	setEnergyval(filteredItems[0])
			// 	setIncomeval(filteredItems[0])
			// }
			
		}
		
	},[cityval,isoperation])
	const filterBuildingPark =(array) => {
		return array.reduce((acc, item) => {
		    if (item.nodeType !== null && item.nodeType !== filtetname) {
		      return acc;
		    } else {
		      const newItem = { ...item, children: filterBuildingPark(item.children) };
		      acc.push(newItem);
		      return acc;
		    }
		  }, []);
	}
	const removeItemsWithNullNodeType =(data) => {
		function checkAndRemove(item) {
			if (item.children && item.children.length > 0) {
				for (let i = item.children.length - 1; i >= 0; i--) {
					if (item.children[i].nodeType !== null) {
						return false;
					}
					if (!checkAndRemove(item.children[i])) {
						return false;
					}
				}
				item.children = [];
				return true;
			}
			return true;
		}
		return data.filter(item => {
			if (item.nodeType === null && checkAndRemove(item)) {
				return false;
			}
			return true;
		});
	
	}
	const findBuildingsInProvinceOrCity =(data, targetTitle, targetType,filtetname) => {
		let results = [];
		
		  function searchNodes(nodes, isTarget) {
		    nodes.forEach(node => {
		      if (node.title === targetTitle && node.type === targetType) {
		        isTarget = true; // We found the target province or city
		      }
		      if (isTarget && node.nodeType === filtetname) {
		        results.push(node);
		      }
		      if (node.children) {
		        searchNodes(node.children, isTarget);
		      }
		      if (node.title === targetTitle && node.type === targetType) {
		        isTarget = false;
		      }
		    });
		  }
		  searchNodes(data, false);
		
		  return results;
		
	}
	const extractIdsByProvinceOrCity =(data, targetTitle, targetType) => {
	    let ids = [];
	
	    function searchNodes(nodes, isTarget) {
	        nodes.forEach(node => {
	            if (node.title === targetTitle && node.type === targetType) {
	                isTarget = true; 
	            }
	            if (isTarget && node.nodeType !== null) {
	                ids.push(node.id); 
	            }
	            if (node.children) {
	                searchNodes(node.children, isTarget);
	            }
	            if (node.title === targetTitle && node.type === targetType) {
	                isTarget = false;
	            }
	        });
	    }
	
	    searchNodes(data, false);
	
	    return ids;
	}
	useEffect(() =>{
		if(filtetname){
			if(filtetname!=='全部'){
				if(selectCity==='全国'){
					const buildingParks = filterBuildingPark(treeDataList);
					const list =removeItemsWithNullNodeType(buildingParks)
					const energylist = deepTraversalmap(list);
					let listmap = []
					energylist?.map(res =>{
						res.value = res.id
						res.label = res.title
						listmap.push(res.id)
					})
					setNodeIds(listmap)
				}else{
					const buildingsInZhejiang = cityname&&findBuildingsInProvinceOrCity(treeDataList, cityname.title,cityname.type,filtetname);
					console.log(buildingsInZhejiang,cityname)
					let nodes = []
					buildingsInZhejiang?.map(res =>{
						nodes.push(res.id)
					})
					setNodeIds(nodes)
				}
				
			}else{
				if(selectCity==='全国'){
					cityATypeProNodes()
				}else{
					const idsInZhejiang = cityname&&extractIdsByProvinceOrCity(treeDataList, cityname.title,cityname.type,);
					setNodeIds(cityname?idsInZhejiang:[])
				}
				
			}
			
		}
		
	},[filtetname])
	
	// 资源统计
	const getNodesInfoCount =(val) =>{
		http.post('homePage/getNodesInfoCount',{
			nodes:val
		}).then(res =>{
			if(res.data.code===200){
				let data = res.data.data
				setDataList(data)
				setLoadCount(data?.loadCount)
				setLoadJieRu(data?.loadJieRu!==null&&data?.loadJieRu!==undefined&&data?.loadJieRu!==''?Number(data.loadJieRu).toFixed(2):'-')
				setLoadKeTiao(data?.loadKeTiao!==null&&data?.loadKeTiao!==undefined&&data?.loadKeTiao!==''?Number(data.loadKeTiao).toFixed(2):'-')
				setStorageEnergyCount(data.storageEnergyCount)
				setStorageEnergyCapacity(data.storageEnergyCapacity)
				setStorageEnergyPower(data.storageEnergyPower)
				setWindCapacity(data.windCapacity)
				setWindCount(data.windCount)
				setPvCount(data.pvCount)
				setPvCapacity(data.pvCapacity)
				
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	
	// 
	const changeDay =(date,datestring) =>{
		setStartTs(datestring[0])
		setEndTs(datestring[1])
	}
	const handleChangeEnergy =(item,val) =>{
		setEnergyval(item)
		setNodeId(item)
		setIsoperation(val.nodeState==='运营中'?true:false)
	}
	const handleChangeIncome =(item,val) =>{
		setIncomeval(item)
		setIsIncomeblock(val.nodeState==='运营中'?true:false)
		
		// setNodeId(item)
	}
	const onExpand = (expandedKeys,info) => {
		setExpandedKeys(expandedKeys);
	};
	const dateFormat = 'YYYY-MM-DD'; 
	
	
	return(
	
		<div className="generating-cockpit">
			<div className="generating-cockpit-map" id='container-map' ref={mapRef}>
				<MapContainer nodeIds={nodeIds} selectCity={selectCity} />
			</div>
			<div className="generating-cockpit-type">
				<div className="generating-cockpit-province">
					<div className={isVisible?'generating-cockpit-province-selectval':'generating-cockpit-province-noselectval'} 
					onClick={handleButtonClick}>	{selectval}</div>
					
					<div 
						className={`fade-in ${isVisible ? 'visible' : 'generating-cockpit-province-tree'}`}
					>
						{
							isVisible?<Tree
								defaultExpandAll
								showIcon
							    selectedKeys={selectedKeys}
							    onSelect={onSelect}
							    treeData={treeData}
								expandedKeys={expandedKeys}
								onExpand={onExpand}
								width={{maxWidth:360}}
							/>:null
						}
					</div>
				</div>	
				<div className="generating-cockpit-province-type">
				{
					nodeTypeList.map((res,idex) =>{
						return 	<div 
						onClick={() =>onChangeType(res,idex)} 
						key={idex} className={idex===index?'active':''}>
									<span >
										{
											res.nodeType!=='全部'?<i 
											className={res.nodeType==='智算中心'?'generating-cockpit-province-zhisuan':
											res.nodeType==='智能交通'?'generating-cockpit-province-jiaotong':
											res.nodeType==='楼宇园区'?'generating-cockpit-province-louyu':
											res.nodeType==='智能制造'?'generating-cockpit-province-zhizao':''
											}></i>:null
										}
									{res.nodeType}<b>{res.count}</b></span>
								</div>
					})
					
				}
					
					
				</div>
			</div>
			<div className="generating-cockpit-Energy-income-block">
				<div className="generating-cockpit-Energy-block-map">
					<div>规划中</div>
					<div>建设中</div>
					<div>运营中</div>
					<div>已关闭</div>
				</div>
				<div className="generating-cockpit-Energy-income-block-chart">
					<div className="generating-cockpit-Energy-block">
						<div className="generating-cockpit-Energy-block-title">
							<span className="generating-cockpit-Energy-block-title-header">
							能量块预测
							</span>					
							<Select
							      style={{minWidth:126,maxWidth:280}}
								  popupClassName="nodestyle"
								  popupMatchSelectWidth={false}
								  value={energyval}
							      onChange={handleChangeEnergy}
							      options={energylist}
								  
							    />
							<RangePicker onChange={changeDay} style={{ width: 246,height:32 }}
								value={startTs!=''&&endTs!=''? [dayjs(startTs, dateFormat), dayjs(endTs, dateFormat)] : undefined}
							 />
						</div>
						<div>
							<Energyblock startTs={startTs} endTs={endTs} 
								energyval={energyval}
								isoperation={isoperation}
								
							/>
						</div>
					</div>
					<div className="generating-cockpit-Energy-block">
						<div className="generating-cockpit-Energy-block-title">
							<span className="generating-cockpit-Energy-block-title-header">
							收益分析
							</span>
							
							<Select
							      value={incomeval}
							      onChange={handleChangeIncome}
							      options={energylist}
								  popupClassName="nodestyle"
								  style={{minWidth:126,maxWidth:280}}
								  popupClassName="nodestyle"
								  popupMatchSelectWidth={false}
							      
							/>
							<div className="hand-drawn-line-day">
								<div onClick={() =>drawnline(1)} className={drawnlineindex===1?'drawactive':''}>月</div>
								<div onClick={() =>drawnline(2)} className={drawnlineindex===2?'drawactive':''}>年</div>
							</div>
							
						</div>
						<div className="hand-drawn-line-Revenue-frequency">
							<div
								onClick={() => { 
									setRevenueIndex(1)
								}}
								className={revenueindex===1?'active':''}
							>收益</div>
							<div
								className={revenueindex===2?'active':''}
								onClick={() => { 
									setRevenueIndex(2)
								}}>充放电次数</div>
						</div>
						<Incomeblock incomeval={incomeval} revenueindex={revenueindex} 
						isIncomeblock={isIncomeblock}
						drawnlineindex={drawnlineindex} />
					</div>
				</div>
			</div>
			
			<div className="generating-cockpit-quantity-of-electricity">
				<div className="generating-cockpit-quantity-of-electricity-load">
					<div className="generating-cockpit-quantity-of-electricity-load-title">
						<span className="generating-cockpit-Energy-block-title-header">项目概览</span>
					</div>
					<div>
						{
							<Loadecharts dataList={dataList} infoNode={infoNode}  />
						}
					</div>
				</div>
				<div className="generating-cockpit-quantity-of-electricity-load">
					<div className="generating-cockpit-quantity-of-electricity-load-title">
						<span className="generating-cockpit-Energy-block-title-header">负荷</span>
					</div>
					<div className="generating-cockpit-quantity-of-electricity-load-num">
						<div>
							<p>{loadCount}</p>
							<span>节点数量<i>/个</i></span>
						</div>
						<div>
							<p>{loadJieRu}</p>
							<span>可调负荷<i>/kW</i></span>
						</div>
						<div>
							<p>{loadKeTiao}</p>
							<span>接入负荷<i>/kW</i></span>
						</div>	
						
					</div>
					
				</div>
				<div className="generating-cockpit-quantity-of-electricity-load">
					<div className="generating-cockpit-quantity-of-electricity-load-title">
						<span className="generating-cockpit-Energy-block-title-header">储能</span>
					</div>
					<div className="generating-cockpit-quantity-of-electricity-load-num-Energy">
						<div className="Energy">
							<p>{storageEnergyCount}</p>
							<span>节点数量<i>/个</i></span>
						</div>
						<div className="Energy">
							<div>
								{storageEnergyCapacity}
							</div>
							<p>容量<i>/kWh</i></p>
						</div>
						<div className="Energy">
							<div>{storageEnergyPower}</div>
							<p>功率<i>/kW</i></p>
						</div>	
						
					</div>
					
				</div>
				<div className="generating-cockpit-quantity-of-electricity-load">
					<div className="generating-cockpit-quantity-of-electricity-load-title">
						<span className="generating-cockpit-Energy-block-title-header">发电</span>
					</div>
					<div className="generating-cockpit-quantity-of-electricity-load-num-Energy">
						<div className="Energy-electric">
							<img src={pv} />
							<div>
								<p>光伏电站 - {pvCount}个</p>
								<span><b>{pvCapacity}</b> kW</span>
							</div>
						</div>
						
						<div className="Energy-electric">
							<img src={wind} />
							<div>
								<p>风场 - {windCount}个</p>
								<span><b>{windCapacity}</b> kW</span>
							</div>
						</div>	
						
					</div>
				</div>
			</div>
			
		</div>
	)
}
export default Generating