<?php

require_once '../AopClient.php';
require_once '../AopCertification.php';
require_once '../request/AlipayTradeQueryRequest.php';
require_once '../request/AlipayTradeWapPayRequest.php';
require_once '../request/AlipayTradeAppPayRequest.php';

/**
 * 证书类型AopClient功能方法使用测试
 * 1、execute 调用示例
 * 2、sdkExecute 调用示例
 * 3、pageExecute 调用示例
 */

// //1、execute 使用
// $aop = new AopClient();
// $aop->gatewayUrl = 'https://openapi.alipaydev.com/gateway.do';
// $aop->appId = '2016101400687398';
// $aop->rsaPrivateKey = 'MIIEowIBAAKCAQEAmYx5TQsJpneULhJnaLe+KhjjL/ht8zpCHnkJDFlTpqckr7Yy9tbPyOTLSNn+w9RfhwUBpEcRj6RRWYoEP5Cmx5kU68o3WIxhhLYaxptUFmxYIB9waGqv0CM63kX3Wgy0fX2P3sZqPezKDNyPlLI1cUIkQowBIW9rYSJOsiDIUycgbME1bx2wL6SeBWUq5D9VdArhUpHrK/JE5btaVvqfqh43qZreCssfP0g+nQ9II+Xyo5nTOJ5J5WCVabGSsr7A1GRoG8dkJATfEuVZU89a4SZh1Icpq9vRTqYpbGStPcOHE7cdmVA3qiv+ltJgUKXCa3h8nosYHdwo3XZHTNiB3wIDAQABAoIBAQCX35+jGoXDB4ejQkTSCNuo29Dqg7iM2VTIu0K92iSpM61PttEV4UbIF7USK7yXiZmq/MKw8DJod0iXXbHyBWtRBRLyXaL8jG1wTIaTZtN3Elbf/vi57rxTDGWPsHiFswdbEDSbTnmLIoh+zDGgC9xcV+VxhPUhUwlMnBhmi5JY21QoqMvZptdY2pghU4ZiEFTD49IPS4rcywseo4a7BZxx3O9yP+Z0gXOiouq7pn9IESkTk2zVWaABCBRb5ELmUrHndiUY8i3TdZ0QzRba+P1SGTYkHkEbHOj0HYvadFWRrjvlnIvagwW+4JvoOOiGiY2Wmz0wTs139Cq8XFarxyYhAoGBAMowC2aTMxfE0vDYx6CoJHBrMiHC8SX5Y+tjVimxrrzAXCQuvD4HNG+rzo7/J8dSzIkm544BUdy/kHKSZy9ZkbwhbAZsK1Cu84JOzFpJt8reXFm2kIdrtZwXoCHfGP6oFuGLR8bT4SwdR4FA8jK4LcirFemHuX5d3kI+hubyGnonAoGBAMJqcGS/hLM55Ujg9uO6HaHXN76PuW/WJLLuHPnd95pf+GWs7ui5+3yTJDPUFWHYJqM7ApDBNJlWZ5PUc9o0Ffo1jovyYwuflH34Smk0IPXt9WbW0dBVTzWAzU1eA5+V00Opn3WEIATiZp+AVcjcEGzRJoHC8a1wJZKUAdLFvqWJAoGAZI40fVVkKcQX7PTDg+FcDhUiH6xmZq57xUvoRGA8OV+p88zSjj7jE07tMi0wSW+ijeutafC6GAGmA3VQoDn5wwkUQrV1bXE1Zm9uLtFO+TRR1cR4aQFmlzUgzPe55J0QlCfDEN27tUbykxQL4LG9A3ojMbvwwypIKcYTLbTm+i0CgYApNe7FLGOwqiEUJbuv4g/N8pT6a/Te5AG7fAXgYVDPBHDe/J1X77SDX51Y7yfpKaxnBWcx0AKaTlp+V5aHNmtCMoAY4jRkjlsnqK39RvOFrf9K4VZwVuUHbloWgEs8VMIHobkPbAZtgqBGRynn/d3tcxg6gVxBa3hLbdxFFW2oIQKBgF3RuVIjfI3OMIpEV6YH3uzOxk6+4asNUL77MMu0YwLzxqFRNClmCZLR+j9AhXyoSukC3tOQ8Hk2AxVjGJkT8vMBQGc2hGA7bRygRqx4yov+Ri233LZfulzSRuZoczxKYcpA0XyFFveZwJ1Nti1Gz72RdzvUYr+PACT0YbZq0VTZ';
// $aop->alipayrsaPublicKey = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7z/tHL1E0sggCGQpPisNLxBgzLYfJXmxoeY83nRr/yc3g1TgyjZ+SOi7icaZMp2xiinarqGk3ktKa3DeqTIrtRg9n3cyUtUpWv/xbhQSnuvIjgdKEyhm5Za8HfjqsJ2cLfyv2uQkAZWeEeOL6FM0xN1MFoaXCSpCEAKSi1mI5PRlGvLca5lPLQDXDbj4MRQG/N1lsKAf1vNpN/f59APegIf5JnqOKeM1EYk+6mwF0/4YPP8CZ6SXb074hvSB7oj+DGHPmpqqY2xmbZ87tc7BxTF7boEVlPyNQtC0l5w3NuH/UU0RlevaGo6MtWsBhIw0o5sDa2y6COCZO769p8ieUwIDAQAB';
// $aop->apiVersion = '1.0';
// $aop->signType = 'RSA2';
// $aop->postCharset = 'utf-8';
// $aop->format = 'json';

// $request = new AlipayTradeQueryRequest ();
// $request->setBizContent("{" .
//     "\"out_trade_no\":\"20150320010101001\"," .
//     "\"trade_no\":\"2014112611001004680 073956707\"," .
//     "\"org_pid\":\"2088101117952222\"," .
//     "      \"query_options\":[" .
//     "        \"TRADE_SETTE_INFO\"" .
//     "      ]" .
//     "  }");
// $result = $aop->execute($request);

// var_dump($result);

// // //echo $result;

// return;

$aop = new AopClient;
$aop->gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
$aop->appId = "2016101400687398";
$aop->rsaPrivateKey = 'MIIEowIBAAKCAQEAmYx5TQsJpneULhJnaLe+KhjjL/ht8zpCHnkJDFlTpqckr7Yy9tbPyOTLSNn+w9RfhwUBpEcRj6RRWYoEP5Cmx5kU68o3WIxhhLYaxptUFmxYIB9waGqv0CM63kX3Wgy0fX2P3sZqPezKDNyPlLI1cUIkQowBIW9rYSJOsiDIUycgbME1bx2wL6SeBWUq5D9VdArhUpHrK/JE5btaVvqfqh43qZreCssfP0g+nQ9II+Xyo5nTOJ5J5WCVabGSsr7A1GRoG8dkJATfEuVZU89a4SZh1Icpq9vRTqYpbGStPcOHE7cdmVA3qiv+ltJgUKXCa3h8nosYHdwo3XZHTNiB3wIDAQABAoIBAQCX35+jGoXDB4ejQkTSCNuo29Dqg7iM2VTIu0K92iSpM61PttEV4UbIF7USK7yXiZmq/MKw8DJod0iXXbHyBWtRBRLyXaL8jG1wTIaTZtN3Elbf/vi57rxTDGWPsHiFswdbEDSbTnmLIoh+zDGgC9xcV+VxhPUhUwlMnBhmi5JY21QoqMvZptdY2pghU4ZiEFTD49IPS4rcywseo4a7BZxx3O9yP+Z0gXOiouq7pn9IESkTk2zVWaABCBRb5ELmUrHndiUY8i3TdZ0QzRba+P1SGTYkHkEbHOj0HYvadFWRrjvlnIvagwW+4JvoOOiGiY2Wmz0wTs139Cq8XFarxyYhAoGBAMowC2aTMxfE0vDYx6CoJHBrMiHC8SX5Y+tjVimxrrzAXCQuvD4HNG+rzo7/J8dSzIkm544BUdy/kHKSZy9ZkbwhbAZsK1Cu84JOzFpJt8reXFm2kIdrtZwXoCHfGP6oFuGLR8bT4SwdR4FA8jK4LcirFemHuX5d3kI+hubyGnonAoGBAMJqcGS/hLM55Ujg9uO6HaHXN76PuW/WJLLuHPnd95pf+GWs7ui5+3yTJDPUFWHYJqM7ApDBNJlWZ5PUc9o0Ffo1jovyYwuflH34Smk0IPXt9WbW0dBVTzWAzU1eA5+V00Opn3WEIATiZp+AVcjcEGzRJoHC8a1wJZKUAdLFvqWJAoGAZI40fVVkKcQX7PTDg+FcDhUiH6xmZq57xUvoRGA8OV+p88zSjj7jE07tMi0wSW+ijeutafC6GAGmA3VQoDn5wwkUQrV1bXE1Zm9uLtFO+TRR1cR4aQFmlzUgzPe55J0QlCfDEN27tUbykxQL4LG9A3ojMbvwwypIKcYTLbTm+i0CgYApNe7FLGOwqiEUJbuv4g/N8pT6a/Te5AG7fAXgYVDPBHDe/J1X77SDX51Y7yfpKaxnBWcx0AKaTlp+V5aHNmtCMoAY4jRkjlsnqK39RvOFrf9K4VZwVuUHbloWgEs8VMIHobkPbAZtgqBGRynn/d3tcxg6gVxBa3hLbdxFFW2oIQKBgF3RuVIjfI3OMIpEV6YH3uzOxk6+4asNUL77MMu0YwLzxqFRNClmCZLR+j9AhXyoSukC3tOQ8Hk2AxVjGJkT8vMBQGc2hGA7bRygRqx4yov+Ri233LZfulzSRuZoczxKYcpA0XyFFveZwJ1Nti1Gz72RdzvUYr+PACT0YbZq0VTZ';
$aop->format = "json";
$aop->charset = "UTF-8";
$aop->signType = "RSA2";
$aop->alipayrsaPublicKey = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7z/tHL1E0sggCGQpPisNLxBgzLYfJXmxoeY83nRr/yc3g1TgyjZ+SOi7icaZMp2xiinarqGk3ktKa3DeqTIrtRg9n3cyUtUpWv/xbhQSnuvIjgdKEyhm5Za8HfjqsJ2cLfyv2uQkAZWeEeOL6FM0xN1MFoaXCSpCEAKSi1mI5PRlGvLca5lPLQDXDbj4MRQG/N1lsKAf1vNpN/f59APegIf5JnqOKeM1EYk+6mwF0/4YPP8CZ6SXb074hvSB7oj+DGHPmpqqY2xmbZ87tc7BxTF7boEVlPyNQtC0l5w3NuH/UU0RlevaGo6MtWsBhIw0o5sDa2y6COCZO769p8ieUwIDAQAB';
//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
$request = new AlipayTradeAppPayRequest();
//SDK已经封装掉了公共参数，这里只需要传入业务参数
$bizcontent = "{\"body\":\"我是测试数据\","
                . "\"subject\": \"App支付测试\","
                . "\"out_trade_no\": \"20170125test011\","
                . "\"timeout_express\": \"30m\","
                . "\"total_amount\": \"0.01\","
                . "\"product_code\":\"QUICK_MSECURITY_PAY\""
                . "}";
//$request->setNotifyUrl("商户外网可以访问的异步地址");
$request->setBizContent($bizcontent);
//这里和普通的接口调用不同，使用的是sdkExecute
$response = $aop->sdkExecute($request);

//htmlspecialchars是为了输出到页面时防止被浏览器将关键参数html转义，实际打印到日志以及http传输不会有这个问题
echo htmlspecialchars($response);//就是orderString 可以直接给客户端请求，无需再做处理




return;

//2、sdkExecute 测试
$aop = new AopClient();

$aop->gatewayUrl = 'https://openapi.alipay.com/gateway.do';
$aop->appId = '你的appid';
$aop->rsaPrivateKey = '你的应用私钥';
$aop->alipayrsaPublicKey = '你的支付宝公钥';
$aop->apiVersion = '1.0';
$aop->signType = 'RSA2';
$aop->postCharset = 'utf-8';
$aop->format = 'json';

$request = new AlipayTradeAppPayRequest();
$request->setBizContent("{" .
    "\"timeout_express\":\"90m\"," .
    "\"total_amount\":\"9.00\"," .
    "\"product_code\":\"QUICK_MSECURITY_PAY\"," .
    "\"body\":\"Iphone6 16G\"," .
    "\"subject\":\"大乐透\"," .
    "\"out_trade_no\":\"70501111111S001111119\"," .
    "\"time_expire\":\"2016-12-31 10:05\"," .
    "\"goods_type\":\"0\"," .
    "\"promo_params\":\"{\\\"storeIdType\\\":\\\"1\\\"}\"," .
    "\"passback_params\":\"merchantBizType%3d3C%26merchantBizNo%3d2016010101111\"," .
    "\"extend_params\":{" .
    "\"sys_service_provider_id\":\"2088511833207846\"," .
    "\"hb_fq_num\":\"3\"," .
    "\"hb_fq_seller_percent\":\"100\"," .
    "\"industry_reflux_info\":\"{\\\\\\\"scene_code\\\\\\\":\\\\\\\"metro_tradeorder\\\\\\\",\\\\\\\"channel\\\\\\\":\\\\\\\"xxxx\\\\\\\",\\\\\\\"scene_data\\\\\\\":{\\\\\\\"asset_name\\\\\\\":\\\\\\\"ALIPAY\\\\\\\"}}\"," .
    "\"card_type\":\"S0JP0000\"" .
    "    }," .
    "\"merchant_order_no\":\"20161008001\"," .
    "\"enable_pay_channels\":\"pcredit,moneyFund,debitCardExpress\"," .
    "\"store_id\":\"NJ_001\"," .
    "\"specified_channel\":\"pcredit\"," .
    "\"disable_pay_channels\":\"pcredit,moneyFund,debitCardExpress\"," .
    "      \"goods_detail\":[{" .
    "        \"goods_id\":\"apple-01\"," .
    "\"alipay_goods_id\":\"20010001\"," .
    "\"goods_name\":\"ipad\"," .
    "\"quantity\":1," .
    "\"price\":2000," .
    "\"goods_category\":\"34543238\"," .
    "\"categories_tree\":\"124868003|126232002|126252004\"," .
    "\"body\":\"特价手机\"," .
    "\"show_url\":\"http://www.alipay.com/xxx.jpg\"" .
    "        }]," .
    "\"ext_user_info\":{" .
    "\"name\":\"李明\"," .
    "\"mobile\":\"16587658765\"," .
    "\"cert_type\":\"IDENTITY_CARD\"," .
    "\"cert_no\":\"362334768769238881\"," .
    "\"min_age\":\"18\"," .
    "\"fix_buyer\":\"F\"," .
    "\"need_check_info\":\"F\"" .
    "    }," .
    "\"business_params\":\"{\\\"data\\\":\\\"123\\\"}\"," .
    "\"agreement_sign_params\":{" .
    "\"personal_product_code\":\"CYCLE_PAY_AUTH_P\"," .
    "\"sign_scene\":\"INDUSTRY|DIGITAL_MEDIA\"," .
    "\"external_agreement_no\":\"test20190701\"," .
    "\"external_logon_id\":\"13852852877\"," .
    "\"access_params\":{" .
    "\"channel\":\"ALIPAYAPP\"" .
    "      }," .
    "\"sub_merchant\":{" .
    "\"sub_merchant_id\":\"2088123412341234\"," .
    "\"sub_merchant_name\":\"滴滴出行\"," .
    "\"sub_merchant_service_name\":\"滴滴出行免密支付\"," .
    "\"sub_merchant_service_description\":\"免密付车费，单次最高500\"" .
    "      }," .
    "\"period_rule_params\":{" .
    "\"period_type\":\"DAY\"," .
    "\"period\":3," .
    "\"execute_time\":\"2019-01-23\"," .
    "\"single_amount\":10.99," .
    "\"total_amount\":600," .
    "\"total_payments\":12" .
    "      }" .
    "    }" .
    "  }");
$result = $aop->sdkExecute($request);

$responseNode = str_replace(".", "_", $request->getApiMethodName()) . "_response";
echo $responseNode;
$resultCode = $result->$responseNode->code;
if (!empty($resultCode) && $resultCode == 10000) {
    echo "成功";
} else {
    echo "失败";
}

//3、pageExecute 测试
$aop = new AopClient();

$aop->gatewayUrl = 'https://openapi.alipay.com/gateway.do';
$aop->appId = '你的appid';
$aop->rsaPrivateKey = '你的应用私钥';
$aop->alipayrsaPublicKey = '你的支付宝公钥';
$aop->apiVersion = '1.0';
$aop->signType = 'RSA2';
$aop->postCharset = 'utf-8';
$aop->format = 'json';

$request = new AlipayTradeWapPayRequest();
$request->setBizContent("{" .
    "    \"body\":\"对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。\"," .
    "    \"subject\":\"测试\"," .
    "    \"out_trade_no\":\"70501111111S001111119\"," .
    "    \"timeout_express\":\"90m\"," .
    "    \"total_amount\":9.00," .
    "    \"product_code\":\"QUICK_WAP_WAY\"" .
    "  }");
$result = $aop->pageExecute($request);
echo $result;
