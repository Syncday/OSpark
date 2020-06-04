<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/*
| -------------------------------------------------------------------------
| URI ROUTING
| -------------------------------------------------------------------------
| This file lets you re-map URI requests to specific controller functions.
|
| Typically there is a one-to-one relationship between a URL string
| and its corresponding controller class/method. The segments in a
| URL normally follow this pattern:
|
|	example.com/class/method/id/
|
| In some instances, however, you may want to remap this relationship
| so that a different class/function is called than the one
| corresponding to the URL.
|
| Please see the user guide for complete details:
|
|	https://codeigniter.com/user_guide/general/routing.html
|
| -------------------------------------------------------------------------
| RESERVED ROUTES
| -------------------------------------------------------------------------
|
| There are three reserved routes:
|
|	$route['default_controller'] = 'welcome';
|
| This route indicates which controller class should be loaded if the
| URI contains no data. In the above example, the "welcome" class
| would be loaded.
|
|	$route['404_override'] = 'errors/page_missing';
|
| This route will tell the Router which controller/method to use if those
| provided in the URL cannot be matched to a valid route.
|
|	$route['translate_uri_dashes'] = FALSE;
|
| This is not exactly a route, but allows you to automatically route
| controller and method names that contain dashes. '-' isn't a valid
| class or method name character, so it requires translation.
| When you set this option to TRUE, it will replace ALL dashes in the
| controller and method URI segments.
|
| Examples:	my-controller/index	-> my_controller/index
|		my-controller/my-method	-> my_controller/my_method
*/

// $route['default_controller'] = 'welcome';
// $route['404_override'] = '';
// $route['translate_uri_dashes'] = FALSE;
//$route['default_controller'] = 'index';

$route['default_controller'] = 'admin';

$route['login'] = 'admin/login';
$route['get_login'] = 'admin/get_login';
$route['get_logout'] = 'admin/get_logout';

$route['get_parking_table/(:num)'] = 'admin/get_parking_table/$1';

$route['bills'] = 'admin/bills';
$route['get_bills_table'] = 'admin/get_bills_table';
$route['update_bills'] = 'admin/update_bills';
$route['delete_bills'] = 'admin/delete_bills';
$route['search_bills'] = 'admin/search_bills';

$route['accounts'] = 'admin/accounts';
$route['get_accounts_table'] = 'admin/get_accounts_table';
$route['update_accounts'] = 'admin/update_accounts';
$route['delete_accounts'] = 'admin/delete_accounts';
$route['search_accounts'] = 'admin/search_accounts';
$route['add_accounts'] = 'admin/add_accounts';


$route['charges'] = 'admin/charges';
$route['get_charges_table'] = 'admin/get_charges_table';
$route['delete_charges'] = 'admin/delete_charges';
$route['add_charges'] = 'admin/add_charges';

$route['upload_img'] = 'admin/upload_img';
$route['get_platform_table'] = 'admin/get_platform_table';
$route['delete_platform'] = 'admin/delete_platform';



$route['api'] = 'api';
$route['404_override'] = '';


