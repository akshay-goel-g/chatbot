package com.example.aky.chatbot;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    private ChatMessageAdapter mAdapter;
    int x = 0;
    String getmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (Button) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

//code for sending the message
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                Client myClient = new Client("testelb1-1617194167.ap-south-1.elb.amazonaws.com", 9000,message);
                myClient.execute();
                sendMessage(message);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }
    public class Client extends AsyncTask<Void, Void, String> {
        String returnmsg;
        String dstAddress;
        int dstPort;
        String sendText;
        static final int BUFFER_SIZE = 2048;
        public String message = "";

        Client(String addr, int port, String mainText) {
            dstAddress = addr;
            dstPort = port;
            sendText = mainText;
        }

        @Override
        protected String doInBackground(Void... arg0) {

            {
                Socket socket = null;
                Socket s;
                try {
                    try {

                            s = new Socket(dstAddress, dstPort);
                            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                            printWriter.print(sendText);
                            printWriter.flush();
                            DataInputStream dis = new DataInputStream(s.getInputStream());
                            String strrr = (String) dis.readLine();
                            dis.close();
                            s.close();
                            return strrr;


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    //this.exception = e;
                    return null;
                }

                return null;

            }

        }
        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if(aVoid!=null)
            {
            Bundle b = new Bundle();
            b.putString("myname", aVoid);
          Log.e("returnnnnn",aVoid+"hell"+sendText);
                         returnmsg=aVoid;
                mimicOtherMessage(returnmsg);
            }
            Log.e("returnnnnn",aVoid+"hell"+sendText);
        }

        public String getReturnmsg() {
            return returnmsg;
        }
    }
    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);
        //respond as Helloworld
        //mimicOtherMessage("HelloWorld"+getmsg);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
        }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }

}


class ChatMessage {
    private boolean isImage, isMine;
    private String content;

    public ChatMessage(String message, boolean mine, boolean image) {
        content = message;
        isMine = mine;
        isImage = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }
}
class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;

    public ChatMessageAdapter(Context context, List<ChatMessage> data) {
        super(context, R.layout.sender, data);
    }

    @Override
    public int getViewTypeCount() {
        // my message, other message, my image, other image
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);

        if (item.isMine() && !item.isImage()) return MY_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return OTHER_MESSAGE;
        else if (item.isMine() && item.isImage()) return MY_IMAGE;
        else return OTHER_IMAGE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sender, parent, false);

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());

        } else if (viewType == OTHER_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bot, parent, false);

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());
        } else if (viewType == MY_IMAGE) {
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_image, parent, false);
        } else {
            // convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_image, parent, false);
        }
        convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "onClick", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }
}



