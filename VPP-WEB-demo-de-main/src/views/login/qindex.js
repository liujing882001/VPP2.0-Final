import React,{Component} from 'react'
import { Form, Input, Button, Checkbox ,Space,message} from 'antd';
import axios from 'axios';
// import http from '../../server/server.js'
// import './index1.css'
import './login.scss'

import { UserOutlined,UnlockOutlined } from '@ant-design/icons';
import { receiveData } from '../../action/index.js';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import md5 from 'js-md5';
import Index from '../Index/index.js'
import resource from '../resource/index.js'
// import storageUtils from '../../untils/storageUtils'

import { createHashHistory } from "history";
const history = createHashHistory();

React.Component.prototype.$md5 = md5;


class login extends Component {
	constructor(props) {
		super(props)
		this.state={
			username:'',
			password :'',
			token:'',
			rememberVal:'',
			loading:false,
			imgSrc:'',
			logoSrc:''
		}
	}
	    // 获取日
	    // startDate 2022-03-26
	    // endDate 2022-04-26
	   
		
	        
	componentDidMount(){
		this.forceUpdate();
		// this.getDay('2023-01-01','2023-05-31')
		sessionStorage.removeItem('userid')
		axios({
			url:'vpp/captcha',
			method:'post',
			
		}).then(response =>{
			if(response.data.code==200){
				// localStorage.setItem("token",response.data.data.token)
				// this.token = response.data.data.verifyCode
				this.setState({
					token:response.data.data.verifyCode
				})
				console.log(response)
				let imgDiv = document.getElementById('secret');
				imgDiv.src = response.data.data.captchaImg;//这个可以
				console.log(imgDiv)
				
			}else{
				message.info(response.data.msg)
			}
		})
		this.checkRememberPassword()
		this.sysLogoSysParam()
		this.mainLogoSysParam()
		console.log(sessionStorage.getItem('tokens'))
	}
	// 获取系统图片
	sysLogoSysParam =() =>{
		axios.get('system_management/systemParam/sysLogoSysParam').then(res =>{
			console.log(res)
			if(res.data.code==200){
				this.setState({
					imgSrc:res.data.data
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	getCaptcha=() =>{
		axios({
			url:'vpp/captcha',
			method:'post',
			
		}).then(response =>{
			console.log(response)
			if(response.data.code==200){
				console.log(response.data.data.verifyCode)
				// localStorage.setItem("token",response.data.data.token)
				// this.token = response.data.data.verifyCode
				this.setState({
					token:response.data.data.verifyCode
				})
				console.log(response)
				let imgDiv = document.getElementById('secret');
				imgDiv.src = response.data.data.captchaImg;//这个可以
				console.log(imgDiv)
				
			}else{
				message.info(response.data.msg)
			}
		})
		
	}
	setCookie = (name, value, expiryDate) => {
		let currentDate = new Date();
		currentDate.setDate(currentDate.getDate() + expiryDate);
		document.cookie = name + '=' + value + '; expires=' + currentDate;
	}
	getCookie = name => {
		let arr = document.cookie.split('; ');
		for (let i = 0; i < arr.length; i++) {
			let arr2 = arr[i].split('=');
			if (arr2[0] === name) {
			return arr2[1];
			}
		}
		return '';
	}
	removeCookie = name => {
		this.setCookie(name, 1, -1);
	};
	isPasswordEncrypted(password) {
		// console.log(atob('!QtStxnDc0602'))/
		
	  try {
	    // 尝试解码 Base64 字符串
	    const decodedPassword = atob(password);
	    
	    // 检查解码后的密码长度或其他特征等
	    // 这里仅作为示例，可以根据具体情况进行判断
	    return true;
	  } catch (error) {
	    // 解码失败，说明密码没有经过 atob 加密
	    return false;
	  }
	}
	checkRememberPassword = () => {
		// console.log(this.getCookie('userName'),this.getCookie('passWord'))
		
		// const encryptedPassword = btoa("mypassword");
		// console.log(this.isPasswordEncrypted(encryptedPassword));  // 输出：true

		if (this.getCookie('userName') !== '' && this.getCookie('passWord') !== '') {
			const plainPassword = this.getCookie('passWord')
			// console.log(plainPassword)
			console.log(this.isPasswordEncrypted(plainPassword));  // 输出：false不加密true加密
			let isTrue = this.isPasswordEncrypted(plainPassword)
			// btoa(values.passWord), 1)
			this.refs.myForm.setFieldsValue({
				userName: this.getCookie('userName'),
				passWord: plainPassword==false?this.getCookie('passWord'):window.atob(this.getCookie('passWord')),
			});
			this.setState({
			  rememberVal: true,
			});
		}
	};

	// 记住密码
	remberPassword =(e) =>{
		const { userName, passWord } = this.refs.myForm.getFieldsValue();
		// console.log(this.refs.myForm.getFieldsValue())、
		let values = this.refs.myForm.getFieldsValue()
		// console.log(username,password)
		// let username = 
		    this.setState({
		        rememberVal: e.target.checked,
		    },() => {
		        if (e.target.checked) {
					this.setCookie('userName', values.userName, 1);
					// this.setCookie('passWord', values.passWord, 1);
					this.setCookie('passWord', btoa(values.passWord), 1);
					
		        } else {
					this.removeCookie('userName');
					this.removeCookie('passWord');
		        }
		      }
		    );
	}
	// 首页插画logo
	mainLogoSysParam(){
		axios.get('system_management/systemParam/mainLogoSysParam').then(res =>{
			console.log(res)
			if(res.data.code==200){
				this.setState({
					logoSrc:res.data.data
				})
			}
		}).catch(err =>{
			console.log(err)
		})
	}
	render(){
		
		let {rememberVal,loading,imgSrc,logoSrc} = this.state
		const onFinish = (values: any) => {
			// let token = localStorage.getItem('token')
			// console.log(token)
		    console.log('Success:', values);
			const { username, password } = values;
			let passwords = md5(values.passWord)
			sessionStorage.setItem('name',values.userName)
			if(rememberVal==true){
				this.setCookie('userName', values.userName, 1);
				// this.setCookie('passWord', values.passWord, 1);
				this.setCookie('passWord', btoa(values.passWord), 1);
			}
			this.setState({
				loading:true
			})
			axios({
				url:'vpp/doLogin',
				method:'post',
				data:{
					passWord: passwords,
					verifyCode: this.state.token,
					userName: values.userName,
					verifyCodeText: values.verificationCode
				}
			}).then(response => {
				console.log(response.data)
				if(response.data.code ==200){
					sessionStorage.removeItem('userid')
					sessionStorage.removeItem('tokens')
					sessionStorage.removeItem('permissions')
					sessionStorage.setItem('userid',response.data.data.userId)
					sessionStorage.setItem('tokens',response.data.data.refreshToken)
					// history.push({pathname:"/Index",state:{}})
					sessionStorage.setItem('permissions',response.data.data.permissions)
					sessionStorage.setItem('username',response.data.data.username)
					console.log(sessionStorage.getItem('permissions'),'------')
					this.setState({
						loading:false
					},() =>{
						this.props.history.push({
						  pathname: '/Index',
						  // pathname: '/resource',
						});
					})
					
					// 
					
					// this.props.history.push('/Index');
				}else{
					this.setState({
						loading:false
					},() =>{
						message.error(response.data.msg)
						this.getCaptcha()
						this.refs.myForm.setFieldsValue({
							verificationCode:''
						})
					})
					
					// this.refs.myForm.resetFields()
				}
			})

		};
		
		  const onFinishFailed = (errorInfo: any) => {
		    console.log('Failed:', errorInfo);
		  };
		  // <img id="logo" src={require('../../style/img/xu.png')}  />
		  // 
		  // <img id="logo" src={require('../../style/imgs/login.png')}  />
		  // <img id="logo" src={require('../../style/img/xu.png')}  />
		  // <img className="logo" src={require('../../style/imgs/loginlogo.png')}  />
		  // <img className="logo" src={require('../../style/imgs/logo123.png')}  />
		  // <h1>欢迎登录</h1>
		  // <h1>宁波市海曙区综合智慧零碳电厂管理平台</h1>
		  // <img id="logo" src={require('../../style/imgs/ning.png')}  />
		  // <img id="logo" src={require('../../style/img/xu.png')}  />
		return(
			<div className="xulogin">
				<div className="header" style={{height:'108px !important'}}>
					<img src={require('../../style/guo/guo.png')} />
					
				</div>
				
				<div className="content">
					
					<Form
							name="basics2"
							labelCol={{ span: 0 }}
							wrapperCol={{ span: 25 }}
							initialValues={{ remember: true }}
							onFinish={onFinish}
							onFinishFailed={onFinishFailed}
							autoComplete="off"
							ref="myForm"
					>
						<h1 style={{textAlign:'center',fontSize:24}}>AI虚拟电厂智能交易平台</h1>
					      <Form.Item
					        name="userName"
					        rules={[{ required: true, message: '请输入账号' }]}
							autoComplete="off"
					      >
					        <Input autoComplete="off" prefix={<UserOutlined />} />
					      </Form.Item>
					
					      <Form.Item
					        name="passWord"
							autoComplete="new-password"
					        rules={[{ required: true, message: '请输入密码' }]}
							rules={[{
							            pattern:
							                /^(?![^a-zA-Z]+$)(?!\\D+$)(?=.*[^a-zA-Z0-9]).{8,100}$/,
							            message: "不能小于8位字符，必须包括字母,数字和特殊字符",
							        }]}
					      >
					        <Input.Password autoComplete="new-password" prefix={<UnlockOutlined />} />
					      </Form.Item>
						<Form.Item name="verificationCode"
							rules={[{ required: true, message: '请输入验证码' }]}
						>
							
							<Space>
							<Input style={{height:40}} /> 
								<img
									id='secret'
									src=''
									alt="点击刷新"
									style={{ cursor:'pointer' }}
									onClick={this.getCaptcha}
								/>
							</Space>
							 
						</Form.Item>
					      <Form.Item  >
					        <Checkbox onChange={this.remberPassword} checked={this.state.rememberVal}>记住密码</Checkbox>
					      </Form.Item>
						
					      <Form.Item >
					        <Button type="primary" loading={loading} style={{height:48,fontSize:16}} htmlType="submit">
					          登录
					        </Button>
					      </Form.Item>
					    </Form>
				</div>
				<div className="footer">
					<span>
						国网数字科技控股有限公司（国网雄安金融科技集团有限公司） 版权所<br />
						地址：北京市西城区广安门内大街311号祥龙商务大厦 | 邮编：100053 京ICP备17072407号-1
					</span>
				</div>
			</div>
		)
	}
	
}

export default login
// const mapStateToPorps = state => {
//   const { auth } = state.httpData;
//   return { auth };
// };
// const mapDispatchToProps = dispatch => ({
//   // fetchData: bindActionCreators(fetchData, dispatch),
//   receiveData: bindActionCreators(receiveData, dispatch)
// });
// export default connect(mapDispatchToProps)(login);
// export default connect(null, mapDispatchToProps)((login));