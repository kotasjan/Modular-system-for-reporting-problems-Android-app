package cz.jankotas.bakalarka.services.reportFetcher.factories

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.services.reportFetcher.ReportClosedDataSource

class ReportClosedDataSourceFactory : DataSource.Factory<Int, Report>() {

    val reportLiveDataSource: MutableLiveData<PageKeyedDataSource<Int, Report>> = MutableLiveData()

    override fun create(): DataSource<Int, Report> {
        val reportDataSource = ReportClosedDataSource()
        reportLiveDataSource.postValue(reportDataSource)
        return reportDataSource
    }
}