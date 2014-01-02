<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload Video</title>
<script type='text/javascript'>
	var _validFileExtensions = [".webM", ".mp4", ".flv"];
	function validateFile(form) {
		
		var arrInputs = form.getElementsByTagName("input");
		for (var i = 0; i < arrInputs.length; i++) {
	        var oInput = arrInputs[i];
	        if (oInput.type == "file") {
	        	 var sFileName = oInput.value;
	             if (sFileName.length > 0) {
	                 var blnValid = false;
	                 for (var j = 0; j < _validFileExtensions.length; j++) {
	                     var sCurExtension = _validFileExtensions[j];
	                     if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() == sCurExtension.toLowerCase()) {
	                         blnValid = true;
	                         break;
	                     }
	                 }

	                 if (!blnValid) {
	                	 document.getElementById('valid_msg').innerHTML = 
	                		 "<h3 style=\"color: red;\">Sorry, " + sFileName + 
	                		 " is invalid, Allowed extensions are: " + _validFileExtensions.join(", ") + "</h3>";
	                     return false;
	                 }
	  
			} else {
				document.getElementById('valid_msg').innerHTML = "<h3 style=\"color: red;\">Please select a file first</h3>";
					return false;
				}

			}
		}
	}
</script>
</head>
<body>
	<div>
		<h3 style="color: blue;">Select the video to upload</h3>
		<form action="upload" method="post" enctype="multipart/form-data" onsubmit="return validateFile(this);">
			<input type="file" name="file" /> <input type="submit" value="Upload" />			
		</form>
		<div id="valid_msg"></div>
	</div>
</body>
</html>