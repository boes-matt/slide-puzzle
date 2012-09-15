package com.boes.slidepuzzle.android;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;

public class Piece {

	private String id;
	public int row;
	public int col;

	public Bitmap bitmap;
	public static float width;
	public static float height;
	
	public Piece(String id, int row, int col) {
		this.id = id;
		this.row = row;
		this.col = col;
	}
	
	public void createBitmap() {
		bitmap = Bitmap.createBitmap((int) width, (int) height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		float margin = 2.0f;
		
		ShapeDrawable drawable = new ShapeDrawable();
		drawable.setBounds((int) margin, (int) margin, (int) (width-2*margin), (int) (height-2*margin));
		Paint tilePaint = drawable.getPaint();
		tilePaint.setColor(Color.BLUE);
		float shadow = margin;
		tilePaint.setShadowLayer(shadow, shadow, shadow, Color.DKGRAY);		

		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.WHITE);
		textPaint.setStyle(Style.FILL);
		textPaint.setTextAlign(Align.CENTER);
		
		Rect r = drawable.getBounds();
		textPaint.setTextSize(r.height() * 0.75f);
		FontMetrics fm = textPaint.getFontMetrics();
		float xText = r.exactCenterX();
		float yText = r.exactCenterY() - (fm.ascent + fm.descent) / 2.0f;
		
		drawable.draw(canvas);
		canvas.drawText(id, xText, yText, textPaint);
	}
	
	@Override
	public boolean equals(Object other) {
		return ((Piece) other).id.equals(id);
	}
	
}
