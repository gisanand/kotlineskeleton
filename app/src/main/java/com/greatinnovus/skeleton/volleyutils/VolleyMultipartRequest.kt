package com.greatinnovus.promotionapp.volleyutils

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.greatinnovus.promotionapp.utils.CommonUtils
import java.io.*

open class VolleyMultipartRequest : Request<NetworkResponse> {
    val twoHyphens = "--";
    val lineEnd = "\r\n";
    val boundary = "apiclient-" + System.currentTimeMillis()

    lateinit var mListener: Response.Listener<NetworkResponse>;
    lateinit var mErrorListener: Response.ErrorListener;
    lateinit var mHeaders: Map<String, String>;


    constructor(
        method: Int, url: String,
        listener: Response.Listener<NetworkResponse>,
        errorListener: Response.ErrorListener
    ) : super(method, url, errorListener) {

        this.mListener = listener;
        this.mErrorListener = errorListener;
    }


    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        if (mHeaders != null) {
            return mHeaders
        }
        return super.getHeaders()
    }

    @Override
    override fun getBodyContentType(): String {
        CommonUtils.log("VolleyMultipartRequest"," Test values "+boundary)
        return "multipart/form-data;boundary=" + boundary;
    }


    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray? {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        try {
            // populate text payload
            val params = getParams()
            if (params != null && params.size > 0) {
                textParse(dos, params, getParamsEncoding());
            }

            // populate data byte payload
            val data = getByteData()
            if (data != null && data.size > 0) {
                dataParse(dos, data);
            }

            // close multipart form data after text and file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            return bos.toByteArray();
        } catch ( e:IOException) {
            e.printStackTrace();
        }
        return null;
    }

    open  fun getByteData(): Map<String, DataPart>?{
        return null;
    }


    override fun parseNetworkResponse(response: NetworkResponse?): Response<NetworkResponse> {
        try {
            return Response.success(
                response,
                HttpHeaderParser.parseCacheHeaders(response)
            );
        } catch (e: Exception) {
            return Response.error(ParseError(e));
        }

    }

    override fun deliverResponse(response: NetworkResponse?) {
        mListener.onResponse(response);
    }

    override fun deliverError(error: VolleyError?) {
        mErrorListener.onErrorResponse(error);
    }

    @Throws(IOException::class)
    fun textParse(
        dataOutputStream: DataOutputStream,
        params: Map<String, String>,
        encoding: String
    ) {
        try {

            for ((key, value) in params) {
                buildTextPart(dataOutputStream, key, value)
            }
        } catch (uee: UnsupportedEncodingException) {
            throw  RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }

    @Throws(IOException::class)
    private fun dataParse(dataOutputStream: DataOutputStream, data: Map<String, DataPart>) {
        for ((key, value) in data) {
            buildDataPart(dataOutputStream, value, key)
        }
    }

    @Throws(IOException::class)
    private fun buildTextPart(
        dataOutputStream: DataOutputStream,
        parameterName: String,
        parameterValue: String
    ) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"$lineEnd")
        //dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd)
        dataOutputStream.writeBytes(parameterValue + lineEnd)
    }


    @Throws(IOException::class)
    private fun buildDataPart(
        dataOutputStream: DataOutputStream,
        dataFile: DataPart,
        inputName: String
    ) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes(
            "Content-Disposition: form-data; name=\"" +
                    inputName + "\"; filename=\"" + dataFile.fileName + "\"" + lineEnd
        )
        if (dataFile.type != null && !dataFile.type!!.trim({ it <= ' ' }).isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.type + lineEnd)
        }
        dataOutputStream.writeBytes(lineEnd)

        val fileInputStream = ByteArrayInputStream(dataFile.content)
        var bytesAvailable = fileInputStream.available()

        val maxBufferSize = 1024 * 1024
        var bufferSize = Math.min(bytesAvailable, maxBufferSize)
        val buffer = ByteArray(bufferSize)

        var bytesRead = fileInputStream.read(buffer, 0, bufferSize)

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize)
            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
        }

        dataOutputStream.writeBytes(lineEnd)
    }


    inner class DataPart {

        var fileName: String? = null

        var content: ByteArray? = null

        var type: String? = null


        constructor() {}


        constructor(name: String, data: ByteArray) {
            fileName = name
            content = data
        }

        constructor(name: String, data: ByteArray, mimeType: String) {
            fileName = name
            content = data
            type = mimeType
        }
    }
}
