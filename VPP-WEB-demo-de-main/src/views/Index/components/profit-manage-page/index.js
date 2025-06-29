import "./index.scss";
import { Input } from "antd";
import React, { useEffect, useRef, useState } from "react";
import Typed from "typed.js";
import http, { RequestUrl } from "../../../../server/server";
import axios from "axios";
import { Tips } from "./components/tips";
import { generateUUID } from "./utils/generateUUID";
import { ChatMessage } from "./components/chat-message";
import chatStore from "./store";
import { useScroll } from "./hooks/useScroll.js";
const { TextArea } = Input;

export const ProfitManagePage = (props) => {
  const { setIsProfitManagePage } = props;
  const [prompt, setPrompt] = useState("");
  const [showTip, setShowTip] = useState(false);
  const [fetchController, setFetchController] = useState(null);
  const [isShowStopBtn, setIsShowStopBtn] = useState(false);
  const showRegenerateBtn = useRef(false);
  const message1 = useRef(null);
  const scrollRef = useRef(null);
  const { scrollToBottom } = useScroll(scrollRef.current);
  const [tipList, setTipList] = useState([]);
  const [sessionIdRet, setSessionIdRet] = useState("");
  let typed1 = "";
  const { setChatRecordList, chatRecordList, setChatRecord, addChatRecord } =
    chatStore();

  const init = async () => {
    const res = await http.post("/revenueManage/queryHint", {
      requestId: null,
      sessionId: null,
      query: null,
      queryCount: null,
      system: null,
    });
    setTipList(res.data.data);

    if (res.data.data?.length > 0) {
      setShowTip(true);
      if (message1.current) {
        typed1 = new Typed(message1?.current, {
          strings: [
            "你好，资产运营Copilot可以帮你分析你的资产收益情况与电量情况",
          ],
          typeSpeed: 10,
          backSpeed: 50,
          showCursor: true,
          cursorChar: "",
          onComplete(self) {
            self.cursor.style.display = "none"; // 隐藏光标
            setShowTip(true);
            //     const list = [...prestate]
            //     list.pop()

            //     return [...list,{
            //         index:0,
            //         dom:  (
            //             <div className='energy-left-default-wrap'>
            //                 <i className='energy-left-robot-icon'/>
            //                 <div className='energy-right-wrap'>
            //                     <div className='energy-left-message'>
            //                         <span>你想要查看哪个节点的调度策略？</span>
            //                     </div>
            //                     {
            //                         <div className='recommend-strategy-wrap'>
            //                             <Tips
            //                                 str1='111111'
            //                                 str2='222222'
            //                                 str3='333333'
            //                             />
            //                         </div>
            //                     }

            //                 </div>

            //             </div>
            //         )
            //     }]
            // })
          },
        });
      }
    }
  };

  const changText = async () => {
    setShowTip(false);
    const res = await http.post("/revenueManage/queryHint", {
      requestId: null,
      sessionId: null,
      query: null,
      queryCount: null,
      system: null,
    });
    setTipList(res.data.data);
    if (res.data.data.length > 0) {
      setShowTip(true);
    }
  };

  const isValidJSON = (text) => {
    if (!isNaN(Number(text))) {
      return false;
    }
    try {
      JSON.parse(text);
      return true;
    } catch (error) {
      return false;
    }
  };

  useEffect(() => {
    init();
    return () => {
      setChatRecordList([]);
    };
  }, []);

  const doConversation = async (content, item, sessionId) => {
    showRegenerateBtn.current = false;
    setIsShowStopBtn(true);
    const controller = new AbortController();
    const signal = controller.signal;
    const userRequestId = generateUUID();
    const assistantMessageId = item?.requestId || generateUUID();
    if (item?.requestId) {
      setChatRecord({
        requestId: assistantMessageId,
        content: "",
        status: "loading",
        role: "assistant",
      });
    } else {
      addChatRecord({
        content: content,
        role: "user",
        status: "pass",
        requestId: userRequestId,
        isOver: false,
      });
      addChatRecord({
        content: "",
        role: "assistant",
        status: "loading",
        requestId: assistantMessageId,
        isOver: false,
      });
    }

    setFetchController(controller);
    const errorRes = (text) => {
      const maxTextLength = text.length;
      let charIndex = 0;
      let textContent = "";
      const writeChar = () => {
        if (charIndex < maxTextLength) {
          textContent += text.charAt(charIndex);
          charIndex++;
          setChatRecord({
            requestId: assistantMessageId,
            role: "assistant",
            status: "pass",
            content: textContent,
            isOver: true,
          });
          if (charIndex < maxTextLength) {
            setTimeout(writeChar, 10);
          }
        }
      };
      writeChar();
      setTimeout(() => {
        setIsShowStopBtn(false);
        scrollToBottom();
      }, 10 * maxTextLength);
    };
    try {
      // 发送请求

      let response = await fetch(`${RequestUrl}revenueChat/chat`, {
        method: "post",
        responseType: "stream",
        headers: {
          "Content-Type": "application/json",
          Authorization: window.sessionStorage.getItem("tokens"),
          AuthorizationCode: window.sessionStorage.getItem("userid"),
        },
        signal: signal,
        body: JSON.stringify({
          requestId: assistantMessageId,
          sessionId: sessionId || "",
          input: content,
          system: "收益管理",
        }),
      });
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }

      const reader = response?.body?.getReader?.();
      const decoder = new TextDecoder();
      let buffer = "";
      let allContent = "";
      let endChartData = [];
      let session = "";
      const processChunk = () => {
        reader
          ?.read()
          .then(({ done, value }) => {
            if (done) {
              setChatRecord({
                content: allContent,
                role: "assistant",
                status: "pass",
                requestId: assistantMessageId,
                endChartData: endChartData,
                isOver: true,
                isStop: false,
              });
              setIsShowStopBtn(false);
              scrollToBottom();
              setSessionIdRet(session);
              return;
            }
            buffer += decoder.decode(value, { stream: true });
            const messages = buffer.split("\n\n");
            buffer = messages.pop() || "";
            messages.forEach((message, i) => {
              setTimeout(() => {
                const jsonData = message.substring(5);
                const obj = JSON.parse(jsonData);
                const textValue = obj.text;
                console.log(textValue, "textValue");
                if (!session) {
                  session = obj.sessionId;
                }
                if (isValidJSON(textValue)) {
                  endChartData.push(textValue);
                } else {
                  allContent += textValue ? textValue : "";
                }
                setChatRecord({
                  content: allContent,
                  role: "assistant",
                  status: "pass",
                  requestId: assistantMessageId,
                  isOver: false,
                  isStop: false,
                });
                scrollToBottom();
              }, (i + 1) * 20);
            });
            setTimeout(() => {
              processChunk();
            }, (messages.length + 1) * 20);
          })
          .catch((error) => {
            setChatRecord({
              content: allContent,
              role: "assistant",
              status: "pass",
              requestId: assistantMessageId,
              isOver: true,
              isStop: true,
            });
            scrollToBottom();
          });
      };
      processChunk();
    } catch (err) {
      errorRes("不好意思，出现一些问题，可以过会再问我一遍。");
    }
  };

  return (
    <>
      <div className="profit-manage-page">
        <div className="profit-manage-page-header">
          <span
            className="profit-manage-page-header-title"
            onClick={() => {
              setIsProfitManagePage(false);
            }}
          >
            AI虚拟电厂Copilot
          </span>
        </div>
        <div className="profit-chat-page-content">
          <div
            className="scroll-content"
            style={{
              width: "947px",
              margin: "0 auto",
              height: "800px",
              padding: "20px 0",
            }}
            ref={scrollRef}
          >
            <div className="default-chat-tips-wrap">
              <i className="ai-icon" />
              <div className="default-chat-tips" ref={message1}></div>
            </div>
            {showTip ? (
              <Tips
                str1={tipList[0]?.prompt}
                str2={tipList[1]?.prompt}
                str3={tipList[2]?.prompt}
                isShowStopBtn={isShowStopBtn}
                click={(content) => {
                  doConversation(content, null, sessionIdRet);
                }}
                changText={changText}
              />
            ) : null}

            {chatRecordList?.map((item, i) => {
              return (
                <>
                  <ChatMessage
                    content={item.content}
                    position={item.role === "user" ? "right" : "left"}
                    status={item.status}
                    isOver={item.isOver}
                    isStop={item.isStop}
                    endChartData={item.endChartData}
                    showRegenerateBtn={showRegenerateBtn.current}
                    doConversation={(str) =>
                      doConversation(str, null, sessionIdRet)
                    }
                    changText={changText}
                    scrollToBottom={scrollToBottom}
                    lastIndex={i === chatRecordList?.length - 1}
                    onRefurbishChatMessage={() => {
                      const { content } =
                        chatRecordList[chatRecordList?.length - 2];
                      doConversation(content, item, sessionIdRet);
                    }}
                  />
                </>
              );
            })}
          </div>
          <div className="chat-bottom-wrap">
            {isShowStopBtn ? (
              <div className="chat-stop-btn-wrap">
                <i
                  className="chat-stop-btn"
                  onClick={() => {
                    showRegenerateBtn.current = true;
                    setIsShowStopBtn(false);
                    fetchController?.abort();
                    setFetchController(null);
                    scrollToBottom();
                  }}
                />
              </div>
            ) : null}

            <div className="chat-bottom-textarea">
              <TextArea
                value={prompt}
                placeholder="请输入你的问题"
                color="#fff"
                autoSize={{
                  maxRows: 4,
                  minRows: 1,
                }}
                onChange={(e) => {
                  setPrompt(e.target.value);
                }}
                onPressEnter={(e) => {
                  e.preventDefault(); //禁止回车的默认换行
                  if (e.target?.value === "" || e.target.value === "/n") {
                    return;
                  }
                  if (!prompt) {
                    return;
                  }
                  if (isShowStopBtn) {
                    return;
                  }
                  doConversation(prompt, null, sessionIdRet);
                  setTimeout(() => {
                    setPrompt("");
                  }, 200);
                }}
              />
              <i
                className="send-icon"
                style={
                  isShowStopBtn ? { pointerEvents: "none", opacity: ".6" } : {}
                }
                onClick={() => {
                  setPrompt("");
                  doConversation(prompt, null, sessionIdRet);
                }}
              />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
