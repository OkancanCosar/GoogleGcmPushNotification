<?php 
require_once("baglan.php");

 if (isset($_GET["regId"])) {
 
 $regId = $_GET['regId'];
 $sql = "INSERT INTO wp_gcm_kullanicilar (registration_id) values ('$regId')";

 if(!mysqli_query($con, $sql)){
  die('MySQL query failed'.mysql_error());
	}
 }
mysqli_close($con);
 
 
?>