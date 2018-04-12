<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <script type="text/javascript">
        $(function(){
			
			$(".minute").blur(function(){
				var minute = $("#minute").val();
				if(minute==null || minute==""){
					$("#minute").val('0');
					return;
				}
				if (!isNumber(minute)){
					alert('分钟必须为数字');
					return ;
				}
			});
			
			$(".second").blur(function(){
				var second = $("#second").val();
				if(second==null || second==""){
					$("#second").val('0');
					return;
				}
				if(second>59 || second<0){
					alert('秒请设置在0-59');
					return;
				}
				
				if (!isNumber(second)){
					$("#second").focus();
					return ;
				}
			});

            $("#setAdTimeButton").click(function(){
				
				var minute = $("#minute").val();
				var second = $("#second").val();
				if (!isNumber(minute)){
					alert('分钟必须为数字');
					return ;
				}
				if (!isNumber(second)){
					alert('秒必须为数字');
					return ;
				}
                if(second>59 || second<0){
                    alert('秒请设置在0-59');
                    $("#second").val('');
                    return;
                }
				var time = parseInt(minute) *60 + parseInt(second)
				if(time==0){
					alert('请设置广告时间！');
					return;
				}
				var data = {
					'time':time
				}
                $.post("/setAdTime",data,function(result){
					//$("span").html(result);
					console.log(result);
					if(result.code!=200){
					    alert(result.message);
					}else{
						alert(result.message);
					}
				});
            });

            $("#movieStart").click(function(){
                var minute = $("#minute").val();
                var second = $("#second").val();
                if (!isNumber(minute)){
                    alert('分钟必须为数字');
                    return ;
                }
                if (!isNumber(second)){
                    alert('秒必须为数字');
                    return ;
                }
                if(second>59 || second<0){
                    alert('秒请设置在0-59');
                    $("#second").val('');
                    return;
                }
                var time = parseInt(minute) *60 + parseInt(second)
                if(time==0){
                    alert('请设置广告时间！');
                    return;
                }
				var data = {
					'command':$(".partyList").val(),
					'time':time
				}
                $.post("/movieStart",data,function(result){
					if(result.code!=200){
					    alert(result.message);
					}else{
						alert(result.message);
					}
				});
            });
        });
		
		function isNumber(value) {
			var patrn = /^[0-9]*$/;
			if (patrn.exec(value) == null || value == "") {
				return false
			} else {
				return true
			}
		}
    </script>
	
	<style type="text/css">
		.align-center{ 
			margin:0 auto; /* 居中 这个是必须的，，其它的属性非必须 */ 
			width:800px; /* 给个宽度 顶到浏览器的两边就看不出居中效果了 */ 
			text-align:center; /* 文字等内容居中 */ 
		} 
		
		.adTimeDiv{
			display: -webkit-flex;
			display: flex;
			-webkit-align-items: center;
            align-items: center;
			-webkit-justify-content: center;
            justify-content: center;
			margin-top:50px;
		}
		.minute{
			height:50px;
			width:60px;
			font-size:50px;
		}
		.second{
			height:50px;
			width:60px;
			font-size:50px;
		}
		.setAdTimeButton{
			margin-left:10px;
			height:50px;
			width:80px;
			font-size:30px;
		}
		
		.movieSetDiv{
			display: -webkit-flex;
			display: flex;
			-webkit-align-items: center;
            align-items: center;
			-webkit-justify-content: center;
            justify-content: center;
			margin-top:150px;
		}
		.partyList {
			width:350px;
			font-size:30px;
			height:60px;
		}
		
		.movieStart{
			height:50px;
			margin-left:10px;
			font-size:30px;
		}
		.link{
			margin-top:150px;
		}
		.noMovie{
			font-size:30px;
		}
	</style>
</head>
<body>
<div  class="align-center">
	<h4>聚时代弹幕影厅</h4>
	<div class="adTimeDiv">
			<label>设置广告时长:</label>
			<input type="input" name="minute" id="minute" class="minute" value="${minute}" maxlength="2"/>分
			<input type="input" name="second" id="second" class="second" value="${second}" maxlength="2"/>秒
			<input type="button" value="确定" id="setAdTimeButton" class="setAdTimeButton"/>
	</div>
	<br/>
	<div class="movieSetDiv">
		
		 <#if partyList?? && (partyList?size>0)>
			<select class="partyList">
			<#list partyList as party>
				<#if command == '${party.movieAlias}' && party.movieAlias??>  
					<option value="${party.movieAlias}" selected="selected">${party.name}</option>
				<#else>
					<option value="${party.movieAlias}">${party.name}</option>
				</#if>
				<input type="button" value="电影开始" class="movieStart" name="movieStart" id="movieStart"/>
			</#list>
			</select>
		<#else>
			<div class="noMovie">
				你好,目前没有电影!!!!
			</div>
		</#if>
		
	</div>

	<div class="link">
		技术联系人：徐亚迪(13811336894)
	</div>
</div>
</body>
</html>

