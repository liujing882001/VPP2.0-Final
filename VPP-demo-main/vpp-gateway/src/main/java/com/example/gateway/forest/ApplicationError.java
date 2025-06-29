package com.example.gateway.forest;

import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationError implements OnError {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationError.class);

    public static ApplicationError create() {
        return new ApplicationError();
    }

    @Override
    public void onError(ForestRuntimeException e, ForestRequest forestRequest, ForestResponse forestResponse) {
        LOGGER.info("调用现异常[{}] ,请求URL{} 数据{}，响应{}",
                e.getMessage(),
                forestRequest.getUrl(),
                forestRequest.getBodyList(),
                forestResponse.getStatusCode());
    }
}
