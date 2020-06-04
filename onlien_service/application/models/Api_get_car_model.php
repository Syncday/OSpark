<?php

/**
 * Api_get_car_model Model Class
 *
 * Android端获取车辆信息模型
 *
 */
class Api_get_car_model extends CI_Model
{

    public function __construct()
    {
        //数据库
        $this->load->database();
        $this->load->model('Verify_user');

    }

    //操作员获取自己当前服务中所有停车信息
    public function get_parking_all($phone_number, $token)
    {

        //验证用户
        $verify_result = $this->Verify_user->verify_operator($phone_number, $token);
        if ($verify_result['user_type'] != 'operator') {
            return $verify_result;
        }

        //获取信息
        $sql = "SELECT parking_car,parking_user,parking_time,parking_address,parking_latitude,parking_longitude FROM parking where parking_operator = '{$phone_number}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        //返回车辆信息
        return array('status' => '1', 'cars' => $result, 'info' => '获取成功');

    }

    //操作员获取自己附近的车辆
    public function get_nearby($phone_number, $token, $latitude, $longitude)
    {

        //验证用户
        $verify_result = $this->Verify_user->verify_operator($phone_number, $token);
        if ($verify_result['user_type'] != 'operator') {
            return $verify_result;
        }

        //返回的指定位置范围的车辆，这里的范围为以操作员为中心的100m
        $max_latitudd = $latitude + 0.001;
        $min_latitudd = $latitude - 0.001;
        $max_longitude = $longitude + 0.001;
        $min_longitude = $longitude - 0.001;

        //获取信息
        $sql = "SELECT parking_car,parking_user,parking_time,parking_address,parking_latitude,parking_longitude FROM parking
                where ( parking_operator = '{$phone_number}' )
                AND ( parking_latitude BETWEEN $min_latitudd AND $max_latitudd )
                AND ( parking_longitude BETWEEN $min_longitude AND $max_longitude )";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        //返回车辆信息
        return array('status' => '1', 'cars' => $result, 'info' => '获取成功');

    }

    //操作员获取某停车车辆信息
    public function get_parking_detail($phone_number, $token, $car)
    {

        //验证操作员
        $verify_result = $this->Verify_user->verify_operator($phone_number, $token);
        if ($verify_result['user_type'] != 'operator') {
            return $verify_result;
        }

        //获取信息
        $sql = "SELECT parking_car,parking_time,parking_address,parking_latitude,parking_longitude FROM parking where parking_car = '{$car}'";
        $query = $this->db->query($sql);
        $result = $query->result_array();

        //返回车辆信息
        return array('status' => '1', 'cars' => $result, 'info' => '获取成功');

    }

    
    /**
     * 用户获取自己的车辆
     */
    public function get_user_car($phone_number, $token)
    {
        //验证用户
        $verify_result = $this->Verify_user->verify_user($phone_number, $token);
        if ($verify_result['user_type'] != 'user') {
            return $verify_result;
        }
        $sql = "SELECT user_car FROM user WHERE user_phone = '{$phone_number}'";
        $query = $this->db->query($sql);
        $car = isset($query->row()->user_car) ? $query->row()->user_car : '';

        return array("status" => 1, "car" => $car);

    }


}