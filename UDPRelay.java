import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 *
 * @author omi
 */
class UDPRelay {
    public static void main(String args[]) throws Exception
    {
     try
     {
      DatagramSocket serverSocket = new DatagramSocket(9999);

      byte[] receiveData = new byte[1024];
      byte[] sendData  = new byte[1024];
      int i=0;
      while(true)

        {  i++;
           System.out.println("This is "+i+"run");
            receiveData = new byte[1024];
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          System.out.println ("Waiting for datagram packet from host...");
          serverSocket.receive(receivePacket);
          String sentence = new String(receivePacket.getData());
          InetAddress IPAddress = receivePacket.getAddress();
          int port = receivePacket.getPort();
          System.out.println ("From host: " + IPAddress + ":" + port);
          System.out.println ("Message from host: " + sentence);
          //String capitalizedSentence = sentence.toUpperCase(); 
          //sendData = capitalizedSentence.getBytes(); 
          //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); 
          //serverSocket.send(sendPacket); 
          //System.out.println("Sent packet to host");

          try {
        String serverHostname = new String ("10.10.12.1");
        //if (args.length > 0)serverHostname = args[0];
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress1 = InetAddress.getByName(serverHostname);
        System.out.println ("Attemping to connect to Controller" + IPAddress1 + ") via UDP port 9999");
        byte[] sendData1 = new byte[1024];
        byte[] receiveData1 = new byte[1024];
        //System.out.print("Enter Message for Controller: ");
        //String sentence1 = inFromUser.readLine(); 
        sendData1 = sentence.getBytes();
        System.out.println ("Sending data to controller " + sendData1.length + " bytes to Relay server.");
        DatagramPacket sendPacket1 = new DatagramPacket(sendData1, sendData1.length, IPAddress1, 9999);
        clientSocket.send(sendPacket1);
       DatagramPacket receivePacket1 = new DatagramPacket(receiveData1, receiveData1.length);
       System.out.println ("Waiting for response from Controller");
        clientSocket.setSoTimeout(10000);
     try {clientSocket.receive(receivePacket1);
         String modifiedSentence = new String(receivePacket1.getData());
         InetAddress returnIPAddress = receivePacket1.getAddress();
          int port1 = receivePacket1.getPort();
          System.out.println ("From Controller at: " + returnIPAddress +":" + port1);
           System.out.println("Message from Controller: " + modifiedSentence);
           modifiedSentence=modifiedSentence.trim();
           String URL1=sentence.trim();
String no="No";
if(modifiedSentence.equals(no))
           {
//              String cmd = "wget"+" "+"-O"+sentence+"_index.html"+" "+sentence;
//      String cmd = "wget"+" "+"-O"+" "+sentence+"_index.html"+" "+sentence;
//          String URL1=sentence.trim();
 	  if (URL1.contains("/"))
	     {
		

//	  String URL="-O"+" "+"robjames.net-Numb.mp3"+" "+URL1;
//          String URL="-O"+" "+URL1+" "+sentence;

	   String URL2=URL1.replaceAll("/","-");
	  URL2=URL2.trim();
         String URL="-O"+" "+URL2+" "+URL1;
	URL=URL.trim();
          String cmd = "sudo wget"+" "+URL;
  

        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd);

  String line;
BufferedReader input =new BufferedReader(new InputStreamReader(proc.getInputStream()));
BufferedReader error =new BufferedReader(new InputStreamReader(proc.getErrorStream()));

System.out.println("OUTPUT");
while ((line = input.readLine()) != null)
  System.out.println(line);
input.close();

System.out.println("ERROR");
while ((line = error.readLine()) != null)
  System.out.println(line);
error.close();

        System.out.println("File downloaded from internet at switch");

        }
	else {
        String URL="-O"+" "+URL1+" "+sentence;
          String cmd = "sudo wget"+" "+URL ;

        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd);

  String line;
BufferedReader input =new BufferedReader(new InputStreamReader(proc.getInputStream()));
BufferedReader error =new BufferedReader(new InputStreamReader(proc.getErrorStream()));

System.out.println("OUTPUT");
while ((line = input.readLine()) != null)
  System.out.println(line);
input.close();

System.out.println("ERROR");
while ((line = error.readLine()) != null)
  System.out.println(line);
error.close();

        System.out.println("File downloaded from internet at switch");

}
String capitalizedSentence = "10.10.4.2";
capitalizedSentence=capitalizedSentence.trim();

          sendData = capitalizedSentence.getBytes();
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
          serverSocket.send(sendPacket);
          System.out.println("Sent command to host to download from Switch"+capitalizedSentence);
}else
           {

               String capitalizedSentence1 ="10.10.1.1";
capitalizedSentence1=capitalizedSentence1.trim();
 sendData = capitalizedSentence1.getBytes();
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
          serverSocket.send(sendPacket);
          System.out.println("Sent command to host to download from Cache VM"+capitalizedSentence1);

               //String cmd = "wget"+" "+sentence ;
        //Runtime rt = Runtime.getRuntime();
        //Process proc = rt.exec(cmd);}



         }}

      catch (SocketTimeoutException ste)
         {System.out.println ("Timeout Occurred: Packet assumed lost" );}
        clientSocket.close();
     }
   catch (UnknownHostException ex) {
     System.err.println(ex);
    }
   catch (IOException ex) {
     System.err.println(ex);
    }

        }

     }
      catch (SocketException ex) {
        System.out.println("UDP Port 9999 is occupied.");
        System.exit(1);
      }







    }


}