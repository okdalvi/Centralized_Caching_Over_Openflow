//package pkgnew;

/**
 *
 * @author omi
 */
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
class CacheSwitch {
    public static void main(String args[]) throws Exception
    {
     try
     {
      DatagramSocket serverSocket = new DatagramSocket(9876);

      byte[] receiveData = new byte[1024];
      byte[] sendData  = new byte[1024];
      int i=0;
      while(true)

        {  i++;
           System.out.println("This is "+i+"run");
            receiveData = new byte[1024];
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          System.out.println ("Waiting for URL from controller...");
          serverSocket.receive(receivePacket);
          String sentence = new String(receivePacket.getData());
          InetAddress IPAddress = receivePacket.getAddress();
          int port = receivePacket.getPort();
          System.out.println ("From controller: " + IPAddress + ":" + port);
          System.out.println ("Message from controller: " + sentence);
          //String capitalizedSentence = sentence.toUpperCase(); 
          //sendData = capitalizedSentence.getBytes(); 
          //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); 
          //serverSocket.send(sendPacket); 
          //System.out.println("Sent packet to host");

          try {
        String serverHostname = new String ("10.10.1.1");
        //if (args.length > 0)serverHostname = args[0];
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress1 = InetAddress.getByName(serverHostname);
        System.out.println ("Attemping to connect to Cache VM" + IPAddress1 + ") via UDP port 9999");
        byte[] sendData1 = new byte[1024];
        byte[] receiveData1 = new byte[1024];
        //System.out.print("Enter Message for Controller: ");
        //String sentence1 = inFromUser.readLine(); 
        sendData1 = sentence.getBytes();
        System.out.println ("Sending URL to Cache VM " + sendData1.length + " bytes to Relay server.");
        DatagramPacket sendPacket1 = new DatagramPacket(sendData1, sendData1.length, IPAddress1, 9999);
        clientSocket.send(sendPacket1);
      // DatagramPacket receivePacket1 = new DatagramPacket(receiveData1, receiveData1.length); 
       //System.out.println ("Waiting for return packet from Controller");
       // clientSocket.setSoTimeout(10000);
    // try {clientSocket.receive(receivePacket1); 
        // String modifiedSentence = new String(receivePacket1.getData()); 
         //InetAddress returnIPAddress = receivePacket1.getAddress();
         // int port1 = receivePacket1.getPort();
         // System.out.println ("From Controller at: " + returnIPAddress +":" + port1);
          // System.out.println("Message from Controller: " + modifiedSentence); 
         // }
      //catch (SocketTimeoutException ste)
        // {System.out.println ("Timeout Occurred: Packet assumed lost" );}
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
