<?php

class queryHouses{
 
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

     public function getAllHouses() {
        $result_houses = mysql_query("SELECT * FROM house ORDER BY datePosted DESC") or die(mysql_error());
        return $result_houses;
     }

     public function getHouse($eid) {
        $result = mysql_query("SELECT * FROM house WHERE idHouse = $eid");
        return $result;
     }

}
 
?>	