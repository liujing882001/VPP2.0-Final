import {
    FrownOutlined
  } from '@ant-design/icons';
import './index.scss'
const Empty = () => {
    return(
        <div className="empty-wrap">
            <FrownOutlined className='empty-icon' />
            <p className='empty-text'>暂无数据</p>
        </div>
    )
    
}

export default Empty;