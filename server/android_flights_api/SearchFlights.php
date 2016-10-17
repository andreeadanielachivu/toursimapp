<?php
function getInformation($slices) {
    $url = "https://www.googleapis.com/qpxExpress/v1/trips/search?key=AIzaSyCKML6Gaqp-ztsOo1DV5A6GtH4eu5U7BSM";

    $postData = '{
                "request": {
                    "passengers": {
                        "adultCount": 1
                        },
                    "slice": ' . json_encode($slices) . ',
					"solutions": 1
                }
            }';

    $curlConnection = curl_init();
    curl_setopt($curlConnection, CURLOPT_HTTPHEADER, array("Content-Type: application/json"));
    curl_setopt($curlConnection, CURLOPT_URL, $url);
    curl_setopt($curlConnection, CURLOPT_CUSTOMREQUEST, "POST");
    curl_setopt($curlConnection, CURLOPT_POSTFIELDS, $postData);
    curl_setopt($curlConnection, CURLOPT_FOLLOWLOCATION, TRUE);
    curl_setopt($curlConnection, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($curlConnection, CURLOPT_SSL_VERIFYPEER, FALSE);
    $results = json_decode(curl_exec($curlConnection), true);
    if (isset($results['error'])) {
        var_dump($results);
        exit();
    }
    // i save the content to a file for better debugging
    //return json_decode(file_get_contents('BOSMUC.JSON'), true);
    return $results;
}

$slices = array(array('origin' => 'TSR', 'destination' => 'SBZ', 'date' => "2016-09-16")
              , array('origin' => 'SBZ', 'destination' => 'TSR', 'date' => "2016-09-20"));

$resultAsArray = getInformation($slices);

$trips = array_filter($resultAsArray['trips']['tripOption'], function($kind) {
        if (!isset($kind['kind'])) {
            return false;
        }
        if ($kind['kind'] == "qpxexpress#tripOption") {
            return true;
        }
        return false;
    });


foreach ($trips as $trip) {
    echo "------- NEW FLIGHT ---------\n";
    echo "FLight Cost: " . $trip['saleTotal'] . "\n";
    foreach ($trip['slice'] as $index => $slice) {
        print "SLICE $index: " . $slices[$index]['origin'] . " TO " . $slices[$index]['destination'] . "\n";
        foreach ($slice['segment'] as $segment) {
            foreach ($segment['leg'] as $leg) {
                print "FROM " . $leg['origin'] . " to " . $leg['destination'] . " (" . $leg['departureTime'] . "-" . $leg['arrivalTime'] . ")" . "\n";
            }
        }
    }
}
?>