define([
  'jquery',
  'underscore',
  'backbone',
  'text!templates/user/registeruser/registeruserTemplate.html',
  'mvc/user/models/user',
], function($, _, Backbone,registeruserTemplate,registerUser){
  
  var registerView = Backbone.View.extend({
    
    el: $("#page"),
	 events: {
            "click #createUser": "createUser",
			"click #cancelUser":"cancelUser"
			
       },
	   initialize: function(opts){
	  this.app_router = opts.router;
	  },

    render: function(){

     this.$el.html(registeruserTemplate); 
	 return this;
    
    },
	createUser: function (){
	var mailId=document.getElementById('userMailId').value;
	var userPWD=document.getElementById('userPWD').value;
	var userName=document.getElementById('userName').value;
	var gender= $('.dataradio:checked').val();
	var authentication=document.getElementById('userAuthen').value;
	//var role=$('#role option:selected').val();
	//var roleId=document.getElementById('role').value;
	
	if($('#userMailId').val()=="")
	{
		alert("Please enter your email id");
		return false;
	}
	if(!(/^[a-z0-9_-]+(\.[a-z0-9_-]+)*@[a-z]+\.([c][o][m]{1})$/.test($('#userMailId').val()))){
		alert("Please enter your valid email id");
		return false;
	}	
		
	if($('#userPWD').val()=="")
	{
		alert("Please enter user password");
		return false;
	}
	if($('#userName').val()=="")
	{
		alert("Please enter userName");
		return false;
	}
	if($('#userAuthen').val()=="")
	{
		alert("Please enter userAuthen");
		return false;
	}
	
	
	

	
	this.model=new registerUser();
	
	this.model.set({"emailId":mailId});
	this.model.set({"password":userPWD});
	this.model.set({"userName":userName});
	this.model.set({"gender":gender});
	this.model.set({"authentication":authentication});
	//this.model.set({"roleId":role});
	
	this.model.save({},
            {
            	type:"PUT",
                success: function (model,responseText) {
                   alert("Inserted successfully.");
                },
                error: function(model,responseText,xhr) {
                	alert("You have been registered as a user at Eventshop. Please log  in."); 
                	 }

            });

	this.app_router.navigate('defaultAction', {trigger:true});
	},
	
	cancelUser: function(){
	this.app_router.navigate('defaultAction', {trigger:true});
	}

   
  });
  return registerView;
});
