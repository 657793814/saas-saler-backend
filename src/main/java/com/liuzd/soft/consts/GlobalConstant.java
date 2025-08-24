package com.liuzd.soft.consts;


public class GlobalConstant {

    public final static String DEFAULT_DB_KEY = "default";  //默认数据源的key,注意与yml中静态库配置成一样的
    public final static String LOG_DB_KEY = "logDbKey";  //log库

    public final static String CONFIG_DB_NAME = "saas_center";  //默认库
    public final static String OPENID_PREFIX = "openid_";
    public final static String UNION_ID_PREFIX = "unionid_";
    public final static String PRODUCT_ID_PREFIX = "spu";
    public final static String SKU_ID_PREFIX = "sku";
    public final static String TEST_SMS_CODE = "9527";
    public final static String LOGIN_USER_INFO = "loginUserInfo"; //登录成功后写入登录用户数据
    public final static String SPEC_TYPE_PREFIX = "tmp_type_";
    public final static String SPEC_VALUE_PREFIX = "tmp_value_";

    public final static String TOKEN_CACHE_PREFIX = "token:";
    public final static String REFRESH_TOKEN_CACHE_PREFIX = "refreshToken:";
    public final static String USER_TOKEN_CACHE_PREFIX = "userTokenInfo:";

    public final static String DEFAULT_ADMIN_ROLE = "admin";
    public final static int DEFAULT_ADMIN_ROLE_ID = 1;

    //request参数
    public final static String HEADER_TENANT_CODE = "header_tenant_code";  //请求头的租户code key
    public final static String REQUEST_PARAM_TENANT_ID_KEY = "tenant_id";
    public final static String REQUEST_PARAM_TENANT_CODE_KEY = "tenant_code";
    public final static String REQUEST_PARAM_RAND_STR_KEY = "rand_str";
    public final static String REQUEST_PARAM_TOKEN_KEY = "token";
    public final static String REQUEST_PARAM_SIGN_KEY = "sign";
    public final static String REQUEST_PARAM_TIMESTAMP_KEY = "timestamp";

    //log
    public final static String LOGTRACE_TRACEID = "x-my-TraceID";
    public final static String LOGTRACE_FAST_TRACEID = "x-fast-trace-id";
}
