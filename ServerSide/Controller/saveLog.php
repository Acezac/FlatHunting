<?php


class saveLogData {    
 
    //put your code here
    // constructor
    function __construct() {
       
    }
 
    // destructor
    function __destruct() {
         
    }
     public function saveData($data, $kindOfDevice){
     	     $file = '../Log.txt';
     	     date_default_timezone_set('Europe/London');
     	     $date = date('d/m/y h:i:s a', time());

     	     $store_data = $data .str_repeat(' ', 30 - strlen($data)) .'| ' .$date .str_repeat(' ', 25 - strlen($date)) .'| ' .$kindOfDevice . PHP_EOL;
     	     // Write the contents to the file, 
     	     // using the FILE_APPEND flag to append the content to the end of the file
     	     // and the LOCK_EX flag to prevent anyone else writing to the file at the same time
     	     file_put_contents($file, $store_data, FILE_APPEND | LOCK_EX);
     	}
}

?>