<?php

/**
 * Web_get_data_model Model Class
 *
 * 网页端获取数据模型
 *
 */
class Web_get_data_model extends CI_Model
{

    public function __construct()
    {
        //数据库
        $this->load->database();
        $this->load->model('Init_database_model');
        
    }



    /**
     * 登录
     */
    public function login($name, $password)
    {
        //echo $hash_password = password_hash($password, PASSWORD_BCRYPT);
        //取出信息
        $sql = "SELECT admin_password FROM admin where admin_name = '{$name}'";
        $query = $this->db->query($sql);
        $admin_password = isset($query->row()->admin_password)?$query->row()->admin_password:"";

        //验证密码失败
        if (!password_verify($password, $admin_password)) {
            return array('status' => '0', 'token' => '',  'info' => '登陆失败');
        }
        
          //更新验证字符串token
          $token = random_string('sha1');
          $sql = "UPDATE admin SET admin_token = '{$token}' WHERE admin_name = '{$name}'";
          $this->db->simple_query($sql);

          return array('status'=>1,'name'=>$name,'token'=>$token,'info'=>"欢迎{$name}");
    }

    /**
     * 获取首页初始数据
     */
    public function get_index_data()
    {
        //获取停车数量
        $sql = "SELECT COUNT(*) AS day_parking FROM parking";
        $query = $this->db->query($sql);
        $day_parking = $query->row()->day_parking;
        //周停车数量
        $sql = "SELECT COUNT(*) AS wek_parking FROM bill WHERE DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= DATE(bill_finish_time)";
        $query = $this->db->query($sql);
        $wek_parking = (double) $query->row()->wek_parking + (double) $day_parking;

        //日收入
        $sql = "SELECT SUM(bill_price) AS day_income FROM bill WHERE to_days(bill_finish_time) = to_days(now()) AND bill_pay_by !='not'";
        $query = $this->db->query($sql);
        $day_income = $query->row()->day_income;
        //周收入
        $sql = "SELECT SUM(bill_price) AS wek_income FROM bill WHERE DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= DATE(bill_finish_time) AND bill_pay_by !='not'";
        $query = $this->db->query($sql);
        $wek_income = $query->row()->wek_income;

        //未支付
        $sql = "SELECT COUNT(*) AS unpaid_count FROM bill WHERE  bill_pay_by ='not'";
        $query = $this->db->query($sql);
        $unpaid_count = $query->row()->unpaid_count;
        $sql = "SELECT SUM(bill_price) AS unpaid_sum FROM bill WHERE  bill_pay_by ='not'";
        $query = $this->db->query($sql);
        $unpaid_sum = $query->row()->unpaid_sum;

        //停车表格数据总数
        $sql = "SELECT COUNT(*) AS parking_count  FROM parking ";
        $query = $this->db->query($sql);
        $parking_count = $query->row()->parking_count;

        return array(
            'day_parking' => $day_parking,
            'wek_parking' => $wek_parking,
            'day_income' => $day_income == null ? 0 : $day_income,
            'wek_income' => $wek_income == null ? 0 : $wek_income,
            'unpaid_count' => $unpaid_count == null ? 0 : $unpaid_count,
            'unpaid_sum' => $unpaid_sum == null ? 0 : $unpaid_sum,
            'parking_count' => $parking_count == null ? 0 : $parking_count,
        );

    }
    /**
     * 请求首页中的表格数据
     */
    public function get_index_parking_table($page)
    {

        //停车数据
        $start = ($page - 1) * 10; //每页10条数据
        $sql = "SELECT * FROM parking LIMIT $start,10";
        $query = $this->db->query($sql);
        $parking_info = $query->result_array();

        return array('parking_data' => $parking_info);
    }

    /**
     * 请求账单信息
     * @param $page 页数
     * @param $limit 每页的数据
     */
    public function get_bills_table($page, $limit)
    {

        //数据总数
        $sql = "SELECT COUNT(*) AS billS_count  FROM bill ";
        $query = $this->db->query($sql);
        $billS_count = $query->row()->billS_count;

        //每页的数据
        $start = ($page - 1) * $limit; //每页数据
        $sql = "SELECT * FROM bill LIMIT $start,$limit";
        $query = $this->db->query($sql);
        $bills_data = $query->result_array();
        //编写为符合layui框架的数据格式
        return array('code' => 0, 'msg' => '', 'count' => $billS_count, 'data' => $bills_data);
    }
    /**
     * 搜索账单
     */
    public function search_bills($data, $page, $limit)
    {

        //拼接查找条件
        $filter = "";
        foreach ($data as $key => $value) {
            if ($value != null) {
                if (strlen($filter) == 0) {
                    $filter = $filter . " $key = '{$value}' ";
                } else {
                    $filter = $filter . " AND $key = '{$value}' ";
                }
            }
        }
        if (strlen($filter) == 0) {
            return array('code' => 0, 'msg' => '没有数据', 'count' => '0', 'data' => '');
        }
        //每页的数据
        $start = ($page - 1) * $limit; //每页数据
        $sql = "SELECT * FROM bill WHERE $filter LIMIT $start,$limit";
        $query = $this->db->query($sql);
        $bills_data = $query->result_array();

        $start = ($page - 1) * $limit; //每页数据
        $sql = "SELECT COUNT(*) AS bills_count FROM bill WHERE $filter ";
        $query = $this->db->query($sql);
        $bills_count = $query->row()->bills_count;

        return array('code' => 0, 'msg' => '', 'count' => $bills_count, 'data' => $bills_data);

    }

    /**
     * 请求账号信息
     * @param $page 页数
     * @param $limit 每页的数据
     */
    public function get_accounts_table($page, $limit)
    {

        //数据总数
        $sql = "SELECT COUNT(*) AS accounts_count  FROM user ";
        $query = $this->db->query($sql);
        $accounts_count = $query->row()->accounts_count;

        //每页的数据
        $start = ($page - 1) * $limit; //每页数据
        $sql = "SELECT * FROM user LIMIT $start,$limit";
        $query = $this->db->query($sql);
        $account_data = $query->result_array();
        //编写为符合layui框架的数据格式
        return array('code' => 0, 'msg' => '', 'count' => $accounts_count, 'data' => $account_data);
    }
    /**
     * 搜索账号
     */
    public function search_accounts($data, $page, $limit)
    {

        //拼接查找条件
        $filter = "";
        foreach ($data as $key => $value) {
            if ($value != null) {
                if (strlen($filter) == 0) {
                    $filter = $filter . " $key = '{$value}' ";
                } else {
                    $filter = $filter . " AND $key = '{$value}' ";
                }
            }
        }
        if (strlen($filter) == 0) {
            return array('code' => 0, 'msg' => '没有数据', 'count' => '0', 'data' => '');
        }
        //每页的数据
        $start = ($page - 1) * $limit; //每页数据
        $sql = "SELECT * FROM user WHERE $filter LIMIT $start,$limit";
        $query = $this->db->query($sql);
        $accounts_data = $query->result_array();

        $start = ($page - 1) * $limit; //每页数据
        $sql = "SELECT COUNT(*) AS user_count FROM user WHERE $filter ";
        $query = $this->db->query($sql);
        $user_count = $query->row()->user_count;

        return array('code' => 0, 'msg' => '', 'count' => $user_count, 'data' => $accounts_data);

    }

    /**
     * 获取收费表
     */
    public function get_charges_table($page, $limit)
    {
        //数据总数
        $sql = "SELECT COUNT(*) AS price_count FROM price ";
        $query = $this->db->query($sql);
        $price_count = $query->row()->price_count;

        //每页的数据
        $start = ($page - 1) * $limit; //每页数据
        $sql = "SELECT * FROM price  ORDER BY price_timeline DESC LIMIT $start,$limit ";
        $query = $this->db->query($sql);
        $price_data = $query->result_array();
        //编写为符合layui框架的数据格式
        return array('code' => 0, 'msg' => '', 'count' => $price_count, 'data' => $price_data);

    }

    /**
     * 获取二维码表
     */
    public function get_platform_table($page, $limit)
    {
        //数据总数
        $sql = "SELECT COUNT(*) AS platform_count FROM  platform";
        $query = $this->db->query($sql);
        $platform_count = $query->row()->platform_count;

        //每页的数据
        $start = ($page - 1) * $limit; //每页数据
        $sql = "SELECT * FROM platform LIMIT $start,$limit ";
        $query = $this->db->query($sql);
        $platform_data = $query->result_array();
        //编写为符合layui框架的数据格式
        return array('code' => 0, 'msg' => '获取成功', 'count' => $platform_count, 'data' => $platform_data);
    }

}
