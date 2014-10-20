<?php
      
   include_once '../Model/queryTeam.php';
   $getTeam = new queryTeam();

   $name = $_GET["groupName"];

   $result = $getTeam -> getTeamName($name);

   if(mysql_num_rows($result)>0){
      $response["flag"] = "exists";
      echo json_encode($response);
   }
   else{
      $response["message"] = "Team does not exist";
      echo json_encode($response);
   }

?>