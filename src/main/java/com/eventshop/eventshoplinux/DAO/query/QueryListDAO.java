package com.eventshop.eventshoplinux.DAO.query;



import static com.eventshop.eventshoplinux.constant.Constant.*;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eventshop.eventshoplinux.DAO.BaseDAO;
import com.eventshop.eventshoplinux.domain.common.FrameParameters;
import com.eventshop.eventshoplinux.domain.login.User;
import com.eventshop.eventshoplinux.domain.query.Query;
import com.eventshop.eventshoplinux.domain.query.QueryDTO;
import com.eventshop.eventshoplinux.servlets.QueryJSONParser;
import com.eventshop.eventshoplinux.servlets.QueryProcess;
import com.eventshop.eventshoplinux.util.commonUtil.Config;

/*
 * This class is related for all QueryList DB operation
 */
public class QueryListDAO extends BaseDAO {
	private Log log = LogFactory.getLog(QueryListDAO.class.getName());
	/*
	 * This method returns the queryList based on user logged in
	 */
	public List<Query> getUserQuery(User user) {
		log.info("Inside getUserQuery()");
		List<Query> qryList = new ArrayList<Query>();
		PreparedStatement ps = null;
		String statusControl = CONTROLFLAG; // Running
		ResultSet rs = null;
		Query query =  null;
		Connection connection = connection();
		String adminQryListSql = SELECT_QRMSTR_QRY; //Admin Qrylist
		String conditionQryListSql = SLT_QRMSTR_BASEON_ID_QRY;
		//"SELECT query_id,query_name,query_status FROM Query_Master WHERE query_creator_id=? OR query_creator_id=0";
		String qrySql = user.getRoleId()==1?adminQryListSql:adminQryListSql + conditionQryListSql;
		
		try {
			ps = connection
					.prepareStatement(qrySql);
			if (user.getRoleId()!= 1) {    // Normal User Qrylist
				ps.setInt(1, user.getId());
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				query = new Query();
				query.setqID(rs.getInt(1)); 
				query.setQueryName(rs.getString(2));
				//query.setStatus(rs.getString(3));				
								
				if (getQueryStatus(rs.getString(1))) {
					query.setControl(1);
					query.setStatus(RUNNING);
				} else {
					query.setControl(0);
					query.setStatus(STOPPED);
				}
				qryList.add(query);
				
			}

		} catch (Exception e) {
		} 
		log.info("Completed getUserQuery()");
		return qryList;
	}
	
	/*
	 * This method will check  Query Object existance
	 */
	
	public boolean chkQryID(int qID){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = connection();
		boolean flag = false;
		try {
			ps = connection
					.prepareStatement(SLT_QRYMSTR_QRY);
			ps.setInt(1, qID);
			rs = ps.executeQuery();
			if (rs.next()!= false) {
					flag = true;
				}

		} catch (Exception e) {
			log.error(e.getMessage());
		} 
		return flag;
	}
	
	
	/*
	 * This method insert the Query Object to Query_Master table for queryRun
	 */
	public int registerQuery(QueryDTO qryDTO,String selectedQueryRun,int noSubQueries){
		PreparedStatement ps = null;
		Connection connection = connection();
		int lastQryId = 0;
		System.out.println("register query0");
		try {
			
			ps = connection.prepareStatement(INST_QRYMSTR_DEFAULT_QRY);
			ps.setString(1, selectedQueryRun.trim());
			ps.setInt(2, (qryDTO.getTimeWindow() == null || qryDTO.getTimeWindow().equals("") ?0:Integer.parseInt(qryDTO.getTimeWindow())));//55
			ps.setDouble(3, qryDTO.getLatitudeUnit());//3.3
			ps.setDouble(4, qryDTO.getLongitudeUnit());//2.3
			ps.setString(5, qryDTO.getBoundingBox());
			//ps.setString(6, qryDTO.getQueryStatus());
//			if (getQueryStatus(qryDTO.getqID())) {
//				ps.setString(6,RUNNING);
//			} else {
				ps.setString(6,STOPPED);
//			}								
			
			ps.setInt(7, qryDTO.getQryCreatorId());				
			ps.setString(8,qryDTO.getQueryName()); // no entry in UI yet -- sanjukta		
			System.out.println("reghister query chilldd");
			
			/*
			//child
			if (firstInsrtedQryId != 0) {
				ps = connection.prepareStatement(INST_QRYMSTR_QRY);
				
				ps.setString(1, selectedQueryRun.trim()); // esql
				ps.setInt(2, (qryDTO.getTimeWindow() == null || qryDTO.getTimeWindow().equals("") ?0:Integer.parseInt(qryDTO.getTimeWindow()))); // hardcoded--Integer.parseInt(qryDTO.getTimeWindow())
				ps.setDouble(3, qryDTO.getLatitudeUnit()); // hardcoded --qryDTO.getLatitudeUnit()
				ps.setDouble(4, qryDTO.getLongitudeUnit()); // hardcoded -qryDTO.getLongitudeUnit()
				ps.setString(5, qryDTO.getBoundingBox()); // hardcoded--not there in grouping
				//ps.setString(6, qryDTO.getQueryStatus()); // hardcoded--qryDTO.getQueryStatus()
				if (getQueryStatus(qryDTO.getqID())) {
					ps.setString(6,RUNNING);
				} else {
					ps.setString(6,STOPPED);
				}								
				
				ps.setInt(7, firstInsrtedQryId);
				ps.setInt(8, qryDTO.getQryCreatorId());
				ps.setString(9,qryDTO.getQueryName()); // no entry in UI yet -- sanjukta
				System.out.println("reghister query parenttt");
			}else {
				
				ps = connection.prepareStatement(INST_QRYMSTR_DEFAULT_QRY);
				ps.setString(1, selectedQueryRun.trim());
				ps.setInt(2, (qryDTO.getTimeWindow() == null || qryDTO.getTimeWindow().equals("") ?0:Integer.parseInt(qryDTO.getTimeWindow())));//55
				ps.setDouble(3, qryDTO.getLatitudeUnit());//3.3
				ps.setDouble(4, qryDTO.getLongitudeUnit());//2.3
				ps.setString(5, qryDTO.getBoundingBox());
				//ps.setString(6, qryDTO.getQueryStatus());
//				if (getQueryStatus(qryDTO.getqID())) {
//					ps.setString(6,RUNNING);
//				} else {
					ps.setString(6,STOPPED);
//				}								
				
				ps.setInt(7, qryDTO.getQryCreatorId());				
				ps.setString(8,qryDTO.getQueryName()); // no entry in UI yet -- sanjukta		
				System.out.println("reghister query chilldd");
			}
			*/
			
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()){
			    lastQryId = rs.getInt(1);
			}

		} catch (Exception e) {
			//log.error(e.getMessage());
			System.out.println("exceptionnn e11"+e);
		} 
		System.out.println("reghister query1 with id "+lastQryId);
		return lastQryId;
	}
	
	
	
	/*
	 * This method return the QryEsql
	 */
	
	public String getQryEsql(int qID){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = connection();
		String qryEsql = "";
		try {
			ps = connection
					.prepareStatement(SLT_QRYMSTR_QRY);
			ps.setInt(1, qID);
			rs = ps.executeQuery();
			while (rs.next()) {
				qryEsql = rs.getString(1);
				
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		} 
		return qryEsql;
	}
	
	/*
	 * This method frame the FrameParametr for query
	 */
	
	public FrameParameters getFrameParameterQry(int qryId){
		//FrameParameters fp = new FrameParameters(timeWindow, syncAtMilliSec, 
		//latUnit,longUnit, swLat,swLong , neLat, neLong);		
		log.info("Inside getFrameParameterQry()");
		Connection connection = connection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		FrameParameters frmParmObj = new FrameParameters(); 
		String [] boundingBox = null;
		try {
			ps = connection.prepareStatement(SLT_QRYMSTR_RUN_QRY);
			ps.setInt(1, qryId);
			rs = ps.executeQuery();
			while (rs.next()) {
				frmParmObj.setTimeWindow(rs.getLong(1));
				frmParmObj.setLatUnit(rs.getDouble(2));
				frmParmObj.setLongUnit(rs.getDouble(3));
				boundingBox = rs.getString(4).split(COMMA);
				System.out.println(" this is here");
				frmParmObj.setSwLat(Double.parseDouble(boundingBox[0]));
				frmParmObj.setSwLong(Double.parseDouble(boundingBox[1]));
				frmParmObj.setNeLat(Double.parseDouble(boundingBox[2]));
				frmParmObj.setNeLong(Double.parseDouble(boundingBox[3]));
				
			}
			System.out.println("we done without excp");

		} catch (Exception e) {
			System.out.println("exvrption  ids h"+e);
			log.error(e.getMessage());
		}
		log.info("Completed getFrameParameterQry()"+frmParmObj.getTimeWindow());
		return frmParmObj;
	}
	
	//used by registerServlet
	public List<QueryDTO> getQueryList() {
		// include all the setters
		QueryDTO qryElements = null;
		List<QueryDTO> qryList = new ArrayList<QueryDTO>();
		Connection connection = connection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statusControl = CONTROLFLAG; // Running
		String sqlQry = SELECT_QRYMSTR_ADMIN_QRY;
		try {
			ps = connection.prepareStatement(sqlQry);
			rs = ps.executeQuery();

			while (rs.next()) {
				qryElements = new QueryDTO();
				qryElements.setqID(rs.getString(1));	
				//user id not required
				qryElements.setQueryName(rs.getString(3));
				qryElements.setQueryEsql(rs.getString(5));
				qryElements.setTimeWindow(rs.getString(6));
				try {
					qryElements.setLatitudeUnit(Double.parseDouble(rs.getString(7)));
					qryElements.setLongitudeUnit(Double.parseDouble(rs.getString(8)));
				} catch (Exception e) {
					log.error("Latitude and longitude conversion issue"+e);
				}
				qryElements.setBoundingBox(rs.getString(9));
				if (getQueryStatus(qryElements.getqID())) {
					qryElements.setStatus(RUNNING);
					qryElements.setControl(1);
				} else {
					qryElements.setStatus(STOPPED);
					qryElements.setControl(0);
				}				
				qryList.add(qryElements);
			}

		} catch (Exception e) {
			
			log.error(e.getMessage());
		}
		log.info("Completed getQueryList()");
		return qryList;
	}
	
		//added by sanjukta
	private boolean getQueryStatus (String qid) {
		//String fileExtn = (Config.getProperty("env").equals(WINOS)?WINEXECFILEEXTN:LINUXEXECFILEEXTN);
		String fileExtn = LINUXEXECFILEEXTN;
		String filePath = new StringBuffer().append(Config.getProperty("context")).append(Config.getProperty("queryDir")).append(qid).append(".").append(fileExtn).toString();
		File file = new File(filePath);
						
		return (file.exists());
		
	}


//added by Siripen
	public List<String> getQueryTree(int qid_parent){
		String SQL_QUERY_TREE = "select query_esql from Query_Master where query_id = ? or qid_parent = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = connection();
		List<String> qryEsql = new ArrayList<String>();
		try {
			ps = connection.prepareStatement(SQL_QUERY_TREE);
			ps.setInt(1, qid_parent);
			ps.setInt(2, qid_parent);
			rs = ps.executeQuery();
			while (rs.next()) {
				qryEsql.add(rs.getString(1));
			}
			System.out.println("no exceppp query tree");
		} catch (Exception e) {
			System.out.println("excp query tree "+e);
			log.error(e.getMessage());
		} 
		System.out.println("querESQL "+qryEsql);
		return qryEsql;
	}


	public QueryProcess parseQueryTree(int qid_parent) {
		QueryProcess aQuery = new QueryProcess(Config.getProperty("context"));
		QueryJSONParser parser = new QueryJSONParser();
		List<String> queryTree = this.getQueryTree(qid_parent);
		//aQuery = parser.parseQuery(queryTree);
		return aQuery;
	}
	
} 
