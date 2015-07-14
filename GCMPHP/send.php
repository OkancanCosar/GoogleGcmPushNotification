<html>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
 <title>GCM Send</title>
</head>
<body>
 <?php
				// 33. SATIRDAKİ 'Authorization: key=***********' BÖLÜMÜNÜ DEĞİŞTİRMEYİ UNUTMAYIN!!
 if(isset($_POST['submit'])){
   require_once("baglan.php");
   $registatoin_ids = array();
   
   $sql = "SELECT * FROM wp_gcm_kullanicilar";
   $result = mysqli_query($con, $sql);
   
   while($row = mysqli_fetch_assoc($result)){
    array_push($registatoin_ids, $row['registration_id']);
   }
 
   // GCM servicelerine gidecek veri
   //Arkadaşlar aşşağıdaki PHP kodlarıyla oynamıyoruz. Bu Google 'n bizden kullanmamızı istediği kodlar
   //Sadece registration_ids,mesaj ve Authorization: key değerlerini değiştiriyoruz
    $url = 'https://android.googleapis.com/gcm/send';
   
    $mesaj = array("notification_message" => $_POST['mesaj']); //gönderdiğimiz mesaj POST 'tan alıyoruz.Androidde okurken notification_message değerini kullanacağız
         $fields = array(
             'registration_ids' => $registatoin_ids,
             'data' => $mesaj,
         );
		
		//Alttaki Authorization: key= kısmına Google Apis kısmında oluşturduğumuz key'i yazacağız
         $headers = array(
             'Authorization: key=********', 
             'Content-Type: application/json'
         );
         $ch = curl_init();
         curl_setopt($ch, CURLOPT_URL, $url);
         curl_setopt($ch, CURLOPT_POST, true);
         curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
         curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
         curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
         curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

         $result = curl_exec($ch);
         if ($result === FALSE) {
             die('Curl failed: ' . curl_error($ch));
         }
         curl_close($ch);
         
		 echo $result;
  }
 ?>
 
 <center>
 <form method="post" action="send.php">
  <textarea rows="4" name="mesaj" cols="90" placeholder="Type message here"></textarea>
 <br> <input type="submit" name="submit" value="Send" />
 </form>
 </center>
 
</body>
</html>