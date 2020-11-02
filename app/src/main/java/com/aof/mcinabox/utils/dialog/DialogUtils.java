package com.aof.mcinabox.utils.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aof.mcinabox.utils.dialog.support.DialogSupports;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;

import zhou.tools.fileselector.FileSelectorAlertDialog;
import zhou.tools.fileselector.config.FileConfig;
import zhou.tools.fileselector.config.FileTheme;
import zhou.tools.fileselector.utils.FileFilter;

public class DialogUtils {

    public final static int COLORPICKER_LIGHTNESS_ONLY = 0;
    public final static int COLORPICKER_ALPHA_ONLY = 1;
    public final static int COLORPICKER_ALL = 2;

    public static void createBothChoicesDialog(Context context, String title, String message, String positiveButtonName, String negativeButtonName, final DialogSupports support) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (support != null) {
                    support.runWhenPositive();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeButtonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (support != null) {
                    support.runWhenNegative();
                }
                dialog.cancel();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static void createSingleChoiceDialog(Context context, String title, String message, String buttonName, final DialogSupports support) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (support != null) {
                    support.runWhenPositive();
                }
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static void createItemsChoiceDialog(Context context, String title, String positiveButtonName, String negativeButtonName, String neutralButtonName, boolean cancelable, @NonNull String[] items, final DialogSupports support) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        if (positiveButtonName != null) {
            builder.setPositiveButton(positiveButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (support != null) {
                        support.runWhenPositive();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (negativeButtonName != null) {
            builder.setNegativeButton(negativeButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (support != null) {
                        support.runWhenNegative();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (neutralButtonName != null) {
            builder.setNegativeButton(neutralButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (support != null) {
                        support.runWhenNeutral();
                    }
                    dialog.dismiss();
                }
            });
        }
        builder.setCancelable(cancelable);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (support != null) {
                    support.runWhenItemsSelected(which);
                }
            }
        });
        builder.show();
    }

    public static com.aof.mcinabox.utils.dialog.support.TaskDialog createTaskDialog(Context context, String totalTaskName, String currentTaskName, boolean cancelable) {
        return new com.aof.mcinabox.utils.dialog.support.TaskDialog(context, cancelable)
                .setCurrentTaskName(currentTaskName)
                .setTotalTaskName(totalTaskName);
    }

    public static void createColorPickerDialog(Context context, String title, String pName, String nName, int initColor, int type, final DialogSupports support) {
        ColorPickerDialogBuilder picker = ColorPickerDialogBuilder
                .with(context)
                .setTitle(title)
                .initialColor(initColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        if (support != null) {
                            support.runWhenItemsSelected();
                        }
                    }
                })
                .setPositiveButton(pName, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if (support != null) {
                            support.runWhenColorSelected(new int[]{selectedColor});
                        }
                    }
                })
                .setNegativeButton(nName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (support != null) {
                            support.runWhenNegative();
                        }
                    }
                });
        switch (type) {
            case COLORPICKER_LIGHTNESS_ONLY:
                picker.lightnessSliderOnly();
                break;
            case COLORPICKER_ALPHA_ONLY:
                picker.alphaSliderOnly();
                break;
            case COLORPICKER_ALL:
                break;
        }
        picker.build().show();
    }

    public static void createFileSelectorDialog(@NonNull Context context, String title, @NonNull String startPath, String[] filter, @Nullable final DialogSupports support){
        FileConfig fileConfig = new FileConfig();
        fileConfig.startPath = startPath;
        fileConfig.rootPath = "/";
        fileConfig.theme = FileTheme.THEME_WHITE;
        if(filter == null){
            fileConfig.positiveFiter = false;
        }else{
            fileConfig.positiveFiter = true;
            fileConfig.filterModel = FileFilter.FILTER_CUSTOM;
            fileConfig.filter = filter;
        }
        fileConfig.showHiddenFiles = true;
        fileConfig.multiModel = false;
        if(title != null){
            fileConfig.title = title;
        }

        final FileSelectorAlertDialog fileDialog = new FileSelectorAlertDialog(context,fileConfig);
        fileDialog.setOnSelectFinishListener(new FileSelectorAlertDialog.OnSelectFinishListener() {
            @Override
            public void selectFinish(ArrayList<String> paths) {
                if(support != null){
                    support.runWhenItemsSelected(paths.get(0));
                }
      //          fileDialog.dismiss();
            }
        });
        fileDialog.show();
    }

}
