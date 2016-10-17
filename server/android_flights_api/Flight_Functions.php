<?php
ini_set("allow_url_fopen", 1);
class Flight_Functions{
	private $conn;
 
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new DB_Connect();
        $this->conn = $db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }
	
	public function storeAirports() {
		$url = "https://iatacodes.org/api/v6/airports?api_key=9b638100-b145-4dcb-bae6-779583796e11";
		$content = file_get_contents($url);
		$jsonData = json_decode($content, true);
		$airports = $jsonData['response'];
		foreach($airports as $item) {
			$row_name = $item['code'] . ', ' . $item['name'];
			echo $row_name;
			$stmt = $this->conn->prepare("INSERT INTO airports(IATACode, name) VALUES(?, ?)");
			$stmt->bind_param("ss", $item['code'], $item['name']);
			$result = $stmt->execute();
			$stmt->close();
			//print 'A message';
		}
    }
	
 
    /**
     * Get airports list
     */
    public function getAirportList() {
		$queryString = 'SELECT * FROM airports';
		$arrayAirports = []; 
		foreach($this->conn->query($queryString) as $row) {
			$row_name = $row['IATACode'].', '.$row['name'];
			//echo $row_name;
			$arrayAirports[] = $row_name;
		}
		return $arrayAirports;
    }
	
	public function getAirportByCode($code) {
		$query = sprintf("SELECT name FROM airports WHERE IATACode = '%s'", $code);
		$result = $this->conn->query($query);
		//return $result['name'];
		foreach ($result as $row) {
			$name = $row['name'];
		}
		return $name;
	}
	
	/**
	* get aiports dynamic
	*/
	public function getDynamicAirportList() {
		$url = "https://iatacodes.org/api/v6/airports?api_key=9b638100-b145-4dcb-bae6-779583796e11";
		$content = file_get_contents($url);
		$jsonData = json_decode($content, true);
		$airports = $jsonData['response'];
		$arrayAirports = []; 
		foreach($airports as $item) {
			//print $item['code'] . ',' . $item['name'];
			$row_name = $item['code'] . ', ' . $item['name'];
			$arrayAirports[] = $row_name;
		}
		return $arrayAirports;
	}
}
?>
