package com.eventshop.eventshoplinux.DAO;

import java.sql.Connection;
import java.sql.DriverManager;


import com.eventshop.eventshoplinux.util.commonUtil.Config;
import static com.eventshop.eventshoplinux.constant.Constant.*;

public class BaseDAO {
	
	
	public Connection con = connection();
	
	public Connection connection()
	{
	  try {
		  Class.forName(DRIVER_NAME);
		    String url = Config.getProperty(DB_URL);
		    String userName = Config.getProperty(USR_NAME);
		    String pwd = Config.getProperty(PASSWORD);
		    con = DriverManager.getConnection(url,userName,pwd);
	    
	  } catch(Exception e) {
		  System.out.println(" driver issues "+e);

	  }
	      return con;
	}

}
