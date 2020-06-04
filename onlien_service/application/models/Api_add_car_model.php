<?php

/**
 * Api_add_car_model Model Class
 *
 * Android端添加停车车辆信息模型
 *
 */
class Api_add_car_model extends CI_Model
{

    public function __construct()
    {
        //数据库
        $this->load->database();
        $this->load->model("Verify_user");

    }

    public function add_car($phone_number, $token, $car, $latitude, $longitude, $address, $time)
    {

        $operator = $phone_number;

        //验证用户与token
        $verify_result = $this->Verify_user->verify_operator($operator, $token);
        if ($verify_result['user_type'] != 'operator') {
            return $verify_result;
        }

        //获取车主信息,返回车主手机号或错误信息
        $check_result = $this->_check_car($car);
        if (isset($check_result['status']) && $check_result['status'] == '0') {
            return $check_result;
        }
        $user = $check_result;

        //验证操作员和车辆成功后加入数据库
        $add_result = $this->_add_to_database($car, $operator, $user, $latitude, $longitude, $address, $time);
        return $add_result;

    }

    private function _check_car($car)
    {
        $sql = "SELECT * FROM user where user_car = '{$car}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        //车牌号未注册
        if (empty($result)) {
            return array('status' => '0', 'info' => '该车辆未绑定账号');
        }
        //车主信息
        $user = $result[0]['user_phone'];

        $sql = "SELECT * FROM parking where parking_car = '{$car}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        //该车辆已经在停车
        if (!empty($result)) {
            return array('status' => '0', 'info' => "该车辆已登记");
        }

        //返回车主信息
        return $user;

    }

    private function _add_to_database($car, $operator, $user, $latitude, $longitude, $address, $time)
    {

        $sql = "INSERT INTO parking(parking_car,parking_operator,parking_user,parking_latitude,parking_longitude,parking_address,parking_time)
                    VALUES ('{$car}','{$operator}','{$user}','{$latitude}','{$longitude}','{$address}','{$time}')";
        if (!$this->db->simple_query($sql)) {
            log_message('error', "创建新停车账单 {$car} 失败:{$this->db->error()['message']}");
            return array('status' => '0', 'info' => '未知错误');
        }
        return array('status' => '1', 'info' => '添加成功');
    }

    /**
     * 绑定车辆
     */
    public function bind_car($phone_number, $token, $car)
    {

        //验证用户与token
        $verify_result = $this->Verify_user->verify_user($phone_number, $token);
        if ($verify_result['user_type'] != 'user') {
            return $verify_result;
        }

        
 

        $sql = "SELECT * FROM user where user_car = '{$car}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        //该车辆已经绑定
        if (!empty($result)) {
            return array('status' => '0', 'info' => "该车辆已绑定，无法绑定该车辆");
        }

         
        $sql = "SELECT * FROM bill where bill_user = '{$phone_number}' AND bill_pay_by ='not'";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        //有账单未支付
        if (!empty($result)) {
            return array('status' => '0', 'info' => "您还有账单未支付呢");
        }


        $sql = "SELECT * FROM parking where parking_car = '{$car}' OR parking_user = '{$phone_number}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        //该车辆已经在停车
        if (!empty($result)) {
            return array('status' => '0', 'info' => "正在服务中，无法绑定该车辆");
        }

      
        $sql = "UPDATE user SET user_car = '{$car}' WHERE user_phone='{$phone_number}'";

        if (!$this->db->simple_query($sql)) {
            return array('status' => '0', 'info' => '无法绑定该车辆');
        }

        return array('status' => '1', 'info' => '绑定成功');
    }
    /**
     * 解绑车辆
     */
    public function unbind_car($phone_number, $token)
    {
        //验证用户与token
        $verify_result = $this->Verify_user->verify_user($phone_number, $token);
        if ($verify_result['user_type'] != 'user') {
            return $verify_result;
        }

        $sql = "SELECT * FROM parking where parking_user = '{$phone_number}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        //该车辆已经在停车
        if (!empty($result)) {
            return array('status' => '0', 'info' => "正在服务中，无法解绑");
        }

        $sql = "SELECT * FROM user where user_phone = '{$phone_number}' AND user_car != ''";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        //未绑定任何车辆
        if (empty($result)) {
            return array('status' => '0', 'info' => "您还没绑定车辆呢");
        }

        $car = $result[0]['user_car'];
        $sql = "SELECT * FROM bill where bill_car = '{$car}' AND bill_pay_by ='not'";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        //有账单未支付
        if (!empty($result)) {
            return array('status' => '0', 'info' => "您还有账单未支付呢");
        }

        //解绑
        $sql = "UPDATE  user SET user_car = NULL where user_phone = '{$phone_number}'";
        if (!$this->db->simple_query($sql)) {
            return array('status' => '0', 'info' => '无法解绑该车辆');
        }
        return array('status' => '1', 'info' => '已解除绑定');

    }
}
