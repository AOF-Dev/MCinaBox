package com.aof.mcinabox.gamecontroller.ckb;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.ckb.achieve.CkbManager;
import com.aof.mcinabox.gamecontroller.ckb.achieve.CkbManagerDialog;
import com.aof.mcinabox.gamecontroller.ckb.support.CallCustomizeKeyboard;
import com.aof.mcinabox.utils.DisplayUtils;
import com.aof.mcinabox.utils.PicUtils;

public class CustomizeKeyboardEditorActivity extends AppCompatActivity implements View.OnClickListener, DrawerLayout.DrawerListener, CallCustomizeKeyboard {

    private Toolbar mToolbar;
    private ViewGroup mLayout_main;
    private DrawerLayout mDrawerLayout;
    private DragFloatActionButton dButton;
    private CkbManagerDialog mDialog;
    private CkbManager mManager;

    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏系统状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ckbe);

        //初始化
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        initUI();
    }

    private void initUI() {

        mToolbar = findViewById(R.id.ckbe_toolbar);
        mLayout_main = findViewById(R.id.ckbe_layout_main);
        mDrawerLayout = findViewById(R.id.ckbe_drawerlayout);
        dButton = new DragFloatActionButton(this);
        mManager = new CkbManager(this, this, null);
        mDialog = new CkbManagerDialog(this, mManager);

        //配置悬浮按钮
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(this, 30), DisplayUtils.getPxFromDp(this, 30));
        this.addContentView(dButton, lp);
        dButton.setBackground(ContextCompat.getDrawable(this, R.drawable.background_floatbutton));
        dButton.setTodo(new ArrangeRule() {
            @Override
            public void run() {
                mDialog.show();
            }
        });
        dButton.setY((float) screenHeight / 2);

        //设定工具栏
        setSupportActionBar(mToolbar);

        //设定监听
        mLayout_main.setOnClickListener(this);
        mDrawerLayout.addDrawerListener(this);

        //设置背景
        mLayout_main.setBackground(new BitmapDrawable(getResources(), PicUtils.blur(this, 10, ((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.background)).getBitmap())));
    }


    @Override
    public void onClick(View v) {
        if (v == mLayout_main) {
            switchToolbar();
        }
    }

    private Float viewPosY;

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        if (viewPosY == null) {
            viewPosY = mToolbar.getY();
        }
        int viewHeight = mToolbar.getHeight();
        float slideSize = viewHeight * slideOffset;
        mToolbar.setY(viewPosY - slideSize);

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    private void switchToolbar() {
        int v = View.VISIBLE;
        switch (mToolbar.getVisibility()) {
            case View.INVISIBLE:
            case View.GONE:
                v = View.VISIBLE;
                break;
            case View.VISIBLE:
                v = View.GONE;
                break;
            default:
                break;
        }
        mToolbar.setVisibility(v);
    }

    @Override
    public void addView(View view) {
        if (view.getLayoutParams() == null) {
            return;
        }
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            this.mLayout_main.addView(view);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(view.getLayoutParams().width, view.getLayoutParams().height);
            view.setLayoutParams(params);
            this.mLayout_main.addView(view);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //当Activity停止的时候自动保存键盘配置
        mManager.autoSaveKeyboard();
    }

    private static class DragFloatActionButton extends LinearLayout implements ViewGroup.OnTouchListener {

        private static final String TAG = "DragButton";
        private int parentHeight;
        private int parentWidth;

        private int lastX;
        private int lastY;

        private boolean isDrag;
        private ViewGroup parent;

        private ArrangeRule aRule;


        public DragFloatActionButton(Context context) {
            super(context);
            this.setOnTouchListener(this);
        }

        public DragFloatActionButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean performClick() {
            super.performClick();
            return false;
        }

        public void behave(MotionEvent event) {
            int rawX = (int) event.getRawX();
            int rawY = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDrag = false;
                    this.setAlpha(0.9f);
                    getParent().requestDisallowInterceptTouchEvent(true);
                    lastX = rawX;
                    lastY = rawY;
                    if (getParent() != null) {
                        parent = (ViewGroup) getParent();
                        parentHeight = parent.getHeight();
                        parentWidth = parent.getWidth();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.setAlpha(0.9f);
                    int dx = rawX - lastX;
                    int dy = rawY - lastY;
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    if (distance > 2 && !isDrag) {
                        isDrag = true;
                    }

                    float x = getX() + dx;
                    float y = getY() + dy;
                    //检测是否到达边缘 左上右下
                    x = x < 0 ? 0 : x > parentWidth - getWidth() ? parentWidth - getWidth() : x;
                    y = getY() < 0 ? 0 : getY() + getHeight() > parentHeight ? parentHeight - getHeight() : y;
                    setX(x);
                    setY(y);
                    lastX = rawX;
                    lastY = rawY;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isDrag) {
                        //恢复按压效果
                        setPressed(false);
                        moveHide(rawX);
                    } else {
                        //执行点击操作
                        startTodo();
                    }
                    break;
            }
        }

        private void moveHide(int rawX) {
            if (rawX >= parentWidth / 2) {
                //靠右吸附
                ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), parentWidth - getWidth());
                oa.setInterpolator(new DecelerateInterpolator());
                oa.setDuration(500);
                oa.start();
            } else {
                //靠左吸附
                ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                oa.setInterpolator(new DecelerateInterpolator());
                oa.setDuration(500);
                oa.start();
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == this) {
                this.behave(event);
                return true;
            }
            return false;
        }

        public void setTodo(ArrangeRule ar) {
            this.aRule = ar;
        }

        public void startTodo() {
            if (aRule != null) {
                aRule.run();
            }
        }
    }

    private static class ArrangeRule {
        public void run() {
            // Override this method.
        }
    }
}
