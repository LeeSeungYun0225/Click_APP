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



   $uid = $_POST[userID];
   $serial = $_POST[serial];
   $functype = $_POST[functype];


   if($functype == '2'){ // 연동해제시



      $sql = "UPDATE users SET device_serial = NULL WHERE uidUsers='$uid'";
      // 유저 테이블에서 제거
      mysqli_query($conn,$sql);

      $sql_updater = "UPDATE device SET bindedto=NULL WHERE device_serial='$serial'";
      // 디바이스 테이블에서 연동 NULL로
      mysqli_query($conn,$sql_updater);
      echo "DeVerify_Success/";


      exit();

   }
   else if($functype == '1')// 연동시
   {
       $device_check = "SELECT bindedto FROM device WHERE device_serial='$serial'";
       $resultANG = mysqli_query($conn,$device_check);
       $existance = mysqli_num_rows($resultANG);
       $CHK= "SELECT * FROM device WHERE device_serial='$serial' AND bindedto IS NOT NULL";
       $resultC = mysqli_query($conn,$CHK);
       $exi = mysqli_num_rows($resultC);
       if($existance == 0)
       {
         echo "Serial_not_Exist/";
         exit();
       }
       else if($exi>0)
       {
         echo "Already_Using_Device/";
         exit();
       }
       else {
         $sql_updater = "UPDATE device SET bindedto='$uid' WHERE device_serial='$serial'";
         mysqli_query($conn,$sql_updater);
         $sql_update = "UPDATE users SET device_serial='$serial' WHERE uidUsers='$uid'";
         mysqli_query($conn,$sql_update);

         echo "Verify_Success/";
       }
   }


   exit();



?>
