package cn.renly.aleiaddress.module.main;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.renly.aleiaddress.App;
import cn.renly.aleiaddress.R;
import cn.renly.aleiaddress.adapter.ContactAdapter;
import cn.renly.aleiaddress.api.bean.Contact;
import cn.renly.aleiaddress.listener.ItemClickListener;
import cn.renly.aleiaddress.module.base.BaseActivity;
import cn.renly.aleiaddress.utils.IntentUtils;
import cn.renly.aleiaddress.utils.LogUtils;
import cn.renly.aleiaddress.utils.PhoneUtils;
import cn.renly.aleiaddress.utils.toast.ToastUtils;
import cn.renly.aleiaddress.widget.CircleImageView;
import cn.renly.aleiaddress.widget.RecycleViewDivider;

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
                this, LinearLayoutManager.VERTICAL, 20, getResources().getColor(R.color.colorDividerDark)));
        // 调整draw缓存,加速recyclerview加载
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    private void initAdapter() {
        printLog("size == " + contactList.size());
        adapter = new ContactAdapter(this, contactList);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                initDialogView(pos);
                new AlertDialog.Builder(MainActivity.this,R.style.dialog)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create()
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private View dialogView;
    private TextView name,phone;
    private LinearLayout shareBtn,editBtn,deleteBtn,moreBtn;
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
        Bitmap contactPhoto = null;
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

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditBtnClicked(pos);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteBtnClicked(pos);
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoreBtnClicked(pos);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallClicked(pos);
            }
        });
    }

    private void onCallClicked(int pos) {
        IntentUtils.callPhone(this, contactList.get(pos).getPhoneNum());
    }

    private void onMoreBtnClicked(int pos) {
        ToastShort("正在施工中www");
    }

    private void onDeleteBtnClicked(int pos) {
    }

    private void onEditBtnClicked(int pos) {
    }

    private void onShareBtnClicked(int pos) {
        IntentUtils.sharePhone(this, contactList.get(pos).toString());
    }

    private void initContactList() {
        contactList = PhoneUtils.getPhoneContacts();
        for (Contact contact : contactList) {
            LogUtils.printLog(contact.toString());
        }
    }

    @OnClick({R.id.action_a, R.id.action_b, R.id.action_c})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_a:
                ToastShort("编辑联系人");
                break;
            case R.id.action_b:
                ToastShort("添加联系人");
                break;
            case R.id.action_c:
                ToastShort("删除联系人");
                break;
        }
    }
}
