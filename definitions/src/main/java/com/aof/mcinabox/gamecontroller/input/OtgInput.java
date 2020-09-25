package com.aof.mcinabox.gamecontroller.input;

import android.view.KeyEvent;
import android.view.View;

import com.aof.mcinabox.definitions.id.AppEvent;

public interface OtgInput extends Input , AppEvent , KeyEvent.Callback , View.OnHoverListener {
}
