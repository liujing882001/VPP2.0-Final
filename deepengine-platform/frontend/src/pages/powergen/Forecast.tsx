import React from 'react'
import { Activity } from 'lucide-react'

const PowerGenForecast: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-dark-100">
            AI功率预测
          </h1>
          <p className="text-gray-600 dark:text-dark-400 mt-1">
            智能预测发电功率和优化策略
          </p>
        </div>
      </div>
      
      <div className="card p-8 text-center">
        <Activity className="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 dark:text-dark-100 mb-2">
          AI预测功能开发中
        </h3>
        <p className="text-gray-600 dark:text-dark-400">
          该功能正在开发中，敬请期待
        </p>
      </div>
    </div>
  )
}

export default PowerGenForecast 