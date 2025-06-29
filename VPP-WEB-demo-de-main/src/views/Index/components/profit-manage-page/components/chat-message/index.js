/* eslint-disable react-hooks/exhaustive-deps */
import Typed from 'typed.js';
import { useState,useRef,useEffect,useMemo} from 'react'
import MarkdownIt from 'markdown-it';
import { message } from 'antd';
import { StatusOne } from '../status-one'
import './index.scss'
import React from 'react';
import classnames from 'classnames';
import hljs from 'highlight.js';
import mila from 'markdown-it-link-attributes';
import mdKatex from '@traptitech/markdown-it-katex';
import markdownItContainer from 'markdown-it-container';
import * as echarts from 'echarts';
import '../../styles/highlight.scss'
import '../../styles/markdown.scss'
import { StatusTwo } from '../status-two';
import { StatusThree } from '../status-three';
import { StatusFour } from '../status-four';
import http from '../../../../../../server/server.js'
import { copyToClipboard } from '../../utils/copyToClipboard.js'
import { Tips } from '../tips'
export const ChatMessage = (props) => {
    const { content,position,status,isOver,isStop,doConversation,showRegenerateBtn,onRefurbishChatMessage,endChartData,scrollToBottom,lastIndex} = props
    const [showTips,setShowTips] = useState(false)
    const markdownBodyRef = useRef(null);
    const [normal,setNormal] = useState(true)
    const [tipList,setTipList] = useState([])
    const highlightBlock = (str, lang, code) => {
        return `<pre class="code-block-wrapper"><div class="code-block-header"><span class="code-block-header__lang">${lang}</span><span class="code-block-header__copy">复制代码</span></div><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
      };
    
    const mdi = new MarkdownIt({
      html: true,
      linkify: false,
      typographer: true,
      breaks: false,
      highlight(code, language) {
        const validLang = !!(language && hljs.getLanguage(language));
        if (validLang) {
          const lang = language ?? '';
          return highlightBlock(hljs.highlight(code, { language: lang }).value, lang, code);
        }
        return highlightBlock(hljs.highlightAuto(code).value, '', code);
      }
    });
    const onCopyOut = (text) => {
      copyToClipboard(text)
        .then(() => {
          message.open({
            type: 'success',
            className: 'custom-success-message-wrap',
            content: '复制成功'
          });
        })
        .catch(() => {
          message.open({
            className: 'custom-success-message-wrap',
            type: 'error',
            content: '复制失败'
          });
        });
    };
  

    // 定义一个插件，处理表格渲染
    mdi.renderer.rules.table_open = function() {
      // 输出一个包含表格的 div
      return '<div class="table-container"><table>';
    };

    // 也可以定义关闭标签的规则
    mdi.renderer.rules.table_close = function() {
      return '</table></div>';
    };
  
    mdi.use(mila, { attrs: { target: '_blank', rel: 'noopener' } });
  
    mdi.use(mdKatex, { blockClass: 'katex-block', errorColor: ' #cc0000', output: 'mathml' });
  
    mdi.use(markdownItContainer, 'chat', {
      render: function (tokens, idx) {
        const m = tokens[idx].info.trim().match(/^chat\s+(.*)\\n:::\\n/);
        if (m) {
          if (tokens[idx].nesting === 1) {
            const data = JSON.parse(m[1]);
            setTimeout(() => {
              const myChart = echarts.init(document.getElementById('echarts_content_' + data.id));
              myChart.setOption(data.option);
            }, 1000);
            return `<div id="echarts_content_${data.id}" style="width: 100%;height: 300px;display: flex;align-items:center;justify-content:center">`;
          } else {
            return '</div>\n';
          }
        } else {
          return '</div>\n';
        }
      }
    });
  
    const renderText = useMemo(() => {
      const value = content || '';
      if (position === 'right') {
        return (
          <div ref={markdownBodyRef} className="markdown-body">
            {value}
          </div>
        );
      }
  
      const renderMdHtml = mdi.render(value || '');
      if (!value) {
        return (
          <>
            <p>正在生成中...</p>
          </>
        );
      }
  
      return (
        <>
          <div
            ref={markdownBodyRef}
            id="no-drag"
            className="markdown-body"
            dangerouslySetInnerHTML={{
              __html: renderMdHtml
            }}
          />
          {isStop ? <p className='chat-stop-text'>用户已停止生成</p> : null}
        </>
      );
    }, [content, position,isStop]);
  
    const init = async () => {
      const res = await http.post('/revenueManage/queryHint',{
        "requestId": null,
        "sessionId":null,
        "query": null,
        "queryCount": null,
        "system": null
      })
      setTipList(res.data.data)
      if(res.data.data.length>0){
        setShowTips(true)
      }   
    }

    const changText = async () => {
      setShowTips(false)
      const res = await http.post('/revenueManage/queryHint',{
          "requestId": null,
          "sessionId":null,
          "query": null,
          "queryCount": null,
          "system": null
      })
      setTipList(res.data.data)
      if(res.data.data.length>0){
        setShowTips(true)
      }   
  }

    useEffect(() => {
      init()
    },[])

    return (
        <>
           <div className={classnames(position==="left" ? "chat-message-left":"chat-message-right")}>
              { position === 'left' ?  
                  <>
                      {!isOver ? <div className='robot-gif'></div>:<div className='robot-icon'></div>}
                  </> : null
              }
              {status === 'loading' ? <div className='loading-gif'></div>: 
              <div style={position==="left" && !normal ? {padding:'0 0 24px 0'}:{}}
                   className={classnames(position==="left" ? "chat-message-content-left":"chat-message-content-right")}
               >
                  { position === "right" ? <div className='chatMessage_content_right_operate' onClick={() => onCopyOut(content || '')}><i/> </div> : null}
                  {position==="left" && !normal ? <div className='special-header'>以下是分析得出的结果</div> : null}
                  <div style={position==="left" && !normal ? {padding:'0 24px 0 24px'}:{}}>
                    {renderText}
                    {
                      position === 'left' && endChartData?.length ? endChartData.map((item) => {
                        const type = JSON.parse(item || '{}')?.type
                        const data = JSON.parse(item || '{}')?.data
                        if(type === 'profit_analysis'){
                          return <StatusTwo data={data}/>
                        }else if(type === 'electricity_analysis'){
                          return <StatusThree data={data}/>
                        }else{
                          return '';
                        }
                      }) : null
                    }
                  
                  </div>
              </div>}
          </div>   
          {showRegenerateBtn && position === 'left' ? <span className='regenerate-btn' onClick={() => onRefurbishChatMessage()}>重新生成</span>
            : null}
          {
            position === 'left' && isOver && showTips && lastIndex ? <Tips   
              str1={tipList[0]?.prompt}
              str2={tipList[1]?.prompt}
              str3={tipList[2]?.prompt}
              click = {
                  (content) => {                
                      doConversation(content)
                  }
            }
            changText={changText} scrollToBottom={scrollToBottom}/> : null
          }
        </>
       
    )
}