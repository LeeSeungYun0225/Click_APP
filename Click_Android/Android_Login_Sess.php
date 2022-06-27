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

	session_start();
	$numID = $_POST[numid];
	$username = $_POST[userid];


		$sql="SELECT * FROM users WHERE uidUsers='$username' AND user_id = '$numID'";
		$result = mysqli_query($conn,$sql);
		$resultCheck = mysqli_num_rows($result);

		if($resultCheck<1){
            echo "Auto_NotFound/";
			exit();
		}else {
			echo "Auto_Login_Success/";
			$result = mysqli_query($conn,"SELECT emailUsers FROM users WHERE uidUsers = '$username'");
			$row = mysqli_fetch_array($result);
			$data = $row[0];
			echo "$data/";

			$result = mysqli_query($conn,"SELECT device_serial FROM users WHERE uidUsers = '$username' AND device_serial IS NOT NULL ");
			$resultCheck = mysqli_num_rows($result);
			if($resultCheck <1)
			{
				echo "null/";
			}
			else{
				$row = mysqli_fetch_array($result);
				$data = $row[0];
				echo "$data/";
			}

		}




?>
