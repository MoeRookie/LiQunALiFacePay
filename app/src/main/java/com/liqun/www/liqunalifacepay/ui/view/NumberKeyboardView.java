package com.liqun.www.liqunalifacepay.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.liqun.www.liqunalifacepay.R;

import java.util.Arrays;
import java.util.List;

/**
 * 数字键盘。
 *
 * @author wuzhen
 */
public class NumberKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

    // 用于区分左下角空白的按键
    private static final int KEYCODE_EMPTY = -10;

    private int mDeleteWidth;
    private int mDeleteHeight;
    private Drawable mDeleteDrawable;
    private Rect mDeleteDrawRect;

    private IOnKeyboardListener mOnKeyboardListener;

    public NumberKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public NumberKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PayKeyboardView,
                defStyleAttr, 0);
        mDeleteDrawable = a.getDrawable(R.styleable.PayKeyboardView_xnkv_deleteDrawable);
        mDeleteWidth = a.getDimensionPixelOffset(R.styleable.PayKeyboardView_xnkv_deleteWidth,
                -1);
        mDeleteHeight = a.getDimensionPixelOffset(R.styleable.PayKeyboardView_xnkv_deleteHeight,
                -1);
        a.recycle();

        // 设置软键盘按键的布局
        Keyboard keyboard = new Keyboard(context, R.xml.keyboard_number);
        setKeyboard(keyboard);

        setEnabled(true);
        setPreviewEnabled(false); // 设置按键没有点击放大镜显示的效果
        setOnKeyboardActionListener(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 遍历所有的按键
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            // 如果是右下角的删除按键则需要绘制删除图标
            if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                drawDeleteButton(key, canvas);
            }
        }
    }
    // 绘制删除按键
    private void drawDeleteButton(Keyboard.Key key, Canvas canvas) {
        if (mDeleteDrawable == null) {
            return;
        }

        // 计算删除图标绘制的坐标
        if (mDeleteDrawRect == null || mDeleteDrawRect.isEmpty()) {
            int drawWidth, drawHeight;
            int intrinsicWidth = mDeleteDrawable.getIntrinsicWidth();
            int intrinsicHeight = mDeleteDrawable.getIntrinsicHeight();

            if (mDeleteWidth > 0 && mDeleteHeight > 0) {
                drawWidth = mDeleteWidth;
                drawHeight = mDeleteHeight;
            } else if (mDeleteWidth > 0 && mDeleteHeight <= 0) {
                drawWidth = mDeleteWidth;
                drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
            } else if (mDeleteWidth <= 0 && mDeleteHeight > 0) {
                drawHeight = mDeleteHeight;
                drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
            } else {
                drawWidth = intrinsicWidth;
                drawHeight = intrinsicHeight;
            }

            // 限制图标的大小，防止图标超出按键
            if (drawWidth > key.width) {
                drawWidth = key.width;
                drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
            }
            if (drawHeight > key.height) {
                drawHeight = key.height;
                drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
            }

            // 获取删除图标绘制的坐标
            int left = key.x + (key.width - drawWidth) / 2;
            int top = key.y + (key.height - drawHeight) / 2;
            mDeleteDrawRect = new Rect(left, top, left + drawWidth, top + drawHeight);
        }

        // 绘制删除的图标
        if (mDeleteDrawRect != null && !mDeleteDrawRect.isEmpty()) {
            mDeleteDrawable.setBounds(mDeleteDrawRect.left, mDeleteDrawRect.top,
                    mDeleteDrawRect.right, mDeleteDrawRect.bottom);
            mDeleteDrawable.draw(canvas);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        // 处理按键的点击事件
        // 点击了删除按键
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            if (mOnKeyboardListener != null)
                mOnKeyboardListener.onDeleteKeyEvent();
        }
        // 点击了数字按键
        else if (primaryCode != KEYCODE_EMPTY) {
            if (mOnKeyboardListener != null) {
                mOnKeyboardListener.onInsertKeyEvent(Character.toString(
                        (char) primaryCode));
            }
        }
        // 点击了清零按键
//         else if (primaryCode == KEYCODE_EMPTY) {
//            if (mOnKeyboardListener != null) {
//                mOnKeyboardListener.onClearKeyEvent();
//            }
//        }
    }

    // 0-9 数字的 Character 值
    private final List<Character> keyCodes = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9');

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    /**
     * 设置键盘的监听事件。
     *
     * @param listener 监听事件
     */
    public void setIOnKeyboardListener(IOnKeyboardListener listener) {
        this.mOnKeyboardListener = listener;
    }

    /**
     * 键盘的监听事件。
     */
    public interface IOnKeyboardListener {

        /**
         * 点击数字按键。
         *
         * @param text 输入的数字
         */
        void onInsertKeyEvent(String text);

        /**
         * 点击了删除按键。
         */
        void onDeleteKeyEvent();
        
        /**
         *点击了清零按键
         */
//        void onClearKeyEvent();
    }
}
