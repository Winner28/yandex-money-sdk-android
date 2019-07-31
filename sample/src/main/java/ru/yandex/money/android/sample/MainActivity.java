/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 NBCO Yandex.Money LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ru.yandex.money.android.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import okhttp3.mockwebserver.MockWebServer;
import ru.yandex.money.android.mockServer.MockDispatcher;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.MOCK_ENABLED) {
            startWebServer();
        }
        setContentView(R.layout.activity_main);

        findButton(R.id.send_p2p).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayActivity.startP2P(MainActivity.this);
            }
        });

        findButton(R.id.top_up_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayActivity.startPhone(MainActivity.this);
            }
        });
    }

    private Button findButton(int id) {
        return (Button) findViewById(id);
    }

    private void startWebServer() {
        new Thread() {
            @Override
            public void run() {
                final MockWebServer mockWebServer = new MockWebServer();
                mockWebServer.setDispatcher(new MockDispatcher(getApplicationContext()));
                try {
                    mockWebServer.start(8080);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
