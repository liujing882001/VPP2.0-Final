import { create } from "zustand";
import { generateUUID } from "../untils/generateUUID.js";

const chatStore = create()((set, get) => ({
  chatRecordList: [
    {
      content: "Hi,我是能源小助手,可以帮你分析各类能源问题",
      role: "assistant",
      status: "pass",
      requestId: generateUUID(),
    },
  ],

  setChatRecordList: (recordList) => {
    set(() => {
      return {
        chatRecordList: recordList,
      };
    });
  },

  addChatRecord: (data) => {
    set((state) => {
      return {
        chatRecordList: [...state.chatRecordList, data],
      };
    });
  },

  initChatRecordList: () => {
    set(() => {
      return {
        chatRecordList: [
          {
            content: "Hi,我是能源小助手,可以帮你分析各类能源问题",
            role: "assistant",
            status: "pass",
            requestId: generateUUID(),
          },
        ],
      };
    });
  },

  setChatRecord: (data) =>
    set((state) => {
      const list = state.chatRecordList?.map((item) => {
        if (item.requestId === data.requestId && item.role === "assistant") {
          return { ...data };
        } else {
          return { ...item };
        }
      });
      return {
        chatRecordList: list,
      };
    }),
}));

export default chatStore;
