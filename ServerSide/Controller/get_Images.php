<?php
   
   include_once './saveLog.php';
   $log = new saveLogData();

   include_once '../Model/queryImages.php';
   $getAllImages = new queryImages(); 

   // array for images
   $image_id = array();

   $eid = $_GET['eid'];
    
   $result_image = $getAllImages -> getImages($eid);
    
   $log -> saveData("Opened the viewPager for id=" .$eid, $_GET["kindOfDevice"]);

    // check for empty result
    if (mysql_num_rows($result_image) > 0) {
       $response["images"] = array();
       while ($row = mysql_fetch_array($result_image)) {
	  $image = array();
	  $image["id"] = $row["fk_idHouseImage"];
	  $image["directory"] = $row["directory"];
          array_push($response["images"], $image);
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