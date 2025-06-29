import React from 'react';
import Login from '../views/login/index.js'
import Index from '../views/Index/index.js'
import calculate from '../views/charge/calculate/index.js'
import {HashRouter as Router, Redirect, Route, Switch} from 'react-router-dom';
import resource from '../views/resource/index.js'
import sildermenu from '../views/Index/sildermenu.js'
import HtmlPage from '../views/big/HtmlPage.js'
// import ceshi from '../views/ceshi/index.js'
// import { createHashHistory } from "history";
// const history = createHashHistory();

class RouterConfig extends React.Component{
    render(){
        return(
            <Router>
                <Switch>
                    <Route exact path="/" render={() => <Redirect to='/index' push />} />
					<Route path='/index' component={Index}/>
					<Route path='/calculate' component={calculate}/>
					<Route path='/resource' component={resource}/>
                </Switch>
            </Router>
        )
    }
}
export default RouterConfig;
// <Router>
//                 <Switch>
//                     <Route exact path="/" render={() => <Redirect to='/Login' push />} />
// 					<Route path='/index' component={Index}/>
//                     <Route path='/login' component={Login}/>
// 					<Route path='/calculate' component={calculate}/>
// 					<Route path='/resource' component={resource}/>
					
//                 </Switch>
//             </Router>