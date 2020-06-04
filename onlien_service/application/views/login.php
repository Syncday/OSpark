<html>

<head>
	<meta charset="utf-8">
	<title>OSPARK 登录</title>
	<meta name="renderer" content="webkit">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<script src="static/layui.js" charset="utf-8"></script>
	<link rel="stylesheet" href="static/css/layui.css" media="all">

</head>
<style>
	.bold-font {
		font-size: 22px;
		font-weight: bold;
		color: #666;
		line-height: 50px;
		padding: 5px 20px;
		overflow: hidden;
		text-overflow: ellipsis;
		word-break: break-all;
		white-space: nowrap;
	}

</style>
<script>
	//表单提交
	layui.use("form", function () {
		var form = layui.form;

		//提交
		form.on("submit(login)", function (data) {
			var xmlHttp = new XMLHttpRequest();
			xmlHttp.open("post", "https://syncday.com/get_login");
			xmlHttp.setRequestHeader("Content-Type", "application/json");
			xmlHttp.send(JSON.stringify(data.field)); // 数据
			xmlHttp.onreadystatechange = function () {
				if (xmlHttp.readyState == 4) { //必须要等待传输完成后才操作，否则可能会有json解码错误
					if (xmlHttp.status == 200) {
						var json = JSON.parse(xmlHttp.responseText);
						layer.msg(json["info"]);
						if (json["status"] == "1") {
							//保存用户名
							sessionStorage.setItem("name",json['name']);
							window.location.replace("https://syncday.com/");
						}
					} else {
						layer.msg("出错");
					}
				}
            };
            return false;
		});
	});

</script>







<body style="background-color:#F2F2F2;">
	<div
		style="width: 500px; height: 300px; position: absolute;left: 50%;top: 50%; margin-top: -250;margin-left: -250px;">
		<div class="layui-card-header bold-font"
			style="text-align: center; background-color: #F2F2F2F2;padding-bottom: 25px;">登录 OSPARK
		</div>
		<div class="layui-card-body">
			<form class="layui-form" style="width: 300px;height: auto; margin: 0 auto; left: 50%;top: 50%;" action="">
				<div class="layui-form-item ">
					<input type="text" name="admin_name" id="admin_name" lay-verify="required" placeholder="用户名"
						class="layui-input" autocomplete="username">
				</div>
				<div class="layui-form-item ">
					<input type="password" name="admin_password" id="admin_password" lay-verify="required" placeholder="密码"
						class="layui-input" autocomplete="password">
				</div>
				<div class="layui-form-item ">
					<div style="text-align: center;">
						<button type="submit" style="width: 100%;" class="layui-btn layui-btn-radius " lay-submit=""
							lay-filter="login">登录</button>
					</div>
				</div>
			</form>
		</div>
	</div>

	<div style="position:absolute; bottom:0px; width:100%;height:auto; text-align: center;margin-bottom: 20px;">
		<p>&copy 2020 Power by Syncday</p>
	</div>
</body>

</html>
