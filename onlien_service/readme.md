## 路边停车APP-服务端

### 简介：

​	这是APP的服务端，主要为APP提供请求接口，同时也带有一个后台管理系统。

​    服务端基于Codeigniter框架进行开发，前后端通过JSON格式进行数据交换。

​    由于现在Codeiginter升级为4.0版本了，你可能需要参考Codeiginter 3.X的官方开发文档。

### 环境要求：

- PHP7.0以上
- 你可能需要安装以下PHP插件：php-mbstring、php-curl、php-mysql

### 提示

​	可能需要修改的地方：

- 将syncday.com改为你的网址
- 使用支付功能则需要在项目里的Models的Api_alipay_model.php填写你的支付宝商家信息

​	

### 预览

![登录]( https://github.com/Syncday/online_service/blob/master/preView/login.jpg )

![主页](https://github.com/Syncday/online_service/blob/master/preView/home.png)

![账号](https://github.com/Syncday/online_service/blob/master/preView/accounts.png)

![账单](https://github.com/Syncday/online_service/blob/master/preView/bills.png)

![收费](https://github.com/Syncday/online_service/blob/master/preView/charges.png)