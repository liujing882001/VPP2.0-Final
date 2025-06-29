class ToTabContent extends React.Component{
    constructor(props){
        super(props)
    }
    render(){
       //通过传入的name属性动态得到自己需要注入的组件，MyComponent首字母要大写
        const MyComponent = pages[this.props.name]
        
        return <MyComponent {...this.props} />
    }
}