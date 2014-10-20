<?php

    include_once '../Model/queryHouses.php';
    $getHouse = new queryHouses();     

    include_once '../Model/queryBookmark.php';
    $allMarkedHouses = new queryBookmark();   

    include_once '../Model/queryLocation.php';
    $allHouseLocations = new queryLocation();

    include_once '../Model/queryImages.php';
    $getImages = new queryImages();
        
     // array for areas
     $area_id = array();

     // array for images
     $image_id = array();

     // array for bookmarks
     $bookmark = array();

    $eid = $_POST['eid'];
    $teamName = $_POST['teamName'];

 

     $result = $getHouse -> getHouse($eid);
     $flat_id = $eid; 

     $result_bookmarks = $allMarkedHouses -> getMarkedHouse($teamName, $flat_id);
     $result_location = $allHouseLocations ->  getLocationHouse($flat_id);
     $result_image = $getImages -> getAllImages($flat_id);

         
    
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
     if (mysql_num_rows($result) > 0) {
     	     $response["houses"] = array();
 	     
     	     while ($row = mysql_fetch_array($result)) {
     	     	     $house = array();
            
     	     	     foreach ($area_id as $area) {
     	     	     	     if ($area['id'] == $flat_id) {
     	     	     	     	     $house["area"] = $area['area'];
     	     	     	     	     break;
     	     	     	     }
     	     	     }
	
     	     	     foreach ($bookmark as $bookmarkID){
     	     	     	     if (reset($bookmark) == $flat_id){
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
     	     	     	     if ($image['id'] == $flat_id) {
     	     	     	     	     $house["image"] = $image['directory'];
     	     	     	     }
     	     	     }
     	     	     $house["name"] = $row["Title"];
     	     	     $house["description"] = $row["Description"];
     	     	     $house["datePosted"] = $row["DatePosted"];
     	     	     $house["price"] = $row["Price"];
     	     	     $house["roomType"] = $row["NoBeds"];
     	     	     $house["couples"] = $row["Couples"];
     	     	     $house["dateAvailable"] = $row["DateAvailable"];
     	     	     $house["sellerType"] = $row["sellerType"];

     	     	     array_push($response["houses"], $house);
     	     }    
     	     // success
     	     $response["success"] = 1;
 
     	     // echoing JSON response
     	     echo json_encode($response);
     } else {
     	     // no houses found
     	     $response["success"] = 0;
     	     $response["message"] = "No houses found";
 
     	     // echo no users JSON
     	     echo json_encode($response);
     }
?>		