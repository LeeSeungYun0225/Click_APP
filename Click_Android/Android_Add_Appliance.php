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

  $userID = $_POST[userID];
	$model = $_POST[model];
	$nickname = $_POST[nickname];
	$comment = $_POST[comment];
	$type = $_POST[type];
	$manu = $_POST[manu];



	$sql = "INSERT INTO user_homeappliance (uidUsers,appliance,nickname,comment,type,manufacturer) VALUES ('$userID','$model','$nickname','$comment','$type','$manu')";

	$result = mysqli_query($conn,$sql);
	$id = mysqli_insert_id($conn);
    if($result){
        echo "success_save/";
				echo "$id/";
    }
    else
    {
        echo "fail_save";
    }



?>
