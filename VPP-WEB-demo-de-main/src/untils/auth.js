export function getToken(){
    return sessionStorage.getItem('token')
}
 
export function setToken(token){
    return sessionStorage.setItem('token',token)
}
 
export function clearToken(){
    return sessionStorage.removeItem('token')
}
export function isLogined(){
    if(sessionStorage.getItem('token')){
        return true;
    }
    return false;
}