package com.eventshop.eventshoplinux.servlets;

import static com.eventshop.eventshoplinux.constant.Constant.SLT_QRYMSTR_QRY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eventshop.eventshoplinux.DAO.admin.AdminManagementDAO;
import com.eventshop.eventshoplinux.DAO.datasource.DataSourceManagementDAO;
import com.eventshop.eventshoplinux.DAO.query.QueryListDAO;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.CSVEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.EmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.STTEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.SensorMongoDBEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.VisualEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emage.ResolutionMapper.SpatialMapper;
import com.eventshop.eventshoplinux.domain.datasource.emage.ResolutionMapper.TemporalMapper;
import com.eventshop.eventshoplinux.domain.datasource.emage.STMerger;


import com.eventshop.eventshoplinux.domain.datasource.DataSource;
import com.eventshop.eventshoplinux.domain.datasource.DataSourceListElement;
import com.eventshop.eventshoplinux.domain.datasource.DataSource.DataFormat;
//import com.eventshop.eventshoplinux.servlets.DataSource.DataFormat;
import com.eventshop.eventshoplinux.domain.datasource.simulator.DistParameters;
import com.eventshop.eventshoplinux.domain.datasource.simulator.GaussianParameters2D;
import com.eventshop.eventshoplinux.domain.datasource.simulator.Simulator.Kernel;
//import com.eventshop.eventshoplinux.domain.query.QueryRunnable;
import com.eventshop.eventshoplinux.servlets.QueryProcess;
import com.eventshop.eventshoplinux.servlets.ResponseJSON;
//import com.eventshop.eventshoplinux.servlets.Query;
import com.eventshop.eventshoplinux.domain.query.QueryDTO;
import com.eventshop.eventshoplinux.util.datasourceUtil.DataProcess;
import com.eventshop.eventshoplinux.util.datasourceUtil.DataSourceParser;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.FlickrWrapper;
import com.eventshop.eventshoplinux.domain.common.FrameParameters;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.SimDataWrapper;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.TwitterWrapper;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.Wrapper;
import com.eventshop.eventshoplinux.util.commonUtil.Config;
import com.eventshop.eventshoplinux.util.commonUtil.SystemUtilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jersey.core.util.Base64;
import static com.eventshop.eventshoplinux.constant.Constant.*;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;

public class RegisterServlet extends HttpServlet
{
	 protected Log log=LogFactory.getLog(this.getClass().getName());
	   

	/**
	 * Automatically generated serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	String context = "";
	String tempDir = "";
	int preRegisteredSrcCount = 0;
	int preRegisteredQueryCount = 0;

	// All the registered data sources in EventShop
	HashMap<String, DataSource> sources;
	// All the data processes
	HashMap<String, DataProcess> dataProcesses;
	// All the data processes
	HashMap<String, QueryProcess> queryProcesses;
		
	// All the registered queries
	//HashMap<String, Query>  queries;
	// A QueryJSONParser object
	QueryJSONParser parser;
	//Query query;
	
	
	public void init()
	{
		//System.out.println(" this init is called");
		context = Config.getProperty("context");
		tempDir = Config.getProperty("tempDir");
		preRegisterDataSourcesQueries();		
	}
		
	
	public void init(final ServletConfig config)
	{
		//System.out.println(" this final init is called");
		//context = config.getServletContext().getRealPath("/");
		context = Config.getProperty("context");
		log.info(context);
		tempDir = Config.getProperty("tempDir");
		preRegisterDataSourcesQueries();	
	}
	
	
	public RegisterServlet()
	{		
		//sources = new ArrayList<DataSource>();
		sources = new HashMap<String, DataSource>();
		dataProcesses = new HashMap<String, DataProcess>();		
		queryProcesses = new HashMap<String, QueryProcess>();
		preRegisterDataSourcesQueries();
		//no json parser required so removed the line
		//QueryJSONParser parser = new QueryJSONParser(this);
		this.parser = new QueryJSONParser(this); //initialized the parser -- sanjukta 06-08-2014
	}
	
	
		
	// This method is most likely for testing and debugging purpose
	public void doGet(HttpServletRequest request,  HttpServletResponse response)
	{
		if(request.getParameter("type") != null && request.getParameter("type").equalsIgnoreCase("show")){
			if(request.getParameter("q").equalsIgnoreCase("runningds")){
				String dsList = "";
				for (Map.Entry<String, DataProcess> entry : dataProcesses.entrySet()) {
					if(entry.getValue().isRunning)
						dsList += entry.getKey() + " ";
						
				}
				new ResponseJSON(response, ResponseJSON.ResponseStatus.INFO, "Running ds: " + dsList);
			} else if(request.getParameter("q").equalsIgnoreCase("dscontrolflag")){
				String output = "";
				for (Map.Entry<String, DataProcess> entry : dataProcesses.entrySet()) {
					output += entry.getKey() + "-" + entry.getValue() + ",";
				}
				new ResponseJSON(response, ResponseJSON.ResponseStatus.INFO, "DS control flag: " + output);
			} else if(request.getParameter("q").equalsIgnoreCase("dsinfo")){
				JsonArray dsArr = new JsonArray();
				for (Map.Entry<String, DataProcess> entry : dataProcesses.entrySet()) {
					JsonObject jo = new JsonObject();
					jo.addProperty(entry.getKey(), entry.getValue().toString());
					dsArr.add(jo);
				}
				new ResponseJSON(response, ResponseJSON.ResponseStatus.INFO, dsArr.toString());
			} else if(request.getParameter("q").equalsIgnoreCase("dataprocess")){
				String dpList = "";
				for (Map.Entry<String, DataProcess> entry : dataProcesses.entrySet()) {
					//if(entry.getValue().isRunning() == 1)
					//	dpList += entry.getKey() + " ";
					dpList += entry.getKey() + "-" + entry.getValue().isRunning + ", ";
				}
				new ResponseJSON(response, ResponseJSON.ResponseStatus.INFO, "DataProcess list: " + dpList);
			}
			else {
				doPost(request, response);
			}
		} else if(request.getParameter("type") != null && request.getParameter("type").equalsIgnoreCase("getds")){
			if(request.getParameter("dsID") != null){
				DataSourceManagementDAO datasourceDAO = new DataSourceManagementDAO();
				String dsID = request.getParameter("dsID");
				DataSource source = datasourceDAO.getDataSource(Integer.parseInt(dsID)); // not sure what this is for -- sanjukta
				if(source != null){
					new ResponseJSON(response, ResponseJSON.ResponseStatus.SUCCESS, source.toString());
				} else {
					new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, dsID + " not found");
				}
			}
		}
	}
	
	public String getQueryTree(int qid_parent){
		
		QueryListDAO queryDAO = new QueryListDAO();
		List<String> queryTree = queryDAO.getQueryTree(qid_parent);
		String queryESQL = "";
		for(int i = 0; i < queryTree.size(); i++){
			queryESQL += queryTree.get(i).toString();
		}
		System.out.println(queryESQL);
		return queryESQL;
		//queryParser.parseQuery(queryTree);
		//new Thread(query).start();
		//queries.add(query);
	}
	//changing from datasource to datap
	public String testStartDataProcess(String dsID){
		DataSourceManagementDAO datasourceDAO = new DataSourceManagementDAO();
		String output = "test start data process: ";
		if(!sources.containsKey(dsID) || sources.get(dsID).url == null){
			sources.put(dsID, datasourceDAO.getDataSource(Integer.parseInt(dsID)));
		}
		DataSource dataSrc =  sources.get(dsID);
		if(!dataProcesses.containsKey(dataSrc.srcID)){		// This data source isn't in the dataProcesses list yet
			startDataProcess(dataSrc);						// start data process and add to the list
			dataSrc.setControl(1);
			sources.put(dataSrc.srcID, dataSrc);					
			output += "DataProcess is started. " + dataSrc.srcID + " is running["+dataSrc.getControl()+"]. ";
		}
		else {								// check whether the data process is running or not
			DataSource ds = sources.get(dataSrc.srcID);
			if(!dataProcesses.get(dataSrc.srcID).isRunning){	// ds isn't running
				startDataProcess(dataSrc);
				ds.setControl(1);			// update control flag
				sources.put(ds.srcID, ds);					
				output += "DataProcess is started. " + ds.srcID + " is running["+ds.getControl()+"]. ";
			}
			else {						// the selected ds is already started and running, so do nothing.
				output += dataSrc.srcID + " is already started and running["+ds.getControl()+"], so do nothing. ";
			}	
		}
		return output;

	}
	
	public String testStopDataProcess(String dsID){
		String output = "Test stop data process: ";
		if(dataProcesses.containsKey(dsID)){
			if(dataProcesses.get(dsID).isRunning){
				dataProcesses.get(dsID).stop();
				dataProcesses.remove(dsID);			// stop the data process, and remove from the list
			}
			sources.get(dsID).setControl(0);
			output += dsID + " is stopped. ";
		} else{
			output += dsID + " isn't running, so do nothing. ";
		}
		return output;
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response){
		response.setContentType("application/json");	
		try {
			log.info("in servlet");
			if(request.getParameter("type") == null){
				log.info("invalid action each request require `type` parameter");
				new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, "invalid parameters, each request required `type` parameter");
				return;
			}
				
			// start data source
			if(request.getParameter("type").equals("d") || request.getParameter("type").equals("startds")){
				// e.g. type=d&dsID=3 or type=startds&dsID=3
				String dsId = request.getParameter("dsID");
				log.info("startds NOW: " + dsId);
				if(dsId == null || dsId.isEmpty()){
					log.info("invalid dsId " + dsId);
					new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, "invalid dsID " + dsId);
				} else{				
					
					DataSourceManagementDAO datasourceDAO = new DataSourceManagementDAO();
					String[] dsIDList = dsId.split(",");
					//List<DataSource> dsList = (List)datasourceDAO.getDatasourceForSelectedDS(dsIDList);
					
					String filepath = "";
					
					List<File> files = new LinkedList<File>();
					Result[] dSourceResult = new Result[dsIDList.length];	
					int dsCount = 0;					
					
					for(int i = 0; i < dsIDList.length; i++){
																
						//log.info("we are in"+dataProcesses.size());
						 filepath =  context + "temp/ds/" + dsIDList[i]+".json";// + "_" + src.srcName;						
						//log.info(dataProcesses.get(dsIDList[i]).getWrapper().getUrl());
						File file =  new File(filepath);	
						
						if ( !file.exists() || (file.exists() && (sources.size() <= 0 || sources.get(dsIDList[i])==null)) || (file.exists() && sources.size() > 0 && sources.get(dsIDList[i])!=null &&  ((new Date().getTime() - file.lastModified()) > sources.get(dsIDList[i]).timeWindow.longValue()))) {
						//use the below if loop to test the UI alone in terms of map generation
						//if (!file.exists()) {
							if (dataProcesses.size() > 0 && dataProcesses.get(dsIDList[i]).isRunning) {
								dataProcesses.get(dsIDList[i]).stop();
								dataProcesses.remove(dsIDList[i]);
							}							
							DataSource dataSource =  datasourceDAO.getDataSource(Integer.parseInt(dsIDList[i]));														
							startDataProcess(dataSource);							
							// start data process and add to the list
                            dataSource.setControl(1);                           
                            sources.put(dataSource.srcID, dataSource);	                            
							Thread.sleep(sources.get(dsIDList[i]).timeWindow.intValue()); // so that the file gets generated before we push back to UI or do you think this wont happen -- sanjukta
						} 
						
							byte[] output = org.apache.commons.io.FileUtils.readFileToByteArray(file);
							Result dsource = new Result();
							dsource.resId = new Integer(dsIDList[i]).toString();
							//query.output = new String(Base64.encode(output));
							dsource.output = new String(output);
							dSourceResult[dsCount] = dsource;
							dsCount++;												
						//}
												
					}
					
					
					// please send back file contents and not string for the map to work, you need the id to differentiate between datasources -- sanjukta
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					ObjectMapper mapper = new ObjectMapper();				
					mapper.writeValue(out, dSourceResult);
					
					//out.print();
					out.flush();
					out.close();
				}
			} 
			// stop data source
			else if(request.getParameter("type").equalsIgnoreCase("stopds")){
				String dsId = request.getParameter("dsID");
				log.info("stopds: " + dsId);
				if(dsId == null || dsId.isEmpty()){
					log.info("invalid dsId " + dsId);					
					new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, "invalid dsID " + dsId);
					
				} else{
					//DataSourceManagementDAO datasourceDAO = new DataSourceManagementDAO();					
					String[] dsIDList = dsId.split(",");
					String output = "";
					for(int i = 0; i < dsIDList.length; i++){	
						if(dataProcesses.containsKey(dsIDList[i])){
							if(dataProcesses.get(dsIDList[i]).isRunning){
								dataProcesses.get(dsIDList[i]).stop();
								dataProcesses.remove(dsIDList[i]);			// stopr the data process, and remove from the list
								sources.get(dsIDList[i]).setControl(0);								
							}							
							
							//dataProcesses.get(dsIDList[i]).stop();
							output += dsIDList[i] + " is stopped. ";
						}
//						} else{
//							output += dsIDList[i] + " isn't running, so do nothing. ";
//						}
					}
					
					new ResponseJSON(response, ResponseJSON.ResponseStatus.SUCCESS, output);
				}
			} 
			else if(request.getParameter("type").equalsIgnoreCase("stopq")){
				String qId = request.getParameter("qIDList");
				log.info("in stopq: " + qId);
				if(qId == null || qId.isEmpty()){
					log.info("invalid qID " + qId);
					new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, "invalid qID " + qId);
				} else{
					String[] qIDArr = ((String) request.getParameter("qIDList")).split(",");
					String output = "";
					for(int i = 0; i < qIDArr.length; i++){
					
						String exePath = context + "proc/Debug/EmageOperators_" + qIDArr[i];
                        log.info(exePath);
                        File exeFile = new File(exePath);
                        boolean exists = exeFile.exists();
                        
                        try {
                            exists = exeFile.delete();
                        } catch (Exception exe) {
                            log.error("delete not working"+exe);
                        }
                        log.info("Did the exeFile get deleted"+exists);

						
						if(queryProcesses.size() > 0  && queryProcesses.containsKey(qIDArr[i])){
							if(queryProcesses.get(qIDArr[i]).isRunning){
								queryProcesses.get(qIDArr[i]).stop();
								queryProcesses.remove(qIDArr[i]);			// stop the query process, and remove from the list
							}
							output += qIDArr[i] + " is stopped. ";
						} else{
							output += qIDArr[i]+ " isn't running, so do nothing. ";
						}
					}
					new ResponseJSON(response, ResponseJSON.ResponseStatus.SUCCESS, output);
					
				}
			}
			// start query
			else if(request.getParameter("type").equalsIgnoreCase("startq")){
				String qId = request.getParameter("qIDList");
				log.info("in startq: " + qId);
				if(qId == null || qId.isEmpty()){
					log.info("invalid qID " + qId);
					new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, "invalid qID " + qId);
					return;
				} 
				
					String[] qIDArr = ((String) request.getParameter("qIDList")).split(",");
					QueryListDAO queryDAO = new QueryListDAO();
					/// why call this sanjuktaaa
					
					//List<Query> queryList = (List)queryDAO.getQueryList(qIDArr);	
					
					// Start every selected process
					String filepath = "";
					//String[] filepathArr = new String[queryList.size()];
					List<File> files = new LinkedList<File>();
					
					Result[] queryResult = new Result[qIDArr.length];
					OutputStream outputStream;
					int count = 0;
					
					for(int i = 0; i < qIDArr.length; ++i)
					{							
						int index = Integer.parseInt(qIDArr[i]);
													
						// siripen, please validate this flow -- sanjukta
                        tempDir ="temp/queries/";
                        filepath =  context + tempDir + index+json;// + "_" + src.srcName;
                        log.info(filepath);
                        File resultFile = new File(filepath);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");                    
                        boolean resultExists = resultFile.exists();    
                                    
                        // siripen, please validate this flow -- sanjukta
                        String exePath = context + "proc/Debug/EmageOperators_" + qIDArr[i];
                        log.info(exePath);
                        File exeFile = new File(exePath);
                        boolean exists = exeFile.exists();

						
                        if(queryProcesses.size() <= 0 ||  (queryProcesses.size() > 0  && !queryProcesses.containsKey(qIDArr[i]))){
                            int qid = Integer.parseInt(qIDArr[i]);
                            QueryProcess q = parser.parseQuery(queryDAO.getQueryTree(index), 
                                    queryDAO.getFrameParameterQry(index));
                            queryProcesses.put(qIDArr[i], q);
                        } 
                        
                        Long timeWindow =  queryProcesses.get(qIDArr[i]).finalResolution.timeWindow;
                        
                        if(exists){
                        	
                            // the query .exe file is already created
                            // check whether the execution file is running or not???
                            // or check whether the result file is up-to-date or not
                        	
                            if(resultExists && (new Date().getTime() - resultFile.lastModified() < timeWindow)){
                                // up-to date result
                                // do nothing                            	
                            } else{
                                // out-dated result, so we need to restart the queryprocess
                                // first, remove the old execution file
                                //
                                // second, restart the queryprocess runnable                            	
                            	try {
                            		exists = exeFile.delete();
                            	} catch (Exception exe) {
                            		log.error("delete not working"+exe);
                            	}
                            	log.info("Did the exeFile get deleted"+exists);
                            	if (queryProcesses.get(qIDArr[i]).isRunning) {
                            		queryProcesses.get(qIDArr[i]).stop();
                            	}                        
                                new Thread(queryProcesses.get(qIDArr[i])).start();
                                // then, wait for the result to be generated
                                Thread.sleep(timeWindow.intValue());
                                //Thread.sleep(1);
                            }
                        } else{
                             // the query .exe file is not created yet
                            // so we need to start the runnable queryprocess 
                            new Thread(queryProcesses.get(qIDArr[i])).start();
                            // then, wait for the result to be generated
                            Thread.sleep(timeWindow.intValue());
                            //Thread.sleep(1);
                        }
 						
                        	byte[] output = org.apache.commons.io.FileUtils.readFileToByteArray(resultFile);
							Result query = new Result();
							query.resId = new Integer(index).toString();
							//query.output = new String(Base64.encode(output));
							query.output = new String(output);
							queryResult[count] = query;
							count++;
					}					
										
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					ObjectMapper mapper = new ObjectMapper();				
					mapper.writeValue(out, queryResult);				
					//out.print();
					out.flush();
					out.close();

			} 
			// Read the query file
			else if (request.getParameter("type").equals("readquery")) {
				String qid = request.getParameter("qid");

				OutputStream out = response.getOutputStream();
				BufferedReader reader = new BufferedReader(new FileReader(
						context + "proc/src/Q" + qid + ".cc"));
				String line;
				while ((line = reader.readLine()) != null) {
					line += "\n";
					out.write(line.getBytes());
				}
				reader.close();
				out.close();
			}
			else if(request.getParameter("type").equals("alert")){
				log.info("in alert part");
				// Parse the alert json
				//String alertJSONText = request.getParameter("json");
				//JSONObject alertObject = JSONObject.fromObject(alertJSONText);
				
				String userDS = request.getParameter("userDS").split(":")[0];
				String srcQuery = request.getParameter("sourceQuery").split(":")[0];
				String nearestDS = request.getParameter("nearestDs").split(":")[0];
				
				String[] qRanges = request.getParameter("qRange").split(":");
				double minValProb = Double.valueOf(qRanges[0]);
				double maxValProb = Double.valueOf(qRanges[1]);
				
				String[] aRanges = request.getParameter("aRange").split(":");
				double minValSol = Double.valueOf(aRanges[0]);
				double maxValSol = Double.valueOf(aRanges[1]);
				
				String msg = request.getParameter("msg");
				
				AlertProcessor alertProc = new AlertProcessor(userDS, srcQuery, nearestDS, minValProb,
						maxValProb, minValSol, maxValSol, msg, this);
				alertProc.DoAlerts();

				byte[] output = "Sent tweets".toString().getBytes();
				OutputStream out = response.getOutputStream();
				out.write(output);	        	      
				out.close();
			}// Read the query file
			else if (request.getParameter("type").equals("readquery")) {
				String qid = request.getParameter("qid");

				OutputStream out = response.getOutputStream();
				BufferedReader reader = new BufferedReader(new FileReader(
						context + "proc/src/q" + qid + ".cc"));
				String line;
				while ((line = reader.readLine()) != null) {
					line += "\n";
					out.write(line.getBytes());
				}
				reader.close();
				out.close();
			}

		} catch (Exception p) {
			log.error(p.getMessage());
			new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, p.getMessage());
			return ;
		}
	}

	/*
	
	@SuppressWarnings("unchecked")
	public DataSource parseDataSource(DataSource dataSrc)
	{
		Enum format = dataSrc.getSrcFormat();
        if(format.equals("stream"))
        	dataSrc.srcFormat = DataFormat.stream;
        else if(format.equals("visual"))
        	dataSrc.srcFormat = DataFormat.visual;
        else if(format.equals("csv"))
        	dataSrc.srcFormat = DataFormat.csv;       
    
        
        return dataSrc;
	}

	*/
	public FrameParameters getFinalParamFromSrc(int index)
	{
		if(index < sources.size())
			return ((DataSource)sources.get(index)).finalParam;
		else
		{
			// Create FrameParameters
			long timeWindow = 1000*10;
			long mSecsOffset = 1000;

			// Group 1
			double latUnit1 = 0.5;
			double longUnit1 = 0.5;
			double swLat1 = 24;
			double swLong1 = -125;
			double neLat1 = 50;
			double neLong1 = -66;

			FrameParameters fp = new FrameParameters(timeWindow, mSecsOffset, latUnit1,longUnit1, 
					swLat1,swLong1 , neLat1, neLong1);
			return fp;
		}
	}
	
	
	public void startDataProcess(DataSource src) //call
	{
		log.info("data source is not null:" + src.toString());		
		DataFormat format = src.getSrcFormat();
		
		String imgBasePath = context + "results/ds/";
		
		//DataProcess process = new DataProcess(merger, eit, wrapper, filepath, imgBasePath+"ds"+src.srcID, "ds"+src.srcID);
		DataSourceParser dataSrcParser= new DataSourceParser();
		DataProcess process = dataSrcParser.processData(src);
		dataProcesses.put(src.srcID, process);
		
		// Start the data collecting process
		new Thread(process).start();
		
//		if(format == DataFormat.stream)
//		{
//			Wrapper wrapper = null;
//			if(src.supportedWrapper.equalsIgnoreCase("Twitter"))
//			{
//				log.info("before creation");
//				wrapper = new TwitterWrapper(src.url, src.srcTheme, src.initParam, true);
//				log.info("wrapper created");
//				
//				String[] words = new String[src.bagOfWords.size()];
//				src.bagOfWords.toArray(words);
//				((TwitterWrapper) wrapper).setBagOfWords(words);
//			}
//			else if(src.supportedWrapper.equalsIgnoreCase("Flickr"))
//			{
//				wrapper = new FlickrWrapper(src.url, src.srcTheme, src.initParam);
//				String[] words = new String[src.bagOfWords.size()];
//				src.bagOfWords.toArray(words);
//				((FlickrWrapper) wrapper).setBagOfWords(words);
//			}
//			else if(src.supportedWrapper.equalsIgnoreCase("sim") || src.supportedWrapper.equalsIgnoreCase("Simulator"))
//			{
//				// Create Distribution Parameters and Generators
//				ArrayList<DistParameters> dParams = new ArrayList<DistParameters>();
//				// LA
//				GaussianParameters2D gParam = new GaussianParameters2D(34.1, -118.2, 3.0, 3.0, 200);
//				dParams.add(gParam);
//				// SF
//				gParam = new GaussianParameters2D(37.8, -122.4, 2.0, 2.0, 200);
//				dParams.add(gParam);
//				// Seattle
//				gParam = new GaussianParameters2D(47.6, -122.3, 1.0, 1.0, 100);
//				dParams.add(gParam);
//				// NYC
//				gParam = new GaussianParameters2D(40.8, -74.0, 5.0, 5.0, 200);
//				dParams.add(gParam);
//
//				
//				
//				// Wrappers
//				//wrapper = new SimDataWrapper(src.url, src.srcTheme, src.initParam, Kernel.gaussian, dParams);//changed to initParam...vks:Aug30,2011
//				wrapper = new SimDataWrapper(src.url, src.srcTheme, src.initParam, com.eventshop.eventshoplinux.util.datasourceUtil.simulator.Simulator.Kernel.gaussian, dParams);
//			}
//							
//			// Add EmageIterator
//			EmageIterator eit = new STTEmageIterator();
//			eit.setSTTPointIterator(wrapper);
//			eit.setSrcID(new Long(src.srcID));
//			
//			// Add output filename
//
//			// change it to be query independent: 08/19/2011 Mingyan
//			String filepath = tempDir + "/ds" + src.srcID + "_" + src.srcName;
//			
//			// Create st merger
//			STMerger merger = null;
//			/*
//			if(!src.finalParam.equals(src.initParam))
//			{
//				log.info("The initial and final frame parameters are different");
//				
//				merger = new STMerger(src.finalParam);
//				SpatialMapper sp = null;
//				TemporalMapper tp = null;
//				if(src.initParam.latUnit < src.finalParam.latUnit 
//						|| src.initParam.longUnit < src.finalParam.longUnit)
//					sp = SpatialMapper.sum;
//				else if(src.initParam.latUnit > src.finalParam.latUnit 
//						|| src.initParam.longUnit > src.finalParam.longUnit)
//					sp = SpatialMapper.repeat;
//				
//				if(src.initParam.timeWindow < src.finalParam.timeWindow)
//					tp = TemporalMapper.sum;
//				else if(src.initParam.timeWindow > src.finalParam.timeWindow)
//					tp = TemporalMapper.repeat;
//				
//				merger.addIterator(eit, sp, tp);
//				merger.setMergingExpression("mulED(R0,1)");
//			}
//			*/
//			// Add data processors
//			DataProcess process = new DataProcess(merger, eit, wrapper, filepath, imgBasePath+"ds"+src.srcID, "ds"+src.srcID);
//			dataProcesses.put(src.srcID, process);
//			
//			// Start the data collecting process
//			new Thread(process).start();
//		}
//		else if(format == DataFormat.visual)
//		{
//			try {
//				BufferedReader reader = new BufferedReader(new FileReader(src.visualParam.tranMatPath));
//				String line = reader.readLine();
//				String[] dim = line.split(",");
//				double[][] tranMat = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
//				int row = 0;
//				String tranMatStr = "tranMat:\n ";
//				while((line = reader.readLine()) != null)
//				{
//					String[] numbers = line.split(",");
//					for(int col = 0; col < numbers.length; ++col)
//						tranMat[row][col] = Double.parseDouble(numbers[col].trim());
//					row++;
//				}
//				reader.close();
//				
//				for(int x = 0; x < tranMat.length; x++){
//					for(int y = 0; y < tranMat[x].length; y++)
//						tranMatStr += tranMat[x][y] + ", ";
//					tranMatStr += "\n";
//				}
//				log.info(tranMatStr);
//				
//				
//				reader = new BufferedReader(new FileReader(src.visualParam.colorMatPath));
//				line = reader.readLine();
//				dim = line.split(",");
//				double[][] colorMat = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
//				row = 0;
//				while((line = reader.readLine()) != null)
//				{
//					String[] numbers = line.split(",");
//					for(int col = 0; col < numbers.length; ++col)
//						colorMat[row][col] = Double.parseDouble(numbers[col].trim());
//					row++;
//				}
//				reader.close();
//				
//				String colorMatStr = "colorMatStr:\n ";
//				for(int x = 0; x < colorMat.length; x++){
//					for(int y = 0; y < colorMat[x].length; y++)
//						colorMatStr += colorMat[x][y] + ", ";
//					colorMatStr += "\n";
//				}
//				log.info(colorMatStr);
//				
//				VisualEmageIterator veit = new VisualEmageIterator(src.initParam, src.srcTheme, src.url, tranMat, 
//						colorMat, src.visualParam.maskPath, src.visualParam.ignoreSinceNumber);
//				veit.setSrcID(new Long(src.srcID));
//				
//				// Add output filename
//				//Change it to be query independent: 08/19/2011 Mingyan
//				String filepath = tempDir + "/ds" + src.srcID + "_" + src.srcName;
//				
//				// Create st merger
//				STMerger merger = null;
//				/*
//				if(!src.finalParam.equals(src.initParam))
//				{
//					merger = new STMerger(src.finalParam);
//					SpatialMapper sp = null;
//					TemporalMapper tp = null;
//					if(src.initParam.latUnit < src.finalParam.latUnit 
//							|| src.initParam.longUnit < src.finalParam.longUnit)
//						sp = SpatialMapper.average;
//					else if(src.initParam.latUnit > src.finalParam.latUnit 
//							|| src.initParam.longUnit > src.finalParam.longUnit)
//						sp = SpatialMapper.repeat;
//					
//					if(src.initParam.timeWindow < src.finalParam.timeWindow)
//						tp = TemporalMapper.sum;
//					else if(src.initParam.timeWindow > src.finalParam.timeWindow)
//						tp = TemporalMapper.repeat;
//					
//					merger.addIterator(veit, sp, tp);
//					merger.setMergingExpression("mulED(R0,1)");
//				}
//				*/
//								
//				// Add data processors
//				DataProcess process = new DataProcess(merger, veit, null, filepath, imgBasePath+"ds"+src.srcID, "ds"+src.srcID);
//				dataProcesses.put(src.srcID, process);
//				
//				// Start the data collecting process
//				new Thread(process).start();
//				log.info("data process started");
//			} catch (NumberFormatException e) {
//				log.error(e.getMessage());
//			} catch (IOException e) {
//				log.error(e.getMessage());
//			}
//		}
//		else if(format == DataFormat.csv)
//		{
//			String filepath = tempDir + "/ds" + src.srcID + "_" + src.srcName;
//			CSVEmageIterator csvEIter = new CSVEmageIterator(src.initParam, src.srcTheme, src.url);
//			csvEIter.setSrcID(new Long(src.srcID));
//			
//			// Create st merger
//			
//			STMerger merger = null;
//			/*
//			if(!src.finalParam.equals(src.initParam))
//			{
//				merger = new STMerger(src.finalParam);
//				SpatialMapper sp = null;
//				TemporalMapper tp = null;
//				if(src.initParam.latUnit < src.finalParam.latUnit 
//						|| src.initParam.longUnit < src.finalParam.longUnit)
//					sp = SpatialMapper.average;
//				else if(src.initParam.latUnit > src.finalParam.latUnit 
//						|| src.initParam.longUnit > src.finalParam.longUnit)
//					sp = SpatialMapper.repeat;
//				
//				if(src.initParam.timeWindow < src.finalParam.timeWindow)
//					tp = TemporalMapper.sum;
//				else if(src.initParam.timeWindow > src.finalParam.timeWindow)
//					tp = TemporalMapper.repeat;
//				
//				merger.addIterator(csvEIter, sp, tp);
//				merger.setMergingExpression("mulED(R0,1)");
//			}
//			*/		
//			DataProcess process = new DataProcess(merger, csvEIter, null, filepath, imgBasePath+"ds"+src.srcID, "ds"+src.srcID);
//			dataProcesses.put(src.srcID, process);
//			
//			// Start the data collecting process
//			new Thread(process).start();
//		}
	}
	
	
	public static void main(String[] args)
	{
		RegisterServlet servlet = new RegisterServlet();
		servlet.init();
		servlet.context = Config.getProperty("context");
		String tempDir = Config.getProperty("tempDir");	
		try{
			System.out.println(servlet.testStartDataProcess("7"));
			new Thread().sleep(2*60*1000);
			System.out.println(servlet.testStopDataProcess("7"));
			
		} catch(Exception e){
			e.printStackTrace();
		}
		//servlet.preRegisterDataSourcesQueries(); //not needed?
		//String queryText = "[{\"qID\":16,\"type\":\"filter_ds6\",\"query\":{\"dataSrcID\":\"ds6\",\"maskMethod\":\"map\",\"coords\":[24,-125,50,-66],\"placename\":\"New York City\",\"filePath\":\"/home/sln/ESProjects/es-auge/Temp/q0_filterFile\",\"valRange\":[\"-99999999\",\"99999999\"],\"timeRange\":[\"0\",\"9223372036854775807\"],\"normMode\":true,\"normVals\":[\"0\",\"100\"]}}]";
		//String queryText = args[0];
		//Query q0 = (Query)servlet.query.parseQuery(queryText);
		//Query q0 = new Query(null);
		//servlet.queries.put(q0.queryID,q0);
		//int count = servlet.queries.size();
		//new Thread((Query)servlet.queries.get(count -1 )).start();
		
		 
	}
	
	private void preRegisterDataSourcesQueries()
	{
    	DataSourceManagementDAO dataSrcDAO = new DataSourceManagementDAO();
    	List<DataSource> tempArry = (ArrayList)dataSrcDAO.getDataSrcList();
    	for (int i=0;i<tempArry.size();i++) {    		
    		sources.put(((DataSource)tempArry.get(i)).srcID,(DataSource)tempArry.get(i));
    	}
    	
    	//preRegisteredSrcCount = sources.size();
    	//this is not the right query object
    	//QueryListDAO adminDAO = new QueryListDAO();
    	//ArrayList<QueryDTO> queryList = (ArrayList)adminDAO.getQueryList();    	
    	//queries = convertDTOListToQueryHash(queryList);
    	//preRegisteredQueryCount = queries.size();
    	
    	//do we need to set finalframe for source?
    			
	}
    
    private FrameParameters getDefaultFrameParams() {
    	long timeWindow = 1000*60*5; //*60*24*2;//the last 2 days
		long syncAtMilliSec = 1000;
		double latUnit = 0.1;
		double longUnit = 0.1;
		double swLat = 24;
		double swLong = -125;
		double neLat = 50;
		double neLong = -66;	
		FrameParameters fp = new FrameParameters(timeWindow, syncAtMilliSec,latUnit,longUnit, swLat,swLong , neLat, neLong);
		return fp;
    }
    
    /*
    private HashMap convertDTOListToQueryHash(ArrayList<QueryDTO> queryList) {
    	HashMap queryHash = new HashMap();
    	for (int i=0;i<queryList.size();i++)	{
    		QueryDTO queryDTO = queryList.get(i);
    		Query query = new Query(context);
    		query.ccPath = queryDTO.getQueryEsql();
    		query.setQueryID(queryDTO.getqID());
    		//query.setOpName(queryDTO.getQueryName());
    		//query.finalResolution(queryDTO.get)
    		queryHash.put(queryDTO.getqID(), query);
    	}  
    	return queryHash;
    }
    */
 
    
}
