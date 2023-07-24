package com.example.viladomar


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    /* ===== INICIALIZAÇÃO DAS FUNÇÕES GERAIS DA MAIN ACTIVITY ==================================== */
    private fun getDataEntry(data: EditText): String {
        return data.text.toString()
    }

    private fun isDataEmpty(checkedData: EditText): Boolean {
        return getDataEntry(checkedData) == ""
    }

    private fun showToastEditText(view: EditText, text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        view.setSelection(0)
    }

    private fun checkInput(
        viewName: EditText,
        viewEmail: EditText,
        viewPhone: EditText,
        viewAddress: EditText,
        type: String
    ): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (isDataEmpty(viewName)) {
            Toast.makeText(this, "O nome não pode estar vazio.", Toast.LENGTH_SHORT).show()
            if (type == "add") {
                viewName.setSelection(0)
            }
            return false
        } else if (isDataEmpty(viewEmail)) {
            Toast.makeText(this, "O e-mail não pode estar vazio.", Toast.LENGTH_SHORT).show()
            if (type == "add") {
                viewEmail.setSelection(0)
            }
            return false
        } else if (isDataEmpty(viewPhone)) {
            Toast.makeText(this, "O número de telefone não pode estar vazio.", Toast.LENGTH_SHORT).show()
            if (type == "add") {
                viewPhone.setSelection(0)
            }
            return false
        } else if (isDataEmpty(viewAddress)) {
            Toast.makeText(this, "O endereço não pode estar vazio.", Toast.LENGTH_SHORT).show()
            if (type == "add") {
                viewAddress.setSelection(0)
            }
            return false
        } else {
            return true
        }
    }

    private fun showRecords() {
        val outputArray = getOutputArray()
        if (outputArray.size == 0) {
            txt_noRecords.visibility = View.VISIBLE
            records_recycler.visibility = View.INVISIBLE
        } else {
            txt_noRecords.visibility = View.INVISIBLE
            records_recycler.visibility = View.VISIBLE
        }
    }

    private fun getOutputArray(): ArrayList<EmpModel> {
        val dataOut = DatabaseHandler(this).showEmployees()
        records_recycler.layoutManager = LinearLayoutManager(this)
        records_recycler.adapter = EmpAdapter(dataOut, this)
        return dataOut
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /* ====== FUNÇÕES DO BANCO DE DADOS CRUD ======================================================== */
    private fun addRecord(
        inputName: EditText,
        inputEmail: EditText,
        inputPhone: EditText,
        inputAddress: EditText
    ) {
        val databaseHandler = DatabaseHandler(this)
        databaseHandler.addEmployee(
            EmpModel(
                0,
                getDataEntry(inputName),
                getDataEntry(inputEmail),
                getDataEntry(inputPhone),
                getDataEntry(inputAddress)
            )
        )

        Toast.makeText(this, "Contato Salvo", Toast.LENGTH_SHORT).show()
        inputName.text.clear()
        inputEmail.text.clear()
        inputPhone.text.clear()
        inputAddress.text.clear()
        showRecords()
        closeKeyboard()
    }

    fun deleteRecord(emp: EmpModel) {
        val builder =
            MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
        builder.setTitle("Deletar Contato")
        builder.setMessage("Você tem certeza disso?")
        builder.setCancelable(false)
        builder.setIcon(R.drawable.ic_warning)

        builder.setPositiveButton("Sim") { dialog: DialogInterface, which ->
            val databaseHandler = DatabaseHandler(this)
            databaseHandler.deleteEmployee(
                EmpModel(
                    emp.id,
                    emp.name,
                    emp.email,
                    emp.phone,
                    emp.address
                )
            )
            Toast.makeText(this, "Contato deletado", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            showRecords()
        }

        builder.setNegativeButton("Não") { dialog: DialogInterface, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    fun editRecord(emp: EmpModel) {
        val builder = Dialog(this, R.style.Theme_Dialog)
        builder.setCancelable(false)
        builder.setContentView(R.layout.dialog_record_edit)
        val editName = builder.findViewById<EditText>(R.id.edit_dialog_inputName)
        val editEmail = builder.findViewById<EditText>(R.id.edit_dialog_inputEmail)
        val editPhone = builder.findViewById<EditText>(R.id.edit_dialog_inputPhone)
        val editAddress = builder.findViewById<EditText>(R.id.edit_dialog_inputAddress)
        val btnCancel = builder.findViewById<TextView>(R.id.edit_dialog_btnCancel)
        val btnConfirm = builder.findViewById<TextView>(R.id.edit_dialog_btnConfirm)

        editName.setText(emp.name)
        editEmail.setText(emp.email)
        editPhone.setText(emp.phone)
        editAddress.setText(emp.address)

        btnCancel.setOnClickListener {
            builder.dismiss()
        }

        btnConfirm.setOnClickListener {
            val databaseHandler = DatabaseHandler(this)
            if (checkInput(editName, editEmail, editPhone, editAddress, "edit")) {
                databaseHandler.updateEmployee(
                    EmpModel(
                        emp.id,
                        getDataEntry(editName),
                        getDataEntry(editEmail),
                        getDataEntry(editPhone),
                        getDataEntry(editAddress)
                    )
                )
                Toast.makeText(this, "Novo registro salvo!", Toast.LENGTH_SHORT).show()
                builder.dismiss()
                showRecords()
                closeKeyboard()
            }
        }
        builder.show()
    }

    fun moreRecord(emp: EmpModel) {
        val builder =
            MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_record_show, null)
        builder.setTitle("Informações do Contato")
        builder.setView(dialogView)
        builder.setCancelable(false)


        val moreName = dialogView.findViewById<TextView>(R.id.show_dialog_outputName)
        val moreEmail = dialogView.findViewById<TextView>(R.id.show_dialog_outputEmail)
        val morePhone = dialogView.findViewById<TextView>(R.id.show_dialog_outputPhone)
        val moreAddress = dialogView.findViewById<TextView>(R.id.show_dialog_outputAddress)

        moreName.setText(emp.name)
        moreEmail.setText(emp.email)
        morePhone.setText(emp.phone)
        moreAddress.setText(emp.address)

        builder.setPositiveButton("Voltar") { dialog: DialogInterface, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    /* ====== FUNÇÃO PRINCIPAL DA MAIN ACTIVITY ===================================================== */

    override fun onCreate(savedInstanceState: Bundle?) {
        //SqlScoutServer.create(this, packageName)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = "Condominio Vila do Mar"

        val inputName = findViewById<EditText>(R.id.input_name)
        val inputEmail = findViewById<EditText>(R.id.input_email)
        val inputPhone = findViewById<EditText>(R.id.input_phone)
        val inputAddress = findViewById<EditText>(R.id.input_address)
        val buttonAddRecord = findViewById<Button>(R.id.btn_add_record)

        showRecords()
        buttonAddRecord.setOnClickListener {
            if (checkInput(inputName, inputEmail, inputPhone, inputAddress, "add")) {
                addRecord(inputName, inputEmail, inputPhone, inputAddress)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.topbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.topbar_refresh -> {
                showRecords()
                Toast.makeText(this, "123456", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
