package com.example.fry.salvavidapp;

import java.util.List;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;


public class SendMailTask extends AsyncTask {

	private Activity sendMailActivity;

	public SendMailTask(Activity activity) {
		sendMailActivity = activity;
	}

	public SendMailTask() {

	}

	protected void onPreExecute() {
	}

	@Override
	protected Object doInBackground(Object... args) {
		try {
			GMail androidEmail = new GMail(args[0].toString(), args[1].toString(), (List) args[2], args[3].toString(), args[4].toString());
			androidEmail.createEmailMessage();
			androidEmail.sendEmail();
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void onProgressUpdate(Object... values) {

	}

	@Override
	public void onPostExecute(Object result) {
	}

}
