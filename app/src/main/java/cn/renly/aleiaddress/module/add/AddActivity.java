package cn.renly.aleiaddress.module.add;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.renly.aleiaddress.R;
import cn.renly.aleiaddress.api.bean.Contact;
import cn.renly.aleiaddress.module.base.BaseActivity;
import cn.renly.aleiaddress.utils.PhoneUtils;
import cn.renly.aleiaddress.utils.toast.MyToast;
import cn.renly.aleiaddress.widget.CircleImageView;
import rx.Observable;
import rx.functions.Action1;

/**
 * @author Renly
 */
public class AddActivity extends BaseActivity {
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.title)
    TextView title;

    private Contact contact;
    public static final int REQUEST_CODE = 10086;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_add;
    }

    @Override
    protected void initData() {
        contact = new Contact();
    }

    @Override
    protected void initView() {
        title.setText("新增联系人");
    }

    @OnClick({R.id.back, R.id.done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (isChanged()) {
                    new AlertDialog.Builder(this)
                            .setMessage("还有未保存的信息，确认离开吗？")
                            .setPositiveButton("确认", (dialogInterface, i) -> {
                                finish();
                            })
                            .setNegativeButton("取消", (dialogInterface, i) -> {
                                //啥也不做
                            })
                            .setCancelable(true)
                            .create()
                            .show();
                } else {
                    finish();
                }
                break;
            case R.id.done:
                if (doAddContact()) {
                    MyToast.showText(this, "保存成功", true);
                    Observable.timer(1, TimeUnit.MILLISECONDS)
                            .subscribe(aLong -> {
                                setResult(RESULT_OK);
                                finishActivity();
                            });
                } else {
                    MyToast.showText(this, "保存失败", true);
                }
                break;
            default:
                break;
        }
    }

    private boolean doAddContact() {
        //增加联系人
        contact.setName(name.getText().toString());
        contact.setPhoneNum(phone.getText().toString());
        PhoneUtils.addContact(contact);
        return true;
    }

    private boolean isChanged() {
        return !TextUtils.isEmpty(name.getText().toString()) &&
                !TextUtils.isEmpty(phone.getText().toString());
    }


}
