<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>PMAY</title>
</head>
<body>
     <div class="row">
       <div class="col-lg-3 col-xs-6">
         <div class="small-box bg-aqua">
           <div class="inner">
             <h3>${total}</h3>
             <p>Total Documents Verified</p>
           </div>
           <div class="icon">
             <i class="ion ion-bag"></i>
           </div>
         </div>
       </div><!-- ./col -->
       <div class="col-lg-3 col-xs-6">
         <div class="small-box bg-green">
           <div class="inner">
             <h3>${total_success}</h3>
             <p>Successful</p>
           </div>
           <div class="icon">
             <i class="ion ion-stats-bars"></i>
           </div>
         </div>
       </div><!-- ./col -->
       <div class="col-lg-3 col-xs-6">
         <div class="small-box bg-red">
           <div class="inner">
             <h3>${total_failed}</h3>
             <p>Total Failed</p>
           </div>
           <div class="icon">
             <i class="ion ion-person-add"></i>
           </div>
         </div>
       </div>
     </div>
</body>
</html>