package com.aof.utils.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aof.utils.R;
import com.aof.utils.dialog.support.DialogSupports;

public class DialogUtils {

    public static void createBothChoicesDialog(Context context, String title, String message, String positiveButtonName, String negativeButtonName, final DialogSupports support){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonName,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                if(support != null){
                    support.runWhenPositive();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeButtonName,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                if(support != null){
                    support.runWhenNegative();
                }
                dialog.cancel();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static void createSingleChoiceDialog(Context context, String title, String message, String buttonName, final DialogSupports support){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(buttonName,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                if(support != null){
                    support.runWhenPositive();
                }
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static void createItemsChoiceDialog(Context context, String title, String positiveButtonName, String negativeButtonName, boolean cancelable, @NonNull String[] items, final DialogSupports support){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(title != null){
            builder.setTitle(title);
        }
        if(positiveButtonName != null){
            builder.setPositiveButton(positiveButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(support != null){
                        support.runWhenPositive();
                    }
                    dialog.dismiss();
                }
            });
        }
        if(negativeButtonName != null){
            builder.setNegativeButton(negativeButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(support != null){
                        support.runWhenNegative();
                    }
                    dialog.dismiss();
                }
            });
        }
        builder.setCancelable(cancelable);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(support != null){
                    support.runWhenItemsSelected(which);
                }
            }
        });
        builder.show();
    }

    public static com.aof.utils.dialog.support.TaskDialog createTaskDialog(Context context, String totalTaskName, String currentTaskName, boolean cancelable){
        return new com.aof.utils.dialog.support.TaskDialog(context,cancelable)
                .setCurrentTaskName(currentTaskName)
                .setTotalTaskName(totalTaskName);
    }

}
