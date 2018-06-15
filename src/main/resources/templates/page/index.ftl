<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <script type="text/javascript">
        $(function(){
            $(".minute").children("option").each(function(){
                var temp_value =$(this).val();
                if(temp_value ==  $(".minuteHidden").val()){
                    $(this).attr("selected","selected");
                }
            });
            $(".second").children("option").each(function(){
                var temp_value =$(this).val();
                if(temp_value ==  $(".secondHidden").val()){
                    $(this).attr("selected","selected");
                }
            });

            $(".minute").change(function(){
                $(".minuteHidden").val($(".minute").val());
				setTime();
			});
			
			$(".second").change(function(){
                $(".secondHidden").val($(".second").val());
				setTime();
			});
			
			
			function setTime(){
				var time = getAdTime();
				if(time==0){
                    hide();
					return;
				}
				var data = {
					'time':time
				}
				$.post("/setAdTime",data,function(result){
					console.log(result);
					if(result.code!=200){
						hide();
					}else{
						hide();
					}
				});
			}
			
			function show(){
				$("#loginmodal").show();
			}
			
			function hide(){
				$("#loginmodal").hide();
			}
			
			function getAdTime(){	
				show();
				var time = parseInt($(".minute").val()) *60 + parseInt($(".second").val())
				return time;
			}
            $("#movieStart").click(function(){
                var time = getAdTime();
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
						hide();
					}else{
						alert(result.message);
						hide();
					}
				});
            });

            $("#movieclose").click(function(){
                $.post("/movieclose",{},function(result){
                    if(result.code!=200){
                        alert(result.message);
                        hide();
                    }else{
                        alert(result.message);
                        hide();
                    }
                });
            });

            $(".shutdown").click(function(){
                $.post("/shutdown",{},function(result){
                    if(result.code!=200){
                        alert(result.message);
                        hide();
                    }else{
                        alert(result.message);
                        hide();
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
        .movieclose{
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

        .shutdown{
            height:50px;
            margin-left:10px;
            font-size:30px;
        }
		
		select{
			width:100px;
			font-size:30pt;
			line-height: 50px;
		}
		
		#loginmodal {
			position: absolute;
			z-index:1000;
			top: 0px;
			left: 0px;
			height:100%;
			width:100%;
			background: lightgray;
			opacity:0.5;
		}
	</style>
</head>
<body>
<div  class="align-center">
	<h4>聚时代弹幕影厅</h4>
	<div class="adTimeDiv">
			<label>设置广告时长:</label>
			
			<!--
			<input type="input" name="minute" id="minute" class="minute" value="${minute}" maxlength="2"/>分
			<input type="input" name="second" id="second" class="second" value="${second}" maxlength="2"/>秒
			<input type="button" value="确定" id="setAdTimeButton" class="setAdTimeButton"/>
			-->

        	<input type="hidden" name="minuteHidden" id="minuteHidden" class="minuteHidden" value="${minute}" maxlength="2"/>
        	<input type="hidden" name="secondHidden" id="secondHidden" class="secondHidden" value="${second}" maxlength="2"/>
			<select class="minute">
				<option>0</option>
				<option>1</option>
				<option>2</option>
				<option>3</option>
				<option>4</option>
				<option>5</option>
				<option>6</option>
				<option>7</option>
				<option>8</option>
				<option>9</option>
				<option>10</option>
				<option>11</option>
				<option>12</option>
				<option>13</option>
				<option>14</option>
				<option>15</option>
				<option>16</option>
				<option>17</option>
				<option>18</option>
				<option>19</option>
				<option>20</option>
				<option>21</option>
				<option>22</option>
				<option>23</option>
				<option>24</option>
				<option>25</option>
				<option>26</option>
				<option>27</option>
				<option>28</option>
				<option>29</option>
				<option>30</option>
			</select>&nbsp;&nbsp;分
			&nbsp;&nbsp;&nbsp;
			<select class="second">
				<option>0</option>
				<option>1</option>
				<option>2</option>
				<option>3</option>
				<option>4</option>
				<option>5</option>
				<option>6</option>
				<option>7</option>
				<option>8</option>
				<option>9</option>
				<option>10</option>
				<option>11</option>
				<option>12</option>
				<option>13</option>
				<option>14</option>
				<option>15</option>
				<option>16</option>
				<option>17</option>
				<option>18</option>
				<option>19</option>
				<option>20</option>
				<option>21</option>
				<option>22</option>
				<option>23</option>
				<option>24</option>
				<option>25</option>
				<option>26</option>
				<option>27</option>
				<option>28</option>
				<option>29</option>
				<option>30</option>
				<option>31</option>
				<option>32</option>
				<option>33</option>
				<option>34</option>
				<option>35</option>
				<option>36</option>
				<option>37</option>
				<option>38</option>
				<option>39</option>
				<option>40</option>
				<option>41</option>
				<option>42</option>
				<option>43</option>
				<option>44</option>
				<option>45</option>
				<option>46</option>
				<option>47</option>
				<option>48</option>
				<option>49</option>
				<option>50</option>
				<option>51</option>
				<option>52</option>
				<option>53</option>
				<option>54</option>
				<option>55</option>
				<option>56</option>
				<option>57</option>
				<option>58</option>
				<option>59</option>
			</select>&nbsp;&nbsp;秒
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
			</#list>
			</select>
             <input type="button" value="电影开始" class="movieStart" name="movieStart" id="movieStart"/>
             <input type="button" value="结束" class="movieclose" name="movieclose" id="movieclose"/>
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
<div style="margin-top: 10px;">
    <input type="button" value="关机" class="shutdown" name="shutdown" id="shutdown"/>
</div>
<div id="loginmodal" style="display:none;">
  
</div>

</body>
</html>

