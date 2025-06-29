/**
 * 路由守卫校验
 */
import React, {useEffect,useState} from "react";
import {Route, Redirect} from "react-router-dom";

import Index from './views/Index/index.js'
const FrontendAuth =(props) =>{
	const {routerConfig, location} = props;
	const {pathname} = location;
	const isLogin = sessionStorage.getItem("userid");
	const [componentname,setComponentname] = useState('');
	useEffect(() =>{
		if(props.compent){
			setComponentname(props.compent)
		}
		if(props.location.pathname==''){
			
		}
	},[props])
	useEffect(() =>{
	},[componentname])
	if(location.pathname=='/'){
		props.isLoginbtn(false)
	}else{
		props.isLoginbtn(true)
	}
	const targetRouterConfig = routerConfig.find(
		(item) => item.path == pathname
	);
	if (targetRouterConfig && !targetRouterConfig.auth && !isLogin) {
		const {component} = targetRouterConfig;
		return <Route exact path={pathname} component={component}/>;
	}		
	if (isLogin) {
		// 如果是登陆状态，想要跳转到登陆，重定向到主页
		if (pathname === "/Login") {
		   return <Redirect to="" />
		} else {
			// 如果路由合法，就跳转到相应的路由
			
			if (targetRouterConfig) {
				if(targetRouterConfig.path=='/index'){
					if(componentname){
						return <Redirect to={componentname} />;
						// return  <Route path={pathname} component={targetRouterConfig.component}/>
					}else{
						return  <Route path={pathname} component={targetRouterConfig.component}/>
					}
				}else{
					return (
						<Route path={pathname} component={targetRouterConfig.component}/>
						
					);
				}
				
				
			} else {
				// 如果路由不合法，重定向到 404 页面
				if(componentname){
					return <Redirect to={componentname} />;
				}else{
					<Redirect to='/index' />;
				}
				
			}
		}
	} else {
		// 非登陆状态下，当路由合法时且需要权限校验时，跳转到登陆页面，要求登陆
		if (targetRouterConfig && targetRouterConfig.auth) {
			// alert(0)
			return (
				<Route path={pathname} component={targetRouterConfig.component}/>
			);
		} else {
			// 非登陆状态下，路由不合法时，重定向至 404
			return <Redirect to=""/>;
		}
	}
}


export default FrontendAuth;

