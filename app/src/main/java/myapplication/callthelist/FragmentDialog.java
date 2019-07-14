package myapplication.callthelist;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import myapplication.callthelist.Data.CallListProvider;
import myapplication.callthelist.EventBusModel.EventBusModels;


public class FragmentDialog extends DialogFragment {

    static final Uri CONTENT_URI = CallListProvider.CONTENT_URI;

    private EditText mEtUserName;
    private MaskedEditText mEtPhone;
    private Button mAddButton;
    private Button mCancelButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_number, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEtUserName = view.findViewById(R.id.etUsername);
        mEtPhone = view.findViewById(R.id.etPhone);
        mAddButton = view.findViewById(R.id.add_button);
        mCancelButton = view.findViewById(R.id.cancel_button);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                if (mEtPhone.getRawText().length() == 10 && mEtUserName.getText().toString().length() > 0) {
                    
                    values.put("name", mEtUserName.getText().toString());
                    values.put("number", mEtPhone.getRawText());
                    getActivity().getContentResolver().insert(CONTENT_URI, values);
                    EventBus.getDefault().post(new EventBusModels.UpdateList(1));
                    dismiss();
                }

            }
        });
    }


}
