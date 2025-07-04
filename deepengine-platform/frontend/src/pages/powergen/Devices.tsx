import React from 'react'
import { Cpu } from 'lucide-react'

const PowerGenDevices: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-dark-100">
            设备管理
          </h1>
          <p className="text-gray-600 dark:text-dark-400 mt-1">
            管理和监控所有发电设备
          </p>
        </div>
      </div>
      
      <div className="card p-8 text-center">
        <Cpu className="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 dark:text-dark-100 mb-2">
          设备管理功能开发中
        </h3>
        <p className="text-gray-600 dark:text-dark-400">
          该功能正在开发中，敬请期待
        </p>
      </div>
    </div>
  )
}

export default PowerGenDevices 