<?php

/**
 * Web_set_data_model Model Class
 *
 * 网页端更改数据模型
 *
 */
class Web_set_data_model extends CI_Model
{

    public function __construct()
    {
        //数据库
        $this->load->database();
        $this->load->helper('string');
    }

    /**
     * 账单相关
     * @param $data array 数据
     */
    public function update_bills($data)
    {

        $id = $data['bill_id'];
        $car = $data['bill_car'];
        $user = $data['bill_user'];
        $operator = $data['bill_operator'];
        $create_time = $data['bill_create_time'];
        $finish_time = $data['bill_finish_time'];
        $address = $data['bill_address'];
        $price = $data['bill_price'];
        $pay_by = $data['bill_pay_by'];

        $sql = "UPDATE bill SET bill_car='{$car}',bill_user='{$user}'
                    ,bill_operator='{$operator}',bill_create_time='{$create_time}'
                    ,bill_finish_time='{$finish_time}',bill_address='{$address}'
                    ,bill_price='{$price}',bill_pay_by='{$pay_by}'
                    WHERE bill_id = '{$id}'";
        if (!$this->db->simple_query($sql)) {
            $error_info = var_export($this->db->error(), true);
            return array('status' => 0, 'info' => "无法修改账单：$error_info");
        }
        return array("status" => 1, 'info' => '修改成功');
    }
    //删除
    public function delete_bills($data)
    {
        $id = $data['bill_id'];
        $sql = "DELETE FROM bill WHERE bill_id = '{$id}'";
        if (!$this->db->simple_query($sql)) {
            $error_info = var_export($this->db->error(), true);
            return array('status' => 0, 'info' => "无法删除账单：$error_info");
        }
        return array('status' => 1, 'info' => "删除成功");

    }

    /**
     * 账号相关
     * @param $data array 数据
     */
    public function update_accounts($data)
    {

        $phone = $data['user_phone'];
        $car = isset($data['user_car']) ? $data['user_car'] : "";
        $password = $data['user_password'];
        $token = $data['user_token'];
        $type = $data['user_type'];

        //查询车辆是否已绑定
        $sql = "SELECT * FROM user where user_car = '{$car}' AND user_phone!='{$phone}'";
        $query = $this->db->query($sql);
        if (!empty($query->result_array())&&$car!=null) {
            return array('status' => '0', 'info' => '车辆已绑定其他用户');
        }

        $sql = "UPDATE user SET user_car='{$car}',user_password='{$password}'
                    ,user_token='{$token}',user_type='{$type}' WHERE user_phone = '{$phone}'";
        if (!$this->db->simple_query($sql)) {
            $error_info = var_export($this->db->error(), true);
            return array('status' => 0, 'info' => "无法修改账号：$error_info");
        }
        return array("status" => 1, 'info' => '修改成功');
    }
    //删除
    public function delete_accounts($data)
    {
        $id = $data['user_phone'];
        $sql = "DELETE FROM user WHERE user_phone = '{$id}'";
        if (!$this->db->simple_query($sql)) {
            $error_info = var_export($this->db->error(), true);
            return array('status' => 0, 'info' => "无法删除账号：$error_info");
        }
        return array('status' => 1, 'info' => "删除成功");
    }
    //添加
    public function add_accounts($data)
    {
        $phone_number = $data['user_phone'];
        $user_car = isset($data['user_car']) ? $data['user_car'] : null;
        $user_type = $data['user_type'];

        //查询是否手机号已注册
        $sql = "SELECT * FROM user where user_phone = '{$phone_number}'";
        $query = $this->db->query($sql);

        //手机号已注册
        if (!empty($query->result_array())) {
            return array('status' => '0', 'info' => '手机号已注册');
        }
        //查询车辆是否已绑定
        $sql = "SELECT * FROM user where user_car = '{$user_car}'";
        $query = $this->db->query($sql);
        if (!empty($query->result_array())&&$user_car!=null) {
            return array('status' => '0', 'info' => '车辆已绑定其他用户');
        }

        //执行新用户注册,密码默认为123456
        $hash_password = password_hash("123456", PASSWORD_BCRYPT);
        //生成随机字符串，用于验证
        $token = random_string('sha1');
        $sql = "INSERT INTO user(user_phone,user_car,user_type,user_password,user_token) VALUES ('{$phone_number}','{$user_car}','{$user_type}','{$hash_password}','{$token}')";
        //插入失败
        if (!$this->db->simple_query($sql)) {
            log_message('error', "创建用户{$phone_number}失败:{$this->db->error()}");
            return array('status' => '0', 'info' => "添加失败");
        }
        //添加成功
        return array('status' => '1', 'info' => '添加成功,密码默认为123456');

    }
    /**
     * 收费标准
     */
    //添加
    public function add_charges($data)
    {
        $timeline = $data['price_timeline'];
        $value = $data['price_value'];

        $sql = "INSERT INTO price(price_timeline,price_value) VALUES($timeline,$value)";
        if (!$this->db->simple_query($sql)) {
            $error_info = var_export($this->db->error(), true);
            return array('status' => 0, 'info' => "添加失败：$error_info");
        }
        return array('status' => 1, 'info' => "添加成功");
    }

    //删除
    public function delete_charges($data)
    {
        $timeline = $data['price_timeline'];
        $sql = "DELETE FROM price WHERE price_timeline = '{$timeline}'";
        if (!$this->db->simple_query($sql)) {
            $error_info = var_export($this->db->error(), true);
            return array('status' => 0, 'info' => "无法删除：$error_info");
        }
        return array('status' => 1, 'info' => "删除成功");
    }

    /**
     * 保存二维码路径到数据库
     */
    public function save_img_path($name, $path)
    {

        date_default_timezone_set('prc');
        $time = date("Y-m-d H:i");

        $time = "\"{$time}\"";
        $name = "\"{$name}\"";
        $path = "\"{$path}\"";

        $sql = "SELECT * FROM platform WHERE platform_app = $name";
        $query = $this->db->query($sql);
        $result = $query->result_array();
        //创建
        if (empty($result)) {
            $sql = "INSERT INTO platform VALUES($name,$path,$time)";
            if (!$this->db->simple_query($sql)) {
                $error_info = var_export($this->db->error(), true);
                return array('status' => 0, 'info' => "添加二维码失败：$error_info");
            }
            return array('status' => 1, 'info' => "上传成功");
        } else { //更新
            $sql = "UPDATE  platform SET platform_url = $path,platform_time = $time WHERE platform_app = $name";
            if (!$this->db->simple_query($sql)) {
                $error_info = var_export($this->db->error(), true);
                return array('status' => 0, 'info' => "添加二维码失败：$error_info");
            }
            return array('status' => 1, 'info' => "上传成功");
        }
    }
    //删除二维码
    public function delete_platform($data)
    {

        $platform = $data['platform_app'];
        $url = $data['platform_url'];
        $tmp = explode(".", $url);
        $img_type = $tmp[sizeof($tmp) - 1];
        $sql = "DELETE FROM platform WHERE platform_app = '{$platform}'";
        if (!$this->db->simple_query($sql)) {
            $error_info = var_export($this->db->error(), true);
            return array('status' => 0, 'info' => "无法删除：$error_info");
        }
        return array('status' => 1, 'info' => "删除成功");
    }

}
