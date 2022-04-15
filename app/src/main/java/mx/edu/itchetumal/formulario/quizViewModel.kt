package mx.edu.itchetumal.formulario

import androidx.lifecycle.ViewModel

class QuizViewModel:ViewModel() {
    private var bancoPreguntas = listOf<Preguntas>(
        Preguntas(R.string.question_1,false),
        Preguntas(R.string.question_2,true),
        Preguntas(R.string.question_3,false),
        Preguntas(R.string.question_4,true),
        Preguntas(R.string.question_5,true)
    )
    var index:Int = 0
    var isCheater = false
    fun getRespuesta():Boolean {
        return bancoPreguntas[index].respuesta
    }
    fun getPregunta():Int{
        return bancoPreguntas[index].textResId
    }
}