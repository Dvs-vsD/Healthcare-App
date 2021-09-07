package com.app.consultationpoint

import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.LoginRegisterRepository
import com.app.consultationpoint.patient.appointment.allAppointments.AllAptRepository
import com.app.consultationpoint.patient.appointment.bookAppointment.BookAptRepository
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptRepository
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptViewModel
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenRepository
import com.app.consultationpoint.patient.chat.room.RoomRepository
import com.app.consultationpoint.patient.chat.roomInfo.RIRepository
import com.app.consultationpoint.patient.dashboard.DashboardRepository
import com.app.consultationpoint.patient.doctor.DoctorRepository
import com.app.consultationpoint.patient.userProfile.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideDashboardRepo(source: FirebaseSource) = DashboardRepository(source)

    @Provides
    @ActivityRetainedScoped
    fun provideDocRepo(source: FirebaseSource) = DoctorRepository(source)

    @Provides
    @ActivityRetainedScoped
    fun provideLoginRegRepo(source: FirebaseSource) = LoginRegisterRepository(source)

    @Provides
    @ActivityRetainedScoped
    fun provideAllAptRepo(source: FirebaseSource) = AllAptRepository(source)

    @Provides
    @ActivityRetainedScoped
    fun provideBookAptRepo(source: FirebaseSource) = BookAptRepository(source)

    @Provides
    @ActivityRetainedScoped
    fun provideMyAptRepo(source: FirebaseSource) = MyAptRepository(source)

    @Provides
    @ActivityRetainedScoped
    fun provideChatScreenRepo(source: FirebaseSource) = ChatScreenRepository(source)

    @Provides
    @ActivityRetainedScoped
    fun provideRoomRepo(source: FirebaseSource) = RoomRepository(source)

    @Provides
    @ActivityRetainedScoped
    fun provideRIRepo(source: FirebaseSource) = RIRepository(source)

    @Provides
    @ActivityRetainedScoped
    fun provideUserRepo(source: FirebaseSource) = UserRepository(source)
}