package cn.renly.aleiaddress.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.renly.aleiaddress.App;
import cn.renly.aleiaddress.R;
import cn.renly.aleiaddress.api.bean.Contact;
import cn.renly.aleiaddress.widget.CircleImageView;

public class ContactAdapter extends BaseAdapter {
    private List<?> list;
    private Context mContext;

    public ContactAdapter(Context context, List<?> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    protected BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_people, parent, false);
        ContactViewHolder viewHolder = new ContactViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ContactViewHolder extends BaseViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.phone)
        TextView phone;
        @BindView(R.id.avatar)
        CircleImageView avatar;

        ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void setData(int pos) {
            if (list.get(pos) instanceof Contact) {
                Contact contact = (Contact) list.get(pos);
                name.setText(contact.getName());
                phone.setText(contact.getPhoneNum());

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
            }
        }
    }
}
