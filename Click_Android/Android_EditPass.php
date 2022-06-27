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

	$userkey = $_POST[userkey];
  $pwd = $_POST[pwd];
	$pwd2 = $_POST[pwd2];



		$sql = "SELECT * FROM users WHERE user_id='$userkey'";
		$result = mysqli_query($conn,$sql);

		if($row = mysqli_fetch_assoc($result)){
			$hashedPwdCheck = password_verify($pwd, $row['pwdUsers']);//비밀번호 검증
			if($hashedPwdCheck == false)
			{//비밀번호 오류
									echo "passWrong/";
				exit();
			}
			else if($hashedPwdCheck == true)
			{
				$hashedPwd = password_hash($pwd2, PASSWORD_DEFAULT);
				$sql = "UPDATE users SET pwdUsers = '$hashedPwd' WHERE user_id='$userkey'";
				$result = mysqli_query($conn,$sql);
				if($result)
				{
					 echo "success/";
				}
			}
		}


    echo "fail/";




?>
