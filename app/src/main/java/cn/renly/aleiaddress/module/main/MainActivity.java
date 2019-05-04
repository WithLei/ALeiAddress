package cn.renly.aleiaddress.module.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.renly.aleiaddress.R;
import cn.renly.aleiaddress.adapter.ContactAdapter;
import cn.renly.aleiaddress.api.bean.Contact;
import cn.renly.aleiaddress.module.base.BaseActivity;
import cn.renly.aleiaddress.utils.LogUtils;
import cn.renly.aleiaddress.utils.PhoneUtils;
import cn.renly.aleiaddress.widget.RecycleViewDivider;

public class MainActivity extends BaseActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<Contact>contactList;
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
                this, LinearLayoutManager.VERTICAL, 20, getResources().getColor(R.color.colorDivider)));
        // 调整draw缓存,加速recyclerview加载
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    private void initAdapter() {
        printLog("size == " + contactList.size());
        adapter = new ContactAdapter(this, contactList);
        recyclerView.setAdapter(adapter);
    }

    private void initContactList() {
        contactList = PhoneUtils.getPhoneContacts();
        for (Contact contact : contactList) {
            LogUtils.printLog(contact.toString());
        }
    }
}
