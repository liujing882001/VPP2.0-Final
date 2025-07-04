import React, { Suspense, useState } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'

// Layout components
import Sidebar from '@/components/layout/Sidebar'
import Header from '@/components/layout/Header'
import LoadingSpinner from '@/components/ui/LoadingSpinner'

// Page components (lazy loaded for code splitting)
const Dashboard = React.lazy(() => import('@/pages/Dashboard'))
const PowerGenDashboard = React.lazy(() => import('@/pages/powergen/Dashboard'))
const PowerGenDevices = React.lazy(() => import('@/pages/powergen/Devices'))
const PowerGenForecast = React.lazy(() => import('@/pages/powergen/Forecast'))
const SmartLoadDashboard = React.lazy(() => import('@/pages/smartload/Dashboard'))
const VPPCloudDashboard = React.lazy(() => import('@/pages/vppcloud/Dashboard'))
const Settings = React.lazy(() => import('@/pages/Settings'))

// Types
interface AppState {
  sidebarOpen: boolean
  darkMode: boolean
  user: any | null
}

function App() {
  const [state, setState] = useState<AppState>({
    sidebarOpen: true,
    darkMode: localStorage.getItem('darkMode') === 'true',
    user: null, // TODO: 从认证服务获取用户信息
  })

  // 切换侧边栏
  const toggleSidebar = () => {
    setState(prev => ({ ...prev, sidebarOpen: !prev.sidebarOpen }))
  }

  // 切换暗色模式
  const toggleDarkMode = () => {
    const newDarkMode = !state.darkMode
    setState(prev => ({ ...prev, darkMode: newDarkMode }))
    localStorage.setItem('darkMode', newDarkMode.toString())
    
    if (newDarkMode) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  // 初始化暗色模式
  React.useEffect(() => {
    if (state.darkMode) {
      document.documentElement.classList.add('dark')
    }
  }, [])

  // 页面过渡动画配置
  const pageVariants = {
    initial: { opacity: 0, y: 20 },
    in: { opacity: 1, y: 0 },
    out: { opacity: 0, y: -20 }
  }

  const pageTransition = {
    type: 'tween',
    ease: 'anticipate',
    duration: 0.4
  }

  return (
    <div className="h-full flex bg-gray-50 dark:bg-dark-900">
      {/* 侧边栏 */}
      <Sidebar 
        isOpen={state.sidebarOpen}
        onToggle={toggleSidebar}
      />

      {/* 主内容区域 */}
      <div className={`flex-1 flex flex-col transition-all duration-200 ${
        state.sidebarOpen ? 'ml-64' : 'ml-0'
      }`}>
        {/* 顶部导航栏 */}
        <Header
          onToggleSidebar={toggleSidebar}
          onToggleDarkMode={toggleDarkMode}
          darkMode={state.darkMode}
          user={state.user}
        />

        {/* 页面内容 */}
        <main className="flex-1 p-6 overflow-auto">
          <Suspense fallback={
            <div className="flex items-center justify-center h-full">
              <LoadingSpinner size="lg" />
            </div>
          }>
            <AnimatePresence mode="wait">
              <Routes>
                {/* 默认重定向到仪表盘 */}
                <Route path="/" element={<Navigate to="/dashboard" replace />} />
                
                {/* 主仪表盘 */}
                <Route 
                  path="/dashboard" 
                  element={
                    <motion.div
                      initial="initial"
                      animate="in"
                      exit="out"
                      variants={pageVariants}
                      transition={pageTransition}
                    >
                      <Dashboard />
                    </motion.div>
                  } 
                />

                {/* PowerGen 智能发电管理 */}
                <Route path="/powergen">
                  <Route 
                    index 
                    element={
                      <motion.div
                        initial="initial"
                        animate="in"
                        exit="out"
                        variants={pageVariants}
                        transition={pageTransition}
                      >
                        <PowerGenDashboard />
                      </motion.div>
                    } 
                  />
                  <Route 
                    path="devices" 
                    element={
                      <motion.div
                        initial="initial"
                        animate="in"
                        exit="out"
                        variants={pageVariants}
                        transition={pageTransition}
                      >
                        <PowerGenDevices />
                      </motion.div>
                    } 
                  />
                  <Route 
                    path="forecast" 
                    element={
                      <motion.div
                        initial="initial"
                        animate="in"
                        exit="out"
                        variants={pageVariants}
                        transition={pageTransition}
                      >
                        <PowerGenForecast />
                      </motion.div>
                    } 
                  />
                </Route>

                {/* SmartLoad 智慧用能管理 */}
                <Route 
                  path="/smartload" 
                  element={
                    <motion.div
                      initial="initial"
                      animate="in"
                      exit="out"
                      variants={pageVariants}
                      transition={pageTransition}
                    >
                      <SmartLoadDashboard />
                    </motion.div>
                  } 
                />

                {/* VPPCloud 虚拟电厂 */}
                <Route 
                  path="/vppcloud" 
                  element={
                    <motion.div
                      initial="initial"
                      animate="in"
                      exit="out"
                      variants={pageVariants}
                      transition={pageTransition}
                    >
                      <VPPCloudDashboard />
                    </motion.div>
                  } 
                />

                {/* 设置页面 */}
                <Route 
                  path="/settings" 
                  element={
                    <motion.div
                      initial="initial"
                      animate="in"
                      exit="out"
                      variants={pageVariants}
                      transition={pageTransition}
                    >
                      <Settings />
                    </motion.div>
                  } 
                />

                {/* 404 页面 */}
                <Route 
                  path="*" 
                  element={
                    <motion.div
                      initial="initial"
                      animate="in"
                      exit="out"
                      variants={pageVariants}
                      transition={pageTransition}
                      className="flex items-center justify-center h-full"
                    >
                      <div className="text-center">
                        <h1 className="text-4xl font-bold text-gray-900 dark:text-dark-100 mb-4">
                          404
                        </h1>
                        <p className="text-lg text-gray-600 dark:text-dark-400 mb-8">
                          页面未找到
                        </p>
                        <button 
                          onClick={() => window.history.back()}
                          className="btn btn-primary btn-lg"
                        >
                          返回上一页
                        </button>
                      </div>
                    </motion.div>
                  } 
                />
              </Routes>
            </AnimatePresence>
          </Suspense>
        </main>
      </div>
    </div>
  )
}

export default App 