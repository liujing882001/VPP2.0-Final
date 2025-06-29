package com.example.vvpcommom;

/**
 * 规定所有的Http头中必须携带Token 来验证用户身份，采用在拦截器用户名信息绑定到ThreadLocal，以供后续方法使用
 * 声明 上下文
 */
public class RequestHeaderContext {

    private static final ThreadLocal<RequestHeaderContext> REQUEST_HEADER_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();


    private String userId;
    // private String token;

    private RequestHeaderContext(RequestHeaderContextBuild requestHeaderContextBuild) {
        this.userId = requestHeaderContextBuild.userId;
        // this.token=requestHeaderContextBuild.token;
        setContext(this);
    }

    public static RequestHeaderContext getInstance() {
        return REQUEST_HEADER_CONTEXT_THREAD_LOCAL.get();
    }

    public static void clean() {
        REQUEST_HEADER_CONTEXT_THREAD_LOCAL.remove();
    }

    public String getUserId() {
        return userId;
    }

    public void setContext(RequestHeaderContext context) {
        REQUEST_HEADER_CONTEXT_THREAD_LOCAL.set(context);
    }

    public static class RequestHeaderContextBuild {
        private String userId;
        // private String token;

        public RequestHeaderContextBuild userId(String userId) {
            this.userId = userId;
            return this;
        }

        /*public RequestHeaderContextBuild token(String token){
            this.token=token;
            return this;
        }*/
        public RequestHeaderContext bulid() {
            return new RequestHeaderContext(this);
        }
    }
}

