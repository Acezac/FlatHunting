<?php

class queryBookmark{
 
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

     public function getMarked($teamName2) {
        $result_bookmarks = mysql_query("SELECT * FROM bookmark WHERE fk_teamName = '$teamName2'");
        return $result_bookmarks;
     }

     public function getMarkedHouse($teamName, $flat_id) {
        $result_bookmarks = mysql_query("SELECT * FROM bookmark WHERE fk_teamName = '$teamName' && fk_idHouse = $flat_id");
        return $result_bookmarks;
     }

     public function deleteMarked($team, $eid) {
        $result = mysql_query("DELETE FROM bookmark WHERE fk_teamName = '$team' AND fk_idHouse = '$eid'");
        return $result;
     }

     public function updateMarked($team, $eid) {
        $result_insert = mysql_query("INSERT INTO bookmark(fk_teamName, fk_idHouse) VALUES('$team', '$eid')");
        return $result_insert ;
     }

}
 
?>	