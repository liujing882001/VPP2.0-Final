package com.example.vvpcommom;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;

/**
 * 数据加密
 */
@Target(value={CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypted {

}
