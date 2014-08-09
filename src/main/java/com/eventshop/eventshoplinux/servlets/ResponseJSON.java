package com.eventshop.eventshoplinux.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import twitter4j.internal.http.HttpResponse;

import com.google.gson.JsonObject;


public class ResponseJSON {
	
	public enum ResponseStatus{SUCCESS, INFO, ERROR};
	public JsonObject response;
	public HttpServletResponse servlet;
	
	public ResponseJSON(HttpServletResponse http, ResponseStatus status, String msg){
		response = new JsonObject();
		response.addProperty(status.name(), msg);
		servlet = http;
		servlet.setContentType("application/json");
		try {
			servlet.getWriter().write(response.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ResponseJSON(HttpServletResponse http){
		servlet = http;
		servlet.setContentType("application/json");
		
	}
	public void writeResponse(JsonObject res){
		try {
			servlet.getWriter().write(response.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public JsonObject getResponse(){
		return response;
	}
	

	public static void main(String[] args) {
		
	}

}
