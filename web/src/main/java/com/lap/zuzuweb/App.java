package com.lap.zuzuweb;

import static spark.Spark.before;
import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.patch;
import static spark.Spark.post;
import static spark.Spark.put;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.common.Provider;
import com.lap.zuzuweb.dao.CriteriaDao;
import com.lap.zuzuweb.dao.DeviceDao;
import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.dao.PurchaseDao;
import com.lap.zuzuweb.dao.ServiceDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.dao.Sql2O.CriteriaDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.DeviceDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.LogDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.NotifyItemDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.PurchaseDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.ServiceDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.Sql2OManager;
import com.lap.zuzuweb.dao.Sql2O.UserDaoBySql2O;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.cognito.CognitoTokenHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaCreateHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaModifyHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaPatchHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaQueryHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaRemoveHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaValidQueryHandler;
import com.lap.zuzuweb.handler.device.DeviceCreateHandler;
import com.lap.zuzuweb.handler.device.DeviceDeleteHandler;
import com.lap.zuzuweb.handler.device.DeviceGetHandler;
import com.lap.zuzuweb.handler.device.DeviceQueryHandler;
import com.lap.zuzuweb.handler.log.LogPatchHandler;
import com.lap.zuzuweb.handler.notifier.NotifierHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemBatchCreateHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemLatestReceiveCountHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemPatchHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemQueryHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemUnreadCountHandler;
import com.lap.zuzuweb.handler.purchase.PurchaseCreateHandler;
import com.lap.zuzuweb.handler.purchase.PurchaseQueryHandler;
import com.lap.zuzuweb.handler.purchase.PurchaseValidHandler;
import com.lap.zuzuweb.handler.service.ServiceQueryHandler;
import com.lap.zuzuweb.handler.system.DBInfoQueryHandler;
import com.lap.zuzuweb.handler.user.UserCheckHandler;
import com.lap.zuzuweb.handler.user.UserForgetPasswordHandler;
import com.lap.zuzuweb.handler.user.UserLoginBySocialTokenHandler;
import com.lap.zuzuweb.handler.user.UserLoginHandler;
import com.lap.zuzuweb.handler.user.UserQueryHandler;
import com.lap.zuzuweb.handler.user.UserRandomIdHandler;
import com.lap.zuzuweb.handler.user.UserRegisterHandler;
import com.lap.zuzuweb.handler.user.UserRemoveHandler;
import com.lap.zuzuweb.handler.user.UserResetPasswordHandler;
import com.lap.zuzuweb.handler.user.UserUpdateHandler;
import com.lap.zuzuweb.handler.user.UserVerifyCodeHandler;
import com.lap.zuzuweb.service.AuthService;
import com.lap.zuzuweb.service.AuthServiceImpl;
import com.lap.zuzuweb.service.CriteriaService;
import com.lap.zuzuweb.service.CriteriaServiceImpl;
import com.lap.zuzuweb.service.DeviceService;
import com.lap.zuzuweb.service.DeviceServiceImpl;
import com.lap.zuzuweb.service.HikariPoolJmxService;
import com.lap.zuzuweb.service.HikariPoolJmxServiceImpl;
import com.lap.zuzuweb.service.LogService;
import com.lap.zuzuweb.service.LogServiceImpl;
import com.lap.zuzuweb.service.NotifyItemService;
import com.lap.zuzuweb.service.NotifyItemServiceImpl;
import com.lap.zuzuweb.service.PurchaseService;
import com.lap.zuzuweb.service.PurchaseServiceImpl;
import com.lap.zuzuweb.service.UserService;
import com.lap.zuzuweb.service.UserServiceImpl;
import com.lap.zuzuweb.util.CommonUtils;
import com.lap.zuzuweb.util.HttpUtils;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.servlet.SparkApplication;

public class App implements SparkApplication
{
	private static final ZuzuLogger logger = ZuzuLogger.getLogger(App.class);
	
	private static boolean enableAuth = true;
	
	public final static String DB_POOL_NAME = "zuzu_rental_pool";
	
	public void init() {
		logger.info("App Initialization...");
		logger.info("Authorization enabled: " + enableAuth);
    	
		// Create Dao
    	UserDao userDao = new UserDaoBySql2O();
    	DeviceDao deviceDao = new DeviceDaoBySql2O();
    	CriteriaDao criteriaDao = new CriteriaDaoBySql2O();
    	NotifyItemDao notifyItemDao = new NotifyItemDaoBySql2O();
    	LogDao logDao = new LogDaoBySql2O();
    	PurchaseDao purchaseDao = new PurchaseDaoBySql2O();
    	ServiceDao serviceDao = new ServiceDaoBySql2O();
    	
    	// Create Svc
    	UserService userSvc = new UserServiceImpl(userDao, serviceDao);
    	DeviceService deviceSvc = new DeviceServiceImpl(deviceDao);
    	CriteriaService criteriaSvc = new CriteriaServiceImpl(criteriaDao);
    	NotifyItemService notifyItemSvc = new NotifyItemServiceImpl(notifyItemDao, logDao, userDao);
    	LogService logSvc = new LogServiceImpl(logDao);
    	PurchaseService purchaseSvc = new PurchaseServiceImpl(purchaseDao, userDao, serviceDao);
    	AuthService authSvc = new AuthServiceImpl(userDao);
    	HikariPoolJmxService dbpoolSvc = new HikariPoolJmxServiceImpl(DB_POOL_NAME);
    	
    	after((request, response) -> {
    		logger.exit("route", "[%s %s] Answer: %s", request.requestMethod(), request.uri().toString(), StringUtils.abbreviateMiddle(response.body(), "\n    ...\n", 512));
    	});
    	
    	before((request, response) -> {
    		logger.entering("route", "[%s %s] from %s", request.requestMethod(), request.uri().toString(), HttpUtils.getIpAddr(request));
    		
    		// discharge
    		if (StringUtils.equalsIgnoreCase(request.uri().toString(), "/alive")) {
    			return;
    		}
    		// discharge
    		if (StringUtils.startsWith(request.uri().toString(), "/public")) {
    			return;
    		}
    		
    		String authToken = request.headers("Authorization");
    		
    		if (StringUtils.isBlank(authToken)) {
    			logger.error("not found parameter [Authorization] in request header");
    		}
    		
    		if (StringUtils.equals(Secrets.SUPER_TOKEN, authToken)) {
    			logger.info("Validate super_token is successful.");
    			return;
    		}
    		
    		if (StringUtils.equals(Secrets.BASIC_TOKEN, authToken)) {
    			logger.info("Validate basic_token is successful.");
    			
    			String userProvider = request.headers("UserProvider");
        		String userToken = request.headers("UserToken");
        		
        		if (StringUtils.isBlank(userProvider)) {
        			userProvider = Provider.ZUZU_NOLOGIN.toString();
        		}
        		
    			if (authSvc.validateToken(userProvider, userToken)) {
    				return;
    			}
    		}
    		
    		response.type("application/json");
			halt(403, CommonUtils.toJson(Answer.forbidden()));
        });
    	
    	// public 
    	get("/public/user/randomid", new UserRandomIdHandler(userSvc));
    	get("/public/user/check/:email", new UserCheckHandler(userSvc));
    	post("/public/user/register", new UserRegisterHandler(userSvc));
    	post("/public/user/login", new UserLoginHandler(authSvc, userSvc));
    	post("/public/user/socialtoken/login", new UserLoginBySocialTokenHandler(authSvc, userSvc));
    	get("/public/user/password/forget/:email", new UserForgetPasswordHandler(authSvc));
    	post("/public/user/password/reset", new UserResetPasswordHandler(authSvc, userSvc));
    	get("/public/user/verify/:email/:verificationcode", new UserVerifyCodeHandler(authSvc));
    	
    	get("/public/db/info", new DBInfoQueryHandler(dbpoolSvc));
    	
    	
    	// cognito
    	post("/public/cognito/token", new CognitoTokenHandler(authSvc));
    	
    	// user
        get("/user/:userid", new UserQueryHandler(userSvc)); //get user by user id
        get("/user/email/:email", new UserQueryHandler(userSvc)); //get user by user id
    	put("/user", new UserUpdateHandler(userSvc)); // create a user
    	delete("/user/:userid/:email", new UserRemoveHandler(userSvc));
        
        // device
        get("/device/:userid", new DeviceQueryHandler(deviceSvc)); // get devices belonging to some user
        get("/device/:userid/:deviceid", new DeviceGetHandler(deviceSvc)); // get devices belonging to some user
        post("/device", new DeviceCreateHandler(deviceSvc));
        delete("/device/:userid", new DeviceDeleteHandler(deviceSvc)); //remove devices belonging to some user
        delete("/device/:userid/:deviceid", new DeviceDeleteHandler(deviceSvc)); // remove a device
        
        // criteria
        post("/criteria", new CriteriaCreateHandler(criteriaSvc)); // add a criteria
        put("/criteria/:userid/:criteriaid", new CriteriaModifyHandler(criteriaSvc)); // modify criteria
        patch("/criteria/:userid/:criteriaid", new CriteriaPatchHandler(criteriaSvc));
        delete("/criteria/:userid/:criteriaid", new CriteriaRemoveHandler(criteriaSvc)); // delete a criteria
        delete("/criteria/:userid", new CriteriaRemoveHandler(criteriaSvc)); // delete a criteria belonging to some user
        get("/criteria", new CriteriaQueryHandler(criteriaSvc)); // get all criteria
        get("/criteria/:userid", new CriteriaQueryHandler(criteriaSvc)); // get criteria belonging to some user
        get("/criteria/valid/:userid", new CriteriaValidQueryHandler(criteriaSvc, userSvc)); // get criteria belonging to some user
        
        // notify
        post("/notifyitem/batch", new NotifyItemBatchCreateHandler(notifyItemSvc)); // add a list of notify items
        get("/notifyitem/:userid", new NotifyItemQueryHandler(notifyItemSvc)); // get notify belonging to some user
        get("/notifyitem/:userid/after/:posttime", new NotifyItemQueryHandler(notifyItemSvc)); // get notify belonging to some user
        get("/notifyitem/unreadcount/:userid", new NotifyItemUnreadCountHandler(notifyItemSvc));
        get("/notifyitem/latestreceivecount/:userid", new NotifyItemLatestReceiveCountHandler(notifyItemSvc));
        patch("/notifyitem/:userid/:itemid", new NotifyItemPatchHandler(notifyItemSvc)); 
        
        // log
        patch("/log/:userid/:deviceid", new LogPatchHandler(logSvc));
        
        // purchase
        post("/purchase", new PurchaseCreateHandler(purchaseSvc)); // add a purchase
        get("/purchase/:userid", new PurchaseQueryHandler(purchaseSvc)); // get a list of purchases belonging to some user
        get("/purchase/valid/:userid", new PurchaseValidHandler(purchaseSvc)); // get a list of purchases belonging to some user
        
        // service
        get("/service/:userid", new ServiceQueryHandler(userSvc, purchaseSvc)); // get a list of purchases belonging to some user
        
        // notifier
        get("/notifier", new NotifierHandler(criteriaSvc, deviceSvc)); // get a list of purchases belonging to some user
        
        get("/alive", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "ok";
            }
        });
	}
	
	public void destroy() {
		logger.info("App destroy.");
		Sql2OManager.close();
	}
	
}
