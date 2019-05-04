package cn.renly.aleiaddress.api.bean;

public class Contact {
    private String name;
    private String phoneNum;
    private Long id;
    private Long photoId;

    public Contact(String name, String phoneNum, Long id, Long photoId) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.id = id;
        this.photoId = photoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    @Override
    public String toString() {
        return "[name:" + name +
                " phoneNum:" + phoneNum +
                " id:" + id +
                " photoId:" + photoId +
                "]";
    }
}
