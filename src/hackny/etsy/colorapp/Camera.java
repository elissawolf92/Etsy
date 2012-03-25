package hackny.etsy.colorapp;

import android.net.Uri;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class Camera extends Activity {
	private static final String TAG = "CameraDemo";
	Camera camera;
	Preview preview;
	Button buttonClick;
	byte[] picture;
	private long time;
	int r;
	int g;
	 int b;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameralayout);
		picture = null;

		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);

		buttonClick = (Button) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
			}
		});

		Log.d(TAG, "onCreate'd");
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");

		}

	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
			FileOutputStream outStream = null;
			int length = data.length;
			int index = 0;
			for (int i = 0; i < length/2; i++){
				index+=3;
			}
			r = data[index];
			g = data[index+1];
			b = data[index+2];
			try {
				// write to local sandbox file system
				// outStream =
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to sdcard
				time = System.currentTimeMillis();
				outStream = new FileOutputStream(String.format(
						"/sdcard/%d.jpg", time));
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				picture = data;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");

		}
	};

	public JSONArray getColorObject() {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;

		//String pathToOurFile = "/sdcard/" + time + ".jpg";
		//String pathToOurFile = "/sdcard/test.jpg";
		String pathToOurFile = "/Users/elissawolf/Desktop/testnecklace";
		String urlServer = "http://api.metalayer.com/s/imglayer/1/color";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ pathToOurFile + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();

			JSONObject object = new JSONObject(serverResponseMessage);

			return object.getJSONArray("colors");

			// fileInputStream.close();
			// outputStream.flush();
			// outputStream.close();
		} catch (Exception ex) {
			// Exception handling
		}
		 
		return null;
	}

	public Bundle parseColors() {
		JSONArray allColors = getColorObject();
		int max = 0;
		 //r = 0;
		 //g = 100;
		 //b = 0;
		if (allColors != null){
		for (int i = 0; i < allColors.length(); i++) {
			try {
				JSONArray colorArray = allColors.getJSONArray(i);
				int pixelCount = colorArray.getInt(0);
				if (pixelCount > max) {
					max = pixelCount;
					JSONArray colors = colorArray.getJSONArray(1);
					r = colors.getInt(0);
					g = colors.getInt(1);
					b = colors.getInt(2);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
		r = 0;
		 g = 100;
		 b = 0;
		
		Bundle bundle = new Bundle();
		bundle.putInt("r", r);
		bundle.putInt("g", g);
		bundle.putInt("b", b);
		return bundle;

	}

	public void onSearchClicked(View view) {
		Bundle bundle = parseColors();
		Intent intent = new Intent(this, SearchActivity.class);
		intent.putExtras(bundle);
		
		startActivity(intent);

	}

}