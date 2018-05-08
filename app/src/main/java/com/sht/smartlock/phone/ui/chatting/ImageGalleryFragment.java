package com.sht.smartlock.phone.ui.chatting;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.sht.smartlock.phone.common.dialog.ECListDialog;
import com.sht.smartlock.phone.common.utils.DemoUtils;
import com.sht.smartlock.phone.common.utils.FileAccessor;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.core.ECAsyncTask;
import com.sht.smartlock.phone.photoview.PhotoView;
import com.sht.smartlock.phone.photoview.PhotoViewAttacher;
import com.sht.smartlock.phone.storage.IMessageSqlManager;
import com.sht.smartlock.phone.storage.ImgInfoSqlManager;
import com.sht.smartlock.phone.ui.CCPFragment;
import com.sht.smartlock.phone.ui.SDKCoreHelper;
import com.sht.smartlock.phone.ui.chatting.model.ImgInfo;
import com.yuntongxun.ecsdk.ECChatManager.OnDeleteMessageListener;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECMessage.Direction;
import com.yuntongxun.ecsdk.SdkErrorCode;

import java.io.File;

/**
 * com.yuntongxun.ecdemo.ui.chatting in ECDemo_Android
 * Created by Jorstin on 2015/3/30.
 */
public class ImageGalleryFragment extends CCPFragment {

    private static final String TAG = "ImageGalleryFragment";
    
    

    @Override
    protected int getLayoutId() {
        return R.layout.image_grallery_fragment;
    }

    private String mImageUrl;
    private PhotoView mImageView;
    private LinearLayout progressBar;
    private TextView mProgress;
    private ViewImageInfo mEntry;
    private Bitmap mThumbnailBitmap;

    private WebView webView;
    private File imgCacheFile;
    private String mCacheImageUrl;
    private FrameLayout mViewContainer;
    private FrameLayout mSuccessLayout;
    private LinearLayout mFailLayout;
    private boolean isGif = false;

    public static ImageGalleryFragment newInstance(String imageUrl) {
        final ImageGalleryFragment f = new ImageGalleryFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }

    public static ImageGalleryFragment newInstance(ViewImageInfo entry) {
        final ImageGalleryFragment f = new ImageGalleryFragment();
        final Bundle args = new Bundle();
        args.putParcelable("entry", entry);
        f.setArguments(args);

        return f;
    }
    public static int i=0;
    
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEntry  = getArguments() != null ? getArguments().<ViewImageInfo>getParcelable("entry") : null;
       
    }


    @Override
    public int getTitleLayoutId() {
        return -1;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewContainer = (FrameLayout) findViewById(R.id.image_container);
        mSuccessLayout = (FrameLayout) findViewById(R.id.image_gallery_download_success);
        mFailLayout = (LinearLayout) findViewById(R.id.image_gallery_download_fail);
        mImageView = (PhotoView) findViewById(R.id.image);
        i++;
        
        if (mEntry == null) {
            finish();
            return ;
        }
        
        
        if(!IMessageSqlManager.isFireMsg(mEntry.getMsgLocalId())){
        	
        	mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ECListDialog dialog = new ECListDialog(getActivity() , new String[]{getString(R.string.save_to_local)});
                    dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                        @Override
                        public void onDialogItemClick(Dialog d, int position) {
                            handleDialogItemClick(position);
                        }
                    });
                    dialog.show();
                    return false;
                }
            });
        }
        
        
        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });
        initWebView();
        progressBar = (LinearLayout) findViewById(R.id.loading);
        mProgress = (TextView) findViewById(R.id.uploading_tv);
        
        if(mThumbnailBitmap == null){
            mThumbnailBitmap = BitmapFactory.decodeFile(FileAccessor.getImagePathName() + "/" + mEntry.getThumbnailurl());
            isGif = mEntry.getThumbnailurl().endsWith(".gif");
        }

        // 查看大图是否已经在本地
        mImageUrl = mEntry.getPicurl();
        if (null != mImageUrl && !TextUtils.isEmpty(mImageUrl) && !mImageUrl.startsWith("http")) {
            // load 本地
            mImageUrl = "file://" + FileAccessor.getImagePathName() + "/"+  mImageUrl;
        } else {
            // 下载
            mImageUrl = mEntry.getPicurl();
        }
        if(!isGif) {
            isGif = mImageUrl.endsWith(".gif");
        }
        mEntry.setIsGif(isGif);
        
        
        if(IMessageSqlManager.isFireMsg(mEntry.getMsgLocalId())&&("1".equals(IMessageSqlManager.getMsgReadStatus(mEntry.getMsgLocalId())))){
        	mImageView.setImageResource(R.drawable.fire_msg_big);
        	return;
        }
        
        DisplayImageOptions.Builder imageOptionsBuilder = DemoUtils.getChatDisplayImageOptionsBuilder();
        imageOptionsBuilder.showImageOnLoading(new BitmapDrawable(mThumbnailBitmap));
        
        boolean isFireMsg= IMessageSqlManager.isFireMsg(mEntry.getMsgLocalId());
        
        ECMessage msg=IMessageSqlManager.getMsg(mEntry.getMsgLocalId());
        
		if(i==1&&msg!=null&&msg.getDirection()==Direction.RECEIVE&&ImageGralleryPagerActivity.isFireMsg&&isFireMsg&&!CCPAppManager.getUserId().equalsIgnoreCase(msg.getForm())){
		   IMessageSqlManager.updateMsgReadStatus(mEntry.getMsgLocalId(), true);
		   ECAsyncTask.execute(new Runnable() {
               @Override
               public void run() {
                   SDKCoreHelper.getECChatManager().deleteMessage(IMessageSqlManager.getMsg(mEntry.getMsgLocalId()), new OnDeleteMessageListener() {
                       @Override
                       public void onDeleteMessage(ECError error, ECMessage arg1) {
                           if (error.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                               IMessageSqlManager.deleteLocalFileAfterFire(mEntry.getMsgLocalId());
                           }

                       }
                   });

               }
           });
		   
		
		   
		    
		}
        ImageLoader.getInstance().displayImage(mImageUrl, mImageView, imageOptionsBuilder.build(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
                mImageView.setImageBitmap(mThumbnailBitmap);
            }


            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mImageView.setImageBitmap(mThumbnailBitmap);
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                    case UNKNOWN:
                    case DECODING_ERROR:
                        break ;
                    case NETWORK_DENIED:
                        message = "网络有问题，无法下载";
                        break;
                    case OUT_OF_MEMORY:
                        message = "图片太大无法显示";
                        break;
                }
                if (message != null) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                mEntry.setIsDownload(false);
                mSuccessLayout.setVisibility(View.GONE);
                mFailLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mSuccessLayout.setVisibility(View.VISIBLE);
                mFailLayout.setVisibility(View.GONE);
                mEntry.setIsDownload(true);
                progressBar.setVisibility(View.GONE);
                imgCacheFile = DiskCacheUtils.findInCache(imageUri, ImageLoader.getInstance().getDiskCache());
                if (imgCacheFile != null) {
                    if (isGif || loadedImage.getHeight() > DemoUtils.getScreenHeight(getActivity())) {
                        mCacheImageUrl = "file://" + imgCacheFile.getAbsolutePath();
                        mImageView.setVisibility(View.GONE);
                        showImgInWebView(mCacheImageUrl);
                    } else {
                        mImageView.setImageBitmap(loadedImage);
                    }

                    if (imageUri.startsWith("http:")) {
                        ImgInfo thumbimginfo = ImgInfoSqlManager.getInstance().getImgInfo(mEntry.getIndex());
                        if(thumbimginfo != null && mCacheImageUrl != null) {
                            thumbimginfo.setBigImgPath(mCacheImageUrl.substring(mCacheImageUrl.lastIndexOf("/")));
                            ImgInfoSqlManager.getInstance().updateImageInfo(thumbimginfo);
                        }
                    }
                }
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String s, View view, int current, int total) {
                mProgress.setText((int)(current * 100f / total) + " %");
            }
        });
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.web_gif);
        webView.setBackgroundColor(0);
    }

    private void handleDialogItemClick(int position) {
        switch (position) {
            case 0:
                try {
                    if(mEntry != null && mEntry.isGif()) {
                        DemoUtils.saveImage(imgCacheFile.getAbsolutePath(),".gif");
                        return;
                    }
                    DemoUtils.saveImage(imgCacheFile.getAbsolutePath());
                } catch (Exception e) {
                    LogUtil.e(TAG, "onContextItemSelected error ");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mImageView != null) {
            mImageView.setImageDrawable(null);
            ImageLoader.getInstance().cancelDisplayTask(mImageView);
        }
        mImageView = null;
        if(mViewContainer != null) {
            mViewContainer.removeView(webView);
        }
        if(webView != null) {
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    /**
     * 使用WebView加载GIF图片和大图
     *
     * @param s
     */
    private void showImgInWebView(final String s) {
        if (webView != null) {
            webView.loadDataWithBaseURL("", "<!doctype html> <html lang=\"en\"> <head> <meta charset=\"UTF-8\"> <title></title><style type=\"text/css\"> html,body{width:100%;height:100%;margin:0;padding:0;background-color:black;} *{ -webkit-tap-highlight-color: rgba(0, 0, 0, 0);}#box{ width:100%;height:100%; display:table; text-align:center; background-color:black;} body{-webkit-user-select: none;user-select: none;-khtml-user-select: none;}#box span{ display:table-cell; vertical-align:middle;} #box img{  width:100%;} </style> </head> <body> <div id=\"box\"><span><img src=\"img_url\" alt=\"\"></span></div> <script type=\"text/javascript\" >document.body.onclick=function(e){window.external.onClick();e.preventDefault(); };function load_img(){var url=document.getElementsByTagName(\"img\")[0];url=url.getAttribute(\"src\");var img=new Image();img.src=url;if(img.complete){\twindow.external.img_has_loaded();\treturn;};img.onload=function(){window.external.img_has_loaded();};img.onerror=function(){\twindow.external.img_loaded_error();};};load_img();</script></body> </html>".replace("img_url", s), "text/html", "utf-8", "");
        }
    }
}
