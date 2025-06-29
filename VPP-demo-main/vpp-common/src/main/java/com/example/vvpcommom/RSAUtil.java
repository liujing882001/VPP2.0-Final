package com.example.vvpcommom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class RSAUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);

    /*public static void main(String[] args) {
        String publicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt/ZsRRI2HHjWqePgJHVCM99Tmfh1Nd8qanUj3vbRVvhX92+if6g/uZ1cU86xnrSfMF0iAuvOAcOhljlkYbPqTNNffc5Jq7z/pV1FKPwDbhHSl0aC64j5SHsjrbmC8oZuhOy7GggmGF60E6r4v0R8ggUmQ7qFz9XMN6esLwDXsDO2knEfLLhw0+5sj7NaEnw1WqWDpPUQlfhLPJVUoighTsiZJ9lWV2VW3Xqi9Mqr3pHNQ02tUxYc+eqEk7rwxp0VcGvxAajCpbHYGWWqbIiRRjwtm6QneVWTeHYQKvWjIAb5CWBNqhEwK5CcV6rh7XVaCx4Tghly+UudmHrLbrJUEwIDAQAB";
        String privateKeyBase64 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC39mxFEjYceNap4+AkdUIz31OZ+HU13ypqdSPe9tFW+Ff3b6J/qD+5nVxTzrGetJ8wXSIC684Bw6GWOWRhs+pM0199zkmrvP+lXUUo/ANuEdKXRoLriPlIeyOtuYLyhm6E7LsaCCYYXrQTqvi/RHyCBSZDuoXP1cw3p6wvANewM7aScR8suHDT7myPs1oSfDVapYOk9RCV+Es8lVSiKCFOyJkn2VZXZVbdeqL0yqvekc1DTa1TFhz56oSTuvDGnRVwa/EBqMKlsdgZZapsiJFGPC2bpCd5VZN4dhAq9aMgBvkJYE2qETArkJxXquHtdVoLHhOCGXL5S52YestuslQTAgMBAAECggEBAJcU02B6j2QcvbdzBX8Lqh4t2FwIWEKxEAW0RIx1ygESHjSBQ0RyKjvHh67wfqFnl6FBq5AYluyN/SQB6HXxK4iCw9bvV0OrCY2Gpyj8p9h63KgOabEEoi4rq2aeh3Okghm6zgKYFKlJBb1tWbLDjTm+/uj6w7Cn1nsSwm8r0vfKahEBQl56Gwwc0p9CvJzL7/GQwN6wdpjfuF7bpgH2YyOM4Vt+Fp/Irlg9RSjKKQnLOTokuwFYJSNdmiAQUQaqLNIAZth0zIG6M/V2yYnP6uYQ9Dn4JrwUtfBYzc14iFlqomp5euCYClZ4Ju5cQdJ+NBu6ZqTPoHTGcT0GYSMANmECgYEA8mOjI3YPR+son8MeQoCT0QRGQhrD4/HBPz3nutYfJ8KjDrrkhfN5vtHdn0Sdv9Xt6FhbqiBYTyQuSgwUzA/TKpJ+nLIG3RGxmHWxxO90AZ2MY7WD1PF87frnt2xv2japAr/j5grpS9HNMMx6AC9PNopsBPOWsvaLRs7Vd6hPKqMCgYEAwkrmEoHSCIDbYXem0cv63xw0H6AEbiJxGMIRb9HJ0KN3gwNzAilCTlUGPdfzfrDQNXbnxXrNyuNn/KbPdNu4f1llRnTgTM6ZsHdKEDUFDVmNMcl9ySNfFYQbUtce6TZKj9Dut31aXJt3CUj1SLuYPrWRue+P+KvHDNWCieZyt9ECgYEAl0AjaZpe+LK9+8tmk3rmQjRXnyJSgGe3/b2kX0sDz+y9kRCATzHaE3+juqG3iiB1P1n7gRKD6IacN+qIA7oQO+2EAkV40PQcjRVMI6aEDgoAs6ttozA5k8rQUv3gYhtfpW6WSFMgtwYo8z11F65t0IvZ0dEqhWwhBsNEI1GsbN8CgYBAmucmAET2zuIj5sVxvYIN7jLm0S6n8Ivqz508sWWKGtECn4L+lPmXUm5g4g31cpXJMWCgKdWEQVmyiUUydl1C+1bRtstevtQb+Y6HFRo49i4dAALzfu9/GA593UpKAeOJLtAx81y4rnlGfRwSs05Mi9OJID+fUH8/pT9i214KAQKBgA2xYw7BzaV78qVlI9dkzvCGSzj09ZuLroFxUxwLbKGBnN8fKjn5erbgIF/D+TK5myqKBFREblMUJBwIEUSrN7ENq1FPRCuQI8bsa3gK/UBhQ87Z5h8hGYNPy6uu1KpwDWL2xVarhhIqZ/wOo9jAGothzcyDvJvh9Fbi8GlFBvic";

        String test = encrypt("{\"code\":200,\"description\":\"ok\",\"requestID\":\"80\",\"root\":\"MomentDataReportRequest\",\"version\":1}", publicKeyBase64);
        System.out.println("11111 " + test);
        System.out.println("11 ");
        String decode = decode(test, privateKeyBase64);
        System.out.println("22 " + decode);
        String decode1 = decode("Qyl/ffeSnmBARDTuewx8FBpCX0SFLEI/B9a1KSr2YKrZSaTJ3f2sgmz0xqJt+6lK5S54YVexw3ZWuhClU0t1YDoAy+rhOxYY3eRdxuPVap2jug3P+6CsDhkFRHohLepLilrZeYjB84aMSlcoa1RwfTPuiVwtv9Kw+RwMMhd+wGS509PPXEoiQRYqswh7WpLBU9vtCnYGtpu2zfNEqoRGlA37UiygZgXzDMXTtVqaIn5CcnGRKhwn4ciXfyw/VNdxcz6m+ZUZmp+ODKbouEPqGoaK8OzvdTAzbMS3y6B2PW0d5v226EZUpieH0diiL8bDYvPJRAJdQ+xMVmF8Su4KqQ==", privateKeyBase64);
        System.out.println("333 " + decode1);
    }*/

    public static void main(String[] args) throws Exception {
        // 蚂蚁
//        String publicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp8LlbAGsfBcr0m/MJgej+kW6a9BJsryo0gP9m5579yNoXSHSmUkWe4rz0iqm3LF+Tw9cW+hhzz13VGuCqzRk8Du48qTRFuhnef9YBRYRgSXDhJSTXNL1AttLhrtRaYbmzoY0dJmA0EZG+zrKg5bat5CHxCkwo0CSIOUeozYHuESyqzrIIsD6dXdmdkOmK2pUzJzCk87WoB9s9wx8iNY6b8pkEAgDFtpQpTvwe9ceWqQxOt5Sj3tUCuXclJMtNVRqfPNPVL0scky58WpQa+6pRwkZW8kj3RrI39K0yGVQITkc1V7Ub9bCRxW0A9ooq8R9oV7WIOJSQ/pcEE04UvxxXwIDAQAB";
//        String privateKeyBase64 = "MIIEowIBAAKCAQEAp8LlbAGsfBcr0m/MJgej+kW6a9BJsryo0gP9m5579yNoXSHSmUkWe4rz0iqm3LF+Tw9cW+hhzz13VGuCqzRk8Du48qTRFuhnef9YBRYRgSXDhJSTXNL1AttLhrtRaYbmzoY0dJmA0EZG+zrKg5bat5CHxCkwo0CSIOUeozYHuESyqzrIIsD6dXdmdkOmK2pUzJzCk87WoB9s9wx8iNY6b8pkEAgDFtpQpTvwe9ceWqQxOt5Sj3tUCuXclJMtNVRqfPNPVL0scky58WpQa+6pRwkZW8kj3RrI39K0yGVQITkc1V7Ub9bCRxW0A9ooq8R9oV7WIOJSQ/pcEE04UvxxXwIDAQABAoIBAA/iLxCb0/Xnx7lj5mNPY+IlSfI9RU0L+bvwrslWJwyOKKpwslzbA6rszAsBCM1qbLgVjzwQc3pIqoyupS+KhWgNb4RhwH43wNxXJcFNhpjAt5NM8ere+l2r36nBAINKR34UTNEhW4XcsYnF1jjBRrIMmB4lfDpoua0tpEn1qqNyjjZEOQ585sUIqo7BkgY11XFMcZX89oj4BDw848Gj2vg0cCfdXPFZoZFu9vfSe8ey4HBFd6h/VyTGikIA6MIW++E6uai/EuMH4HR8uexwJz5zeA4nG+Oe7i8r6gHnHbJABbPmbyPxdJFb2FtqYFa7SnlqOQuW1Mw62hO7j1SVDXECgYEA5jz5VsSTLet7nbJ6QruzYYX4EzRTfAVWYxXSzC5sXybogRLu3l/y1oUXjXim8cpTPKpW2XA6uQPLaKURIvwPpqD9JQwnPwjFsTRy66exAmrXs4nlsvPgDNkq/ogOVonZiOkkCyC29z2oE4AgzNMafW9WJJXztgVIpV+Odb/AX4kCgYEAuohP6SYnT52FedKXuVe1eremq488IGTMXzrpu6LAzSGMLk/BhXldw68oiYOaTboVIH9JobhwmjKkf2HyuQelo0xx0TCq5q7ZXPhT6o5wPv2/LWVVmPuYpMC4aeRfaG8TqrMqXUhVXWys9+LfsluhFKvLx3U8Z7je6y+/1IQeZ6cCgYAXWRuX9vtN7tFsXosTdWFthF5x5B6LNydZiP8FgbevtoxheoAKwQQpSPnoeNHFN1VVk5iXGFB8LqxUAllIiPM1XKIPPbpFJCiYHc+BolHLAva42EUY7q5L30IC6d06XWRYA/yJvFmRnQkMYTPyUX4gNkgwEZzTWpnRW21g938UsQKBgDkATMMnmJ24ukAm/Zxy1A9jLbiBddNEWjvDqgdfzeEhnk/MqYubMe8JiK61G6Mi+S/MFfkofqhwT1pp2R1328MSKjnN0Hvb9Kp3bFViH6TwL8q0i7rLL7Mw/WnGku07/FuOkqSc5RE+k4DQwKElZQ0HDqeuCL59fQmLtug/U3dDAoGBAI4exr09ItGbaKW/sjW6JXz71Wvr66z4rcbQ9+eGwUj+o4btt/fZ81udYRZWWBUIUtH5rZMMSADwRrz/pUh+iQFkO7rLzkDSqfaE5Mzk431A4SZ8uUaHs4EO6D3hf9kHodKgrbbnV3UDvkn234Cp/zn9bKcy5NLA1qQoPL2QjvMq";
        //
        String publicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt/ZsRRI2HHjWqePgJHVCM99Tmfh1Nd8qanUj3vbRVvhX92+if6g/uZ1cU86xnrSfMF0iAuvOAcOhljlkYbPqTNNffc5Jq7z/pV1FKPwDbhHSl0aC64j5SHsjrbmC8oZuhOy7GggmGF60E6r4v0R8ggUmQ7qFz9XMN6esLwDXsDO2knEfLLhw0+5sj7NaEnw1WqWDpPUQlfhLPJVUoighTsiZJ9lWV2VW3Xqi9Mqr3pHNQ02tUxYc+eqEk7rwxp0VcGvxAajCpbHYGWWqbIiRRjwtm6QneVWTeHYQKvWjIAb5CWBNqhEwK5CcV6rh7XVaCx4Tghly+UudmHrLbrJUEwIDAQAB";
//        String privateKeyBase64 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC39mxFEjYceNap4+AkdUIz31OZ+HU13ypqdSPe9tFW+Ff3b6J/qD+5nVxTzrGetJ8wXSIC684Bw6GWOWRhs+pM0199zkmrvP+lXUUo/ANuEdKXRoLriPlIeyOtuYLyhm6E7LsaCCYYXrQTqvi/RHyCBSZDuoXP1cw3p6wvANewM7aScR8suHDT7myPs1oSfDVapYOk9RCV+Es8lVSiKCFOyJkn2VZXZVbdeqL0yqvekc1DTa1TFhz56oSTuvDGnRVwa/EBqMKlsdgZZapsiJFGPC2bpCd5VZN4dhAq9aMgBvkJYE2qETArkJxXquHtdVoLHhOCGXL5S52YestuslQTAgMBAAECggEBAJcU02B6j2QcvbdzBX8Lqh4t2FwIWEKxEAW0RIx1ygESHjSBQ0RyKjvHh67wfqFnl6FBq5AYluyN/SQB6HXxK4iCw9bvV0OrCY2Gpyj8p9h63KgOabEEoi4rq2aeh3Okghm6zgKYFKlJBb1tWbLDjTm+/uj6w7Cn1nsSwm8r0vfKahEBQl56Gwwc0p9CvJzL7/GQwN6wdpjfuF7bpgH2YyOM4Vt+Fp/Irlg9RSjKKQnLOTokuwFYJSNdmiAQUQaqLNIAZth0zIG6M/V2yYnP6uYQ9Dn4JrwUtfBYzc14iFlqomp5euCYClZ4Ju5cQdJ+NBu6ZqTPoHTGcT0GYSMANmECgYEA8mOjI3YPR+son8MeQoCT0QRGQhrD4/HBPz3nutYfJ8KjDrrkhfN5vtHdn0Sdv9Xt6FhbqiBYTyQuSgwUzA/TKpJ+nLIG3RGxmHWxxO90AZ2MY7WD1PF87frnt2xv2japAr/j5grpS9HNMMx6AC9PNopsBPOWsvaLRs7Vd6hPKqMCgYEAwkrmEoHSCIDbYXem0cv63xw0H6AEbiJxGMIRb9HJ0KN3gwNzAilCTlUGPdfzfrDQNXbnxXrNyuNn/KbPdNu4f1llRnTgTM6ZsHdKEDUFDVmNMcl9ySNfFYQbUtce6TZKj9Dut31aXJt3CUj1SLuYPrWRue+P+KvHDNWCieZyt9ECgYEAl0AjaZpe+LK9+8tmk3rmQjRXnyJSgGe3/b2kX0sDz+y9kRCATzHaE3+juqG3iiB1P1n7gRKD6IacN+qIA7oQO+2EAkV40PQcjRVMI6aEDgoAs6ttozA5k8rQUv3gYhtfpW6WSFMgtwYo8z11F65t0IvZ0dEqhWwhBsNEI1GsbN8CgYBAmucmAET2zuIj5sVxvYIN7jLm0S6n8Ivqz508sWWKGtECn4L+lPmXUm5g4g31cpXJMWCgKdWEQVmyiUUydl1C+1bRtstevtQb+Y6HFRo49i4dAALzfu9/GA593UpKAeOJLtAx81y4rnlGfRwSs05Mi9OJID+fUH8/pT9i214KAQKBgA2xYw7BzaV78qVlI9dkzvCGSzj09ZuLroFxUxwLbKGBnN8fKjn5erbgIF/D+TK5myqKBFREblMUJBwIEUSrN7ENq1FPRCuQI8bsa3gK/UBhQ87Z5h8hGYNPy6uu1KpwDWL2xVarhhIqZ/wOo9jAGothzcyDvJvh9Fbi8GlFBvic";
        String privateKeyBase64 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC39mxFEjYceNap4+AkdUIz31OZ+HU13ypqdSPe9tFW+Ff3b6J/qD+5nVxTzrGetJ8wXSIC684Bw6GWOWRhs+pM0199zkmrvP+lXUUo/ANuEdKXRoLriPlIeyOtuYLyhm6E7LsaCCYYXrQTqvi/RHyCBSZDuoXP1cw3p6wvANewM7aScR8suHDT7myPs1oSfDVapYOk9RCV+Es8lVSiKCFOyJkn2VZXZVbdeqL0yqvekc1DTa1TFhz56oSTuvDGnRVwa/EBqMKlsdgZZapsiJFGPC2bpCd5VZN4dhAq9aMgBvkJYE2qETArkJxXquHtdVoLHhOCGXL5S52YestuslQTAgMBAAECggEBAJcU02B6j2QcvbdzBX8Lqh4t2FwIWEKxEAW0RIx1ygESHjSBQ0RyKjvHh67wfqFnl6FBq5AYluyN/SQB6HXxK4iCw9bvV0OrCY2Gpyj8p9h63KgOabEEoi4rq2aeh3Okghm6zgKYFKlJBb1tWbLDjTm+/uj6w7Cn1nsSwm8r0vfKahEBQl56Gwwc0p9CvJzL7/GQwN6wdpjfuF7bpgH2YyOM4Vt+Fp/Irlg9RSjKKQnLOTokuwFYJSNdmiAQUQaqLNIAZth0zIG6M/V2yYnP6uYQ9Dn4JrwUtfBYzc14iFlqomp5euCYClZ4Ju5cQdJ+NBu6ZqTPoHTGcT0GYSMANmECgYEA8mOjI3YPR+son8MeQoCT0QRGQhrD4/HBPz3nutYfJ8KjDrrkhfN5vtHdn0Sdv9Xt6FhbqiBYTyQuSgwUzA/TKpJ+nLIG3RGxmHWxxO90AZ2MY7WD1PF87frnt2xv2japAr/j5grpS9HNMMx6AC9PNopsBPOWsvaLRs7Vd6hPKqMCgYEAwkrmEoHSCIDbYXem0cv63xw0H6AEbiJxGMIRb9HJ0KN3gwNzAilCTlUGPdfzfrDQNXbnxXrNyuNn/KbPdNu4f1llRnTgTM6ZsHdKEDUFDVmNMcl9ySNfFYQbUtce6TZKj9Dut31aXJt3CUj1SLuYPrWRue+P+KvHDNWCieZyt9ECgYEAl0AjaZpe+LK9+8tmk3rmQjRXnyJSgGe3/b2kX0sDz+y9kRCATzHaE3+juqG3iiB1P1n7gRKD6IacN+qIA7oQO+2EAkV40PQcjRVMI6aEDgoAs6ttozA5k8rQUv3gYhtfpW6WSFMgtwYo8z11F65t0IvZ0dEqhWwhBsNEI1GsbN8CgYBAmucmAET2zuIj5sVxvYIN7jLm0S6n8Ivqz508sWWKGtECn4L+lPmXUm5g4g31cpXJMWCgKdWEQVmyiUUydl1C+1bRtstevtQb+Y6HFRo49i4dAALzfu9/GA593UpKAeOJLtAx81y4rnlGfRwSs05Mi9OJID+fUH8/pT9i214KAQKBgA2xYw7BzaV78qVlI9dkzvCGSzj09ZuLroFxUxwLbKGBnN8fKjn5erbgIF/D+TK5myqKBFREblMUJBwIEUSrN7ENq1FPRCuQI8bsa3gK/UBhQ87Z5h8hGYNPy6uu1KpwDWL2xVarhhIqZ/wOo9jAGothzcyDvJvh9Fbi8GlFBvic";
        String privateKeyBase65 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC39mxFEjYceNap4+AkdUIz31OZ+HU13ypqdSPe9tFW+Ff3b6J/qD+5nVxTzrGetJ8wXSIC684Bw6GWOWRhs+pM0199zkmrvP+lXUUo/ANuEdKXRoLriPlIeyOtuYLyhm6E7LsaCCYYXrQTqvi/RHyCBSZDuoXP1cw3p6wvANewM7aScR8suHDT7myPs1oSfDVapYOk9RCV+Es8lVSiKCFOyJkn2VZXZVbdeqL0yqvekc1DTa1TFhz56oSTuvDGnRVwa/EBqMKlsdgZZapsiJFGPC2bpCd5VZN4dhAq9aMgBvkJYE2qETArkJxXquHtdVoLHhOCGXL5S52YestuslQTAgMBAAECggEBAJcU02B6j2QcvbdzBX8Lqh4t2FwIWEKxEAW0RIx1ygESHjSBQ0RyKjvHh67wfqFnl6FBq5AYluyN/SQB6HXxK4iCw9bvV0OrCY2Gpyj8p9h63KgOabEEoi4rq2aeh3Okghm6zgKYFKlJBb1tWbLDjTm+/uj6w7Cn1nsSwm8r0vfKahEBQl56Gwwc0p9CvJzL7/GQwN6wdpjfuF7bpgH2YyOM4Vt+Fp/Irlg9RSjKKQnLOTokuwFYJSNdmiAQUQaqLNIAZth0zIG6M/V2yYnP6uYQ9Dn4JrwUtfBYzc14iFlqomp5euCYClZ4Ju5cQdJ+NBu6ZqTPoHTGcT0GYSMANmECgYEA8mOjI3YPR+son8MeQoCT0QRGQhrD4/HBPz3nutYfJ8KjDrrkhfN5vtHdn0Sdv9Xt6FhbqiBYTyQuSgwUzA/TKpJ+nLIG3RGxmHWxxO90AZ2MY7WD1PF87frnt2xv2japAr/j5grpS9HNMMx6AC9PNopsBPOWsvaLRs7Vd6hPKqMCgYEAwkrmEoHSCIDbYXem0cv63xw0H6AEbiJxGMIRb9HJ0KN3gwNzAilCTlUGPdfzfrDQNXbnxXrNyuNn/KbPdNu4f1llRnTgTM6ZsHdKEDUFDVmNMcl9ySNfFYQbUtce6TZKj9Dut31aXJt3CUj1SLuYPrWRue+P+KvHDNWCieZyt9ECgYEAl0AjaZpe+LK9+8tmk3rmQjRXnyJSgGe3/b2kX0sDz+y9kRCATzHaE3+juqG3iiB1P1n7gRKD6IacN+qIA7oQO+2EAkV40PQcjRVMI6aEDgoAs6ttozA5k8rQUv3gYhtfpW6WSFMgtwYo8z11F65t0IvZ0dEqhWwhBsNEI1GsbN8CgYBAmucmAET2zuIj5sVxvYIN7jLm0S6n8Ivqz508sWWKGtECn4L+lPmXUm5g4g31cpXJMWCgKdWEQVmyiUUydl1C+1bRtstevtQb+Y6HFRo49i4dAALzfu9/GA593UpKAeOJLtAx81y4rnlGfRwSs05Mi9OJID+fUH8/pT9i214KAQKBgA2xYw7BzaV78qVlI9dkzvCGSzj09ZuLroFxUxwLbKGBnN8fKjn5erbgIF/D+TK5myqKBFREblMUJBwIEUSrN7ENq1FPRCuQI8bsa3gK/UBhQ87Z5h8hGYNPy6uu1KpwDWL2xVarhhIqZ/wOo9jAGothzcyDvJvh9Fbi8GlFBvic";

        String test = encrypt("{\"root\":\"DemandResponseInvitation\",\"version\":1,\"requestID\":\"10010103000017\",\"inviteData\":[{\"marketID\":\"20241129\",\"respID\":\"86876-12312-adsas17\",\"respName\":\"2024年11月27日顶峰（实测）\",\"respType\":1,\"resourceIds\":[\"elec20241122052\"],\"mktDeclStart\":\"2024-11-27 14:00:00\",\"mktDeclEnd\":\"2024-11-27 20:00:00\",\"pwrModStart\":\"2024-11-27 14:00:00\",\"pwrModEnd\":\"2024-11-27 20:00:00\",\"maxPrice\":2.4}]}", publicKeyBase64);
//        String test = encrypt("abc", publicKeyBase64);
        logger.debug("RSA编码结果: {}", test);
        logger.debug("开始解码");
        String decode = decode(test, privateKeyBase64);
        logger.debug("RSA解码结果: {}", decode);
//        String decode1 = decode("Qyl/ffeSnmBARDTuewx8FBpCX0SFLEI/B9a1KSr2YKrZSaTJ3f2sgmz0xqJt+6lK5S54YVexw3ZWuhClU0t1YDoAy+rhOxYY3eRdxuPVap2jug3P+6CsDhkFRHohLepLilrZeYjB84aMSlcoa1RwfTPuiVwtv9Kw+RwMMhd+wGS509PPXEoiQRYqswh7WpLBU9vtCnYGtpu2zfNEqoRGlA37UiygZgXzDMXTtVqaIn5CcnGRKhwn4ciXfyw/VNdxcz6m+ZUZmp+ODKbouEPqGoaK8OzvdTAzbMS3y6B2PW0d5v226EZUpieH0diiL8bDYvPJRAJdQ+xMVmF8Su4KqQ==", privateKeyBase64);
//        System.out.println("333 " + decode1);
    }
    /**
     *  加密
     * @param content
     * @param publicKeyBase64
     * @return
     */
    public static String encrypt(String content, String publicKeyBase64) {
        logger.info("content=={}", content);
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            // Use RSA/ECB/PKCS1Padding for better compatibility
            Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

            // Get the maximum block size (key length in bytes - 11 for PKCS1Padding)
            int maxBlockSize = 245;  // For 2048-bit key
            int dataLength = contentBytes.length;

            // If data is smaller than block size, encrypt directly
            if (dataLength <= maxBlockSize) {
                byte[] encryptedData = encryptCipher.doFinal(contentBytes);
                return Base64.getEncoder().encodeToString(encryptedData);
            }

            // For larger data, encrypt in chunks
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cache;

            while (dataLength - offset > 0) {
                if (dataLength - offset > maxBlockSize) {
                    cache = encryptCipher.doFinal(contentBytes, offset, maxBlockSize);
                } else {
                    cache = encryptCipher.doFinal(contentBytes, offset, dataLength - offset);
                }
                out.write(cache);
                offset += maxBlockSize;
            }

            byte[] encryptedData = out.toByteArray();
            out.close();
            return Base64.getEncoder().encodeToString(encryptedData);

        } catch (Exception e) {
            logger.error("Encryption failed", e);
            return null;
        }
    }

    /**
     * 解密
     * @param encryptedContent
     * @param privateKeyBase64
     * @return
     */
    public static String decode(String encryptedContent, String privateKeyBase64) {
        if (StringUtils.isEmpty(encryptedContent) || StringUtils.isEmpty(privateKeyBase64)) {
            logger.error("Decryption failed: Input content or private key is empty");
            return null;
        }

        logger.info("Starting decryption, encrypted content length: {}", encryptedContent.length());
        try {
            // 解码私钥
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            Cipher decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedContent);
            int maxBlockSize = 256;  // 2048位密钥的解密块大小
            int dataLength = encryptedBytes.length;

            logger.debug("Encrypted data length: {}, Max block size: {}", dataLength, maxBlockSize);

            // 小数据直接解密
            if (dataLength <= maxBlockSize) {
                try {
                    byte[] decryptedData = decryptCipher.doFinal(encryptedBytes);
                    return new String(decryptedData, StandardCharsets.UTF_8);
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    logger.error("Decryption failed for small data block", e);
                    throw e;
                }
            }

            // 大数据分块解密
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                int offset = 0;
                byte[] cache;
                int i = 1;

                while (dataLength - offset > 0) {
                    if (dataLength - offset > maxBlockSize) {
                        cache = decryptCipher.doFinal(encryptedBytes, offset, maxBlockSize);
                    } else {
                        cache = decryptCipher.doFinal(encryptedBytes, offset, dataLength - offset);
                    }
                    out.write(cache);
                    offset += maxBlockSize;
                    logger.debug("Processed block {}, offset: {}", i++, offset);
                }

                String result = new String(out.toByteArray(), StandardCharsets.UTF_8);
                logger.info("Decryption completed successfully, decrypted length: {}", result.length());
                return result;
            }

        } catch (Exception e) {
            logger.error("Invalid private key format", e);
            throw new RuntimeException("Invalid private key format", e);
        }
    }
}
