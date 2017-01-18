package com.example.checkersgame;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;
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

    private class DrawableCheckersPiece implements MoveListener{

        private CheckersPiece cp;

        // for animation
        private Queue<Float> velX, velY; // we'll have a queue of velocities, corresponding to the pending animations (in-case more than one move is performed suddenly)
        private final int timesteps = 8;
        private int curr_time_step;
        private Rect dstRect;
        int lastRow, lastCol;

        public DrawableCheckersPiece(CheckersPiece cp) {
            this.cp = cp;
            dstRect = new Rect();
            velX = new ArrayDeque<Float>();
            velY = new ArrayDeque<Float>();
            curr_time_step = 0;
            lastRow = cp.get_position().get_row();
            lastCol = cp.get_position().get_col();
            gw.getBoard().addMoveListener(this);
        }

        private void updateDstRect(){
            Rect bounds = Commons.getBounds();
            CheckersPosition pos = cp.get_position();
            // if piece has changed position
            if (lastRow != pos.get_row() || lastCol != pos.get_col()) {
                float sX = (pos.get_col() - lastCol) * (bounds.right - bounds.left) / CheckersBoard.NUM_COLS ;
                float sY = (pos.get_row() - lastRow) * (bounds.bottom - bounds.top) / CheckersBoard.NUM_ROWS ;
                this.velX.add(sX/timesteps);
                this.velY.add(sY/timesteps);
                lastRow = pos.get_row();
                lastCol = pos.get_col();
            }
            // animate piece to it's position
            if (curr_time_step >= timesteps) {
                if (!velX.isEmpty()) velX.remove();
                if (!velY.isEmpty()) velY.remove();
                curr_time_step = 0;
            }
            if (velX.isEmpty()) {
                dstRect.left = (int) ((bounds.right - bounds.left) / CheckersBoard.NUM_COLS * pos.get_col());
                dstRect.right = (int) ((int) (dstRect.left + (bounds.right - bounds.left) / ((float)CheckersBoard.NUM_COLS) ));
                dstRect.top = (int) ((bounds.bottom - bounds.top) / CheckersBoard.NUM_ROWS * (pos.get_row()) + bounds.top);
                dstRect.bottom = (int) ((int) (dstRect.top + (bounds.bottom - bounds.top) / ((float)CheckersBoard.NUM_ROWS) ));
            } else {
                curr_time_step += 1;
                dstRect.left += velX.peek();
                dstRect.right += velX.peek();
                dstRect.top += velY.peek();
                dstRect.bottom += velY.peek();
            }
        }

        public Rect getdstRect()
        {
            updateDstRect();
            return dstRect;
        }

        @Override
        public void onMove(CheckersMove cm) {
            CheckersPiece cp = gw.getBoard().getPieceAt(cm.get_end().get_row(), cm.get_end().get_col());
            if (cp == this.cp) updateDstRect();
        }
    }


    private Bitmap board;
	private Bitmap darkPiece;
	private Bitmap lightPiece;
	private Bitmap darkCrowned;
	private Bitmap lightCrowned;
	private SurfaceHolder holder;
	private boolean init;
	private double baseLength;
	ScheduledThreadPoolExecutor stpe;
	GameWin gw;
    DrawableCheckersPiece[][] drawableCheckersPieces;

	public void setGameWin(GameWin gw)
	{
		this.gw = gw;
        for (int i = 0; i < CheckersBoard.NUM_ROWS; i++) {
            for (int j = 0; j < CheckersBoard.NUM_COLS; j++) {
                drawableCheckersPieces[i][j] = new DrawableCheckersPiece(gw.getBoard().getPieceAt(i, j));
            }
        }
	}

	public RenderView(Context context, AttributeSet as)
	{
		super(context, as);
		this.gw = gw;
		Log.d("RenderView()", "-here");
		AssetManager assets = context.getAssets();
		InputStream iS = null;
		baseLength = 800;
		init = false;
        drawableCheckersPieces = new DrawableCheckersPiece[CheckersBoard.NUM_ROWS][CheckersBoard.NUM_COLS];

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
	}

	public void draw()
	{
		if (holder.getSurface().isValid())
		{
			Canvas canvas = holder.lockCanvas();

			if (!init)
			{
				double scale = canvas.getWidth() / baseLength;
				int height = (int) (800 * scale);
				int top = (canvas.getHeight() - height) / 2;
				int bottom = top + height;
				int right = (int) (800 * scale);
				Commons.setBounds(new Rect(0, top, right, bottom));
				gw.getBoard().setDstRect(0, top, right, bottom);

				init = true;
			}

			canvas.drawBitmap(board, null, gw.getBoard().getDstRect(), null);
			Rect temp;

			for (int i = 0; i < CheckersBoard.NUM_ROWS; i++)
			{
				for (int j = 0; j < CheckersBoard.NUM_COLS; j++)
				{
                    DrawableCheckersPiece drawablePiece = drawableCheckersPieces[i][j];
                    CheckersPiece cp = drawablePiece.cp;

					if (!cp.is_none_piece())
					{
						temp = drawablePiece.getdstRect();
						if (cp.getPieceType() == CheckersPiece.PieceType.DARK_PIECE)
						{
							if (cp.is_crowned()) canvas.drawBitmap(darkCrowned, null, temp, null);
							else canvas.drawBitmap(darkPiece, null, temp, null);
						}
						else
						{
							if (cp.is_crowned()) canvas.drawBitmap(lightCrowned, null, temp, null);
							else canvas.drawBitmap(lightPiece, null, temp, null);
						}
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
