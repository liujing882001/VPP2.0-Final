import React from 'react';
import { HashRouter as Router, Route, Switch, Redirect } from 'react-router-dom';
import Login from './views/login/index.js';
import Index from './views/Index/index.js'
// import Storey from './pages/Login'
// import Personnel from './components/SystemManage/Personnel/personnel';


import App from './App';

export default () => (

  <Router>
      <Switch>
			<Route exact path="/" render={() => <Redirect to='/Login' push />} />
			<Route path='/login' component={Login}/>
			<Route path='/index' component={Index}/>
  		
      </Switch>
  </Router>
  
)
// debugger