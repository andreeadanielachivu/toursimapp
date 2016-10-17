<?php

$data = array ( "request" => array(
            "passengers" => array( 
                    "adultCount" => 1
                        ),
                    "slice" => array( 
                            array(
                                "origin" => "TSR",
                                "destination" => "OTP",
                                "date" => "2016-09-09"),
                            array(
                                "origin" => "OTP",
                                "destination" => "TSR",
                                "date" => "2016-09-10"),
                            ),
                                "solutions" => "1"
                            ),                   
             );              		 
$data_string = json_encode($data);
$ch = curl_init('https://www.googleapis.com/qpxExpress/v1/trips/search?key=AIzaSyCKML6Gaqp-ztsOo1DV5A6GtH4eu5U7BSM');                                                                      
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");                                                                     
curl_setopt($ch, CURLOPT_POSTFIELDS, $data_string);                                                                  
curl_setopt($ch, CURLOPT_RETURNTRANSFER, false);                                                                      
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));                                                                                                                   

$result = curl_exec($ch);
curl_close($ch);

/* then I echo the result for testing purposes */

echo json_encode($result);

?>