const MenuConfig = [
    {
        title:'虚拟电厂',
        key:'/maps',
		url:'maps',
		icon:'maps',
    },
    {
        title:'资产管理',
		icon:'reconciliation',
        key:'/from',
        children: [
            {
                title: '收益预测',
                key: '/income',
				url:'income'
            },
            {
                title: '收益管理',
                key: '/proceeds',
				url:'proceeds'
            }
        ]       
    },
    {
        title:'用户管理',
		icon:'container',
        key:'/table',
        children: [
            {
                title: '电力用户',
                key: '/consumer',
				url:'consumer'
            },
            {
                title: '项目信息',
                key: '/items',
				url:'items'
            },
			{
			    title: '电费账单',
			    key: '/power',
				url:'power'
			}
        ]        
    },
	{
		title: '服务管理',
		icon:'layout',
		key: '/peaking',
		url:'peaking',
		children: [
		    {
		        title: '售电服务',
		        key: '/modelset',
				url:'e2'
		    },
		    {
		        title: '增值服务',
		        key: '/system',
				url:'a2'
		    }
		]  
	}
   
]

export default MenuConfig;
