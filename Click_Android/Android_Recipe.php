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

	$type = $_POST[type];

	if($type == "Recipe_save")
	{
		$userID = $_POST[userID];

		$if_this = $_POST[if_this];
		$then = $_POST[thenwhat];
		$thisgad = $_POST[thisgad];
		$hour = $_POST[hour];
		$min = $_POST[min];
		$day = $_POST[day];
		$complex1 = $_POST[complex1];
		$complex2 = $_POST[complex2];
		if($day == "")
		{
		 	$day = "null";
			$hour = "null";
			$min = "null";
		}
		if($complex1 == "")
		{
			$complex1 = "null";
		}
		if($complex2 == "")
		{
			$complex2 = "null";
		}

		$sql = "INSERT INTO recipe (status,if_this,thenwhat,thisgadget,creator,hour,mm,day,ifThis_complex,ifThis_complex2) VALUES ('1','$if_this','$then','$thisgad','$userID','$hour','$min','$day','$complex1','$complex2')";

		$result = mysqli_query($conn,$sql);
		$id = mysqli_insert_id($conn);
	    if($result){
	        echo "Recipe_save_Success/";
					echo "$id/";
	    }
	    else
	    {
	        echo "fail_save_Rec";
	    }
			exit();
	}
	else if($type == "Recipe_delete")
	{
		$recipeID = $_POST[recipeID];
		$result = mysqli_query($conn,"DELETE FROM recipe WHERE recipe_ID = '$recipeID'");
		$row = mysqli_fetch_array($result);

		if($result)
		{
			echo "Recipe_delete_Success/";
		}
	}
	else if($type == "Recipe_load")
	{
		$userID = $_POST[userID];
		$result = mysqli_query($conn,"SELECT * FROM recipe WHERE creator = '$userID'");
		if($result)
		{
			echo "Recipe_load_Success/";

			$rowNum = mysqli_num_rows($result);

			echo "$rowNum/";
			while($row= mysqli_fetch_array($result))
			{

				echo $row['recipe_ID'];
				echo "/";
				echo $row['status'];
				echo "/";
				echo $row['if_this'];
				echo "/";
				echo $row['thenwhat'];
				echo "/";
				echo $row['thisgadget'];
				echo "/";
				if($row['hour']==null)
				{
					echo "0";
				}
				else {
						echo $row['hour'];
				}
				echo "/";
				if($row['mm'] == null)
				{
					echo "0";
				}
				else {
					echo $row['mm'];
				}
				echo "/";
				if($row['day'] == null)
				{
					echo "0";
				}
				else {
							echo $row['day'];
				}

				echo "/";
				if($row['ifThis_complex']==null)
				{
					echo "0";
				}
				else
				{
						echo $row['ifThis_complex'];
					}

				echo "/";
				if($row['ifThis_complex2'] == null)
				{
					echo "0";
				}
				else {
					echo $row['ifThis_complex2'];
				}
				echo "/";
			}
		}
		else {
			echo "fail_load/";
		}
	}
	else if($type == "Recep_ON")
	{
		$recipeID = $_POST[recipeID];
		$sql = "UPDATE recipe SET status = '1' WHERE recipe_ID = '$recipeID'";
		$result = mysqli_query($conn,$sql);
		if($result)
		{
			echo "Recipe_on_Success/";
		}
		else {
			echo "Recipe_on_Fail/";
		}
	}
	else if($type == "Recep_OFF")
	{
		$recipeID = $_POST[recipeID];
		$sql = "UPDATE recipe SET status = '0' WHERE recipe_ID = '$recipeID'";
		$result = mysqli_query($conn,$sql);
		if($result)
		{
			echo "Recipe_off_Success/";
		}
		else {
			echo "Recipe_off_Fail/";
		}
	}


exit();

?>
