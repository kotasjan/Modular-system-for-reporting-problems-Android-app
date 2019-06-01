package cz.jankotas.bakalarka

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cz.jankotas.bakalarka.models.Input
import cz.jankotas.bakalarka.models.Module
import java.util.ArrayList

class ReportModuleDataActivity : AppCompatActivity() {

    private lateinit var modules: ArrayList<Module>

    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_module_data)

        linearLayout = findViewById(R.id.generated_layout)

        modules = intent.getParcelableArrayListExtra("modules")

        setLayout()


    }

    private fun setLayout() {

        for (module in modules) {
            // Module header
            val textView = TextView(this)
            textView.text = module.name
            textView.id = module.id
            setModuleHeader(textView)
            linearLayout.addView(textView)

            // Add module's inputs
            setModuleInputs(module.inputs)

            // Line separator
            if (module != modules.last()) addLineSeperator()
        }
    }

    private fun setModuleInputs(inputs: List<Input>) {

    }


    private fun setModuleHeader(textView: TextView) {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        params.setMargins(0, 0, 0, convertDpToPixel(16f))

        textView.setTextColor(Color.BLACK)
        textView.layoutParams = params
    }


    private fun addLineSeperator() {
        val lineLayout = LinearLayout(this)
        lineLayout.setBackgroundColor(getColor(R.color.colorDivider))
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2)
        params.setMargins(0, convertDpToPixel(8f), 0, convertDpToPixel(8f))
        lineLayout.layoutParams = params
        linearLayout.addView(lineLayout)
    }

    //This function to convert DPs to pixels
    private fun convertDpToPixel(dp: Float): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px)
    }

}