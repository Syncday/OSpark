<?php
/**
 * This file is part of workerman.
 *
 * Licensed under The MIT License
 * For full copyright and license information, please see the MIT-LICENSE.txt
 * Redistributions of files must retain the above copyright notice.
 *
 * @author walkor<walkor@workerman.net>
 * @copyright walkor<walkor@workerman.net>
 * @link http://www.workerman.net/
 * @license http://www.opensource.org/licenses/mit-license.php MIT License
 */

/**
 * 用于检测业务代码死循环或者长时间阻塞等问题
 * 如果发现业务卡死，可以将下面declare打开（去掉//注释），并执行php start.php reload
 * 然后观察一段时间workerman.log看是否有process_timeout异常
 */
//declare(ticks=1);

use \GatewayWorker\Lib\Gateway;

require_once 'Connection.php';

/**
 * 主逻辑
 * 主要是处理 onConnect onMessage onClose 三个方法
 * onConnect 和 onClose 如果不需要可以不用实现并删除
 */
class Events
{

    /**
     * 新建一个类的静态成员，用来保存数据库实例
     */
    public static $db = null;

    /**
     * 进程启动后初始化数据库连接
     */
    public static function onWorkerStart($worker)
    {
        self::$db = new \Workerman\MySQL\Connection('127.0.0.1', '3306', 'syncday', '123456', 'ospark');
    }

    /**
     * 当客户端发来消息时触发
     * @param int $client_id 连接id
     * @param mixed $message 具体消息
     */
    public static function onMessage($client_id, $message)
    {
        //空信息为保持连接，不处理
        if ($message == "" || $message == null) {
            return;
        }

        //检查用户是否已合法登录
        if (!(new self())->login($client_id, $message)) {
            Gateway::closeClient($client_id);
            return;
        }

        //获取信息
        $data = json_decode($message, true);
        $type = $data['type'];
        //验证成功后直接返回
        if ($type == 'login') {
            return;
        } else if ($type == 'message') {
            $type = $data['type'];
            $from = $data['from'];
            $to = $data['to'];
            $content = $data['content'];

            //获取系统时间
            date_default_timezone_set('prc');
            $time = date("Y-m-d H:i:s");

            //处理昵称
            $result = self::$db->row("SELECT user_type,user_car FROM user WHERE user_phone='{$from}'");
            if($result['user_type']=='user'){
                $nickname = isset($result['user_car'])?$result['user_car']:$from;
            }else{
                $nickname = "操作员".substr($from,-4,strlen($from));
            }

            //检查接送者是否在线
            if (!Gateway::isUidOnline($to)) { //不在线，保存信息到数据库
                $result = self::$db->query("INSERT INTO chat VALUES('{$type}','{$from}','{$nickname}','{$to}','{$content}','{$time}')");
            } else { //在线，推送
                Gateway::sendToUid($to, json_encode(array('type' => $type, 'from' => $from,'nickname'=>$nickname, 'time' => $time, 'content' => $content)));
            }
        } else if ($type == 'notification') {

            $from = Gateway::getUidByClientId($client_id);
            
            //处理昵称
            $result = self::$db->row("SELECT user_type,user_car FROM user WHERE user_phone='{$from}'");
            if($result['user_type']=='user'){
                $nickname = isset($result['user_car'])?$result['user_car']:$from;
            }else{
                $nickname = "操作员".substr($from,-4,strlen($from));
            }

            $to = $data['to'];
            $content = $data['content'];
            $latitude = (double) $data['latitude'] != null ? $data['latitude'] : 0;
            $longitude = (double) $data['longitude'] != null ? $data['longitude'] : 0;
            $sql = "'{$from}','{$to}','{$latitude}','{$longitude}'";
            //检查接送者是否在线
            if (!Gateway::isUidOnline($to)) { //不在线，保存信息到数据库
                $isSave = self::$db->query("SELECT * FROM note WHERE note_from ='{$from}' AND note_to = '{$to}'");
                if (empty($isSave)) {
                    $result = self::$db->query("INSERT INTO note VALUES('{$from}','{$nickname}','{$to}','{$content}','{$latitude}','{$longitude}')");
                } else {
                    $result = self::$db->query("UPDATE note SET note_content = '{$content}',note_latitude = {$latitude},note_longitude = {$longitude} WHERE note_from ='{$from}' AND note_to = '{$to}'");
                }
            } else { //在线，推送
                Gateway::sendToUid($to, json_encode(array('type' => $type, 'from' => $from, 'nickname'=>$nickname,'content' => $content, 'latitude' => $latitude, 'longitude' => $longitude)));
            }
        }

    }

    /**
     * 检查用户是否登录验证
     * @return bool
     */
    private function login($client_id, $message)
    {

        $data = json_decode($message, true);
        $type = isset($data['type']) ? $data['type'] : "";

        //验证
        if ($type == 'login') {

            $phone_number = isset($data['phone_number']) ? $data['phone_number'] : '';
            $token = isset($data['token']) ? $data['token'] : '';
            $result = self::$db->query("SELECT * FROM user where user_phone = '{$phone_number}' AND user_token = '{$token}' ");

            if (count($result, 0) == 0) { //验证失败
                return false;
            } else { //验证成功
                //$client_id绑定到手机号码
                Gateway::bindUid($client_id, $phone_number);
                //发送未读信息
                $this->_send_unread($phone_number);
                return true;
            }

        } else {
            //查看clientid绑定的uid，为空则说明是非法用户
            $uid = Gateway::getUidByClientId($client_id);
            if (!isset($uid) || $uid == null || $uid == '') {
                var_export("非法用户");
                return false;
            }
            //原手机号验证成功后，非法更改手机号
            $phone_number = isset($data['from']) ? $data['from'] : "";
            if ($uid != $phone_number) {
                var_export("用户非法修改登录信息");
                return false;
            }

            //发送未读信息
            $this->_send_unread($phone_number);

            return true;

        }
    }

    /**
     * 检查发送用户未读信息
     */
    private function _send_unread($phone_number)
    {
        //信息
        $result = self::$db->query("SELECT * FROM chat where chat_to = '{$phone_number}'");
        foreach ($result as $data) {
            if (Gateway::isUidOnline($phone_number)) {
                $type = $data['chat_type'];
                $time = $data['chat_time'];
                $content = $data['chat_content'];
                $nickname = $data['chat_nickname'];
                $from = $data['chat_from'];
                //发送
                Gateway::sendToUid($phone_number, json_encode(array('type' => $type, 'from' => $from,'nickname'=>$nickname, 'time' => $time, 'content' => $content)));
                //从数据库删除
                self::$db->query("DELETE FROM chat WHERE chat_to = '{$phone_number}' AND chat_time = '{$time}' AND chat_content = '{$content}'");
            }
        }
        //通知
        $result = self::$db->query("SELECT * FROM note where note_to = '{$phone_number}'");
        foreach ($result as $data) {
            if (Gateway::isUidOnline($phone_number)) {
                $from = $data['note_from'];
                $content = $data['note_content'];
                $nickname =$data['note_nickname'];
                $latitude = $data['note_latitude'];
                $longitude = $data['note_longitude'];
                //发送
                Gateway::sendToUid($phone_number, json_encode(array('type' => 'notification', 'content' => $content, 'from' => $from,'nickname'=>$nickname, 'latitude' => $latitude, 'longitude' => $longitude)));
                //从数据库删除
                self::$db->query("DELETE FROM note WHERE note_from ='{$from}' AND note_content = '{$content}'");
            }
        }

    }

}
