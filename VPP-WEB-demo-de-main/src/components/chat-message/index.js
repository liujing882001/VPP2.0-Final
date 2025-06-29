/* eslint-disable react-hooks/exhaustive-deps */
import { useState, useRef, useEffect, useMemo } from "react";
import MarkdownIt from "markdown-it";
import { message } from "antd";
import "./index.scss";
import React from "react";
import classnames from "classnames";
import hljs from "highlight.js";
import mila from "markdown-it-link-attributes";
import mdKatex from "@traptitech/markdown-it-katex";
import markdownItContainer from "markdown-it-container";
import * as echarts from "echarts";
// import "../../styles/highlight.scss";
// import "../../styles/markdown.scss";
import "../../style/highlight.scss";
import "../../style/markdown.scss";
import { copyToClipboard } from "../../untils/copyToClipboard";
import {EChartsMixedChart} from '../echarts/index.js'
import {Filter} from '../filter/index.js'
import { StatusOne } from '../../views/Index/components/profit-manage-page/components/status-one';
import { StatusThree } from '../../views/Index/components/profit-manage-page/components/status-three';
import http from "../../server/server.js";
import complete from '../../assets/complete.png';
export const ChatMessage = (props) => {
  const {
    content,
    position,
    status,
    isOver,
    isStop,
    showRegenerateBtn,
    onRefurbishChatMessage,
    lastIndex,
    thinkTime,
    thinkContent,
    index,
    fileList,
	argumentsList,
	onChangeExpand,
	endChartData,
	argumentsdata,
	closeaxios,
	webContent
  } = props;

	const [isShowThink, setIsShowThink] = useState(true);
	const markdownBodyRef = useRef(null);
	const markdownBodyRef1 = useRef(null);
	const [selectedTime, setSelectedTime] = useState(null);
	const [selectedNode, setSelectedNode] = useState(null);
	const [options,setOptions] = useState([])
	const [stationId,setStationId] = useState(null);
	const [isvalue, setIsvalue] = useState(null);
	const [isendbtn, setIsendbtn] = useState(false);
	const [currentIndex, setCurrentIndex] = useState(0);
	const [displayItem, setDisplayItem] = useState('');
	const [siteNameItem, setSiteNameItem] = useState('');
	const [isExpanded, setIsExpanded] = useState(false);

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
        const lang = language ?? "";
        return highlightBlock(
          hljs.highlight(code, { language: lang }).value,
          lang,
          code
        );
      }
      return highlightBlock(hljs.highlightAuto(code).value, "", code);
    },
  });
	
  const onCopyOut = (text) => {
    copyToClipboard(text)
      .then(() => {
        message.open({
          type: "success",
          className: "custom-success-message-wrap",
          content: "复制成功",
        });
      })
      .catch(() => {
        message.open({
          className: "custom-success-message-wrap",
          type: "error",
          content: "复制失败",
        });
      });
  };

  // 定义一个插件，处理表格渲染
  mdi.renderer.rules.table_open = function () {
    // 输出一个包含表格的 div
    return '<div class="xiaoda-table-container"><table>';
  };

  // 也可以定义关闭标签的规则
  mdi.renderer.rules.table_close = function () {
    return "</table></div>";
  };

  mdi.use(mila, { attrs: { target: "_blank", rel: "noopener" } });

  mdi.use(mdKatex, {
    blockClass: "katex-block",
    errorColor: " #cc0000",
    output: "mathml",
  });

	mdi.use(markdownItContainer, "chat", {
		render: function (tokens, idx) {
		  const m = tokens[idx].info.trim().match(/^chat\s+(.*)\\n:::\\n/);
		  if (m) {
			if (tokens[idx].nesting === 1) {
			  const data = JSON.parse(m[1]);
			  setTimeout(() => {
				const myChart = echarts.init(
				  document.getElementById("echarts_content_" + data.id)
				);
				myChart.setOption(data.option);
			  }, 1000);
			  return `<div id="echarts_content_${data.id}" style="width: 100%;height: 300px;display: flex;align-items:center;justify-content:center">`;
			} else {
			  return "</div>\n";
			}
		  } else {
			return "</div>\n";
		  }
		},
	});
	// 赋值
	const handleTimeChange = (time) => {
	    setSelectedTime(time);
	};
	const handleNodeChange = (val) => {
	    setSelectedNode(val);
	};
	// 获取节点
	const getqueryStationNode = (url) =>{
		http.get(url+'?category='+'storageEnergy').then(res =>{
			if(res.data.code===200){
				let data = res.data.data
				data?.map(res =>{
					res.value = res.stationId
					res.label = res.stationName
				})
				setOptions(data)
			}
		})
	}
	useEffect(() =>{
		if(options.length>0&&stationId){
			const isValid = options.some(option => option.value === stationId);
			setIsvalue(isValid)
		}
	},[options,stationId])
	const parseContent = (str) => {
	  if (str) {
	    const regex = /{[^{}]+}/g; // 匹配对象的正则表达式
	    const matches = str.match(regex) || []; // 如果没有匹配到，返回空数组
	    const result = matches.map((item) => {
	      // 使用JSON.stringify 先将对象字符串的内容规范化
	      const jsonStr = item.replace(/'/g, '"'); // 替换单引号为双引号
	      try {
	        return JSON.parse(jsonStr); // 转换为对象
	      } catch (error) {
	        console.log(error);
	        return null; // 或者根据你的需求返回其他值
	      }
	    });
	    return result;
	  } else {
	    return [];
	  }
	};
	const toggleExpand = () => {
	    setIsExpanded(!isExpanded);
	  };
	useEffect(() => {
	    const list = parseContent(webContent);
	    if (list.length > 0 && currentIndex < list.length) {
	      const interval = setInterval(() => {
	        setDisplayItem(list[currentIndex]?.snippet);
	        setSiteNameItem(list[currentIndex]?.siteName);
	        setCurrentIndex((prevIndex) => prevIndex + 1);
	      }, 500);
	      if (currentIndex > 0 && currentIndex === list.length - 1) {
	        clearInterval(interval);
	        setIsendbtn(true);
	      }
	      return () => clearInterval(interval);
	    }
	  }, [currentIndex, webContent]);
	const renderText = useMemo(() => {
		const value = content || "";
		const thinkValue = thinkContent || "";
		// const
		if (position === "right") {
		  return (
			<div ref={markdownBodyRef} className="markdown-body">
			  {value}
			</div>
		  );
		}
		const thinkMdHtml = mdi.render(thinkValue || "");
		const lists = parseContent(webContent);
		const renderMdHtml = mdi.render(value || "");
		// const hasTable = renderMdHtml.includes('<table') || thinkMdHtml.includes('<table');
		if (!value && !thinkValue &&!webContent ) {
		  return (
			<>
			  <p className={classnames("assistant-wrap-loading")} style={{ marginBottom: "0",padding :0,height:20 }}>
			  	<div className="loading-gif" style={{marginTop:'-10px',marginLeft:'-10px'}}></div>
			  	
			  </p>
			</>
		  );
		}
		
		// 判断是否展开全屏
		// if(argumentsList  || endChartData?.length ||hasTable){
		// 	onChangeExpand(true)
		// }
		
		
		return (
		  <>
			{
			          // 联网
				webContent ? (
					<div className="networking-interent">
					  {!isendbtn  ? (
						<div className="networking-interent-reading">
						  <img src={complete} />
						  <span>联网查询</span>
						  {displayItem && (
							<div className="networking-item">
							  <p>{displayItem && displayItem}</p>
							  <b> - {siteNameItem}</b>
							</div>
						  )}
						</div>
					  ) : (
						<div>
						  <div
							className={
							  isExpanded
								? 'networking-interent-reading-Read-already'
								: 'networking-interent-reading-Read-already-active'
							}
							onClick={toggleExpand}
						  >
							<img src={complete} />
							<span>已阅读{lists?.length}个网页</span>
							<i></i>
						  </div>
						  {isExpanded && (
							<div className="networking-interent-title">
							  {lists &&
								lists.map((res: any, index: number) => {
								  return (
									<div key={index}>
									  <img src={res?.siteIcon} />
									  <a
										href="#"
										onClick={() => {
										  window.open(res?.url, '_blank');
										}}
									  >
										{res?.name}
									  </a>
									  <span>{res?.siteName}</span>
									</div>
								  );
								})}
							</div>
						  )}
						</div>
					  )}
					</div>
				  ) : null
				}
			{
				argumentsdata&&argumentsdata.length>0  ?(
				<div className="message-echarts">
					<div style={(showRegenerateBtn || isOver) &&
							position === "left" &&
							lastIndex &&
							index ?{}  : {pointerEvents: "none"}}><Filter argumentsList={argumentsList}
						onTimeChange={handleTimeChange}
						onnodeChange={handleNodeChange}
						argumentsdata={argumentsdata}
						
						
					/></div>
					<EChartsMixedChart argumentsList={argumentsList} selectedTime={selectedTime} selectedNode={selectedNode}
					argumentsdata={argumentsdata} 
					 />
				</div>
				):null
			}
			
			{thinkValue ? (
			  <div
				className="think-content"
				style={!value ? { marginBottom: "0" } : {}}
			  >
				<div
				  className="think-header"
				  onClick={() => {
					setIsShowThink(!isShowThink);
				  }}
				>
				  <div className="think-header-left">
					<i className={!value ? "think-loading" : "think-complete"} />
					<span className="think-text">
					  {!value ? (
						"问题思考中，请耐心等待"
					  ) : (
						<>
						  <span style={{ marginRight: "12px" }}>已深度思考</span>
						  <span>(用时{thinkTime}秒)</span>
						</>
					  )}
					</span>
				  </div>
				  <i className={isShowThink ? "up-arrow" : "down-arrow"} />
				</div>
				<div
				  className="think-content-content"
				  style={
					!isShowThink
					  ? { height: 0, overflow: "hidden", padding: "0" }
					  : { height: "auto" }
				  }
				>
				  <div
					ref={markdownBodyRef1}
					id="no-drag"
					className="markdown-body think"
					dangerouslySetInnerHTML={{
					  __html: thinkMdHtml,
					}}
				  />
				</div>
			  </div>
			) : null}
			
			<div
			  ref={markdownBodyRef}
			  id="no-drag"
			  className="markdown-body"
			  dangerouslySetInnerHTML={{
				__html: renderMdHtml,
			  }}
			/>
			
			{
			  position === 'left' && endChartData?.length ? endChartData.map((item) => {
			    const type = JSON.parse(item || '{}')?.type
			    const data = JSON.parse(item || '{}')?.data
			    if(type === 'profit_analysis'){
			      return <StatusOne data={data}/>
			    }else if(type === 'electricity_analysis'){
				  return <StatusThree data={data}/>
				}else{
				  return '';
				}
			  }) : null
			}
			
			{isStop ? <p className="chat-stop-text">用户已停止生成</p> : null}
		  </>
		);
	  }, [
		content,
		position,
		isStop,
		thinkContent,
		thinkTime,
		isShowThink,
		fileList,
		argumentsList,
		selectedTime,
		selectedNode,
		endChartData,
		argumentsdata,
		isvalue,
		showRegenerateBtn,
		isOver,
		lastIndex,
		index,
		webContent,
		isExpanded,
		    displayItem,
		    isendbtn,
	  ]);

	  return (
		<>
		  <div
			className={classnames(
			  position === "left" ? "assistant-wrap" : "user-wrap",
			  status === "loading" ? "assistant-wrap-loading" : ""
			)}
		  >
			{status === "loading" ? (
			  <div className="loading-gif"></div>
			) : (
			  <>{renderText}</>
			)}
			<>
			  {position !== "left" ? (
				<div className="user-copy" onClick={() => onCopyOut(content)}>
				  <i />
				</div>
			  ) : null}
			</>

			{position === "left" && index && isOver ? (
			  <i className="assistant-copy" onClick={() => onCopyOut(content)} />
			) : null}
		  </div>

		  {position === "right" ? (
			<>
			  {(fileList || [])?.length ? (
				<div className="uploadContainer">
				  <div className="uploadContainer_right">
					{(fileList || [])?.map((item, i) => {
					  return (
						<div key={i} className="file_container">
						  <div
							className={classnames("default_icon", {
							  img_icon: item?.fileType !== "pdf",
							  pdf_icon: item?.fileType === "pdf",
							})}
						  ></div>
						  <div className="file_content">
							<div className="file_name">{item?.fileName}</div>
							<div className="file_count">{item?.fileSize}</div>
						  </div>
						</div>
					  );
					})}
				  </div>
				</div>
			  ) : null}
			</>
		  ) : null}

		  {(showRegenerateBtn || isOver) &&
		  position === "left" &&
		  lastIndex &&
		  index ? (
			<span
			  className="regenerate-btn"
			  onClick={() => onRefurbishChatMessage()}
			>
			  重新生成
			</span>
		  ) : null}
		</>
	  );
	};
