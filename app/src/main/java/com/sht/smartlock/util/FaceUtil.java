package com.sht.smartlock.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.ImageView;


import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.model.User;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jerry on 2015/6/27 0027.
 */
public class FaceUtil {
    public static final int POSITION_GALLERY = 0;
    public static final int POSITION_CAMERA = 1;

    private Activity mActivity;
    private Fragment mFragment;
    private String theLarge, theThumbnail;
    private File mImgFile;
    private ImageView mImageView;
    private boolean isModified;

    public FaceUtil(Activity activity, ImageView imageView) {
        this.mActivity = activity;
        this.mImageView = imageView;
    }

    public FaceUtil(Fragment fragment, ImageView imageView) {
        this.mFragment = fragment;
        this.mImageView = imageView;
    }

    public boolean isModified() {
        return isModified;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (msg.what == 1 && msg.obj != null) {
//                HttpClient.instance().modifyFace(mImgFile, new HttpCallBack() {
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                        ProgressDialog.show(getContext(), R.string.upload_loading);
//                    }
//
//                    @Override
//                    public void onSuccess(ResponseBean responseBean) {
//                        if (responseBean.isFailure()) {
//                            onFailure("", "");
//                            return;
//                        }
//                        ProgressDialog.disMiss();
//                        try {
//                            AppContext.toast(R.string.upload_success);
//                            String photoPath = responseBean.getString("photo");
//                            User user = AppContext.instance().getUser();
//                           // user.setPhoto(photoPath);
//                            AppContext.instance().setUser(user);
//                           // HttpClient.getImage(photoPath, mImageView);
//                            isModified = true;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            onFailure("", "");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String error, String message) {
//                        super.onFailure(error, message);
//                        ProgressDialog.disMiss();
//                        AppContext.toastFail(R.string.upload_fail);
//                    }
//                });
            }
        }
    };

    public void onActivityResult(final int requestCode, final int resultCode, final Intent imageReturnIntent) {
        if (resultCode != Activity.RESULT_OK)
            return;
        new Thread() {
            private String selectedImagePath;

            public void run() {
                Bitmap bitmap = null;
                if (requestCode == ImageUtil.REQUEST_CODE_GETIMAGE_BYSDCARD) {
                    if (imageReturnIntent == null)
                        return;
                    Uri selectedImageUri = imageReturnIntent.getData();
                    if (selectedImageUri != null) {
                        selectedImagePath = ImageUtil.getImagePath(selectedImageUri, (Activity) getContext());
                    }

                    if (selectedImagePath != null) {
                        theLarge = selectedImagePath;
                    } else {
                        bitmap = ImageUtil.loadPicasaImageFromGalley(selectedImageUri, (Activity) getContext());
                    }

                    if (AppContext.isMethodsCompat(Build.VERSION_CODES.ECLAIR_MR1)) {
                        String imaName = FileUtil.getFileName(theLarge);
                        if (imaName != null)
                            bitmap = ImageUtil.loadImgThumbnail((Activity) getContext(), imaName, MediaStore.Images.Thumbnails.MICRO_KIND);
                    }
                    if (bitmap == null && !StringUtil.isEmpty(theLarge))
                        bitmap = ImageUtil.loadImgThumbnail(theLarge, 100, 100);
                } else if (requestCode == ImageUtil.REQUEST_CODE_GETIMAGE_BYCAMERA) {
                    // 拍摄图片
                    if (bitmap == null && !StringUtil.isEmpty(theLarge)) {
                        bitmap = ImageUtil.loadImgThumbnail(theLarge, 100, 100);
                    }
                }

                if (bitmap != null) {// 存放照片的文件夹
                    String savePath = Config.SELECT_IMAGE_SAVE_PAHT;
                    File savedir = new File(savePath);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }
                    String largeFileName = FileUtil.getFileName(theLarge);
                    String largeFilePath = savePath + largeFileName;
                    // 判断是否已存在缩略图
                    if (largeFileName.startsWith("thumb_") && new File(largeFilePath).exists()) {
                        theThumbnail = largeFilePath;
                        mImgFile = new File(theThumbnail);
                    } else {
                        // 生成上传的800宽度图片
                        String thumbFileName = "thumb_" + largeFileName;
                        theThumbnail = savePath + thumbFileName;
                        if (new File(theThumbnail).exists()) {
                            mImgFile = new File(theThumbnail);
                        } else {
                            try {
                                // 压缩上传的图片
                                ImageUtil.createImageThumbnail(getContext(), theLarge, theThumbnail, 800, 80);
                                mImgFile = new File(theThumbnail);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // ------ ------ ------ ---- ---- -- -- - -
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    public void selectPicture(int position) {
        if (getActivity() == null && getFragment() == null) {
            AppContext.toastFail("无法打开，请重试");
            return;
        }
        switch (position) {
            case POSITION_GALLERY:
                Intent intent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    if (getActivity() != null) {
                        getActivity().startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtil.REQUEST_CODE_GETIMAGE_BYSDCARD);
                    } else if (getFragment() != null) {
                        getFragment().startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtil.REQUEST_CODE_GETIMAGE_BYSDCARD);
                    }
                } else {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    if (getActivity() != null) {
                        getActivity().startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtil.REQUEST_CODE_GETIMAGE_BYSDCARD);
                    } else if (getFragment() != null) {
                        getFragment().startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtil.REQUEST_CODE_GETIMAGE_BYSDCARD);
                    }
                }
                break;
            case POSITION_CAMERA:
                // 判断是否挂载了SD卡
                String savePath = "";
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    savePath = Config.SELECT_IMAGE_SAVE_PAHT;
                    File savedir = new File(savePath);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }
                }
//   a
                // 没有挂载SD卡，无法保存文件
                if (StringUtil.isEmpty(savePath)) {
                    AppContext.toastFail("无法保存照片，请检查SD卡是否挂载");
                    return;
                }

                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String fileName = getContext().getString(R.string.app_name) + "_" + timeStamp + ".jpg";// 照片命名
                File out = new File(savePath, fileName);
                Uri uri = Uri.fromFile(out);

                theLarge = savePath + fileName;// 该照片的绝对路径

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                if (getActivity() != null) {
                    getActivity().startActivityForResult(intent, ImageUtil.REQUEST_CODE_GETIMAGE_BYCAMERA);
                } else if (getFragment() != null) {
                    getFragment().startActivityForResult(intent, ImageUtil.REQUEST_CODE_GETIMAGE_BYCAMERA);
                }
                break;
            default:
                break;
        }
    }

    public Context getContext() {
        if (getActivity() != null) {
            return getActivity();
        }
        if (getFragment() != null) {
            return getFragment().getActivity();
        }
        return null;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }
}
