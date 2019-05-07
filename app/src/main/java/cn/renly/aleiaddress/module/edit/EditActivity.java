package cn.renly.aleiaddress.module.edit;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.renly.aleiaddress.App;
import cn.renly.aleiaddress.R;
import cn.renly.aleiaddress.api.bean.Contact;
import cn.renly.aleiaddress.module.base.BaseActivity;
import cn.renly.aleiaddress.utils.PhoneUtils;
import cn.renly.aleiaddress.utils.toast.MyToast;
import cn.renly.aleiaddress.utils.toast.ToastUtils;
import cn.renly.aleiaddress.widget.CircleImageView;
import rx.Observable;

/**
 * @author Renly
 */
public class EditActivity extends BaseActivity {
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.title)
    TextView title;

    private Contact contact;
    public static final String NAME_IMG_AVATAR = "imgAvatar";
    public static final int REQUEST_CODE = 2333;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_edit;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        contact = JSON.parseObject(intent.getStringExtra("contact"), Contact.class);
    }

    @Override
    protected void initView() {
        title.setText("编辑联系人");
        ViewCompat.setTransitionName(avatar, NAME_IMG_AVATAR);
        //得到联系人头像Bitamp
        Bitmap contactPhoto = null;
        //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
        if (contact.getPhotoId() > 0) {
            ContentResolver resolver = App.getContext().getContentResolver();
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact.getId());
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
            contactPhoto = BitmapFactory.decodeStream(input);
            avatar.setImageBitmap(contactPhoto);
        }
        name.setText(contact.getName());
        name.setSelection(contact.getName().length());
        phone.setText(contact.getPhoneNum());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private boolean isChanged() {
        return !phone.getText().toString().equals(contact.getPhoneNum()) || !name.getText().toString().equals(contact.getName());
    }

    @OnClick({R.id.back, R.id.done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (isChanged()) {
                    new AlertDialog.Builder(this)
                            .setMessage("还有未保存的信息，确认离开吗？")
                            .setPositiveButton("确认", (dialogInterface, i) -> {
                                this.finish();
                            })
                            .setNegativeButton("取消", (dialogInterface, i) -> {
                                //啥也不做
                            })
                            .setCancelable(true)
                            .create()
                            .show();
                } else {
                    this.finish();
                }
                break;
            case R.id.done:
                if (doChangeContact()) {
                    contact.setName(name.getText().toString());
                    contact.setPhoneNum(phone.getText().toString());
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

    private boolean doChangeContact() {
        //修改数据库信息
        contact.setName(name.getText().toString().trim());
        contact.setPhoneNum(phone.getText().toString().trim());
        return PhoneUtils.updateContact(contact);
    }
}
