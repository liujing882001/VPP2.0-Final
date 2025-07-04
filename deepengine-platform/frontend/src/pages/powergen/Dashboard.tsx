import React from 'react'
import { motion } from 'framer-motion'
import { Zap, Sun, Wind, Battery, TrendingUp, AlertCircle, Activity, Settings } from 'lucide-react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, PieChart, Pie, Cell } from 'recharts'

const PowerGenDashboard: React.FC = () => {
  // 模拟数据
  const forecastData = [
    { time: '00:00', predicted: 0, actual: 0 },
    { time: '06:00', predicted: 15, actual: 12 },
    { time: '12:00', predicted: 85, actual: 89 },
    { time: '18:00', predicted: 35, actual: 38 },
    { time: '24:00', predicted: 0, actual: 0 }
  ]

  const storageData = [
    { name: '储能系统1', capacity: 85, status: 'charging' },
    { name: '储能系统2', capacity: 62, status: 'discharging' },
    { name: '储能系统3', capacity: 94, status: 'idle' }
  ]

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-dark-100">
            PowerGen 智能发电管理
          </h1>
          <p className="text-gray-600 dark:text-dark-400 mt-1">
            AI驱动的分布式发电系统智能管理平台
          </p>
        </div>
        <div className="flex items-center space-x-3">
          <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400">
            <Activity className="w-4 h-4 mr-1" />
            AI预测运行中
          </span>
          <button className="btn btn-primary btn-md">
            <Settings className="w-4 h-4 mr-2" />
            系统配置
          </button>
        </div>
      </div>

      {/* 关键指标 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <motion.div
          whileHover={{ scale: 1.02 }}
          className="card p-6 bg-gradient-to-br from-yellow-50 to-orange-50 dark:from-yellow-900/20 dark:to-orange-900/20"
        >
          <div className="flex items-center">
            <div className="p-3 bg-yellow-100 dark:bg-yellow-900/30 rounded-lg">
              <Sun className="w-6 h-6 text-yellow-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-dark-400">光伏发电</p>
              <p className="text-2xl font-bold text-gray-900 dark:text-dark-100">1,234 kW</p>
              <p className="text-sm text-green-600">+15.2% 今日</p>
            </div>
          </div>
        </motion.div>

        <motion.div
          whileHover={{ scale: 1.02 }}
          className="card p-6 bg-gradient-to-br from-cyan-50 to-blue-50 dark:from-cyan-900/20 dark:to-blue-900/20"
        >
          <div className="flex items-center">
            <div className="p-3 bg-cyan-100 dark:bg-cyan-900/30 rounded-lg">
              <Wind className="w-6 h-6 text-cyan-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-dark-400">风力发电</p>
              <p className="text-2xl font-bold text-gray-900 dark:text-dark-100">856 kW</p>
              <p className="text-sm text-green-600">+8.7% 今日</p>
            </div>
          </div>
        </motion.div>

        <motion.div
          whileHover={{ scale: 1.02 }}
          className="card p-6 bg-gradient-to-br from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20"
        >
          <div className="flex items-center">
            <div className="p-3 bg-purple-100 dark:bg-purple-900/30 rounded-lg">
              <Battery className="w-6 h-6 text-purple-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-dark-400">储能状态</p>
              <p className="text-2xl font-bold text-gray-900 dark:text-dark-100">78%</p>
              <p className="text-sm text-blue-600">充电中</p>
            </div>
          </div>
        </motion.div>

        <motion.div
          whileHover={{ scale: 1.02 }}
          className="card p-6 bg-gradient-to-br from-green-50 to-emerald-50 dark:from-green-900/20 dark:to-emerald-900/20"
        >
          <div className="flex items-center">
            <div className="p-3 bg-green-100 dark:bg-green-900/30 rounded-lg">
              <TrendingUp className="w-6 h-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-dark-400">系统效率</p>
              <p className="text-2xl font-bold text-gray-900 dark:text-dark-100">96.8%</p>
              <p className="text-sm text-green-600">优秀</p>
            </div>
          </div>
        </motion.div>
      </div>

      {/* 图表区域 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* AI功率预测 */}
        <div className="card p-6">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-dark-100">
              AI功率预测 vs 实际发电
            </h3>
            <div className="flex items-center space-x-2 text-sm">
              <div className="w-3 h-3 bg-blue-500 rounded-full"></div>
              <span className="text-gray-600 dark:text-dark-400">预测值</span>
              <div className="w-3 h-3 bg-green-500 rounded-full ml-4"></div>
              <span className="text-gray-600 dark:text-dark-400">实际值</span>
            </div>
          </div>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={forecastData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="time" stroke="#6b7280" />
                <YAxis stroke="#6b7280" />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: 'white', 
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px'
                  }}
                />
                <Line type="monotone" dataKey="predicted" stroke="#3b82f6" strokeWidth={2} strokeDasharray="5 5" />
                <Line type="monotone" dataKey="actual" stroke="#10b981" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* 储能系统状态 */}
        <div className="card p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-dark-100 mb-6">
            储能系统状态
          </h3>
          <div className="space-y-4">
            {storageData.map((storage, index) => (
              <div key={index} className="p-4 border border-gray-200 dark:border-dark-700 rounded-lg">
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center space-x-3">
                    <Battery className="w-5 h-5 text-purple-500" />
                    <span className="font-medium text-gray-900 dark:text-dark-100">
                      {storage.name}
                    </span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${
                      storage.status === 'charging' ? 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400' :
                      storage.status === 'discharging' ? 'bg-orange-100 text-orange-800 dark:bg-orange-900/20 dark:text-orange-400' :
                      'bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400'
                    }`}>
                      {storage.status === 'charging' ? '充电中' : 
                       storage.status === 'discharging' ? '放电中' : '待机'}
                    </span>
                  </div>
                </div>
                <div className="w-full bg-gray-200 dark:bg-dark-700 rounded-full h-2">
                  <div 
                    className="bg-purple-500 h-2 rounded-full transition-all duration-300"
                    style={{ width: `${storage.capacity}%` }}
                  />
                </div>
                <div className="flex items-center justify-between mt-2 text-sm">
                  <span className="text-gray-500 dark:text-dark-400">容量</span>
                  <span className="font-medium text-gray-900 dark:text-dark-100">
                    {storage.capacity}%
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* AI优化建议 */}
      <div className="card p-6">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-dark-100 mb-6">
          AI优化建议
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="p-4 bg-green-50 dark:bg-green-900/20 rounded-lg border border-green-200 dark:border-green-800">
            <div className="flex items-start space-x-3">
              <div className="w-8 h-8 bg-green-100 dark:bg-green-900/30 rounded-full flex items-center justify-center">
                <TrendingUp className="w-4 h-4 text-green-600" />
              </div>
              <div>
                <h4 className="font-medium text-green-900 dark:text-green-100">发电效率优化</h4>
                <p className="text-sm text-green-700 dark:text-green-300 mt-1">
                  基于天气预测，建议调整光伏板角度，预计可提升15%发电效率
                </p>
                <div className="mt-3">
                  <button className="text-sm font-medium text-green-600 hover:text-green-700">
                    立即优化 →
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
            <div className="flex items-start space-x-3">
              <div className="w-8 h-8 bg-blue-100 dark:bg-blue-900/30 rounded-full flex items-center justify-center">
                <Battery className="w-4 h-4 text-blue-600" />
              </div>
              <div>
                <h4 className="font-medium text-blue-900 dark:text-blue-100">储能调度建议</h4>
                <p className="text-sm text-blue-700 dark:text-blue-300 mt-1">
                  预测明日用电高峰在14:00-16:00，建议提前充电以应对需求
                </p>
                <div className="mt-3">
                  <button className="text-sm font-medium text-blue-600 hover:text-blue-700">
                    设置调度 →
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="p-4 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg border border-yellow-200 dark:border-yellow-800">
            <div className="flex items-start space-x-3">
              <div className="w-8 h-8 bg-yellow-100 dark:bg-yellow-900/30 rounded-full flex items-center justify-center">
                <AlertCircle className="w-4 h-4 text-yellow-600" />
              </div>
              <div>
                <h4 className="font-medium text-yellow-900 dark:text-yellow-100">维护提醒</h4>
                <p className="text-sm text-yellow-700 dark:text-yellow-300 mt-1">
                  风机WF-002运行异常，建议安排检修，避免影响发电效率
                </p>
                <div className="mt-3">
                  <button className="text-sm font-medium text-yellow-600 hover:text-yellow-700">
                    查看详情 →
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="p-4 bg-purple-50 dark:bg-purple-900/20 rounded-lg border border-purple-200 dark:border-purple-800">
            <div className="flex items-start space-x-3">
              <div className="w-8 h-8 bg-purple-100 dark:bg-purple-900/30 rounded-full flex items-center justify-center">
                <Zap className="w-4 h-4 text-purple-600" />
              </div>
              <div>
                <h4 className="font-medium text-purple-900 dark:text-purple-100">收益分析</h4>
                <p className="text-sm text-purple-700 dark:text-purple-300 mt-1">
                  本月发电收益较上月增加12.5%，建议继续当前运营策略
                </p>
                <div className="mt-3">
                  <button className="text-sm font-medium text-purple-600 hover:text-purple-700">
                    查看报告 →
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default PowerGenDashboard 