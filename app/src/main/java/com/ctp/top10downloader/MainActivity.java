package com.ctp.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btnParse;
    private ListView listApps;
    private String mFileContents;  //Stores the result

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnParse=(Button) findViewById(R.id.btnParse);
        listApps = (ListView) findViewById(R.id.xmlListView);

        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo Add Parse activation code
                ParseApplications parseApplications = new ParseApplications(mFileContents);
                parseApplications.process();
                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                        MainActivity.this,R.layout.list_item,parseApplications.getApplications());
                listApps.setAdapter(arrayAdapter);
            }
        });

        DownloadData downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadData extends AsyncTask<String,Void,String>{

        //we want to take advantage of async task provided by android. It takes in three parameters
        //first parameter specifies download location
        //second is void. Used for progress bar. In case youre downloading 100 MB of date. Shows user progress.
        //Since this file is small we dont use it. We use void to tell we dont wanna use it.
        //Third parameter is the result. Specify the type you want the result in.



        @Override
        protected String doInBackground(String... params) {

            // String... means variable number of parameters. Could be one,two or many more. Stores in an array with same name as parameter.
            // Using any datatype with ... means there can be variable number of parameters
            // Whatever we place in this method will be run automatically by android.

            mFileContents = downloadXMLFile(params[0]);                                              // This method will actually download the file. Defined below

            if(mFileContents==null){                                                                 // If the contents of the file is empty, we log that there was an error downloading
                Log.d("DownloadData", "Error downloading");
            }
            return mFileContents;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("DownloadData","Result was:"+result);

        }

        private String downloadXMLFile(String urlPath){
            StringBuilder tempBuffer = new StringBuilder();                                          //Store the data read from xml file here
            try{
                URL url = new URL(urlPath);                                                          // defines the url to point to top ten apps. It throws IOException
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();             // Opens the URL
                int response = connection.getResponseCode();                                         /*gets the response code for the webpage opened. For example, if a 404 error appears
                                                                                                       on the webpage, its retrieved and stored in response.*/
                Log.d("DownloadData","The response code was "+ response);                            /*Response code is logged so that we can see what is happening*/
                InputStream is = connection.getInputStream();                                        // Gets the input stream using the connection object
                InputStreamReader isr = new InputStreamReader(is);                                   // Used to process the input stream object. Java code to access data

                //Define variables to help read data in steps of 500 characters
                int charRead;
                char[] inputBuffer = new char[500];                                                 // Read files 500 characters at a time

                while(true){                                                                        //loop to read all characters in inputstream
                    charRead = isr.read(inputBuffer);                                               // read the data in the inputbuffer
                    if(charRead <=0){                                                               //Exits the loop if no characters in charreaf
                        break;
                    }
                    tempBuffer.append(String.copyValueOf(inputBuffer,0,charRead));                  // Reads the data in input buffer from position 0 to charRead. The reason we dont write 500
                                                                                                    // is because in the last iteration, there might be less than 500.
                }
                return tempBuffer.toString();                                                       // Return a string by converting the buffer to string
            }
            catch(IOException e){
                Log.d("DownloadData","IOExceptions reading data"+ e.getMessage());                  //If connection fails, it will appear in log

            }
            catch(SecurityException e){                                                             //Catches the security exception if permission is not asked in manifest
                Log.d("DownloadData","Security exception. Needs Permission?");
            }
            return null;
        }
    }
}
