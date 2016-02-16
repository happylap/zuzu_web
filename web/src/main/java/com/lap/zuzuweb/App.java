package com.lap.zuzuweb;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.patch;
import static spark.Spark.post;
import static spark.Spark.put;

import com.lap.zuzuweb.dao.CriteriaDao;
import com.lap.zuzuweb.dao.DeviceDao;
import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.dao.Sql2O.CriteriaDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.DeviceDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.NotifyItemDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.UserDaoBySql2O;
import com.lap.zuzuweb.handler.criteria.CriteriaCreateHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaModifyHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaPatchHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaQueryHandler;
import com.lap.zuzuweb.handler.criteria.CriteriaRemoveHandler;
import com.lap.zuzuweb.handler.device.DeviceCreateHandler;
import com.lap.zuzuweb.handler.device.DeviceDeleteHandler;
import com.lap.zuzuweb.handler.device.DevicePatchHandler;
import com.lap.zuzuweb.handler.device.DeviceQueryHandler;
import com.lap.zuzuweb.handler.device.DeviceUpdateHandler;
import com.lap.zuzuweb.handler.notifyItem.GetUserNotifyItemHandler;
import com.lap.zuzuweb.handler.notifyItem.GetUserUnreadNotifyItemCountHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemBatchCreateHandler;
import com.lap.zuzuweb.handler.notifyItem.NotifyItemPatchHandler;
import com.lap.zuzuweb.handler.user.UserCreateHandler;
import com.lap.zuzuweb.handler.user.UserQueryHandler;
import com.lap.zuzuweb.service.CriteriaService;
import com.lap.zuzuweb.service.CriteriaServiceImpl;
import com.lap.zuzuweb.service.DeviceService;
import com.lap.zuzuweb.service.DeviceServiceImpl;
import com.lap.zuzuweb.service.NotifyItemService;
import com.lap.zuzuweb.service.NotifyItemServiceImpl;
import com.lap.zuzuweb.service.UserService;
import com.lap.zuzuweb.service.UserServiceImpl;

import spark.Request;
import spark.Response;
import spark.Route;

public class App 
{
    public static void main( String[] args )
    {
    	UserDao userDao = new UserDaoBySql2O();
    	UserService userSvc = new UserServiceImpl(userDao);
 
    	DeviceDao deviceDao = new DeviceDaoBySql2O();
    	DeviceService deviceSvc = new DeviceServiceImpl(deviceDao);

    	CriteriaDao criteriaDao = new CriteriaDaoBySql2O();
    	CriteriaService criteriaSvc = new CriteriaServiceImpl(criteriaDao);
    	
    	NotifyItemDao notifyItemDao = new NotifyItemDaoBySql2O();
    	NotifyItemService notifyItemSvc = new NotifyItemServiceImpl(notifyItemDao);
    	
    	// user
    	post("/user", new UserCreateHandler(userSvc)); // create a user
        get("/user/:userid", new UserQueryHandler(userSvc)); //get user by user id
        
        // device
        post("/device", new DeviceCreateHandler(deviceSvc));
        put("/device", new DeviceUpdateHandler(deviceSvc));
        delete("/device/userid/:userid", new DeviceDeleteHandler(deviceSvc)); //remove devices belonging to some user
        delete("/device/:deviceid", new DeviceDeleteHandler(deviceSvc)); // remove a device
        get("/device", new DeviceQueryHandler(deviceSvc)); // get all devices
        get("/device/:userid", new DeviceQueryHandler(deviceSvc)); // get devices belonging to some user
        patch("/device/:deviceid/:userid", new DevicePatchHandler(deviceSvc));
        
        // criteria
        post("/criteria", new CriteriaCreateHandler(criteriaSvc)); // add a criteria
        put("/criteria/update/:criteriaid/:userid", new CriteriaModifyHandler(criteriaSvc)); // modify criteria
        patch("/criteria/:criteriaid/:userid", new CriteriaPatchHandler(criteriaSvc));
        delete("/criteria/:criteriaid/:userid", new CriteriaRemoveHandler(criteriaSvc)); // delete a criteria
        delete("/criteria/:userid", new CriteriaRemoveHandler(criteriaSvc)); // delete a criteria belonging to some user
        get("/criteria", new CriteriaQueryHandler(criteriaSvc)); // get all criteria
        get("/criteria/:criteriaid/:userid", new CriteriaQueryHandler(criteriaSvc)); // get criteria belonging to some user
        get("/criteria/:userid", new CriteriaQueryHandler(criteriaSvc)); // get criteria belonging to some user
        
        // notify
        post("/notifyitem/batch", new NotifyItemBatchCreateHandler(notifyItemSvc)); // add a list of notify items
        get("/notifyitem/:userid", new GetUserNotifyItemHandler(notifyItemSvc)); // get notify belonging to some user
        get("/notifyitem/unread/count/:userid", new GetUserUnreadNotifyItemCountHandler(notifyItemSvc));
        patch("/notifyitem/:itemid/:userid", new NotifyItemPatchHandler(notifyItemSvc)); 
        
        
        get("/alive", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "ok";
            }
        });
    }
}
