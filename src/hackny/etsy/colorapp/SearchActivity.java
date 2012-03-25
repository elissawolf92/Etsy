package hackny.etsy.colorapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SearchActivity extends Activity {
	
	private HttpClient client;
	private int resultsNum;
	private String color;
	String h;
	String s;
	String v;
	

	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlayout);
        System.out.println("stuff prints");
//        Bundle bundle = this.getIntent().getExtras();
//        int r = bundle.getInt("r");
//        int g = bundle.getInt("g");
//        int b = bundle.getInt("b");
//        float[] hsv = new float[3];
//        Color.RGBToHSV(r, g, b, hsv);
//        h = Float.toString(hsv[0]);
//        s = Float.toString(hsv[1]);
//        v = Float.toString(hsv[2]);
        int r = 0;
        int g = 0;
        int b = 0;
        client = new DefaultHttpClient();
        resultsNum = 0;
        color = "000000";
        //need to get the color input from the image and set it
        try {
			populateLayout();
		} catch (JSONException e) {
			
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
	
	public JSONArray getSearchResults() throws JSONException, ClientProtocolException, IOException, URISyntaxException{
		//send the request, get the JSON object
		System.out.println("in getsearchresults");
		HttpGet get = new HttpGet();
		get.setURI(new URI("http://openapi.etsy.com/v2/public/listings/active?api_key=" +
				"ac3681yhl1tbjgztnft8x9px&offset=0&fields=listing_id%2Cstate%2Ccategory_path&" +
				"color=" +
				h + "%2C" + s + "%2C" + v + "&color_accuracy=5&includes=MainImage&category=Jewelry"));
		//get.setURI(new URI("http://openapi.etsy.com/v2/public/listings/active?keywords=jewelry&api_key=ac3681yhl1tbjgztnft8x9px&offset=0&fields=listing_id,state&color=%23"
			//	+ color + "&includes=MainImage"));
		System.out.println("set URI");
		HttpResponse response = client.execute(get);
		System.out.println("just executed");
		BufferedReader in = new BufferedReader
        (new InputStreamReader(response.getEntity().getContent()));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
            sb.append(line + NL);
            System.out.println("in loop");
        }
        in.close();
        String json = sb.toString();
        System.out.println(json);
		//BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		//String json = reader.readLine();
		JSONObject object = new JSONObject(json);
		resultsNum = object.getInt("count");
		return object.getJSONArray("results");
		
//		BufferedReader in = null;
//		try {
//            HttpClient client = new DefaultHttpClient();
//            HttpGet request = new HttpGet();
//            request.setURI(new URI("http://w3mentor.com/"));
//            HttpResponse response = client.execute(request);
//            in = new BufferedReader
//            (new InputStreamReader(response.getEntity().getContent()));
//            StringBuffer sb = new StringBuffer("");
//            String line = "";
//            String NL = System.getProperty("line.separator");
//            while ((line = in.readLine()) != null) {
//                sb.append(line + NL);
//            }
//            in.close();
//            String page = sb.toString();
//            System.out.println(page);
//            } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                    } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
	}
	
	
	public void populateLayout() throws JSONException, ClientProtocolException, IOException, URISyntaxException{
		System.out.println("in populate layout");
		JSONArray results = getSearchResults();
		//getSearchResults();
		//resultsNum = 1;
		LinearLayout layout = (LinearLayout) findViewById(R.id.searchlayout);
		for (int i = 0; i < 2; i++){
			//get image from i entry of results array
			System.out.println("in for loop");
			JSONObject current = results.getJSONObject(i);
			JSONObject mainImage = current.getJSONObject("MainImage");
			String urlString = mainImage.getString("url_fullxfull");
			URL url = new URL(urlString);
		    InputStream content = (InputStream)url.getContent();
		    Drawable d = Drawable.createFromStream(content , "src"); 
			ImageView image = new ImageView(this);
			Drawable picture = getResources().getDrawable(R.drawable.testnecklace);
			image.setImageDrawable(d);
			layout.addView(image);
			System.out.println("image supposedly added");
			
		}
		
	}

}
