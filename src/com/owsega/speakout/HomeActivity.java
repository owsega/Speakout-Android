package com.owsega.speakout;

import java.util.Locale;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnInitListener {

	ImageButton speakBtn;
	EditText inputTextField;
	final static int CHECK_TTS = 200;
	private TextToSpeech mTts;
	private boolean ttsEngineReady;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		speakBtn = (ImageButton) findViewById(R.id.speak_button);
		inputTextField = (EditText) findViewById(R.id.input_field);

		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, CHECK_TTS);
		ttsEngineReady = false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHECK_TTS) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				mTts = new TextToSpeech(this, this);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void speakButtonClicked(View btn) {
		Editable input = inputTextField.getText();
		if (input.length() == 0)
			Toast.makeText(this, " :(  Nothing to speak !", Toast.LENGTH_SHORT)
					.show();
		else if (!ttsEngineReady)
			Toast.makeText(this, "Please wait while we set things up",
					Toast.LENGTH_SHORT).show();
		else {
			mTts.speak(input.toString(), TextToSpeech.QUEUE_FLUSH, null);
			try {
				cacheNewSpeechData(input.toString());
			} catch (Exception e) {
				// if caching speech data failed, just move on !! Who cares?
			}
		}
	}

	private final static String CACHE_PATH = Environment.getDataDirectory()
			.getPath();

	private void cacheNewSpeechData(String input) {
		String filename = (input.length() > 10) ? input : input.substring(0, 9);
		mTts.addSpeech(input, CACHE_PATH + filename);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS)
			mTts.setLanguage(Locale.UK);
		ttsEngineReady = true;
	}

	@Override
	public void onDestroy() {
		mTts.shutdown();
	}
}
