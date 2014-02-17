/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package pkgnew;

/**
 *
 * @author omi
 */
import java.io.*;
import java.net.*;

class UDPClient {
    public static void main(String args[]) throws Exception
    {
     try {
        String serverHostname = new String ("10.10.4.2");
        //if (args.length > 0)serverHostname = args[0];
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(serverHostname);
        System.out.println ("Attemping to connect to Relay Server" + IPAddress + ") via UDP port 9999");
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        System.out.print("Enter Message for Relay Server: ");
        String sentence = inFromUser.readLine();
        sendData = sentence.getBytes();
        System.out.println ("Sending data to " + sendData.length + " bytes to Relay server.");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        System.out.println ("Waiting for return packet from Relay Server");
        clientSocket.setSoTimeout(10000);
     try {clientSocket.receive(receivePacket);
          String modifiedSentence = new String(receivePacket.getData());
           InetAddress returnIPAddress = receivePacket.getAddress();
           int port = receivePacket.getPort();
           System.out.println ("From Relay server at: " + returnIPAddress +":" + port);
           System.out.println("Message from Relay Server: " + modifiedSentence);
           modifiedSentence=modifiedSentence.trim();
        String URL1=sentence.trim();
        System.out.println(modifiedSentence);
String s="Switch";
       // if(modifiedSentence.equals(s))
         //  {
                //String cmd = "wget"+" "+sentence+"Switch *****************address*********************" ; //download from local switch
//          String URL1=sentence.trim();
//          String URL="-O"+" "+URL1+" "+sentence;
//                String cmd = "wget"+" "+URL ;

       // String cmd = "sudo wget"+" "+"http://10.10.4.2/"+URL1;
//              Runtime rt = Runtime.getRuntime();
  //      Process proc = rt.exec(cmd);
    //    System.out.println("Downloaded from Switch");
//}else
          // {String cmd = "wget"+" "+sentence ; //Download from cache vm

//String URL2;
String cmd;

if (URL1.contains("/"))
{

 String URL2=URL1.replaceAll("/","-");
          URL2=URL2.trim();
  cmd = "sudo wget http://"+modifiedSentence+"/"+URL2;
//"sudo wget"+" "+modifiedSentence;
System.out.println("Command is:"+cmd  +URL2   +URL1);
cmd=cmd.trim();

}

else
{

       cmd = "sudo wget http://"+modifiedSentence+"/"+URL1;
//"sudo wget"+" "+modifiedSentence;
System.out.println("Command is:"+cmd);
cmd=cmd.trim();
//for (int i=0;i<1000;i++){;}
}

 try {
                   Thread.sleep(2000);
                } catch (InterruptedException ie) {
                }
Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd);
proc.waitFor();
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


System.out.println("Downloaded!!!");

         }

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
