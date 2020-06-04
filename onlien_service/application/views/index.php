
<!----------------------------------  改变css -------------------------------------->
<script type="text/javascript">
    document.getElementById('home').className="layui-this";
</script>

<!----------------------------------  ajax获取数据 ---------------------------------->
<script type="text/javascript">
var xmlhttp;

function loadXMLDoc(url) {
    xmlhttp = null;
    if (window.XMLHttpRequest) { // all modern browsers
        xmlhttp = new XMLHttpRequest();
    } else if (window.ActiveXObject) { // for IE5, IE6
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    if (xmlhttp != null) {
        xmlhttp.onreadystatechange = state_Change;
        xmlhttp.open("GET", url, true);
        xmlhttp.send(null);
    } else {
        alert("Browser does not support XMLHTTP.");
    }
}

function state_Change() {
    if (xmlhttp.readyState == 4) { // 4 = "loaded"
        if (xmlhttp.status == 200) { // 200 = "OK"
            //console.log(xmlhttp.responseText);
            var json = JSON.parse(xmlhttp.responseText); //解析成json对象
            var data = json['parking_data'];
            var table = '';
            //拼接数据成表格
            for (i=0;i<data.length;i++) {
                table = table +"<tr>" 
                +"<td>"+data[i]['parking_car']+"</td>"
                +"<td>"+data[i]['parking_operator']+"</td>"
                +"<td>"+data[i]['parking_time']+"</td>"
                +"<td>"+data[i]['parking_address']+"</td>"
                +"</tr>"
            }

            document.getElementById('parking_table').innerHTML = table;
            table = null;

        } else {
            alert("获取数据失败");
        }
    }
}
</script>

<!---------------------------------- 分页 ------------------------------>
<script>
layui.use('laypage', function() {
    var laypage = layui.laypage;

    //执行一个laypage实例
    laypage.render({
        elem: 'paging', //注意，这里的是 ID，不用加 # 号
        count: <?php echo $parking_count ?> , //数据总数，从服务端得到
        jump: function(obj, first) {
            //obj包含了当前分页的所有参数，比如：obj.curr 当前页，以便向服务端请求对应页的数据。obj.limit 每页显示的条数
       
            loadXMLDoc("https://syncday.com/get_parking_table/" + obj.curr);
            
        }
    });
});
</script>

<!--
    卡片数据：day_parking,wek_parking,day_income,wek_income,unpaid_count,unpaid_sum
    表格数据：paking_info:[car,time,address]
    -->

<!--------------------------------------页面开始-------------------------------------->
<div class="layui-fluid">
    
<div class="layui-row layui-col-space15">

        <div class="layui-col-sm6 layui-col-md4">
            <div class="layui-card">
                <div class="layui-card-header">
                    当前停车
                    <span class="layui-badge layui-bg-blue layuiadmin-badge">日</span>

                </div>
                <div class="layui-card-body layuiadmin-card-list">
                    <p class="layuiadmin-big-font"><?php echo $day_parking; ?></p>
                    <p>
                        最近一周
                        <span class="layuiadmin-span-color"><?php echo $wek_parking; ?> <i
                                class="layui-inline layui-icon layui-icon-flag"></i></span>
                    </p>
                </div>
            </div>
        </div>
        <div class="layui-col-sm6 layui-col-md4">
            <div class="layui-card">
                <div class="layui-card-header">
                    收入
                    <span class="layui-badge layui-bg-blue layuiadmin-badge">日</span>
                </div>
                <div class="layui-card-body layuiadmin-card-list">
                    <p class="layuiadmin-big-font"><?php echo $day_income; ?></p>
                    <p>
                        最近一周
                        <span class="layuiadmin-span-color"><?php echo $wek_income; ?> <i
                                class="layui-inline layui-icon layui-icon-rmb"></i></span>
                    </p>
                </div>
            </div>
        </div>

        <div class="layui-col-sm6 layui-col-md4">
            <div class="layui-card">
                <div class="layui-card-header">
                    未支付
                    <span class="layui-badge layui-bg-orange layuiadmin-badge">一</span>
                </div>
                <div class="layui-card-body layuiadmin-card-list">

                    <p class="layuiadmin-big-font"><?php echo $unpaid_count; ?></p>
                    <p>
                        总金额
                        <span class="layuiadmin-span-color"><?php echo $unpaid_sum; ?> <i
                                class="layui-inline layui-icon layui-icon-rmb"></i></span>
                    </p>
                </div>
            </div>
        </div>

    </div>

    <div class="layui-row layui-col-space15">
        <div class="layui-col-md12">
            <div class="layui-card">
                <div class="layui-card-header bold-font">当前停车信息</div>
                <div class="layui-card-body">

                    <table class="layui-table">
                        <colgroup>
                            <col width="150">
                            <col width="200">
                            <col>
                        </colgroup>
                        <thead>
                            <tr>
                                <th>车辆</th>
                                <th>操作员</th>
                                <th>时间</th>
                                <th>地点</th>
                            </tr>
                        </thead>
                        <tbody id="parking_table">
                            <!-- <tr><td></td></tr> -->
                        </tbody>
                    </table>
                </div>
                <div id="paging" style="text-align: center"></div>
            </div>
        </div>
    </div>
</div>