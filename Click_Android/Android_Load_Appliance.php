<?php


	$dbServername = "localhost";
	$dbUsername = "root";
	$dbPassword = "1456";
	$dbName = "loginsystem";

	// Create connection
	$conn = mysqli_connect($dbServername,$dbUsername,$dbPassword,$dbName)or die("Error " . mysqli_error($link));


   if(!$conn)
   {
       echo "MySQL 접속 에러/";
       exit();
   }


	mysqli_query("SET NAMES UTF8");

	session_start();

  $userID = $_POST[userID];


	$result = mysqli_query($conn,"SELECT * FROM user_homeappliance WHERE uidUsers = '$userID'");

	if($result)
	{
		echo "success_load/";

		$rowNum = mysqli_num_rows($result);

		echo "$rowNum/";
		while($row= mysqli_fetch_array($result))
		{
			echo $row['appliance'];
			echo "/";
			echo $row['nickname'];
			echo "/";
			echo $row['comment'];
			echo "/";
			echo $row['type'];
			echo "/";
			echo $row['manufacturer'];
			echo "/";
			echo $row['ID'];
			echo "/";
		}
	}
	else {
		echo "fail_load/";
	}


?>
