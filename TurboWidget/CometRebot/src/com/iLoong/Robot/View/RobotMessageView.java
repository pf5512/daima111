package com.iLoong.Robot.View;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooeeui.cometrobot.R;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.Texture;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class RobotMessageView extends View3D {
	private MainAppContext mAppContext;
	private RobotMessageLineView mRobotMessageLineView;
	private Timeline mShowMessageTimeline;

	public RobotMessageView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(name);
		this.mAppContext = appContext;
		x = 0;
		y = 0;
		this.region.setRegion(region);
		this.width = region.getRegionWidth();
		this.height = region.getRegionHeight();
		this.setOrigin(width / 2, height / 2);
	}

	public void setRobotMessageLineView(
			RobotMessageLineView robotMessageLineView) {
		this.mRobotMessageLineView = robotMessageLineView;
	}

	public void startShowAnimation() {
		// Log.e("message", "startShowAnimation");
		mAppContext.mGdxApplication.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Texture oldTexture = (Texture) RobotMessageView.this.region
						.getTexture();
				TextureRegion newRegion = RobotMessageView.this
						.getRobotMessageTexture(mAppContext.mWidgetContext,
								WidgetRobot.REBOT_MESSAGE);
				RobotMessageView.this.region.setRegion(newRegion);
				oldTexture.dispose();
			}
		});
		// 设置scaleY=0，则当view显示的时候是不会出现跳出显示的效果
		this.scaleX = 1;
		this.scaleY = 0;
		this.show();

		mShowMessageTimeline = Timeline.createParallel();
		mShowMessageTimeline.push(Tween
				.to(this, View3DTweenAccessor.SCALE_XY, 0.1f).ease(Quad.INOUT)
				.target(1, 1));
		mShowMessageTimeline.push(Tween
				.to(this, View3DTweenAccessor.SCALE_XY, 0.1f).ease(Quad.INOUT)
				.target(1, 0).delay(3f));
		mShowMessageTimeline.start(View3DTweenAccessor.manager)
				.setCallbackTriggers(TweenCallback.COMPLETE).setCallback(this);

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if (source.equals(this.mShowMessageTimeline)
				&& TweenCallback.COMPLETE == type) {
			mShowMessageTimeline.free();
			mShowMessageTimeline = null;
			this.hide();
			if (mRobotMessageLineView != null) {
				mRobotMessageLineView.stopShowAnimation();
			}
		}
	}

	public TextureRegion getRobotMessageTexture(Context context, String message) {
		Bitmap tempImage = ImageHelper.getImageFromResource(context,
				R.drawable.robot_message);
		Bitmap backImage = tempImage.copy(Config.ARGB_8888, true);
		tempImage.recycle();

		Canvas canvas = new Canvas(backImage);
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setTextSize(context.getResources().getDimension(
				R.dimen.robot_message_font));
		paint.setTextAlign(Align.CENTER);
		float paddintTop = context.getResources().getDimension(
				R.dimen.robot_message_padding_top);
		float paddingLeft = paddintTop;
		float cellSpace = context.getResources().getDimension(
				R.dimen.robot_message_cell_space);
		FontMetrics fontMetrics = paint.getFontMetrics();
		// float lineHeight = fontMetrics.bottom - fontMetrics.top;
		// float lineHeight = getFontHeight(context.getResources().getDimension(
		// R.dimen.robot_message_font));
		float lineHeight = (float) Math.ceil(fontMetrics.descent
				- fontMetrics.ascent);

		// // 计算文字高度
		// float fontHeight = fontMetrics.bottom - fontMetrics.top;
		// // 计算文字baseline
		// float textBaseY = height - (height - fontHeight) / 2
		// - fontMetrics.bottom;
		// canvas.drawText(text, width / 2, textBaseY, paint);

		List<String> messageLines = new ArrayList<String>();
		String messageItem = this.splitMessage(paint, message,
				(int) (backImage.getWidth() - paddingLeft * 2));
		while (messageItem != null && messageItem.length() > 0) {
			messageLines.add(messageItem);
			message = message.substring(messageItem.length());
			messageItem = splitMessage(paint, message,
					(int) (backImage.getWidth() - paddingLeft * 2));
		}

		float posX = backImage.getWidth() / 2;
		float posY = 0;
		if (messageLines.size() == 1) {
			posY = backImage.getHeight() - (backImage.getHeight() - lineHeight)
					/ 2 - fontMetrics.bottom;
		} else if (messageLines.size() == 2) {
			// posY = backImage.getHeight() - (backImage.getHeight() -
			// lineHeight)
			// / 2 - fontMetrics.bottom - paddintTop;
			posY = backImage.getHeight()
					- (backImage.getHeight() - lineHeight * messageLines.size() - cellSpace
							* (messageLines.size() - 1)) / 2
					- fontMetrics.bottom - lineHeight;
			// Log.e("testPosY", "testPosY:" + testPosY);
		}
		int i = 0;
		float pos = posY;
		for (String tempMessage : messageLines) {
			if (i == 0) {
				pos = posY;
			} else {
				pos = posY + i * (cellSpace + lineHeight);
			}
			canvas.drawText(tempMessage, posX, pos, paint);
			i++;
		}

		Texture3D texture = new Texture3D(mAppContext.gdx,
				ImageHelper.bmp2Pixmap(backImage));
		TextureRegion newTextureRegion = new TextureRegion(texture);
		backImage.recycle();
		return newTextureRegion;
	}

	public int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	private String splitMessage(Paint paint, String message, int totalWidth) {
		if (paint.measureText(message) > totalWidth) {
			while (paint.measureText(message) > totalWidth) {
				message = message.substring(0, message.length() - 1);
			}
		}
		return message;
	}
}
