
package com.zofers.zofers.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.zofers.zofers.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mr Nersesyan on 25/08/2018.
 */

public class Offer implements Parcelable {
    public static final String DOC_NAME = "offer";

    public static final int COST_MODE_CREATOR = 0;
    public static final int COST_MODE_BOTH = 1;
    public static final int COST_MODE_GUEST = 2;

    public static final int GENDER_NOT_SELECTED = 0;
    public static final int GENDER_FOR_MALE = 1;
    public static final int GENDER_FOR_FEMALE = 2;
    public static final int GENDER_FOR_CUSTOM = 3;
    public static final int GENDER_NO_MATTER = 4;

    private String id;
    private String country; //req 1
    private String countryCode; //req 1
    private String city; //req 1
    private String name; //req 2
    private String description; //req 2
    private String imageUrl; //req 2
    private int costMode; //req 1
    private int cost; //req 1 // 0 if paymentMode == PAYMENT_MODE_CREATOR
    @Nullable
    private String currency;
    private int peopleCount; // opt 3
    private int gender; // opt 3 // male female custom
    private String requirements; // opt 3
    private String availability; // opt 3

    private String userID;
    private int bookCount;
    private float rating; //don't need yet
    private int rateCount;
    private int viewCount;
    private Date creationDate;
    private List<String> interestedUsers;
    private List<String> approvedUsers;

    public Offer() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
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

    @Nullable
    public String getRequirements() {
        return requirements;
    }
    @Nullable
    public String getAvailability() {
        return availability;
    }

    public int getGender() {
        return gender;
    }

    public String getUserID() {
        return userID;
    }

    public int getBookCount() {
        return bookCount;
    }

    @Nullable
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCurrency(@Nullable String currency) {
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

    public void setUserID(String userID) {
        this.userID = userID;
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

    public List<String> getInterestedUsers() {
        return interestedUsers;
    }

    public void setInterestedUsers(List<String> interestedUsers) {
        this.interestedUsers = interestedUsers;
    }

    public List<String> getApprovedUsers() {
        return approvedUsers;
    }

    public void setApprovedUsers(List<String> approvedUsers) {
        this.approvedUsers = approvedUsers;
    }

    public String getCostText(Context context) {
        if (currency == null) {
            currency = "$";
        }
        switch (costMode) {
            case COST_MODE_GUEST:
                return context.getString(R.string.cost_mode_full, cost + currency);
            case COST_MODE_BOTH:
                return context.getString(R.string.cost_mode_part, cost + currency);
            default:// COST_MODE_CREATOR:
                return context.getString(R.string.cost_mode_free);
        }
    }


    protected Offer(Parcel in) {
        id = in.readString();
        country = in.readString();
        city = in.readString();
        name = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        costMode = in.readInt();
        cost = in.readInt();
        currency = in.readString();
        peopleCount = in.readInt();
        gender = in.readInt();
        requirements = in.readString();
        availability = in.readString();
        userID = in.readString();
        bookCount = in.readInt();
        rating = in.readFloat();
        rateCount = in.readInt();
        viewCount = in.readInt();
        if (in.readByte() == 0x01) {
            interestedUsers = new ArrayList<>();
            in.readList(interestedUsers, String.class.getClassLoader());
        } else {
            interestedUsers = null;
        }
        if (in.readByte() == 0x01) {
            approvedUsers = new ArrayList<String>();
            in.readList(approvedUsers, String.class.getClassLoader());
        } else {
            approvedUsers = null;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Offer) {
            return ((Offer) obj).id.equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(country);
        dest.writeString(city);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeInt(costMode);
        dest.writeInt(cost);
        dest.writeString(currency);
        dest.writeInt(peopleCount);
        dest.writeInt(gender);
        dest.writeString(requirements);
        dest.writeString(availability);
        dest.writeString(userID);
        dest.writeInt(bookCount);
        dest.writeFloat(rating);
        dest.writeInt(rateCount);
        dest.writeInt(viewCount);
        if (interestedUsers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(interestedUsers);
        }
        if (approvedUsers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(approvedUsers);
        }
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

    public int getPeopleTextResource() {
        if (gender == GENDER_FOR_FEMALE) {
            return R.plurals.for_woman;
        } else if (gender == GENDER_FOR_MALE) {
            return R.plurals.for_men;
        } else return R.plurals.for_person;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}