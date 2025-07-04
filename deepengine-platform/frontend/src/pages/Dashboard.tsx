import React from 'react'
import { motion } from 'framer-motion'
import { 
  Zap, 
  Activity, 
  Battery, 
  TrendingUp, 
  AlertTriangle,
  CheckCircle,
  ArrowUpRight,
  ArrowDownRight,
  BarChart3,
  PieChart,
  Users,
  Building2
} from 'lucide-react'
import { LineChart, Line, AreaChart, Area, PieChart as RechartsPieChart, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'

// 模拟数据
const energyData = [
  { time: '00:00', solar: 0, wind: 45, storage: 20, load: 65 },
  { time: '04:00', solar: 0, wind: 52, storage: 15, load: 58 },
  { time: '08:00', solar: 35, wind: 48, storage: 25, load: 78 },
  { time: '12:00', solar: 85, wind: 42, storage: 30, load: 92 },
  { time: '16:00', solar: 65, wind: 38, storage: 35, load: 88 },
  { time: '20:00', solar: 15, wind: 55, storage: 28, load: 82 },
  { time: '24:00', solar: 0, wind: 48, storage: 22, load: 68 }
]

const pieData = [
  { name: '光伏发电', value: 45, color: '#f59e0b' },
  { name: '风力发电', value: 35, color: '#06b6d4' },
  { name: '储能放电', value: 20, color: '#8b5cf6' }
]

const Dashboard: React.FC = () => {
  const stats = [
    {
      name: '总发电量',
      value: '2,847',
      unit: 'kWh',
      change: '+12.5%',
      changeType: 'positive',
      icon: Zap,
      color: 'text-energy-solar',
      bgColor: 'bg-yellow-50 dark:bg-yellow-900/20'
    },
    {
      name: '实时功率',
      value: '1,234',
      unit: 'kW',
      change: '+8.2%',
      changeType: 'positive',
      icon: Activity,
      color: 'text-energy-wind',
      bgColor: 'bg-cyan-50 dark:bg-cyan-900/20'
    },
    {
      name: '储能容量',
      value: '856',
      unit: 'kWh',
      change: '-3.1%',
      changeType: 'negative',
      icon: Battery,
      color: 'text-energy-storage',
      bgColor: 'bg-purple-50 dark:bg-purple-900/20'
    },
    {
      name: '系统效率',
      value: '94.2',
      unit: '%',
      change: '+2.8%',
      changeType: 'positive',
      icon: TrendingUp,
      color: 'text-energy-grid',
      bgColor: 'bg-green-50 dark:bg-green-900/20'
    }
  ]

  const systemStatus = [
    { name: '太阳能系统', status: 'online', count: 12, total: 12 },
    { name: '风力发电', status: 'online', count: 8, total: 8 },
    { name: '储能系统', status: 'warning', count: 5, total: 6 },
    { name: '负荷设备', status: 'online', count: 45, total: 48 }
  ]

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  }

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: {
        type: 'spring',
        stiffness: 300,
        damping: 24
      }
    }
  }

  return (
    <motion.div
      variants={containerVariants}
      initial="hidden"
      animate="visible"
      className="space-y-6"
    >
      {/* 页面标题 */}
      <motion.div variants={itemVariants} className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-dark-100">
            能源管理总览
          </h1>
          <p className="text-gray-600 dark:text-dark-400 mt-1">
            实时监控分布式能源系统运行状态
          </p>
        </div>
        <div className="flex items-center space-x-3">
          <div className="flex items-center space-x-2 text-sm text-gray-500 dark:text-dark-400">
            <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse" />
            <span>系统正常运行</span>
          </div>
          <button className="btn btn-primary btn-md">
            生成报告
          </button>
        </div>
      </motion.div>

      {/* 关键指标卡片 */}
      <motion.div variants={itemVariants} className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => (
          <motion.div
            key={stat.name}
            variants={itemVariants}
            whileHover={{ scale: 1.02, boxShadow: '0 10px 25px rgba(0,0,0,0.1)' }}
            className="card p-6"
          >
            <div className="flex items-center justify-between">
              <div className={`p-3 rounded-lg ${stat.bgColor}`}>
                <stat.icon className={`w-6 h-6 ${stat.color}`} />
              </div>
              <div className={`flex items-center text-sm font-medium ${
                stat.changeType === 'positive' ? 'text-green-600' : 'text-red-600'
              }`}>
                {stat.changeType === 'positive' ? (
                  <ArrowUpRight className="w-4 h-4 mr-1" />
                ) : (
                  <ArrowDownRight className="w-4 h-4 mr-1" />
                )}
                {stat.change}
              </div>
            </div>
            <div className="mt-4">
              <div className="flex items-baseline space-x-2">
                <span className="text-2xl font-bold text-gray-900 dark:text-dark-100">
                  {stat.value}
                </span>
                <span className="text-sm text-gray-500 dark:text-dark-400">
                  {stat.unit}
                </span>
              </div>
              <p className="text-sm text-gray-600 dark:text-dark-400 mt-1">
                {stat.name}
              </p>
            </div>
          </motion.div>
        ))}
      </motion.div>

      {/* 图表区域 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 能源趋势图 */}
        <motion.div variants={itemVariants} className="lg:col-span-2 card p-6">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-dark-100">
              能源生产与消耗趋势
            </h3>
            <div className="flex items-center space-x-4 text-sm">
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-energy-solar rounded-full" />
                <span className="text-gray-600 dark:text-dark-400">光伏</span>
              </div>
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-energy-wind rounded-full" />
                <span className="text-gray-600 dark:text-dark-400">风电</span>
              </div>
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-energy-storage rounded-full" />
                <span className="text-gray-600 dark:text-dark-400">储能</span>
              </div>
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-energy-load rounded-full" />
                <span className="text-gray-600 dark:text-dark-400">负荷</span>
              </div>
            </div>
          </div>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={energyData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="time" stroke="#6b7280" />
                <YAxis stroke="#6b7280" />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: 'white', 
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px',
                    boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)'
                  }}
                />
                <Area type="monotone" dataKey="solar" stackId="1" stroke="#f59e0b" fill="#f59e0b" fillOpacity={0.6} />
                <Area type="monotone" dataKey="wind" stackId="1" stroke="#06b6d4" fill="#06b6d4" fillOpacity={0.6} />
                <Area type="monotone" dataKey="storage" stackId="1" stroke="#8b5cf6" fill="#8b5cf6" fillOpacity={0.6} />
                <Line type="monotone" dataKey="load" stroke="#ef4444" strokeWidth={3} strokeDasharray="5 5" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* 能源构成饼图 */}
        <motion.div variants={itemVariants} className="card p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-dark-100 mb-6">
            发电构成
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <RechartsPieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  innerRadius={40}
                  outerRadius={80}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {pieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip 
                  formatter={(value) => [`${value}%`, '占比']}
                  contentStyle={{ 
                    backgroundColor: 'white', 
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px'
                  }}
                />
              </RechartsPieChart>
            </ResponsiveContainer>
          </div>
          <div className="mt-4 space-y-2">
            {pieData.map((item, index) => (
              <div key={index} className="flex items-center justify-between text-sm">
                <div className="flex items-center space-x-2">
                  <div 
                    className="w-3 h-3 rounded-full" 
                    style={{ backgroundColor: item.color }}
                  />
                  <span className="text-gray-600 dark:text-dark-400">{item.name}</span>
                </div>
                <span className="font-medium text-gray-900 dark:text-dark-100">
                  {item.value}%
                </span>
              </div>
            ))}
          </div>
        </motion.div>

        {/* 系统状态 */}
        <motion.div variants={itemVariants} className="card p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-dark-100 mb-6">
            系统状态
          </h3>
          <div className="space-y-4">
            {systemStatus.map((system, index) => (
              <div key={index} className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className={`w-2 h-2 rounded-full ${
                    system.status === 'online' ? 'bg-green-400' :
                    system.status === 'warning' ? 'bg-yellow-400' : 'bg-red-400'
                  }`} />
                  <span className="text-sm font-medium text-gray-900 dark:text-dark-100">
                    {system.name}
                  </span>
                </div>
                <div className="text-sm text-gray-500 dark:text-dark-400">
                  {system.count}/{system.total}
                </div>
              </div>
            ))}
          </div>
        </motion.div>

        {/* AI预测建议 */}
        <motion.div variants={itemVariants} className="lg:col-span-2 card p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-dark-100 mb-6">
            AI智能建议
          </h3>
          <div className="space-y-4">
            <div className="flex items-start space-x-3 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
              <CheckCircle className="w-5 h-5 text-blue-500 mt-0.5" />
              <div>
                <h4 className="font-medium text-blue-900 dark:text-blue-100">
                  储能充电优化
                </h4>
                <p className="text-sm text-blue-700 dark:text-blue-300 mt-1">
                  建议在10:00-14:00期间增加储能充电功率，预计可提升整体效率8%
                </p>
              </div>
            </div>
            <div className="flex items-start space-x-3 p-4 bg-green-50 dark:bg-green-900/20 rounded-lg">
              <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
              <div>
                <h4 className="font-medium text-green-900 dark:text-green-100">
                  负荷调度建议
                </h4>
                <p className="text-sm text-green-700 dark:text-green-300 mt-1">
                  明日14:00-16:00光伏发电峰值期，建议调度高耗能设备运行
                </p>
              </div>
            </div>
            <div className="flex items-start space-x-3 p-4 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg">
              <AlertTriangle className="w-5 h-5 text-yellow-500 mt-0.5" />
              <div>
                <h4 className="font-medium text-yellow-900 dark:text-yellow-100">
                  维护提醒
                </h4>
                <p className="text-sm text-yellow-700 dark:text-yellow-300 mt-1">
                  风机WF-003运行时间超过5000小时，建议进行定期维护检查
                </p>
              </div>
            </div>
          </div>
        </motion.div>
      </div>

      {/* 快速操作面板 */}
      <motion.div variants={itemVariants} className="card p-6">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-dark-100 mb-6">
          快速操作
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <button className="flex items-center justify-center space-x-2 p-4 border-2 border-dashed border-gray-300 dark:border-dark-600 rounded-lg hover:border-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20 transition-colors">
            <BarChart3 className="w-5 h-5 text-gray-400" />
            <span className="text-sm font-medium text-gray-600 dark:text-dark-400">生成分析报告</span>
          </button>
          <button className="flex items-center justify-center space-x-2 p-4 border-2 border-dashed border-gray-300 dark:border-dark-600 rounded-lg hover:border-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20 transition-colors">
            <PieChart className="w-5 h-5 text-gray-400" />
            <span className="text-sm font-medium text-gray-600 dark:text-dark-400">策略优化</span>
          </button>
          <button className="flex items-center justify-center space-x-2 p-4 border-2 border-dashed border-gray-300 dark:border-dark-600 rounded-lg hover:border-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20 transition-colors">
            <Users className="w-5 h-5 text-gray-400" />
            <span className="text-sm font-medium text-gray-600 dark:text-dark-400">用户管理</span>
          </button>
          <button className="flex items-center justify-center space-x-2 p-4 border-2 border-dashed border-gray-300 dark:border-dark-600 rounded-lg hover:border-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20 transition-colors">
            <Building2 className="w-5 h-5 text-gray-400" />
            <span className="text-sm font-medium text-gray-600 dark:text-dark-400">设备配置</span>
          </button>
        </div>
      </motion.div>
    </motion.div>
  )
}

export default Dashboard 