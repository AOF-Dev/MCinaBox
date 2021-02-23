package com.aof.mcinabox.utils.dialog;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aof.mcinabox.filechooser.FileChooserDialog;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;
import com.aof.mcinabox.utils.dialog.support.TaskDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class DialogUtils {

    public final static int COLORPICKER_LIGHTNESS_ONLY = 0;
    public final static int COLORPICKER_ALPHA_ONLY = 1;
    public final static int COLORPICKER_ALL = 2;

    public static void createBothChoicesDialog(Context context, String title, String message, String positiveButtonName, String negativeButtonName, final DialogSupports support) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonName, (dialog, which) -> {
            if (support != null) {
                support.runWhenPositive();
            }
            dialog.dismiss();
        });
        builder.setNegativeButton(negativeButtonName, (dialog, which) -> {
            if (support != null) {
                support.runWhenNegative();
            }
            dialog.cancel();
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static void createSingleChoiceDialog(Context context, String title, String message, String buttonName, final DialogSupports support) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(buttonName, (dialog, which) -> {
            if (support != null) {
                support.runWhenPositive();
            }
            dialog.dismiss();
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
            builder.setPositiveButton(positiveButtonName, (dialog, which) -> {
                if (support != null) {
                    support.runWhenPositive();
                }
                dialog.dismiss();
            });
        }
        if (negativeButtonName != null) {
            builder.setNegativeButton(negativeButtonName, (dialog, which) -> {
                if (support != null) {
                    support.runWhenNegative();
                }
                dialog.dismiss();
            });
        }
        if (neutralButtonName != null) {
            builder.setNegativeButton(neutralButtonName, (dialog, which) -> {
                if (support != null) {
                    support.runWhenNeutral();
                }
                dialog.dismiss();
            });
        }
        builder.setCancelable(cancelable);
        builder.setItems(items, (dialog, which) -> {
            if (support != null) {
                support.runWhenItemsSelected(which);
            }
        });
        builder.show();
    }

    public static TaskDialog createTaskDialog(Context context, String totalTaskName, String currentTaskName, boolean cancelable) {
        return new TaskDialog(context, cancelable)
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
                .setOnColorSelectedListener(selectedColor -> {
                    if (support != null) {
                        support.runWhenItemsSelected();
                    }
                })
                .setPositiveButton(pName, (dialog, selectedColor, allColors) -> {
                    if (support != null) {
                        support.runWhenColorSelected(new int[]{selectedColor});
                    }
                })
                .setNegativeButton(nName, (dialog, which) -> {
                    if (support != null) {
                        support.runWhenNegative();
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

    public static void createFileSelectorDialog(@NonNull Context context, String title, @NonNull String startPath, String extension, @Nullable final DialogSupports support){
        new FileChooserDialog.Builder(context, file -> {
            if (support != null) {
                support.runWhenItemsSelected(file.getAbsolutePath());
            }
        })
                .setTitle(title)
                .setExtension(extension)
                .setStartPath(startPath)
                .build()
                .show();
    }

}
