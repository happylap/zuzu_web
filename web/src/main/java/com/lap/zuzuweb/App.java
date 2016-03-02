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
import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.dao.PurchaseDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.dao.Sql2O.CriteriaDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.LogDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.NotifyItemDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.PurchaseDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.UserDaoBySql2O;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.criteria.CriteriaCreateHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaModifyHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaPatchHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaQueryHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaRemoveHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaValidQueryHandler;
import com.lap.zuzuweb.handler.log.LogPatchHandler;
import com.lap.zuzuweb.handler.notifyItem.GetUserNotifyItemHandler;
import com.lap.zuzuweb.handler.notifyItem.GetUserUnreadNotifyItemCountHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemBatchCreateHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemPatchHandler;
import com.lap.zuzuweb.handler.purchase.PurchaseCreateHandler;
import com.lap.zuzuweb.handler.purchase.PurchaseQueryHandler;
import com.lap.zuzuweb.handler.user.UserCreateOrUpdateHandler;
import com.lap.zuzuweb.handler.user.UserQueryHandler;
import com.lap.zuzuweb.service.CriteriaService;
import com.lap.zuzuweb.service.CriteriaServiceImpl;
import com.lap.zuzuweb.service.LogService;
import com.lap.zuzuweb.service.LogServiceImpl;
import com.lap.zuzuweb.service.NotifyItemService;
import com.lap.zuzuweb.service.NotifyItemServiceImpl;
import com.lap.zuzuweb.service.PurchaseService;
import com.lap.zuzuweb.service.PurchaseServiceImpl;
import com.lap.zuzuweb.service.UserService;
import com.lap.zuzuweb.service.UserServiceImpl;
import com.lap.zuzuweb.util.AuthUtils;
import com.lap.zuzuweb.util.CommonUtils;

import spark.Request;
import spark.Response;
import spark.Route;

public class App 
{
	public static boolean enableAuth = false;
	
    public static void main( String[] args )
    {	
    	
    	before((request, response) -> {

    		System.out.println("url: " + request.uri().toString());
    		
    		if (!enableAuth) {
    			return;
    		}
    		
    		if (AuthUtils.isSuperTokenValid(request.headers("Authorization"))) {
    			return;
    		}
    		
    		System.out.println("Header Authorization: " + request.headers("Authorization"));
    		
    		if (AuthUtils.isBasicTokenValid(request.headers("Authorization"))) {
    			
	    		System.out.println("Header UserProvider: " + request.headers("UserProvider"));
	    		System.out.println("Header UserToken: " + request.headers("UserToken"));
	    		System.out.println("Header UserId: " + request.headers("UserId"));
	
	    		String userProvider = request.headers("UserProvider");
	    		String userToken = request.headers("UserToken");
	    		String userId = request.headers("UserId");
	    		
	    		try {
		    		if (StringUtils.equalsIgnoreCase(userProvider, Provider.FB.toString())) {
		    			if (AuthUtils.isFacebookTokenValid(userToken, userId)) {
		    				return;
		    			}
		    		}
		    		
		    		if (StringUtils.equalsIgnoreCase(userProvider, Provider.GOOGLE.toString())) {
		    			if (AuthUtils.isGoogleTokenValid(userToken, userId)) {
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
        
    	
    	UserDao userDao = new UserDaoBySql2O();
    	UserService userSvc = new UserServiceImpl(userDao);
 
    	/*
    	DeviceDao deviceDao = new DeviceDaoBySql2O();
    	DeviceService deviceSvc = new DeviceServiceImpl(deviceDao);
		*/
    	
    	CriteriaDao criteriaDao = new CriteriaDaoBySql2O();
    	CriteriaService criteriaSvc = new CriteriaServiceImpl(criteriaDao);
    	
    	NotifyItemDao notifyItemDao = new NotifyItemDaoBySql2O();
    	NotifyItemService notifyItemSvc = new NotifyItemServiceImpl(notifyItemDao);

    	LogDao logDao = new LogDaoBySql2O();
    	LogService logSvc = new LogServiceImpl(logDao);
    	
    	PurchaseDao purchaseDao = new PurchaseDaoBySql2O();
    	PurchaseService purchaseSvc = new PurchaseServiceImpl(purchaseDao, userDao, criteriaDao);
    	
    	// user
    	post("/user", new UserCreateOrUpdateHandler(userSvc)); // create a user
        get("/user/:userid", new UserQueryHandler(userSvc)); //get user by user id
        
        // device
        /*
        post("/device", new DeviceCreateHandler(deviceSvc));
        put("/device", new DeviceUpdateHandler(deviceSvc));
        delete("/device/userid/:userid", new DeviceDeleteHandler(deviceSvc)); //remove devices belonging to some user
        delete("/device/:deviceid", new DeviceDeleteHandler(deviceSvc)); // remove a device
        get("/device", new DeviceQueryHandler(deviceSvc)); // get all devices
        get("/device/:userid", new DeviceQueryHandler(deviceSvc)); // get devices belonging to some user
        patch("/device/:deviceid/:userid", new DevicePatchHandler(deviceSvc));
        */
        
        // criteria
        post("/criteria", new CriteriaCreateHandler(criteriaSvc)); // add a criteria
        put("/criteria/update/:criteriaid/:userid", new CriteriaModifyHandler(criteriaSvc)); // modify criteria
        patch("/criteria/:criteriaid/:userid", new CriteriaPatchHandler(criteriaSvc));
        delete("/criteria/:criteriaid/:userid", new CriteriaRemoveHandler(criteriaSvc)); // delete a criteria
        delete("/criteria/:userid", new CriteriaRemoveHandler(criteriaSvc)); // delete a criteria belonging to some user
        get("/criteria", new CriteriaQueryHandler(criteriaSvc)); // get all criteria
        get("/criteria/:userid", new CriteriaQueryHandler(criteriaSvc)); // get criteria belonging to some user
        get("/criteria/valid/:userid", new CriteriaValidQueryHandler(criteriaSvc)); // get criteria belonging to some user
        
        
        // notify
        post("/notifyitem/batch", new NotifyItemBatchCreateHandler(notifyItemSvc)); // add a list of notify items
        get("/notifyitem/:userid", new GetUserNotifyItemHandler(notifyItemSvc)); // get notify belonging to some user
        get("/notifyitem/unread/count/:userid", new GetUserUnreadNotifyItemCountHandler(notifyItemSvc));
        patch("/notifyitem/:itemid/:userid", new NotifyItemPatchHandler(notifyItemSvc)); 
        
        // log
        patch("/log/:deviceid/:userid", new LogPatchHandler(logSvc));
        
        // purchase
        post("/purchase", new PurchaseCreateHandler(purchaseSvc)); // add a purchase
        get("/purchase/:userid", new PurchaseQueryHandler(purchaseSvc)); // get a list of purchases belonging to some user 
        
        
        get("/alive", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "ok";
            }
        });
        
    }
}
