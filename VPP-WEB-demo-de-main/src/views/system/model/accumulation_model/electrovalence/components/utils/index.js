export const renderResponseType = (type) => {
    if (type === 1) {
        return '削峰响应'
    } else if (type === 2) {
        return '填谷响应'
    } else {
        return '-'
    }
}



export const pairwiseSum = (arr) => {
    return arr.reduce((result, value, index, array) => {
      if (index > 0) {
        result.push(`${array[index - 1]}-${value}`);
      }
      return result;
    }, []);
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


export const renderResponseLevel = (type) => {
    if (type === 1) {
        return '日前响应'
    } else if (type === 2) {
        return '小时响应'
    } else if (type === 3) {
        return '分钟响应'
    } else if (type === 4) {
        return '秒级响应'
    }
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


export const formatDate = (objDate) => {
    const timestamp = new Date(objDate);
    const year = timestamp.getFullYear();
    const month = (timestamp.getMonth() + 1).toString().padStart(2, '0'); // Adding 1 because getMonth() returns zero-based month
    const day = timestamp.getDate().toString().padStart(2, '0');
    const hours = timestamp.getHours().toString().padStart(2, '0');
    const minutes = timestamp.getMinutes().toString().padStart(2, '0');
    const formattedDate = `${year}-${month}-${day} ${hours}:${minutes}`;
    return formattedDate;
}

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


export const renderResponseStatus = (dstatus, declareStatus) => {
    if(!declareStatus){
        if(dstatus === 1){
            return '待申报'
        }
        if(dstatus === 2){
            return '执行中'
        }
        if(dstatus === 3){
            return '已完成'
        }
        if(dstatus === 4 || dstatus === 0){
            return '不参加'
        }
    }

    if(!dstatus){
        if(declareStatus === 1){
            return '待申报'
        }
        if(declareStatus === 2){
            return '待出清'
        }
        if(declareStatus === 3){
            return '已出清'
        }  
    }

    if(declareStatus){
        if(declareStatus === 1 && dstatus === 1){
            return '待申报'
        }
        if(declareStatus === 2 && dstatus === 1){
            return '待出清'
        }
        if(declareStatus === 3 && dstatus === 1){
            return '已出清'
        }
        if(declareStatus === 3 && dstatus === 2){
            return '执行中'
        }
        if(declareStatus === 3 && dstatus === 3){
            return '已完成'
        }
        if(dstatus === 4 || dstatus === 0){
            return '不参加'
        }
    }
    
    return '不参加'
}


export const calcEndDate = (date) => {
    const timestamp = new Date(date);
    const year = timestamp.getFullYear();
    const month = (timestamp.getMonth() + 1).toString().padStart(2, '0'); // Adding 1 because getMonth() returns zero-based month
    const day = timestamp.getDate().toString().padStart(2, '0');
    let hours = ''
    let minutes = ''
    if (Number(timestamp.getHours().toString()) >= 22) {
        hours = '23'
        minutes = '45'
    } else {
        const startDate = new Date(date);
        startDate.setTime(startDate.getTime() + (2.5 * 60 * 60 * 1000));
        hours = (Number(startDate.getHours().toString())).toString().padStart(2, '0');
        minutes = startDate.getMinutes().toString().padStart(2, '0');
    }
    return {
        startTime: date?.split(' ')[1],
        endDate: `${year}-${month}-${day}`,
        endTime: `${hours}:${minutes}`
    }
}   

export const calcNum = (num) => {
    if(isNaN(num)){
        return 0
    }
    return num ? Math.round(num*100) / 100 : 0
}


export const deepTraversal = (data, path = []) => {
    let result = [];
    let num = 1
    data.forEach(item => {
      // 将当前节点的 key 和 value 添加到路径中
      let newPath = [...path, { key: item.value, value: item.label }];
      result.push(newPath);
      if (item.children && item.children.length > 0) {
        num += 1
        result = result.concat(deepTraversal(item.children, newPath));
      }
    });
    console.log(num,'===========')
    return result;
  }
  
export const format = (objDate) => {
    const timestamp = new Date(objDate);
    const year = timestamp.getFullYear();
    const month = (timestamp.getMonth() + 1).toString().padStart(2, '0'); // Adding 1 because getMonth() returns zero-based month
    const day = timestamp.getDate().toString().padStart(2, '0');
    const formattedDate = `${year}-${month}-${day}`;
    return formattedDate;
}


export const getNextDay = (date) => {
    let tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    if (tomorrow.getMonth() !== date.getMonth()) {
        tomorrow.setDate(1);
    }

    return format(tomorrow);
}   