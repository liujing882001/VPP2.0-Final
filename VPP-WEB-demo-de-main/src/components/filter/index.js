
import React, {
	useEffect,
	useRef,
	useState
} from 'react';
import { Select ,DatePicker,ConfigProvider} from 'antd';
import './index.scss'
import locale from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import http from "../../server/server.js";

const { RangePicker } = DatePicker;

export const Filter = (props) => {
	const {argumentsList,onTimeChange,onnodeChange,argumentsdata,position,showRegenerateBtn,isOver,
	lastIndex,index} = props;
	const [pickerVal,setPickerVal] = useState([dayjs().subtract(1, 'day').startOf('day'), dayjs().subtract(1, 'day').endOf('day')])
	const [dateList,setdateList] = useState(['昨日','今日','近7日'])
	const [dateVal,setDateVal] = useState('昨日')
	const [options,setOptions] = useState([])
	const [stationId,setStationId] = useState('')
	const [loading,setLoading] = useState(false)
	const [selectedMonth, setSelectedMonth] = useState(null);
	const [isvalue, setIsvalue] = useState(null);
	console.log(position,showRegenerateBtn,isOver,lastIndex,index)
	useEffect(() =>{
		if(argumentsList){
			
			if(argumentsList?.arguments?.name ||argumentsList?.arguments?.category){
				setLoading(true)	
				getqueryStationNode(argumentsList?.url)
			}
			
			if(argumentsList?.arguments?.stationId){
				setStationId(argumentsList?.arguments?.stationId)
			}
			if(argumentsList?.arguments?.nodeId){
				setStationId(argumentsList?.arguments?.nodeId)
				
			}
			if(argumentsList?.arguments?.st &&argumentsList?.arguments?.et){
				setDateVal('日期')
				setPickerVal([dayjs(argumentsList?.arguments?.st) ,dayjs(argumentsList?.arguments?.et)])
			}
			if(!Object.keys(argumentsList?.arguments).length > 0){
				getqueryStationNode(argumentsList?.url)
			}
			
		}
		
	},[argumentsList])
	useEffect(() =>{
		if(argumentsdata){
			argumentsdata?.map(res =>{
				if(res?.arguments?.name || res?.arguments?.category){
					getqueryStationNode(res?.url)
				}
				if(res?.arguments?.stationId){
					setStationId(res?.arguments?.stationId)
				}
				if(res?.arguments?.nodeId){
					setStationId(res?.arguments?.nodeId)
				}
				if(res?.arguments?.st &&res?.arguments?.et){
					setDateVal('日期')
					setPickerVal([dayjs(res?.arguments?.st) ,dayjs(res?.arguments?.et)])
				}
			})
		}
	},[argumentsdata])
	
	// 获取节点列表
	const getqueryStationNode = (url) =>{
		http.get(url+'?category='+'storageEnergy').then(res =>{
			if(res.data.code===200){
				let data = res.data.data
				data?.map(res =>{
					res.value = res.stationId
					res.label = res.stationName
				})
				setOptions(data)
				setLoading(false)
			}
		})
	}
	// 计算昨日日期范围
	const getYesterdayRange = () => {
	    const start = dayjs().subtract(1, 'day').startOf('day');
	    const end = dayjs().subtract(1, 'day').endOf('day');
	    return [start, end];
	};
	
	  // 计算今日日期范围
	const getTodayRange = () => {
	    const start = dayjs().startOf('day');
	    const end = dayjs().endOf('day');
	    return [start, end];
	  };
	
	  // 计算近七日日期范围
	const getLastSevenDaysRange = () => {
	    const start = dayjs().subtract(7, 'day').startOf('day');
	    const end = dayjs().endOf('day');
	    return [start, end];
	};

	const onChangedate =(date,dateString) =>{
		setPickerVal(date)
		onTimeChange(dateString);
		setDateVal('日期')
		setSelectedMonth(null)
	}
	const daterange =(val) =>{
		setDateVal(val)
		setSelectedMonth(null)
		// console.log(val)
		if(val==='昨日'){
			setPickerVal(getYesterdayRange());
			const dates = getYesterdayRange();
			const formattedDates = dates.map(date => dayjs(date).format('YYYY-MM-DD'));
			onTimeChange(formattedDates)
		}else if(val==='今日'){
			setPickerVal(getTodayRange());
			const dates = getTodayRange();
			const formattedDates = dates.map(date => dayjs(date).format('YYYY-MM-DD'));
			onTimeChange(formattedDates)
		}else{
			setPickerVal(getLastSevenDaysRange());
			const dates = getLastSevenDaysRange();
			const formattedDates = dates.map(date => dayjs(date).format('YYYY-MM-DD'));
			onTimeChange(formattedDates)
		}
	}
	const handleChange = (value: string) => {
	  // console.log(`selected ${value}`);
	  setStationId(value)
	  onnodeChange(value )
	 
	};
	const handleCalendarChange = (dates) => {
	    if (dates && dates[0]) {
	      setSelectedMonth(dates[0].month());
	    } else {
	      setSelectedMonth(null);
	    }
	};
	const disabledDate = (current) => {
	    if (selectedMonth === null) {
	      return current.month() !== dayjs().month();
	    }
	    return current.month() !== selectedMonth;
	};

	return(
		<div className="message-filter">
			<Select
				onChange={handleChange}
				popupClassName="filter-select"
				style={{ minWidth: 120, maxWidth: 249 }}
				className="filter-select-option"
				options={options}
				value={stationId}
				loading={loading}
				
			/>
			<div className="message-filter-date"
				className={dateVal=='今日'?'message-filter-date1':'message-filter-date'}
				
			>
				{
					dateList.map(res =>{
						return <button onClick={() =>daterange(res)} className={dateVal===res?'active':''}>{res}</button>
					})
				}
					
				      
				<ConfigProvider locale={locale}>
					<RangePicker 
					style={{width:234}}
					onCalendarChange={handleCalendarChange}
					      disabledDate={selectedMonth?disabledDate:''}
					value={pickerVal}
					defaultValue={pickerVal}
					format="YYYY-MM-DD"
					className={dateVal==='日期'?'ant-picker-active':''}
					onChange={onChangedate} 
					 />
				</ConfigProvider>
			</div>
		</div>)
}