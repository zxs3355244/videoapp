package org.yczbj.ycvideoplayer.ui.video.model.binder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.jakewharton.rxbinding2.view.RxView;

import org.yczbj.ycvideoplayer.R;
import org.yczbj.ycvideoplayer.api.http.video.VideoModel;
import org.yczbj.ycvideoplayer.ui.test.test2.model.Video;
import org.yczbj.ycvideoplayer.ui.video.model.bean.MultiNewsArticleDataBean;
import org.yczbj.ycvideoplayer.ui.video.model.bean.VideoContentBean;
import org.yczbj.ycvideoplayer.ui.video.view.activity.VideoContentActivity;
import org.yczbj.ycvideoplayer.util.ImageUtil;
import org.yczbj.ycvideoplayer.util.SettingUtil;
import org.yczbj.ycvideoplayerlib.constant.ConstantKeys;
import org.yczbj.ycvideoplayerlib.controller.VideoPlayerController;
import org.yczbj.ycvideoplayerlib.inter.listener.OnPlayOrPauseListener;
import org.yczbj.ycvideoplayerlib.inter.listener.OnPlayerTypeListener;
import org.yczbj.ycvideoplayerlib.inter.listener.OnVideoBackListener;
import org.yczbj.ycvideoplayerlib.inter.listener.OnVideoControlListener;
import org.yczbj.ycvideoplayerlib.manager.VideoPlayerManager;
import org.yczbj.ycvideoplayerlib.player.VideoPlayer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.drakeet.multitype.ItemViewBinder;

public class NewsArticleVideoViewBinder extends ItemViewBinder<MultiNewsArticleDataBean, NewsArticleVideoViewBinder.ViewHolder> {

    private static final String TAG = "NewsArticleHasVideoView";
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_news_article_video,parent,false);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MultiNewsArticleDataBean item) {
        final Context context = holder.itemView.getContext();
        try {
//            if(null != item.getVideo_detail_info()) {
//                if(null != item.getVideo_detail_info().getDetail_video_large_image()) {
//                    String image = item.getVideo_detail_info().getDetail_video_large_image().
//                            getUrl();
//                    if(!TextUtils.isEmpty(image)) {
//                        ImageUtil.loadImgByPicasso(context,image,R.drawable.image_default,holder.ivVideoImage);
//                    }
//                }
//            } else {
//                holder.ivVideoImage.setImageResource(R.drawable.image_default);
//            }
            if(null != item.getUser_info()) {
                String avatar_url = item.getUser_info().getAvatar_url();
                if(!TextUtils.isEmpty(avatar_url)) {
                    ImageUtil.loadImgByPicasso(context,avatar_url,R.drawable.image_default,holder.ivMedia);
                }
            }
            String tv_title = item.getTitle();
            holder.tvTitle.setTextSize(SettingUtil.getInstance().getTextSize());
            String tv_source = item.getSource();
            String tv_comment_count = item.getComment_count() + "评论";
            String tv_datetime =item.getBehot_time() + "";
            if(!TextUtils.isEmpty(tv_datetime)) {
                tv_datetime = TimeUtils.getFriendlyTimeSpanByNow(tv_datetime);
            }
            int video_duration = item.getVideo_duration();
            String min = String.valueOf(video_duration / 60);
            String second = String.valueOf(video_duration %10);
            if(Integer.parseInt(second) < 10) {
                second = "0" + second;
            }
            String tv_video_time = min + ":" + second;
            holder.tvTitle.setText(tv_title);
            holder.tvExtra.setText(tv_source + " - " + tv_comment_count +
                    " - " + tv_datetime);
            holder.bindData(item);
//            holder.mController.imageView().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if( holder.videoUrls == null) {
//                        String videoId = item.getVideo_id();
//                        String url = getVideoContentApi(videoId);
//                        VideoModel model = VideoModel.getInstance();
//                        getVideoData(model,url,holder);
//                    }
//                }
//            });

            String videoId = item.getVideo_id();
            String url = getVideoContentApi(videoId);
            VideoModel model = VideoModel.getInstance();
            getVideoData(model,url,holder);

//            RxView.clicks( holder.mController.imageView())
//                    .throttleFirst(1,TimeUnit.SECONDS)
//                    .subscribe(new Consumer<Object>() {
//                        @Override
//                        public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
//                            new Handler().post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if( holder.videoUrls == null) {
//                                        Log.e("MMM",">>>>>>");
//                                        String videoId = item.getVideo_id();
//                                        String url = getVideoContentApi(videoId);
//                                        VideoModel model = VideoModel.getInstance();
//                                        getVideoData(model,url,holder);
//                                    }
//                                }
//                            });
//                        }
//                    });
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }
    }

    private static String getRandom() {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }
    private static String getVideoContentApi(String videoid) {
        String VIDEO_HOST = "http://ib.365yg.com";
        String VIDEO_URL = "/video/urls/v/1/toutiao/mp4/%s?r=%s";
        String r = getRandom();
        String s = String.format(VIDEO_URL,videoid,r);
        // 将/video/urls/v/1/toutiao/mp4/{videoid}?r={Math.random()} 进行crc32加密
        CRC32 crc32 = new CRC32();
        crc32.update(s.getBytes());
        String crcString = crc32.getValue() + "";
        String url = VIDEO_HOST + s + "&s=" +crcString;
        return url;
    }



    @SuppressLint("CheckResult")
    private void getVideoData(VideoModel model, String url,ViewHolder holder) {
        model.getVideoContent(url)
                .subscribeOn(Schedulers.io())
                .map(new Function<VideoContentBean, String>() {

                    @Override
                    public String apply(VideoContentBean videoContentBean) throws Exception {
                        VideoContentBean.DataBean.VideoListBean videoList = videoContentBean.
                                getData().getVideo_list();
                        if(videoList.getVideo_3() != null) {
                            String base64 = videoList.getVideo_3().
                                    getMain_url();
                            String url = (new String(Base64.decode(base64.getBytes(),Base64.DEFAULT)));
                            Log.d(TAG,"getVideoUrls: " + url);
                            return url;
                        }
                        if(videoList.getVideo_2() != null) {
                            String base64 =videoList.getVideo_2().
                                    getMain_url();
                            String url = (new String(Base64.decode(base64.getBytes(),Base64.DEFAULT
                            )));
                            Log.d(TAG, "getVideoUrls: " + url);
                            return url;
                        }

                        if (videoList.getVideo_1() != null) {
                            String base64 = videoList.getVideo_1().getMain_url();
                            String url = (new String(Base64.decode(base64.getBytes(),Base64.DEFAULT)));
                            Log.d(TAG,"getVideoUrls: " + url);
                            return url;
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG,
                                "urls" + s);
                        Log.e(TAG,"holder" + holder);
                        holder.setVideoPlayer(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    protected void onStop() {
        VideoPlayerManager.instance().suspendVideoPlayer();
        LogUtils.e("VideoContentActivity----"+"onStop");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_media)
        ImageView ivMedia;
        @BindView(R.id.tv_extra)
        TextView tvExtra;
        @BindView(R.id.iv_dots)
        ImageView ivDots;
        @BindView(R.id.header)
        LinearLayout header;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.content)
        LinearLayout content;

        @BindView(R.id.video_player)
        VideoPlayer mVideoPlayer;
        VideoPlayerController mController;
        String videoUrls;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mVideoPlayer.setPlayerType(ConstantKeys.IjkPlayerType.TYPE_IJK);

            //创建视频控制器
            mController = new VideoPlayerController(itemView.getContext());
            mController.setLoadingType(ConstantKeys.Loading.LOADING_RING);
            mController.imageView().setBackgroundResource(R.color.blackText);
            mController.setOnVideoBackListener(new OnVideoBackListener() {
                @Override
                public void onBackClick() {

                }
            });

            mController.setOnPlayerTypeListener(new OnPlayerTypeListener() {
                /*
                切换到全屏播放监听
                 */
                @Override
                public void onFullScreen() {
                    LogUtils.e("setOnPlayerTypeListener"+"onFullScreen");
                }
                /*
                切换到小窗播放监听
                 */
                @Override
                public void onTinyWindow() {
                    LogUtils.e("setOnPlayerTypeListener"+"onTinyWindow");
                }
                /*
                切换到正常播放监听
                 */
                @Override
                public void onNormal() {
                    LogUtils.e("setOnPlayerTypeListener"+"onNormal");
                }
            });
            //设置视频监控器
            mVideoPlayer.setController(mController);
        }

        public void bindData(@NonNull MultiNewsArticleDataBean item) {
            Log.e(TAG,"mController" +mController);
            mController.setTitle(item.getTitle());
            Log.e(TAG, "bindData: "+ item.getVideo_detail_info().getDetail_video_large_image().getUrl());
            ImageUtil.loadImgByPicasso(itemView.getContext(),item.getVideo_detail_info().getDetail_video_large_image().getUrl(),
                    R.drawable.image_default,mController.imageView());
        }

        public void setVideoPlayer(String urls) {
            if(mVideoPlayer == null || urls == null) {
                return;
            }
            videoUrls = urls;
            Log.e(TAG, "setVideoPlayer: "+ urls );
            mVideoPlayer.setUp(urls,null);
        }
    }
}
