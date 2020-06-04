<?php

/**
 * Api_alipay_model Model Class
 *
 * 支付宝支付模型
 *
 */
class Api_alipay_model extends CI_Model
{

    public function __construct()
    {

        require_once 'alipay/AopClient.php';
        require_once 'alipay/AopCertification.php';
        require_once 'alipay/request/AlipayTradeQueryRequest.php';
        require_once 'alipay/request/AlipayTradeWapPayRequest.php';
        require_once 'alipay/request/AlipayTradeAppPayRequest.php';
        //数据库
        $this->load->database();

    }

    /**
     * 支付宝沙箱支付
     */
    public function get_order($phone_number, $token, $id)
    {

        //验证用户
        $verify_result = $this->Verify_user->verify_user($phone_number, $token);
        if ($verify_result['user_type'] != 'user') {
            return $verify_result;
        }
        //先检查账单已支付
        $isPaid = $this->verify_order($id);
        if($isPaid['status']=='1'){
            return array('status' =>'0','info'=>'该账单已支付');
        }
        //创建账单
        //创建新唯一账单id
        $new_id = $phone_number . time();
        $sql = "UPDATE bill SET bill_id = '{$new_id}' WHERE bill_id = '{$id}'";
        $this->db->simple_query($sql);
        //获取系统的账单信息
        $sql = "SELECT * FROM bill WHERE bill_id = '{$new_id}' AND bill_pay_by ='not'";
        $query = $this->db->query($sql);
        $result = $query->row();
        if (!isset($result->bill_user) || $result->bill_user != $phone_number) {
            return array('status' => '0', 'info' => '创建账单失败');
        }

        $order_info = json_encode(
            array('body' => $result->bill_id, 'subject' => "支付'{$result->bill_car}'停车费用", 'out_trade_no' => $result->bill_id, 'timeout_express' => '10m', 'total_amount' => $result->bill_price, 'product_code' => 'QUICK_MSECURITY_PAY')
        );

        //支付宝设置
        $aop = new AopClient;
        //网关
        $aop->gatewayUrl = "https://openapi.alipaydev.com/gateway.do";//这是沙盒环境
        //商户id
        $aop->appId = "";
        //私钥
        $aop->rsaPrivateKey = '';//输入你在支付宝开发助手创建的PKCS1应用私钥
        $aop->format = "json";
        $aop->charset = "UTF-8";
        $aop->signType = "RSA2";
        //支付宝公钥
        $aop->alipayrsaPublicKey = '';//输入你上传应用私钥到支付宝开发平台后的公钥
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        $request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数
        $bizcontent = $order_info;
        //回调异步地址
        //$request->setNotifyUrl("商户外网可以访问的异步地址");
        $request->setBizContent($bizcontent);
        //这里和普通的接口调用不同，使用的是sdkExecute
        $response = $aop->sdkExecute($request);

        return array("status" => "1", "order" => $response,'order_id'=>$result->bill_id,'info'=>'');
    }

    /**
     * 验证用户是否支付成功
     */

    public function verify_order($order_id)
    {

        $aop = new AopClient;
        //网关
        $aop->gatewayUrl = "https://openapi.alipaydev.com/gateway.do";//这是沙盒环境
        //商户id
        $aop->appId = "";
        //私钥
        $aop->rsaPrivateKey = '';//输入你在支付宝开发助手创建的PKCS1应用私钥
        $aop->format = "json";
        $aop->charset = "UTF-8";
        $aop->signType = "RSA2";
        //支付宝公钥
        $aop->alipayrsaPublicKey = '';//输入你上传应用私钥到支付宝开发平台后的公钥
		
        $request = new AlipayTradeQueryRequest();
        $request->setBizContent("{\"out_trade_no\":\"{$order_id}\"}");
        $result = $aop->execute($request);

        $responseNode = str_replace(".", "_", $request->getApiMethodName()) . "_response";
        $resultCode = $result->$responseNode->code;
        if (!empty($resultCode) && $resultCode == 10000) {
            $sql = "UPDATE bill SET bill_pay_by = 'alipay' WHERE bill_id = '{$order_id}' AND bill_pay_by = 'not'";
            $this->db->simple_query($sql);
            return array('status'=>'1','info'=>'支付成功'); 
        } else {
            return array('status'=>'0','info'=>'支付失败');
        }

    }

}
