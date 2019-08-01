package ru.yandex.money.android.sample.mockServer;

public enum ResponseType {
    INSTANCE_ID("responses/instance-id.json"),
    REQUEST_EXTERNAL_PAYMENT("responses/request-external-payment.json"),
    PROCESS_EXTERNAL_PAYMENT("responses/process-external-payment.json");

    ResponseType(String path) {
        this.path = path;
    }

    final String path;
}