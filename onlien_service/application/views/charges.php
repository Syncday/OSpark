<!------------------------------------改变侧边栏--------------------------------->
<script type="text/javascript">
	document.getElementById("charges").className = "layui-this";

</script>

<!-------------------------------------表单------------------------------------>
<script type="text/html" id="toolbar">
	<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>

</script>

<script>
	layui.use("table", function () {
		var table = layui.table;

		//监听工具条
		table.on("tool(charges_bar)", function (obj) {
			var data = obj.data;
			if (obj.event === "del") {
				layer.confirm("确定删除？", function (index) {

					var xmlHttp = new XMLHttpRequest();
					xmlHttp.open("post", "https://syncday.com/delete_charges", true);
					xmlHttp.setRequestHeader("Content-Type", "application/json");
					xmlHttp.send(JSON.stringify(data)); // 数据
					xmlHttp.onreadystatechange = function () {
						if (xmlHttp.readyState == 4) { //必须要等待传输完成后才操作，否则可能会有json解码错误
							if (xmlHttp.status == 200) {
								var json = JSON.parse(xmlHttp.responseText);
								layer.msg(json["info"]);
								if (json["status"] == "1") {
									obj.del();
									layer.close(index);
								}
							} else {
								layer.msg("出错");
							}
						}
					};

				});
			}
		});
		table.on("tool(platform_bar)", function (obj) {
			var data = obj.data;
			if (obj.event === "del") {
				layer.confirm("确定删除？", function (index) {

					var xmlHttp = new XMLHttpRequest();
					xmlHttp.open("post", "https://syncday.com/delete_platform", true);
					xmlHttp.setRequestHeader("Content-Type", "application/json");
					xmlHttp.send(JSON.stringify(data)); // 数据
					xmlHttp.onreadystatechange = function () {
						if (xmlHttp.readyState == 4) { //必须要等待传输完成后才操作，否则可能会有json解码错误
							if (xmlHttp.status == 200) {
								var json = JSON.parse(xmlHttp.responseText);
								layer.msg(json["info"]);
								if (json["status"] == "1") {
									obj.del();
									layer.close(index);
								}
							} else {
								layer.msg("出错");
							}
						}
					};

				});
			}
		});
	});

	//表单提交
	layui.use(["form", "table"], function () {
		var form = layui.form;
		var table = layui.table;
		//弹出窗表单提交
		form.on("submit(add)", function (data) {
			var xmlHttp = new XMLHttpRequest();
			xmlHttp.open("post", "https://syncday.com/add_charges");
			xmlHttp.setRequestHeader("Content-Type", "application/json");
			xmlHttp.send(JSON.stringify(data.field)); // 数据
			xmlHttp.onreadystatechange = function () {
				if (xmlHttp.readyState == 4) { //必须要等待传输完成后才操作，否则可能会有json解码错误
					if (xmlHttp.status == 200) {
						var json = JSON.parse(xmlHttp.responseText);
						layer.msg(json["info"]);
						if (json["status"] == "1") {
							layer.closeAll("page");
							table.reload('charges_table', {
								url: "https://syncday.com/get_charges_table"
							});
						}
					} else {
						layer.msg("出错");
					}
				}
			};
			return false;
		});
	});

	//图片上传
	layui.use(['upload', 'table'], function () {
		var $ = layui.jquery,
			upload = layui.upload;
		var table = layui.table;
		upload.render({
			elem: '#upload_img',
			url: 'https://syncday.com/upload_img' //上传接口
				,
			done: function (res) {
				layer.msg(res.info);
				console.log(res);
				if (res.status == 1) {
					table.reload('platform_table', {
						url: "https://syncday.com/get_platform_table"
					});
				}
			}
		});
	});

</script>






<!-------------------------------------页面开始----------------------------------->
<div class="layui-fluid">

	<div class="layui-row layui-col-space15">

		<div class="layui-card layui-col-md8 layui-inline" style="margin: 10px; ">
			<div class="layui-card-header bold-font">
				添加收费标准
			</div>
			<div class="layui-card-body">

				<div class="layui-form layui-card-body layuiadmin-card-header-auto">
					<blockquote class="layui-elem-quote">提示：收费标准为停车时间少或等于该时间段但大于前一个时间端</blockquote>

					<div class="layui-form-item layui-inline ">
						<label class="layui-form-label">时间</label>
						<div class="layui-input-inline">
							<input type="text" name="price_timeline" lay-verify="required|number" placeholder="请输入累计时间"
								autocomplete="off" class="layui-input">
						</div>
						<div class="layui-form-mid layui-word-aux">分钟</div>
					</div>
					<div class="layui-form-item layui-inline ">
						<label class="layui-form-label">收费</label>
						<div class="layui-input-inline">
							<input type="text" name="price_value" lay-verify="required|number" placeholder="请输入累计金额"
								autocomplete="off" class="layui-input">
						</div>
						<div class="layui-form-mid layui-word-aux">元</div>
					</div>
					<div class=" layui-inline" style="margin-bottom: 15px; margin-left: 10px; ">
						<div class="layui-input-inline">
							<button type="submit" class="layui-btn" data-type="submit" lay-submit="" lay-filter="add">
								添加
							</button>
						</div>
					</div>
				</div>

			</div>
		</div>

		<!--------------------------------------上传---------------------------------->
		<div class="layui-card layui-block  layui-col-md3" style="margin: 10px;">
			<div class="layui-card-header bold-font">上传支付二维码</div>
			<div class=" layui-card-body">
				<div class="layui-upload-drag" id="upload_img" style="margin-top: 20px;">
					<i class="layui-icon"></i>
					<p>点击上传，或将文件拖拽到此处</p>
				</div>
			</div>
		</div>


		<!-----------------------------表格----------------------------------->
		<div class="layui-card layui-inline layui-col-md5" style="margin: 10px;">
			<div class="layui-card-header bold-font" ">收费标准</div>
			<div class=" layui-card-body">
				<table class="layui-table" id="charges_table"
					lay-data="{width: 0, height:0, url:'get_charges_table/', page:true, id:'charges_table'}"
					lay-filter="charges_bar">
					<thead>
						<tr>
							<th lay-data="{field:'price_timeline', width:150, sort: true}">
								时间
							</th>
							<th lay-data="{field:'price_value', width:150}">
								收费
							</th>
							<th lay-data="{fixed: 'right', width:150, align:'center', toolbar: '#toolbar'}">
								操作
							</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>


		<!-----------------------------表格----------------------------------->
		<div class="layui-card layui-block layui-col-md6" style="margin: 10px;">
			<div class="layui-card-header bold-font" ">支付二维码</div>
			<div class=" layui-card-body">
				<table class="layui-table" id="platform_table"
					lay-data="{width: 0, height:0, url:'get_platform_table/', page:true, id:'platform_table'}"
					lay-filter="platform_bar">
					<thead>
						<tr>
							<th lay-data="{field:'platform_time', width:180, sort: true}">
								上传时间
							</th>
							<th lay-data="{field:'platform_app', width:120, sort: true}">
								支付平台
							</th>
							<th lay-data="{field:'platform_url', width:190}">
								图片链接
							</th>
							<th lay-data="{fixed: 'right', width:150, align:'center', toolbar: '#toolbar'}">
								操作
							</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>

	</div>
</div>
