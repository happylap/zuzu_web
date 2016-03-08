package com.lap.zuzuweb;

import static spark.Spark.before;
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
import com.lap.zuzuweb.dao.Sql2O.UserDaoBySql2O;
import com.lap.zuzuweb.handler.Answer;
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
import com.lap.zuzuweb.handler.notifyItem.NotifyItemQueryHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemUnreadCountHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemBatchCreateHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemPatchHandler;
import com.lap.zuzuweb.handler.purchase.PurchaseCreateHandler;
import com.lap.zuzuweb.handler.purchase.PurchaseQueryHandler;
import com.lap.zuzuweb.handler.service.ServiceQueryHandler;
import com.lap.zuzuweb.handler.user.UserCreateOrUpdateHandler;
import com.lap.zuzuweb.handler.user.UserQueryHandler;
import com.lap.zuzuweb.service.AuthService;
import com.lap.zuzuweb.service.AuthServiceImpl;
import com.lap.zuzuweb.service.CriteriaService;
import com.lap.zuzuweb.service.CriteriaServiceImpl;
import com.lap.zuzuweb.service.DeviceService;
import com.lap.zuzuweb.service.DeviceServiceImpl;
import com.lap.zuzuweb.service.LogService;
import com.lap.zuzuweb.service.LogServiceImpl;
import com.lap.zuzuweb.service.NotifyItemService;
import com.lap.zuzuweb.service.NotifyItemServiceImpl;
import com.lap.zuzuweb.service.PurchaseService;
import com.lap.zuzuweb.service.PurchaseServiceImpl;
import com.lap.zuzuweb.service.UserService;
import com.lap.zuzuweb.service.UserServiceImpl;
import com.lap.zuzuweb.util.CommonUtils;

import spark.Request;
import spark.Response;
import spark.Route;

public class App 
{
	public static boolean enableAuth = true;
	
    public static void main( String[] args )
    {	

    	AuthService authSvc = new AuthServiceImpl();
    	
    	before((request, response) -> {

    		System.out.println("url: " + request.uri().toString());
    		
    		if (!enableAuth) {
    			return;
    		}
    		
    		if (authSvc.isSuperTokenValid(request.headers("Authorization"))) {
    			return;
    		}
    		
    		System.out.println("Header Authorization: " + request.headers("Authorization"));
    		
    		if (authSvc.isBasicTokenValid(request.headers("Authorization"))) {
    			
	    		System.out.println("Header UserProvider: " + request.headers("UserProvider"));
	    		System.out.println("Header UserToken: " + request.headers("UserToken"));
	    		System.out.println("Header UserId: " + request.headers("UserId"));
	
	    		String userProvider = request.headers("UserProvider");
	    		String userToken = request.headers("UserToken");
	    		//String userId = request.headers("UserId");
	    		
	    		try {
		    		if (StringUtils.equalsIgnoreCase(userProvider, Provider.FB.toString())) {
		    			if (authSvc.isFacebookTokenValid(userToken)) {
		    				return;
		    			}
		    		}
		    		
		    		if (StringUtils.equalsIgnoreCase(userProvider, Provider.GOOGLE.toString())) {
		    			if (authSvc.isGoogleTokenValid(userToken)) {
		    				return;
		    			}
		    		}
	    		} catch (Exception e) {
	    			response.type("application/json");
	    			halt(403, CommonUtils.toJson(Answer.forbidden(e.getMessage())));
	    		}
    		}
    		
    		response.type("application/json");
			halt(403, CommonUtils.toJson(Answer.forbidden()));
        });
        
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
    	NotifyItemService notifyItemSvc = new NotifyItemServiceImpl(notifyItemDao);
    	LogService logSvc = new LogServiceImpl(logDao);
    	PurchaseService purchaseSvc = new PurchaseServiceImpl(purchaseDao, userDao, serviceDao);
    	
    	// user
    	post("/user/:provider/:userid", new UserCreateOrUpdateHandler(userSvc)); // create a user
        get("/user/:provider/:userid", new UserQueryHandler(userSvc)); //get user by user id
        
        // device
        get("/device/:provider/:userid", new DeviceQueryHandler(deviceSvc)); // get devices belonging to some user
        get("/device/:provider/:userid/:deviceid", new DeviceGetHandler(deviceSvc)); // get devices belonging to some user
        post("/device/:provider/:userid", new DeviceCreateHandler(deviceSvc));
        delete("/device/:provider/:userid", new DeviceDeleteHandler(deviceSvc)); //remove devices belonging to some user
        delete("/device/:provider/:userid/:deviceid", new DeviceDeleteHandler(deviceSvc)); // remove a device
        
        // criteria
        post("/criteria/:provider/:userid", new CriteriaCreateHandler(criteriaSvc)); // add a criteria
        put("/criteria/:provider/:userid/:criteriaid", new CriteriaModifyHandler(criteriaSvc)); // modify criteria
        patch("/criteria/:provider/:userid/:criteriaid", new CriteriaPatchHandler(criteriaSvc));
        delete("/criteria/:provider/:userid/:criteriaid", new CriteriaRemoveHandler(criteriaSvc)); // delete a criteria
        delete("/criteria/:provider/:userid", new CriteriaRemoveHandler(criteriaSvc)); // delete a criteria belonging to some user
        get("/criteria", new CriteriaQueryHandler(criteriaSvc)); // get all criteria
        get("/criteria/:provider/:userid", new CriteriaQueryHandler(criteriaSvc)); // get criteria belonging to some user
        get("/criteria/:provider/:userid/valid", new CriteriaValidQueryHandler(criteriaSvc, userSvc)); // get criteria belonging to some user
        
        // notify
        post("/notifyitem/batch", new NotifyItemBatchCreateHandler(notifyItemSvc)); // add a list of notify items
        get("/notifyitem/:provider/:userid", new NotifyItemQueryHandler(notifyItemSvc)); // get notify belonging to some user
        get("/notifyitem/:provider/:userid/unread/count", new NotifyItemUnreadCountHandler(notifyItemSvc));
        patch("/notifyitem/:provider/:userid/:itemid", new NotifyItemPatchHandler(notifyItemSvc)); 
        
        // log
        patch("/log/:provider/:userid/:deviceid", new LogPatchHandler(logSvc));
        
        // purchase
        post("/purchase/:provider/:userid", new PurchaseCreateHandler(purchaseSvc)); // add a purchase
        get("/purchase/:provider/:userid", new PurchaseQueryHandler(purchaseSvc)); // get a list of purchases belonging to some user 
        
        // service
        get("/service/:provider/:userid", new ServiceQueryHandler(userSvc, purchaseSvc)); // get a list of purchases belonging to some user
        
        get("/alive", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "ok";
            }
        });
        
    }
}
