package cz.jankotas.bakalarka.services.reportFetcher

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import cz.jankotas.bakalarka.models.Report

class ReportDataSourceFactory : DataSource.Factory<Int, Report>() {

    val reportLiveDataSource: MutableLiveData<PageKeyedDataSource<Int, Report>> = MutableLiveData()

    override fun create(): DataSource<Int, Report> {
        val reportDataSource = ReportDataSource()
        reportLiveDataSource.postValue(reportDataSource)
        return reportDataSource
    }
}