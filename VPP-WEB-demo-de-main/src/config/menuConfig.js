const MenuConfig = [
	{
	    title:'商汤智慧虚拟电厂',
	    key:'/HtmlPage',
		url:'HtmlPage',
		icon:'FolderViewOutlined',
	},
    {
        title:'灵活性资源管理',
        key:'/resource',
		url:'resource',
		icon:'PicCenterOutlined',
    },
	
    {
        title:'负荷管理',
		icon:'FolderOpenOutlined',
        key:'/from',
        children: [
            {
                title: '负荷预测',
                key: '/calculate',
				url:'calculate',
				icon:'AppstoreOutlined'
            },
            {
                title: '可调负荷',
                key: '/condenser',
				url:'condenser',
				icon:'AppstoreOutlined'
            },
			{
			    title: '光伏资源',
			    key: '/electricity',
				url:'electricity',
				icon:'AppstoreOutlined'
			},
			{
			    title: '储能资源',
			    key: '/accumulation',
				url:'accumulation',
				icon:'AppstoreOutlined'
			}
        ]       
    },
    {
        title:'运行调度',
		icon:'InteractionOutlined',
        key:'/table',
        children: [
			{
			    title: '运行分析',
			    key: '/manage',
				url:'manage',
				icon:'AppstoreOutlined'
			},
			{
			    title: '可调负荷运行策略 ',
			    key: '/tactics',
				url:'tactics',
				icon:'AppstoreOutlined'
			},
            {
                title: '运行策略',
                key: '/modelset',
				url:'e1',
				icon:'AppstoreOutlined',
				
            },
			
			{
			    title: '运行数据',
			    key: '/Operating',
				url:'Operating',
				icon:'AppstoreOutlined',	
			}
            
			
        ]        
    },
	{
		title: 'AI赋能',
		icon:'FolderViewOutlined',
		key: '/enabling',
		url:'enabling',
		
	},
	{
		title: '需求侧响应',
		icon:'FundProjectionScreenOutlined',
		key: '/respond',
		url:'respond',
		children: [
			{
			    title: '响应看板',
			    key: '/Rspondboard',
				url:'Respondboard',
				icon:'AppstoreOutlined'
			},
			{
			    title: '响应任务 ',
			    key: '/Responsetask',
				url:'Responsetask',
				icon:'AppstoreOutlined'
			},
		    {
		        title: '实时监测',
		        key: '/Detection',
				url:'Detection',
				icon:'AppstoreOutlined',
				
		    },
			
			{
			    title: '历史记录',
			    key: '/History',
				url:'History',
				icon:'AppstoreOutlined',	
			}
		    
			
		]        
	},
	{
		title: '辅助服务',
		icon:'ReconciliationOutlined',
		key: '/assist',
		url:'assist',
		children: [
			{
			    title: '服务任务',
			    key: '/ServiceTask',
				url:'ServiceTask',
				icon:'AppstoreOutlined'
			},
			{
			    title: '实时监测 ',
			    key: '/Monitoring',
				url:'Monitoring',
				icon:'AppstoreOutlined'
			},
			
			{
			    title: '历史记录',
			    key: '/chronicle',
				url:'chronicle',
				icon:'AppstoreOutlined',	
			}
		    
			
		] 
		
	},
	{
		title: '收益管理',
		icon:'ShopOutlined',
		key: '/income',
		url:'income',
		
	},
	{
		title: '电费账单',
		icon:'ProfileOutlined',
		key: '/power',
		url:'power',
		
	},
	{
	    title:'碳资产管理',
		icon:'CiCircleOutlined',
	    key:'/Carbon',
	    children: [
	        {
	            title: '碳溯源',
	            key: '/source',
				url:'source',
				icon:'AppstoreOutlined'
	        },
	        {
	            title: '碳监控',
	            key: '/peaking',
				url:'peaking',
				icon:'AppstoreOutlined'
	        },
			{
			    title: '碳资产',
			    key: '/property',
				url:'property',
				icon:'AppstoreOutlined'
			},
			{
			    title: '碳汇',
			    key: '/skin',
				url:'skin',
				icon:'AppstoreOutlined'
			},
			{
			    title: '碳模型',
			    key: '/pattern',
				url:'pattern',
				icon:'AppstoreOutlined'
			}
	    ]       
	},
	{
	    title:'报警管理',
	    key:'/police',
		url:'police',
		icon:'BoxPlotOutlined'
		// <BoxPlotOutlined />
	},
	{
	    title:'系统管理',
		icon:'SettingOutlined',
	    key:'/system',
	    children: [
	        {
	            title: '市场主体',
	            key: '/subscriber',
				url:'subscriber',
				icon:'AppstoreOutlined'
	        },
	        {
	            title: '节点模型',
	            key: '/hybrid',
				url:'hybrid',
				icon:'AppstoreOutlined'
	        },
			{
			    title: '系统模型',
			    key: '/systematics',
				url:'systematics',
				icon:'AppstoreOutlined'
			},
			{
			    title: '设备模型',
			    key: '/equipment',
				url:'equipment',
				icon:'AppstoreOutlined'
			},
			{
			    title: '系统用户',
			    key: '/Personnel',
				url:'Personnel',
				icon:'AppstoreOutlined',
				children: [
				    {
				        title: '角色',
				        key: '/role',
						url:'role',
						icon:'AppstoreOutlined'
				    },
				    {
				        title: '菜单',
				        key: '/menu_management',
						url:'menu_management',
						icon:'AppstoreOutlined'
				    },
					{
					    title: '用户',
					    key: '/parameter',
						url:'parameter',
						icon:'AppstoreOutlined'
					},
					
				]  
			},
			
			{
				title: '能源模型',
				key: '/model',
				url:'model',
				icon:'AppstoreOutlined',
				children: [
				    {
				        title: '储能模型',
				        key: '/accumulation_model',
						url:'accumulation_model',
						icon:'AppstoreOutlined'
				    },
				    {
				        title: '光伏模型',
				        key: '/Photovoltaic_model',
						url:'Photovoltaic_model',
						icon:'AppstoreOutlined'
				    },
					{
					    title: '模型参数',
					    key: '/parameter',
						url:'parameter',
						icon:'AppstoreOutlined'
					},
					
				]  
			}
	    ]       
	},
   
]

export default MenuConfig;
