<?php

/**
 * Init_database_Model Model Class
 *
 *  初始化数据库，检查并创建表
 *
 */
class Init_database_model extends CI_Model
{

    public function __construct()
    {
        $this->load->database();
        $this->_checkTableAndCreate();
    }

    private function _checkTableAndCreate()
    {
        //数据库中应该创建的表
        $tables = array('admin','user', 'parking', 'chat', 'price', 'platform', 'bill', 'note');

        foreach ($tables as $table) {
            if (!$this->db->table_exists($table)) {
                $this->_createTable($table);
            }
        }
    }

    private function _createTable($table)
    {
        switch ($table) {
            case "admin":
                $sql = "CREATE TABLE IF NOT EXISTS `admin`(
                    `admin_name` VARCHAR(20) NOT NULL,
                    `admin_password` VARCHAR(100) NOT NULL,
                    `admin_token` VARCHAR(100) ,
                    PRIMARY KEY (`admin_name`)
                 )";
                if (!$this->db->simple_query($sql)) {
                    log_message('error', "InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败:{$this->db->error()}");
                    show_error("InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败", 500, "出错！");
                }
                break;
            case "user":
                $sql = "CREATE TABLE IF NOT EXISTS `user`(
                    `user_phone` VARCHAR(20) NOT NULL,
                    `user_type` VARCHAR(20) NOT NULL,
                    `user_car` VARCHAR(20) ,
                    `user_password` VARCHAR(100) NOT NULL,
                    `user_token` VARCHAR(100),
                    PRIMARY KEY ( `user_phone` )
                 )";
                if (!$this->db->simple_query($sql)) {
                    log_message('error', "InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败:{$this->db->error()}");
                    show_error("InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败", 500, "出错！");
                }
                break;
            case 'parking':
                $sql = "CREATE TABLE IF NOT EXISTS `parking`(
                    `parking_car` VARCHAR(20) NOT NULL,
                    `parking_operator` VARCHAR(20) NOT NULL,
                    `parking_user` VARCHAR(20) NOT NULL,
                    `parking_latitude` DECIMAL(9,6) NOT NULL,
                    `parking_longitude` DECIMAL(9,6) NOT NULL,
                    `parking_address` VARCHAR(100) NOT NULL,
                    `parking_time` DATETIME NOT NULL,
                    PRIMARY KEY ( `parking_car` )
                 )";
                if (!$this->db->simple_query($sql)) {
                    log_message('error', "InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败:{$this->db->error()}");
                    show_error("InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败", 500, "出错！");
                }
                break;
            case 'chat':
                $sql = "CREATE TABLE IF NOT EXISTS `chat`(
                        `chat_type` VARCHAR(20) NOT NULL,
                        `chat_from` VARCHAR(20) NOT NULL,
                        `chat_nickname` VARCHAR(50) NOT NULL,
                        `chat_to` VARCHAR(20) NOT NULL,
                        `chat_content` TEXT NOT NULL,
                        `chat_time` DATETIME NOT NULL
                     )";
                if (!$this->db->simple_query($sql)) {
                    log_message('error', "InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败:{$this->db->error()}");
                    show_error("InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败", 500, "出错！");
                }
                break;
            case 'price':
                $sql = "CREATE TABLE IF NOT EXISTS `price`(
                            `price_timeline` INT NOT NULL,
                            `price_value` DECIMAL(5,2) NOT NULL,
                            PRIMARY KEY ( `price_timeline` )

                         )";
                if (!$this->db->simple_query($sql)) {
                    log_message('error', "InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败:{$this->db->error()}");
                    show_error("InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败", 500, "出错！");
                }
                break;
            case 'platform':
                $sql = "CREATE TABLE IF NOT EXISTS `platform`(
                                `platform_app` VARCHAR(50)  NOT NULL,
                                `platform_url` VARCHAR(150)  NOT NULL,
                                `platform_time` DATETIME  NOT NULL,
                                PRIMARY KEY ( `platform_app` )
                             )";
                if (!$this->db->simple_query($sql)) {
                    log_message('error', "InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败:{$this->db->error()}");
                    show_error("InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败", 500, "出错！");
                }
                break;
            case 'bill':
                $sql = "CREATE TABLE IF NOT EXISTS `bill`(
                                    `bill_id` VARCHAR(50) NOT NULL,
                                    `bill_car` VARCHAR(20) NOT NULL,
                                    `bill_user` VARCHAR(20) NOT NULL,
                                    `bill_operator` VARCHAR(20) NOT NULL,
                                    `bill_create_time` DATETIME NOT NULL,
                                    `bill_finish_time` DATETIME NOT NULL,
                                    `bill_address` VARCHAR(100) NOT NULL,
                                    `bill_price` DECIMAL(5,2) NOT NULL,
                                    `bill_pay_by` VARCHAR(20) NOT NULL,
                                    PRIMARY KEY ( `bill_id` )
                                 )";
                if (!$this->db->simple_query($sql)) {
                    $error_info = var_export($this->db->error(), true);
                    log_message('error', "InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败:{$error_info}");
                    show_error("InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败", 500, "出错！");
                }
                break;
            case 'note':
                $sql = "CREATE TABLE IF NOT EXISTS `note`(
                                        `note_from` VARCHAR(20) NOT NULL,
                                        `note_nickname` VARCHAR(50) NOT NULL,
                                        `note_to` VARCHAR(20) NOT NULL,
                                        `note_content` VARCHAR(100) NOT NULL,
                                        `note_latitude` DECIMAL(9,6),
                                        `note_longitude` DECIMAL(9,6)

                                     )";
                if (!$this->db->simple_query($sql)) {
                    log_message('error', "InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败:{$this->db->error()}");
                    show_error("InitDatabase_Model: 数据库初始化时出错，表{$table}创建失败", 500, "出错！");
                }
                break;
        }
    }
}
