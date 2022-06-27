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

	$username = $_POST[username];
	$pwd = $_POST[pwd];

		$sql="SELECT * FROM users WHERE uidUsers='$username'";
		$result = mysqli_query($conn,$sql);
		$resultCheck = mysqli_num_rows($result);
		if($resultCheck<1){
            echo "NotFound/";
			exit();
		}else {
			if($row = mysqli_fetch_assoc($result)){
				$hashedPwdCheck = password_verify($pwd, $row['pwdUsers']);
				if($hashedPwdCheck == false){
                    echo "passWrong/";
					exit();
				}else if ($hashedPwdCheck == true){
                    echo "success/";

                    $result = mysqli_query($conn,"SELECT user_id FROM users WHERE uidUsers = '$username'");
                    $row = mysqli_fetch_array($result);
                    $data = $row[0];
                    echo "$data/";
                    $result = mysqli_query($conn,"SELECT emailUsers FROM users WHERE uidUsers = '$username'");
                    $row = mysqli_fetch_array($result);
                    $data = $row[0];
                    echo "$data/";
										$result = mysqli_query($conn,"SELECT device_serial FROM users WHERE uidUsers = '$username' AND device_serial IS NOT NULL");
                    $row = mysqli_fetch_array($result);
										$existance = mysqli_num_rows($row);
										if($row == 0)
										{
											echo "NULL/";
										}
										else {
												echo "$row[0]";
										}

					exit();
				}
			}
		}


?>
