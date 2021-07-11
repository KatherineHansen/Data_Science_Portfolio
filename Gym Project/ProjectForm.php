<!DOCTYPE html>
<html>
<head>
  <style>
    .content {
        margin-left: 300px;
        margin-right: 300px;
        text-align: center;
        padding-left:15px;
        padding-right:15px;
        padding-top:0px;
        padding-bottom:15px;
        border: 2px solid;
    }
  </style>
</head>

<body>
<div class="content">

<h1> Welcome to our gym </h1>
<h4><u> Login </u></h4>

<form action="sendToPage.php" method="post">
    Username:
    <input type="text" name="uname"><br><br>

    Password: 
    <input type="password" name="pw"><br><br>

    <input type="submit">
</form>

</div>
</body>
</html>
