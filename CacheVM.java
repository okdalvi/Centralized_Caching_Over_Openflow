/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package pkgnew;

/**
 *
 * @author omi
 */
//package pkgnew;

/**
 *
 * @author omi
 */
import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;

class Cache {
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
           System.out.println("This is"+i+"run");
           receiveData = new byte[1024];
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          System.out.println ("Waiting for URL from CacheSwitch");
          serverSocket.receive(receivePacket);
          String sentence = new String(receivePacket.getData());
          InetAddress IPAddress = receivePacket.getAddress();
          int port = receivePacket.getPort();
          System.out.println ("From Relay Server: " + IPAddress + ":" + port);
          System.out.println ("Message: " + sentence);
         String URL1=sentence.trim();
         URL1=URL1.trim();
         String d="Del";
         String URL2=URL1.replaceAll("/","-");
 URL2=URL2.trim();
        
         if(URL1.startsWith(d))
{
if(URL1.contains("-"))
{

String t=URL1.substring(3);
String URL="rm"+" "+t;
                String cmd = "sudo" +" "+URL2 ;
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
        System.out.println("URL deleted at Cache VM");

}

else{



String t=URL2.substring(3);
String URL="rm"+" "+t;
                String cmd = "sudo" +" "+URL;
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
        System.out.println("URL deleted at Cache VM");





}


}

else    { 
             
              if (URL1.contains("/"))
              {
              
            
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

        System.out.println("File downloaded from internet at CacheVM");
              
              }
             
             else
             
             
              {
              
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

        System.out.println("URL downloaded at Cache VM"); 
              
              
              
              
              
              }    //String URL="-O"+" "+URL1+" "+sentence;
       
        
}
          //String capitalizedSentence = sentence.toUpperCase(); 
          //sendData = capitalizedSentence.getBytes(); 
         // System.out.println("Sent data to Relay Server");
          //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); 
          //serverSocket.send(sendPacket); 

        }

     }
      catch (SocketException ex) {
        System.out.println("UDP Port 9999 is occupied.");
        System.exit(1);
      }

    }
}