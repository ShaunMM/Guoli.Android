package Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import zj.com.mc.R;

/**
 * 列车编组Dialog
 */
public class TianGrapDialog extends Dialog {

    private TextView tv_cencle, tv_save;
    private EditText edt_CarriageCount;
    private EditText edt_CarryingCapacity;
    private EditText edt_Length;
    private EditText edt_educe;
    private EditText edt_expel;
    private EditText edt_rush;
    private EditText edt_eave;
    private OntvOkClick click;

    public TianGrapDialog(Context context, OntvOkClick click) {
        super(context);
        this.click = click;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_taingrap);
        tv_cencle = (TextView) findViewById(R.id.tv_cencle);
        tv_save = (TextView) findViewById(R.id.tv_save);
        edt_CarriageCount = (EditText) findViewById(R.id.edt_CarriageCount);
        edt_CarryingCapacity = (EditText) findViewById(R.id.edt_CarryingCapacity);
        edt_Length = (EditText) findViewById(R.id.edt_Length);
        edt_educe = (EditText) findViewById(R.id.edt_educe);
        edt_expel = (EditText) findViewById(R.id.edt_expel);
        edt_rush = (EditText) findViewById(R.id.edt_rush);
        edt_eave = (EditText) findViewById(R.id.edt_eave);

        setKeyListener(edt_CarriageCount);
        setKeyListener(edt_CarryingCapacity);
        setKeyListener(edt_Length);
        setKeyListener(edt_educe);
        setKeyListener(edt_expel);
        setKeyListener(edt_rush);
        setKeyListener(edt_eave);

        setCanceledOnTouchOutside(true);
        setCancelable(true);
        tv_cencle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CarriageCount = edt_CarriageCount.getText().toString();
                String CarryingCapacity = edt_CarryingCapacity.getText().toString();
                String Length = edt_Length.getText().toString();
                String Decompress = edt_educe.getText().toString();
                String RowTime = edt_expel.getText().toString();
                String FillingTime = edt_rush.getText().toString();
                String Missing = edt_eave.getText().toString();

                if (TextUtils.isEmpty(CarriageCount)) {
                    CarriageCount = "";
                }
                if (TextUtils.isEmpty(CarryingCapacity)) {
                    CarryingCapacity = "";
                }
                if (TextUtils.isEmpty(Length)) {
                    Length = "";
                }
                if (TextUtils.isEmpty(Decompress)) {
                    Decompress = "";
                }
                if (TextUtils.isEmpty(RowTime)) {
                    RowTime = "";
                }
                if (TextUtils.isEmpty(FillingTime)) {
                    FillingTime = "";
                }
                if (TextUtils.isEmpty(Missing)) {
                    Missing = "";
                }

                if (!Length.isEmpty() && !Length.equals("")) {
                    int leng = Integer.parseInt(Length);
                    float fleng = leng;
                    fleng = fleng / 10;
                    Length = fleng + "";
                }
                click.getStr(CarriageCount, CarryingCapacity, Length, Decompress, RowTime, FillingTime, Missing);
            }
        });
    }

    private void setKeyListener(EditText editText) {
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
    }

    public interface OntvOkClick {
        public void getStr(String CarriageCount, String CarryingCapacity, String Length, String Decompress, String RowTime, String FillingTime, String Missing);
    }
}
