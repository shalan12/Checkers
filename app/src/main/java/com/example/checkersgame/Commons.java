package com.example.checkersgame;

import android.graphics.Rect;

public class Commons
{
	private static Rect bounds;

	public static Rect getBounds()
	{
		return bounds;
	}

	public static void setBounds(Rect bounds)
	{
		Commons.bounds = bounds;
	}
}
