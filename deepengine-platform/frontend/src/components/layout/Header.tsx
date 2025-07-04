import React from 'react'
import { motion } from 'framer-motion'
import { 
  Menu, 
  Search, 
  Bell, 
  Sun, 
  Moon, 
  User,
  ChevronDown,
  Settings,
  LogOut,
  HelpCircle
} from 'lucide-react'
import { clsx } from 'clsx'

interface HeaderProps {
  onToggleSidebar: () => void
  onToggleDarkMode: () => void
  darkMode: boolean
  user: any | null
}

const Header: React.FC<HeaderProps> = ({ 
  onToggleSidebar, 
  onToggleDarkMode, 
  darkMode, 
  user 
}) => {
  const [searchValue, setSearchValue] = React.useState('')
  const [showUserMenu, setShowUserMenu] = React.useState(false)
  const [showNotifications, setShowNotifications] = React.useState(false)

  // 模拟通知数据
  const notifications = [
    {
      id: 1,
      title: 'AI预测完成',
      message: '光伏功率预测已更新，预测精度98.5%',
      time: '2分钟前',
      type: 'success',
      unread: true
    },
    {
      id: 2,
      title: '储能优化建议',
      message: '建议在14:00-16:00期间进行储能充电',
      time: '10分钟前',
      type: 'info',
      unread: true
    },
    {
      id: 3,
      title: '设备维护提醒',
      message: '风机WF-001需要进行定期维护检查',
      time: '1小时前',
      type: 'warning',
      unread: false
    }
  ]

  const unreadCount = notifications.filter(n => n.unread).length

  return (
    <header className="bg-white dark:bg-dark-900 border-b border-gray-200 dark:border-dark-700 px-6 py-4">
      <div className="flex items-center justify-between">
        {/* 左侧：菜单按钮和搜索 */}
        <div className="flex items-center space-x-4">
          {/* 侧边栏切换按钮 */}
          <button
            onClick={onToggleSidebar}
            className="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 dark:hover:bg-dark-800 transition-colors"
          >
            <Menu className="w-5 h-5" />
          </button>

          {/* 搜索框 */}
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Search className="h-4 w-4 text-gray-400" />
            </div>
            <input
              type="text"
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              placeholder="搜索设备、数据或功能..."
              className="block w-80 pl-10 pr-3 py-2 border border-gray-300 rounded-lg text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 dark:bg-dark-800 dark:border-dark-600 dark:text-dark-100 dark:placeholder-dark-400"
            />
          </div>
        </div>

        {/* 右侧：通知、主题切换、用户菜单 */}
        <div className="flex items-center space-x-4">
          {/* 主题切换按钮 */}
          <button
            onClick={onToggleDarkMode}
            className="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 dark:hover:bg-dark-800 transition-colors"
            title={darkMode ? '切换到亮色模式' : '切换到暗色模式'}
          >
            <motion.div
              initial={false}
              animate={{ rotate: darkMode ? 180 : 0 }}
              transition={{ duration: 0.3 }}
            >
              {darkMode ? (
                <Sun className="w-5 h-5" />
              ) : (
                <Moon className="w-5 h-5" />
              )}
            </motion.div>
          </button>

          {/* 通知按钮 */}
          <div className="relative">
            <button
              onClick={() => setShowNotifications(!showNotifications)}
              className="relative p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 dark:hover:bg-dark-800 transition-colors"
            >
              <Bell className="w-5 h-5" />
              {unreadCount > 0 && (
                <motion.span
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center"
                >
                  {unreadCount}
                </motion.span>
              )}
            </button>

            {/* 通知下拉菜单 */}
            {showNotifications && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                className="absolute right-0 mt-2 w-80 bg-white dark:bg-dark-800 rounded-lg shadow-lg border border-gray-200 dark:border-dark-700 z-50"
              >
                <div className="p-4 border-b border-gray-200 dark:border-dark-700">
                  <h3 className="text-sm font-medium text-gray-900 dark:text-dark-100">
                    通知中心
                  </h3>
                </div>
                <div className="max-h-64 overflow-y-auto">
                  {notifications.map((notification) => (
                    <div
                      key={notification.id}
                      className={clsx(
                        'p-4 border-b border-gray-100 dark:border-dark-700 hover:bg-gray-50 dark:hover:bg-dark-700 cursor-pointer',
                        notification.unread && 'bg-primary-50 dark:bg-primary-900/10'
                      )}
                    >
                      <div className="flex items-start space-x-3">
                        <div className={clsx(
                          'w-2 h-2 rounded-full mt-2',
                          notification.type === 'success' && 'bg-green-400',
                          notification.type === 'info' && 'bg-blue-400',
                          notification.type === 'warning' && 'bg-yellow-400',
                          notification.type === 'error' && 'bg-red-400'
                        )} />
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-gray-900 dark:text-dark-100">
                            {notification.title}
                          </p>
                          <p className="text-sm text-gray-500 dark:text-dark-400 mt-1">
                            {notification.message}
                          </p>
                          <p className="text-xs text-gray-400 dark:text-dark-500 mt-1">
                            {notification.time}
                          </p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="p-4 text-center">
                  <button className="text-sm text-primary-600 hover:text-primary-700 dark:text-primary-400">
                    查看全部通知
                  </button>
                </div>
              </motion.div>
            )}
          </div>

          {/* 用户菜单 */}
          <div className="relative">
            <button
              onClick={() => setShowUserMenu(!showUserMenu)}
              className="flex items-center space-x-2 p-2 rounded-lg text-gray-700 hover:bg-gray-100 dark:text-dark-300 dark:hover:bg-dark-800 transition-colors"
            >
              <div className="w-8 h-8 bg-primary-600 rounded-full flex items-center justify-center">
                <User className="w-4 h-4 text-white" />
              </div>
              <div className="hidden md:block text-left">
                <p className="text-sm font-medium">
                  {user?.name || '管理员'}
                </p>
                <p className="text-xs text-gray-500 dark:text-dark-400">
                  {user?.role || '系统管理员'}
                </p>
              </div>
              <ChevronDown className="w-4 h-4" />
            </button>

            {/* 用户下拉菜单 */}
            {showUserMenu && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                className="absolute right-0 mt-2 w-48 bg-white dark:bg-dark-800 rounded-lg shadow-lg border border-gray-200 dark:border-dark-700 z-50"
              >
                <div className="py-2">
                  <a
                    href="#"
                    className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-dark-300 dark:hover:bg-dark-700"
                  >
                    <User className="w-4 h-4 mr-3" />
                    个人资料
                  </a>
                  <a
                    href="#"
                    className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-dark-300 dark:hover:bg-dark-700"
                  >
                    <Settings className="w-4 h-4 mr-3" />
                    系统设置
                  </a>
                  <a
                    href="#"
                    className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-dark-300 dark:hover:bg-dark-700"
                  >
                    <HelpCircle className="w-4 h-4 mr-3" />
                    帮助中心
                  </a>
                  <hr className="my-2 border-gray-200 dark:border-dark-700" />
                  <a
                    href="#"
                    className="flex items-center px-4 py-2 text-sm text-red-600 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                  >
                    <LogOut className="w-4 h-4 mr-3" />
                    退出登录
                  </a>
                </div>
              </motion.div>
            )}
          </div>
        </div>
      </div>

      {/* 点击外部关闭下拉菜单 */}
      {(showUserMenu || showNotifications) && (
        <div
          className="fixed inset-0 z-40"
          onClick={() => {
            setShowUserMenu(false)
            setShowNotifications(false)
          }}
        />
      )}
    </header>
  )
}

export default Header 