/*
 * SlidePuzzleAndroid
 * Copyright (C) 2012 Matt Boes.
 * All rights reserved.
 */

package com.boes.slidepuzzle.android;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;

public class Piece {

	private static final String TAG = Piece.class.getSimpleName();

	private String id;
	public int row;
	public int col;

	public Bitmap bitmap;
	public static Activity game;
	public static float width;
	public static float height;
	
	public Piece(String id, int row, int col) {
		this.id = id;
		this.row = row;
		this.col = col;
	}
	
	public void createBitmap() {
		Log.d(TAG, "width = " + width);
		
		bitmap = Bitmap.createBitmap((int) width, (int) height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);	

		int margin = (int) width / 40; // 160 / 40 = 4 on G2 phone
		int rht = (int) width - margin;
		int bot = (int) height - margin;
				
		int[] colors = {Color.CYAN, Color.BLUE};

		/*
		Resources res = game.getResources();
		int[] colors = {res.getColor(R.color.LightCyan),
						res.getColor(R.color.Cyan),
						res.getColor(R.color.DarkCyan)};
		*/
		
		float radius = 16;
		RoundRectShape tile = new RoundRectShape(
				new float[] {radius, radius, radius, radius, radius, radius, radius, radius}, 
				null, 
				null);
		
		ShapeDrawable shadow = new ShapeDrawable(tile);
		shadow.setBounds(margin, margin, rht, bot);
		shadow.getPaint().setShadowLayer(1, margin, margin, Color.DKGRAY);
		shadow.draw(canvas);

		ShapeDrawable drawable = new ShapeDrawable(tile);
		drawable.setBounds(margin, margin, rht, bot);
		drawable.getPaint().setShader(new LinearGradient(margin, margin, rht, bot, colors, null, Shader.TileMode.REPEAT));
		drawable.draw(canvas);
		
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.WHITE);
		textPaint.setStyle(Style.FILL);
		textPaint.setTextAlign(Align.CENTER);
		
		Rect r = drawable.getBounds();
		textPaint.setTextSize(r.height() * 0.75f);
		FontMetrics fm = textPaint.getFontMetrics();
		float xText = r.exactCenterX();
		float yText = r.exactCenterY() - (fm.ascent + fm.descent) / 2.0f;
		
		canvas.drawText(id, xText, yText, textPaint);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		else return ((Piece) other).id.equals(id);
	}
	
}