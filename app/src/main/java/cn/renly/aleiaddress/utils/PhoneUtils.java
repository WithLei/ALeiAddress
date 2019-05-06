package cn.renly.aleiaddress.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.renly.aleiaddress.App;
import cn.renly.aleiaddress.R;
import cn.renly.aleiaddress.api.bean.Contact;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class PhoneUtils {
    /**
     * 获取库Phon表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;


    /**
     * 联系人名称
     **/
    private ArrayList<String> mContactsName = new ArrayList<String>();

    /**
     * 联系人头像
     **/
    private ArrayList<String> mContactsNumber = new ArrayList<String>();

    /**
     * 联系人头像
     **/
    private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();
    static final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    /**
     * 获取通讯录联系人
     *
     * @param context
     * @return
     */
    public static List<Contact> getPhoneContacts(final Activity context) {
        int hasWriteContactsPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWriteContactsPermission = App.getContext().checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
        }
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(context, new String[]{Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return null;
        }
        List<Contact> contactList = new ArrayList<>();
        ContentResolver resolver = App.getContext().getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

                contactList.add(new Contact(contactName, phoneNumber, contactid, photoid));

                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;

                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if (photoid > 0) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                } else {
                    contactPhoto = null;
                }
            }
            phoneCursor.close();
        }
        return contactList;
    }

    /**
     * 新建联系人
     * @param contact
     */
    public static void addContact(Contact contact) {
        ContentValues values = new ContentValues();
        Uri uri = App.getContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long contact_id = ContentUris.parseId(uri);

        //插入data表
        uri = ContactsContract.Data.CONTENT_URI;
        String raw_contact_id = ContactsContract.Data.RAW_CONTACT_ID;
        String data2 = ContactsContract.Data.DATA2;
        String data1 = ContactsContract.Data.DATA1;
        //add Name
        values.put(raw_contact_id, contact_id);
        values.put(ContactsContract.CommonDataKinds.Phone.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(data2, contact.getName());
        values.put(data1, contact.getName());
        App.getContext().getContentResolver().insert(uri, values);
        values.clear();
        //add Phone
        values.put(raw_contact_id, contact_id);
        values.put(ContactsContract.CommonDataKinds.Phone.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(data2, contact.getPhoneNum());
        values.put(data1, contact.getPhoneNum());
        App.getContext().getContentResolver().insert(uri, values);
        values.clear();
    }

    /**
     * 修改联系人
     * @param contact
     */
    public static boolean updateContact(Contact contact) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        ContentResolver resolver = App.getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.DATA1, contact.getPhoneNum());
        int result = resolver.update(uri, values, ContactsContract.Data.MIMETYPE + "=? and " + ContactsContract.PhoneLookup.DISPLAY_NAME + "=?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, contact.getName()});
        return result > 0;
    }

    /**
     * 删除联系人
     * @param contact
     */
    public static void deleteContact(Contact contact) {
        //根据姓名求id
        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
        ContentResolver resolver = App.getContext().getContentResolver();
        String where = ContactsContract.PhoneLookup.DISPLAY_NAME;
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID},
                where + "=?", new String[]{contact.getName()}, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            //根据id删除data中的相应数据
            resolver.delete(uri, where + "=?", new String[]{contact.getName()});
            uri = ContactsContract.Data.CONTENT_URI;
            resolver.delete(uri, ContactsContract.Data.RAW_CONTACT_ID + "=?",
                    new String[]{id + ""});
        }
    }
}
