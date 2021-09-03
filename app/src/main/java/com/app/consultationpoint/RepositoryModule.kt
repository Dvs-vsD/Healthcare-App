package com.app.consultationpoint

import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.patient.dashboard.DashboardRepository
import com.app.consultationpoint.patient.doctor.DoctorRepository
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
}