import React, { Component } from 'react';
import './index.css'
var root_path = "https://workspace.easyv.cloud/shareScreen/eyJzY3JlZW5JZCI6MTI5ODU3Nn0=?timeStamp=182d3f86c02 《商汤智慧虚拟电厂 Copy》";
 
export default class HtmlPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            iFrameHeight: '0px'
        }
    }
    render() {
        // let path = this.props.match.params.path;//路由参数
        // let url = root_path+"default.html";
        // if (path === "service") {
        //     url = root_path+"service.html";
        // } else if (path === "interfaceCase") {
        //     url = root_path+"interface_view.html";
        // } 
        // console.log(path,url);
        return (
            <div style={{background:'#000',height:'100%'}}>
                <iframe  ref="iframe" scrolling="yes" frameBorder="0"
                    style={{background:'#000',width:'100%',height:'100%', overflow:'visible'}}
                    onLoad={() => {//iframe高度不超过content的高度即可
                        let h = document.documentElement.clientHeight-200;
                        this.setState({
                            "iFrameHeight": h + 'px'
                        });
						document.getElementById('ifm')
                    }} 
                    src={root_path}
                />
            </div>
        )
    }
}