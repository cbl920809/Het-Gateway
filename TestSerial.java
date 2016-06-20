/*									tab:4
 * "Copyright (c) 2005 The Regents of the University  of California.  
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and
 * its documentation for any purpose, without fee, and without written
 * agreement is hereby granted, provided that the above copyright
 * notice, the following two paragraphs and the author appear in all
 * copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY
 * PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
 * DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS."
 *
 */

/**
 * Java-side application for testing serial port communication.
 * 
 *
 * @author Phil Levis <pal@cs.berkeley.edu>
 * @date August 12 2005
 */

import java.io.IOException;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

import net.tinyos.message.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;

public class TestSerial implements MessageListener {

  private MoteIF moteIF;
  private String local_time;
  private String off_set;
  
  public static void SocketForward(){
    String message;
    String forward = " ";
    try {
      ServerSocket server = null;

      try {
         server = new ServerSocket(5050);
      } catch (Exception e){
        System.out.println("can not listen to"+e);
      }
      Socket socket = null;
      try {
        socket = server.accept();
      } catch (Exception e){
        System.out.println("Error"+e);
      }
      BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter os = new PrintWriter(socket.getOutputStream());
      String response = "Get the message";
      forward = is.readLine();
      os.println(response);
      os.flush();

      System.out.println("Sending the message");

      os.close();
      is.close();
      socket.close();
      server.close();
    } catch (Exception e){
      System.out.println("Error:"+e);
    }
    try{
    Socket socket = new Socket("140.114.91.151",5050);
    PrintWriter os = new PrintWriter(socket.getOutputStream());
    BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    message = forward;
    os.println(message);
    os.flush();

    System.out.println("Client: "+ message);
    System.out.println("140.114.91.151 "+ is.readLine());
    
    os.close();
    is.close();
    socket.close();
  } catch(Exception e){
    System.out.println("Error"+ e);
  }
  }
  public TestSerial(MoteIF moteIF) {
    this.moteIF = moteIF;
    this.moteIF.registerListener(new TestSerialMsg(), this);
  }
  
  public void messageReceived(int to, Message message) {
	TestSerialMsg msg = (TestSerialMsg)message;
	local_time = Long.toString(msg.get_localtime());
	off_set = Long.toString(msg.get_offset());
	int source = message.getSerialPacket().get_header_src();
    System.out.println("Node :"+msg.get_nodeid() + ": Power = "+msg.get_power()
	                   + ": Rssi = "+msg.get_rssi()
					   + ": Lqi = "+msg.get_lqi()+": localtime = "+msg.get_localtime()+": offset ="+msg.get_offset());
    try{
    Socket socket = new Socket("192.168.2.0",5050);
    PrintWriter os = new PrintWriter(socket.getOutputStream());
    BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String readline = "localtime: " + local_time + " and " + "offset: " + off_set;
    os.println(readline);
    os.flush();
      
    System.out.println("Client"+readline);
    System.out.println("Server"+is.readLine());

    os.close();
    is.close();
    socket.close();
  } catch(Exception e){
    System.out.println("Error"+ e);
  }
}
  
  private static void usage() {
    System.err.println("usage: TestSerial [-comm <source>]");
  }
  
  public static void main(String[] args) throws Exception {
    String source = null;
    if (args.length == 2) {
      if (!args[0].equals("-comm")) {
	usage();
	System.exit(1);
      }
      source = args[1];
    }
    else if (args.length != 0) {
      usage();
      System.exit(1);
    }
    
    PhoenixSource phoenix;
    
    if (source == null) {
      phoenix = BuildSource.makePhoenix(PrintStreamMessenger.err);
    }
    else {
      phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
    }

    MoteIF mif = new MoteIF(phoenix);
    TestSerial serial = new TestSerial(mif);
	  
    Timer timer = new Timer();
          timer.schedule(new TimerTask() {
             public void run(){
                    SocketForward();
             }
          }, 0,1000); 
	
  }
}
