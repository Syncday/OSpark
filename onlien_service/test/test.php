<?php


$aop = new AopClient ();
$aop->gatewayUrl = 'https://openapi.alipay.com/gateway.do';
$aop->appId = 'your app_id';
$aop->rsaPrivateKey = '请填写开发者私钥去头去尾去回车，一行字符串';
$aop->alipayrsaPublicKey='请填写支付宝公钥，一行字符串';
$aop->apiVersion = '1.0';
$aop->signType = 'RSA2';
$aop->postCharset='GBK';
$aop->format='json';
$request = new AlipayTradeQueryRequest ();
$request->setBizContent("{" .
"\"out_trade_no\":\"20150320010101001\"," .
"\"trade_no\":\"2014112611001004680 073956707\"," .
"\"org_pid\":\"2088101117952222\"," .
"      \"query_options\":[" .
"        \"trade_settle_info\"" .
"      ]" .
"  }");
$result = $aop->execute ( $request); 

$responseNode = str_replace(".", "_", $request->getApiMethodName()) . "_response";
$resultCode = $result->$responseNode->code;
if(!empty($resultCode)&&$resultCode == 10000){
echo "成功";
} else {
echo "失败";
}