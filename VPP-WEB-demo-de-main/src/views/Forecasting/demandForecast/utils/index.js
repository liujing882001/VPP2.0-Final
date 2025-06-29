export const mapStrategyName = (key) => {
    const obj = {
        '3da72e052a0b48759b0f4633df42235a': '深圳某工业设备新材料股份有限公司',
        '176c0991f24e30c2b25a9dbf1185b7b9': '深圳泰伦广场A座',
        '5eb413037ba16ea6108c12e0d6353be3': '深圳泰伦广场B座'
    }
    return obj[key]
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


export const isTimeDifferenceTwoHours = (timeStr1, timeStr2) => {
    const date1 = new Date(timeStr1);
    const date2 = new Date(timeStr2);
    const diff = Math.abs(date2.getTime() - date1.getTime());
    return diff > 2.5 * 60 * 60 * 1000; // 2 hours in milliseconds
}

export const renderResponseType = (type) => {
    if (type === 1) {
        return '削峰响应'
    } else if (type === 2) {
        return '填谷响应'
    } else {
        return '-'
    }
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

export const renderNodeStatus = (status) => {
    switch (status) {
        case 11:
            return '待申报'
        case 12:
            return '待申报'
        case 15:
            return '不参加'
        case 21:
            return '待出清'
        case 22:
            return '已出清'
        case 24:
            return '执行中'
        case 25:
            return '已完成'
        default:
            return '-'
    }

}


// 示例用法

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
        startTime: date.split(' ')[1],
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

export const renderDeclareLoad = (nodeList) => {
    let declareLoad = 0
    if(nodeList && nodeList?.length > 0){
        nodeList.forEach((item) => {
            if(renderNodeStatus(item?.drsStatus) ==='待申报'){
                declareLoad += Number(item?.declareLoad)
            } 
        })
    }
    return declareLoad
}

export const renderAdjustLoad = (nodeList) => {
    let adjustLoad = 0
    if(nodeList && nodeList?.length > 0){
        nodeList.forEach((item) => {
            if(renderNodeStatus(item?.drsStatus) ==='待申报'){
                adjustLoad += Number(item?.adjustLoad)
            } 
        })
    }
    
    return adjustLoad
}