package com.eventshop.eventshoplinux.DAO.datasource;


import static com.eventshop.eventshoplinux.constant.Constant.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eventshop.eventshoplinux.DAO.BaseDAO;
import com.eventshop.eventshoplinux.domain.common.ConversionMatrix;
import com.eventshop.eventshoplinux.domain.common.FrameParameters;
import com.eventshop.eventshoplinux.domain.common.Result;
import com.eventshop.eventshoplinux.domain.datasource.DataSource;
import com.eventshop.eventshoplinux.domain.datasource.DataSource.DataFormat;
import com.eventshop.eventshoplinux.domain.datasource.DataSourceListElement;
import com.eventshop.eventshoplinux.domain.datasource.Wrapper;
import com.eventshop.eventshoplinux.domain.datasource.emage.STMerger;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.EmageIterator;
import com.eventshop.eventshoplinux.domain.login.User;
import com.eventshop.eventshoplinux.util.commonUtil.CommonUtil;
import com.eventshop.eventshoplinux.util.commonUtil.Config;
import com.eventshop.eventshoplinux.util.commonUtil.ResultConfig;
import com.eventshop.eventshoplinux.util.datasourceUtil.DataProcess;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.FlickrWrapper;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.SimDataWrapper;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.TwitterWrapper;
import com.sun.jersey.core.util.Base64;

public class DataSourceManagementDAO extends BaseDAO {
	
	private Log log=LogFactory.getLog(DataSourceManagementDAO.class.getName());

	//At the time of creating/adding a datasource, wrapper_type will always be PULL
	/*public final static String WRAPPER_TYPE="PULL";
	public final static String STATUS_AVAILABLE="Available";
	public final static String STATUS_NOT_AVAILABLE="Connecting";
	public final static String INITIAL_RESOLUTION="inital";
	public final static String FINAL_RESOLUTION="final";*/
	

	public Result saveDataSource(DataSource ds) {
		
		Result result = new Result();
		int inserted = 0;
		String dsmasterSql = ((ds.getSrcID() == null || ds.getSrcID() == "" || ds.getSrcID().equals("0"))?INSERT_DSMSTR_QRY:UPDATE_DSMSTR_QRY);
		//System.out.println("save datasource "+dsmasterSql);
		int updtFlg = ((ds.getSrcID() == null || ds.getSrcID() == "" || ds.getSrcID().equals("0"))?0:1);
		String dsResStr = (updtFlg == 0?"dsAdd":"dsErr");
		try {
			PreparedStatement ps=con.prepareStatement(dsmasterSql);
			ps.setString(1, ds.getSrcName());
			ps.setString(2, ds.getSrcTheme());
			ps.setString(3, ds.getUrl());
			ps.setString(4, ds.getSrcFormat().toString());
			if (updtFlg == 0) {
				ps.setInt(5,Integer.parseInt(ds.getUserId()));
			} else {
				ps.setLong(5, Long.parseLong(ds.getSrcID()));
			}
			
			inserted=ps.executeUpdate(); // run the query			
			if(inserted!=0 && updtFlg == 0){
				ps=con.prepareStatement(SELECT_DSMSTR_ID);				
				ps.setString(1, ds.getSrcTheme());
				ps.setString(2, ds.getSrcName());
				ps.setString(3, ds.getUserId());
				ResultSet rs = ps.executeQuery();	 
				rs.next();
				ds.setSrcID(rs.getString(1));
			}	
				
			if(ds.getInitParam()!=null) {	
				//System.out.println("frame parameters saving hhh");
				if (checkFrameParam(Integer.parseInt(ds.getSrcID())))  {
					updateFrameParameter(ds.getInitParam(),  Integer.parseInt(ds.getSrcID()), INITIAL_RESOLUTION);
				} else {
					saveFrameParameter(ds.getInitParam(), Integer.parseInt(ds.getSrcID()), INITIAL_RESOLUTION);
				}
			}
			if(ds.getWrapper() != null)	{
				saveWrapper(ds);
			}
			
			result.setResultCode(ResultConfig.getProperty(dsAddSuccCode));
			result.setStatus(ResultConfig.getProperty(SUCCESS));
			result.setComment(ResultConfig.getProperty(dsAddSuccComment));
			
		} catch (SQLException sqle) {			
			sqle.printStackTrace();
			result.setResultCode(ResultConfig.getProperty(dsAddErrCode));
			result.setStatus(ResultConfig.getProperty(FAILURE));
			result.setComment(new StringBuffer().append(ResultConfig.getProperty(dsAddErrComment)).append(sqle.toString()).toString());
		}
		
				
		return result;
	}
	

	private void saveWrapper(DataSource ds){

		ResultSet rs=null;
		String query="";
		String translationMatrix=null;
		String colorMatrix=null;
		//String bag_of_words_str= CommonUtil.listToCSV(ds.getBagOfWords());
		
		if(ds.getWrapper().getWrprId() != null && !EMPTY_STRING.equals(ds.getWrapper().getWrprId()) && !ds.getWrapper().getWrprId().equals("0")) 
			query = UPDATE_WRPR_QRY;
		else
			query = INSRT_WRPR_QRY;
		
		try{
						
			//PreparedStatement psWrpr=con.prepareStatement("insert into Wrapper(wrapper_name,wrapper_type,wrapper_key_value,bag_of_words,visual_tran_mat,visual_color_mat,visual_mask_mat,visual_ignore_since) values(?,?,?,?,?,?,?,?)");
			PreparedStatement psWrpr=con.prepareStatement(query);
			psWrpr.setString(1, ds.wrapper.getWrprName()); // wrapper_name
			psWrpr.setString(2, (ds.wrapper.getWrprType() == null ||ds.wrapper.getWrprType().equals("")?WRAPPER_TYPE:ds.wrapper.getWrprType()));
			
			psWrpr.setString(3, ds.wrapper.getWrprKeyValue()); //key_value
			psWrpr.setString(4, ds.getWrapper().getWrprBagOfWords()); //bag
			//logged in user for fifth parameter
			if(ds.wrapper!=null)
			{
				//String tranMatPath=ds.getVisualParam().tranMatPath; //visual_tran 
				if(ds.wrapper.getWrprVisualTransMat()!=null)		{
					 byte[] bytes = Base64.decode(ds.wrapper.getWrprVisualTransMat());
					 //FileInputStream fis = new FileInputStream(ds.getVisualParam().tranMatPath);
					 psWrpr.setBytes(5,bytes); 
					// translationMatrix=CommonUtil.parseFiletoString(ds.getVisualParam().tranMatPath);
				}
				else
				{
					psWrpr.setNull(5, java.sql.Types.BLOB);
				}
				
				//String colorMatPath=ds.getVisualParam().colorMatPath; //colormat we are not storing the path anywhere
				if(ds.wrapper.getWrprVisualColorMat() !=null)  		 {
				 	byte[] bytes = Base64.decode(ds.wrapper.getWrprVisualTransMat());
				 	psWrpr.setBytes(6,bytes); 
	    			 //FileInputStream fis=new FileInputStream(maskPath);
	    			 //psWrpr.setBinaryStream(7, fis, fis.available()); 
	    	
	    	}  else	 {
	    			psWrpr.setNull(6, java.sql.Types.BLOB);
	    	 }
				
				//String maskPath=ds.getVisualParam().maskPath;
				if(ds.wrapper.getWrprVisualMaskMat() != null) 		{
					 byte[] bytes = Base64.decode(ds.wrapper.getWrprVisualColorMat());
					 psWrpr.setBytes(7,bytes); 
					//FileInputStream fis=new FileInputStream(colorMatPath);
					//psWrpr.setBinaryStream(6, fis, fis.available()); 
					// translationMatrix=CommonUtil.parseFiletoString(ds.getVisualParam().tranMatPath);
				}		else	{
					psWrpr.setNull(7, java.sql.Types.BLOB);
				}				
				
				psWrpr.setInt(8, Integer.parseInt(ds.getWrapper().getWrprVisualIgnore())); //visual_ignore
				psWrpr.setString(9, ds.wrapper.getWrprCSVFileURL());
				
			}	else {
				
				psWrpr.setString(5, null);
				psWrpr.setString(6, null);
				psWrpr.setString(7, null);
				psWrpr.setInt(8, 0);
				psWrpr.setString(9,null);
			}

			//System.out.println("idddd"+ds.getSrcID());
			if (ds.getWrapper().getWrprId() == null || ds.getWrapper().getWrprId().equals("")) {	
				//System.out.println("we are null wrapped");
				psWrpr.setInt(10,Integer.parseInt(ds.getSrcID()));
			} else {
				psWrpr.setInt(10,Integer.parseInt(ds.getWrapper().getWrprId()));
			}
			
			psWrpr.executeUpdate();	

		}catch(Exception e){
			e.printStackTrace();
		}
		 

	}

	private int saveFrameParameter(FrameParameters param, int dsmaster_id,  String resolution_type)
	{
		
		PreparedStatement ps = null;
		int inserted=0;		
		String queryDatasourceResolution = INSERT_DSRES_QRY;
				
	  try{
		  
	    	 ps=con.prepareStatement(queryDatasourceResolution);
	    	 //take dsmaster_id inserted above and insert at first place
	    	 ps.setInt(1, dsmaster_id);	    	 
	    	 //how do we get the datastream name?
	    	 ps.setString(2, null);
	    	 ps.setLong(3, param.getTimeWindow());
	    	 ps.setDouble(4, param.getLatUnit());
	    	 ps.setDouble(5, param.getLongUnit());
	    	 ps.setString(6, param.boundingBoxString());
	    	 ps.setString(7, resolution_type);	    	 
	    	 inserted=ps.executeUpdate();
	    
	    	
	    }catch(Exception e){
	    	log.error("not working "+e);
	    	e.printStackTrace();
	    	
	    }
	 return inserted;
	}
	
	private int updateFrameParameter(FrameParameters param, int dsmaster_id, String resolution_type)
	{
		PreparedStatement ps = null;
		int updated=0;
		
		String queryDatasourceResolution = UPDATE_DSRES_QRY;
		//System.out.println(" parameter details for update param.getTimeWindow()"+param.getTimeWindow()+ " param.getLatUnit() "+param.getLatUnit()+" param.getLongUnit()"+param.getLongUnit()+" param.boundingBoxString()"+param.boundingBoxString());
		try
		{
		 ps=con.prepareStatement(queryDatasourceResolution);
		 
		 ps.setLong(1, param.getTimeWindow());
    	 ps.setDouble(2, param.getLatUnit());
    	 ps.setDouble(3, param.getLongUnit());
    	 ps.setString(4, param.boundingBoxString());
    	 ps.setInt(5, dsmaster_id);
    	 ps.setString(6, resolution_type);
    	 //logged in user for fifth parameter
    	 
    	 updated=ps.executeUpdate();
       	 //log.info("Record updated ");
    
		}
		catch(SQLException e)
		{
			e.printStackTrace();		
			
			log.error(e.getMessage());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return updated;
	}
	
/**
 * @param dsmaster_id
 * @return
 */
public DataSource getDataSource(int dsmaster_id){
	//log.info("getDataSource() method entered");
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    DataSource source=new DataSource();
	    String tempDir = Config.getProperty(TEMPDIR);
	    try{
	    	ps=con.prepareStatement(SELECT_DSMSTR_ALL_QRY);
	    	ps.setInt(1, dsmaster_id);
	    	rs=ps.executeQuery();
	    	
	    	//initial and final frame params are saved as part of Datasource_Resolution table.
	    	//so, we will get 2 resultset records from above query , one for initial frameparam and other for final frame param.
	    	
	    	if(rs.next()) //get initial frame param
	    	{
	    			    		  	
		    	source.setSrcID(Integer.toString(dsmaster_id));	
		    	source.setSrcName(rs.getString(dsmaster_title));
		    	source.setUrl(rs.getString(DSMSTR_URL));
		    	source.setSrcTheme(rs.getString(DSMSTR_THEME));	 
		    	String filepath = tempDir + PATH_DS + dsmaster_id+json;
		    	//System.out.println(filepath);
				File file = new File(filepath);
				boolean exists = file.exists();
				
				if(exists){
					source.setStatus(STATUS_AVAILABLE);
					source.setControl(1);
				}else{
					source.setStatus(STATUS_NOT_AVAILABLE);
				    source.setControl(0);
				}
						    	   	
		    	String format=rs.getString(DSMSTR_FRMT);
		    	
		    	if(STREAM.equals(format) )
		    	source.setSrcFormat(DataSource.DataFormat.stream);
		    	else if(VISUAL.equals(format))
		    	source.setSrcFormat(DataSource.DataFormat.visual);
		    	else if(CSV.equals(format))
			    	source.setSrcFormat(DataSource.DataFormat.csv);
		    	
	    	}
//	    	if(rs.next()) //get final frame param is not needed for datasource as per Siripen -- sanjukta 
//	    	{
//	    		populateFrameParam(source.finalParam, rs);
//	    	}
	    			    	
	    }catch(Exception e){
	    	log.info("DatasourceManagementDao.getDatasource has issues "+e);
	   
	    }finally{
	
	    }
	    
	    if (source != null) {	    	
	    	source = populateWrapper(source, dsmaster_id);
	       	populateFrameParam(source.initParam, dsmaster_id);
	    }
	    
	    
	    log.info("getDataSource() method end");
		return source;
	} 


	public DataSource populateWrapper(DataSource source,int dsmaster_id) {
		
		List<String> listofWord=new ArrayList<String>();		
		Wrapper wrapper = new Wrapper();
		try {
			PreparedStatement preparedStatementWrpr = con.prepareStatement(SELECT_WRPR_QRY);
			//System.out.println("SELECT_WRPR_QRY "+SELECT_WRPR_QRY);
		    preparedStatementWrpr.setInt(1,dsmaster_id);
		    ResultSet rsWrpr=preparedStatementWrpr.executeQuery();
		    //SELECT wr.wrapper_id,wr.wrapper_name,wr.wrapper_type,wr.wrapper_key_value,wr.bag_of_words,
		    //wr.visual_tran_mat,wr.visual_color_mat,wr.visual_mask_mat,wr.visual_ignore_since 
			while (rsWrpr.next())
			{
				wrapper.setWrprId(rsWrpr.getString(1));
				wrapper.setWrprName(rsWrpr.getString(2));
				wrapper.setWrprType(rsWrpr.getString(3)); 
				wrapper.setWrprKeyValue(rsWrpr.getString(4));
				
				source.setSupportedWrapper(wrapper.getWrprName());				
				String bag_of_words_str= rsWrpr.getString(BAG_OF_WORDS);
				listofWord.add(bag_of_words_str);		      	
		      	
		      	String [] bag_of_words_array= bag_of_words_str.split(COMMA);
		      	ArrayList<String> bag_of_words_list=new ArrayList<String>(Arrays.asList(bag_of_words_array));
		      	
		       	source.setBagOfWords(bag_of_words_list);		       	
		       	
		       			
				wrapper.setWrprBagOfWords(bag_of_words_str);
				wrapper.setWrprVisualTransMat(rsWrpr.getString(6));
				wrapper.setWrprVisualColorMat(rsWrpr.getString(7));
				wrapper.setWrprVisualMaskMat(rsWrpr.getString(8));
				wrapper.setWrprVisualIgnore(rsWrpr.getString(9));
				
					if(VISUAL.equals(source.getSrcFormat()))
			    	{
				    	InputStream is=rsWrpr.getBinaryStream(VISUAL_TRANS_MAT);
				    	
				    	BufferedReader reader=new BufferedReader(new InputStreamReader(is));
				    	
				    	ConversionMatrix tranMat=CommonUtil.parsefileToMatrix(reader);
				    	source.visualParam.setTranslationMatrix(tranMat);
				    	
				    	is=rsWrpr.getBinaryStream(VISUAL_COLR_MAT);
				    	
				    	reader=new BufferedReader(new InputStreamReader(is));
				    	
				    	ConversionMatrix colorMat=CommonUtil.parsefileToMatrix(reader);
				    	source.visualParam.setColorMatrix(colorMat);
			    	
				    	/*source.visualParam.setTranMatPath(rs.getString("visual_tran_mat"))	;  
				    	source.visualParam.setColorMatPath(rs.getString("visual_color_mat")); */
				    //	source.visualParam.setMaskPath(rs.getString("visual_mask_mat"));
				    	source.visualParam.setIgnoreSinceNumber(rsWrpr.getInt(VISUAL_IGNOR_SINCE));
			    	}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		source.setWrapper(wrapper);		
		return source;
	}
	public void populateFrameParam(FrameParameters param, int dsmaster_id)
	{
		try {
			PreparedStatement psFP = con.prepareStatement(SELECT_DSRES_QRY);
		    psFP.setInt(1,dsmaster_id);
		    ResultSet rsFP=psFP.executeQuery();
		    		    
			while (rsFP.next())
			{
				param.setTimeWindow(rsFP.getInt(TIME_WINDOW)) ;   			    		
				param.setLatUnit(rsFP.getDouble(LATITUDE_UNIT)); 	
				param.setLongUnit(rsFP.getDouble(LONGITUDE_UNIT));	
		    	String boundingbox=rsFP.getString(BOUNDINGBOX);
		    	String [] boundingboxVal = (boundingbox == null || boundingbox.equals("")?null:boundingbox.split(COMMA));
		    	
		    	double swLat=Double.parseDouble(boundingboxVal[0]);
		    	double swLong=Double.parseDouble(boundingboxVal[1]);
		    	double neLat=Double.parseDouble(boundingboxVal[2]);
		    	double neLong=Double.parseDouble(boundingboxVal[3]);
		    	
		    	param.setSwLat(swLat);
		    	param.setSwLong(swLong);
		    	param.setNeLat(neLat);
		    	param.setNeLong(neLong);
			}
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	


public List<DataSourceListElement> getDataSourceList(User user){
	
 List<DataSourceListElement> listDataSource=new ArrayList<DataSourceListElement>();
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    String tempDir=Config.getProperty(TEMPDIR);
	    String adminDSListSql = SELECT_DSMSR_QRY;
	    String userDSListSql = SLT_DSMSR_QRY_BASEON_ID_QRY;
		//"SELECT query_id,query_name,query_status FROM Query_Master WHERE dsmaster_creator=? OR dsmaster_creator=0";
		String qrySql = user.getRoleId()==1?adminDSListSql:adminDSListSql + userDSListSql;
	   
	    try{
	    	ps=con.prepareStatement(qrySql);
	    	if (user.getRoleId()!= 1) {    // Normal User DSlist
				ps.setInt(1, user.getId());
			}
	    	rs=ps.executeQuery();
	    	
	    	while(rs.next()){
	    		DataSourceListElement source=new DataSourceListElement();	
	    		int srcId=rs.getInt(dsmaster_id);
	    		String srcTitle=rs.getString(dsmaster_title);
	    		source.setCreater(rs.getInt(3)); // added on 16/07/2014
	    		String filepath = tempDir + PATH_DS + rs.getInt(1) + UNDERSCORE
						+ rs.getString(4);
				File file = new File(filepath);
				boolean exists = file.exists();
				
				if(exists){
					source.setStatus(STATUS_AVAILABLE);
					source.setControl(1);
				}else{
					source.setStatus(STATUS_NOT_AVAILABLE);
				    source.setControl(0);
				}
				source.setSrcID(srcId);	
		    	source.setSrcName(srcTitle);	
		    	
		    	listDataSource.add(source);
	    	}
	    	
	    }catch(Exception e){
	    
	    }finally{
	    	
	    }
		
		return listDataSource;
	} 
	
//new stuff to merge with Dec release
public List<DataSource> getDatasourceForSelectedDS(
		String[] selectedDSIds) {
	
	List<DataSource> dsList = new ArrayList<DataSource>();
	DataSource dsElement = null;
	Connection connection = connection();
	PreparedStatement ps = null;
	ResultSet rs = null;
	for(int i=0;i<selectedDSIds.length;i++) {
		
		String dsSql = SELECT_DS_QRY;
		 

		try {
			ps = connection.prepareStatement(dsSql);
			ps.setString(1, selectedDSIds[i]);
			rs = ps.executeQuery();
			//System.out.println("select query "+SELECT_DS_QRY);
			String tempDir = Config.getProperty(TEMPDIR);
			while (rs.next()) {
				dsElement = new DataSource();
				dsElement.setSrcID(rs.getString(1));
				dsElement.setSrcTheme(rs.getString(2));
				dsElement.setSrcName(rs.getString(3)); //desc 
				dsElement.setUrl(rs.getString(4));
				String filepath = tempDir + PATH_DS + rs.getInt(1) + UNDERSCORE
						+ rs.getString(4);
				File file = new File(filepath);
				boolean exists = file.exists();

				if (exists) {
					dsElement.setStatus(STATUS_AVAILABLE);
					dsElement.setControl(1);
				} else {
					dsElement.setStatus(STATUS_NOT_AVAILABLE);
					dsElement.setControl(0);
				}

				//System.out.println("source format for "+rs.getString(5));
				if (rs.getString(5)!= null) {
					dsElement.setSrcFormat(DataFormat.valueOf(rs.getString(5)));
				}
				
				
				dsElement.setType(rs.getString(6));
				dsElement.setArchive(rs.getInt(7));
				dsElement.setUnit(rs.getInt(8));
				//dsElement.setCreatedDate(rs.getString(9));
				dsElement.setTimeWindow(new Long(rs.getLong(10)));// 16-07-2014 -- sanjukta
				dsElement.setEmailOfCreator(rs.getString(11));
				
				dsList.add(dsElement);

			}

		} catch (Exception e) {
			
			log.error("error in query" +e.getMessage());
		}

	}
	log.info("Completed getDataSrcProfileForSelectedDS()");
	return dsList;
}

public List<DataSource> getDataSrcList() {
	
	List<DataSource> dsList = new ArrayList<DataSource>();
	Connection connection = connection();
	PreparedStatement ps = null;
	ResultSet rs = null;
	DataSource dsElement = null;
	String tempDir = Config.getProperty(TEMPDIR);
	String qrySql = SELECT_DSMSTR_DSRES_QRY;
	try {
		ps = connection.prepareStatement(qrySql);
		rs = ps.executeQuery();

		while (rs.next()) {
			dsElement = new DataSource();
			dsElement.setSrcID(rs.getString(1));
			dsElement.setSrcTheme(rs.getString(2));
			dsElement.setSrcName(rs.getString(3)); //desc
			dsElement.setSrcFormat(DataFormat.valueOf(rs.getString(4)));
			//String filepath = tempDir + PATH_DS + dsElement.getSrcID() + UNDERSCORE + rs.getString(5);
			String filepath = tempDir + PATH_DS + dsElement.getSrcID() + json;
			File file = new File(filepath);
			boolean exists = file.exists();

			if (exists) {
				dsElement.setStatus(STATUS_AVAILABLE);
				dsElement.setControl(1);
			} else {
				dsElement.setStatus(STATUS_NOT_AVAILABLE);
				dsElement.setControl(0);
			}
			dsElement.setBoundingbox(rs.getString(6));
			dsElement.setAccess(rs.getString(7));
			dsElement.setTimeWindow(rs.getLong(8));// need to set timeWindow to be in sync with the AdminManagementDao method -- sanjukta 06-08-2014
			
			dsList.add(dsElement);
		}

	} catch (Exception e) {
		
		log.error(e.getMessage());
	}
	log.info("Completed getDataSrcList()");
	return dsList;
}

private boolean checkFrameParam(int dsmaster_id) {
	String query =CHECK_FRAMEPARAM;
	boolean flag = false;
	try {
		Connection connection = connection();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, dsmaster_id);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			flag = (rs.getInt(1) > 0?true:false);
		}
	} catch (Exception e) {
		
		log.error(e.getMessage());
	}
	return flag;
}

}
