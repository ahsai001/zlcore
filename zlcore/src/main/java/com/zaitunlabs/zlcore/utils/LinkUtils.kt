package com.zaitunlabs.zlcore.utils

import android.net.Uri
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

import java.util.regex.Matcher
import java.util.regex.Pattern

object LinkUtils {
    val URL_PATTERN = Pattern.compile(
            "((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+" // named host

                    + "(?:" // plus top level domain

                    + "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
                    + "|(?:biz|b[abdefghijmnorstvwyz])"
                    + "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])"
                    + "|d[ejkmoz]"
                    + "|(?:edu|e[cegrstu])"
                    + "|f[ijkmor]"
                    + "|(?:gov|g[abdefghilmnpqrstuwy])"
                    + "|h[kmnrtu]"
                    + "|(?:info|int|i[delmnoqrst])"
                    + "|(?:jobs|j[emop])"
                    + "|k[eghimnrwyz]"
                    + "|l[abcikrstuvy]"
                    + "|(?:mil|mobi|museum|m[acdghklmnopqrstuvwxyz])"
                    + "|(?:name|net|n[acefgilopruz])"
                    + "|(?:org|om)"
                    + "|(?:pro|p[aefghklmnrstwy])"
                    + "|qa"
                    + "|r[eouw]"
                    + "|s[abcdeghijklmnortuvyz]"
                    + "|(?:tel|travel|t[cdfghjklmnoprtvwz])"
                    + "|u[agkmsyz]"
                    + "|v[aceginu]"
                    + "|w[fs]"
                    + "|y[etu]"
                    + "|z[amw]))"
                    + "|(?:(?:25[0-5]|2[0-4]" // or ip address

                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
                    + "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9])))"
                    + "(?:\\:\\d{1,5})?)" // plus option port number

                    + "(\\/(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~" // plus option query params

                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?:\\b|$)")

    interface OnClickListener {
        fun onLinkClicked(link: String?)

        fun onClicked()
    }

    internal class SensibleUrlSpan(url: String,
                                   /** Pattern to match.  */
                                   private val mPattern: Pattern) : URLSpan(url) {

        fun onClickSpan(widget: View): Boolean {
            val matched = mPattern.matcher(url).matches()
            if (matched) {
                //super.onClick(widget);
            }
            return matched
        }
    }

    internal class SensibleLinkMovementMethod : LinkMovementMethod() {

        var isLinkClicked: Boolean = false
            private set

        var clickedLink: String? = null
            private set

        override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
            val action = event.action

            if (action == MotionEvent.ACTION_UP) {
                isLinkClicked = false
                clickedLink = null
                var x = event.x.toInt()
                var y = event.y.toInt()

                x -= widget.totalPaddingLeft
                y -= widget.totalPaddingTop

                x += widget.scrollX
                y += widget.scrollY

                val layout = widget.layout
                val line = layout.getLineForVertical(y)
                val off = layout.getOffsetForHorizontal(line, x.toFloat())

                val link = buffer.getSpans<ClickableSpan>(off, off, ClickableSpan::class.java)

                if (link.size != 0) {
                    val span = link[0] as SensibleUrlSpan
                    isLinkClicked = span.onClickSpan(widget)
                    clickedLink = span.url
                    return isLinkClicked
                }
            }
            super.onTouchEvent(widget, buffer, event)

            return false
        }

    }

    @JvmOverloads
    fun autoLink(view: TextView, listener: OnClickListener?,
                 patternStr: String? = null) {
        val text = view.text.toString()
        if (TextUtils.isEmpty(text)) {
            return
        }
        val spannable = SpannableString(text)

        val pattern: Pattern
        if (TextUtils.isEmpty(patternStr)) {
            pattern = URL_PATTERN
        } else {
            pattern = Pattern.compile(patternStr)
        }
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            val urlSpan = SensibleUrlSpan(matcher.group(1), pattern)
            spannable.setSpan(urlSpan, matcher.start(1), matcher.end(1),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        view.setText(spannable, TextView.BufferType.SPANNABLE)

        val method = SensibleLinkMovementMethod()
        view.movementMethod = method
        if (listener != null) {
            view.setOnClickListener {
                if (method.isLinkClicked) {
                    listener.onLinkClicked(method.clickedLink)
                } else {
                    listener.onClicked()
                }
            }
        }
    }


    fun getUrlFromUri(uri: Uri): String {
        var url = ""
        url += uri.scheme
        url += "://"
        url += uri.authority
        url += uri.path

        if (!TextUtils.isEmpty(uri.query)) {
            url += "?" + uri.query!!
        }
        if (!TextUtils.isEmpty(uri.fragment)) {
            url += "#" + uri.fragment!!
        }
        return url
    }

}
