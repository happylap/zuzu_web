package com.lap.zuzuweb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class TestDB {
	public static void main(String[] args) {
		 
         System.out.println(  "  This is a DBTest  "  );
         try
         {

     		String dbHost ="ec2-52-77-238-225.ap-southeast-1.compute.amazonaws.com";
     		dbHost = "localhost";
    		String dbPort = "5432";
    		String database = "zuzu_rentals_db";
    		String dbUsername = "zuzu";
    		String dbPassword = "ji3g4xu3ej;jo3";
    		
        	 System.out.println(  "  This is a DBTest 2 "  );
             Class.forName( "org.postgresql.Driver" ).newInstance();
             System.out.println(  "  This is a DBTest 3 "  );
             String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + database;
             url = "jdbc:postgresql://localhost:5432/zuzu_rentals_db";
             Connection con = DriverManager.getConnection(url, dbUsername , dbPassword ); //帳號密碼
             System.out.println(  "  This is a DBTest 4 "  );
             Statement st = con.createStatement();
             System.out.println(  "  This is a DBTest 5 "  );
             String sql = " select * from User" ; //SQL語法
             ResultSet rs = st.executeQuery(sql);
             int rsCount = 0;
             while (rs.next())
             {
            	 rsCount = rsCount +1;
                System.out.println(rs.getString(1)); //將結果用while印出
             }
             System.out.println(rsCount);
              rs.close();
              st.close();
              con.close();
        }
        catch (Exception ee)
        {
        	System.out.println("Exception:");
            System.out.print(ee.getMessage());
 
        }
 
    }
}

