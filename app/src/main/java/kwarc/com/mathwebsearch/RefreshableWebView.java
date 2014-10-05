package kwarc.com.mathwebsearch;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * @author Radu Hambasan
 *         05 Oct 2014
 */
public class RefreshableWebView extends WebView {
    private OnOverScrollListener onOverScrollListener;
    boolean isOverScrolling = false;

    public RefreshableWebView(final Context context) {
        super(context);
    }

    public RefreshableWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshableWebView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (y < oldY) isOverScrolling = false;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (scrollY > 0 && clampedY && !isOverScrolling) {
            isOverScrolling = true;
            if (onOverScrollListener != null) onOverScrollListener.onOverScroll();
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public void setOnOverScrollListener(final OnOverScrollListener onOverScrollListener) {
        this.onOverScrollListener = onOverScrollListener;
    }

    public static interface OnOverScrollListener {
        public void onOverScroll();
    }
}
