
package tree.love.animtabsview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnimTabsView extends RelativeLayout {

//    private static final String TAG = "AnimTabsView";

    private Context mContext;
    private int mTotalItemsCount = 0;
    private int mCurrentItemPosition = 0;

    private LinearLayout mItemsContainerLayout;
    private Bitmap mSlideIcon;
    private Bitmap mShadow;
    private Rect mLeftDrawRect;
    private Rect mRightDrawRect;
    private int mCurrentSlideX;
    private int mCurrentSlideY;

    private final PageListener mPageListener = new PageListener();
    private ViewPager.OnPageChangeListener mDelegatePageListener;
    private ViewPager mViewPager;

    private int mScrollState;
    private boolean isFirstLayout;


    public AnimTabsView(Context context) {
        this(context, null);
    }

    public AnimTabsView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        setWillNotDraw(false);

        this.mContext = context;
        this.mLeftDrawRect = new Rect();
        this.mRightDrawRect = new Rect();
        this.mSlideIcon = BitmapFactory.decodeResource(getResources(), R.drawable.blk_menubtn_arr);
        this.mShadow = BitmapFactory.decodeResource(getResources(), R.drawable.blk_menubtn_shadow);
        LinearLayout slideLayout = new LinearLayout(this.mContext);
        slideLayout.setOrientation(LinearLayout.VERTICAL);
        View localView = new View(this.mContext);
        localView.setBackgroundResource(R.drawable.blk_menubtn_bg);
        slideLayout.addView(localView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0F));
        slideLayout.addView(new View(this.mContext), new LinearLayout.LayoutParams(-1, this.mSlideIcon.getHeight()));
        this.mItemsContainerLayout = new LinearLayout(this.mContext);
        this.mItemsContainerLayout.setBackgroundColor(0);
        RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        float f1 = context.getResources().getDisplayMetrics().density;
        localLayoutParams.setMargins(0, 0, 0, (int) (f1 * 4.0F));
        addView(slideLayout);
        addView(this.mItemsContainerLayout, localLayoutParams);
    }

    public int getCount() {
        return this.mTotalItemsCount;
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        if ((this.mTotalItemsCount > 0) && (getItemView(tabIndex) != null)) {
            View currentSeletedItemView = getItemView(tabIndex);
            this.mCurrentSlideX = (currentSeletedItemView.getLeft()
                    + currentSeletedItemView.getWidth() / 2 - this.mSlideIcon.getWidth() / 2)
                    + positionOffset;
            invalidate();
//            Log.d(TAG, "scrollToTab >> mCurrentSlideX" + mCurrentSlideX);
        }
    }

    public void addItem(final int position, String itemText) {
        RelativeLayout itemLayout = (RelativeLayout) View.inflate(this.mContext, R.layout.anim_tab_item, null);
        ((TextView) itemLayout.getChildAt(0)).setText(itemText);
        itemLayout.setTag(Integer.valueOf(position));
        itemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(position);
            }
        });
        this.mItemsContainerLayout.addView(itemLayout, new LinearLayout.LayoutParams(0, -1, 1.0F));
    }

    private void setCurrentItemSelected(int position) {
        for (int i = 0; i < mTotalItemsCount; i++) {
            if (i == position) {
                getItemView(i).setSelected(true);
            } else {
                getItemView(i).setSelected(false);
            }
        }
    }

    public int getCurrentItemPosition() {
        return this.mCurrentItemPosition;
    }

    public View getItemView(int itemPosition) {
        if ((itemPosition >= 0) && (itemPosition < this.mTotalItemsCount)) {
            return this.mItemsContainerLayout.getChildAt(itemPosition);
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d(TAG, "onDraw >> mCurrentSlideX" + mCurrentSlideX);
        this.mLeftDrawRect.set(0, this.mCurrentSlideY, this.mCurrentSlideX, this.mCurrentSlideY + this.mShadow.getHeight());
        canvas.drawBitmap(this.mShadow, null, this.mLeftDrawRect, null);
        this.mRightDrawRect.set(this.mCurrentSlideX + this.mSlideIcon.getWidth(), this.mCurrentSlideY, getWidth(), this.mCurrentSlideY + this.mShadow.getHeight());
        canvas.drawBitmap(this.mShadow, null, this.mRightDrawRect, null);
        canvas.drawBitmap(this.mSlideIcon, this.mCurrentSlideX, this.mCurrentSlideY, null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        Log.d(TAG, "onLayout");
        if (!isFirstLayout || mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            if ((this.mTotalItemsCount > 0) && (getItemView(this.mCurrentItemPosition) != null)) {
                isFirstLayout = true;
//                Log.d(TAG, "onLayout this.mTotalItemsCount > 0");
                View currentItemView = getItemView(this.mCurrentItemPosition);
                this.mCurrentSlideX = currentItemView.getLeft() + (currentItemView.getWidth() / 2) - (this.mSlideIcon.getWidth() / 2);
                this.mCurrentSlideY = (b - t - this.mSlideIcon.getHeight());
//                Log.d(TAG, "onLayout mCurrentSlideX : " + mCurrentSlideX);
            }
        }
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;

        if (mViewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        mViewPager.setOnPageChangeListener(mPageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener changeListener) {
        this.mDelegatePageListener = changeListener;
    }

    private void notifyDataSetChanged() {
        mItemsContainerLayout.removeAllViews();
        mTotalItemsCount = mViewPager.getAdapter().getCount();

        for (int i = 0; i < mTotalItemsCount; i++) {
            addItem(i, mViewPager.getAdapter().getPageTitle(i).toString());
        }

        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                        mCurrentItemPosition = mViewPager.getCurrentItem();
                        scrollToTab(mCurrentItemPosition, 0);
                        getItemView(mCurrentItemPosition).setSelected(true);
                    }
                });
    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            Log.d(TAG, "onPageScrolled >> " + "position:" + position +
//            ",positionOffset:"
//            + positionOffset + ",positionOffsetPixels:" +
//            positionOffsetPixels);

            int itemCount = mItemsContainerLayout.getChildCount();
            if ((itemCount == 0) || (position < 0) || (position >= itemCount)) {
                return;
            }
            mCurrentItemPosition = position;

            View selectedItem = mItemsContainerLayout.getChildAt(position);
            int extraOffset = (selectedItem != null)
                    ? (int) (positionOffset * selectedItem.getWidth()) : 0;
            scrollToTab(position, extraOffset);

//            Log.d(TAG, "extraOffset:" + extraOffset);

            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
//            Log.d(TAG, "onPageSelected >> " + "position:" + position);
            setCurrentItemSelected(position);
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
//                Log.d(TAG, "onPageSelected SCROLL_STATE_IDLE");
                scrollToTab(position, 0);
            }
            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
//            Log.d(TAG, "onPageScrollStateChanged >> " + "state:" + state);
            mScrollState = state;
            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageScrollStateChanged(state);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentItemPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = mCurrentItemPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
