package com.example.checkersgame;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.example.checkersgame.CheckersPiece.PieceType;

public class GameWin extends Activity implements Callback, OnTouchListener
{
	private RenderView v;
	private CheckersBoard cb;
	private CheckersMove move;
	MediaPlayer mediaPlayer = null;
	int difficulty;
	GameTree gt;
	private int MOVEMENT_THRESHOLD = -1;
	int x, y;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        difficulty = this.getIntent().getIntExtra(getString(R.string.extra_difficulty), -1); // get difficulty level
		move = null;
        cb = new CheckersBoard();

        LayoutInflater l = this.getLayoutInflater();
		View root = l.inflate(R.layout.game_win, null);
		v = (RenderView) root.findViewById(R.id.renderView);
		v.setGameWin(this);
		v.setOnTouchListener(this);
		if (v == null) Log.d("GameWin.onActivityResult()", "v == null ");
		v.getHolder().addCallback(this);
		this.setContentView(root);
		initMediaPlayer();
	}

	public CheckersBoard getBoard()
	{
		return cb;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1)
	{
		if (MOVEMENT_THRESHOLD == -1)
		{
			MOVEMENT_THRESHOLD = Commons.getBounds().width() / 8 / 2;
			// moved only if moved greater than half the width of a block
		}
		if (arg0 == v)
		{

			if (arg1.getAction() == MotionEvent.ACTION_DOWN)
			{
				x = (int) arg1.getX();
				y = (int) arg1.getY();
				Log.d("GameWin.onTouch", "x = : " + x + "y = : " + y);
			}

			if (arg1.getAction() == MotionEvent.ACTION_UP)
			{
				// Log.d("here", "here");
				if (Math.sqrt(Math.pow(x - arg1.getX(), 2) + Math.pow(y - arg1.getY(), 2)) < MOVEMENT_THRESHOLD)
				{

					int col = (int) (arg1.getX() / (cb.getDstRect().width() / 8));
					int row = (int) ((arg1.getY() - Commons.getBounds().top) / (cb.getDstRect().height() / 8));
					Log.d("GameWin.onTouch", "row = : " + row + "col = : " + col);

					if (move == null)
					{
						if (!cb.board_get_piece_at(row, col).is_none_piece())
						{
							move = new CheckersMove();
							move.setStart(new CheckersPosition(row, col));
						}
					}
					else
					{
						move.setEnd(new CheckersPosition(row, col));

						if (cb.isMoveValid(move) == CheckersBoard.MOVE_VALID)
						{
							cb.move(move);
							endGame(cb.winner());

							while (cb.get_turn() == PieceType.DARK_PIECE)
							{

								gt = new GameTree(cb, PieceType.DARK_PIECE, 0, difficulty);
								if (gt.subTrees.size() == 0)
								{
									cb.set_winner(CheckersBoard.WINNER_LIGHT);
									endGame(cb.winner());
									cb.setTurn(PieceType.DARK_PIECE);
									break;
								}
								else
								{
									cb.move(gt.playMove());
									endGame(cb.winner());
								}

							}

						}

						else
						{
							CharSequence message = "Invalid Move";
							Toast toast = Toast.makeText(this.getApplicationContext(), message,
									Toast.LENGTH_SHORT);
							toast.show();
						}
						move = null;
					}
				}
			}
			return true;
		}
		// let the system handle everything else
		return false;
	}

	private void endGame(int winner)
	{
		if (winner == CheckersBoard.DRAW) return;
		String msg = "";
		if (winner == CheckersBoard.WINNER_DARK) msg = "The Dark Side Won\nDo you Wish to Play Again?";
		else msg = "Light Won\n Do you Wish to Play Again ?";

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Game End");
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				cb = new CheckersBoard();
				dialog.cancel();

			}
		});
		builder.setNegativeButton("No", new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				GameWin.this.finish();

			}
		});
		builder.create();
		builder.show();
	}

	private void initMediaPlayer()
	{
		AssetManager manager;
		AssetFileDescriptor descriptor;
		try
		{
			this.setVolumeControlStream(android.media.AudioManager.STREAM_MUSIC);
			manager = this.getAssets();
			descriptor = manager.openFd("music/gamemusic.mp3");
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(),
					descriptor.getLength());
			mediaPlayer.setLooping(true);
			mediaPlayer.prepare();
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
			descriptor.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			descriptor = null;
			manager = null;
		}
	}

    @Override
    public void onPause()
    {
        super.onPause();
        if (v != null) v.pause();
        if (mediaPlayer != null) mediaPlayer.pause();
        if (this.isFinishing()) mediaPlayer.release();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mediaPlayer != null) mediaPlayer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d("GameWin.surfaceCreated()", "-here");
        if (v == null) Log.d("GameWin.surfaceCreated()", "v == null ");
        v.resume();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.d("GameWin.surfaceDestroyed()", "-here");

    }
}
