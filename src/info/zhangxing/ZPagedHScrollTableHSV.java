package info.zhangxing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * 自定义横向滚动视图，可自动控制左右滚动指示控件显示与否
 *
 * @author open@zhangxing.info
 *
 */
public class ZPagedHScrollTableHSV extends HorizontalScrollView {

	View leftIndicator;
	View rightIndicator;

	public ZPagedHScrollTableHSV(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setLeftIndicator(View leftIndicator) {
		this.leftIndicator = leftIndicator;
	}

	public void setRightIndicator(View rightIndicator) {
		this.rightIndicator = rightIndicator;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (leftIndicator != null) {
			leftIndicator.setVisibility(INVISIBLE);
		}

		if (rightIndicator != null) {
			if (getChildAt(0).getMeasuredWidth() <= getWidth()) {
				rightIndicator.setVisibility(INVISIBLE);
			} else {
				rightIndicator.setVisibility(VISIBLE);
			}
		}
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onScrollChanged(final int l, final int t, final int oldl,
			final int oldt) {
		if (leftIndicator != null) {
			if (l == 0) {
				leftIndicator.setVisibility(INVISIBLE);
			} else {
				leftIndicator.setVisibility(VISIBLE);
			}
		}

		/*
		 * 对于横向 ScrollView，判断滚动到最右侧的标准为 LinearLayout.getMeasuredWidth() <= l +
		 * getWidth()
		 *
		 * 对于纵向 ScrollView，判断滚动到最底部的标准为 LinearLayout.getMeasuredHeight() <= t +
		 * getHeight()
		 */
		if (rightIndicator != null) {
			if (getChildAt(0).getMeasuredWidth() <= l + getWidth()) {
				rightIndicator.setVisibility(INVISIBLE);
			} else {
				rightIndicator.setVisibility(VISIBLE);
			}
		}

		super.onScrollChanged(l, t, oldl, oldt);
	}

}
