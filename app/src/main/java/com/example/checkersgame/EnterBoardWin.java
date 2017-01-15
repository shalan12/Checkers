package com.example.checkersgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class EnterBoardWin extends Activity
{
	static final int EXIT_APP = -1;
	static final int SUCCESS = 1;
	AlertDialog confirmationDialog;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d("STARTING WIN", "REACHED");
		LayoutInflater l = this.getLayoutInflater();
		View v = l.inflate(R.layout.starting_win, null);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(v);
	}

	public void onClick(View v)
	{
		Object o = v.getTag();
		if (o != null)
		{
			String s = o.toString();
			if (s.equals(getString(R.string.StartingWin_doneButtonTag))) doneButtonClicked();
		}
	}

	public void doneButtonClicked()
	{
		String boardString = ((EditText) findViewById(R.id.boardEditText)).getText().toString();
		if (isBoardValid(boardString))
		{
			if (boardString != null && boardString.isEmpty()) boardString = null;
			Intent i = new Intent();
			i.putExtra(getString(R.string.extra_boardString), boardString);
			this.setResult(SUCCESS, i);
			this.finish();
		}
		else
		{
			CharSequence text = "Invalid String(s)";
			Toast toast = Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private boolean isBoardValid(String boardString)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent kE)
	{

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			EnterBoardWin.this.setResult(EXIT_APP);
			this.finish();
			return true;
		}
		return false;
	}
}
