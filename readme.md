### 路边停车收费系统

### 简介

 	路边停车收费系统主要包括三部分：前端（Android，Web），服务端（PHP），聊天服务端（PHP）。

​	系统功能：

- 地图展示
- 在线支付
- 即时通讯
- 后台管理

  ### 主要的库和框架：

- 高德地图SDK：提供地图展示，定位以及逆地理编码
- 支付宝支付SDK：实现在线支付功能
- Volley框架：Android与服务端的通讯框架是Volley，而且自带网络图片展示控件。
- JSON：Android端和服务端通过JSON格式进行数据交换
- Java_websocket框架：Android端的的技术通讯基于该框架进行开发，实现心跳连接
- Codeigniter框架：后端使用Codeigniter 3.X版本进行开发，实现前端各种接口
- GatewayWorker框架：即时通讯服务端基于该控件进行开发

### 预览：

<img src="https://i.loli.net/2020/06/04/CkO9r6Kl8Bq2EgY.jpg" alt="uhome.jpg" style="zoom: 33%;" /><img src="https://i.loli.net/2020/06/04/oJ4wKMHYvIktrpz.png" alt="ohome.png" style="zoom:33%;" />

<img src="https://i.loli.net/2020/06/04/wVsagxOU4izYlnp.jpg" alt="addcar.jpg" style="zoom:33%;" /><img src="https://i.loli.net/2020/06/04/9zJgS8h6MZBLn7X.jpg" alt="onearby.jpg" style="zoom:33%;" />

<img src="https://i.loli.net/2020/06/04/skCcFxYfToHrzAI.png" alt="chat.png" style="zoom:33%;" /><img src="https://i.loli.net/2020/06/04/tsrvYeyW419A3NF.jpg" alt="upay.jpg" style="zoom:33%;" />