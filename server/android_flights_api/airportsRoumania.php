<?php
	require_once 'Flight_Functions.php';
	$flight = new Flight_Functions();
 
	// json response array
	$response = array("error" => FALSE);
 
	$json = file_get_contents('php://input');
	$obj = json_decode($json);

if (isset($obj->{'request'})) {
 
    // receiving the post params
    $request_type = $obj->{'request'};
	if (strcmp($request_type, "airports") == 0) {
		$listAirport = $flight->getAirportList();
		//$flight->storeAirports();
		//$listAirport = $flight->getDynamicAirportList();
		$response["error"] = FALSE;
		$response["airports"] = $listAirport;
		echo json_encode($response);
	} else if (strcmp($request_type, "request_iata_code") == 0) {
		if (isset($obj->{'iataCode'})) {
			$code = $obj->{'iataCode'};
			$name_airport = $flight->getAirportByCode($code);
			$response["error"] = FALSE;
			$response["name"] = $name_airport;
			echo json_encode($response);
		} else {
			$response["error"] = TRUE;
			$response["error_msg"] = "Airport Code isn't set";
			echo json_encode($response);
		}
	} else {
		$response["error"] = TRUE;
		$response["error_msg"] = "Request type isn't for airports!";
		echo json_encode($response);
	}
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Request type isn't set!";
    echo json_encode($response);
}
?>