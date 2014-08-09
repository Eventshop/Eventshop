package com.eventshop.eventshoplinux.servlets;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.eventshop.eventshoplinux.DAO.query.QueryListDAO;
import com.eventshop.eventshoplinux.domain.common.FrameParameters;
import com.eventshop.eventshoplinux.domain.datasource.DataSource;
import com.eventshop.eventshoplinux.util.commonUtil.Config;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class QueryJSONParser 
{
	QueryProcess newQuery;
	RegisterServlet servlet;
	
	// For duplicate data sources
	int srcSubscript = 0;
	
	// For the same type of constructs used more than once
	int maskID = 0;
	int fcondID = 0;
	int grpCritID = 0;
	int spPatternID = 0;
	int tpPatternID = 0;
	int aggID = 0;
	
	JsonParser parser = new JsonParser();
	
	public QueryJSONParser(RegisterServlet servlet)
	{
		newQuery = null;
		this.servlet = servlet;
	}
	
//	public QueryJSONParser(ArrayList<DataProcess> dp){
//		newQuery = null;
//		servlet = new RegisterServlet();
//		
//		for(int i = 0; i < dp.size(); i++){
//			servlet.dataProcesses.add(dp.get(i));
//		}		
//	}
	public QueryJSONParser(ArrayList<DataSource> ds){
		newQuery = null;
		servlet = new RegisterServlet();
		
		for(int i = 0; i < ds.size(); i++){
			servlet.sources.put(ds.get(i).srcID, ds.get(i));
		}		
	}
	
	// For testing purpose only
	public QueryJSONParser() 
	{
		newQuery = null;
		servlet = new RegisterServlet();
		
		String tempDir = Config.getProperty("tempDir");
		//servlet.init();	

	}
	



	public QueryProcess parseQuery(List<String> queryTree, FrameParameters finalFrame)
	{
		newQuery = new QueryProcess(servlet.context);
		newQuery.setFinalResolution(finalFrame);
		maskID = 0;
		fcondID = 0;		
		for(int i = 0; i < queryTree.size(); i++)
		{
			System.out.println("queryTree"+ queryTree.get(i));			
			JsonArray queryArr = (JsonArray) parser.parse(queryTree.get(i)); // have to first get the query Array --sanjukta 06-08-2014			
			JsonObject query = queryArr.get(0).getAsJsonObject(); // then get the first element and convert to jsonobject --sanjukta 06-08-2014
			String queryType = query.get("patternType").getAsString();
			
			String cc = null;
			if(queryType.indexOf("filter") != -1)
				cc = parseFilter(query);
			else if(queryType.indexOf("grouping") != -1)
				cc = parseGrouping(query);
			else if(queryType.indexOf("aggregation") != -1)
				cc = parseAggregation(query);
			else if(queryType.indexOf("spchar") != -1)
				cc = parseSpatialChar(query);
			else if(queryType.indexOf("spmatching") != -1)
				cc = parseSpatialMatching(query);
			else if(queryType.indexOf("tpchar") != -1)
				cc = parseTemporalChar(query);
			else if(queryType.indexOf("tpmatching") != -1)
				cc = parseTemporalMatching(query);
			else
				System.err.println("No such operators!");
			
			// push the cc string into query list of query object
			newQuery.queryCC.add(cc);	
			
			// queryID, and queryType of this query will refer to the root of the query tree
		}		
		return newQuery;
	}
	
	
	private String parseFilter(JsonObject query)
	{
		// generate the operator variable
		String opVar = "Q" + query.get("qID").getAsString();
		
		// Set the opVar to be query ID of this query
		newQuery.setQueryID(opVar);
		// Set the operator type
		newQuery.setQueryType("filter");
		
		// get the source ID
		JsonArray dataSrc = query.get("dataSources").getAsJsonArray();
		// For Filter op, only one data source can be used
		String source = dataSrc.get(0).getAsString();
		if(source.indexOf("ds")  != -1){
			// add the used srcID into newQuery.srcIDs
			String srcID = source.substring(2);
			boolean srcFound = false;
			for(int i = 0; i < newQuery.queriedSources.size(); ++i)
			{
				// If the data source has been added once, then ignore it
				if(srcID.equalsIgnoreCase(newQuery.queriedSources.get(i).srcID))
				{
					srcFound = true;
					// Change the data Src to be the dsID_subscript
					source = source + "_" + srcSubscript;
					srcSubscript++;
					
					// update the srcVarName
					DataSource src = new DataSource(newQuery.queriedSources.get(i));
					src.srcID = srcID;
					src.srcVarName = source;
					newQuery.queriedSources.add(src);
					System.out.println("add datasource into queriedSources list: --------\n" + src.toString());
					break;
				}
			}
			if(!srcFound)
			{
				newQuery.queriedSources.add(servlet.sources.get(srcID));
				//newQuery.setFinalResolution(servlet.getFinalParamFromSrc(srcID));
			}
		}
		
		
		// get the mask method
		int lowX = 0, lowY = 0, upX = 0, upY = 0;
		String maskMethod = query.get("maskMethod").getAsString();
		if(maskMethod.equals("map") || maskMethod.equals("textual"))
		{
			JsonArray coordsArray = query.get("coords").getAsJsonArray();
			double[] coords = new double[coordsArray.size()];
			for(int i = 0; i < coordsArray.size(); i++){
				coords[i] = coordsArray.get(i).getAsDouble();
			}
			
			// System.out.println(coords);
			lowX = (int) Math.abs(Math.ceil((coords[1] - newQuery.finalResolution.swLong) / newQuery.finalResolution.longUnit));
			//lowX = Math.min(0, lowX);
			
			upX = (int) Math.abs(Math.ceil((coords[3] - newQuery.finalResolution.swLong) / newQuery.finalResolution.longUnit));
			//upX = Math.max(newQuery.finalResolution.numOfColumns, upX);
			
			upY = newQuery.finalResolution.numOfRows - 
					(int) Math.abs(Math.ceil((coords[0] - newQuery.finalResolution.swLat) / newQuery.finalResolution.latUnit));
			//lowY = Math.min(0, lowY);
			
			lowY = newQuery.finalResolution.numOfRows - 
					(int) Math.abs(Math.ceil((coords[2] - newQuery.finalResolution.swLat) / newQuery.finalResolution.latUnit));
			//upY = Math.max(newQuery.finalResolution.numOfRows, upY);
			
			if(lowX == upX) lowX--;
			if(lowY == upY) lowY--;
		} else{
			lowX = 0;
			upX = newQuery.finalResolution.getNumOfColumns();
			lowY = 0;
			upY = newQuery.finalResolution.getNumOfRows();
		}
		
		// get the value range
		JsonArray vRange = query.get("valRange").getAsJsonArray();
		
		String valMin = vRange.get(0).getAsString();
		String valMax = vRange.get(1).getAsString();
		
		// get the time range
		JsonArray interval = query.get("timeRange").getAsJsonArray();
		String tMin = interval.get(0).getAsString();
		String tMax = interval.get(1).getAsString();
		
		if(tMax.equals("-999") || tMax.equals("1"))
		{
			long min = System.currentTimeMillis() - Long.parseLong(tMin)*1000;
			tMin = String.valueOf(min);
			tMax = String.valueOf(Long.MAX_VALUE);
		}
		
		//get the norm values range 
		JsonArray normRange = query.get("normVals").getAsJsonArray();
		String nMin=normRange.get(0).getAsString();
		String nMax=normRange.get(1).getAsString();
		String nMode=query.get("normMode").getAsString();
		
		
		// generate the cc string
		String filterCC = "";
		// generate the mask
		String maskVar = "";
		if(maskMethod.equals("matrix"))
		{
			maskVar = "\"" + query.get("filePath").getAsString().replace("\\", "\\\\") + "\"";
		} else {
			maskVar = "mask" + maskID;
			filterCC = "\tint rows" + maskID + " = " + newQuery.finalResolution.numOfRows + ";\n";
			filterCC += "\tint cols" + maskID + " = " + newQuery.finalResolution.numOfColumns + ";\n";
			filterCC += ("\tMat " + maskVar + "(rows"+maskID+" , cols"+maskID+" , CV_8U);\n");
			filterCC += "\tfor(int i = 0; i < rows"+maskID+"; ++i)\n";
			filterCC += "\t{\n";
			filterCC += "\t\tfor(int j = 0; j < cols"+maskID+"; ++j)\n";
			filterCC += "\t\t{\n";
			filterCC += ("\t\t\tif(j <= " + upX + " && j> " + lowX + " && i<=" + upY + " && i> " + lowY + ")\n");
			filterCC += ("\t\t\t\t" + maskVar + ".at<unsigned char>(i, j) = 255;\n");
			filterCC += "\t\t\telse\n";
			filterCC += ("\t\t\t\t" + maskVar + ".at<unsigned char>(i, j) = 0;\n");
			filterCC += "\t\t}\n";
			filterCC += "\t}\n";
			maskID++;
		}
		
		// generate the filter condition
		String fcondVar = "fcond" + fcondID;
		fcondID++;
		filterCC += ("\tFilterCondition " + fcondVar + "(" + maskVar + ", LT, " + valMin + ", " 
				+ valMax + ", " + tMin + ", " + tMax +", " + nMode + ", " + nMin + ", " + nMax + ");\n");
		// generate the filter operator
		filterCC += ("\tFilter " + opVar + "("  + source + ", " + fcondVar + ");\n\n");
		
		System.out.println("FILTER:--------------------------------- \n" +filterCC);
		return filterCC;
	}
	
	
	private String parseGrouping(JsonObject query)
	{
		// generate the operator variable
		String opVar = "Q" + query.get("qID").getAsString();
		// Set the opVar to be query ID of this query
		newQuery.setQueryID(opVar);
		// Set the operator type
		newQuery.setQueryType("grouping");
		
		// get the source ID
		JsonArray dataSrc = query.get("dataSources").getAsJsonArray();
		// For grouping op, only one data source can be used
		String source = dataSrc.get(0).getAsString();
		
		if(source.indexOf("ds") != -1)
		{
			// add the used srcID into newQuery.srcIDs
			// add final resolution
			String srcID = source.substring(2);
			boolean srcFound = false;
			for(int i = 0; i < newQuery.queriedSources.size(); ++i)
			{
				// If the data source has been added once, then ignore it
				if(srcID.equalsIgnoreCase(newQuery.queriedSources.get(i).srcID))
				{
					srcFound = true;
					// Change the data Src to be the dsID_subscript
					source = source + "_" + srcSubscript;
					srcSubscript++;
					
					// update the srcVarName
					DataSource src = new DataSource(newQuery.queriedSources.get(i));
					src.srcID = srcID;
					src.srcVarName = source;
					newQuery.queriedSources.add(src);
					break;
				}
			}
			if(!srcFound)
			{
				newQuery.queriedSources.add(servlet.sources.get(srcID));
				//newQuery.setFinalResolution(servlet.getFinalParamFromSrc(srcID));
			}
		}

		String split = query.get("split").getAsString().toLowerCase();
		String doColoring = query.get("doColoring").getAsString().toLowerCase();
		String colorVector = null;
		if(doColoring.equals("true"))
		{
			JsonArray colors = query.get("colorCodes").getAsJsonArray();

			colorVector = "\tvector<GroupingColors> grpColors;\n";
			for(int i = 0; i < colors.size(); i++)
			{
				colorVector += ("\tgrpColors.push_back(" + colors.get(i).getAsString().trim().toLowerCase() + ");\n");
			}
		}
		
		String groupCC = null;
		String method = query.get("method").getAsString();
		System.out.println("grouping method : " + method);
		if(method.equalsIgnoreCase("KMeans"))
		{
			String numGroups = query.get("numGroup").getAsString();
			
			groupCC = ("\n\tGrouping " + opVar + "(" + dataSrc);
			groupCC += (", " + numGroups + ", KMEANS, ");
			groupCC += (split + ", " + doColoring);
		}
		else if(method.indexOf("Threshold") != -1)
		{
			groupCC = ("\n\tGroupingCriteria gcrit" + grpCritID + " = GroupingCriteria();\n");
			groupCC += ("\tgcrit" + grpCritID + ".critGrpType = gt_absolute;\n");
			groupCC += ("\tvector<double> thresh" + grpCritID + ";\n");
			
			JsonArray thresholds = query.get("thresholds").getAsJsonArray();
			for(int i = 0; i < thresholds.size(); i++)
			{
				groupCC += ("\tthresh" + grpCritID + ".push_back(" + thresholds.get(i).getAsString().trim() + ");\n");
			}
			
			groupCC += ("\tgcrit" + grpCritID + ".setThresholds(thresh" + grpCritID + ");\n");
			groupCC += ("\tGrouping " + opVar + "(" + source);
			groupCC += (", gcrit" + grpCritID + ", ");
			groupCC += (split + ", " + doColoring);
			grpCritID++;
		}
		
		if(colorVector != null)
			groupCC = colorVector + groupCC + (", grpColors);\n");
		else
			groupCC += (");\n");
		
		return groupCC;
	}

	
	private String parseAggregation(JsonObject query)
	{
		// generate the operator variable
		String opVar = "Q" + query.get("qID").getAsString();
		// Set the opVar to be query ID of this query
		newQuery.setQueryID(opVar);
		// Set the operator type
		newQuery.setQueryType("aggregation");
		
		// Get the source IDs
		String aggCC;
		
		aggCC = "\n\tvector<ProcEmageIterator*> eits" + aggID + ";\n";
		JsonArray srcArray = query.get("dataSources").getAsJsonArray();
		for(int i = 0; i < srcArray.size(); i++)
		{
			String dataSrc = srcArray.get(i).getAsString();			
			if(dataSrc.indexOf("ds") != -1)
			{
				String srcID = dataSrc.substring(2);
				System.out.println("qsource size" + newQuery.queriedSources.size());
				System.out.println("get id " +newQuery.queriedSources.get(0).toString());
				boolean srcFound = false;
				for(int j = 0; j < newQuery.queriedSources.size(); ++j)
				{
					// If the data source has been added once, then ignore it
					if(srcID.equalsIgnoreCase(newQuery.queriedSources.get(i).srcID))
					{
						srcFound = true;
						// Change the data Src to be the dsID_subscript
						dataSrc = dataSrc + "_" + srcSubscript;
						srcSubscript++;
						
						System.out.println("data source has been added onece " + srcID);
						// update the srcVarName
						DataSource src = new DataSource(newQuery.queriedSources.get(i));
						src.srcID = srcID;
						src.srcVarName = dataSrc;
						newQuery.queriedSources.add(src);
						break;
					}
				}
				if(!srcFound)
				{
					newQuery.queriedSources.add(servlet.sources.get(srcID));
					//newQuery.setFinalResolution(servlet.getFinalParamFromSrc(srcID));
				}
			}
			aggCC += ("\teits" + aggID + ".push_back(&" + dataSrc + ");\n"); 
		}
		
		JsonArray valueArray = query.get("values").getAsJsonArray();
		if(valueArray.size() > 0)
		{
			aggCC += ("\n\tvector<double> scalars" + aggID + ";\n");
			for(int i = 0; i < valueArray.size(); ++i)
			{
				aggCC += ("\tscalars" + aggID + ".push_back(" + valueArray.get(i).getAsString() + ");\n");
			}
			// get scalarFirst
			String scalarFirst = query.get("scalarFirst").getAsString();
			// get aggOperator
			String operation = query.get("aggOperator").getAsString().substring(3).toUpperCase();
			// get normalization
			if(query.get("valueNorm") != null || query.get("valueNorm").getAsString().equals("false"))
			{
				aggCC += "\n\tAggregate " + opVar + "(eits" + aggID + ", scalars" + aggID + ", Agg"	 
						+ operation + ", " + scalarFirst + ");\n\n";
			}
			else
			{
				JsonArray normArray = query.get("normedRange").getAsJsonArray();
				String minValue = normArray.get(0).getAsString();
				String maxValue = normArray.get(1).getAsString();
				aggCC += "\n\tAggregate " + opVar + "(eits" + aggID + ", scalars" + aggID + ", Agg"	 
						+ operation + ", " + scalarFirst + ", true, " + minValue + ", " + maxValue + ");\n\n";
			}
		}
		else
		{
			// get aggOperator
			String operation = query.get("aggOperator").getAsString().substring(3).toUpperCase();
			// get normalization
			if(query.get("valueNorm") != null|| query.get("valueNorm").getAsString().equals("false"))
			{
				aggCC += "\n\tAggregate " + opVar + "(eits" + aggID + ", Agg" + operation + ");\n\n";
			}
			else
			{
				JsonArray normArray = query.get("normedRange").getAsJsonArray();
				String minValue = normArray.get(0).getAsString();
				String maxValue = normArray.get(1).getAsString();
				aggCC += "\n\tAggregate " + opVar + "(eits" + aggID + ", Agg" + operation + ", true, " + minValue + ", " + maxValue + ");\n\n";
			}
		}
		
		aggID++;
		
	    System.out.println(aggCC);
		return aggCC;		
	}
	
	
	private String parseSpatialChar(JsonObject query)
	{
		// generate the operator variable
		String opVar = "Q" + query.get("qID").getAsString();
		// Set the opVar to be query ID of this query
		newQuery.setQueryID(opVar);
		// Set the operator type
		newQuery.setQueryType("spchar");
		
		// get the source ID
		JsonArray dataSrc = query.get("dataSources").getAsJsonArray();
		// For grouping op, only one data source can be used
		String source = dataSrc.get(0).getAsString();
		
		if(source.indexOf("ds") != -1)
		{
			// add the used srcID into newQuery.srcIDs
			// add final resolution
			String srcID = source.substring(2);
			boolean srcFound = false;
			for(int i = 0; i < newQuery.queriedSources.size(); ++i)
			{
				// If the data source has been added once, then ignore it
				if(srcID.equalsIgnoreCase(newQuery.queriedSources.get(i).srcID))
				{
					srcFound = true;
					// Change the data Src to be the dsID_subscript
					source = source + "_" + srcSubscript;
					srcSubscript++;
					
					// update the srcVarName
					DataSource src = new DataSource(newQuery.queriedSources.get(i));
					src.srcID = srcID;
					src.srcVarName = source;
					newQuery.queriedSources.add(src);
					break;
				}
			}
			if(!srcFound)
			{
				newQuery.queriedSources.add(servlet.sources.get(srcID));
				//newQuery.setFinalResolution(servlet.getFinalParamFromSrc(srcID));
			}
		}
		
		String spcharOp = query.get("spCharoperator").getAsString();
		newQuery.setOpName(spcharOp);
		spcharOp = spcharOp.toUpperCase();		
		
		String spcharCC;
		spcharCC = "\tSpatialChar " + opVar + "(" + source + ", " + spcharOp + ");\n\n";
		return spcharCC;		
	}
	
	
	private String parseSpatialMatching(JsonObject query)
	{
		// generate the operator variable
		String opVar = "Q" + query.get("qID").getAsString();
		// Set the opVar to be query ID of this query
		newQuery.setQueryID(opVar);
		// Set the operator type
		newQuery.setQueryType("spmatching");
		
		// get the source ID
		JsonArray dataSrc = query.get("dataSources").getAsJsonArray();
		// For spatial matching op, only one data source can be used
		String source = dataSrc.get(0).getAsString();
		
		if(source.indexOf("ds") != -1)
		{
			// add the used srcID into newQuery.srcIDs
			// add final resolution
			String srcID = source.substring(2);
			boolean srcFound = false;
			for(int i = 0; i < newQuery.queriedSources.size(); ++i)
			{
				// If the data source has been added once, then ignore it
				if(srcID.equalsIgnoreCase(newQuery.queriedSources.get(i).srcID))
				{
					srcFound = true;
					// Change the data Src to be the dsID_subscript
					source = source + "_" + srcSubscript;
					srcSubscript++;
					
					// update the srcVarName
					DataSource src = new DataSource(newQuery.queriedSources.get(i));
					src.srcID = srcID;
					src.srcVarName = source;
					newQuery.queriedSources.add(src);
					break;
				}
			}
			if(!srcFound)
			{
				newQuery.queriedSources.add(servlet.sources.get(srcID));
				//newQuery.setFinalResolution(servlet.getFinalParamFromSrc(srcID));
			}
		}
		
		String sizeNorm = query.get("sizeNorm").getAsString().toLowerCase();
		String valueNorm = query.get("valueNorm").getAsString().toLowerCase();
		int src = query.get("patternSrc").getAsInt();
		
		String spMatchingCC = null;
		// Get input from file
		if(src == 0)
		{
			String filePath = query.get("filePath").getAsString().replace("\\", "\\\\");
			spMatchingCC = "\tSpatialPatternMatching " + opVar + "(" + dataSrc + ", Input"
					+ ", " + sizeNorm + ", " + valueNorm + ", \"" + filePath + "\");\n\n";
		}
		// Get input from generated patterns
		else if(src == 1)
		{
			String pattern = null;
			String patternType;

			String numRows = query.get("numRows").getAsString().trim();
			String numCols = query.get("numCols").getAsString().trim();
			patternType = query.get("patternType").getAsString();
			
			// Generate the gaussian pattern code
			if(patternType.equals("gaussian"))
			{
				patternType = "Gaussian";
				JsonObject gaussParam = query.get("gaussParam").getAsJsonObject();
				String centerX = gaussParam.get("centerX").getAsString();
				String centerY = gaussParam.get("centerY").getAsString();
				String varX = gaussParam.get("varX").getAsString();
				String varY = gaussParam.get("varY").getAsString();
				String amplitude = gaussParam.get("amplitude").getAsString();
				pattern = "\tGaussianPattern spPattern"+ spPatternID + "(" + numRows + ", " +
						numCols + ", " + centerX + ", " + centerY + ", " + varX + ", " + 
						varY + ", " + amplitude + ");\n";
			}
			// Generate the linear pattern code
			else if(patternType.equals("linear"))
			{
				patternType = "Linear2D";
				JsonObject linearParam = query.get("linearParam").getAsJsonObject();
				String startX = linearParam.get("startX").getAsString();
				String startY = linearParam.get("startY").getAsString();
				String startValue = linearParam.get("startValue").getAsString();
				String dirGradient = linearParam.get("dirGradient").getAsString();
				String valGradient = linearParam.get("valGradient").getAsString();
				pattern = "\tLinearPattern spPattern" + spPatternID + "(" + numRows + ", " +
						numCols + ", " + startX + ", " + startY + ", " + startValue + ", " +
						dirGradient + ", " + valGradient + ");\n";
				
			}
			
			spMatchingCC = pattern;
			spMatchingCC += "\tSpatialPatternMatching " + opVar + "(" + source + ", " + patternType
					+ ", " + sizeNorm + ", " + valueNorm + ", &spPattern" + spPatternID + ");\n\n";
			
			spPatternID++;
		}
		
		return spMatchingCC;		
	}
	
	
	private String parseTemporalChar(JsonObject query)
	{
		// generate the operator variable
		String opVar = "Q" + query.get("qID").getAsString();
		// Set the opVar to be query ID of this query
		newQuery.setQueryID(opVar);
		// Set the operator type
		newQuery.setQueryType("tpchar");
		
		// get the source ID
		JsonArray dataSrc = query.get("dataSources").getAsJsonArray();
		// For temporal char op, only one data source can be used
		String source = dataSrc.get(0).getAsString();
		
		if(source.indexOf("ds") != -1)
		{
			// add the used srcID into newQuery.srcIDs
			// add final resolution
			String srcID = source.substring(2);
			boolean srcFound = false;
			for(int i = 0; i < newQuery.queriedSources.size(); ++i)
			{
				// If the data source has been added once, then ignore it
				if(srcID.equalsIgnoreCase(newQuery.queriedSources.get(i).srcID))
				{
					srcFound = true;
					// Change the data Src to be the dsID_subscript
					source = source + "_" + srcSubscript;
					srcSubscript++;
					
					// update the srcVarName
					DataSource src = new DataSource(newQuery.queriedSources.get(i));
					src.srcID = srcID;
					src.srcVarName = source;
					newQuery.queriedSources.add(src);
					break;
				}
			}
			if(!srcFound)
			{
				newQuery.queriedSources.add(servlet.sources.get(srcID));
				//newQuery.setFinalResolution(servlet.getFinalParamFromSrc(srcID));
			}
		}
		
		String tpcharOp = query.get("tmplCharOperator").getAsString();
		newQuery.setOpName(tpcharOp);
		tpcharOp = tpcharOp.toUpperCase();
		String window = query.get("timeWindow").getAsString().trim();
		
		String tpcharCC;
		tpcharCC = "\tTemporalChar " + opVar + "(" + source + ", " + tpcharOp + ", " + window + "*1000);\n\n";
		return tpcharCC;	
	}
	
	
	private String parseTemporalMatching(JsonObject query)
	{
		// generate the operator variable
		String opVar = "Q" + query.get("qID").getAsString();
		// Set the opVar to be query ID of this query
		newQuery.setQueryID(opVar);
		// Set the operator type
		newQuery.setQueryType("tpmatching");
		
		// get the source ID
		JsonArray dataSrc = query.get("dataSources").getAsJsonArray();
		// For temporal matching op, only one data source can be used
		String source = dataSrc.get(0).getAsString();
		
		if(source.indexOf("ds") != -1)
		{
			// add the used srcID into newQuery.srcIDs
			// add final resolution
			String srcID = source.substring(2);
			boolean srcFound = false;
			for(int i = 0; i < newQuery.queriedSources.size(); ++i)
			{
				// If the data source has been added once, then ignore it
				if(srcID.equalsIgnoreCase(newQuery.queriedSources.get(i).srcID))
				{
					srcFound = true;
					// Change the data Src to be the dsID_subscript
					source = source + "_" + srcSubscript;
					srcSubscript++;
					
					// update the srcVarName
					DataSource src = new DataSource(newQuery.queriedSources.get(i));
					src.srcID = srcID;
					src.srcVarName = source;
					newQuery.queriedSources.add(src);
					break;
				}
			}
			if(!srcFound)
			{
				newQuery.queriedSources.add(servlet.sources.get(srcID));
				//newQuery.setFinalResolution(servlet.getFinalParamFromSrc(srcID));
			}
		}
		
		String dataDuration = query.get("dataDuration").getAsString().trim();
		String durationNorm = query.get("durationNorm").getAsString().toLowerCase();
		String valueNorm = query.get("valueNorm").getAsString().toLowerCase();
		String patternSrc = query.get("patternSrc").getAsString().toLowerCase();
		
		String tpMatchingCC = null;
		String pattern = null;
		String samplingRate = query.get("patternSamplingRate").getAsString().trim();
		String patternDuration = query.get("patternDuration").getAsString().trim();
		String patternType = query.get("patternType").getAsString().trim().toLowerCase();
		
		if(patternSrc.equals("file"))
		{
			String filePath = "\"" + query.get("filePath").getAsString().replace("\\", "\\\\") + "\"";
			pattern = "\tInputTemporalPatternTemplate tpPattern" + tpPatternID + "(" 
					+ filePath + ");\n";
		}
		else if(patternSrc.equals("create"))
		{		
			if(patternType.equals("linear"))
			{
				JsonObject linear = query.get("linearParam").getAsJsonObject();
				String slope = linear.get("slope").getAsString();
				String intercept = linear.get("yIntercept").getAsString();
				
				pattern = "\tLinearTemporalPatternTemplate tpPattern" + tpPatternID + "(" +
						slope + ", " + intercept + ", " + samplingRate + "*1000, " + patternDuration + 
						"*1000);\n";
			}
			else if(patternType.equals("exponential"))
			{
				JsonObject exp = query.get("expParam").getAsJsonObject();
				String base = exp.get("base").getAsString();
				String scale = exp.get("scale").getAsString();
				
				pattern = "\tExponentialTemporalPatternTemplate tpPattern" + tpPatternID + "(" +
						base + ", " + scale + ", " + samplingRate + "*1000, " + patternDuration + 
						"*1000);\n";
			}
			else if(patternType.equals("periodic"))
			{
				JsonObject periodic = query.get("periodicParam").getAsJsonObject();
				String frequency = periodic.get("frequency").getAsString();
				String amplitude = periodic.get("amplitude").getAsString();
				String phaseDelay = periodic.get("phaseDelay").getAsString();
				
				pattern = "\tPeriodicTemporalPatternTemplate tpPattern" + tpPatternID + "(" +
						frequency + ", " + amplitude + ", " + phaseDelay + ", " + samplingRate + "*1000, " +
						patternDuration + "*1000);\n";
			}
		}
		
		tpMatchingCC = pattern;
		tpMatchingCC += "\tTemporalPatternMatching " + opVar + "(" + dataSrc + ", tpo_" + patternType.toUpperCase() +
				", " + valueNorm + ", " + durationNorm + ", " + dataDuration + "*1000, &tpPattern" + tpPatternID + ");\n\n";
		tpPatternID++;
		
		return tpMatchingCC;
	}
	
	
	public static void main(String[] args)
	{
		RegisterServlet servlet = new RegisterServlet();
		servlet.init();
		QueryJSONParser parser = new QueryJSONParser(servlet);
		System.out.println(servlet.sources.size());
		Iterator<String> key = servlet.sources.keySet().iterator();
		while(key.hasNext()){
			String id = key.next();
			System.out.println("id: " + id + "\n" + servlet.sources.get(id).toString() + "\n-----------------------");
		}
		QueryListDAO qdao = new QueryListDAO();
		QueryProcess q = parser.parseQuery(qdao.getQueryTree(9), qdao.getFrameParameterQry(9));
		q.build();
		
		new Thread(q).start();
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		q.stop();
	}

	
}
