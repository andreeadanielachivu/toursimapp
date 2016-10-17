<?php
class DB_Functions {
	private $conn;
 
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
 
        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt);
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            return $user;
        } else {
            return false;
        }
    }
	
	/**
	* Insert location in table
	*/
	public function storeLocation($name, $address, $phone, $latitude, $longitude, $rating, $website, $placeid, $id_placetype) {
		$stmt = $this->conn->prepare("INSERT INTO place(name, address, phone, latitude, longitude, rating, website, placeid, id_placetype) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("sssdddssi", $name, $address, $phone, $latitude, $longitude, $rating, $website, $placeid, $id_placetype);
        $result = $stmt->execute();
        $stmt->close();
		//echo 'Testing';
		if ($result) {
			$stmt = $this->conn->prepare("SELECT * FROM place WHERE placeid = ?");
            $stmt->bind_param("s", $placeid);
            $stmt->execute();
            $place = $stmt->get_result()->fetch_assoc();
            $stmt->close();
			$id_place = $place['id'];
            return $id_place;
		} else {
			return false;
		}
	}
	
	public function storeIdPlace($placetype, $category) {
		$stmt = $this->conn->prepare("SELECT id from category WHERE name = ?");
		$stmt->bind_param("s", $category);
		if ($stmt->execute()) {
            // user existed 
			$category_result = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			$id_category = $category_result['id'];
        } else {
            // user not existed
            $stmt->close();
			$id_category = -1;	
        }
		if ($id_category > 0) {
			$stmt = $this->conn->prepare("INSERT INTO placetype(name, id_category) VALUES(?, ?)");
			$stmt->bind_param("si", $placetype, $id_category);
			$result = $stmt->execute();
			$stmt->close();
			//echo 'Testing';
			if ($result) {
				$stmt = $this->conn->prepare("SELECT * FROM placetype WHERE name = ?");
				$stmt->bind_param("s", $placetype);
				$stmt->execute();
				$place = $stmt->get_result()->fetch_assoc();
				$stmt->close();
				$id_place = $place['id'];
				return $id_place;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	public function storeHistory($id_location, $id_user) {
		$stmt = $this->conn->prepare("INSERT INTO history(id_place, id_user) VALUES(?, ?)");
        $stmt->bind_param("ii", $id_location, $id_user);
        $result = $stmt->execute();
		$stmt->close();
		if ($result) {
			$stmt = $this->conn->prepare("SELECT * FROM history WHERE id_place = ? AND id_user = ?");
            $stmt->bind_param("ii", $id_location, $id_user);
            $stmt->execute();
            $history = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $history;
		} else {
			return false;
		}
	}
 
    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {
 
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
 
        $stmt->bind_param("s", $email);
 
        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            // verifying user password
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $user;
            }
        } else {
            return NULL;
        }
    }
 
	public function getUserByEmailAndName($email, $name) {
		$stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ? AND name = ?");
 
        $stmt->bind_param("ss", $email, $name);
 
        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
	}
 
 
    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");
 
        $stmt->bind_param("s", $email);
 
        $stmt->execute();
 
        $stmt->store_result();
 
        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }
	
	public function deleteFromDb($name, $email) {
		$stmt = $this->conn->prepare("DELETE from users WHERE email = ? AND name = ?");
 
        $stmt->bind_param("ss", $email, $name);
 
        if ($stmt->execute()) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
	}
	
	/**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
 
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
 
        return $hash;
    }
	
	public function storeLocationType($id_place, $id_types) {
		echo 'Call store function';
		/*$stmt = $this->conn->prepare("INSERT INTO place_placetype(id_place, id_placetype) VALUES(?, ?)");
        $stmt->bind_param("ii", $id_place, $id_type);
        $result = $stmt->execute();
		$stmt->close();
		echo 'Store location' . $id_place . '=>' . $id_type;
		if (!$result) {
			throw new Exception($mysqli->error);
		}
		if ($result) {
			echo "something";
            return true;
		} else {
			return false;
		}*/
		$stmt = $this->conn->prepare("INSERT INTO place_placetype(id_place, id_placetype) VALUES(?, ?)");
		$id = 2;
		$stmt->bind_param("ii", $id_place, $id);
		foreach ($id_types as $id) {
			echo 'Value id = ' . $id;
			$stmt->execute();
		}
		$stmt->close();
	}
	
	public function getIdPlaceType($typeOfplace) {
		$stmt = $this->conn->prepare("SELECT id from placetype WHERE name = ?");
		$stmt->bind_param("s", $typeOfplace);
		if ($stmt->execute()) {
            // user existed 
			$placetype = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			$id_place_type = $placetype['id'];
            return $id_place_type;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
	}
	
	public function isPlaceExist($name, $placeid) {
		$stmt = $this->conn->prepare("SELECT id from place WHERE name = ? AND placeid = ?");
		$stmt->bind_param("ss", $name, $placeid);
		if ($stmt->execute()) {
            // user existed 
			$place = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			$id_place = $place['id'];
            return $id_place;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
	}
	
	public function isPlaceinHistory($id_place, $id_user) {
		$stmt = $this->conn->prepare("SELECT * from history WHERE id_user = ? AND id_place = ?");
		$stmt->bind_param("ii", $id_user, $id_place);
		if ($stmt->execute()) {
            // user existed 
			$result = $stmt->get_result()->fetch_assoc();
			$stmt->close();
            return $result;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
	}
	
	public function storeLocationPlaceType($id_location, $typeOfplace) {
		$start = 0;
		$position = strpos($typeOfplace,",", $start);
		$id_types = [];
		while ($position) {
			$place_type = substr($typeOfplace, $start, $position - $start);
			echo "For place name: " . $place_type . '||||||';
			$id_type = $this->getIdPlaceType(trim($place_type));
			//id_place = $this->storeLocationType($id_location, $id_type);
			$id_types[] = $id_type;
			$start = $position + 1;
			$position = strpos($typeOfplace,",", $start);
		}
		$place_type = substr($typeOfplace, $start);
		echo $place_type . '|""""Last one';
		$id_type = $this->getIdPlaceType(trim($place_type));
		$id_types[] = $id_type;
		$id_place = $this->storeLocationType($id_location, $id_types);
	}
	
	public function getAllPlaces($id_user) {
		
		$queryString = sprintf("SELECT id_place FROM history 
				WHERE id_user='%s'", $id_user);
		$places = [];
		foreach($this->conn->query($queryString) as $row) {
			$row_name = $row['id_place'];
			//echo $row_name;
			$places[] = $row_name;
		}
		return $places;
	}
	
	public function getPlacesByCategory($id_user, $category) {
		if (strcmp($category, "all") == 0) {
			$queryString = sprintf("create temporary table placesByCategory
								as
								select p.name as place_name , p.address as place_address, p.phone as place_phone
								, p.placeid as place_id, p.website as place_website, p.rating as place_rating, 
								p.latitude as place_lat, p.longitude as place_lng,
								p.id as id_place, pt.name as type_name
								from place p inner join placetype pt
								on p.id_placetype = pt.id
								inner join category c
								on pt.id_category = c.id");
		} else {
			$queryString = sprintf("create temporary table placesByCategory
								as
								select p.name as place_name , p.address as place_address, p.phone as place_phone
								, p.placeid as place_id, p.website as place_website, p.rating as place_rating, 
								p.latitude as place_lat, p.longitude as place_lng,
								p.id as id_place, pt.name as type_name
								from place p inner join placetype pt
								on p.id_placetype = pt.id
								inner join category c
								on pt.id_category = c.id
								where c.name = '%s'", $category);
		}
		
		$result = $this->conn->query($queryString);
		$query = sprintf("
						select * from placesByCategory p inner join history h
						on p.id_place = h.id_place
						where h.id_user = '%s'", $id_user);
		$places = [];
		foreach($this->conn->query($query) as $row) {
			$row_name = $row['place_name'];
			//echo $row_name;
			$places[] = $row;
		}
		return $places;
	}
 
}
?>
