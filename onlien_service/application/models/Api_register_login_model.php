<?php

/**
 * Api_register_login_model Model Class
 *
 * Android端普通用户信息操作模型
 *
 */
class Api_register_login_model extends CI_Model
{

    public function __construct()
    {
        //数据库
        $this->load->database();
        //字符串辅助函数
        $this->load->helper('string');
        $this->load->model('Verify_user');
    }

    /**
     * 获取登录信息
     *
     * @param $phpne_number String //手机号
     * @param $password String //明文密码
     *
     * @return Array //json {status:(-1:用户未注册，0:密码错误,1:登录成功)，token: ,accounty_type:(user：用户，operator：操作员)}
     *
     */
    public function get_login_info($phone_number, $password)
    {
        //取出用户信息
        $sql = "SELECT * FROM user where user_phone = '{$phone_number}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        //手机号未注册
        if (empty($result)) {
            return array('status' => '0', 'token' => '', 'account_type' => '', 'info' => '手机号未注册');
        }

        //验证密码失败
        if (!password_verify($password, $result[0]['user_password'])) {
            return array('status' => '0', 'token' => '', 'account_type' => '', 'info' => '密码错误');
        }

        //登录成功

        //更新验证字符串token
        $token = random_string('sha1');
        $sql = "UPDATE user SET user_token = '{$token}' WHERE user_phone = '{$phone_number}'";
        $this->db->simple_query($sql);
        
        return array('status' => '1', 'token' => $token, 'account_type' => $result[0]['user_type'], 'info' => '登录成功');

    }

    /**
     * 获取登录信息
     *
     * @param $phpne_number String //手机号
     * @param $password String //明文密码
     *
     * @return Array //json {status:(0:注册失败,1:注册成功)，token: }
     *
     */
    public function get_register($phone_number, $password)
    {

        //查询是否手机号已注册
        $sql = "SELECT * FROM user where user_phone = '{$phone_number}'";
        $query = $this->db->query($sql);

        //手机号已注册
        if (!empty($query->result_array())) {
            return array('status' => '0', 'token' => '', 'info' => '手机号已注册');
        }

        //执行新用户注册
        $hash_password = password_hash($password, PASSWORD_BCRYPT);
        //生成随机字符串，用于验证
        $token = random_string('sha1');
        $user_type = 'user';
        $sql = "INSERT INTO user(user_phone,user_type,user_password,user_token) VALUES ('{$phone_number}','{$user_type}','{$hash_password}','{$token}')";
        //插入失败
        if (!$this->db->simple_query($sql)) {
            log_message('error',"创建用户{$phone_number}失败:{$this->db->error()}");
            return array('status' => '0', 'token' => '', 'info' => $error);
        }
        //注册成功
        return array('status' => '1', 'token' => $token, 'info' => '注册成功');

    }

    /**
     * 修改密码
     */

     public function change_password($phone_number, $token ,$password){

        //验证用户
        $sql = "SELECT * FROM user WHERE user_phone = '{$phone_number}' AND user_token = '{$token}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        if(count($result)==0){
            return array('status' => '0', 'info' => "更改失败");
        }
        
        //执行更改
        $hash_password = password_hash($password, PASSWORD_BCRYPT);
        $sql = "UPDATE  user SET  user_password = '{$hash_password}' WHERE user_phone = '{$phone_number}'";
        //失败
        if (!$this->db->simple_query($sql)) {
            var_dump($this->db->error());
            return array('status' => '0', 'info' => "更改失败");
        }
        //成功
        return array('status' => '1',  'info' => '更改成功');


     }

}
