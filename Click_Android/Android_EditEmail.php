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
  $email = $_POST[email];
	$pwd = $_POST[pwd];


	$sql = "SELECT * FROM users WHERE user_id='$userkey'";
	$result = mysqli_query($conn,$sql);

		if(!filter_var($email,FILTER_VALIDATE_EMAIL))
		{
				echo "Invalid email/";
				exit();
		}
		else if($row = mysqli_fetch_assoc($result)){
			$hashedPwdCheck = password_verify($pwd, $row['pwdUsers']);//비밀번호 검증
			if($hashedPwdCheck == false){//비밀번호 오류
									echo "passWrong/";
				exit();
		}
		else if ($hashedPwdCheck == true){// 비밀번호 검증 성공
			$sql = "UPDATE users SET emailUsers = '$email' WHERE user_id = '$userkey'";
			$result = mysqli_query($conn,$sql);
			if($result){
					echo "success/";
			}
			else
			{
					echo "fail/";
			}
			}
		}




exit();

?>
