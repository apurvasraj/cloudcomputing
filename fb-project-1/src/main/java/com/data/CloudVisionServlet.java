package com.data;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MultipartConfig
@WebServlet(name = "CloudVisionServlet", urlPatterns = { "/upload" })
public class CloudVisionServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private static  String TEST_FILENAME = "whiter.jpg";
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  	
		Map<String,String> hm = new HashMap<>();
	 	Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
		  List<BlobKey> blobKeys = blobs.get("fileToUpload");
		  
		    // Our form only contains a single file input, so get the first index.
		    BlobKey blobKey = blobKeys.get(0);
	
		    // Use ImagesService to get a URL that points to the uploaded file.
		    ImagesService imagesService = ImagesServiceFactory.getImagesService();
		    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
		    String imageUrl = imagesService.getServingUrl(options);
		    
		    System.out.println("imageUrl --> " + imageUrl);
		    
		    hm.put("imageSource", imageUrl);
		  
		  if (blobKeys == null || blobKeys.isEmpty()) { response.sendRedirect("/"); }
		  else {
		  		  
		  byte[] blobBytes = getBlobBytes(blobKeys.get(0)); 
		  List<AnnotateImageResponse> imageLabels = generateLabel(blobBytes);	  
		  
		  //List<AnnotateImageResponse> labelResponses = generateLabel(filePath); 
		  for(AnnotateImageResponse res : imageLabels) {
			  if (res.hasError()) {
			  response.getWriter().println("Error: %s%n" + res.getError().getMessage()); 
			  }
		  
			  
			  
		  for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
			  Map<Descriptors.FieldDescriptor, Object> fields = annotation.getAllFields();
			  String key ="", value = "";
				  for(Descriptors.FieldDescriptor fd: fields.keySet()){
					  
					  if(!fd.getName().contains("mid") && !fd.getName().contains("topicality")) {
						  
						  if(fd.getJsonName().equalsIgnoreCase("description")) {
							  key = fields.get(fd).toString();
						  }
						  
						  if(fd.getJsonName().equalsIgnoreCase("score")) {
							  value = fields.get(fd).toString();
						  }
						  response.getWriter().println(fd.getJsonName() + ":" +fields.get(fd).toString());
						  
						  
					
					  }
				  } 
				  hm.put(key, value);
			  }
		  request.setAttribute("data1", hm);
		
		  
		  for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
		  Map<Descriptors.FieldDescriptor, Object> fields = annotation.getAllFields();
		  for(Descriptors.FieldDescriptor fd: fields.keySet()){
		  if(!fd.getName().contains("bounding") && !fd.getName().contains("landmark"))
				  { 
			  		request.setAttribute(fd.getJsonName(), fields.get(fd).toString());
				  response.getWriter().println(fd.getJsonName() + ":" +
				  fields.get(fd).toString()); 
				  } 
		  		} 
		  }
		  
		  }
	
		    
		  RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/insert"); 
		  try { 
			  dispatcher.forward(request,response); 
			  } catch (ServletException | IOException ec) { 
			  	ec.printStackTrace(); 
			  	}
		  } 
		 
		  
	
	}

    private List<AnnotateImageResponse> generateLabel(byte[] imgBytes) throws IOException {
    	//generateLabel(String filePath) use this signature for local uplaod
    	ByteString byteString = ByteString.copyFrom(imgBytes);
    	Image img = Image.newBuilder().setContent(byteString).build();
    	
        List<AnnotateImageRequest> requests = new ArrayList<>();

        System.out.println(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));//give your path name

       
        
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        
        Feature feat2 = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
        
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);
        
        AnnotateImageRequest request2 =
                AnnotateImageRequest.newBuilder().addFeatures(feat2).setImage(img).build();
        requests.add(request2);

        try {
            ImageAnnotatorClient client = ImageAnnotatorClient.create();
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            return responses;

        } catch (Exception e){
            System.out.println(e);
        }
        return null;
    }
    
    private byte[] getBlobBytes(BlobKey blobKey) throws IOException {
 		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
 		ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

 		int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
 		long currentByteIndex = 0;
 		boolean continueReading = true;
 		while (continueReading) {
 			// end index is inclusive, so we have to subtract 1 to get fetchSize bytes
 			byte[] b = blobstoreService.fetchData(blobKey, currentByteIndex, currentByteIndex + fetchSize - 1);
 			outputBytes.write(b);

 			// if we read fewer bytes than we requested, then we reached the end
 			if (b.length < fetchSize) {
 				continueReading = false;
 			}

 			currentByteIndex += fetchSize;
 		}

 		return outputBytes.toByteArray();
 	}
    
   
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
