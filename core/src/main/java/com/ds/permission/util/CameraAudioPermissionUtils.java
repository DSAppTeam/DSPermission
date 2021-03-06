package com.ds.permission.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.ds.permission.PermissionManager;
import com.ds.permission.PermissionConfig;
import com.ds.permission.R;
import com.ds.permission.ResultCall;
import com.ds.permission.core.RxPermissions;
import com.ds.permission.ui.dialog.CommonDialog;

import java.util.ArrayList;
import java.util.List;

public class CameraAudioPermissionUtils {
    private static final String TAG = "CAPermissionUtils";

    public static final int REQUEST_CODE_REQUEST_PERMISSION = 0x64;

    public static boolean checkPermissions(@NonNull Activity activity) {
        return checkPermissions(activity, true, false, null);
    }

    public static boolean checkPermissions(@NonNull Activity activity, boolean isFinish, boolean needDeniedDialog, Runnable grantedRunnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions;
            boolean cameraGranted = PermissionUtils.check(activity, Manifest.permission.CAMERA);
            boolean audioGranted = PermissionUtils.check(activity, Manifest.permission.RECORD_AUDIO);
            if (cameraGranted && audioGranted) {
                return true;
            }
            if (!cameraGranted && !audioGranted) {
                permissions = new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                };
            } else if (cameraGranted) {
                permissions = new String[]{Manifest.permission.RECORD_AUDIO};
            } else {
                permissions = new String[]{Manifest.permission.CAMERA};
            }
            RxPermissions manager = PermissionManager.get().inject(activity);
            if (manager != null) {
                manager.request(new ResultCall() {
                                    @Override
                                    public void denied(boolean never) {
                                        if (needDeniedDialog) {
                                            onRequestPermissionsDenied(activity, permissions);
                                        }
                                    }

                                    @Override
                                    public void granted() {
                                        if (grantedRunnable != null) {
                                            grantedRunnable.run();
                                        }
                                    }
                                }, new PermissionConfig.Builder()
                                .addPermission(permissions)
                                .setShowSysSettingDialog(!needDeniedDialog)
                                .build()
                );
            }
            return false;
        } else {
            boolean cameraGranted = checkCameraPermission(activity);
            if (!cameraGranted) {
                showPermissionDialog(activity, activity.getString(R.string.libpermission_permission_camera_need), isFinish);
                return false;
            }
            boolean audioGranted = checkAudioPermission();
            if (!audioGranted) {
                showPermissionDialog(activity, activity.getString(R.string.libpermission_permission_audio_need), isFinish);
                return false;
            }
            return true;
        }
    }

    public static boolean checkPermissionsForFaceDetect(@NonNull Activity activity, boolean isFinish) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean cameraGranted = PermissionUtils.check(activity, Manifest.permission.CAMERA);
            boolean audioGranted = PermissionUtils.check(activity, Manifest.permission.RECORD_AUDIO);
            boolean writeGranted = PermissionUtils.check(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (cameraGranted && audioGranted && writeGranted) {
                return true;
            }
            List<String> reqList = new ArrayList<>();
            if (!cameraGranted) {
                reqList.add(Manifest.permission.CAMERA);
            }
            if (!audioGranted) {
                reqList.add(Manifest.permission.RECORD_AUDIO);
            }
            if (!writeGranted) {
                reqList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            String[] permissions = new String[reqList.size()];
            reqList.toArray(permissions);
            RxPermissions manager = PermissionManager.get().inject(activity);
            if (manager != null) {
                manager.request(new ResultCall() {
                    @Override
                    public void granted() {

                    }

                    @Override
                    public void denied(boolean never) {

                    }
                }, new PermissionConfig.Builder().addPermission(permissions).build());
            }
            return false;
        } else {
            boolean cameraGranted = checkCameraPermission(activity);
            if (!cameraGranted) {
                showPermissionDialog(activity, activity.getString(R.string.libpermission_permission_camera_need), isFinish);
                return false;
            }
            boolean audioGranted = checkAudioPermission();
            if (!audioGranted) {
                showPermissionDialog(activity, activity.getString(R.string.libpermission_permission_audio_need), isFinish);
                return false;
            }
            return true;
        }
    }

    public static boolean checkRecordAudioPermissionForComment(Activity activity, DialogInterface.OnDismissListener onDismissListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions;
            boolean audioGranted = PermissionUtils.check(activity, Manifest.permission.RECORD_AUDIO);
            if (audioGranted) {
                return true;
            }
            permissions = new String[]{Manifest.permission.RECORD_AUDIO};
            RxPermissions manager = PermissionManager.get().inject(activity);
            if (manager != null) {
                manager.request(new ResultCall() {
                    @Override
                    public void granted() {

                    }

                    @Override
                    public void denied(boolean never) {

                    }
                }, new PermissionConfig.Builder().addPermission(permissions).build());
            }
            return false;
        } else {
            boolean audioGranted = checkAudioPermission();
            if (!audioGranted) {
                showPermissionDialogForComment(activity, onDismissListener);
                return false;
            }
            return true;
        }
    }

    public static void showPermissionDialogForComment(Activity activity, DialogInterface.OnDismissListener onDismissListener) {
        final CommonDialog commonDialog = new CommonDialog(activity);
        commonDialog.setTitle(activity.getString(R.string.libpermission_camera_audio_permission_utils_unable_to_use_voice));
        commonDialog.setContent(activity.getString(R.string.libpermission_camera_audio_permission_utils_please_open_n_permission_of_ds_to_use_microphone_in_settings));
        commonDialog.setLeftAction(activity.getString(R.string.libpermission_camera_audio_permission_utils_not_set_up_temporarily), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
                commonDialog.setWhich(CommonDialog.WHICH_LEFT);
            }
        });
        commonDialog.setRightAction(activity.getString(R.string.libpermission_camera_audio_permission_utils_go_to_set_up), R.color.libpermission_textColorPrimary, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
                commonDialog.setWhich(CommonDialog.WHICH_RIGHT);
                CameraAudioPermissionUtils.startAppDetailsSetting(activity);
            }
        });
        commonDialog.setCanceledOnTouchOutside(false);
        if (onDismissListener != null) {
            commonDialog.setOnDismissListener(onDismissListener);
        }
        commonDialog.show();
    }

    public static boolean checkPermissionsForCamera(@NonNull Activity activity, boolean isFinish) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions;
            boolean cameraGranted = PermissionUtils.check(activity, Manifest.permission.CAMERA);
            if (cameraGranted) {
                return true;
            }
            permissions = new String[]{Manifest.permission.CAMERA};
            RxPermissions manager = PermissionManager.get().inject(activity);
            if (manager != null) {
                manager.request(new ResultCall() {
                    @Override
                    public void granted() {

                    }

                    @Override
                    public void denied(boolean never) {

                    }
                }, new PermissionConfig.Builder().addPermission(permissions).build());
            }
            return false;
        } else {
            boolean cameraGranted = checkCameraPermission(activity);
            if (!cameraGranted) {
                showPermissionDialog(activity, activity.getString(R.string.libpermission_permission_camera_need), isFinish);
                return false;
            }
            return true;
        }
    }

    public static boolean checkPermissionsForTakeIdCard(@NonNull Activity activity, boolean isFinish) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean cameraGranted = PermissionUtils.check(activity, Manifest.permission.CAMERA);
            boolean writeGranted = PermissionUtils.check(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (cameraGranted && writeGranted) {
                return true;
            }

            List<String> reqList = new ArrayList<>();
            if (!cameraGranted) {
                reqList.add(Manifest.permission.CAMERA);
            }
            if (!writeGranted) {
                reqList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            String[] permissions = new String[reqList.size()];
            reqList.toArray(permissions);
            RxPermissions manager = PermissionManager.get().inject(activity);
            if (manager != null) {
                manager.request(new ResultCall() {
                    @Override
                    public void granted() {

                    }

                    @Override
                    public void denied(boolean never) {

                    }
                }, new PermissionConfig.Builder().addPermission(permissions).build());
            }
            return false;
        } else {
            boolean cameraGranted = checkCameraPermission(activity);
            if (!cameraGranted) {
                showPermissionDialog(activity, activity.getString(R.string.libpermission_permission_camera_need), isFinish);
                return false;
            }
            return true;
        }
    }

    public static void onRequestPermissionsDenied(Activity context, String[] permissions) {
        onRequestPermissionsDenied(context, permissions, false);
    }

    public static void onRequestPermissionsDenied(Activity context, String[] permissions, boolean isFinish) {
        for (String permission : permissions) {
            if (!PermissionUtils.check(context, permission)) {
                String content = "";
                if (permission.equals(Manifest.permission.CAMERA)) {
                    content = context.getString(R.string.libpermission_permission_camera_need);
                } else if (TextUtils.equals(permission, Manifest.permission.RECORD_AUDIO)) {
                    content = context.getString(R.string.libpermission_permission_audio_need);
                }
                if (!TextUtils.isEmpty(content)) {
                    CameraAudioPermissionUtils.showPermissionDialog(context, content, isFinish);
                    return;
                }
            }
        }
    }

    public static boolean checkPermissionsForTakeAvator(@NonNull Activity activity) {
        return checkPermissionsForTakeAvator(activity, false, null);
    }

    public static boolean checkPermissionsForTakeAvator(@NonNull Activity activity, boolean needDeniedDialog, Runnable grantedRunnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions;
            boolean cameraGranted = PermissionUtils.check(activity, Manifest.permission.CAMERA);
            if (cameraGranted) {
                return true;
            } else {
                permissions = new String[]{Manifest.permission.CAMERA};
            }
            RxPermissions manager = PermissionManager.get().inject(activity);
            if (manager != null) {
                manager.request(new ResultCall() {
                                    @Override
                                    public void granted() {
                                        if (grantedRunnable != null) {
                                            grantedRunnable.run();
                                        }
                                    }

                                    @Override
                                    public void denied(boolean never) {
                                        if (needDeniedDialog) {
                                            onRequestPermissionsDenied(activity, permissions);
                                        }
                                    }
                                }, new PermissionConfig.Builder()
                                .addPermission(permissions)
                                .setShowSysSettingDialog(!needDeniedDialog)
                                .build()
                );
            }
            return false;
        } else {
            boolean cameraGranted = checkCameraPermission(activity);
            if (!cameraGranted) {
                showPermissionDialog(activity, activity.getString(R.string.libpermission_permission_camera_need_for_take_avator), true);
                return false;
            }
            return true;
        }
    }

    public static void showPermissionDialog(final Activity activity, String content, final boolean finish) {
        final CommonDialog commonDialog = new CommonDialog(activity);
        commonDialog.setContent(content);
        commonDialog.setLeftAction(R.string.libpermission_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
                if (finish) {
                    activity.finish();
                }
            }
        });
        commonDialog.setRightAction(R.string.libpermission_permission_apply_goto_app_settings, R.color.libpermission_themeColorPrimary,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonDialog.dismiss();
                        startAppDetailsSetting(activity);
                    }
                });
        commonDialog.show();
    }

    public static void startAppDetailsSetting(Activity activity) {
        try {
            //???????????????????????????
            if (TextUtils.equals(BrandUtils.getSystemInfo().getOs(), BrandUtils.SYS_MIUI)) {
                Intent miuiIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                miuiIntent.putExtra("extra_pkgname", activity.getPackageName());
                //???????????????????????????Intent???Activity??????
                List<ResolveInfo> resolveInfos = activity.getPackageManager().queryIntentActivities(miuiIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfos.size() > 0) {
                    //                activity.startActivityForResult(miuiIntent, CODE_REQUEST_CAMERA_PERMISSIONS);
                    activity.startActivity(miuiIntent);
                    return;
                }
            }
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS").setData(uri);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????Android 6.0 ?????????????????????
     *
     * @return
     */
    public static boolean checkCameraPermission(Context context) {
        boolean canUse = true;
        try {
            CameraPermissionTest cameraPermissionTest = new CameraPermissionTest(context);
            return cameraPermissionTest.test();
        } catch (Exception e) {
            canUse = false;
            e.printStackTrace();
        } catch (Throwable throwable) {
            canUse = false;
            throwable.printStackTrace();
        }
        return canUse;
    }

    // ???????????????
    public static int audioSource = MediaRecorder.AudioSource.MIC;

    // ????????????????????????44100???????????????????????????????????????????????????22050???16000???11025
    public static int sampleRateInHz = 44100;

    // ??????????????????????????????CHANNEL_IN_STEREO???????????????CHANNEL_CONFIGURATION_MONO????????????
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;

    // ??????????????????:PCM 16???????????????????????????????????????PCM 8???????????????????????????????????????????????????
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    // ?????????????????????
    public static int bufferSizeInBytes = 0;

    /**
     * ??????Android 6.0 ???????????????????????????
     */
    public static boolean checkAudioPermission() {
        AudioRecord audioRecord = null;
        try {
            // ?????????????????????
            int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                    channelConfig, audioFormat);
            audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                    channelConfig, audioFormat, bufferSizeInBytes);

            //??????????????????
            try {
                // ???????????????????????????????????????
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return false;
            }

            /**
             * ?????????????????????????????????????????????
             */
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING
                    && audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }

//            if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
//                // ??????????????????????????????????????????audioRecord??????????????????????????????????????????RECORDSTATE_STOPPED?????????
//                // ??????read????????????0???size?????????????????????????????????
//                return true;
//            }

            byte[] bytes = new byte[bufferSizeInBytes];
            int readSize = audioRecord.read(bytes, 0, 1024);
            if (readSize == AudioRecord.ERROR_INVALID_OPERATION || readSize <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "catch1 checkAudioPermission: ", e);
            return false;
        } finally {
            if (audioRecord != null) {
                try {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                } catch (Exception e) {
                    Log.e(TAG, "catch2 checkAudioPermission: ", e);
                }
            }
        }
    }
}
