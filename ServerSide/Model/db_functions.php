<?php

class DB_Functions {
 
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
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $teamName, $regID) {
        // insert user into database

        $result = mysql_query("INSERT INTO user(regID, Username, fk_teamName) VALUES('$regID', '$name', '$teamName')");
        // check for successful store

        if ($result) {

            // get user details
            $id = mysql_insert_id(); // last inserted id
            $result = mysql_query("SELECT * FROM user WHERE idUser = $id") or die(mysql_error());
            // return user details
            if (mysql_num_rows($result) > 0) {
                return mysql_fetch_array($result);
            } else {
                return false;
            }
        }else {
       mysql_query("UPDATE user SET fk_teamName = '$teamName' WHERE regID = '$regID'");
        

        } 
        $result2 = mysql_query("SELECT * FROM user WHERE regID = '$regID' && Username = '$name'") or die(mysql_error());
        echo mysql_num_rows($result2);
        if(mysql_num_rows($result2) == 0){
        	echo "fdsf";
        	mysql_query("UPDATE user SET Username = '$name' WHERE regID = '$regID'");
        }
        
        
    }

    public function createTeam($teamName) {
        // insert user into database

        $result = mysql_query("INSERT INTO team(teamName) VALUES('$teamName')");

        // check for successful store
        if ($result) {
            // get user details         
            $result = mysql_query("SELECT * FROM team WHERE teamName LIKE '$teamName'") or die(mysql_error());
            // return user details          

            if (mysql_num_rows($result) > 0) {
                return mysql_fetch_array($result);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }



    public function unregistered($regId) {
        // insert user into database
        $result = $result = mysql_query("DELETE FROM user WHERE regID = '$regId'");
        echo $result;
    }
 
    
 
}
 
?>