package com.android.mygrade.data.remote.dto;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SheetResponse {
    @SerializedName("values")
    public List<List<String>> values;
}
