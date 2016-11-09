##Watson Speech to Text app
If you like this lab - please give me a star!

The voice user interface is a great element for enhancing IoT, Robots, or your vehicle user interface while driving (not able to look away from the road ahead). IBM is giving you could extend your application with such an interface in about 10 minutes.

IBM Watson Speech-to-Text service is available as IBM cognitive service for us to be used from IBM Bluemix platform.

Prerequisites: [In order to use this example you need to **finish the previous lab**](https://github.com/blumareks/2016wdc/tree/master/SentimentSensitiveApp)
- in addition to previous reqs
- you would need the IBM Watson Speech-to-text cognitive services
I explain at the end of this blogpost on how to get straight with prereqs.
<b>Here goes the steps:</b>
<ul>
	<li>Get your previous lab ready - and extend it with one additional button - voice!</li>
	<li>Add the Watson lib: Watson-Developer-Cloud SDK for Android (for audio manipulation resources).</li>
	<li>Instantiate the Bluemix Watson service and get the username and password to it.</li>
	<li>Add some little code in your Android app to invoke the cognitive service.</li>
</ul>

<b>Extend it with one additional button - voice!</b>
You’ll add a new button (button2) to the existing design.

<pre>public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;
    Button button;
    Button button2; //new button for voice
</pre>

Add the async task for launching the operations when a button2 is pressed (alike previously):
<pre>    private class SpeakToWatson extends AsyncTask&lt;String, Void, String&gt; {

        @Override
        protected String doInBackground(String... voicesToTranscribe) {
</pre>
this insert is good to pass the parameters to UI from inside of the async thread:
<pre>            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("we are listening to the Speech");
                }
            });
</pre>
this is just a simple return (we will add the IBM Watson call later):
<pre>            return "speech to text done";
        }

</pre>
When done we will set the Text View with the status:
<pre>        @Override
        protected void onPostExecute(String result) {
            textView.setText("STT status: " + result);
        }

    }
</pre>
Setting up the UI parameters:
<pre>    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        //STT init
        button2 = (Button) findViewById(R.id.button2);
</pre>
Add the on click listener for button2:
<pre>        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("the STT ");

                WatsonTask task = new WatsonTask();
                task.execute(new String[]{});

            }
        });
    }
}
</pre>

Run the test to see if the button works as planned.

<b>Add the Watson lib: Watson-Developer-Cloud SDK for Android.</b>

Add also the classes for the Android Sound: [https://github.com/watson-developer-cloud/android-sdk](https://github.com/watson-developer-cloud/android-sdk)
_I downloaded the sources of Watson Developer Cloud SDK for Android classes and you can add them directly to your project under app/main/src/java/_. Alternatively copy over the classes (as it was shown on the provided video).

<b>Instantiate the Bluemix Watson STT service and get the user and the password to it.</b>
<blockquote>Create a Watson service and get the username and password for it</blockquote>
Now, you create the Watson Speech to Text service. From the IBM Bluemix catalog, click Watson &gt; Speech to Text&gt; Create.
Go to IBM Bluemix and add/create the IBM Watson Speech to Text service - click <i>Create</i> the service.

Be sure to use a static API username and password. And copy over the credentials for the service. You would need them in the app later.

Now we need to allow to call Watson service from our app. Double click <code>/app/manifest/AndroidManifest.xml</code> in the view Android:
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

<b>Final steps</b>

The final steps - first add the imports (they might be added by Android Studio for you when typing the code):
<pre>//0. adding WDC SDK for Java/Android
     import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
     import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
     import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
     import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
     import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;
</pre>
Then instantiate the code by adding the username and the password to access Watson services from the app.
And finally add the call to the Watson STT service before the return clause:
<pre>            SpeechToText service = new SpeechToText();
                             String username = "your STT user";
                             String password = "your STT pwd";
                             service.setUsernameAndPassword(username, password);
                             service.setEndPoint("https://stream.watsonplatform.net/speech-to-text/api");
                             TextToSpeech textToSpeech = initTextToSpeechService();

RecognizeOptions recOptions = new RecognizeOptions.Builder().continuous(true)
                    .contentType(MicrophoneInputStream.CONTENT_TYPE).model("en-US_BroadbandModel")
                    .interimResults(true).inactivityTimeout(2000).build();
            service.recognizeUsingWebSocket(new MicrophoneInputStream(), recOptions, new MicrophoneRecognizeDelegate());
            return "";
</pre>

In addiiton to this call you need to add couple more methods:
<pre>

    //6. addditional helper class
    private class MicrophoneRecognizeDelegate implements RecognizeCallback {
        @Override
        public void onTranscription(SpeechResults speechResults) {
            String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
            //String takeASelfie = "take a selfie ";
            /*if (text.equals(takeASelfie) && !input.getText().toString().equals(takeASelfie)) {
                cameraHelper.dispatchTakePictureIntent();
            }*/
            showMicText(text);
        }

        @Override
        public void onConnected() {

        }

        @Override
        public void onError(Exception e) {
            showError(e);
            enableMicButton();
        }

        @Override
        public void onDisconnected() {
            enableMicButton();
        }
    }

//4. UI: enable mic
    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                editText.setText(text);
            }
        });
    }

    private void enableMicButton() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                button2.setEnabled(true);
            }
        });
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    //Watch for keyboard input
    private abstract class EmptyTextWatcher implements TextWatcher {
        private boolean isEmpty = false; // assumes text is initially empty

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                isEmpty = true;
                onEmpty(true);
            } else if (isEmpty) {
                isEmpty = false;
                onEmpty(false);
            }
        }

        @Override public void afterTextChanged(Editable s) {}

        public abstract void onEmpty(boolean empty);
    }
</pre>

You are ready to run and hear the voice of IBM Watson!

<b>Check the entire code:</b>

```java
package com.ibm.sentimentsensitiveapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


//adding Watson Developer Cloud SDK for Java:
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;

//0. adding WDC SDK for Java/Android
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;

// add: <uses-permission android:name="android.permission.RECORD_AUDIO" />


public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;
    Button button;
    String sentiment;

    //1. adding Speech to Text Watson service
    SpeechToText speechService;
    Button button2;



    private class AskWatsonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... textsToAnalyse) {

            System.out.println(editText.getText());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("what is happening inside a thread - we are running Watson AlchemyAPI");
                }
            });
            AlchemyLanguage service = new AlchemyLanguage();
            service.setApiKey("API Key");

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(AlchemyLanguage.TEXT, editText.getText());
            DocumentSentiment sentiment = service.getSentiment(params).execute();
            System.out.println(sentiment);

            //passing the result to be displayed at UI in the main tread
            return sentiment.getSentiment().getType().name();
        }

        //setting the value of UI outside of the thread
        @Override
        protected void onPostExecute(String result) {
            textView.setText("The message's sentiment is: " + result);
        }


    }

    private class SpeakToWatson extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... voicesToTranscribe) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("we are listening to the Speech");
                }
            });

            // 2. init STT service
            SpeechToText service = new SpeechToText();
            String username = "Watson STT User";
            String password = "Watson STT Pwd";
            service.setUsernameAndPassword(username, password);
            service.setEndPoint("https://stream.watsonplatform.net/speech-to-text/api");

            RecognizeOptions recOptions = new RecognizeOptions.Builder().continuous(true)
                    .contentType(MicrophoneInputStream.CONTENT_TYPE).model("en-US_BroadbandModel")
                    .interimResults(true).inactivityTimeout(2000).build();
            service.recognizeUsingWebSocket(new MicrophoneInputStream(), recOptions, new MicrophoneRecognizeDelegate());
            return "";
        }
    }

    //6. addditional helper class
    private class MicrophoneRecognizeDelegate implements RecognizeCallback {
        @Override
        public void onTranscription(SpeechResults speechResults) {
            String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
            showMicText(text);
        }

        @Override
        public void onConnected() {

        }

        @Override
        public void onError(Exception e) {
            showError(e);
            enableMicButton();
        }

        @Override
        public void onDisconnected() {
            enableMicButton();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize UI parameters
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        //3. STT service setup
        button2 = (Button) findViewById(R.id.button2);
        //speechService = initSpeechToTextService();


        //fire action when button is pressed
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Logging to the console that the button pressed for the text : " + editText.getText());
                textView.setText("Displaying at UI the sentiment to be checked for : " + editText.getText());

                AskWatsonTask task = new AskWatsonTask();
                task.execute(new String[]{});
            }

        });


        //5. setup microphone listener
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button2.setEnabled(false);

                SpeakToWatson taskSTT = new SpeakToWatson();
                taskSTT.execute(new String[]{});
            }

        });
    }


    //4. UI: enable mic
    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                editText.setText(text);
            }
        });
    }

    private void enableMicButton() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                button2.setEnabled(true);
            }
        });
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }


}

```
<b>Prerequisites:</b>
– <a href="https://developer.android.com/studio/index.html" target="_blank">getting the Android Studio, and install it, run it – for windows and/or mac</a>
– <a href="https://developer.android.com/training/basics/firstapp/running-app.html" target="_blank">Get yourself either a real Android device (a smartphone or a tablet and the data cable), and enable the device for development – follow the instruction for real device</a>
– <a href="https://developer.android.com/studio/run/emulator.html" target="_blank">alternatively or in addition to a real device you might want to setup a virtual Android device</a>
– <a href="http://ibm.biz/Bdrp22" target="_blank">setup an account with IBM Bluemix for IBM Watson cognitive services – free of charge for 30 days</a>

Please follow me on Twitter: <a href="https://twitter.com/blumareks" target="_blank">@blumareks</a>, and check my blog on <a href="http://blumareks.blogspot.com/" target="_blank">blumareks.blogspot.com</a>
