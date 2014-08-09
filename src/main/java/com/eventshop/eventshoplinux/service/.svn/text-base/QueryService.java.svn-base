package com.eventshop.eventshoplinux.service;

import static com.eventshop.eventshoplinux.constant.Constant.RUN;
import static com.eventshop.eventshoplinux.constant.Constant.SUCCESS;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import com.eventshop.eventshoplinux.DAO.query.QueryListDAO;
import com.eventshop.eventshoplinux.domain.login.User;
import com.eventshop.eventshoplinux.domain.query.Query;
import com.eventshop.eventshoplinux.domain.query.QueryDTO;
import com.eventshop.eventshoplinux.domain.query.QueryHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;


@Path("/queryservice")
public class QueryService {
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/queriesByUser")
	/*
	 * This method used for displaying queryList accessed by User Home page 
	 */
	public Query[] getQueryList(User user) {
		
		QueryListDAO queryDAO = new QueryListDAO();
		Query[] qryListArray = new Query[] {};
		List<Query> queryList = queryDAO.getUserQuery(user);
		Query[] qryList;
		qryList = queryList.toArray(qryListArray);
		return qryList;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/queries")
	/*
	 * This method used for displaying queryList accessed by User Home page 
	 */
	public Query[] getQueryList(@QueryParam("userId") int userId) {
		
		User user = new User();
		user.setId(userId);
		QueryListDAO queryDAO = new QueryListDAO();
		Query[] qryListArray = new Query[] {};
		List<Query> queryList = queryDAO.getUserQuery(user);		
		Query[] qryList = queryList.toArray(qryListArray);
		return qryList;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/queries")
	
	 /*
	  *  This method will execute the Query/QueryList with Operator
	  */
	public String[] runQuery(
			@JsonProperty("selectedQueryRun") String selectedQueryRun) {
	

		//System.out.println(selectedQueryRun);
			JsonParser parser = new JsonParser();
			JsonObject rootObj = parser.parse(selectedQueryRun).getAsJsonObject();
			JsonElement queryElement = rootObj.get("query");
			Gson gson = new Gson();
			List<QueryDTO> queryDTOList = null;

			//Check if "project" element is an array or an object and parse accordingly...
			if (queryElement.isJsonObject()) {
				    //The returned list has only 1 element
				QueryDTO queryDTO = gson.fromJson(queryElement, QueryDTO.class);
				queryDTOList.add(queryDTO);
				}
				else if (queryElement.isJsonArray()) {
					Type queryListType = new TypeToken<List<QueryDTO>>() {}.getType();
				    queryDTOList = gson.fromJson(queryElement, (Type) queryListType);
				}	
						
			//System.out.println("Exception");
			QueryListDAO queryListDAO = new QueryListDAO(); 
			
			queryDTOList = replaceIntmQuery(queryDTOList);//Replace the qo,q1 with real datasourceIds 

			/* blocking to see if we can just stick to one record
			 *
			 int firstInsrtedQryId = 0;
			for (int i = 0; i < queryDTOList.size(); ++i) {
					queryListDAO = new QueryListDAO();
					
					if (i==0) { //first queryDTO will have the qid_parent=null
						firstInsrtedQryId = queryListDAO.registerQuery(queryDTOList.get(i),queryElement.toString(),firstInsrtedQryId); 
						queryDTOList.get(i).setqID(Integer.toString(firstInsrtedQryId));
					}else {
						int lastInsrtedQryId = queryListDAO.registerQuery(queryDTOList.get(i),queryElement.toString(),firstInsrtedQryId);
						queryDTOList.get(i).setqID(Integer.toString(lastInsrtedQryId));
					}

			}*/			
			try {
			queryListDAO.registerQuery(queryDTOList.get(0),queryElement.toString(),queryDTOList.size()); 
			} catch (Exception e) {
				System.out.println("exc"+e);
			}
			
//			QueryHelper qryHelper = new QueryHelper();
//			String output = qryHelper.queryProcess(queryDTOList,RUN);			
			String[] strArr = new String[1];
			strArr[0] = SUCCESS;
			return strArr;
		}
	
		
	//@POST
	//@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	//@Path("/runsinglequery")
	
	/*
	 * This method execute the query from left nav
	 */
	/*
	public String runQuery(@JsonProperty("selectedQryId") HashMap selectedQryId) {
		List<String> qryIdList = (List<String>) selectedQryId.get("selectedQryId");
		int qryId = Integer.parseInt(qryIdList.get(0));   //First element is qryId
		String runOption = qryIdList.get(1);              //Second element is run option
		QueryListDAO qryDAO = new QueryListDAO();
		String qryEsql = qryDAO.getQryEsql(qryId);	
		Gson gson = new Gson();
		TypeToken<List<QueryDTO>> token = new TypeToken<List<QueryDTO>>() {};
		List<QueryDTO> queryDTOList = gson.fromJson(qryEsql,
				token.getType());
		QueryHelper qryHelper = new QueryHelper();
		String outPut = qryHelper.queryProcess(queryDTOList, runOption);
		return outPut;
	}
	*/
	/*
	 * This method will replace the qo,q1 to real datasource ids
	 */
	
	public List<QueryDTO> replaceIntmQuery(List<QueryDTO> queryDTOListInit){
		for (int i = 0; i < queryDTOListInit.size(); ++i) {
			//checking for q0,q1..and replacing with datasources
			String [] datasrc = queryDTOListInit.get(i).getDataSources();
			String [] dataSourcesFinal = null;
			int intmQryIsExist = 0;
			if (queryDTOListInit.get(i).getDataSources() != null && queryDTOListInit.get(i).getDataSources().length > 0) {
				for (int j = 0; j < datasrc.length; j++) {
					if (datasrc[j].indexOf("q") != -1) {//checking for q0,q1
						String qid= datasrc[j].substring(1);//finding for q0,q1..
						for (int k = 0; k < queryDTOListInit.size(); ++k) {//comparing to get the queryDTO for q0,q1..
							if (queryDTOListInit.get(k).getqID().equals(qid)) {
								if (queryDTOListInit.get(k).getDataSources() != null && queryDTOListInit.get(i).getDataSources().length > 0) {
									//dataSourcesFinal.queryDTOList.get(k).getDataSources();
									dataSourcesFinal = (String[]) ArrayUtils.addAll(dataSourcesFinal, queryDTOListInit.get(k).getDataSources());
									intmQryIsExist = 1;
								}
							}
						}
					}
				}
				
			}
			if (intmQryIsExist == 1) {
				queryDTOListInit.get(i).setDataSources(dataSourcesFinal); //replacing q0,q1 with datasources
			}
			
	 }
		return queryDTOListInit;
	}
	
}
