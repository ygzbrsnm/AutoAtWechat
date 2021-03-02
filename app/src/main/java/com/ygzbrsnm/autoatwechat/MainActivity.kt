package com.ygzbrsnm.autoatwechat

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.anim.AppFloatDefaultAnimator
import com.lzf.easyfloat.anim.DefaultAnimator
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnDisplayHeight
import com.lzf.easyfloat.interfaces.OnInvokeView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.float_layout.view.*

class MainActivity : AppCompatActivity() {
    companion object {
        var NAME_LIST: MutableList<String> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        open_accessibility_setting.setOnClickListener { jumpToSettingPage(baseContext) }
        btn_save.setOnClickListener {
            if (handlerInputName()) return@setOnClickListener
            startFloat()
            openWeChatApplication()
        }
        btn_plus.setOnClickListener {
            startActivity(Intent(this, ExcelPlusActivity::class.java))
        }
    }

    private fun handlerInputName(): Boolean {
        if (TextUtils.isEmpty(edit.text.toString())) {
            Toast.makeText(baseContext, "要先输入关键字哦瓜西皮", Toast.LENGTH_SHORT).show()
            return true
        }
        NAME_LIST.clear()
        edit.text.toString().split("，").forEach { NAME_LIST.add(it) }
        return false
    }

    private fun startFloat() {
        EasyFloat.with(this)
            // 设置浮窗xml布局文件，并可设置详细信息
            .setLayout(R.layout.float_layout, OnInvokeView { root ->
                root.run {
                    close.setOnClickListener { expandRoot.visibility = View.GONE }
                    topButton.setOnClickListener { expandRoot.visibility = View.VISIBLE }
                    start.setOnClickListener {
                        if (handlerInputName()) return@setOnClickListener
                        expandRoot.visibility = View.GONE
                    }
                    end.setOnClickListener {
                        NAME_LIST.clear()
                        expandRoot.visibility = View.GONE
                    }
                }
            })
            // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示、仅后台显示
            .setShowPattern(ShowPattern.ALL_TIME)
            // 设置吸附方式，共15种模式，详情参考SidePattern
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            // 设置浮窗的标签，用于区分多个浮窗
            .setTag("testFloat")
            // 设置浮窗是否可拖拽，默认可拖拽
            .setDragEnable(true)
            // 系统浮窗是否包含EditText，仅针对系统浮窗，默认不包含
            .hasEditText(false)
            // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
            .setLocation(100, 200)
            // 设置浮窗的对齐方式和坐标偏移量
            .setGravity(Gravity.END or Gravity.CENTER_VERTICAL, 0, 200)
            // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
            .setMatchParent(widthMatch = false, heightMatch = false)
            // 设置Activity浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
            .setAnimator(DefaultAnimator())
            // 设置系统浮窗的出入动画，使用同上
            .setAppFloatAnimator(AppFloatDefaultAnimator())
            // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
            // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
            .registerCallback {
                createResult { isCreated, msg, view -> }
                show { }
                hide { }
                dismiss { }
                touchEvent { view, motionEvent -> }
                drag { view, motionEvent -> }
                dragEnd { }
            }
            // 创建浮窗（这是关键哦😂）
            .show()
    }

    private fun openWeChatApplication() {
        baseContext.packageManager.getLaunchIntentForPackage("com.tencent.mm")?.let {
            startActivity(it)
        }
    }
}

private const val ACTION = "action"
private const val ACTION_START_ACCESSIBILITY_SETTING = "action_start_accessibility_setting"

fun jumpToSettingPage(context: Context) {
    try {
        val intent = Intent(context, AccessibilityOpenHelperActivity::class.java)
        intent.putExtra(ACTION, ACTION_START_ACCESSIBILITY_SETTING)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    } catch (ignore: Exception) {
    }
}
