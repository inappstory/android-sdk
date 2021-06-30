package com.inappstory.sdk.stories.ui.widgets.readerscreen.generated;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.StoryLink;
import com.inappstory.sdk.stories.api.models.StoryLinkObject;
import com.inappstory.sdk.stories.api.models.slidestructure.Element;
import com.inappstory.sdk.stories.api.models.slidestructure.Source;

import java.io.File;
import java.io.IOException;

public class ElementGenerator {
    public static GeneratedView generate(Element element, Context context, int ySize, int xSize) {
        switch (element.type) {
            case TYPE_IMAGE:
                return new GeneratedView(element.type, generateImage(element, context, ySize, xSize));
            case TYPE_TEXT:
                return new GeneratedView(element.type, generateText(element, context, ySize, xSize));
            case TYPE_LINK:
            case TYPE_TEXT_LINK:
                return new GeneratedView(element.type, generateLink(element, context, ySize, xSize));
            case TYPE_VIDEO:
                return new GeneratedView(element.type, generateVideo(element, context, ySize, xSize));
            default:
                return null;
        }
    }

    public static void loadContent(Element element, GeneratedView view, SimpleViewCallback callback, String storyId) {
        switch (element.type) {
            case TYPE_IMAGE:
                loadImage(element, view, storyId);
                return;
            case TYPE_TEXT:
                loadText(element, view);
                return;
            case TYPE_LINK:
            case TYPE_TEXT_LINK:
                loadLink(element, view, callback);
                return;
            case TYPE_VIDEO:
                loadVideo(element, view, storyId);
                return;
            default:
                return;
        }
    }

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_LINK = "link";
    public static final String TYPE_TEXT_LINK = "text-link";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";


    public static final String TEXT_ALIGN_LEFT = "left";
    public static final String TEXT_ALIGN_RIGHT = "right";
    public static final String TEXT_ALIGN_CENTER = "center";
    public static final String TEXT_ALIGN_JUSTIFY = "justify";

    static final String TEST_PRE_PATH = "https://cs.test.inappstory.com/np/file/";
    static final String PROD_PRE_PATH = "https://cs2.kiozk.ru/file/";

    public static String prePath = TEST_PRE_PATH;

    static void loadImage(Element element, GeneratedView generatedView, String storyId) {
        String imgPath = element.path;
        if (element.sources != null && element.sources.size() > 0) {
            for (Source source : element.sources) {
                if (source.type.contains("webp")) {
                    imgPath = source.path;
                }
            }
        }
        File fl = null;
        try {
            fl = InAppStoryService.getInstance().getCommonCache().get(prePath + imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fl == null) {
            ImageLoader.getInstance().displayImage(prePath + imgPath,
                    -1, (GeneratedImageView) generatedView.view,
                    InAppStoryService.getInstance().getCommonCache());
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
          //  options.inPreferredConfig = Bitmap.Config.;
            Bitmap bitmap = BitmapFactory.decodeFile(fl.getAbsolutePath(), options);
            ((GeneratedImageView) generatedView.view).setImageBitmap(bitmap);
            ((GeneratedImageView) generatedView.view).onLoaded();
        }

    }


    static void loadText(Element element, GeneratedView generatedView) {

    }


    static void loadLink(final Element element, GeneratedView generatedView, final SimpleViewCallback callback) {
        generatedView.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoryLink link = new StoryLink(element.linkType, element.linkTarget);
                StoryLinkObject object = new StoryLinkObject("link", link);
                try {
                    callback.doAction(JsonParser.getJson(object));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    static void loadVideo(Element element, GeneratedView generatedView, String storyId) {
        if (element.thumbnail != null && element.thumbnail.path != null) {
            ((GeneratedVideoView) generatedView.view).loadCover(
                    prePath + element.thumbnail.path
            );
        }
        if (element.sources != null && element.sources.size() > 0) {
            ((GeneratedVideoView) generatedView.view).loadVideo(prePath + element.sources.get(0).path, storyId);
        }
    }

    static GeneratedImageView generateImage(Element element, Context context, int ySize, int xSize) {
        GeneratedImageView imageView = new GeneratedImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams lp;
        if (element.geometry.expand) {
            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp = new RelativeLayout.LayoutParams(
                    (int) (xSize * (element.geometry.width / 100)),
                    (int) (ySize * (element.geometry.height / 100)));
            int left = (int) ((element.geometry.x / 100) * xSize);
            int top = (int) ((element.geometry.y / 100) * ySize);
            lp.setMargins(left, top, 0, 0);
        }
        imageView.setLayoutParams(lp);
        if (element.opacity != null)
            imageView.setAlpha(element.opacity);
        //   imageView.setTranslationX(element.geometry.x * Sizes.getScreenSize().x);
        //   imageView.setTranslationY(element.geometry.x * Sizes.getScreenSize().x);
        return imageView;
    }

    static AppCompatTextView generateText(Element element, Context context, int ySize, int xSize) {
        GeneratedTextView textView = new GeneratedTextView(context);
        textView.setTextColor(ColorParser.getColor(element.color, false));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, element.textSize * GeneratedViewSizes.getEMInPx());
        if (element.content != null)
            textView.setText(element.content);
        RelativeLayout.LayoutParams lp;
        if (element.geometry.expand) {
            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp = new RelativeLayout.LayoutParams(
                    (int) (xSize * (element.geometry.width / 100)),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = (int) ((element.geometry.x / 100) * xSize);
            int top = (int) ((element.geometry.y / 100) * ySize);
            lp.setMargins(left, top, 0, 0);
        }
        int padding = (int) ((element.padding * xSize) / 20);
        textView.setPadding(padding, padding, padding, padding);
        // textView.setTextAlignment();
        if (element.border != null || element.background != null) {
            GradientDrawable shape = new GradientDrawable();
            if (element.border != null) {
                shape.setCornerRadius((element.border.radius * xSize) / 20);
                if (element.border.width > 0) {
                    shape.setStroke((int) (xSize * element.border.width / 20),
                            ColorParser.getColor(element.border.color, false));
                }
            }
            if (element.background != null) {
                shape.setColor(ColorParser.getColor(element.background.color, false));
            }
            textView.setBackground(shape);
        }
        textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        switch (element.align) {
            case TEXT_ALIGN_LEFT:
            case TEXT_ALIGN_JUSTIFY:
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;
            case TEXT_ALIGN_RIGHT:
                textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                break;
            case TEXT_ALIGN_CENTER:
                textView.setGravity(Gravity.CENTER);
                break;
        }
        textView.setLayoutParams(lp);
        Typeface t = AppearanceManager.getCommonInstance().getFont(element.secondaryFont, element.bold, element.italic);
        int bold = element.bold ? 1 : 0;
        int italic = element.italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), bold + italic);
        return textView;
    }


    static AppCompatTextView generateLink(Element element, Context context, int ySize, int xSize) {
        GeneratedTextView textView = new GeneratedTextView(context);
        textView.setTextColor(ColorParser.getColor(element.color, false));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, element.textSize * GeneratedViewSizes.getEMInPx());
        if (element.content != null)
            textView.setText(element.content);
        RelativeLayout.LayoutParams lp;
        if (element.geometry.expand) {
            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp = new RelativeLayout.LayoutParams(
                    (int) (xSize * (element.geometry.width / 100)),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = (int) ((element.geometry.x / 100) * xSize);
            int top = (int) ((element.geometry.y / 100) * ySize);
            lp.setMargins(left, top, 0, 0);
        }
        int padding = (int) ((element.padding * xSize) / 20);
        textView.setPadding(padding, padding, padding, padding);
        if (element.border != null || element.background != null) {
            GradientDrawable shape = new GradientDrawable();
            if (element.border != null) {
                shape.setCornerRadius((element.border.radius * xSize) / 20);
                if (element.border.width > 0) {
                    shape.setStroke((int) (xSize * element.border.width / 20),
                            ColorParser.getColor(element.border.color, false));
                }
            }
            if (element.background != null) {
                shape.setColor(ColorParser.getColor(element.background.color, false));
            }
            textView.setBackground(shape);
        }
        textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        switch (element.align) {
            case TEXT_ALIGN_LEFT:
            case TEXT_ALIGN_JUSTIFY:
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;
            case TEXT_ALIGN_RIGHT:
                textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                break;
            case TEXT_ALIGN_CENTER:
                textView.setGravity(Gravity.CENTER);
                break;
        }
        textView.setLayoutParams(lp);
        Typeface t = AppearanceManager.getCommonInstance().getFont(element.secondaryFont, element.bold, element.italic);
        int bold = element.bold ? 1 : 0;
        int italic = element.italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), bold + italic);
        return textView;
    }

    static GeneratedVideoView generateVideo(Element element, Context context, int ySize, int xSize) {
        GeneratedVideoView videoView = new GeneratedVideoView(context);
        RelativeLayout.LayoutParams lp;
        if (element.geometry.expand) {
            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp = new RelativeLayout.LayoutParams(
                    (int) (xSize * (element.geometry.width / 100)),
                    (int) (ySize * (element.geometry.height / 100)));
            int left = (int) ((element.geometry.x / 100) * xSize);
            int top = (int) ((element.geometry.y / 100) * ySize);
            lp.setMargins(left, top, 0, 0);
        }
        videoView.setLayoutParams(lp);

        return videoView;
    }
}
