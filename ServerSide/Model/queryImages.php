<?php

class queryImages{
 
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

     public function getImages($eid) {
        $result_image = mysql_query("SELECT * From image WHERE fk_idHouseImage = $eid ") or die(mysql_error());
        return $result_image;
     }

     public function getAllImages() {
        $result_image = mysql_query("SELECT * From image") or die(mysql_error());
        return $result_image;
     }

}
 
?>	