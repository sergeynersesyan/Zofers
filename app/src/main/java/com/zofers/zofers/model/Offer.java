
package com.zofers.zofers.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.zofers.zofers.R;

/**
 * Created by Mr Nersesyan on 25/08/2018.
 */

public class Offer implements Parcelable {

    public static final int COST_MODE_CREATOR = 0;
    public static final int COST_MODE_BOTH = 1;
    public static final int COST_MODE_GUEST = 2;

    public static final int GENDER_NOT_SELECTED = 0;
    public static final int GENDER_FOR_MALE = 1;
    public static final int GENDER_FOR_FEMALE = 2;
    public static final int GENDER_FOR_CUSTOM = 3;
    public static final int GENDER_NO_MATTER = 4;

    private String country; //req 1
    private String city; //req 1
    private String title; //req 2
    private String description; //req 2
    private String imageUrl; //req 2
    private int costMode; //req 1
    private int cost; //req 1 // 0 if paymentMode == PAYMENT_MODE_CREATOR
    private String currency;
    private int peopleCount; // opt 3
    private int gender; // opt 3 // male female custom
    private String requirements; // opt 3
    private String availability; // opt 3

    private int userId;
    private int bookCount;
    private float rating; //don't need yet
    private int rateCount;
    private int viewCount;

    public Offer() {}


    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCostMode() {
        return costMode;
    }

    public int getCost() {
        return cost;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getAvailability() {
        return availability;
    }

    public int getGender() {
        return gender;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookCount() {
        return bookCount;
    }

    public String getCurrency() {
        return currency;
    }

    public float getRating() {
        return rating;
    }

    public int getRateCount() {
        return rateCount;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCostMode(int costMode) {
        this.costMode = costMode;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }

    public String getCostText(Context context) {
        switch (costMode) {
            case COST_MODE_GUEST:
                return context.getString(R.string.cost_mode_attender);
            case COST_MODE_BOTH:
                return context.getString(R.string.cost_mode_both);
            default:// COST_MODE_CREATOR:
                return context.getString(R.string.cost_mode_creator);
        }
    }

    protected Offer(Parcel in) {
        country = in.readString();
        city = in.readString();
        title = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        costMode = in.readInt();
        cost = in.readInt();
        currency = in.readString();
        peopleCount = in.readInt();
        gender = in.readInt();
        requirements = in.readString();
        availability = in.readString();
        userId = in.readInt();
        bookCount = in.readInt();
        rating = in.readFloat();
        rateCount = in.readInt();
        viewCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(city);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeInt(costMode);
        dest.writeInt(cost);
        dest.writeString(currency);
        dest.writeInt(peopleCount);
        dest.writeInt(gender);
        dest.writeString(requirements);
        dest.writeString(availability);
        dest.writeInt(userId);
        dest.writeInt(bookCount);
        dest.writeFloat(rating);
        dest.writeInt(rateCount);
        dest.writeInt(viewCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Offer> CREATOR = new Parcelable.Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };
}