<?php

class queryUser {
 
    private $db;
 
    //put your code here
    // constructor
    function __construct() {
        include_once '../db_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }

     public function UpdateStatus($regId, $status) {
        $result = mysql_query("UPDATE user SET Disable = '$status' WHERE regID = '$regId'");
        return $result;
     }

     public function getTeam($regID) {
        $result_team = mysql_query("SELECT fk_teamName FROM user WHERE regId = '$regID'") or die(mysql_error());
        return $result_team;
     }

     public function getActive($team) {
        $result = mysql_query("SELECT regID,Disable FROM user WHERE fk_teamName = '$team' AND Disable = '1'") or die(mysql_error());
        return $result;
     }
}
 
?>	