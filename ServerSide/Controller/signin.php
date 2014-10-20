<?php
 
// response json
$json = array();

include_once './saveLog.php';
$log = new saveLogData(); 

include_once '../Model/db_functions.php';
include_once '../GCM.php';
 
/**
 * Registering a user device
 * Store reg id in users table
 */
if (isset($_POST["name"]) && isset($_POST["groupName"]) && isset($_POST["regId"])) {
    $name = $_POST["name"];
    $groupName = $_POST["groupName"];
    $gcm_regid = $_POST["regId"]; // GCM Registration ID
    // Store user details in db
    $db = new DB_Functions(); 
    $log -> saveData("Sign in as " .$name, $_POST["kindOfDevice"]);
    $res = $db->storeUser($name, $groupName, $gcm_regid);
        
} else {
    // user details missing
}
?>