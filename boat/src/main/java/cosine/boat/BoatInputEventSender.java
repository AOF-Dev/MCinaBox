package cosine.boat;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.net.Socket;
import java.io.OutputStream;

public class BoatInputEventSender{

	public static final int KeyPress        = 2;
	public static final int KeyRelease      = 3;
	public static final int ButtonPress     = 4;
	public static final int ButtonRelease	= 5;
	public static final int MotionNotify	= 6;

	private static final int MESSAGE_SIZE = 10;
	private static final int CACHE_SIZE = 8 * MESSAGE_SIZE;


	private Deque<byte[]> cachedObjs = new ArrayDeque<byte[]>(CACHE_SIZE);
	private BlockingDeque<byte[]> deque = new LinkedBlockingDeque<byte[]>();

	public ServerSocket serverSock;
	public Socket sock;

	private OutputStream os;
	private InputStream is;
	public int port;
	public boolean receiving;
	public boolean running;

	private BoatClientActivity activity;
	public void startServer(BoatClientActivity a){
		activity = a;
		running = true;
		try{
			this.serverSock = new ServerSocket();
			this.serverSock.bind(new InetSocketAddress("127.0.0.1", 0));
			new Thread(new Sender()).start();
			new Thread(new Receiver()).start();
			port = this.serverSock.getLocalPort();
			System.out.println("BoatInputEventSender is created!The port is:" + port);

		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	private class Receiver implements Runnable
	{

		@Override
		public void run()
		{
			// TODO: Implement this method
			try {

				while(!receiving){

				}
				byte[] msg = new byte[1];

				while (running) {

					is.read(msg, 0, 1);

					activity.changeGrab(msg[0]);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}


		}


	}

	private class Sender implements Runnable
	{

		@Override
		public void run()
		{
			// TODO: Implement this method
			try {
				BoatInputEventSender.this.sock = BoatInputEventSender.this.serverSock.accept();
				BoatInputEventSender.this.serverSock.close();
				BoatInputEventSender.this.os = BoatInputEventSender.this.sock.getOutputStream();
				BoatInputEventSender.this.is = BoatInputEventSender.this.sock.getInputStream();
				BoatInputEventSender.this.receiving = true;
				while (running) {
					byte[] event = deque.take();
					os.write(event);
					recycle(event);
				}
				sock.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Exiting input event sender");
		}


	}


	private byte[] obtain() {
		byte[] msg = this.cachedObjs.poll();
		if (msg == null) {
			return new byte[MESSAGE_SIZE];
		}
		return msg;
	}

	private void recycle(byte[] msg) {
		if (this.cachedObjs.size() < CACHE_SIZE) {
			this.cachedObjs.add(msg);
		}
	}



	public static void writeInt(byte[] src, int offset, int i) {
		src[0 + offset] = (byte)( i >> (0 * 8));
		src[1 + offset] = (byte)( i >> (1 * 8));
		src[2 + offset] = (byte)( i >> (2 * 8));
		src[3 + offset] = (byte)( i >> (3 * 8));
	}


	public void setMouseButton(byte button, boolean press) {
		byte[] msg = obtain();
		msg[0] = (byte) (press ? ButtonPress : ButtonRelease);
		msg[1] = button;
		this.deque.add(msg);
	}
	public void setPointer(int x, int y) {
		byte[] msg = obtain();
		msg[0] = (byte) (MotionNotify);
		writeInt(msg, 2, x);
		writeInt(msg, 6, y);
		this.deque.add(msg);
	}
	public void setKey(int keyCode, boolean press , int keyChar){
		//处理鼠标按钮
		if(keyCode == 1001){
			setMouseButton((byte)1,press);
			return;
		}else if(keyCode == 1002){
			setMouseButton((byte)2,press);
			return;
		}


		byte[] msg = obtain();
		msg[0] = (byte) (press ? KeyPress : KeyRelease);
		writeInt(msg, 2, keyCode);
		writeInt(msg, 6, keyChar);
		this.deque.add(msg);
	}

}
