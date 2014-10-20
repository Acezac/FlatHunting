<?php
 
include_once '../GCM.php';
$gcm = new GCM();

include_once './saveLog.php';
$log = new saveLogData();

include_once '../Model/queryUser.php';
$getUser = new queryUser();  

    function __destruct() {
         
    }
    
    if (isset($_GET["regId"]) && isset($_GET["message"])) {
	$regID = $_GET["regId"];

        $result_team = $getUser -> getTeam($regID);
        $row_teamName = mysql_fetch_array($result_team);
        $team = $row_teamName["fk_teamName"];

        $result = $getUser -> getActive($team);

        $eid = $_GET["message"];
        $log -> saveData("Selects flat with id= " .$eid, $_GET["kindOfDevice"]);

        $regids[] = array();

        while ($row = mysql_fetch_array($result)) {
           // temp user array
   	   if($row["regID"] != $regID){
              // push single advert into final response array
              array_push($regids, $row["regID"]);
           }
      }
	
      $result2 = count($regids);

      for ($i = 0; $i < $result2; $i++) {

          $regId = $regids[$i];
          $message = $_GET["message"];

          $registatoin_ids = array($regId);
          $message = array("price" => $message);

          $gcm->send_notification($regId, $message);
      }
}
?>