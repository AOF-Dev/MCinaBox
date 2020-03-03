package cosine.boat;


import android.view.MotionEvent;
import android.os.Bundle;
import android.app.NativeActivity;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.nio.ByteBuffer;
import android.widget.LinearLayout;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import org.lwjgl.input.Keyboard;
import java.io.*;
import android.view.ViewGroup;

import static org.lwjgl.glfw.GLFW.*;

public class BoatClientActivity extends NativeActivity implements View.OnClickListener, View.OnTouchListener, TextWatcher, TextView.OnEditorActionListener
{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
		popupWindow = new PopupWindow();
		popupWindow.setWidth(LayoutParams.FILL_PARENT);
		popupWindow.setHeight(LayoutParams.FILL_PARENT);
		popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		popupWindow.setFocusable(true);
		base = (RelativeLayout)LayoutInflater.from(BoatClientActivity.this).inflate(R.layout.overlay,null);
		mouseCursor = (ImageView)base.findViewById(R.id.mouse_cursor);
		touchPad = this.findButton(R.id.touch_pad);
		controlUp = this.findButton(R.id.control_up);
		controlDown = this.findButton(R.id.control_down);
		controlLeft = this.findButton(R.id.control_left);
		controlRight = this.findButton(R.id.control_right);
		controlJump = this.findButton(R.id.control_jump);
		controlInv = this.findButton(R.id.control_inventory);
		controlLshift = this.findButton(R.id.control_lshift);
		control1 = this.findButton(R.id.control_1);
		control2 = this.findButton(R.id.control_2);
		control3 = this.findButton(R.id.control_3);
		control4 = this.findButton(R.id.control_4);
		control5 = this.findButton(R.id.control_5);
		control6 = this.findButton(R.id.control_6);
		control7 = this.findButton(R.id.control_7);
		control8 = this.findButton(R.id.control_8);
		control9 = this.findButton(R.id.control_9);
		itemBar = (LinearLayout)base.findViewById(R.id.item_bar);
		mousePrimary = this.findButton(R.id.mouse_primary);
		mouseSecondary = this.findButton(R.id.mouse_secondary);
		esc = this.findButton(R.id.esc);
		controlChat = this.findButton(R.id.control_chat);
		controlCommand = this.findButton(R.id.control_command);
		control3rd = this.findButton(R.id.control_3rd);
		inputScanner = (EditText)base.findViewById(R.id.input_scanner);
		inputScanner.setFocusable(true);
		inputScanner.addTextChangedListener(this);
		inputScanner.setOnEditorActionListener(this);
		inputScanner.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_FLAG_NO_FULLSCREEN|EditorInfo.IME_ACTION_DONE);
		inputScanner.setSelection(1);	
		
		int height = getWindowManager().getDefaultDisplay().getHeight();
		int width = getWindowManager().getDefaultDisplay().getWidth();	
		int scale = 1;	
		while(width / (scale + 1) >= 320 && height / (scale + 1) >= 240) {
			scale++;
		}	
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)itemBar.getLayoutParams();
		lp.height = 20 * scale;
		lp.width = 20 * scale * 9;
		itemBar.setLayoutParams(lp);
		
		popupWindow.setContentView(base);
		
		mHandler = new MyHandler();
		
	}

	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		super.onPause();
		popupWindow.dismiss();
	}
	
	


	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		// TODO: Implement this method
		
		super.surfaceCreated(holder);
		System.out.println("Surface is created!");
		
		
		new Thread(){
			@Override
			public void run(){
				
				LauncherConfig config = LauncherConfig.fromFile(getIntent().getExtras().getString("config"));
				LoadMe.exec(config, BoatClientActivity.this);		
				Message msg=new Message();
				msg.what = -1;
				mHandler.sendMessage(msg);
				
			}
		}.start();
		
		
	}
	
	

	//private boolean overlayCreated = false;
	private PopupWindow popupWindow;
	private RelativeLayout base;
	private Button touchPad;
	private Button controlUp;
	private Button controlDown;
	private Button controlLeft;
	private Button controlRight;
	private Button controlJump;
	private Button controlInv;
	private Button controlLshift;
	private Button control1;
	private Button control2;
	private Button control3;
	private Button control4;
	private Button control5;
	private Button control6;
	private Button control7;
	private Button control8;
	private Button control9;
	private LinearLayout itemBar;
	private Button mousePrimary;
	private Button mouseSecondary;
	private Button controlChat;
	private Button controlCommand;
	private Button control3rd;
	private ImageView mouseCursor;
	private Button esc;
	
	private EditText inputScanner;
	public boolean mode = false;
	
	private class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
				case 0:
					BoatClientActivity.this.mouseCursor.setVisibility(View.INVISIBLE);
					BoatClientActivity.this.itemBar.setVisibility(View.VISIBLE);
					BoatClientActivity.this.mode = true;
					break;
				case 1:
					BoatClientActivity.this.mouseCursor.setVisibility(View.VISIBLE);
					BoatClientActivity.this.itemBar.setVisibility(View.INVISIBLE);
					BoatClientActivity.this.mode = false;
					break;
				default:
				    BoatClientActivity.this.finish();
				    break;
			}
		}
	}
	private Button findButton(int id){
		Button b = (Button)base.findViewById(id);
		b.setOnTouchListener(this);
		return b;
	}
	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		if (p1 == inputScanner){
			inputScanner.setSelection(1);
		}
	}
	private MyHandler mHandler;
	public void setCursorMode(int mode){
		Message msg=new Message();
		msg.what = mode;
		mHandler.sendMessage(msg);
	}
	private int initialX;
	private int initialY;
	private int baseX;
	private int baseY;
	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void afterTextChanged(Editable p1)
	{
		// TODO: Implement this method
		String newText = p1.toString();
		if (newText.length() < 1){
			
			BoatInputEventSender.setKey(GLFW_KEY_BACKSPACE, true,0);
			BoatInputEventSender.setKey(GLFW_KEY_BACKSPACE, false,0);
			inputScanner.setText(">");
			inputScanner.setSelection(1);
		}
		if (newText.length() > 1){
			for(int i = 1; i < newText.length(); i++){
				BoatInputEventSender.setKey(0, true, newText.charAt(i));
				BoatInputEventSender.setKey(0, false, newText.charAt(i));
			}
			
			inputScanner.setText(">");
			inputScanner.setSelection(1);
		}
	}
	
	@Override
	public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
	{
		// TODO: Implement this method
		
		BoatInputEventSender.setKey(GLFW_KEY_ENTER, true, '\n');
		BoatInputEventSender.setKey(GLFW_KEY_ENTER, false, '\n');
        return false;  
	}

	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		
		if (p1 == inputScanner){
			inputScanner.setSelection(1);
			return false;

		}
		
		if (p1 == mousePrimary){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setMouseButton((byte)1, true);

			}
			if (p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setMouseButton((byte)1, false);

			}
			return false;
			
		}
		if (p1 == mouseSecondary){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setMouseButton((byte)3, true);

			}
			if (p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setMouseButton((byte)3, false);

			}
			return false;
		}
		if (p1 == controlChat){
			
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_T, true,0);

			}
			if (p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_T, false,0);

			}
			
			return false;
		}
		if (p1 == controlCommand){

			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_SLASH, true,0);

			}
			if (p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_SLASH, false,0);

			}

			return false;
		}
		if (p1 == control3rd){

			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_F5, true,0);

			}
			
			if (p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_F5, false,0);

			}
			

			return false;
		}
		if (p1 == control1){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_1, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_1, false, 0);

			}
			return false;
		}
		if (p1 == control2){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_2, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_2, false, 0);

			}
			return false;
		}
		if (p1 == control3){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_3, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_3, false, 0);

			}
			return false;
		}
		if (p1 == control4){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_4, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_4, false, 0);

			}
			return false;
		}
		if (p1 == control5){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_5, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_5, false, 0);

			}
			return false;
		}
		if (p1 == control6){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_6, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_6, false, 0);

			}
			return false;
		}
		if (p1 == control7){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_7, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_7, false, 0);

			}
			return false;
		}
		if (p1 == control8){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_8, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_8, false, 0);

			}
			return false;
		}
		if (p1 == control9){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_9, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_9, false, 0);

			}
			return false;
		}
		if (p1 == controlUp){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_W, true, 0);
				
			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_W, false, 0);
				
			}
			return false;
		}
		if (p1 == controlInv){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_E, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_E, false, 0);

			}
			return false;
		}
		if (p1 == controlLshift){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_LEFT_SHIFT, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_LEFT_SHIFT, false, 0);

			}
			return false;
		}
		if (p1 == controlDown){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_S, true, 0);
				
			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_S, false, 0);
				
			}
			return false;
		}
		if (p1 == controlLeft){
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_A, true, 0);
				
			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_A, false, 0);
				
			}
			return false;
		}
		if (p1 == controlRight){
			
			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_D, true, 0);
				
			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_D, false, 0);
				
			}
			return false;
		}
		if (p1 == controlJump){

			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_SPACE, true, 0);
				
			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_SPACE, false, 0);
				
			}
			return false;
		}
		if (p1 == esc){

			if (p2.getActionMasked() == MotionEvent.ACTION_DOWN){
				BoatInputEventSender.setKey(GLFW_KEY_ESCAPE, true, 0);

			}
			else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
				BoatInputEventSender.setKey(GLFW_KEY_ESCAPE, false, 0);

			}
			return false;
		}
		// TODO: Implement this method
		if (p1 == touchPad){
			if (mode){
				switch(p2.getActionMasked()){
					case MotionEvent.ACTION_DOWN:
						initialX = (int)p2.getX();
						initialY = (int)p2.getY();
					case MotionEvent.ACTION_MOVE:
						BoatInputEventSender.setPointer(baseX + (int)p2.getX() -initialX, baseY + (int)p2.getY() - initialY);
						break;
					case MotionEvent.ACTION_UP:
						baseX += ((int)p2.getX() - initialX);
						baseY += ((int)p2.getY() - initialY);
						
						BoatInputEventSender.setPointer(baseX, baseY);
						break;
					default:
						break;
				}
			}
			else{
				baseX = (int)p2.getX();
				baseY = (int)p2.getY();
				BoatInputEventSender.setPointer(baseX, baseY);
				
				
			}
			
			mouseCursor.setX(p2.getX());
			mouseCursor.setY(p2.getY());
			return true;
		}
		return false;
		
	}

	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		// TODO: Implement this method
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus){
			popupWindow.showAtLocation(BoatClientActivity.this.getWindow().getDecorView(),Gravity.TOP|Gravity.LEFT,0,0);	
			
		}
		
	}
	
	
	
	
}



