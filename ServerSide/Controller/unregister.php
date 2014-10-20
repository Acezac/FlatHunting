<?php
 
// response json
$json = array();

    // Store user details in db
    include_once './db_functions.php';
    include_once './GCM.php';
 
    $db = new DB_Functions(); 

include_once './saveLog.php';
$log = new saveLogData();
 
/**
 * Registering a user device
 * Store reg id in users table
 */
 
$response = array();
if (isset($_GET["regId"])) {
  
    $regId = $_GET["regId"];
   
   
    $log -> saveData("Unregister", $_GET["kindOfDevice"]);
    
    $db->unregistered($regId);
    $response["message"] = "User unregistered";
    echo json_encode($response);
    
    
} else {
    $response["message"] = "Error";
    echo json_encode("rejected");
}

?>