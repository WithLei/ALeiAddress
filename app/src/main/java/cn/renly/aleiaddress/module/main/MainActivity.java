package cn.renly.aleiaddress.module.main;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.renly.aleiaddress.App;
import cn.renly.aleiaddress.R;
import cn.renly.aleiaddress.adapter.ContactAdapter;
import cn.renly.aleiaddress.api.RetrofitService;
import cn.renly.aleiaddress.api.bean.Contact;
import cn.renly.aleiaddress.module.add.AddActivity;
import cn.renly.aleiaddress.module.base.BaseActivity;
import cn.renly.aleiaddress.module.edit.EditActivity;
import cn.renly.aleiaddress.utils.IntentUtils;
import cn.renly.aleiaddress.utils.LogUtils;
import cn.renly.aleiaddress.utils.PhoneUtils;
import cn.renly.aleiaddress.utils.RunntimePermissionHelper;
import cn.renly.aleiaddress.widget.CircleImageView;
import cn.renly.aleiaddress.widget.RecycleViewDivider;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import rx.Observable;

/**
 * @author Renly
 */
public class MainActivity extends BaseActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ActionMenu)
    FloatingActionsMenu actionsMenu;

    private List<Contact> contactList;
    private ContactAdapter adapter;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        initContactList();
        initAdapter();
    }

    @Override
    protected void initView() {
        initToolBar(false, "ALei通讯录");
        initRecyclerView();
    }

    /**
     * 初始化recylerView的一些属性
     */
    protected RecyclerView.LayoutManager mLayoutManager;

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, 20, getResources().getColor(R.color.blue_light)));
        // 调整draw缓存,加速recyclerview加载
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    AlertDialog dialog;
    private void initAdapter() {
        adapter = new ContactAdapter(this, contactList);
        adapter.setOnItemClickListener((v, pos) -> {
            initDialogView(pos);
            dialog = new AlertDialog.Builder(MainActivity.this, R.style.dialog)
                    .setView(dialogView)
                    .setCancelable(true)
                    .show();
        });
        recyclerView.setAdapter(adapter);
    }

    private View dialogView;
    private TextView name, phone;
    private LinearLayout shareBtn, editBtn, deleteBtn, moreBtn;
    private Button call;
    private CircleImageView avatar;

    private void initDialogView(int pos) {
        dialogView = View.inflate(this, R.layout.layout_dialog, null);
        name = dialogView.findViewById(R.id.name);
        phone = dialogView.findViewById(R.id.phone);
        shareBtn = dialogView.findViewById(R.id.share_btn);
        editBtn = dialogView.findViewById(R.id.edit_btn);
        deleteBtn = dialogView.findViewById(R.id.delete_btn);
        moreBtn = dialogView.findViewById(R.id.more_btn);
        call = dialogView.findViewById(R.id.call);
        avatar = dialogView.findViewById(R.id.avatar);

        Contact contact = contactList.get(pos);
        name.setText(contact.getName());
        //得到联系人头像Bitamp
        Bitmap contactPhoto;
        //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
        if (contact.getPhotoId() > 0) {
            ContentResolver resolver = App.getContext().getContentResolver();
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact.getId());
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
            contactPhoto = BitmapFactory.decodeStream(input);
            avatar.setImageBitmap(contactPhoto);
        }
        phone.setText(contact.getPhoneNum());

        // setListener
        shareBtn.setOnClickListener(view -> onShareBtnClicked(pos));

        editBtn.setOnClickListener(view -> onEditBtnClicked(pos));

        deleteBtn.setOnClickListener(view -> onDeleteBtnClicked(pos));

        moreBtn.setOnClickListener(view -> onMoreBtnClicked(pos));

        call.setOnClickListener(view -> onCallClicked(pos));
    }

    private void onCallClicked(int pos) {
        IntentUtils.callPhone(this, contactList.get(pos).getPhoneNum());
    }

    private void onMoreBtnClicked(int pos) {
        ToastShort("正在施工中www");
    }

    private void onDeleteBtnClicked(int pos) {
        new AlertDialog.Builder(this)
                .setMessage("确认删除该联系人吗？")
                .setPositiveButton("确认", (dialogInterface, i) -> {
                    doDeleteContact(pos);
                    if (dialog != null) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(true)
                .create()
                .show();
    }

    private void onEditBtnClicked(int pos) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("contact", JSON.toJSONString(contactList.get(pos)));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, avatar, EditActivity.NAME_IMG_AVATAR);
        ActivityCompat.startActivityForResult(this, intent,
                EditActivity.REQUEST_CODE, options.toBundle());
        Observable.timer(1, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                    if (dialog != null) {
                        dialog.cancel();
                    }
                });
    }

    private void onShareBtnClicked(int pos) {
        IntentUtils.sharePhone(this, contactList.get(pos).toString());
    }

    private void initContactList() {
        if (!checkAndRequestPermission())return;
        contactList = PhoneUtils.getPhoneContacts(this);
        if (contactList == null) {
            return;
        }
        for (Contact contact : contactList) {
            LogUtils.printLog(contact.toString());
        }
    }

    private void doDeleteContact(int pos) {
        //删除联系人
        PhoneUtils.deleteContact(contactList.get(pos));
        contactList.remove(pos);
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.action_a, R.id.action_b, R.id.action_c, R.id.action_d})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_a:
                ToastShort("编辑联系人");
                break;
            case R.id.action_b:
                startActivityForResult(new Intent(this, AddActivity.class),
                        AddActivity.REQUEST_CODE);
                break;
            case R.id.action_c:
                ToastShort("删除联系人");
                break;
            case R.id.action_d:
                ToastShort("上传成功！");

//                RetrofitService.doUpload(contactList)
//                        .subscribe(responseBody -> {
//                            ToastShort("上传成功！");
//                            printLog(responseBody.string());
//                        }, throwable -> {
//                            ToastShort("上传失败！");
//                            printLog(throwable.getMessage());
//                        });
                break;
            default:
                break;
        }
        actionsMenu.collapse();
    }

    private long mExitTime;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            if ((System.currentTimeMillis() - mExitTime) > 1500) {
                ToastShort("再按一次退出客户端(｡･ω･｡)~~");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyBoard();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        hideKeyBoard();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AddActivity.REQUEST_CODE:
                    initContactList();
                    initAdapter();
                    break;
                case EditActivity.REQUEST_CODE:
                    initContactList();
                    initAdapter();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean checkAndRequestPermission() {
        return RunntimePermissionHelper.checkAndRequestForRunntimePermission(
                this, new String[] { Manifest.permission.READ_CONTACTS });
    }
}
