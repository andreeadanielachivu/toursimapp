<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
$json = file_get_contents('php://input');
$obj = json_decode($json);

if (isset($obj->email) && isset($obj->name)) {
		$email = $obj->email;
		$name = $obj->name;
		$db->deleteFromDb($name, $email);
		$response["error"] = FALSE;
		$response["error_msg"] = "Delete user FB";
		echo json_encode($response);
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email or password is missing!";
    echo json_encode($response);
}
?>