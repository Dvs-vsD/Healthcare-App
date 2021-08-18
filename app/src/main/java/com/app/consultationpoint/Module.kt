package com.app.consultationpoint

import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.LoginRegisterRepository
import com.app.consultationpoint.general.LoginRegisterViewModel
import com.app.consultationpoint.patient.appointment.allAppointments.AllAptRepository
import com.app.consultationpoint.patient.appointment.allAppointments.AllAptViewModel
import com.app.consultationpoint.patient.appointment.bookAppointment.BookAptRepository
import com.app.consultationpoint.patient.appointment.bookAppointment.BookAptViewModel
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptRepository
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptViewModel
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenRepository
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenViewModel
import com.app.consultationpoint.patient.chat.room.RoomRepository
import com.app.consultationpoint.patient.chat.room.RoomViewModel
import com.app.consultationpoint.patient.dashboard.DashboardRepository
import com.app.consultationpoint.patient.dashboard.DashboardViewModel
import com.app.consultationpoint.patient.doctor.DoctorRepository
import com.app.consultationpoint.patient.doctor.DoctorViewModel
import com.app.consultationpoint.patient.userProfile.UserRepository
import com.app.consultationpoint.patient.userProfile.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginRegisterViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { DoctorViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { BookAptViewModel(get()) }
    viewModel { MyAptViewModel(get()) }
    viewModel { ChatScreenViewModel(get()) }
    viewModel { RoomViewModel(get()) }
    viewModel { AllAptViewModel(get()) }
}

val repositoryModule = module {
    single { LoginRegisterRepository(get()) }
    single { DashboardRepository(get()) }
    single { DoctorRepository(get()) }
    single { UserRepository(get()) }
    single { BookAptRepository(get()) }
    single { MyAptRepository(get()) }
    single { ChatScreenRepository(get()) }
    single { RoomRepository(get()) }
    single { AllAptRepository(get()) }
}

val firebaseModule = module {
    single { FirebaseSource() }
}