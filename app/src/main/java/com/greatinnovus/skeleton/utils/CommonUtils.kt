package com.greatinnovus.promotionapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.text.Spannable
import android.text.format.DateFormat
import android.text.style.URLSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.ListPopupWindow
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.greatinnovus.promotionapp.app.App
import com.greatinnovus.promotionapp.constants.StringConstants
import com.greatinnovus.skeleton.R
import com.greatinnovus.skeleton.utils.dropdownmodel
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommonUtils {
    companion object {

        val TAG = "CommonUtil";
        lateinit var dialog: Dialog;
        lateinit var listPopupWindow: ListPopupWindow;

        fun log(tag: String, string: String) {
            if (StringConstants.DEBUG) {

                val maxLogSize = 1000;
                for (i in 0..string.length / maxLogSize) {
                    val start = i * maxLogSize;
                    var end: Int = (i + 1) * maxLogSize;
                    if (end > string.length) {
                        end = string.length
                    }
                    Log.d(tag, string.substring(start, end));
                }
            }
        }


        fun removeUnderlines(p_Text: Spannable) {
            val spans = p_Text.getSpans(0, p_Text.length, URLSpan::class.java)


            for (span in spans) {
                val start = p_Text.getSpanStart(span);
                val end = p_Text.getSpanEnd(span);
                p_Text.removeSpan(span);
                val spanval = URLSpanNoUnderline(span.getURL());
                p_Text.setSpan(spanval, start, end, 0);
            }
        }

        public fun hideKeyboard(activity: Activity) {
            val view = activity.getCurrentFocus();
            if (view != null) {
                if (view.getWindowToken() != null) {
                    val methodManager =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
                    if (methodManager != null) {

                        methodManager.hideSoftInputFromWindow(
                            view.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS
                        );
                    }
                }
            }
        }

        public fun showKeyboard(activity: Activity) {
            val view = activity.currentFocus;
            val methodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (methodManager != null && view != null) {
                methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }


        inline fun <reified A, reified B> Pair<*, *>.asPairOf(): Pair<A, B>? {
            if (first !is A || second !is B) return null
            return first as A to second as B
        }


        public fun setProgressBar(context: Context) {
            if (context is Activity) {
                val activity = context as Activity
                if (!activity.isFinishing()) {


                    dialog = Dialog(context, R.style.AlertDialogCustom);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.swipeprogress);
                    dialog.setCancelable(false);

                    Objects.requireNonNull(dialog.getWindow())?.setLayout(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                    );
                    dialog.getWindow()!!.getAttributes().windowAnimations =
                        R.style.AlertDialogCustom

                    val swipeRefreshLayout =
                        dialog.findViewById<SwipeRefreshLayout>(R.id.activity_main_swipe_refresh_layout)
                    swipeRefreshLayout.setColorSchemeResources(R.color.applogocolor)
                    swipeRefreshLayout.setRefreshing(true)

                    dialog.show();
                }


            }
        }


        public fun dpToPx(dp: Int): Int {
            val px = (dp * Resources.getSystem().getDisplayMetrics().density) as Int
            return px
        }

        public fun pxToDp(px: Int): Int {
            val dp = (px / Resources.getSystem().getDisplayMetrics().density) as Int
            return dp
        }


        public fun cancelProgressBar() {
            if (::dialog.isInitialized) {
                dialog?.let {
                    if (it.isShowing()) {
                        it.dismiss()
                    }
                }

            }
        }


        public fun checkCameraFront(context: Context): Boolean {
            return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
        }

        public fun showNoNetworkDialog(context: Context) {
            val alertDialog = MaterialAlertDialogBuilder(context);
            alertDialog
                .setMessage(StringConstants.ERROR_MESSAGE_NO_INTERNET)
                .setCancelable(false)
                .setPositiveButton("Settings",
                    object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_SETTINGS
                                )
                            );
                        }

                    })
                .setNegativeButton("Cancel",
                    object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            dialog.cancel();
                        }

                    });

            // show it
            alertDialog.show();

        }

        public fun showToastMessage(context: Context, message: String) {

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        }

        public fun showError(context: Context, msg: String) {

            val smthngtxt = "Need Permissions";
            if (!msg.equals(smthngtxt)) {
                if (context is Activity) {
                    val activity = context as Activity;
                    if (!activity.isFinishing()) {

                        val builder = MaterialAlertDialogBuilder(context);
                        builder.setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    dialog.dismiss();
                                }


                            });
                        // AlertDialog alert = builder.create();
                        builder.show();

                    }
                }
            } else {
                if (context is Activity) {
                    val activity = context as Activity;
                    if (!activity.isFinishing()) {

                        val builder = MaterialAlertDialogBuilder(context);
                        builder.setTitle("Lost connection to Servers")
                            .setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton("Retry", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    dialog.dismiss();
                                }


                            })
                            .setNegativeButton("Okay", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    dialog.dismiss();
                                }

                            });
                        // AlertDialog alert = builder.create();
                        builder.show();

                    }
                }

            }
        }


        public fun loginexpired(context: Context) {


            val activity = context as Activity;
            if (!activity.isFinishing()) {

                val builder = MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);

                builder.setTitle("Login Expired")
                    .setCancelable(false)
                    .setPositiveButton("goto login", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            cancelProgressBar()
                            /*val intent = Intent(context, MainActivity::class.java);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("key", "");
                            context.startActivity(intent);
                            context.finish();*/

                        }

                    });
                val alert = builder.create();
                alert.show();


            }
        }


        fun encodeImage(bm: Bitmap?): String? {

            val baos = ByteArrayOutputStream()
            if (bm != null) {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                val b = baos.toByteArray()
                //Base64.de
                return Base64.encodeToString(b, Base64.DEFAULT)
            } else {
                return null
            }

        }


        public fun setImage(
            context: Context,
            url: String,
            placeholder: Int,
            imageView: AppCompatImageView,
            colorres: Int
        ) {

            if (!(context as Activity).isFinishing()) {


                Glide.with(context).load(url)
                    .apply(
                        RequestOptions()
                            .fitCenter()
                            .override(512, 512)
                    )
                    .placeholder(getcircleprogressbar(context, colorres))
                    .error(placeholder)
                    .into(imageView);
            }

        }

        public fun getcircleprogressbar(context: Context, res: Int): CircularProgressDrawable {
            val circularProgressDrawable = CircularProgressDrawable(context);
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(5f);
            circularProgressDrawable.setColorFilter(
                context.getResources().getColor(res),
                PorterDuff.Mode.SRC_IN
            );
            circularProgressDrawable.start();
            //circularProgressDrawable.setStyle(android.R.attr.progressBarStyleSmall);

            return circularProgressDrawable;
        }

        public fun circlesetimage(
            context: Context,
            url: String,
            placeholder: Int,
            imageView: ImageView,
            rescolor: Int
        ) {

            if (!(context as Activity).isFinishing()) {
                if (url != null && !url.equals("") && !url.equals("null") && !url.contains("null")) {

                    Glide.with(context).load(url)
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .override(512, 512)
                                .transform(RoundedCorners(convertDpToPixel(12f, context) as Int))
                        )
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(getcircleprogressbar(context, rescolor))
                        .error(placeholder)
                        .into(imageView);
                } else {
                    imageView.setImageResource(placeholder);
                }
            }

        }

        public fun loadLocalCircleImage(
            context: Context,
            drawableimg: Int,
            imageView: AppCompatImageView,
            bitmapdrawable: Drawable
        ) {

            if (!(context as Activity).isFinishing()) {


                Glide.with(context)
                    .load(if (bitmapdrawable == null) drawableimg else bitmapdrawable)
                    .apply(
                        RequestOptions()
                            .override(512, 512)
                            .transform(RoundedCorners(convertDpToPixel(12f, context) as Int))
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);

            }

        }

        public fun convertDpToPixel(dp: Float, context: Context): Float {
            val resources = context.getResources();
            val metrics = resources.getDisplayMetrics();
            val px = (dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat()
            return px;
        }

        public fun isNetworkAvailable(mContext: Context): Boolean {
            var connection: Boolean = false;
            try {
                val cm =
                    mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (cm != null) {
                    val net_info = cm.activeNetworkInfo;
                    if (net_info != null && net_info.isConnected) {
                        connection = true;
                        val type = net_info.getType();


                    }
                }
            } catch (e: Exception) {
                e.printStackTrace();
            }
            return connection;
        }


        @Throws(JSONException::class)
        fun jsonToMap(json: JSONObject): HashMap<String, Any> {
            var retMap: HashMap<String, Any> = java.util.HashMap()

            if (json !== JSONObject.NULL) {
                retMap = toMap(json)
            }
            return retMap
        }


        @Throws(JSONException::class)
        public fun toMap(obj: JSONObject): HashMap<String, Any> {
            var objmap: HashMap<String, Any> = HashMap<String, Any>()

            val keysItr = obj.keys();
            while (keysItr.hasNext()) {
                val key = keysItr.next();
                var value: Any = obj.get(key);

                if (value is JSONArray) {
                    value = toList(value as JSONArray);
                } else if (value is JSONObject) {
                    value = toMap(value as JSONObject);
                }

                objmap.put(key, value as Object);
            }
            return objmap;
        }

        public fun toList(array: JSONArray): List<Any> {
            var list: ArrayList<Any> = ArrayList<Any>();
            for (i in 0..array.length()) {
                var value: Any = array.get(i);
                if (value is JSONArray) {
                    value = toList(value as JSONArray);
                } else if (value is JSONObject) {
                    value = toMap(value as JSONObject);
                }
                list.add(value as Object);
            }
            return list;
        }


        public fun AppAlert(mActivity: Context, Alertmsg: String, Message: String) {
            val alert =
                MaterialAlertDialogBuilder(mActivity, R.style.AlertDialogTheme)
            if (Alertmsg.isHavingvalue()) {
                alert.setTitle(Alertmsg)
            }

            alert.setMessage(Message)
            alert.setPositiveButton(mActivity.resources.getString(R.string.ok), null)
            alert.show()

        }


        public fun checkgpsenable(mActivity: Activity): Boolean {

            val service = mActivity.getSystemService(LOCATION_SERVICE) as LocationManager;
            val enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Check if enabled and if not send user to the GPS settings
            return enabled;
        }


        public fun saveData(key: String, value: Any, context: Context) {
            log("Save data", "" + key + "+--+" + value);
            val settings = context.applicationContext.getSharedPreferences(
                StringConstants.APP_PREFS_ACCOUNT,
                0
            );
            val editor = settings.edit()
            if (value is String) {
                editor.putString(key, value as String);
            } else if (value is Integer) {
                editor.putInt(key, value as Int);
            }
            editor.commit();
        }


        public fun loadData(key: String, context: Context, type: Any): Any {
            val settings = context.getApplicationContext()
                .getSharedPreferences(StringConstants.APP_PREFS_ACCOUNT, 0);
            if (type is String) {
                return settings.getString(key, "")!!;
            } else {
                return settings.getInt(key, 0);
            }

        }


        public fun decodeFile(f: File): Bitmap {
            var b: Bitmap? = null
            var fis: FileInputStream
            //Decode image size
            val o = BitmapFactory.Options();
            o.inJustDecodeBounds = true;


            try {
                fis = FileInputStream(f);
                BitmapFactory.decodeStream(fis, null, o);
                fis.close();
            } catch (e: FileNotFoundException) {
                e.printStackTrace();
            } catch (e: IOException) {
                e.printStackTrace();
            }

            val IMAGE_MAX_SIZE = 1024;
            var scale: Int = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = Math.pow(
                    2.0,
                    Math.ceil(
                        Math.log(
                            IMAGE_MAX_SIZE / Math.max(
                                o.outHeight,
                                o.outWidth
                            ) as Double
                        ) as Int / Math.log(0.5)
                    )
                ) as Int;
            }
            //  scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            //Decode with inSampleSize
            val o2 = BitmapFactory.Options();
            o2.inSampleSize = scale;
            try {
                fis = FileInputStream(f);
                b = BitmapFactory.decodeStream(fis, null, o2);
                fis.close();
            } catch (e: FileNotFoundException) {
                e.printStackTrace();
            } catch (e: IOException) {
                e.printStackTrace();
            }
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

            CommonUtils.log(TAG, "Width :" + b!!.width + " Height :" + b!!.width);

            val destFile = File(dir, "Agrouserimage.jpeg");
            try {
                val out = FileOutputStream(destFile);
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

            } catch (e: Exception) {
                e.printStackTrace();
            }
            return b;
        }


        public fun getcurrentimageuri(): Uri {


            return Uri.fromFile(getUserImage());
        }


        public fun showPopmenu(
            mActivity: Activity,
            customview: View,
            arraylist: ArrayList<String>,
            listener: AdapterView.OnItemClickListener
        ) {


            listPopupWindow = ListPopupWindow(
                mActivity
            );

            listPopupWindow.setAdapter(
                ArrayAdapter(
                    mActivity,
                    R.layout.simple_spinner_dropdown_item, R.id.txtv_items, arraylist
                )
            );
            listPopupWindow.setAnchorView(customview);
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(listener);
            listPopupWindow.show();

        }

        public fun showCustomPopmenu(
            mActivity: Activity,
            customview: View,
            arraylist: ArrayList<dropdownmodel>,
            listener: AdapterView.OnItemClickListener,
            customwith: Int
        ) {


            listPopupWindow = ListPopupWindow(
                mActivity
            );
            if (customwith > 0) {
                listPopupWindow.width = customwith
            }
            val itemlist: List<String> = arraylist.map { it.name }

            listPopupWindow.setAdapter(
                ArrayAdapter(
                    mActivity,
                    R.layout.simple_spinner_dropdown_item, R.id.txtv_items, itemlist
                )
            );
            listPopupWindow.setAnchorView(customview);
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(listener);
            listPopupWindow.show();

        }

        public fun cancelMenu() {
            if (listPopupWindow != null) {
                listPopupWindow.dismiss();
            }
        }

        fun getUserImage(): File? {
            val file = File(
                Environment.getExternalStorageDirectory().path,
                "${StringConstants.MAINFOLDERNAME}/Images"
            )
            if (!file.exists()) {
                file.mkdirs()
            }

            return File(file, StringConstants.USERIMAGE)
        }

        fun getuserTRYON(): File? {

            val file = File(
                Environment.getExternalStorageDirectory().path,
                "${StringConstants.MAINFOLDERNAME}/TRYONImages"
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            val now = Date()
            DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

            return File(file, "$now${StringConstants.IMAGENAME}")
        }


        fun getuserTRYONCount(): Int? {

            val file = File(
                Environment.getExternalStorageDirectory().path,
                "${StringConstants.MAINFOLDERNAME}/TRYONImages"
            )
            if (!file.exists()) {
                file.mkdirs()
            }


            return file.listFiles().size
        }

        public fun AddWhatermark(
            src: Bitmap,
            watermark: String,
            color: Int,
            alpha: Int,
            size: Float,
            underline: Boolean
        ): Bitmap {
            val w = src.getWidth();
            val h = src.getHeight();
            var result: Bitmap = Bitmap.createBitmap(w, h, src.getConfig());

            val canvas = Canvas(result);
            canvas.drawBitmap(src, 0f, 0f, null);

            val paint = Paint();
            paint.setColor(color);
            paint.setAlpha(alpha);
            paint.setTextSize(size);
            paint.setAntiAlias(true);
            paint.setUnderlineText(underline);
            val rectText = Rect();
            paint.getTextBounds(watermark, 0, watermark.length, rectText);
            canvas.drawText(watermark, 100f, 100f, paint);

            return result;
        }


    }


}

/*public fun JSONObject.spearateJsonResponse(model:Any): Any {
    var obj:Any
    val jsonObject=this
    try {
        val gson =  GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        val t =  TypeToken<model>() {
        }.getType();
        obj = gson.fromJson(jsonObject.toString(), t);

    } catch ( e:Exception) {
        e.printStackTrace();
    }
    return obj;
}*/
public fun Date.convertCurrentDateTosting(tofarmat: String): String {

    val date = this
    var returndate: String = "";
    try {

        val simpleDate = SimpleDateFormat(tofarmat);
        returndate = simpleDate.format(date);

    } catch (e: Exception) {
        e.printStackTrace();
    }

    return returndate;
}

public fun Long.logngToDateString(tofarmat: String): String {


    var returndate: String = "";
    try {

        val cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(this);
        returndate = DateFormat.format(tofarmat, cal).toString();

    } catch (e: Exception) {
        e.printStackTrace();
    }

    return returndate;
}

public fun String.saveData(valeus: Any) {
    val editor = App.editor
    if (valeus is String) {
        editor.putString(this, valeus)
    } else if (valeus is Int) {
        editor.putInt(this, valeus)
    } else if (valeus is Boolean) {
        editor.putBoolean(this, valeus)
    } else if (valeus is Float) {
        editor.putFloat(this, valeus)
    } else if (valeus is Long) {
        editor.putLong(this, valeus)
    }

    editor.commit()
}

//1-String 2-Int 3-boolen 4-float 5-Long
public fun String.loadData(type: Int): Any {
    val shared = App.getSharedPreferenceval()
    var obj: Any = "0"
    if (type == 1) {
        obj = shared.getString(this, "")!!
    } else if (type == 2) {
        obj = shared.getInt(this, 0)!!
    } else if (type == 3) {
        obj = shared.getBoolean(this, false)!!
    } else if (type == 4) {
        obj = shared.getFloat(this, 0f)!!
    } else if (type == 5) {
        obj = shared.getLong(this, 0)!!
    }
    return obj
}

public fun Int.getString(): String {
    return App.mContext.resources.getString(this)
}

public fun String.isHavingvalue(): Boolean {

    if (this == null) {
        return false;
    }
    if (this.length == 0) {
        return false;
    } else {
        if (this.equals("null", true)) {
            return false;
        } else {
            return true;
        }
    }

}


public fun String.isValidemail(): Boolean {
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    val pattern: Pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE)

    if (this == null) {
        return false;
    }
    if (this.length == 0) {
        return false;
    } else {
        return if (this.equals("null", true)) {
            false;
        } else {
            val matcher: Matcher = pattern.matcher(this)
            matcher.matches()
        }
    }

}


public fun String.setDefaultvalue(dfvalue: String): String {

    if (this.isHavingvalue()) {
        return this
    } else {
        return dfvalue
    }

}

public fun String.convertToDouble(): Double {
    var doublvl: Double = 0.0
    try {
        if (this.isHavingvalue()) {
            doublvl = this.toDouble()
        } else {
            doublvl = 0.0
        }

    } catch (e: Exception) {
        e.printStackTrace()
        doublvl = 0.0
    }
    return doublvl

}
public fun String.convertToFloat(): Float {
    var doublvl: Float = 0f
    try {

        if (this.isHavingvalue()) {
            doublvl = this.toFloat()
        } else {
            doublvl = 0f
        }

    } catch (e: Exception) {
        e.printStackTrace()
        doublvl = 0f
    }
    return doublvl

}

public fun Any.isinitialize(): Boolean {
    try {
        if (this != null) {

            return true
        } else {
            return false
        }
    } catch (e: Exception) {
        return false;
    }

}



fun <T> ArrayList<T>.filterOnCondition(condition: (T) -> Boolean): ArrayList<T> {
    val result = arrayListOf<T>()
    for (item in this) {
        if (condition(item)) {
            result.add(item)
        }
    }

    return result
}

fun View.onClick(onClick: () -> Unit) {
    setOnClickListener { onClick() }
}