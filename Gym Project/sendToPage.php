<!DOCTYPE html>
<html>
<body>
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

<?php
    session_start();
    $unamei = "Unkown";
    if (isset($_POST["uname"])) { $unamei = $_POST["uname"]; }
    $pwi = "Unkown";
    if (isset($_POST["pw"])) { $pwi = $_POST["pw"]; }

    $uname = $unamei;
    $pw = $pwi;

    if($uname == '') {
      #echo "Enter user name.";
      echo '<script>alert("Please enter username.");history.go(-1);</script>';
      #header("Location: http://localhost/ProjectForm.php");
      exit;
    }
    else if($pw == '') {
      #echo "Enter password.";
      echo '<script>alert("Please enter password.");history.go(-1);</script>';
      exit;
    }

    setSession();

  ?>

<!--connect to database-->
  <?php
    #$dbuser = "root"; //change to root
    $dbuser = $_SESSION['USER_ROOT'];
  	#$dbpass = ""; //change to password right?
    $dbpass = $_SESSION['PW_ROOT'];
  	$conn = mysqli_connect("localhost:3306", $dbuser, $dbpass);
  	if(! $conn ) {
	  	echo "Error: Unable to connect to MySQL." . "<br>\n";
	  	echo "Debugging errno: " . mysqli_connect_errno() . "<br>\n";
	  	echo "Debugging error: " . mysqli_connect_error() . "<br>\n";
	  	die("Could not connect: " . mysqli_error()); 
  	}

    $sql = "SELECT role From gym.login WHERE username = ? AND password = ?";
    if($stmt = $conn->prepare($sql)) {}
    else {
      echo "Error in: " .$sql;
    }

    $stmt->bind_param("ss",$uname,$pw);
    $stmt->execute();

    $role ="Unknown";
    $result = $stmt->get_result();
    #if($result = $stmt->get_result()) {
      #$rowcount = $stmt->numrows;
      if($result->num_rows == 0) {
        #echo "<center>Unknown username or password</center>";
        echo '<script>alert("Unknown username or password.");history.go(-1);</script>';
        #header("Location: http://localhost/ProjectForm.php");
        #echo '<br><center><button onclick="history.go(-1);">Back </button></center>';
        exit;
      }
      if ($row = $result -> fetch_row()) {
        $row = $row[0];

      }
      #else {
       # echo "No result set.";
      #}
    #}

    $_SESSION['role'] = $role;
    $_SESSION['uname'] = $uname;

    $stmt->close();
    $conn->close();
    #echo $row;
    if($row == 'frontDesk') {
        header("Location: http://localhost/FrontDesk.php");
        exit;
    }
  ?>

</body>
</html>