<html>

<head>
	<meta charset="utf-8">
	<title>OSPARK后台管理系统</title>
	<meta name="renderer" content="webkit">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<script src="static/layui.js" charset="utf-8"></script>
	<link rel="stylesheet" href="static/css/layui.css" media="all">
	<script>
		layui.use('element', function () {
			var element = layui.element; //导航的hover效果、二级菜单等功能，需要依赖element模块

			//监听导航点击
			element.on('nav(demo)', function (elem) {
				//console.log(elem)
				layer.msg(elem.text());
			});
		});

	</script>
</head>

<style type="text/css">
	p.layuiadmin-big-font {
		font-size: 36px;
		color: #666;
		line-height: 36px;
		padding: 5px 0 10px;
		overflow: hidden;
		text-overflow: ellipsis;
		word-break: break-all;
		white-space: nowrap;
	}

	.bold-font {
		font-size: 18px;
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

<body>
	<!-- 容器 -->
	<div class="layui-layout layui-layout-admin">

		<!-- 头部 -->
		<div class="layui-header header header-doc">
			<ul class="layui-nav" style="left:0;position:absolute">
				</li>
				<li class="layui-nav-item">
					<a href="/" style="font-size:16px">
						<font style="font-size:22px;color:#009688">OSPARK</font>后台管理
					</a>
				</li>
			</ul>
			<ul class="layui-nav " style="right:0;position:absolute">

				<li class="layui-nav-item">
					<a href="javascript:;" id="setname" style="font-size: 18px;">我</a>
					<dl class="layui-nav-child">
						<dd><a href="/get_logout">退出</a></dd>
					</dl>
				</li>
			</ul>

        </div>

        <!-------------渲染后，更改用户名----------------->
        <script>
            var username = sessionStorage.getItem("name");
            var setname = document.getElementById("setname");
            setname.innerText = username;
        </script>
        

		<!-- 侧边栏 -->
		<div class="layui-side layui-bg-black">
			<div class="layui-side-scroll">
				<ul class="layui-nav layui-nav-tree">

					<li class="layui-nav-item layui-nav-itemed">
						<a href="/" id="home"><span class="layui-icon layui-icon-home"></span> 主页</a>
					</li>
					<li class="layui-nav-item layui-nav-itemed">
						<a href="/bills" id="bills"><span class="layui-icon layui-icon-form"></span> 账单记录</a>
					</li>
					<li class="layui-nav-item layui-nav-itemed">
						<a href="/accounts" id="accounts"><span class="layui-icon layui-icon-user"></span> 账号管理</a>
					</li>
					<li class="layui-nav-item layui-nav-itemed">
						<a href="/charges" id="charges"><span class="layui-icon layui-icon-rmb"></span> 收费标准</a>
					</li>
				</ul>
			</div>
        </div>
        


		<!-- 内容 -->

		<div class="layui-body layui-tab-content" style="background-color:#f2f2f2;bottom:0px">
