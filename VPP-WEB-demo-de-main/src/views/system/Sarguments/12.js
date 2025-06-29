import React,{Component} from 'react'
import { Table ,Modal,message,Badge ,Views ,Button,Select  } from 'antd';
import dayjs from 'dayjs';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';// 日历格子显示
import interactionPlugin from '@fullcalendar/interaction'; // 拖拽插件
import locale from '@fullcalendar/core/locales/zh-cn';// 中文
import http from '../../../server/server.js'
import { MenuOutlined,UploadOutlined ,LeftCircleOutlined} from '@ant-design/icons';
import './index.scss'
let calendarApi;
const { Option } = Select;

class newDate extends Component {
	
	constructor(props) {
		super(props)
		this.state={
			events:[],
			views: {
				month: true,
				// Today:true
				// week: MyWeek,
			  },
			monthdate:'',
			currentDate: dayjs().startOf('month').toDate(),
			dateTypeList:[],
			dateTypeListTwo:[],
			allTypeList:[],
			
			isModalOpen:false,
			title:'',
			content:'',
			nodeList:[
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
			],
			isOpen:false,
			dateList:[],
			NewdateType:'',
			yearMonths:[],
			// date:new Date()
			month:dayjs().format('YYYY-MM'),
			value:dayjs().format('YYYY-MM'),
			dateTypeval:'请选择',
			dateTypeOpen:false,
			titleName:'',
			start:'',
			end:'',
			dateType:'',
			dateTypeTwo:'',
			dateTypeThree:'',
			dateTypeFour:'',
			dateTypeFive:'',
			dateTypeSix:'',
			dateTypeSeven:'',
			dateTypeEight:'',
			dateTypeNine:'',
			dateTypeTen:'',
			
			
		}
	}
	sortDateList (data) {
	  data.sort(function (a, b) {
	    return new Date(a).getTime() - new Date(b).getTime()
	  })
	  return data
	}
	
	componentDidMount(){
		// 原始多维数组
		this.getDateList()
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
			this.setState({
				yearMonths:yearMonths
			})
		}
		 // calendarApi = calendarRef.current.getApi();  // 获取api，使用内置方法
		// FullCalendarDate(calendarApi.view.title,'month');  // calendarApi.view.title 获取当前时间，存储store



		    
	}
	getDay(startDate, endDate) {
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
			result.push(this.formatDate(new Date(parseInt(k)), 'yyyy-MM-dd'))
			k=k+24*60*60*1000
		}
		console.log(result)
		return result
	}
	// 日期格式化
	formatDate  (date, fmt) {
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
	addOneDay(currentDate) {
	  var date = new Date(currentDate);
	  date.setDate(date.getDate() + 1);
	  var year = date.getFullYear();
	  var month = (date.getMonth() + 1).toString().padStart(2, '0');
	  var day = date.getDate().toString().padStart(2, '0');
	  return year + '-' + month + '-' + day;
	}
	mergeDateRanges(dates) {
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
	generateDateRanges(dates) {
	  let ranges = [];
	  let currentRange = null;
	
	  // 遍历所有日期
	  for (let i = 0; i < dates.length; i++) {
	    let currentDate = dates[i];
	    let nextDate = dates[i + 1];
	
	    // 如果当前范围是空的，或者当前日期的dateType与当前范围的dateType不同
	    // 开始一个新的范围
	    if (!currentRange || currentRange.dateType !== currentDate.dateType) {
	      currentRange = {
	        startDate: currentDate.date,
	        dateType: currentDate.dateType,
	        endDate: currentDate.date
	      };
	      // 如果这是最后一个元素或者下一个元素的dateType不同，结束当前范围
	      if (!nextDate || nextDate.dateType !== currentDate.dateType) {
	        currentRange.endDate = this.addDays(currentRange.endDate, 1);
	        ranges.push(currentRange);
	        currentRange = null;
	      }
	    } else {
	      // 否则，更新当前范围的结束日期
	      currentRange.endDate = currentDate.date;
	      // 如果这是最后一个元素或者下一个元素的dateType不同，结束当前范围
	      if (!nextDate || nextDate.dateType !== currentDate.dateType) {
	        currentRange.endDate = this.addDays(currentRange.endDate, 1);
	        ranges.push(currentRange);
	        currentRange = null;
	      }
	    }
	  }
	
	  return ranges;
	}
	addDays(date, days) {
	  let result = new Date(date);
	  result.setDate(result.getDate() + days);
	  return result.toISOString().split('T')[0];
	}
	addDayToEndDates(events) {
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
	getDateList(){
		// 1-工作日 2-非工作日 3-删除计算日 4-元旦 5-春节 6-清明节 7-劳动节 8-端午节 9-中秋节 10-国庆节
		http.post('demand_resp/calendar/getDateList?month='+this.state.month).then(res =>{
			console.log(res)
			if(res.data.code==200){
				// if()
				let dates = res.data.data
				let dateRanges = this.mergeDateRanges(dates);
				
				// 输出结果
				console.log(dateRanges);
				let updatedEvents =this.addDayToEndDates(dateRanges)
				console.log(updatedEvents)
				this.setState({
					events:updatedEvents
				})
			}
		}).catch(err =>{
			console.log(err)
		})
		
	}
	handleSelect = ({ start, end }) => {
		 // alert(0)
	    const title = window.prompt('New Event name')
	    // console.log(start, end)
	    if (title)
	      this.setState({
	        events: [
	          ...this.state.events,
	          {
	            start,
	            end,
	            title,
	          },
	        ],
	      })
	}
	handleNavigate = (date, view) => {
		console.log(date,view)
		console.log(dayjs(date).format('YYYY-MM'))
	    this.setState({
			date:date,
	        currentDate: dayjs(date).startOf('month').toDate(),
			month:dayjs().format('YYYY-MM'),
			value:dayjs().format('YYYY-MM')
	    },() =>{
			this.getDateList()
		});
	};
	handleEventDrop = ({ event, start, end, allDay }) => {
	  // 更新事件
		console.log(event)
		console.log(start)
		console.log(end)
		console.log(allDay)
	}
	// 编辑
	edit =() =>{
		console.log(1)
		this.setState({
			isOpen:true
		})
	}
	// 选择属性
	chosenode =(val) =>{
		console.log(val)
		this.setState({
			NewdateType:val,
			dateTypeval:val,
			titleName:val
		})
	}
	// 选择日期
	choseDate =(val) =>{
		console.log(val)
		this.setState({
			date:val,
			month:val,
			value:val,
			currentDate:val
		},() =>{
			this.getDateList()
		})
	}
	// 返回
	area = () =>{
		this.props.history.push({
			pathname: '/Sarguments',
		});
	}
	render() {
		let {views,monthdate,title,content,allTypeList,nodeList,isOpen,NewdateType,yearMonths,dateTypeval} = this.state
		const handleSelectEvent =(event) =>{
			console.log(event)
			let list = []
			list.push(event)
			console.log(list)
			// this.getDay(event.start,event.end)
			
			const newArr = list.map(({id,start,end,title, ...rest}) => rest)
			console.log(newArr);
			let newList = this.getDay(event.start,event.end)
			const lastItem = newList.pop();
			console.log(newList);
			this.setState({
				dateList:newList
			})
			const sameItems = [];
			console.log(allTypeList)
			console.log(newList)
			for (let i = 0; i < allTypeList.length; i++) {
			  for (let j = 0; j < newList.length; j++) {
			    if (allTypeList[i].date === newList[j]) {
					sameItems.push(allTypeList[i]);
					break;
			    }
			  }
			}
			
			// console.log(sameItems); // 输出 [3, 5]
			const weekday = [7, 1, 2, 3, 4,5,6];
			let newList1 = this.getDay(event.start,event.end)
			const lastItem1= newList1.pop();
			console.log(newList1);
			let list1 = []
			
			for(var i=0;i<newList1.length;i++){
				let day = newList1[i].substring(5,7)
				console.log(newList1[i].substring(5,7))
				// if(newList[i].substr(8,10).charAt(0) === '0'){
				// 	alert(0)
				// }
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
			console.log(list1)
			this.setState({
				isModalOpen:true,
				title:event.title
			},() =>{
				http.post('demand_resp/calendar/convertDateStr',list1
				).then(res =>{
					console.log(res)
					if(res.data.code==200){
						this.setState({
							content:res.data.data
						})
					}
				}).catch(err =>{
					console.log(err)
				})
				
			})
			
			// window.alert(event.title)
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
		    // setIsModalOpen(false);
			this.setState({
				isModalOpen:false
			})
		};
		
		const handleCancel = () => {
		    // setIsModalOpen(false);
			this.setState({
				isModalOpen:false
			})
		};
		// 编辑
		const handleOk1 = () => {
		    // setIsModalOpen(false);
			// console.log(this.state.dateList)
			this.setState({
				isOpen:false
			},() =>{
				http.post('demand_resp/calendar/editDateType',{
					"dateList": this.state.dateList,
					"dateType": this.state.NewdateType
				}).then(res =>{
					console.log(res)
					if(res.data.code==200){
						this.setState({
							dateTypeval:''
						},() =>{
							message.success('编辑成功')
							this.getDateList()
							
						})
						
					}else{
						message.info(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
			})
		};
		const handleCancel1 =() =>{
			this.setState({
				isOpen:false,
				dateTypeval:''
			})
		}
		const handleSelectSlot =({ event, start, end, allDay }) =>{
			this.setState({
				dateTypeOpen:true,
				start:dayjs(start).format('YYYY-MM-DD'),
				end:dayjs(end).format('YYYY-MM-DD'),
			})
			
			let start1 = dayjs(start).format('YYYY-MM-DD')
			let end1 = dayjs(end).format('YYYY-MM-DD')
			
		}
		
		const handleToday = () => {
			// onNavigate('TODAY')
			this.setState({
				date:dayjs().format('YYYY-MM'),
				value:dayjs().format('YYYY-MM'),
				month:dayjs().format('YYYY-MM'),
			},() =>{
				this.getDateList()
			})
		}
		// 属性编辑
		const handleOk2 =() =>{
			
			
			const weekday = [7, 1, 2, 3, 4,5,6];
			let newList = this.getDay(this.state.start,this.state.end)
			const lastItem = newList.pop();
			console.log(newList);
			let list = []
			
			for(var i=0;i<newList.length;i++){
				let day = newList[i].substring(5,7)
				console.log(newList[i].substring(5,7))
				// if(newList[i].substr(8,10).charAt(0) === '0'){
				// 	alert(0)
				// }
				list.push({
					dateType:this.state.titleName,
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
			this.setState({
				events: [
					...this.state.events,
					{
						start:this.state.start,
						end:this.state.end,
						title:this.state.titleName==1?'工作日':this.state.titleName==2?'休息日' :this.state.titleName==3?'删除计算日' :
						this.state.titleName==4?'元旦' :this.state.titleName==5?'春节' :this.state.titleName==6?'清明节' :this.state.titleName==7?'劳动节' 
						:this.state.titleName==8?'端午节' :this.state.titleName==9?'中秋节':'国庆节',
					},
				],
				dateTypeOpen:false
			},() =>{
				console.log(this.state.titleName)
				http.post('demand_resp/calendar/editDateType',{
					"dateList": newList,
					"dateType": this.state.titleName
				}).then(res =>{
					console.log(res)
					if(res.data.code==200){
						this.setState({
							dateTypeval:''
						},() =>{
							message.success('编辑成功')
							this.getDateList()
						})
						
					}else{
						message.info(res.data.msg)
					}
				}).catch(err =>{
					console.log(err)
				})
			})
			
		}
		const handleCancel2 =() =>{
			this.setState({
				dateTypeOpen:false,
				dateTypeval:''
			})
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
			// alert(0)
			console.log(eventInfo.event)
			// console.log(data)
			let event = eventInfo.event._def
			let dayDate = eventInfo.event._instance.range
			console.log(dayDate)
			console.log(dayjs(dayDate.start).format('YYYY-MM-DD'))
			let start = dayjs(dayDate.start).format('YYYY-MM-DD')
			let end = dayjs(dayDate.end).format('YYYY-MM-DD')
			let newList = []
			newList.push(start)
			newList.push(end)
			this.setState({
				dateList:newList
			})
			const sameItems = [];
			console.log(allTypeList)
			console.log(newList)
			for (let i = 0; i < allTypeList.length; i++) {
			  for (let j = 0; j < newList.length; j++) {
			    if (allTypeList[i].date === newList[j]) {
					sameItems.push(allTypeList[i]);
					break;
			    }
			  }
			}
			
			// console.log(sameItems); // 输出 [3, 5]
			const weekday = [7, 1, 2, 3, 4,5,6];
			let newList1 = this.getDay(start,end)
			const lastItem1= newList1.pop();
			console.log(newList1);
			let list1 = []
			
			for(var i=0;i<newList1.length;i++){
				let day = newList1[i].substring(5,7)
				console.log(newList1[i].substring(5,7))
				// if(newList[i].substr(8,10).charAt(0) === '0'){
				// 	alert(0)
				// }
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
			console.log(list1)
			this.setState({
				isModalOpen:true,
				title:event.title
			},() =>{
				http.post('demand_resp/calendar/convertDateStr',list1
				).then(res =>{
					console.log(res)
					if(res.data.code==200){
						this.setState({
							content:res.data.data
						})
					}
				}).catch(err =>{
					console.log(err)
				})
				
			})
		}
		const selectClick =(event ) =>{
			// alert(1)
			console.log(event )
			this.setState({
				start:event.startStr,
				end:event.endStr,
				dateTypeOpen:true
			})
			
		}
		const eventDrop =() =>{
			// alert(2)
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
				<div className="newDatetitle"><a style={{color:'#FFF'}}  onClick={this.area}>
				<LeftCircleOutlined style={{marginRight:10}} />返回</a></div>
				<div className="newDate">
					
					<div className="newDateName">
					<Button type="button" ghost onClick={handleToday}>
						今天
					</Button>
						<Select
							// placeholder=''
							style={{width:'119px',marginLeft:20}}
							onChange={this.choseDate}
							value={this.state.value}
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
						events={this.state.events}
						eventContent={renderEventContent}
						eventClick={eventClick} // 事项点击事件
						dayClick ={selectClick}
						//设置是否可被单击或者拖动选择
						selectable= {true}
						select={selectClick} //选中日历格事件
						editable 	//可以拖拽
						customButtons={{
								  // 自定义按钮触发回调函数
							// getToday: { text: "今天", click: getToday },
							getNext: { text: ">", click: getNext },
							getPrev: { text: "<", click: getPrev },
							// month: { text: "月", click: month },
							// week: { text: "周", click: week },
							// list: { text: "列表", click: list }
						}}
						headerToolbar={{
						          left: "today", // 左侧按钮展示为下方自定义按钮名称
						          center: "title", // 时间展示
						          // right: "month" // 右侧按钮展示
						        }}
						date={this.state.currentDate}
						
						
					/>
					
					<Modal title="属性编辑" visible={this.state.dateTypeOpen} 
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
							onChange={this.chosenode}
							value={dateTypeval}
						 >
						 
							{
								nodeList.length &&nodeList.map(res =>{
									return <Option key={res.value} value={res.value}>{res.label}</Option>
								})
							}
						
						</Select>
					</Modal>
					<Modal title={title} mask={false} visible={this.state.isModalOpen} onOk={handleOk} onCancel={handleCancel}
						footer={null} centered
						
					>
						
						<p className="titlename">{content}</p>
						<div className="blockName">
							<Button type="text" 
								onClick={this.edit}
							style={{color:'#FFF',background:'#0092FF',marginTop:40}} block ghost>
							      编辑
							</Button>
						</div>
					</Modal>
					<Modal title="编辑" 
					centered visible={this.state.isOpen} onOk={handleOk1} onCancel={handleCancel1}
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
							onChange={this.chosenode}
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
}
export default  newDate




// <select onChange={handleViewChange}>
						
// 					</select>