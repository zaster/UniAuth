package com.dianrong.common.uniauth.common.cons;

/**
 * Created by Arc on 26/1/16.
 */
public interface AppConstants {
	// 0 = 启用
    Byte ZERO_Byte = (byte)0;
	// 1 = 禁用
    Byte ONE_Byte = (byte)1;
	// 0 = 启用
    byte ZERO_byte = (byte)0;
	// 1 = 禁用
    byte ONE_byte = (byte)1;

	String NODE_TYPE_GROUP = "grp";
	String NODE_TYPE_MEMBER_USER = "mUser";
	String NODE_TYPE_OWNER_USER = "oUser";

    String GRP_ROOT = "GRP_ROOT";
    byte MAX_AUTH_FAIL_COUNT = 10;
    int MAX_PASSWORD_VALID_MONTH = 2;
	// this value can avoid the "data.query.result.number.exceed" exception when you need query as much as more.
	Integer MAX_PAGE_SIZE_MINUS_ONE = 4999;
	// if you don't pass pageSize parameter to the query api, it is the default value.
	Integer MAX_PAGE_SIZE = 5000;
	// if you don't pass pageNumber parameter to the query api, it is the default value.
	Integer MIN_PAGE_NUMBER = 0;
	String PERM_TYPE_DOMAIN = "DOMAIN";
	String PERM_TYPE_URIPATTERN = "URI_PATTERN";
	String PERM_TYPE_PRIVILEGE = "PRIVILEGE";
    String DOMAIN_CODE_TECHOPS = "techops";
    
	// 分隔符
	String ZK_CFG_SPLIT= ".";
	// 域
	String ZK_DOMAIN = "domains";
	// eg domains.
	String ZK_DOMAIN_PREFIX = ZK_DOMAIN + ZK_CFG_SPLIT ;
	// 域的登陆页属性
	String ZK_DOMAIN_LOGIN_PAGE = "loginPage";
	// zk 配置的node name， 是否手动删除service ticket
	String ZK_NODE_NAME_MANUAL_DELETE_SERVICE_TICKET = "cas.reuse_st_not_allowed";
	
	String SERVICE_LOGIN_POSTFIX = "/login/cas";
	
	String ROLE_ADMIN = "ROLE_ADMIN";
	String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
	
	String CROSS_RESOURCE_ORIGIN_HEADER = "Origin";
	String AJAX_HEADER = "X-Requested-With";
	String JQUERY_XMLHttpRequest_HEADER = "XMLHttpRequest";
	String LOGIN_REDIRECT_URL = "LOGIN_REDIRECT_URL";
	String NO_PRIVILEGE = "NO_PRIVILEGE";

	String PERM_TYPE_DOMAIN_ID = "DOMAIN_ID";
	String CFG_TYPE_TEXT = "TEXT";
	String CFG_TYPE_FILE = "FILE";


	//constants for password forget

	//dispatcher parameter
	String PWDFORGET_DISPATCHER_STEP_KEY = "step";
	String PWDFORGET_DISPATCHER_CONTEXTURL_KEY = "savedLoginContext";
	String CAS_CAPTCHA_SESSION_TYPE_KEY = "captchaType";

	//request
	String PWDFORGET_PAGE_VERIFY_CODE_CLIENT_KEY = "pageVerifyCode";
	String PWDFORGET_MAIL_VAL_CLIENT_KEY="email";
	String PWDFORGET_MAIL_VERIFY_CODE_CLIENT_KEY = "verifyCode";
	String PWDFORGET_NEW_PASSWORD_KEY = "newPassword";
	String LOGIN_SCROLL_IMAGES_MODEL_KEY = "loginImges";

	//session
	String CAS_CAPTCHA_SESSION_KEY = "CAS_CAPTCHA_SESSION_KEY";
	String PWDFORGET_MAIL_VAL_KEY = "pwdg_emailVal";
	String PWDFORGET_MAIL_VERIFY_CODE_KEY = "pwdg_verifyCode";
	String PWDFORGET_DISPATCHER_CONTEXTURL_SESSION_KEY = "pwdg_savedLoginContext";
	String PWDFORGET_MAIL_VERIFY_EXPIRDATE_KEY = "pwdg_verifyExpirDate";

	//60 secondes
	long PWDFORGET_MAIL_VERIFY_CODE_EXPIRE_MILLES = 60L * 60L * 1000L;
	//5 minitues
	long PWDFORGET_MAIL_VERIFY_EXPIRDATE_MILLES = 5L * 60L * 1000L;
	
	//tag for userinfo edit in request
	String CAS_USERINFO_MANAGE_EDIT_KEY = "edituserinfo";
	String CAS_USERINFO_MANAGE_REQUEST_METHOD_KEY = "form_method";
	String CAS_USERINFO_MANAGE_FLOW_REQUEST_METHOD_TYPE_KEY = "user_mangage_flow_request_type";
	String CAS_USERINFO_MANAGE_OPERATE_ERRORMSG_TAG = "user_manage_errormsg";
	
	//更新类型 
	String CAS_USERINFO_MANAGE_UPDATE_METHOD_TYPE_TAG = "updatetype";
	
	//用户信息获取
	String CAS_USERINFO_MANAGE_USER_ID_TAG = "id";
	String CAS_USERINFO_MANAGE_UPDATE_EMAIL_TAG = "email";
	String CAS_USERINFO_MANAGE_UPDATE_PHONE_TAG = "phone";
	String CAS_USERINFO_MANAGE_UPDATE_NAME_TAG = "name";
	String CAS_USERINFO_MANGE_UPDATE_PASSWORD_TAG = "password";
	String CAS_USERINFO_MANGE_UPDATE_ORIGINPASSWORD_TAG = "orign_password";
	
	//cas登陆captcha的session值的key
	String CAS_USER_LOGIN_CAPTCHA_VALIDATION_SESSION_KEY = "cas_login_captcha_validation_session_key";
	
	String PERM_GROUP_OWNER = "PERM_GROUP_OWNER";

	String PERM_USERID_CHECK = "PERM_USERID_CHECK";
	String PERM_ROLEID_CHECK = "PERM_ROLEID_CHECK";
	String PERM_ROLEIDS_CHECK = "PERM_ROLEIDS_CHECK";
	String PERM_PERMID_CHECK = "PERM_PERMID_CHECK";
	String PERM_PERMIDS_CHECK = "PERM_PERMIDS_CHECK";
	String PERM_TAGTYPEID_CHECK = "PERM_TAGTYPEID_CHECK";
	String PERM_TAGID_CHECK = "PERM_TAGID_CHECK";

	String MAIL_PREFIX = "[TechOps]";
	int GLOBALVAR_QUEUE_SIZE = 2048;
	int AUDIT_INSERT_LIST_SIZE = 30;
	int AUDIT_INSERT_EVERY_SECOND = 120;
	int AUDIT_INSERT_EXP_LENGTH = 3072;
	int AUDIT_INSET_PARAM_LENGTH = 256;
	
	//cas cfg refresh
	//default 120 minitues
	long CAS_CFG_CACHE_REFRESH_PERIOD_MILLES = 120L * 60L * 1000L;
	
	//cascfg cache model in application's key
	String CAS_CFG_CACHE_MODEL_APPLICATION_KEY = "cascfg_cache_key";
	
	//cas cfg cfg_key list
	String CAS_CFG_KEY_LOGO = "CAS_LOGO";
	String CAS_CFG_KEY_ICON = "CAS_ICON";
	String CAS_CFG_KEY_TITLE = "CAS_TITLE";
	String CAS_CFG_KEY_ALL_RIGHT = "CAS_ALL_RIGHT";
	String CAS_CFG_KEY_LOGIN_AD_IMG = "CAS_LOGIN_AD_IMG";
	// cfg cas orgin cfg key prefix
	String CAS_CFG_KEY_CROSS_FILTER_ORIGIN_PREFIX = "CAS_CROSS_FILTER_ORGIN";
	
	// define the cfg type id 
	int CAS_CFG_TYPE_FILE_ID = 1;
	int CAS_CFG_TYPE_TEXT_ID = 2;
	
	// 首页广告对应herf的cfg key的后缀
	String CAS_CFG_LOGIN_AD_HREF_SUFFIX = "_HREF";
	//默认定义的跳转url
	String CAS_CFG_HREF_DEFALT_VAL= "#";
	
	String CAS_CFG_KEY_BACKGROUND_COLOR = "CAS_BACKGROUND_COLOR";
	
	String HTTP_METHOD_ALL = "ALL";
	
	// login ticket prefix
	String CAS_LOGIN_TICKET_PREFIX = "LT";
	
	// 用户跳转的url路径
	String CAS_CUSTOM_LOGIN_PAGE_KEY = "custom_login_page_key";
	String CAS_CUSTOM_LOGIN_LT_KEY = "custom_login_lt_key";
	String CAS_CUSTOM_LOGIN_URL_KEY = "custom_login_url_key";
}
