<?php

/**
 * Api_get_pay_model Model Class
 *
 * Android端获取车辆信息模型
 *
 */
class Api_get_pay_model extends CI_Model
{

    public function __construct()
    {
        //数据库
        $this->load->database();
        $this->load->model('Verify_user');

    }

    //操作员获取车辆支付信息
    public function operator_get_pay_info($phone_number, $token, $car)
    {
        //验证用户
        $verify_result = $this->Verify_user->verify_operator($phone_number, $token);
        if ($verify_result['user_type'] != 'operator') {
            return $verify_result;
        }

        //获取车辆基本信息
        $sql = "SELECT parking_time,parking_address FROM parking WHERE parking_car = '{$car}'";
        $query = $this->db->query($sql);
        $result = $query->row();
        $sys_time = $this->_get_system_time();
        $time = (int) strtotime($sys_time) - strtotime($result->parking_time); //停车时间，单位，秒;

        if ($time != 0) { //转换为分钟
            $time = $time / 60;
        }
        $address = $result->parking_address; //停车地址

        //获取价格
        $price = $this->_get_car_price($car);

        //获取支付二维码url
        $sql = "SELECT * FROM platform";
        $query = $this->db->query($sql);
        $pay = $query->result_array();

        return array('car' => $car, 'time' => $time, 'address' => $address, 'price' => $price, 'pays' => $pay);

    }

    /**
     * 操作员对车辆进行结单
     */
    public function operator_get_to_pay($phone_number, $token, $car, $pay_by)
    {
        //验证用户
        $verify_result = $this->Verify_user->verify_operator($phone_number, $token);
        if ($verify_result['user_type'] != 'operator') {
            return $verify_result;
        }

        //记录账单
        //获取车辆的停车信息
        $sql = "SELECT parking_user,parking_time,parking_address FROM parking WHERE parking_car = '{$car}'";
        $query = $this->db->query($sql);
        $result = $query->row();
        if (!isset($result->parking_time) && !isset($result->parking_address)) {
            return array('status' => 0, 'info' => "该车辆已支付");
        }
        $parking_user = $result->parking_user;
        $parking_time = $result->parking_time;
        $parking_address = $result->parking_address;
        //获取价格
        $price = $this->_get_car_price($car);
        $sys_time = $this->_get_system_time();

        //价格不为零则登记
        if ($price != 0) {
            $id = (String) $phone_number . (string) time();

            $sql = "INSERT INTO bill
                    VALUES('{$id}','{$car}','{$parking_user}','{$phone_number}','{$parking_time}','{$sys_time}','{$parking_address}','{$price}','{$pay_by}')";
            //记录到账单
            if (!$this->db->simple_query($sql)) { //无法登记
                $error_info = var_export($this->db->error(), true);
                log_message('error', "get_pay_model: 记录账单出错{$error_info}");
                return array('status' => 0, 'info' => "无法记录该车辆账单");
            }

        }

        //将车辆从当前停车表删除
        $sql = "DELETE FROM parking WHERE parking_car = '{$car}'";
        if (!$this->db->simple_query($sql)) { //无法删除
            return array('status' => 0, 'info' => "无法更改该车辆服务状态");
        }

        return array('status' => 1, 'info' => "登记账单成功");

    }
    /**
     * 操作员获取历史账单
     */
    public function get_history($phone_number, $token)
    {

        //验证用户
        $verify_result = $this->Verify_user->verify_operator($phone_number, $token);
        if ($verify_result['user_type'] != 'operator') {
            return $verify_result;
        }

        //获取
        $sql = "SELECT * FROM bill WHERE bill_operator = '{$phone_number}' ORDER BY bill_finish_time DESC";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        return array('count' => count($result), 'bills' => $result);

    }

    /**
     * 获取价格表
     */

    public function get_price_list()
    {

        $sql = "SELECT price_timeline AS timeLine,price_value AS price FROM price ORDER BY price_timeline ASC";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        return array('price_list' => $result);
    }

    /**
     * 用户获取服务状态
     */
    public function get_service_status($phone_number, $token)
    {

        //验证用户
        $verify_result = $this->Verify_user->verify_user($phone_number, $token);
        if ($verify_result['user_type'] != 'user') {
            return $verify_result;
        }

        $sql = "SELECT parking_time,parking_car,parking_operator,parking_address FROM parking WHERE parking_user = '{$phone_number}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        //不在停车表
        if (empty($result)) {
            return array('status' => 0, 'price' => 0);
        }
        $sys_time = $this->_get_system_time();
        $time = (int) strtotime($sys_time) - strtotime($result[0]['parking_time']); //停车时间，单位，秒;

        if ($time != 0) { //转换为分钟
            $time = $time / 60;
        }

        $price = $this->_get_car_price($result[0]['parking_car']);
        $operator = $result[0]['parking_operator'];
        $address = $result[0]['parking_address'];
        $car = $result[0]['parking_car'];

        return array('status' => 1, 'car' => $car, 'price' => $price, 'operator' => $operator, 'address' => $address, 'time' => $time);

    }

    /**
     * 用户获取未支付账单
     */
    public function get_unpaid($phone_number, $token)
    {
        //验证用户
        $verify_result = $this->Verify_user->verify_user($phone_number, $token);
        if ($verify_result['user_type'] != 'user') {
            return $verify_result;
        }

        $sql = "SELECT * FROM bill WHERE bill_user = '{$phone_number}' AND bill_pay_by = 'not'ORDER BY bill_finish_time DESC";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        return array('count' => count($result), 'bills' => $result);

    }

    /**
     * 用户获取已支付账单
     */
    public function get_paid($phone_number, $token)
    {
        //验证用户
        $verify_result = $this->Verify_user->verify_user($phone_number, $token);
        if ($verify_result['user_type'] != 'user') {
            return $verify_result;
        }

        $sql = "SELECT * FROM bill WHERE bill_user = '{$phone_number}' AND bill_pay_by != 'not' ORDER BY bill_finish_time DESC";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        return array('count' => count($result), 'bills' => $result);

    }

    /**
     * 获取系统时间
     */
    private function _get_system_time()
    {
        date_default_timezone_set('prc');
        $time = date("Y-m-d H:i");
        return $time;
    }
    /**
     * 获取车辆停车价格
     */
    private function _get_car_price($car)
    {
        //获取停车时间
        $sql = "SELECT parking_time,parking_address FROM parking WHERE parking_car = '{$car}'";
        $query = $this->db->query($sql);
        $result = $query->row();
        $sys_time = $this->_get_system_time();
        $time = (int) strtotime($sys_time) - strtotime($result->parking_time); //停车时间，单位，秒;

        if ($time != 0) { //转换为分钟
            $time = $time / 60;
        }
        //根据时间获取价格
        $sql = "SELECT * FROM price ORDER BY price_timeline DESC";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        $price = 0; //价格
        $flag = 0;
        foreach ($result as $list) {
            if ($time <= $list['price_timeline']) {
                $price = $list['price_value'];
                $flag = 1;
            } else {
                break;
            }
        }
        if ($flag == 0) {
            $price = $result[0]['price_value'];
        }
        if ($time == 0) {
            $price = 0;
        }
        return $price;
    }

}
