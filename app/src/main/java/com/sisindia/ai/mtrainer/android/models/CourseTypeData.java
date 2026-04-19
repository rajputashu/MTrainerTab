package com.sisindia.ai.mtrainer.android.models;

import org.parceler.Parcel;

@Parcel
public class CourseTypeData {
    public String CourseType;
    public int Id;

    @Override
    public String toString() {
        return "CourseTypeData{" +
                "CourseType='" + CourseType + '\'' +
                ", Id=" + Id +
                '}';
    }
}
