<!----------------------------------  改变css -------------------------------------->
<script type="text/javascript">
	document.getElementById("accounts").className = "layui-this";

</script>


<!----------------------------------  表格toolbar创建监听、调用弹窗  -------------------------->

<script type="text/html" id="toolbar">
	<a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
	<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>

</script>

<script>
	//表格
	layui.use("table", function () {
		var table = layui.table;

		//监听工具条
		table.on("tool(accounts_bar)", function (obj) {
			var data = obj.data;
			if (obj.event === "del") {
				layer.confirm("确定删除？", function (index) {

					var xmlHttp = new XMLHttpRequest();
					xmlHttp.open("post", "https://syncday.com/delete_accounts", true);
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
			} else if (obj.event === "edit") {
				layui.use("layer", function () {
					var $ = layui.jquery,
						layer = layui.layer;
					bill_id = "wht";
					layer.open({
						title: "修改账号",
						type: 1,
						shadeClose: true,
						content: $(accounts_form),
						success: function (layero) {
							//解决弹窗被遮罩层覆盖bug
							var mask = $(".layui-layer-shade");
							mask.appendTo(layero.parent()); //其中：layero是弹层的DOM对象
							//表单赋值
							Object.keys(data).forEach(function (key) {
								document.getElementById(key).value = data[key];
								if (key == 'user_type') {

									document.getElementById("type_" + data[
										key]).checked = true;
									layui.use("form", function () {
										var form = layui.form;
										form.render(); //必须要，动态渲染
									});
								}
							});
						},
					});
				});
			}
		});
	});
	//表单提交
	layui.use(["form", "table"], function () {
		var form = layui.form;
		var table = layui.table;

		//弹出窗表单提交
		form.on("submit(submit)", function (data) {
			console.log(data);
			var xmlHttp = new XMLHttpRequest();
			xmlHttp.open("post", "https://syncday.com/update_accounts");
			xmlHttp.setRequestHeader("Content-Type", "application/json");
			xmlHttp.send(JSON.stringify(data.field)); // 数据
			xmlHttp.onreadystatechange = function () {
				if (xmlHttp.readyState == 4) { //必须要等待传输完成后才操作，否则可能会有json解码错误
					if (xmlHttp.status == 200) {
						var json = JSON.parse(xmlHttp.responseText);
						layer.msg(json["info"]);
						if (json["status"] == "1") {
							layer.closeAll("page");
							table.reload('accounts_table', {

							});
						}
					} else {
						layer.msg("出错");
					}
				}
			};
			return false;
		});
		form.on("submit(add_account)", function (data) {
			console.log(data);
			var xmlHttp = new XMLHttpRequest();
			xmlHttp.open("post", "https://syncday.com/add_accounts");
			xmlHttp.setRequestHeader("Content-Type", "application/json");
			xmlHttp.send(JSON.stringify(data.field)); // 数据
			xmlHttp.onreadystatechange = function () {
				if (xmlHttp.readyState == 4) { //必须要等待传输完成后才操作，否则可能会有json解码错误
					if (xmlHttp.status == 200) {
						var json = JSON.parse(xmlHttp.responseText);
						layer.msg(json["info"]);
						if (json["status"] == "1") {
							layer.closeAll("page");
							table.reload('accounts_table', {
							});
						}
					} else {
						layer.msg("出错");
					}
				}
			};
			return false;
		});

		//搜索表单提交
		form.on("submit(search)", function (data) {

			table.reload('accounts_table', {
				url: "https://syncday.com/search_accounts",
				where: {
					data: data.field,
				}
			});
			return false;
		});
		form.on("submit(refresh)", function () {

			table.reload('accounts_table', {
				url: "https://syncday.com/get_accounts_table",
				where: {
					data: null
				}
			});
			return false;
		});
		form.on("submit(add)", function () {

			var $ = layui.jquery;
			console.log("123456");
			layer.open({
				title: "添加账号",
				type: 1,
				shadeClose: true,
				content: $(add_accounts_form),
				success: function (layero) {
					//解决弹窗被遮罩层覆盖bug
					var mask = $(".layui-layer-shade");
					mask.appendTo(layero.parent()); //其中：layero是弹层的DOM对象

				},
			});
			return false;
		});
	});

</script>

<!--------------------------------------页面开始-------------------------------------->
<div class="layui-fluid">
	<div class="layui-row layui-col-space15">
		<!------------------------------搜索------------------------------------>
		<div class="layui-card">
			<div class="layui-form layui-card-body layuiadmin-card-header-auto">
				<blockquote class="layui-elem-quote">提示：可以输入一个或者多个限定条件</blockquote>
				<div class="layui-form-item">
					<div class="layui-inline layui-col-md3">
						<label class="layui-form-label">用户账号</label>
						<div class="layui-input-block">
							<input type="text" name="user_phone" placeholder="请输入" autocomplete="off"
								class="layui-input">
						</div>
					</div>
					<div class="layui-inline layui-col-md2">
						<label class="layui-form-label">车牌号</label>
						<div class="layui-input-block">
							<input type="text" name="user_car" placeholder="请输入" autocomplete="off" class="layui-input">
						</div>
					</div>
					<div class="layui-inline layui-col-md4">
						<label class="layui-form-label">用户类型</label>
						<div class="layui-input-block">
							<input type="radio" name="user_type" value="user" title="普通用户">
							<input type="radio" name="user_type" value="operator" title="操作员">
						</div>
					</div>

					<div class="layui-inline">
						<button class="layui-btn" lay-submit="" lay-filter="search">
							<i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
						</button>
					</div>

					<div class="layui-inline">
						<button class="layui-btn" lay-submit="" lay-filter="add">
							<i class="layui-icon">&#xe608;</i> 添加
						</button>
					</div>

					<div class="layui-inline">
						<button class="layui-btn" lay-submit="" lay-filter="refresh">
							<i class="layui-icon layui-icon-refresh-3 layuiadmin-button-btn"></i>
						</button>
					</div>
				</div>
			</div>
		</div>


		<!-----------------------表格------------------------->
		<div class="layui-card">
			<div class="layui-card-header bold-font">账号信息</div>
			<div class=" layui-card-body">
				<table class="layui-table" id="accounts_table"
					lay-data="{width: 0, height:0, url:'get_accounts_table/', page:true, id:'accounts_table'}"
					lay-filter="accounts_bar">
					<thead>
						<tr>
							<th lay-data="{field:'user_phone', width:150, sort: true, fixed: true}">
								用户账号
							</th>
							<th lay-data="{field:'user_type', width:120, sort: true}">用户类型</th>
							<th lay-data="{field:'user_car', width:120, sort: true}">
								用户车辆
							</th>
							<th lay-data="{field:'user_password', width:300}">
								用户密码
							</th>
							<th lay-data="{field:'user_token', width:300}">
								用户校验码
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

<!------------------------------------弹窗表单样式(默认隐藏)--------------------------------->
<div class="layui-card" style="display: none; " id="accounts_form">

	<div class="layui-card-body">
		<form class="layui-form" action="" lay-filter="example">
			<div class="layui-form-item">
				<label class="layui-form-label">用户账号</label>
				<div class="layui-input-block">
					<input id="user_phone" disabled style="cursor:not-allowed;" type="text" name="user_phone"
						lay-verify="required|phone" autocomplete="off" class="layui-input" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">用户类型</label>
				<div class="layui-input-block" id="user_type">
					<input type="radio" name="user_type" id="type_user" value="user" title="普通用户">
					<input type="radio" name="user_type" id="type_operator" value="operator" title="操作员">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">用户车辆</label>
				<div class="layui-input-block">
					<input id="user_car" type="text" name="user_car" autocomplete="off" class="layui-input" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">用户密码</label>
				<div class="layui-input-block">
					<input id="user_password" type="text" name="user_password" lay-verify="required" autocomplete="off"
						class="layui-input" />
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">用户校验码</label>
				<div class="layui-input-block">
					<input id="user_token" type="text" name="user_token" autocomplete="off" class="layui-input" />
				</div>
			</div>

			<div class="layui-form-item">
				<div class="layui-input-block">
					<button id="form_submit" type="submit" class="layui-btn" data-type="close" lay-submit=""
						lay-filter="submit">
						确定修改
					</button>
				</div>
			</div>
		</form>
	</div>
</div>
<!------------------------------------弹窗表单样式(默认隐藏)--------------------------------->
<div class="layui-card" style="display: none; " id="add_accounts_form">

	<div class="layui-card-body">
		<form class="layui-form" action="" lay-filter="example">
			<div class="layui-form-item">
				<label class="layui-form-label">用户账号</label>
				<div class="layui-input-block">
					<input type="text" name="user_phone" lay-verify="required|phone" autocomplete="off"
						class="layui-input" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">用户类型</label>
				<div class="layui-input-block">
					<input type="radio" name="user_type" value="user" title="普通用户" checked="">
					<input type="radio" name="user_type" value="operator" title="操作员">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">用户车辆</label>
				<div class="layui-input-block">
					<input type="text" name="user_car" autocomplete="off" class="layui-input" />
				</div>
			</div>

			<div class="layui-form-item">
				<div class="layui-input-block">
					<button type="submit" class="layui-btn" data-type="close" lay-submit="" lay-filter="add_account">
						确定添加
					</button>
				</div>
			</div>
		</form>
	</div>
</div>
