package tk.blankstudio.isliroutine.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by deadsec on 11/8/17.
 *
 * mapping to the retrofit responze and the internal database
 */

public class Teacher {
    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("office_hour")
    private String mOfficeHour;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("website")
    private String mWebsite;
    @SerializedName("qualification")
    private String mQualification;
    @SerializedName("experience")
    private String mExperience;
    @SerializedName("misc")
    private String mMisc;
    @SerializedName("updated_at")
    private String mupdatedAt;
    @SerializedName("created_at")
    private String mCreatedAt;

    public Teacher(int id, String name, String officeHour, String phone, String email, String website, String qualification, String experience, String misc, String mupdatedAt, String createdAt) {
        mId = id;
        mName = name;
        mOfficeHour = officeHour;
        mPhone = phone;
        mEmail = email;
        mWebsite = website;
        mQualification = qualification;
        mExperience = experience;
        mMisc = misc;
        this.mupdatedAt = mupdatedAt;
        mCreatedAt = createdAt;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }


    public String getOfficeHour() {
        return mOfficeHour;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public String getQualification() {
        return mQualification;
    }

    public String getExperience() {
        return mExperience;
    }

    public String getMisc() {
        return mMisc;
    }

    public String getUpdatedAt() {
        return mupdatedAt;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }
}
