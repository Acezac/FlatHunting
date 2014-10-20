<?php
 
include_once './saveLog.php';
$log = new saveLogData();

include_once '../Model/queryHouses.php';
$allHouses = new queryHouses();

include_once '../Model/queryBookmark.php';
$allMarkedHouses = new queryBookmark();

include_once '../Model/queryLocation.php';
$allHouseLocations = new queryLocation();

include_once '../Model/queryImages.php';
$getAllImages = new queryImages();
    
$marked = "";

// array for JSON response
$response = array();

// array for areas
$area_id = array();

// array for areas
$image_id = array();

// array for bookmarks
$bookmark = array();

	if (isset($_GET["teamName"])) {
		$teamName2 = $_GET["teamName"];

		// get all products from products table
		$result_houses = $allHouses -> getAllHouses();
		$result_bookmarks =  $allMarkedHouses -> getMarked($teamName2);
		$result_location = $allHouseLocations ->  getLocation();
		$result_image = $getAllImages -> getAllImages();
		$log -> saveData("Show all flats", $_GET["kindOfDevice"]);
		// check for empty result
		
		if (mysql_num_rows($result_bookmarks) > 0) {
			while ($row = mysql_fetch_array($result_bookmarks)) {
				array_push($bookmark, $row["fk_idHouse"]);
			}
		}

		// check for empty result
		if (mysql_num_rows($result_location) > 0) { 
			while ($row = mysql_fetch_array($result_location)) {
				$area = array();
				$area["id"] = $row["idLocation"];
				$area["area"] = $row["Area"];
				$area["latitude"] = $row["Latitude"];
				$area["longitude"] = $row["Longitude"];
				array_push($area_id, $area);
			}
		}

		// check for empty result
		if (mysql_num_rows($result_image) > 0) {
			while ($row = mysql_fetch_array($result_image)) {
				$image = array();
				$image["id"] = $row["fk_idHouseImage"];
				$image["directory"] = $row["directory"];
				array_push($image_id, $image);
			}
		}

		// check for empty result
		if (mysql_num_rows($result_houses) > 0) {
			$response["houses"] = array();
			while ($row = mysql_fetch_array($result_houses)) {
				$house = array();
	
				foreach ($area_id as $area) {
					if ($area['id'] == $row["idHouse"]) {
						$house["area"] = $area['area'];
						$house["longitude"] = $area["longitude"];
						$house["latitude"] = $area["latitude"];
						break;
					}
				}
	
				foreach ($bookmark as $bookmarkID){

					if ($bookmarkID == $row["idHouse"]){
						$house["marked"] = "1";
						break;
					}
					else{
						$house["marked"] = "0";
					}
				}
	
				if(!isset($house["marked"])){
					$house["marked"] = "0";	
				}
	
				foreach ($image_id as $image) {
					if ($image['id'] == $row["idHouse"]) {
						$house["image"] = $image['directory'];
						break;
					}
				}
	
				$house["idHouse"] = $row["idHouse"];
				$house["name"] = $row["Title"];
				$house["datePosted"] = $row["DatePosted"];
				$house["price"] = $row["Price"];
        
				// push single advert into final response array
				array_push($response["houses"], $house);
			}
			// success
			$response["success"] = 1;
 
			// echoing JSON response
			echo json_encode($response);
		} else {
			// no adverts found
			$response["success"] = 0;
			$response["message"] = "No adverts found";
 
			// echo no users JSON
			echo json_encode($response);
		}
	}
?>