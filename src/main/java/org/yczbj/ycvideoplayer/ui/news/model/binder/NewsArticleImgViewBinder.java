package org.yczbj.ycvideoplayer.ui.news.model.binder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.jakewharton.rxbinding2.view.RxView;

import org.w3c.dom.Text;
import org.yczbj.ycvideoplayer.R;
import org.yczbj.ycvideoplayer.ui.main.contract.MainContract;
import org.yczbj.ycvideoplayer.ui.video.model.bean.MultiNewsArticleDataBean;
import org.yczbj.ycvideoplayer.util.ImageUtil;
import org.yczbj.ycvideoplayer.util.SettingUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 带图片的 item
 */

public class NewsArticleImgViewBinder extends ItemViewBinder<MultiNewsArticleDataBean, NewsArticleImgViewBinder.ViewHolder> {
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_news_article_img,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MultiNewsArticleDataBean item) {
        final Context context = holder.itemView.getContext();
        try {
            String imgUrl = "http://p3.pstatp.com/";
            List<MultiNewsArticleDataBean.ImageListBean> img_list = item.getImage_list();
            if(img_list != null && img_list.size() != 0) {
                String url = img_list.get(0).getUrl();
                ImageUtil.loadImgByPicasso(context,url,R.drawable.image_default,holder.ivImage);
                if(!TextUtils.isEmpty(img_list.get(0).getUri())) {
                    imgUrl += img_list.get(0).getUrl().replace("list",
                            "large");
                }
            }

            if(null != item.getUser_info()) {
                String avatar_url = item.getUser_info().getAvatar_url();
                if(!TextUtils.isEmpty(avatar_url)) {
                    ImageUtil.loadImgByPicasso(context,avatar_url,
                            R.drawable.image_default,holder.ivMedia);
                }
            }

            String tv_title = item.getTitle();
            String tv_abstract = item.getAbstractX();
            String tv_source = item.getSource();
            String tv_comment_count = item.getComment_count() + "评论";
            String tv_datetime = item.getBehot_time() + "";
            if(!TextUtils.isEmpty(tv_datetime)) {
                tv_datetime = TimeUtils.getFriendlyTimeSpanByNow(tv_datetime);
            }
            holder.tvTitle.setText(tv_title);
            holder.tvTitle.setTextSize(SettingUtil.getInstance().getTextSize());
            holder.tvAbstract.setText(tv_abstract);
            holder.tvExtra.setText(tv_source + " - " + tv_comment_count + " - "+
                    tv_datetime);
            holder.ivDots.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {

                                                 }
                                             });

            final String finalImgUrl = imgUrl;
            RxView.clicks(holder.itemView)
                    .throttleFirst(1,TimeUnit.SECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            //NewsContentActivity.launch(item, finalImgUrl);
                        }
                    });
        } catch (Exception e) {

        }
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
        @BindView(R.id.tv_abstract)
        TextView tvAbstract;
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.ll_content)
        LinearLayout llContent;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
