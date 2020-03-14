package com.mak.classportal.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mak.classportal.AppController;
import com.mak.classportal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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
        headers = new HashMap<>();
        params = new HashMap<>();
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

    public void addPostParam(final String name, final String value) {
        params.put(name, value);

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
                                            JSONArray array = new JSONArray(jsonError);
                                            JSONObject jsonObject = new JSONObject();
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
        }) {

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

    public String executeStringRequest(int METHOD) {
        StringRequest stringRequest = new StringRequest(METHOD, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject object = new JSONObject(response);
                            object.put("code", mStatusCode);
                            Log.e("response", "response" + response);
                            if (pDialog != null)
                                pDialog.dismiss();
                            onTaskCompleted.onResponse(object);
                            // msgResponse.setText(response.toString());

                        } catch (Exception e) {
                            if (pDialog != null)
                                pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response", error.toString());
                        if (pDialog != null)
                            pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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
        stringRequest.setRetryPolicy(mRetryPolicy);
        AppController.getInstance().addToRequestQueue(stringRequest);

        return "";
    }
    String contentType = "";
    String fileName = "";
    Dialog dialog1;
    public void setContentType(String contentType){
        this.contentType = contentType;
    }
    public void setFileName(String fileName){
        this.fileName = fileName;
    }
    public void executeMultiPartRequest(int METHOD, final Bitmap[] bitmapArray, final String attribute, InputStream pdfIStream) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(METHOD, requestUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            if (response!=null) {
                                JSONObject obj = new JSONObject(new String(response.data));
                                if (obj.getString("error_code").contains("200"))
                                    obj.put("code", 200);
                                Log.e("response", "response" + response.toString());

                                onTaskCompleted.onResponse(obj);
                            }
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if (error!=null && error.networkResponse!=null) {
                                JSONObject obj = new JSONObject(new String(error.networkResponse.data));
                                onTaskCompleted.onErrorResponse(error, 400, obj);
                            }
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                headers.put("Content-Type", "application/json");
                return headers;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, VolleyMultipartRequest.DataPart[]> getByteData() {

                Map<String, DataPart[]> params = new HashMap<>();
                if (contentType.equals(Constant.CONTENT_TYPE_IMAGE)&& bitmapArray!=null) {
                    DataPart[] dataParts = new DataPart[bitmapArray.length];
                    if (bitmapArray.length > 0) {
                        long imageName = System.currentTimeMillis();
                        for (int i = 0; i < bitmapArray.length; i++) {
                            Bitmap bitmap = bitmapArray[i];
                            dataParts[i] = new DataPart(imageName + ".png", getFileDataFromDrawable(bitmap));
                        }
                        params.put(attribute, dataParts);
                    }
                }else if (contentType.equals(Constant.CONTENT_TYPE_PDF_DOC)&& pdfIStream!=null){
                    DataPart[] dataParts = new DataPart[1];
                    try {
                        dataParts[0] = new DataPart(fileName , getBytes(pdfIStream));
                        params.put(attribute, dataParts);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogStyle));
                    builder.setTitle("Error");
                    builder.setMessage("Invalid File");
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return params;
            }
        };

        //adding the request to volley
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        volleyMultipartRequest.setRetryPolicy(mRetryPolicy);
        volleyMultipartRequest.setShouldCache(false);
        RequestQueue rQueue;
        rQueue = Volley.newRequestQueue(context);
        rQueue.add(volleyMultipartRequest);

    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public interface OnTaskCompleted {
        void onResponse(JSONObject result);

        void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse);
    }

}

