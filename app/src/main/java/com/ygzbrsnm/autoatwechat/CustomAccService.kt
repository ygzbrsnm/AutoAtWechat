package com.ygzbrsnm.autoatwechat

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ygzbrsnm.autoatwechat.MainActivity.Companion.NAME_LIST

class CustomAccService : AccessibilityService() {
    private val TEMP = 100L
    override fun onInterrupt() {}

    private var isAtSomeoneUI = false
    private var accessibilityNodeInfo: AccessibilityNodeInfo? = null
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        accessibilityNodeInfo = rootInActiveWindow
        Log.e("微信测试", "" + event?.eventType + "         " + event?.className)
        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (event.className == "com.tencent.mm.ui.chatting.AtSomeoneUI") {
                    isAtSomeoneUI = true
                    Handler().postDelayed({
                        val ids =
                            accessibilityNodeInfo?.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/fdi")
                        performClickBtn(ids)
                    }, TEMP)

                } else {
                    isAtSomeoneUI = false
                }
            }
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                Log.e("微信测试", "TYPE_VIEW_FOCUSED")
                if (event.className == "android.widget.EditText") {
                    if (NAME_LIST.isEmpty()) {
                        return
                    }
                    Handler().postDelayed(Runnable {
                        if (accessibilityNodeInfo == null) {
                            return@Runnable
                        }
                        //聊天输入框
                        val auj =
                            accessibilityNodeInfo?.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/auj")
                        if (!auj.isNullOrEmpty()) {
                            pasteContent(auj[1], "@")
                        }
                        //@输入框
                        val bxz =
                            accessibilityNodeInfo?.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bxz")
                        if (!bxz.isNullOrEmpty()) {
                            if (NAME_LIST.isNotEmpty()) {
                                pasteContent(bxz[0], NAME_LIST[0])
                                NAME_LIST.removeAt(0)
                            }
                        }
                    }, TEMP)
                }
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                Log.e("微信测试", "TYPE_VIEW_SCROLLED")
                if (isAtSomeoneUI && event.className == "android.widget.ListView") {
                    Handler().postDelayed(Runnable {
                        if (accessibilityNodeInfo == null) {
                            return@Runnable
                        }
                        //@人的列表
                        val ark =
                            accessibilityNodeInfo?.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ark")
                        if (ark?.isNotEmpty() == true && ark[0]?.childCount == 1) {
                            performClickBtn(ark[0]?.getChild(0))
                        }
                    }, TEMP)
                }
            }
        }
    }

    private fun performClickBtn(accessibilityNodeInfoList: List<AccessibilityNodeInfo>?): Boolean {
        if (accessibilityNodeInfoList != null && accessibilityNodeInfoList.isNotEmpty()) {
            accessibilityNodeInfoList.forEach { accessibilityNodeInfo ->
                if (accessibilityNodeInfo.isClickable && accessibilityNodeInfo.isEnabled) {
                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                }
            }
        }
        return false
    }

    private fun performClickBtn(accessibilityNodeInfo: AccessibilityNodeInfo?) {
        if (accessibilityNodeInfo != null) {
            if (accessibilityNodeInfo.isClickable && accessibilityNodeInfo.isEnabled) {
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

    /**
     * 粘贴文本
     *
     * @param tempInfo
     * @param contentStr
     * @return true 粘贴成功，false 失败
     */
    private fun pasteContent(tempInfo: AccessibilityNodeInfo?, contentStr: String): Boolean {
        if (tempInfo == null) {
            return false
        }
        if (tempInfo.isEnabled && tempInfo.isClickable && tempInfo.isFocusable) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("text", contentStr)
            clipboard.primaryClip = clip
            //tempInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            tempInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE)
            return true
        }
        return false
    }
}