package com.android.mygrade.data.remote;


import com.android.mygrade.data.remote.dto.SheetResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class GoogleSheetsApi {
    @GET("spreadsheets/d/{spreadsheetId}/values/{range}")
    Call<SheetResponse> getCell(
            @Path("spreadsheetId") String spreadsheetId,
            @Path("range") String range,
            @Query("key") String apiKey
    );
}
