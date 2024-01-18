<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User</title>
</head>
<body>
	<div class="center row">
		<div class="col-md-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">Cooperative</h3>
				</div>
				<form:form method="post" action="cooperative">
					<div class="box-body">
						<div class="row">
							<div class="form-group col-xs-4">
								<label>Registration Number</label> <input type="text" name="copRegNo"
									class="form-control" placeholder="Registration Number"
									required="required" />
							</div>
							<div class="form-group col-xs-4">
								<label>Name</label> <input type="text" name="copName"
									class="form-control" placeholder="Coopertaive Name"
									required="required" />
							</div>
							<div class="form-group col-xs-4">
								<label>Address</label> <textarea  name="copAddress" class="form-control"  required="required"> </textarea>
							</div>
						</div>
						<input type="hidden" name="copId">
						<input type="hidden" name="msg" value="${msg}">
						<button type="submit" class="btn btn-primary">Submit</button>
					</div>
					<div class="box-footer">
					</div>
				</form:form>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">Cooperatives</h3>
				</div>
				<div class="box-body">
					<table id="example1" class="table table-bordered table-striped">
						<thead>
							<tr>
								<th>Sr.No</th>
								<th>Registration Number</th>
								<th>Cooperative Name</th>
								<th>Address</th>
								<th>Active/Inactive</th>
								<th>Action</th>
							</tr>
						</thead>
						<tbody>
							<c:set var="srNo" scope="page" value="1"></c:set>
							<c:forEach var="var" items="${lstCooperative}">
								<tr>
									<td><c:out value="${srNo}"></c:out> <c:set var="srNo" value="${srNo + 1}" scope="page" /></td>
									<td>${var.copRegNo}</td>
									<td>${var.copName}</td>
									<td>${var.copAddress}</td>
									<td  align="center">
									<c:choose>
										<c:when test="${fn:containsIgnoreCase(var.isActive, 'Y')}">
											<input type="checkbox" class="mychk"  
												onchange="activeInactive(${var.copId})" checked>	
									   	</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${fn:containsIgnoreCase(var.isActive, 'N')}">
													<input type="checkbox" class=""  
													onchange="activeInactive(${var.copId})" >
												</c:when>
												<c:otherwise>
													<input type="checkbox" class=""  disabled="disabled"
														onchange="activeInactive(${var.copId})">
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
                    				</td>
									<td><a href="#" onclick="editUser(${var.copId})">
									<i class="fa fa-edit"></i></a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

    <script src="resources/plugins/jQuery/jQuery-2.1.3.min.js"></script>
    
	<script type="text/javascript">
		function editUser(copId){
			$.ajax({
				url : 'loadCooperativeDetails',
				type : 'GET',
				data : {
					copId :copId
				},
				success : function(response) {
					var model = response.copModel;
					$("[name=copAddress]").val(model.copAddress);
					$("[name=copRegNo]").val(model.copRegNo);
					$("[name=copName]").val(model.copName);
					$("[name=copId]").val(model.copId);
				}
			});
		}
		function activeInactive(copId){
			$.ajax({
				url : 'activateDeactivateCoperative',
				type : 'POST',
				data : {
					copId : copId
				},
				success : function(response) {
					alert(response.message);
				}
			});
		}
		
		$(function() {
			if($("[name=msg]").val() != ""){
				alert($("[name=msg]").val());
			}
			$('#example1').dataTable();
		});
	</script>
</body>
</html>