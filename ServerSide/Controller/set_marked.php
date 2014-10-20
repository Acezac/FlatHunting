<?php
 
include_once '../GCM.php';
$gcm = new GCM();

include_once './saveLog.php';
$log = new saveLogData();

include_once '../Model/queryUser.php';
$getUser = new queryUser();  

include_once '../Model/queryBookmark.php';
$updateMarked = new queryBookmark(); 
 
// check for post data
if (isset($_GET["eid"]) && isset($_GET["regId"])) {
    $eid = $_GET['eid'];
    $mid = $_GET['regId'];
    
    $result_team = $getUser -> getTeam($mid );
    $row_teamName = mysql_fetch_array($result_team);
    $team = $row_teamName["fk_teamName"];

    $flat_id = $eid;
        
    $log -> saveData("Marked the flat with id=" .$flat_id, $_GET["kindOfDevice"]);
 
    $result_insert = $updateMarked -> updateMarked($team, $eid);;

    $result = $getUser -> getActive($team);

    $regids[] = array();

    while ($row = mysql_fetch_array($result)) {
   	if($row["regID"] != $mid){
        // push single advert into final response array
        array_push($regids, $row["regID"]);
        }
    }
	
    $result2 = count($regids);

    for ($i = 0; $i < $result2; $i++) { 
    	    $regId = $regids[$i];
    	    $message = $_GET["eid"];
    	    $registatoin_ids = array($regId);

    	    $message = array("price" => $message);
    	    $result = $gcm->send_notification($regId, $message);
    	    echo $result;
    }
}
?>	