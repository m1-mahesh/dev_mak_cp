package com.mak.classportal.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
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
    public void downloadFileRequest(){
        final InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, requestUrl,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // TODO handle the response
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        try {
                            if (response!=null) {

                                //Read file name from headers (We have configured API to send file name in "Content-Disposition" header in following format: "File-Name:File-Format" example "MyDoc:pdf"

                                String filename = "downloadd";
                                filename = filename.replace(":", ".");
                                Log.d("DEBUG::FILE NAME", filename);

                                try{
                                    long lenghtOfFile = response.length;

                                    //covert reponse to input stream
                                    InputStream input = new ByteArrayInputStream(response);

                                    //Create a file on desired path and write stream data to it
                                    File path = Environment.getExternalStorageDirectory();
                                    File file = new File(path, filename);
                                    map.put("resume_path", file.toString());
                                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                                    byte data[] = new byte[1024];

                                    long total = 0;

                                    int count;
                                    while ((count = input.read(data)) != -1) {
                                        total += count;
                                        output.write(data, 0, count);
                                    }

                                    output.flush();

                                    output.close();
                                    input.close();
                                }catch(IOException e){
                                    e.printStackTrace();

                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                            e.printStackTrace();
                        }
                    }
                } ,new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                error.printStackTrace();
            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context, new HurlStack());
        mRequestQueue.add(request);
    }


    public interface OnTaskCompleted {
        void onResponse(JSONObject result);

        void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse);
    }

}

