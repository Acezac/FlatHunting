<?php

include_once './saveLog.php';
$log = new saveLogData();
 
include_once '../Model/queryUser.php';
$update = new queryUser();        

    if (isset($_GET["regId"]) && isset($_GET["status"])) {
    	    $status = $_GET["status"];
    	    $regId = $_GET["regId"];
            
            $update -> UpdateStatus($regId, $status );
             
            if($status == 1){
               $log -> saveData("Enabled Notifications", $_GET["kindOfDevice"]);
            }
            if($status == 0){
               $log -> saveData("Disabled Notifications", $_GET["kindOfDevice"]);
            }

    	    $result = mysql_query("UPDATE user SET Disable = '$status' WHERE regID = '$regId'");
            echo $result;
}

?>			