<?php

class queryTeam {
 
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

     public function getTeamName($name) {
        $data = mysql_query("SELECT * FROM team WHERE teamName = '$name'"); 
        return $data;
     }

  
}
 
?>	