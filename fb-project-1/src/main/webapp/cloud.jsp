
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%
BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>
<html>
<head>
    <title>File Upload Example</title>
</head>
<body>
<form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="POST" enctype="multipart/form-data">
    <p>What's your name?</p>
    <input type="text" name="name" value="Joey">
    <p>What file do you want to upload?</p>
    <input type="file" name="fileToUpload">
    <br/><br/>
    <input type="submit" value="Submit">
</form>
</body>
</html>
