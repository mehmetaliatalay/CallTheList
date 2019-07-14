package myapplication.callthelist.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import myapplication.callthelist.EventBusModel.EventBusModels;

public class CallReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isIncoming;
    private static String savedNumber;


    @Override
    public void onReceive(Context context, Intent intent) {


        try {
            //to Fix Lolipop broadcast issue.
            long subId = intent.getLongExtra("subscription", Long.MIN_VALUE);
            if (subId < Integer.MAX_VALUE) {
                if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                    savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                } else {
                    String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                    String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    int state = 0;
                    if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        state = TelephonyManager.CALL_STATE_IDLE;

                    } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        state = TelephonyManager.CALL_STATE_OFFHOOK;

                    } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        state = TelephonyManager.CALL_STATE_RINGING;
                    }
                    onCallStateChanged(context, state, number);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

        public void onCallStateChanged( final Context context, int state, String number){
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    savedNumber = number;

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                        isIncoming = true;
                       // onReceiveCall(context, savedNumber, callStartTime, "received");
                    } else {
                        isIncoming = false;
                        Log.i("MyCallLog","Arama Başladı");
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (lastState == TelephonyManager.CALL_STATE_RINGING) {

                    //    onMissedCall(context, savedNumber, callStartTime, "Missed");
                    } else if (isIncoming) {
                      //  onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                    } else {
                        Log.i("MyCallLog","Arama Bitti");
                        //onOutgoingCallEnded(context, savedNumber, callStartTime, new Date(), "outgoing");
                        EventBus.getDefault().postSticky(new EventBusModels.MakePhoneCall(1));
                    }
                    break;
            }
            lastState = state;

        }
    }

