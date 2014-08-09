package com.eventshop.eventshoplinux.service;

import javax.ws.rs.Consumes;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eventshop.eventshoplinux.DAO.user.UserManagementDAO;
import com.eventshop.eventshoplinux.domain.login.RegisterUser;
import com.eventshop.eventshoplinux.domain.login.User;


@Path("/registeruserservice")
public class RegisterUserService {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/register")
	public String registerUser(RegisterUser registerUser){
		System.out.println("we are entering");
		UserManagementDAO eventShopDAO=new UserManagementDAO(); 
		
		return eventShopDAO.saveUser(registerUser);
		
		//return "Success";
		
	}
}
