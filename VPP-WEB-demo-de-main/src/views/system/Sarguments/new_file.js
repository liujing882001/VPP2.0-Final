import React,{useEffect,useState} from 'react'
import { Table ,Modal,message,Badge ,Views ,Button,Select  } from 'antd';
import dayjs from 'dayjs';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';// 日历格子显示
import interactionPlugin from '@fullcalendar/interaction'; // 拖拽插件
import locale from '@fullcalendar/core/locales/zh-cn';// 中文
import http from '../../../server/server.js'
import { MenuOutlined,UploadOutlined ,LeftCircleOutlined} from '@ant-design/icons';
import './index.scss'
// let calendarApi;
const { Option } = Select;

const NewDate =(props) =>{
	const [isModalVisible, setIsModalVisible] = useState(false);
	const [events,setEvents] = useState([]);
	const [monthDate, setMonthDate] = useState('');
	const [currentDate, setCurrentDate] = useState(dayjs().startOf('month').toDate());
	const [dateTypeList, setDateTypeList] = useState([]);
	const [dateTypeListTwo, setDateTypeListTwo] = useState([]);
	const [allTypeList, setAllTypeList] = useState([]);
	const [date,setDate] = useState('');
	const [isModalOpen, setIsModalOpen] = useState(false);
	const [title, setTitle] = useState('');
	const [content, setContent] = useState('');
	const [nodeList, setNodeList] = useState(
		[
				{
					label:'工作日',
					value:1
				},
				{
					label:'休息日',
					value:2
				},
				{
					label:'删除计算日',
					value:3
				},
				{
					label:'元旦',
					value:4
				},
				// 5-春节 6-清明节 7-劳动节 8-端午节 9-中秋节 10-国庆节
				{
					label:'春节',
					value:5
				},
				{
					label:'清明节',
					value:6
				},
				{
					label:'劳动节',
					value:7
				},
				{
					label:'端午节',
					value:8
				},
				{
					label:'中秋节',
					value:9
				},
				{
					label:'国庆节',
					value:10
				}
			]);
	
	  const [isOpen, setIsOpen] = useState(false);
	  const [dateList, setDateList] = useState([]);
	  const [NewdateType, setNewdateType] = useState('');
	  const [yearMonths, setYearMonths] = useState([]);
	  const [month, setMonth] = useState(dayjs().format('YYYY-MM'));
	  const [value, setValue] = useState(dayjs().format('YYYY-MM'));
	  const [dateTypeval, setDateTypeval] = useState('请选择');
	  const [dateTypeOpen, setDateTypeOpen] = useState(false);
	  const [titleName, setTitleName] = useState('');
	  const [start, setStart] = useState('');
	  const [end, setEnd] = useState('');
	  const [dateType, setDateType] = useState('');
	  const [dateTypeTwo, setDateTypeTwo] = useState('');
	  const [dateTypeThree, setDateTypeThree] = useState('');
	  const [dateTypeFour, setDateTypeFour] = useState('');
	  const [dateTypeFive, setDateTypeFive] = useState('');
	  const [dateTypeSix, setDateTypeSix] = useState('');
	  const [dateTypeSeven, setDateTypeSeven] = useState('');
	  const [dateTypeEight, setDateTypeEight] = useState('');
	  const [dateTypeNine, setDateTypeNine] = useState('');
	  const [dateTypeTen, setDateTypeTen] = useState('');
	
	useEffect(() =>{
		getDateList()
		var currentDate = new Date();
		var currentYear = currentDate.getFullYear();
		
		var yearMonths = [];
		for (var i = 0; i < 12; i++) {
			var date = new Date(currentYear, i, 1);
			var year = date.getFullYear();
			var month = date.getMonth() + 1;
			
			if(month==10||month==11||month==12){
				// month
			}else{
				month ='0'+month
			}
			console.log(month)
			yearMonths.push(year +'-' + month);
			setYearMonths(yearMonths)
		}
	},[])
	const getDay=(startDate, endDate)=> {
		var result = new Array();
		var ab = startDate.split("-")
		console.log(ab)
		var ae = endDate.split("-")
		var db = new Date()
		db.setUTCFullYear(ab[0], ab[1]-1, ab[2])
		var de = new Date()
		de.setUTCFullYear(ae[0], ae[1]-1, ae[2])
		var unixDb=db.getTime()
		var unixDe=de.getTime()
		for(var k=unixDb;k<=unixDe;){
			result.push(formatDate(new Date(parseInt(k)), 'yyyy-MM-dd'))
			k=k+24*60*60*1000
		}
		console.log(result)
		return result
	}
	// 日期格式化
	const formatDate = (date, fmt) => {
		if (date === "" || date === null || date === undefined) {
			return null;
		}
		if (fmt === "" || fmt === null || fmt === undefined) {
			fmt = "yyyy-MM";
		}
		date = new Date(date);
		var o = {
			"M+": date.getMonth() + 1, // 月份
			"d+": date.getDate(), // 日
			"h+": date.getHours(), // 小时
			"m+": date.getMinutes(), // 分
			"s+": date.getSeconds(), // 秒
			"q+": Math.floor((date.getMonth() + 3) / 3), // 季度
			S: date.getMilliseconds(), // 毫秒
		};
		if (/(y+)/.test(fmt))
			fmt = fmt.replace(
				RegExp.$1,
				(date.getFullYear() + "").substr(4 - RegExp.$1.length)
			);
		for (var k in o) {
			if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(
	          RegExp.$1,
	          RegExp.$1.length === 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length)
			);
		}
		return fmt;
	}
	const mergeDateRanges=(dates) => {
	    // 先根据 dateType 和 date 进行排序
	    dates.sort((a, b) => {
	        if (a.dateType !== b.dateType) {
	            return a.dateType - b.dateType;
	        }
	        return new Date(a.date) - new Date(b.date);
	    });
	
	    let ranges = [];
	    let currentRange = null;
	    let id = 0;
	
	    for (const date of dates) {
	        if (!currentRange || currentRange.dateType !== date.dateType) {
	            // 如果当前范围不存在，或者 dateType 发生变化，则开始新的范围
	            currentRange = { start: date.date, end: date.date, dateType: date.dateType, id: id++,allDay: true };
	            ranges.push(currentRange);
	        } else {
	            // 如果是连续的日期，更新当前范围的结束日期
	            if (new Date(currentRange.end).getTime() + 86400000 === new Date(date.date).getTime()) {
	                currentRange.end = date.date;
	            } else {
	                // 如果不连续，开始新的范围
	                currentRange = { start: date.date, end: date.date, dateType: date.dateType, id: id++,allDay: true };
	                ranges.push(currentRange);
	            }
	        }
	    }
	
	    // 根据dateType添加标题
	    ranges.forEach(range => {
	        if (range.dateType === 1) {
	            range.title = '工作日';
	        } else if (range.dateType === 2) {
	            range.title = '休息日';
	        } else if (range.dateType === 3) {
	            range.title = '删除计算日';
	        } else if (range.dateType === 4) {
	            range.title = '元旦';
	        }else if (range.dateType === 5) {
	            range.title = '春节';
	        }else if (range.dateType === 6) {
	            range.title = '清明节';
	        }else if (range.dateType === 7) {
	            range.title = '劳动节';
	        }else if (range.dateType === 8) {
	            range.title = '端午节';
	        }else if (range.dateType === 9) {
	            range.title = '中秋节';
	        }else if (range.dateType === 10) {
	            range.title = '国庆节';
	        }
	        // 删除dateType，因为它不再需要
	        delete range.dateType;
	    });
	
	    return ranges;
	}
	
	const addDays=(date, days) =>{
	  let result = new Date(date);
	  result.setDate(result.getDate() + days);
	  return result.toISOString().split('T')[0];
	}
	const addDayToEndDates=(events)=> {
	  return events.map(event => {
	    // 解析当前end日期
	    let endDate = new Date(event.end);
	    // 在日期上加上一天
	    endDate.setDate(endDate.getDate() + 1);
	    // 更新event对象的end属性
	    event.end = endDate.toISOString().split('T')[0];
	    return event;
	  });
	}
	// 查询基线负荷日期
	const getDateList=() =>{
		// 1-工作日 2-非工作日 3-删除计算日 4-元旦 5-春节 6-清明节 7-劳动节 8-端午节 9-中秋节 10-国庆节
		http.post('demand_resp/calendar/getDateList?month='+month).then(res =>{
			console.log(res)
			if(res.data.code==200){
				let dates = res.data.data
				let dateRanges = mergeDateRanges(dates);
				
				// 输出结果
				// console.log(dateRanges);
				let updatedEvents =addDayToEndDates(dateRanges)
				// console.log(updatedEvents)
				setEvents(updatedEvents)
			}
		}).catch(err =>{
			console.log(err)
		})
		
	}
	const handleSelect = ({ start, end }) => {
		 // alert(0)
	    const title = window.prompt('New Event name')
	    // console.log(start, end)
	    debugger
	    if (title){
			setEvents(...events,{
			      start,
			      end,
			      title,
			    });
		}
		  
	}
	// 编辑
	const edit =() =>{
		setIsOpen(true)
	}
	// 选择属性
	const chosenode =(val) =>{
		console.log(val)
		setNewdateType(val);
		setDateTypeval(val);
		setTitleName(val)
	}
	// 选择日期
	const choseDate =(val) =>{
		console.log(val)
		// this.setState({
		// 	date:val,
		// 	month:val,
		// 	value:val,
		// 	currentDate:val
		// },() =>{
		// 	this.getDateList()
		// })
		setDate(val);
		setMonth(val);
		setValue(val);
		setCurrentDate(val);
		getDateList()
	}
	// 返回
	const area = () =>{
		props.history.push({
			pathname: '/Sarguments',
		});
	}
	
		
		const renderEventContent =(eventInfo)=> {
		  return (
		    <>
		      <b>{eventInfo.timeText}</b>
		      <i>{eventInfo.event.title}</i>
		    </>
		  )
		} 
		const handleOk = () => {
			setIsModalOpen(false)
		};
		
		const handleCancel = () => {
			setIsModalOpen(false)
		};
		// 编辑
		const handleOk1 = () => {
		    setIsModalOpen(false);
			http.post('demand_resp/calendar/editDateType',{
				"dateList": dateList,
				"dateType": NewdateType
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					
					setDateTypeval('')
					message.success('编辑成功')
					getDateList()
					
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
		};
		const handleCancel1 =() =>{
			setIsOpen(false);
			setDateTypeval('')
		}
		const handleSelectSlot =({ event, start, end, allDay }) =>{
			setDateTypeOpen(true);
			setStart(dayjs(start).format('YYYY-MM-DD'));
			setEnd(dayjs(end).format('YYYY-MM-DD'))
			
			let start1 = dayjs(start).format('YYYY-MM-DD')
			let end1 = dayjs(end).format('YYYY-MM-DD')
			
		}
		
		const handleToday = () => {
			// onNavigate('TODAY')
			// this.setState({
			// 	date:dayjs().format('YYYY-MM'),
			// 	value:dayjs().format('YYYY-MM'),
			// 	month:dayjs().format('YYYY-MM'),
			// },() =>{
			// 	this.getDateList()
			// })
			// setdate
		}
		// 属性编辑
		const handleOk2 =() =>{
			
			
			const weekday = [7, 1, 2, 3, 4,5,6];
			let newList =getDay(start,end)
			const lastItem = newList.pop();
			console.log(newList);
			let list = []
			
			for(var i=0;i<newList.length;i++){
				let day = newList[i].substring(5,7)
				console.log(newList[i].substring(5,7))
				
				list.push({
					dateType:titleName,
					date:newList[i],
					dayOfWeek:weekday[new Date(newList[i]).getDay()],
					year: newList[i].substr(0,4),
					month: newList[i].substr(5,10).charAt(0) === '0'?Number(newList[i].substr(6,10)):Number(newList[i].substr(5,7)),
					day: newList[i].substr(8,10).charAt(0) === '0'?Number(newList[i].substr(9,10)):Number(newList[i].substr(8,10)),
					updateBy: null,
					updateTime: null,
				})
				// console.log(weekday[new Date(newList[i]).getDay()])
			}
			console.log(list)
			setEvents(...events,{
						start:start,
						end:end,
						title:titleName==1?'工作日':titleName==2?'休息日' :titleName==3?'删除计算日' :
						titleName==4?'元旦' :titleName==5?'春节' :titleName==6?'清明节' :titleName==7?'劳动节' 
						:titleName==8?'端午节' :titleName==9?'中秋节':'国庆节',
					})
			setDateTypeOpen(false)
			http.post('demand_resp/calendar/editDateType',{
				"dateList": newList,
				"dateType": titleName
			}).then(res =>{
				console.log(res)
				if(res.data.code==200){
					setDateTypeval('');
					message.success('编辑成功')
					getDateList()
					
				}else{
					message.info(res.data.msg)
				}
			}).catch(err =>{
				console.log(err)
			})
			
		}
		const handleCancel2 =() =>{
			setDateTypeOpen(false);
			setDateTypeval('')
		}
		const MyToolbar = props => {
			const { label, onNavigate, onView } = props

			const handlePrev = () => {
				onNavigate('PREV')
			}

			

			const handleNext = () => {
				onNavigate('NEXT')

			}

			const handleViewChange = event => {
				// onView(event.target.value)
			}

			return (
				<div style={{padding:'10px 15px'}}>
			 
					<Button type="button" ghost onClick={handleToday}>
						今天
					</Button>
					
					<span style={{color:'#FFF',marginLeft:200}}>
						{label}
					</span>
				</div>
			)
		}
		const eventClick =(eventInfo,data) =>{
			let event = eventInfo.event._def
			let dayDate = eventInfo.event._instance.range
			let start = dayjs(dayDate.start).format('YYYY-MM-DD')
			let end = dayjs(dayDate.end).format('YYYY-MM-DD')
			let newList = []
			newList.push(start)
			newList.push(end)
			setDateList(newList)
			const sameItems = [];
			for (let i = 0; i < allTypeList.length; i++) {
			  for (let j = 0; j < newList.length; j++) {
			    if (allTypeList[i].date === newList[j]) {
					sameItems.push(allTypeList[i]);
					break;
			    }
			  }
			}
			const weekday = [7, 1, 2, 3, 4,5,6];
			let newList1 = getDay(start,end)
			const lastItem1= newList1.pop();
			let list1 = []
			
			for(var i=0;i<newList1.length;i++){
				let day = newList1[i].substring(5,7)
				list1.push({
					dateType:event.title=='工作日'?1:event.title=='休息日'?2:event.title=='休息日'?2:event.title=='删除计算日'?3:event.title=='元旦'?4
					:event.title=='春节'?5:event.title=='清明节'?6:event.title=='劳动节'?7:event.title=='端午节'?8:event.title=='中秋节'?9
					:event.title=='国庆节'?10:'',
					date:newList1[i],
					dayOfWeek:weekday[new Date(newList1[i]).getDay()],
					year: newList1[i].substr(0,4),
					month: newList1[i].substring(5,7).charAt(0) == 0?Number(newList1[i].substring(6,7)):Number(newList1[i].substring(5,7)),
					day: newList1[i].substr(8,10).charAt(0) == '0'?Number(newList1[i].substr(9,10)):Number(newList1[i].substr(8,10)),
					updateBy: null,
					updateTime: null,
				})
				// console.log(weekday[new Date(newList[i]).getDay()])
			}
			setIsModalOpen(true);
			setTitle(event.title);
			http.post('demand_resp/calendar/convertDateStr',list1
			).then(res =>{
				console.log(res)
				if(res.data.code==200){
					setContent(res.data.data)
				}
			}).catch(err =>{
				console.log(err)
			})
		}
		const selectClick =(event ) =>{
			setStart(event.startStr);
			setEnd(event.endStr);
			setDateTypeval(true);
			
		}
		const eventDrop =() =>{
		}
		// 下个月
		const getNext =() =>{
			
		}
		// 上个月
		const getPrev =(event) =>{
			console.log(event)
		}
		return (
			<div style={{height:'calc(100% - 16px)'}} className="newDatebody">
				<div className="newDatetitle"><a style={{color:'#FFF'}}  onClick={area}>
				<LeftCircleOutlined style={{marginRight:10}} />返回</a></div>
				<div className="newDate">
					
					<div className="newDateName">
					<Button type="button" ghost onClick={handleToday}>
						今天
					</Button>
						<Select
							// placeholder=''
							style={{width:'119px',marginLeft:20}}
							onChange={choseDate}
							value={value}
						 >
							{
								yearMonths.map(res =>{
									return <Option key={res} value={res}>{res}</Option>
								})
							}
						
						</Select>
					</div>
					<FullCalendar
						locale={locale}
						plugins={[dayGridPlugin, interactionPlugin]}
						initialView='dayGridMonth'
						editable // false之后不可以拖拽
						selectable
						events={events}
						eventContent={renderEventContent}
						eventClick={eventClick} // 事项点击事件
						dayClick ={selectClick}
						//设置是否可被单击或者拖动选择
						selectable= {true}
						select={selectClick} //选中日历格事件
						editable 	//可以拖拽
						// customButtons={{
						// 	// 自定义按钮触发回调函数
						// 	// getToday: { text: "今天", click: getToday },
						// 	getNext: { text: ">", click: getNext },
						// 	getPrev: { text: "<", click: getPrev },
						// 	// month: { text: "月", click: month },
						// 	// week: { text: "周", click: week },
						// 	// list: { text: "列表", click: list }
						// }}
						// headerToolbar={{
						//           left: "today", // 左侧按钮展示为下方自定义按钮名称
						//           center: "title", // 时间展示
						//           // right: "month" // 右侧按钮展示
						//         }}
						// date={this.state.currentDate}
						
						
					/>
					
					<Modal title="属性编辑" visible={dateTypeOpen} 
					wrapClassName="Modal-visible" 
					onOk={handleOk2} onCancel={handleCancel2}
						 centered
						 footer={[
						 // 重点：定义右下角 
						 <Button ghost  onClick={handleCancel2}>取消</Button>,
						 <Button key="submit" type="primary" onClick={handleOk2}>
						 确定
						 </Button> ]}
					>
						<Select
							placeholder="请选择"
							style={{width:'100%',marginBottom:30}}
							onChange={chosenode}
							value={dateTypeval}
						 >
						 
							{
								nodeList.length &&nodeList.map(res =>{
									return <Option key={res.value} value={res.value}>{res.label}</Option>
								})
							}
						
						</Select>
					</Modal>
					<Modal title={title} mask={false} visible={isModalOpen} onOk={handleOk} onCancel={handleCancel}
						footer={null} centered
						
					>
						
						<p className="titlename">{content}</p>
						<div className="blockName">
							<Button type="text" 
								onClick={edit}
							style={{color:'#FFF',background:'#0092FF',marginTop:40}} block ghost>
							      编辑
							</Button>
						</div>
					</Modal>
					<Modal title="编辑" 
					centered visible={isOpen} onOk={handleOk1} onCancel={handleCancel1}
						// footer={null}
						wrapClassName="Modal-visible" 
						footer={[
						// 重点：定义右下角 
						<Button ghost onClick={handleCancel1}>取消</Button>,
						<Button key="submit" type="primary" onClick={handleOk1}>
						确定
						</Button> ]}
					>
						<Select
							placeholder="请选择"
							style={{width:'100%',marginBottom:30}}
							onChange={chosenode}
							value={dateTypeval}
						 >
						 
							{
								nodeList.length &&nodeList.map(res =>{
									return <Option key={res.value} value={res.value}>{res.label}</Option>
								})
							}
						
						</Select>
					</Modal>
				</div>
			</div>
				
		)
	}

export default  NewDate




// <select onChange={handleViewChange}>
						
// 					</select>