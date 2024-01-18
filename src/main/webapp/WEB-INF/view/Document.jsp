<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style>
.loader {
  border: 10px solid #f3f3f3;
  border-radius: 50%;
  border-top: 10px solid blue;
  border-bottom: 10px solid blue;
  width: 40px;
  height: 40px;
  -webkit-animation: spin 2s linear infinite;
  animation: spin 2s linear infinite;
}

@-webkit-keyframes spin {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
</head>
<body>
	<div class="center row">
		<div class="col-md-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">Document Verification</h3>
				</div>
				<form:form method="post" action="document">
					<div class="box-body">
						<div class="row">
							<div class="form-group col-xs-4">
								<label>Document Type</label> 	
								<select class="form-control" id="docTypeId" name="docTypeId">
									<option value="">Select</option>
									<c:forEach var="doc" items="${lstDocumentType}">
										<option value="${doc.id}">${doc.sysName}</option>
									</c:forEach>
								</select>
							</div>
							<div class="form-group col-xs-4">
								<label>Document Number</label> <input type="text" id="docNumber"
									class="form-control" placeholder="Document Number"
									required="required" />
							</div>
						</div>
						<div class="row">
							<div class="form-group col-xs-1">
								<button type="button" id="btnVerify" class="btn btn-primary">Verify</button>
							</div>
							<div class="form-group col-xs-4">
								<div class="loader" id="loader" style="display: none;"></div>
							</div>
						</div>
					</div>
				</form:form>
			</div>
		</div>
		<div class="col-md-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">Document Details</h3>
				</div>
				<div class="box-body">
					<div class="row">
						<div class="form-group col-xs-1">
							<i  class="fa fa-check-circle" id="greenCheck" style="display:none;font-size:48px;color:green"></i>
							<i class="fa fa-times-circle-o" id="crossRed" style="display:none;font-size:48px;color:red"></i>
						</div>
						<div class="form-group col-xs-11">
							<div id="api_response">
						</div>
					</div>
					</div>
				</div>
			</div>
		</div>
	</div>
    <script src="resources/plugins/jQuery/jQuery-2.1.3.min.js"></script>
	<script type="text/javascript">
		$(function() {
		    $('#btnVerify').on('click', function() {
		    	$("#loader").show();
		    	$.ajax({
					url : 'loadDocumentDetails',
					type : 'POST',
					data : {
						docNumber : $("#docNumber").val(),
						docTypeId : $("#docTypeId").val()
					},
					success : function(response) {
						var api_response = JSON.parse(response.response);
						
						if(api_response.status_code == 200){
							$("#greenCheck").show();
							$("#crossRed").hide();
							
							$("#api_response").html(" <b> Status :"+api_response.status_code+" </b> " +
									" <br> Aadhaar Number: "+api_response.data.aadhaar_number+" " +
									" <br> State: "+api_response.data.state+" "+
									" <br> Age Range : "+api_response.data.age_range+" <br> Gender : "+api_response.data.gender+" " +
									" <br> Mobile Number:" +api_response.data.last_digits+ " " + 
									" <br> Remark : "+api_response.data.remarks+"");
						} else {
							$("#greenCheck").hide();
							$("#crossRed").show();
							
							$("#api_response").html(" <b> Status :"+api_response.status_code+" </b> " +
									" <br> Aadhaar Number: "+api_response.data.aadhaar_number+" " +
									" <br> Message: "+api_response.message+" " +
									" <br> Remark : "+api_response.data.remarks+"");
						}
						$("#loader").hide();
					}
				});
		    });
		});
	</script>
</body>
</html>