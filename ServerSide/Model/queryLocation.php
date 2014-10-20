<?php

class queryLocation{
 
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

     public function getLocation() {
        $result_location = mysql_query("SELECT * FROM location") or die(mysql_error());
        return $result_location;
     }

     public function getLocationHouse($flat_id) {
        $result_location = mysql_query("SELECT * FROM location WHERE idLocation = $flat_id") or die(mysql_error());
        return $result_location;
     }

}
 
?>	