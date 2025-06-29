/* eslint-disable react-hooks/exhaustive-deps */
import Typed from 'typed.js';
import { useState,useRef,useEffect } from 'react'
import './index.scss'

export const RecommendStrategy = (props) => {
    const { recommendList,onSend1,radioValue } = props || {}

    return (
        <div className='profit-recommend-strategy-wrap'>  
            {
                recommendList?.map((item,i) => {
                    if(i > 2){
                        return null
                    }else{
                        return <div className='recommend-strategy-item' onClick={() => onSend1(item.name,item.id,radioValue)}>{item?.name}</div>
                    }
                })
            }
        </div>
    )
}