<?php

/**
 * 网页控制器
 */

class Admin extends CI_Controller
{

    public function __construct()
    {
        parent::__construct();

        $this->load->database();
        //初始化数据库
        $this->load->model('Init_database_model');
        $this->load->model('Web_get_data_model');
        $this->load->model('Web_set_data_model');
        $this->load->helper(array('form', 'url'));
        $this->load->helper('file');
        $this->load->library('session');

        $this->_verify();
    }

    /**
     * 根据session验证管理员
     */
    private function _verify()
    {
        //登录界面不跳转，避免无限重定向
        if ($_SERVER['REQUEST_URI'] == "/login" || $_SERVER['REQUEST_URI'] == "/index.php/login"
            || $_SERVER['REQUEST_URI'] == "/get_login" || $_SERVER['REQUEST_URI'] == "/index/get_login") {
            return;
        }
        
        if (!isset($_SESSION['name']) || !isset($_SESSION['token'])) {
            //重定向登录界面
            redirect('https://syncday.com/login');
            return;
        }
        $name = $_SESSION['name'];
        $token = $_SESSION['token'];

        $sql = "SELECT admin_token FROM admin WHERE admin_name='{$name}'";
        $query = $this->db->query($sql);
        $admin_token = $query->row()->admin_token;

        if ($token != $admin_token) {
            //重定向登录界面
            redirect('https://syncday.com/login');
        }

    }

    /**
     * 登录页面
     */
    public function login()
    {
        $this->load->view('login');
    }

    /**
     * 请求登录
     */
    public function get_login()
    {
        $data = json_decode(file_get_contents('php://input'), true);
        $name = $data['admin_name'];
        $password = $data['admin_password'];
        $result = $this->Web_get_data_model->login($name, $password);
        //保存session
        $this->session->set_userdata('name', $name);
        $this->session->set_userdata('token', $result['token']);

        $this->_send_json($result);
    }
    /**
     * 登出
     */
    public function get_logout(){
        //删除session
        unset($_SESSION['name']);
        unset($_SESSION['token']);
         //重定向登录界面
         redirect('https://syncday.com/login');
    }

    /**
     * 主页
     */
    public function index()
    {
        $this->load->view('templates/header');
        $this->load->view('index', $this->Web_get_data_model->get_index_data());
        $this->load->view('templates/footer');
    }
    /**
     * 获取主页的停车数据
     * @param $page 停车数据的页数
     */
    public function get_parking_table($page)
    {
        $result = $this->Web_get_data_model->get_index_parking_table($page);
        $this->_send_json($result);
    }

    /**
     * 账单页面
     */
    public function bills()
    {
        $this->load->view('templates/header');
        $this->load->view('bills');
        $this->load->view('templates/footer');
    }

    /**
     * 获取账单表格信息
     */
    public function get_bills_table()
    {

        $page = $this->input->get('page', true);
        $limit = $this->input->get('limit', true);

        //默认10条数据
        if (!isset($limit) || $limit == null) {
            $limit = 10;
        }
        $result = $this->Web_get_data_model->get_bills_table($page, $limit);
        $this->_send_json($result);
    }
    /**
     * 搜索账单
     */
    public function search_bills()
    {

        $get_data = $this->input->get('data', true);
        $page = $this->input->get('page', true);
        $limit = $this->input->get('limit', true);

        $result = $this->Web_get_data_model->search_bills($get_data, $page, $limit);
        $this->_send_json($result);

    }
    /**
     * 更新账单
     */
    public function update_bills()
    {
        $get_data = json_decode(file_get_contents('php://input'), true);
        $result = $this->Web_set_data_model->update_bills($get_data);
        $this->_send_json($result);

    }
    /**
     * 删除账单
     */
    public function delete_bills()
    {
        $get_data = json_decode(file_get_contents('php://input'), true);

        $result = $this->Web_set_data_model->delete_bills($get_data);
        $this->_send_json($result);

    }

    /**
     * 账号页面
     */
    public function accounts()
    {
        $this->load->view('templates/header');
        $this->load->view('accounts');
        $this->load->view('templates/footer');
    }

    /**
     * 获取账号表格信息
     */
    public function get_accounts_table()
    {

        $page = $this->input->get('page', true);
        $limit = $this->input->get('limit', true);

        //默认10条数据
        if (!isset($limit) || $limit == null) {
            $limit = 10;
        }
        $result = $this->Web_get_data_model->get_accounts_table($page, $limit);
        $this->_send_json($result);
    }
    /**
     * 搜索账号
     */
    public function search_accounts()
    {

        $get_data = $this->input->get('data', true);
        $page = $this->input->get('page', true);
        $limit = $this->input->get('limit', true);

        $result = $this->Web_get_data_model->search_accounts($get_data, $page, $limit);
        $this->_send_json($result);

    }
    /**
     * 更新账号
     */
    public function update_accounts()
    {
        $get_data = json_decode(file_get_contents('php://input'), true);
        $result = $this->Web_set_data_model->update_accounts($get_data);
        $this->_send_json($result);

    }
    /**
     * 删除账号
     */
    public function delete_accounts()
    {
        $get_data = json_decode(file_get_contents('php://input'), true);

        $result = $this->Web_set_data_model->delete_accounts($get_data);
        $this->_send_json($result);

    }
    /**
     * 添加账号
     */
    public function add_accounts()
    {
        $get_data = json_decode(file_get_contents('php://input'), true);

        $result = $this->Web_set_data_model->add_accounts($get_data);
        $this->_send_json($result);

    }

    /**
     * 收费标准页面
     */

    public function charges()
    {
        $this->load->view('templates/header');
        $this->load->view('charges');
        $this->load->view('templates/footer');
    }
    //获取表格
    public function get_charges_table()
    {

        $page = $this->input->get('page', true);
        $limit = $this->input->get('limit', true);

        //默认10条数据
        if (!isset($limit) || $limit == null) {
            $limit = 10;
        }
        $result = $this->Web_get_data_model->get_charges_table($page, $limit);
        $this->_send_json($result);
    }
    //添加价格
    public function add_charges()
    {
        $get_data = json_decode(file_get_contents('php://input'), true);

        $result = $this->Web_set_data_model->add_charges($get_data);
        $this->_send_json($result);
    }
    //删除价格
    public function delete_charges()
    {
        $get_data = json_decode(file_get_contents('php://input'), true);

        $result = $this->Web_set_data_model->delete_charges($get_data);
        $this->_send_json($result);
    }

    /**
     * 上传支付二维码
     */
    public function upload_img()
    {

        $file_name = $_FILES['file']['name'];

        $config['upload_path'] = './static/payQR/';
        $config['allowed_types'] = '*';
        $config['max_size'] = 100;
        $config['max_width'] = 1024;
        $config['max_height'] = 768;

        $this->load->library('upload', $config);

        //删除同名文件
        $file_path = "static/payQR/" . $file_name;
        if (file_exists($file_path)) {
            unlink($file_path);
        }

        //保存到文件夹
        if (!$this->upload->do_upload('file')) {
            $error = array('error' => $this->upload->display_errors());
            $this->_send_json(array('code' => 0, 'info' => "上传失败:" . var_export($error, true)));
        } else {
            $data = array('upload_data' => $this->upload->data());

            $file_name = explode(".", $file_name)[0];

            $result = $this->Web_set_data_model->save_img_path($file_name, "https://syncday.com/" . $file_path);

            $this->_send_json($result);
        }
    }

    /**
     * 获取支付表格
     */
    public function get_platform_table()
    {
        $page = $this->input->get('page', true);
        $limit = $this->input->get('limit', true);

        //默认10条数据
        if (!isset($limit) || $limit == null) {
            $limit = 10;
        }
        $result = $this->Web_get_data_model->get_platform_table($page, $limit);
        $this->_send_json($result);
    }
    //删除
    public function delete_platform()
    {
        $get_data = json_decode(file_get_contents('php://input'), true);

        $result = $this->Web_set_data_model->delete_platform($get_data);
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
