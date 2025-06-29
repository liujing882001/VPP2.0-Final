import { create } from 'zustand';

const chatStore = create()((set, get) => ({
    chatRecordList: [],

    setChatRecordList: (recordList) => {
      set(() => {
        return {
          chatRecordList: recordList
        };
      });
    },
    addChatRecord: (data) => {
      set((state) => {
        return {
          chatRecordList: [...state.chatRecordList, data]
        };
      });
    },
    setChatRecord: (data) =>
      set((state) => {
        const list = state.chatRecordList?.map((item) => {
          if (item.requestId === data.requestId && item.role === 'assistant') {
            return { ...data };
          } else {
            return { ...item };
          }
        });
        return {
          chatRecordList: list
        };
      })
  }));

  export default chatStore;