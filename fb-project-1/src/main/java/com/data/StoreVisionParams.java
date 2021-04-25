package com.data;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@WebServlet(
	    name = "HelloAppEngine",
	    urlPatterns = {"/insert"}
	    )
public class StoreVisionParams extends HttpServlet {
	
	 @Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response) 
	      throws IOException {
		 
		 String joyLikelihood = request.getParameter("joyLikelihood");
		 
		 System.out.println("joyLikelihood is: " + joyLikelihood);
		 
		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 
		 Enumeration<String> parameterNames = request.getAttributeNames();
		 Entity e = new Entity("Person");
		 
			
			/*
			 * while (parameterNames.hasMoreElements()) {
			 * 
			 * String paramName = parameterNames.nextElement();
			 * 
			 * 
			 * String[] paramValues = request.getParameterValues(paramName);
			 * System.out.println("paramValues.length" + paramValues.length); for (int i =
			 * 0; i < paramValues.length; i++) { String paramValue = paramValues[i];
			 * e.setProperty(paramName, paramValue); }
			 * 
			 * }
			 * 
			 * 
			 * Enumeration<String> names = request.getAttributeNames(); while
			 * (names.hasMoreElements()) { String name = names.nextElement(); Object value =
			 * request.getAttribute(name); e.setProperty(name, value); }
			 */
	        
	        
	        
	        e.setProperty("imageSource","https://scontent-lga3-1.xx.fbcdn.net/v/t1.0-0/p480x480/128798852_133385965226747_3890389982001678669_n.jpg?_nc_cat=110&ccb=2&_nc_sid=0be424&_nc_ohc=eC1vi062HjwAX-iaj4Q&_nc_oc=AQkM6oxLGAYb5Orw5ZCuKHsIlInHMogOvfrTF4VKoAalL2z8Q5vt8CD_KXKngw_dvCg&_nc_ht=scontent-lga3-1.xx&tp=6&oh=6e138dc400320caab31ffb078aa699a3&oe=5FEA68CF"); 
	        e.setProperty("joyLikelihood", "VERY_UNLIKELY");
	        e.setProperty("sorrowLikelihood", "VERY_UNLIKELY");
	        e.setProperty("angerLikelihood", "VERY_UNLIKELY");
	        e.setProperty("surpriseLikelihood", "VERY_UNLIKELY");
	        e.setProperty("underExposedLikelihood", "VERY_UNLIKELY");
	        e.setProperty("blurredLikelihood", "VERY_UNLIKELY");
	        e.setProperty("headwearLikelihood", "VERY_UNLIKELY");
	        e.setProperty("Fashion", "0.85830715"); 
	        e.setProperty("Gadget","0.72765457"); 
	        e.setProperty("Jewellery", "0.7816493");
	        e.setProperty("addedDate", "2020-29-10"); 
	      //  ds.put(e);
	        ds.put(e);
	 }

}
