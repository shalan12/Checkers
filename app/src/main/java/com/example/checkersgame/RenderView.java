package com.example.checkersgame;

import java.io.InputStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RenderView extends SurfaceView
{
	private Bitmap board;
	private Bitmap darkPiece;
	private Bitmap lightPiece;
	private Bitmap darkCrowned;
	private Bitmap lightCrowned;
	private SurfaceHolder holder;
	private boolean init;
	private double xScale, yScale;
	private double baseLength;
	String initBoardState;
	private Context context;
	ScheduledThreadPoolExecutor stpe;
	GameWin gw;

	public void setGameWin(GameWin gw)
	{
		this.gw = gw;
	}

	public RenderView(Context context, AttributeSet as)
	{
		super(context, as);
		this.context = context;
		this.gw = gw;
		Log.d("RenderView()", "-here");
		AssetManager assets = context.getAssets();
		InputStream iS = null;
		baseLength = 800;
		init = false;
		try
		{
			iS = assets.open("images/checkersBoard.png");
			board = BitmapFactory.decodeStream(iS);
			iS = assets.open("images/dark_piece.png");
			darkPiece = BitmapFactory.decodeStream(iS);
			iS = assets.open("images/light_piece.png");
			lightPiece = BitmapFactory.decodeStream(iS);
			iS = assets.open("images/dark_crowned.png");
			darkCrowned = BitmapFactory.decodeStream(iS);
			iS = assets.open("images/light_crowned.png");
			lightCrowned = BitmapFactory.decodeStream(iS);

			holder = this.getHolder();
			this.setZOrderOnTop(true);
			holder.setFormat(PixelFormat.TRANSPARENT);
		}
		catch (Exception e)
		{

		}
		// TODO Auto-generated constructor stub
	}

	public void draw()
	{
		if (holder.getSurface().isValid())
		{
			Canvas canvas = holder.lockCanvas();

			if (!init)
			{
				xScale = canvas.getWidth() / baseLength;
				// yScale = canvas.getHeight() / baseLength;
				int height = (int) (800 * xScale);
				int top = (canvas.getHeight() - height) / 2;
				int bottom = top + height;
				int right = (int) (800 * xScale);
				Commons.setBounds(new Rect(0, top, right, bottom));
				gw.getBoard().setDstRect(0, top, right, bottom);

				init = true;
			}
			// canvas.drawARGB(0, 0, 0, 0);
			canvas.drawBitmap(board, null, gw.getBoard().getDstRect(), null);
			CheckersPiece cp;
			Rect temp;
			// System.out.println("width = " +
			// Integer.toString(canvas.getWidth()) + "height = "
			// + Integer.toString(canvas.getHeight()));

			for (int i = 0; i < 8; i++)
			{
				for (int j = 0; j < 8; j++)
				{
					cp = gw.getBoard().board_get_piece_at(i, j);
					if (!cp.is_none_piece())
					{
						temp = cp.getdstRect();
						if (cp.is_dark() && !cp.is_crowned()) canvas.drawBitmap(darkPiece, null,
								temp, null);
						else if (cp.is_dark() && cp.is_crowned())
							canvas.drawBitmap(darkCrowned, null, temp, null);
						if (!cp.is_dark() && !cp.is_crowned()) canvas.drawBitmap(lightPiece, null,
								temp, null);
						else if (!cp.is_dark() && cp.is_crowned())
							canvas.drawBitmap(lightCrowned, null, temp, null);

					}
				}

			}
			holder.unlockCanvasAndPost(canvas);
		}
	}

	public void pause()
	{
		stpe.shutdownNow();
		stpe.purge();
	}

	public void resume()
	{
		stpe = new ScheduledThreadPoolExecutor(1);
		stpe.scheduleWithFixedDelay(new Runnable()
		{
			public void run()
			{
				RenderView.this.draw();
			}
		}, 200, 60, TimeUnit.MILLISECONDS);

	}
}