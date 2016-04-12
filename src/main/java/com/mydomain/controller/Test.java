package com.mydomain.controller;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
 
import java.sql.Connection;
import java.sql.DriverManager;
 
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
 
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
 
/**
 * The Class Test.
 */
public class Test {
 
    /** The Constant LOG. */
    private static final Logger LOG = Logger.getLogger(Test.class);
     
    /** The Constant BUFFER. */
    final static int BUFFER = 10240;
     
    /** The connection. */
    static Connection connection = null;
 
    /**
     * Connect database.
     */
    static Connection ConnectDatabase() {
        try {           
            // Load the JDBC driver
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
 
            // Create a connection to the database
            
            String url = "jdbc:mysql://localhost:3306/test";
            String username = "root";
            String password = "admin123";
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find the database driver");
        } catch (Exception e) {
            System.err.println("Could not connect to the database");
        }
        return connection;
    }
 
    /**
     * File zip.
     */
    static void fileZip() {
         
        BufferedInputStream origin = null;
        try
        {
            FileOutputStream dest = new FileOutputStream("test.zip");
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];
             
            // get a list of files from current directory
            File f = new File("src/.");
            String files[] = f.list();
 
            for (int i=0; i<files.length; i++) {
             
                System.out.println("Adding: "+files[i]);
                FileInputStream fi = new FileInputStream("src/"+files[i]);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(files[i]);
                out.putNextEntry(entry);
                int count;
                while((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      
    /**
     * The main method.
     * 
     * @param args the arguments
     */
    public static void main1111() {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
         
        long start = System.currentTimeMillis();
         
        try {           
            // Log log4j configuration
            final Properties log4jProperties = new Properties();
            log4jProperties.load(new FileInputStream("D:/log4j.properties"));
            PropertyConfigurator.configure(log4jProperties);
             
            LOG.info("Start");
            LOG.info("--------");
                         
            LOG.info("Compile Jasper XML Report");
            jasperReport = JasperCompileManager.compileReport("D:/test.jrxml");
            LOG.info("time : " + (System.currentTimeMillis() - start)+ " ms.");
             
            LOG.info("Create Database connection");
            ConnectDatabase();
            LOG.info("time : " + (System.currentTimeMillis() - start)+ " ms.");
             
            LOG.info("Create parameters");
            Map <String, Object> parameters = new HashMap<String, Object>();
            parameters.put("ReportTitle", "User Report");
            parameters.put("DataFile", "src/test.jrxml");
            parameters.put("IdRange", 100000);  
             
            LOG.info("Generated report");
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            LOG.info("time : " + (System.currentTimeMillis() - start)+ " ms.");
             
            LOG.info("Generated PDF");
            JasperExportManager.exportReportToPdfFile(jasperPrint, "D:/test.pdf");
            LOG.info("time : " + (System.currentTimeMillis() - start)+ " ms.");
             
            LOG.info("Create Zip File");
        //    fileZip();
            LOG.info("time : " + (System.currentTimeMillis() - start)+ " ms.");
             
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info("--------");
        LOG.info("Done");
    }
}