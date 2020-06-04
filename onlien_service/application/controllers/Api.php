<?php

/**
 * Api Controller Class
 *
 * Api控制器为Android端的接口，用于处理有关Android的相关请求
 *
 */
class Api extends CI_Controller
{

    public function __construct()
    {
        parent::__construct();
        $this->load->model('Api_register_login_model');
        $this->load->model("Api_add_car_model");
        $this->load->model('Api_get_car_model');
        $this->load->model('Api_get_pay_model');
        $this->load->model('Api_alipay_model');
    }

    /**
     * 请求登录
     */
    public function login()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $password = (string) $get_data['password'];

        //基本验证
        //特殊情况：用户名或者密码为空,用户名或者密码长度大于12，减少SQL注入可能性
        if ($phone_number == '' || $password == '' || strlen($phone_number) > 12 || strlen($password) > 12) {
            $this->_send_json(array('status' => '0', 'token' => '', 'account_type' => '', 'info' => '请检查手机号或密码'));
            return;
        }

        //请求数据库判断密码是否正确
        $result = $this->Api_register_login_model->get_login_info($phone_number, $password);
        //输出登录json
        $this->_send_json($result);

    }

    /**
     * 请求注册
     *
     */
    public function register()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $password = (string) $get_data['password'];

        //基本验证
        //特殊情况：用户名或者密码为空,用户名或者密码长度大于12，减少SQL注入可能性
        if ($phone_number == '' || $password == '' || strlen($phone_number) > 12 || strlen($password) > 12) {
            $this->_send_json(array('status' => '0', 'token' => '', 'info' => '请检查手机号或密码'));
            return;
        }

        //请求数据库进行注册
        $result = $this->Api_register_login_model->get_register($phone_number, $password);

        $this->_send_json($result);

    }

    /**
     * 请求添加车辆
     */

    public function add_car()
    {

        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];
        $car = (string) $get_data['car'];
        $latitude = (float) $get_data['latitude'];
        $longitude = (float) $get_data['longitude'];
        $address = (string) $get_data['address'];

        //获取系统时间
        date_default_timezone_set('prc');
        $time = date("Y-m-d H:i");

        $result = $this->Api_add_car_model->add_car($phone_number, $token, $car, $latitude, $longitude, $address, $time);
        $this->_send_json($result);

    }

    /**
     * 请求操作员的所有当前停车车辆
     */

    public function get_parking_all()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];
        $result = $this->Api_get_car_model->get_parking_all($phone_number, $token);
        $this->_send_json($result);

    }

    /**
     * 请求操作员的所有当前停车车辆
     */

    public function get_parking_detail()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];
        $car = (string) $get_data['car'];

        $result = $this->Api_get_car_model->get_parking_detail($phone_number, $token, $car);
        $this->_send_json($result);

    }

    /**
     * 请求操作员的所有当前停车车辆
     */

    public function get_nearby()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];
        $latitude = (double) $get_data['latitude'];
        $longitude = (double) $get_data['longitude'];

        $result = $this->Api_get_car_model->get_nearby($phone_number, $token, $latitude, $longitude);
        $this->_send_json($result);

    }

    /**
     * 请求系统当前时间
     */
    public function get_time()
    {
        date_default_timezone_set('prc');
        $time = date("Y-m-d H:i");
        $this->_send_json(array("time" => $time));
    }

    /**
     * 操作员获取车辆支付信息
     */
    public function get_pay_info()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];
        $car = (string) $get_data['car'];

        $result = $this->Api_get_pay_model->operator_get_pay_info($phone_number, $token, $car);
        $this->_send_json($result);
    }

    /**
     * 操作员对车辆进行确认支付
     */

    public function operator_get_to_pay()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];
        $car = (string) $get_data['car'];
        $pay_by = (string) $get_data['pay_by'];

        $result = $this->Api_get_pay_model->operator_get_to_pay($phone_number, $token, $car, $pay_by);
        $this->_send_json($result);

    }

    /**
     * 操作员获取历史服务记录
     */
    public function get_history()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];

        $result = $this->Api_get_pay_model->get_history($phone_number, $token);
        $this->_send_json($result);

    }

    /**
     * 用户获取价格表
     */
    public function get_price_list()
    {
        $result = $this->Api_get_pay_model->get_price_list();
        $this->_send_json($result);
    }

    /**
     * 用户获取当前服务状态
     */
    public function get_service_status()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];

        $result = $this->Api_get_pay_model->get_service_status($phone_number, $token);
        $this->_send_json($result);
    }

    /**
     * 用户获取自己的车辆
     */
    public function get_user_car()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];

        $result = $this->Api_get_car_model->get_user_car($phone_number, $token);
        $this->_send_json($result);
    }

    /**
     * 用户获取未支付信息
     */

    public function get_unpaid()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];

        $result = $this->Api_get_pay_model->get_unpaid($phone_number, $token);
        $this->_send_json($result);

    }

    /**
     * 用户获取已支付信息
     */

    public function get_paid()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];

        $result = $this->Api_get_pay_model->get_paid($phone_number, $token);
        $this->_send_json($result);

    }
    /**
     * 用户绑定车辆
     */

    public function get_bind_car()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];
        $car = (string) $get_data['car'];

        $result = $this->Api_add_car_model->bind_car($phone_number, $token, $car);
        $this->_send_json($result);
    }
    /**
     * 用户解绑车辆
     */

    public function get_unbind_car()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];

        $result = $this->Api_add_car_model->unbind_car($phone_number, $token);
        $this->_send_json($result);
    }

    /**
     * 用户更改密码
     */
    public function get_change_password()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];
        $password = (string) $get_data['password'];

        $result = $this->Api_register_login_model->change_password($phone_number, $token, $password);
        $this->_send_json($result);
    }

    /**
     * 用户请求支付宝支付订单
     */

    public function get_alipay_order()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $phone_number = (string) $get_data['phone_number'];
        $token = (string) $get_data['token'];
        $id = (string) $get_data['id'];

        $result = $this->Api_alipay_model->get_order($phone_number, $token, $id);
        $this->_send_json($result);
    }
    /**
     * 用户返回支付账单，验证支付结果
     */
    public function get_verify_order()
    {
        //获取post的数据，并转换为Array
        $get_data = json_decode(file_get_contents('php://input'), true);
        $order_id = (string) $get_data['order_id'];

        $result = $this->Api_alipay_model->verify_order($order_id);
        $this->_send_json($result);
    }

    /**
     * 向前端返回json数据
     */
    private function _send_json($json)
    {
        $this->output
            ->set_content_type('application/json')
            ->set_output(json_encode($json));
    }

}
