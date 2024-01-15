package vic.dom.agendaapp.fragment

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import vic.dom.agendaapp.R
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.FragmentContactoDialogBinding
import vic.dom.agendaapp.models.Contacto

class ContactoDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentContactoDialogBinding

    private var imagePath = ""

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri->
        if (uri != null) {
            imagePath = uri.toString()
            asignarFoto(imagePath)
        }
    }

    interface DialogListener {
        fun onDialogOpened()
        fun onDialogDismissed()
    }

    private var dialogListener: DialogListener? = null

    fun setDialogListener(listener: DialogListener) {
        dialogListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactoDialogBinding.inflate(inflater, container, false)

        if (tag == "editarContactoDialogFragment") {
            binding.tituloDialogoContacto.text = "Editar contacto"

            binding.nombreDialogo.setText(arguments?.getString("nombreContacto"))
            binding.numeroDialogo.setText(arguments?.getString("numeroContacto"))
            binding.correoDialogo.setText(arguments?.getString("correoContacto"))
            asignarFoto(arguments?.getString("imagenContacto")!!)
            binding.switchDialogo.isChecked = arguments?.getBoolean("favoritoContacto")!!
        } else if (tag == "nuevoContactoFavoritoDialogFragment") {
            binding.tituloDialogoContacto.text = "Nuevo contacto favorito"
            binding.switchDialogo.isChecked = true
            binding.switchDialogo.isEnabled = false
            binding.switchDialogo.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Gris))
            binding.switchDialogo.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.GrisOscuro))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imagenDialogo.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.botonCancelarDialogoContacto.setOnClickListener {
            this.dismiss()
        }

        binding.botonConfirmarDialogoContacto.setOnClickListener {

            val correoRegex =
                Regex("^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\$")

            if (
                binding.nombreDialogo.text.isNullOrEmpty() ||
                binding.numeroDialogo.text.isNullOrEmpty() ||
                binding.correoDialogo.text.isNullOrEmpty()
                ) {

                Toast.makeText(
                    requireContext(),
                    "Los campos con un * son obligatorios",
                    Toast.LENGTH_SHORT
                ).show()

            } else if(
                (tag == "nuevoContactoDialogFragment" || tag == "nuevoContactoFavoritoDialogFragment") &&
                database.agendaDao().getAllContactos()
                    .map { it.nombre }
                    .contains(binding.nombreDialogo.text.toString())
                ) {

                Toast.makeText(requireContext(), "Ya existe un contacto con el mismo nombre", Toast.LENGTH_SHORT).show()

            } else if(
                tag == "editarContactoDialogFragment" &&
                database.agendaDao().getAllContactos()
                    .filter { it.nombre != arguments?.getString("nombreContacto") }
                    .map { it.nombre }
                    .contains(binding.nombreDialogo.text.toString())
                ) {

                Toast.makeText(requireContext(), "Ya existe un contacto con el mismo nombre", Toast.LENGTH_SHORT).show()

            } else if(binding.numeroDialogo.text.toString().length != 9) {

                Toast.makeText(requireContext(), "El número de teléfono no es válido", Toast.LENGTH_SHORT).show()

            } else if(!binding.correoDialogo.text.toString().matches(correoRegex)) {

                Toast.makeText(requireContext(), "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()

            } else {

                if (tag == "editarContactoDialogFragment") {

                    val contacto = database.agendaDao().getAllContactos().find { it.nombre == arguments?.getString("nombreContacto") }?.copy(
                        nombre = binding.nombreDialogo.text.toString(),
                        numero = binding.numeroDialogo.text.toString(),
                        correo = binding.correoDialogo.text.toString(),
                        imagen = imagePath,
                        favorito = binding.switchDialogo.isChecked
                    )!!

                    database.agendaDao().updateContacto(contacto)
                    Toast.makeText(requireContext(), "Contacto editado con éxito", Toast.LENGTH_SHORT).show()
                } else {

                    val contacto = Contacto(
                        nombre = binding.nombreDialogo.text.toString(),
                        numero = binding.numeroDialogo.text.toString(),
                        correo = binding.correoDialogo.text.toString(),
                        imagen = imagePath,
                        favorito = binding.switchDialogo.isChecked
                    )

                    database.agendaDao().addContacto(contacto)
                    Toast.makeText(requireContext(), "Contacto añadido con éxito", Toast.LENGTH_SHORT).show()
                }

                this.dismiss()

            }

        }
    }

    private fun asignarFoto(imagePath: String) {

        this.imagePath = imagePath

        if (imagePath.isNotEmpty()) {
            try {
                Glide.with(binding.imagenDialogo)
                    .load(Uri.parse(imagePath))
                    .transform(CenterCrop(), RoundedCorners(20))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imagenDialogo)
            } catch (e: Exception) {
                e.printStackTrace()
                Glide.with(binding.imagenDialogo)
                    .load(R.drawable.fotonula)
                    .transform(CenterCrop(), RoundedCorners(20))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imagenDialogo)
            }
        } else {
            Glide.with(binding.imagenDialogo)
                .load(R.drawable.fotonula)
                .transform(CenterCrop(), RoundedCorners(20))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imagenDialogo)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogListener?.onDialogDismissed()
    }

}