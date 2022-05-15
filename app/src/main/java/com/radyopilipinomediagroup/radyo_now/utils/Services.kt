package com.radyopilipinomediagroup.radyo_now.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularimageview.CircularImageView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.local.SessionManager
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.ui.account.login.LoginActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Services {
    companion object {

        private var firebaseAnalytics: FirebaseAnalytics? = Firebase.analytics

        fun popUpAds(context: Context, data: AdsModel.Data){
            val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(R.layout.dialog_popup)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val parentLayout = dialog.findViewById<RelativeLayout>(R.id.parentLayout)
            val popupPic = dialog.findViewById<ImageView>(R.id.popup_pic)
            val closeAds = dialog.findViewById<ImageView>(R.id.closeAds)

            try {
                Glide.with(context)
                    .load(data.assets[0].imageUrl)
                    .into(popupPic)

                popupPic.setOnClickListener {
                    try {
                        openBrowser(context, data.assets[0].link!!)
                        setDataAnalytics(firebaseAnalytics,
                            data.title,
                            data.location,
                            data.id.toString(),
                            "Location",
                            "select_ad")
                    } catch (e: Exception) {}
                }
            } catch (e: Exception) {}
            parentLayout.setOnClickListener { dialog.dismiss() }
            closeAds.setOnClickListener { dialog.dismiss() }

            dialog.setCancelable(true)
            dialog.show()
        }

        fun nextIntent(context: Activity, toClass: Class<*>){
            val intent = Intent(context, toClass)
            context.startActivity(intent)
            context.finish()
        }


        fun nextIntent(context: Context, toClass: Class<*>){
            val intent = Intent(context, toClass)
            context.startActivity(intent)
        }

        fun nextIntent(context: Activity, data: String, toClass: Class<*>){
            val intent = Intent(context, toClass)
            intent.putExtra("dataKey", data)
            context.startActivity(intent)
            context.finish()
        }

        fun openBrowser(context: Context, urlLink: String){
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlLink))
            context.startActivity(intent)
        }

        fun notAvailable(context: Context){
            Toast.makeText(context, "Feature not Available", Toast.LENGTH_SHORT).show()
        }

        fun getToolbarDrawerIcon(activity: Activity) : ImageView?{
            return activity.findViewById(R.id.toolbarDrawerIcon)
        }

        fun getToolbarText(activity: Activity) : TextView? {
            return activity.findViewById(R.id.toolbarText)
        }

        fun getToolbarBack(activity: Activity) : ImageView?{
            return activity.findViewById<ImageView>(R.id.toolbarBack)
        }

        fun getToolbarLogo(activity: Activity) : ImageView?{
            return activity.findViewById<ImageView>(R.id.toolbarLogo)
        }

        fun getToolbarSearch(activity: Activity) : EditText?{
            return activity.findViewById<EditText>(R.id.toolbarSearch)
        }

        fun getToolbarClose(activity: Activity) : CircularImageView?{
            return activity.findViewById<CircularImageView>(R.id.imgCloseSearch)
        }

        fun getToolbarLayoutSearch(activity: Activity) : RelativeLayout?{
            return activity.findViewById<RelativeLayout>(R.id.layoutSearch)
        }

        fun getToolbarMainHolder(activity: Activity) : RelativeLayout?{
            return activity.findViewById<RelativeLayout>(R.id.mainToolbar)
        }

        fun getAppBarLayout(activity: Activity) : AppBarLayout?{
           return activity.findViewById(R.id.appBarLayout)
        }

        fun changeFragment(fragmentManager: FragmentManager, fragment: Fragment, tag: String) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            fragmentTransaction.replace(R.id.dashboardFrame, fragment, tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commit()
        }

        fun changeFragment(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            tag: String,
            bundleName: String?,
            bundleValue: String?
        ) {

            val bundle = Bundle()
            bundle.putString(bundleName, bundleValue)
            fragment.arguments = bundle
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            fragmentTransaction.replace(R.id.dashboardFrame, fragment, tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commit()
        }

        fun changeFragment(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            tag: String,
            stationName: String
        ) {

            val bundle = Bundle()
            bundle.putString("stationName", stationName)
            fragment.arguments = bundle
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            fragmentTransaction.replace(R.id.dashboardFrame, fragment, tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commit()
        }

        fun changeFragment(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            tag: String,
            stationId: String,
            contentId: String,
            stationName: String,
            stationType: String
        ) {

            val bundle = Bundle()
            bundle.putString("stationId", stationId)
            bundle.putString("contentId", contentId)
            bundle.putString("stationName", stationName)
            bundle.putString("stationType", stationType)
            fragment.arguments = bundle
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            fragmentTransaction.replace(R.id.dashboardFrame, fragment, tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commit()
        }

        fun setStatusBarLight(activity: Activity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = Color.WHITE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
        fun setStatusBarOriginal(activity: Activity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.red)
            }
        }

        fun fragmentBackOnClick(activity: Activity) : View.OnClickListener {
            val fragmentManager: FragmentManager =
                (activity as FragmentActivity).supportFragmentManager
            return View.OnClickListener {
                fragmentManager.popBackStack()
            }
        }

        fun setActivityFullScreen(activity: Activity) {
//            activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }

        fun openPlaylistOptionsDialog(context: Context) {
            val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
            val bsPlaylist: LinearLayout? = dialog.findViewById(R.id.bs_playlist)
            val bottomSheetView = LayoutInflater.from(context).inflate(
                R.layout.dialog_bs_playlist,
                bsPlaylist
            )

            val donePlaylist: Button? = bottomSheetView.findViewById(R.id.donePlaylist)
            val createPlaylist: TextView? = bottomSheetView.findViewById(R.id.createPlaylist)
            val playlistRecycler : RecyclerView? = bottomSheetView.findViewById(R.id.playlistRecycler)
            (context as DashboardActivity).playlistRecycler(playlistRecycler!!)

            donePlaylist?.setOnClickListener {
                context.onDone()
                dialog.dismiss()
            }
            createPlaylist?.setOnClickListener {
                context.onCreatePlaylist()
            }

            dialog.setContentView(bottomSheetView)
            dialog.show()
        }

        fun convertDate(strDate: String) : String{
            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            )
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val date: Date = inputFormat.parse(strDate)!!
            return outputFormat.format(date)
        }

        fun convertTimeZoneDate(strDate: String) : String{
            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.getDefault()
            )
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val date: Date = inputFormat.parse(strDate)!!
            return outputFormat.format(date)
        }

        fun convertDate(strDate: String?, currentFormat: String, toFormat: String) : String{
            val inputFormat = SimpleDateFormat(
                currentFormat,
                Locale.getDefault()
            )
            val outputFormat = SimpleDateFormat(toFormat, Locale.getDefault())
            val date: Date = inputFormat.parse(strDate!!)!!
            return outputFormat.format(date)
        }

        fun convertAge(string: String) : Int {
            val newString = string
            return if (newString.isEmpty()) {
                0
            } else {
                val date : Date? = SimpleDateFormat("MM/dd/yyyy").parse(newString)
                val cal = Calendar.getInstance()
                cal.time = date!!
                val monthBirth = cal.get(Calendar.MONTH) + 1
                val dayBirth = cal.get(Calendar.DAY_OF_MONTH)
                val yearBirth = cal.get(Calendar.YEAR)
                val currentDay = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
                val currentMonth = Calendar.getInstance()[Calendar.MONTH] + 1
                val currentYear = Calendar.getInstance()[Calendar.YEAR]
                if (currentMonth - monthBirth >= 0 && currentDay - dayBirth >= 0) {
                    if (currentYear - yearBirth < 0) 0
                    else currentYear - yearBirth
                } else if (currentYear - yearBirth - 1 < 0) 0
                else currentYear - yearBirth - 1
            }
        }

        fun getCurrentDate(): Date? {
            return Date()
        }

        fun dateBetween(date: Date?, dateStart: Date?, dateEnd: Date?): Boolean {
            return if (date != null && dateStart != null && dateEnd != null) {
                dateEqualOrAfter(date, dateStart) && dateEqualOrBefore(date, dateEnd)
            } else false
        }

        private fun dateEqualOrAfter(dateInQuestion: Date, date2: Date): Boolean {
            return if (dateInQuestion == date2) true else dateInQuestion.after(date2)
        }

        private fun dateEqualOrBefore(dateInQuestion: Date, date2: Date): Boolean {
            return if (dateInQuestion == date2) true else dateInQuestion.before(date2)
        }

        fun getTotalTextDuration(milliSeconds: Int) : String {
            var timeString = ""
            val secondsString: String

            val hours = (milliSeconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliSeconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()
            if (hours > 0) {
                timeString = ":$hours"
            }

            secondsString = if (seconds < 10) {
                "0$seconds"
            } else {
                "" + seconds
            }
            timeString = "$timeString$minutes:$secondsString"

            return timeString
        }

        fun getTotalDuration(milliSeconds: Int) : Int {
            var totalDuration = 0

            var hours = (milliSeconds / (1000 * 60 * 60)).toInt()
            var minutes = (milliSeconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            var seconds = ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()

            if (hours > 0) {
                totalDuration = hours
            } else if (minutes > 0) {
                totalDuration = minutes
            } else if (seconds > 0) {
                totalDuration = seconds
            }

            return totalDuration
        }

        fun getTermsHTML(): String {
            return """
                   <!DOCTYPE html>
                    <html>
                    <style type = "text/css">
                    body{
                        font-family: Avenir;
                        text-align: justify;
                        margin: 35px;
                    }
                    h2{
                        text-align: left;
                    }
                    </style>
                        <body>
                             <!-- <h2 align="center">TERMS & CONDITIONS</h2> -->
                             
                             <h2>Welcome to Radyo Pilipino Media Group!</h2>
                             <br>
                             
                             <h2 align="center">1. Introduction</h2>
                        
                             <br>
                             <br>
                             These Terms of Service govern your use of our website located at <a href="www.radyopilipino.com">www.radyopilipino.com</a> individually operated by Radyo Pilipino Media Group.
                             <br>
                             <br>
                             Our Privacy Policy also governs your use of our Service and explains how we collect, safeguard and disclose information that results from your use of our web pages.     
                             <br>
                             <br>
                             Your agreement with us includes these Terms and our Privacy Policy. You acknowledge that you have read and understood Agreements, and agree to be bound by them.
                             <br>
                             <br>
                             If you do not agree with (or cannot comply with) Agreements, then you may not use the Service, but please let us know by emailing at wehearyou@radyopilipino.com so we can try to find a solution. These Terms apply to all visitors, users and others who wish to access or use Service.
                        
                             <p><h2 align="center">2. Communications</h2></p>
                        
                             <p>By using our Service, you agree to subscribe to newsletters, marketing or promotional materials and other information we may send. However, you may opt out of receiving any, or all, of these communications from us by following the unsubscribe link or by emailing at wehearyou@radyopilipino.com.</p>
                        
                             <h2 align="center">3. Contests, Sweepstakes and Promotions</h2>
                        
                             <p>Any contests, sweepstakes or other promotions (collectively, ìPromotionsî) made available through Service may be governed by rules that are separate from these Terms of Service. If you participate in any Promotions, please review the applicable rules as well as our Privacy Policy. If the rules for a Promotion conflict with these Terms of Service, Promotion rules will apply.</p>
                        
                             <h2 align="center">4. Content</h2>
                             <p>Content found on or through this Service are the property of Radyo Pilipino Media Group or used with permission. You may not distribute, modify, transmit, reuse, download, repost, copy, or use said Content, whether in whole or in part, for commercial purposes or for personal gain, without express advance written permission from us.</p>
                        
                             <h2 align="center">5. Prohibited Uses</h2>
                        
                             <p>You may use Service only for lawful purposes and in accordance with Terms. You agree not to use Service:
                             <br>
                             <br>
                             0.1. In any way that violates any applicable national or international law or regulation.
                             <br>
                             <br>
                             0.2. For the purpose of exploiting, harming, or attempting to exploit or harm minors in any way by exposing them to inappropriate content or otherwise.
                             <br>
                             <br>
                             0.3. To transmit, or procure the sending of, any advertising or promotional material, including any ìjunk mailî, ìchain letter,î ìspam,î or any other similar solicitation.
                             <br>
                             <br>
                             0.4. To impersonate or attempt to impersonate Company, a Company employee, another user, or any other person or entity.
                             <br>
                             <br>
                             0.5. In any way that infringes upon the rights of others, or in any way is illegal, threatening, fraudulent, or harmful, or in connection with any unlawful, illegal, fraudulent, or harmful purpose or activity.
                             <br>
                             <br>
                             0.6. To engage in any other conduct that restricts or inhibits anyoneís use or enjoyment of Service, or which, as determined by us, may harm or offend Company or users of Service or expose them to liability.
                             <br>
                             <br>
                             Additionally, you agree not to:
                             <br>
                             <br>
                             0.1. Use Service in any manner that could disable, overburden, damage, or impair Service or interfere with any other partyís use of Service, including their ability to engage in real time activities through Service.
                             <br>
                             <br>
                             0.2. Use any robot, spider, or other automatic device, process, or means to access Service for any purpose, including monitoring or copying any of the material on Service.
                             <br>
                             <br>
                             0.3. Use any manual process to monitor or copy any of the material on Service or for any other unauthorized purpose without our prior written consent.
                             <br>
                             <br>
                             0.4. Use any device, software, or routine that interferes with the proper working of Service.     
                             <br>
                             <br>
                             0.5. Introduce any viruses, trojan horses, worms, logic bombs, or other material which is malicious or technologically harmful.
                             <br>
                             <br>
                             0.6. Attempt to gain unauthorized access to, interfere with, damage, or disrupt any parts of Service, the server on which Service is stored, or any server, computer, or database connected to Service.
                             <br>
                             <br>
                             0.7. Attack Service via a denial-of-service attack or a distributed denial-of-service attack.
                             <br>
                             <br>
                             0.8. Take any action that may damage or falsify Company rating.
                             <br>
                             <br>
                             0.9. Otherwise attempt to interfere with the proper working of Service.</p>
                        
                             <h2 align="center">6. Analytics</h2>
                             <p>We may use third-party Service Providers to monitor and analyze the use of our Service.</p>
                        
                             <h2 align="center">7. Intellectual Property</h2>
                        
                             <p>Service and its original content (excluding Content provided by users), features and functionality are and will remain the exclusive property of Radyo Pilipino and its licensors. Service is protected by copyright, trademark, and other laws of and foreign countries. Our trademarks may not be used in connection with any product or service without the prior written consent of Radyo Pilipino.</p>
                        
                             <h2 align="center">8. Copyright Policy</h2>
                        
                             <p>We respect the intellectual property rights of others. It is our policy to respond to any claim that Content posted on Service infringes on the copyright or other intellectual property rights (ìInfringementî) of any person or entity.
                             If you are a copyright owner, or authorized on behalf of one, and you believe that the copyrighted work has been copied in a way that constitutes copyright infringement, please submit your claim via email to wehearyou@radyopilipino.com, with the subject line: ìCopyright Infringementî and include in your claim a detailed description of the alleged Infringement as detailed below, under ìDMCA Notice and Procedure for Copyright Infringement Claims.
                             You may be held accountable for damages (including costs and attorneysí fees) for misrepresentation or bad-faith claims on the infringement of any Content found on and/or through Service on your copyright.</p>
                        
                             <h2 align="center">9. DMCA Notice and Procedure for Copyright Infringement Claims</h2>
                        
                             <p>You may submit a notification pursuant to the Digital Millennium Copyright Act (DMCA) by providing our Copyright Agent with the following information in writing (see 17 U.S.C 512(c)(3) for further detail):
                             <br>
                             <br>
                             0.1. an electronic or physical signature of the person authorized to act on behalf of the owner of the copyrightís interest;
                             <br>
                             <br>
                             0.2. a description of the copyrighted work that you claim has been infringed, including the URL (i.e., web page address) of the location where the copyrighted work exists or a copy of the copyrighted work;
                             <br>
                             <br>
                             0.3. identification of the URL or other specific location on Service where the material that you claim is infringing is located;
                             <br>
                             <br>
                             0.4. your address, telephone number, and email address;
                             <br>
                             <br>
                             0.5. a statement by you that you have a good faith belief that the disputed use is not authorized by the copyright owner, its agent, or the law;
                             <br>
                             <br>
                             0.6. a statement by you, made under penalty of perjury, that the above information in your notice is accurate and that you are the copyright owner or authorized to act on the copyright ownerís behalf.
                             <br>
                             <br>
                             You can contact our Copyright Agent via email at wehearyou@radyopilipino.com.
                             <br>
                             <br>
                             10. Error Reporting and Feedback
                             <br>
                             <br>
                             You may provide us either directly at wehearyou@radyopilipino.com or via third party sites and tools with information and feedback concerning errors, suggestions for improvements, ideas, problems, complaints, and other matters related to our Service (ìFeedbackî). You acknowledge and agree that: (i) you shall not retain, acquire or assert any intellectual property right or other right, title or interest in or to the Feedback; (ii) Company may have development ideas similar to the Feedback; (iii) Feedback does not contain confidential information or proprietary information from you or any third party; and (iv) Company is not under any obligation of confidentiality with respect to the Feedback. In the event the transfer of the ownership to the Feedback is not possible due to applicable mandatory laws, you grant Company and its affiliates an exclusive, transferable, irrevocable, free-of-charge, sub-licensable, unlimited and perpetual right to use (including copy, modify, create derivative works, publish, distribute and commercialize) Feedback in any manner and for any purpose.</p>
                        
                             <h2 align="center">11. Links To Other Websites</h2>
                             <p>Our Service may contain links to third party web sites or services that are not owned or controlled by Radyo Pilipino Media Group.
                             <br>
                             <br>
                             Radyo Pilipino Media Group has no control over, and assumes no responsibility for the content, privacy policies, or practices of any third party web sites or services. We do not warrant the offerings of any of these entities/individuals or their websites.
                             <br>
                             <br>
                             For example, the outlined Terms of Use have been created using PolicyMaker.io, a free web application for generating high-quality legal documents. PolicyMakerís Terms and Conditions generator is an easy-to-use free tool for creating an excellent standard Terms of Service template for a website, blog, e-commerce store or app.
                             <br>
                             <br>
                             YOU ACKNOWLEDGE AND AGREE THAT COMPANY SHALL NOT BE RESPONSIBLE OR LIABLE, DIRECTLY OR INDIRECTLY, FOR ANY DAMAGE OR LOSS CAUSED OR ALLEGED TO BE CAUSED BY OR IN CONNECTION WITH USE OF OR RELIANCE ON ANY SUCH CONTENT, GOODS OR SERVICES AVAILABLE ON OR THROUGH ANY SUCH THIRD PARTY WEB SITES OR SERVICES.
                             <br>
                             <br>
                             WE STRONGLY ADVISE YOU TO READ THE TERMS OF SERVICE AND PRIVACY POLICIES OF ANY THIRD PARTY WEB SITES OR SERVICES THAT YOU VISIT.
                             </p>
                        
                             <h2 align="center">12. Disclaimer Of Warranty</h2>
                             <p>THESE SERVICES ARE PROVIDED BY COMPANY ON AN ìAS ISî AND ìAS AVAILABLEî BASIS. COMPANY MAKES NO REPRESENTATIONS OR WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED, AS TO THE OPERATION OF THEIR SERVICES, OR THE INFORMATION, CONTENT OR MATERIALS INCLUDED THEREIN. YOU EXPRESSLY AGREE THAT YOUR USE OF THESE SERVICES, THEIR CONTENT, AND ANY SERVICES OR ITEMS OBTAINED FROM US IS AT YOUR SOLE RISK.
                             NEITHER COMPANY NOR ANY PERSON ASSOCIATED WITH COMPANY MAKES ANY WARRANTY OR REPRESENTATION WITH RESPECT TO THE COMPLETENESS, SECURITY, RELIABILITY, QUALITY, ACCURACY, OR AVAILABILITY OF THE SERVICES. WITHOUT LIMITING THE FOREGOING, NEITHER COMPANY NOR ANYONE ASSOCIATED WITH</p>
                        </body>
                    </html>
                """.trimIndent()
        }

        fun getSignUpTermsHTML(): String {
            return """
                   <!DOCTYPE html>
                    <html>
                        <body>
                            <b>Terms and Conditions</b>
                            Your privacy matters to us. By clicking Agree, you hereby authorize RadyoNow and all of its legal, accredited, and existing subsidiaries and partners entitled to access your personal information for the sole purpose of making your experience with this app more efficient. RadyoNow and all of its legal, accredited, subsidiaries and partner entities are committed to providing exemplary customer service that does not hinge on the misuse of your personal information.
                            
                            Data Protection Act 2020
                            Republic <b>Act</b> No.10173, otherwise known as the  <b>Data Privacy Act</b> is a law that seeks to protect all forms of information, be it private, personal, or sensitive. It is meant to cover both natural and juridical persons involved in the processing of personal information. RadyoNow hereby swears that our user’s personal data is extracted only within a professional capacity, that of which is used to better provide a better experience for the user.
                            
                            Cookie Clauses for Privacy Policy We collect data in order to improve your journey with us. By clicking “Accept,” you’re authorizing us to enable our cookies to make your experience with RadyoNow better.
                        </body>
                    </html>
                """
        }

        fun checkValidDate(strDate: String) : Boolean{
            return try {
                val inputFormat = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                )
                val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                val date: Date = inputFormat.parse(strDate)!!
                true
            } catch (e: Exception) {
                false
            }
        }

        fun getVideoId(videoUrl: String?): String? {
            if (videoUrl == null || videoUrl.trim { it <= ' ' }.isEmpty()) {
                return null
            }
            val pattern: Pattern = Pattern.compile(Constants.expression)
            val matcher: Matcher = pattern.matcher(videoUrl)
            try {
                if (matcher.find()) return matcher.group()
            } catch (ex: ArrayIndexOutOfBoundsException) {
                ex.printStackTrace()
            }
            return null
        }

        @SuppressLint("HardwareIds")
        fun getDeviceId(context: Context): String {
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }

        fun setDataAnalytics(firebaseAnalytics: FirebaseAnalytics?, data: String, eventName: String){
            firebaseAnalytics?.logEvent(eventName) {
                param(Constants.ANALYTICS_PARAM, data)
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "custom")
            }
        }

        fun setDataAnalytics(firebaseAnalytics: FirebaseAnalytics?, paramType: String, paramData: String,  eventName: String){
            firebaseAnalytics?.logEvent(eventName) {
                param(paramType, paramData)
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "custom")
            }
        }

        fun setDataAnalytics(
            firebaseAnalytics: FirebaseAnalytics?,
            name: String?,
            program: String?,
            id: String?,
            type: String?,
            eventName: String){

            firebaseAnalytics?.logEvent(eventName) {
                param("name", name!!)
                param(type!!, program!!)
                param("id", id!!)
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "custom")
            }
        }

        fun checkIfAuthenticated(message: String): Boolean {
            return !message.contains("Unauthenticated")
        }

        fun openPermissionSettings(packageName: String) : Intent {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            return intent
        }

        fun showWarningDialog(
            context: Context,
            title: String,
            message: String,
            callback: DialogInterface.OnClickListener) {

            AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes
            ) { dialog, which ->
                callback.onClick(dialog, which)
            }
            .setNegativeButton(android.R.string.no) { dialog, which ->
                callback.onClick(dialog, which)
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
        }

        fun signOutExpired(context: Context, sessionManager: SessionManager?){
            facebookGmailSignOut(context)
            sessionManager?.setData(SessionManager.SESSION_STATUS, "logged_out")
            sessionManager?.setLoginType("")
            nextIntent(context, LoginActivity::class.java)
        }

        private fun facebookGmailSignOut(context: Context) {
            try {
                //Facebook Sign Out
                LoginManager.getInstance().logOut()

                //Google Sign Out
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut()
            } catch (e : Exception) {}
        }

        fun gmailSignOut(context: Context){
            try {
                //Google Sign Out
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut()
            } catch (e: Exception) {}
        }

        fun facebookSignOut(){
            try {
                //Facebook Sign Out
                LoginManager.getInstance().logOut()
            } catch (e: Exception) {}
        }
    }
}