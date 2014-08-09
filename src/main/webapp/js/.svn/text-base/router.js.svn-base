// Filename: router.js
define([
  'jquery',
  'underscore',
  'backbone',
  'mvc/admin/views/login/adminLogin',
  'mvc/admin/views/adminHome' , 
  'mvc/user/views/login/login',
  //'mvc/admin/models/adminHomeModel',
  //'mvc/admin/models/dataSourceModel',
  'mvc/user/views/registeruser/Register',
 'mvc/user/views/homePage',
  
  ], function($, _, Backbone, adminLogin, adminHome, loginView,registerView,homePageView) {
  
var AppRouter = Backbone.Router.extend({
    routes: {
      
       
       'admin': 'showAdminLogin',
       'adminHome': 'showAdminHome',
	   //'addDS':'showAddDS',
	   'registerUserPage':'showRegisterUserPage',
       'homePage':'showHomePage',	
    
	  '*actions': 'defaultAction'
    }
  });
  
  var initialize = function(){

    var app_router = new AppRouter;
    
    
	app_router.on('route:showAdminHome', function () {	   
        this.adminHome = new adminHome({router:this});
		this.adminHome.render();
    });
	
	app_router.on('route:showAdminLogin', function (actions) {           
        adminLogin = new adminLogin({router:this});
        adminLogin.render();
    });
	app_router.on('route:defaultAction', function (actions) {           
       this.loginView = new loginView({router:this});
       this.loginView.render();
    });
	/*app_router.on('route:showAddDS', function () {
        
        this.dataSourceModel = new dataSourceModel({router:this});
		this.dataSourceModel.render();
    });*/

	     app_router.on('route:showHomePage', function () {
        this.homePageView = new homePageView({router:this});
        this.homePageView.render();
                                                                                                
    }); 

    app_router.on('route:showRegisterUserPage', function () {
    
        this.registerView = new registerView({router:this});
		this.registerView.render();
    });
	 
	
    Backbone.history.start();
  };
  return { 
    initialize: initialize
  };
});
