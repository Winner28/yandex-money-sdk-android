package ru.yandex.money.android.sample.mockServer;

import android.content.Context;

import com.yandex.money.api.util.HttpHeaders;
import com.yandex.money.api.util.MimeTypes;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static ru.yandex.money.android.sample.mockServer.ResponseType.*;


public class MockDispatcher extends Dispatcher {

    private final Context context;

    public MockDispatcher(@NotNull Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    public MockResponse dispatch(@NotNull RecordedRequest request) {
        switch (Objects.requireNonNull(request.getRequestUrl()).url().getPath()) {
            case "/api/request-external-payment": return successResponse(REQUEST_EXTERNAL_PAYMENT);
            case "/api/process-external-payment": return successResponse(PROCESS_EXTERNAL_PAYMENT);
            case "/api/instance-id": return successResponse(INSTANCE_ID);
            default: return new MockResponse().setResponseCode(400).setBody("Unsupported request");
        }
    }

    @NotNull
    private MockResponse successResponse(@NotNull ResponseType type) {
        return new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MimeTypes.Text.JSON)
                .setBody(loadResponse(type));
    }

    @NotNull
    private String loadResponse(@NotNull ResponseType responseType) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(responseType.path)))) {
            String line = "";
            final StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}