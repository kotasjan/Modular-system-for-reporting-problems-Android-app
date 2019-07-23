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
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import cz.jankotas.bakalarka.models.InputData
import cz.jankotas.bakalarka.models.ModuleData
import kotlin.collections.ArrayList

/**
 * Aktivita je součástí procesu přidávání nového podnětu a stará se o sběr detailních (zejména textových) informací o novém
 * podnětu. Obsahuje také část s automaticky generovanými moduly.
 */
class ReportGetDescriptionActivity : AppCompatActivity() {

    // seznam modulů získaných z předchozí aktivity
    private lateinit var modules: ArrayList<Module>

    // linearLayout pro vkládání vygenerovaných modulů
    private lateinit var linearLayout: LinearLayout

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_get_description)

        // nastavení toolbaru a tlačítka zrušit (close icon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        // listener který naslouchá změně/úpravě textu v editTextu titulku
        report_title.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                title_letters.text = "${report_title.text.length}/80"
            }
        })

        // listener který naslouchá změně/úpravě textu v editTextu popisu
        report_description.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                description_letters.text = "${report_description.text.length}/255"
            }
        })

        // pokud byly pole již předtím vyplněny, vložit do nich minulá data
        if (Common.newReport.title != null) report_title.setText(Common.newReport.title)
        if (Common.newReport.userNote != null) report_description.setText(Common.newReport.userNote)

        linearLayout = findViewById(R.id.generated_layout) // přiřazení layoutu z XML souboru

        modules = intent.getParcelableArrayListExtra("modules") // získání modulů z předhozí aktivity

        // zlačítko pokaračovat v procesu přidávání nového podnětu
        btn_continue.setOnClickListener {
            if (checkInputs()) {
                saveState()
                startNextActivity()
            }
        }

        // tlačítko zpět v procesu přidávání nového podnětu
        btn_back.setOnClickListener {
            saveState()
            finish()
        }

        setLayout()
    }

    // zobrazení menu po kliknutí na ikonu tří teček v pravém horním rohu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report_bug, menu)
        return true
    }

    // definování akcí vzhledem k výběru položky z menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_report_bug -> {
                // spustit aktivitu ReportBugActivity
                startActivity(Intent(this, ReportBugActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // zobrazení dialogu zda si uživatel opravdu přeje opustit proces přidávání podnětu
    override fun onSupportNavigateUp(): Boolean {
        showDialog()
        return true
    }

    // zobrazení dialogu před opuštěním procesu přidávání nového hlášení
    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        // potvrzení akce
        builder1.setPositiveButton("OK") { dialog, _ ->
            run {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                Common.newReport.clearData()
                dialog.cancel()
            }
        }

        // zrušení akce
        builder1.setNegativeButton("Cancel") { dialog, _ ->
            run {
                dialog.cancel()
            }
        }

        val alert11 = builder1.create()
        alert11.show()
    }

    // validace, zda byly poviná pole (titulek a popis) vyplněna
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

    // spuštění další aktivity
    private fun startNextActivity() {
        val intent = Intent(this, NewReportDetailActivity::class.java)
        startActivity(intent)
    }

    // uložení aktuálního stavu (hodnot vstup) do objektu newReport
    private fun saveState() {
        Common.newReport.title = report_title.text.toString()
        Common.newReport.userNote = report_description.text.toString()
        Common.newReport.moduleData = getModuleDataList() // zpracování vstupů modulů
        Log.d(Common.APP_NAME, Common.newReport.toString())
    }

    // řídící funkce pro generování modulů
    private fun setLayout() {

        for ((index, module) in modules.withIndex()) {
            // přidání separátoru v podobě tenké čáry mezi moduly
            addLineSeparator()

            // nastavení hlavičky modulu
            val textView = TextView(this) // vytvoření komponenty textView
            setModuleHeader(textView, module.name) // vložení jména modulu do komponenty
            linearLayout.addView(textView) // přidání komponety do layoutu

            // přidání vstupů do modulu
            if (Common.newReport.moduleData != null && Common.newReport.moduleData!!.isNotEmpty())
                setModuleInputs(module.inputs, Common.newReport.moduleData?.get(index)?.inputData)
            else
                setModuleInputs(module.inputs, null)
        }
    }

    // generování různých typů vstupů
    private fun setModuleInputs(inputs: List<Input>, inputsData: ArrayList<InputData>?) {
        for ((index, input) in inputs.withIndex()) {
            when (input.inputType) {
                "string" -> addEditText(input, inputsData?.get(index)) // vstup typu textový řetězec
                "number" -> addEditText(input, inputsData?.get(index)) // vstup typu číslo
                "spinner" -> addSpinner(input, inputsData?.get(index)) // vstup typu selecBox/spinner
            }
        }
    }

    // generování komponenty EditText (textový vstup)
    private fun addEditText(input: Input, inputData: InputData?) {
        // nastavení parametrů komponenty
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        // specifikování okrajů komponenty
        params.setMargins(convertDpToPixel(16f), convertDpToPixel(8f), convertDpToPixel(16f), convertDpToPixel(8f))

        // vytvoření layoutu pro komponentu
        val editTextLayout = LinearLayout(this)
        editTextLayout.orientation = LinearLayout.VERTICAL
        editTextLayout.layoutParams = params // vložení parametrů layoutu

        linearLayout.addView(editTextLayout) // přidání layoutu s EditTextem do hlavního layoutu

        val editText = EditText(this) // vytvoření komponenty EditText
        editText.hint = input.title // určení názvu komponenty
        editText.id = input.id // určení identifikátoru pro pozdější sběr dat

        setEditText(editText, input.inputType == "number") // dodatečná nastavení komponenty

        editTextLayout.addView(editText) // přidat komponenty do layoutu

        // nastavení počítadla znaků
        if (input.characters != null) {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(input.characters!!))

            val textView = TextView(this) // vytvořit komponentu pro zobrazování počtu

            setNumberOfCharacters(textView, input.characters!!) // nastavení počtu znaků

            // listener který naslouchá změně/úpravě textu v editTextu popisu
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    textView.text = "${editText.text.length}/${input.characters}"
                }
            })
            editText.setText(inputData?.value)
            editTextLayout.addView(textView)
        }
    }

    // nastavení možných vstupních hodnot pro editText
    private fun setEditText(editText: EditText, isNumber: Boolean) {
        editText.inputType = when (isNumber) {
            true -> InputType.TYPE_CLASS_NUMBER // vstup pouze číselný
            false -> InputType.TYPE_CLASS_TEXT // vstup libovolný text
        }
        editText.textSize = 16f // velikost textu
    }

    // prvotní nastavení počtu znaků
    private fun setNumberOfCharacters(textView: TextView, number: Int) {
        textView.text = "0/${number}"
        textView.textSize = 14f
        textView.setTextColor(resources.getColor(R.color.colorSecondaryText, theme))
        textView.gravity = Gravity.END
    }

    // generování komponenty spinner/selectBox
    private fun addSpinner(input: Input, inputData: InputData?) {
        // nastavení parametrů komponenty
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        // specifikování okrajů komponenty
        params.setMargins(convertDpToPixel(16f), convertDpToPixel(8f), convertDpToPixel(16f), convertDpToPixel(8f))

        // vytvoření layoutu pro umístění komponenty
        val spinnerLayout = LinearLayout(this)
        spinnerLayout.orientation = LinearLayout.HORIZONTAL
        spinnerLayout.layoutParams = params
        spinnerLayout.isBaselineAligned = false

        linearLayout.addView(spinnerLayout) // přidání layoutu komponenty do hlavního layoutu

        // vytvoření popisku spinneru
        val textView = TextView(this)
        setSpinnerName(textView, input.title)
        spinnerLayout.addView(textView)

        // vytvoření komponenty spinneru / selectBoxu
        val spinner = Spinner(this)
        spinner.id = input.id // označení komponenty identifikátorem pro následný sběr dat

        // přidání položek seznamu do komponenty spinneru
        if (input.items != null) {
            val itemTextsList = input.items!!.map { it.text } // namapopvání názvů položek v seznamu položek do seznamu spinneru
            val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item, itemTextsList) // vytvoření adaptéru pro položky spinneru
            spinner.adapter = arrayAdapter // přiřazení adaptéru

            // naslouchání změnám výběru položky
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {}
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        // v případě, že již byla nějaká položka v minulosti zvolena, nastavit ji znovu
        inputData?.let { spinner.setSelection((spinner.adapter as ArrayAdapter<String>).getPosition(inputData.value)) }
        spinnerLayout.addView(spinner) // přidání komponenty do layoutu
    }

    // nastavení dodatečných parametrů popisku spinneru / selectBoxu
    private fun setSpinnerName(textView: TextView, name: String) {
        // nastavení parametrů komponenty
        val params = TableLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        // nastavení okrajů
        params.setMargins(0, convertDpToPixel(8f), 0, 0)
        textView.layoutParams = params
        textView.text = name
        textView.textSize = 16f
        textView.setTextColor(resources.getColor(R.color.colorSecondaryText, theme))
    }

    // nastavení dodatečných parametrů popisku modulu (hlavičky)
    private fun setModuleHeader(textView: TextView, name: String) {
        // nastavení parametrů komponenty
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        // nastavení okrajů
        params.setMargins(convertDpToPixel(16f), convertDpToPixel(8f), convertDpToPixel(16f), convertDpToPixel(8f))

        textView.layoutParams = params
        textView.setTextColor(getColor(R.color.colorPrimaryText)) // nastavení barvy textu
        textView.textSize = 16f // nastavení velikosti písma
        textView.text = name.split(' ').joinToString(" ") { it.capitalize() } // Kapitálky
    }

    // přidání separační čáry mezi moduly
    private fun addLineSeparator() {
        // vytvoření layoutu pro separátor
        val lineLayout = LinearLayout(this)
        // nastavení barvy linky
        lineLayout.setBackgroundColor(getColor(R.color.colorDivider))
        // nastavení obecných parametrů komponenty
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2)
        // nastavení okrajů
        params.setMargins(0, convertDpToPixel(8f), 0, convertDpToPixel(8f))
        lineLayout.layoutParams = params
        // přidání komponenty do layoutu
        linearLayout.addView(lineLayout)
    }

    // pomocná funkce pro konvertování DP (density pixels) na PX (pixely)
    private fun convertDpToPixel(dp: Float): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px)
    }

    // řídící funkce pro získávání dat ze vstupů modulů
    private fun getModuleDataList(): ArrayList<ModuleData>? {

        val moduleDataList = ArrayList<ModuleData>() // seznam objektů obsahujících data modulů

        // každý modul se postupně zpracovává
        for (module in modules) {
            moduleDataList.add(getModuleData(module)) // získání objektu ModuleData a jeho přidání do seznamu
        }
        // vrácení seznamu obsahujícího data modulů
        return if (moduleDataList.isNotEmpty()) moduleDataList else null
    }

    // získání dat konkrétního modulu
    private fun getModuleData(module: Module): ModuleData {

        val inputDataList = ArrayList<InputData>() // seznam objektů obsahujících data vstupů

        // každý vstup se postupně zpracovává
        for (input in module.inputs) {
            inputDataList.add(getInputData(input)) // získání objektu InputData a jeho přidání do seznamu
        }
        // vrácení seznamu dat vstupů konkrétního modulu
        return ModuleData(module.id, inputDataList)
    }

    // získání dat konkrétního vstupu
    private fun getInputData(input: Input): InputData {

        // získání a vrácení dat v závislosti na typu vstupu
        return InputData(input.id, when (input.inputType) {
            "string" -> getEditText(input)
            "number" -> getEditText(input)
            "spinner" -> getSpinnerText(input)
            else -> ""
        })
    }

    // získání dat z editTextu pomocí jeho identifikátoru
    private fun getEditText(input: Input): String {
        val et: EditText = findViewById(input.id)
        return et.text.toString()
    }

    // získání dat ze spinneru pomocí jeho identifikátoru
    private fun getSpinnerText(input: Input): String {
        val spinner: Spinner = findViewById(input.id)
        return spinner.selectedItem.toString()
    }
}
