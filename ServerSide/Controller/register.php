<?php
 
// response json
$json = array();
 
/**
 * Registering a user device
 * Store reg id in users table
 */

include_once './saveLog.php';
$log = new saveLogData();

include_once '../Model/db_functions.php';
include_once '../GCM.php';
 
$db = new DB_Functions();
$gcm = new GCM();
 
$response = array();

	if (isset($_POST["groupName"])) {
		$groupName = $_POST["groupName"];

		if($reg = $db->createTeam($groupName)){ 
			$response["message"] = "Team registered";
			echo json_encode($response);
			$log -> saveData("Register a team ".$groupName, $_POST["kindOfDevice"]);
		}
		else{
			$response["message"] = "Team name exists";
			echo json_encode($response);
		}
	} else {    
	}
?>