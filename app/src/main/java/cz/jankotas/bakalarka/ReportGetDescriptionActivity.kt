package cz.jankotas.bakalarka

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_report_get_description.*
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.Input
import cz.jankotas.bakalarka.models.Module
import java.util.ArrayList
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.AdapterView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import cz.jankotas.bakalarka.models.InputData
import cz.jankotas.bakalarka.models.ModuleData


class ReportGetDescriptionActivity : AppCompatActivity() {

    private lateinit var modules: ArrayList<Module>

    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_get_description)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        report_title.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                title_letters.text = "$count/80"
            }
        })

        report_description.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                description_letters.text = "$count/255"
            }
        })

        if (Common.newReport.title != null) report_title.setText(Common.newReport.title)
        if (Common.newReport.title != null) report_description.setText(Common.newReport.userNote)

        linearLayout = findViewById(R.id.generated_layout)

        modules = intent.getParcelableArrayListExtra("modules")

        btn_continue.setOnClickListener {
            if (checkInputs()) {
                startNextActivity()
                Common.newReport.moduleData = getModuleDataList()
                Log.d(Common.APP_NAME, Common.newReport.moduleData.toString())
            }
        }

        btn_back.setOnClickListener {
            finish()
        }

        setLayout()
    }

    override fun onSupportNavigateUp(): Boolean {
        showDialog()
        return true
    }

    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        builder1.setPositiveButton("OK") { dialog, id ->
            run {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                Common.newReport.clearData()
                Common.selectedImages.clear()
                dialog.cancel()
            }
        }

        builder1.setNegativeButton("Cancel") { dialog, id ->
            run {
                dialog.cancel()
            }
        }

        val alert11 = builder1.create()
        alert11.show()
    }

    private fun checkInputs(): Boolean {
        var okay = true

        if (report_title.length() == 0) {
            okay = false
            report_title.error = "Tohle pole je povinné"
        }
        if (report_description.length() == 0) {
            okay = false
            report_description.error = "Tohle pole je povinné"
        }
        return okay
    }

    private fun startNextActivity() {
        Common.newReport.title = report_title.text.toString()
        Common.newReport.userNote = report_description.text.toString()

        /*val intent = Intent(this, ReportGetPhotosActivity::class.java)
        startActivity(intent)*/
    }


    private fun setLayout() {

        for (module in modules) {
            // Line separator
            addLineSeparator()

            // Module header
            val textView = TextView(this)
            setModuleHeader(textView, module.name)
            linearLayout.addView(textView)

            // Add module's inputs
            setModuleInputs(module.inputs)
        }
    }

    private fun setModuleInputs(inputs: List<Input>) {
        for (input in inputs) {
            when (input.inputType) {
                "string" -> addEditText(input)
                "number" -> addEditText(input)
                "spinner" -> addSpinner(input)
            }
        }
    }

    private fun addEditText(input: Input) {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        params.setMargins(convertDpToPixel(16f), convertDpToPixel(8f), convertDpToPixel(16f), convertDpToPixel(8f))

        val editTextLayout = LinearLayout(this)
        editTextLayout.orientation = LinearLayout.VERTICAL
        editTextLayout.layoutParams = params

        linearLayout.addView(editTextLayout)

        val editText = EditText(this)
        editText.hint = input.title
        editText.id = input.id

        setEditText(editText, input.inputType == "number")

        editTextLayout.addView(editText)

        if (input.characters != null) {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(input.characters!!))

            val textView = TextView(this)

            setNumberOfCharacters(textView, input.characters!!)

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    textView.text = "$count/${input.characters}"
                }
            })
            editTextLayout.addView(textView)
        }
    }

    private fun setEditText(editText: EditText, isNumber: Boolean) {
        editText.inputType = when (isNumber) {
            true -> InputType.TYPE_CLASS_NUMBER
            false -> InputType.TYPE_CLASS_TEXT
        }
        editText.textSize = 16f
    }

    private fun setNumberOfCharacters(textView: TextView, number: Int) {
        textView.text = "0/${number}"
        textView.textSize = 14f
        textView.setTextColor(resources.getColor(R.color.colorSecondaryText, theme))
        textView.gravity = Gravity.END
    }

    private fun addSpinner(input: Input) {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        params.setMargins(convertDpToPixel(16f), convertDpToPixel(8f), convertDpToPixel(16f), convertDpToPixel(8f))

        val spinnerLayout = LinearLayout(this)
        spinnerLayout.orientation = LinearLayout.HORIZONTAL
        spinnerLayout.layoutParams = params
        spinnerLayout.isBaselineAligned = false

        linearLayout.addView(spinnerLayout)

        val textView = TextView(this)
        setSpinner(textView, input.title)
        spinnerLayout.addView(textView)

        val spinner = Spinner(this)
        spinner.id = input.id

        if (input.items != null) {
            val itemTextsList = input.items!!.map { it.text }
            val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item, itemTextsList)
            spinner.adapter = arrayAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {}

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
        spinnerLayout.addView(spinner)
    }

    private fun setSpinner(textView: TextView, name: String) {
        val params = TableLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        params.setMargins(0, convertDpToPixel(8f), 0, 0)
        textView.layoutParams = params
        textView.text = name
        textView.textSize = 16f
        textView.setTextColor(resources.getColor(R.color.colorSecondaryText, theme))
    }

    private fun setModuleHeader(textView: TextView, name: String) {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        params.setMargins(convertDpToPixel(16f), convertDpToPixel(8f), convertDpToPixel(16f), convertDpToPixel(8f))

        textView.setTextColor(Color.BLACK)
        textView.layoutParams = params
        textView.setTextColor(getColor(R.color.colorPrimaryText))
        textView.textSize = 16f
        textView.text = name.split(' ').joinToString(" ") { it.capitalize() }
    }


    private fun addLineSeparator() {
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

    private fun getModuleDataList(): ArrayList<ModuleData>? {

        val moduleDataList = ArrayList<ModuleData>()
        for (module in modules) {
            moduleDataList.add(getModuleData(module))
        }
        return if(moduleDataList.isNotEmpty()) moduleDataList else null
    }

    private fun getModuleData(module: Module): ModuleData {

        val inputDataList = ArrayList<InputData>()
        for (input in module.inputs) {
            inputDataList.add(getInputData(input))
        }
        return ModuleData(module.id, inputDataList)
    }

    private fun getInputData(input: Input): InputData {

        return InputData(input.id, when (input.inputType) {
            "string" -> getEditText(input)
            "number" -> getEditText(input)
            "spinner" -> getSpinnerText(input)
            else -> ""
        })
    }

    private fun getEditText(input: Input): String {
        val et: EditText = findViewById(input.id)
        return et.text.toString()
    }

    private fun getSpinnerText(input: Input): String {
        val spinner: Spinner = findViewById(input.id)
        return spinner.selectedItem.toString()
    }
}
