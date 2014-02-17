import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;

class LRU
{
	int counter;
	String URL;
	int flag;
	
	public int getCount()
	{
		return this.counter;
	}
	
	public int getFlag()
	{
		return this.flag;
	}
	
	public String getURL()
	{
		return this.URL;
	}
	

	public void setFlag(int flag)
	{
		this.flag=flag;
	}
	
	
	public void setCount(int count)
	{
		this.counter=count;
	}
	
	public void setURL( String URL)
	{
		 this.URL=URL;   
	}
	
}


class UDPServer {
int empty=0;
static String eldestURL=null;
static boolean flag=false;
int num = 0;
 int currentIndex=0;
public static int size=5;

  public static void main(String args[]) throws Exception
    {
     try
     {
      DatagramSocket serverSocket = new DatagramSocket(9999);
      DatagramSocket serverSocket1 = new DatagramSocket(9876);
      byte[] receiveData = new byte[1024];
      byte[] sendData  = new byte[1024];

 byte[] sendData1  = new byte[1024];
byte[] sendData2  = new byte[1024];

// Initialising Cache Array 
  LRU[] cachearray = new LRU[size];
	   
	 for(int i=0;i<size;i++)
	 {
	  cachearray[i]=new LRU();
	 }
	
//final String eldestURL;       
      int i=0;
      String temp=null;
String temp1=null;
int t;
final int num=3;
String URL=null;
// boolean flag=false;

 UDPServer lruCache = new UDPServer();
      
 lruCache.LRUinitialize(cachearray);

      while(true)
        {  i++;
           System.out.println("This is"+i+"run");
           receiveData = new byte[1024];
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          System.out.println ("Waiting for datagram packet from Relay Server");
          serverSocket.receive(receivePacket);
          String sentence = new String(receivePacket.getData());
          InetAddress IPAddress = receivePacket.getAddress();
          int port = receivePacket.getPort();
          System.out.println ("From Relay Server: " + IPAddress + ":" + port);
          System.out.println ("Message: " + sentence);
         
          int found;
          URL=sentence.trim();
         String capitalizedSentence;
         
         
       
       found=lruCache.findURL(URL,cachearray);
       lruCache.process(cachearray,URL,serverSocket,serverSocket1,sentence,IPAddress,port,found);
         
         
/*if(flag)
{
InetAddress IPAddress1 = InetAddress.getByName("10.10.9.2");
int port1=9876;
String sent=eldestURL.trim();
String sentence1="Del"+sent;
sendData2 = sentence1.getBytes();

          DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, IPAddress1, port1);
          serverSocket1.send(sendPacket2);
          System.out.println("Sent Del to Caching Switch");



} */
        }


     }
      catch (SocketException ex) {
        System.out.println("UDP Port 9999 is occupied.");
        System.exit(1);
      }

    }
  
  
  //Initialising the counters of LRU and URL
   public void LRUinitialize( LRU[] cachearray)
  {
	  
	  for(int i=size-1;i>0;i--)
	  {
		  cachearray[i].setCount(i);
		  cachearray[i].setURL("0");
		  cachearray[i].setFlag(-1);
	  }
	  
  }
   //To fetch maximum counter value
   public int getMax(LRU[] cachearray)
  {
	  int i,max,maxindex;
	  max=-1;
	  maxindex=0;
	  
	  for(i=0;i<size;i++)
	  {
	    if( max < cachearray[i].getCount())
	    {
	    	maxindex=i;
                System.out.println("\nMAx index="+i);
	        max=cachearray[i].getCount();
            }
	    
	  }
	  
	System.out.println("Maxindex="+maxindex);	  
	return maxindex;  
  }
   
 
     
   public int findURL(String URL,LRU[] cachearray)
   {
       URL=URL.trim();
       int flag=0;
       if(empty==0)
       {
           return 0;
         }
      else
       {for (int i=0;i<size;i++)
       {
           cachearray[i].setURL(cachearray[i].getURL().trim());

           if(URL.equals(cachearray[i].getURL()))
           {
               flag=1;
              System.out.println("\n======================URL found======================");
           }
       }
    return flag;
       }
   }
    public int findindex(String URL,LRU[] cachearray)
   {
       int index=0;
       for (int i=0;i<size;i++)
       {
           cachearray[i].setURL(cachearray[i].getURL().trim());
           if(URL.equals(cachearray[i].getURL()))
           {
               index=i;
           }
       }
    return index;
   }
  
   //Main Process function
public  void process(LRU[] cachearray,String URL,DatagramSocket serverSocket,DatagramSocket serverSocket1,String sentence,InetAddress IPAddress,int port,int found) throws IOException{
        
   int maxval;
  int index,setindex;
  byte[] sendData  = new byte[1024];
  byte[] sendData1  = new byte[1024];
 
 
  System.out.print("Enter URL: ");
//  String str = in.readLine();
String urlval;
int countval; 
  index=currentIndex+1;

  if((index > size)&&(found==0))
  {
          String delURL;
            System.out.println("Before:");
            for(int i=0;i<size;i++)
          {
            urlval=cachearray[i].getURL();
            countval=cachearray[i].getCount();
             System.out.println(" "+urlval+" "+countval);

           }

        
  
	  setindex=getMax(cachearray);
          System.out.println("Removing value at index:"+setindex);
          delURL="Del"+cachearray[setindex].getURL().trim();
	  System.out.println("Removing URL:"+ cachearray[setindex].getURL());
	  cachearray[setindex].setURL(URL);
	  maxval=cachearray[setindex].getCount();
	
	  
	  for(int i=0;i<size;i++)
	  {
		  int count=cachearray[i].getCount();
		  int flag=cachearray[i].getFlag();
		  if(count<maxval) 
		  {
			  cachearray[i].setCount(count+1);
			  
		  }
	  }
	  System.out.println("Removing an entry and adding new entry in its place"+delURL);
	  cachearray[setindex].setCount(0);	
          String capitalizedSentence1 = "No";
         sendData = capitalizedSentence1.getBytes();
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
          serverSocket.send(sendPacket);
        System.out.println("Sent negative data to Relay Server");

        InetAddress IPAddress1 = InetAddress.getByName("10.10.9.2");
        int port1=9876;
          sendData1 = delURL.getBytes();
            DatagramPacket sendPacket1 = new DatagramPacket(sendData1, sendData1.length, IPAddress1, port1);
          serverSocket1.send(sendPacket1);
          System.out.println("Sent Delete  URL to Caching Switch");


	  sendData1 = URL.getBytes();
             sendPacket1 = new DatagramPacket(sendData1, sendData1.length, IPAddress1, port1);
          serverSocket1.send(sendPacket1);
          System.out.println("Sent normal URL to Caching Switch");
 

      System.out.println("After:");
         for(int i=0;i<size;i++)
          {
            urlval=cachearray[i].getURL();
            countval=cachearray[i].getCount();
             System.out.println(" "+urlval+" "+countval);

           }


  }
  else
      if(found==1)
      {
       setindex=findindex(URL,cachearray);
        
        maxval=cachearray[setindex].getCount();
	System.out.println("Updating existing entry and the counters");
	  
	  for(int i=0;i<size;i++)
	  {
		  int count=cachearray[i].getCount();
		  int flag=cachearray[i].getFlag();
		  if(count<maxval) 
		  {
			  cachearray[i].setCount(count+1);
			  
		  }
	  }
	  
	  cachearray[setindex].setCount(0);
      
  for(int i=0;i<size;i++)
          {
            urlval=cachearray[i].getURL();
            countval=cachearray[i].getCount();
             System.out.println(" "+urlval+" "+countval);

           }

         String   capitalizedSentence = "Yes";
       capitalizedSentence.trim();
      sendData = capitalizedSentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
      serverSocket.send(sendPacket);
      System.out.println("Sent data to Relay Server");

      }
  else
  if((currentIndex<size)&&(found==0))
  {
  //System.out.println("String:"+str);
  cachearray[currentIndex].setURL(URL);
  System.out.println("Reached");
  maxval=cachearray[currentIndex].getCount();
  if(empty==0)
    empty=1; 
  
  for(int i=0;i<size;i++)
  {
	  int count=cachearray[i].getCount();
	  int flag=cachearray[i].getFlag();
	  if(count<maxval)
	  {
		  cachearray[i].setCount(count+1);
		  
	  }
  }
  
  cachearray[currentIndex].setCount(0);
  currentIndex++;
  for(int i=0;i<size;i++)
          {
            urlval=cachearray[i].getURL();
            countval=cachearray[i].getCount();
             System.out.println(" "+urlval+" "+countval);

           }

System.out.println("Adding new entry and adjusting the counter value"); 
String capitalizedSentence1 = "No";
sendData = capitalizedSentence1.getBytes();
//System.out.println("Sent negative data to Relay Server");
DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
serverSocket.send(sendPacket);
System.out.println("Sent negative data to Relay Server");

InetAddress IPAddress1 = InetAddress.getByName("10.10.9.2");
int port1=9876;
//String capitalizedSentence = sentence.toUpperCase(); 
          sendData1 = sentence.getBytes();

          DatagramPacket sendPacket1 = new DatagramPacket(sendData1, sendData1.length, IPAddress1, port1);
          serverSocket1.send(sendPacket1);
          System.out.println("Sent URL to Caching Switch");
   }

  }
  
  
 
 
   
 
  }
