package com.example.checkersgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MenuWin extends Activity
{
	private AlertDialog confirmationDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exit Application");
		builder.setMessage("Are You Sure You Want To Exit?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				MenuWin.this.finish();
			}
		});
		builder.setNegativeButton("No", new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.cancel();
			}
		});
		confirmationDialog = builder.create();
		this.setContentView(R.layout.menu_win);
	}

	public void onClick(View v)
	{
		int difficulty = Integer.parseInt(v.getTag().toString());
		Intent i = new Intent(this, GameWin.class);
		i.putExtra(getString(R.string.extra_difficulty), difficulty);
		this.startActivity(i);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent kE)
	{

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			confirmationDialog.show();
			return true;
		}
		return false;
	}

}
