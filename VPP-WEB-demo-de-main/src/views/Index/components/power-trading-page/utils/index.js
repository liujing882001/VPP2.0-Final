import moment from 'moment';

export const tableTimer = [
    '00:00',
    '01:00',
    '02:00',
    '03:00',
    '04:00',
    '05:00',
    '06:00',
    '07:00',
    '08:00',
    '09:00',
    '10:00',
    '11:00',
    '12:00',
    '13:00',
    '14:00',
    '15:00',
    '16:00',
    '17:00',
    '18:00',
    '19:00',
    '20:00',
    '21:00',
    '22:00',
    '23:00',
    '24:00'
]

export const daysBetween = (date1, date2) => {
    const oneDay = 24 * 60 * 60 * 1000; // 每天的毫秒数
    const firstDate = new Date(date1);
    const secondDate = new Date(date2);
   
    // 计算时间差
    const diff = Math.abs(firstDate - secondDate);
   
    // 返回差值除以每天的毫秒数
    return Math.round(diff / oneDay);
}
export const isWindows = () => {
    return navigator.userAgent.includes('Windows')
}


export const getLastMonthDate = () => {
    var nowdays = new Date()
    var year = nowdays.getFullYear()
    var month = nowdays.getMonth()
    if (month == 0) {
      month = 12
      year = year - 1
    }
    if (month < 10) {
      month = '0' + month
    }
    var myDate = new Date(year, month, 0)
    // var startDate = year + '-' + month + '-01 00:00:00' //上个月第一天
    var endDate = year + '-' + month + '-' + myDate.getDate() //上个月最后一天
    return new Date(endDate).getTime()
  }


export const betweenDate = (startTime, endTime) =>  {
    //初始化日期列表，数组
    var allDate = new Array();
    var i = 0;
    //开始日期小于等于结束日期,并循环
    while (startTime <= endTime) {
        allDate[i] = startTime;
        //获取开始日期时间戳
        var startTime_ts = new Date(startTime).getTime();
        //增加一天时间戳后的日期
        var next_date = startTime_ts + 24 * 60 * 60 * 1000;
        //拼接年月日，这里的月份会返回（0-11），所以要+1
        var next_dates_y = new Date(next_date).getFullYear() + "-";
        var next_dates_m =
            new Date(next_date).getMonth() + 1 < 10 ?
            "0" + (new Date(next_date).getMonth() + 1) + "-" :
            new Date(next_date).getMonth() + 1 + "-";
        var next_dates_d =
            new Date(next_date).getDate() < 10 ?
            "0" + new Date(next_date).getDate() :
            new Date(next_date).getDate();
        startTime = next_dates_y + next_dates_m + next_dates_d;
        //增加数组key
        i++;
    }
    return allDate
}


export const pairwiseSum = (arr) => {
    return arr.reduce((result, value, index, array) => {
      if (index > 0) {
        result.push(`${array[index - 1]}-${value}`);
      }
      return result;
    }, []);
}
  


export const getTimeSlots = (start, end) => {
  let current = start;
  let slots = [];
  let flag = false
  while (current <= end) {
    let timer =(new Date(current).getHours() < 10 ? ('0' + new Date(current).getHours()) : new Date(current).getHours()) + ':' + (new Date(current).getMinutes() ? new Date(current).getMinutes(): '00')
    if(timer === '00:00' && flag){
      timer = '24:00'
    }
    flag = true
    slots.push(timer);
    current.setMinutes(current.getMinutes() + 15);
  }
 
  return pairwiseSum(slots);
}
export const getCurrentWeekStartAndEnd = () => {
    const now = new Date();
    const currentDayOfWeek = now.getDay(); // 0 表示周日，1 到 6 表示周一到周六
    const startOfWeek = new Date(now);
    const endOfWeek = new Date(now);
 
    // 设置为当前周的周一
    startOfWeek.setDate(startOfWeek.getDate() - (currentDayOfWeek - 1) || 7);
    // 设置为当前周的周日
    endOfWeek.setDate(endOfWeek.getDate() + (7 - currentDayOfWeek) || 7);
 
    // 格式化日期为 YYYY-MM-DD
    function formatDate(date) {
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);
        return `${year}-${month}-${day}`;
    }
 
    return {
        start: formatDate(startOfWeek),
        end: formatDate(endOfWeek)
    };
}
 
export const getTodayDate = () => {
  // 获取今天的日期
  let today = new Date();

  // 获取年、月、日
  let year = today.getFullYear();
  let month = today.getMonth() + 1; // 月份从0开始，所以要加1
  let day = today.getDate();

  // 格式化输出为 YYYY/MM/DD
  let formattedDate = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
  return formattedDate
}

export const getTomorrowDate = () => {
  // Create a new Date object for today
  let today = new Date();

  // Use setHours to set the time to 0:00:00 to avoid DST issues
  today.setHours(0, 0, 0, 0);

  // Get tomorrow's date by adding 1 to the current date
  let tomorrow = new Date(today);
  tomorrow.setDate(today.getDate() + 1);

  // Format the date as YYYY/MM/DD
  let formattedTomorrow = `${tomorrow.getFullYear()}/${(tomorrow.getMonth() + 1).toString().padStart(2, '0')}/${tomorrow.getDate().toString().padStart(2, '0')}`;

  return formattedTomorrow;
}
  
  
export const getBetweenDate = (start,end) => {
  // Define start and end dates
  const startDate = moment(start);
  const endDate = moment(end);

  // Array to store dates
  let datesInRange = [];

  // Loop through each day from startDate to endDate
  let currentDate = startDate;
  while (currentDate <= endDate) {
    datesInRange.push(currentDate.format('YYYY-MM-DD'));
    currentDate = currentDate.clone().add(1, 'days');
  }
  return datesInRange
}