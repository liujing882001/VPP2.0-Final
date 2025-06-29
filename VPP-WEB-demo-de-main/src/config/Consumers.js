const MenuConfig = [
    {
        title:'虚拟电厂',
        key:'/maps',
		url:'maps',
		icon:'maps',
    },
    {
        title:'账单管理',
		icon:'reconciliation',
        key:'/from',
        children: [
            {
                title: '收益管理',
                key: '/proceeds',
				url:'proceeds'
            },
            {
                title: '电费账单',
                key: '/power',
				url:'power'
            }
        ]       
    },
    {
        title:'服务管理',
		icon:'container',
        key:'/table',
        children: [
            {
                title: '基础服务',
                key: '/consumer',
				url:'consumer'
            },
            {
                title: '增值服务',
                key: '/items',
				url:'items'
            }
        ]        
    },
	
   
]

export default MenuConfig;
