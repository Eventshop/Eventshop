package com.eventshop.eventshoplinux.util.datasourceUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eventshop.eventshoplinux.domain.datasource.DataSource;
import com.eventshop.eventshoplinux.domain.datasource.DataSource.DataFormat;
import com.eventshop.eventshoplinux.domain.datasource.emage.STMerger;
import com.eventshop.eventshoplinux.domain.datasource.emage.ResolutionMapper.SpatialMapper;
import com.eventshop.eventshoplinux.domain.datasource.emage.ResolutionMapper.TemporalMapper;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.CSVEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.EmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.STTEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.VisualEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.simulator.DistParameters;
import com.eventshop.eventshoplinux.domain.datasource.simulator.GaussianParameters2D;
import com.eventshop.eventshoplinux.util.commonUtil.Config;
import com.eventshop.eventshoplinux.util.datasourceUtil.simulator.Simulator.Kernel;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.FlickrWrapper;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.SimDataWrapper;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.TwitterWrapper;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.Wrapper;

public class DataSourceParser {
	
	protected Log log=LogFactory.getLog(this.getClass().getName());
	String context;
	String tempDir;
	
	public DataSourceParser()
	{
	context = Config.getProperty("context");
	tempDir = Config.getProperty("tempDir");
	}
	
//	public void init()
//	{
//	context = Config.getProperty("context");
//	tempDir = Config.getProperty("tempDir");
//	}
	
	
	public DataProcess processData(DataSource src)
	{
		
		DataProcess process = null;
		
		DataFormat format = src.srcFormat;		
		String imgBasePath = context + "results/ds/";		
		if(format == DataFormat.stream)
		{			
			Wrapper wrapper = null;
			if(src.supportedWrapper.equalsIgnoreCase("Twitter"))
			{				
				log.info("before creation");
				wrapper = new TwitterWrapper(src.url, src.srcTheme, src.initParam, true);
				log.info("wrapper created");
				
				String[] words = new String[src.bagOfWords.size()];
				src.bagOfWords.toArray(words);
				((TwitterWrapper) wrapper).setBagOfWords(words);
			}
			else if(src.supportedWrapper.equalsIgnoreCase("Flickr"))
			{
				wrapper = new FlickrWrapper(src.url, src.srcTheme, src.initParam);
				String[] words = new String[src.bagOfWords.size()];
				src.bagOfWords.toArray(words);
				((FlickrWrapper) wrapper).setBagOfWords(words);
			}
			else if(src.supportedWrapper.equalsIgnoreCase("sim"))
			{
				// Create Distribution Parameters and Generators
				ArrayList<DistParameters> dParams = new ArrayList<DistParameters>();
				// LA
				GaussianParameters2D gParam = new GaussianParameters2D(34.1, -118.2, 3.0, 3.0, 200);
				dParams.add(gParam);
				// SF
				gParam = new GaussianParameters2D(37.8, -122.4, 2.0, 2.0, 200);
				dParams.add(gParam);
				// Seattle
				gParam = new GaussianParameters2D(47.6, -122.3, 1.0, 1.0, 100);
				dParams.add(gParam);
				// NYC
				gParam = new GaussianParameters2D(40.8, -74.0, 5.0, 5.0, 200);
				dParams.add(gParam);

				// Wrappers
				wrapper = new SimDataWrapper(src.url, src.srcTheme, src.initParam, Kernel.gaussian, dParams);//changed to initParam...vks:Aug30,2011
			}
			
			// Add EmageIterator
			EmageIterator eit = new STTEmageIterator();			
			eit.setSTTPointIterator(wrapper);			
			eit.setSrcID(Long.parseLong(src.srcID));
			
			// Add output filename

			// change it to be query independent: 08/19/2011 Mingyan
			String filepath = tempDir + "/ds" + src.srcID;// + "_" + src.srcName;
			log.info("file path: " + filepath);
			// Create st merger
			STMerger merger = null;
			//finalParam not used
			/*if(!src.finalParam.equals(src.initParam))
			{
				log.info("The initial and final frame parameters are different");
				
				merger = new STMerger(src.finalParam);
				SpatialMapper sp = null;
				TemporalMapper tp = null;
				if(src.initParam.latUnit < src.finalParam.latUnit 
						|| src.initParam.longUnit < src.finalParam.longUnit)
					sp = SpatialMapper.sum;
				else if(src.initParam.latUnit > src.finalParam.latUnit 
						|| src.initParam.longUnit > src.finalParam.longUnit)
					sp = SpatialMapper.repeat;
				
				if(src.initParam.timeWindow < src.finalParam.timeWindow)
					tp = TemporalMapper.sum;
				else if(src.initParam.timeWindow > src.finalParam.timeWindow)
					tp = TemporalMapper.repeat;
				
				merger.addIterator(eit, sp, tp);
				merger.setMergingExpression("mulED(R0,1)");
			}*/
			// Add data processors
			process = new DataProcess(merger, eit, wrapper, filepath, imgBasePath+"ds"+src.srcID, "ds"+src.srcID);
			
			
			// Start the data collecting process
			new Thread(process).start();
		}
		else if(format == DataFormat.visual)
		{			
			try {
				
				System.out.println("----visual param not getting passed "+ src.visualParam.tranMatPath);
			
				double tranMat[][]=parsefileToMatrix(src.visualParam.tranMatPath);
				
				double colorMat[][]=parsefileToMatrix(src.visualParam.colorMatPath);
				
				VisualEmageIterator veit = new VisualEmageIterator(src.initParam, src.srcTheme, src.url, tranMat, 
						colorMat, src.visualParam.maskPath, src.visualParam.ignoreSinceNumber);
				veit.setSrcID(Integer.parseInt(src.srcID));
				
				// Add output filename
				//Change it to be query independent: 08/19/2011 Mingyan
				String filepath = tempDir + "/ds" + src.srcID;// + "_" + src.srcName;
				
				// Create st merger
				STMerger merger = null;
				//finalParam not used
				/*if(!src.finalParam.equals(src.initParam))
				{
					merger = new STMerger(src.finalParam);
					SpatialMapper sp = null;
					TemporalMapper tp = null;
					if(src.initParam.latUnit < src.finalParam.latUnit 
							|| src.initParam.longUnit < src.finalParam.longUnit)
						sp = SpatialMapper.average;
					else if(src.initParam.latUnit > src.finalParam.latUnit 
							|| src.initParam.longUnit > src.finalParam.longUnit)
						sp = SpatialMapper.repeat;
					
					if(src.initParam.timeWindow < src.finalParam.timeWindow)
						tp = TemporalMapper.sum;
					else if(src.initParam.timeWindow > src.finalParam.timeWindow)
						tp = TemporalMapper.repeat;
					
					merger.addIterator(veit, sp, tp);
					merger.setMergingExpression("mulED(R0,1)");
				}*/
								
				// Add data processors
				process = new DataProcess(merger, veit, null, filepath, imgBasePath+"ds"+src.srcID, src.srcID);
			
				
				// Start the data collecting process
				new Thread(process).start();
			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			} 
		}
		else if(format == DataFormat.csv)
		{			
			String filepath = tempDir + "/ds" + src.srcID;	// + "_" + src.srcName;
			CSVEmageIterator csvEIter = new CSVEmageIterator(src.initParam, src.srcTheme, src.url);
			csvEIter.setSrcID(Long.parseLong(src.srcID));
			
			// Create st merger
			STMerger merger = null;
			//finalParam not used
			/*if(!src.finalParam.equals(src.initParam))
			{
				merger = new STMerger(src.finalParam);
				SpatialMapper sp = null;
				TemporalMapper tp = null;
				if(src.initParam.latUnit < src.finalParam.latUnit 
						|| src.initParam.longUnit < src.finalParam.longUnit)
					sp = SpatialMapper.average;
				else if(src.initParam.latUnit > src.finalParam.latUnit 
						|| src.initParam.longUnit > src.finalParam.longUnit)
					sp = SpatialMapper.repeat;
				
				if(src.initParam.timeWindow < src.finalParam.timeWindow)
					tp = TemporalMapper.sum;
				else if(src.initParam.timeWindow > src.finalParam.timeWindow)
					tp = TemporalMapper.repeat;
				
				merger.addIterator(csvEIter, sp, tp);
				merger.setMergingExpression("mulED(R0,1)");
			}*/
						
			process = new DataProcess(merger, csvEIter, null, filepath, imgBasePath+"ds"+src.srcID, src.srcID);
			
			
			// Start the data collecting process
			new Thread(process).start();
		}
		return process;
	}
	
	public double[][] parsefileToMatrix(String filePath)
	{
		
		double[][] matrix=null;
		try
		{
			
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			String[] dim = line.split(",");
			matrix = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
			int row = 0;
			String matrixStr = "tranMat:\n ";
			while((line = reader.readLine()) != null)
			{
				String[] numbers = line.split(",");
				for(int col = 0; col < numbers.length; ++col)
					matrix[row][col] = Double.parseDouble(numbers[col].trim());
				row++;
			}
			reader.close();
			
			
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	
	return matrix;
	}
	
	

}
