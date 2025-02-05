<?php

/***********************************************
 **** Contact Form Configuration
 ***********************************************/

// Email ID from where the message sent. It used to be an email id from your domain
$from = 'no-reply@yourdomain.com';

// Email ID Where to send message 
$sendTo = 'info@yourdomain.com';

// Message Subject
$subject = 'New message from contact form';

$fields = array( 
  'name' => 'Name', 
  'surname' => 'Surname', 
  'phone' => 'Phone', 
  'email' => 'Email', 
  'message' => 'Message' 
  ); // array variable name => Text Message to appear in email

// Alert Message After Success 
$okMessage = 'Contact successfully submitted. Thank you, We will get back to you soon!';

// Alert Message if any error
$errorMessage = 'There was an error while submitting the form. Please try again later';

// Sending Message with Error Handler
try
{
    $emailText = "You have new message from contact form\n=============================\n";

    foreach ($_POST as $key => $value) {

        if (isset($fields[$key])) {
            $emailText .= "$fields[$key]: $value\n";
        }
    }
	
	//Message Sending
    mail($sendTo, $subject, $emailText, "From: " . $from);
	
	//Respond if success
    $responseArray = array('type' => 'success', 'message' => $okMessage);
}
catch (\Exception $e)
{
	//Respond if error
    $responseArray = array('type' => 'danger', 'message' => $errorMessage);
}

// Preparing json data as an ajax respond
if (!empty($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH']) == 'xmlhttprequest') {
    $encoded = json_encode($responseArray);
    
    header('Content-Type: application/json');
    
    echo $encoded;
}
else {
    echo $responseArray['message'];
}