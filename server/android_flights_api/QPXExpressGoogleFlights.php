<?php
	require '/vendor/autoload.php';
	use GuzzleHttp\Client;
	$api_key="AIzaSyCKML6Gaqp-ztsOo1DV5A6GtH4eu5U7BSM";

	$json = file_get_contents('php://input');
	$obj = json_decode($json);
	$response = array("error" => FALSE);
	
	if (isset($obj->{'from'}) && isset($obj->{'to'}) && isset($obj->{'dateTur'}) && isset($obj->{'dateRetur'})) {
		$fromAirport = $obj->{'from'};
		$toAirport = $obj->{'to'};
		$dateFrom = $obj->{'dateTur'};
		$dateTo = $obj->{'dateRetur'};
		$client = new Google_Client();
		$client->setApplicationName("TravelApp");
		$client->setDeveloperKey($api_key);
		
		// initating
		$qpxExpressService = new Google_Service_QPXExpress($client);
		$trips = $qpxExpressService->trips;
		
		// passengers
		$passenger = new Google_Service_QPXExpress_PassengerCounts();
		$passenger->setAdultCount(1);

		// slices / trips
		/*$slices = [];
		$slice = new Google_Service_QPXExpress_SliceInput();
		$slice->setDestination('SBZ');
		$slice->setOrigin('TSR');
		$slice->setDate('2016-09-09');
		$slices[] = $slice;*/
		//$slices = array(array('origin' => 'TSR', 'destination' => 'SBZ', 'date' => "2016-09-09")
			//	  , array('origin' => 'SBZ', 'destination' => 'TSR', 'date' => "2016-09-16"));
		
		$slices = array(array('origin' => $fromAirport, 'destination' => $toAirport, 'date' => $dateFrom)
				  , array('origin' => $toAirport, 'destination' => $fromAirport, 'date' => $dateTo));
		
		$request = new Google_Service_QPXExpress_TripOptionsRequest();
		$request->setSolutions(5);
		$request->setPassengers($passenger);
		$request->setSlice($slices);

		$searchRequest = new Google_Service_QPXExpress_TripsSearchRequest();
		$searchRequest->setRequest($request);
		
		// search
		$result = $qpxExpressService->trips->search($searchRequest);
		$trips_res = $result->getTrips();
		$res1 = $result['trips']['tripOption'];
		
		$allFlights = [];
		$prices = [];
		foreach ($res1 as $trip) {
			$flightReturn = [];
			foreach ($trip['slice'] as $index => $slice) {
				$sliceTrip = [];
				foreach ($slice['segment'] as $segment) {
					foreach ($segment['leg'] as $leg) {
						$sliceTrip[] = array(
							"origin" => $leg['origin'],
							"destination" => $leg['destination'],
							"departureTime" => $leg['departureTime'],
							"arrivalTime" => $leg['arrivalTime'],
							"aircraft" => $leg['aircraft']
						);
					}
				}
				$flightReturn[] = array(
					"flight" => $sliceTrip,
					"duration" => $slice['duration']
				);
			}
			//$allFlights[] = $flightReturn;
			$allFlights[] = array(
				"flightTR" => $flightReturn,
				"price" => $trip['saleTotal']
			);
		}
		//echo json_encode($allFlights);
		$response['error'] = FALSE;
		$response['flights'] = $allFlights;
		echo json_encode($response);
	} else {
		$response["error"] = TRUE;
		$response["error_msg"] = "Data fields aren't set!";
		echo json_encode($response);
	}
    
?>