<?php
 
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);

$json = file_get_contents('php://input');
$obj = json_decode($json);

if (isset($obj->email) && isset($obj->username) && isset($obj->request)) {
	$email = $obj->email;
	$username = $obj->username;
	$request = $obj->request;
	
	if (strcmp($request, "save") == 0) {
		if (isset($obj->name) && isset($obj->address) && isset($obj->phone) && isset($obj->latitude) && isset($obj->longitude)
			&& isset($obj->rating) && isset($obj->website) && isset($obj->typeplace) && isset($obj->placeid) && isset($obj->category)) {
	 
			// receiving the post params
			$name = $obj->name;
			$address = $obj->address;
			$phone = $obj->phone;
			$latitude = $obj->latitude;
			$longitude = $obj->longitude;
			$rating = $obj->rating;
			$website = $obj->website;
			$typeplace = $obj->typeplace;
			$placeid = $obj->placeid;
			$category = $obj->category;
	 
			//id_user
			$user = $db->getUserByEmailAndName($email, $username);
			$id_user = $user['id'];
			//$id_placetype = $db->getIdPlaceType(trim($typeplace));
			if ($db->getIdPlaceType(trim($typeplace))) {
				$id_placetype = $db->getIdPlaceType(trim($typeplace));
			} else {
				$id_placetype = $db->storeIdPlace($typeplace, $category);
			}
			//id place
			if ($db->isPlaceExist($name, $placeid)) {
				$id_location = $db->isPlaceExist($name, $placeid);
			} else {
				$id_location = $db->storeLocation($name, $address, $phone, $latitude, $longitude, $rating, $website, $placeid, $id_placetype);
			}
			//echo 'id location' . $id_location;
			if ($db->isPlaceinHistory($id_location, $id_user)) {
				$history_response = $db->isPlaceinHistory($id_location, $id_user);
			} else {
				$history_response = $db->storeHistory($id_location, $id_user);
			}
			$response["error"] = FALSE;
			$response["id_history"] = $history_response;
			echo json_encode($response);
		} else {
			$response["error"] = TRUE;
			$response["error_msg"] = "Save params aren't set!";
			echo json_encode($response);
		}
	} else if (strcmp($request, "getCategory") == 0){
		if (isset($obj->category)) {
			$category = $obj->category;
			//id_user
			$user = $db->getUserByEmailAndName($email, $username);
			$id_user = $user['id'];
			$places = $db->getPlacesByCategory($id_user, trim($category));
			$response["error"] = FALSE;
			$response["places"] = $places;
			echo json_encode($response);
		} else {
			$response["error"] = TRUE;
			$response["error_msg"] = "Category isn't set!";
			echo json_encode($response);
		}
	} else {
		$response["error"] = TRUE;
		$response["error_msg"] = "Request type is missing!";
		echo json_encode($response);
	}
	
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters is missing!";
    echo json_encode($response);
}
?>