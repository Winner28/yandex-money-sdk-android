package ru.yandex.money.android.fragments;

import android.app.Fragment;
import android.content.Intent;

import com.yandex.money.model.cps.ProcessExternalPayment;

import ru.yandex.money.android.IntentHandler;
import ru.yandex.money.android.MultipleBroadcastReceiver;
import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.parcelables.ProcessExternalPaymentParcelable;
import ru.yandex.money.android.services.DataService;

/**
 * @author vyasevich
 */
public abstract class PaymentFragment extends Fragment {

    protected static final String EXTRA_REQUEST_ID = "ru.yandex.money.android.extra.REQUEST_ID";

    protected String reqId;

    private final MultipleBroadcastReceiver receiver = buildReceiver();

    @Override
    public void onResume() {
        super.onResume();
        getPaymentActivity().registerReceiver(receiver, receiver.buildIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getPaymentActivity().unregisterReceiver(receiver);
    }

    protected PaymentActivity getPaymentActivity() {
        return (PaymentActivity) getActivity();
    }

    protected boolean isManageableIntent(Intent intent) {
        String requestId = intent.getStringExtra(DataService.EXTRA_REQUEST_ID);
        return requestId != null && requestId.equals(reqId);
    }

    protected void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        reqId = null;
    }

    private MultipleBroadcastReceiver buildReceiver() {
        return new MultipleBroadcastReceiver()
                .addHandler(DataService.ACTION_EXCEPTION, new IntentHandler() {
                    @Override
                    public void handle(Intent intent) {
                        if (isManageableIntent(intent)) {
                            String error = intent.getStringExtra(DataService.EXTRA_EXCEPTION_ERROR);
                            String status = intent.getStringExtra(DataService.EXTRA_EXCEPTION_STATUS);
                            getPaymentActivity().showError(error, status);
                        }
                    }
                })
                .addHandler(DataService.ACTION_PROCESS_EXTERNAL_PAYMENT, new IntentHandler() {
                    @Override
                    public void handle(Intent intent) {
                        if (isManageableIntent(intent)) {
                            ProcessExternalPaymentParcelable parcelable = intent.getParcelableExtra(
                                    DataService.EXTRA_SUCCESS_PARCELABLE);
                            assert parcelable != null : "request extra is null";
                            onExternalPaymentProcessed(parcelable.getProcessExternalPayment());
                        }
                    }
                });
    }
}
