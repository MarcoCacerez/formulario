package mx.edu.itchetumal.formulario

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import mx.edu.itchetumal.formulario.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
private const val REQUEST_CODE_CHEAT = 0
private const val KEY_INDEX = "index"
private const val KEY_CORRECTAS = "correctas"
private const val KEY_INCORRECTAS = "incorrectas"
private const val KEY_CONTESTADO = "contestado"

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var correctas = 0
    private var incorrectas = 0
    private var contestado: Boolean = false;
    private val quizViewModel:QuizViewModel by lazy{
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.index = currentIndex

        val currentCorrectas = savedInstanceState?.getInt(KEY_CORRECTAS,0) ?: 0
        correctas = currentCorrectas

        val currentIncorrectas = savedInstanceState?.getInt(KEY_INCORRECTAS,0)?:0
        incorrectas = currentIncorrectas

        val currentContestado = savedInstanceState?.getBoolean(KEY_CONTESTADO, false) ?: false
        contestado = currentContestado

        binding.btnTrue.setOnClickListener{
            contestar(true)
        }
        binding.btFalse.setOnClickListener{
            contestar(false)
        }
        binding.btNext.setOnClickListener {
            if(contestado && quizViewModel.index < 5){
                updatePregunta()
                contestado = false
            }else{
                quizViewModel.index = 0
                incorrectas = 0
                correctas = 0
                binding.tvCorrectas.text = "0"
                binding.tvIncorrectas.text = "0"
                updatePregunta()
                contestado = false
            }
        }
        updatePregunta()
        updatePuntaje()
        //Segunda Actividad
        binding.cheatButton.setOnClickListener{
            val answerIsTrue = quizViewModel.getRespuesta()
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        //Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.index)
        savedInstanceState.putInt(KEY_CORRECTAS,correctas)
        savedInstanceState.putInt(KEY_INCORRECTAS,incorrectas)
        savedInstanceState.putBoolean(KEY_CONTESTADO,contestado)
    }
    private fun contestar(estado : Boolean){
        val respuesta = quizViewModel.getRespuesta()
        val messageResId = when {
            quizViewModel.isCheater -> getString(R.string.judgment_toast)
            estado == respuesta -> getString(R.string.correct_toast)
            else -> getString(R.string.incorrect_toast)
        }
        if(respuesta == estado){
            correctas ++
            binding.tvCorrectas.text = correctas.toString()
        }else{
            incorrectas ++
            binding.tvIncorrectas.text = incorrectas.toString()
        }
        sendMsg(messageResId)
        quizViewModel.index ++
        quizViewModel.isCheater = false
        contestado = true
    }
    private fun sendMsg(msg : String){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }
    private fun updatePregunta(){
        val preguntaResId = quizViewModel.getPregunta()
        binding.questionTextView.setText(preguntaResId)
    }
    private fun updatePuntaje(){
        binding.tvIncorrectas.text = incorrectas.toString()
        binding.tvCorrectas.text = correctas.toString()
    }
}