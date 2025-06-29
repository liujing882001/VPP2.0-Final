
import { Button, Modal,DatePicker,Input,Radio } from 'antd';
import './index.scss'
import dayjs from 'dayjs';

const { RangePicker } = DatePicker
const {Search} = Input
const ReportModal = (props) => {
    const { setIsTipsModalOpen,isTipsModalOpen } = props || {}
    const dateFormat = 'YYYY/MM/DD';
    const onChange = (date, dateString) => {
        console.log(date, dateString);
      };
    const handleCancel = () => {
      setIsTipsModalOpen(false);
    };

    const onChange1 = (e) => {
        console.log('radio checked', e.target.value);
    };
    return (
        <Modal className="report-modal" visible={isTipsModalOpen} footer={null} title="提示" onCancel={handleCancel}>
           <p className='tips-content'>是否确认按当前的申报负荷进行申报？</p>
           <div className='btn-wrapper'>
                <Button className='cancel-btn' onClick={handleCancel}>
                    取消
                </Button>
                <Button className='ok-btn' onClick={handleCancel}>
                    确定
                </Button>
            </div>
        </Modal>
    );
};
export default ReportModal;