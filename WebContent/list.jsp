<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="tube.MySqlAPI"%>
<%@ page import="tube.VideoVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<script type='text/javascript'	src='https://d192q0b6oqt9a5.cloudfront.net/jwplayer.js'></script>

<script>
var _currentVideoName;
var _currentVideoVotes;
var _currentVideoRating;
var _currentVideoTime;
function bigImg(x)
{
x.style.height="70px";
x.style.width="100px";
}

function normalImg(x)
{
x.style.height="60px";
x.style.width="85px";
}

function linkNormalImg(x)
{
x.style.height="60px";
x.style.width="60px";
}

function uploadPopUpOpen()
{
	var w = 400;
	var h = 200; 
	var left = (screen.width/2)-(w/2);
	var top = (screen.height/2)-(h/2);	  
    var popup = window.open("upload.jsp","uploadScreen",'toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width='+w+', height='+h+', top='+top+', left='+left);
    // Set interval to check if wheter it has been closed
    var windowCheckInterval = setInterval(function() { windowCheck(); }, 300);

    // Check function
    function windowCheck() {
        if (!popup || popup.closed) {
            clearInterval(windowCheckInterval);
			location.reload();
        }
    }
}
	function loadVideo(videoFile, rating, votes, time) {
		_currentVideoName = videoFile;
		_currentVideoRating = rating;
		_currentVideoVotes = votes;
		_currentVideoTime = time;
		var y = 0.5 * screen.height;
		var x = 0.5 * screen.width;			
		jwplayer('mediaplayer').setup({
		      file: videoFile,
		      autostart: true,
		      width: x,
		      height: y
		   });
		
		document.getElementById("Rating").style.display= "block";
		document.getElementById("radioGroup").style.display= "block";
		document.getElementById("deleteGroup").style.display= "block";
		
		color = "red";
		if(_currentVideoRating > 3)
			{
			  color = "green";
			}
		if(_currentVideoRating == 3)
		{
		  color = "orange";
		}
		document.getElementById("votesText").innerHTML =  "<font color=\""+color+"\" size=\"4\">"+ _currentVideoVotes +" users rated this video</font> </br> </br>";
		document.getElementById("uploadTime").innerHTML =  "<font color=\"black\" size=\"4\"> Uploaded on "+ _currentVideoTime +"</font> </br> </br>";
		document.getElementById("uploadTime").style.display= "block";
		document.getElementById("votesText").style.display= "block";
		
		if (rating==1)
			{
			radiobtn = document.getElementById("radio1");
			radiobtn.checked = true;
			}
		if (rating==2)
		{
		radiobtn = document.getElementById("radio2");
		radiobtn.checked = true;
		}
		if (rating==3)
		{
		radiobtn = document.getElementById("radio3");
		radiobtn.checked = true;
		}
		if (rating==4)
		{
		radiobtn = document.getElementById("radio4");
		radiobtn.checked = true;
		}
		if (rating==5)
		{
		radiobtn = document.getElementById("radio5");
		radiobtn.checked = true;
		}
		
	}
	
	function updateRating()
	{
		var rating = 0;
		radiobtn = document.getElementById("radio1");
		if(radiobtn.checked)
			rating=1;
		radiobtn = document.getElementById("radio2");
		if(radiobtn.checked)
			rating=2;
		radiobtn = document.getElementById("radio3");
		if(radiobtn.checked)
			rating=3
		radiobtn = document.getElementById("radio4");
		if(radiobtn.checked)
			rating=4;
		radiobtn = document.getElementById("radio5");
		if(radiobtn.checked)
			rating=5;
		document.radioform.action = "rating?videoname="+_currentVideoName+"&rating="+rating;     
		document.radioform.submit();
	}
	
	function deleteVideo()
	{
		if(_currentVideoRating > 2)
		{
			var r=confirm("The is video is highly rated!!! Sure you want to delete it ?");
			if (r==true)
		  	{
				document.deleteForm.action = "delete?videoname="+_currentVideoName;   
				//document.deleteForm.method = "POST";
				document.deleteForm.submit();
		  	}
			else
		 	 {
				return false;
		  	}
		}
		else
			{
			
			document.deleteForm.action = "delete?videoname="+_currentVideoName;     
			document.deleteForm.submit();
			}	
	}
</script>
<title>MuV - My Movie</title>
<link rel="stylesheet" href="style.css" type="text/css" />
</head>
<body>
	<div id="logo"  >
		<h1 align="center">
			<font color="black">Welcome to MuV - My Movie</font>
		</h1>
		<font size="4" color="green">Green are highly rated</font><br/><br/>
		<font size="4" color="Orange">   Orange are medium rated</font><br/><br/>
		<font size="4" color="red">   Red are low rated</font>
		
	</div>
	<div id="nav">
		<ul>
			<li class="red"><a href="/MuV/list">
			<img onmouseover="bigImg(this)" onmouseout="linkNormalImg(this)" title="Home"  src="https://s3.amazonaws.com/edu.columbia.ms.cse.2013.ab3900/home.png" height="60" width="60">
			</a></li>
			<li class="orange"><a href="" onclick="uploadPopUpOpen(); return false;">
			<img onmouseover="bigImg(this)" onmouseout="linkNormalImg(this)" title="Upload" src="https://s3.amazonaws.com/edu.columbia.ms.cse.2013.ab3900/upload.jpg" height="60" width="60">
			</a></li>
			<li class="blue"><a href="/MuV/about.html">
			<img onmouseover="bigImg(this)" onmouseout="linkNormalImg(this)" title="About Us" src="https://s3.amazonaws.com/edu.columbia.ms.cse.2013.ab3900/about.png" height="60" width="60">
			</a></li>
		</ul>
	</div>
	<%  
  	List<VideoVO> list = (List<VideoVO>)request.getAttribute("videoList");
  	if(list.isEmpty())
  	{
  		 %> <div align="center"> <h1 style="color: blue;">
			No Videos found. Upload new videos and Enjoy Streaming </h1></div><%	
	}
  	else
  	{ %>
		<table border="0" width="100%" height="100%" id="tb">
			<tr>
				<td width=25%> <div style="overflow:auto; height: 450px; max-height: 450px;">
				<h1>Video List</h1>
					<%  
  					  for(VideoVO vo : list)
	  					{ 
	  						if(vo.getRating() > 3)
	  						{
	  							%> 
		  						<div id="thumb">
		  						<img onmouseover="bigImg(this)" onmouseout="normalImg(this)" id="playJpg" 
		  						onclick="loadVideo('<%=vo.getCflink()%>',<%=vo.getRating() %>,<%=vo.getTotalvotes() %>,'<%=vo.getDate()%>' )" 
		  						src="https://s3.amazonaws.com/edu.columbia.ms.cse.2013.ab3900/play.jpg" alt="play.jpg" height="60" width="85"><br/>
	  							<font color="green" size="3"> <% out.println(vo.getName());%></font>	<br/>	<br/> </div><%
	  						}
	  						else if (vo.getRating() < 3)
	  						{
	  							%> 
		  						<div id="thumb">
		  						<img onmouseover="bigImg(this)" onmouseout="normalImg(this)" id="playJpg" 
		  						onclick="loadVideo('<%=vo.getCflink()%>',<%=vo.getRating() %>,<%=vo.getTotalvotes() %>,'<%=vo.getDate() %>'  )" 
		  						src="https://s3.amazonaws.com/edu.columbia.ms.cse.2013.ab3900/play.jpg" alt="play.jpg" height="60" width="85"><br/>
	  							<font color="red" size="3"> <% out.println(vo.getName());%></font>	<br/>	<br/> </div><%
	  						}
	  						else
	  						{
	  							%> 
		  						<div id="thumb">
		  						<img onmouseover="bigImg(this)" onmouseout="normalImg(this)" id="playJpg" 
		  						onclick="loadVideo('<%=vo.getCflink()%>',<%=vo.getRating() %>,<%=vo.getTotalvotes() %>, '<%=vo.getDate() %>' )" 
		  						src="https://s3.amazonaws.com/edu.columbia.ms.cse.2013.ab3900/play.jpg" alt="play.jpg" height="60" width="85"><br/>
	  							<font color="orange" size="3"> <% out.println(vo.getName());%></font>	<br/>	<br/> </div><%
	  						}
	  						
 	 					} 
  					%>
				</div></td>
				<td width="55%">
					<div id="mediaplayer" align="center"> <h1>Click on your favourite video....</h1></div> 
				</td>
				<td width=20%>
				<table width="100%" height="100%" align="left">
				<tr height="100%">
				<td width="100%" height="100%"><div  id="uploadTime" align="center" style="display:none">
				<font color="blue" size="4">Uploaded on </font></div>
				</td>
				</tr>
				<tr height="100%">
				<td width="100%" height="100%"><div  id="votesText" align="center" style="display:none">
				<font color="blue" size="4"> users rated this video</font> </br> </br> </div>
				</td>
				</tr>
				<tr height="100%">
				<td width="100%">
				<div  id="deleteGroup" align="center" style="display:none">
				 <font color="blue" size="5">Delete the Video</font> </br> </br>
					<form id="deleteformid" name="deleteForm" method="POST">
								  <input id="delete" type="button" value="Delete Video"  class="buttonC"  onclick="deleteVideo()">  
								 <!--<button id="deleteBtn" value="Delete Video" onclick="deleteVideo()"> </button>-->
					</form></div> </br> </br></td>
				</tr>
				
				<tr height="100%">
				<td> <div id="Rating" align="center" style="display:none"><font color="blue" size="5">Rate the Video</font> </div>
				<div id="radioGroup" align="center" style="display:none">
					<form id="radioformid" name="radioform"	action="rating" method="POST">
							<br> <input  id="radio1" type="radio" name="radiog" value="1"> 1 <br/>
								<input id="radio2" type="radio" name="radiog" value="2"> 2 <br/>
								 <input	id="radio3" type="radio" name="radiog" value="3"> 3 <br/>
								 <input	id="radio4" type="radio" name="radiog" value="4"> 4 <br/>
								 <input	id="radio5" type="radio" name="radiog" value="5"> 5 <br/>
								 <input id="radioSubmit" type="button" value="Submit" onclick="updateRating()">
					</form>
				</div>
				</tr>
				</table>
				</td>
			</tr>
		</table>		

	
	<%	
	}%>
</body>

</html>