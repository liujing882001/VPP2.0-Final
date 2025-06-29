// import React from 'react';
// import ReactDOM from 'react-dom';
// import RouterConfig from './routes/index.js';
// import App from './App.js'
// import { BrowserRouter, HashRouter, Switch, Route } from 'react-router-dom';
// import registerServiceWorker from './registerServiceWorker';

// // // import 'antd/dist/antd.min.css'

// ReactDOM.render(
//        <BrowserRouter><App /></BrowserRouter> ,
//     document.getElementById("root")
// );
// // // ReactDOM.render(<RouterConfig/>, document.getElementById('root'));
// // // ReactDOM.render(
// // //         <RouterConfig />,
// // //     document.getElementById("root")
// // // );
// registerServiceWorker();
import React from 'react'
import ReactDOM from 'react-dom'
import '../src/module.css'
import App from './App'

import { BrowserRouter,HashRouter } from 'react-router-dom'
// import './views/Index/index.css'
ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>,
  document.getElementById('root')
)
