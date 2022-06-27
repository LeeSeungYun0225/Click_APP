<?php


	$dbServername = "localhost";
	$dbUsername = "root";
	$dbPassword = "1456";
	$dbName = "loginsystem";

	// Create connection
	$conn = mysqli_connect($dbServername,$dbUsername,$dbPassword,$dbName)or die("Error " . mysqli_error($link));


   if(!$conn)
   {
       echo "MySQL 접속 에러";
       exit();
   }


	mysqli_query("SET NAMES UTF8");

	session_start();

  $appID = $_POST[appID];


	$result = mysqli_query($conn,"DELETE FROM user_homeappliance WHERE ID = '$appID'");
	$row = mysqli_fetch_array($result);

	if($result)
	{
		echo "success_delete";
	}
	else {
		echo "fail_delete";
	}






?>
