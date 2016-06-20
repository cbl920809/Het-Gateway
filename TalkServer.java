import java.io.*;
import java.net.*;
import java.applet.Applet;
import java.util.Timer;
import java.util.TimerTask;


public class TalkServer{

  public static void SocketServer(){
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
      os.println(response);
      os.flush();
      System.out.println("Server:"+response);
      System.out.println("Client:"+is.readLine());
      os.close();
      is.close();
      socket.close();
      server.close();
    } catch (Exception e){
      System.out.println("Error:"+e);
    }
  }

  public static void main(String args[]){

    Timer timer = new Timer();
          timer.schedule(new TimerTask() {
             public void run(){
                    SocketServer();
             }
          }, 0,1000);
    
  }
}