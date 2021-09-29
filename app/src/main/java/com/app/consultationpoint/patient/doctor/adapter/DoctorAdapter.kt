package com.app.consultationpoint.patient.doctor.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.RowOfDoctorListBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.bookAppointment.ChooseTimeActivity
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenActivity
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.patient.doctor.DoctorDetailsActivity
import com.app.consultationpoint.patient.doctor.DoctorViewModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import com.app.consultationpoint.utils.Utils.show
import io.realm.RealmList
import timber.log.Timber

class DoctorAdapter(
    list2: ArrayList<UserModel>?,
    private val activity: FragmentActivity?,
    private val viewModel: DoctorViewModel,
    private val listener: OnButtonChatCLick
) : RecyclerView.Adapter<DoctorAdapter.MyViewHolder>() {

    private var list: ArrayList<UserModel>? = null

    init {
        list = list2
    }

    fun setDataList(dataList: ArrayList<UserModel>?) {
        list = dataList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: RowOfDoctorListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "BinaryOperationInTimber")
        fun bind(item: UserModel) {
            if (item.profile != "" && item.profile != "null" && item.profile != null) {
                activity?.let { binding.ivProfile.loadImageFromCloud(item.profile!!) }
            }
            Timber.d(item.first_name + " " + item.doc_id)
            binding.tvDocName.text = "${item.first_name} ${item.last_name}"

            var roomId: Long = 0

            if (Utils.getUserType() == 0) {
                binding.tvSpecAdr.show()
                if (item.specialist_id != 0)
                    binding.tvSpecAdr.text = viewModel.getSpecializationName(item.specialist_id)
                else
                    binding.tvSpecAdr.text = ""

                binding.btnTakeAppointment.text = activity?.getString(R.string.btn_appointment)
            } else {
                binding.tvSpecAdr.hide()
                binding.btnTakeAppointment.text = activity?.getString(R.string.bn_chat)
                roomId = viewModel.checkRoomAvailability(Utils.getUserId().toLong(), item.id)
            }

            binding.btnTakeAppointment.setOnClickListener {
                if (Utils.getUserType() == 0) {
                    val intent = Intent(activity, ChooseTimeActivity::class.java)
                    intent.putExtra("doctor_id", item.doc_id)
                    intent.putExtra("isUpdate", false)
                    activity?.startActivity(intent)
                } else {
                    if (roomId != 0L) {
                        val intent = Intent(activity, ChatScreenActivity::class.java)
                        intent.putExtra("receiver_id", item.id)
                        intent.putExtra("room_id", roomId)
                        activity?.startActivity(intent)
                    } else {
                        listener.onChatBtnClick(item.id)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            RowOfDoctorListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list?.get(position)?.let { holder.bind(it) }
        holder.itemView.setOnClickListener {
            if (Utils.getUserType() == 0) {
                val intent = Intent(activity, DoctorDetailsActivity::class.java)
                Timber.d("user id %s", list?.get(position)?.id)
                intent.putExtra("doctor_id", list?.get(position)?.id)
                activity?.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    interface OnButtonChatCLick {
        fun onChatBtnClick(receiver_id: Long)
    }
}