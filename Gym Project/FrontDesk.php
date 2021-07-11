<!DOCTYPE html>
<html>
<head>
  <title>Front Desk</title>
  <style>
    body {
      margin-left:30px;
    }
    
  </style>
</head>
<!--javascripts-->
<script>
function togglediv(id) {
  document.querySelectorAll(".TableBody").forEach(function(div) {
    if (div.id == id) {
      // Toggle specified div
      div.style.display = div.style.display == "none" ? "block" : "none";
    } else {
      // Hide other divs
      div.style.display = "none";
    }
  });
}//function
</script>

<?php
  function setSession() {
    $stream = fopen("db_login.txt", "r") or die("Unable to open user file!");
    while(($line = fgets($stream)) != false) {
      $key_data = explode(':', $line);
      $_SESSION[trim($key_data[0])] = trim($key_data[1]);
    }
    fclose($stream);
  }

?>


<body>
<!--connect to database-->
  <?php
    session_start();
    setSession();

    #$dbuser = "root"; //change to root
    $dbuser = $_SESSION['FRONTDESK'];
  	#$dbpass = ""; //change to password right?
    $dbpass = $_SESSION['FRONTDESK_PW'];
  	$conn = mysqli_connect("localhost:3306", $dbuser, $dbpass, "gym");
  	if(! $conn ) {
	  	echo "Error: Unable to connect to MySQL." . "<br>\n";
	  	echo "Debugging errno: " . mysqli_connect_errno() . "<br>\n";
	  	echo "Debugging error: " . mysqli_connect_error() . "<br>\n";
	  	die("Could not connect: " . mysqli_error()); 
  	}

    
  ?>

 
    <h1> Front Desk Page </h1>
<!--the buttons-->
    <button onclick="togglediv('addCust')" type="button"> Add new customer </button>
    <button onclick="togglediv('regTrainer')" type="button"> Schedule customer with trainer </button>
    
<!--add a customer-->
  <div id="addCust" class="TableBody" style="display:none">
    <h3> Add new customer </h3>

    <form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post">
      First name:
      <br>
      <input type="text" name="name">
      <br><br>
      <input type="submit">
    </form>

    <?php
    if (isset($_POST['name']) && $_POST['name'] != "") {
        $name = $_POST['name'];
        $maxSQL = "SELECT MAX(ID) AS 'max' FROM customer";
        
        mysqli_select_db($conn,"gym");
        if ($result = mysqli_query($conn, $maxSQL)) {
          while($row = mysqli_fetch_assoc($result)) {
            $next_id = $row["max"] + 1;
          }
        }
        
        $addCustSQL = "INSERT INTO gym.customer(ID, name) VALUES(?, ?)";

        if($custStmt = $conn->prepare($addCustSQL)) {}
        else {
          echo "Error in: " .$addCustSQL;
        }
        $custStmt->bind_param("ss",$next_id,$name);
      
        if ($custStmt->execute()) {
          #echo "made";
          echo '<script>alert("Successfully added '.$name.'!");</script>';
        } else {
          #echo "Error: " . $addCustSQL . "<br>" . $conn->error;
        }
    } 
    else if (isset($_POST['name']) || $_POST['name'] = "") {
      echo '<script>alert("Failed. Please fill out all boxes.");</script>';
    }
    ?>
  </div>

<!--add customer/trainer pair-->
  <div id="regTrainer" class="TableBody" style="display:none">
    <br>
    <?php
      $scheduleSQL = "SELECT T.i_ID, I.name, T.c_ID, T.weekDate, T.startTime, T.endTime FROM gym.trains AS T JOIN gym.instructor AS I WHERE T.i_ID = I.ID ORDER BY T.i_ID, T.startTime";
    
      $sql = "SELECT * FROM gym.trains"; 
      mysqli_select_db($conn,"gym");
#the table
      echo "<table border='1'>
      <tr>
      <th>ID</th>
      <th>name</th>
      <th>customer</th>
      <th>date</th>
      <th>start time</th>
      <th>end time</th>
      </tr>";

      if ($result = mysqli_query($conn, $scheduleSQL)) {
       // output data of each row
        while($row = mysqli_fetch_assoc($result)) {
          echo "<tr>";
          echo "<td>". $row["i_ID"] . "</td>";
          echo "<td>". $row["name"] . "</td>";
          echo "<td>". $row["c_ID"] . "</td>";
          echo "<td>". $row["weekDate"] . "</td>";
          echo "<td>". $row["startTime"] . "</td> " ;
          echo "<td>". $row["endTime"] . "</td> " ;
          echo "</tr>";
        }
      }
      echo "</table>";
    ?>

    <h3> Schedule customer with trainer </h3>
  <div style="float:left;">
    <h4>Add session:</h4>
    <form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post">
      Customer ID:
      <br><input type="text" name="trainCustID"><br>

      Instructer ID: 
      <br><input type="text" name="trainEmployID"><br>

      Weekday: 
      <br><input type="text" name="weekday"><br>

      Start time: 
      <br><input type="text" name="startTime"><br>

      End time: 
      <br><input type="text" name="endTime"><br>
      <br>
      <input type="submit" name="add" value="add">
    </form>
  </div>

<!--update-->
  <div style="display:inline-block;">
    <h4>&nbsp;&nbsp; Edit existing session: &emsp;&ensp; Updated info: </h4>
    <form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post">
      &nbsp;&nbsp;Customer ID:
      <br>&nbsp;&nbsp;<input type="text" name="etrainCustID">&nbsp;<input type="text" name="ntrainCustID"><br>

      &nbsp;&nbsp;Instructer ID: 
      <br>&nbsp;&nbsp;<input type="text" name="etrainEmployID">&nbsp;<input type="text" name="ntrainEmployID"><br>

      &nbsp;&nbsp;Weekday: 
      <br>&nbsp;&nbsp;<input type="text" name="eweekday">&nbsp;<input type="text" name="nweekday"><br>

      &nbsp;&nbsp;Start time: 
      <br>&nbsp;&nbsp;<input type="text" name="estartTime">&nbsp;<input type="text" name="nstartTime"><br>

      &nbsp;&nbsp;End time: 
      <br>&nbsp;&nbsp;<input type="text" name="eendTime">&nbsp;<input type="text" name="nendTime"><br>
      <br>
      &nbsp;&nbsp;<input type="submit" name="update" value="update">
    </form>
  </div>
<!--delete-->
  <div style="display:inline-block;">
    <h4>Delete session:</h4>
    <form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post">
      &nbsp;Customer ID:
      <br>&nbsp;<input type="text" name="dtrainCustID"><br>

      &nbsp;Instructer ID: 
      <br>&nbsp;<input type="text" name="dtrainEmployID"><br>

      &nbsp;Weekday: 
      <br>&nbsp;<input type="text" name="dweekday"><br>

      &nbsp;Start time: 
      <br>&nbsp;<input type="text" name="dstartTime"><br>

      &nbsp;End time: 
      <br>&nbsp;<input type="text" name="dendTime"><br>
      <br>
      &nbsp;<input type="submit" name="delete" value="delete">
    </form>
  </div>

<!--Add training session-->
    <?php
    if (isset($_POST['add'])) {
      if (isset($_POST['trainCustID'], $_POST['trainEmployID'], $_POST['weekday'], $_POST['startTime'], $_POST['endTime']) && $_POST['trainCustID'] != "" && $_POST['trainEmployID'] != "" && $_POST['weekday'] != "" && $_POST['startTime'] != "" && $_POST['endTime'] != "") {
        $custID = $_POST['trainCustID'];
        $trainID = $_POST['trainEmployID'];
        $weekday = $_POST['weekday'];
        $start = $_POST['startTime'];
        $end = $_POST['endTime'];


        $cIDSQL = "SELECT * FROM gym.customer WHERE ID = ?";
        if($stmt1 = $conn->prepare($cIDSQL)) {}
        else {
          #echo "Error in: " .$cIDSQL;
        }
        $stmt1->bind_param("s",$custID);
        $stmt1->execute();
        $result1 = $stmt1->get_result();

        $iIDSQL = "SELECT * FROM gym.instructor WHERE ID = ?";
        if($stmt2 = $conn->prepare($iIDSQL)) {}
        else {
          #echo "Error in: " .$iIDSQL;
        }
        $stmt2->bind_param("s",$trainID);
        $stmt2->execute();
        $result2 = $stmt2->get_result();

        if($result1->num_rows == 0) {
          #echo "Customer ID does not exist. Try again.";
          echo '<script>alert("Customer ID does not exist. Try again.");</script>';
        } 
        if($result2->num_rows == 0) {
          #echo "Instructor ID does not exist. Try again.";
          echo '<script>alert("Instructor ID does not exist. Try again.");</script>';
        }
        if($result1->num_rows != 0 && $result2->num_rows != 0) {
          $trainsSQL = "INSERT INTO gym.trains(i_ID, c_ID, startTime, endTime, weekDate) VALUES(?,?,?,?,?)";
          if($stmt3 = $conn->prepare($trainsSQL)) {}
          else {
            #echo "Error in: " .$trainsSQL;
          }
          $stmt3->bind_param("sssss",$trainID,$custID,$start,$end,$weekday);
          #$stmt3->execute();
          #$result3 = $stmt3->get_result();

          if ($stmt3->execute()) {
            echo '<script>alert("Successfully added session!");</script>';
            #header("Refresh:1");
            #echo 'location.reload()';
            echo "<meta http-equiv='refresh' content='0'>";
          } 
          else {
            #echo "Error: " . $trainsSQL . "<br>" . $conn->error;
            echo '<script>alert("Failed. Error in input.");</script>';
          }
        }

      } 
      else if (isset($_POST['trainCustID'], $_POST['trainEmployID'], $_POST['weekday'], $_POST['startTime'], $_POST['endTime']) || $_POST['trainCustID'] = "" || $_POST['trainEmployID'] = "" || $_POST['weekday'] = "" || $_POST['startTime'] = "" || $_POST['endTime'] = "") {
        echo '<script>alert("Failed. Please fill out all boxes.");</script>';
      }
    }
    ?>

<!--Edit training session-->
    <?php
    if (isset($_POST['update'])) {
      if (isset($_POST['etrainCustID'], $_POST['etrainEmployID'], $_POST['eweekday'], $_POST['estartTime'], $_POST['eendTime'], $_POST['ntrainCustID'], $_POST['ntrainEmployID'], $_POST['nweekday'], $_POST['nstartTime'], $_POST['nendTime']) && $_POST['etrainCustID'] != "" && $_POST['etrainEmployID'] != "" && $_POST['eweekday'] != "" && $_POST['estartTime'] != "" && $_POST['eendTime'] != "" && $_POST['ntrainCustID'] != "" && $_POST['ntrainEmployID'] != "" && $_POST['nweekday'] != "" && $_POST['nstartTime'] != "" && $_POST['nendTime'] != "") {
        $ecustID = $_POST['etrainCustID'];
        $etrainID = $_POST['etrainEmployID'];
        $eweekday = $_POST['eweekday'];
        $estart = $_POST['estartTime'];
        $eend = $_POST['eendTime'];
        $ncustID = $_POST['ntrainCustID'];
        $ntrainID = $_POST['ntrainEmployID'];
        $nweekday = $_POST['nweekday'];
        $nstart = $_POST['nstartTime'];
        $nend = $_POST['nendTime'];


        
        $updateSQL = "UPDATE gym.trains SET i_ID = ?, c_ID = ?, startTime = ?, endTime = ?, weekDate = ? WHERE i_ID = ? AND c_ID = ? AND startTime = ? AND endTime = ? AND weekDate = ?";
        
        
        if($stmt4 = $conn->prepare($updateSQL)) {}
        else {
          #echo "Error in: " .$trainsSQL;
        }
        $stmt4->bind_param("ssssssssss",$ntrainID,$ncustID,$nstart,$nend,$nweekday,$etrainID,$ecustID,$estart,$eend,$eweekday);
        #$stmt3->execute();
        $result4 = $stmt4->get_result();

        if ($stmt4->execute() & mysqli_affected_rows($conn)==1) {
          echo '<script>alert("Successfully updated session!");</script>';
          #header("Refresh:1");
          echo "<meta http-equiv='refresh' content='0'>";
        } 
        else {
          #echo "Error: " . $trainsSQL . "<br>" . $conn->error;
          echo '<script>alert("Failed. Error in input.");</script>';
        }
        
        
      } 

      //isset($_POST['etrainCustID'], $_POST['etrainEmployID'], $_POST['eweekday'], $_POST['estartTime'], $_POST['eendTime'], $_POST['ntrainCustID'], $_POST['ntrainEmployID'], $_POST['nweekday'], $_POST['nstartTime'], $_POST['nendTime']) || 
      else if ($_POST['etrainCustID'] == "" || $_POST['etrainEmployID'] == "" || $_POST['eweekday'] == "" || $_POST['estartTime'] == "" || $_POST['eendTime'] == "" || $_POST['ntrainCustID'] == "" || $_POST['ntrainEmployID'] == "" || $_POST['nweekday'] == "" || $_POST['nstartTime'] == "" || $_POST['nendTime'] == "") {
        echo '<script>alert("Failed. Please fill out all boxes.");</script>';
      }
    }
    ?>
  </div>

<!--delete-->
  <?php
      if (isset($_POST['delete'])) {
        if (isset($_POST['dtrainCustID'], $_POST['dtrainEmployID'], $_POST['dweekday'], $_POST['dstartTime'], $_POST['dendTime']) && $_POST['dtrainCustID'] != "" && $_POST['dtrainEmployID'] != "" && $_POST['dweekday'] != "" && $_POST['dstartTime'] != "" && $_POST['dendTime'] != "") {
          $dcustID = $_POST['dtrainCustID'];
          $dtrainID = $_POST['dtrainEmployID'];
          $dweekday = $_POST['dweekday'];
          $dstart = $_POST['dstartTime'];
          $dend = $_POST['dendTime'];

          $deleteSQL = "DELETE FROM gym.trains WHERE i_ID = ? AND c_ID = ? AND startTime = ? AND endTime = ? AND weekDate = ?";
          if($stmt5 = $conn->prepare($deleteSQL)) {}
          else {
            #echo "Error in: " .$trainsSQL;
          }
          $stmt5->bind_param("sssss",$dtrainID,$dcustID,$dstart,$dend,$dweekday);
          #$stmt3->execute();
          #$result3 = $stmt3->get_result();

          if ($stmt5->execute() & mysqli_affected_rows($conn)==1) {
            echo '<script>alert("Successfully deleted session!");</script>';
            echo "<meta http-equiv='refresh' content='0'>";
          } 
          else {
            #echo "Error: " . $trainsSQL . "<br>" . $conn->error;
            echo '<script>alert("Failed. Error in input.");</script>';
          }
        }

       
        else if (isset($_POST['dtrainCustID'], $_POST['dtrainEmployID'], $_POST['dweekday'], $_POST['dstartTime'], $_POST['dendTime']) || $_POST['dtrainCustID'] = "" || $_POST['dtrainEmployID'] = "" || $_POST['dweekday'] = "" || $_POST['dstartTime'] = "" || $_POST['dendTime'] = "") {
          echo '<script>alert("Failed. Please fill out all boxes.");</script>';
        }
    }
  ?>
  <?php
    $custStmt
    //$stmt1->close();
    //$stmt2->close();
    //$stmt3->close();
    //$stmt4->close();
    //$stmt5->close();
    //$conn->close();
  ?>


</body>
</html>