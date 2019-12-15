package com.mak.classportal.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mak.classportal.AppController;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;


import static com.android.volley.VolleyLog.TAG;

/**
 * Created by ambrosial on 15/6/17.
 */

public class ExecuteAPI {

    Context context;
    String requestUrl, requestParam;
    ProgressDialog pDialog;
    String responseString = "";
    OnTaskCompleted onTaskCompleted;
    int requestCode;
    JSONObject jsonObject;
    int mStatusCode = 0;
    NetworkResponse networkresponse;
    Map<String, String> params;
    HashMap<String, String> headers;

    public ExecuteAPI(Context context, String requestUrl, JSONObject jsonObject) {
        this.context = context;
        this.jsonObject = jsonObject;
        this.requestUrl = requestUrl;
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        headers = new HashMap<String, String>();
    }

    public void showProcessBar(boolean isShowPrcess) {
        if (isShowPrcess)
            pDialog.show();
    }

    public void executeCallback(OnTaskCompleted callbackClass) {
        onTaskCompleted = callbackClass;

    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);

    }

    public String execute(int METHOD) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                METHOD, requestUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString());
                        try {

                            response.put("code", mStatusCode);
                            Log.e("response", "response" + response.toString());
                            if (pDialog != null)
                                pDialog.dismiss();
                            onTaskCompleted.onResponse(response);
                            // msgResponse.setText(response.toString());

                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (networkresponse != null) {
                    if (networkresponse.headers.get("Content-Type").contains("html")) {
                        onTaskCompleted.onErrorResponse(error, mStatusCode, null);
                        if (pDialog != null)
                            pDialog.dismiss();
                    } else {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        System.out.println("Status code " + mStatusCode);
                        if (pDialog != null)
                            pDialog.dismiss();
                        if (networkresponse != null)
                            mStatusCode = networkresponse.statusCode;
                        if (mStatusCode == 0) {
                            onTaskCompleted.onErrorResponse(error, mStatusCode, null);
                        } else {
                            try {
                                if (networkresponse != null && networkresponse.data != null) {
                                    String jsonError = new String(networkresponse.data);
                                    Object json = new JSONTokener(jsonError).nextValue();
                                    if (jsonError.length() > 0) {
                                        if (json instanceof JSONArray) {
                                            JSONArray array=new JSONArray(jsonError);
                                            JSONObject jsonObject=new JSONObject();
                                            jsonObject.put("code", mStatusCode);
                                            jsonObject.put("data", array);
                                            onTaskCompleted.onResponse(jsonObject);
                                        } else if (!jsonError.equalsIgnoreCase("null")) {
                                            JSONObject jsonObject = new JSONObject(jsonError);
                                            jsonObject.put("code", mStatusCode);
                                            onTaskCompleted.onErrorResponse(error, mStatusCode, jsonObject);
                                        } else {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("code", mStatusCode);
                                            onTaskCompleted.onErrorResponse(error, mStatusCode, jsonObject);
                                        }

                                    } else {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("code", mStatusCode);
                                        onTaskCompleted.onResponse(jsonObject);
                                    }


                                    Log.d("Server Error", jsonError);
                                    // Print Error!
                                } else {

                                }

                            } catch (Exception e) {


                            }
                        }
                    }
                }
            }
        })


        {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                networkresponse = response;
                return super.parseNetworkResponse(response);

            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                    if (pDialog != null)
                        pDialog.dismiss();
                    mStatusCode = volleyError.networkResponse.statusCode;
                    networkresponse = volleyError.networkResponse;

                } else {
                    if (pDialog != null)
                        pDialog.dismiss();
                    JSONObject jsonObject = new JSONObject();
                    onTaskCompleted.onErrorResponse(volleyError, mStatusCode, jsonObject);
                }
                return super.parseNetworkError(volleyError);
            }

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
       /* jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(mRetryPolicy);
        AppController.getInstance().addToRequestQueue(jsonObjReq);

        return "";
    }


    public interface OnTaskCompleted {
        void onResponse(JSONObject result);
        void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse);
    }

}

