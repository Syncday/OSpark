<!----------------------------------  改变css -------------------------------------->
<script type="text/javascript">
	document.getElementById("bills").className = "layui-this";

</script>

<!----------------------------------  表格toolbar创建监听、调用弹窗  -------------------------->

<script type="text/html" id="toolbar">
	<a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
	<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>

</script>

<script>
	//日期选择
	layui.use("laydate", function () {
		var laydate = layui.laydate;
		laydate.render({
			elem: "#bill_finish_time",
			type: "datetime",
		});
		laydate.render({
			elem: "#bill_create_time",
			type: "datetime",
		});
	});
	//表格
	layui.use("table", function () {
		var table = layui.table;

		//监听工具条
		table.on("tool(bills_bar)", function (obj) {
			var data = obj.data;
			if (obj.event === "del") {
				layer.confirm("确定删除？", function (index) {

					var xmlHttp = new XMLHttpRequest();
					xmlHttp.open("post", "https://syncday.com/delete_bills", true);
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
						title: "修改账单",
						type: 1,
						shadeClose: true,
						area: ["500px", "600px"],
						content: $(bills_form),
						success: function (layero) {
							//解决弹窗被遮罩层覆盖bug
							var mask = $(".layui-layer-shade");
							mask.appendTo(layero.parent()); //其中：layero是弹层的DOM对象
							//表单赋值
							Object.keys(data).forEach(function (key) {
								document.getElementById(key).value = data[key];
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
			var xmlHttp = new XMLHttpRequest();
			xmlHttp.open("post", "https://syncday.com/update_bills");
			xmlHttp.setRequestHeader("Content-Type", "application/json");
			xmlHttp.send(JSON.stringify(data.field)); // 数据
			xmlHttp.onreadystatechange = function () {
				if (xmlHttp.readyState == 4) { //必须要等待传输完成后才操作，否则可能会有json解码错误
					if (xmlHttp.status == 200) {
						var json = JSON.parse(xmlHttp.responseText);
						layer.msg(json["info"]);
						if (json["status"] == "1") {
							layer.closeAll("page");
							table.reload('bills_table', {

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

			table.reload('bills_table', {
				url: "https://syncday.com/search_bills",
				where: {
					data: data.field
				}
			});
			return false;
		});
		form.on("submit(refresh)", function () {

			table.reload('bills_table', {
				url: "https://syncday.com/get_bills_table",
				where:{
					data:null
				}
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
					<div class="layui-inline layui-col-md4">
						<label class="layui-form-label">账单ID</label>
						<div class="layui-input-block">
							<input type="text" name="bill_id" placeholder="请输入" autocomplete="off" class="layui-input">
						</div>
					</div>
					<div class="layui-inline layui-col-md4">
						<label class="layui-form-label">车牌号</label>
						<div class="layui-input-block">
							<input type="text" name="bill_car" placeholder="请输入" autocomplete="off" class="layui-input">
						</div>
					</div>
					<div class="layui-inline layui-col-md4">
						<label class="layui-form-label">用户</label>
						<div class="layui-input-block">
							<input type="text" name="bill_user" placeholder="请输入" autocomplete="off"
								class="layui-input">
						</div>
					</div>
					<div class="layui-inline layui-col-md4">
						<label class="layui-form-label">操作员</label>
						<div class="layui-input-block">
							<input type="text" name="bill_operator" placeholder="请输入" autocomplete="off"
								class="layui-input">
						</div>
					</div>

					<div class="layui-inline">
						<button class="layui-btn" lay-submit="" lay-filter="search">
							<i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
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
			<div class="layui-card-header bold-font" ">账单信息</div>
			<div class=" layui-card-body">
				<table class="layui-table" id="bills_table"
					lay-data="{width: 0, height:0, url:'get_bills_table/', page:true, id:'bills_table'}"
					lay-filter="bills_bar">
					<thead>
						<tr>
							<th lay-data="{field:'bill_id', width:200, sort: true, fixed: true}">
								账单ID
							</th>
							<th lay-data="{field:'bill_car', width:100}">车牌号</th>
							<th lay-data="{field:'bill_user', width:120, sort: true}">
								用户账号
							</th>
							<th lay-data="{field:'bill_operator', width:120, sort: true}">
								操作员账号
							</th>
							<th lay-data="{field:'bill_create_time', width:150, sort: true}">
								开始时间
							</th>
							<th lay-data="{field:'bill_finish_time', width:150, sort: true}">
								结束时间
							</th>
							<th lay-data="{field:'bill_address', width:150}">
								停车地址
							</th>
							<th lay-data="{field:'bill_price', width:80, sort: true}">
								金额
							</th>
							<th lay-data="{field:'bill_pay_by', width:120, sort: true}">
								支付方式
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
</div>

<!------------------------------------弹窗表单样式(默认隐藏)--------------------------------->
<div class="layui-card" style="display: none; padding: 10px;" id="bills_form">

	<div class="layui-card-body">
		<form class="layui-form" action="" lay-filter="example">
			<div class="layui-form-item">
				<label class="layui-form-label">账单ID</label>
				<div class="layui-input-block">
					<input id="bill_id" style="cursor: not-allowed;" disabled type="text" name="bill_id"
						lay-verify="required" autocomplete="off" class="layui-input" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">车牌号</label>
				<div class="layui-input-block">
					<input id="bill_car" type="text" name="bill_car" lay-verify="required" autocomplete="off"
						class="layui-input" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">用户账号</label>
				<div class="layui-input-block">
					<input id="bill_user" type="text" name="bill_user" lay-verify="required|phone" autocomplete="off"
						class="layui-input" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">操作员账号</label>
				<div class="layui-input-block">
					<input id="bill_operator" type="text" name="bill_operator" lay-verify="required" autocomplete="off"
						class="layui-input" />
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">地址</label>
				<div class="layui-input-block">
					<input id="bill_address" type="text" name="bill_address" lay-verify="required" autocomplete="off"
						class="layui-input" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">金额</label>
				<div class="layui-input-block">
					<input id="bill_price" type="text" name="bill_price" lay-verify="required|number" autocomplete="off"
						class="layui-input" />
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">支付方式</label>
				<div class="layui-input-block">
					<input id="bill_pay_by" type="text" name="bill_pay_by" lay-verify="required" autocomplete="off"
						class="layui-input" />
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">开始时间</label>
				<div class="layui-input-block">
					<div class="layui-inline">
						<input type="text" id="bill_create_time" name="bill_create_time" lay-verify="bill_create_time"
							class="layui-input" id="bill_create_time" placeholder="yyyy-MM-dd HH:mm:ss" />
					</div>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">结束时间</label>
				<div class="layui-inline">
					<input type="text" id="bill_finish_time" name="bill_finish_time" lay-verify="bill_finish_time"
						class="layui-input" id="bill_finish_time" placeholder="yyyy-MM-dd HH:mm:ss" />
				</div>
			</div>

			<div class="layui-form-item">
				<div class="layui-input-block">
					<button type="submit" style="margin-left: 50px;" class="layui-btn" data-type="close" lay-submit=""
						lay-filter="submit">
						确定修改
					</button>
				</div>
			</div>
		</form>
	</div>
</div>
