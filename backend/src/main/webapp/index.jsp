<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<html>
    <head>
        <title>Upload Test</title>
    </head>
    <body>
        <form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">

           Application Id:  <input type="text" name="appid"/></br>
            Application Name: <input type="text" name="appname"/>
            </br>
            Package Name: <input type="text" name="packagename"/>
            </br>
            Choose App Icon:<input type="file" name="icon">
            </br>
             Choose App banner:<input type="file" name="banner">
                                                     </br></br>
           IsNew: <input type="text" name="new"/>
                       </br>
           Grid Ad Count:  <input type="text" name="gridcount"/></br>

           </br>
           </br>

           set
           0= in active
           1= static ad (you need to add data for static in other table)
           2= dynamic ad

           </br>

           bannerType:  <input type="text" name="bannerType"/></br>
           interstialType:  <input type="text" name="interstialType"/></br>
           gridType:  <input type="text" name="gridType"/></br>
           <input type="submit" value="Submit">
        </form>
    </body>
</html>
