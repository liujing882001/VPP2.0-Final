import React from 'react'
import { NavLink, useLocation } from 'react-router-dom'
import { motion } from 'framer-motion'
import { 
  Home, 
  Zap, 
  Activity,
  Cloud,
  Settings,
  BarChart3,
  Cpu,
  Battery,
  X
} from 'lucide-react'
import { clsx } from 'clsx'

interface SidebarProps {
  isOpen: boolean
  onToggle: () => void
}

interface NavItem {
  name: string
  href: string
  icon: React.ComponentType<any>
  badge?: string
  children?: NavItem[]
}

const navigationItems: NavItem[] = [
  {
    name: '总览仪表盘',
    href: '/dashboard',
    icon: Home,
  },
  {
    name: 'PowerGen 发电管理',
    href: '/powergen',
    icon: Zap,
    badge: 'AI',
    children: [
      {
        name: '发电概览',
        href: '/powergen',
        icon: BarChart3,
      },
      {
        name: '设备管理',
        href: '/powergen/devices',
        icon: Cpu,
      },
      {
        name: 'AI功率预测',
        href: '/powergen/forecast',
        icon: Activity,
      },
      {
        name: '储能优化',
        href: '/powergen/storage',
        icon: Battery,
      },
    ]
  },
  {
    name: 'SmartLoad 用能管理',
    href: '/smartload',
    icon: Activity,
    badge: 'Smart',
  },
  {
    name: 'VPPCloud 虚拟电厂',
    href: '/vppcloud',
    icon: Cloud,
    badge: 'VPP',
  },
  {
    name: '系统设置',
    href: '/settings',
    icon: Settings,
  },
]

const Sidebar: React.FC<SidebarProps> = ({ isOpen, onToggle }) => {
  const location = useLocation()

  return (
    <>
      {/* 移动端遮罩 */}
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onClick={onToggle}
          className="fixed inset-0 z-40 bg-black bg-opacity-50 lg:hidden"
        />
      )}

      {/* 侧边栏 */}
      <motion.aside
        initial={false}
        animate={{
          x: isOpen ? 0 : '-100%',
        }}
        transition={{
          type: 'spring',
          stiffness: 300,
          damping: 30,
        }}
        className={clsx(
          'fixed inset-y-0 left-0 z-50 w-64 bg-white dark:bg-dark-900',
          'border-r border-gray-200 dark:border-dark-700',
          'transform transition-transform duration-200 ease-in-out',
          'lg:relative lg:translate-x-0'
        )}
      >
        {/* 顶部logo区域 */}
        <div className="flex items-center justify-between h-16 px-4 border-b border-gray-200 dark:border-dark-700">
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-600 rounded-lg flex items-center justify-center">
              <Zap className="w-5 h-5 text-white" />
            </div>
            <div>
              <h1 className="text-lg font-bold text-gray-900 dark:text-white">
                DeepEngine
              </h1>
              <p className="text-xs text-gray-500 dark:text-dark-400">
                AI原生能源平台
              </p>
            </div>
          </div>
          
          {/* 移动端关闭按钮 */}
          <button
            onClick={onToggle}
            className="lg:hidden p-1 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100 dark:hover:bg-dark-800"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* 导航菜单 */}
        <nav className="flex-1 px-4 py-6 space-y-2 overflow-y-auto scrollbar-thin">
          {navigationItems.map((item) => (
            <div key={item.name}>
              <NavLink
                to={item.href}
                className={({ isActive }) =>
                  clsx(
                    'group flex items-center px-3 py-2 text-sm font-medium rounded-lg transition-all duration-200',
                    isActive || location.pathname.startsWith(item.href)
                      ? 'bg-primary-50 text-primary-700 dark:bg-primary-900/20 dark:text-primary-400'
                      : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50 dark:text-dark-400 dark:hover:text-dark-100 dark:hover:bg-dark-800'
                  )
                }
              >
                <item.icon 
                  className={clsx(
                    'mr-3 flex-shrink-0 h-5 w-5',
                    location.pathname === item.href || location.pathname.startsWith(item.href)
                      ? 'text-primary-500'
                      : 'text-gray-400 group-hover:text-gray-500 dark:text-dark-500 dark:group-hover:text-dark-400'
                  )} 
                />
                <span className="flex-1">{item.name}</span>
                {item.badge && (
                  <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800 dark:bg-primary-900/30 dark:text-primary-400">
                    {item.badge}
                  </span>
                )}
              </NavLink>

              {/* 子菜单 */}
              {item.children && (location.pathname === item.href || location.pathname.startsWith(item.href)) && (
                <motion.div
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  transition={{ duration: 0.2 }}
                  className="ml-6 mt-2 space-y-1"
                >
                  {item.children.map((child) => (
                    <NavLink
                      key={child.name}
                      to={child.href}
                      className={({ isActive }) =>
                        clsx(
                          'group flex items-center px-3 py-2 text-sm font-medium rounded-lg transition-all duration-200',
                          isActive
                            ? 'bg-primary-50 text-primary-700 dark:bg-primary-900/20 dark:text-primary-400'
                            : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50 dark:text-dark-400 dark:hover:text-dark-100 dark:hover:bg-dark-800'
                        )
                      }
                    >
                      <child.icon 
                        className={clsx(
                          'mr-3 flex-shrink-0 h-4 w-4',
                          location.pathname === child.href
                            ? 'text-primary-500'
                            : 'text-gray-400 group-hover:text-gray-500 dark:text-dark-500 dark:group-hover:text-dark-400'
                        )} 
                      />
                      <span className="flex-1">{child.name}</span>
                    </NavLink>
                  ))}
                </motion.div>
              )}
            </div>
          ))}
        </nav>

        {/* 底部信息 */}
        <div className="p-4 border-t border-gray-200 dark:border-dark-700">
          <div className="flex items-center space-x-3 p-3 bg-gray-50 dark:bg-dark-800 rounded-lg">
            <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse" />
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-gray-900 dark:text-dark-100">
                系统运行正常
              </p>
              <p className="text-xs text-gray-500 dark:text-dark-400 truncate">
                99.9% 可用性
              </p>
            </div>
          </div>
        </div>
      </motion.aside>
    </>
  )
}

export default Sidebar 