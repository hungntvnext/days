package com.mydomain.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.Connection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;


/**
 * Dummy test class
 * 
 * @author Aykut
 * 
 */

@Controller
@RequestMapping("/dummy")
public class DummyController {

    @RequestMapping(method = RequestMethod.GET)
  
    public String test() {
  
        return "index";
    }
    //------------
    private ArrayList<ObjectData> mytest(){
    	  Connection con = null;
          PreparedStatement pst = null;
          ResultSet rs = null;
          ArrayList<ObjectData> list= new ArrayList<>();
          try {
              
              con = Test.ConnectDatabase();
              pst = con.prepareStatement("SELECT * FROM gebruiker");
              rs = pst.executeQuery();
ObjectData obj;
              while (rs.next()) {
            	  obj = new ObjectData();
            	  obj.setId(rs.getInt(1));
            	  obj.setPuik_id(rs.getString(3));
            	  obj.setDisplaynaam(rs.getString(2));
                  list.add(obj);
              }

          } catch (SQLException ex) {
                

          } finally {

              try {
                  if (rs != null) {
                      rs.close();
                  }
                  if (pst != null) {
                      pst.close();
                  }
                  if (con != null) {
                      con.close();
                  }

              } catch (SQLException ex) {
                
              }
          }
      return list;
    }
    //-------------
    private void exportPDF(String json,Map<String, Object> parameters,HttpServletRequest request,
			HttpServletResponse response) throws Exception{
    	
    	InputStream stream = new ByteArrayInputStream(json.getBytes("UTF-8"));
		JsonDataSource xx= new JsonDataSource(stream);    	    	
		JasperPrint jasperPrint = JasperFillManager.fillReport("D:\\day5\\test.jasper",
				parameters, xx);
		if (jasperPrint != null) {
			byte[] pdfReport = JasperExportManager
					.exportReportToPdf(jasperPrint);
			response.reset();
			response.setContentType("application/pdf");
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Content-Disposition", "attachment; filename=your-file.pdf");
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-store");
			response.setContentLength(pdfReport.length);
			response.getOutputStream().write(pdfReport);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
    }
    //--------------
    protected void sendPdfFile(HttpServletRequest request,
            HttpServletResponse response, String fname)
            throws ServletException, IOException {
        
        File file = new File(fname);
        byte[] b = new byte[(int) (file.length())];                
            response.setHeader("Content-Disposition", "attachment; filename=\""
                    + fname + "\"");
            response.setContentType("application/pdf");
            
        response.setContentLength((int) (file.length()));
        
        ServletOutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(file);
        in.read(b);
        in.close();
        out.write(b);
        file.delete();
    
    }
    public static void WriteFile(File file, String filePath)
	{
		try {
			File fileRenameTo = new File(filePath);
			file.renameTo(fileRenameTo);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
	
		}
	}
    	File CallRestWithUrl( String requestBody) {
    		try {
    			String urlRest="http://localhost:8080/jasperserver/rest/pdf/Onlytest";
    			HttpClient httpClient = new DefaultHttpClient();
    			HttpPost postRequest = new HttpPost(urlRest);

    			StringEntity input = new StringEntity(requestBody, "UTF-8");
    			input.setContentType("application/json");
    			postRequest.setEntity(input);

    			HttpResponse response = httpClient.execute(postRequest);

    		
    			//----------------
    		
    			//----------------
    			if (response.getStatusLine().getStatusCode() != 200) {
    				throw new RuntimeException("Failed : HTTP error code : "
    						+ response.getStatusLine().getStatusCode());
    			}
    			
    			BufferedHttpEntity buf = new BufferedHttpEntity(response.getEntity());
    			
    			InputStream is = buf.getContent();

    			final File tempFile = File.createTempFile("sheet", ".tmp");
    			tempFile.deleteOnExit();
    			try (FileOutputStream os = new FileOutputStream(tempFile)) {
    				IOUtils.copy(is, os);
    			}
    			try {
    				return tempFile;
    			} finally {
    				is.close();
    				httpClient.getConnectionManager().shutdown();
    			}

    		} catch (MalformedURLException e) {
    			
    		} catch (IOException e) {
    			
    		}

    		return null;
    	}
    
    //-------------
    	void Onlytest(HttpServletRequest request,
    			HttpServletResponse response)throws Exception{
    		 String str = FileUtils.readFileToString(new File("D://day5/data.txt"));
    		 System.err.println(str+" =My data");
    	File file=	 CallRestWithUrl(str);
    	WriteFile(file,"D:/1.pdf");
    	sendPdfFile(request, response, "D:/1.pdf");
    	}
        //-------------
    @RequestMapping(value = "/jasper")
    public void generatePDFJasperChart(HttpServletRequest request,
    			HttpServletResponse response) throws IOException {
     
    	
    	
    	Map<String, Object> parameters = new HashMap<String, Object>();
    	parameters.put("ReportTitle", "Jasper Demo");
    	parameters.put("Author", "Prepared By jCombat");
    	try {
    		
    		ArrayList<ObjectData> dataList = mytest();
//    		/ create a new Gson instance
    		 Gson gson = new Gson();
    		 // convert your list to json
    		 String jsonCartList = gson.toJson(dataList);
    		 System.err.println(jsonCartList);
    		// exportPDF(jsonCartList, parameters, request, response);
    		// CallRestWithUrl(jsonCartList);
    		 Onlytest(request,response);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    @RequestMapping(value = "/jasper1")
    public void generatePDFJasperChart1(HttpServletRequest request,
    			HttpServletResponse response) throws IOException {
     
    	
    	
    	Map<String, Object> parameters = new HashMap<String, Object>();
    	parameters.put("ReportTitle", "Jasper Demo");
    	parameters.put("Author", "Prepared By jCombat");
    	try {
    		
    		ArrayList<ObjectData> dataList = mytest();
//    		/ create a new Gson instance
    		 Gson gson = new Gson();
    		 // convert your list to json
    		 String jsonCartList = gson.toJson(dataList);
    		 System.err.println(jsonCartList);
    		exportPDF(jsonCartList, parameters, request, response);

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
