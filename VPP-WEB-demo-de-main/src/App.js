import React, {
  useEffect,
  useState,
  Suspense,
  useContext,
  useRef,
} from "react";
import {
  BrowserRouter,
  Route,
  Redirect,
  Switch,
  useLocation,
} from "react-router-dom";
import RouteMap from "./routes/index.js";
import { Layout, ConfigProvider, Spin, Drawer } from "antd";
import SiderCustom from "./views/Index/sildermenu.js";
// 根据环境变量选择头部组件
// 产投版本: header-chantou.js, 标准版本: header.js
import Header1 from "./views/Index/header.js";
import FrontendAuth from "./FrontendAuth";
import { routerMap } from "./routerMap.js";
import dayjs from "dayjs";
import http from "./server/server.js";
import zhCN from "antd/es/locale/zh_CN";
import "dayjs/locale/zh-cn"; // 切换为中文 locale
import { AssistMode } from "./views/Index/components/assist-mode/index.js";
import { SearchMode } from "./views/Index/components/search-mode/index.js";
import "./xiaoda.scss";
import { XiaoDa } from "./components/xiaoda/index.js";
const { Header, Footer, Sider, Content } = Layout;

const App = (props) => {
  const [isLogin, setIsLogin] = useState(false);
  const [openLogin, setOpenLogin] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);
  const [isAssistMode, setIsAssistMode] = useState(false);
  const [isSearchMode, setIsSearchMode] = useState(false);
  const [coplitAuthList, setCoplitAuthList] = useState([]);
  const [openKeysval, setOpenKeysval] = useState("/generating");
  const [compent, setCompent] = useState("");
  const contentStyle: React.CSSProperties = {};
  const [isExpand, setIsExpand] = useState(false);
  const isExit = useRef(false);
  const isMouseUp = useRef(false);
  const [isScrolling, setIsScrolling] = useState(false);
  const [isXiaoDaExpand, setIsXiaoDaExpand] = useState(false);

  let draggable;
  // 记录偏移量
  let offsetX = 0;
  let offsetY = 0;
  const [isDemandPage, setIsDemandPage] = useState(false);
  const [isEnergyPage, setIsEnergyPage] = useState(false);
  const [isTradePage, setIsTradePage] = useState(false);
  const [isProfitPage, setIsProfitPage] = useState(false);
  const [isProfitManagePage, setIsProfitManagePage] = useState(false);
  const [isgenerating, setIsgenerating] = useState(false);
  const [isShowBtn, setIsShowBtn] = useState(true);
  const [open, setOpen] = useState(false);
  const topRef = useRef(null);
  const leftRef = useRef(null);
  // 存储拖拽时的偏移量

  let isDragging = false;
  let moveThreshold = 10;
  let startX = 0;
  let startY = 0;
  let location = useLocation();
  const isLoginbtn = (val) => {
    setIsLogin(val);
  };
  const toggle = (val) => {
    setIsExpanded(val);
    console.log("1111");
  };
  const openKeysvalbtn = (val) => {
    setOpenKeysval(val);
  };
  const isAssistModebtn = (val) => {
    console.log(val);
    setIsAssistMode(val);
  };
  const isSearchModebtn = (val) => {
    setIsSearchMode(val);
  };
  const opencompant = (val) => {
    console.log(val);
    setCompent(val);
  };
  useEffect(() => {
    if (location?.pathname === "/generating") {
      setIsgenerating(true);
    } else {
      setIsgenerating(false);
    }
  }, [location]);

  useEffect(() => {
    if (document.getElementById("draggable")) {
      draggable = document.getElementById("draggable");
      draggable.addEventListener("dragstart", function (e) {
        const dragImg = new Image(0, 0);
        dragImg.src =
          "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";
        e.dataTransfer.setDragImage(dragImg, 0, 0);

        // 记录鼠标相对元素的位置
        offsetX = e.clientX - draggable.getBoundingClientRect().left;
        offsetY = e.clientY - draggable.getBoundingClientRect().top;

        // 设置拖拽的样式（可选）
        draggable.style.cursor = "pointer";
      });
      // 拖拽中
      draggable.addEventListener("drag", function (e) {
        if (e.clientX && e.clientY) {
          // 更新元素的位置
          const left = e.clientX - offsetX;
          const top = e.clientY - offsetY;

          draggable.style.left = `${left}px`;
          draggable.style.top = `${top}px`;
          leftRef.current = left;
          topRef.current = top;
        }
      });

      // 拖拽结束
      draggable.addEventListener("dragend", function (e) {
        // 获取屏幕宽度
        const screenWidth = window.innerWidth;

        // 获取元素的边界信息
        const draggableRect = draggable.getBoundingClientRect();
        const distanceToLeft = draggableRect.left;
        const distanceToRight = screenWidth - draggableRect.right;

        // 判断是否靠近屏幕的左边或右边，如果是，吸附
        if (distanceToLeft < 50) {
          draggable.style.left = "0"; // 吸附到左边
          leftRef.current = "0";
        } else if (distanceToRight < 50) {
          draggable.style.left = `${screenWidth - draggableRect.width}px`; // 吸附到右边
          leftRef.current = screenWidth - draggableRect.width;
        } else {
          // 如果距离两边都比较远，可以保持当前位置（可选择是否做其他效果）
          // draggable.style.opacity = 1;
        }
      });
      draggable.addEventListener("dragover", function (e) {
        e.preventDefault();
      });

      draggable.addEventListener("touchstart", function (e) {
        let touch = e.touches[0];
        offsetX = touch.clientX - draggable.offsetLeft;
        offsetY = touch.clientY - draggable.offsetTop;
        startX = touch.clientX;
        startY = touch.clientY;
        // 防止页面滚动
        e.preventDefault();
      });
      // 拖拽中
      draggable.addEventListener("touchmove", function (e) {
        // 只处理一个触摸点
        if (e.touches.length > 1) return;

        let touch = e.touches[0];
        let newX = touch.clientX - offsetX;
        let newY = touch.clientY - offsetY;

        // 判断触摸移动是否超过阈值，超过则认为是拖动
        if (
          Math.abs(touch.clientX - startX) > moveThreshold ||
          Math.abs(touch.clientY - startY) > moveThreshold
        ) {
          isDragging = true;
          draggable.style.left = newX + "px";
          draggable.style.top = newY + "px";
        }

        // 防止页面滚动
        e.preventDefault();
      });

      // 拖拽结束
      draggable.addEventListener("touchend", function (e) {
        if (!isDragging) {
          // 如果没有发生拖动，则触发点击事件
          setOpen(true);
        }

        // 重置拖动状态
        isDragging = false;
        e.preventDefault();
      });
    }
  });

  const onClose = () => {
    setOpen(false);
  };

  return (
    <>
      {location.pathname !== "/" ? (
        <div
          draggable="true"
          id="draggable"
          style={{
            width: "80px",
            height: "80px",
            display: "flex",
            position: "absolute",
            cursor: "pointer",
            left: leftRef.current === null ? "60vw" : leftRef.current + "px",
            top: topRef.current === null ? "50vh" : topRef.current + "px",
            zIndex: 9999,
          }}
          onClick={() => {
            if (!isDragging) {
              setOpen(true);
            }
          }}
        >
          <i className="xiaoda-icon" />
          <i className="xiaoda-icon-bottom" />
        </div>
      ) : null}

      {/* <Drawer
        placement="right"
        closable={false}
        onClose={onClose}
        open={open}
        key="right"
        className="xiaoda-drawer"
      >
        <XiaoDa />
      </Drawer> */}

      {open ? (
        <XiaoDa
          setOpen={setOpen}
          isScrolling={isScrolling}
          setIsScrolling={setIsScrolling}
          setIsXiaoDaExpand={setIsXiaoDaExpand}
          isXiaoDaExpand={isXiaoDaExpand}
        />
      ) : null}

      <ConfigProvider
        locale={zhCN}
        theme={{
          token: {
            colorBorder: "#FFF",
            colorIcon: "#FFF",
            colorBorderHover: "#000",
            borderRadius: 2,
            colorText: "#FFF",
            colorPrimary: "#00b96b",
          },
          components: {
            DatePicker: {
              cellActiveWithRangeBg: "rgba(0, 146, 255, .1)",
              cellHoverWithRangeBg: "#312D36",
              colorText: "#FFF",
              cellHoverBg: "rgba(255, 255, 255, 0.05)",
            },
            Table: {
              headerBg: "#312D36",
              headerColor: "#FFF",
              headerSplitColor: "#000",
              footerColor: "#FFF",
              borderColor: "none",
              headerSplitColor: "nonesss",
            },
            Select: {
              clearBg: "#FFF",
              optionSelectedBg: "#0092FF",
              optionSelectedColor: "#FFF",
              selectorBg: "#312D36",
              optionSelectedFontWeight: "undefined",
              colorText: "#FFF",
              multipleItemBg: "#312D36",
              lineFocus: "#312D36",
            },

            Cascader: {
              optionSelectedBg: "rgba(0, 146, 255, .1)",
              optionSelectedFontWeight: "undefined",
              colorIcon: "#FFF",
            },
            Modal: {
              contentBg: "#212029",
              headerBg: "#212029",
              titleColor: "#FFF",
            },
            Input: {
              hoverBg: "none",
              activeBg: "none",
              activeBorderColor: "#0092FF",
              warningActiveShadow: "none",
              colorWarning: "#000",
            },
            Button: {
              ghostHoverBorderColor: "#000",
              // defaultHoverBorderColor
            },
            Radio: {
              buttonBg: "#000",
              buttonCheckedBg: "#0092FF",
              buttonSolidCheckedBg: "#0092FF",
            },
            Tree: {
              nodeSelectedBg: "#0092FF",
              nodeHoverBg: "#FFF",
            },
          },
        }}
      >
        <Layout>
          {
            // 头部导航
            isLogin && (
              <Header1
                toggle={toggle}
                collapsed={isExpanded}
                isAssistModebtn={isAssistModebtn}
                openKeysvalbtn={openKeysvalbtn}
                isSearchModebtn={isSearchModebtn}
                isSearchMode={isSearchMode}
                isAssistMode={isAssistMode}
                isExpand={isExpand}
                setIsExpand={setIsExpand}
                setIsAssistMode={setIsAssistMode}
                isShowBtn={isShowBtn}
                isExit={isExit}
                setOpen={setOpen}
                open={open}
                isXiaoDaExpand={isXiaoDaExpand}
              />
            )
          }

          <Layout>
            {
              // 菜单
              isLogin && !isAssistMode ? (
                <SiderCustom
                  collapsed={isExpanded}
                  trigger={null}
                  style={{ overflowY: "auto" }}
                  className={isExpanded ? "bgblue" : "bgred"}
                  openKeysvalbtn={openKeysvalbtn}
                  opencompant={opencompant}
                  isAssistModebtn={isAssistModebtn}
                  setIsShowBtn={setIsShowBtn}
                />
              ) : (
                ""
              )
            }

            <Suspense
              fallback={
                <Spin
                  className="spin-center"
                  style={{ width: "100%", height: "100%" }}
                />
              }
            >
              {isAssistMode ? (
                <AssistMode
                  coplitAuthList={coplitAuthList}
                  isDemandPage={isDemandPage}
                  setIsDemandPage={setIsDemandPage}
                  isEnergyPage={isEnergyPage}
                  setIsEnergyPage={setIsEnergyPage}
                  isTradePage={isTradePage}
                  setIsTradePage={setIsTradePage}
                  isProfitPage={isProfitPage}
                  setIsProfitPage={setIsProfitPage}
                  isProfitManagePage={isProfitManagePage}
                  setIsProfitManagePage={setIsProfitManagePage}
                  isAssistModebtn={isAssistModebtn}
                  isExit={isExit}
                />
              ) : (
                ""
              )}
              {isSearchMode ? (
                <SearchMode
                  isSearchModebtn={isSearchModebtn}
                  isAssistModebtn={isAssistModebtn}
                  isExpand={isExpand}
                  setIsExpand={setIsExpand}
                  setIsDemandPage={setIsDemandPage}
                  setIsEnergyPage={setIsEnergyPage}
                  setIsTradePage={setIsTradePage}
                  setIsProfitPage={setIsProfitPage}
                  setIsProfitManagePage={setIsProfitManagePage}
                />
              ) : (
                ""
              )}
              <Content
                style={contentStyle}
                className="NewContent"
                id="NewContent"
                style={{
                  padding:
                    openKeysval == "/generating" ||
                    openKeysval == "/" ||
                    isAssistMode
                      ? "0px 0px 0px 0px"
                      : "16px 16px 0px 16px",
                }}
              >
                <div id="App">
                  <Switch>
                    <FrontendAuth
                      compent={compent}
                      isLoginbtn={isLoginbtn}
                      routerConfig={routerMap}
                    />
                  </Switch>
                </div>
              </Content>
            </Suspense>
          </Layout>
        </Layout>
      </ConfigProvider>
    </>
  );
};

export default App;
