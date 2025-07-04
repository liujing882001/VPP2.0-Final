import React from 'react'
import { Settings as SettingsIcon } from 'lucide-react'

const Settings: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-dark-100">
            系统设置
          </h1>
          <p className="text-gray-600 dark:text-dark-400 mt-1">
            配置系统参数和用户偏好
          </p>
        </div>
      </div>
      
      <div className="card p-8 text-center">
        <SettingsIcon className="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 dark:text-dark-100 mb-2">
          系统设置功能开发中
        </h3>
        <p className="text-gray-600 dark:text-dark-400">
          该功能正在开发中，敬请期待
        </p>
      </div>
    </div>
  )
}

export default Settings 