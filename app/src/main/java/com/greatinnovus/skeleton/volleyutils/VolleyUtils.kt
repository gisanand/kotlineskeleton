package com.greatinnovus.promotionapp.volleyutils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kotlinsample.volleyutils.VolleySingleton
import com.google.gson.Gson
import com.greatinnovus.promotionapp.constants.StringConstants
import com.greatinnovus.promotionapp.utils.CommonUtils
import com.greatinnovus.skeleton.R
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.text.Charsets.UTF_8


class VolleyUtils {
    lateinit var volleyInterface: VolleyInterface
    var request: Int = -1
    var paramsobj: JSONObject? = null;

    //1=post method 2=Get Method, 3=Put Method, 4 =Delete Method
    public fun <T> callnetworkrequest(
        context: Context,
        listener: VolleyInterface,
        getorpost: Int,
        url: String,
        params: HashMap<String, Any>?,
        TAG: String,
        requestcode: Int,
        senttoken: Int,
        model: Class<T>?,
        progress: Boolean,
        notshowerror: Boolean
    ) {

        try {
            if(!CommonUtils.isNetworkAvailable(context))
            {
                CommonUtils.showNoNetworkDialog(context)
                return;
            }

            if (progress) {
                CommonUtils.setProgressBar(context);
            }
            CommonUtils.log("apiUrl:", "  $url");


            val shared = context.applicationContext.getSharedPreferences(
                StringConstants.APP_PREFS_ACCOUNT,
                0
            );
            val token = shared.getString(StringConstants.PREF_TOKEN, "");
            CommonUtils.log(TAG, " SHare toekn " + token);
            volleyInterface = listener

            if (getorpost == 1) {
                request = Request.Method.POST
            } else if (getorpost == 3) {
                request = Request.Method.PUT
            } else if (getorpost == 4) {
                request = Request.Method.DELETE
            } else {
                request = Request.Method.GET
            }

            if (params != null) {
                paramsobj = JSONObject(params as Map<*, *>)
                CommonUtils.log("ReqestData:", "  ${paramsobj.toString()}");

            }


            val jsonObjReq = object : JsonObjectRequest(request, url, paramsobj,
                Response.Listener<JSONObject> { response ->

                    try {
                        CommonUtils.cancelProgressBar();
                        CommonUtils.log(TAG, " Main Response " + response.toString(4));

                        if (response.getString("status").equals("true",true)) {
                            if (model!!.equals(JSONObject::class.java)) {
                                CommonUtils.log(TAG," Models are JSon OBject")
                                volleyInterface.successResponse(response, requestcode);
                            } else {
                                CommonUtils.log(TAG," Models are NOT A JSon OBject")
                                val gson = Gson()
                                val predictions: Any =gson.fromJson(response.toString(), model!!)!!
                                volleyInterface.successResponse(predictions, requestcode);


                            }


                        } else {
                            if (notshowerror) {
                                CommonUtils.AppAlert(
                                    context,
                                    context.resources.getString(R.string.alerttitle),
                                    response.getString("message")
                                );
                            }

                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        CommonUtils.cancelProgressBar();
                        if (notshowerror) {
                            CommonUtils.AppAlert(
                                context,
                                context.resources.getString(R.string.alerttitle),
                                "" + context.resources.getString(R.string.unkonwnerror)
                            )
                        }
                    }

                }, Response.ErrorListener { error ->
                    try {
                        CommonUtils.cancelProgressBar()

                        val responseBody = String(error.networkResponse.data, UTF_8)
                        val data = JSONObject(responseBody)

                        CommonUtils.log(TAG, "Checking Response $data ")
                        val networkResponse = error.networkResponse
                        if (networkResponse != null && networkResponse.statusCode == 401) {
                            // HTTP Status Code: 401 Unauthorized
                            CommonUtils.loginexpired(context)
                        } else {
                            if (notshowerror) {
                                CommonUtils.AppAlert(
                                    context,
                                    context.resources.getString(R.string.alerttitle),
                                    "" + data.getString("message")
                                )
                            }
                        }

                        volleyInterface.errorResponse(data.getString("message"), requestcode)
                    } catch (e: Exception) {
                        if (notshowerror) {
                            CommonUtils.AppAlert(
                                context,
                                context.resources.getString(R.string.alerttitle),
                                "" + context.resources.getString(R.string.unkonwnerror)
                            )
                        }
                        CommonUtils.cancelProgressBar()
                    }


                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String, String>();
                    if (senttoken == 1) {
                        header.put("Authorization", "Bearer "+token )
                    }
                    header.put("Content-Type", "application/json")
                    return header
                }


            }


            val requestQueue = Volley.newRequestQueue(context.applicationContext);
            jsonObjReq.setRetryPolicy(
                DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )

            requestQueue.add(jsonObjReq)


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    public fun ImageUpload(
        context: Context,
        listener: VolleyInterface,
        getorpost: Int,
        url: String,
        params: HashMap<String, Any>?,
        TAG: String,
        requestcode: Int,
        senttoken: Int,
        progress: Boolean,
        notshowerror: Boolean, imageparams: HashMap<String, Bitmap>?
    ) {
        if(!CommonUtils.isNetworkAvailable(context))
        {
            CommonUtils.showNoNetworkDialog(context)
            return;
        }

        volleyInterface = listener
        if (progress) {
            CommonUtils.setProgressBar(context)
        }
        CommonUtils.log("apiUrl: ", url)
        val shared =
            context.applicationContext.getSharedPreferences(StringConstants.APP_PREFS_ACCOUNT, 0)
        val token = shared.getString(StringConstants.PREF_TOKEN, "")
        CommonUtils.log(TAG, " SHare toekn " + token!!)
        if (params != null) {

            val jsonObject = JSONObject(params as Map<*, *>)
            CommonUtils.log(TAG, "  Json Respone $jsonObject")
        }
        var request = Request.Method.POST
        if (getorpost == 1) {
            request = Request.Method.POST
        } else if (getorpost == 3) {
            request = Request.Method.PUT
        }

        val multipartRequest = object :
            VolleyMultipartRequest(request, url, object : Response.Listener<NetworkResponse> {
                override fun onResponse(response: NetworkResponse?) {

                    CommonUtils.log(TAG, Arrays.toString(response!!.data))
                    CommonUtils.cancelProgressBar()

                    val resultResponse = String(response!!.data)
                    Log.i(TAG, "onResponse: response $resultResponse")


                    var responseobj = JSONObject()
                    try {
                        responseobj = JSONObject(resultResponse)
                        CommonUtils.cancelProgressBar()
                        CommonUtils.log(TAG, " Main Response " + responseobj.toString(4))

                        if (responseobj.getString("status").equals("true", ignoreCase = true)) {

                            volleyInterface.successResponse(responseobj, requestcode)

                        } else {
                            CommonUtils.AppAlert(
                                context,
                                context.resources.getString(R.string.alerttitle),
                                responseobj.getString("message")
                            )

                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        CommonUtils.cancelProgressBar()
                        if (notshowerror) {
                            CommonUtils.AppAlert(
                                context,
                                context.resources.getString(R.string.alerttitle),
                                "" + context.resources.getString(R.string.unkonwnerror)
                            )
                        }
                    }


                }

            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    try {
                        CommonUtils.cancelProgressBar();

                        error!!.printStackTrace()
                        val responseBody = String(error.networkResponse.data, UTF_8);
                        val data = JSONObject(responseBody);

                        CommonUtils.log(TAG, "Checking Response " + data.toString() + " ");
                        val networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 401) {
                            // HTTP Status Code: 401 Unauthorized
                            CommonUtils.loginexpired(context);
                        } else {
                            CommonUtils.AppAlert(
                                context,
                                context.resources.getString(R.string.alerttitle),
                                "" + data.getString("message")
                            );
                        }

                    } catch (e: Exception) {
                        CommonUtils.cancelProgressBar();
                        CommonUtils.AppAlert(
                            context,
                            context.resources.getString(R.string.alerttitle),
                            "" + context.resources.getString(R.string.unkonwnerror)
                        )
                    }
                }

            }) {
            override fun getHeaders(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
                if (senttoken == 1) {
                    params["Authorization"] = "Bearer $token"
                }
                CommonUtils.log(TAG, " header params $params")
                return params
            }

            override fun getParams(): MutableMap<String, String> {
                val requestparams = java.util.HashMap<String, String>()

                if (params != null) {
                    CommonUtils.log(TAG, " Image Upload PARAMS $params")
                    for (key in params.keys) {

                        val value = params[key]
                        requestparams[key] = "" + value

                    }
                }

                return requestparams
            }


            override fun getByteData(): MutableMap<String, DataPart> {
                val params = java.util.HashMap<String, DataPart>()

                if (imageparams != null) {
                    for (key in imageparams.keys) {
                        params["" + key] =
                            DataPart(
                                key,
                                AppHelper.getFileDataFromDrawable(
                                    context,
                                    imageparams.get(key) as Bitmap
                                ),
                                "image/jpeg"
                            )
                    }
                }


                return params
            }

            override fun getBodyContentType(): String {
                return super.getBodyContentType()
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest)

    }
}

