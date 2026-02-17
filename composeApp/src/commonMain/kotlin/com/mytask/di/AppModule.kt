package com.mytask.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

// Pre-built from core/
import com.mytask.core.data.local.AppDatabase
import com.mytask.core.data.auth.AuthRepository
import com.mytask.core.di.platformModule
import com.mytask.core.di.networkModule

// All DAOs, repositories, use cases, ViewModels from previous phases
import com.mytask.data.local.dao.AssignmentDao
import com.mytask.data.local.dao.ExamDao
import com.mytask.data.local.dao.ProjectDao
import com.mytask.data.local.dao.AppConfigDao
import com.mytask.data.repository.assignment.AssignmentRepository
import com.mytask.data.repository.assignment.AssignmentRepositoryImpl
import com.mytask.data.repository.exam.ExamRepository
import com.mytask.data.repository.exam.ExamRepositoryImpl
import com.mytask.data.repository.project.ProjectRepository
import com.mytask.data.repository.project.ProjectRepositoryImpl
import com.mytask.data.repository.appconfig.AppConfigRepository
import com.mytask.data.repository.appconfig.AppConfigRepositoryImpl
import com.mytask.domain.usecase.GetAssignmentListUseCase
import com.mytask.domain.usecase.MarkAssignmentCompleteUseCase
import com.mytask.domain.usecase.ViewAssignmentDetailsUseCase
import com.mytask.domain.usecase.GetExamListUseCase
import com.mytask.domain.usecase.ViewExamDetailsUseCase
import com.mytask.domain.usecase.GetProjectListUseCase
import com.mytask.domain.usecase.ViewProjectDetailsUseCase
import com.mytask.domain.usecase.GetDashboardOverviewUseCase
import com.mytask.domain.usecase.GetUpcomingItemsUseCase
import com.mytask.domain.usecase.UpdateGoogleSheetUrlUseCase
import com.mytask.domain.usecase.ValidateSheetUrlUseCase
import com.mytask.domain.usecase.GetCurrentSheetConfigUseCase
import com.mytask.presentation.assignmenttracking.AssignmentListViewModel
import com.mytask.presentation.assignmenttracking.AssignmentDetailViewModel
import com.mytask.presentation.examtracking.ExamListViewModel
import com.mytask.presentation.examtracking.ExamDetailViewModel
import com.mytask.presentation.projecttracking.ProjectListViewModel
import com.mytask.presentation.projecttracking.ProjectDetailViewModel
import com.mytask.presentation.dashboardoverview.DashboardViewModel
import com.mytask.presentation.googlesheetsconfiguration.SettingsViewModel
import com.mytask.presentation.googlesheetsconfiguration.SheetUrlConfigViewModel

fun moduleList(): List<Module> = listOf(
    platformModule(),
    networkModule(),
    appModule()
)

fun appModule() = module {
    // Auth
    single { AuthRepository(database = get(), backendHandler = getOrNull()) }

    // DAOs (from AppDatabase)
    single { get<AppDatabase>().assignmentDao() }
    single { get<AppDatabase>().examDao() }
    single { get<AppDatabase>().projectDao() }
    single { get<AppDatabase>().appConfigDao() }

    // Repositories
    singleOf(::AssignmentRepositoryImpl) { bind<AssignmentRepository>() }
    singleOf(::ExamRepositoryImpl) { bind<ExamRepository>() }
    singleOf(::ProjectRepositoryImpl) { bind<ProjectRepository>() }
    singleOf(::AppConfigRepositoryImpl) { bind<AppConfigRepository>() }

    // Use Cases
    factoryOf(::GetAssignmentListUseCase)
    factoryOf(::MarkAssignmentCompleteUseCase)
    factoryOf(::ViewAssignmentDetailsUseCase)
    factoryOf(::GetExamListUseCase)
    factoryOf(::ViewExamDetailsUseCase)
    factoryOf(::GetProjectListUseCase)
    factoryOf(::ViewProjectDetailsUseCase)
    factoryOf(::GetDashboardOverviewUseCase)
    factoryOf(::GetUpcomingItemsUseCase)
    factoryOf(::UpdateGoogleSheetUrlUseCase)
    factoryOf(::ValidateSheetUrlUseCase)
    factoryOf(::GetCurrentSheetConfigUseCase)

    // ViewModels
    viewModelOf(::AssignmentListViewModel)
    viewModelOf(::AssignmentDetailViewModel)
    viewModelOf(::ExamListViewModel)
    viewModelOf(::ExamDetailViewModel)
    viewModelOf(::ProjectListViewModel)
    viewModelOf(::ProjectDetailViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SheetUrlConfigViewModel)
}