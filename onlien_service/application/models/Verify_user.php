<?php

/**
 * verify_user Model Class
 *
 * 验证用户模型
 * 
 * @param  $phone_number,$token
 * @return array('status','user_type','info')
 *
 */
class verify_user extends CI_Model
{

    public function __construct()
    {
        //数据库
        $this->load->database();

    }

    public function verify_operator($phone_number, $token)
    {
        //取出用户信息
        $sql = "SELECT * FROM user where user_phone = '{$phone_number}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        //手机号未注册
        if (empty($result)) {
            return array('status' => '-1', 'user_type'=>'','info' => '用户不存在，请重新登录');
        }
        //验证token失败
        if ($token != $result[0]['user_token']) {
            return array('status' => '-1', 'user_type'=>'','info' => '登录过时，请重新登录');
        }

        //非法用户
        if($result[0]['user_type'] != 'operator'){
            return array('status' => '-1', 'user_type'=>'','info' => '用户验证失败，请重新登录');
        }
        
        //默认验证成功

        return array('status' => '1', 'user_type'=>$result[0]['user_type'],'info' =>'验证成功');
    }

    public function verify_user($phone_number, $token)
    {
        //取出用户信息
        $sql = "SELECT * FROM user where user_phone = '{$phone_number}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        //手机号未注册
        if (empty($result)) {
            return array('status' => '-1', 'user_type'=>'','info' => '用户不存在，请重新登录');
        }
        //验证token失败
        if ($token != $result[0]['user_token']) {
            return array('status' => '-1', 'user_type'=>'','info' => '登录过时，请重新登录');
        }

        //非法用户
        if($result[0]['user_type'] != 'user'){
            return array('status' => '-1', 'user_type'=>'','info' => '用户验证失败，请重新登录');
        }
        
        //默认验证成功

        return array('status' => '1', 'user_type'=>$result[0]['user_type'],'info' =>'验证成功');
    }
}
