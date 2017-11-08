package com.example.deadsec.isliroutine.model;

/**
 * Created by deadsec on 11/8/17.
 */

public class Teacher {
    private int mId;
    private String mFirstName;
    private String mLastName;
    private String mOfficeHour;
    private String mPhone;
    private String mEmail;
    private String mWebsite;
    private String mQualification;
    private String mExperience;
    private String mMisc;
    private String mupdatedAt;
    private String mCreatedAt;

    public Teacher(int id, String firstName, String lastName, String officeHour, String phone, String email, String website, String qualification, String experience, String misc, String mupdatedAt, String createdAt) {
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
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

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
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
